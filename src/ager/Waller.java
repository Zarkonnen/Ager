package ager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Waller {
	static boolean[] GO_THRU = new boolean[1024];
	static {
		GO_THRU[Types.Air + 1] = true;
		GO_THRU[Types.Tall_Grass + 1] = true;
		GO_THRU[Types.Dead_Bush + 1] = true;
		GO_THRU[Types.Rose + 1] = true;
		GO_THRU[Types.Dandelion + 1] = true;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		MCMap map = new MCMap(new File(args[0]), 10);
		for (MCAFile f : map.files) {
			for (int cz = 0; cz < 32; cz++) {
				for (int cx = 0; cx < 32; cx++) {
					Chunk ch = f.chunks[cz][cx];
					if (ch != null) {
						ch.prepare();
						for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
							if (ch.getBlockType(x, y, z) == Types.Wool && ch.getData(x, y, z) == Types.Wool_Dark_Green) {
								int Y = y;
								do {
									ch.setBlockType((byte) Types.Cobblestone, x, Y, z);
									Y--;
								} while (Y > 0 && GO_THRU[ch.getBlockType(x, Y, z) + 1]);
							}
						}}}
					}
				}
			}
		}
		map.writeAndClose();
	}
}
