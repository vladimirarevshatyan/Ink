package kashmirr.social.utils;

/**
 * Created by PC-Comp on 1/9/2017.
 */

public class Random {
    private static final java.util.Random RANDOM = new java.util.Random();

    public float getRandom(float lower, float upper) {
        float min = Math.min(lower, upper);
        float max = Math.max(lower, upper);
        return getRandom(max - min) + min;
    }

    public float getRandom(float upper) {
        return RANDOM.nextFloat() * upper;
    }

    public int getRandom(int upper) {
        return RANDOM.nextInt(upper);
    }

    public static int getRandomNumberInRange(int min, int max) {
        java.util.Random r = new java.util.Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
