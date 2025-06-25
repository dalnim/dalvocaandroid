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
import com.dalread.util.BaseVoca;
import com.dalread.util.BaseVocaKnow;
import com.dalread.util.Constant;
import com.dalread.util.DLog;
import com.dalread.util.FuriganaUtil;
import com.dalread.util.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaseFuriganaView extends AppCompatTextView {
    protected static final String TAG = BaseFuriganaView.class.getSimpleName();
    protected static final boolean DEBUG = false;
    protected static final int TIME_LONG_CLICK = 1000;
    protected static final int TYPE_NORMAL = 1;
    //    protected static final int TYPE_BOLDER = 2;
//    protected static final int TYPE_ITALIC = 4;
    protected static final int TYPE_MEANING = 5;
    protected static final int TYPE_FURIGANA = 2;
    protected static final String KANJI_REGEX = "(([{])" +
            "(.*?)([#])" +
            "(.*?)([#])" +
            "(.*?)([}])" +
            ")";
    //    protected static final String BOLD_TEXT_REGEX = "(?m)(?d)(?s)(([<][b][>])(.*?)([<][\\/][b][>]))";
//    protected static final String ITALIC_TEXT_REGEX = "(?m)(?d)(?s)(([<][i][>])(.*?)([<][\\/][i][>]))";
    public static final Pattern patternAsterisk = Pattern.compile(Constant.PATTERN_SPECIAL_CHARACTERS);
    public static final Pattern patternBr = Pattern.compile(Constant.RUBY.KEY.BREAK_REGEX);

    protected int mWidthSize;
    protected String mText;
    protected String meaningText;
    protected Vector<BaseFuriganaView.Line> mLines;
    protected Vector<BaseFuriganaView.PairText> mAllTexts;
    protected TextPaint mBackgroundPaint; // Background paint of text.
    protected TextPaint mNormalTextPaint;
    protected TextPaint mNormalTextPaintOutline; //Like Subtitle on full screen mode, it needs outline (To give shadow)
    protected TextPaint mFuriganaTextPaint;
    //    protected TextPaint mBoldTextPaint;
//    protected TextPaint mItalicTextPaint;
//    protected TextPaint mBoldItalicTextPaint;
    protected TextPaint mMeaningTextPaint; // For Difficult word and meaning above text??? (문장위에 어려운 단어와 뜻을 표시???)
    protected TextPaint mMeaningTextPaintOutline; // To use on Full Screen mode
//    protected float mLineHeight; //If the value is big then above of text area is getting bigger. (각이 크면 후리가나 위쪽의 공간이 늘어난다. 값이 작아지면 후리가나가 있으면 잘리기도 한다.)
    protected float mMaxLineWidth;
    protected float mLineSpacing; // line space above of furigana and below of furigana. (후리가나의 위의 간격을 늘리거나 줄여줌)
    protected String textClicked = null;
    protected float mTouchX, mTouchY;
    protected BaseFuriganaView.OnTextSelectedListener mOnTextSelectedListener;
    // Double click
    int numberOfTaps = 0;
    boolean isDoubleClick = false;
    final Handler mHandler = new Handler();
    protected SharedPreferencesDB mSharedPref;
    protected boolean isKnownPronounceMeaning; // true shows pronounce, false shows meaning //한글 : true이면 발음을 보여주고, false이면 뜻을 보여줌

    protected boolean isTutor; // Hide meaning and pronouce when isTutor = true. //튜터일때는 hurigana를 보여주지 않는다. isShowFurigana와 겹침 나중에 하나를 없애야 함.
    // [Lesson] Japanese&Chinese : Don’t show meaning, but show pronunciation.
    protected boolean isShowMeaningJPCN = false;
    // using DalPlayer when value is true
    protected String baseText = Constant.BASE_BLANK;
    protected boolean isBaseContent;
    protected String listId, listType;
    protected final int TEXT_BONUS = 15;
    // change color when playing DalPlayer
    protected boolean isShowOutline = false; //Subtitle on Full Screen, show outline to read subtitle easier
    protected boolean isUseHighLightColorForUnknownWord = true; //Use red for unkown color or not (In AraHanja's book use black color in the difficult words view)
    // https://github.com/dalnim/IssueOnly/issues/95
    protected boolean isWord;
    // https://github.com/dalnim/IssueOnly/issues/93
    protected boolean displayPronunciation = true;
    // https://github.com/dalnim/IssueOnly/issues/104
    protected int showAsterisk = Constant.SHOW_ASTERISK.SHOW_SENTENCE;
    protected boolean isTextCenterHorizontal; // Subtitle on Full Screen, it's true and make the alignment to center.
    protected HashMap<Integer, RubyTextModel> listRubyText = new HashMap<>(); //Has voca's know value and other info.
    protected int minLines = -1;
    protected boolean isHideSubtitleFromAsterisk = true;
    protected boolean isSetColorOnSentence = false;
    protected int vocaKnow_Sentence = Constant.VOCA_KNOW.VOCA_KNOW_NOTRATED;
    protected int isShowFurigana = Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY;
    private boolean isNormalTextView = false; //true has no furigana, it's used in the Search subtitle. (자막 검색시 키워드 색을 따로 표시하기 위해 후리가나기능을 안쓰게 할려고)
    private boolean isLongClick = false;
    protected Runnable mLongClickRunnable = new Runnable() {
        @Override
        public void run() {
            performLongClick();
        }
    };

    public BaseFuriganaView(Context context) {
        super(context);
        initialize(context, null);
    }

    public BaseFuriganaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public BaseFuriganaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs);
    }

    public void setOnTouchListener(boolean isOnTouchListener) {
        setOnTouchListener(isOnTouchListener ? onTouchListener : null);
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        updateTextSize(getTextSize());
    }

    protected void updateTextSize(float size) {
        mBackgroundPaint.setTextSize(size);
        mNormalTextPaint.setTextSize(size);
        mNormalTextPaintOutline.setTextSize(size);
        setTextSizeOfmFuriganaTextPaint(size);
        mMeaningTextPaint.setTextSize(mFuriganaTextPaint.getTextSize() - 5);
        mMeaningTextPaintOutline.setTextSize(mMeaningTextPaint.getTextSize());
//        mLineSpacing = (int)(size * 0.2) ; // I think this code is useless
//        updateLineHeight();
    }
    //Make furigana's font size smaller than text.
    protected void setTextSizeOfmFuriganaTextPaint(float size) {
        mFuriganaTextPaint.setTextSize(size - 1);
    }

    protected void initialize(Context context, AttributeSet attrs) {
        setOnTouchListener(true);
        mSharedPref = SharedPreferencesDB.getInstance(context);
        isKnownPronounceMeaning = BaseVoca.checkStudyLanguageJPCN(mSharedPref);
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

        mMeaningTextPaintOutline = new TextPaint(textPaint);
        mMeaningTextPaintOutline.setStyle(Paint.Style.STROKE);
        mMeaningTextPaintOutline.setColor(ContextCompat.getColor(getContext(), R.color.color_ruby_text_normal));
        mMeaningTextPaintOutline.setAntiAlias(true);
        mMeaningTextPaintOutline.setStrokeWidth(5);

        updateTextSize(textPaint.getTextSize());


        if (attrs == null) {
            mLineSpacing = Constant.RUBY.DEFAULT_LINE_SPACE;
        } else {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.FuriganaView, 0, 0);
            try {
                mText = typedArray.getString(R.styleable.FuriganaView_jText);
                mLineSpacing = typedArray.getInteger(R.styleable.FuriganaView_line_spacing, getFuriganaView_line_spacing());
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
//        updateLineHeight();
        if (!TextUtils.isEmpty(mText)) {
            setJText();
        }
    }

    protected int getFuriganaView_line_spacing() {
        return Constant.RUBY.DEFAULT_LINE_SPACE;
    }

    protected float updateLineHeight() {
        return mFuriganaTextPaint.getFontSpacing()
                + Math.max(mNormalTextPaint.getTextSize(),
                mMeaningTextPaint.getTextSize())
                + getMLineSpacingAgain(); //mLineSpacing;
    }

    private float getMLineSpacingAgain() {
        return getFuriganaView_line_spacing();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        log("onMeasure");
        if (isNormalTextView) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
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

    protected int getHeightRuby() {
        float y = 0;
        if (mLines.size() > 0) {
            float height = 0;
            for (int i = 0; i < mLines.size(); i++) {
                BaseFuriganaView.Line line = mLines.get(i);
                if (line.mWidth <= 0)
                    continue;
//                boolean isShowMeaning = false;
//                for (int j = 0; j < line.mPairTexts.size(); j++) {
//                    BaseFuriganaView.PairText pairText = line.mPairTexts.get(j);
//                    if (!isShowMeaning) {
//                        if (isTutor) {
//                            isShowMeaning = false;
//                        } else {
//                            isShowMeaning = pairText.mJText.rubyTextModel.isShowMeaning() && pairText.mJText.rubyTextModel.isHasMeaning();
//                        }
//                    }
//
//                }
//                height = isShowMeaning ? mLineHeight : mLineHeight / 2;
                y += getLineHeight(line);;
            }
        }
        return (int) y;
    }

    /***
     * Measure view with max width.
     * @param width if width < 0 → the view has one line, which has width unlimited.
     */
    protected void measureText(int width) {
        log( "measureText - width=" + width);
        mLines.clear();
        mMaxLineWidth = 0;

        if (width <= 0) {
            BaseFuriganaView.Line line = new BaseFuriganaView.Line();
            line.setPairTexts(mAllTexts);
            for (int i = 0; i < mAllTexts.size(); i++) {
                mMaxLineWidth += mAllTexts.get(i).mWidth;
            }
        } else {
            float widthTemp = 0;
            Vector<BaseFuriganaView.PairText> pairTexts = new Vector<>();
            for (int i = 0; i < mAllTexts.size(); i++) {
                BaseFuriganaView.PairText pairText = mAllTexts.get(i);
                log( "easureText" + pairText.toString());
                // Break to new line if {@PairText} contain break character.
//                if (pairText.isBreak) {
//                    BaseFuriganaView.Line line = new BaseFuriganaView.Line();
//                    line.setPairTexts(pairTexts);
//                    mLines.add(line);
//
////                    //이게 있으면. 리턴키를 두개를 추가한다. 한개만 추가해야 하는데...
//                        Vector<BaseFuriganaView.PairText> pt1 = new Vector<>();
//                        pt1.add(new BaseFuriganaView.PairText(new BaseFuriganaView.JText("isBreak dalnim", TYPE_NORMAL, 0)));
//                        BaseFuriganaView.Line l1 = new BaseFuriganaView.Line();
//                        l1.setPairTexts(pt1);
//                        mLines.add(l1);
//
//                    mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
//                    // Reset for new line
//                    pairTexts = new Vector<>();
//                    widthTemp = 0;
//                    continue;
//                }
//                if ((pairText.mJText != null)
//                        && (pairText.mJText.mText != null)
//                        && StringUtils.startsWithAny(pairText.mJText.mText.trim(), new String[] {"<br>", "<br/>", "<br />"})) {
//                    BaseFuriganaView.Line line = new BaseFuriganaView.Line();
//                    line.setPairTexts(pairTexts);
//                    mLines.add(line);
//
////                    //이게 있으면. 리턴키를 두개를 추가한다. 한개만 추가해야 하는데...
//                        Vector<BaseFuriganaView.PairText> pt1 = new Vector<>();
//                        pt1.add(new BaseFuriganaView.PairText(new BaseFuriganaView.JText("mJText dalnim", TYPE_NORMAL, 0)));
//                        BaseFuriganaView.Line l1 = new BaseFuriganaView.Line();
//                        l1.setPairTexts(pt1);
//                        mLines.add(l1);
//                    mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
//                    // Reset for new line
//                    pairTexts = new Vector<>();
//                    widthTemp = 0;
//                    continue;
//                }
                if (pairText.isBreak ||
                        StringUtils.startsWithAny(pairText.mJText.mText.trim(), new String[] {"<br>", "<br/>", "<br />"})) {
                    BaseFuriganaView.Line line = new BaseFuriganaView.Line();
                    line.setPairTexts(pairTexts);
                    mLines.add(line);
                    addNewLineBreak();
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
                        BaseFuriganaView.Line line = new BaseFuriganaView.Line();
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
                            BaseFuriganaView.PairText splitPairText = pairText.split(widthTemp, width, pairTexts);

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
                                line = new BaseFuriganaView.Line();
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
                        BaseFuriganaView.Line line = new BaseFuriganaView.Line();
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
                            BaseFuriganaView.PairText splitPairText = pairText.split(widthTemp, width, pairTexts, false);
                            log( "measureText - kanji is null");
                            if (splitPairText == null) {
                                log( "measureText - splitPairText is null");
                                widthTemp = pairText.mWidth;
                                if (i == (mAllTexts.size() - 1)) {
                                    line = new BaseFuriganaView.Line();
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
                    BaseFuriganaView.Line line = new BaseFuriganaView.Line();
                    line.setPairTexts(pairTexts);
                    mLines.add(line);
                    mMaxLineWidth = mMaxLineWidth > widthTemp ? mMaxLineWidth : widthTemp;
                }
            }
        }
        // check max lines
        int index = 0;
        while (mLines.size() < getMinLines()) {
            Vector<BaseFuriganaView.PairText> pt1 = new Vector<>();
            pt1.add(new BaseFuriganaView.PairText(new BaseFuriganaView.JText(Constant.BREAK_CHARACTER, TYPE_NORMAL, index)));
            BaseFuriganaView.Line l1 = new BaseFuriganaView.Line();
            l1.setPairTexts(pt1);
            mLines.add(index, l1);
            index++;
        }
    }
    //아라 한자에서 빈줄을 추가한다. 이게 없을때는 빈줄이 안보여서 갑갑했다.
    protected void addNewLineBreak() {

    }

    private void calculateLineRectAndPairText() {
        float y = 0;
        float height;
        for (int i = 0; i < mLines.size(); i++) {
            BaseFuriganaView.Line line = mLines.get(i);
            if (line.mWidth <= 0)
                continue;
            height = getLineHeight(line);
            y += height;

            int left = isTextCenterHorizontal() ? (int) ((mWidthSize - line.mWidth) / 2) : 0;
            line.mLineRect = new Rect(left, (int) (y - height), (int) line.mWidth, (int) y);
            float x = isTextCenterHorizontal() ? line.mLineRect.left : 0;

            for (int j = 0; j < line.mPairTexts.size(); j++) {
                BaseFuriganaView.PairText pairText = line.mPairTexts.get(j);
                Rect pairRect = new Rect((int) x, (int) (y - height), (int) (x + pairText.mWidth), (int) y);
                pairText.mPairRect = pairRect;
                x += pairText.mWidth;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mLines.size() > 0) {
            float y = 0;
            float height;
            for (int i = 0; i < mLines.size(); i++) {
                BaseFuriganaView.Line line = mLines.get(i);
                if (line.mWidth <= 0)
                    continue;
                height = getLineHeight(line);
                y += height;

                log( "onDraw - y=" + y + " - mMaxLineWidth=" + mMaxLineWidth);
                //Dalnim : // I can't click the left most Hanja In AraHanja(Not center aligned) so set to 0 if it's not isTextCenterHorizontal
                //Rect rect = new Rect((int) ((mWidthSize - line.mWidth) / 2), (int) (y - height), (int) line.mWidth, (int) y);
                int left = isTextCenterHorizontal() ? (int) ((mWidthSize - line.mWidth) / 2) : 0;
                Rect rect = new Rect(left, (int) (y - height), (int) line.mWidth, (int) y);
                // TEST - add background color to React
//                if (BuildConfig.DEBUG && BuildConfig.FLAVOR.equals(EnumFlavor.ARAHANJA.getName())) {
//                    mBackgroundPaint.setColor(Color.CYAN);
//                    canvas.drawRect(rect, mBackgroundPaint);
//                }
                //----------
                line.mLineRect = rect;
                float x = isTextCenterHorizontal() ? line.mLineRect.left : 0;

                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    BaseFuriganaView.PairText pairText = line.mPairTexts.get(j);
                    Rect pairRect = new Rect((int) x, (int) (y - height), (int) (x + pairText.mWidth), (int) y);
                    //For Debug
                    //return BuildConfig.DEBUG && BuildConfig.FLAVOR.equals(EnumFlavor.ARAHANJA.getName());
//                    if (BaseVoca.isAraHanjaDebugMode()) {
//                        int randomColor= ((int)(Math.random()*16777215)) | (0xFF << 24);
//                        mBackgroundPaint.setColor(randomColor);
//                        canvas.drawRect(pairRect, mBackgroundPaint);
//                    }
                    log( "onDraw - pairRect=" + pairRect.toString());
                    pairText.mPairRect = pairRect;
                    Paint.FontMetrics metrics = pairText.mJText.mTextPaint.getFontMetrics();
//                    pairText.onDraw(canvas, x, pairRect.bottom - mLineSpacing); //If I don't minus mLineSpacing then the bottom of main text(Not Ruby) is cut
                    pairText.onDraw(canvas, x, pairRect.bottom - 20); //If I don't minus mLineSpacing then the bottom of main text(Not Ruby) is cut, and hardcoding is better, Because mLineSpacing affects the above area and changable.
                    x += pairText.mWidth;
                }
            }
        } else {
            super.onDraw(canvas);
        }
    }

    private float getLineHeight(Line line) {
        float height;
        boolean isShowMeaning = false;
        for (int j = 0; j < line.mPairTexts.size(); j++) {
            if (isShowMeaning) {
                break;
            }
            PairText pairText = line.mPairTexts.get(j);
            if (!isShowMeaning) {
                if (isTutor) {
                    isShowMeaning = false;
                } else {
                    isShowMeaning = pairText.mJText.rubyTextModel.isShowMeaning() && pairText.mJText.rubyTextModel.isHasMeaning();
                }
            }
            if (isSHOW_FURIGANA_ALL()) {
                isShowMeaning = true;
            }


        }
        //If the value is big then above of text area is getting bigger. (각이 크면 후리가나 위쪽의 공간이 늘어난다. 값이 작아지면 후리가나가 있으면 잘리기도 한다.)
        float lineHeight = updateLineHeight();
        height = isShowMeaning ? lineHeight : lineHeight / 2;
        return height;
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
    protected void setJText() {
        log( "setJText");
        mLines.clear();
        mText = replaceRuby(mText);
//        mText = mText.replaceAll(Constant.BREAK_REGEX, Constant.BREAK_CHARACTER);
        if (!Utils.isEmpty(meaningText)) {
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

    protected String replaceRuby(final String textWithRuby) {
        String textWithRubyParsed = FuriganaUtil.checkAndParserRubyText(textWithRuby);
        log( "textWithRuby parsed=" + textWithRubyParsed);
        String parsed;
        parsed = textWithRubyParsed.replace(Constant.RUBY.KEY.RUBY_OPEN, Constant.RUBY.KEY.KEY_REPLACE);
        parsed = parsed.replace(Constant.RUBY.KEY.RT_OPEN, Constant.RUBY.KEY.KEY_REPLACE);
        parsed = parsed.replace(Constant.RUBY.KEY.RT_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RB_OPEN, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RB_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.SPAN_OPEN_CHECK, "{<span");
        parsed = parsed.replace(Constant.RUBY.KEY.SPAN_CLOSE, Constant.BASE_BLANK);
        parsed = parsed.replace(Constant.RUBY.KEY.RUBY_CLOSE, "}");
        log( "parse=" + parsed);
        int noOccurence = parsed.split("<span", -1).length - 1;
//        isWord = noOccurence == 1; //Dalnim : Don't check isWord here but use "setIsWord" method.
        log( "parse=noOccurence=" + noOccurence + " - isWord=" + isWord);
        return parsed;
    }

    protected void parseText(String text) {
        parseText(text, TYPE_NORMAL);
    }

    /**
     * Parse text with struct {kanji;furigana}
     *
     * @param text text to parse
     * @param type 4 type for display. bold, italic, bold-italic and normal.
     */
    protected void parseText(String text, int type) {
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

            BaseFuriganaView.JText kanjiText = new BaseFuriganaView.JText(kanji, mText, html, type, mAllTexts.size());
            if (isWord &&
                    !Utils.isEmpty(kanjiText.rubyTextModel.getPronounce()) &&
                    (!kanjiText.rubyTextModel.isKnown() || !kanjiText.rubyTextModel.isKnownPronounce())) {
                kanjiText = new BaseFuriganaView.JText(kanji + (displayPronunciation ? " [" + kanjiText.rubyTextModel.getPronounce() + "]" : ""), mText, html, type, mAllTexts.size());
            }
            if (kanjiText.rubyTextModel.getVocaId() > 0) {
                setListId(addItemList(listId, kanjiText.rubyTextModel.getVocaId()));
                setListType(addItemList(listType, kanjiText.rubyTextModel.getVocaType()));
            }
            BaseFuriganaView.FuriganaText furiganaText = null;
            if (!isTutor) {
                if (isKnownPronounceMeaning) {
                    if (kanjiText.rubyTextModel.isShowMeaning() || isSHOW_FURIGANA_ALL()) {
                        furiganaText = new BaseFuriganaView.FuriganaText(furigana, html, mAllTexts.size());
                    }
                } else {
                    if (kanjiText.rubyTextModel.isShowMeaning()) {
                        furiganaText = new BaseFuriganaView.FuriganaText(kanjiText.rubyTextModel.getMeaning(), html, mAllTexts.size());
                    }
                }
            }
            BaseFuriganaView.PairText pairText = new BaseFuriganaView.PairText(kanjiText, furiganaText, mAllTexts.size());
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
    protected void parseBreakLineText(String text, int type) {
        if (text.contains(Constant.BREAK_CHARACTER)) {
            int breakIndex = text.indexOf(Constant.BREAK_CHARACTER);
            String firstText = text.substring(0, breakIndex);
            BaseFuriganaView.JText jText = new BaseFuriganaView.JText(firstText, type, mAllTexts.size());
            BaseFuriganaView.PairText pairText = new BaseFuriganaView.PairText(jText);
            mAllTexts.add(pairText);

            BaseFuriganaView.PairText breakPairText = new BaseFuriganaView.PairText();
            mAllTexts.add(breakPairText);

            String secondText = text.substring(breakIndex + Constant.BREAK_CHARACTER.length());
            parseBreakLineText(secondText, type);
        } else {
            BaseFuriganaView.JText jText = new BaseFuriganaView.JText(text, type, mAllTexts.size());
            BaseFuriganaView.PairText pairText = new BaseFuriganaView.PairText(jText, mAllTexts.size());
            //Dalnim : Don't use trim here, so I can add multiple spaces too.
            if (!Utils.isEmptyWithoutTrim(pairText.mJText.mText)) {
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
//        handleClickText();
        isLongClick = true;
        StringBuilder copiedText = new StringBuilder();
        for (PairText pairText : mAllTexts) {
            copiedText.append(pairText.mJText.mText);
        }
        Utils.copyToClipboard(getContext(), copiedText.toString(), getContext().getString(R.string.copied) + "\n" + copiedText);
        return super.performLongClick();
    }

//    public long getGap() {
//        return 500L; //500ms
//    }

    protected OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            final int action = event.getAction() & MotionEvent.ACTION_MASK;
            log( "onTouchEvent - action" + action);
            switch (action) {
                //Son's code
                //-------
                case MotionEvent.ACTION_DOWN:
                    //--------Dalnim : if I call postDelayed in AraHanja, when I scroll over text it copies.
                    //postDelayed(mLongClickRunnable, TIME_LONG_CLICK);
                    //--------
//                case MotionEvent.ACTION_MOVE:
                //-------
                //Dalnim : if I call postDelayed in AraHanja, it makes me click a Hanja when I scroll over Hanja furigana.
//                case MotionEvent.ACTION_DOWN:
                    break;
                //---------

                case MotionEvent.ACTION_MOVE:
                    mTouchX = event.getX();
                    mTouchY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    //--------Dalnim : if I call postDelayed in AraHanja, when I scroll over text it copies.
//                    removeCallbacks(mLongClickRunnable);
//                    if (isLongClick) {
//                        isLongClick = false;
//                        return true;
//                    }
                    //--------
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
                    isLongClick = false;
//                    removeCallbacks(mLongClickRunnable);
            }
            return true;
        }
    };

    protected boolean handleClickText() {
        if (mOnTextSelectedListener == null) return false;
        log( "mTouchX=" + mTouchX + " - mTouchY=" + mTouchY);
        boolean result = false;
        for (int i = 0; i < mLines.size(); i++) {
            BaseFuriganaView.Line line = mLines.get(i);
            if (line.contain((int) mTouchX, (int) mTouchY)) {
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    BaseFuriganaView.PairText pairText = line.mPairTexts.get(j);
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
                                    //Commented because when I double tap on a word in Hanja Classics book's content, the recycler view is moved sometimes.
                                    //https://dalnimsoft.slack.com/archives/CE4G02796/p1642127461065200
//                                    reDrawLayout(pairText.mJText.rubyTextModel.getContent());
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

    protected String getCurrentTextClicked() {
        calculateLineRectAndPairText(); //If I recalculate this again, some mLineRecat's y is 0 so can't click a text,
        for (int i = 0; i < mLines.size(); i++) {
            BaseFuriganaView.Line line = mLines.get(i);
            if (line.contain((int) mTouchX, (int) mTouchY)) {
                for (int j = 0; j < line.mPairTexts.size(); j++) {
                    BaseFuriganaView.PairText pairText = line.mPairTexts.get(j);
                    if (pairText.contain((int) mTouchX, (int) mTouchY)) {
                        return pairText.mJText.mText;
                    }
                }
                break;
            }
        }

        return null;
    }

    public void setOnTextSelectedListener(BaseFuriganaView.OnTextSelectedListener onTextSelectedListener) {
        this.mOnTextSelectedListener = onTextSelectedListener;
    }

    public void setIsKnownPronounceMeaning(boolean isKnownPronounceMeaning) {
        this.isKnownPronounceMeaning = isKnownPronounceMeaning;
    }

    protected class Line {
        protected Vector<BaseFuriganaView.PairText> mPairTexts;
        protected Rect mLineRect;
        protected float mWidth;

        public boolean contain(int x, int y) {
            return mLineRect != null && mLineRect.contains(x, y);
        }

        public void setPairTexts(Vector<BaseFuriganaView.PairText> pairTexts) {
            mPairTexts = pairTexts;
            measureWidth();
        }

        protected void measureWidth() {
            mWidth = 0;
            for (BaseFuriganaView.PairText pairText : mPairTexts) {
                mWidth += pairText.mWidth;
            }
        }
    }

    protected class PairText {
        Rect mPairRect;
        BaseFuriganaView.JText mJText;
        BaseFuriganaView.FuriganaText mFuriganaText;
        float mWidth;
        boolean isBreak = false;
        int index = 0;

        public PairText() {
            isBreak = true;
        }

        public PairText(final BaseFuriganaView.JText jText) {
            log( "PairText(JText jText)=" + index + " - jText=" + jText);
            mJText = jText;
            measureWidth();
        }

        public PairText(final BaseFuriganaView.JText jText, final int index) {
            log( "PairText(JText jText, int index)=" + index + " - jText=" + jText);
            mJText = jText;
            if (mAllTexts != null &&
                    mAllTexts.size() > 0 &&
                    mJText.rubyTextModel != null &&
                    mJText.rubyTextModel.getVocaId() <= 0) {
                final BaseFuriganaView.PairText temp = mAllTexts.get(index - 1);
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

        public PairText(final BaseFuriganaView.JText jText, final BaseFuriganaView.FuriganaText furiganaText, final int index) {
            log( "PairText(JText jText, FuriganaText furiganaText, int index)=" + index + " - jText=" + jText);
            mJText = jText;
            mFuriganaText = furiganaText;
            this.index = index;
            measureWidth();
        }

        protected void measureWidth() {
            if (mFuriganaText == null) {
                mWidth = mJText.mWidth;
            } else {
                mWidth = Math.max(mJText.mWidth, mFuriganaText.mWidth);
            }
        }

        public BaseFuriganaView.PairText split(float width, float maxWidth, Vector<BaseFuriganaView.PairText> pairTexts) {
            if (mFuriganaText != null) {
                return null;
            }

            BaseFuriganaView.PairText pairText = mJText.split(width, maxWidth, pairTexts, true);
            return pairText;
        }

        public BaseFuriganaView.PairText split(float width, float maxWidth, Vector<BaseFuriganaView.PairText> pairTexts, boolean isAdd) {
            if (mFuriganaText != null) {
                return null;
            }

            BaseFuriganaView.PairText pairText = mJText.split(width, maxWidth, pairTexts, isAdd);
            return pairText;
        }

        public void onDraw(Canvas canvas, float x, float y) {
            if (mFuriganaText == null) {
                mJText.onDraw(canvas, x, y);
            } else {
                float normalX = x + (mWidth - mJText.mWidth) / 2;
                mJText.onDraw(canvas, normalX, y);

                float furiganaX = x + (mWidth - mFuriganaText.mWidth) / 2;
                float furiganaY = mPairRect.bottom - mJText.mHeight;
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

    protected class JText {
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
            //Dalnim : I use isEmptyWithoutTrim in parseBreakLineText, so don't need to handle space here.
//            // remove last space
//            if (!Utils.isEmpty(mText)) {
//                if (Character.isWhitespace(mText.charAt(mText.length() - 1))) {
//                    mText = mText.substring(0, mText.length() - 1);
//                }
//            }
            this.index = index;
            if (isBaseContent) {
                rubyTextModel.setBaseContent(baseText);
            }
            rubyTextModel.parserHtml(content, html);
            if (listRubyText != null && listRubyText.containsKey(rubyTextModel.getVocaId())) {
                rubyTextModel.parserFromRubyTextModel(listRubyText.get(rubyTextModel.getVocaId()));
            }
            rubyTextModel.setKnownPronounceMeaning(isKnownPronounceMeaning);
            mType = type;
            //Dalnim : I use isEmptyWithoutTrim in parseBreakLineText, so don't need to handle space here.
//            // add first space when new char
//            if (index > 0 && !Utils.isEmpty(mText) && !((mType & TYPE_FURIGANA) == TYPE_FURIGANA) && rubyTextModel.getVocaId() != 0) {
//                mText = Constant.RUBY.KEY.SPACE + mText;
//                log( "add space   mText=" + mText);
//            }
            if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE && isHideSubtitleFromAsterisk()) {
                //If I have this code, then the item's height is shrinked when AraPlayer hides the subtitle in the table view.
                // set invisible view is enough, no need to reset text to keep height of item subtitle.
//                mText = Constant.BASE_BLANK;
            } else if (showAsterisk == Constant.SHOW_ASTERISK.SHOW_DIFFICULT_WORD_ONLY && !(isTypeMeaning())) {
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
            } else if (isTypeMeaning()) {
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

            //Dalnim : To display furigana by the if conditions.
            if ((mType & TYPE_FURIGANA) == TYPE_FURIGANA) {
                if (isSHOW_FURIGANA_OFF()) {
                    return;
                } else if (isSHOW_FURIGANA_DIFFICULT_WORDS_ONLY()) {
                    if (isKnownPronounceMeaning && rubyTextModel.isKnownPronounce())
                        return;
                    if (rubyTextModel.isKnown() && rubyTextModel.isKnownPronounce())
                        return;
                } else {

                }
            }
            log( "onDraw - mText=" + mText);
            mTextPaint.setColor(getColorText());
            mBackgroundPaint.setColor(getColorBackground());
            Rect background = getTextBackgroundSize(x, y, mText, mTextPaint);
            if (isShowOutline) {
                if ((mType & TYPE_NORMAL) == TYPE_NORMAL) {
                    canvas.drawText(mText, 0, mText.length(), x, y, mNormalTextPaintOutline);
                } else if (isTypeMeaning()) {
                    canvas.drawText(mText, 0, mText.length(), x, y, mMeaningTextPaintOutline);
                }
            }
            canvas.drawRect(background, mBackgroundPaint);
            canvas.drawText(mText, 0, mText.length(), x, y, mTextPaint);
        }

        protected int getColorText() {
            log( "getColorText - mText=" + mText);
            if (isTypeMeaning()) {
                if (isShowOutline()) {
                    return getColorMeaningTextWithShowOutline();
                } else {
                    return getColorMeaningText();
                }
            }

            if (rubyTextModel.getVocaId() == 0) {
                if (isSetColorOnSentence) {
                    return getSentenceKnowColorText();
                } else {
                    return getDefaultColorText();
                }
            } else {
                if (rubyTextModel.isKnown()) {
                    if (isSetColorOnSentence) {
                        return getSentenceKnowColorText();
                    }
                    if (!rubyTextModel.isKnownPronounce()) {
                        return getUnknownPronounceColorText();
                    }
                    return getDefaultColorText();
                }
                return getUnknownMeaningColorText();
            }
        }

        protected boolean isTypeMeaning() {
            return (mType & TYPE_MEANING) == TYPE_MEANING;
        }

        protected int getSentenceKnowColorText() {
            if (BaseVocaKnow.isKnown(vocaKnow_Sentence)) {
                return ContextCompat.getColor(getContext(), R.color.color_ruby_text_known_sentence);
            } else if (BaseVocaKnow.isAmkiGrade1(vocaKnow_Sentence)) {
                return ContextCompat.getColor(getContext(), R.color.color_ruby_text_amki_grade_1_sentence);
            } else if (BaseVocaKnow.isAmkiGrade2(vocaKnow_Sentence)) {
                return ContextCompat.getColor(getContext(), R.color.color_ruby_text_amki_grade_2_sentence);
            }
            return getDefaultColorText();
        }

//        protected int getSentenceHideColorText() {
//            return ContextCompat.getColor(getContext(), R.color.color_ruby_text_hide_sentence);
//        }

        protected int getDefaultColorText() {
            return ContextCompat.getColor(getContext(), getDefaultColorTextByOutline());
        }

        private int getDefaultColorTextByOutline() {
            return isShowOutline ? R.color.rubyTextNormalColor : R.color.rubyTextNormalInTableColor;
        }

        protected int getUnknownMeaningColorText() {
            return ContextCompat.getColor(getContext(), isUseHighLightColorForUnknownWord() ?
                    R.color.color_ruby_text_unknown_meaning
                    : getDefaultColorTextByOutline());
        }

        protected int getUnknownPronounceColorText() {
            return getDefaultColorText();
            // TODO : Dalnim : I will use UnknownPronounce later.
//            return ContextCompat.getColor(getContext(), R.color.color_ruby_text_unknown_pronounce);
        }

        protected int getColorBackground() {
            if (!((mType & TYPE_FURIGANA) == TYPE_FURIGANA) && rubyTextModel.isBookmark()) {
                return ContextCompat.getColor(getContext(), R.color.color_ruby_text_bookmark);
            }
            return getColorBackgroundDefault();
        }

        protected int getColorBackgroundDefault() {
            return ContextCompat.getColor(getContext(), R.color.transparent);
        }

        protected int getColorMeaningText() {
            return ContextCompat.getColor(getContext(), R.color.rubyTextDifficultWordAndMeaningInTableColor);
        }

        protected int getColorMeaningTextWithShowOutline() {
            return ContextCompat.getColor(getContext(), R.color.rubyTextDifficultWordAndMeaningColor);
        }

        protected @NonNull
        Rect getTextBackgroundSize(float x, float y, @NonNull String text, @NonNull TextPaint paint) {
            log( "getTextBackgroundSize - text=" + text);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float halfTextLength = paint.measureText(text) / 2 + 5;
            return new Rect((int) (x - Constant.RUBY.KEY.SPACE.length()), (int) (y + fontMetrics.top), (int) (x + (halfTextLength * 2)), (int) (y + fontMetrics.bottom));
        }

        public BaseFuriganaView.PairText split(float width, float maxWidth, Vector<BaseFuriganaView.PairText> pairTexts, boolean isAdd) {
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
                return new BaseFuriganaView.PairText(new BaseFuriganaView.JText(mText, mType, pairTexts.size()));
            } else {
                String newText = mText.substring(0, i);
                BaseFuriganaView.PairText pairText1 = new BaseFuriganaView.PairText(new BaseFuriganaView.JText(newText, mType, pairTexts.size()));
                if (isAdd) {
                    pairTexts.add(pairText1);
                }
                log( "newText=" + newText + " i=" + i + " - mText.length()=" + mText.length());
                if (i == mText.length()) {
                    return null;
                } else {
                    String newText1 = mText.substring(i);
                    BaseFuriganaView.PairText result = new BaseFuriganaView.PairText(new BaseFuriganaView.JText(newText1, mType, pairTexts.size()));
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

    protected class FuriganaText extends BaseFuriganaView.JText {

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

    protected String addItemList(String data, int value) {
        return addItemList(data, String.valueOf(value));
    }

    protected String addItemList(String data, String value) {
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

    public boolean isUseHighLightColorForUnknownWord() {
        return isUseHighLightColorForUnknownWord;
    }

    public void setUseHighLightColorForUnknownWord(boolean useHighLightColorForUnknownWord) {
        isUseHighLightColorForUnknownWord = useHighLightColorForUnknownWord;
    }

    public void setDisplayPronunciation(boolean displayPronunciation) {
        this.displayPronunciation = displayPronunciation;
    }

    protected void log(String msg) {
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
        if (showAsterisk == Constant.SHOW_ASTERISK.HIDE_SENTENCE) {
            DLog.d("Difficult", "Constant.SHOW_ASTERISK.HIDE_SENTENCE in BaseFuriganaView" );
            setVisibility(View.INVISIBLE);
        } else {
            DLog.d("Difficult", "Not Constant.SHOW_ASTERISK_ALL in BaseFuriganaView" );
            setVisibility(View.VISIBLE);
        }
    }

    public boolean isTextCenterHorizontal() {
        return isTextCenterHorizontal;
    }

    public void setTextCenterHorizontal(boolean textCenterHorizontal) {
        isTextCenterHorizontal = textCenterHorizontal;
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

    public HashMap<Integer, RubyTextModel> getListRubyText() {
        return listRubyText;
    }

    public void setListRubyText(HashMap<Integer, RubyTextModel> listRubyText) {
        this.listRubyText = listRubyText;
    }

    @Override
    public int getMinLines() {
        return minLines;
    }

    @Override
    public void setMinLines(int minLines) {
        this.minLines = minLines;
    }

    public boolean isHideSubtitleFromAsterisk() {
        return isHideSubtitleFromAsterisk;
    }

    public void setHideSubtitleFromAsterisk(boolean hideSubtitleFromAsterisk) {
        isHideSubtitleFromAsterisk = hideSubtitleFromAsterisk;
    }

    public boolean isWord1() {
        return isWord;
    }

    public void setIsWord(boolean word) {
        isWord = word;
    }

    public void setSetColorOnSubtitle(boolean setColorOnSentence) {
        isSetColorOnSentence = setColorOnSentence;
    }

    public void setVocaKnow_Sentence(int vocaKnow_Sentence) {
        this.vocaKnow_Sentence = vocaKnow_Sentence;
    }

    public void setIsShowFurigana(int value) {
        isShowFurigana = value;
    }

    public int getIsShowFurigana() {
        return isShowFurigana;
    }

    public void setNormalTextView(boolean normalTextView) {
        isNormalTextView = normalTextView;
    }

    private boolean isSHOW_FURIGANA_OFF() {
        return isShowFurigana == Constant.SHOW_FURIGANA_OFF;
    }
    private boolean isSHOW_FURIGANA_DIFFICULT_WORDS_ONLY() {
        return isShowFurigana == Constant.SHOW_FURIGANA_DIFFICULT_WORDS_ONLY;
    }
    private boolean isSHOW_FURIGANA_ALL() {
        return isShowFurigana == Constant.SHOW_FURIGANA_ALL;
    }

}