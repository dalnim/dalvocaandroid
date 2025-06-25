package com.dalread.component.page_curl;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class PageCurlFrameLayout extends FrameLayout implements PageCurlListener {

   private float mCurl;

   private Path mClipPath;

   private Path mCurlPath;

   private Paint mCurlStrokePaint;

   private Paint mCurlFillPaint;

   private PointF mBottomFold = new PointF();

   private PointF mTopFold = new PointF();

   private PointF mBottomFoldTip = new PointF();

   private PointF mTopFoldTip = new PointF();

   public PageCurlFrameLayout(Context context) {
      super(context);
      init();
   }

   public PageCurlFrameLayout(Context context, AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public PageCurlFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   public PageCurlFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init();
   }

   private void init() {

      mCurlStrokePaint = new Paint();
      mCurlStrokePaint.setStyle(Paint.Style.STROKE);
      mCurlStrokePaint.setStrokeWidth(0.5F);
      mCurlStrokePaint.setColor(Color.BLACK);
      mCurlStrokePaint.setAntiAlias(true);

      mCurlFillPaint = new Paint();
      mCurlFillPaint.setStyle(Paint.Style.FILL);
      mCurlFillPaint.setColor(Color.WHITE);
      mCurlFillPaint.setShadowLayer(20, 0, 0, Color.BLACK);
      setLayerType(LAYER_TYPE_SOFTWARE, mCurlFillPaint);

      mCurlPath = new Path();

      mClipPath = new Path();
   }

   @Override
   public void setCurlFactor(float curl) {

      /*
       * From 1.0 to 0.0, clip reveals page from corner to full, as though beneath the folding page that precedes
       * From 0.0 to -1.0 clip hides corner of page and shows page folding over
       */

      mCurl = curl;
      boolean foldingPage = curl < 0;
      if (curl < 0) curl += 1;

      int w = getWidth();
      int h = getHeight();

      // This math based on logic from:
      // https://github.com/moritz-wundke/android-page-curl/blob/master/src/com/mystictreegames/pagecurl/PageCurlView.java

      mBottomFold.x = w * curl;
      mBottomFold.y = h;

      if (mBottomFold.x > w / 2) {
         // fold is on right edge
         mTopFold.x = w;
         mTopFold.y = h - (w - mBottomFold.x) * h / mBottomFold.x;
      } else {
         // fold is on top edge
         mTopFold.x = 2 * mBottomFold.x;
         mTopFold.y = 0;
      }

      // this is the angle of the fold
      double angle = Math.atan((h - mTopFold.y) / (mTopFold.x - mBottomFold.x));

      // multiple fold angle by 2 to get the angle of the right page edge
      double cosFactor = Math.cos(2 * angle);
      double sinFactor = Math.sin(2 * angle);

      float foldWidth = w - mBottomFold.x;
      mBottomFoldTip.x = (float) (mBottomFold.x + foldWidth * cosFactor);
      mBottomFoldTip.y = (float) (h - foldWidth * sinFactor);

      if (mBottomFold.x > w / 2) {
         mTopFoldTip.x = mTopFold.x;
         mTopFoldTip.y = mTopFold.y;
      } else {
         mTopFoldTip.x = (float) (mTopFold.x + (w - mTopFold.x) * cosFactor);
         mTopFoldTip.y = (float) - (sinFactor * (w - mTopFold.x));
      }

      mClipPath.reset();
      if (foldingPage) {
         // clip to show the page disappearing as it's folded
         mClipPath.moveTo(0, 0);
         if (mTopFold.y != 0) {
            mClipPath.lineTo(w, 0);
         }
         mClipPath.lineTo(mTopFold.x, mTopFold.y);
         mClipPath.lineTo(mBottomFold.x, mBottomFold.y);
         mClipPath.lineTo(0, h);
      } else {
         // clip to show the page underneath revealing
         mClipPath.moveTo(w, h);
         if (mTopFold.y == 0) {
            mClipPath.lineTo(w, 0);
         }
         mClipPath.lineTo(mTopFold.x, mTopFold.y);
         mClipPath.lineTo(mBottomFold.x, mBottomFold.y);
      }
      mClipPath.close();

      mCurlPath.reset();
      mCurlPath.moveTo(mBottomFold.x, mBottomFold.y);
      mCurlPath.lineTo(mBottomFoldTip.x, mBottomFoldTip.y);
      mCurlPath.lineTo(mTopFoldTip.x, mTopFoldTip.y);
      mCurlPath.lineTo(mTopFold.x, mTopFold.y);
      mCurlPath.close();

      invalidate();
   }

   @Override
   protected void dispatchDraw(Canvas canvas) {
      canvas.save();
      if (mCurl != 0 && mCurl != 1 && mCurl != -1) {
         canvas.clipPath(mClipPath);
      }
      super.dispatchDraw(canvas);
      canvas.restore();
      if (mCurl < 0) {
         canvas.drawPath(mCurlPath, mCurlFillPaint);
         canvas.drawPath(mCurlPath, mCurlStrokePaint);
      }
   }
}
