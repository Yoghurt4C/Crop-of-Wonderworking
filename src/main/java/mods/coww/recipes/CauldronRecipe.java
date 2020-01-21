package mods.coww.recipes;

import mods.coww.CropWonderWorking;
import mods.coww.registry.CropWonderWorkingRecipes;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CauldronRecipe implements Recipe<Inventory> {
    private final List<Ingredient> ingredients;
    private final String fluid;
    private final Ingredient catalyst;
    private final ItemStack result;
    private final Identifier recipeId;

    CauldronRecipe(Ingredient ingredient, String fluid, Ingredient catalyst, ItemStack result, Identifier recipeId) {
        ingredients = Collections.singletonList(ingredient);
        this.fluid = fluid;
        this.catalyst = catalyst;
        this.result = result;
        this.recipeId = recipeId;
    }

    CauldronRecipe(ItemStack result, String fluid, Ingredient catalyst, Identifier recipeId, Ingredient... ingredients) {
        this.ingredients = Arrays.asList(ingredients);
        this.fluid = fluid;
        this.catalyst = catalyst;
        this.result = result;
        this.recipeId = recipeId;
    }

    CauldronRecipe(List<Ingredient> ingredients,  String fluid, Ingredient catalyst, ItemStack result, Identifier recipeId) {
        this.ingredients = ingredients;
        this.fluid = fluid;
        this.catalyst = catalyst;
        this.result = result;
        this.recipeId = recipeId;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /*
    @Override
    public boolean matches(Inventory inventory, World world) {
        final ArrayList<ItemStack> inventoryList = new ArrayList<>();
        for(int i = 0; i < inventory.getInvSize(); i++) {
            inventoryList.add(inventory.getInvStack(i));
        }

        return hasRequiredIngredients(inventoryList);
    }
     */

    @Override
    public boolean matches(Inventory inventory, World world) {
        List<Ingredient> ingredientsMissing = new ArrayList<>(ingredients);

        for(int i = 0; i < inventory.getInvSize(); i++) {
            ItemStack input = inventory.getInvStack(i);
            if(input.isEmpty())
                break;

            int stackIndex = -1;

            for(int j = 0; j < ingredientsMissing.size(); j++) {
                Ingredient ingredient = ingredientsMissing.get(j);
                if(ingredient.test(input)) {
                    stackIndex = j;
                    break;
                }
            }

            if(stackIndex != -1)
                ingredientsMissing.remove(stackIndex);
            else return false;
        }

        return ingredientsMissing.isEmpty();
    }

    private boolean hasRequiredIngredients(List<ItemStack> toCheck) {
        for (final Ingredient ingredient : ingredients) {
            boolean hasIngredient = false;
            for (final ItemStack potentialIngredient : toCheck) {
                if (ingredient.test(potentialIngredient)) {
                    toCheck.remove(potentialIngredient);
                    hasIngredient = true;
                    break;
                }
            }

            if (!hasIngredient) {
                return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack craft(Inventory inventory) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int var1, int var2) {
        return false;
    }

    public String getFluid() { return fluid; }

    public Ingredient getCatalyst(){
        return catalyst;
    }

    @Override
    public ItemStack getOutput() {
        return result;
    }

    @Override
    public Identifier getId() {
        return recipeId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CropWonderWorkingRecipes.CAULDRON_RECIPE_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CauldronRecipe> {
        public static final Type INSTANCE = new Type();
        public static final Identifier ID = CropWonderWorking.cowwIdentifier("cauldron");

        private Type() {
            // NO-OP
        }
    }
}
