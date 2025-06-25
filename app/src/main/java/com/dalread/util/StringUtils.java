package com.dalread.util;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Patterns;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.base.EnumLanguage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class StringUtils {
    private static final String regExKorean = "[\\uAC00-\\uD7AF0-9]";
    private static final String regExChinese = "[\\u4E00-\\u9FFF0-9]";
    private static final String regExJapanese = "[\\p{IsHiragana}\\p{IsKatakana}\\p{IsCJKUnifiedIdeographs}\\p{IsHalfwidthandFullwidthForms}0-9]";
    private static final String regExEnglish = "[a-zA-Z0-9]";

    private static final String[] BOM_CHARS = {
            "\uFEFF"
    };

    public static String removeBOM(String text) {
        for (int i=0; i<BOM_CHARS.length; i++) {
            String bomChar = BOM_CHARS[i];
            if (text.startsWith(bomChar)) {
                text = text.substring(1);

                // There is only bom char at the beginning of text
                break;
            }
        }

        return text;
    }

    public static String replaceHTML(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        value = value.replaceAll("&nbsp;", Constant.BASE_BLANK);
        value = value.replaceAll(Constant.BREAK_CHARACTER, Constant.BASE_BLANK);
        value = value.replaceAll(Constant.RUBY.KEY.BREAK_BR_END, Constant.RUBY.KEY.BREAK_BR_START);
        value = value.replaceAll(Constant.RUBY.KEY.BREAK_BR_END_2, Constant.RUBY.KEY.BREAK_BR_START);
        return value;
    }

    public static String replaceNewLineToBRTag(String value) {
        value = value.replaceAll("\n", Constant.RUBY.KEY.BREAK_BR_END);
        value = value.replaceAll("\r\n", Constant.RUBY.KEY.BREAK_BR_END);
        value = value.replaceAll("\r", Constant.RUBY.KEY.BREAK_BR_END);
        return value;
    }

    public static String replaceHTMLRuby(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        value = value.replaceAll("&nbsp;", Constant.BASE_BLANK);
        return value;
    }

    public static InputStreamReader createInputStreamReader(String path, String charset) throws FileNotFoundException, UnsupportedEncodingException {
        if (Utils.isEmpty(charset)) {
            return new InputStreamReader(new FileInputStream(path));
        }
        return new InputStreamReader(new FileInputStream(path), charset);
    }

    public static InputStreamReader createInputStreamReader(InputStream is, String charset) throws UnsupportedEncodingException {
        if (Utils.isEmpty(charset)) {
            return new InputStreamReader(is);
        }
        return new InputStreamReader(is, charset);
    }

    public static String addString(String data, String value) {
        return addString(true, data, value);
    }

    public static String addString(boolean isChecked, String data, int value) {
        return addString(isChecked, data, String.valueOf(value));
    }

    public static String addString(boolean isChecked, String data, String value) {
        if (isChecked && !Utils.isEmpty(value)) {
            if (!Utils.isEmpty(data)) {
                data += ", ";
            }
            data += value;
        }
        return data;
    }

    public static String insertText(String baseText, String insertText) {
        return baseText.replace("(())", insertText);
    }

    public static String removeSpecial(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        return value.replaceAll(Constant.PLAYER.SPECIAL_CHARACTERS, " ")
                .replaceAll("\\[.*?\\]", "")
                .replaceAll("\\s+"," ")
                .trim();
    }
    public static String trimIncludeWhitespace(String value) {
        return value.replaceAll("^\\s+|\\s+$", "");
    }

//    public static List<Integer> getSeasonEpisodeNumber(String value) {
//        List<Integer> listResult = new ArrayList<>();
//        listResult.add(-1);
//        listResult.add(-1);
//        if (Utils.isEmpty(value))
//            return listResult;
//
//        Matcher matcherResolution = Pattern.compile("(.*)(\\d{3,4})x(\\d{3,4})(.*)", Pattern.CASE_INSENSITIVE).matcher(value); //ex) ~~3840x2160~~
//        if (matcherResolution.find())
//            return listResult;
//
//        String strSeasonNumber = "";
//        String strEpisodeNumber = "";
//        Matcher matcher = Pattern.compile("(.*)(\\d{1,2})e(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(value); //ex) ~~S01E01~~
//        if (matcher.find()) {
//            strSeasonNumber = matcher.group(2);
//            strEpisodeNumber = matcher.group(3);
//        } else {
//            matcher = Pattern.compile("(.*)(\\d{1,2})x(\\d{1,2})(.*)", Pattern.CASE_INSENSITIVE).matcher(value); //ex) ~~1x01~~~
//            if (matcher.find()) {
//                strSeasonNumber = matcher.group(2);
//                strEpisodeNumber = matcher.group(3);
//            }
//        }
//
//        DLog.d("", strSeasonNumber + "," + strEpisodeNumber);
//        Integer seasonNumber = -1;
//        Integer episodeNumber = -1;
//        try {
//            seasonNumber = Integer.valueOf(strSeasonNumber);
//            episodeNumber = Integer.valueOf(strEpisodeNumber);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        listResult.set(0, seasonNumber);
//        listResult.set(1, episodeNumber);
//        return listResult;
//    }

    public static String createURL(String url, String replace) {
        return url.replace(" ", replace).toLowerCase();
    }
    //@deprecated Replaced by {@link CopyTextUtil#getTextFromClipboard(Context context)}
    public static String getTextFromClipboard(Context context) {
        String str = "";
        ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        if ((cm != null) && (cm.hasPrimaryClip() == true)) {
            ClipData clip = cm.getPrimaryClip();
            ClipData.Item item = clip.getItemAt(0);
            if ((item != null) && (item.getText() != null)) {
                str = item.getText().toString();
            }
        }
        return str;
    }

    //This doesn't work well. for 𢦏
//    public static boolean isOnlyChinese1(String str) {
//        boolean result = false;
//        for (int i = 0; i < str.length(); i++) {
//            int codepoint = str.codePointAt(i);
//            if (Character.UnicodeScript.of(codepoint) != Character.UnicodeScript.HAN) {
//                result = false;
//                break;
//            }
//        }
//        return result;
//
////        //https://blog.naver.com/PostView.nhn?blogId=bb_&logNo=220861238329&parentCategoryNo=&categoryNo=103&viewDate=&isShowPopularPosts=true&from=search
//////        String regEx = ".*[\u2e80-\u2eff\u31c0-\u31ef\u3200-\u32ff\u3400-\u4dbf\u4e00-\u9fbf\uf900-\ufaff].*";
////        String regEx = ".*[\u2e80-\u2eff\u31c0-\u31ef\u3400-\u4dbf\u4e00-\u9fff\uf900-\ufaff\u20000-\u2A6DF\u2A700-\u2B73F\u2B740-\u2B81F\u2B820-\u2CEAF\u2CEB0-\u2EBE0\u2F800-\u2FA1F\u30000-\u3134F].*";
////        if (str.matches(regEx)) {
////            return true;
////        } else {
////            return false;
////        }
//    }
    public static boolean isOnlyChinese(String str) {
        Set<Character.UnicodeBlock> hanjaUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {
            {
                add(Character.UnicodeBlock.CJK_COMPATIBILITY);
//                add(Character.UnicodeBlock.CJK_COMPATIBILITY_FORMS); //U+FE30 ~ U+FE4F  ︰	︱	︲	︳	︴	︵	︶	︷	︸	︹	︺	︻	︼	︽	︾	︿
                add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS);
                add(Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS_SUPPLEMENT);
                add(Character.UnicodeBlock.CJK_RADICALS_SUPPLEMENT);
//                add(Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION); //U+3000 ~ U+303F 		、	。	〃	〄	々	〆	〇
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_C);
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_D);
                add(Character.UnicodeBlock.KANGXI_RADICALS);
//                add(Character.UnicodeBlock.IDEOGRAPHIC_DESCRIPTION_CHARACTERS); //U+2FF0 ~ U+2FFB These letters ⿰	⿱	⿲	⿳	⿴	⿵	⿶	⿷	⿸	⿹	⿺	⿻
            }
        };
        return isThisLanguageOnly(str, hanjaUnicodeBlocks);
    }

    public static boolean isOnlyCKJUnifiedChinese(String str) {
        Set<Character.UnicodeBlock> hanjaUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {
            {
                add(Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS);
            }
        };
        return isThisLanguageOnly(str, hanjaUnicodeBlocks);
    }

    public static String getOnlyChinese(String str) {
        return getOnlyThisLanguage(str, regExChinese);
    }

    public static String getOnlyKorean(String str) {
        return getOnlyThisLanguage(str, regExKorean);
    }
    public static String getOnlyHanja(String str) {
        return getOnlyThisLanguage(str, regExChinese);
    }
    public static String getOnlyJapanese(String str) {
        return getOnlyThisLanguage(str, regExJapanese);
    }

    public static String getOnlyEnglish(String str) {
        return getOnlyThisLanguage(str, regExEnglish);
    }

    public static boolean isOnlyKorean(String str) {
        Set<Character.UnicodeBlock> koreanUnicodeBlocks = new HashSet<Character.UnicodeBlock>() {
            {
                add(Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO);
                add(Character.UnicodeBlock.HANGUL_JAMO);
                add(Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_A);
                add(Character.UnicodeBlock.HANGUL_JAMO_EXTENDED_B);
                add(Character.UnicodeBlock.HANGUL_SYLLABLES);
            }
        };
        return isThisLanguageOnly(str, koreanUnicodeBlocks);
    }

    public static String getOnlyThisLanguage(String str, String regEx) {
        if (Utils.isEmpty(str))
            return "";

        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        StringBuilder sbResult = new StringBuilder();
        while (matcher.find()) {
            sbResult.append(matcher.group());
        }
//        String result = matcher.replaceAll("").trim(); // This code remove the matched text.
        return sbResult.toString();
    }

