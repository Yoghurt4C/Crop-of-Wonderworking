package mods.coww.items;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GoatDetectorBlockItem extends BlockItem {
    public GoatDetectorBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public static boolean writeTagToBlockEntity(World world, PlayerEntity player, BlockPos pos, ItemStack stack) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be != null) {
                if (!world.isClient && be.shouldNotCopyTagFromItem() && (player == null || !player.isCreativeLevelTwoOp())) {
                    return false;
                }
                CompoundTag beWriteTag = be.toTag(new CompoundTag());
                beWriteTag.putInt("x", pos.getX());
                beWriteTag.putInt("y", pos.getY());
                beWriteTag.putInt("z", pos.getZ());
                if (stack.hasTag()) {
                    if (stack.getTag().contains("display")) {
                        beWriteTag.put("display",stack.getSubTag("display"));
                    }
                    be.fromTag(beWriteTag);
                }
            }
        }
        return false;
    }

    protected boolean postPlacement(BlockPos pos, World world, @Nullable PlayerEntity player, ItemStack stack, BlockState state) {
        return writeTagToBlockEntity(world, player, pos, stack);
    }
}
