package ager;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import unknown.Tag;

public class MCMap {
	public final HashMap<Point, MCAFile> files = new HashMap<Point, MCAFile>();
	private final File worldF;

	public MCMap(File worldF) throws FileNotFoundException, IOException {
		this.worldF = worldF;
		
		for (File f : new File(worldF, "region").listFiles()) {
			if (f.getName().endsWith(".mca")) {
				String[] bits = f.getName().split("[.]");
				int x = Integer.parseInt(bits[1]);
				int z = Integer.parseInt(bits[2]);
				files.put(new Point(x, z), new MCAFile(new File(worldF, "region"), x, z));
			}
		}
		
		for (MCAFile f : files.values()) {
			for (int z = 0; z < 32; z++) { for (int x = 0; x < 32; x++) {
				int chunkX = f.xOffset * 32 + x;
				int chunkZ = f.zOffset * 32 + z;
				Chunk ch = getChunk(chunkX, chunkZ);
				if (ch == null) { continue; }
				for (int dz = -1; dz < 2; dz++) { for (int dx = -1; dx < 2; dx++) {
					Chunk ch2 = getChunk(chunkX + dx, chunkZ + dz);
					if (ch2 != null) {
						ch.chunkCtx[dz + 1][dx + 1] = ch2;
					}
				}}
			}}
		}
	}
	
	public void writeAndClose() throws IOException {
		for (MCAFile f : files.values()) {
			f.writeAndClose();
		}
	}
	
	static int fileC(int c) {
		return c < 0 ? ((c + 1) / 512 - 1) : c / 512;
	}
	
	static Point fileP(int x, int y, int z) {
		return new Point(fileC(x), fileC(z));
	}
	
	static int rem(int c) {
		return c - fileC(c) * 512;
	}
	
	static int chunkFileC(int c) {
		return c < 0 ? ((c + 1) / 32 - 1) : c / 32;
	}
	
	static Point chunkFileP(int x, int z) {
		return new Point(chunkFileC(x), chunkFileC(z));
	}
	
	static int chunkRem(int c) {
		return c - chunkFileC(c) * 32;
	}
	
	public final Chunk getChunk(int chunkX, int chunkZ) {
		Point fp = chunkFileP(chunkX, chunkZ);
		if (!files.containsKey(fp)) { return null; }
		return files.get(fp).getChunk(chunkRem(chunkX), chunkRem(chunkZ));
	}
	
