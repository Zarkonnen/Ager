package ager;

import java.util.ArrayList;
import static ager.Rule.*;
import static ager.Types.*;

public class Rules {
	public static final ArrayList<Rule> rules = new ArrayList<Rule>();
	static Rule rule() {
		Rule r = new Rule();
		rules.add(r);
		return r;
	}
	
	public static boolean[] ruleTypes = new boolean[1024];
	public static final ArrayList<Rule>[] rulesForType = new ArrayList[1024];
	
	static {
		// Falling
		rule().desc("Falling sand.").
				p(1).when(is(Sand)).when(above(Air)).then(fall());
		rule().desc("Falling gravel.").
				p(1).when(is(Gravel)).when(above(Air)).then(fall());
		rule().desc("Falling glass.").
				p(1).when(is(Glass)).when(above(Air)).then(become(Air));
		rule().desc("Falling glass.").
				p(1).when(is(Glass_Pane)).when(above(Air)).then(become(Air));
		rule().desc("Falling cobble.").
				p(0.5).when(is(Cobblestone)).when(above(Air)).when(nextToAtLeast(4, Air)).then(become(Gravel)).then(fall());
		rule().desc("Falling stone.").
				p(0.5).when(is(Stone_Brick)).when(above(Air)).when(nextToAtLeast(4, Air)).then(become(Gravel)).then(fall());
		rule().desc("Falling sandstone.").
				p(0.5).when(is(Sandstone)).when(above(Air)).when(nextToAtLeast(4, Air)).then(become(Sand)).then(fall());
		rule().desc("Falling bricks.").
				p(0.5).when(is(Brick)).when(above(Air)).when(nextToAtLeast(4, Air)).then(become(Gravel)).then(fall());
		
		rule().desc("Exposed cobble turns to gravel.").
				p(0.1).when(is(Cobblestone)).when(skyExposed()).when(nextToAtLeast(5, Air)).then(become(Gravel));
		rule().desc("Cobble near water or mossy cobble turns mossy.").
				p(0.05).when(is(Cobblestone)).moreLikelyWhen(inVicinityOf(0.01, 3, Water)).moreLikelyWhen(touching(0.2, Mossy_Cobblestone)).then(become(Mossy_Cobblestone));
		rule().desc("Cobble weathering.").
				p(0.001).when(is(Cobblestone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Mossy cobble weathering.").
				p(0.01).when(is(Mossy_Cobblestone)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.001, Air)).then(become(Air));
		rule().desc("Grass growth.").
				p(1).when(is(Dirt)).when(skyExposed()).when(nextTo(Grass)).then(become(Grass));
		rule().desc("Gravel vanishing.").
				p(0.00).when(is(Gravel)).moreLikelyWhen(touching(0.03, Air)).then(become(Air));
		rule().desc("Sand becoming sandstone.").
				p(0.05).when(is(Sand)).when(below(Sand)).then(become(Sandstone));
		rule().desc("Sand becoming sandstone.").
				p(0.4).when(is(Sand)).when(below(Sandstone)).then(become(Sandstone));
		rule().desc("Sandstone vanishing.").
				p(0).when(is(Sandstone)).moreLikelyWhen(skyExposed(0.2)).moreLikelyWhen(below(0.1, Air)).moreLikelyWhen(touching(0.001, Air)).then(become(Air));
		/*rule().desc("Sand vanishing.").
				p(0.00).when(is(Sand)).moreLikelyWhen(touching(0.01, Air)).then(become(Air));*/
		rule().desc("Stone blocks vanishing.").
				p(0.01).when(is(Stone_Brick)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.01, Air)).then(become(Air));
		rule().desc("Crafting table vanishing.").
				p(0.1).when(is(Crafting_Table)).then(become(Air));
		rule().desc("Crafting table vanishing.").
				p(0.1).when(is(Crafting_Table)).then(become(Air));
		rule().desc("Furnace vanishing.").
				p(0.01).when(is(Furnace)).then(become(Air));
		rule().desc("Enchantment table breaking.").
				p(0.001).when(is(Enchantment_Table)).then(become(Obsidian));
		rule().desc("Enchantment table breaking, but in a nice way.").
				p(0.001).when(is(Enchantment_Table)).then(become(Diamond_Block));
		rule().desc("TNT vanishing.").
				p(0.01).when(is(TNT)).then(become(Air));
		rule().desc("Piston vanishing.").
				p(0.005).when(is(Piston)).then(become(Air));
		rule().desc("Redstone lamp vanishing.").
				p(0.01).when(is(Redstone_Lamp_off)).then(become(Air));
		rule().desc("Redstone lamp vanishing.").
				p(0.01).when(is(Redstone_Lamp_on)).then(become(Air));
		rule().desc("Rail vanishing.").
				p(0.002).when(is(Rails)).then(become(Air));
		rule().desc("Powered rail becoming depowered.").
				p(0.02).when(is(Powered_Rail)).then(become(Rails));
		rule().desc("Detector rail becoming normal.").
				p(0.02).when(is(Detector_Rail)).then(become(Rails));
		rule().desc("Lever vanishing.").
				p(0.1).when(is(Lever)).then(become(Air));
		rule().desc("Button vanishing.").
				p(0.05).when(is(Stone_Button)).then(become(Air));
		rule().desc("Stone pressure plate vanishing.").
				p(0.005).when(is(Stone_Pressure_Plate)).then(become(Air));
		rule().desc("Wooden pressure plate vanishing.").
				p(0.03).when(is(Wooden_Pressure_Plate)).then(become(Air));
		rule().desc("Redstone repeater vanishing.").
				p(0.05).when(is(Redstone_Repeater_Block_off)).then(become(Air));
		rule().desc("Redstone repeater vanishing.").
				p(0.05).when(is(Redstone_Repeater_Block_on)).then(become(Air));
		rule().desc("Redstone torch vanishing.").
				p(0.15).when(is(Redstone_Torch_off)).then(become(Air));
		rule().desc("Redstone torch vanishing.").
				p(0.15).when(is(Redstone_Torch_on)).then(become(Air));
		rule().desc("Redstone vanishing.").
				p(0.3).when(is(Redstone_Wire)).then(become(Air));
		rule().desc("Clay bricks vanishing.").
				p(0.012).when(is(Clay_Brick)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.01, Air)).then(become(Air));
		rule().desc("Farmland vanishing.").
				p(0.6).when(is(Farmland)).when(below(Air)).then(become(Dirt));
		rule().desc("Glass vanishing.").
				p(0.5).when(is(Glass)).then(become(Air));
		rule().desc("Glass vanishing.").
				p(0.7).when(is(Glass_Pane)).then(become(Air));
		rule().desc("Jack o' Lantern vanishing.").
				p(0.7).when(is(Jack_O_Lantern)).then(become(Pumpkin));
		rule().desc("Fence vanishing.").
				p(0.2).when(is(Fence)).then(become(Air));
		rule().desc("Fence gate vanishing.").
				p(0.4).when(is(Fence_Gate)).then(become(Air));
		rule().desc("Trapdoor vanishing.").
				p(0.3).when(is(Trapdoor)).then(become(Air));
		rule().desc("Signpost vanishing.").
				p(0.7).when(is(Sign_Post)).then(become(Air));
		rule().desc("Sign vanishing.").
				p(0.7).when(is(Sign)).then(become(Air));
		rule().desc("Ladder vanishing.").
				p(0.3).when(is(Ladder)).then(become(Air));
		rule().desc("Iron bars vanishing.").
				p(0.02).when(is(Iron_Bars)).then(become(Air));
		rule().desc("Brewing stand vanishing.").
				p(0.02).when(is(Brewing_Stand)).then(become(Air));
		rule().desc("Cauldron vanishing.").
				p(0.001).when(is(Cauldron)).then(become(Air));
		rule().desc("Cake vanishing.").
				p(0.9).when(is(Cake_Block)).then(become(Air));
		rule().desc("Torch vanishing.").
				p(0.65).when(is(Torch)).then(become(Air));
		rule().desc("Wool vanishing.").
				p(0.1).when(is(Wool)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.1, Air)).then(become(Air));
		rule().desc("Wheat vanishing.").
				p(0.4).when(is(Wheat_Crops)).then(become(Air));
		rule().desc("Torch vanishing.").
				p(0.65).when(is(Torch)).then(become(Air));
		
		// Can't distinguish between slab types yet.
		rule().desc("Slabs weathering.").
				p(0.01).when(is(Stone_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.01).when(is(Double_Stone_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.2).when(is(Wooden_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.2).when(is(Double_Wooden_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0).when(is(Sandstone_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.01).when(is(Brick_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.01).when(is(Double_Brick_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.01).when(is(Double_Brick_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.01).when(is(Double_Stone_Brick_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		
		rule().desc("Stone stairs weathering.").
				p(0.2).when(is(Cobblestone_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Wooden stairs weathering.").
				p(0.4).when(is(Wooden_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Wooden stairs weathering.").
				p(0.05).when(is(Brick_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Stone brick stairs weathering.").
				p(0.1).when(is(Stone_Brick_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		for (Rule r : rules) {
			int type = -1;
			for (Condition cond : r.conditions) {
				if (cond.check() instanceof Is) {
					ruleTypes[((Is) cond.check()).type + 1] = true;
					type = ((Is) cond.check()).type;
				}
			}
			if (rulesForType[type + 1] == null) {
				rulesForType[type + 1] = new ArrayList<Rule>();
			}
			rulesForType[type + 1].add(r);
		}
	}
}
