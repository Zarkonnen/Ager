package ager;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import unknown.Tag;

public class Chunk {
	public Tag t;
	Tag[] sections = new Tag[16];
	byte[][] sectionBlocks = new byte[16][0];
	public int globalChunkX, globalChunkZ;
	Chunk[][] chunkCtx = new Chunk[3][3];
	
	BitSet wasSupported = new BitSet(256 * 16 * 16); // yzx
	BitSet isSupported = new BitSet(256 * 16 * 16); // yzx
	boolean firstPass = true;
	final BytePt3Stack q = new BytePt3Stack(8);
	
	BitSet partOfBlob = new BitSet(256 * 16 * 16);
	
	final BytePt4Stack lightQ = new BytePt4Stack(8);
	boolean lightFirstPass = true;
	byte[][] sectionSkyLights = new byte[16][0];
	
	// New support code!
	static final byte[] NO_SUPPORT = new byte[256 * 16 * 16];
	byte[] supported = new byte[256 * 16 * 16];
	boolean nFirstPass = true;
	final BytePt4Stack nq = new BytePt4Stack(8);
	
	public Chunk(InputStream is, int globalChunkX, int globalChunkZ) throws IOException {
		t = Tag.readFrom(is);
		Tag[] sArray = (Tag[]) t.findTagByName("Level").findTagByName("Sections").getValue();
		for (Tag section : sArray) {
			this.sections[(Byte) section.findTagByName("Y").getValue()] = section;
			this.sectionBlocks[(Byte) section.findTagByName("Y").getValue()] = (byte[]) section.findTagByName("Blocks").getValue();
			this.sectionSkyLights[(Byte) section.findTagByName("Y").getValue()] = (byte[]) section.findTagByName("SkyLight").getValue();
		}
		this.globalChunkX = globalChunkX;
		this.globalChunkZ = globalChunkZ;
	}
	
