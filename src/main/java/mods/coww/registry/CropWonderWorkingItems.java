package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.items.HoesMadItem;
import mods.coww.items.MobJarBlockItem;
import mods.coww.items.RosehipSyrupItem;
import mods.coww.items.WaterBowlItem;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

public class CropWonderWorkingItems {
    private static final Item.Settings settings = new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup);

    public static Item SWEED_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.SWEED, settings);
    public static Item REDLON_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.REDLON, settings);
    public static Item LAZULLIA_SEEDS = new AliasedBlockItem(CropWonderWorkingBlocks.LAZULLIA, settings);

    public static Item BRIAR_FRUIT = new Item(settings);
    public static Item ROSEHIP_SYRUP = new RosehipSyrupItem(new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).alwaysEdible().build()).maxCount(16));

    public static Item HOES_MAD = new HoesMadItem(ToolMaterials.IRON,-2F, new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup));
    public static Item BOWL_OF_WATER = new WaterBowlItem(Fluids.WATER, new Item.Settings().maxCount(1).recipeRemainder(Items.BOWL).group(CropWonderWorking.CropWonderWorkingCoreGroup));
    public static BlockItem MOB_JAR = new MobJarBlockItem(CropWonderWorkingBlocks.MOB_JAR,new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup));

    public static void init() {
        register("sweed_seeds",SWEED_SEEDS);
        register("redlon_seeds",REDLON_SEEDS);
        register("lazullia_seeds",LAZULLIA_SEEDS);

        register("briar_fruit",BRIAR_FRUIT);
        register("rosehip_syrup",ROSEHIP_SYRUP);

        register("hoes_mad",HOES_MAD);
        register("bowl_of_water",BOWL_OF_WATER);
        register("mob_jar",MOB_JAR);
    }
    public static void register(String name, Item item) {
        Registry.register(Registry.ITEM, CropWonderWorking.getId(name), item);
    }
}