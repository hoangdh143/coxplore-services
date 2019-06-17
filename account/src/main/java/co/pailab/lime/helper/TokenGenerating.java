package co.pailab.lime.helper;

import java.util.Random;

public class TokenGenerating {
    public static String getRandomNumberString() {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        Random rnd = new Random();
        int number = rnd.nextInt(1000000);

        // this will convert any number sequence into 6 character.
        return String.format("%06d", number);
    }
}
