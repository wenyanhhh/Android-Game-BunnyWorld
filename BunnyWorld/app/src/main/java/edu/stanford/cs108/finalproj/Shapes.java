package edu.stanford.cs108.finalproj;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.*;
import android.graphics.drawable.shapes.Shape;
import android.os.Trace;

import java.util.ArrayList;

import android.util.Log;
import android.view.*;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by yiyangli on 2/27/18.
 */



public class Shapes {
    private static final String TAG = Shapes.class.getSimpleName();
    // =======ATTRIBUTES======

    public static final int RECT_TYPE = 0;
    public static final int PNG_TYPE = 1;
    public static final int TEXT_TYPE = 2;

    private int type;

    private float x;
    private float y;
    private float width;
    private float height;
    //private RectF rectf;

    private String shapeName;
    private String objName;

    private String parentName;
    // Each shape belongs to a particular Pages, or the possessions area.
    private Boolean inPage; // true if belong to page, false if belong to possessions
    private ArrayList<ShapeScript> scripts;

    private boolean visible;
    private boolean movable;
    private String text;
    private float textsize;
    private boolean clickable;
    private int withFrame;
    // withGrame = 0 no frame
    // 1 green
    // 2 red
    // 3 black

    // ====== CONSTRUCTOR ======
    public Shapes(String shapeName, float x, float y, float width, float height, String t_objName,
                  boolean t_visible, int t_withFrame, boolean t_inPage, boolean t_movable, String t_text, float t_textsize, boolean t_clickable) {
        this.shapeName = shapeName;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.objName = t_objName;
        this.scripts = new ArrayList<ShapeScript>();
        this.visible = t_visible;
        this.withFrame = t_withFrame;
        this.inPage = t_inPage;
        this.movable = t_movable;
        this.text = t_text;
        this.textsize = t_textsize;
        this.clickable = t_clickable;
    }

    // Default Setting of Editor Mode;
    public Shapes(String shapeName, String t_objName) {
        this.shapeName = shapeName;
        this.x = 200;
        this.y = 200;
        this.width = 150;
        this.height = 150;
        this.objName = t_objName;
        this.scripts = new ArrayList<ShapeScript>();
        this.visible = true;
        this.withFrame = 0;
        this.inPage = true;
        this.movable = true;

    }


//===============================================================================================================
    // ======METHODS======
    // ====== GET ======

    // get shape name, obj name
    public String getName() {return shapeName;}
    public String getObjName() {return objName;}

    // get x, y, width, height
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    // get rectf
    public RectF getRectf() {return new RectF(this.getX(),this.getY(),this.getX()+this.getWidth(),this.getY()+this.getHeight());}

    // get scripts
    public ArrayList<ShapeScript> getScript() {
        return scripts;
    }

    // get shape type
    public int getType() { return type; }

    // see if shape is visible
    public boolean getVisibility() {return visible; }
    public boolean getMovability() {return movable;}
    public String getText() {return text;}
    public boolean getClickability() {return clickable;}
    public float getTextsize() {return textsize;}
    public int getWithFrame() {return withFrame;}

    // check if shape is in page
    public boolean isInPage() {return inPage;}

    public String getParentName() {
        return parentName;
    }

    // ====== SET ======
    public void setShapeName(String name) {this.shapeName=name;}
    public void setX(float t_x) { this.x = t_x; }
    public void setY(float t_y) { this.y = t_y; }
    public void setWidth(float t_width) { this.width = t_width; }
    public void setHeight(float t_height) { this.height = t_height; }

