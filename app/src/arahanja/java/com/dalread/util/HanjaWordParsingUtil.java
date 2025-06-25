package com.dalread.util;

import com.dalread.model.DIC_HANJA;

public class HanjaWordParsingUtil {
    //한자 단어의 모든 뜻과 발음이 MEANING_KO에 없을수도 있고, 업데이트가 덜 되었을수도 있어서 따로 계산해서 가져온다.
    //근데 MEANING_KO에는 왜 한자 단어의 모든 뜻과 발음을 두었지? 최신성이 유지 안될수도 있는데?
    public static String getMeaningKO(DIC_HANJA dicHanja) {
        String result = "";
        if (!dicHanja.getMEANING1().equals("") && !dicHanja.getPRONOUNCE1_FIRST().equals("")) {
            result = dicHanja.getMEANING1() + " " + dicHanja.getPRONOUNCE1_FIRST();
            if (!dicHanja.getMEANING2().equals("") && !dicHanja.getPRONOUNCE2_FIRST().equals("")) {
                result += ", " + dicHanja.getMEANING2() + " " + dicHanja.getPRONOUNCE2_FIRST();
                if (!dicHanja.getMEANING3().equals("") && !dicHanja.getPRONOUNCE3_FIRST().equals("")) {
                    result += ", " + dicHanja.getMEANING3() + " " + dicHanja.getPRONOUNCE3_FIRST();
                }
            }
        }
        return StringUtils.trimAndNormalizeString(result);
    }
}
