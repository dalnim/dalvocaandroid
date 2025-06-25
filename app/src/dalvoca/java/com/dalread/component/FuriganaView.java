package com.dalread.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.model.RubyTextModel;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FuriganaUtil;
import com.dalread.util.Utils;
import com.dalread.util.Voca;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Akira on 2016/06/24.
 */
public class FuriganaView extends AppCompatTextView {
    private static final String TAG = FuriganaView.class.getSimpleName();
    private static final boolean DEBUG = false;
    private static final int TIME_LONG_CLICK = 1000;
    private static final int TYPE_NORMAL = 1;
//    private static final int TYPE_BOLDER = 2;
//    private static final int TYPE_ITALIC = 4;
    private static final int TYPE_MEANING = 5;
    private static final int TYPE_FURIGANA = 2;
    private static final String KANJI_REGEX = "(([{])" +
            "(.*?)([#])" +
            "(.*?)([#])" +
            "(.*?)([}])" +
            ")";
//    private static final String BOLD_TEXT_REGEX = "(?m)(?d)(?s)(([<][b][>])(.*?)([<][\\/][b][>]))";
//    private static final String ITALIC_TEXT_REGEX = "(?m)(?d)(?s)(([<][i][>])(.*?)([<][\\/][i][>]))";
    public static final Pattern patternAsterisk = Pattern.compile(Constant.PATTERN_SPECIAL_CHARACTERS);
    public static final Pattern patternBr = Pattern.compile(Constant.RUBY.KEY.BREAK_REGEX);

    private int mWidthSize;
    private String mText;
    private String meaningText;
    private Vector<Line> mLines;
    private Vector<PairText> mAllTexts;
    private TextPaint mBackgroundPaint;
    private TextPaint mNormalTextPaint;
    private TextPaint mNormalTextPaintOutline;
    private TextPaint mFuriganaTextPaint;
//    private TextPaint mBoldTextPaint;
//    private TextPaint mItalicTextPaint;
//    private TextPaint mBoldItalicTextPaint;
    private TextPaint mMeaningTextPaint;
    private TextPaint mMeaningTextPaintOutline;
    private float mLineHeight;
    private float mMaxLineWidth;
    private float mLineSpacing;
    private String textClicked = null;
    private float mTouchX, mTouchY;
    private OnTextSelectedListener mOnTextSelectedListener;
    // Double click
    int numberOfTaps = 0;
    boolean isDoubleClick = false;
    final Handler mHandler = new Handler();
    private SharedPreferencesDB mSharedPref;
    private boolean isKnownPronounceMeaning;
    // Hide meaning and pronouce when isTutor = true.
    private boolean isTutor;
    // [Lesson] Japanese&Chinese : Don’t show meaning, but show pronunciation.
    private boolean isShowMeaningJPCN = false;
    // using DalPlayer when value is true
    private String baseText = Constant.BASE_BLANK;
    private boolean isBaseContent;
    private String listId, listType;
    private final int TEXT_BONUS = 15;
    // change color when playing DalPlayer
    private boolean isShowOutline = false;
    // https://github.com/dalnim/IssueOnly/issues/95
    private boolean isWord;
    // https://github.com/dalnim/IssueOnly/issues/93
    private boolean displayPronunciation = true;
    // https://github.com/dalnim/IssueOnly/issues/104
    private int showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
    private boolean isTextCenter;

    private Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            performLongClick();
        }
    };

    public FuriganaView(Context context) {
        super(context);
        initialize(context, null);
    }

    public FuriganaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public FuriganaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void setOnTouchListener(boolean isOnTouchListener) {
        setOnTouchListener(isOnTouchListener ? onTouchListener : null);
    }

    private void initialize(Context context, AttributeSet attrs) {
        setOnTouchListener(true);
        mSharedPref = SharedPreferencesDB.getInstance(context);
        isKnownPronounceMeaning = Voca.checkStudyLanguageJPCN(mSharedPref);
        mLines = new Vector<>();
        mAllTexts = new Vector<>();
        TextPaint textPaint = getPaint();

        mBackgroundPaint = new TextPaint(textPaint);
        mNormalTextPaint = new TextPaint(textPaint);

        mNormalTextPaintOutline = new TextPaint(textPaint);
        mNormalTextPaintOutline.setStyle(Paint.Style.STROKE);
        mNormalTextPaintOutline.setColor(ContextCompat.getColor(getContext(), R.color.color_ruby_text_normal));
        mNormalTextPaintOutline.setAntiAlias(true);
        mNormalTextPaintOutline.setStrokeWidth(5);

        mFuriganaTextPaint = new TextPaint(textPaint);
        mFuriganaTextPaint.setTextSize(textPaint.getTextSize() - 1);

//        mBoldTextPaint = new TextPaint(textPaint);
//        mBoldTextPaint.setFakeBoldText(true);
//
//        mItalicTextPaint = new TextPaint(textPaint);
//        mItalicTextPaint.setTextSkewX(-0.35f);
//
//        mBoldItalicTextPaint = new TextPaint(textPaint);
//        mBoldItalicTextPaint.setTextSkewX(-0.35f);
//        mBoldItalicTextPaint.setFakeBoldText(true);

        mMeaningTextPaint = new TextPaint(textPaint);
        mMeaningTextPaint.setTextSize(mFuriganaTextPaint.getTextSize() - 5);

        mMeaningTextPaintOutline = new TextPaint(textPaint);
        mMeaningTextPaintOutline.setStyle(Paint.Style.STROKE);
        mMeaningTextPaintOutline.setColor(ContextCompat.getColor(getContext(), R.color.color_ruby_text_normal));
        mMeaningTextPaintOutline.setAntiAlias(true);
        mMeaningTextPaintOutline.setStrokeWidth(5);
        mMeaningTextPaintOutline.setTextSize(mMeaningTextPaint.getTextSize());

        if (attrs == null) {
            mLineSpacing = 25;
        } else {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FuriganaView, 0, 0);
            try {
                mText = typedArray.getString(R.styleable.FuriganaView_jText);
                mLineSpacing = typedArray.getInteger(R.styleable.FuriganaView_line_spacing, 25);
            } finally {
                typedArray.recycle();
            }
        }

        // Calculate the height of one line.
