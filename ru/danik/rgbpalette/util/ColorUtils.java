package ru.danik.rgbpalette.util;

import java.awt.*;

public class ColorUtils {

    public static float[] rgbToHSV(int r, int g, int b) {
        return Color.RGBtoHSB(r, g, b, null);
    }

    public static float distance(float[] a, float[] b) {
        float dh = Math.min(Math.abs(a[0] - b[0]), 1 - Math.abs(a[0] - b[0]));
        float ds = Math.abs(a[1] - b[1]);
        float dv = Math.abs(a[2] - b[2]);
        return dh * dh + ds * ds + dv * dv;
    }
}