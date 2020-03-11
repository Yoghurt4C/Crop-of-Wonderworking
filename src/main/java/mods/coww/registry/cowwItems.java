package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.power.cowwHoneyBucketItem;
import mods.coww.items.GoatDetectorBlockItem;
import mods.coww.items.HoesMadItem;
import mods.coww.items.RosehipSyrupItem;
import mods.coww.items.WaterBowlItem;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.registry.Registry;

import static mods.coww.CropWonderWorking.cowwCoreGroup;

public class cowwItems {
    private static final Item.Settings settings = new Item.Settings().group(cowwCoreGroup);

    public static Item SWEED_SEEDS = new AliasedBlockItem(cowwBlocks.SWEED, settings);
    public static Item REDLON_SEEDS = new AliasedBlockItem(cowwBlocks.REDLON, settings);
    public static Item LAZULLIA_SEEDS = new AliasedBlockItem(cowwBlocks.LAZULLIA, settings);

    public static Item BRIAR_FRUIT = new Item(settings);
    public static Item ROSEHIP_SYRUP = new RosehipSyrupItem(new Item.Settings().group(cowwCoreGroup).recipeRemainder(Items.GLASS_BOTTLE).food(new FoodComponent.Builder().hunger(2).saturationModifier(0.3F).alwaysEdible().build()).maxCount(16));

    public static Item HOES_MAD = new HoesMadItem(ToolMaterials.IRON,-2F, new Item.Settings().group(cowwCoreGroup));
    public static Item BOWL_OF_WATER = new WaterBowlItem(Fluids.WATER, new Item.Settings().maxCount(1).recipeRemainder(Items.BOWL).group(cowwCoreGroup));
    public static Item THAUMATURGES_HONEY_BUCKET = new cowwHoneyBucketItem(cowwBlocks.THAUMATURGES_HONEY, new Item.Settings().maxCount(1).recipeRemainder(Items.BUCKET).group(cowwCoreGroup));

    public static Item GOAT_DETECTOR = new GoatDetectorBlockItem(cowwBlocks.GOAT_DETECTOR,new Item.Settings().group(cowwCoreGroup));

    public static void init() {
        register("sweed_seeds",SWEED_SEEDS);
        register("redlon_seeds",REDLON_SEEDS);
        register("lazullia_seeds",LAZULLIA_SEEDS);

        register("briar_fruit",BRIAR_FRUIT);
        register("rosehip_syrup",ROSEHIP_SYRUP);

        register("hoes_mad",HOES_MAD);
        register("bowl_of_water",BOWL_OF_WATER);
        register("thaumaturges_honey_bucket", THAUMATURGES_HONEY_BUCKET);

        register("goat_detector",GOAT_DETECTOR);
    }
    public static void register(String name, Item item) {
        Registry.register(Registry.ITEM, CropWonderWorking.cowwIdentifier(name), item);
    }
}