package me.softik.nerochat.utils;

import com.ibm.icu.text.Transliterator;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import org.jetbrains.annotations.NotNull;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

public class NeroStringUtil {
    private static final Pattern DIATRICAL_MARKS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    /**
     * Reverts L33t
     *
     * @param input The string to be reverted.
     * @return The input string with l33t turned into regular text characters
     */
    public static String revertLeet(String input) {
        input = input.toLowerCase(Locale.ROOT);

        input = input.replace("0", "o");
        input = input.replace("1", "i");
        input = input.replace("2", "z");
        input = input.replace("3", "e");
        input = input.replace("4", "a");
        input = input.replace("5", "s");
        input = input.replace("6", "g");
        input = input.replace("7", "t");
        input = input.replace("8", "b");
        input = input.replace("9", "g");
        input = input.replace("&", "a");
        input = input.replace("@", "a");
        input = input.replace("(", "c");
        input = input.replace("#", "h");
        input = input.replace("!", "i");
        input = input.replace("]", "i");
        input = input.replace("|", "i");
        input = input.replace("}", "i");
        input = input.replace("?", "o");
        input = input.replace("$", "s");

        return input;
    }

    /**
     * Converts accented letters to non-accented counterparts
     *
     * @param input The string to be stripped.
     * @return The input string with accented characters turned into non-accented
     */
    public static String stripAccents(final String input) {
        StringBuilder decomposed = new StringBuilder(Normalizer.normalize(input, Normalizer.Form.NFD));
        for (int i = 0; i < decomposed.length(); i++) {
            switch (decomposed.charAt(i)) {
                case 'Ł':
                    decomposed.setCharAt(i, 'L');
                    break;
                case 'ł':
                    decomposed.setCharAt(i, 'l');
                    break;
                case 'Ø':
                    decomposed.setCharAt(i, 'O');
                    break;
                case 'ø':
                    decomposed.setCharAt(i, 'o');
                    break;
            }
        }

        // Strip crazy stuff like zero-width spaces
        return DIATRICAL_MARKS_PATTERN.matcher(decomposed).replaceAll("");
    }

    /**
     * Converts unicode to alphanumeric counterparts
     *
     * @param input The string to be converted.
     * @return The input string with any unicode characters converted to alphanumeric
     */
    public static String translateUnicode(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{Alnum}]", "");
    }

    public static String translateLatin(String input) {
        return Transliterator.getInstance("Any-Latin; Latin-ASCII; Lower").transliterate(input);
    }

    /**
     * Strips all spaces from a string
     *
     * @param input The string to be stripped.
     * @return The input string with their spaces stripped.
     */
    public static String stripSpaces(String input) {
        return input.replaceAll(" ", "");
    }

    /**
     * Get the similarity in percent between two strings.
     *
     * @param string1 The first string.
     * @param string2 The second string.
     * @return An integer between 0 and 100.
     */
    public static int stringSimilarityInPercent(@NotNull String string1, @NotNull String string2) {
        string1 = revertLeet(string1);
        string2 = revertLeet(string2);

        string1 = stripAccents(string1);
        string2 = stripAccents(string2);

        string1 = translateUnicode(string1);
        string2 = translateUnicode(string2);

        string1 = stripSpaces(string1);
        string2 = stripSpaces(string2);

        return FuzzySearch.weightedRatio(string1, string2);
    }
}
