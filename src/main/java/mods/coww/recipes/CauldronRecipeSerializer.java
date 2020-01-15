package mods.coww.recipes;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;

public class CauldronRecipeSerializer implements RecipeSerializer<CauldronRecipe> {
    private static final String INPUT_KEY = "input";
    private static final String FLUID_KEY = "fluid";
    private static final String CATALYST_KEY = "catalyst";
    private static final String RESULT_KEY = "result";
    private static final String ITEM_KEY = "item";
    private static final String COUNT_KEY = "count";

    @Override
    public CauldronRecipe read(Identifier id, JsonObject json) {
        final Item result;
        int count;
        final String fluid;
        final Ingredient catalyst;
        final ArrayList<Ingredient> ingredients = getIngredientList(json);

        // get item result && count
        if(json.get(RESULT_KEY)==null) {
            throw new JsonSyntaxException("Expected a JsonObject as \"" + RESULT_KEY + "\", got ... nothing" + "!\n" + prettyPrintJson(json));
        } else if(json.get(RESULT_KEY).isJsonObject()) {
            final JsonObject resultObject = json.getAsJsonObject(RESULT_KEY);
            result = getItem(resultObject);
            count = getCount(resultObject);
        } else {
            throw new JsonSyntaxException("Expected a JsonObject as \"" + RESULT_KEY + "\", got " + json.get(INPUT_KEY).getClass() + "\n" + prettyPrintJson(json));
        }


        fluid=getFluid(json.getAsJsonObject());

        if(json.get(CATALYST_KEY)==null) {
            throw new JsonSyntaxException("Expected a JsonObject as \"" + CATALYST_KEY + "\", got ... nothing" + "!\n" + prettyPrintJson(json));
        } else if(json.get(CATALYST_KEY).isJsonObject()){
            final JsonObject catalystObject = json.getAsJsonObject(CATALYST_KEY);
            catalyst = Ingredient.fromJson(catalystObject);
        } else {
            throw new JsonSyntaxException("Expected a JsonObject as \"" + CATALYST_KEY + "\", got " + json.get(INPUT_KEY).getClass() + "\n" + prettyPrintJson(json));
        }

        verifyIngredientsList(ingredients, json);

        return new CauldronRecipe(ingredients, fluid, catalyst, new ItemStack(result, count), id);
    }

    /**
     * Verifies the given list of {@link Ingredient}s is not empty.
     * If the list is empty, an exception is thrown, which prints the offending json recipe.
     * @param ingredients list of Ingredients to check
     * @param originalJson original JsonObject to print in the case of the list being empty
     */
    private void verifyIngredientsList(ArrayList<Ingredient> ingredients, JsonObject originalJson) {
        if(ingredients.isEmpty()) {
            throw new JsonSyntaxException("Recipe Ingredient list can't be empty! " + "\n" + prettyPrintJson(originalJson));
        }
    }

    /**
     * Attempts to extract the recipe result count from the given JsonObject.
     * Assumes we're inside the top level of a result block:
     *
     *  "result": {
     *    "item": "minecraft:cobblestone",
     *    "count": 2
     *  }
     *
     * If the count is invalid or is not an int, 0 is returned.
     * @param countJson JsonObject to extract recipe result count from
     * @return recipe result count
     */
    private int getCount(JsonObject countJson) {
        int count;
        // get count int
        if(countJson.has(COUNT_KEY)) {
            if (countJson.get(COUNT_KEY).isJsonPrimitive()) {
                final JsonPrimitive countPrimitive = countJson.getAsJsonPrimitive(COUNT_KEY);

                if (countPrimitive.isNumber()) {
                    count = countPrimitive.getAsNumber().intValue();
                } else {
                    throw new IllegalArgumentException("Expected JsonPrimitive to be an int, got " + countJson.getAsString() + "!\n" + prettyPrintJson(countJson));
                }
            } else {
                throw new JsonSyntaxException("\"" + ITEM_KEY + "\" needs to be a JsonPrimitive int, found " + countJson.getClass() + "!\n" + prettyPrintJson(countJson));
            }
        } else {
            return 1;
        }

        return count;
    }

