package com.dalread.util;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;

import androidx.annotation.StringRes;

import com.dalread.R;
import com.dalread.database.SharedPreferencesDB;
import com.dalread.dialog.SingleChoiceDialog;
import com.dalread.listener.OnClickDialogListener;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ViewAnimatorUtil {
    public enum MoveDirection {
        DOWN,
        UP,
        LEFT,
        RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT,
        UP_LEFT,
        UP_RIGHT
    }
    public interface OnTransparencyChangeListener {
        void onTransparencyChanged(int newTransparencyValue);
    }

    public static float getAlpha(Context context) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        return 1.0f - (sharedPreferences.getHiddenButtonsTransparencyOnFullScreen() / 100f);
    }
    public static void hideViewsOnFullScreenWithAnimation(Context context, List<View> views) {
        float alphaMax = 1.0f;
        float alphaMin = getAlpha(context);
        //      float alphaMin = 0.5f;
        int startDelay = 500;
        int duration = 1200;

        setVisibleViews(views, View.VISIBLE);

        for (View view : views) {
        view.setAlpha(alphaMax);
        view.animate()
                .setStartDelay(startDelay)
                .alpha(alphaMin)
                .setDuration(duration)
                .start();
        }
    }
    public static  void openChangeHiddenButtonsTransparency(Context context, OnTransparencyChangeListener listener) {
        SharedPreferencesDB sharedPreferences = SharedPreferencesDB.getInstance(context);
        List<String> listTransparencyValues = IntStream.rangeClosed(0, 100)
                .boxed()
                .map(e -> e.toString() + "%")
                .collect(Collectors.toList());
        String[] arrayTransparencyValues = listTransparencyValues.stream().toArray(String[]::new);
        int index = sharedPreferences.getHiddenButtonsTransparencyOnFullScreen();
        @StringRes int titleId = R.string.transparency_hidden_buttons_full_screen;
        SingleChoiceDialog singleChoiceDialog = new SingleChoiceDialog(context);
        singleChoiceDialog.show(
          titleId,
          arrayTransparencyValues,
          index,
          R.string.ok,
          R.string.cancel,
          new OnClickDialogListener() {
            @Override
            public void onClick(View view, Object object) {
              Integer which = (Integer) object;
              if (index == which) return;
              sharedPreferences.setHiddenButtonsTransparencyOnFullScreen(which);
              // 콜백 리스너를 통해 이벤트 전달
              if (listener != null) {
                listener.onTransparencyChanged(which);
              }
            }


            @Override
            public void onDismiss(View view, Object object) {
            }
          });
    }
    // 이미지 확대 및 이동 애니메이션 실행
    public static void animateImageView(View view, MoveDirection moveDirection) {
        animateImageView(view, 2f, 0.5f, 0.5f, 500, moveDirection);
    }
    public static void animateImageView(View view, float endScale, float moveXFactor, float moveYFactor, long duration, MoveDirection moveDirection) {
        // 뷰의 크기를 계산
        view.post(() -> {
            float width = view.getWidth();
            float height = view.getHeight();

            // 이동할 거리 계산 (뷰의 크기에 비례하여 이동)
            float moveX = width * moveXFactor;
            float moveY = height * moveYFactor;

            // 이동 방향에 따른 거리 조정
            switch (moveDirection) {
                case DOWN: // 아래로 이동
                    moveX = 0; // X 이동은 없음
                    break;
                case UP: // 위로 이동
                    moveY = -moveY; // Y 이동을 음수로 설정
                    break;
                case LEFT: // 왼쪽으로 이동
                    moveX = -moveX; // X 이동을 음수로 설정
                    moveY = 0; // Y 이동은 없음
                    break;
                case RIGHT: // 오른쪽으로 이동
                    moveY = 0; // Y 이동은 없음
                    break;
                case DOWN_LEFT: // 왼쪽 아래로 이동
                    moveX = -moveX;
                    moveY = moveY;
                    break;
                case DOWN_RIGHT: // 오른쪽 아래로 이동
                    moveX = moveX;
                    moveY = moveY;
                    break;
                case UP_LEFT: // 왼쪽 위로 이동
                    moveX = -moveX;
                    moveY = -moveY;
                    break;
                case UP_RIGHT: // 오른쪽 위로 이동
                    moveX = moveX;
                    moveY = -moveY;
                    break;
                default:
                    // 기본값 (기본적으로 우하로 이동)
                    moveX = width * moveXFactor;
                    moveY = height * moveYFactor;
                    break;
            }

            // 확대 및 이동 애니메이션 (Scale + Translation)
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", endScale);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", endScale);
            ObjectAnimator moveRight = ObjectAnimator.ofFloat(view, "translationX", moveX);
            ObjectAnimator moveDown = ObjectAnimator.ofFloat(view, "translationY", moveY);

            // Alpha 애니메이션: 처음에는 0에서 1로 증가
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

            // 원래 크기 및 위치로 돌아가는 애니메이션
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
            ObjectAnimator moveBackRight = ObjectAnimator.ofFloat(view, "translationX", 0);
            ObjectAnimator moveBackDown = ObjectAnimator.ofFloat(view, "translationY", 0);

            // Alpha 애니메이션: 다시 1에서 0으로 감소
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);

            // 애니메이션 세트 (확대, 이동 및 불투명하게 만들기)
            AnimatorSet scaleAndMoveUpSet = new AnimatorSet();
            scaleAndMoveUpSet.playTogether(scaleUpX, scaleUpY, moveRight, moveDown, fadeIn);
            scaleAndMoveUpSet.setDuration(duration); // 확대 및 이동 시간 설정

            // 애니메이션 세트 (축소 및 원래 위치로 이동)
            AnimatorSet scaleAndMoveDownSet = new AnimatorSet();
            scaleAndMoveDownSet.playTogether(scaleDownX, scaleDownY, moveBackRight, moveBackDown, fadeOut);
            scaleAndMoveDownSet.setDuration(duration); // 축소 및 복귀 시간 설정

            // 애니메이션 순차 실행
            AnimatorSet finalSet = new AnimatorSet();
            finalSet.playSequentially(scaleAndMoveUpSet, scaleAndMoveDownSet);
            finalSet.start();
        });
    }

    public static void animateImageView2(View view, float endScale, float moveXFactor, float moveYFactor, long duration) {
        // 뷰의 크기를 계산
        view.post(() -> {
            float width = view.getWidth();
            float height = view.getHeight();

            // 이동할 거리 계산 (뷰의 크기에 비례하여 이동)
            float moveX = width * moveXFactor;
            float moveY = height * moveYFactor;

            // 확대 및 이동 애니메이션 (Scale + Translation)
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", endScale);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", endScale);
            ObjectAnimator moveRight = ObjectAnimator.ofFloat(view, "translationX", moveX);
            ObjectAnimator moveDown = ObjectAnimator.ofFloat(view, "translationY", moveY);

            // Alpha 애니메이션: 처음에는 0에서 1로 증가
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);

            // 원래 크기 및 위치로 돌아가는 애니메이션
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);
            ObjectAnimator moveBackRight = ObjectAnimator.ofFloat(view, "translationX", 0);
            ObjectAnimator moveBackDown = ObjectAnimator.ofFloat(view, "translationY", 0);

            // Alpha 애니메이션: 다시 1에서 0으로 감소
            ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);

            // 애니메이션 세트 (확대, 이동 및 불투명하게 만들기)
            AnimatorSet scaleAndMoveUpSet = new AnimatorSet();
            scaleAndMoveUpSet.playTogether(scaleUpX, scaleUpY, moveRight, moveDown, fadeIn);
            scaleAndMoveUpSet.setDuration(duration); // 확대 및 이동 시간 설정

            // 애니메이션 세트 (축소 및 원래 위치로 이동)
            AnimatorSet scaleAndMoveDownSet = new AnimatorSet();
            scaleAndMoveDownSet.playTogether(scaleDownX, scaleDownY, moveBackRight, moveBackDown, fadeOut);
            scaleAndMoveDownSet.setDuration(duration); // 축소 및 복귀 시간 설정

            // 애니메이션 순차 실행
            AnimatorSet finalSet = new AnimatorSet();
            finalSet.playSequentially(scaleAndMoveUpSet, scaleAndMoveDownSet);
            finalSet.start();
        });
    }

    public static void animateImageView1(View view, float endScale, float moveXFactor, float moveYFactor, long duration) {

        // 뷰의 크기를 계산
        view.post(() -> {
            float width = view.getWidth();
            float height = view.getHeight();

            // 이동할 거리 계산 (뷰의 크기에 비례하여 이동)
            float moveX = width * moveXFactor;
            float moveY = height * moveYFactor;

            // 확대 및 이동 애니메이션 (Scale + Translation)
            ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(view, "scaleX", endScale);
            ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(view, "scaleY", endScale);

            ObjectAnimator moveRight = ObjectAnimator.ofFloat(view, "translationX", moveX);
            ObjectAnimator moveDown = ObjectAnimator.ofFloat(view, "translationY", moveY);

            // 원래 크기 및 위치로 돌아가는 애니메이션
            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f);
            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f);

            ObjectAnimator moveBackRight = ObjectAnimator.ofFloat(view, "translationX", 0);
            ObjectAnimator moveBackDown = ObjectAnimator.ofFloat(view, "translationY", 0);

            // 애니메이션 세트 (확대 및 이동)
            AnimatorSet scaleAndMoveUpSet = new AnimatorSet();
            scaleAndMoveUpSet.playTogether(scaleUpX, scaleUpY, moveRight, moveDown);
            scaleAndMoveUpSet.setDuration(duration); // 확대 및 이동 시간 설정

            // 애니메이션 세트 (축소 및 원래 위치로 이동)
            AnimatorSet scaleAndMoveDownSet = new AnimatorSet();
            scaleAndMoveDownSet.playTogether(scaleDownX, scaleDownY, moveBackRight, moveBackDown);
            scaleAndMoveDownSet.setDuration(duration); // 축소 및 복귀 시간 설정

            // 애니메이션 순차 실행
            AnimatorSet finalSet = new AnimatorSet();
            finalSet.playSequentially(scaleAndMoveUpSet, scaleAndMoveDownSet);
            finalSet.start();
        });
    }
    //이건 항상 보여줘야 할때 부른다. 아직은 사용안하고 있음)
    public static void cancelHidingButtonsOnFullScreenWithAnimation(List<View> views) {
        float alphaMax = 1.0f;

        for (View view : views) {
        view.setAlpha(alphaMax);
        view.animate().cancel();
        }
    }

    private static void setVisibleViews(List<View> views, int visibility) {
      for (View view : views) {
        view.setVisibility(visibility);
      }
    }
}
