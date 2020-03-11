package mods.coww.mixins;

import mods.coww.registry.cowwBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(ItemStack.class)
public abstract class GoatDetectorItemStackMixin {

    public GoatDetectorItemStackMixin() {
    }

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract boolean hasCustomName();

    @Shadow @Nullable
    public abstract CompoundTag getSubTag(String key);

    @Inject(method = "getName",at = @At("RETURN"))
    public Text coww_getGoatDetectorName(CallbackInfoReturnable<Text> ctx) {
        if (getItem().equals(cowwBlocks.GOAT_DETECTOR.asItem()) && this.hasCustomName()) {
            CompoundTag tag = this.getSubTag("display");
            return new LiteralText(tag.get("Name").asString().split(":")[1].replace("\"","").replace("}",""))
                    .append(" ")
                    .append(new TranslatableText(getItem().getTranslationKey()).asFormattedString().split(" ")[1]);
        } else return ctx.getReturnValue();
    }
}
