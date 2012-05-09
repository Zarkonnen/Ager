/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ager;

import java.io.File;
import java.util.Random;

public class Ager {
	public static void main(String[] args) throws Exception {
		Random r = new Random();
		
		MCMap m = new MCMap(new File(args[0]));
		
		m.calcSupport(false); //qqDPS EXTREME GRAVITYS
		IntPt4Stack lightQ = new IntPt4Stack(2048);
		
		int loops = args.length > 1 ? Integer.parseInt(args[1]) * 4 : 4;
		for (int lp = 0; lp < loops; lp++) {
			if (lp % 4 == 0 || lp % 4 == 1) {
				m.clearPartOfBlob();
				//continue; // qqDPS
			}
			if (lp % 4 == 2) {
				m.calcSupport(true);
			}
			if (lp % 4 == 3) {
				m.removeLighting();
				m.calcSkyLight();
			}
			
			int fi = 0;
			Rule.ApplicationCache ac = new Rule.ApplicationCache();
			for (MCAFile f : m.files.values()) {
				for (int zBlock = 0; zBlock < 32; zBlock++) {
					for (int xBlock = 0; xBlock < 32; xBlock++) {
						if (f.chunks[zBlock][xBlock] == null) { continue; }
						/*if (lp % 4 == 3) {
							f.chunks[zBlock][xBlock].calculateInitialSkyLights(lightQ, f.xOffset * 512 + xBlock * 16, f.zOffset * 512 + zBlock * 16);
							m.floodSkyLight(lightQ);
						}*/
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
								if (lp % 4 == 0) {
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
								if (lp % 4 == 1) {
									if (!Rules.secondRuleTypes[ac.type + 1]) { continue; }
									for (Rule rule : Rules.secondRulesForType[ac.type + 1]) {
										if (rule.apply(x, y, z, m, r, ac)) { break; }
									}
								}
								if (lp % 4 == 2) {
									if (ac.type > Types.Air &&
										!f.chunks[zBlock][xBlock].isSupported.get(y * 256 + lz * 16 + lx) &&
										f.chunks[zBlock][xBlock].wasSupported.get(y * 256 + lz * 16 + lx))
									{
										Rule.fall(x, y, z, m, Rules.fallChanges[ac.type]);
									}
									if (Rules.flows[ac.type + 1]) {
										m.doFlow(x, y, z);
									}
									/*if (ac.type > Types.Air &&
										f.chunks[zBlock][xBlock].isSupported.get(y * 256 + lz * 16 + lx)
									)
									{
										f.chunks[zBlock][xBlock].setBlockType((byte) Types.Gold_Block, lx, y, lz);
									}*/
								}
								if (lp % 4 == 3) {
									m.floodBlockLight(x, y, z, lightQ);
								}
							}}}
						} 
					}
				}
				System.out.println(++fi + "/" + m.files.values().size());
			}	
		}
				
		m.writeAndClose();
	}
}
