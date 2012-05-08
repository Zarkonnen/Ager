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
		
		int loops = args.length > 1 ? Integer.parseInt(args[1]) * 3 : 3;
		for (int lp = 0; lp < loops; lp++) {
			if (lp % 3 == 2) {
				m.calcSupport(true);
			} else {
				m.clearPartOfBlob();
				//continue; // qqDPS
			}
			
			int fi = 0;
			Rule.ApplicationCache ac = new Rule.ApplicationCache();
			for (MCAFile f : m.files.values()) {
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
								if (lp % 3 == 0) {
									if (!Rules.ruleTypes[ac.type + 1]) { continue; }
									for (Rule rule : Rules.rulesForType[ac.type + 1]) {
										if (rule.apply(x, y, z, m, r, ac)) { break; }
									}
								}
								if (lp % 3 == 1) {
									if (!Rules.secondRuleTypes[ac.type + 1]) { continue; }
									for (Rule rule : Rules.secondRulesForType[ac.type + 1]) {
										if (rule.apply(x, y, z, m, r, ac)) { break; }
									}
								}
								if (lp % 3 == 2) {
									if (ac.type > Types.Air &&
										!f.chunks[zBlock][xBlock].isSupported.get(y * 256 + lz * 16 + lx) &&
										f.chunks[zBlock][xBlock].wasSupported.get(y * 256 + lz * 16 + lx))
									{
										Rule.fall(x, y, z, m, Rules.fallChanges[ac.type]);
									}
									/*if (ac.type > Types.Air &&
										f.chunks[zBlock][xBlock].isSupported.get(y * 256 + lz * 16 + lx)
									)
									{
										f.chunks[zBlock][xBlock].setBlockType((byte) Types.Gold_Block, lx, y, lz);
									}*/
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
