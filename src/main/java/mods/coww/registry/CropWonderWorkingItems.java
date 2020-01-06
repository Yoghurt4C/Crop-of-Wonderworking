package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.items.HoesMadItem;
import mods.coww.items.WaterBowlItem;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

public class CropWonderWorkingItems {
    static Item.Settings settings = new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup);

    public static Item SWEED_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.SWEED, settings);
    public static Item REDLON_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.REDLON, settings);
    public static Item LAZULLIA_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.LAZULLIA, settings);

    public static Item HOES_MAD = new HoesMadItem(ToolMaterials.IRON,-2F, settings);

    public static Item BOWL_OF_WATER = new WaterBowlItem(Fluids.WATER, new Item.Settings().recipeRemainder(Items.BOWL).maxCount(1).group(CropWonderWorking.CropWonderWorkingCoreGroup));

    public static void init() {
        register("sweed_seeds",SWEED_SEEDS);
        register("redlon_seeds",REDLON_SEEDS);
        register("lazullia_seeds",LAZULLIA_SEEDS);

        register("hoes_mad",HOES_MAD);
        register("bowl_of_water",BOWL_OF_WATER);
    }
    public static void register(String name, Item item) {
        Registry.register(Registry.ITEM, CropWonderWorking.getId(name), item);
    }
}