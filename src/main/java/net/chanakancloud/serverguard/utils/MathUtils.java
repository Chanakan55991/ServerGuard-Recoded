package net.chanakancloud.serverguard.utils;

public class MathUtils {
    public static double gcd(double a, double b)
    {
        if (a < b)
            return gcd(b, a);

        // base case
        if (Math.abs(b) < 0.001)
            return a;

        else
            return (gcd(b, a -
                    Math.floor(a / b) * b));
    }
}
