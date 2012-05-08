package ager;

import java.util.ArrayList;
import static ager.Rule.*;
import static ager.Types.*;
import static ager.Blueprints.*;

public class Rules {
	static Rule rule() {
		Rule r = new Rule();
		rules.add(r);
		return r;
	}
	
	public static final ArrayList<Rule> rules = new ArrayList<Rule>();
	public static boolean[] ruleTypes = new boolean[1024];
	public static final ArrayList<Rule>[] rulesForType = new ArrayList[1024];
	
	static Rule secondRule() {
		Rule r = new Rule();
		secondRules.add(r);
		return r;
	}
	
	public static final ArrayList<Rule> secondRules = new ArrayList<Rule>();
	public static boolean[] secondRuleTypes = new boolean[1024];
	public static final ArrayList<Rule>[] secondRulesForType = new ArrayList[1024];
	
	public static int[] fallChanges = new int[1024];
	public final static boolean[] providesSupport = new boolean[1024];
	public final static boolean[] needsSupportFromBelow = new boolean[1024];
	public final static boolean[] needsSupportFromFaces = new boolean[1024];
	public final static boolean[] fallThru = new boolean[1024];
	
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
	
	static void itemsFallThrough(int type) {
		fallThru[type + 1] = true;
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
		
		itemsFallThrough(Air);
		itemsFallThrough(Water);
		itemsFallThrough(Stationary_Water);
		itemsFallThrough(Lava);
		itemsFallThrough(Stationary_Lava);
		
		rule().desc("Exposed cobble turns to gravel.").
				p(0.05).when(is(Cobblestone)).when(skyExposed()).when(nextToAtLeast(5, Air)).then(become(Gravel));
		rule().desc("Cobble near water or mossy cobble turns mossy.").
				p(0.001).when(is(Cobblestone)).moreLikelyWhen(inVicinityOf(0.25, 3, Water)).moreLikelyWhen(touching(0.15, Mossy_Cobblestone)).then(become(Mossy_Cobblestone));
		rule().desc("Cobble weathering.").
				p(0.0001).when(is(Cobblestone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Mossy cobble weathering.").
				p(0.0001).when(is(Mossy_Cobblestone)).moreLikelyWhen(skyExposed(0.1)).moreLikelyWhen(below(0.03, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Grass growth.").
				p(1).when(is(Dirt)).when(nextTo(Grass)).then(become(Grass));
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
				p(0.05).when(is(Furnace)).then(become(Air));
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
		rule().desc("Wool vanishing."). // qqDPS
				p(1.1).when(is(Wool)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.1, Air)).then(become(Air));
		rule().desc("Wheat vanishing.").
				p(0.4).when(is(Wheat_Crops)).then(become(Air));
		rule().desc("Torch vanishing.").
				p(0.65).when(is(Torch)).then(become(Air));
		rule().desc("Bookcases rotting.").
				p(0.1).when(is(Bookshelf)).moreLikelyWhen(below(0.1, Air)).then(become(Air));
		
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
		
		// Trees and non-trees.
		rule().desc("Trees rarely disappearing en bloc.").
				p(0.02).when(isConnectedBlobOf(Wood, Leaves, Vines)).when(connectedBlobContains(Leaves)).
				then(applyCollectively(new Rule().p(1.0).then(become(Air)))).
				then(applyNearby(4, new Rule().p(0.02).when(is(Air)).when(above(Grass)).then(become(Sapling))));
		rule().desc("Non-trees disappearing individually.").
				p(1.0).when(isConnectedBlobOf(Wood, Leaves, Vines)).when(connectedBlobDoesNotContain(Leaves)).then(applyIndividually(new Rule().p(0.04).moreLikelyWhen(below(0.07, Air)).moreLikelyWhen(skyExposed(0.07)).then(become(Air))));
		
		// Doors
		rule().desc("Wooden doors disappear pretty quickly.").
				p(0.3).when(isConnectedBlobOf(Wooden_Door_Block)).then(applyCollectively(new Rule().p(1.0).then(become(Air))));
		rule().desc("Metal doors disappear very rarely.").
				p(0.01).when(isConnectedBlobOf(Wooden_Door_Block)).then(applyCollectively(new Rule().p(1.0).then(become(Air))));
		
		// Beds
		rule().desc("Beds disappear pretty quickly.").
				p(0.3).when(isConnectedBlobOf(Bed_Block)).then(applyCollectively(new Rule().p(1.0).then(become(Air))));
		
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
		
		// Spawning spawners
		rule().desc("Spawning a spawner.").
				p(1.0).when(is(Cobblestone)).then(new CreateStructure(SPAWNER, null, -2, 1, -2, 0, 0));
		
		// Spawning trees.
		rule().desc("Spawning a tree.").
				p(0.7).when(is(Sapling)).then(become(Air)).then(new CreateStructure(TREE_6X5X5, null, -2, 0, -2, 8, 15));
		
		// Detaching
		secondRule().desc("North torches despawning.").
				p(1.0).when(is(Torch)).when(hasData(Torch_Pointing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South torches despawning.").
				p(1.0).when(is(Torch)).when(hasData(Torch_Pointing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East torches despawning.").
				p(1.0).when(is(Torch)).when(hasData(Torch_Pointing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West torches despawning.").
				p(1.0).when(is(Torch)).when(hasData(Torch_Pointing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North torches despawning.").
				p(1.0).when(is(Redstone_Torch_on)).when(hasData(Redstone_Torch_Pointing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South torches despawning.").
				p(1.0).when(is(Redstone_Torch_on)).when(hasData(Redstone_Torch_Pointing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East torches despawning.").
				p(1.0).when(is(Redstone_Torch_on)).when(hasData(Redstone_Torch_Pointing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West torches despawning.").
				p(1.0).when(is(Redstone_Torch_on)).when(hasData(Redstone_Torch_Pointing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North torches despawning.").
				p(1.0).when(is(Redstone_Torch_off)).when(hasData(Redstone_Torch_Pointing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South torches despawning.").
				p(1.0).when(is(Redstone_Torch_off)).when(hasData(Redstone_Torch_Pointing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East torches despawning.").
				p(1.0).when(is(Redstone_Torch_off)).when(hasData(Redstone_Torch_Pointing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West torches despawning.").
				p(1.0).when(is(Redstone_Torch_off)).when(hasData(Redstone_Torch_Pointing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North wall signs despawning.").
				p(1.0).when(is(Wall_Sign)).when(hasData(Wall_Sign_Facing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South wall signs despawning.").
				p(1.0).when(is(Wall_Sign)).when(hasData(Wall_Sign_Facing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East wall signs despawning.").
				p(1.0).when(is(Wall_Sign)).when(hasData(Wall_Sign_Facing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West wall signs despawning.").
				p(1.0).when(is(Wall_Sign)).when(hasData(Wall_Sign_Facing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North buttons despawning.").
				p(1.0).when(is(Stone_Button)).when(hasData(Stone_Button_Facing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South buttons despawning.").
				p(1.0).when(is(Stone_Button)).when(hasData(Stone_Button_Facing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East buttons despawning.").
				p(1.0).when(is(Stone_Button)).when(hasData(Stone_Button_Facing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West buttons despawning.").
				p(1.0).when(is(Stone_Button)).when(hasData(Stone_Button_Facing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North ladders despawning.").
				p(1.0).when(is(Ladder)).when(hasData(Ladder_Facing_North)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South ladders despawning.").
				p(1.0).when(is(Ladder)).when(hasData(Ladder_Facing_South)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East ladders despawning.").
				p(1.0).when(is(Ladder)).when(hasData(Ladder_Facing_East)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West ladders despawning.").
				p(1.0).when(is(Ladder)).when(hasData(Ladder_Facing_West)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		secondRule().desc("North trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_South_Wall_Closed)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_North_Wall_Closed)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_East_Wall_Closed)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_West_Wall_Closed)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		secondRule().desc("North trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_South_Wall_Open)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_North_Wall_Open)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_East_Wall_Open)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West trapdoors despawning.").
				p(1.0).when(is(Trapdoor)).when(hasData(Trapdoor_Attached_To_West_Wall_Open)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
		for (Rule r : rules) {
			ArrayList<Integer> typesL = new ArrayList<Integer>();
			for (Condition cond : r.conditions) {
				if (cond.check() instanceof Is) {
					ruleTypes[((Is) cond.check()).type + 1] = true;
					typesL.add(((Is) cond.check()).type);
				}
				if (cond.check() instanceof IsConnectedBlobOf) {
					for (int t : ((IsConnectedBlobOf) cond.check()).types) {
						ruleTypes[t + 1] = true;
						typesL.add(t);
					}
				}
			}
			for (int type : typesL) {
				if (rulesForType[type + 1] == null) {
					rulesForType[type + 1] = new ArrayList<Rule>();
				}
				rulesForType[type + 1].add(r);
			}
		}
		
		for (Rule r : secondRules) {
			ArrayList<Integer> typesL = new ArrayList<Integer>();
			for (Condition cond : r.conditions) {
				if (cond.check() instanceof Is) {
					secondRuleTypes[((Is) cond.check()).type + 1] = true;
					typesL.add(((Is) cond.check()).type);
				}
				if (cond.check() instanceof IsConnectedBlobOf) {
					for (int t : ((IsConnectedBlobOf) cond.check()).types) {
						secondRuleTypes[t + 1] = true;
						typesL.add(t);
					}
				}
			}
			for (int type : typesL) {
				if (secondRulesForType[type + 1] == null) {
					secondRulesForType[type + 1] = new ArrayList<Rule>();
				}
				secondRulesForType[type + 1].add(r);
			}
		}
	}
}
