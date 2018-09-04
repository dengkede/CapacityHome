package com.tutk.IOTC;

/**
 * TUTK Through Co., Ltd.
 * Created by howard_chu on 2017/1/10.
 */
public class int16_t {
    /**
     * Short to byte array
     * @param value
     * @return 2 byte array
     */
    public static byte[] toByteArray (short value) {
        return new byte[] {(byte) value, (byte) (value >>> 8)};
    }

    /**
     * Byte array to Short
     * @param value 2 byte array
     * @return
     */
    public static short toShort (byte[] value) {
        return toShort(value,0);
    }

    /**
     * Byte array to Short
     * @param value 2 byte array
     * @param beginPos
     * @return
     */
    public static short toShort (byte[] value, int beginPos) {
        return (short) ((0xff & value[beginPos]) | (0xff & value[beginPos + 1]) << 8);
    }
}
