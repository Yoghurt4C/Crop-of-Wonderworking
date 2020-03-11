package mods.coww.api.lens;

import net.minecraft.item.ItemStack;

public interface LensInterface extends LensEffectInterface {
    public int getLensColor(ItemStack stack);

    /**
     * Can the source lens be combined with the composite lens? This is called
     * for both the ILens instance of ItemStack.getItem() of sourceLens and compositeLens.
     */
    public boolean canCombineLenses(ItemStack sourceLens, ItemStack compositeLens);

    /**
     * Gets the composite lens in the stack passed in, return empty for none.
     */
    public ItemStack getCompositeLens(ItemStack stack);

    /**
     * Sets the composite lens for the sourceLens as the compositeLens, returns
     * the ItemStack with the combination.
     */
    public ItemStack setCompositeLens(ItemStack sourceLens, ItemStack compositeLens);

}
