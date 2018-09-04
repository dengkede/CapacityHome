package com.tutk.IOTC;

/**
 * TUTK Through Co., Ltd.
 * Created by howard_chu on 2017/1/10.
 */
public class int32_t {

    /**
     * Integer to byte array
     * @param value
     * @return 4 byte array
     */
    public static byte[] toByteArray (int value) {
        return new byte[] {(byte) value, (byte) (value >>> 8), (byte) (value >>> 16), (byte) (value >>> 24)};
    }

    /**
     * Byte array to integer
     * @param value 4 byte array
     * @return
     */
    public static int toInteger (byte[] value) {
        return toInteger(value,0);
    }

    /**
     * Byte array yo integer
     * @param value 4 byte array
     * @param beginPos
     * @return
     */
    public static int toInteger (byte[] value, int beginPos) {
        return (int) (0xff & value[beginPos]) |
                (0xff & value[beginPos + 1]) << 8 |
                (0xff & value[beginPos + 2]) << 16 |
                (0xff & value[beginPos + 3]) << 24;
    }
}
