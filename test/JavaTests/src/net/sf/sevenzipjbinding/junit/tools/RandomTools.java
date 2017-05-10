package net.sf.sevenzipjbinding.junit.tools;

import java.util.Random;

public final class RandomTools {
    private static final long INITIAL_SEED = 7329427545945041180L;

    private static final char[] SYMBOLS = new char[] { ' ', '_', '-', '+', '=' };
    private static final char[] SYMBOLS_FIRST_CHAR = new char[] { '_', '-', '+', '=' };

    private static final ThreadLocal<Random> RANDOM_TL = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random(INITIAL_SEED);
        };
    };

    public static Random getRandom() {
        return RANDOM_TL.get();
    }

    public static void initForThread() {
        getRandom().setSeed(INITIAL_SEED);
    }

    public static String getRandomFilename() {
        Random random = getRandom();
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
            filenameArray[j] = getRandomFilenameChar(j == 0);
        }
        return new String(filenameArray);
    }

    public static String getRandomName() {
        Random random = getRandom();
        int length = 3 + random.nextInt(9);
        char[] filenameArray = new char[length];
        for (int j = 0; j < length; j++) {
            filenameArray[j] = getRandomLetter();
        }
        return new String(filenameArray);
    }

    public static char getRandomFilenameChar(boolean firstChar) {
        Random random = getRandom();
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
            return getRandomLetter();
        }
    }

    public static char getRandomLetter() {
        Random random = getRandom();
        if (random.nextBoolean()) {
            // Upper case letters
            return (char) ('A' + random.nextInt(26));
        }
        // Lower case letters
        return (char) ('a' + random.nextInt(26));
    }
}
