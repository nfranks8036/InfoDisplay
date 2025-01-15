package net.cybercake.display.utils;

public class Center {

    // thank you ChatGPT <3
    public static String text(String original, int size) {
        int textLength = original.length();
        if (textLength >= size) {
            // Text is longer than or equal to total length, return as is
            return original;
        }

        int padding = (size - textLength) / 2;
        StringBuilder centeredText = new StringBuilder();

        centeredText.append(" ".repeat(padding));
        centeredText.append(original);
        centeredText.append(" ".repeat(padding));

        // Add an extra space if the total length is odd
        if (textLength % 2 != 0 && size % 2 != 0)
            centeredText.append(" ");

        return centeredText.toString();
    }

}