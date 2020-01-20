package mods.coww.registry;

import mods.coww.CropWonderWorking;
import mods.coww.recipes.CauldronRecipe;
import mods.coww.recipes.CauldronRecipeSerializer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.registry.Registry;

public class CropWonderWorkingRecipes {
    private static final String CAULDRON_RECIPE_ID = "cauldron";
    public static RecipeSerializer<CauldronRecipe> CAULDRON_RECIPE_SERIALIZER;
    public static RecipeType<CauldronRecipe> CAULDRON_RECIPE_TYPE;

    public static void init(){
        CAULDRON_RECIPE_SERIALIZER = registerRecipeSerializer(CAULDRON_RECIPE_ID, new CauldronRecipeSerializer());
        CAULDRON_RECIPE_TYPE = registerRecipeType(CAULDRON_RECIPE_ID);
    }

    public static <T extends RecipeSerializer<?>> T registerRecipeSerializer(String name, T serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, CropWonderWorking.getId(name), serializer);
    }

    public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(String name) {
        return Registry.register(Registry.RECIPE_TYPE, CropWonderWorking.getId(name), new RecipeType<T>() {
            @Override
            public String toString() {
                return name;
            }
        });
    }
}
