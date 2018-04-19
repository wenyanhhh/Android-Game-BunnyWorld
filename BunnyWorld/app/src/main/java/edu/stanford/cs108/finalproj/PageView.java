//package edu.stanford.cs108.finalproj;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.database.sqlite.SQLiteDatabase;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.text.TextPaint;
//import android.util.AttributeSet;
//import android.view.MotionEvent;
//import android.view.View;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * TODO: document your custom view class.
// */
//public class PageView extends View {
////    private String mExampleString; // TODO: use a default from R.string...
////    private int mExampleColor = Color.RED; // TODO: use a default from R.color...
////    private float mExampleDimension = 0; // TODO: use a default from R.dimen...
////    private Drawable mExampleDrawable;
////
////    private TextPaint mTextPaint;
////    private float mTextWidth;
////    private float mTextHeight;
//
//    SQLiteDatabase db;
//    float x, y;
//
//    public PageView(Context context) {
//        super(context);
//        init();
//    }
//
//    public PageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    public PageView(Context context, AttributeSet attrs, int defStyle) {
//        super(context, attrs, defStyle);
//        init();
//    }
//
//    List<BitmapDrawable> drawableList = new ArrayList<>();
//
//
//    private void init() {
//        // Load attributes
//        // go through all shapes in a page, and then add to the list.
//        BitmapDrawable cur = (BitmapDrawable) getResources().getDrawable(R.drawable.ic_launcher_background);
//
//        drawableList.add(cur);
//    }
//
//
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        Bitmap bunnymap = drawableList.get(0).getBitmap();
//
//        canvas.drawBitmap(bunnymap, x, y, null);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch(event.getAction()) {
//            case MotionEvent.ACTION_MOVE:
//                x = event.getX();
//                y = event.getY();
//                // Force to reDraw.
//                invalidate();
//        }
//        return true;
//    }
//
//
//}
//
//
//
//
//
//
//
//
//
//
//
//
//
