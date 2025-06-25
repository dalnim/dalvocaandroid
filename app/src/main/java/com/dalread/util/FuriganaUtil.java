package com.dalread.util;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class FuriganaUtil {
    private static final String TAG = "FuriganaUtil";
    private static final boolean DEBUG = false;

    private static void log(String msg) {
        if (DEBUG) {
            DLog.d(TAG, msg);
        }
    }

    public static String checkAndParserRubyText(String data) {
        String txt = "";
        final String[] items = data.split(Constant.RUBY.KEY.SPAN_CLOSE);
        for (String item : items) {
            if (Utils.isEmpty(item))
                continue;
            log( "span1 start=" + item);
            //\n이 있으면 BaseFuriganaView에서는 전부 <br>로 대체하는데, \n이 본문끝에 있고, 또다른 \n이 본문 다음에 있으면 빈줄이 하나인데, BaseFuriganaView에서는 <br>을 전부 새로운 줄에 보여주므로 빈줄이 2개로 보이는 버그가 있다. 그래서 일단 \n이 여러개이면 하나를 줄이는 편법을 쓴다.
            item = StringUtils.reduceNewLinesByOne(item);
            //\m이 있으면 <br>로 대체하는 코드. 바로 위에서 여러개의 \n는 한개를 줄였다.
            item = item.replaceAll(Constant.RUBY.KEY.CHARACTER_N, Constant.RUBY.KEY.BREAK_BR_START);
            if (item.trim().startsWith(Constant.RUBY.KEY.BREAK_BR_START)) {
                String brItem = item.substring(0, item.indexOf(">") + 1);
                item = item.replace(brItem, Constant.BASE_BLANK);
                brItem = generateRubyIncorrect(brItem.trim());
                log( "span1 brTag=" + brItem);
                txt += brItem;
            } else {
                if (item.contains(Constant.RUBY.KEY.BREAK_BR_START)) {
                    String[] arrBr = item.split(Constant.RUBY.KEY.BREAK_BR_START);
                    String brTmp = Constant.BASE_BLANK;
                    for (String br : arrBr) {
                        if (!Utils.isEmpty(brTmp)) {
                            brTmp += generateRubyIncorrect(Constant.RUBY.KEY.BREAK_BR_START);
                        }
                        brTmp += generateRubyIncorrect(br);
                    }
                    txt += brTmp;
                    continue;
                }
            }
            if (!item.startsWith(Constant.RUBY.KEY.SPAN_OPEN_CHECK) && item.indexOf(Constant.RUBY.KEY.SPAN_OPEN_CHECK) > 1) {
                String value = item.substring(0, item.indexOf(Constant.RUBY.KEY.SPAN_OPEN_CHECK));
                item = item.replace(value, Constant.BASE_BLANK);
                value = generateRubyIncorrect(value);
                txt += value;
                log( "span1 value=" + value + " - checked=" + item);
            }

            String span = item;
            log( "span1=" + span);
            Document doc = Jsoup.parse(span);
            String rbStr = Constant.BASE_BLANK;
            Elements rbs = doc.getElementsByTag(Constant.RUBY.KEY.RB_TAG);
            if (rbs != null && rbs.size() > 1) {
                for (Element rb : rbs) {
                    log( "rb=" + rb.text());
                    span = span.replaceFirst(Constant.RUBY.KEY.RB_OPEN + rb.text() + Constant.RUBY.KEY.RB_CLOSE, Constant.BASE_BLANK);
                    rbStr += rb.text();
                }
                log( "rbStr=" + rbStr);
            }

            String rtStr = Constant.BASE_BLANK;
            Elements rts = doc.getElementsByTag(Constant.RUBY.KEY.RT_TAG);
            if (rts != null && rts.size() > 1) {
                for (Element rt : rts) {
                    log( "rt=" + rt.text());
                    span = span.replaceFirst(Constant.RUBY.KEY.RT_OPEN + rt.text() + Constant.RUBY.KEY.RT_CLOSE, Constant.BASE_BLANK);
                    rtStr += rt.text();
                }
                log( "rtStr=" + rtStr);
            }
            if (TextUtils.isEmpty(rbStr) && TextUtils.isEmpty(rtStr)) {
                String temp = generateRubyIncorrect(item);
                log( "temp1=" + temp);
                txt += temp;
            } else {
                span = span.replace(Constant.RUBY.KEY.RUBY_OPEN, Constant.BASE_BLANK);
                span = span.replace(Constant.RUBY.KEY.RUBY_CLOSE, Constant.BASE_BLANK);
                String temp = Constant.BASE_BLANK;
                if (span.indexOf("\">") > 0) {
                    temp = span.substring(0, span.indexOf("\">") + 2);
                } else if (span.indexOf(">") > 0) {
                    temp = span.substring(0, span.indexOf(">") + 1);
                }
                if (TextUtils.isEmpty(temp)) {
                    final String temp1 = generateRubyIncorrect(item);
                    log( "temp2=" + temp1);
                    txt += temp1;
                } else {
                    log( "temp3=" + temp);
                    span = span.replace(temp, temp + Constant.RUBY.KEY.RUBY_OPEN +
                            Constant.RUBY.KEY.RB_OPEN + rbStr + Constant.RUBY.KEY.RB_CLOSE +
                            Constant.RUBY.KEY.RT_OPEN + rtStr + Constant.RUBY.KEY.RT_CLOSE +
                            Constant.RUBY.KEY.RUBY_CLOSE);
                    span += Constant.RUBY.KEY.SPAN_CLOSE;

                    log( "span2=" + span);
                    txt += span;
                }
            }
        }
        return TextUtils.isEmpty(txt) ? data : txt;
    }

    public static String generateRubyIncorrect(String item) {
        String temp = Constant.BASE_BLANK;
        if (!item.contains(Constant.RUBY.KEY.SPAN_OPEN_CHECK)) {
            temp += Constant.RUBY.KEY.SPAN_OPEN;
        }
        if (!item.contains(Constant.RUBY.KEY.RUBY_OPEN_CHECK)) {
            temp += Constant.RUBY.KEY.RUBY_OPEN;
        }
        if (!item.contains(Constant.RUBY.KEY.RB_OPEN_CHECK)) {
            temp += Constant.RUBY.KEY.RB_OPEN;
        }
        temp += item;
        if (!item.contains(Constant.RUBY.KEY.RB_CLOSE_CHECK)) {
            temp += Constant.RUBY.KEY.RB_CLOSE;
        }
        if (!item.contains(Constant.RUBY.KEY.RT_OPEN_CHECK)) {
            temp += Constant.RUBY.KEY.RT_OPEN;
        }
        if (!item.contains(Constant.RUBY.KEY.RT_CLOSE_CHECK)) {
            temp += Constant.RUBY.KEY.RT_CLOSE;
        }
        if (!item.contains(Constant.RUBY.KEY.RUBY_CLOSE_CHECK)) {
            temp += Constant.RUBY.KEY.RUBY_CLOSE;
        }
        temp += Constant.RUBY.KEY.SPAN_CLOSE;
        return temp;
    }
}
