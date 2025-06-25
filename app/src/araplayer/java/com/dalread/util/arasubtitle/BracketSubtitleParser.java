package com.dalread.util.arasubtitle;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BracketSubtitleParser extends BaseParser<SRTSub> {
    private static final String TS_PATTERN = "%02d:%02d:%02d,%03d";
    public static final String USELESS_CHAR_IN_TIME = " x";

    @Override
    protected void parse(BufferedReader br, SRTSub sub) throws IOException, InvalidSubException {

        boolean found = true;
        int id = 1;
        while (found) {
            SRTLine line = parseSubtitle(br, id);
            if (found = (line != null)) {
                sub.add(line);
                id++;
            }
        }
    }

    /**
     *
     * Example of SRT Bracket line:
     *
     * <pre>
     * [0][12]to contain the most feared|thing in the universe.
     * </pre>	 *
     */
    private static SRTLine parseSubtitle(BufferedReader br, int id) {
        String timeLine = "";
        try {
            timeLine = br.readLine();
            if (timeLine == null) {
                return null;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }

        try {
            Pattern EXPRESSION_PATTERN = Pattern.compile("^\\[(?<start>\\d+)\\]\\[(?<end>\\d+)\\](?<content>.*)$");

            Matcher matcher = EXPRESSION_PATTERN.matcher(timeLine);
            if (matcher.find()) {
                String strStartTime = matcher.group("start").trim();
                String strEndTime = matcher.group("end").trim();
                String content = matcher.group("content").trim();

                strStartTime = makeTimeToCompleteForm(strStartTime); //12을 00:00:12,000으로 바꾸어준다.
                strEndTime = makeTimeToCompleteForm(strEndTime);


                LocalTime start = SRTTime.fromString(strStartTime);
                LocalTime end = SRTTime.fromString(strEndTime);
                SRTTime time = new SRTTime(start, end);

                List<String> contentList = Arrays.asList(content.split(System.lineSeparator()));

                return new SRTLine(id, time, contentList);
            }
        } catch(InvalidSRTSubException e) {

        }
        return null;
    }

    private static String makeTimeToCompleteForm(String second) {
        SubtitleTimeCode timeCode = new SubtitleTimeCode(Integer.parseInt(second + "00"));
        String completedForm = String.format(TS_PATTERN, timeCode.getHour(), timeCode.getMinute(),timeCode.getSecond(), timeCode.getMillisecond());
        return completedForm;
    }
}
