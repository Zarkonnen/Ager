/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ager;

import java.io.File;
import java.util.Random;

public class Ager {
	public static void main(String[] args) throws Exception {
		/*File f = new File("/Users/zar/Desktop/Fishcakes/region/r.0.0.mca");
		int[] sectorOffsets = new int[32 * 32];
		int[] sectorSizes = new int[32 * 32];
		BufferedInputStream fis = new BufferedInputStream(new FileInputStream(f));
		for (int i = 0; i < 32 * 32; i++) {
			for (int j = 0; j < 3; j++) {
				sectorOffsets[i] <<= 8;
				sectorOffsets[i] |= fis.read();
			}
			sectorSizes[i] = fis.read();
		}
				
		fis.close();*/
		/*
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				byte foo = 0;
				foo = Chunk.setNybble(foo, 0, a);
				System.out.println(Chunk.getNybble(foo, 0) == a);
				foo = Chunk.setNybble(foo, 1, b);
				System.out.println(Chunk.getNybble(foo, 0) == a);
				System.out.println(Chunk.getNybble(foo, 1) == b);
				System.out.println();
			}
		}
		
		System.exit(0);*/
		
		Random r = new Random();
		
		for (int fz = -1; fz < 1; fz++) { for (int fx = -1; fx < 1; fx++) {
			MCAFile f = new MCAFile(new File("/Users/zar/Desktop/Creative/region/"), fz, fx);
			/*for (int y = 0; y < 256; y++) {
				System.out.println(f.getBlockType(0, y, 0));
			}
			//System.out.println("-----");*/

			for (int y = 0; y < 255; y++) {
				for (int z = 0; z < 512; z++) {
					for (int x = 0; x < 512; x++) {
						if (f.getBlockType(x, y, z) == 4 && f.getBlockType(x, y + 1, z) == 0 && r.nextBoolean()) {
							f.setBlockType((byte) 0, x, y, z);
							f.setSkyLight((byte) f.getSkyLight(x, y, z), x, y - 1, z);
							f.setSkyLight((byte) f.getSkyLight(x, y + 1, z), x, y, z);
						}
					}
				}
			}
			
			f.removeLighting();
			
			f.writeAndClose();
		}}
	}
}
