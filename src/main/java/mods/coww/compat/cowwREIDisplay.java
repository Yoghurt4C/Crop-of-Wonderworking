package mods.coww.compat;

import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.utils.CollectionUtils;
import mods.coww.recipes.CauldronRecipe;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class cowwREIDisplay<R extends CauldronRecipe> implements RecipeDisplay {
    private final R recipe;
    private List<List<EntryStack>> ingredients;
    private String fluid;
    private List<EntryStack> catalysts;
    private List<EntryStack> result;

    public cowwREIDisplay(R recipe){
        this.recipe = recipe;
        this.ingredients = CollectionUtils.map(recipe.getIngredients(), ingredient -> CollectionUtils.map(ingredient.getMatchingStacksClient(), EntryStack::create));
        this.fluid = recipe.getFluid();
        this.catalysts=CollectionUtils.map(recipe.getCatalyst().getMatchingStacksClient(),EntryStack::create);
        this.result= Collections.singletonList(EntryStack.create(recipe.getOutput()));
    }

    @Override
    public List<List<EntryStack>> getRequiredEntries() {
        return ingredients;
    }

    public List<EntryStack> getCatalysts() {
        return catalysts;
    }

    @Override
    public List<EntryStack> getOutputEntries() {
        return result;
    }

    public String getFluid() {
        return fluid;
    }

    @Override
    public Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(recipe).map(CauldronRecipe::getId);
    }

    @Override
    public List<List<EntryStack>> getInputEntries() {
        return ingredients;
    }

    @Override
    public Identifier getRecipeCategory() {
        return cowwREIPlugin.CAULDRON;
    }
}
