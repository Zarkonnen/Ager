package ager;

import com.jcraft.jzlib.GZIPInputStream;
import com.jcraft.jzlib.GZIPOutputStream;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;
import unknown.Tag;

public class MCMap {
	public final CoordMap<MCAFile> files = new CoordMap<MCAFile>();
	private final File worldF;
	public final Tag levelDat;
	public final Tag levelDatOld;

	public MCMap(File worldF) throws FileNotFoundException, IOException {
		this.worldF = worldF;
		
		InputStream fis = null;
		try {
			fis = new GZIPInputStream(new FileInputStream(new File(worldF, "level.dat")));
			levelDat = Tag.readFrom(fis);
		} finally {
			fis.close();
		}
		
		fis = null;
		try {
			if (new File(worldF, "level.dat_old").exists()) {
				fis = new GZIPInputStream(new FileInputStream(new File(worldF, "level.dat_old")));
				levelDatOld = Tag.readFrom(fis);
			} else {
				levelDatOld = null;
			}
		} finally {
			fis.close();
		}
		
		for (File f : new File(worldF, "region").listFiles()) {
			if (f.getName().endsWith(".mca")) {
				String[] bits = f.getName().split("[.]");
				int x = Integer.parseInt(bits[1]);
				int z = Integer.parseInt(bits[2]);
				files.put(x, z, new MCAFile(new File(worldF, "region"), x, z));
			}
		}
		for (MCAFile f : files) {
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
	
	public void killPlayers() {
		Tag pl = getPlayer();
		if (pl != null) {
			killPlayer(pl);
		}
		for (File f : new File(worldF, "players").listFiles()) {
			if (f.getName().endsWith(".dat")) {
				try {
					GZIPInputStream gis = new GZIPInputStream(new FileInputStream(f));
					Tag t = Tag.readFrom(gis);
					gis.close();
					killPlayer(t);
					GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(f));
					t.writeTo(gos);
					gos.flush();
					gos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void makePlayersAdventurers(Random r) {
		Tag pl = getPlayer();
		if (pl != null) {
			makePlayerAdventurer(pl, r);
		}
		for (File f : new File(worldF, "players").listFiles()) {
			if (f.getName().endsWith(".dat")) {
				try {
					GZIPInputStream gis = new GZIPInputStream(new FileInputStream(f));
					Tag t = Tag.readFrom(gis);
					gis.close();
					makePlayerAdventurer(t, r);
					GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(f));
					t.writeTo(gos);
					gos.flush();
					gos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void killPlayer(Tag player) {
		player.findTagByName("SleepTimer").setValue(Short.valueOf("0"));
		player.findTagByName("HurtTime").setValue(Short.valueOf("0"));
		player.findTagByName("XpLevel").setValue(Integer.valueOf("0"));
		player.findTagByName("Health").setValue(Short.valueOf("20"));
		player.findTagByName("Dimension").setValue(Integer.valueOf("0"));
		player.findTagByName("Air").setValue(Short.valueOf("0"));
		player.findTagByName("Inventory").clearList();
		int myX = (Integer) levelDat.findTagByName("SpawnX").getValue();
		int myY = (Integer) levelDat.findTagByName("SpawnY").getValue();
		int myZ = (Integer) levelDat.findTagByName("SpawnZ").getValue();
		while (getBlockType(myX, myY, myZ) > Types.Air || getBlockType(myX, myY + 1, myZ) > Types.Air) { myY++; }
		((Tag[]) player.findTagByName("Pos").getValue())[0].setValue(Double.valueOf(myX));
		((Tag[]) player.findTagByName("Pos").getValue())[1].setValue(Double.valueOf(myY + 1.7));
		((Tag[]) player.findTagByName("Pos").getValue())[2].setValue(Double.valueOf(myZ));
		player.findTagByName("Sleeping").setValue(Byte.valueOf("0"));
		player.findTagByName("Fire").setValue(Short.valueOf("-20"));
		player.findTagByName("foodLevel").setValue(Integer.valueOf("20"));
		player.findTagByName("XpTotal").setValue(Integer.valueOf("0"));
		player.findTagByName("XpP").setValue(Float.valueOf("0"));
	}
	
	public void makePlayerAdventurer(Tag player, Random r) {
		killPlayer(player);
		int[][] adv = Adventurers.ALL[r.nextInt(Adventurers.ALL.length)];
		for (int i = 0; i < adv.length; i++) {
			Tag eq = new Tag(Tag.Type.TAG_Compound, null, new Tag[] {
				new Tag(Tag.Type.TAG_Short, "id", Short.valueOf((short) adv[i][0])),
				new Tag(Tag.Type.TAG_Short, "Damage", Short.valueOf((short) adv[i][1])),
				new Tag(Tag.Type.TAG_Byte, "Count", Byte.valueOf((byte) adv[i][2])),
				new Tag(Tag.Type.TAG_Byte, "Slot", Byte.valueOf((byte) adv[i][3])),
				new Tag(Tag.Type.TAG_End, null, null)
			});
			if (adv[i].length > 4) {
				switch (adv[i][4]) {
					case 1: // Aqua helmet
						eq.addTag(new Tag(Tag.Type.TAG_Compound, "tag", new Tag[] {
							new Tag(Tag.Type.TAG_List, "ench", new Tag[] {
								new Tag(Tag.Type.TAG_Compound, null, new Tag[] {
									new Tag(Tag.Type.TAG_Short, "id", Short.valueOf("5")), // respiration
									new Tag(Tag.Type.TAG_Short, "lvl", Short.valueOf("3")),
									new Tag(Tag.Type.TAG_End, null, null)
								}),
								new Tag(Tag.Type.TAG_Compound, null, new Tag[] {
									new Tag(Tag.Type.TAG_Short, "id", Short.valueOf("6")), // aqua affinity
									new Tag(Tag.Type.TAG_Short, "lvl", Short.valueOf("1")),
									new Tag(Tag.Type.TAG_End, null, null)
								})
							}),
							new Tag(Tag.Type.TAG_End, null, null)
						}));
						break;
					case 2: // Mountaineer shoes
						eq.addTag(new Tag(Tag.Type.TAG_Compound, "tag", new Tag[] {
							new Tag(Tag.Type.TAG_List, "ench", new Tag[] {
								new Tag(Tag.Type.TAG_Compound, null, new Tag[] {
									new Tag(Tag.Type.TAG_Short, "id", Short.valueOf("2")), // feather falling
									new Tag(Tag.Type.TAG_Short, "lvl", Short.valueOf("4")),
									new Tag(Tag.Type.TAG_End, null, null)
								})
							}),
							new Tag(Tag.Type.TAG_End, null, null)
						}));
						break;
				}
			}
			player.findTagByName("Inventory").addTag(eq);
		}
	}
	
	public Tag getPlayer() {
		return levelDat.findTagByName("Player");
	}
	
	public void resetLevelData() {
		levelDat.findTagByName("raining").setValue(Byte.valueOf((byte) 0));
		levelDat.findTagByName("thundering").setValue(Byte.valueOf((byte) 0));
		levelDat.findTagByName("Time").setValue(Long.valueOf((long) 1776562l));
		if (levelDatOld != null) {
			levelDatOld.findTagByName("raining").setValue(Byte.valueOf((byte) 0));
			levelDatOld.findTagByName("thundering").setValue(Byte.valueOf((byte) 0));
			levelDatOld.findTagByName("Time").setValue(Long.valueOf((long) 1776562l));
		}
	}
	
	public void setGameType(int type, boolean hardcore) {
		levelDat.findTagByName("GameType").setValue(Integer.valueOf(type));
		levelDat.findTagByName("hardcore").setValue(hardcore ? (byte) 1 : (byte) 0);
		if (levelDatOld != null) {
			levelDatOld.findTagByName("GameType").setValue(Integer.valueOf(type));
			levelDatOld.findTagByName("hardcore").setValue(hardcore ? (byte) 1 : (byte) 0);
		}
	}
	
	public void writeAndClose() throws IOException {
		for (MCAFile f : files) {
			f.writeAndClose();
		}
		
		OutputStream fos = null;
		try {
			fos = new GZIPOutputStream(new FileOutputStream(new File(worldF, "level.dat")));
			levelDat.writeTo(fos);
		} finally {
			fos.flush();
			fos.close();
		}
		
		fos = null;
		try {
			if (levelDatOld != null) {
				fos = new GZIPOutputStream(new FileOutputStream(new File(worldF, "level.dat_old")));
				levelDatOld.writeTo(fos);
			}
		} finally {
			fos.flush();
			fos.close();
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
		MCAFile f = files.get(chunkFileC(chunkX), chunkFileC(chunkZ));
		if (f == null) { return null; }
		return f.getChunk(chunkRem(chunkX), chunkRem(chunkZ));
	}
	
	public boolean getPartOfBlob(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return false; }
		return f.getPartOfBlob(rem(x), y, rem(z));
	}
	
	public void setPartOfBlob(int x, int y, int z, boolean value) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setPartOfBlob(rem(x), y, rem(z), value);
	}
	
	public void clearPartOfBlob() {
		for (MCAFile f : files) {
			f.clearPartOfBlob();
		}
	}
	
	public int getBlockType(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return -1; }
		return f.getBlockType(rem(x), y, rem(z));
	}
	
	public void setBlockType(byte type, int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setBlockType(type, rem(x), y, rem(z));
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
		for (MCAFile f : files) {
			f.initSupport(postRun);
		}
		
		int pass = 1;
		lp: while (true) {
			System.out.println("FinishSupport pass " + pass++);
			for (MCAFile f : files) {
				if (!f.finishSupport(postRun)) {
					continue lp;
				}
			}
			
			return;
		}
	}
	
	public void newCalcSupport() {
		for (MCAFile f : files) {
			f.newInitSupport();
		}
		
		int pass = 1;
		lp: while (true) {
			System.out.println("FinishSupport pass " + pass++);
			for (MCAFile f : files) {
				if (!f.newFinishSupport()) {
					continue lp;
				}
			}
			
			return;
		}
	}
	
	public boolean isDataMaskSet(int mask, int x, int y, int z) {
		int d = getData(x, y, z);
		return (d & mask) == mask;
	}
	
	public int getData(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return -1; }
		return f.getData(rem(x), y, rem(z));
	}
	
	public void setData(byte data, int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setData(data, rem(x), y, rem(z));
	}
	
	public int getSkyLight(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return -1; }
		return f.getSkyLight(rem(x), y, rem(z));
	}
	
	public void setSkyLight(byte light, int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setSkyLight(light, rem(x), y, rem(z));
	}
	
	public int getBlockLight(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return -1; }
		return f.getBlockLight(rem(x), y, rem(z));
	}
	
	public void setBlockLight(byte light, int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setBlockLight(light, rem(x), y, rem(z));
	}

	public void clearTileEntity(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.clearTileEntity(rem(x), y, rem(z), x, y, z);
	}
	
	public Tag getTileEntity(int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return null; }
		return f.getTileEntity(rem(x), y, rem(z), x, y, z);
	}
	
	public void setTileEntity(Tag te, int x, int y, int z) {
		MCAFile f = files.get(fileC(x), fileC(z));
		if (f == null) { return; }
		f.setTileEntity(te, rem(x), y, rem(z), x, y, z);
	}
	
	public void clearAllEntities() {
		for (MCAFile f : files) {
			f.clearAllEntities();
		}
	}
	
	public void removeLighting() {
		for (MCAFile f : files) {
			f.removeLighting();
		}
	}
	
	static final int[] NS_X = {-1, 1, 0, 0, 0, 0 };
	static final int[] NS_Y = { 0, 0, 0, 0,-1, 1 };
	static final int[] NS_Z = { 0, 0,-1, 1, 0, 0 };
	
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
				if (q.l - Rules.extraLightAttenuation[localType + 1] > 1) {
					q.push(nx, ny, nz, q.l - 1 - Rules.extraLightAttenuation[localType + 1]);
				}
			}
		}
	}
	
	public void calcSkyLight() {
		for (MCAFile f : files) {
			f.calculateInitialSkyLights();
		}
		
		int pass = 1;
		lp: while (true) {
			System.out.println("FinishSkyLights pass " + pass++);
			for (MCAFile f : files) {
				if (!f.finishSkyLights()) {
					continue lp;
				}
			}
			
			return;
		}
	}
	
	public void doFlow(int x, int y, int z) {
		int startLevel = getData(x, y, z);
		/*if (startLevel != 0) {
			return;
		}*/
		/*int startType = getBlockType(x, y, z);
		System.out.println(startType);*/
		IntPt3Stack flowQ = new IntPt3Stack(10);
		flowQ.push(x, y, z);
		while (!flowQ.isEmpty()) {
			flowQ.pop();
			int type = getBlockType(flowQ.x, flowQ.y, flowQ.z);
			int level = getData(flowQ.x, flowQ.y, flowQ.z);
			//System.out.println("type " + type + " level " + level + " x " + flowQ.x + " y " + flowQ.y + " z " + flowQ.z);
			boolean isSource = level == 0;
			
			int belowType = getBlockType(flowQ.x, flowQ.y - 1, flowQ.z);
			//int belowLevel = getData(flowQ.x, flowQ.y - 1, flowQ.z);
			boolean hasBottom = belowType != type && belowType != Types.Air;
			
			if (hasBottom || isSource) {
				for (int j = 0; j < 4; j++) {
					final int newLevel = (level == 8 ? 0 : level) + (type == Types.Water ? 1 : 2);
					if (newLevel > 7) { continue; }
					final int nx = flowQ.x + NS_X[j];
					final int ny = flowQ.y;
					final int nz = flowQ.z + NS_Z[j];
					int nType = getBlockType(nx, ny, nz);
					if (nType == type && getData(nx, ny, nz) <= newLevel) {
						continue;
					}
					if (nType == type || nType == Types.Air) {
						setBlockType((byte) type, nx, ny, nz);
						setData((byte) newLevel, nx, ny, nz);
						flowQ.push(nx, ny, nz);
					}
				}
			}
			
			if (!hasBottom) {
				setBlockType((byte) type, flowQ.x, flowQ.y - 1, flowQ.z);
				setData((byte) 8, flowQ.x, flowQ.y - 1, flowQ.z);
				flowQ.push(flowQ.x, flowQ.y - 1, flowQ.z);
			}
		}
	}
	
	/*public void doFlow2(int x, int y, int z) {
		IntPt3Stack flowQ = new IntPt3Stack(10);
		int startType = getBlockType(x, y, z);
		if (2 * 2 == 4) { return; }
		boolean isLava = startType == Types.Lava || startType == Types.Source_Lava;
		boolean isSource = startType == Types.Source_Lava || startType == Types.Source_Water;
		flowQ.push(x, y, z);
		while (!flowQ.isEmpty()) {
			flowQ.pop();
			int belowType = getBlockType(flowQ.x, flowQ.y - 1, flowQ.z);
			int amt = getData(flowQ.x, flowQ.y, flowQ.z);
			if (belowType == Types.Air) {
				setBlockType((byte) (isLava ? Types.Lava : Types.Water), flowQ.x, flowQ.y - 1, flowQ.z);
				setData((byte) 8, flowQ.x, flowQ.y - 1, flowQ.z);
				flowQ.push(flowQ.x, flowQ.y - 1, flowQ.z);
				continue;
			}
			if (!isSource && !isLava && belowType == Types.Water) {
				setData((byte) 8, flowQ.x, flowQ.y - 1, flowQ.z);
				flowQ.push(flowQ.x, flowQ.y - 1, flowQ.z);
				if (amt == 8) {
					continue; // Falling water don't spread!
				}
			}
			if (!isSource && !isLava && belowType == Types.Source_Water) {
				continue;
			}
			if (!isSource && isLava && belowType == Types.Lava) {
				setData((byte) 8, flowQ.x, flowQ.y - 1, flowQ.z);
				flowQ.push(flowQ.x, flowQ.y - 1, flowQ.z);
				if (amt == 8) {
					continue; // Falling lava don't spread!
				}
			}
			if (!isSource && isLava && belowType == Types.Source_Lava) {
				continue;
			}

			int type = getBlockType(flowQ.x, flowQ.y, flowQ.z);
			int startAmt =
				type == Types.Source_Lava ? 0 : type == Types.Source_Water ? 0 : amt;
			if (startAmt == 8) { startAmt = 0; }
			for (int dz = -1; dz < 2; dz++) { for (int dx = -1; dx < 2; dx++) {
				if (dz == 0 && dx == 0) { continue; }
				if (dz != 0 && dx != 0) { continue; }
				int nx = flowQ.x + dx;
				int ny = flowQ.y;
				int nz = flowQ.z + dz;
				int t = getBlockType(nx, ny, nz);
				int newAmt = startAmt + (isLava ? 2 : 1);
				if (t == Types.Source_Lava) {
					if (type == Types.Source_Lava || type == Types.Lava) {
						continue;
					}
					if (type == Types.Source_Water || type == Types.Water) {
						setBlockType((byte) Types.Obsidian, nx, ny, nz);
						continue;
					}
				}
				if (t == Types.Lava) {
					int myAmt = getData(nx, ny, nz);
					if (type == Types.Lava && newAmt >= myAmt) {
						continue;
					}
					if (type == Types.Source_Water || type == Types.Water) {
						setBlockType((byte) Types.Cobblestone, nx, ny, nz);
						continue;
					}
				}
				if (t == Types.Source_Water) {
					if (type == Types.Source_Water || type == Types.Water) {
						continue;
					}
					if (type == Types.Source_Lava) {
						setBlockType((byte) Types.Obsidian, nx, ny, nz);
						continue;
					}
					if (type == Types.Lava) {
						setBlockType((byte) Types.Cobblestone, nx, ny, nz);
						continue;
					}
				}
				if (t == Types.Water) {
					int myAmt = getData(nx, ny, nz);
					if (type == Types.Water && newAmt >= myAmt) {
						continue;
					}
					if (type == Types.Source_Lava || type == Types.Lava) {
						setBlockType((byte) Types.Cobblestone, nx, ny, nz);
						continue;
					}
				}
				if (newAmt < 8 && (t == Types.Air || t == Types.Water || t == Types.Lava)) {
					setBlockType((byte) (isLava ? Types.Lava : Types.Water), nx, ny, nz);
					setData((byte) newAmt, nx, ny, nz);
					//System.out.println("setting " + amt);
					flowQ.push(nx, ny, nz);
				}
			}}
		}
	}*/
}