    public void setVisible(boolean flag) { this.visible = flag; }
    public void setMovable(boolean flag) { this.movable = flag; }
    public void setWithFrame(int color) {this.withFrame = color;}
    public void addScript(String clause) {
        scripts.add(new ShapeScript(clause));
    }
    public void setText(String textstr) {this.text = textstr;}
    public void setTextsize(float textsizestr) {this.textsize = textsizestr;}
    public void setClickable (boolean flag) { this.clickable = flag;}
    /*
    // draw the shape
    public void draw(Canvas canvas, Context context, float sw, float sh) { // remember to pass the context (Activity) to your method.
        // hard code for now, use hash map in the future

        Log.d("visible", String.valueOf(visible));
        if (!visible) {
            return;
        }
        int obj = R.drawable.carrot;

        // check if draw text
        if (obj == -1) {
            Paint paint = new Paint();
            canvas.drawPaint(paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(16);
            canvas.drawText("My Text", 200, 200, paint);

            return;
        }

        // use shapeData


    //    need its objName
  //      once enter gameview
//        arraylist of all image names

//        hashmap key image name -> value bitmapdrawable(int)

//        shape.getObjName()

   //     Context context = imageView.getContext();
        //int id = context.getResources().getIdentifier(db.getname().getobjname(), "drawable", context.getPackageName());
        //imageView.setImageResource(id);



        if (objName.equals("carrot")) {
            obj = R.drawable.carrot;
        } else if (objName.equals("death")) {
            obj = R.drawable.death;
        } else if (objName.equals("duck")) {
            obj = R.drawable.duck;
        } else if (objName.equals("fire")) {
            obj = R.drawable.fire;
        }



        Log.d(TAG, "draw");
        BitmapDrawable obj_drawable = (BitmapDrawable) context.getResources().getDrawable(obj);
        Bitmap objectBitmap = obj_drawable.getBitmap();
        if (this.getX() < 0 ) {
            this.setX(0);
        } else if (this.getX() > sw - this.getWidth()) {
            this.setX((float) (sw - this.getWidth()));
        }

        if (this.getY() < 0) {
            this.setY(0);
        } else if (this.getY() > sh - this.getHeight()) {
            this.setY((float) (sh - this.getHeight()));
        }

        Log.d("shape.draw x", String.valueOf(this.getX()));
        Log.d("shape.draw y", String.valueOf(this.getY()));

        RectF rectf = new RectF(this.getX(),this.getY(),this.getX()+this.getWidth(),this.getY()+this.getHeight());

        canvas.drawBitmap(objectBitmap, null, rectf, null);

        if (withFrame == 1) {
            drawFrame(canvas,"green");
        } else if (withFrame == 2) {
            drawFrame(canvas, "red");
        } else if (withFrame == 3) {
            drawFrame(canvas, "black");
        }

        return;
    }
    */
    public void draw(Canvas canvas, Context context, float sw, float sh ,int obj) { // remember to pass the context (Activity) to your method.
        // hard code for now, use hash map in the future

        Log.d("visible", String.valueOf(visible));
        if (!visible) {
            return;
        }
        Log.d("shape.draw, getX", String.valueOf(this.getX()));

        // check if draw text

        Log.d(TAG, "draw");
        Log.d(TAG, String.valueOf(this.getX()));
        if (this.getX() < 0 ) {
            this.setX(0);
        } else if (this.getX() > sw - this.getWidth()) {
            this.setX( sw - this.getWidth());
        }
        Log.d(TAG, "setY");
        if (this.getY() < 0) {
            this.setY(0);
        } else if (this.getY() >= sh - this.getHeight()) {
            this.setY((float) (sh - this.getHeight()));
        }

        Log.d("shape.draw, getY", String.valueOf(this.getY()));
        Log.d("shape.draw, bottom", String.valueOf(this.getY() + this.getHeight()));
        Log.d("shape.draw, get width", String.valueOf(this.getWidth()));
        Log.d("shape.draw, get height", String.valueOf(this.getHeight()));
        Log.d("shape.draw, obj", String.valueOf(obj));
        if (obj == -1) {
            // draw text_icon
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(this.textsize);
            Log.d("testTextSize", Float.toString(this.textsize));
            Log.d("testShape", this.shapeName);
            canvas.drawText(this.text, this.getX(), this.getY(), paint);
            return;

        } else if (obj == 0) {
            // draw a gray rect
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.GRAY);
            rectPaint.setStyle(Paint.Style.FILL);
            //rectPaint.setStrokeWidth(5.0f);
            canvas.drawRect(getRectf(),rectPaint);

        } else {
            // draw image
            BitmapDrawable obj_drawable = (BitmapDrawable) context.getResources().getDrawable(obj);
            Bitmap objectBitmap = obj_drawable.getBitmap();
            RectF rectf = new RectF(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
            canvas.drawBitmap(objectBitmap, null, rectf, null);
        }

        if (withFrame == 1) {
            drawFrame(canvas,"green");
        } else if (withFrame == 2) {
            drawFrame(canvas, "red");
        } else if (withFrame == 3) {
            drawFrame(canvas, "black");
        }

        return;
    }