//    public static boolean containsHanjaInText(String s) {
//        return s.codePoints().anyMatch(
//                codepoint ->
//                        Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
//    }

    public static boolean containChineseCharacters(String str) {
        return containThisLanguage(str, regExChinese);
    }

    public static boolean containThisLanguage(String str, String regEx) {
        if (Utils.isEmpty(str))
            return false;

        Pattern p = Pattern.compile(regEx);
        Matcher matcher = p.matcher(str);
        return matcher.find();
    }

    public static boolean isThisLanguageOnly(String str, Set<Character.UnicodeBlock> unicodeBlocks) {
        if (Utils.isEmpty(str))
            return false;

        boolean blnResult = false;
        for (int i = 0; i < str.length(); i++) {
            int codepoint = str.codePointAt(i);
//        for (char c : str.toCharArray()) { //This code doesn't work. 이걸로는 제대로 안된다.
            if (unicodeBlocks.contains(Character.UnicodeBlock.of(codepoint))) {
                blnResult = true;
                break;
            }
        }
        return blnResult;

    }

    public static String removeNumbers(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        return value.replaceAll("[0-9]", "").trim();
    }

    public static String removeSpaces(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        return value.replaceAll(" ", "").trim();
    }

    public static String removeLineBreaks(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        return value.replaceAll("[\r\n]", " ")
                .trim();
    }
    public static String replaceLineBreaksToBRtag(String value) {
        if (Utils.isEmpty(value)) return Constant.BASE_BLANK;
        return value.replaceAll("[\r\n]+", "<br>")
                .trim();
    }

    public static boolean hasURL(CharSequence input) {
        boolean blnResult = false;
        if (TextUtils.isEmpty(input)) {
            return false;
        }

        blnResult = input.toString().contains("www");
        blnResult = input.toString().contains("http") || blnResult;
        if (blnResult)
            return true;


        Pattern URL_PATTERN = Patterns.WEB_URL;
        boolean hasURL = URL_PATTERN.matcher(input).matches();

        return hasURL;
    }

    public static boolean isOneWord(String input) {
        input = removeUselessStringInSubtitle(input);
        return input.split("\\s+").length == 1;
    }

    public static boolean isRepeatedOneWord(String input) {
        input = removeUselessStringInSubtitle(input);

        String[] words = input.split("\\s+");
        if (words.length <= 1)
            return false;
        for (int i = 0; i < words.length - 1; i++) {
            for (int j = i + 1; j < words.length; j++) {
                if (!words[i].equalsIgnoreCase(words[j])) {
                    return false; //다른 단어가 있으면
                }
            }
        }
        return true;
    }

    @NonNull
    private static String removeUselessStringInSubtitle(String input) {
//        input = input.replaceAll("-", ""); // Remove hyphens
        input = input.replaceAll("\\s+", " "); // Replace multiple spaces with single space
        input = input.trim(); // Remove leading/trailing spaces
        return removePunctuation(input);
    }


    public static boolean isAllCapitalWords(String input) {
        return input.replaceAll("\\s", "").matches("[A-Z]+");
    }

    //왜 minCharCount를 추가했을까? YES 이런건 삭제 안할려고 했나?
