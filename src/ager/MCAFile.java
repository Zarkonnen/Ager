package ager;

import java.io.File;
import java.io.FileNotFoundException;
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
	}
	
	public void calcSupport(boolean postRun) {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].calcSupport(postRun);
			}
		}}
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
	
	public int getData(int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return -1; }
		return chunks[chunkZ][chunkX].getData(x % 16, y, z % 16);
	}
	
	public void setData(byte data, int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setData(data, x % 16, y, z % 16);
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
	
	public int getBlockLight(int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return -1; }
		return chunks[chunkZ][chunkX].getBlockLight(x % 16, y, z % 16);
	}
	
	public void setBlockLight(byte light, int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setBlockLight(light, x % 16, y, z % 16);
	}
}
