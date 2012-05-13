package ager;

import java.util.ArrayList;
import static ager.Rule.*;
import static ager.Types.*;
import static ager.Blueprints.*;
import java.util.Random;
import unknown.Tag;

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
	public static final boolean[] checkTileEntity = new boolean[1024];
	
	public static final int[] lightFrom = new int[1024];
	public static final boolean[] transparent = new boolean[1024];
	
	public static final boolean[] flows = new boolean[1024];
	
	public static final int[] extraLightAttenuation = new int[1024];
	
	public static final int[] support = new int[1024];
	public static final int[] weight = new int[1024];
	public static final int[] maxSupport = new int[1024];
	
	static {
		for (int i = 0; i < 1024; i++) {
			support[i] = 6;
			maxSupport[i] = 100;
			weight[i] = 25;
		}
	}
	
	static void mat(int type, int sup, int wt) {
		support[type + 1] = sup;
		weight[type + 1] = wt;
	}
	
	static void mat(int type, int sup, int wt, int ms) {
		support[type + 1] = sup;
		weight[type + 1] = wt;
		maxSupport[type + 1] = ms;
	}
	
	static void flows(int type) {
		flows[type + 1] = true;
	}
	
	static void castsLight(int type, int amt) {
		lightFrom[type + 1] = amt;
	}
	
	static void isTransparent(int type) {
		transparent[type + 1] = true;
	}
	
	public static final ArrayList<StoredItemRule> storedItemRules = new ArrayList<StoredItemRule>();
	
	static void itemVanishes(int type, double p) {
		storedItemRules.add(new StoredItemRule(type, p, -1, 0, 0));
	}
	
	static void itemBecomes(int type, double p, int newType) {
		storedItemRules.add(new StoredItemRule(type, p, newType, 0, 0));
	}
	
	static void itemTakesDamage(int type, double p, int dmg, int breakDmg) {
		storedItemRules.add(new StoredItemRule(type, p, -1, dmg, breakDmg));
	}
	
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
	
	static void hasTileEntity(int type) {
		checkTileEntity[type + 1] = true;
	}
	
	static {
		// default is sup 6 wt 25 max 100
		mat(Air, 0, 0, 0);
		mat(Water, 0, 60);
		mat(Lava, 100, 70, 0);
		mat(More_Lava, 100, 70, 0);
		mat(Stone, 2, 60);
		mat(Cobblestone, 3, 50);
		mat(Grass, 5, 35, 50);
		mat(Dirt, 5, 30, 70);
		mat(Sand, 100, 30, 0);
		mat(Gravel, 100, 40, 0);
		mat(Gold_Ore, 1, 70);
		mat(Iron_Ore, 1, 65);
		mat(Coal_Ore, 2, 55);
		mat(Wood, 3, 45);
		mat(Leaves, 1, 2, 15);
		mat(Glass, 1, 50);
		mat(Lapis_Lazuli_Ore, 1, 60);
		mat(Dispenser, 2, 20);
		mat(Sandstone, 10, 45, 80);
		mat(Note_Block, 7, 15);
		mat(Bed, 100, 30, 0);
		mat(Powered_Rail, 100, 40, 0);
		mat(Rails, 100, 40, 0);
		mat(Detector_Rail, 100, 40, 0);
		mat(Sticky_Piston, 4, 35);
		mat(Web, 2, 1, 10);
		mat(Tall_Grass, 100, 5, 0);
		mat(Dead_Bush, 100, 8, 0);
		mat(Piston, 5, 32);
		mat(Piston_Head, 5, 0);
		mat(Wool, 20, 10, 40);
		mat(Dandelion, 100, 1, 0);
		mat(Rose, 100, 1, 0);
		mat(Brown_Mushroom, 100, 1, 0);
		mat(Red_Mushroom, 100, 1, 0);
		mat(Gold_Block, 1, 90);
		mat(Iron_Block, 1, 80);
		mat(Brick, 4, 45);
		mat(Mossy_Cobblestone, 3, 50);
		mat(Obsidian, 2, 60);
		mat(Torch, 0, 5, 0);
		mat(Diamond_Ore, 2, 50);
		mat(Diamond_Block, 1, 45);
		mat(Wheat_Crops, 100, 1, 0);
		mat(Farmland, 5, 30, 70);
		mat(Furnace, 2, 30);
		mat(Burning_Furnace, 2, 30);
		mat(Sign_Post, 100, 10, 0);
		mat(Ladder, 100, 10, 0);
		mat(Wall_Sign, 100, 10, 0);
		mat(Redstone_Ore, 2, 50);
		mat(Glowing_Redstone_Ore, 2, 50);
		mat(Glowstone, 3, 30);
		mat(Stone_Button, 100, 10, 0);
		mat(Lever, 100, 10, 0);
		mat(Snow, 100, 10, 0);
		mat(Ice, 5, 50, 80);
		mat(Snow_Block, 20, 25, 70);
		mat(Cactus, 100, 30, 0);
		mat(Clay, 20, 40, 50);
		mat(Sugar_Cane, 100, 30, 0);
		mat(Cake_Block, 100, 30, 0);
		mat(Redstone_Repeater_Block_on, 100, 10, 0);
		mat(Redstone_Repeater_Block_off, 100, 10, 0);
		mat(Trapdoor, 0, 10, 0);
		mat(Stone_Brick, 2, 55);
		mat(Double_Stone_Slab, 3, 55);
		mat(Iron_Bars, 1, 20);
		mat(Glass_Pane, 5, 15, 60);
		mat(Pumpkin_Stem, 100, 10, 0);
		mat(Melon_Stem, 100, 10, 0);
		mat(Vines, 100, 10, 0);
		mat(Lily_Pad, 100, 10, 0);
		mat(Nether_Wart, 100, 10, 0);
		mat(Enchantment_Table, 100, 40, 0);
		mat(Brewing_Stand, 100, 10, 0);
		mat(Cauldron, 100, 10, 0);
		
		flows(More_Lava); // qqDPS
		flows(Lava);
		//flows(Source_Lava);
		flows(Water);
		//flows(Source_Water);
		extraLightAttenuation[Water + 1] = 2;
		//extraLightAttenuation[Source_Water + 1] = 2;
		extraLightAttenuation[Ice + 1] = 2;
		
		castsLight(Fire, 15);
		castsLight(Jack_O_Lantern, 15);
		castsLight(Lava, 15);
		//castsLight(Source_Lava, 15);
		castsLight(Glowstone, 15);
		castsLight(Redstone_Lamp_on, 15);
		castsLight(Torch, 14);
		castsLight(Burning_Furnace, 13);
		castsLight(Portal, 11);
		castsLight(Redstone_Repeater_Block_on, 9);
		castsLight(Redstone_Torch_on, 7);
		castsLight(Brown_Mushroom, 1);
		castsLight(Brewing_Stand, 1);
		// qqDPS Dragon Egg, End Portal Block, Locked Chest
		
		isTransparent(-1);
		isTransparent(Ice);
		isTransparent(Glass);
		isTransparent(Glass_Pane);
		isTransparent(Web);
		isTransparent(TNT);
		isTransparent(Monster_Spawner);
		isTransparent(Leaves);
		isTransparent(Piston);
		isTransparent(Glowstone);
		isTransparent(Chest);
		isTransparent(Farmland);
		isTransparent(Stone_Slab);
		isTransparent(Wooden_Slab);
		isTransparent(Cobblestone_Stairs);
		isTransparent(Brick_Stairs);
		isTransparent(Stone_Brick_Stairs);
		isTransparent(Wooden_Stairs);
		isTransparent(Nether_Brick_Stairs);
		isTransparent(Ladder);
		isTransparent(Fence);
		isTransparent(Fence_Gate);
		isTransparent(Cake_Block);
		isTransparent(Bed_Block);
		isTransparent(Wooden_Door_Block);
		isTransparent(Iron_Door_Block);
		isTransparent(Redstone_Repeater_Block_on);
		isTransparent(Redstone_Repeater_Block_off);
		isTransparent(Trapdoor);
		isTransparent(Rails);
		isTransparent(Detector_Rail);
		isTransparent(Powered_Rail);
		isTransparent(Lever);
		isTransparent(Wooden_Pressure_Plate);
		isTransparent(Stone_Pressure_Plate);
		isTransparent(Nether_Brick_Fence);
		isTransparent(Iron_Bars);
		isTransparent(Stone_Button);
		isTransparent(Vines);
		isTransparent(Redstone_Wire);
		isTransparent(Redstone_Torch_off);
		isTransparent(Redstone_Repeater_Block_on);
		isTransparent(Air);
		isTransparent(Snow_Block);
		isTransparent(Torch);
		isTransparent(Sign_Post);
		isTransparent(Wall_Sign);
		isTransparent(Fire);
		isTransparent(Portal);
		isTransparent(Cactus);
		isTransparent(Sugar_Cane);
		isTransparent(Wheat_Crops);
		isTransparent(Rose);
		isTransparent(Dandelion);
		isTransparent(Red_Mushroom);
		isTransparent(Brown_Mushroom);
		isTransparent(Sapling);
		isTransparent(Tall_Grass);
		isTransparent(Dead_Bush);
		isTransparent(Water);
		isTransparent(Lava);
		isTransparent(Enchantment_Table);
		
		itemVanishes(Dandelion, 0.4);
		itemVanishes(Rose, 0.4);
		itemVanishes(Wool, 0.2);
		itemVanishes(Brown_Mushroom, 0.5);
		itemVanishes(Red_Mushroom, 0.5);
		itemVanishes(Wooden_Stairs, 0.1);
		itemVanishes(Wooden_Door, 0.1);
		itemVanishes(Wood, 0.01);
		itemVanishes(Wooden_Plank, 0.1);
		itemVanishes(Ladder, 0.1);
		itemVanishes(Sign, 0.1);
		itemVanishes(Sugarcane, 0.2);
		itemVanishes(Pumpkin, 0.4);
		itemVanishes(Pumpkin_Seeds, 0.05);
		itemVanishes(Trapdoor, 0.1);
		itemVanishes(Vines, 0.7);
		itemVanishes(Fence, 0.1);
		itemVanishes(Fence_Gate, 0.1);
		itemTakesDamage(Iron_Axe, 0.2, 20, 251);
		itemTakesDamage(Iron_Boots, 0.1, 20, 196);
		itemVanishes(Iron_Door, 0.05);
		itemTakesDamage(Iron_Chestplate, 0.1, 20, 529);
		itemTakesDamage(Iron_Hoe, 0.2, 20, 251);
		itemTakesDamage(Iron_Helmet, 0.1, 20, 166);
		itemTakesDamage(Iron_Leggings, 0.1, 20, 226);
		itemTakesDamage(Iron_Pickaxe, 0.2, 20, 251);
		itemTakesDamage(Iron_Shovel, 0.2, 20, 251);
		itemTakesDamage(Iron_Sword, 0.2, 20, 251);
		itemTakesDamage(Gold_Axe, 0.2, 5, 33);
		itemTakesDamage(Gold_Hoe, 0.2, 5, 33);
		itemTakesDamage(Gold_Pickaxe, 0.2, 5, 33);
		itemTakesDamage(Gold_Shovel, 0.2, 5, 33);
		itemTakesDamage(Gold_Sword, 0.2, 5, 33);
		itemVanishes(Diamond_Axe, 0.01);
		itemVanishes(Diamond_Hoe, 0.01);
		itemVanishes(Diamond_Pickaxe, 0.01);
		itemVanishes(Diamond_Shovel, 0.01);
		itemVanishes(Diamond_Sword, 0.01);
		itemTakesDamage(Wooden_Axe, 0.3, 20, 60);
		itemTakesDamage(Wooden_Hoe, 0.3, 20, 60);
		itemTakesDamage(Wooden_Pickaxe, 0.3, 20, 60);
		itemTakesDamage(Wooden_Shovel, 0.3, 20, 60);
		itemTakesDamage(Wooden_Sword, 0.3, 20, 60);
		itemTakesDamage(Leather_Boots, 0.5, 7, 66);
		itemTakesDamage(Leather_Chestplate, 0.5, 7, 82);
		itemTakesDamage(Leather_Helmet, 0.5, 7, 56);
		itemTakesDamage(Leather_Leggings, 0.5, 7, 76);
		itemVanishes(Apple, 0.4);
		itemVanishes(String, 0.4);
		itemVanishes(Feather, 0.4);
		itemVanishes(Wheat_Seeds, 0.1);
		itemVanishes(Wheat, 0.4);
		itemVanishes(Bread, 0.8);
		itemVanishes(Cookie, 0.7);
		itemVanishes(Painting, 0.1);
		itemVanishes(Saddle, 0.1);
		itemVanishes(Snowball, 1.0);
		itemVanishes(Leather, 0.3);
		itemBecomes(Milk_Bucket, 1.0, Bucket);
		itemVanishes(Lava_Bucket, 1.0);
		itemVanishes(Paper, 0.1);
		itemVanishes(Book, 0.01);
		itemVanishes(Sugar, 0.3);
		itemVanishes(Map, 0.1);
		itemVanishes(Melon, 1.0);
		itemVanishes(Rotten_Flesh, 0.02);
		itemBecomes(Raw_Beef, 1.0, Rotten_Flesh);
		itemBecomes(Raw_Chicken, 1.0, Rotten_Flesh);
		itemBecomes(Raw_Fish, 1.0, Rotten_Flesh);
		itemBecomes(Raw_Porkchop, 1.0, Rotten_Flesh);
		itemBecomes(Steak, 0.8, Rotten_Flesh);
		itemBecomes(Cooked_Chicken, 1.0, Rotten_Flesh);
		itemBecomes(Cooked_Fish, 0.9, Rotten_Flesh);
		itemBecomes(Cooked_Porkchop, 1.0, Rotten_Flesh);
		
		hasTileEntity(Chest);
		hasTileEntity(Monster_Spawner);
		hasTileEntity(Dispenser);
		hasTileEntity(Note_Block);
		hasTileEntity(Furnace);
		hasTileEntity(Sign_Post);
		hasTileEntity(Wall_Sign);
		hasTileEntity(Jukebox);
		hasTileEntity(Enchantment_Table);
		hasTileEntity(Brewing_Stand);
		hasTileEntity(Cauldron);
		
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
		
		itemsFallThrough(UNKNOWN);
		itemsFallThrough(Air);
		itemsFallThrough(Water);
		//itemsFallThrough(Source_Water);
		itemsFallThrough(Lava);
		itemsFallThrough(More_Lava);
		itemsFallThrough(Rose);
		itemsFallThrough(Dandelion);
		itemsFallThrough(Tall_Grass);
		itemsFallThrough(Dead_Bush);
		itemsFallThrough(Brown_Mushroom);
		itemsFallThrough(Red_Mushroom);
		itemsFallThrough(Wheat_Crops);
		itemsFallThrough(Rails);
		itemsFallThrough(Wooden_Pressure_Plate);
		itemsFallThrough(Stone_Pressure_Plate);
		itemsFallThrough(Nether_Wart);
		itemsFallThrough(Brewing_Stand);
		itemsFallThrough(Fire);
		itemsFallThrough(Snow);
		itemsFallThrough(Lily_Pad);
		itemsFallThrough(Torch);
		itemsFallThrough(Lever);
		itemsFallThrough(Wall_Sign);
		itemsFallThrough(Stone_Button);
		
		rule().desc("Exposed cobble turns to gravel.").
				p(0.05).when(is(Cobblestone)).when(skyExposed()).when(nextToAtLeast(5, Air)).then(become(Gravel));
		rule().desc("Cobble near water or mossy cobble turns mossy.").
				p(0.001).when(is(Cobblestone)).moreLikelyWhen(inVicinityOf(0.12, 3, Water)).moreLikelyWhen(touching(0.06, Mossy_Cobblestone)).then(become(Mossy_Cobblestone));
		rule().desc("Cobble weathering.").
				p(0.0001).when(is(Cobblestone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
		rule().desc("Mossy cobble weathering.").
				p(0.0001).when(is(Mossy_Cobblestone)).moreLikelyWhen(skyExposed(0.3)).moreLikelyWhen(below(0.2, Air)).moreLikelyWhen(touching(0.00001, Air)).then(become(Air));
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
		rule().desc("Glowstone vanishing.").
				p(0.01).when(is(Glowstone)).moreLikelyWhen(skyExposed(0.05)).then(become(Air));
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
				p(0.2).when(is(Glass)).then(become(Air));
		rule().desc("Glass vanishing.").
				p(0.4).when(is(Glass_Pane)).then(become(Air));
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
		rule().desc("Wool vanishing.").
				p(0.1).when(is(Wool)).moreLikelyWhen(skyExposed(0.15)).moreLikelyWhen(below(0.1, Air)).moreLikelyWhen(touching(0.05, Air)).then(become(Air));
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
				moreLikelyWhen(connectedBlobContains(-0.001, Wood)).
				then(applyCollectively(new Rule().p(1.0).then(become(Air)))).
				then(applyNearby(8, new Rule().p(0.05).when(is(Air)).when(above(Grass)).then(become(Sapling))));
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
		
		// Chests
		/*rule().desc("Chests disappear very rarely.").
				p(0.001).when(isConnectedBlobOf(Chest)).then(applyCollectively(new Rule().p(1.0).then(become(Air))));*/
		rule().desc("Chests disappear only when empty.").
				p(0.05).when(is(Chest)).when(isEmpty()).then(become(Air));
		
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
		
		// Hice, tmp. qqDPS
		/*rule().desc("Spawning a house.").
				p(1.0).when(anyOf(Grass, Stone)).then(new CreateStructure(HOUSE, null, -2, 1, -2, 0, 15));*/
		
		// Spawning spawners
		secondRule().desc("Spawning a spawner.").
				p(0.5).when(anyOf(Cobblestone, Wooden_Plank, Nether_Brick, Sandstone, Brick, Stone_Brick, Mossy_Cobblestone, Double_Stone_Slab, Wooden_Double_Slab, Iron_Block, Gold_Block, Diamond_Block)).then(new CreateStructure(SPAWNER, null, -2, 1, -2, 0, 0)).
				then(new Outcome()
		{
			@Override
			public boolean perform(int x, int y, int z, MCMap map, Random r, ApplicationCache ac) {
				Tag t = new Tag(Tag.Type.TAG_Compound, null, new Tag[] { new Tag(Tag.Type.TAG_End, null, null) });
				t.addTag(new Tag(Tag.Type.TAG_String, "id", "MobSpawner"));
				t.addTag(new Tag(Tag.Type.TAG_Short, "Delay", Short.valueOf((short) 265)));
				t.addTag(new Tag(Tag.Type.TAG_Int, "z", Integer.valueOf(z)));
				t.addTag(new Tag(Tag.Type.TAG_String, "EntityId",
						new String[] {"Spider", "Skeleton", "Zombie"}[r.nextInt(3)]));
				t.addTag(new Tag(Tag.Type.TAG_Int, "y", Integer.valueOf(y + 1)));
				t.addTag(new Tag(Tag.Type.TAG_Int, "x", Integer.valueOf(x)));
				
				map.setTileEntity(t, x, y, z);
				return true;
			}
		});
		
		// Spawning trees.
		secondRule().desc("Spawning a tree.").
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
		
		secondRule().desc("North levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_North_on)).when(noSupportFrom(0, 0, 1)).then(become(Air));
		secondRule().desc("South levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_South_on)).when(noSupportFrom(0, 0, -1)).then(become(Air));
		secondRule().desc("East levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_East_on)).when(noSupportFrom(-1, 0, 0)).then(become(Air));
		secondRule().desc("West levers despawning.").
				p(1.0).when(is(Lever)).when(hasData(Lever_Facing_West_on)).when(noSupportFrom(1, 0, 0)).then(become(Air));
		
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
				if (cond.check() instanceof AnyOf) {
					for (int i = 0; i < ((AnyOf) cond.check()).types.length; i++) {
						int t = ((AnyOf) cond.check()).types[i];
						ruleTypes[t + 1] = true;
						typesL.add(t);
					}
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
				if (cond.check() instanceof AnyOf) {
					for (int i = 0; i < ((AnyOf) cond.check()).types.length; i++) {
						int t = ((AnyOf) cond.check()).types[i];
						secondRuleTypes[t + 1] = true;
						typesL.add(t);
					}
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
