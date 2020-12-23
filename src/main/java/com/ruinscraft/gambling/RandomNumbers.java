package com.ruinscraft.gambling;

import java.security.SecureRandom;

public final class RandomNumbers {

    private static final SecureRandom SRAND = new SecureRandom();

    public static int getRandomIntInRange(int min, int max) {
        return SRAND.nextInt(max - min + 1) + min;
    }

    public static boolean getRandomBoolean() {
        return SRAND.nextBoolean();
    }

}
