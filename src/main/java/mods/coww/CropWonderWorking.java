package mods.coww;

import mods.coww.api.power.PowerNetworkEvent;
import mods.coww.registry.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class CropWonderWorking implements ModInitializer {

	public static ItemGroup cowwCoreGroup = FabricItemGroupBuilder.build(
			cowwIdentifier("core_group"),
			() -> new ItemStack(cowwItems.SWEED_SEEDS));

	@Override
	public void onInitialize() {
		cowwBlocks.init();
		cowwEntities.init();
		cowwItems.init();
		cowwSounds.init();
		cowwRecipes.init();
		cowwEvents.init();
		cowwParticles.init();

		//once more, I have sinned
	}

	public static final String modid="coww";
	public static Identifier cowwIdentifier(String name) {
		return new Identifier(modid, name);
	}
}
