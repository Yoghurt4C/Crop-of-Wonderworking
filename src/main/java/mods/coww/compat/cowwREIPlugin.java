package mods.coww.compat;

import me.shedaniel.rei.api.ConfigObject;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import me.shedaniel.rei.plugin.information.DefaultInformationDisplay;
import mods.coww.CropWonderWorking;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.registry.cowwBlocks;
import net.fabricmc.loader.api.SemanticVersion;
import net.fabricmc.loader.util.version.VersionParsingException;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class cowwREIPlugin implements REIPluginV0 {

    public static final Identifier PLUGIN = CropWonderWorking.cowwIdentifier("coww_rei_plugin");
    public static final Identifier CAULDRON = CropWonderWorking.cowwIdentifier("cauldron");

    @Override
    public Identifier getPluginIdentifier() {
        return PLUGIN;
    }

    @Override
    public SemanticVersion getMinimumVersion() throws VersionParsingException {
        return SemanticVersion.parse("3.2.5");
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new cowwREICategory<CauldronRecipe>());
    }

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        for (Recipe<?> recipe : recipeHelper.getAllSortedRecipes()) {
            if (recipe instanceof CauldronRecipe) {
                recipeHelper.registerDisplay(CAULDRON, new cowwREIDisplay<>((CauldronRecipe) recipe));
            }
        }
        DefaultPlugin.registerInfoDisplay(DefaultInformationDisplay.createFromEntry(EntryStack.create(cowwBlocks.COWW_CAULDRON), new TranslatableText("block.coww.cauldron"))
                .lines(new TranslatableText("cauldron.coww.info_line_1"),
                        new TranslatableText("cauldron.coww.info_line_2")
                ));
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        if (!ConfigObject.getInstance().isLoadingDefaultPlugin()) {
            return;
        }
        recipeHelper.registerWorkingStations(CAULDRON, EntryStack.create(cowwBlocks.COWW_CAULDRON));
    }
}