	public void initSupport(boolean postRun) {
		firstPass = true;
		isSupported.clear();
		final BitSet supported = postRun ? isSupported : wasSupported;
		
		// Start out by going from the bottom and marking everything as supported until we hit air.
		for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
			int y = 0;
			int type = getBlockType(x, y, z);
			//while (type != Types.Air) {
			while (true) {
				supported.set(y * 256 + z * 16 + x);
				if (y == 255) { break; }
				if (type != -1 && !Rules.providesSupport[type + 1]) {
					break;
				}
				y++;
				type = getBlockType(x, y, z);
			}
		}}
	}
	
	public void floodFill(boolean postRun) {
		/*if (2 * 2 == 4) { // qqDPS
			q.clear();
			return;
		}*/
		
		final BitSet supported = postRun ? isSupported : wasSupported;
		if (firstPass) {
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
				if (supported.get(y * 256 + z * 16 + x) && Rules.providesSupport[getBlockType(x, y, z) + 1]) {
					q.push(x, y, z);
				}
			}}}
			firstPass = false;
		}
		
		while (!q.isEmpty()) {
			q.pop();
			for (int dy = -1; dy < 2; dy++) {
				int ny = q.y + dy;
				if (ny < 0 || ny >= 256) { continue; }
				for (int dx = -1; dx < 2; dx++) {
					int nx = q.x + dx;
					//if (nx < 0 || nx >= 16) { continue; }
					for (int dz = -1; dz < 2; dz++) {
						if (dy != 0 && dx != 0 && dz != 0) { continue; }
						if (dy == 0 && dx == 0 && dz == 0) { continue; }
						int nz = q.z + dz;
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
							int zInOtherChunk = (nz + 16) % 16;
							//if (getBlockType(xInOtherChunk, ny, zInOtherChunk) > Types.Air && !(postRun ? targetChunk.isSupported : targetChunk.wasSupported).get(ny * 256 + zInOtherChunk * 16 + xInOtherChunk))
							int type = targetChunk.getBlockType(xInOtherChunk, ny, zInOtherChunk);
							if (
								type > Types.Air &&
								((dy == 1 && dx == 0 && dz == 0) || !Rules.needsSupportFromBelow[type + 1]) &&
								((dy != 0 && dx == 0 && dz == 0) || (dy == 0 && dx != 0 && dz == 0) || (dy == 0 && dx == 0 && dz != 0) || !Rules.needsSupportFromFaces[type + 1]) &&
								!(postRun ? targetChunk.isSupported : targetChunk.wasSupported).get(ny * 256 + zInOtherChunk * 16 + xInOtherChunk)
							)
							{
								(postRun ? targetChunk.isSupported : targetChunk.wasSupported).set(ny * 256 + zInOtherChunk * 16 + xInOtherChunk);
								if (Rules.providesSupport[type + 1]) {
									targetChunk.q.push(xInOtherChunk, ny, zInOtherChunk);
								}
							}
						} else {
							//if (getBlockType(nx, ny, nz) > Types.Air && !supported.get(ny * 256 + nz * 16 + nx)) {
							int type = getBlockType(nx, ny, nz);
							if (type > Types.Air &&
								((dy == 1 && dx == 0 && dz == 0) || !Rules.needsSupportFromBelow[type + 1]) &&
								((dy != 0 && dx == 0 && dz == 0) || (dy == 0 && dx != 0 && dz == 0) || (dy == 0 && dx == 0 && dz != 0) || !Rules.needsSupportFromFaces[type + 1]) &&
								!supported.get(ny * 256 + nz * 16 + nx))
							{
								supported.set(ny * 256 + nz * 16 + nx);
								if (Rules.providesSupport[type + 1]) {
									q.push(nx, ny, nz);
								}
							}
						}
					}
				}
			}
		}
		
		q.compactTo(8);
	}
	
	public void newInitSupport() {
		nFirstPass = true;
		System.arraycopy(NO_SUPPORT, 0, supported, 0, NO_SUPPORT.length);
		
		// Start out by going from the bottom and marking everything as supported until we hit air.
		for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
			int y = 0;
			int type = getBlockType(x, y, z);
			while (true) {
				supported[y * 256 + z * 16 + x] = 100;
				if (y == 255) { break; }
				if (type != -1 && !Rules.providesSupport[type + 1]) {
					break;
				}
				y++;
				type = getBlockType(x, y, z);
			}
		}}
	}
	
	static final int[] DX = { 0,  0,  0, -1,  1, -1, -1, -1,  0,  0,  1,  1,  1,  0,  0,  0, -1,  1};
	static final int[] DY = {-1, -1, -1, -1, -1,  0,  0,  0,  0,  0,  0,  0,  0,  1,  1,  1,  1,  1};
	static final int[] DZ = { 0, -1,  1,  0,  0, -1,  0,  1, -1,  1, -1,  0,  1,  0, -1,  1,  0,  0};
	static final int[] SC = { 2,  2,  2,  2,  2,  4,  1,  4,  1,  1,  4,  1,  4,  0,  2,  2,  2,  2};
	
	public void newFloodFill() {
		/*if (2 * 2 == 4) { // qqDPS
			q.clear();
			return;
		}*/
		
		if (nFirstPass) {
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
				byte supp = supported[y * 256 + z * 16 + x];
				if (supp > 0 && Rules.providesSupport[getBlockType(x, y, z) + 1]) {
					nq.push(x, y, z, supp);
				}
			}}}
			nFirstPass = false;
		}
		
		while (!nq.isEmpty()) {
			nq.pop();
			int srcType = getBlockType(nq.x, nq.y, nq.z);
			int srcSupport = nq.l;
			for (int j = 0; j < SC.length; j++) {
				int nx = nq.x + DX[j];
				int ny = nq.y + DY[j];
				int nz = nq.z + DZ[j];
				Chunk targetChunk = this;
				if ((nx < 0 || nx >= 16) || (nz < 0 || nz >= 16)) {
					// We've crossed state lines, er, chunk boundaries!
					int chunkXOffset =
							nx < 0 ? -1 : nx >= 16 ? 1 : 0;
					int chunkZOffset =
							nz < 0 ? -1 : nz >= 16 ? 1 : 0;
					targetChunk = chunkCtx[chunkZOffset + 1][chunkXOffset + 1];
					if (targetChunk == null) { continue; }
					nx = (nx + 16) % 16;
					nz = (nz + 16) % 16;
				}
				int targetType = targetChunk.getBlockType(nx, ny, nz);
				if (targetType == -1) { continue; }
				if (Rules.needsSupportFromBelow[targetType + 1] && !(DX[j] == 0 && DY[j] == 1 && DZ[j] == 0)) {
					continue;
				}
				if ((Rules.needsSupportFromFaces[srcType + 1] || Rules.needsSupportFromFaces[targetType + 1]) && (Math.abs(DX[j]) + Math.abs(DY[j]) + Math.abs(DZ[j]) > 1)) {
					continue;
				}
				int newSupport = srcSupport - SC[j] * Rules.support[srcType + 1];
				if (newSupport > 0 && Rules.weight[targetType + 1] <= newSupport && newSupport > targetChunk.supported[ny * 256 + nz * 16 + nx]) {
					targetChunk.supported[ny * 256 + nz * 16 + nx] = (byte) newSupport;
					if (!Rules.providesSupport[targetType + 1]) { continue; }
					int continuedSupport = Math.min(Rules.maxSupport[targetType + 1], newSupport);
					if (continuedSupport > 0) {
						targetChunk.nq.push(nx, ny, nz, continuedSupport);
					}
				}
			}
		}
		
		nq.compactTo(8);
	}
	
	public void removeLighting() {
		byte[] empty = new byte[2048];
		
		for (int y = 0; y < 16; y++) {
			if (sections[y] != null) {
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("BlockLight").getValue(), 0, 2048);
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("SkyLight").getValue(), 0, 2048);
			}
		}
	}
	
	public boolean getPartOfBlob(int x, int y, int z) {
		return partOfBlob.get(y * 256 + z * 16 + x);
	}
	
	public void setPartOfBlob(int x, int y, int z, boolean value) {
		partOfBlob.set(y * 256 + z * 16 + x, value);
	}
	
	public void clearPartOfBlob() {
		partOfBlob.clear();
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
			//return ((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)];
			return sectionBlocks[section][((remY * 16 + z) * 16 + x)];
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
		//((byte[]) sections[section].findTagByName("Blocks").getValue())[((remY * 16 + z) * 16 + x)] = type;
		sectionBlocks[section][((remY * 16 + z) * 16 + x)] = type;
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
		//byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		byte b = sectionSkyLights[section][addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		//byte b = ((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2];
		byte b = sectionSkyLights[section][addr / 2];
		//((byte[]) sections[section].findTagByName("SkyLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
		sectionSkyLights[section][addr / 2] = setNybble(b, addr % 2, light);
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
	
	public void calculateInitialSkyLights() {
		for (int z = 0; z < 16; z++) { lp: for (int x = 0; x < 16; x++) {
			int y = 255;
			int l = 15;
			int type = getBlockType(x, y, z);
			while (Rules.transparent[type + 1]) {
				l -= Rules.extraLightAttenuation[type + 1];
				setSkyLight((byte) l, x, y, z);
				if (l <= 0) { continue lp; }
				//lightQ.push(x, y, z, l);
				y--;
				if (y < 0) { continue lp; }
				type = getBlockType(x, y, z);
			}
		}}
		lightFirstPass = true;
	}
	
	static final int[] NS_X = {-1, 1, 0, 0, 0, 0 };
	static final int[] NS_Y = { 0, 0,-1, 1, 0, 0 };
	static final int[] NS_Z = { 0, 0, 0, 0,-1, 1 };
	
	public void skyLightFloodFill() {
		if (lightFirstPass) {
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { lp: for (int x = 0; x < 16; x++) {
				int l = getSkyLight(x, y, z);
				if (l > 0) {
					lightQ.push(x, y, z, l);
				}
			}}}
			lightFirstPass = false;
		}
		
		while (!lightQ.isEmpty()) {
			lightQ.pop();
			for (int j = 0; j < 6; j++) {
				int nx = lightQ.x + NS_X[j];
				int ny = lightQ.y + NS_Y[j];
				int nz = lightQ.z + NS_Z[j];
				if (ny < 0 || ny >= 256) { continue; }
				
				if ((nx < 0 || nx >= 16) || (nz < 0 || nz >= 16)) {
					// We've crossed state lines, er, chunk boundaries!
					int chunkXOffset =
							nx < 0 ? -1 : nx >= 16 ? 1 : 0;
					int chunkZOffset =
							nz < 0 ? -1 : nz >= 16 ? 1 : 0;
					Chunk targetChunk = chunkCtx[chunkZOffset + 1][chunkXOffset + 1];
					if (targetChunk == null) { continue; }
					int xInOtherChunk = (nx + 16) % 16;
					int zInOtherChunk = (nz + 16) % 16;
					
					int localType = targetChunk.getBlockType(xInOtherChunk, ny, zInOtherChunk);
					if (!Rules.transparent[localType + 1]) { continue; }
					int localL = targetChunk.getSkyLight(xInOtherChunk, ny, zInOtherChunk);
					if (localL == -1) { continue; } // There is no block there.
					int newLight = lightQ.l - Rules.extraLightAttenuation[localType + 1];
					if (newLight <= 0) { continue; }
					if (localL >= newLight) { continue; } // It's already as bright or brighter than we can make it.
					targetChunk.setSkyLight((byte) newLight, xInOtherChunk, ny, zInOtherChunk);
					if (newLight > 1) {
						targetChunk.lightQ.push(xInOtherChunk, ny, zInOtherChunk, newLight - 1);
					}
				} else {
					int localType = getBlockType(nx, ny, nz);
					if (!Rules.transparent[localType + 1]) { continue; }
					int localL = getSkyLight(nx, ny, nz);
					if (localL == -1) { continue; } // There is no block there.
					int newLight = lightQ.l - Rules.extraLightAttenuation[localType + 1];
					if (newLight <= 0) { continue; }
					if (localL >= newLight) { continue; } // It's already as bright or brighter than we can make it.
					setSkyLight((byte) newLight, nx, ny, nz);
					if (newLight > 1) {
						lightQ.push(nx, ny, nz, newLight - 1);
					}
				}
			}
		}
		
		lightQ.compactTo(8);
	}
	
	public void clearTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
		if (y < 0 || y > 255) { return; }
		Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
		Tag[] tes = (Tag[]) tesN.getValue();
		//System.out.println(tes);
		for (int i = 0; i < tes.length; i++) {
			/*System.out.println(tes[i].findTagByName("x").getValue());
			System.out.println(tes[i].findTagByName("y").getValue());
			System.out.println(tes[i].findTagByName("z").getValue());
			System.out.println();*/
			if (((Integer) tes[i].findTagByName("x").getValue()).equals(globX) &&
				((Integer) tes[i].findTagByName("y").getValue()).equals(globY) &&
				((Integer) tes[i].findTagByName("z").getValue()).equals(globZ))
			{
				tesN.removeTag(i);
				//System.out.println("FOUND AND REMOVED!");
				return;
			}
		}
	}
	
	public Tag getTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
		if (y < 0 || y > 255) { return null; }
		Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
		Tag[] tes = (Tag[]) tesN.getValue();
		for (int i = 0; i < tes.length; i++) {
			if (((Integer) tes[i].findTagByName("x").getValue()).equals(globX) &&
				((Integer) tes[i].findTagByName("y").getValue()).equals(globY) &&
				((Integer) tes[i].findTagByName("z").getValue()).equals(globZ))
			{
				return tes[i];
			}
		}
		
		return null;
	}
	
	public void setTileEntity(Tag te, int x, int y, int z, int globX, int globY, int globZ) {
		if (y < 0 || y > 255) { return; }
		Tag tesN = t.findTagByName("Level").findTagByName("TileEntities");
		Tag[] tes = (Tag[]) tesN.getValue();
		for (int i = 0; i < tes.length; i++) {
			if (((Integer) tes[i].findTagByName("x").getValue()).equals(globX) &&
				((Integer) tes[i].findTagByName("y").getValue()).equals(globY) &&
				((Integer) tes[i].findTagByName("z").getValue()).equals(globZ))
			{
				tes[i] = te;
				return;
				/*tesN.removeTag(i);
				break;*/
			}
		}
		
		tesN.addTag(te);
	}

	public void clearAllEntities() {
		t.findTagByName("Level").findTagByName("Entities").clearList();//setValue(Tag.Type.TAG_Compound);
	}
}
