package com.example.nydp.utils;

import com.example.nydp.netty.server.HexStringUtils;

import java.io.UnsupportedEncodingException;

public class HexToAcscii {

    public static String stringToAscii(String value)
    {
        StringBuffer sbu = new StringBuffer();
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if(i != chars.length - 1)
            {
                sbu.append((int)chars[i]).append(",");
            }
            else {
                sbu.append((int)chars[i]);
            }
        }
        String[] strs = sbu.toString().split(",");
        String rs = "";
        for (int i = 0;i<strs.length;i++){
            rs = rs+Integer.toHexString(Integer.parseInt(strs[i]));
        }

        return rs.toUpperCase();
    }

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }


    public static void main(String[] args){
        try {
            String str = stringToAscii("30313033303030313030303144354341");
            System.out.println(str+"+++-+-+");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


