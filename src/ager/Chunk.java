package ager;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.LinkedList;
import unknown.Tag;

public class Chunk {
	public Tag t;
	Tag[] sections = new Tag[16];
	public int globalChunkX, globalChunkZ;
	Chunk[][] chunkCtx = new Chunk[3][3];
	
	BitSet wasSupported = new BitSet(256 * 16 * 16); // yzx
	BitSet isSupported = new BitSet(256 * 16 * 16); // yzx
	boolean firstPass = true;
	final LinkedList<Pt3> q = new LinkedList<Pt3>();
	
	public void initSupport(boolean postRun) {
		firstPass = true;
		isSupported.clear();
		final BitSet supported = postRun ? isSupported : wasSupported;
		
		// Start out by going from the bottom and marking everything as supported until we hit air.
		for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
			int y = 0;
			int type = getBlockType(x, y, z);
			while (type != Types.Air) {
				supported.set(y * 256 + z * 16 + x);
				//q.add(new Pt3(x, y, z));
				if (y == 255) { break; }
				y++;
				type = getBlockType(x, y, z);
			}
		}}
	}
	
	public void floodFill(boolean postRun) {
		//if (2 * 2 == 4) { q.clear(); return; }
		
		final BitSet supported = postRun ? isSupported : wasSupported;
		if (firstPass) {
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
				if (supported.get(y * 256 + z * 16 + x)) {
					q.add(new Pt3(x, y, z));
				}
			}}}
			firstPass = false;
		}
		
		while (!q.isEmpty()) {
			Pt3 p = q.pop();
			for (int dy = -1; dy < 2; dy++) {
				int ny = p.y + dy;
				if (ny < 0 || ny >= 256) { continue; }
				for (int dx = -1; dx < 2; dx++) {
					int nx = p.x + dx;
					//if (nx < 0 || nx >= 16) { continue; }
					for (int dz = -1; dz < 2; dz++) {
						if (dy != 0 && dx != 0 && dz != 0) { continue; }
						if (dy == 0 && dx == 0 && dz == 0) { continue; }
						int nz = p.z + dz;
						//if (nz < 0 || nz >= 16) { continue; }
						if ((nx < 0 || nx >= 16) || (nz < 0 || nz >= 16)) {
							// We've crossed state lines, er, chunk boundaries!
							int chunkXOffset =
									nx < 0 ? -1 : nx >= 16 ? 1 : 0;
							int chunkZOffset =
									nz < 0 ? -1 : nz >= 16 ? 1 : 0;
							Chunk targetChunk = chunkCtx[chunkZOffset + 1][chunkXOffset + 1];
							if (targetChunk == null) { continue; }
							int xInOtherChunk = (nx + 16) % 16;
							int zInOtherChunk = (nx + 16) % 16;
							if (!(postRun ? targetChunk.isSupported : targetChunk.wasSupported).get(ny * 256 + zInOtherChunk * 16 + xInOtherChunk) && getBlockType(xInOtherChunk, ny, zInOtherChunk) > Types.Air) {
								(postRun ? targetChunk.isSupported : targetChunk.wasSupported).set(ny * 256 + zInOtherChunk * 16 + xInOtherChunk);
								targetChunk.q.add(new Pt3(xInOtherChunk, ny, zInOtherChunk));
							}
						} else {
							if (!supported.get(ny * 256 + nz * 16 + nx) && getBlockType(nx, ny, nz) > Types.Air) {
								supported.set(ny * 256 + nz * 16 + nx);
								q.add(new Pt3(nx, ny, nz));
							}
						}
					}
				}
			}
		}
	}
	
	public Chunk(InputStream is, int globalChunkX, int globalChunkZ) throws IOException {
		t = Tag.readFrom(is);
		Tag[] sArray = (Tag[]) t.findTagByName("Level").findTagByName("Sections").getValue();
		for (Tag section : sArray) {
			this.sections[(Byte) section.findTagByName("Y").getValue()] = section;
		}
		this.globalChunkX = globalChunkX;
		this.globalChunkZ = globalChunkZ;
	}
	
	
	
	public void removeLighting() {
		byte[] empty = new byte[2048];
		
		for (int y = 0; y < 16; y++) {
			if (sections[y] != null) {
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("BlockLight").getValue(), 0, 2048);
			}
		}
	}
	
	public boolean getSupported(int x, int y, int z, boolean postRun) {
		return (postRun ? isSupported : wasSupported).get(y * 256 + z * 16 + x); 
	}
	
	public void setSupported(boolean supported, int x, int y, int z, boolean postRun) {
		(postRun ? isSupported : wasSupported).set(y * 256 + z * 16 + x, supported);
	}
	
	public int getBlockType(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		try {
			return ((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)];
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(x + "/" + y + "/" + z);
			throw new RuntimeException(e);
		}
	}
	
	public void setBlockType(byte type, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)] = type;
	}
	
	public static int getNybble(byte b, int offset) {
		int val = b < 0 ? b + 256 : b;
		return offset == 0 ? (val % 16) : (val / 16);
	}
	
	public static byte setNybble(byte b, int offset, int value) {
		int val = b < 0 ? b + 256 : b;
		int result = offset == 0 ? ((val / 16) * 16 + value) : (val % 16 + value * 16);
		if (result > 127) {
			result -= 256;
		}
		return (byte) result;
	}
	
	public int getData(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setData(byte data, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2] = setNybble(b, addr % 2, data);
	}
	
	public int getSkyLight(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
	}
	
	public int getBlockLight(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setBlockLight(byte light, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
	}
}
