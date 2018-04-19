package edu.stanford.cs108.finalproj;

import java.util.*;
import android.graphics.*;

/**
 * Created by wenshengli on 2/27/18.
 */

/*
#Page# You should be able to draw ALL shapes in your object.
        Or, you should tell your shapes to draw themselves.
 */

/*
constructor
getshapes(pageName) // get shapes on that page
draw() // draw all shapes on that page
trigger
*/

/*
1. A page is a rectangular area with a white background that can contain a number of "shape" objects.
2. Shapes or parts of shapes that fall outside the current page size are simply not drawn.
3. The overall document is made up of many pages. Only one page and its contents are visible at a time.
4. Certain actions within the program will determine which page is currently displayed.
5. There is a special page, which we will refer to as “page1”, where the game starts out when first run. Every element in the game,
6. including pages, has a unique "name" string that identifies it.
7. All the operations below that involve names or other text elements should not be case-sensitive.
*/

public class Pages {

    // ======CONSTANTS======
    public static final float DEFAULT_HEIGHT = 600; // set to be 100 for example
    public static final float DEFAULT_WIDTH = 800; // set to be 200 for example
    public static final String DEFAULT_NAME = "page1"; // special page

    // ======ATTRIBUTES======
    private float width;
    private float height;
    private String name;
    private ArrayList<Shapes> shapes;
    private Shapes selectShape;


    // private boolean visible;

//===============================================================================================================

    // ======METHODS======
    /*
     * Constructors (Create a page)
     */
    // page name, page size, shape list
    public Pages (){
        //this(DEFAULT_NAME, DEFAULT_HEIGHT,DEFAULT_WIDTH);
        this(DEFAULT_NAME);
    }

    public Pages(String t_name){ // , int height, int width, boolean visible
        this.name = t_name;
        this.height = DEFAULT_HEIGHT;
        this.width = DEFAULT_WIDTH;
        //this.visible = visible;
    }


    // ====== GET ======
    public String getName() {return name;}
    public ArrayList<Shapes> getShapes() {return shapes;}
    public float getWidth() {return width;}
    public float getHeight() {return height;}


    // ====== SET ======
    // change page name
    public void setName(String t_name) {this.name = t_name;}

    // resize the page
    public void resize(float t_width, float t_height) {
        this.width = t_width;
        this.height = t_height;
    }

    // add a shape to current page
    public void addShape(Shapes shape){
        // corner case
        if (shapes == null){
            shapes = new ArrayList<Shapes>();
        }
        shapes.add(shape);
    }

    public void addShape(int i, Shapes shape){
        // corner case
        if (shapes == null){
            shapes = new ArrayList<Shapes>();
        }
        shapes.add(i,shape);
    }












    // ====================================================

    //Delete shape from the current page
    public void deleteShape(Shapes shape){
        if (shapes.size()==0) return;
        shapes.remove(shape);
    }

    public void deleteShape(int i){
        //if (shapes.size()==0) return;
        shapes.remove(i);
    }

    /*
     * Select shape
     */
    public void selectShape(float x, float y) {
        for (int i = shapes.size() - 1;i >= 0; i --){
            Shapes shape = shapes.get(i);
            RectF rectF = new RectF(shape.getX(),shape.getY(),shape.getX()+shape.getWidth(),shape.getY()+shape.getHeight());
            if(rectF.contains(x,y)) {
                selectShape = shape;
                return;
            }
        }
        selectShape = null;
    }

    /* Shapes or parts of shapes that fall outside
     * the current page size are simply not drawn.
     */
    public boolean inBound(Shapes shape){
        return true;
    }

    /*
     * Clear the current page
     */
    public void clear(){
        shapes = new ArrayList<Shapes>();
    }
    // test

}
