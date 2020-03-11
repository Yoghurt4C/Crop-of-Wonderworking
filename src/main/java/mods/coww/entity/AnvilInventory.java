package mods.coww.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.Direction;

import javax.annotation.Nullable;

public interface AnvilInventory extends Inventory {

    DefaultedList<ItemStack> getItems();

    @Override
    default int getInvSize() { return getItems().size(); }

    default DefaultedList<ItemStack> getItemsInInv() {return getItems();}

    default boolean canInsertInvStack(int slot, ItemStack stack, @Nullable Direction dir){ return false; }

    default boolean canExtractInvStack(int slot, ItemStack stack, Direction dir) { return false; }

    @Override
    default boolean isInvEmpty() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    default boolean isInvFull() {
        for (int i = 0; i < getInvSize(); i++) {
            ItemStack stack = getInvStack(i);
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default ItemStack getInvStack(int slot) { return getItems().get(slot); }

    @Override
    default ItemStack takeInvStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }

    @Override
    default ItemStack removeInvStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default void setInvStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getInvMaxStackAmount()) {
            stack.setCount(getInvMaxStackAmount());
        }
    }

    @Override
    default void clear() { getItems().clear(); }

    @Override
    default void markDirty() { }

    @Override
    default boolean canPlayerUseInv(PlayerEntity player) { return true; }

    @Override
    default int getInvMaxStackAmount() { return 1; }
}
