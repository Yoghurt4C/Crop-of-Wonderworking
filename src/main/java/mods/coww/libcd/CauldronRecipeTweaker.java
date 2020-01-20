package mods.coww.libcd;

import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeTweaker;
import mods.coww.recipes.CauldronRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;

public class CauldronRecipeTweaker {
    public void addCauldronRecipe(Object[] inputs, String fluid, String catalyst, String output){
        try {
            ItemStack outputItemStack = RecipeParser.processItemStack(output);
            Identifier recipeId = RecipeTweaker.INSTANCE.getRecipeId(outputItemStack);
            DefaultedList<Ingredient> ingredients = DefaultedList.of();
            for (int i = 0; i < Math.min(inputs.length, 9); i++) {
                Object id = inputs[i];
                if (id.equals("")) continue;
                ingredients.add(i, RecipeParser.processIngredient(id));
            }
            Ingredient catalystIngredient = RecipeParser.processIngredient(catalyst);
            RecipeTweaker.INSTANCE.addRecipe(new CauldronRecipe(ingredients, fluid, catalystIngredient, outputItemStack, recipeId));
        } catch (Exception e) {
            System.out.println("Error parsing cauldron recipe - " + e.getMessage());
        }
    }

    public static void init(){
        TweakerManager.INSTANCE.addAssistant("mods.coww.libcd.CauldronRecipeTweaker", new CauldronRecipeTweaker());
    }
}