    public void draw_edit(Canvas canvas, Context context, float sw, float sh ,int obj) { // remember to pass the context (Activity) to your method.
        // hard code for now, use hash map in the future

        Log.d("visible", String.valueOf(visible));
        /*
        if (!visible) {
            return;
        }
        */
        Log.d("shape.draw, getX", String.valueOf(this.getX()));

        // check if draw text

        Log.d(TAG, "draw");
        Log.d(TAG, String.valueOf(this.getX()));
        if (this.getX() < 0 ) {
            this.setX(0);
        } else if (this.getX() > sw - this.getWidth()) {
            this.setX( sw - this.getWidth());
        }
        Log.d(TAG, "setY");
        if (this.getY() < 0) {
            this.setY(0);
        } else if (this.getY() >= sh - this.getHeight()) {
            this.setY((float) (sh - this.getHeight()));
        }

        Log.d("shape.draw, getY", String.valueOf(this.getY()));
        Log.d("shape.draw, bottom", String.valueOf(this.getY() + this.getHeight()));
        Log.d("shape.draw, get width", String.valueOf(this.getWidth()));
        Log.d("shape.draw, get height", String.valueOf(this.getHeight()));
        Log.d("shape.draw, obj", String.valueOf(obj));
        if (obj == -1) {
            // draw text_icon
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setTextSize(this.textsize);
            Log.d("testTextSize", Float.toString(this.textsize));
            Log.d("testShape", this.shapeName);
            canvas.drawText(this.text, this.getX(), this.getY(), paint);
            return;

        } else if (obj == 0) {
            // draw a gray rect
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.GRAY);
            rectPaint.setStyle(Paint.Style.FILL);
            //rectPaint.setStrokeWidth(5.0f);
            canvas.drawRect(getRectf(),rectPaint);

        } else {
            // draw image
            BitmapDrawable obj_drawable = (BitmapDrawable) context.getResources().getDrawable(obj);
            Bitmap objectBitmap = obj_drawable.getBitmap();
            RectF rectf = new RectF(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
            canvas.drawBitmap(objectBitmap, null, rectf, null);
        }

        if (withFrame == 1) {
            drawFrame(canvas,"green");
        } else if (withFrame == 2) {
            drawFrame(canvas, "red");
        } else if (withFrame == 3) {
            drawFrame(canvas, "black");
        }

        return;
    }

    public boolean inBound(float sw, float sh) {

        //Log.d("inb func", String.valueOf(sw));
        //Log.d("inb func", String.valueOf(sh));

        float centerX = this.getX() + 0.5f * this.getWidth();
        float centerY = this.getY() + 0.5f * this.getHeight();

        //Log.d("inb func", String.valueOf(centerX));
        //Log.d("inb func", String.valueOf(centerY));

        if ( centerX >= 0.5f * this.getWidth() &&
                centerX <= sw - 0.5f * this.getWidth() &&
                centerY >= 0.5f * this.getHeight() &&
                centerY <= sh - 0.5f * this.getHeight() ) {
            return true;
        }
        return false;
    }


    //public void changeOwnership(Pages page, Possession poss) {
    public void changeOwnership () {
        if (inPage) {
            // change to poss
            inPage = false;
        } else {
            // change to page
            inPage = true;
        }
    }

    //

    // add frame (red or green)
    private void drawFrame (Canvas canvas, String color) {
        Paint OutlinePaint = new Paint();

        if (color.equals("green")) {
            OutlinePaint.setColor(Color.GREEN);
        } else if (color.equals("black")) {
            OutlinePaint.setColor(Color.BLACK);
        } else if (color.equals("red")) {
            OutlinePaint.setColor(Color.RED);
        }

        OutlinePaint.setStyle(Paint.Style.STROKE);
        OutlinePaint.setStrokeWidth(5.0f);
        canvas.drawRect(getRectf(),OutlinePaint);
    }


    /*
        For debugging
     */
    @Override
    public String toString(){
        return getName();
    }

}
