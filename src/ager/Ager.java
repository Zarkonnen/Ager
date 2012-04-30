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
		int loops = args.length > 1 ? Integer.parseInt(args[1]) : 2;
		for (int lp = 0; lp < loops; lp++) {
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
								ac.typeKnown = true;
								if (!Rules.ruleTypes[ac.type + 1]) { continue; }
								for (Rule rule : Rules.rulesForType[ac.type + 1]) {
									if (rule.apply(x, y, z, m, r, ac)) { break; }
								}
							}}}
						} 
					}
				}
				System.out.println(++fi + "/" + m.files.values().size());
			}	
		}
		
		m.writeAndClose();
		
		/*for (int fz = -1; fz < 1; fz++) { for (int fx = -1; fx < 1; fx++) {
			MCAFile f = new MCAFile(new File("/Users/zar/Desktop/Creative/region/"), fz, fx);
			for (int y = 0; y < 255; y++) {
				for (int z = 0; z < 512; z++) {
					for (int x = 0; x < 512; x++) {
						if (f.getBlockType(x, y, z) == 4 && f.getBlockType(x, y + 1, z) == 0 && r.nextBoolean()) {
							f.setBlockType((byte) 0, x, y, z);
							f.setSkyLight((byte) f.getSkyLight(x, y, z), x, y - 1, z);
							f.setSkyLight((byte) f.getSkyLight(x, y + 1, z), x, y, z);
						}
					}
				}
			}
			
			f.removeLighting();
			
			f.writeAndClose();
		}}*/
	}
}
