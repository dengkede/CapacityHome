package com.tutk.IOTC;


import java.nio.ByteBuffer;

/**
 * @auther James Huang
 * @date 2017/5/15
 */

public class Float {
    /**
     * float to byte array
     *
     * @param value
     * @return 4 byte array
     */
    public static byte[] toByteArray (float value) {
        int bits = java.lang.Float.floatToIntBits(value);
        return new byte[] {(byte) (bits & 0xff), (byte) ((bits >> 8) & 0xff), (byte) ((bits >> 16) & 0xff), (byte) ((bits >> 24) & 0xff)};
    }

    /**
     * Byte array to float
     *
     * @param value 4 byte array
     * @return
     */
    public static float toFloat (byte[] value) {
        return toFloat(value, 0);
    }

    /**
     * Byte array to float
     *
     * @param value    4 byte array
     * @param beginPos
     * @return
     */
    public static float toFloat (byte[] value, int beginPos) {
        return ByteBuffer.wrap(value, beginPos, 4).getFloat();
    }
}
