package ager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.world.level.chunk.storage.RegionFile;
import unknown.Tag;

public class MCAFile {
	public final File file;
	public final int xOffset;
	public final int zOffset;
	public final Chunk[][] chunks = new Chunk[32][32];
	private RegionFile rf;
	private PooledPagingByteArray.Pool pool = new PooledPagingByteArray.Pool();
	
	public MCAFile(File regions, int xOffset, int zOffset, ArrayList<Chunk> loadedChunkCache, int maxChunksLoaded) throws FileNotFoundException, IOException {
		file = new File(regions, "r." + xOffset + "." + zOffset + ".mca");
		this.xOffset = xOffset;
		this.zOffset = zOffset;
		if (file.exists()) {
			rf = new RegionFile(file);
			for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
				InputStream is = rf.getChunkDataInputStream(x, z);
				if (is != null) {
					chunks[z][x] = new Chunk(rf, x, z, x + xOffset * 32, z + zOffset * 32, loadedChunkCache, maxChunksLoaded, pool);
					is.close();
				}
			}}
		}
	}
	
	public Chunk getChunk(int x, int z) {
		return chunks[z][x];
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
				/*OutputStream os = rf.getChunkDataOutputStream(x, z);
				chunks[z][x].t.writeTo(os);
				os.flush();
				os.close();*/
				chunks[z][x].save();
			}
		}}
		if (rf != null) { rf.close(); }
	}
	
	public void newInitSupport(Blinkenlights bl) {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].newInitSupport();
				if (bl != null) { bl.repaint(); }
			}
		}}
	}
	
	public boolean newFinishSupport(Blinkenlights bl, int dragMultiplier) {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].newFloodFill(dragMultiplier);
				if (bl != null) { bl.repaint(); }
			}
		}}
			
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null && !chunks[z][x].nq.isEmpty()) {
				return false;
			}
		}}
			
		return true;
	}
	
	public boolean getPartOfBlob(int x, int y, int z) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return false; }
		return chunks[chunkZ][chunkX].getPartOfBlob(x % 16, y, z % 16);
	}
	
	public void setPartOfBlob(int x, int y, int z, boolean value) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setPartOfBlob(x % 16, y, z % 16, value);
	}
	
	public void clearPartOfBlob() {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].clearPartOfBlob();
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
	
	public void clearTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].clearTileEntity(x % 16, y, z % 16, globX, globY, globZ);
	}
	
	public Tag getTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return null; }
		return chunks[chunkZ][chunkX].getTileEntity(x % 16, y, z % 16, globX, globY, globZ);
	}
	
	public void setTileEntity(Tag te, int x, int y, int z, int globX, int globY, int globZ) {
		int chunkX = x / 16;
		int chunkZ = z / 16;
		if (chunks[chunkZ][chunkX] == null) { return; }
		chunks[chunkZ][chunkX].setTileEntity(te, x % 16, y, z % 16, globX, globY, globZ);
	}
	
	public void calculateInitialSkyLights(Blinkenlights bl) {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].calculateInitialSkyLights();
				if (bl != null) { bl.repaint(); }
			}
		}}
	}
	
	public boolean finishSkyLights(Blinkenlights bl) {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].skyLightFloodFill();
				if (bl != null) { bl.repaint(); }
			}
		}}
			
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null && !chunks[z][x].lightQ.isEmpty()) {
				return false;
			}
		}}
			
		return true;
	}

	void clearAllEntities() {
		for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
			if (chunks[z][x] != null) {
				chunks[z][x].clearAllEntities();
			}
		}}
	}
}
