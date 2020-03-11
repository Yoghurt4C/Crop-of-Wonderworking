package mods.coww.entity;

import alexiil.mc.lib.attributes.Simulation;
import alexiil.mc.lib.attributes.fluid.FixedFluidInv;
import alexiil.mc.lib.attributes.fluid.FluidVolumeUtil;
import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import alexiil.mc.lib.attributes.fluid.impl.SimpleFixedFluidInv;
import alexiil.mc.lib.attributes.fluid.mixin.api.IBucketItem;
import alexiil.mc.lib.attributes.fluid.volume.PotionFluidKey;
import mods.coww.blocks.CauldronBlock;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.registry.cowwBlocks;
import mods.coww.registry.cowwItems;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
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

import static alexiil.mc.lib.attributes.fluid.FluidVolumeUtil.interactWithTank;
import static net.minecraft.block.CauldronBlock.LEVEL;

public class CauldronBlockEntity extends BlockEntity implements CauldronInventory, BlockEntityClientSerializable, Tickable {
    private static final int[] TOP_SLOTS = new int[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16};
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);
    public SimpleFixedFluidInv fluid = new SimpleFixedFluidInv(1, FluidAmount.BUCKET);
    private List<ItemStack> lastRecipeStacks = null;
    public ItemStack lastRecipeResult = null;
    public int lastRecipeTimer = 0;
    private static Random random=new Random();

    public CauldronBlockEntity() {
        super(cowwBlocks.COWW_CAULDRON_BLOCKENTITY);
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
        fluid.fromTag(tag.getCompound("Fluid"));
        this.lastRecipeTimer=tag.getInt("LastRecipeTimer");
        if(tag.contains("LastRecipeResult")){ this.lastRecipeResult=ItemStack.fromTag(tag.getCompound("LastRecipeResult")); }
        betterFromTag(tag,items);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Fluid",fluid.toTag());
        tag.putInt("LastRecipeTimer",this.lastRecipeTimer);
        if(this.lastRecipeResult!=null){ tag.put("LastRecipeResult",this.lastRecipeResult.toTag(new CompoundTag())); }
        Inventories.toTag(tag,this.items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag){this.fromTag(tag);}

    @Override
    public CompoundTag toClientTag(CompoundTag tag){return this.toTag(tag);}

    public boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir){
        if (this.getInvStack(slot).isEmpty()) {
            this.sync();
            return this.isValidInvStack(slot, stack);
        } else return false;
    }

    public boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) { return false; }

    public void matchRecipeInputs(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand){
        final CauldronBlockEntity cauldron = (CauldronBlockEntity)world.getBlockEntity(pos);
        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, cauldron, world);

        if (match.isPresent()) {
            int level = state.get(LEVEL);
            ItemStack stack = player.getStackInHand(hand);
            if (match.get().getCatalyst().test(stack) && level > 0) {
                stack.decrement(1);
                spawnCraftingResult(world, player, match.get().getOutput());
                cauldron.fluid.getInvFluid(0).split(FluidAmount.BOTTLE);
                ((CauldronBlock)world.getBlockState(pos).getBlock()).setLevel(world, pos, state, level - 1);
                cauldron.saveLastRecipe();
                lastRecipeResult=match.get().getOutput().copy();

                for (final Ingredient ingredient : match.get().getIngredients()) {
                    for (int i = 0; i <= cauldron.getInvSize(); i++) {
                        if (ingredient.test(cauldron.getInvStack(i))) {
                            cauldron.removeInvStack(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void spawnCraftingResult(final World world, final PlayerEntity player, final ItemStack result) {
        player.inventory.offerOrDrop(world, result.copy());
    }

    public void matchRecipeInputs(BlockState state, World world, BlockPos pos, ItemStack stack){
        final CauldronBlockEntity cauldron = (CauldronBlockEntity)world.getBlockEntity(pos);
        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, cauldron, world);

        if (match.isPresent()) {
            String fluidString = (cauldron.fluid.getInvFluid(0).fluidKey.toString().split(" "))[2].replace("}","");
            int level = state.get(LEVEL);
            if (match.get().getCatalyst().test(stack) && fluidString.equals(match.get().getFluid()) && level>0) {
                stack.decrement(1);
                spawnCraftingResult(world, pos, match.get().getOutput());
                cauldron.fluid.getInvFluid(0).split(FluidAmount.BOTTLE);
                ((CauldronBlock)world.getBlockState(pos).getBlock()).setLevel(world, pos, state, cauldron.fluid.getInvFluid(0).getAmount_F().as1620() / 540);
                cauldron.saveLastRecipe();
                lastRecipeResult=match.get().getOutput().copy();

                for (final Ingredient ingredient : match.get().getIngredients()) {
                    for (int i = 0; i <= cauldron.getInvSize(); i++) {
                        if (ingredient.test(cauldron.getInvStack(i))) {
                            cauldron.removeInvStack(i);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void spawnCraftingResult(final World world, final BlockPos pos, final ItemStack result) {
        double randomOffset=(random.nextDouble()*2-1)/20;
        final ItemEntity ientity = new ItemEntity(world,
                pos.getX()+0.5d,
                pos.getY() + 1,
                pos.getZ()+0.5d,
                result.copy());
        ientity.addVelocity(randomOffset,0.15,randomOffset);
        ((IItemEntity)ientity).coww_setSpawnedByCauldron(true);
        world.spawnEntity(ientity);
    }

    public void interact(CauldronBlockEntity cauldron, PlayerEntity player, Hand hand) {
        interactWithTank((FixedFluidInv) cauldron.fluid, player, hand);
        ((CauldronBlock)world.getBlockState(pos).getBlock()).setLevel(world, pos, world.getBlockState(pos), cauldron.fluid.getInvFluid(0).getAmount_F().as1620()/540);
    }

    public void handleInventory(CauldronBlockEntity cauldron, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        int level = cauldron.getCachedState().get(LEVEL);
        if (!stack.isEmpty()) {
            if (stack.getItem().equals(Items.BOWL) && level == 3) {
                if (!world.isClient) {
                    if (!player.abilities.creativeMode) {
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(cowwItems.BOWL_OF_WATER));
                        } else player.inventory.offerOrDrop(world, new ItemStack(cowwItems.BOWL_OF_WATER)); }
                    world.playSound(null,pos,SoundEvents.ENTITY_GENERIC_SPLASH,SoundCategory.BLOCKS,1F,1F); }
                cauldron.fluid.setInvFluid(0,FluidVolumeUtil.EMPTY,Simulation.ACTION);
                world.setBlockState(pos,cauldron.getCachedState().with(LEVEL,0));
            } else if (stack.getItem().equals(cowwItems.BOWL_OF_WATER) && level<3) {
                cauldron.fluid.attemptInsertion(((IBucketItem)stack.getItem()).libblockattributes__getFluid(stack).withAmount(FluidAmount.BUCKET),Simulation.ACTION);
                if (!world.isClient) {
                    if (!player.abilities.creativeMode) {
                        stack.decrement(1);
                        if (stack.isEmpty()) {
                            player.setStackInHand(hand, new ItemStack(Items.BOWL));
                        } else player.inventory.offerOrDrop(world, new ItemStack(Items.BOWL)); }
                    world.playSound(null,pos,SoundEvents.ENTITY_GENERIC_SPLASH,SoundCategory.BLOCKS,1F,1F); }
                world.setBlockState(pos,cauldron.getCachedState().with(LEVEL,3));
            } else if (stack.getItem() instanceof IBucketItem
                    && cauldron.fluid.getInvFluid(0).getAmount_F().as1620()%((IBucketItem)player.getStackInHand(hand).getItem()).libblockattributes__getFluidVolumeAmount().as1620()==0) {
                cauldron.interact(cauldron, player, hand);
            } else
                for (int i = 0; i < cauldron.getInvSize(); i++) {
                    if (!cauldron.getInvStack(i).isEmpty()) {
                        final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, cauldron, world);

                        if (match.isPresent()) {
                            cauldron.matchRecipeInputs(cauldron.getCachedState(), world, pos, player, hand);
                            if(!world.isClient){splash(world, pos);}
                            if (cauldron.isInvEmpty()) {
                                break;
                            }
                        }
                    } else if (cauldron.getInvStack(i).isEmpty()) {
                        cauldron.setInvStack(i, stack.copy());
                        stack.decrement(1);
                        if(!world.isClient){splash(world, pos);}
                        break;
                    }
                }
        } else {
            if (!player.isSneaking()) {
                if (cauldron.isInvEmpty() && cauldron.getLastRecipeStacks() != null
                        && cauldron.lastRecipeTimer > 0 && !cauldron.getLastRecipeStacks().isEmpty()) { cauldron.trySetLastRecipe(player); }
            } else {
                for (int j = cauldron.getInvSize() - 1; j >= 0; j--) {
                    if (!cauldron.isInvEmpty() && player.isSneaking()) {
                        if (!cauldron.getInvStack(j).isEmpty()) {
                            player.inventory.offerOrDrop(world, cauldron.getInvStack(j));
                            cauldron.removeInvStack(j);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void interact(CauldronBlockEntity cauldron, ItemStack stack) {
        IBucketItem bucket = (IBucketItem) stack.getItem();
        if (cauldron.fluid.getInvFluid(0).isEmpty() && !bucket.libblockattributes__getFluid(stack).isEmpty()
                ||bucket.libblockattributes__getFluid(stack)==cauldron.fluid.getInvFluid(0).fluidKey
                && !bucket.libblockattributes__getFluid(stack).isEmpty() && cauldron.getCachedState().get(LEVEL)<3 ) {
            cauldron.fluid.attemptInsertion(bucket.libblockattributes__getFluid(stack).withAmount(bucket.libblockattributes__getFluidVolumeAmount()), Simulation.ACTION);
            if (bucket instanceof PotionItem) {
                spawnCraftingResult(world, pos, new ItemStack(Items.GLASS_BOTTLE));
            } else if (bucket instanceof FishBucketItem) {
                ((FishBucketItem) stack.getItem()).onEmptied(world, stack, pos);
                spawnCraftingResult(world, pos, new ItemStack(Items.BUCKET));
            } else {spawnCraftingResult(world, pos, new ItemStack(stack.getItem().getRecipeRemainder()));}
            stack.decrement(1);
            ((CauldronBlock) world.getBlockState(pos).getBlock()).setLevel(world, pos, world.getBlockState(pos), cauldron.fluid.getInvFluid(0).getAmount_F().as1620() / 540);
        } else if (!cauldron.fluid.getInvFluid(0).isEmpty() && bucket.libblockattributes__getFluid(stack).isEmpty()
                && (cauldron.fluid.getInvFluid(0).getAmount_F().as1620() % bucket.libblockattributes__getFluidVolumeAmount().as1620())==0) {
            if (bucket == Items.BUCKET && cauldron.fluid.getInvFluid(0).fluidKey instanceof PotionFluidKey) {
                handleInventory(cauldron, stack);
            } else {
                cauldron.fluid.attemptExtraction(cauldron.fluid.getFilterForTank(0), bucket.libblockattributes__getFluidVolumeAmount(), Simulation.ACTION);
                spawnCraftingResult(world, pos, bucket.libblockattributes__withFluid(cauldron.fluid.getInvFluid(0).fluidKey));
                stack.decrement(1);
                ((CauldronBlock) world.getBlockState(pos).getBlock()).setLevel(world, pos, world.getBlockState(pos), cauldron.fluid.getInvFluid(0).getAmount_F().as1620() / 540);
            }
        } else handleInventory(cauldron,stack);
    }

    public void handleInventory(CauldronBlockEntity cauldron, ItemStack stack){
        for (int i = 0; i < cauldron.getInvSize(); i++) {
            if (!cauldron.getInvStack(i).isEmpty()) {
                final Optional<CauldronRecipe> match = world.getRecipeManager().getFirstMatch(CauldronRecipe.Type.INSTANCE, cauldron, world);

                if (match.isPresent()) {
                    cauldron.matchRecipeInputs(cauldron.getCachedState(), world, pos, stack);
                }
            } else if (cauldron.getInvStack(i).isEmpty()) {
                cauldron.setInvStack(i, stack.copy());
                stack.decrement(1);
                break;
            }
        }
        if (!cauldron.isInvFull()) {
            if(!world.isClient){splash(world, pos);}
        }
    }

    public void splash (World world, BlockPos pos) {
        if (world.getBlockState(pos).get(LEVEL)>0) {
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1F, 10F);
        } else world.playSound(null, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 0.75F, 5F);
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