    /**
     * Attempts to extract a {@link Item} from the given JsonObject.
     * Assumes we're inside the top level of a result block:
     *
     *  "result": {
     *    "item": "minecraft:cobblestone",
     *    "count": 2
     *  }
     *
     * If the Item does not exist in {@link Registry#ITEM}, an exception is thrown and {@link Items#AIR} is returned.
     *
     * @param itemJson JsonObject to extract Item from
     * @return Item extracted from Json
     */
    private Item getItem(JsonObject itemJson) {
        Item result;

        if(itemJson.get(ITEM_KEY).isJsonPrimitive()) {
            final JsonPrimitive itemPrimitive = itemJson.getAsJsonPrimitive(ITEM_KEY);

            if(itemPrimitive.isString()) {
                final Identifier itemIdentifier = new Identifier(itemPrimitive.getAsString());

                if(Registry.ITEM.getIds().contains(itemIdentifier)) {
                    result = Registry.ITEM.get(itemIdentifier);
                } else {
                    throw new IllegalArgumentException("Item registry does not contain " + itemIdentifier.toString() + "!" + "\n" + prettyPrintJson(itemJson));
                }
            } else {
                throw new IllegalArgumentException("Expected JsonPrimitive to be a String, got " + itemPrimitive.getAsString() + "\n" + prettyPrintJson(itemJson));
            }
        } else {
            throw new JsonSyntaxException("\"" + ITEM_KEY + "\" needs to be a String JsonPrimitive, found " + itemJson.getClass() + "!\n" + prettyPrintJson(itemJson));
        }

        return result;
    }

    private String getFluid(JsonObject fluidJson) {
        String fluid;

        if (fluidJson.get(FLUID_KEY)==null) {
            throw new JsonSyntaxException("\"" + FLUID_KEY + "\" needs to be a String JsonPrimitive, found... nothing" + "!\n" + prettyPrintJson(fluidJson));
        } else if (fluidJson.get(FLUID_KEY).isJsonPrimitive()) {
            final JsonPrimitive fluidPrimitive = fluidJson.getAsJsonPrimitive(FLUID_KEY);
            if (fluidPrimitive.isString()) {
                fluid = fluidPrimitive.getAsString();
            } else {
                throw new IllegalArgumentException("Expected "+"\"" + FLUID_KEY + "\" to be a String, got " + fluidPrimitive.getAsString() + "\n" + prettyPrintJson(fluidJson));
            }
        } else {
            throw new JsonSyntaxException("\"" + FLUID_KEY + "\" needs to be a String JsonPrimitive, found " + fluidJson.getClass() + "!\n" + prettyPrintJson(fluidJson));
        }
        return fluid;
    }

    /**
     * Retrieves a list of required {@link Ingredient}s from the given JsonObject.
     * If the JsonObject doesn't have a proper list, an exception is thrown.
     *
     * @param json JsonObject to take Ingredient list from
     * @return list of Ingredients required for the recipe
     */
    private ArrayList<Ingredient> getIngredientList(JsonObject json) {
        final ArrayList<Ingredient> ingredients = new ArrayList<>();
        if(json.get(INPUT_KEY).isJsonArray()) {
            final JsonArray inputArray = json.get(INPUT_KEY).getAsJsonArray();
            inputArray.forEach(jsonElement -> ingredients.add(Ingredient.fromJson(jsonElement)));
        } else {
            throw new JsonSyntaxException("Expected a JsonArray as \"input\", got " + json.get(INPUT_KEY).getClass() + "\n" + prettyPrintJson(json));
        }

        return ingredients;
    }

    private static String prettyPrintJson(JsonObject json) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    @Override
    public void write(PacketByteBuf buf, CauldronRecipe recipe) {
        buf.writeInt(recipe.getIngredients().size());
        recipe.getIngredients().forEach(ingredient -> ingredient.write(buf));
        buf.writeString(recipe.getFluid());
        recipe.getCatalyst().write(buf);
        buf.writeItemStack(recipe.getOutput());
    }

    @Override
    public CauldronRecipe read(Identifier id, PacketByteBuf buf) {
        final int size = buf.readInt();

        final ArrayList<Ingredient> ingredients = new ArrayList<>();
        for(int i = 0; i < size; i++) {
            ingredients.add(Ingredient.fromPacket(buf));
        }
        final String fluid = buf.readString();
        final Ingredient catalyst = Ingredient.fromPacket(buf);

        return new CauldronRecipe(ingredients, fluid, catalyst, buf.readItemStack(), id);
    }
}