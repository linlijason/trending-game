package com.bistro.utils;

import com.bistro.common.utils.sign.Md5Utils;

public class SidUtils {
    private static final String PASSWORD = "aCdb6aaaYZAAZd13";

    public static String encode(String id) {
        String ts = String.valueOf(System.currentTimeMillis());
        String raw = ts + id + PASSWORD;
        String encode = new String(Base62.createInstance().encode(ts.getBytes())) + "-" + Md5Utils.hash(raw);
        return "_" + encode;
    }

    public static boolean check(String id, String encoded) {
        if (encoded.startsWith("_")) {//走新的base62
            String[] encodeArray = encoded.split("-");
            String ts = new String(Base62.createInstance().decode(encodeArray[0].substring(1).getBytes()));
            String raw = ts + id + PASSWORD;
            String md5 = Md5Utils.hash(raw);
            if (encodeArray[1].equals(md5)) {
                return true;
            } else {
                return false;
            }
        } else {
            return Pbkdf2PasswordUtils.matchesWithSalt(id, encoded);
        }
    }

    public static void main(String[] args) {
        String s = encode("6608ab34c8fd0d8e5dbdddc14cbf5937");
        System.out.println(s);
        System.out.println(check("6608ab34c8fd0d8e5dbdddc14cbf5937", s));
//        System.out.println(check("6608ab34c8fd0d8e5dbdddc14cbf5937", "10b753dd38a0eb6185f4424febc0deeca6cd9e9f0a49cfeb0a055d8355eea17d7fb2aa3660caf97a"));
    }
}
