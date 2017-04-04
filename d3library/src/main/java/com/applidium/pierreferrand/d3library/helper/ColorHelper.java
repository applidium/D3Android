package com.applidium.pierreferrand.d3library.helper;

public final class ColorHelper {

    private static final int WHITE = 0XFF_FF_FF_FF;
    private static final int BLACK = 0XFF_00_00_00;

    private static final float COEFFICIENT_BLUE = 0.114F;
    private static final float COEFFICIENT_GREEN = 0.587F;
    private static final float COEFFICIENT_RED = 0.299F;
    private static final float LIMIT_VALUE = 0.5F;

    private ColorHelper() {}

    /***
     * Return the most suitable color depending on the given background color
     * @param color color of the background
     * @return black or white
     */
    public static int colorDependingOnBackground(int color) {
        int blue = color % 0x100;
        blue = blue < 0 ? blue + 256 : blue;
        int green = (color >> 8) % 0x100;
        green = green < 0 ? green + 256 : green;
        int red = (color >> 16) % 0x100;
        red = red < 0 ? red + 256 : red;
        float score = (COEFFICIENT_BLUE * blue
            + COEFFICIENT_RED * red
            + COEFFICIENT_GREEN * green) / 255F;
        return score > LIMIT_VALUE ? BLACK : WHITE;
    }
}
