package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.blocks.*;
import mods.coww.entity.power.PowerSpreaderBlockEntity;
import mods.coww.power.cowwHoneyFluid;
import mods.coww.power.cowwHoneyFluidBlock;
import mods.coww.entity.AnvilBlockEntity;
import mods.coww.entity.CauldronBlockEntity;
import mods.coww.entity.GoatDetectorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.BaseFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class cowwBlocks {

    public static Block SWEED = new SweedBlock(4,Block.Settings.copy(Blocks.BEETROOTS));
    public static Block REDLON = new RedlonBlock(Blocks.REDSTONE_BLOCK, 4, Block.Settings.copy(Blocks.MELON_STEM));
    public static Block ATTACHED_REDLON = new AttachedRedlonBlock(Blocks.REDSTONE_BLOCK,Block.Settings.copy(Blocks.ATTACHED_MELON_STEM));
    public static Block LAZULLIA = new LazulliaBlock(16,Block.Settings.copy(Blocks.WHEAT));
    public static Block RAINBOW_CACTI = new RainbowCactiBlock(Block.Settings.copy(Blocks.CACTUS));
    public static Block BRIAR = new BriarPlantBlock(4,Block.Settings.copy(Blocks.WHEAT));

    public static Block COWW_CAULDRON = new CauldronBlock(Block.Settings.copy(Blocks.CAULDRON));
    public static Block GOAT_DETECTOR = new GoatDetectorBlock(Block.Settings.copy(Blocks.OBSERVER));
    public static Block WONDERWORKING_ANVIL = new AnvilBlock(Block.Settings.copy(Blocks.ANVIL).nonOpaque());
    public static Block POWER_SPREADER = new PowerSpreaderBlock(Block.Settings.copy(Blocks.STONE));

    public static final BlockEntityType<CauldronBlockEntity> COWW_CAULDRON_BLOCKENTITY=BlockEntityType.Builder.create(CauldronBlockEntity::new,COWW_CAULDRON).build(null);
    public static final BlockEntityType<GoatDetectorBlockEntity> GOAT_DETECTOR_BLOCKENTITY=BlockEntityType.Builder.create(GoatDetectorBlockEntity::new,GOAT_DETECTOR).build(null);
    public static final BlockEntityType<AnvilBlockEntity> WONDERWORKING_ANVIL_BLOCKENTITY=BlockEntityType.Builder.create(AnvilBlockEntity::new,WONDERWORKING_ANVIL).build(null);
    public static final BlockEntityType<PowerSpreaderBlockEntity> POWER_SPREADER_BLOCK_ENTITY=BlockEntityType.Builder.create(PowerSpreaderBlockEntity::new,POWER_SPREADER).build(null);

    public static final BaseFluid THAUMATURGES_HONEY = new cowwHoneyFluid.Still();
    public static final BaseFluid FLOWING_THAUMATURGES_HONEY = new cowwHoneyFluid.Flowing();
    public static final Block THAUMATURGES_HONEY_FLUID = new cowwHoneyFluidBlock(Block.Settings.copy(Blocks.WATER));

    public static void init(){
        subRegister("sweed",SWEED);
        subRegister("redlon_stem",REDLON);
        subRegister("attached_redlon_stem",ATTACHED_REDLON);
        subRegister("lazullia",LAZULLIA);
        register("rainbow_cacti",RAINBOW_CACTI);
        register("briar",BRIAR);

        register("cauldron",COWW_CAULDRON);
        subRegister("goat_detector",GOAT_DETECTOR);
        register("wonderworking_anvil",WONDERWORKING_ANVIL);
        register("power_spreader", POWER_SPREADER);

        register("cauldron",COWW_CAULDRON_BLOCKENTITY);
        register("goat_detector",GOAT_DETECTOR_BLOCKENTITY);
        register("wonderworking_anvil", WONDERWORKING_ANVIL_BLOCKENTITY);
        register("power_spreader", POWER_SPREADER_BLOCK_ENTITY);

        register("thaumaturges_honey", THAUMATURGES_HONEY);
        register("flowing_thaumaturges_honey", FLOWING_THAUMATURGES_HONEY);
        subRegister("thaumaturges_honey", THAUMATURGES_HONEY_FLUID);
    }

    public static BlockItem register(String name, Block block, Item.Settings settings) {
        Identifier id = CropWonderWorking.cowwIdentifier(name);
        Registry.register(Registry.BLOCK, id, block);
        BlockItem item = new BlockItem(block, settings);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registry.ITEM, id, item);
        return item;
    }

    public static BlockItem register(String name, Block block) {
        return register(name, block, new Item.Settings().group(CropWonderWorking.cowwCoreGroup));
    }

    public static Block subRegister(String name, Block block){
        Identifier id = CropWonderWorking.cowwIdentifier(name);
        Registry.register(Registry.BLOCK, id, block);
        return block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType<T> build) {
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, CropWonderWorking.cowwIdentifier(name), build);
    }

    private static Fluid register(String name, Fluid fluid){
        Identifier id = CropWonderWorking.cowwIdentifier(name);
        return Registry.register(Registry.FLUID, id, fluid);
    }
}
