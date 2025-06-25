package com.dalread.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormatSMI {
    private static String filleName;
    private  List<TimedTextObject> subtitles = new ArrayList<>();

    public  List<TimedTextObject> parseSS(String fileName,
                                                InputStream inputStream, String encoding)
            throws Exception {
        filleName = fileName;
        String ss = convertStreamToString(inputStream, encoding);

        if(!createTimedObjects(ss))
            return null;

        ss = ss.replace("\n&nbsp;", "&nbsp;");
        String[] lines = ss.split("\n");

        StringBuilder captionTxt = new StringBuilder();
        String className = "";
        int startMs = 0, endMs = 0;
        boolean first = false, captionStart = true;

        for(int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if(!line.isEmpty() && !line.equals("\n")) {
                if (line.contains("&nbsp;")||line.contains("nbsp;")) {
                    if (captionStart) {
                        String[] tags = splitTag(line.toLowerCase());
                        for (int j = 0; j < tags.length; ++j) {
                            String tag = tags[j];
                            if (tag.contains("<sync start")) {
                                endMs = Integer.parseInt(tag.substring(tag.indexOf("=") + 1,
                                        tag.indexOf(">")));
                                addCaption(className, startMs, endMs, captionTxt.toString());
                                captionTxt = new StringBuilder();
                                captionStart = false;

                                break;
                            }
                        }
                    }
                } else if (line.toLowerCase().contains("<sync start")) {
                    String[] tags = splitTag(line.toLowerCase());
                    for (int j = 0; j < tags.length; ++j) {
                        String tag = tags[j];
                        if (tag.contains("<sync start")) {
                            startMs = Integer.parseInt(tag.substring(tag.indexOf("=") + 1,
                                    tag.indexOf(">")));
                        } else if (tag.contains("<p class")) {
                            className = tag.substring(tag.indexOf("=") + 1,
                                    tag.indexOf(">"));
                        }
                    }

                    captionStart = true;
                    first = true;
                } else {

                    if (first) {
                        line = line.replaceAll("\\<.*?\\>", "");
                        captionTxt.append(line);
                    }
                }
            }
        }

        return subtitles;
    }

    private static String[] splitTag(String message)
    {

        Vector <String> tempVector = new Vector<String> ();
        StringBuffer temp = new StringBuffer();

        int index = 0;
        boolean bFirst = false;

        for(int i = 0 ; i < message.length(); i++)
        {
            if(message.charAt(i) == '<')
            {
                if(bFirst)
                {
                    tempVector.add(temp.toString());
                    temp = new StringBuffer();  // reset
                    index++;
                }
                else
                {
                    bFirst = true;
                }
                temp.append(message.charAt(i));
            }
            else if(message.charAt(i) == '>')
            {
                temp.append(message.charAt(i));
                index++;
                tempVector.add(temp.toString());
                temp = new StringBuffer();  // reset
            }
            else
            {
                temp.append(message.charAt(i));
            }

        }
        if(temp.length() > 0)
        {
            tempVector.add(temp.toString());
            temp = new StringBuffer();  // reset
        }

        String[] outMessage = new String[tempVector.size()];
        for(int i = 0; i< tempVector.size(); i++)
        {
            outMessage[i] = tempVector.get(i);
        }

        return outMessage;
    }

    public  boolean createTimedObjects(String ss) {
        List<String> classes = findClasses(ss);
        if(classes == null)
            return false;

        for(String s : classes) {
            TimedTextObject object = new TimedTextObject();
            object.title = getClassId(s).toLowerCase();
            object.fileName = filleName;

            List<String> classProp = findClassProp(s);

            if(classProp == null)
                return false;

            for(String prop: classProp) {
                String[] keyVal = prop.split(":");
                if(keyVal[0].toLowerCase().contains("lang")) {
                    keyVal[1] = keyVal[1].replace(";", "");
                    object.language = keyVal[1].toLowerCase();

                    String temp[] = object.language.split("-");
                    object.language = temp[0];
                }
            }


            Log.e("langugage clas", object.language);

            if(!object.title.equals(".P") && !object.title.equals(".p"))
                subtitles.add(object);
        }

        return true;
    }

    public static List<String> findClasses(String ss) {
        String classPattern = "\\.[a-zA-Z0-9]+\\s*\\{.*?\\}";
        Pattern pattern = Pattern.compile(classPattern);
        Matcher matcher = pattern.matcher(ss);
        List<String> classes = new ArrayList<>();

        int count = 0;

        while (matcher.find()) {
            classes.add(matcher.group());

            Log.e("matvcher count", classes.get(count)+"");

            ++count;
        }

        return classes;
    }

    private static List<String> findClassProp(String ss) {
        String classPropPattern = "([a-zA-Z]+):([a-zA-Z-]+);";
        Pattern pattern = Pattern.compile(classPropPattern);
        Matcher matcher = pattern.matcher(ss);
        List<String> classes = new ArrayList<>();

        int count = 0;

        while (matcher.find()) {
            classes.add(matcher.group());
            Log.e("prop", classes.get(count));

            ++count;
        }

        return classes;
    }

    public static String getClassId(String ss) {
        String classIdPattern = "\\.([a-zA-Z0-9]+)";
        Pattern pattern = Pattern.compile(classIdPattern);
        Matcher matcher = pattern.matcher(ss);

        matcher.matches();
        if(matcher.find()) {
            Log.e("martcher clas", matcher.group(0));
            return matcher.group(0);
        }

        return null;
    }
    private  void addCaption(String className, int startMs, int endMs, String text) {
        for(TimedTextObject object: subtitles) {

            if(object.title.contains(className.toLowerCase())) {

                Caption caption = new Caption();
                caption.start = new Time(startMs);
                caption.content = text;
                caption.end = new Time(endMs);

                /*if(object.captions.size() > 0) {
                    object.captions.pollLastEntry().getValue().end = new Time(startMs);
                }*/

                if(object.captions.containsKey(startMs)) {
                    object.captions.put(startMs + 1, caption);
                } else {
                    object.captions.put(startMs, caption);
                }

                Log.e("cation text", caption.content);
            }
        }
    }

    public static String convertStreamToString(InputStream is, String encoding) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is,
                encoding));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }



}