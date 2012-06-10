package ager;

import java.io.OutputStream;
import java.util.LinkedList;
import net.minecraft.world.level.chunk.storage.RegionFile;
import unknown.Tag;

public class Chunk {
	private Tag t;
	private Tag[] sections = new Tag[16];
	private byte[][] sectionBlocks = new byte[16][0];
	public int globalChunkX, globalChunkZ;
	Chunk[][] chunkCtx = new Chunk[3][3];
	
	//BitSet partOfBlob = new BitSet(256 * 16 * 16);
	PooledPagingByteArray partOfBlob;
	
	final BytePt4Stack lightQ = new BytePt4Stack(8);
	boolean lightFirstPass = true;
	byte[][] sectionSkyLights = new byte[16][0];
	
	// New support code!
	static final byte[] NO_SUPPORT = new byte[256 * 16 * 16];
	//byte[] supported = new byte[256 * 16 * 16];
	PooledPagingByteArray supported;
	boolean nFirstPass = true;
	final BytePt4Stack nq = new BytePt4Stack(8);
	
	// Loading
	LinkedList<Chunk> loadedChunkCache;
	RegionFile rf;
	int rfX, rfZ;
	int maxChunksLoaded;
	
	public boolean processed = false;

	public Chunk(RegionFile rf, int rfX, int rfZ, int globalChunkX, int globalChunkZ, LinkedList<Chunk> loadedChunkCache, int maxChunksLoaded, PooledPagingByteArray.Pool pool) {
		this.rf = rf;
		this.rfX = rfX;
		this.rfZ = rfZ;
		this.globalChunkX = globalChunkX;
		this.globalChunkZ = globalChunkZ;
		this.loadedChunkCache = loadedChunkCache;
		this.maxChunksLoaded = maxChunksLoaded;
		supported = pool.getArray(256 * 16 * 16);
		partOfBlob = pool.getArray(256 * 16 * 16);
	}
	
	public boolean loaded() {
		return t != null;
	}
	
	public void demoteNeighboursOrSelfIfDone() {
		if (!processed) { return; }
		for (int z = 0; z < 3; z++) { for (int x = 0; x < 3; x++) {
			if (chunkCtx[z][x] != null && chunkCtx[z][x].processed && chunkCtx[z][x].neighboursDone()) {
				/*chunkCtx[z][x].save();
				loadedChunkCache.remove(chunkCtx[z][x]);
				System.out.println("Stabbing neighbours.");*/
				loadedChunkCache.remove(chunkCtx[z][x]);
				loadedChunkCache.push(chunkCtx[z][x]);
			}
		}}
		
		if (neighboursDone()) {
			//save();
			loadedChunkCache.remove(this);
			loadedChunkCache.push(this);
		}
	}
	
	public boolean neighboursDone() {
		for (int z = 0; z < 3; z++) { for (int x = 0; x < 3; x++) {
			if (chunkCtx[z][x] != null && !chunkCtx[z][x].processed) { return false; }
		}}
		return true;
	}
	
	public Chunk prepare() {
		if (!loaded()) {
			if (loadedChunkCache.size() >= maxChunksLoaded) {
				// Should prolly just take X.
				int pops = Math.max(1, Math.min(40, loadedChunkCache.size() / 8));
				System.out.println("Popping " + pops);
				for (int i = 0; i < pops; i++) {
					loadedChunkCache.pop().save();
				}
			}
			load();
		} else {
			loadedChunkCache.remove(this);
		}
		loadedChunkCache.add(this);
		//System.out.println(loadedChunkCache.size());
		return this;
	}
	
