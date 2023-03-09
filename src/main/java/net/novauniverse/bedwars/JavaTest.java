package net.novauniverse.bedwars;

public class JavaTest {

    public static void main(String[] args) {
        // WORKS (thanks stack overflow for working for the first time ever) (nvm its 2015 stack overflow makes sense)

        int min1 = 0;
        int max1 = 90;
        int min2 = -180;
        int max2 = 0;

        System.out.println(distanceOfDirectedAngles(-1, 180));

        for (int i = 0; i < 100; i ++) {
            float from = (float) (Math.random()*(max1 - min1)) + min1;
            float to = (float) (Math.random()*(max2 - min2)) + min2;
            float distance = distanceOfDirectedAngles(from, to);
            System.out.println("------------------");
            System.out.println("DIFFERENCE OF \"" + from + "\" AND \"" + to + "\": " + distance);
            System.out.println("STARTING PLUS DIFFERENCE: " + fixDirectedAngle(from + distance));
        }
        System.out.println("------------------");
    }

    public static float fixDirectedAngle(float value) {
        return value > 180 ? -360 + value : value < -180 ? 360 + value : value;
    }

    private static float distanceOfDirectedAngles(float from, float to) {
        float from360 = from + 180;
        float to360 = to + 180;
        float opt1 = to360 - from360;
        float opt2 = (360 - from360) + to360;
        return getSmallest(opt1, opt2);
    }

    private static boolean isPositive(Number number) {
        return number.doubleValue() >= 0;
    }

    private static boolean isNegative(Number number) {
        return !isPositive(number);
    }

    public static float getSmallest(float... floats) {
        if (floats.length == 0) {
            return Float.MAX_VALUE;
        }
        float current = Float.MAX_VALUE;
        float currentAbs = Float.MAX_VALUE;
        for (float f : floats) {
            if (Math.abs(f) <= currentAbs) {
                current = f;
                currentAbs = Math.abs(f);
            }
        }
        return current;
    }

    private static float asRealNumber(float num) {
        if (Float.isInfinite(num) || Float.isNaN(num)) {
            return 0;
        } else {
            return num;
        }
    }
    public static float distance(float angle1,float angle2) {
        float diff = (angle2 - angle1 + 180) % 360 - 180;
        return diff < -180 ? diff + 360 : diff;
    }

    public static float calc(float num) {
        float turn = (int) Math.abs(Math.ceil(num/3));
        return num / turn;
    }
}
