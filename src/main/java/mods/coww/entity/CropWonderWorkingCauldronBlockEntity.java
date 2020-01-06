package mods.coww.entity;

import mods.coww.blocks.CropWonderWorkingCauldronBlock;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.registry.CropWonderWorkingBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Hand;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static net.minecraft.block.CauldronBlock.LEVEL;

public class CropWonderWorkingCauldronBlockEntity extends BlockEntity implements CropWonderWorkingCauldronInventory, BlockEntityClientSerializable, Tickable {
    private static final int[] TOP_SLOTS = new int[]{0,1,2,3};
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private List<ItemStack> lastRecipeStacks = null;
    public static ItemStack lastRecipeResult = null;
    public int lastRecipeTimer = 0;
    private static Random random=new Random();

    public CropWonderWorkingCauldronBlockEntity() {
        super(CropWonderWorkingBlocks.COWW_CAULDRON_BLOCKENTITY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public List<ItemStack> getLastRecipeStacks() {return lastRecipeStacks;}

    public ItemStack getLastRecipeResult() {return lastRecipeResult;}

    @Override
    public int[] getInvAvailableSlots(Direction side) { return TOP_SLOTS; }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        this.lastRecipeTimer=tag.getInt("LastRecipeTimer");
        betterFromTag(tag,items);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putInt("LastRecipeTimer",this.lastRecipeTimer);
        Inventories.toTag(tag,this.items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag){this.fromTag(tag);}

    @Override
    public CompoundTag toClientTag(CompoundTag tag){return this.toTag(tag);}

    public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir){
        return this.isValidInvStack(slot, stack);
    }

    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) { return false; }

    public static void matchRecipeInputs(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand){
        final Inventory inventory = (SidedInventory) world.getBlockEntity(pos);
        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);

        if (match.isPresent()) {
            int level = state.get(LEVEL);
            ItemStack stack = player.getStackInHand(hand);
            if (match.get().getCatalyst().test(stack)) {
                stack.decrement(1);
                spawnCraftingResult(world, player, match.get().getOutput());
                ((CropWonderWorkingCauldronBlock)world.getBlockState(pos).getBlock()).setLevel(world, pos, state, level - 1);

                for (final Ingredient ingredient : match.get().getIngredients()) {
                    for (int i = 0; i <= inventory.getInvSize(); i++) {
                        if (ingredient.test(inventory.getInvStack(i))) {
                            inventory.removeInvStack(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void spawnCraftingResult(final World world, final PlayerEntity player, final ItemStack result) {
        player.inventory.offerOrDrop(world, result.copy());
    }

    public static void matchRecipeInputs(BlockState state, World world, BlockPos pos, Entity entity){
        final Inventory inventory = (SidedInventory) world.getBlockEntity(pos);
        final CropWonderWorkingCauldronBlockEntity cauldron = (CropWonderWorkingCauldronBlockEntity)world.getBlockEntity(pos);
        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);

        if (match.isPresent() && entity instanceof ItemEntity) {
            int level = state.get(LEVEL);
            ItemEntity ientity = entity instanceof ItemEntity ? (ItemEntity) entity : null;
            ItemStack stack = ientity.getStack();
            if (match.get().getCatalyst().test(stack) && level>0) {
                stack.decrement(1);
                spawnCraftingResult(world, pos, match.get().getOutput());
                ((CropWonderWorkingCauldronBlock)world.getBlockState(pos).getBlock()).setLevel(world, pos, state, level - 1);
                cauldron.saveLastRecipe();
                lastRecipeResult=match.get().getOutput().copy();

                for (final Ingredient ingredient : match.get().getIngredients()) {
                    for (int i = 0; i <= inventory.getInvSize(); i++) {
                        if (ingredient.test(inventory.getInvStack(i))) {
                            inventory.removeInvStack(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void spawnCraftingResult(final World world, final BlockPos pos, final ItemStack result) {
        double randomOffset=(random.nextDouble()*2-1)/20;
        final ItemEntity ientity = new ItemEntity(world,
                pos.getX()+0.5d,
                pos.getY() + 1,
                pos.getZ()+0.5d,
                result.copy());
        ientity.addVelocity(randomOffset,0.15,randomOffset);
        ((IItemEntity)ientity).setSpawnedByCauldron(true);
        world.spawnEntity(ientity);
    }

    public void saveLastRecipe() {
        Inventory inventory=this;
        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, inventory, world);
        lastRecipeStacks = new ArrayList<>();
        for(int i = 0; i < inventory.getInvSize(); i++) {
            ItemStack stack = inventory.getInvStack(i);
            if(stack.isEmpty()){break;}
            lastRecipeStacks.add(stack.copy());
        }
        lastRecipeTimer=400;
    }

    public void trySetLastRecipe(PlayerEntity player) {
        Inventory inventory=this;
        tryToSetLastRecipe(player, inventory, lastRecipeStacks);
        if(!inventory.isInvEmpty()){this.sync();}
    }

    public static void tryToSetLastRecipe(PlayerEntity player, Inventory inventory, List<ItemStack> lastRecipeStacks) {
        if(lastRecipeStacks == null || lastRecipeStacks.isEmpty() || player.world.isClient){ return; }

        int slot = 0;
        boolean stackMoved = false;
        for(ItemStack stack : lastRecipeStacks) {
            if(stack.isEmpty()){ continue; }

            for(int i = 0; i < player.inventory.getInvSize(); i++) {
                ItemStack playerStack = player.inventory.getInvStack(i);
                if(!playerStack.isEmpty() && playerStack.isItemEqual(stack) && ItemStack.areTagsEqual(stack, playerStack)) {
                    inventory.setInvStack(slot, playerStack.split(1));
                    stackMoved = true;
                    slot++;
                    break;
                }}}
        if(stackMoved) {
            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 10F);
            ((ServerPlayerEntity)player).container.sendContentUpdates();
        }
    }

    @Override
    public void tick(){
        if (this.getCachedState().get(LEVEL)>0 && lastRecipeTimer>0){ lastRecipeTimer--; }
    }

    public static void betterFromTag(CompoundTag tag, DefaultedList<ItemStack> stacks) {
        ListTag listTag = tag.getList("Items", 10);

        if (!listTag.isEmpty()) {
            for (int i = 0; i < listTag.size(); ++i) {
                CompoundTag compoundTag = listTag.getCompound(i);
                int j = compoundTag.getByte("Slot") & 255;
                if (j >= 0 && j < stacks.size()) {
                    stacks.set(j, ItemStack.fromTag(compoundTag));
                }
            }
        } else { stacks.clear(); }
    }
}
