package com.dalread.util;

import android.text.Html;
import android.text.Spanned;

/**
 * HTML 엔티티를 디코딩하는 유틸리티 클래스
 * &amp;, &lt;, &gt;, &quot;, &#39; 등을 올바른 문자로 변환
 */
public class HtmlEntityDecoder {
    
    /**
     * HTML 엔티티를 디코딩하여 일반 텍스트로 변환
     * @param htmlText HTML 엔티티가 포함된 텍스트
     * @return 디코딩된 텍스트
     */
    public static String decodeHtmlEntities(String htmlText) {
        if (htmlText == null || htmlText.isEmpty()) {
            return htmlText;
        }
        
        // 수동 치환 방식으로 공백 보존
        return decodeCommonEntities(htmlText);
    }
    
    /**
     * 일반적인 HTML 엔티티를 수동으로 치환 (공백 보존)
     * @param text HTML 엔티티가 포함된 텍스트
     * @return 치환된 텍스트
     */
    public static String decodeCommonEntities(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        
        // 공백을 보존하면서 HTML 엔티티 치환
        return text
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace("&copy;", "©")
            .replace("&reg;", "®")
            .replace("&trade;", "™");
    }
}
