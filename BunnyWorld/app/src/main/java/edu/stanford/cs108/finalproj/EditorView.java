package edu.stanford.cs108.finalproj;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * TODO: document your custom view class.
 */
public class EditorView extends View {
    private static final String TAG = GameView.class.getSimpleName();

    //*********** Variables for testing *********
    boolean indoor; // a variable just for testing

    //*********** Variables for testing *********


    EditorActivity editorActivity = (EditorActivity) getContext();

    //Pages currentPage = gameActivity.getPage("Page1");    // cmmented out only for test case 1

    private Map<String, Integer> resmap = new HashMap<>();

    Possession currentPossession = new Possession("Possession");
    // Store shapes in Possession
    Map<String, Shapes> PossessionShapes = new HashMap<>();
    Pages currentPage;
    Shapes currentShape;
    ArrayList<Shapes> shapes;  // ShapeList of Current Page
    ArrayList<RectF> frames = new ArrayList<>();

    MediaPlayer hooraySound;

    Shapes selected; // currently selected shape

    float screenW;  // size of displaying screen
    float screenH;

    float mouseorigx;
    float mouseorigy;
    float shapeorigx;
    float shapeorigy;


    // constructor
    public EditorView(Context context, AttributeSet atrrs) {
        super(context, atrrs);
        editorActivity.init();  // such that pageTable and shape table are loaded and Page1 is initialized
        currentPage = editorActivity.getPage();

        shapes = currentPage.getShapes();

        loadResMap();
    }



    private void loadResMap(){

        Map<String, Shapes> shapemap = editorActivity.getShapeData();
        int obj;
        for (HashMap.Entry<String, Shapes> entry: shapemap.entrySet()){
            String targetobjname = entry.getValue().getObjName();
            String shapename = entry.getValue().getName();
            String PACKAGE_NAME = this.getContext().getPackageName();
            obj = getResources().getIdentifier(targetobjname, "drawable",PACKAGE_NAME);
            if (obj == 0){ // cannot get drawable image
                if (entry.getValue().getText().length() != 0){// test whether there is text
                    obj = -1; // if draw text, obj is -1. if cannot get text and img, obj is 0.
                }
            }
            resmap.put(shapename, obj);
            Log.d("testresmap", "objvalue" + Integer.toString(obj));
        }

    }

    public void setCurrentPage(Pages page) {
        currentPage = page;
        invalidate();
    }



    // redraw current page and current possession
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d(TAG, "GameView onDraw()" );

        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(0, 0, canvas.getWidth(), canvas.getHeight());
        canvas.drawRect(rectF, fillPaint);


        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextSize(36);

        // draw page background
        BitmapDrawable back_drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.forestbackground);
        Bitmap backBitmap = back_drawable.getBitmap();
        RectF rectF2 = new RectF(0, 0, canvas.getWidth(), currentPage.getHeight());
        canvas.drawBitmap(backBitmap, null, rectF2, null);

        canvas.drawText(currentPage.getName(), 50, 400, paint);


        shapes = currentPage.getShapes(); // update shapes
        if(shapes != null && shapes.size() > 0) {
            int obj = -2;
            for (int i = shapes.size() - 1;i >= 0; i --){ // loop each shape
                if(shapes.get(i) == null) continue;
                //String shapename = entry.getValue().getName();
                if (shapes.get(i).getText() != null && !shapes.get(i).getText().equals("")){ // there is text
                    obj = -1;
                } else{// there is no text, check img resource
                    String targetobjname = shapes.get(i).getObjName();
                    String PACKAGE_NAME = this.getContext().getPackageName();
                    obj = getResources().getIdentifier(targetobjname, "drawable",PACKAGE_NAME);
                }
                Log.d(TAG, "Draw shape from page");

                if(shapes != null && shapes.size() > 0) {
                    shapes.get(i).draw_edit(canvas, getContext(), canvas.getWidth(), canvas.getHeight(), obj);
                }
//                shapes.get(i).draw(canvas, getContext(), canvas.getWidth(), canvas.getHeight(), obj);
//                //shapes.get(i).draw(canvas, getContext(), screenW, screenH, -1);

                Log.d(TAG, "get rectf");
                if (shapes.get(i).getRectf() != null) {
                    frames.add(shapes.get(i).getRectf()); // add to page class?
                }
            }
        }



//        // draw each shape of the current possession
//        ArrayList<Shapes> shapesPoss = currentPossession.getShapes();
//        Log.d("size of shapesPoss", String.valueOf(shapesPoss.size()));
//        int count = 0;
//        for (int i = shapesPoss.size() - 1;i >= 0; i --){
//            //for (Shapes shape: shapesPoss) {
//            Log.d(TAG, "Draw shape from possession");
//            Log.d("gameview ondraw, sh", String.valueOf(screenH));
//            shapesPoss.get(i).setX(20 + 100 * count);
//            shapesPoss.get(i).setY(500);
//            shapesPoss.get(i).draw(canvas, getContext(), screenW, screenH,resmap.get(shapes.get(i).getName()));
//            count += 1;
//
//        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // on touch and check what event happens
        //ArrayList<Shapes> canBeDroppedShapes = new ArrayList<Shapes>();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:   // This action permits us to select a current shape
                float downX = event.getX();
                float downY = event.getY();
                // record mouse click location
                mouseorigx = event.getX();
                mouseorigy = event.getY();


                Log.d("dragX", Float.toString(downY));
                boolean getselected = false;
                if(shapes != null) {
                    for (int i = shapes.size() - 1; i >= 0; i--) {
                        if (shapes.get(i) != null && shapes.get(i).getRectf().contains(downX, downY)) {
                            // move s to the back of arraylist

                            if (i != shapes.size() - 1) {
                                currentPage.addShape(shapes.get(i));
                                currentPage.deleteShape(i);

                            }
                            selected = shapes.get(shapes.size() - 1);
                            editorActivity.setSelected(selected.getName());
                            getselected = true;
                        }
                    }

                    if (selected != null) {
                        // record selected shape original location
                        shapeorigx = selected.getX();
                        shapeorigy = selected.getY();
                    }
                }


                ArrayList<Shapes> shapesPOSS = currentPossession.getShapes();
                for (int i = shapesPOSS.size() - 1; i >= 0; i--) {
                    if (shapesPOSS.get(i).getRectf().contains(downX, downY)) {
                        // move s to the back of arraylist

                        if (i != shapesPOSS.size() - 1) {
                            currentPage.addShape(shapesPOSS.get(i));
                            currentPage.deleteShape(i);

                        }
                        selected = shapesPOSS.get(shapesPOSS.size() - 1);
                        getselected = true;
                    }
                }

                if (!getselected) {
                    selected = null;
                }
                // generate a list of all shapes which can be dropped by selected shape
                // draw the frame of these shapes
                // can get canBeDroppedShapes
