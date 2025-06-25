package com.dalread.util.arasubtitle;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SamiParser {
    private enum CursorStatus {
        NONE,
        BODY_START,
        BODY_END,
        CUE_TIMECODE,
        CUE_TEXT,
        STYLE_START,
        STYLE_END,
        COMMENT_START,
        COMMENT_END,
        SYNC_START,
        SYNC_END;
    }

    private String charset; // Charset of the input files

    public SamiParser(String charset) {
        this.charset = charset;
    }

    //    @Override
    public SamiObject parse(InputStream is) throws IOException, SubtitleParsingException {
        // Create SAMI object
        SamiObject samiObject = new SamiObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));

        String textLine = "";
        CursorStatus cursorStatus = CursorStatus.NONE;
        SamiCue cue = null;
        SamiCue previousCue = null;
        SubtitleTextLine line = null;
        StringBuilder contentsInStyleTag = new StringBuilder();
        Integer id = 0;
        while ((textLine = br.readLine()) != null) {
            textLine = textLine.trim().replaceAll("\\x00", "");  // remove all NULLs, Some <SYNC has 0x00 in front of every character
            id++;

            // Lower case text line
            String lcTextLine = textLine.toLowerCase();

            if ((lcTextLine.startsWith("<!--")) || (lcTextLine.startsWith("-->"))) {
                continue;
            }

            if (lcTextLine.trim().startsWith("<sync")) {
                cursorStatus = CursorStatus.SYNC_START;
                // Get start time
                String text = textLine.substring(5).trim();

                if (!text.toLowerCase().startsWith("start=")) {
                    throw new SubtitleParsingException(String.format(
                            "Unexpected time code: %s", textLine));
                }

                // New cue
                cue = new SamiCue();
                // Make sure this is an integer
                String startTimeSync = text.substring(6, text.length()).trim();
                long time;
                String language = "";
                try {
//                    time = Long.valueOf(startTime);
                    time = getTimeFromSMISync(startTimeSync);
                    if (startTimeSync.toUpperCase().contains("END")) {
                        long endTime = getEndTimeFromSMISync(startTimeSync);
                        cue.setEndTime(new SubtitleTimeCode(endTime));
                    }
                    language = getLanguageFromSMISync(startTimeSync);
                    samiObject.addLanguages(language);
                } catch (NumberFormatException e) {
                    throw new SubtitleParsingException(String.format(
                            "Unable to parse start time: %s",
                            textLine));
                }


                cue.setStartTime(new SubtitleTimeCode(time));
                String languageName = getLanguageNameAsStudyLanguageForm(language);
                cue.setLanguage(languageName);
                cue.setLanguageSMIForm(language);
                Integer languageCode = getLangCodeFromLangName(languageName);
                cue.setLanguageCode(languageCode);
//                samiObject.addLanguage(language);
                // Set end time for previous cue
                if (previousCue != null) {
                    previousCue.addLine(line);
                    previousCue.setEndTime(new SubtitleTimeCode(time));
                }

                samiObject.addCue(cue);
                previousCue = cue;

                String strContentAfterStartSync = getContentAfterStartSync(startTimeSync);
                strContentAfterStartSync = getSimpleTextWithoutHTMLTag(strContentAfterStartSync);
                line = new SubtitleTextLine();
                if (strContentAfterStartSync.trim().length() > 0) {
//                	System.out.println("strContentAfterStartSync : '" + strContentAfterStartSync + "'");
                    line.addText(new SubtitlePlainText(strContentAfterStartSync));
                }
                continue;
            } else if (cursorStatus == CursorStatus.SYNC_START)  {
                String text = textLine;

                // Remove p start tag
                if (lcTextLine.startsWith("<p")) {
                    text = text.substring(text.indexOf(">")+1);
                }

                // Remove p end tag
                if (lcTextLine.endsWith("</p>")) {
                    text = text.substring(0, text.length()-4);
                }

                // Add new text line
                text = getSimpleTextWithoutHTMLTag(text);
                line.addText(new SubtitlePlainText(text));
                continue;
            }

        }
        //이건 왜하지?
        // This is the end
        // Set end time for the last cue
        if (previousCue != null) {
            // Last cue duration is 2s
            previousCue.setEndTime(new SubtitleTimeCode(previousCue.getStartTime().getTime() + 2000));
        }

        return samiObject;
    }

    //  @Override
    public SamiObject parseOld(InputStream is) throws IOException, SubtitleParsingException {
        // Create SAMI object
        SamiObject samiObject = new SamiObject();

        // Read each lines
        BufferedReader br = new BufferedReader(new InputStreamReader(is, this.charset));
//      System.out.println("\n\nthis.charset : " + this.charset);
//      BufferedReader in = new BufferedReader(new InputStreamReader(is, "euc-kr"));
//      String textLine1 = "";
//      while ((textLine1 = in.readLine()) != null) {
//          textLine1 = textLine1.trim();
//          String utf8Text1 = new String(textLine1.getBytes("UTF-8"));
//          String utf8Text2 = new String(textLine1.getBytes("UTF-8"), "UTF-8");
//          String utf8Text = new String(textLine1.getBytes("EUC-KR"), "UTF-8");
//          System.out.println("\n\ntextLine1 : " + textLine1);
//          System.out.println("\nutf8Text2 : " + utf8Text1);
//          System.out.println("\nutf8Text2 : " + utf8Text2);
//          System.out.println("\nutf8Text : " + utf8Text);
//      }
        String textLine = "";
        CursorStatus cursorStatus = CursorStatus.NONE;
        SamiCue cue = null;
        SamiCue previousCue = null;
        SubtitleTextLine line = null;
        StringBuilder contentsInStyleTag = new StringBuilder();
        Integer id = 0;
        while ((textLine = br.readLine()) != null) {
            textLine = textLine.trim();
            id++;

            // Lower case text line
            String lcTextLine = textLine.toLowerCase();

            if (lcTextLine.startsWith("<style type=\"text/css\">")) {
                cursorStatus = CursorStatus.STYLE_START;
                continue;
            }

            if (!lcTextLine.startsWith("</style>")  && (cursorStatus == CursorStatus.STYLE_START)) {
                contentsInStyleTag.append(textLine + System.getProperty("line.separator"));
                continue;
            }

            if (lcTextLine.startsWith("</style>") && cursorStatus == CursorStatus.STYLE_START) {
                cursorStatus = CursorStatus.NONE;
                samiObject.addContentsInStyleTag(contentsInStyleTag.toString());
                continue;
            }

            if (lcTextLine.startsWith("</body>") || cursorStatus == CursorStatus.BODY_END) {
                cursorStatus = CursorStatus.BODY_END;
                continue;
            }

            if (cursorStatus == CursorStatus.NONE) {
                if (!lcTextLine.startsWith("<body>")) {
                    continue;
                }

                cursorStatus = CursorStatus.BODY_START;
                continue;
            }

            if (cursorStatus == CursorStatus.BODY_START) {
                if (textLine.isEmpty()) {
                    continue;
                }
            }

            //BODY 밑에서 주석을 만났을때는 아무처리도 안하고 넘어간다.
            if ((cursorStatus == CursorStatus.BODY_START) && lcTextLine.startsWith("<!--")) {
                cursorStatus = CursorStatus.COMMENT_START;
                if ((cursorStatus == CursorStatus.COMMENT_START) && lcTextLine.endsWith("-->")) {
                    cursorStatus = CursorStatus.BODY_START; //만약에 주석이 한줄에 다 있으면 다시 BODY_START로 바꾸어준다.
                }
                continue;
            }
            if ((cursorStatus == CursorStatus.COMMENT_START) && lcTextLine.endsWith("-->")) {
                cursorStatus = CursorStatus.BODY_START;
                continue;
            }
            if (cursorStatus == CursorStatus.COMMENT_START) {
                continue;
            }

            //BODY밑에서 sync태그를 만났을때...
            if ((cursorStatus == CursorStatus.BODY_START) && lcTextLine.startsWith("<sync")) {
                // Get start time
                String text = textLine.substring(5).trim();

                if (!text.toLowerCase().startsWith("start=")) {
                    throw new SubtitleParsingException(String.format(
                            "Unexpected time code: %s", textLine));
                }

                // New cue
                cue = new SamiCue();
                // Make sure this is an integer
                String startTimeSync = text.substring(6, text.length()).trim();
                long time;
                String language = "";
                try {
//                  time = Long.valueOf(startTime);
                    time = getTimeFromSMISync(startTimeSync);
                    if (startTimeSync.toUpperCase().contains("END")) {
                        long endTime = getEndTimeFromSMISync(startTimeSync);
                        cue.setEndTime(new SubtitleTimeCode(endTime));
                    }
                    language = getLanguageFromSMISync(startTimeSync);
                    samiObject.addLanguages(language);
                } catch (NumberFormatException e) {
                    throw new SubtitleParsingException(String.format(
                            "Unable to parse start time: %s",
                            textLine));
                }


                cue.setStartTime(new SubtitleTimeCode(time));
                String languageName = getLanguageNameAsStudyLanguageForm(language);
                cue.setLanguage(languageName);
                cue.setLanguageSMIForm(language);
                Integer languageCode = getLangCodeFromLangName(languageName);
                cue.setLanguageCode(languageCode);
//              samiObject.addLanguage(language);
                // Set end time for previous cue
                if (previousCue != null) {
                    previousCue.addLine(line);
                    previousCue.setEndTime(new SubtitleTimeCode(time));
                }

                samiObject.addCue(cue);
                previousCue = cue;

                String strContentAfterStartSync = getContentAfterStartSync(startTimeSync);
                strContentAfterStartSync = getSimpleTextWithoutHTMLTag(strContentAfterStartSync);
                line = new SubtitleTextLine();
                if (strContentAfterStartSync.trim().length() > 0) {
//              	System.out.println("strContentAfterStartSync : '" + strContentAfterStartSync + "'");
                    line.addText(new SubtitlePlainText(strContentAfterStartSync));
                }
                continue;
            } else if (cursorStatus == CursorStatus.BODY_START)  {
                String text = textLine;

                // Remove p start tag
                if (lcTextLine.startsWith("<p")) {
                    text = text.substring(text.indexOf(">")+1);
                }

                // Remove p end tag
                if (lcTextLine.endsWith("</p>")) {
                    text = text.substring(0, text.length()-4);
                }

                // Add new text line
                text = getSimpleTextWithoutHTMLTag(text);
                line.addText(new SubtitlePlainText(text));
                continue;
            }

            throw new SubtitleParsingException(String.format(
                    "Unexpected line: %s", textLine));
        }

        // This is the end
        // Set end time for the last cue
        if (previousCue != null) {
            // Last cue duration is 2s
            previousCue.setEndTime(new SubtitleTimeCode(previousCue.getStartTime().getTime() + 2000));
        }

        return samiObject;
    }

    private String getSimpleTextWithoutHTMLTag(String text)
    {
//    	Document document = Jsoup.parse(text);

        String newText = text.replaceAll("<br>","\r");

        //html tag를 없애고 new line을 보존할려고 하는거다...
        //근데 &nbsp;는 그대로있다.
        //ref : https://stackoverflow.com/questions/5640334/how-do-i-preserve-line-breaks-when-using-jsoup-to-convert-html-to-plain-text
        String resultText = Jsoup.clean(newText, "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
        resultText = resultText.replaceAll("&nbsp;"," ");
        //지우지말것, 이건 new line이 보존안된 상태로 html tag없이 text로만 변환시켜준다.
        //&nbsp;는 " "으로 변경된다.
        String text2 = Jsoup.parse(text).text();
        return resultText;
    }
    private Integer getTimeFromSMISync(String strSync) {
        Integer resultTime = 0;
        String strSyncResult = strSync;
        if (strSyncResult.contains(">")) {
            strSyncResult = strSyncResult.substring(0, strSyncResult.indexOf(">"));
        }
        if (strSyncResult.toUpperCase().contains("END")) {
            strSyncResult = strSyncResult.substring(0, strSyncResult.indexOf("E"));
        }
        try {
            resultTime = Integer.parseInt(strSyncResult.trim());
        } catch (NumberFormatException e) {
            resultTime = 0;
        }
        return resultTime;
    }

    private Integer getEndTimeFromSMISync(String strSync) {
        Integer resultTime = 0;
        String strSyncResult = strSync;
        if (strSyncResult.toUpperCase().contains("END=") && strSyncResult.contains(">")) {
            strSyncResult = strSyncResult.substring(strSyncResult.indexOf("=")+1, strSyncResult.indexOf(">"));
        }
        try {
            resultTime = Integer.parseInt(strSyncResult.trim());
        } catch (NumberFormatException e) {
            resultTime = 0;
        }
        return resultTime;
    }

    private String getContentAfterStartSync(String strSync) {
        String strContent = "";
        String strSyncResult = strSync;
        String strTag = "<P Class=";

        if (strSyncResult.contains(strTag)) {
            strSyncResult = strSyncResult.substring(strSyncResult.indexOf(strTag) + strTag.length(), strSyncResult.length());
        }

        if (strSyncResult.contains(">")) {
            strSyncResult = strSyncResult.substring(strSyncResult.indexOf(">")+1, strSyncResult.length());
        }

//		language = getLanguageNameAsStudyLanguageForm(strSyncResult);
        strContent = strSyncResult.trim();
        return strContent;
    }

    private String getLanguageFromSMISync(String strSync) {
        String language = "";
        String strSyncResult = strSync;
        String strTag = "<P Class=";

        if (strSyncResult.contains(strTag)) {
            strSyncResult = strSyncResult.substring(strSyncResult.indexOf(strTag) + strTag.length(), strSyncResult.length());
        }

        if (strSyncResult.contains(">")) {
            strSyncResult = strSyncResult.substring(0, strSyncResult.indexOf(">"));
        }

//		language = getLanguageNameAsStudyLanguageForm(strSyncResult);
        language = strSyncResult.trim();
        return language;
    }

    private String getLanguageNameAsStudyLanguageForm(String strClassLanuage) {
        String languageNameAsStudyLanguageForm = strClassLanuage;
        switch (strClassLanuage.toUpperCase()) {
            case Constants.LANG_SMI_CLASS_ENGLISH_EN:
            case Constants.LANG_SMI_CLASS_ENGLISH_ENCC:
            case Constants.LANG_SMI_CLASS_ENGLISH_EGCC:
                languageNameAsStudyLanguageForm = Constants.LANG_EN;
                break;
            case Constants.LANG_SMI_CLASS_KOREAN_KR:
            case Constants.LANG_SMI_CLASS_KOREAN_KOR:
            case Constants.LANG_SMI_CLASS_KOREAN_KRCC:
                languageNameAsStudyLanguageForm = Constants.LANG_KO;
                break;
            case Constants.LANG_SMI_CLASS_JAPANESE_JP:
            case Constants.LANG_SMI_CLASS_JAPANESE_JPCC:
            case Constants.LANG_SMI_CLASS_JAPANESE_JA:
            case Constants.LANG_SMI_CLASS_JAPANESE_JACC:
                languageNameAsStudyLanguageForm = Constants.LANG_JP;
                break;
            case Constants.LANG_SMI_CLASS_CHINESE_CH_S:
            case Constants.LANG_SMI_CLASS_CHINESE_CHCC:
                languageNameAsStudyLanguageForm = Constants.LANG_CH_S;
                break;

        }
        return languageNameAsStudyLanguageForm;
    }

    private Integer getLangCodeFromLangName(String langName) {
        Integer langCode = Constants.LANGCODE_EN;
        try {
            switch (langName.toUpperCase()) {
                case Constants.LANG_CH_S:
                    langCode = Constants.LANGCODE_CH_S;
                    break;
                case Constants.LANG_JP:
                    langCode = Constants.LANGCODE_JP;
                    break;
                case Constants.LANG_KO:
                    langCode = Constants.LANGCODE_KO;
                    break;
                case Constants.LANG_EN:
                    langCode = Constants.LANGCODE_EN;
                    break;
                case Constants.LANG_HANJA:
                    langCode = Constants.LANGCODE_HANJA;
                    break;
                case Constants.LANG_VI:
                    langCode = Constants.LANGCODE_VI;
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return langCode;
    }

}