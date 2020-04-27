package com.lgh.test;

public class ImageInitParameter {
    private static int[] paletteIndex={0,1,2,3,4,5,6,7,8,9,10};

    private static float[] scale={1,2,3};

    private static int[] rotateState={0,1,2,3};

    public static int getPaletteIndex() {
        return paletteIndex[4];
    }

    public static float getScale() {
        return scale[2];
    }

    public static int getRotateState() {
        return rotateState[0];
    }
}