	private void load() {
		if (loaded()) { return; }
		try {
			t = Tag.readFrom(rf.getChunkDataInputStream(rfX, rfZ));
			Tag[] sArray = (Tag[]) t.findTagByName("Level").findTagByName("Sections").getValue();
			for (Tag section : sArray) {
				this.sections[(Byte) section.findTagByName("Y").getValue()] = section;
				this.sectionBlocks[(Byte) section.findTagByName("Y").getValue()] = (byte[]) section.findTagByName("Blocks").getValue();
				this.sectionSkyLights[(Byte) section.findTagByName("Y").getValue()] = (byte[]) section.findTagByName("SkyLight").getValue();
			}
			supported.pageIn();
			partOfBlob.pageIn();
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public void save() {
		if (!loaded()) { return; }
		try {
			OutputStream os = rf.getChunkDataOutputStream(rfX, rfZ);
			t.writeTo(os);
			os.close();
			t = null;
			sections = new Tag[16];
			sectionBlocks = new byte[16][0];
			sectionSkyLights = new byte[16][0];
			supported.pageOut();
			partOfBlob.pageOut();
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public Tag[] sections() {
		prepare();
		return sections;
	}
	
	public void newInitSupport() {
		prepare();
		nFirstPass = true;
		System.arraycopy(NO_SUPPORT, 0, supported.array, 0, NO_SUPPORT.length);
		
		// Start out by going from the bottom and marking everything as supported until we hit air.
		for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
			int y = 0;
			int type = getBlockType(x, y, z);
			while (true) {
				supported.array[y * 256 + z * 16 + x] = 100;
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
	static final int[] SC = { 2,  3,  3,  3,  3,  5,  1,  5,  1,  1,  5,  1,  5,  0,  3,  3,  3,  3};
	
	public void newFloodFill() {
		//prepare();
		/*if (2 * 2 == 4) { // qqDPS
			q.clear();
			return;
		}*/
		
		if (nFirstPass) {
			prepare();
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { for (int x = 0; x < 16; x++) {
				byte supp = supported.array[y * 256 + z * 16 + x];
				if (supp > 0 && Rules.providesSupport[getBlockType(x, y, z) + 1]) {
					nq.push(x, y, z, supp);
				}
			}}}
			nFirstPass = false;
		} else {
			if (!nq.isEmpty()) {
				prepare();
			}
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
					targetChunk.prepare();
					prepare();
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
				if (newSupport > 0 && Rules.weight[targetType + 1] <= newSupport && newSupport > targetChunk.supported.array[ny * 256 + nz * 16 + nx]) {
					targetChunk.supported.array[ny * 256 + nz * 16 + nx] = (byte) newSupport;
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
		prepare();
		byte[] empty = new byte[2048];
		
		for (int y = 0; y < 16; y++) {
			if (sections[y] != null) {
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("BlockLight").getValue(), 0, 2048);
				System.arraycopy(empty, 0, (byte[]) sections[y].findTagByName("SkyLight").getValue(), 0, 2048); // qqDPS
			}
		}
	}
	
	public boolean getPartOfBlob(int x, int y, int z) {
		//prepare();
		return partOfBlob.array[y * 256 + z * 16 + x] > 0;
		//return partOfBlob.get(y * 256 + z * 16 + x);
	}
	
	public void setPartOfBlob(int x, int y, int z, boolean value) {
		//prepare();
		partOfBlob.array[y * 256 + z * 16 + x] = (byte) (value ? 1 : 0);
		//partOfBlob.set(y * 256 + z * 16 + x, value);
	}
	
	public void clearPartOfBlob() {
		partOfBlob.reset();
	}
	
	public int getBlockType(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		//prepare();
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
		//prepare();
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
		//prepare();
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setData(byte data, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		//prepare();
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("Data").getValue())[addr / 2] = setNybble(b, addr % 2, data);
	}
	
	public int getSkyLight(int x, int y, int z) {
		if (y < 0 || y > 255) { return -1; }
		//prepare();
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
		//prepare();
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
		//prepare();
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return -1; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2];
		return getNybble(b, addr % 2);
	}
	
	public void setBlockLight(byte light, int x, int y, int z) {
		if (y < 0 || y > 255) { return; }
		//prepare();
		int section = y / 16;
		int remY = y % 16;
		if (sections[section] == null) { return; }
		int addr = ((remY * 16 + z) * 16 + x);
		byte b = ((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2];
		((byte[]) sections[section].findTagByName("BlockLight").getValue())[addr / 2] = setNybble(b, addr % 2, light);
	}
	
	public void calculateInitialSkyLights() {
		prepare();
		for (int z = 0; z < 16; z++) { lp: for (int x = 0; x < 16; x++) {
			int y = 255;
			int l = 15;
			int type = getBlockType(x, y, z);
			while (Rules.transparent[type + 1]) {
				l -= Rules.extraLightAttenuation[type + 1];
				if (l < 0) { l = 0; }
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
		//prepare();
		if (lightFirstPass) {
			prepare();
			for (int y = 0; y < 256; y++) { for (int z = 0; z < 16; z++) { lp: for (int x = 0; x < 16; x++) {
				int l = getSkyLight(x, y, z);
				if (l > 0) {
					lightQ.push(x, y, z, l);
				}
			}}}
			lightFirstPass = false;
		} else {
			if (!lightQ.isEmpty()) {
				prepare();
			}
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
					//if (!Rules.transparent[localType + 1]) { continue; } // qqDPS
					int localL = targetChunk.getSkyLight(xInOtherChunk, ny, zInOtherChunk);
					if (localL == -1) { continue; } // There is no block there.
					int newLight = lightQ.l - Rules.extraLightAttenuation[localType + 1];
					if (newLight <= 0) { continue; }
					if (localL >= newLight) { continue; } // It's already as bright or brighter than we can make it.
					targetChunk.setSkyLight((byte) newLight, xInOtherChunk, ny, zInOtherChunk);
					if (newLight > 1 && Rules.transparent[localType + 1]) { // qqDPS
						targetChunk.lightQ.push(xInOtherChunk, ny, zInOtherChunk, newLight - 1);
					}
				} else {
					int localType = getBlockType(nx, ny, nz);
					//if (!Rules.transparent[localType + 1]) { continue; }
					int localL = getSkyLight(nx, ny, nz);
					if (localL == -1) { continue; } // There is no block there.
					int newLight = lightQ.l - Rules.extraLightAttenuation[localType + 1];
					if (newLight <= 0) { continue; }
					if (localL >= newLight) { continue; } // It's already as bright or brighter than we can make it.
					setSkyLight((byte) newLight, nx, ny, nz);
					if (newLight > 1 && Rules.transparent[localType + 1]) {
						lightQ.push(nx, ny, nz, newLight - 1);
					}
				}
			}
		}
		
		lightQ.compactTo(8);
	}
	
	public void clearTileEntity(int x, int y, int z, int globX, int globY, int globZ) {
		if (y < 0 || y > 255) { return; }
		//prepare();
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
		//prepare();
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
		//prepare();
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
		prepare();
		t.findTagByName("Level").findTagByName("Entities").clearList();//setValue(Tag.Type.TAG_Compound);
	}
}
