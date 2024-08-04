package dev.gether.getsklep.utils;

import org.bukkit.ChatColor;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorFixer {

    private static final Pattern GRADIENT_PATTERN = Pattern.compile("<#([A-Fa-f0-9]{6})>(.*?)</#([A-Fa-f0-9]{6})>");
    private static final Pattern BOLD_PATTERN = Pattern.compile("<b>(.*?)</b>");

    public static List<String> addColors(List<String> input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        for (int i = 0; i < input.size(); i++) {
            input.set(i, addColors(input.get(i)));
        }
        return input;
    }
    public static String addColors(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        input = ChatColor.translateAlternateColorCodes('&', input);

        Matcher gradientMatcher = GRADIENT_PATTERN.matcher(input);
        StringBuffer gradientResult = new StringBuffer();

        while (gradientMatcher.find()) {
            String startColorHex = gradientMatcher.group(1);
            String text = gradientMatcher.group(2);
            String endColorHex = gradientMatcher.group(3);

            Color startColor = Color.decode("#" + startColorHex);
            Color endColor = Color.decode("#" + endColorHex);
            String gradientText = applyGradient(text, startColor, endColor);

            gradientMatcher.appendReplacement(gradientResult, gradientText);
        }
        gradientMatcher.appendTail(gradientResult);

        Matcher boldMatcher = BOLD_PATTERN.matcher(gradientResult.toString());
        StringBuffer finalResult = new StringBuffer();

        while (boldMatcher.find()) {
            String boldText = ChatColor.BOLD + boldMatcher.group(1) + ChatColor.RESET;
            boldMatcher.appendReplacement(finalResult, boldText);
        }
        boldMatcher.appendTail(finalResult);

        return finalResult.toString();
    }

    public static String applyGradient(String input, Color startColor, Color endColor) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        int length = input.length();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < length; i++) {
            double ratio = (double) i / (length - 1);
            int red = (int) (startColor.getRed() * (1 - ratio) + endColor.getRed() * ratio);
            int green = (int) (startColor.getGreen() * (1 - ratio) + endColor.getGreen() * ratio);
            int blue = (int) (startColor.getBlue() * (1 - ratio) + endColor.getBlue() * ratio);

            String hexColor = String.format("#%02x%02x%02x", red, green, blue);
            result.append(ChatColor.translateAlternateColorCodes('&', "&x"
                            + "&" + hexColor.charAt(1) + "&" + hexColor.charAt(2)
                            + "&" + hexColor.charAt(3) + "&" + hexColor.charAt(4)
                            + "&" + hexColor.charAt(5) + "&" + hexColor.charAt(6)))
                    .append(input.charAt(i));
        }

        return result.toString();
    }


}