//    public static boolean isAllCapitalWords(String input, int minCharCount) {
//        String strAlphabet = input.replaceAll("[^a-zA-Z]", "");
//        if (strAlphabet.length() < minCharCount)
//            return false;
//
//        boolean allUpper = strAlphabet.chars().allMatch(Character::isUpperCase);
//        return allUpper;
//    }

    public static boolean isNonStudyLangOnly(String input) {
        input = input.replaceAll("\\s", "");
        String strAlphabet = input.replaceAll("[a-zA-Z]", "");
        if (strAlphabet.length() == input.length())
            return true;

        return false;
    }

    public static boolean hasMusicText(String input) {
        for (char c : input.toCharArray()) {
            String strOneAtIndex = Character.toString(c);
            if (strOneAtIndex.matches("[♪♫♩♬]")) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasPairedBracketPunctuation(String input) {
        input = input.replaceAll("-", ""); // Remove all whitespace characters
        input = input.replaceAll("\\s", ""); // Remove all whitespace characters
        return (input.startsWith("(") && input.endsWith(")"))
                || (input.startsWith("[") && input.endsWith("]"))
                || (input.startsWith("{") && input.endsWith("}"));
    }


    public static boolean hasPairedBracketPunctuation1(String input) {
        try
        {
            input = input.trim();
            Map<String,String> mapSpecialChars = new HashMap<String, String>();
            mapSpecialChars.put("(", ")");
            mapSpecialChars.put("[", "]");

            if (input.length() >= 3) {
                String firstChar = input.substring(0,1);
                String lastChar = input.substring(input.length() -1);
                for( String key : mapSpecialChars.keySet() ){
                    String value = mapSpecialChars.get(key).toString();
                    if ((firstChar.equals(key)) && (lastChar.equals(value)))
                        return true;
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
        return false;
    }


    public static String cutMeaningIfTooLongAboveSubtitle(String str) {
        int limitCharacters = 20;
        return cutTextFromStart(str, limitCharacters);
    }

    public static String cutMeaningIfTooLongInWordListView(String str) {
        int limitCharacters = 100;
        return cutTextFromStart(str, limitCharacters);
    }

    public static String cutTextFromStart(String str, int limitCharacters) {
        if (Utils.isEmpty(str))
            return "";

        return org.apache.commons.lang3.StringUtils.substring(str, 0, limitCharacters);
    }

    public static String getTextBeforeCharacter(String text, String character) {
        if (Utils.isEmpty(text))
            return "";

        int index = text.indexOf(character);
        if (index != -1) {
            return text.substring(0, index);
        }
        return "";
    }


    public static String getTrimTextFromTextView(TextView textView) {
        if (textView == null)
            return "";

        return textView.getText().toString().trim();
    }

    public static String covertStringToNFD(String str) {
        if (!Normalizer.isNormalized(str, Normalizer.Form.NFD)) {
            return Normalizer.normalize(str, Normalizer.Form.NFD);
        }
        return str;
    }

    public static String covertStringToNFC(String str) {
        if (!Normalizer.isNormalized(str, Normalizer.Form.NFC)) {
            return Normalizer.normalize(str, Normalizer.Form.NFC);
        }
        return str;
    }
    //이건 영문에서만 써야 한다.
    public static boolean areEnglishStringsEqualIgnoreCaseExceptPunctation(String str1, String str2) {
        // Remove all punctuation marks and special characters from the strings
        str1 = removeNonEnglishNumberSpace(str1).toLowerCase();
        str2 = removeNonEnglishNumberSpace(str2).toLowerCase();
        return str1.equals(str2);
    }

    //영문자나 숫자나 공백이 아닌걸 찾아서 지운다. a letter (\\p{L}), a digit (\\p{N}), or a space.
    private static String removeNonEnglishNumberSpace(String str) {
        Pattern pattern = Pattern.compile("[^\\p{L}\\p{N} ]");
        return pattern.matcher(str).replaceAll("");
    }

    //특수문자를 찾아서 지운다. !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
    private static String removePunctuation(String str) {
        Pattern pattern = Pattern.compile("\\p{Punct}");
        return pattern.matcher(str).replaceAll("");
    }


    public static String getSubstringWithMoreText(String str, int maxStringLength) {
        String strResult = str;
        if (strResult.length() >= maxStringLength) {
            strResult = strResult.substring(0, maxStringLength) + "...";
        }
        return strResult;
    }
    public static SpannableString getUnderlineOnWholeText(Context context, String string) {
        SpannableString spanString = new SpannableString(string);
        spanString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_web_url)), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new UnderlineSpan(), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    public static SpannableString getSpanDefaultColorOnKeyword(String string, String keyword) {
        int color = Color.RED;
        return getSpanColorOnKeyword(string, Arrays.asList(keyword), color);
    }
    public static SpannableString getSpanDefaultColorOnKeyword(String string, List<String> keywords) {
        int color = Color.RED;
        return getSpanColorOnKeyword(string, keywords, color);
    }
//    public static SpannableString getSpanColorOnKeyword(String string, String keyword, int color) {
//        SpannableString spanString = new SpannableString(string);
//        Pattern p = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(string);
//        while (m.find()){
//            spanString.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return spanString;
//    }
//    public static SpannableString getSpanColorOnKeyword(String string, String keyword, int color) {
//        return getSpanColorOnKeyword(string, Arrays.asList(keyword), color);
//    }

    public static SpannableString getSpanColorOnKeyword(String string, List<String> keywords, int color) {
        if (string == null || string.isEmpty()) {
            return new SpannableString("");
        }
        SpannableString spanString = new SpannableString(string);
        for (String keyword : keywords) {
            Pattern p = Pattern.compile(Pattern.quote(keyword), Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(string);
            while (m.find()){
                spanString.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanString;
    }


//
//    public static SpannableString getSpanColorOnKeyword(String string, String keyword, int color) {
//        SpannableString spanString = new SpannableString(string);
//        Pattern p = Pattern.compile(keyword, Pattern.CASE_INSENSITIVE);
//        Matcher m = p.matcher(string);
//        while (m.find()){
//            spanString.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            spanString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        }
//        return spanString;
//    }

    public static SpannableStringBuilder makeBoldOnPronounceOnHanjaWord(String meaning, String pronounce) {
        String strMeaningPronounceHanja = meaning + " " + pronounce;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strMeaningPronounceHanja);
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), strMeaningPronounceHanja.length() - 1, strMeaningPronounceHanja.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder makeBoldOnPronounceOnHanjaSentence(String pronounce, String meaning) {
        String strMeaningPronounceHanja = pronounce + " : " + meaning;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strMeaningPronounceHanja);
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, pronounce.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder makeBoldOnText(String str) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, str.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    public static String trimAndNormalizeString(String str) {
        if (Utils.isEmpty(str))
            return str;
        return normalizeString(str.trim());
    }

    //Convert Compitible CJK to Unified CJK. (["車" -> "車"] They look same but the codes are different)
    public static String normalizeString(String str) {
        if (Utils.isEmpty(str))
            return str;

//        String nfdNormalizedString1 = Normalizer.normalize(str, Normalizer.Form.NFC);
//        String nfdNormalizedString2 = Normalizer.normalize(str, Normalizer.Form.NFD);
//        String nfdNormalizedString3 = Normalizer.normalize(str, Normalizer.Form.NFKC);
//        String nfdNormalizedString4 = Normalizer.normalize(str, Normalizer.Form.NFKD);
//
//        DLog.d("normalizeHanja", "str : " + str);
//        for (int i = 0; i < str.length(); i++) {
//            char ch = str.charAt(i);
//            DLog.d("normalizeHanja", "(ch)" + ch + " : " + String.format("\\u%04x", (int) ch) + ", " + Character.UnicodeBlock.of(ch));
//        }
//        DLog.d("normalizeHanja", "nfdNormalizedString1 : " + nfdNormalizedString1);
//        for (int i = 0; i < nfdNormalizedString1.length(); i++) {
//            char ch = nfdNormalizedString1.charAt(i);
//            DLog.d("normalizeHanja", "(ch)" + ch + " : " + String.format("\\u%04x", (int) ch) + ", " + Character.UnicodeBlock.of(ch));
//        }
//        DLog.d("normalizeHanja", "nfdNormalizedString2 : " + nfdNormalizedString2);
//        for (int i = 0; i < nfdNormalizedString2.length(); i++) {
//            char ch = nfdNormalizedString2.charAt(i);
//            DLog.d("normalizeHanja", "(ch)" + ch + " : " + String.format("\\u%04x", (int) ch) + ", " + Character.UnicodeBlock.of(ch));
//        }
//        DLog.d("normalizeHanja", "nfdNormalizedString3 : " + nfdNormalizedString3);
//        for (int i = 0; i < nfdNormalizedString3.length(); i++) {
//            char ch = nfdNormalizedString3.charAt(i);
//            DLog.d("normalizeHanja", "(ch)" + ch + " : " + String.format("\\u%04x", (int) ch) + ", " + Character.UnicodeBlock.of(ch));
//        }
//        DLog.d("normalizeHanja", "nfdNormalizedString4 : " + nfdNormalizedString4);
//        for (int i = 0; i < nfdNormalizedString4.length(); i++) {
//            char ch = nfdNormalizedString4.charAt(i);
//            DLog.d("normalizeHanja", "(ch)" + ch + " : " + String.format("\\u%04x", (int) ch) + ", " + Character.UnicodeBlock.of(ch));
//        }
        return Normalizer.normalize(str, Normalizer.Form.NFKC);
    }

    public static String sliceStingIfTooLongToConvertHanja(String str, int maxLengthStringToConvert) {
        if (Utils.isEmpty(str))
            return "";
        try {
            if (str.length() > maxLengthStringToConvert) {
                str = str.substring(0, maxLengthStringToConvert);
            }
        } catch (Exception e) {

        }
        return str;
    }

    //"로 감싸면 Select쿼리시 word라는 단어를 WORD라는 필드에서 찾을때 모든 레코드가 나온다.
    public static String splitSentenceIntoWordListWithComma(String sentence) {
        if (Utils.isEmpty(sentence)) {
            return "";
        }
        sentence = sentence.toLowerCase().replaceAll("[^a-z0-9 ]", "");
        Set<String> wordSet = new LinkedHashSet<>();

        StringTokenizer st = new StringTokenizer(sentence);
        while (st.hasMoreTokens()) {
            wordSet.add(st.nextToken());
        }
        // Join words with commas, enclosing each word with single quotes
        return wordSet.stream().map(e -> "'" + e + "'").collect(Collectors.joining(","));
    }

    public static String getUsernameFromEmail(String email) {
        int index = email.indexOf("@");
        return index != -1 ? email.substring(0, index) : email;
    }

    public static String replaceLanguageMarker(String strToEdit, Context context) {
        String languageName = EnumLanguage.getLangNameByLanguage(EnumLanguage.getStudyLanguage(context), EnumLanguage.getMotherTongueLanguage(context));
        String languageNameMotherTongue = EnumLanguage.getLangNameByLanguage(EnumLanguage.getMotherTongueLanguage(context), EnumLanguage.getMotherTongueLanguage(context));
        return strToEdit.replace(Constant.studyLangMarker, languageName).replace(Constant.motherTongueLangMarker, languageNameMotherTongue);
    }

    //\n이 한개일때는 그냥두고 여러개일때는 \n를 한개 줄인다.
    public static String reduceNewLinesByOne(String text) {
        String result = "";
        boolean isMeetNewLineFirstTime = true;
        boolean isMeetNewLineSecondTime = true;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == '\n') {
                if (isMeetNewLineFirstTime) {
                    isMeetNewLineFirstTime = false;
                    isMeetNewLineSecondTime = true;
                    result += character;
                    continue;
                } else if (isMeetNewLineSecondTime) {
                    isMeetNewLineSecondTime = false;
                    continue;
                }
            } else {
                isMeetNewLineFirstTime = true;
            }
            result += character;
        }
        return result;
    }
}
