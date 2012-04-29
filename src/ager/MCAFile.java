package ager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.minecraft.world.level.chunk.storage.RegionFile;

public class MCAFile {
	public final File file;
	public final int xOffset;
	public final int zOffset;
	public final Chunk[][] chunks = new Chunk[32][32];
	private RegionFile rf;
	
	public MCAFile(File regions, int xOffset, int zOffset) throws FileNotFoundException, IOException {
		file = new File(regions, "r." + xOffset + "." + zOffset + ".mca");
		this.xOffset = xOffset;
		this.zOffset = zOffset;
		if (file.exists()) {
			rf = new RegionFile(file);
			for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
				InputStream is = rf.getChunkDataInputStream(x, z);
				if (is != null) {
					chunks[z][x] = new Chunk(is);
					is.close();
				}
			}}
			/*int[] chunkOffsets = new int[32 * 32];
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
					System.err.println(chunkOffsets[i] + " " + chunkSizes[i]);
				}
								
				byte[] data = new byte[(int) file.length() - 32 * 32 * 4];
				fis.read(data);
				
				for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
					if (chunkSizes[z * 32 + x] > 0) {
						ByteArrayInputStream is = new ByteArrayInputStream(data,
								(chunkOffsets[z * 32 + x] - 1) * 4096,
								chunkSizes[z * 32 + x] * 4096);
						System.err.println("z " + z + " x " + x +  " " + chunkOffsets[z * 32 + x] + " " + chunkSizes[z * 32 + x]);
						chunks[z][x] = new Chunk(is);
					}
				}}
			} finally {
				try { fis.close(); } catch (Exception e) {}
			}*/
		}
	}
	
	public void removeLighting() {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].removeLighting();
			}
		}}
	}
	
	public void writeAndClose() throws IOException {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				OutputStream os = rf.getChunkDataOutputStream(x, z);
				chunks[z][x].t.writeTo(os);
				os.flush();
				os.close();
			}
		}}
		if (rf != null) { rf.close(); }
		/*byte[][][] savedChunks = new byte[32][32][0];
		int fourKBlocksNeeded = 1;
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				savedChunks[z][x] = chunks[z][x].write();
				fourKBlocksNeeded +=
						savedChunks[z][x].length % 4096 == 0
							? savedChunks[z][x].length / 4096
							: (savedChunks[z][x].length / 4096 + 1);
			}
		}}
		
		byte[] assembledData = new byte[fourKBlocksNeeded * 4096];
		int currentOffset = 1;
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				int blocksNeeded =
						savedChunks[z][x].length % 4096 == 0
							? savedChunks[z][x].length / 4096
							: (savedChunks[z][x].length / 4096 + 1);
				// First 3 bytes are the current offset.
				ByteArrayOutputStream foo = new ByteArrayOutputStream();
				DataOutputStream dfoo = new DataOutputStream(foo);
				dfoo.writeInt(currentOffset);
				dfoo.flush();
				byte[] offsBytes = foo.toByteArray();
				for (int j = 0; j < 3; j++) {
					assembledData[(z * 32 + x) * 4 + j] = offsBytes[1 + j];
				}
				// Final byte is number of blocks needed.
				assembledData[(z * 32 + x) * 4 + 3] = (byte) blocksNeeded;
				// Now copy in the data.
				System.arraycopy(savedChunks[z][x], 0, assembledData, currentOffset * 4096, savedChunks[z][x].length);
				// Increment the offset.
				currentOffset += blocksNeeded;
			}
		}}
		
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(assembledData);
		} finally {
			fos.flush();
			fos.close();
		}*/
	}
	
	public int getBlockType(int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return -1; }
		return chunks[chunkZ][chunkX].getBlockType(x % 16, y, z % 16);
	}
	
	public void setBlockType(byte type, int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setBlockType(type, x % 16, y, z % 16);
	}
	
	public int getSkyLight(int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return -1; }
		return chunks[chunkZ][chunkX].getSkyLight(x % 16, y, z % 16);
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setSkyLight(light, x % 16, y, z % 16);
	}
}
