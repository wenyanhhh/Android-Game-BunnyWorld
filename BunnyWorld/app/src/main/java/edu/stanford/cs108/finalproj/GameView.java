package edu.stanford.cs108.finalproj;

import android.content.Context;
import android.graphics.*;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.shapes.Shape;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.*;


/**
 * Created by wenyan on 2/27/18.
 */

public class GameView extends View {

    private static final String TAG = GameView.class.getSimpleName();

    //*********** Variables for testing *********
    boolean indoor; // a variable just for testing

    //*********** Variables for testing *********


    GameActivity gameActivity = (GameActivity) getContext();

    //Pages currentPage = gameActivity.getPage("Page1");    // cmmented out only for test case 1

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

    private Map<String, Integer> resmap = new HashMap<>();

    // constructor
    public GameView(Context context, AttributeSet atrrs) {
        super(context, atrrs);

        Log.d(TAG, "GameView Constuctor");
        // set the entering page as Page1.
        gameActivity.init();
        currentPage = gameActivity.getPage("Page1");

        currentShape = gameActivity.getShape("Shape1");// test
        init();
        // when entering an new activity: it first run view, then run activity
        onEnterPage(); // commented out for testing
        loadResMap();
    }

    // generate a drawable resource map: key shapename, value int
    // if not in drawable folder, value is 0.
    // then check whether there is a text attr of the shape
    // if not, draw light gray frame
    // if yes, draw text
    private void loadResMap(){

        Map<String, Shapes> shapemap = gameActivity.getShapeData();
        int obj;
        String shapename;
        for (HashMap.Entry<String, Shapes> entry: shapemap.entrySet()){
            if (entry.getValue().getText() != null && !entry.getValue().getText().equals("")){
                shapename = entry.getValue().getName();
                obj = -1;// if there is text, obj = -1, draw text;
            } else{ // there is no text, check if there is a legal img
                String targetobjname = entry.getValue().getObjName();
                shapename = entry.getValue().getName();
                String PACKAGE_NAME = this.getContext().getPackageName();
                obj = getResources().getIdentifier(targetobjname, "drawable",PACKAGE_NAME);
            }
            resmap.put(shapename, obj);
            Log.d("testresmap", "objvalue" + Integer.toString(obj));
        }
    }

    // ************** Initialize size stuff ************** begin
    // Set up ScreenSize, pageSize and Possession Size
    private void init() {
        setSreenSize();
        //setPageSize();
        //setPossSize();
    }