//        mLineHeight = mFuriganaTextPaint.getFontSpacing()
//                + Math.max(Math.max(Math.max(mNormalTextPaint.getFontSpacing(),
//                mBoldTextPaint.getFontSpacing()),
//                mItalicTextPaint.getFontSpacing()),
//                mBoldItalicTextPaint.getFontSpacing())
//                + mLineSpacing;
        mLineHeight = mFuriganaTextPaint.getFontSpacing()
                + Math.max(mNormalTextPaint.getFontSpacing(),
                mMeaningTextPaint.getFontSpacing())
                + mLineSpacing;

        if (!TextUtils.isEmpty(mText)) {
            setJText();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("onMeasure");
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        mWidthSize = widthSize;
        if (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST && widthMode > 0) {
            measureText(widthSize);
        } else {
            measureText(-1);
            mMaxLineWidth += TEXT_BONUS;
        }
        int height = getHeightRuby();

        int width = widthSize;
        if (widthMode != MeasureSpec.EXACTLY && mLines.size() <= 1) {
            width = (int) Math.round(Math.ceil(mMaxLineWidth));
        }
        if (heightMode != MeasureSpec.UNSPECIFIED && height > heightSize) {
            height |= MEASURED_STATE_TOO_SMALL;
        }
        log( "onMeasure - width=" + width);
        setMeasuredDimension(width, height);
    }

    private int getHeightRuby() {
        float y = 0;
        if (mLines.size() > 0) {
            float height = 0;
            for (int i = 0; i < mLines.size(); i++) {
                Line line = mLines.get(i);
                boolean isShowMeaning = false;
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    PairText pairText = line.mPairTexts.get(j);
                    if (!isShowMeaning) {
                        if (isTutor) {
                            isShowMeaning = false;
                        } else {
                            isShowMeaning = pairText.mJText.rubyTextModel.isShowMeaning() && pairText.mJText.rubyTextModel.isHasMeaning();
                        }
                    }
                }
                height = isShowMeaning ? mLineHeight : mLineHeight / 2;
                y += height;
            }
        }
        return (int) y;
    }

    /***
     * Measure view with max width.
     * @param width if width < 0 → the view has one line, which has width unlimited.
     */
    private void measureText(int width) {
        log( "measureText - width=" + width);
        mLines.clear();
        mMaxLineWidth = 0;

        if (width <= 0) {
            Line line = new Line();
            line.setPairTexts(mAllTexts);
            for (int i = 0; i < mAllTexts.size(); i++) {
                mMaxLineWidth += mAllTexts.get(i).mWidth;
            }
        } else {
            float widthTemp = 0;
            Vector<PairText> pairTexts = new Vector<>();
            for (int i = 0; i < mAllTexts.size(); i++) {
                PairText pairText = mAllTexts.get(i);
                log( "easureText" + pairText.toString());
                // Break to new line if {@PairText} contain break character.
                if (pairText.isBreak ||
                        StringUtils.startsWithAny(pairText.mJText.mText.trim(), new String[] {"<br>", "<br/>", "<br />"})) {
                    Line line = new Line();
                    line.setPairTexts(pairTexts);
                    mLines.add(line);
                    mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
                    // Reset for new line
                    pairTexts = new Vector<>();
                    widthTemp = 0;
                    continue;
                }

                widthTemp += pairText.mWidth;
                log( "measureText - widthTemp=" + widthTemp + " - width=" + width + " - pairText.mWidth=" + pairText.mWidth);
                if (widthTemp < width) {
                    pairTexts.add(pairText);
                } else {
                    log( "measureText - " + pairText.mJText.mText + " - id=" + pairText.mJText.rubyTextModel.getVocaId());
                    if (pairText.mJText.rubyTextModel.getVocaId() == 0) {
                        Line line = new Line();
                        widthTemp -= pairText.mWidth;
                        // If kanji -> break to new line
                        if(pairText.mFuriganaText != null) {
                            line.setPairTexts(pairTexts);
                            mLines.add(line);
                            mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;

                            // Reset for new line
                            pairTexts = new Vector<>();
                            pairTexts.add(pairText);
                            widthTemp = pairText.mWidth;
                        } else {
                            PairText splitPairText = pairText.split(widthTemp, width, pairTexts);

                            // Add new line
                            line.setPairTexts(pairTexts);
                            mLines.add(line);
                            mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
                            if(splitPairText == null) {
                                pairTexts = new Vector<>();
                                continue;
                            }
                            // Reset for new line
                            widthTemp = splitPairText.mWidth;
                            //split for long text
                            while (widthTemp > width) {
                                widthTemp = 0;
                                pairTexts = new Vector<>();
                                splitPairText = splitPairText.split(widthTemp, width, pairTexts);
                                line = new Line();
                                line.setPairTexts(pairTexts);
                                mLines.add(line);
                                mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;

                                if(splitPairText != null) {
                                    widthTemp = splitPairText.mWidth;
                                } else {
                                    widthTemp = 0;
                                }
                            }

                            pairTexts = new Vector<>();
                            if(splitPairText != null) {
                                pairTexts.add(splitPairText);
                            }
                        }
                    } else {
                        Line line = new Line();
                        // If kanji -> break to new line
                        if (pairText.mFuriganaText != null) {
                            widthTemp -= pairText.mWidth;
                            log( "measureText - kanji");
                            line.setPairTexts(pairTexts);
                            mLines.add(line);
                            mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;

                            // Reset for new line
                            pairTexts = new Vector<>();
                            pairTexts.add(pairText);
                            widthTemp = pairText.mWidth;
                        } else {
                            // Add new line when widthTemp > width. Need new line
                            line.setPairTexts(pairTexts);
                            mLines.add(line);

                            pairTexts = new Vector<>();
                            mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
                            pairTexts.add(pairText);

                            // long text
                            widthTemp = 0;
                            PairText splitPairText = pairText.split(widthTemp, width, pairTexts, false);
                            log( "measureText - kanji is null");
                            if (splitPairText == null) {
                                log( "measureText - splitPairText is null");
                                widthTemp = pairText.mWidth;
                                if (i == (mAllTexts.size() - 1)) {
                                    line = new Line();
                                    line.setPairTexts(pairTexts);
                                    mLines.add(line);
                                }
                                continue;
                            }

                            pairTexts = new Vector<>();
                            if (splitPairText != null) {
                                pairTexts.add(splitPairText);
                            }
                        }
                    }
                }

                // Make the last line before quit loop.
                if (i == (mAllTexts.size() - 1) && pairTexts.size() > 0) {
                    Line line = new Line();
                    line.setPairTexts(pairTexts);
                    mLines.add(line);
                    int index = 0;
                    while (mLines.size() < 2) {
                        Vector<PairText> pt1 = new Vector<>();
                        pt1.add(new PairText(new JText("", TYPE_NORMAL, pairTexts.size())));
                        Line l1 = new Line();
                        l1.setPairTexts(pt1);
                        mLines.add(index, l1);
                        index++;
                    }
                    mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        log( "SON - onDraw");
        if (mLines.size() > 0) {
            float y = 0;
            float height;
            for (int i = 0; i < mLines.size(); i++) {
                Line line = mLines.get(i);
                boolean isShowMeaning = false;
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    PairText pairText = line.mPairTexts.get(j);
                    if (!isShowMeaning) {
                        if (isTutor) {
                            isShowMeaning = false;
                        } else {
                            isShowMeaning = pairText.mJText.rubyTextModel.isShowMeaning() && pairText.mJText.rubyTextModel.isHasMeaning();
                        }
                    }
                }
                height = isShowMeaning ? mLineHeight : mLineHeight / 2;
                y += height;
                log( "onDraw - y=" + y + " - mMaxLineWidth=" + mMaxLineWidth);
                Rect rect = new Rect((int) ((mWidthSize - line.mWidth) / 2), (int) (y - height), (int) line.mWidth, (int) y);
                line.mLineRect = rect;
                float x = isTextCenter() ? rect.left : 0;

                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    PairText pairText = line.mPairTexts.get(j);
                    Rect pairRect = new Rect((int) x, (int) (y - height), (int) (x + pairText.mWidth), (int) y);
                    log( "onDraw - pairRect=" + pairRect.toString());
                    pairText.mPairRect = pairRect;
                    pairText.onDraw(canvas, x, y - (TEXT_BONUS * 2));
                    x += pairText.mWidth;
                    log( "onDraw - x=" + x + " - text=" + pairText.mJText.mText);
                }
            }
        } else {
            super.onDraw(canvas);
        }
    }

    public void resetText() {
        mLines.clear();
        mAllTexts.clear();
        listId = Constant.BASE_BLANK;
        listType = Constant.BASE_BLANK;
        invalidate();
        requestLayout();
    }

    /**
     * Sets the text that this View is to display.
     *
     * @param text text to display
     */
    public void setJText(CharSequence text) {
        setJText(text, Constant.BASE_BLANK);
    }

    public void setJText(CharSequence text, CharSequence meaningText) {
        if (TextUtils.isEmpty(text) && TextUtils.isEmpty(meaningText)) {
            return;
        }
        log( "setJText - text");
        mText = (String) text;
        setMeaningText((String) meaningText);
        if (isBaseContent) {
            baseText = mText;
        }
        setJText();
        invalidate();
        requestLayout();
    }

    /**
     * Set text without invalidate
     */
    private void setJText() {
        log( "setJText");
        mLines.clear();
        mText = replaceRuby(mText);
//        mText = mText.replaceAll(Constant.BREAK_REGEX, Constant.BREAK_CHARACTER);
        if (!Utils.isEmpty(meaningText) && showAsterisk != Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
            parseText(meaningText + Constant.BREAK_CHARACTER, TYPE_MEANING);
        }
        if (!Utils.isEmpty(mText)) {
            parseText(mText);
        }
    }

    public void reload() {
        if (!TextUtils.isEmpty(mText)) {
            mLines.clear();
            mAllTexts.clear();
            parseText(mText);
            invalidate();
            requestLayout();
        }
    }

    public void reDrawLayout(String content) {
        mLines.clear();
        mAllTexts.clear();
        mText = content;
        parseText(mText);
        invalidate();
        requestLayout();
    }

    private String replaceRuby(String textWithRuby) {
        textWithRuby = FuriganaUtil.checkAndParserRubyText(textWithRuby);
        log( "textWithRuby parsed=" + textWithRuby);
        String parsed;
        parsed = textWithRuby.replace(Constant.RUBY.KEY.RUBY_OPEN, Constant.RUBY.KEY.KEY_REPLACE);
        parsed = parsed.replace(Constant.RUBY.KEY.RT_OPEN, Constant.RUBY.KEY.KEY_REPLACE);
        parsed = parsed.replace(Constant.RUBY.KEY.RT_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RB_OPEN, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RB_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.SPAN_OPEN_CHECK, "{<span");
        parsed = parsed.replace(Constant.RUBY.KEY.SPAN_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RUBY_CLOSE, "}");
        log( "parse=" + parsed);
        int noOccurence = parsed.split("<span", -1).length - 1;
        isWord = noOccurence == 1;
        log( "parse=noOccurence=" + noOccurence + " - isWord=" + isWord);
        return parsed;
    }

    private void parseText(String text) {
        parseText(text, TYPE_NORMAL);
    }

    /**
     * Parse text with struct {kanji;furigana}
     *
     * @param text text to parse
     * @param type 4 type for display. bold, italic, bold-italic and normal.
     */
    private void parseText(String text, int type) {
        log( "parseText - text=" + text + " - type=" + type);
        Pattern pattern = Pattern.compile(KANJI_REGEX);
        Matcher matcher = pattern.matcher(text);
        int start = 0;
        int end;
        while (matcher.find()) {
            String html = matcher.group(3);
            log( "html=" + html);
            String fullText = matcher.group(1);
            log( "fullText=" + fullText);
            String kanji = matcher.group(5);
            log( "kanji=" + kanji);
            String furigana = matcher.group(7);
            log( "furigana=" + furigana);

            end = text.indexOf(fullText, start);
            if (end < 0) {
                continue;
            }

            if (end > start) {
                String normalText = text.substring(start, end);
                parseBreakLineText(normalText, type);
            }

            JText kanjiText = new JText(kanji, mText, html, type, mAllTexts.size());
            if (isWord &&
                    !Utils.isEmpty(kanjiText.rubyTextModel.getPronounce()) &&
                    (!kanjiText.rubyTextModel.isKnown() || !kanjiText.rubyTextModel.isKnownPronounce())) {
                kanjiText = new JText(kanji + (displayPronunciation ? " [" + kanjiText.rubyTextModel.getPronounce() + "]" : ""), mText, html, type, mAllTexts.size());
            }
            if (kanjiText.rubyTextModel.getVocaId() > 0) {
                setListId(addItemList(listId, kanjiText.rubyTextModel.getVocaId()));
                setListType(addItemList(listType, kanjiText.rubyTextModel.getVocaType()));
            }
            FuriganaText furiganaText = null;
            if (!isTutor) {
                if (isKnownPronounceMeaning) {
                    if (kanjiText.rubyTextModel.isShowMeaning()) {
                        furiganaText = new FuriganaText(furigana, html, mAllTexts.size());
                    }
                } else {
                    if (kanjiText.rubyTextModel.isShowMeaning()) {
                        furiganaText = new FuriganaText(kanjiText.rubyTextModel.getMeaning(), html, mAllTexts.size());
                    }
                }
            }
            PairText pairText = new PairText(kanjiText, furiganaText, mAllTexts.size());
            mAllTexts.add(pairText);
            start = end + fullText.length();
        }

        end = text.length();
        if (end > start) {
            String normalText = text.substring(start, end);
            parseBreakLineText(normalText, type);
        }
    }

    /**
     * Parse text with struct \n \r <br> <br /> <br/>
     *
     * @param text text to parse
     * @param type 4 type for display. bold, italic, bold-italic and normal.
     */
    private void parseBreakLineText(String text, int type) {
        if (text.contains(Constant.BREAK_CHARACTER)) {
            int breakIndex = text.indexOf(Constant.BREAK_CHARACTER);
            String firstText = text.substring(0, breakIndex);
            JText jText = new JText(firstText, type, mAllTexts.size());
            PairText pairText = new PairText(jText);
            mAllTexts.add(pairText);

            PairText breakPairText = new PairText();
            mAllTexts.add(breakPairText);

            String secondText = text.substring(breakIndex + Constant.BREAK_CHARACTER.length());
            parseBreakLineText(secondText, type);
        } else {
            JText jText = new JText(text, type, mAllTexts.size());
            PairText pairText = new PairText(jText, mAllTexts.size());
            if (!Utils.isEmpty(pairText.mJText.mText)) {
                mAllTexts.add(pairText);
            }
        }
    }

    @Override
    public boolean performClick() {
        handleClickText();
        return super.performClick();
    }


    @Override
    public boolean performLongClick() {
        handleClickText();
        return super.performLongClick();
    }

    public long getGap() {
        return 500L; //500ms
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int action = event.getAction() & MotionEvent.ACTION_MASK;
            log( "onTouchEvent - action" + action);
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    postDelayed(mLongClickRunnable, TIME_LONG_CLICK);
                case MotionEvent.ACTION_MOVE:
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    removeCallbacks(mLongClickRunnable);
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    if (textClicked == null) {
                        textClicked = getCurrentTextClicked();
                    }
                    if (numberOfTaps == 0) {
                        numberOfTaps++;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isDoubleClick = numberOfTaps > 1;
                                numberOfTaps = 0;
                                performClick();
                            }
                        }, ViewConfiguration.getDoubleTapTimeout());
                    } else {
                        numberOfTaps++;
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    numberOfTaps = 0;
                    break;
                default:
                    removeCallbacks(mLongClickRunnable);
            }
            return true;
        }
    };

    private boolean handleClickText() {
        if (mOnTextSelectedListener == null) return false;
        log( "mTouchX=" + mTouchX + " - mTouchY=" + mTouchY);
        boolean result = false;
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            log( "line=" + line.mLineRect.toString());
            if (line.contain((int) mTouchX, (int) mTouchY)) {
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    PairText pairText = line.mPairTexts.get(j);
                    result = pairText.contain((int) mTouchX, (int) mTouchY);
                    log( "result=" + result + " - pairText=" + pairText.mPairRect.toString());
                    if (result) {
                        String text = pairText.mJText.mText;
                        log( "textClicked=" + textClicked + " - text=" + text);
                        isDoubleClick = isDoubleClick && (!TextUtils.isEmpty(textClicked) && !TextUtils.isEmpty(text) && textClicked.equals(text));
                        if (pairText.mJText.rubyTextModel != null
                                && pairText.mJText.rubyTextModel.getVocaId() > 0) {
                            if (isDoubleClick) {
                                if (pairText.mJText.rubyTextModel != null && pairText.mJText.rubyTextModel.isHasMeaning()) {
                                    pairText.mJText.rubyTextModel.handleDoubleClick();
                                    if (pairText.mFuriganaText != null && pairText.mFuriganaText.rubyTextModel != null) {
                                        pairText.mFuriganaText.rubyTextModel.handleDoubleClick();
                                    }
                                    reDrawLayout(pairText.mJText.rubyTextModel.getContent());
                                    mOnTextSelectedListener.onDoubleClick(text, pairText.mJText.rubyTextModel);
                                }
                            } else {
                                mOnTextSelectedListener.onTextSelected(text, pairText.mJText.rubyTextModel);
                            }
                        }
                        break;
                    }
                }
                break;
            }
        }
        textClicked = null;
        return result;
    }

    private String getCurrentTextClicked() {
        boolean result = false;
        for (int i = 0; i < mLines.size(); i++) {
            Line line = mLines.get(i);
            if (line.contain((int) mTouchX, (int) mTouchY)) {
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    PairText pairText = line.mPairTexts.get(j);
                    result = pairText.contain((int) mTouchX, (int) mTouchY);
                    if (result) {
                        return pairText.mJText.mText;
                    }
                }
                break;
            }
        }

        return null;
    }

    public void setOnTextSelectedListener(OnTextSelectedListener onTextSelectedListener) {
        this.mOnTextSelectedListener = onTextSelectedListener;
    }

    public void setIsKnownPronounceMeaning(boolean isKnownPronounceMeaning) {
        this.isKnownPronounceMeaning = isKnownPronounceMeaning;
    }

    private class Line {
        private Vector<PairText> mPairTexts;
        private Rect mLineRect;
        private float mWidth;

        public boolean contain(int x, int y) {
            return mLineRect != null && mLineRect.contains(x, y);
        }

        public void setPairTexts(Vector<PairText> pairTexts) {
            mPairTexts = pairTexts;
            measureWidth();
        }

        private void measureWidth() {
            mWidth = 0;
            for (PairText pairText : mPairTexts) {
                mWidth += pairText.mWidth;
            }
        }
    }

    private class PairText {
        Rect mPairRect;
        JText mJText;
        FuriganaText mFuriganaText;
        float mWidth;
        boolean isBreak = false;
        int index = 0;

        public PairText() {
            isBreak = true;
        }

        public PairText(final JText jText) {
            log( "PairText(JText jText)=" + index + " - jText=" + jText);
            mJText = jText;
            measureWidth();
        }

        public PairText(final JText jText, final int index) {
            log( "PairText(JText jText, int index)=" + index + " - jText=" + jText);
            mJText = jText;
            if (mAllTexts != null &&
                    mAllTexts.size() > 0 &&
                    mJText.rubyTextModel != null &&
                    mJText.rubyTextModel.getVocaId() <= 0) {
                final PairText temp = mAllTexts.get(index - 1);
                if (temp != null && temp.mJText != null && temp.mJText.rubyTextModel != null) {
                    log( "temp.mJText.rubyTextModel.isKnown()=" + temp.mJText.rubyTextModel.isKnown());
                    log( "temp.mJText.rubyTextModel.generateMeaningKnownPronounce()=" + temp.mJText.rubyTextModel.generateMeaningKnownPronounce());
                    log( "mJText.mText=" + mJText.mText);
                    log( "Equals=" + (temp.mJText.rubyTextModel.generateMeaningKnownPronounce().compareTo(mJText.mText)));
                    final int compareTo = temp.mJText.rubyTextModel.generateMeaningKnownPronounce().
                            compareTo(mJText.mText);
                    if (isKnownPronounceMeaning &&
                            (compareTo == -1 || compareTo == 0)) {
                        if (isShowMeaningJPCN && !temp.mJText.rubyTextModel.isKnown() && !Utils.isEmpty(temp.mJText.rubyTextModel.getMeaning()) && !isTutor) {
                            mJText.rubyTextModel = new RubyTextModel(temp.mJText.rubyTextModel);
                            mJText.rubyTextModel.setVocaId(-1);
                        } else {
                            mJText.mText = Constant.BASE_BLANK;
                        }
                    }
                }
            }
            log( "index02=" + index + " - jText=" + jText);
            this.index = index;
            measureWidth();
        }

        public PairText(final JText jText, final FuriganaText furiganaText, final int index) {
            log( "PairText(JText jText, FuriganaText furiganaText, int index)=" + index + " - jText=" + jText);
            mJText = jText;
            mFuriganaText = furiganaText;
            this.index = index;
            measureWidth();
        }

        private void measureWidth() {
            if (mFuriganaText == null) {
                mWidth = mJText.mWidth;
            } else {
                mWidth = Math.max(mJText.mWidth, mFuriganaText.mWidth);
            }
        }

        public PairText split(float width, float maxWidth, Vector<PairText> pairTexts) {
            if (mFuriganaText != null) {
                return null;
            }

            PairText pairText = mJText.split(width, maxWidth, pairTexts, true);
            return pairText;
        }

        public PairText split(float width, float maxWidth, Vector<PairText> pairTexts, boolean isAdd) {
            if (mFuriganaText != null) {
                return null;
            }

            PairText pairText = mJText.split(width, maxWidth, pairTexts, isAdd);
            return pairText;
        }

        public void onDraw(Canvas canvas, float x, float y) {
            if (mFuriganaText == null) {
                mJText.onDraw(canvas, x, y);
            } else {
                float normalX = x + (mWidth - mJText.mWidth) / 2;
                mJText.onDraw(canvas, normalX, y);

                float furiganaX = x + (mWidth - mFuriganaText.mWidth) / 2;
                float furiganaY = y - mJText.mHeight;
                mFuriganaText.onDraw(canvas, furiganaX, furiganaY);
            }
        }

        public boolean contain(int x, int y) {
            return mPairRect.contains(x, y);
        }

        @Override
        public String toString() {
            return "PairText{" +
                    "mPairRect=" + mPairRect +
                    ", mJText=" + mJText +
                    ", mFuriganaText=" + mFuriganaText +
                    ", mWidth=" + mWidth +
                    ", isBreak=" + isBreak +
                    ", index=" + index +
                    '}';
        }
    }

    private class JText {
        String mText;
        RubyTextModel rubyTextModel = new RubyTextModel();
        int mType;
        float mWidth;
        float mHeight;
        float[] mWidthCharArray;
        TextPaint mTextPaint;
        int index;

        public JText(final String text, final int type, final int index) {
            this(text, null, null, type, index);
        }

        public JText(final String text, final String content, final String html, final int type, final int index) {
            log( "JText - text=" + text + " - type=" + type + " - index=" + index);
            mText = text;
            // remove last space
            if (!Utils.isEmpty(mText)) {
                if (Character.isWhitespace(mText.charAt(mText.length() - 1))) {
                    mText = mText.substring(0, mText.length() - 1);
                }
            }
            this.index = index;
            if (isBaseContent) {
                rubyTextModel.setBaseContent(baseText);
            }
            rubyTextModel.parserHtml(content, html);
            rubyTextModel.setKnownPronounceMeaning(isKnownPronounceMeaning);
            mType = type;
            // add first space when new char
            if (index > 0 && !Utils.isEmpty(mText) && !((mType & TYPE_FURIGANA) == TYPE_FURIGANA) && rubyTextModel.getVocaId() != 0) {
                mText = Constant.RUBY.KEY.SPACE + mText;
                log( "add space   mText=" + mText);
            }

            if(showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
                mText = "";
            } else if (showAsterisk == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY && !((mType & TYPE_MEANING) == TYPE_MEANING)) {
                if (!patternBr.matcher(mText).matches()) {
                    String[] splitInput = mText.split(" ");
                    StringBuilder output = new StringBuilder();
                    for (String aSplitInput : splitInput) {
                        for (int i = 0; i < aSplitInput.length(); i++) {
                            String aChar = aSplitInput.substring(i, i + 1);
                            if (patternAsterisk.matcher(aChar).matches()) {
                                output.append(aChar);
                            } else {
                                if (rubyTextModel.isKnown() && rubyTextModel.isKnownPronounce()) {
                                    output.append("-");
                                } else {
//                                    if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
//                                        output.append("*");
//                                    } else {
                                        output.append(aChar);
//                                    }
                                }
                            }
                        }
                        output.append(" ");
                    }
                    mText = output.toString();
                }
            }

            mWidthCharArray = new float[mText.length()];

            if ((mType & TYPE_FURIGANA) == TYPE_FURIGANA) {
                mFuriganaTextPaint.getTextWidths(mText, mWidthCharArray);
                mHeight = mFuriganaTextPaint.descent() - mFuriganaTextPaint.ascent();
                mTextPaint = mFuriganaTextPaint;
            } else if ((mType & TYPE_NORMAL) == TYPE_NORMAL) {
                mNormalTextPaint.getTextWidths(mText, mWidthCharArray);
                mHeight = mNormalTextPaint.descent() - mNormalTextPaint.ascent();
                mTextPaint = mNormalTextPaint;
            } else if ((mType & TYPE_MEANING) == TYPE_MEANING) {
                mMeaningTextPaint.getTextWidths(mText, mWidthCharArray);
                mHeight = mMeaningTextPaint.descent() - mMeaningTextPaint.ascent();
                mTextPaint = mMeaningTextPaint;
            }

            mWidth = 0;
            for (int i = 0; i < mWidthCharArray.length; i++) {
                log( "mWidthCharArray[i]=" + mWidthCharArray[i]);
                mWidth += mWidthCharArray[i];
            }
            log( "mWidth=" + mWidth);
        }

        public void onDraw(Canvas canvas, float x, float y) {
            if (TextUtils.isEmpty(mText)) {
                return;
            }
            if ((mType & TYPE_FURIGANA) == TYPE_FURIGANA) {
                if (isKnownPronounceMeaning && rubyTextModel.isKnownPronounce())
                    return;
                if (rubyTextModel.isKnown() && rubyTextModel.isKnownPronounce())
                    return;
            }
            log( "onDraw - mText=" + mText);
            mTextPaint.setColor(getColorText());
            mBackgroundPaint.setColor(getColorBackground());
            Rect background = getTextBackgroundSize(x, y, mText, mTextPaint);
            if (isShowOutline) {
                if ((mType & TYPE_NORMAL) == TYPE_NORMAL) {
                    canvas.drawText(mText, 0, mText.length(), x, y, mNormalTextPaintOutline);
                } else if ((mType & TYPE_MEANING) == TYPE_MEANING) {
                    canvas.drawText(mText, 0, mText.length(), x, y, mMeaningTextPaintOutline);
                }
            }
            canvas.drawRect(background, mBackgroundPaint);
            canvas.drawText(mText, 0, mText.length(), x, y, mTextPaint);
        }

        private int getColorText() {
            log( "getColorText - mText=" + mText);
//            log( "getColorText - " + rubyTextModel.toString());
            if (!isShowOutline() && (mType & TYPE_MEANING) == TYPE_MEANING) {
                return getColorMeaningText();
            }
            if (isKnownPronounceMeaning) {
                if (rubyTextModel.isKnown()) {
                    if (!rubyTextModel.isKnownPronounce()) {
                        return getUnknownPronounceColorText();
                    }
                    return getDefaultColorText();
                }
                return getUnknownMeaningColorText();
            }

            if (rubyTextModel.getVocaId() != 0) {
                if (rubyTextModel.isKnown()) {
                    if (!rubyTextModel.isKnownPronounce()) {
                        return getUnknownPronounceColorText();
                    }
                    return getDefaultColorText();
                }
                return getUnknownMeaningColorText();
            }
            return getDefaultColorText();
        }

        private int getDefaultColorText() {
            return ContextCompat.getColor(getContext(), isShowOutline ? R.color.white : R.color.color_ruby_text_normal);
        }

        private int getUnknownMeaningColorText() {
            return ContextCompat.getColor(getContext(), R.color.color_ruby_text_unknown_meaning);
        }

        private int getUnknownPronounceColorText() {
            return ContextCompat.getColor(getContext(), R.color.color_ruby_text_unknown_pronounce);
        }

        private int getColorBackground() {
            if (!((mType & TYPE_FURIGANA) == TYPE_FURIGANA) && rubyTextModel.isBookmark()) {
                return ContextCompat.getColor(getContext(), R.color.color_ruby_text_bookmark);
            }
            return getColorBackgroundDefault();
        }

        private int getColorBackgroundDefault() {
            return ContextCompat.getColor(getContext(), R.color.transparent);
        }

        private int getColorMeaningText() {
            return ContextCompat.getColor(getContext(), R.color.color_text);
        }

        private @NonNull
        Rect getTextBackgroundSize(float x, float y, @NonNull String text, @NonNull TextPaint paint) {
            log( "getTextBackgroundSize - text=" + text);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float halfTextLength = paint.measureText(text) / 2 + 5;
            return new Rect((int) (x - Constant.RUBY.KEY.SPACE.length()), (int) (y + fontMetrics.top), (int) (x + (halfTextLength * 2)), (int) (y + fontMetrics.bottom));
        }

        public PairText split(float width, float maxWidth, Vector<PairText> pairTexts, boolean isAdd) {
            log( "split - width=" + width + " -maxWidth=" + maxWidth);
            int i = 0;
            for (; i < mWidthCharArray.length; i++) {
                width += mWidthCharArray[i];
                if (width < maxWidth) {
                    continue;
                } else {
                    width -= mWidthCharArray[i];
                    i--;
                    break;
                }
            }
            log( "split - i=" + i);
            if (i <= 0) {
                return new PairText(new JText(mText, mType, pairTexts.size()));
            } else {
                String newText = mText.substring(0, i);
                PairText pairText1 = new PairText(new JText(newText, mType, pairTexts.size()));
                if (isAdd) {
                    pairTexts.add(pairText1);
                }
                log( "newText=" + newText + " i=" + i + " - mText.length()=" + mText.length());
                if (i == mText.length()) {
                    return null;
                } else {
                    String newText1 = mText.substring(i);
                    PairText result = new PairText(new JText(newText1, mType, pairTexts.size()));
                    log( "newText1=" + newText1);
                    return result;
                }
            }
        }

        @Override
        public String toString() {
            return "JText{" +
                    "mText='" + mText + '\'' +
                    ", rubyTextModel=" + rubyTextModel.toString() +
                    ", mType=" + mType +
                    ", mWidth=" + mWidth +
                    ", mHeight=" + mHeight +
                    ", mWidthCharArray=" + Arrays.toString(mWidthCharArray) +
                    ", mTextPaint=" + mTextPaint +
                    '}';
        }
    }

    private class FuriganaText extends JText {

        public FuriganaText(final int index) {
            super(Constant.BASE_BLANK, null, Constant.BASE_BLANK, TYPE_FURIGANA, index);
        }
        public FuriganaText(final String text, final String html, final int index) {
            super(text, null, html, TYPE_FURIGANA, index);
        }
    }

    public interface OnTextSelectedListener {
        void onTextSelected(String text, RubyTextModel rubyTextModel);

        void onDoubleClick(String text, RubyTextModel rubyTextModel);
    }

    public boolean isTutor() {
        return isTutor;
    }

    public void setTutor(boolean tutor) {
        isTutor = tutor;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    private String addItemList(String data, int value) {
        return addItemList(data, String.valueOf(value));
    }

    private String addItemList(String data, String value) {
        if (!Utils.isEmpty(value)) {
            if (!Utils.isEmpty(data)) {
                data += ",";
            }
            data += value;
        }
        return data;
    }

    public void setBaseContent(boolean baseContent) {
        isBaseContent = baseContent;
    }

    public boolean isShowMeaningJPCN() {
        return isShowMeaningJPCN;
    }

    public void setShowMeaningJPCN(boolean showMeaningJPCN) {
        isShowMeaningJPCN = showMeaningJPCN;
    }

    public boolean isShowOutline() {
        return isShowOutline;
    }

    public void setShowOutline(boolean showOutline) {
        isShowOutline = showOutline;
    }

    public void setDisplayPronunciation(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    private void log(String msg) {
        if (DEBUG) {
            DLog.d(TAG, msg);
        }
    }

    public int getShowAsterisk() {
        return showAsterisk;
    }

    public void setShowAsterisk(int showAsterisk) {
        this.showAsterisk = showAsterisk;
        if (!Utils.isEmpty(getBaseText())) {
            resetText();
            setJText(getBaseText(), getMeaningText());
        }
    }

    public boolean isTextCenter() {
        return isTextCenter;
    }

    public void setTextCenter(boolean textCenter) {
        isTextCenter = textCenter;
    }

    public String getBaseText() {
        return baseText;
    }

    public void setBaseText(String baseText) {
        this.baseText = baseText;
    }

    public String getMeaningText() {
        return meaningText;
    }

    public void setMeaningText(String meaningText) {
        this.meaningText = meaningText;
    }
}