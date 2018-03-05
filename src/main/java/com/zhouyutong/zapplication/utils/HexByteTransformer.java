package com.zhouyutong.zapplication.utils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <b>十六进制与二进制字节转换器</b><br>
 *
 * @author homelink
 * @thread-safe
 * @since 2015-10-31
 */
public final class HexByteTransformer {

    private HexByteTransformer() {
    }

    /**
     * 十六进制编码字符串转换为二进制字节数组
     *
     * @param hex
     * @return
     * @throws NullPointerException
     */
    public static byte[] hex2Bytes(String hex) {
        checkNotNull(hex);

        byte[] arrB = hex.getBytes();
        int iLen = arrB.length;

        byte[] bytes = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            bytes[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return bytes;
    }

    /**
     * 二进制字节数组转换为十六进制编码字符串
     *
     * @param bytes
     * @return
     * @throws NullPointerException
     */
    public static String bytes2Hex(byte[] bytes) {
        checkNotNull(bytes);

        StringBuilder sb = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            sb.append(hexDigits[(b >> 4) & 0xf]).append(hexDigits[b & 0xf]);
        }
        return sb.toString();
    }

    private static final char[] hexDigits = "0123456789abcdef".toCharArray();
}
