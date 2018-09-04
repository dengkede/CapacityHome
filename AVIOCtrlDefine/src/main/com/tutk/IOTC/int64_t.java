package com.tutk.IOTC;

/**
 * @auther James Huang
 * @date 202017/1/16
 */

public class int64_t {

    /**
     * Long to byte array
     * @param value
     * @return 8 byte array.
     */
    public static byte[] toByteArray (long value) {
        return new byte[] {(byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24), (byte) (value >>> 32), (byte) (value
                >>> 40), (byte) (value >>> 48), (byte) (value >>> 56)};
    }

    /**
     * Byte array to long
     * @param value 8 byte array
     * @return
     */
    public static long toLong(byte[] value)
    {
        return toLong(value,0);
    }
    /**
     * byte array yo long
     * @param value 8 byte array
     * @param beginPos
     * @return
     */
    public static long toLong (byte[] value, int beginPos) {
        return toLong(value,beginPos , 8);
    }

    public static long toLong (byte[] value, int beginPos , int size) {
        long l = 0;
        for (int i = 0 ; i < size ; i++) {
            l = l | ((0xffL & value[beginPos + i]) << (8 * i));
        }

        return l;
    }
}
