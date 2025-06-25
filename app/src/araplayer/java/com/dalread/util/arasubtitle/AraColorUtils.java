package com.dalread.util.arasubtitle;

import android.graphics.Color;

public class AraColorUtils {

    /**
     * Convert the hexadecimal color code to BGR code
     *
     * @param hex the hexadecimal color code
     * @return the BGR code
     */
    public static int hexToBGR(String hex) {
        int color = Color.parseColor(hex);
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = (color) & 0xFF;
        return (blue << 16) | (green << 8) | red;
    }

    /**
     * Convert a &HAABBGGRR to hexadecimal
     *
     * @param haabbggrr the color code
     * @throws InvalidColorCode if the pattern is invalid
     * @return the hexadecimal code
     */
    public static String HAABBGGRRToHex(String haabbggrr) {
        if (haabbggrr.length() != 10) {
            throw new InvalidColorCode("Invalid pattern, must be &HAABBGGRR");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(haabbggrr.substring(8, 10));
        sb.append(haabbggrr.substring(6, 8));
        sb.append(haabbggrr.substring(4, 6));
        return sb.toString().toLowerCase();
    }

    /**
     * Convert a &HBBGGRR to hexadecimal
     *
     * @param hbbggrr the color code
     * @throws InvalidColorCode if the pattern is invalid
     * @return the hexadecimal code
     */
    public static String HBBGGRRToHex(String hbbggrr) {
        if (hbbggrr.length() != 8) {
            throw new InvalidColorCode("Invalid pattern, must be &HBBGGRR");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(hbbggrr.substring(6, 8));
        sb.append(hbbggrr.substring(4, 6));
        sb.append(hbbggrr.substring(2, 4));
        return sb.toString().toLowerCase();
    }

    /**
     * Convert a &HAABBGGRR to BGR
     *
     * @param haabbggrr the color code
     * @throws InvalidColorCode if the pattern is invalid
     * @return the BGR code
     */
    public static int HAABBGGRRToBGR(String haabbggrr) {
        return hexToBGR(HAABBGGRRToHex(haabbggrr));
    }

    /**
     * Convert a &HBBGGRR to BGR
     *
     * @param hbbggrr the color code
     * @throws InvalidColorCode if the pattern is invalid
     * @return the BGR code
     */
    public static int HBBGGRRToBGR(String hbbggrr) {
        return hexToBGR(HBBGGRRToHex(hbbggrr));
    }

    // Custom exception for invalid color codes
    public static class InvalidColorCode extends RuntimeException {
        public InvalidColorCode(String message) {
            super(message);
        }
    }
}

