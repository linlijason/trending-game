package com.bistro.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

public class BetidHashUtils {
    private static String hashSalt = "&8aB大）c_";
    private static int replaceIndex[] = {5, 20, 7, 9, 19, 12, 13, 15, 11, 17, 10, 21};
    private static char replaceZero[] = {'a', 'b', 'd', 'e', 'f', 'd', 'c', 'b', 'c', 'd', 'a', 'f'};

    /**
     * @param id
     * @return
     */
    public static String hashId(Long id) {
        String md5Digest = DigestUtils.md5DigestAsHex((hashSalt + id).getBytes());
        char[] chars = md5Digest.toCharArray();

        int shit = chars[3] % 10;
        char[] digits = StringUtils.leftPad(id.toString(), 12, "0").toCharArray();
        int digitIndex = 0;
        for (Integer index : replaceIndex) {
            char before = chars[index + shit];
            if (digits[digitIndex] == '0') {//md5被替换位置是数字,但原始id替换位置为0
                if (Character.isDigit(before)) {
                    chars[index + shit] = replaceZero[digitIndex];
                }
            } else {
                chars[index + shit] = digits[digitIndex];
            }
            digitIndex++;

        }
        return String.valueOf(chars);
    }

    public static Long betId(String hashId) {

        if (hashId == null || hashId.length() < 32) {
            return null;
        }
        char[] chars = hashId.toCharArray();

        int shit = chars[3] % 10;
        char[] digits = new char[12];
        int digitIndex = 0;
        for (Integer index : replaceIndex) {
            char before = chars[index + shit];
            if (Character.isAlphabetic(before)) {
                digits[digitIndex] = '0';
            } else {
                digits[digitIndex] = before;
            }
            digitIndex++;

        }

        return Long.valueOf(String.valueOf(digits));
    }

    public static void main(String[] args) {

        char[] aaa = "11ddaa".toCharArray();
        System.out.println((int) aaa[0]);
        System.out.println(hashId(1L));
        System.out.println(hashId(2L));
        System.out.println(hashId(3L));
        System.out.println(hashId(1112119700L));

        System.out.println(betId(hashId(1L)));
        System.out.println(betId(hashId(2L)));
        System.out.println(betId(hashId(3L)));
        System.out.println(betId(hashId(1112119700L)));

    }
}