	public boolean getPartOfBlob(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return false; }
		return files.get(fp).getPartOfBlob(rem(x), y, rem(z));
	}
	
	public void setPartOfBlob(int x, int y, int z, boolean value) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setPartOfBlob(rem(x), y, rem(z), value);
	}
	
	public void clearPartOfBlob() {
		for (MCAFile f : files.values()) {
			f.clearPartOfBlob();
		}
	}
	
	public int getBlockType(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return -1; }
		return files.get(fp).getBlockType(rem(x), y, rem(z));
	}
	
	public void setBlockType(byte type, int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setBlockType(type, rem(x), y, rem(z));
	}
	
	public void healBlockLight(int x, int y, int z) {
		int max = 0;
		for (int dz = -1; dz < 2; dz++) { for (int dx = -1; dx < 2; dx++) { for (int dy = -1; dy < 2; dy++) {
			if (dz != 0 && dz != 0 && dy != 0) { continue; }
			//if (dz == 0 && dx == 0 && dy == 0) { continue; }
			max = Math.max(getBlockLight(x + dx, y + dy, z + dz), max);
		}}}
		setBlockLight((byte) max, x, y, z);
	}
	
	public void healSkyLight(int x, int y, int z) {
		int max = 0;
		for (int dz = -1; dz < 2; dz++) { for (int dx = -1; dx < 2; dx++) { for (int dy = -1; dy < 2; dy++) {
			if (dz != 0 && dz != 0 && dy != 0) { continue; }
			//if (dz == 0 && dx == 0 && dy == 0) { continue; }
			max = Math.max(getSkyLight(x + dx, y + dy, z + dz), max);
		}}}
		setSkyLight((byte) max, x, y, z);
	}
	
	public void calcSupport(boolean postRun) {
		for (MCAFile f : files.values()) {
			f.initSupport(postRun);
		}
		
		int pass = 1;
		lp: while (true) {
			System.out.println("FinishSupport pass " + pass++);
			for (MCAFile f : files.values()) {
				if (!f.finishSupport(postRun)) {
					continue lp;
				}
			}
			
			return;
		}
	}
	
	public int getData(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return -1; }
		return files.get(fp).getData(rem(x), y, rem(z));
	}
	
	public void setData(byte data, int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setData(data, rem(x), y, rem(z));
	}
	
	public int getSkyLight(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return -1; }
		return files.get(fp).getSkyLight(rem(x), y, rem(z));
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setSkyLight(light, rem(x), y, rem(z));
	}
	
	public int getBlockLight(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return -1; }
		return files.get(fp).getBlockLight(rem(x), y, rem(z));
	}
	
	public void setBlockLight(byte light, int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setBlockLight(light, rem(x), y, rem(z));
	}

	public void clearTileEntity(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).clearTileEntity(rem(x), y, rem(z), x, y, z);
	}
	
	public Tag getTileEntity(int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return null; }
		return files.get(fp).getTileEntity(rem(x), y, rem(z), x, y, z);
	}
	public void setTileEntity(Tag te, int x, int y, int z) {
		Point fp = fileP(x, y, z);
		if (!files.containsKey(fp)) { return; }
		files.get(fp).setTileEntity(te, rem(x), y, rem(z), x, y, z);
	}
	
	public void removeLighting() {
		for (MCAFile f : files.values()) {
			f.removeLighting();
		}
	}
	
	static final int[] NS_X = {-1, 1, 0, 0, 0, 0 };
	static final int[] NS_Y = { 0, 0,-1, 1, 0, 0 };
	static final int[] NS_Z = { 0, 0, 0, 0,-1, 1 };
	
	public void floodBlockLight(int x, int y, int z, IntPt4Stack q) {
		int type = getBlockType(x, y, z);
		int l = Rules.lightFrom[type + 1];
		if (l <= 0) { return; }
		//System.out.println("Flooding!");
		setBlockLight((byte) l, x, y, z);
		q.push(x, y, z, l - 1);
		while (!q.isEmpty()) {
			q.pop();
			for (int j = 0; j < 6; j++) {
				int nx = q.x + NS_X[j];
				int ny = q.y + NS_Y[j];
				int nz = q.z + NS_Z[j];
				int localType = getBlockType(nx, ny, nz);
				if (!Rules.transparent[localType + 1]) { /*System.out.println("solid " + localType);*/ continue; }
				int localL = getBlockLight(nx, ny, nz);
				if (localL == -1) { /*System.out.println("light is -1");*/ continue; } // There is no block there.
				if (localL >= q.l) { /*System.out.println("already brighter");*/ continue; } // It's already as bright or brighter than we can make it.
				//System.out.println("lighting up to " + q.l);
				setBlockLight((byte) q.l, nx, ny, nz);
				if (q.l > 1) {
					q.push(nx, ny, nz, q.l - 1);
				}
			}
		}
	}
	
	public void floodSkyLight(IntPt4Stack q) {
		while (!q.isEmpty()) {
			q.pop();
			for (int j = 0; j < 6; j++) {
				int nx = q.x + NS_X[j];
				int ny = q.y + NS_Y[j];
				int nz = q.z + NS_Z[j];
				int localType = getBlockType(nx, ny, nz);
				if (!Rules.transparent[localType + 1]) { continue; }
				int localL = getSkyLight(nx, ny, nz);
				if (localL == -1) { continue; } // There is no block there.
				if (localL >= q.l) { continue; } // It's already as bright or brighter than we can make it.
				setSkyLight((byte) q.l, nx, ny, nz);
				if (q.l > 1) {
					q.push(nx, ny, nz, q.l - 1);
				}
			}
		}
	}
	
	public void calcSkyLight() {
		for (MCAFile f : files.values()) {
			f.calculateInitialSkyLights();
		}
		
		int pass = 1;
		lp: while (true) {
			System.out.println("FinishSkyLights pass " + pass++);
			for (MCAFile f : files.values()) {
				if (!f.finishSkyLights()) {
					continue lp;
				}
			}
			
			return;
		}
	}
}
