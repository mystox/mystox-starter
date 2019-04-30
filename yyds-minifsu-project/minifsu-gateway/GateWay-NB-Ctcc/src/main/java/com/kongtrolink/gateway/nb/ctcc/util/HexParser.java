package com.kongtrolink.gateway.nb.ctcc.util;

import io.netty.buffer.ByteBuf;

import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * simple introduction
 * <p/>
 * <p>detailed comment
 *
 * @author hWX333438 Create on 2016/7/13.
 * @see
 */

public class HexParser {

    public static String parseByte2HexStr(byte[] buf) {
        if (null == buf) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {

        try {
            hexStr = hexStr.replaceAll(" ", "");
            if (hexStr.length() < 1) {
                return null;
            }
            if (hexStr.length() % 2 != 0) {
                hexStr = "0" + hexStr;
            }
            byte[] result = new byte[hexStr.length() / 2];
            for (int i = 0; i < hexStr.length() / 2; i++) {
                int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
                int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
                result[i] = (byte) (high * 16 + low);
            }
            return result;
        } catch (Exception e) {
//            Simulator.getInstance().printErrorConsole(3001,"decode data failed: input binary data is invalid,data is " + hexStr);
            return null;
        }

    }

    /**
     * 为了性能，能不用就不用，该函数用于打日志时类型转换
     * <p>
     * 为了安全， 最好不要修改该函数 里面会对in 的readIndex操作，会破坏业务后续操作，因此函数内会将readIndex重置（此操作一定要！）
     *
     * @param in
     * @return
     */
    public static String parseByteBuf2HexStr(ByteBuf in) {

        byte[] ins = new byte[in.readableBytes()];

        in.readBytes(ins);

        // 此操作很重要
        in.resetReaderIndex();

        return parseByte2HexStr(ins);
    }

    public static byte[] parseHexStr2Bcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        if (len >= 2) {
            len = len / 2;
        }
        byte[] bbt = new byte[len];
        byte[] abt = asc.getBytes();
        int j;
        int k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if (abt[2 * p] >= '0' && abt[2 * p] <= '9') {
                j = abt[2 * p] - '0';
            } else if (abt[2 * p] >= 'a' && abt[2 * p] <= 'z') {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if (abt[2 * p + 1] >= '0' && abt[2 * p + 1] <= '9') {
                k = abt[2 * p + 1] - '0';
            } else if (abt[2 * p + 1] >= 'a' && abt[2 * p + 1] <= 'z') {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
            // System.out.format("%02X\n", bbt[p]);
        }
        return bbt;
    }

    public static String parseBcd2HexStr(byte[] bytes) {
        char[] temp = new char[bytes.length * 2];
        char val;

        for (int i = 0; i < bytes.length; i++) {
            val = (char) (((bytes[i] & 0xf0) >> 4) & 0x0f);
            temp[i * 2] = (char) (val > 9 ? val + 'A' - 10 : val + '0');

            val = (char) (bytes[i] & 0x0f);
            temp[i * 2 + 1] = (char) (val > 9 ? val + 'A' - 10 : val + '0');
        }
        return new String(temp);
    }

    public static String parseStringToHexStr(String plainText)
    {
        return String.format("%x", new BigInteger(1, plainText.getBytes(Charset.forName("UTF-8"))));
    }
}
