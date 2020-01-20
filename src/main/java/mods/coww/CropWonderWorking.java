package mods.coww;

import mods.coww.libcd.CauldronRecipeTweaker;
import mods.coww.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CropWonderWorking implements ModInitializer {

	public static ItemGroup CropWonderWorkingCoreGroup = FabricItemGroupBuilder.build(
			getId("core_group"),
			() -> new ItemStack(CropWonderWorkingItems.SWEED_SEEDS));

	@Override
	public void onInitialize() {
		CropWonderWorkingBlocks.init();
		CropWonderWorkingItems.init();
		CropWonderWorkingSounds.init();
		CropWonderWorkingRecipes.init();
		CropWonderWorkingEvents.init();
		if (FabricLoader.getInstance().isModLoaded("libcd")){
			CauldronRecipeTweaker.init();
		}
	}

	public static Identifier getId(String name) {
		return new Identifier("coww", name);
	}
	public static final String modid="coww";
}
