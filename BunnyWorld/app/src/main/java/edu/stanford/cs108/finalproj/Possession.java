package edu.stanford.cs108.finalproj;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import java.util.*;

/**
 * Created by Li Guo on 2/27/18.
 */



public class Possession {

    // ====== ATTRIBUTES ======
    public static final int HEIGHT = 100; // set to be 100 for example
    public static final int WIDTH = 200; // set to be 200 for example

    public String name;
    public ArrayList<Shapes> shapeList;
    public float width;
    public  float height;

    // ====== Constructor ======
    public Possession(String t_name) {
        this.name = t_name;
        shapeList = new ArrayList<Shapes>();
    }

    // ==========================
    // ====== METHODS ======

    // ====== GET ======
    public String getName() {return name;}
    public ArrayList<Shapes> getShapes() {return shapeList;}

    public float getWidth() {return width;}
    public float getHeight() {return height;}

    // ====== SET ======
    public void rename(String name){
        this.name = name;
    }

    public void resize(float t_width, float t_height) {
        width = t_width;
        height = t_height;
    }

    // add shape to possession
    public void addShape(Shapes shape){
        if (shape.equals(null) ) {
            return;
        }
        shapeList.add(shape);
    }

    public void addShape(int i, Shapes shape){
        // corner case
        if (shape.equals(null)){
            shapeList = new ArrayList<Shapes>();
        }
        shapeList.add(i,shape);
    }

    public void deleteShape(Shapes shape) {
        if (shape.equals(null)) {
            return;
        }
        if (shapeList.size() == 0 ) {
            return;
        }
        shapeList.remove(shape);
    }

    public void deleteShape(int i){
        if (shapeList.size() == 0) return;
        shapeList.remove(i);
    }


    // ====================

    /* 
     * Determine if the shape is inside the Possession area or not.
     */
    public boolean containsShape(Shapes shape){
        float h = shape.getHeight();
        float y = shape.getY();
        // to be specified.
        return true;
    }

    /*
     * Clear the current page
     */
    public void clear(){
        shapeList = new ArrayList<Shapes>();
    }

}
