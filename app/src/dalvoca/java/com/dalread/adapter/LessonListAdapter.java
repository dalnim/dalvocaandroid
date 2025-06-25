package com.dalread.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dalread.R;
import com.dalread.base.EnumLanguage;
import com.dalread.listener.OnClickListener;
import com.dalread.model.Lesson;
import com.dalread.model.LessonPreviewReviewModel;
import com.dalread.util.Constant;
import com.dalread.util.DateUtils;
import com.dalread.util.Voca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LessonListAdapter extends RecyclerView.Adapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_LESSON = 1;
    private static final int TYPE_PREVIEW_AND_REVIEW = 2;

    private EnumLanguage enumDisplayLanguage;
    private EnumLanguage enumStudyLanguage;
    private List<Object> objects;
    private OnClickListener listener;

    @BindString(R.string.yesterday)
    String strYesterday;
    @BindString(R.string.today)
    String strToday;
    @BindString(R.string.tomorrow)
    String strTomorrow;
    @BindString(R.string.lessons)
    String strLessons;
    @BindString(R.string.tpl_lessons_admin)
    String tplAdmin;
    @BindString(R.string.tpl_lessons_this_week)
    String tplThisWeek;
    @BindString(R.string.tpl_lessons_next_week)
    String tplNextWeek;
    @BindString(R.string.tpl_lessons_other_weeks)
    String tplOtherWeeks;

    private LessonPreviewReviewModel lessonPreviewReviewModel;

    public LessonListAdapter(Activity activity,
                             EnumLanguage enumDisplayLanguage,
                             EnumLanguage enumStudyLanguage,
                             LessonPreviewReviewModel lessonPreviewReviewModel) {
        this.enumDisplayLanguage = enumDisplayLanguage;
        this.enumStudyLanguage = enumStudyLanguage;
        this.lessonPreviewReviewModel = lessonPreviewReviewModel;

        objects = new ArrayList<>();

        ButterKnife.bind(this, activity);
    }

    @Override
    public int getItemViewType(int position) {
        if (objects.get(position) instanceof String)
            return TYPE_HEADER;
        if (objects.get(position) instanceof Lesson)
            return TYPE_LESSON;
        return TYPE_PREVIEW_AND_REVIEW;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER)
            return new HeaderHolder(layoutInflater.inflate(R.layout.header_simple, parent, false));
        if (viewType == TYPE_LESSON) {
            if (lessonPreviewReviewModel.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
                return new LessonAdminHolder(layoutInflater.inflate(R.layout.item_lesson_admin, parent, false));
            }
            return new LessonHolder(layoutInflater.inflate(R.layout.item_lesson, parent, false));
        }
        return new PreviewAndReviewHolder(layoutInflater.inflate(R.layout.layout_preview_and_review, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderHolder) {
            ((HeaderHolder) holder).bind((String) objects.get(position));
        } else if (holder instanceof LessonHolder) {
            Lesson lesson = (Lesson) objects.get(position);
            if (lessonPreviewReviewModel.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_TUTOR) {
                ((LessonHolder) holder).bindTutor(lesson);
            } else {
                ((LessonHolder) holder).bindStudent(lesson);
            }
        } else if (holder instanceof LessonAdminHolder) {
            ((LessonAdminHolder) holder).bind((Lesson) objects.get(position));
        } else {
            ((PreviewAndReviewHolder) holder).bind();
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public int setData(List<Lesson> lessons) {
        objects.clear();
        if (lessonPreviewReviewModel != null) {
            objects.add(lessonPreviewReviewModel); // preview & review
        }
        int focusPos = -1;
        if (lessons != null) {
            Lesson previousLesson = null;
            int count = 0;
            for (Lesson lesson : lessons) {
                String bookId = String.valueOf(lesson.getBookId());
                lesson.setBookName(Voca.getBookName(bookId, enumDisplayLanguage));
                lesson.setBookNameCombined(Voca.getBookNameCombined(bookId, enumDisplayLanguage, enumStudyLanguage));
                objects.add(lesson);
                if (previousLesson != null) {
                    previousLesson.setHasNextLessonInARow(lesson.getLessonStartTimeTS() == previousLesson.getLessonFinishTimeTS());
                    Date previousLessonStartTime = new Date(DateUtils.secondsToMillis(previousLesson.getLessonStartTimeTS()));
                    Date lessonStartTime = new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
                    if (isSameGroup(previousLessonStartTime, lessonStartTime)) {
                        if (lessons.indexOf(lesson) == lessons.size() - 1) { // lesson is the last item
                            if (focusPos == -1 && !DateUtils.isTodayOrFuture(previousLessonStartTime)) {
                                focusPos = getHeaderIndex(count);
                            }
                            addHeaderText(getHeaderIndex(count), previousLessonStartTime, count + 1);
                            return focusPos;
                        }
                    } else {
                        if (focusPos == -1 && DateUtils.isTodayOrFuture(previousLessonStartTime)) {
                            focusPos = getHeaderIndex(count);
                        }
                        addHeaderText(getHeaderIndex(count), previousLessonStartTime, count);
                        if (lessons.indexOf(lesson) == lessons.size() - 1) { // lesson is the last item
                            if (focusPos == -1 && DateUtils.isTodayOrFuture(lessonStartTime)) {
                                focusPos = getHeaderIndex(0);
                            }
                            addHeaderText(getHeaderIndex(0), lessonStartTime, 1);
                            return focusPos;
                        }
                        count = 0;
                    }
                }
                previousLesson = lesson;
                count++;
            }
        }
        return focusPos;
    }

    private boolean isSameGroup(Date previousLessonStartTime, Date lessonStartTime) {
        return lessonPreviewReviewModel.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN
                ? DateUtils.isSameDate(previousLessonStartTime, lessonStartTime)
                : DateUtils.isSameWeek(previousLessonStartTime, lessonStartTime);
    }

    private int getHeaderIndex(int count) {
        return objects.size() - 1 - count;
    }

    private void addHeaderText(int index, Date date, int count) {
        String headerText = getHeaderText(date, count);
        objects.add(index, headerText);
    }

    private String getHeaderText(Date date, int count) {
        String headerText;
        if (lessonPreviewReviewModel.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
            String firstText;
            if (DateUtils.isYesterday(date)) {
                firstText = strYesterday;
            } else if (DateUtils.isToday(date)) {
                firstText = strToday;
            } else if (DateUtils.isTomorrow(date)) {
                firstText = strTomorrow;
            } else {
                firstText = strLessons;
            }
            headerText = String.format(tplAdmin, firstText, count, DateUtils.getDateOnlyWithDayNameFormat().format(date));
        } else {
            if (DateUtils.isThisWeek(date)) {
                headerText = String.format(tplThisWeek, count);
            } else if (DateUtils.isNextWeek(date)) {
                headerText = String.format(tplNextWeek, count);
            } else {
                headerText = String.format(
                        tplOtherWeeks,
                        count,
                        DateUtils.getFirstDayOfWeek(date),
                        DateUtils.getLastDayOfWeek(date)
                );
            }
        }
        return headerText;
    }

    public Lesson notifyLessonRemoved(int pos) {
        if (getItemViewType(pos) == TYPE_LESSON) {
            Lesson lesson = (Lesson) objects.remove(pos);
            if (pos > 0 && getItemViewType(pos - 1) == TYPE_HEADER
                    && (pos == objects.size() || getItemViewType(pos) == TYPE_HEADER)) {
                objects.remove(pos - 1);
                notifyItemRangeRemoved(pos - 1, 2);
            } else {
                notifyItemRemoved(pos);
            }
            return lesson;
        }
        return null;
    }

    public boolean isSwipeable(int pos) {
        return getItemViewType(pos) == TYPE_LESSON;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    class HeaderHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header)
        TextView tvHeader;

        public HeaderHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            if (lessonPreviewReviewModel.getLessonType() == Constant.API_VALUE.LIST_LESSON_FOR_ADMIN) {
                tvHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_content_copy_black_24dp, 0);
                tvHeader.setOnClickListener(v -> {
                    if (listener != null) {
                        int i = getAdapterPosition() + 1; // get the first lesson of selected date
                        if (i < objects.size()) {
                            listener.onClick(v, objects.get(i));
                        }
                    }
                });
            }
        }

        public void bind(String text) {
            tvHeader.setText(text);
        }
    }

    class LessonHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ic_left)
        ImageView icLeft;
        @BindView(R.id.ic_right)
        ImageView icRight;
        @BindView(R.id.tv_primary)
        TextView tvPrimary;
        @BindView(R.id.tv_secondary)
        TextView tvSecondary;
        @BindView(R.id.tv_lang)
        TextView tvLang;
        @BindView(R.id.tv_keyboard)
        TextView tvKeyboard;

        @BindString(R.string.tpl_lesson_info)
        String tplLessonInfo;

        private Lesson lesson;

        LessonHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
            initLayout();
        }

        private void initLayout() {
            icLeft.setImageResource(R.drawable.ic_face_black_24dp);
            icRight.setImageResource(R.drawable.ic_keyboard_arrow_right_36dp);
        }

        private void bind(Lesson lesson) {
            this.lesson = lesson;

            Date lessonStartTime = new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
            String text = String.format(
                    tplLessonInfo,
                    DateUtils.getTimeToHourFormat().format(lessonStartTime),
                    lesson.getLessonTimeLength(),
                    DateUtils.getDateOnlyWithDayNameFormat().format(lessonStartTime)
            );
            tvPrimary.setText(text);

            tvLang.setText(Voca.localeToEmoji(lesson.getStudyLangCode()));
            tvKeyboard.setVisibility(lesson.getLessonTypeApi() == Constant.API_VALUE.LESSON_TYPE_TEXT_CHAT ? View.VISIBLE : View.GONE);
        }

        void bindStudent(Lesson lesson) {
            bind(lesson);
            tvSecondary.setText(lesson.getTutorName());
        }

        void bindTutor(Lesson lesson) {
            bind(lesson);
            tvSecondary.setText(lesson.getStudentName());
        }

        @OnClick({R.id.v_item, R.id.ic_left})
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, lesson);
            }
        }
    }

    class LessonAdminHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_foreground)
        View vItem;
        @BindView(R.id.ic_right)
        View icRight;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.ic_student_joined)
        ImageView icStudentJoined;
        @BindView(R.id.tv_student)
        TextView tvStudent;
        @BindView(R.id.ic_tutor_joined)
        ImageView icTutorJoined;
        @BindView(R.id.tv_tutor)
        TextView tvTutor;
        @BindView(R.id.tv_lang)
        TextView tvLang;
        @BindView(R.id.tv_keyboard)
        TextView tvKeyboard;
        @BindView(R.id.tv_book_name)
        TextView tvBookName;

        @BindColor(R.color.colorButtonOkDisable)
        int clInactiveLesson;
        @BindColor(R.color.colorBlue)
        int clActiveLesson;

        @BindString(R.string.tpl_lesson_admin_info)
        String tplLessonInfo;

        private Lesson lesson;

        LessonAdminHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bind(Lesson lesson) {
            this.lesson = lesson;

            vItem.setBackgroundResource(
                    Voca.getLessonBackgroundColor(
                            lesson.getLessonStartTimeTS(),
                            lesson.getLessonFinishTimeTS(),
                            lesson.getStarted() == 1,
                            lesson.getFinished() == 1
                    )
            );
            icRight.setVisibility(
                    System.currentTimeMillis() > DateUtils.secondsToMillis(lesson.getLessonFinishTimeTS()) || lesson.getFinished() == 1
                            ? View.GONE
                            : View.VISIBLE
            );
            //
            Date lessonStartTime = new Date(DateUtils.secondsToMillis(lesson.getLessonStartTimeTS()));
            String text = String.format(
                    tplLessonInfo,
                    DateUtils.getTimeToHourFormat().format(lessonStartTime),
                    lesson.getLessonTimeLength()
            );
            tvTime.setText(text);
            //
            icStudentJoined.setColorFilter(lesson.getStudentJoined() == 1 ? clActiveLesson : clInactiveLesson);
            tvStudent.setText(lesson.getStudentName());
            //
            icTutorJoined.setColorFilter(lesson.getTutorJoined() == 1 ? clActiveLesson : clInactiveLesson);
            tvTutor.setText(lesson.getTutorName());
            //
            tvLang.setText(Voca.localeToEmoji(lesson.getStudyLangCode()));
            tvKeyboard.setVisibility(lesson.getLessonTypeApi() == Constant.API_VALUE.LESSON_TYPE_TEXT_CHAT ? View.VISIBLE : View.GONE);
            tvBookName.setText(lesson.getBookNameCombined());
        }

        @OnClick({R.id.v_foreground, R.id.tv_student, R.id.tv_tutor})
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, lesson);
            }
        }
    }

    class PreviewAndReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.v_preview_today_lesson) LinearLayout vPreviewTodayLesson;
        @BindView(R.id.v_record_lessons) LinearLayout vRecordLessons;

        PreviewAndReviewHolder(@NonNull View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }

        private void bind() {
            vPreviewTodayLesson.setVisibility(lessonPreviewReviewModel.isPreviewTodayLesson() ? View.VISIBLE : View.GONE);
            vRecordLessons.setVisibility(lessonPreviewReviewModel.isRecordLesson() ? View.VISIBLE : View.GONE);
        }

        @OnClick({R.id.v_preview_today_lesson, R.id.v_record_lessons})
        void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, null);
            }
        }
    }
}
