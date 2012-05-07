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
	public static int[] fallChanges = new int[1024];
	public final static boolean[] providesSupport = new boolean[1024];
	public final static boolean[] needsSupportFromBelow = new boolean[1024];
	public final static boolean[] needsSupportFromFaces = new boolean[1024];
	
	static {
		for (int i = 0; i < providesSupport.length; i++) {
			providesSupport[i] = true;
		}
	}
	
	static void survivesFallAs(int type, int as) {
		fallChanges[type] = as;
	}
	
	static void doesNotSupport(int type) {
		providesSupport[type + 1] = false;
	}
	
	static void requiresSupportFromBelow(int type) {
		needsSupportFromBelow[type + 1] = true;
	}
	
	static void requiresDirectSupport(int type) {
		needsSupportFromFaces[type + 1] = true;
	}
	
	static {
		survivesFallAs(Sand, Sand);
		survivesFallAs(Gravel, Gravel);
		survivesFallAs(Cobblestone, Gravel);
		survivesFallAs(Stone_Brick, Gravel);
		survivesFallAs(Sandstone, Sand);
		survivesFallAs(Brick, Gravel);
		survivesFallAs(Cauldron, Cauldron);
		survivesFallAs(Dirt, Dirt);
		survivesFallAs(Stone, Gravel);
		survivesFallAs(Enchantment_Table, Obsidian);
		survivesFallAs(Obsidian, Obsidian);
		survivesFallAs(Iron_Bars, Iron_Bars);
		survivesFallAs(Nether_Brick, Nether_Brick);
		survivesFallAs(Soul_Sand, Soul_Sand);
		survivesFallAs(Diamond_Block, Diamond_Block);
		survivesFallAs(Iron_Block, Iron_Block);
		survivesFallAs(Gold_Block, Gold_Block);
		
		doesNotSupport(-1);
		doesNotSupport(Air);
		doesNotSupport(Tall_Grass);
		doesNotSupport(Dead_Bush);
		doesNotSupport(Dandelion);
		doesNotSupport(Rose);
		doesNotSupport(Brown_Mushroom);
		doesNotSupport(Red_Mushroom);
		doesNotSupport(Snow);
		doesNotSupport(Vines);
		doesNotSupport(Lily_Pad);
		doesNotSupport(Web);
		doesNotSupport(Torch);
		doesNotSupport(Wheat_Crops);
		doesNotSupport(Ladder);
		doesNotSupport(Rails);
		doesNotSupport(Wooden_Pressure_Plate);
		doesNotSupport(Stone_Pressure_Plate);
		doesNotSupport(Stone_Button);
		doesNotSupport(Lever);
		doesNotSupport(Fire);
		doesNotSupport(Nether_Wart);
		doesNotSupport(Brewing_Stand);
		
		requiresSupportFromBelow(Gravel);
		requiresSupportFromBelow(Sand);
		requiresSupportFromBelow(Tall_Grass);
		requiresSupportFromBelow(Dead_Bush);
		requiresSupportFromBelow(Dandelion);
		requiresSupportFromBelow(Rose);
		requiresSupportFromBelow(Brown_Mushroom);
		requiresSupportFromBelow(Red_Mushroom);
		requiresSupportFromBelow(Snow);
		requiresSupportFromBelow(Wheat_Crops);
		requiresSupportFromBelow(Rails);
		requiresSupportFromBelow(Chest);
		requiresSupportFromBelow(Wooden_Pressure_Plate);
		requiresSupportFromBelow(Stone_Pressure_Plate);
		requiresSupportFromBelow(Nether_Wart);
		requiresSupportFromBelow(Brewing_Stand);
		
		requiresDirectSupport(Glass_Pane);
		requiresDirectSupport(Fence);
		requiresDirectSupport(Fence_Gate);
		requiresDirectSupport(Nether_Brick_Fence);
		requiresDirectSupport(Iron_Bars);
		
		rule().desc("Exposed cobble turns to gravel.").
				p(0.05).when(is(Cobblestone)).when(skyExposed()).when(nextToAtLeast(5, Air)).then(become(Gravel));
		rule().desc("Cobble near water or mossy cobble turns mossy.").
				p(0.001).when(is(Cobblestone)).moreLikelyWhen(inVicinityOf(0.25, 3, Water)).moreLikelyWhen(touching(0.15, Mossy_Cobblestone)).then(become(Mossy_Cobblestone));
		rule().desc("Cobble weathering.").
				p(0.0001).when(is(Cobblestone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Mossy cobble weathering.").
				p(0.0001).when(is(Mossy_Cobblestone)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Grass growth.").
				p(1).when(is(Dirt)).when(skyExposed()).when(nextTo(Grass)).then(become(Grass));
		rule().desc("Gravel vanishing.").
				p(0.00).when(is(Gravel)).moreLikelyWhen(touching(0.003, Air)).then(become(Air));
		rule().desc("Gravel turning into soil.").
				p(0.1).when(is(Gravel)).when(below(Air)).when(touching(Dirt)).then(become(Dirt));
		rule().desc("Sand becoming sandstone.").
				p(0.05).when(is(Sand)).when(below(Sand)).then(become(Sandstone));
		rule().desc("Sand becoming sandstone.").
				p(0.4).when(is(Sand)).when(below(Sandstone)).then(become(Sandstone));
		rule().desc("Sandstone vanishing.").
				p(0).when(is(Sandstone)).moreLikelyWhen(skyExposed(0.2)).moreLikelyWhen(below(0.1, Air)).then(become(Air));
		rule().desc("Stone blocks vanishing.").
				p(0.00001).when(is(Stone_Brick)).moreLikelyWhen(skyExposed(0.03)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Wooden planks vanishing.").
				p(0.1).when(is(Wooden_Plank)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.01, Air)).then(become(Air));
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
				p(0.00001).when(is(Clay_Brick)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
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
		
		rule().desc("Slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Stone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Inverted slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Stone_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.00001).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Stone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Cobble)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Inverted slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Cobble_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.00001).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Cobble)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Stone_Brick)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Inverted slabs weathering.").
				p(0.00001).when(is(Stone_Slab)).when(hasData(Stone_Slab_Stone_Brick_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.00001).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Stone_Brick)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		
		
		rule().desc("Slabs weathering.").
				p(0.2).when(is(Wooden_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.2).when(is(Wooden_Double_Slab)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.2).when(is(Stone_Slab)).when(hasData(Stone_Slab_Wood)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Slabs weathering.").
				p(0.2).when(is(Stone_Slab)).when(hasData(Stone_Slab_Wood_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.1).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Wood)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		
		
		rule().desc("Slabs weathering.").
				p(0).when(is(Stone_Slab)).when(hasData(Stone_Slab_Sandstone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Slabs weathering.").
				p(0).when(is(Stone_Slab)).when(hasData(Stone_Slab_Sandstone_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Slabs weathering.").
				p(0).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Sandstone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Slabs weathering.").
				p(0.01).when(is(Stone_Slab)).when(hasData(Stone_Slab_Clay_Brick)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Slabs weathering.").
				p(0.01).when(is(Stone_Slab)).when(hasData(Stone_Slab_Clay_Brick_Inverted)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Double slabs weathering.").
				p(0.01).when(is(Double_Stone_Slab)).when(hasData(Double_Stone_Slab_Clay_Brick)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		rule().desc("Stone stairs weathering.").
				p(0.1).when(is(Cobblestone_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Wooden stairs weathering.").
				p(0.4).when(is(Wooden_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Brick stairs weathering.").
				p(0.05).when(is(Brick_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		rule().desc("Stone brick stairs weathering.").
				p(0.01).when(is(Stone_Brick_Stairs)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.002, Air)).then(become(Air));
		
		// Sliding and falling.
		rule().desc("Gravel heaping.").
				p(1.0).when(is(Gravel)).when(below(Air)).then(slideDown(1)).recurseDownwardsOnSuccess();
		rule().desc("Sand heaping.").
				p(0.5).when(is(Sand)).when(below(Air)).then(slideDown(2)).recurseDownwardsOnSuccess();
		rule().desc("Sandstone falling.").
				p(0.01).when(is(Sandstone)).when(nextTo(Air)).then(become(Sand)).then(slideDown(2));
		rule().desc("Cobble falling.").
				p(0.01).when(is(Cobblestone)).when(below(Air)).then(become(Gravel)).then(slideDown(1));
		rule().desc("Bricks falling.").
				p(0.01).when(is(Brick)).when(below(Air)).then(become(Gravel)).then(slideDown(1));
		
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
