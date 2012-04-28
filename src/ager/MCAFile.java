package ager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MCAFile {
	private final File file;
	private final int xOffset;
	private final int zOffset;
	private final Chunk[][] chunks = new Chunk[32][32];
	
	public MCAFile(File regions, int xOffset, int zOffset) throws FileNotFoundException, IOException {
		file = new File(regions, "r." + xOffset + "." + zOffset + ".mca");
		this.xOffset = xOffset;
		this.zOffset = zOffset;
		if (file.exists()) {
			int[] chunkOffsets = new int[32 * 32];
			int[] chunkSizes = new int[32 * 32];
			BufferedInputStream fis = null;
			try {
				fis = new BufferedInputStream(new FileInputStream(file));
				for (int i = 0; i < 32 * 32; i++) {
					for (int j = 0; j < 3; j++) {
						chunkOffsets[i] <<= 8;
						chunkOffsets[i] |= fis.read();
					}
					chunkSizes[i] = fis.read();
				}
				
				byte[] data = new byte[(int) file.length() - 32 * 32 * 4];
				fis.read(data);
				
				for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
					if (chunkSizes[z * 32 + x] > 0) {
						ByteArrayInputStream is = new ByteArrayInputStream(data,
								(chunkOffsets[z * 32 + x] - 1) * 4096,
								chunkSizes[z * 32 + x] * 4096);
						chunks[z][x] = new Chunk(is, data, chunkSizes[z * 32 + x] * 4096);
					}
				}}
			} finally {
				try { fis.close(); } catch (Exception e) {}
			}			
		}
	}
}
