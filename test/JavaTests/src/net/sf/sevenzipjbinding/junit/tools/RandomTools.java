package net.sf.sevenzipjbinding.junit.tools;

import java.util.Random;

public final class RandomTools {
    private static final char[] SYMBOLS = new char[] { ' ', '_', '-', '+', '=' };
    private static final char[] SYMBOLS_FIRST_CHAR = new char[] { '_', '-', '+', '=' };

    public static String getRandomFilename(Random random) {
        int length;
        switch (random.nextInt(3)) {
        case 0:
            length = 1;
            break;
        case 1:
            // Length: 2-10
            length = 2 + random.nextInt(9);
            break;
        default:
            // Length: 20-30
            length = 20 + random.nextInt(11);
        }
        char[] filenameArray = new char[length];
        for (int j = 0; j < length; j++) {
            filenameArray[j] = getRandomFilenameChar(random, j == 0);
        }
        return new String(filenameArray);
    }

    public static String getRandomName(Random random) {
        int length = 3 + random.nextInt(9);
        char[] filenameArray = new char[length];
        for (int j = 0; j < length; j++) {
            filenameArray[j] = getRandomLetter(random);
        }
        return new String(filenameArray);
    }

    public static char getRandomFilenameChar(Random random, boolean firstChar) {
        switch (random.nextInt()) {
        case 0:
            // Symbols
            if (firstChar) {
                return SYMBOLS_FIRST_CHAR[random.nextInt(SYMBOLS_FIRST_CHAR.length)];
            }
            return SYMBOLS[random.nextInt(SYMBOLS.length)];
        case 1:
            // Digits
            return (char) ('0' + random.nextInt(10));

        default:
            return getRandomLetter(random);
        }
    }

    public static char getRandomLetter(Random random) {
        if (random.nextBoolean()) {
            // Upper case letters
            return (char) ('A' + random.nextInt(26));
        }
        // Lower case letters
        return (char) ('a' + random.nextInt(26));
    }
}
