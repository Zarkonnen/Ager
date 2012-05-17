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
		}
		
		Random r = new Random();
		
		MCMap m = new MCMap(new File(args[0]));
		
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
		}
		
		//System.out.println(m.levelDat.findTagByName("Time").getValue());
		
		IntPt4Stack lightQ = new IntPt4Stack(2048);
		
		int iters = args.length > 1 ? Integer.parseInt(args[1]) : 1;
		int loops = iters * 4;
		for (int lp = 0; lp < loops; lp++) {
			final int phase = lp % 4;
			final int iter = lp / 4;
			System.out.println("Round " + (iter + 1) + ", phase " + (phase + 1));
			final boolean nextToFinalIter = iter == iters - 2;
			final boolean finalIter = iter == iters - 1;
			final boolean tenthIter = iter % 10 == 0;
			final boolean doExtendedPhase = nextToFinalIter || finalIter || tenthIter;
			
			if (phase == APPLY_RULES || phase == APPLY_SECONDARY_RULES) {
				m.clearPartOfBlob();
			}
			if (phase == FALL && doExtendedPhase) {
				m.newCalcSupport();
			}
			if (phase == LIGHTING && doExtendedPhase) {
				m.removeLighting();
				m.calcSkyLight();
			}
			
			int fi = 0;
			Rule.ApplicationCache ac = new Rule.ApplicationCache();
			for (MCAFile f : m.files) {
				for (int zBlock = 0; zBlock < 32; zBlock++) {
					for (int xBlock = 0; xBlock < 32; xBlock++) {
						if (f.chunks[zBlock][xBlock] == null) { continue; }
						for (int ySection = 0; ySection < 16; ySection++) {
							if (f.chunks[zBlock][xBlock].sections[ySection] == null) { continue; }
							byte[] sectionData = (byte[]) f.chunks[zBlock][xBlock].sections[ySection].findTagByName("Blocks").getValue();
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
										f.chunks[zBlock][xBlock].supported[y * 256 + lz * 16 + lx] < Rules.weight[ac.type + 1])
									{
										Rule.fall(x, y, z, m, Rules.fallChanges[ac.type]);
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
					}
				}
				System.out.println(++fi + "/" + m.files.size());
			}	
		}
		
		m.clearAllEntities();
				
		m.writeAndClose();
	}
}
