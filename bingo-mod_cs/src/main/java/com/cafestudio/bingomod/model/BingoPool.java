package com.cafestudio.bingomod.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public final class BingoPool {
	private final List<Item> items;

	public BingoPool() {
		this(createDefaultCatalog());
	}

	public BingoPool(List<Item> customItems) {
		Objects.requireNonNull(customItems, "customItems");
		this.items = List.copyOf(customItems);
	}

	public List<Item> getItems() {
		return items;
	}

	public int size() {
		return items.size();
	}

	private static List<Item> createDefaultCatalog() {
		List<Item> catalog = new ArrayList<>();

		catalog.add(Items.OAK_LOG);
		catalog.add(Items.BIRCH_LOG);
		catalog.add(Items.SPRUCE_LOG);
		catalog.add(Items.COBBLESTONE);
		catalog.add(Items.STONE);
		catalog.add(Items.DIRT);
		catalog.add(Items.SAND);
		catalog.add(Items.GRAVEL);
		catalog.add(Items.CLAY_BALL);
		catalog.add(Items.COAL);
		catalog.add(Items.CHARCOAL);
		catalog.add(Items.STICK);
		catalog.add(Items.TORCH);
		catalog.add(Items.CRAFTING_TABLE);
		catalog.add(Items.CHEST);
		catalog.add(Items.FURNACE);
		catalog.add(Items.RAW_IRON);
		catalog.add(Items.IRON_INGOT);
		catalog.add(Items.RAW_GOLD);
		catalog.add(Items.GOLD_INGOT);
		catalog.add(Items.RAW_COPPER);
		catalog.add(Items.COPPER_INGOT);
		catalog.add(Items.REDSTONE);
		catalog.add(Items.LAPIS_LAZULI);
		catalog.add(Items.DIAMOND);
		catalog.add(Items.EMERALD);
		catalog.add(Items.FLINT);
		catalog.add(Items.FLINT_AND_STEEL);
		catalog.add(Items.BUCKET);
		catalog.add(Items.WATER_BUCKET);
		catalog.add(Items.LAVA_BUCKET);
		catalog.add(Items.OBSIDIAN);
		catalog.add(Items.IRON_PICKAXE);
		catalog.add(Items.IRON_SWORD);
		catalog.add(Items.DIAMOND_SWORD);
		catalog.add(Items.DIAMOND_PICKAXE);
		catalog.add(Items.DIAMOND_BLOCK);
		catalog.add(Items.BOW);
		catalog.add(Items.ARROW);
		catalog.add(Items.SHIELD);
		catalog.add(Items.STRING);
		catalog.add(Items.FEATHER);
		catalog.add(Items.LEATHER);
		catalog.add(Items.BONE);
		catalog.add(Items.GUNPOWDER);
		catalog.add(Items.SPIDER_EYE);
		catalog.add(Items.ROTTEN_FLESH);
		catalog.add(Items.ENDER_PEARL);
		catalog.add(Items.APPLE);
		catalog.add(Items.BREAD);
		catalog.add(Items.COOKED_BEEF);
		catalog.add(Items.WHEAT);
		catalog.add(Items.CARROT);
		catalog.add(Items.POTATO);
		catalog.add(Items.SUGAR_CANE);
		catalog.add(Items.PAPER);
		catalog.add(Items.BOOK);
		catalog.add(Items.BOOKSHELF);
		catalog.add(Items.PUMPKIN);
		catalog.add(Items.MELON_SLICE);
		catalog.add(Items.EGG);
		catalog.add(Items.WHITE_WOOL);
		catalog.add(Items.BRICK);
		catalog.add(Items.GLASS);
		catalog.add(Items.SNOWBALL);
		catalog.add(Items.COMPASS);
		catalog.add(Items.CLOCK);
		catalog.add(Items.RAIL);
		catalog.add(Items.MINECART);
		catalog.add(Items.TNT);
		catalog.add(Items.SLIME_BALL);
		catalog.add(Items.HONEYCOMB);
		catalog.add(Items.NETHERRACK);
		catalog.add(Items.SOUL_SAND);
		catalog.add(Items.NETHER_WART);
		catalog.add(Items.BLAZE_ROD);
		catalog.add(Items.GLOWSTONE);
		catalog.add(Items.QUARTZ);
		catalog.add(Items.MAGMA_CREAM);
		catalog.add(Items.BLACKSTONE);
		catalog.add(Items.BASALT);
		catalog.add(Items.CRIMSON_STEM);
		catalog.add(Items.WARPED_STEM);
		catalog.add(Items.GOLDEN_APPLE);
		catalog.add(Items.ENCHANTING_TABLE);

		return Collections.unmodifiableList(catalog);
	}
}
