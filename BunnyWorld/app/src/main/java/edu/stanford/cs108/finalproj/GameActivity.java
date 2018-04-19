package edu.stanford.cs108.finalproj;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.*;

/**
 * Created by liguo on 3/2/18.
 */



public class GameActivity extends AppCompatActivity {
    private static final String TAG = GameView.class.getSimpleName();

    private Map<String, Pages> pageData = new HashMap<>();
    private Map<String, Shapes> shapeData = new HashMap<>();
    SQLiteDatabase db;
    String name, shapeList;


    // put an XML layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "game activity on create");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // activity is run after view!!!!
        init();
        getPageMap();
        Log.d(TAG, "Gameactivity create database");
    }

    public void init() {
        openDB();
        setUpPage();
        setUpShape();
        Log.d(TAG, "Finish Setup!!!!!!!");
        populatePage();
        //populateShape();
        Log.d(TAG, "Enter getpagemap!!!!!!!!!!!!");
        // put shapes objects into page, and add page to pageData
    }

    private void openDB() {
        db = openOrCreateDatabase("GameDB.db", MODE_PRIVATE, null);
        Log.d(TAG, "db is null ? " + (db == null)); // false: db is created
    }

    // *************************** temporary for testing *************

    // create page Table
    private void setUpPage() {
//        String clearStr = "Drop Table if Exists page;";
//        System.err.println(clearStr);
//        db.execSQL(clearStr);


        String setupStr = "create table if not exists page ("
                + "pageName TEXT, shapeList TEXT, "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        System.err.println(setupStr);
        db.execSQL(setupStr);
    }

    // create shape Table
    private void setUpShape() {
//        String clearStr = "Drop Table if Exists shape;";
//        db.execSQL(clearStr);

        String setupStr = "create table if not exists shape (" +
                "shapeName TEXT, shapeScript TEXT, " +
                "x float, y float, " +
                "width float, height float, imageName TEXT, visibility INT, movability INT, textStr TEXT, textSize float, clickability INT, " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
                ");";

        System.err.println(setupStr);
        db.execSQL(setupStr);
    }


    // add entries to page table
    private void populatePage() {
        Cursor cursor = db.rawQuery("select * from page", null);
        if(!cursor.moveToNext()) {
            String dataStr = "Insert into page Values "
                    + "('Page1',NULL, NULL)"
//                + "('Page1','page1door1 page1door2 page1door3 page1text1', NULL),"
//                + "('Page2','page2bunny1 page2door1 page2text1 page2carrot1', NULL),"
//                + "('Page3','page3fire page3text1 page3door page3carrot', NULL),"
//                + "('Page4','page4text1 page4door page4hungrybunny', NULL),"
//                + "('Page5','page5carrot1 page5carrot2 page5carrot3 page5text1', NULL)"
                    + ";";
            System.err.println(dataStr);
            db.execSQL(dataStr);
        }

    }

    // add entries to shape table
    private void populateShape() {
        String dataStr = "Insert into shape Values "
                + "('page1door1', 'onClick goto Page2;', 300, 300, 80, 80, 'door', 1, 0, '', 0, 1, NULL),"
                + "('page1door2', 'onClick goto Page3;', 550, 300, 80, 80, 'door', 0, 0, '', 0, 0, NULL),"
                + "('page1door3', 'onClick goto Page4;', 800, 300, 80, 80, 'door', 1, 0, '', 0, 1, NULL),"
                + "('page1text1', '', 200, 200, 100, 100, '', 1, 0, 'You are in a maze of twisty little passages, all alike', 36, 0, NULL),"
                + "('page2bunny1', 'onClick hide page2carrot1 play munching;onEnter show page1door2', 500, 5, 450, 400, 'gibhli2', 1, 0, '', 30, 1, NULL),"
                + "('page2door1', 'onClick goto Page1;', 200, 350, 80, 80, 'door', 1, 0, '', 0, 1, NULL),"
                + "('page2text1', '', 400, 400, 100, 100, '', 1, 0, 'Rub my tummy for a big surprise', 36, 0, NULL),"
                + "('page2carrot1', '', 300, 100, 100, 100, 'carrot', 1, 0, '', 0, 0, NULL),"
                + "('page3fire', 'onEnter play fire;', 300, 50, 200, 200, 'fire', 1, 0, '', 0, 0, NULL),"
                + "('page3text1', '', 300, 350, 100, 100, '', 1, 0, 'Eek! Fire-Room. Run away!', 36, 0, NULL),"
                + "('page3door', 'onClick goto Page2;', 800, 300, 80, 80, 'door', 1, 0, '', 0, 1, NULL),"
                + "('page3carrot', '', 800, 100, 100, 100, 'carrot', 1, 1, '', 0, 1, NULL),"
                + "('page4text1', '', 400, 350, 100, 100, '', 1, 0, 'You must appease the Bunny of Death!', 36, 0, NULL),"
                + "('page4door', 'onClick goto Page5', 800, 350, 80, 80, 'door', 0, 0, '', 36, 0, NULL),"
                + "('page4hungrybunny', 'onEnter play evillaugh;onDrop page3carrot hide page3carrot play munching hide page4hungrybunny show page4door;onClick play evillaugh;', 500, 5, 350, 300, 'badbunny', 1, 0, '', 0, 1, NULL),"
                + "('page5carrot1', '', 300, 150, 100, 100, 'carrot', 1, 0, '', 36, 0, NULL),"
                + "('page5carrot2', '', 550, 150, 100, 100, 'carrot', 1, 0, '', 36, 0, NULL),"
                + "('page5carrot3', '', 800, 150, 100, 100, 'carrot', 1, 0, '', 36, 0, NULL),"
                + "('page5text1', 'onEnter play hooray', 500, 300, 100, 100, '', 1, 0, 'You Win! Yay!', 36, 0, NULL),"
                + "('duck', 'onClick hide carrot;onEnter play munching', 0, 100, 100, 100, 'duck', 1, 1, '', 0, 1, NULL)"
                + ";";
        System.err.println(dataStr);
        db.execSQL(dataStr);
    }

    // ******************************


    /*
     * get Page info from page Table
     */
    private void getPageMap() {
        Cursor cursor = db.rawQuery("SELECT * FROM page;", null);

        while(cursor.moveToNext()) {
            String pageName = cursor.getString(0);

            String[] shapeList = new String[0];

            if(cursor.getString(1) != null) {
                shapeList = cursor.getString(1).split(" ");
            }

            // create a new Page with given pageName
            Pages page = new Pages(pageName);

            // go through all shapes in the Page, and then add all shapes to the Page
            for(int i=0; i<shapeList.length; i++) {

                // add current shape objects to the Page, pass in the unique shapeName, instead of imageName
                page.addShape(getShapesMap(shapeList[i]));

                Log.d(TAG, "page.getShapes == null ?" + (page.getShapes() == null));
            }

            // put current Page to Page Map
            Log.d(TAG,"ADD Page!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            pageData.put(pageName, page);

        }
    }

    /*
     * get Shape info from shape table; Set up pageData with pageName, shapes and scripts.
     */
    private Shapes getShapesMap(String shapeName) {

        // query for info for given shape
        Cursor cursor = db.rawQuery("SELECT * FROM shape where shapeName = \"" +shapeName + "\";", null);

        // go through all shapes with the specific Name.
        while(cursor.moveToNext()) {

            // get the imageType of current shape
            String imageName = cursor.getString(6);
            String textstr = cursor.getString(9);

            // Initialize current shape via shapeName and imageName
            Shapes shape = new Shapes(shapeName, 0, 0, 100, 100, imageName, true, 0, true, true, textstr, 36, true);
            Log.d(TAG, "newly added shape: " + shape);

            // Constructed scripts for current shape, scripts are separated by ';'
            String scriptList = cursor.getString(1);
            if(cursor.getString(1) != null) {
                String[] scripts = scriptList.split(";");


                // Go through all scripts and add them to the current shape
                for(int i=0; i<scripts.length; i++) {
                    shape.addScript(scripts[i]);

                    // DEBUGER
                    Log.d(TAG, scripts[i]);
                }
            }

            // Set shape's coordinates and width and height
            float x = cursor.getFloat(2);
            float y = cursor.getFloat(3);
            float width = cursor.getFloat(4);
            float height = cursor.getFloat(5);
            boolean visibility = cursor.getInt(7) == 1 ? true: false;
            boolean movability = cursor.getInt(8) == 1 ? true: false;
            float textsize = cursor.getFloat(10);
            boolean clickability = cursor.getInt(11) == 1 ? true: false;

            shape.setX(x);
            shape.setY(y);
            shape.setWidth(width);
            shape.setHeight(height);
            shape.setVisible(visibility);
            shape.setMovable(movability);
            shape.setText(textstr);
            shape.setTextsize(textsize);
            shape.setClickable(clickability);
            if (clickability == true){
                Log.d("inputclickable",shapeName + ' '+ "clickable");
            } else Log.d("inputclickable",shapeName + ' '+ "unclickable");


            // put current Shape to Shape Map.
            shapeData.put(shapeName, shape);
            Log.d(TAG, "ADD SHAPE!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            Log.d(TAG, "shape.getScripts == null ?" + (shape.getScript() == null));

        }
        Log.d(TAG, "newly added shape in return: " + shapeData.get(shapeName));
        return shapeData.get(shapeName);
    }



    public Pages getPage(String pageName) {
        //init();
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        getPageMap();
        return pageData.get(pageName);
    }

    public Shapes getShape(String shapeName) {
        //init();
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        getPageMap();
        Log.d(TAG, "GameView get Page: Activity " + (shapeData.containsKey(shapeName)));

        return shapeData.get(shapeName);
    }

    public Map<String, Shapes> getShapeData(){
        return shapeData;
    }


    // for visibility
    public void updateShapeVisibility(String shapeName, boolean visibility) {
        //init();
        // update info for given shape in database
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        int vis = visibility ? 1:0;

        String updateStr = "Update shape "
                + "set visibility = " + vis
                + " where shapeName = \"" + shapeName
                + "\" ;";
        System.err.println(updateStr);
        db.execSQL(updateStr);

        getPageMap();

        shapeData.get(shapeName).setVisible(visibility);
    }

    public void updateShapeClickability(String shapeName, boolean clickability) {
        // update info for given shape in database
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        int cli = clickability ? 1:0;

        String updateStr = "Update shape "
                + "set clickability = " + cli
                + " where shapeName = \"" + shapeName
                + "\" ;";
        System.err.println(updateStr);
        db.execSQL(updateStr);

        getPageMap();

        shapeData.get(shapeName).setClickable(clickability);
    }

    public void updatePosition(String shapeName, float x, float y) {
        //init();
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        String updateStr = "Update shape "
                + "set x = " + x
                + " where shapeName = \"" + shapeName
                + "\" ;";
        System.err.println(updateStr);
        db.execSQL(updateStr);

        String updateStr2 = "Update shape "
                + "set y = " + y
                + " where shapeName = \"" + shapeName
                + "\" ;";
        System.err.println(updateStr2);
        db.execSQL(updateStr2);
        getPageMap();

    }

    public void updateOwnership(String shapeName, String parentPage, boolean leavePage) {
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
//        String updateStr = "Update page "
//                + "set pageName = " + parentPage
//                + " where shapeList like '%" + shapeName
//                + "%' ;";
//        System.err.println(updateStr);
//        db.execSQL(updateStr);

        if(leavePage) {
            // leave current page and get into Possession

            Cursor cursor = db.rawQuery("SELECT * FROM page where pageName = \""
                    + parentPage + "\";", null);

            if(cursor.moveToNext()) Log.d(TAG, "Cursor works!!!!!!!!!!!!!");
            List<String> shapeList = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));

            // delete given shapeName from given page entry
            shapeList.remove(shapeName);

            String shapeStr = "";
            for(String each : shapeList) {
                shapeStr += " " + each;
            }

            if (shapeStr.length() > 0) shapeStr = shapeStr.substring(1);
            String ownerStr = "Update page "
                    + "set shapeList = '" + shapeStr
                    + "' where pageName = \"" + parentPage
                    + "\" ;";

            System.err.println(ownerStr);
            db.execSQL(ownerStr);

        } else {
            // get into current Page and leave Possession area
            Cursor cursor = db.rawQuery("SELECT * FROM page where pageName = \""
                    + parentPage + "\";", null);

            cursor.moveToNext();
            List<String> shapeList = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));

            shapeList.add(shapeName);

            String shapeStr = "";
            for(String each : shapeList) {
                shapeStr += " " + each;
            }

            shapeStr = shapeStr.substring(1);

            String ownerStr = "Update page "
                    + "set shapeList = '" + shapeStr
                    + "' where pageName = \"" + parentPage
                    + "\" ;";

            System.err.println(ownerStr);
            db.execSQL(ownerStr);
        }

        // update pageData and shapeData by going through all elements in database
        getPageMap();

        Log.d(TAG, "after onwership change, current page has number of shapes: "
        + pageData.get(parentPage).getShapes().size() + " !!!!!!!!!!!!");
    }


}



