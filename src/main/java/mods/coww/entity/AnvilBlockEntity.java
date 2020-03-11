package mods.coww.entity;

import alexiil.mc.lib.attributes.fluid.amount.FluidAmount;
import mods.coww.power.cowwPowerTank;
import mods.coww.registry.cowwBlocks;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;

import java.util.List;

import static mods.coww.entity.CauldronBlockEntity.betterFromTag;

public class AnvilBlockEntity extends BlockEntity implements AnvilInventory, BlockEntityClientSerializable {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(16, ItemStack.EMPTY);
    public cowwPowerTank power = new cowwPowerTank(FluidAmount.of1620(1620*4));
    private List<ItemStack> lastRecipeStacks = null;
    public ItemStack lastRecipeResult = null;
    public int lastRecipeTimer = 0;

    public AnvilBlockEntity() {
        super(cowwBlocks.WONDERWORKING_ANVIL_BLOCKENTITY);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    public List<ItemStack> getLastRecipeStacks() {return lastRecipeStacks;}

    public ItemStack getLastRecipeResult() {return lastRecipeResult;}

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        power.fromTag(tag.getCompound("Power"));
        this.lastRecipeTimer=tag.getInt("LastRecipeTimer");
        if(tag.contains("LastRecipeResult")){ this.lastRecipeResult=ItemStack.fromTag(tag.getCompound("LastRecipeResult")); }
        betterFromTag(tag,items);
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("Power",power.toTag());
        tag.putInt("LastRecipeTimer",this.lastRecipeTimer);
        if(this.lastRecipeResult!=null){ tag.put("LastRecipeResult",this.lastRecipeResult.toTag(new CompoundTag())); }
        Inventories.toTag(tag,this.items);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag){this.fromTag(tag);}

    @Override
    public CompoundTag toClientTag(CompoundTag tag){return this.toTag(tag);}
}
