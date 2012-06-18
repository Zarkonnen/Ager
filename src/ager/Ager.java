/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ager;

import java.io.File;
import java.util.Random;

public class Ager {
	public static final int APPLY_RULES = 0;
	public static final int FALL = 1;
	public static final int LIGHTING = 2;
	public static final int APPLY_SECONDARY_RULES = 3;
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("Usage:");
			System.out.println("java -Xmx4000m -jar Ager.jar <path to world folder> [number of iterations] [options]");
			System.out.println("Options are in key=value format. Available options:");
			System.out.println("gamemode=survival|hardcore|creative");
			System.out.println("players=keep|reset|explorers");
			System.out.println("blinkenlights=false|true");
		}
		
		Random r = new Random();
		
		Blinkenlights bl = null;
		MCMap m = new MCMap(new File(args[0]), (int) (Math.min(Runtime.getRuntime().maxMemory() / 500000, 100000000)));
		
		m.resetLevelData();
		
		for (int i = 2; i < args.length; i++) {
			String k = args[i].split("=", 2)[0];
			String v = args[i].split("=", 2)[1];
			if (k.equals("gamemode")) {
				if (v.equals("survival")) {
					m.setGameType(0, false);
				}
				if (v.equals("hardcore")) {
					m.setGameType(0, true);
				}
				if (v.equals("creative")) {
					m.setGameType(1, false);
				}
			}
			if (k.equals("players")) {
				if (v.equals("reset")) {
					m.killPlayers();
				}
				if (v.equals("explorers")) {
					m.makePlayersAdventurers(r);
				}
			}
			if (k.equals("blinkenlights") && v.equals("true")) {
				bl = new Blinkenlights(m);
				m.bl = bl;
			}
		}
		
		//System.out.println(m.levelDat.findTagByName("Time").getValue());
		
		IntPt4Stack lightQ = new IntPt4Stack(2048);
		
		int iters = args.length > 1 ? Integer.parseInt(args[1]) : 1;
		int loops = iters * 4;
		for (int lp = 0; lp < loops; lp++) {
			System.out.println("Free memory: " + Runtime.getRuntime().freeMemory());
			final int phase = lp % 4;
			final int iter = lp / 4;
			System.out.println("Round " + (iter + 1) + ", phase " + (phase + 1));
			final boolean nextToFinalIter = iter == iters - 2;
			final boolean finalIter = iter == iters - 1;
			final boolean tenthIter = iter % 10 == 0;
			final boolean doExtendedPhase = nextToFinalIter || finalIter || tenthIter;
			
			if (phase == APPLY_RULES || phase == APPLY_SECONDARY_RULES) {
				System.out.println("clearPartOfBlob start");
				m.clearPartOfBlob();
				System.out.println("clearPartOfBlob end");
			}
			if (phase == FALL && doExtendedPhase) {
				System.out.println("calcsupport start");
				m.newCalcSupport(Math.min(3, iter + 1));
				System.out.println("calcsupport end");
			}
			if (phase == LIGHTING && doExtendedPhase) {
				System.out.println("lighting start");
				m.removeLighting();
				m.calcSkyLight();
				System.out.println("lighting end");
			}
			
			for (MCAFile f : m.files) {
				for (int zBlock = 0; zBlock < 32; zBlock++) {
					for (int xBlock = 0; xBlock < 32; xBlock++) {
						if (f.chunks[zBlock][xBlock] == null) { continue; }
						f.chunks[zBlock][xBlock].processed = false;
					}
				}
			}
			
			int fi = 0;
			Rule.ApplicationCache ac = new Rule.ApplicationCache();
			for (MCAFile f : m.files) {
				for (int zBlock = 0; zBlock < 32; zBlock++) {
					for (int xBlock = 0; xBlock < 32; xBlock++) {
						if (f.chunks[zBlock][xBlock] == null) { continue; }
						for (int dz = -1; dz < 2; dz++) { for (int dx = -1; dx < 2; dx++) {
							Chunk ch = m.getChunk(xBlock + f.xOffset * 32 + dx, zBlock + f.zOffset * 32 + dz);
							if (ch != null) { ch.prepare(); }
						}}
						// qqDPS
						if (bl != null) {
							bl.repaint();
						}
						//f.chunks[zBlock][xBlock].prepare();
						for (int ySection = 0; ySection < 16; ySection++) {
							if (f.chunks[zBlock][xBlock].sections()[ySection] == null) { continue; }
							byte[] sectionData = (byte[]) f.chunks[zBlock][xBlock].sections()[ySection].findTagByName("Blocks").getValue();
							for (int ly = 0; ly < 16; ly++) { for (int lz = 0; lz < 16; lz++) { for (int lx = 0; lx < 16; lx++) {
								int x = f.xOffset * 512 + xBlock * 16 + lx;
								int y = ySection * 16 + ly;
								int z = f.zOffset * 512 + zBlock * 16 + lz;
								ac.type = sectionData[((ly * 16 + lz) * 16 + lx)];
								ac.knownX = x;
								ac.knownY = y;
								ac.knownZ = z;
								if (phase == APPLY_RULES) {
									boolean changed = false;
									if (Rules.ruleTypes[ac.type + 1]) {
										for (Rule rule : Rules.rulesForType[ac.type + 1]) {
											if (rule.apply(x, y, z, m, r, ac)) { changed = true; break; }
										}
									}
									if (!changed && Rules.checkTileEntity[ac.type + 1]) {
										for (StoredItemRule sir : Rules.storedItemRules) {
											sir.run(x, y, z, m, r, ac);
										}
									}
								}
								if (phase == FALL && doExtendedPhase) {
									if (ac.type > Types.Air &&
										f.chunks[zBlock][xBlock].supported.array[y * 256 + lz * 16 + lx] < Rules.weight[ac.type + 1])
									{
										Rule.fall(x, y, z, m, Rules.fallChanges[ac.type], Rules.fallStaySameP[ac.type], Rules.fallChangeP[ac.type], r);
									} else {
										if (Rules.flows[ac.type + 1]) {
											m.doFlow(x, y, z);
										}
									}
								}
								if (phase == LIGHTING && doExtendedPhase) {
									m.floodBlockLight(x, y, z, lightQ);
								}
								if (phase == APPLY_SECONDARY_RULES && doExtendedPhase) {
									if (!Rules.secondRuleTypes[ac.type + 1]) { continue; }
									for (Rule rule : Rules.secondRulesForType[ac.type + 1]) {
										if (rule.apply(x, y, z, m, r, ac)) { break; }
									}
								}
							}}}
						}
						f.chunks[zBlock][xBlock].processed = true;
						f.chunks[zBlock][xBlock].demoteNeighboursOrSelfIfDone();
					}
				}
				System.out.println(++fi + "/" + m.files.size());
			}	
		}
		
		m.clearAllEntities();
				
		m.writeAndClose();
		
		if (bl != null) {
			bl.dispose();
		}
	}
}
