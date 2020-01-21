package mods.coww;

import mods.coww.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CropWonderWorking implements ModInitializer {

	public static ItemGroup CropWonderWorkingCoreGroup = FabricItemGroupBuilder.build(
			cowwIdentifier("core_group"),
			() -> new ItemStack(CropWonderWorkingItems.SWEED_SEEDS));

	@Override
	public void onInitialize() {
		CropWonderWorkingBlocks.init();
		CropWonderWorkingItems.init();
		CropWonderWorkingSounds.init();
		CropWonderWorkingRecipes.init();
		CropWonderWorkingEvents.init();

	}

	public static final String modid="coww";
	public static Identifier cowwIdentifier(String name) {
		return new Identifier(modid, name);
	}
}