    // This method sets the screen size;
    private void setSreenSize(){
        DisplayMetrics screenDM = new DisplayMetrics();
        ((GameActivity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(screenDM);
        screenW = screenDM.widthPixels;
        screenH = screenDM.heightPixels;
    }



    private void setPageSize() {
        currentPage.resize(screenW, (float) (0.6 * screenH));

        Log.d("screenH", " " + 0.6* screenH);
    }

    private void setPossSize() {
        currentPossession.resize(screenW,(float) (0.4 * screenH));
    }

    // ************** Initialize size stuff ************** end



    /*
    When load a new page, check whether there are shapes on page
    which have onEnter triggers
    If true, need to implement corresponding scrip
     */
    private void onEnterPage(){
        Log.d(TAG, "GameView on enter page");

        Log.d(TAG, "Current Page is null:  " + (currentPage == null) + currentPage.getName());

        shapes = currentPage.getShapes();

        // go through all shapes, check whether shapes is null or not
        if(shapes != null) {
            for (Shapes shape : shapes){
                Log.d(TAG, "go through shape's script");
                implementScript(shape);
            }
            Log.d(TAG, "GameView finish on enter page");
        }

    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        screenW = (float) MeasureSpec.getSize(widthMeasureSpec);
//        screenH = (float) MeasureSpec.getSize(heightMeasureSpec);
//    }




    /*
        by Vincent commented out only for test case1
        DO NOT DELETE!!!!!!!!!!!!!!!
     */
//    private void onEnterPage(){
//        Log.d(TAG, "GameView on enter page");
//        shapes = currentPage.getShapes();
//        Log.d(TAG, "GameView get shapes");
//        // go through all shapes
//        for (Shapes shape : shapes){
//            Log.d(TAG, "go through shape's script");
//            // go through all scripts for each shape
//            for (ShapeScript clause : shape.getScript()){
//                Log.d(TAG, "go through script's clause");
//                if(clause.getTrigger().equals("onEnter")){
//                    implementClause(clause);
//                }
//            }
//        }
//        Log.d(TAG, "GameView finish on enter page");
//    }




    // redraw current page and current possession
    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d(TAG, "GameView onDraw()" );



        setPageSize();
        setPossSize();

        // ------ BACKGROUND ------
        // draw page background if it's not page1
        // if (currentPage is not page1) {
        /*
        Paint fillPaint = new Paint();
        fillPaint.setColor(Color.WHITE);
        fillPaint.setStyle(Paint.Style.FILL);
        RectF rectF = new RectF(0, 0, currentPage.getWidth(), currentPage.getHeight());
        Log.d("getpagesize", Float.toString(currentPage.getWidth())+Float.toString(currentPage.getHeight()));
        canvas.drawRect(rectF, fillPaint);
        */
        // }

        // draw possession background if it's default page
        // if (currentPage is not page1) {
        /*
        Paint fillPaint1 = new Paint();
        fillPaint1.setColor(Color.LTGRAY);
        fillPaint1.setStyle(Paint.Style.FILL);
        RectF rectF1 = new RectF(0, currentPage.getHeight(), currentPossession.getWidth(), currentPage.getHeight() + currentPossession.getHeight());
        canvas.drawRect(rectF1, fillPaint1);
        */

        // draw possession background
        String PACKAGE_NAME = this.getContext().getPackageName();
        int obj = getResources().getIdentifier("po", "drawable", PACKAGE_NAME);
        // draw image
        BitmapDrawable obj_drawable = (BitmapDrawable) getResources().getDrawable(obj);
        Bitmap objectBitmap = obj_drawable.getBitmap();
        RectF rectF1 = new RectF(0, currentPage.getHeight(), canvas.getWidth(), canvas.getHeight());
        canvas.drawBitmap(objectBitmap, null, rectF1, null);

        // draw page background
        int back = getResources().getIdentifier("forestbackground", "drawable", PACKAGE_NAME);
        // draw image
        BitmapDrawable back_drawable = (BitmapDrawable) getResources().getDrawable(back);
        Bitmap backBitmap = back_drawable.getBitmap();
        RectF rectF2 = new RectF(0, 0, canvas.getWidth(), currentPage.getHeight());
        canvas.drawBitmap(backBitmap, null, rectF2, null);

        // ------ SHAPE ------
        // }

        // draw shapes from page
        ArrayList<Shapes> shapesPage = currentPage.getShapes();
        if(shapes != null) {
            for (int i = shapes.size() - 1;i >= 0; i --){
                Log.d("size of shapesPage", String.valueOf(shapesPage.size()));

                //for (Shapes shape: shapesPage) {
                // draw the shape: LYY
                Log.d(TAG, "Draw shape from page");
                //Log.d(TAG, shape.getObjName());

                if(shapes.get(i) == null) continue;
                shapes.get(i).draw(canvas, getContext(), screenW, screenH, resmap.get(shapes.get(i).getName()));
                //shapes.get(i).draw(canvas, getContext(), screenW, screenH, -1);

                Log.d(TAG, "get rectf");
                if (shapes.get(i).getRectf() != null) {
                    frames.add(shapes.get(i).getRectf()); // add to page class?
                }
            }
        }


        // draw each shape of the current possession
        ArrayList<Shapes> shapesPoss = currentPossession.getShapes();
        Log.d("size of shapesPoss", String.valueOf(shapesPoss.size()));
        int count = 0;
        for (int i = shapesPoss.size() - 1;i >= 0; i --){
        //for (Shapes shape: shapesPoss) {
            Log.d(TAG, "Draw shape from possession");
            Log.d("gameview ondraw, sh", String.valueOf(screenH));
            shapesPoss.get(i).setX(50 + 170 * count);
            Log.d(TAG, "set y");
            shapesPoss.get(i).setY(currentPage.getHeight() + 40);
            Log.d(TAG, "draw shape poss");
            // shrink
            shapesPoss.get(i).setWidth(75);
            shapesPoss.get(i).setHeight(75);

            shapesPoss.get(i).draw(canvas, getContext(), canvas.getWidth(), canvas.getHeight(),resmap.get(shapesPoss.get(i).getName()));
            Log.d(TAG, "increase count");
            count += 1;
        }


//        if(shapes != null && shapes.size() > 0)
//            shapes.get(0).draw(canvas, getContext(), screenW, screenH, -1);

        //shapes.get(0).draw(canvas, getContext(), screenW, screenH, -1);


        // switch to different pages by redraw all stuff on the GameView canvas! According to scripts on shapes


        // Map<String, Pages> curPage = game.getMap();

        //Pages page = curPage.get("Page1");

        //List<Shapes> shapesList = page.getShapes();
        //ImageView imgView = new ImageView(this);
        // InputStream is = getClass().getResourceAsStream("/drawable/" + fileName);
        // imgView.setImageDrawable(Drawable.createFromStream(is, ""));

        // List<Shapes> shapesList = page.getShapes();
        Log.d(TAG, "finish Drawing" );
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) { // on touch and check what event happens
        ArrayList<Shapes> canBeDroppedShapes = new ArrayList<Shapes>();
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
                    for (Shapes shape : shapes) {
                        //for (int i = shapes.size() - 1;i >= 0; i --){
                        Log.d(TAG,"actionDown, go through shape");

                        if (shape != null && shape.getRectf().contains(downX,downY)){
                            // move s to the back of arraylist
                            Log.d(TAG,"actionDown, change arraylist");
                            int i = shapes.indexOf(shape);
                            currentPage.addShape(0,shape);
                            currentPage.deleteShape(i+1);
                            Log.d("shapes size", String.valueOf(shapes.size()));

                            Log.d(TAG,"actionDown, get selected shape");

                            selected = shapes.get(0);
                            Log.d(TAG,"actionDown, got selected");
                            getselected = true;
                            break;
                        }
                    }
                }


                ArrayList<Shapes> shapesPOSS = currentPossession.getShapes();
                for (Shapes shape : shapesPOSS) {
                    //for (int i = shapes.size() - 1;i >= 0; i --){
                    Log.d(TAG,"actionDown, go through shape");

                    if (shape.getRectf().contains(downX,downY)){
                        // move s to the back of arraylist
                        Log.d(TAG,"actionDown, change arraylist");
                        int i = shapesPOSS.indexOf(shape);

                        //currentPossession.addShape(0,shape);
                        //currentPossession.deleteShape(i+1);
                        Log.d("shapes size", String.valueOf(shapesPOSS.size()));

                        Log.d(TAG,"actionDown, get selected shape");
                        //selected = shapesPOSS.get(0);
                        selected = shape;
                        Log.d(TAG,"actionDown, got selected");
                        getselected = true;
                        break;
                    }
                }

                Log.d(TAG,"actionDown, getX,Y");
                if (selected != null) {
                    // record selected shape original location
                    shapeorigx = selected.getX();
                    shapeorigy = selected.getY();
                }
                /*
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
                */
                Log.d(TAG,"actionDown, check if get selected");
                if (!getselected) {
                    selected = null;
                }
                // generate a list of all shapes which can be dropped by selected shape
                // draw the frame of these shapes
                // can get canBeDroppedShapes
                Log.d(TAG,"actionDown, get can be dropped");
                if (selected != null) {
                    canBeDroppedShapes = canBeDroppedShapes(selected);
                    for (Shapes shape : canBeDroppedShapes) {
                        shape.setWithFrame(1);
                    }
                }
                Log.d(TAG,"actionDown, break");
                break;
            case MotionEvent.ACTION_UP:
                if (selected != null) {
                    float upX = event.getX();
                    float upY = event.getY();
                    Log.d("changeXY", Float.toString(selected.getX()));
                    //check whether there is a click on the selected shape
                    if (upX == mouseorigx && upY == mouseorigy && selected.getClickability()) {
                        checkClick(upX, upY);
                    }
                    // check whether should trigger onDrop
                    canBeDroppedShapes = canBeDroppedShapes(selected);
                    int flag = 0;
                    for (Shapes shape : shapes) {
                        // if there is overlap
                        if (checkDrop(selected, shape) && !selected.equals(shape)) {
                            // two shapes overlap and the target is a legal one.
                            if (canBeDroppedShapes.contains(shape)) {
                                implementOnDrop(shape);
                                flag = 1;
                            } else {flag = 2;}
                        }
                    }
                    // if there is overlap but not legal, need to go back.
                    if(flag == 2){
                        selected.setX(shapeorigx);
                        selected.setY(shapeorigy);
                        invalidate();
                    }
                    for (Shapes shape : canBeDroppedShapes) {
                        Log.d("testGreenframe", "run here");
                        shape.setWithFrame(0);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:   // This action permits us to drag the current shape
                // Note before drag, store the shape's position,
                // in case the onDrop is not successful.
                // Note: This method contains bugs.
                if (selected != null) {
                    if (selected.getMovability()) {

                        float x = event.getX() - 0.5f * selected.getWidth();    // make mouse at center
                        float y = event.getY() - 0.5f * selected.getHeight();

                        Log.d("move++", Float.toString(x));
                        selected.setX(x);
                        selected.setY(y);
                        gameActivity.updatePosition(selected.getName(), x, y);
                        //currentPage = gameActivity.getPage(currentPage.getName());
                        //shapes = currentPage.getShapes();
                        invalidate();
                        Log.d("move", Float.toString(x));

                        setPageSize();


                        Log.d(TAG, "change ownership");
                        Log.d("event gety", String.valueOf(event.getY()));
                        Log.d("page height", String.valueOf(currentPage.getHeight()));
                        // check if shape in page or possession
                        if (selected.getY() + selected.getHeight() < currentPage.getHeight() && !selected.isInPage()) {
                            Log.d(TAG, "move to page");
                            // belong to page
                            selected.changeOwnership();   // inPage = true

                            gameActivity.updateOwnership(selected.getName(), currentPage.getName(), false);

                            currentPage.addShape(0,selected);
                            currentPossession.deleteShape(selected);
                            // geng duo cao zuo ??

                        } else if (selected.getY() + selected.getHeight() >= currentPage.getHeight() && selected.isInPage()) {
                            Log.d(TAG, "move to possession");
                            // belong to possession
                            selected.changeOwnership(); // inPage = false

                            gameActivity.updateOwnership(selected.getName(), currentPage.getName(), true);

                            currentPossession.addShape(0,selected);
                            currentPage.deleteShape(selected);

                        }

                    }
                }
                break;
        }
        return true;
    }
    /*
        #Shapes#
        This method prevents user from dragging to outside the screen
        Should included in Shapes.
        @ Input (x, y) is the center of bitmap
        @ Input (sw, sh) screen width and screen height
     */
    /*
    private boolean isOutSide(Shapes s, float x, float y, float sw, float sh){
        return ( x - 0.5f*s.getWidth() < 0 ||
                y - 0.5f*s.getHeight() <0 ||
                x + 0.5f*s.getWidth() > sw ||
                y + 0.5f*s.getHeight() > sh);
    }
*/


    /*
     * check if I click on carrots or on bunny to determine which page to go
     * deal with clause with onClick Triggers
     */

    public void checkClick(float x, float y){

        // get all shapes in currentPage
        shapes = currentPage.getShapes();
        // Any function that can return the clicked Object?
        for(int i=0; i<shapes.size(); i++) {
            if(shapes.get(i).getRectf().contains(x, y)) {
                Log.d("testboolean", "true");
                currentShape = shapes.get(i);
                // can get the original rectf 0,0,0,0,
                // no problem here to get current shape
                String currentShapeName = currentShape.getName();
                Log.d("testcurrentshape", currentShapeName);

                // *****************
                if(currentShape.getScript() != null) {
                    for (ShapeScript clause : currentShape.getScript()){
                        if(clause.getTrigger().equals("onClick")){
                            implementClause(clause);
                        }
                    }
                }
                // *****************

                invalidate();
                break;
            }
        }
    }

    private void implementScript(Shapes shape){
        // go through all scripts for each shape; check whether scripts are null or not.
        if(shape != null && shape.getScript() != null) {
            for (ShapeScript clause : shape.getScript()){
                Log.d(TAG, "go through script's clause");
                if(clause.getTrigger().equals("onEnter")){
                    implementClause(clause);
                }
            }
        }
    }


    /*
    * Following actions for given Clause.
    */
    private void implementClause(ShapeScript clause){

        // get Actions and their corresponding Objects(conducted by this action).
        ArrayList<String> actions = clause.getAction();
        ArrayList<String> dests = clause.getDest();
        //Shapes targetShape;
        // go through all actions and all objects
        for (int i = 0; i < actions.size(); i++) {
            switch (actions.get(i)) {
                // if action is goto, change current page to the target page
                // redraw through invalidate()
                // check onEnterPage case
                case "goto":
                    currentPage = gameActivity.getPage(dests.get(i));
                    invalidate();
                    onEnterPage();
                    break;
                case "play":
                    String mpstr = dests.get(i);
                    String PACKAGE_NAME = this.getContext().getPackageName();
                    int mpid = getResources().getIdentifier(mpstr, "raw", PACKAGE_NAME);
                    hooraySound = MediaPlayer.create(this.getContext(), mpid);
                    hooraySound.start();
                    break;
                case "hide":
                    // it's better to set different name for all shapes, even for shape in different page, unique name to map unique shape;

                    gameActivity.updateShapeVisibility(dests.get(i), false);
                    gameActivity.updateShapeClickability(dests.get(i), false);
                    currentPage = gameActivity.getPage(currentPage.getName());
                    shapes = currentPage.getShapes();
                    break;
                case "show":
                    gameActivity.updateShapeVisibility(dests.get(i), true);
                    gameActivity.updateShapeClickability(dests.get(i), true);
                    currentPage = gameActivity.getPage(currentPage.getName());
                    shapes = currentPage.getShapes();
                    break;
            }
        }
    }

    /*
    Return a list of shapes
    which can be dropped by the selected shape
    Draw a green rectangle of each corresponding shape
     */
    private ArrayList<Shapes> canBeDroppedShapes(Shapes selected){
        ArrayList<Shapes> potentialShapes = new ArrayList<Shapes>();
        if(shapes != null) {
            for (Shapes shape : shapes){
                if(shape != null && shape.getScript() != null) {
                    for (ShapeScript clause : shape.getScript()){
                        Log.d(TAG, "go through script's clause");
                        if(clause.getTrigger().equals("onDrop")){
                            String onDropTarget = clause.getOnDropTarget();
                            if (onDropTarget.equals(selected.getName())){
                                potentialShapes.add(shape);
                                // shape.drawRect();
                            }
                        }
                    }
                }
            }
        }
        return potentialShapes;
    }

    /*
    Return true if two shapes are overlapped
    else return false
     */
    private boolean checkDrop(Shapes selected, Shapes target){
        RectF selectedRect = selected.getRectf();
        if(target != null) {
            RectF targetRect = target.getRectf();
            // if there is intersection of the two rectangles
            if (RectF.intersects(selectedRect, targetRect)){
                return true;
            }
        }

        return false;

    }

    /*
    Implement a clause starting as onDrop
     */
    private void implementOnDrop(Shapes target){
        if(target.getScript() != null) {
            for (ShapeScript clause : target.getScript()){
                Log.d(TAG, "go through script's clause");
                if(clause.getTrigger().equals("onDrop")){
                    implementClause(clause);
                }
            }
        }
    }



}