//                if (selected != null) {
//                    canBeDroppedShapes = canBeDroppedShapes(selected);
//                    for (Shapes shape : canBeDroppedShapes) {
//                        shape.setWithFrame(1);
//                    }
//                }

                break;
            case MotionEvent.ACTION_UP:
                if (selected != null) {
                    float upX = event.getX();
                    float upY = event.getY();
                    Log.d("changeXY", Float.toString(selected.getX()));
                    //check whether there is a click on the selected shape
                    if (upX == mouseorigx && upY == mouseorigy) {
                        checkClick(upX, upY);
                    }
                    selected.setX(upX);
                    selected.setY(upY);

                    // check whether this is a legal position
                    if (checkDrop()) { // if there is overlap, go back to original position
                        //selected.setX(shapeorigx);
                        //selected.setY(shapeorigy);
                        editorActivity.updatePosition(selected.getName(), shapeorigx, shapeorigy);
                    }
                    // check whether should trigger onDrop
//                    canBeDroppedShapes = canBeDroppedShapes(selected);
//                    int flag = 0;
//                    for (Shapes shape : shapes) {
//                        // if there is overlap
//                        if (checkDrop(selected, shape) && !selected.equals(shape)) {
//                            // two shapes overlap and the target is a legal one.
//                            if (canBeDroppedShapes.contains(shape)) {
//                                implementOnDrop(shape);
//                                flag = 1;
//                            } else {flag = 2;}
//                        }
//                    }
//                    // if there is overlap but not legal, need to go back.
//                    if(flag == 2){
//                        selected.setX(shapeorigx);
//                        selected.setY(shapeorigy);
//                        invalidate();
//                    }
//                    for (Shapes shape : canBeDroppedShapes) {
//                        Log.d("testGreenframe", "run here");
//                        shape.setWithFrame(0);
//                        invalidate();
//                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:   // This action permits us to drag the current shape
                // Note before drag, store the shape's position,
                // in case the onDrop is not successful.
                // Note: This method contains bugs.

                if (selected!=null) {
                    float x = event.getX() - 0.5f * selected.getWidth();    // make mouse at center
                    float y = event.getY() - 0.5f * selected.getHeight();

                    Log.d("move++", Float.toString(x));
                    selected.setX(x);
                    selected.setY(y);
                    editorActivity.updatePosition(selected.getName(), x, y);

                    //currentPage = gameActivity.getPage(currentPage.getName());
                    //shapes = currentPage.getShapes();
                    invalidate();
                    Log.d("move", Float.toString(x));

                    //setPageSize();


                    Log.d(TAG, "change ownership");
                    Log.d("event gety", String.valueOf(event.getY()));
                    Log.d("page height", String.valueOf(currentPage.getHeight()));
                    // check if shape in page or possession
                    if (event.getY() < currentPage.getHeight() && !selected.isInPage()) {
                        Log.d(TAG, "move to page");
                        // belong to page
                        selected.changeOwnership();   // inPage = true

                        //gameActivity.updateOwnership(selected.getName(), currentPage.getName(), false);

                        currentPage.addShape(selected);
                        currentPossession.deleteShape(selected);
                        // geng duo cao zuo ??

                    } else if (event.getY() >= currentPage.getHeight() && selected.isInPage()) {
                        Log.d(TAG, "move to possession");
                        // belong to possession
                        selected.changeOwnership(); // inPage = false

                        //gameActivity.updateOwnership(selected.getName(), currentPage.getName(), true);

                        currentPossession.addShape(selected);
                        currentPage.deleteShape(selected);

                    }
                    break;
                }
        }
        return true;
    }


    /*
     * check if I click on carrots or on bunny to determine which page to go
     * deal with clause with onClick Triggers
     */

    public void checkClick(float x, float y){

        // get all shapes in currentPage
        shapes = currentPage.getShapes();
        // Any function that can return the clicked Object?
        for(int i=0; i<shapes.size(); i++) {
            if(shapes.get(i) != null && shapes.get(i).getRectf().contains(x, y)) {
                Log.d("testboolean", "true");
                currentShape = shapes.get(i);

            }
        }
    }

    private boolean checkDrop(){
        RectF selectedRect = selected.getRectf();
        shapes = currentPage.getShapes();
        for (Shapes shape:shapes){
            if (shape == null || shape.getName().equals(selected.getName())) continue;
            RectF targetRect = shape.getRectf();
            if (RectF.intersects(selectedRect, targetRect)) {
                return true;
            }
        }
        return false;
    }



}
