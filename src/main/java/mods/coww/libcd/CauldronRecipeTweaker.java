package mods.coww.libcd;

import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeTweaker;
import mods.coww.recipes.CauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

public class CauldronRecipeTweaker {
    public void addCauldronRecipe(Object[] inputs, String fluid, Object catalyst, Object output){
        try {
            ItemStack outputItemStack = RecipeParser.processItemStack(output);
            Identifier recipeId = RecipeTweaker.INSTANCE.getRecipeId(outputItemStack);
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            for (int i = 0; i < Math.min(inputs.length, 16); i++) {
                Object id = inputs[i];
                ingredients.add(i, RecipeParser.processIngredient(id));
            }
            Ingredient catalystIngredient = RecipeParser.processIngredient(catalyst);
            RecipeTweaker.INSTANCE.addRecipe(new CauldronRecipe(ingredients, fluid, catalystIngredient, outputItemStack, recipeId));
        } catch (Exception e) {
            System.out.println("Error parsing cauldron recipe - " + e.getMessage());
        }
    }
}