package com.recipia.recipe.common.utils;

public class RecipeStringUtils {

    public static String extractLastPart(String input) {
        int lastIndex = input.lastIndexOf(":");
        if (lastIndex != -1) {
            return input.substring(lastIndex + 1);
        } else {
            return "";
        }
    }

}
