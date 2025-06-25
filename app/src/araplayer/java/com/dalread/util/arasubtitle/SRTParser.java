package com.dalread.util.arasubtitle;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public final class SRTParser extends BaseParser<SRTSub> {

    public static final String USELESS_CHAR_IN_TIME = " x";

    @Override
    protected void parse(BufferedReader br, SRTSub sub) throws IOException, InvalidSubException {

        boolean found = true;
        while (found) {
            SRTLine line = firstIn(br);
            if (found = (line != null)) {
                sub.add(line);
            }
        }
    }

    /**
     * Extract the firt SRTLine found in a buffered reader. <br/>
     *
     * Example of SRT line:
     *
     * <pre>
     * 1
     * 00:02:46,813 --> 00:02:50,063
     * A text line
     * </pre>
     *
     * @param br
     * @return SRTLine the line extracted, null if no SRTLine found
     * @throws IOException
     * @throws InvalidSRTSubException
     */
    private static SRTLine firstIn(BufferedReader br) throws IOException, InvalidSRTSubException {

        String idLine = readFirstTextLine(br);
        String timeLine = br.readLine();

        if (idLine == null || timeLine == null) {
            return null;
        }

        try {
            int id = parseId(idLine);
            SRTTime time = parseTime(timeLine);
            List<String> textLines = new ArrayList<>();
            String testLine;
            while ((testLine = br.readLine()) != null) {
                if (StringUtils.isEmpty(testLine.trim())) {
                    break;
                }
                textLines.add(testLine);
            }
            return new SRTLine(id, time, textLines);
        } catch(InvalidSRTSubException e) {
            return null;
        }

    }

    /**
     * Extract a subtitle id from string
     *
     * @param textLine ex 1
     * @return the id extracted
     * @throws InvalidSRTSubException
     */
    private static int parseId(String textLine) throws InvalidSRTSubException {

        int idSRTLine;
        try {
            idSRTLine = Integer.parseInt(textLine.trim());
        } catch (NumberFormatException e) {
            throw new InvalidSRTSubException("Expected id not found -> " + textLine);
        }

        return idSRTLine;
    }

    /**
     * Extract a subtitle time from string
     *
     * @param timeLine: ex 00:02:08,822 --> 00:02:11,574
     * @return the SRTTime object
     * @throws InvalidSRTSubException
     */
    public static SRTTime parseTime(String timeLine) throws InvalidSRTSubException {

        String normalizeTimeLine = normalizeTimeLine(timeLine);
        SRTTime time = null;
        String times[] = normalizeTimeLine.split(SRTTime.DELIMITER.trim());

        if (times.length != 2) {
            throw new InvalidSRTSubException("Subtitle " + normalizeTimeLine + " - invalid times : " + normalizeTimeLine);
        }

        try {
            LocalTime start = SRTTime.fromString(times[0]);
            LocalTime end = SRTTime.fromString(times[1]);
            time = new SRTTime(start, end);
        } catch (DateTimeParseException e) {
            throw new InvalidSRTSubException("Invalid time string : " + timeLine, e);
        }

        return time;
    }

    // 시간뒤에 xx같은게 붙어있는것을 제거한다.
    private static String normalizeTimeLine(String timeLine) {
        String normalizedTimeLine = timeLine;
        String times[] = timeLine.split(SRTTime.DELIMITER.trim());

        if (times.length == 2) {
            String normalizedStartTime= times[0].trim();
            String endTime = times[1].trim();
            String normalizedEndTime  = endTime.split(" ")[0];
            normalizedStartTime = makeTimeToCompleteForm(normalizedStartTime); //00,000을 00:00:00,000으로 바꾸어준다.
            normalizedEndTime = makeTimeToCompleteForm(normalizedEndTime);

            StringBuilder sb = new StringBuilder();
            sb.append(normalizedStartTime);
            sb.append(SRTTime.DELIMITER.trim());
            sb.append(normalizedEndTime);
            normalizedTimeLine = sb.toString();
//				public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SRTTime.PATTERN);
//				public static final String PATTERN = "HH:mm:ss,SSS";
//				private static final String TS_PATTERN = "%02d:%02d:%02d,%03d";
//				public static final String DELIMITER = " --> ";
        }

        return normalizedTimeLine;
    }

    private static String makeTimeToCompleteForm(String text) {
        int hour = 0, minute = 0, second = 0, milliSecond = 0;
        String strMilliSecond = "000";
        String[] values = text.split(",");
        if (values.length > 0) {
            String[] times = values[0].split(":");

            if (times.length == 3){
                hour = Integer.parseInt(times[0]);
                minute = Integer.parseInt(times[1]);
                second = Integer.parseInt(times[2]);
            } else if (times.length == 2) {
                minute = Integer.parseInt(times[0]);
                second = Integer.parseInt(times[1]);
            } else {
                second = Integer.parseInt(times[0]);
            }

            if (values.length > 1) {
                strMilliSecond = values[1];
                if (strMilliSecond.length() == 1) {
                    strMilliSecond += "00";
                } else if (strMilliSecond.length() == 2) {
                    strMilliSecond += "0";
                }
                milliSecond = Integer.parseInt(strMilliSecond);
            }
        }
        String completedForm = String.format("%02d:%02d:%02d,", hour, minute ,second) + strMilliSecond;
        return completedForm;
    }
//

//        DLog.d(getLogTag(), "text=" + text + " --> data=" + String.format("%02d:%02d:%02d,%d", h, m ,s ,ms));

}