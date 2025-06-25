package com.dalread.util;

import com.dalread.util.arasubtitle.SRTTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SubtitleFormatDetector {
    public static final int FORMAT_NONE = 0;
    public static final int FORMAT_SMI = 1;
    public static final int FORMAT_SRT = 2;
    public static final int FORMAT_ASS = 3;
    public static final int FORMAT_BRACKET = 4;

    public int detectSubtitleFormatFromFile(File file) {
        List<String> lines = readLinesFromFile(file);
        return detectSubtitleFormat(lines);
    }

    public int detectSubtitleFormatFromContent(String content) {
        List<String> subtitleList = readLinesFromContent(content);
        return detectSubtitleFormat(subtitleList);
    }

    private List<String> readLinesFromFile(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readLinesFromContent(String content) {
        return new BufferedReader(new StringReader(content))
                .lines()
                .collect(Collectors.toList());
    }

    private int detectSubtitleFormat(List<String> lines) {
        if (isSMISubtitleFile(lines)) {
            return FORMAT_SMI;
        } else if (isSRTSubtitleFile(lines)) {
            return FORMAT_SRT;
        } else if (isASSSubtitleFile(lines)) {
            return FORMAT_ASS;
        } else if (isBracketSubtitleFile(lines)) {
            return FORMAT_BRACKET;
        }
        return FORMAT_NONE;
    }

    private boolean isSMISubtitleFile(List<String> subtitleList) {
        Pattern pattern = Pattern.compile("^<sync start.*$", Pattern.CASE_INSENSITIVE);
        return containsMatchingLine(subtitleList, pattern);
//        return subtitleList.stream()
//                .anyMatch(line -> pattern.matcher(line).matches());
    }

    private boolean isSRTSubtitleFile(List<String> subtitleList) {
        return subtitleList.stream()
                .anyMatch(this::parseTime);
    }

    private boolean isASSSubtitleFile(List<String> subtitleList) {
        Pattern pattern = Pattern.compile("^Dialogue:.*$", Pattern.CASE_INSENSITIVE);
        return containsMatchingLine(subtitleList, pattern);
    }

    private boolean isBracketSubtitleFile(List<String> subtitleList) {
        Pattern pattern = Pattern.compile("^\\[\\d+\\]\\[\\d+\\].*$");
        return containsMatchingLine(subtitleList, pattern);
    }

    private boolean containsMatchingLine(List<String> subtitleList, Pattern pattern) {
        return subtitleList.stream().anyMatch(line -> pattern.matcher(line).matches());
    }
//
//    public int detectSubtitleFormatFromFile(File file) {
//        int subtitleFormat = FORMAT_NONE;
//
//        List<String> lines = null;
//        try {
//            lines = Files.readAllLines(file.toPath());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (isSMISubtitleFile(lines)) {
//            subtitleFormat = FORMAT_SMI;
//        } else if (isSRTSubtitleFile(lines)) {
//            subtitleFormat = FORMAT_SRT;
//        } else if (isASSSubtitleFile(lines)) {
//            subtitleFormat = FORMAT_ASS;
//        } else if (isBracketSubtitleFile(lines)) {
//            subtitleFormat = FORMAT_BRACKET;
//        } else if (isLrcFile(lines)) {
//            subtitleFormat = FORMAT_LRC;
//        }
//
//        return subtitleFormat;
//    }
//    public int detectSubtitleFormatFromContent(String content) {
//        int subtitleFormat = FORMAT_NONE;
//        List<String> subtitleList = new BufferedReader(new StringReader(content))
//                .lines()
//                .collect(Collectors.toList());
//
//        if (isSMISubtitleFile(subtitleList)) {
//            subtitleFormat = FORMAT_SMI;
//        } else if (isSRTSubtitleFile(subtitleList)) {
//            subtitleFormat = FORMAT_SRT;
//        } else if (isASSSubtitleFile(subtitleList)) {
//            subtitleFormat = FORMAT_ASS;
//        } else if (isBracketSubtitleFile(subtitleList)) {
//            subtitleFormat = FORMAT_BRACKET;
//        } else if (isLrcFile(subtitleList)) {
//            subtitleFormat = FORMAT_LRC;
//        }
//
//        return subtitleFormat;
//    }
//
//    private boolean isSMISubtitleFile(List<String> subtitleList) {
//        try {
//            Pattern pattern = Pattern.compile("^<sync start.*$", Pattern.CASE_INSENSITIVE);
//            for (String subtitle : subtitleList) {
//                Matcher matcher = pattern.matcher(subtitle);
//                if (matcher.matches()) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private boolean isSRTSubtitleFile(List<String> subtitleList) {
//        try {
//            for (String subtitle : subtitleList) {
//                if (parseTime(subtitle)) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private boolean isASSSubtitleFile(List<String> subtitleList) {
//        try {
//            Pattern pattern = Pattern.compile("^Dialogue:.*$", Pattern.CASE_INSENSITIVE);
//            for (String subtitle : subtitleList) {
//                Matcher matcher = pattern.matcher(subtitle);
//                if (matcher.matches()) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }
//
//    private boolean isBracketSubtitleFile(List<String> subtitleList) {
//        try {
//            Pattern pattern = Pattern.compile("^\\[\\d+\\]\\[\\d+\\].*$");
//            for (String subtitle : subtitleList) {
//                Matcher matcher = pattern.matcher(subtitle);
//                if (matcher.matches()) {
//                    return true;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    private boolean parseTime1(String subtitleLine) {
        // Implement SRT time parsing logic here
        // Example: check if the line matches SRT time format
        return subtitleLine.matches("\\d{2}:\\d{2}:\\d{2},\\d{3} --> \\d{2}:\\d{2}:\\d{2},\\d{3}");
    }

    private boolean parseTime(String timeLine) {
        boolean result = false;
        String normalizeTimeLine = normalizeTimeLine(timeLine);
        String times[] = normalizeTimeLine.split(SRTTime.DELIMITER.trim());

        try {
            if (times.length == 2) {
                LocalTime start = SRTTime.fromString(times[0]);
                LocalTime end = SRTTime.fromString(times[1]);
                SRTTime time = new SRTTime(start, end);
                result = true;
            }
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            result = false;
        }

        return result;
    }

    private String normalizeTimeLine(String timeLine) {
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

    private String makeTimeToCompleteForm(String text) {
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
}
