package ager;

import java.awt.Point;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

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
	}
	
	public void removeLighting() {
		for (MCAFile f : files.values()) {
			f.removeLighting();
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
			if (dz == 0 && dx == 0 && dy == 0) { continue; }
			max = Math.max(getBlockLight(x + dx, y + dy, z + dz), max);
		}}}
		setBlockLight((byte) max, x, y, z);
	}
	
	public void calcSupport(boolean postRun) {
		for (MCAFile f : files.values()) {
			f.calcSupport(postRun);
		}
	}
	
	public boolean isSupported(int x, int y, int z) {
		return false;
	}
	
	public boolean wasSupported(int x, int y, int z) {
		return false;
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
}
