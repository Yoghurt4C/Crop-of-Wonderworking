package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.blocks.*;
import mods.coww.entity.CropWonderWorkingCauldronBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class CropWonderWorkingBlocks {

    public static Block SWEED = new SweedBlock(4,Block.Settings.copy(Blocks.BEETROOTS));
    public static Block REDLON = new RedlonBlock(Blocks.REDSTONE_BLOCK, Block.Settings.copy(Blocks.MELON_STEM));
    public static Block ATTACHED_REDLON = new AttachedRedlonBlock(Blocks.REDSTONE_BLOCK,Block.Settings.copy(Blocks.ATTACHED_MELON_STEM));
    public static Block LAZULLIA = new LazulliaBlock(16,Block.Settings.copy(Blocks.WHEAT));
    public static Block RAINBOW_CACTI = new RainbowCactiBlock(Block.Settings.copy(Blocks.CACTUS));

    public static Block COWW_CAULDRON = new CropWonderWorkingCauldronBlock(Block.Settings.copy(Blocks.CAULDRON));

    public static final BlockEntityType<CropWonderWorkingCauldronBlockEntity> COWW_CAULDRON_BLOCKENTITY=BlockEntityType.Builder.create(CropWonderWorkingCauldronBlockEntity::new,CropWonderWorkingBlocks.COWW_CAULDRON).build(null);

    public static void init(){
        subRegister("sweed",SWEED);
        subRegister("redlon_stem",REDLON);
        subRegister("attached_redlon_stem",ATTACHED_REDLON);
        subRegister("lazullia",LAZULLIA);
        register("rainbow_cacti",RAINBOW_CACTI);

        register("cauldron",COWW_CAULDRON);
        beRegister("cauldron",COWW_CAULDRON_BLOCKENTITY);
    }

    public static BlockItem register(String name, Block block, Item.Settings settings) {
        Identifier id = CropWonderWorking.getId(name);
        Registry.register(Registry.BLOCK, id, block);
        BlockItem item = new BlockItem(block, settings);
        item.appendBlocks(Item.BLOCK_ITEMS, item);
        Registry.register(Registry.ITEM, id, item);
        return item;
    }

    public static BlockItem register(String name, Block block) {
        return register(name, block, new Item.Settings().group(CropWonderWorking.CropWonderWorkingCoreGroup));
    }

    public static Block subRegister(String name, Block block){
        Identifier id = CropWonderWorking.getId(name);
        Registry.register(Registry.BLOCK, id, block);
        return block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> beRegister(String name, BlockEntityType<T> build) {
        return Registry.register(Registry.BLOCK_ENTITY, CropWonderWorking.getId(name), build);
    }
}
