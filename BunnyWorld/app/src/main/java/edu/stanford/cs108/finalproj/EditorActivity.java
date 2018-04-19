package edu.stanford.cs108.finalproj;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL;

/**
 * Created by liguo on 3/2/18.
 */


// An activity is a user interface for carrying out some task, and is closer to what you might think of as a Form or Window in other frameworks. A View is a self-controlled rectangular section of a window that interaction can occur in, and is a much lower-level representation of UI. It's closer to what you might think of when you hear Control or Widget in a different framework.

// The visual content of a window is provided by a hierarchy of views, which are objects derived from the base View class. Each and every view controls a rectangular space within the window. A parent view contains and organizes the layout of its children; leaf views, at the bottom of the hierarchy, actually draw in the rectangles they control and respond to user actionStr. Thus, a view is where an activity's interaction with the user actually takes place.

public class EditorActivity extends AppCompatActivity {

    SQLiteDatabase db;


    // Our current shape/page name list and list view
    ArrayList<String> pageList;     // all pages
    ListView pageListView = null;
    ArrayList<String> soundList;    // possible sound selections
    ListView soundListView = null;
    ArrayList<String> shapeList;    // Possible selections
    ListView shapeListView = null;
    ArrayList<String> allShapeList; // all shapes
    ListView allShapeListView;

    // store pageData and shapeData
    HashMap<String, Pages> pageData = new HashMap<>();
    HashMap<String, Shapes> shapeData = new HashMap<>();

    //====== These are used for editor UI========


    // whenever you want to redraw something, just reset currentPageName, then call invalidate(): ondraw will get info for currentPage directly.
    String currentPageName;     // page name shown currently
    String currentShapeName;    // shape shown currently
    String oldShapeName = "";
    String copiedShapeName;
    String currentScripts;
    String selectedOnDropShapeStr;
    String selectedImageStr;
    int onDropMode;
    String selectedPageStr;     // page name selected by user from the list
    String selectedSoundStr;    // sound name selected by user from the list
    String actionStr;           // current action string
    String selectedShapeStr;    // shape name selected by user in the list
    String selectedShapeType;   // Not sure want to do
    String selectedShowOrHideShape; // shape name that will be shown or hidden
    String selectedCurrentShapeStr;

    EditorView editorView;

    int pageCount;
    int shapeCount;
    //====== These are used for editor UI========

    Pages currentPage;
    Shapes currentShape;



    public Pages getPage() {
        openDB();
        updateHashMap();
        return pageData.get(currentPageName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        editorView = findViewById(R.id.editorView);
        db = openOrCreateDatabase("GameDB.db", MODE_PRIVATE, null);
        init();
        shapeButtonSetup();



    }

    public void updatePosition(String shapeName, float x, float y){
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

        updateHashMap();
        currentShape = shapeData.get(currentShapeName);
        currentPage = pageData.get(currentPageName);

        // call editorView to redraw:
        editorView.setCurrentPage(pageData.get(currentPageName));

    }

    public void setSelected(String shapeName){

        currentShapeName = shapeName;


        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        updateHashMap();
        //Log.d("current shape name", ""+shapeData.get(currentShapeName).getName());

        currentShape = shapeData.get(currentShapeName);
        currentShape.setWithFrame(3);
        currentPage = pageData.get(currentPageName);

        // call editorView to redraw:
        editorView.setCurrentPage(pageData.get(currentPageName));
    }



    // ================== setUp database =====================
    public void init() {
        openDB();
        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        setUpPage();
        setUpShape();
        updateHashMap();
        pageCount = pageData.size() + 1;
        Log.d("pageCount", ""+pageData.size());
        shapeCount = shapeData.size() + 1;
        // put shapes objects into page, and add page to pageData
    }

    private void openDB() {
        db = openOrCreateDatabase("GameDB.db", MODE_PRIVATE, null);
    }

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

        Cursor cursor = db.rawQuery("select * from page where pageName = 'Page1';", null);
        while(cursor.moveToNext()) {
            if(cursor.getString(0) == null || cursor.getString(0).equals("")) {
                String defaultStr = "Insert into page Values "
                        + "(\"Page1\", NULL, NULL)"
                        + ";";
                System.err.println(defaultStr);
                db.execSQL(defaultStr);
            }
        }
        if (cursor!=null){ cursor.close(); }

        // setup currentPage as Page1
        currentPageName = "Page1";

        // add newly created page to hashMap
        pageData.put(currentPageName, new Pages(currentPageName));
        currentPage = pageData.get(currentPageName);

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

    // reset database!!!
    private void resetHelper() {
        String clearStr = "Drop Table if Exists page;";
        System.err.println(clearStr);
        db.execSQL(clearStr);

        String setupStr = "create table if not exists page ("
                + "pageName TEXT, shapeList TEXT, "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT"
                + ");";

        System.err.println(setupStr);
        db.execSQL(setupStr);

        String defaultStr = "Insert into page Values "
                + "(\"Page1\", NULL, NULL)"
                + ";";
        System.err.println(defaultStr);
        db.execSQL(defaultStr);

        // ===================================

        String clearShapeStr = "Drop Table if Exists shape;";
        db.execSQL(clearShapeStr);

        setUpShape();
        pageCount = 2;
        shapeCount = 1;
        editorView.setCurrentPage(new Pages("Page1"));
    }

    public Map<String, Shapes> getShapeData(){
        return shapeData;
    }



//========================= UI Design ============================================//



    // to create a list of pages in DB
    public void createPageList(){
        /*
            #DB# You should create an ArrayList of all pages in DB
                Assign these to pageList.
         */

        pageListHelper();

        //pageList = new ArrayList<>(Arrays.asList("Page1", "Page2", "Page11"));
        //Strange inputs
//        pageList = new ArrayList<>(Arrays.asList("Page1", "Page2", "Page11", "Ppppage2",
//                "Page 2", "Page   2", "Page12222222", "Page2222222222", "page1", "kkkkkk",
//                "mmmmmm","gggggg", "ffffffff", "okokok"));

        // setUp a ListView here
        pageListView = new ListView(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.popwindow_list_view,
                R.id.page_list_menu, pageList);   // what is page_list_menu used for??

        pageListView.setAdapter(adapter);

        // setUp an ItemClickListener for ListView items
        pageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get all items in the view
                ViewGroup viewgroup = (ViewGroup) view;

                // can you describe this a bit?????
                TextView textView = (TextView) viewgroup.findViewById(R.id.page_list_menu);

                // set clicked view as selected
                view.setSelected(true);

                // get selected pageName
                selectedPageStr = pageListView.getItemAtPosition(i).toString();
                Toast.makeText(EditorActivity.this, textView.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }

        });

    }



    /*
        Helper function for createPageList
       create an ArrayList of all pages in DB
       Assign these to pageList.
    */
    private void pageListHelper() {
        pageList = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from page;", null);
        while(cursor.moveToNext()) {
            pageList.add(cursor.getString(0));
        }
        if (cursor!=null){ cursor.close(); }
    }

    /*
        Create a list of all shapes info in database.
     */
    private void createAllShapeList(){
        allShapeListHelper();

        /*
            #DB#
         */
        //allShapeList = new ArrayList<>(Arrays.asList("shape1", "shape2", "shape3"));
        allShapeListView = new ListView(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.popwindow_list_view,
                R.id.page_list_menu, allShapeList);   // what is page_list_menu used for??

        allShapeListView.setAdapter(adapter);

        // setUp an ItemClickListener for ListView items
        allShapeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // get all items in the view
                ViewGroup viewgroup = (ViewGroup) view;

                // can you describe this a bit?????
                TextView textView = (TextView) viewgroup.findViewById(R.id.page_list_menu);

                // set clicked view as selected
                view.setSelected(true);

                // get selected pageName
                selectedShowOrHideShape = allShapeListView.getItemAtPosition(i).toString();
                selectedOnDropShapeStr = allShapeListView.getItemAtPosition(i).toString();
                selectedCurrentShapeStr = allShapeListView.getItemAtPosition(i).toString();
                Toast.makeText(EditorActivity.this, textView.getText().toString(),
                        Toast.LENGTH_SHORT).show();

            }

        });
    }

    private void allShapeListHelper() {
        allShapeList = new ArrayList<>();
        Cursor cursor = db.rawQuery("Select * from shape;", null);
        while(cursor.moveToNext()) {
            allShapeList.add(cursor.getString(0));
        }
        if (cursor!=null){ cursor.close(); }
    }


    // create a list of Sound(fixed list)
    public void createSoundList(){
        soundList = new ArrayList<>(Arrays.asList("carrotcarrotcarrot", "evillaugh",
                "fire", "hooray", "munch", "munching", "woof"));

        soundListView = new ListView(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.popwindow_list_view,
                R.id.page_list_menu, soundList);
        soundListView.setAdapter(adapter);

        soundListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //ViewGroup viewgroup = (ViewGroup) view;
                //TextView textView = (TextView) viewgroup.findViewById(R.id.page_list_menu);
                view.setSelected(true);
                selectedSoundStr = soundListView.getItemAtPosition(i).toString();
                tryToPlaySound(selectedSoundStr);
                Toast.makeText(EditorActivity.this, "Playing " + selectedSoundStr,
                        Toast.LENGTH_SHORT).show();
            }

        });


    }


    // create a list of shapes that can be selected (fixed list); create a shapeList View, and update selectedShapeStr
    // upto select an item on shape List View
    public void createShapeList(){
        shapeList = new ArrayList<>(Arrays.asList("text_icon", "carrot", "death",
                "duck", "fire", "mystic", "door"));

        ArrayList<Integer> imageID = new ArrayList<>(Arrays.asList(R.drawable.text_icon,
                R.drawable.carrot,
                R.drawable.death, R.drawable.duck, R.drawable.fire,
                R.drawable.mystic, R.drawable.door));
        shapeListView = new ListView(this);

        ShapeCustomListAdapter adapter = new ShapeCustomListAdapter(EditorActivity.this, shapeList, imageID);
        shapeListView.setAdapter(adapter);

        shapeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                selectedShapeStr = shapeListView.getItemAtPosition(i).toString();
                Toast.makeText(EditorActivity.this, selectedShapeStr + " is selected",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void shapeButtonSetup(){
        Button shapeAddBtn = findViewById(R.id.add_shape_menu);
        Button shapeShowBtn = findViewById(R.id.shape_show_all_menu);
        Button shapeEditBtn = findViewById(R.id.shape_edit_menu);
        Button shapeCopyBtn = findViewById(R.id.shape_copy_menu);
        Button shapePasteBtn = findViewById(R.id.shape_paste_menu);
        Button shapeDeleteBtn = findViewById(R.id.shape_delete_menu);

        shapeAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addShape();
            }
        });
        shapeShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllShapes();
            }
        });
        shapeEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setProperty();
            }
        });

        shapeCopyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyShape();
            }
        });
        shapePasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pasteShape();
            }
        });

        shapeDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shapeDelete();
            }
        });

    }



    // Create menu: call directly??
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.editor_menu, menu);
        return true;
    }


    // Choose Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){

            //============Page Options===========================
            case R.id.create_page_menu:
                newPage();
                break;
            case R.id.change_page_menu:
                changePage("Change Page",false, false);
                break;
            case R.id.delete_page_menu:
                changePage("Delete Page",true, false);
                break;
            case R.id.rename_page_menu:
                changePage("Rename Page",false,true);
                break;
            case R.id.clear_page_menu:
                /*
                    Easy, do something in DB.
                 */
                clearPage();
                break;
            //case R.id.undo_menu:
                /*
                    Extensions.
                 */
            //    break;
            //============Page Options===========================

            //============Shape Options===========================
            //case R.id.add_shape_menu:
            //    addShape();
            //    break;
            case R.id.shape_click_goto_page_menu:
                setGoTo("onClick ");
                break;
            case R.id.shape_click_play_sound_menu:
                setPlaySound("onClick ");
                break;
            case R.id.shape_click_show_menu:
                setShow("onClick ");
                break;
            case R.id.shape_click_hide_menu:
                setHide("onClick ");
                break;

            case R.id.shape_enter_goto_page_menu:
                setGoTo("onEnter ");
                break;
            case R.id.shape_enter_play_sound_menu:
                setPlaySound("onEnter ");
                break;
            case R.id.shape_enter_show_menu:
                setShow("onEnter ");
                break;
            case R.id.shape_enter_hide_menu:
                setHide("onEnter ");
                break;

            case R.id.shape_drop_goto_page_menu:
                onDropStarter(0);
                break;
            case R.id.shape_drop_play_sound_menu:
                onDropStarter(1);
                break;
            case R.id.shape_drop_show_menu:
                onDropStarter(2);
                break;
            case R.id.shape_drop_hide_menu:
                onDropStarter(3);
                break;
            case R.id.play_bunny_world_menu:
                bunnyMode();
                break;
            case R.id.db_reset_menu:
                resetHelper();
                break;

            //case R.id.shape_show_all_menu:
            //    showAllShapes();
            //    break;
            //case R.id.shape_show_script_menu:
                /*
                    Extensions????????
                 */
            //    break;
            //case R.id.shape_edit_menu:
            //    setProperty();
            //    break;
            //case R.id.shape_copy_menu:
                /*
                    Extensions
                 */
            //    break;
            //case R.id.shape_paste_menu:
                /*
                    Extensions
                 */
            //    break;
            //case R.id.shape_delete_menu:
                /*
                   Easy, delete it from database
                 */
            //    break;
            //case R.id.shape_change_text_menu:
            //    break;

            //============Shape Options===========================

        }
        return true;
    }

    public void onDropStarter(int mode){
        /*
            mode = 0; goto
            mode = 1; play
            mode = 2; show
            mode = 3; hide
         */
        createAllShapeList();
        onDropMode = mode;
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);
        layout.addView(allShapeListView);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                final String starter = "onDrop " + selectedOnDropShapeStr + " ";

                if (onDropMode==0){
                    setGoTo(starter);
                } else if (onDropMode==1){
                    setPlaySound(starter);
                } else if (onDropMode==2){
                    setShow(starter);
                }else if (onDropMode ==3){
                    setHide(starter);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle("Select a shape that can be dropped on the selected shape ");
        dialog.show();
    }

    /*
        clear page
     */
    private void clearPage() {
        // ================= update page table when clearing selected page ==========

        Cursor cursor = db.rawQuery("SELECT * FROM page where pageName = \""
                + currentPageName + "\";", null);
        while(cursor.moveToNext()) {
            Log.d(TAG, "clear Page !!!!!!!!!!!! " + cursor.getString(1));
            if(cursor.getString(1) != null) {
                List<String> shapeList = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));

                // delete all shapes in current page from shape table
                for(String each : shapeList) {
                    String deleteStr = "delete from shape where shapeName = '"
                            + each + "';";

                    System.err.println(deleteStr);
                    db.execSQL(deleteStr);
                }
            }

        }

        if (cursor!=null){ cursor.close(); }

        String dataStr = "Update page set shapeList = NULL where pageName = \""
                + currentPageName + "\";";

        System.err.println(dataStr);
        db.execSQL(dataStr);

        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        updateHashMap();
        currentPage = pageData.get(currentPageName);
        editorView.setCurrentPage(currentPage);
    }

    /*
        Create a new page dialog
    */
    public void newPage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        View createView = getLayoutInflater().inflate(R.layout.popwindow_editor,null);
        final EditText createPageInput = (EditText) createView.findViewById(R.id.create_page_str_menu);
        createPageInput.setText("Page" + pageCount++);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){

                createPageList();
                selectedPageStr = createPageInput.getText().toString();

                if (pageList.contains(selectedPageStr)){
                    Toast.makeText(EditorActivity.this,
                            "Page Name Already Exists", Toast.LENGTH_SHORT).show();
                } else {
                /*
                    #DB# You should add a new page, named createdPage into DB
                 */

                    String createStr = "Insert into page values (\""
                            + selectedPageStr
                            + "\",NULL, NULL);";

                    System.err.println(createStr);
                    db.execSQL(createStr);

                    Toast.makeText(EditorActivity.this,
                            selectedPageStr + " is added",
                            Toast.LENGTH_SHORT).show();

                    pageData = new HashMap<>();
                    shapeData = new HashMap<>();
                    updateHashMap();

                    currentPageName = selectedPageStr;
                    currentPage = pageData.get(selectedPageStr);

//                currentPage = new Pages(selectedPageStr);

                    editorView.setCurrentPage(currentPage);
                }
            }
        });
        builder.setNegativeButton("Cancel",null);

        builder.setView(createView);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Create a new page:");
        dialog.show();
    }

    private static final String TAG = GameView.class.getSimpleName();

    public void changePage(String title, final boolean delete, final boolean rename){ // can also be used for deleting page.
        createPageList();

        final AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText renameText = new EditText(EditorActivity.this);

        String renameStr = "";
        if (rename){
            renameText.setText("Add new name here");
            //renameStr = renameText.getText().toString();
            layout.addView(renameText);
        }
        builder.setView(layout);
        layout.addView(pageListView);


        //final String finalRenameStr = renameStr;
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                if ( (delete || rename ) && selectedPageStr.equals("Page1")){
                    Toast.makeText(EditorActivity.this,
                            "Page1 cannot be edited",
                            Toast.LENGTH_SHORT).show();
                } else {
                 /*
                    #DB#
                     if (delete) -> delete a page from DB
                     else if (rename) -> replace selectedPageStr with renameStr in DB
                     else -> change to the page, which is called selectedPageStr

                     and redraw the current canvas & call createPage();
                 */

                    // get TEXT in the selected Dialog entry
                    if(delete && pageData.size() > 1) {

                        Cursor cursor = db.rawQuery("Select * from page where pageName = \""
                                + selectedPageStr + "\";", null);

                        Log.d(TAG, "selectedPageStr: !!!!!!!!!" + selectedPageStr);

                        while(cursor.moveToNext()) {
                            String[] shapeList = cursor.getString(0).split(" ");

                            for(int i=0; i<shapeList.length; i++) {
                                String deleteShapeStr = "Delete from shape "
                                        + "where shapeName = \"" + shapeList[i]
                                        + "\";";
                                System.err.println(deleteShapeStr);
                                db.execSQL(deleteShapeStr);

                            }
                        }

                        if (cursor!=null){ cursor.close(); }

                        String deleteStr = "Delete from page "
                                + "where pageName = \"" + selectedPageStr
                                + "\";";
                        System.err.println(deleteStr);
                        db.execSQL(deleteStr);


                        Toast.makeText(EditorActivity.this,selectedPageStr + " is changed.", Toast.LENGTH_SHORT).show();

                        selectedPageStr = "";
                        selectedShapeStr = "";

                        pageList.remove(selectedPageStr);

                        pageData = new HashMap<>();
                        shapeData = new HashMap<>();
                        updateHashMap();
                        currentPageName = pageList.get(0);
                        currentPage = pageData.get(pageList.get(0));
                        editorView.setCurrentPage(currentPage);

                    } else if(rename && selectedPageStr != null) {   //#TODO#: ERROR HANDLING
                        // you should check whether a pageName is selected here!!!
                        final String finalRenameStr = renameText.getText().toString();
                        if (pageList.contains(finalRenameStr)){
                            Toast.makeText(EditorActivity.this,
                                    "Name already existed. Please try another one.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String renamedStr = "Update page "
                                    + "set pageName = \"" + finalRenameStr
                                    + "\" where pageName = \"" + selectedPageStr
                                    + "\" ;";
                            System.err.println(renamedStr);
                            db.execSQL(renamedStr);
                            Toast.makeText(EditorActivity.this,selectedPageStr + " is changed.", Toast.LENGTH_SHORT).show();
                        }

                        pageData = new HashMap<>();
                        shapeData = new HashMap<>();
                        updateHashMap();
                        currentPageName = finalRenameStr;
                        currentPage = pageData.get(currentPageName);
                        editorView.setCurrentPage(currentPage);

                    } else if (selectedPageStr != null){

                        currentPageName = selectedPageStr;
                        currentPage = pageData.get(currentPageName);
                        editorView.setCurrentPage(pageData.get(selectedPageStr));
                        // call onDraw in EditorView to redraw on the canvas: change to another page
                        selectedShapeStr = "";
                        //selectedPageStr = name;
                        Toast.makeText(EditorActivity.this,selectedPageStr + " is changed.", Toast.LENGTH_SHORT).show();
                        pageData = new HashMap<>();
                        shapeData = new HashMap<>();
                        updateHashMap();
                        currentPage = pageData.get(currentPageName);
                        editorView.setCurrentPage(currentPage);
                        selectedPageStr = "";
                    }


                }

            }
        });
        builder.setNegativeButton("cancel",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle(title);
        dialog.show();
    }

    public void showAllShapes(){
        createAllShapeList();

        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);
        layout.addView(allShapeListView);


        builder.setPositiveButton("Select", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                if (selectedCurrentShapeStr!=null) setSelected(selectedCurrentShapeStr);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setTitle("All Shapes");
        dialog.show();
    }

    public void addShape(){
        currentShapeName = "Shape" + shapeCount++;

        // create a list of shapes to be shown for shapes selection
        // create and show a shapeList View, and update selectedShapeStr
        createShapeList();

        // deal with Create in the shape list
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        builder.setView(layout);
        layout.addView(shapeListView);


        builder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                /*
                    #DB#
                     add selectedShapeStr into current page in DB
                     you can assign default values, e,g, at (0,0)

                 */


                addShapeHelper();

                Toast.makeText(EditorActivity.this,
                        currentShapeName + " is created.",
                        Toast.LENGTH_SHORT).show();

                pageData = new HashMap<>();
                shapeData = new HashMap<>();
                updateHashMap();
                currentShape = shapeData.get(currentShapeName);
                currentPage = pageData.get(currentPageName);

                setProperty();

                pageData = new HashMap<>();
                shapeData = new HashMap<>();
                updateHashMap();
                currentShape = shapeData.get(currentShapeName);
                currentPage = pageData.get(currentPageName);

                // call editorView to redraw:
                editorView.setCurrentPage(pageData.get(currentPageName));


            }
        });
        builder.setNegativeButton("cancel",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Shape Selection");
        dialog.show();
    }

    private void addShapeHelper() {

        // =========== update page table when adding shape to current page =====

        // currentPage is set as "Page1" in setPage()!!!
        Cursor cursor = db.rawQuery("SELECT * FROM page where pageName = \""
                + currentPageName + "\";", null);

        cursor.moveToNext();
        Log.d(TAG, "current !!!!!!!!!!! page !!!!!!!!! " + currentPageName);
        //String a = cursor.getString(1);
        //Log.d(TAG, "add !!!!!!!!!!! shape !!!!!!!!! " + a);
        String shapeStr = "";
        if(cursor.getString(1) != null) {
            List<String> shapeList = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));

            // add currentShapeName to shapeList
            //shapeList.add(currentShapeName);

            for(String each : shapeList) {
                shapeStr += " " + each;
            }

        }
        if (cursor!=null){ cursor.close(); }

        shapeStr += " " + currentShapeName;
        if (shapeStr.length() > 0) shapeStr = shapeStr.substring(1);
        String ownerStr = "Update page "
                + "set shapeList = '" + shapeStr
                + "' where pageName = \"" + currentPageName
                + "\" ;";

        System.err.println(ownerStr);
        db.execSQL(ownerStr);

        // =======================end for page table ==========================


        // ================= update shape table when adding shape to current page ==========


        String dataStr;
        if (selectedShapeStr.equals("text_icon")){
            dataStr = "Insert into shape Values (\""
                    + currentShapeName + "\", '', 200, 200, 150, 150, '', 1,1,'text', 36, 1, NULL);";
        } else {
            // db for newly created shape: without scripts
            dataStr = "Insert into shape Values (\""
                    // shapeName got from EditText!!!!!!!!!!!!!!!!
                    + currentShapeName + "\", NULL, 200, 200, 150, 150, \""
                    //+ currentPageName + "\", 1, 1, NULL);";
                    + selectedShapeStr + "\", 1,1,'', 0, 1, NULL);";
        }

        // call editorView to redraw:
//        currentShape = new Shapes(currentShapeName, selectedShapeStr);
//        currentPage.addShape(currentShape);

        System.err.println(dataStr);
        db.execSQL(dataStr);
    }




    public void copyShape(){

        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        updateHashMap();
        //currentPage = pageData.get(currentPageName);
        //currentShape = shapeData.get(currentShapeName);
        copiedShapeName = currentShapeName + "-copy";
    }

    public void pasteShape(){

        // currentPageName is the current page

        //currentShapeName = copiedShapeName;

        if(copiedShapeName != null) {
            currentShapeName = copiedShapeName.replace("-copy", "");
        }

        pastePageTable();
        pasteShapeTable();

        pageData = new HashMap<>();
        shapeData = new HashMap<>();
        updateHashMap();
        currentPage = pageData.get(currentPageName);
        editorView.setCurrentPage(currentPage);

    }

    private void pastePageTable() {
        // currentPage is set as "Page1" in setPage()!!!
        Cursor cursor = db.rawQuery("SELECT * FROM page where pageName = \""
                + currentPageName + "\";", null);

        cursor.moveToNext();

        String shapeStr = "";
        if(cursor.getString(1) != null) {
            List<String> shapeList = new ArrayList<>(Arrays.asList(cursor.getString(1).split(" ")));

            for(String each : shapeList) {
                shapeStr += " " + each;
            }
        }
        if (cursor!=null){ cursor.close(); }

        shapeStr += " " + copiedShapeName;
        if (shapeStr.length() > 0) shapeStr = shapeStr.substring(1);
        String ownerStr = "Update page "
                + "set shapeList = '" + shapeStr
                + "' where pageName = \"" + currentPageName
                + "\" ;";

        System.err.println(ownerStr);
        db.execSQL(ownerStr);
    }


    private void pasteShapeTable() {
        // if setProperty() is called directly after addShape(), currentShapeName is default shapeName
//        Cursor cursor = db.rawQuery("select * from shape where shapeName = '" + currentShapeName + "';", null);
//        while(cursor.moveToNext()) {
//            currentScripts = cursor.getString(1);
//            float x = cursor.getFloat(2);
//            float y = cursor.getFloat(3);
//            float width = cursor.getFloat(4);
//            float height = cursor.getFloat(5);
//            String objName = cursor.getString(6);
//            int a1 = cursor.getInt(7);
//            int a2 = cursor.getInt(8);
//            String textMode = cursor.getString(9);
//            int textSize = cursor.getInt(10);
//            int a3 = cursor.getInt(11);
//
//            String a = "'" + cursor.getString(0) + "', " + cursor.getString(1) +
//                    cursor.getString(2) + cursor.getString(3) +cursor.getString(4) +
//                    cursor.getString(5) + cursor.getString(6) + cursor.getString(7) +
//                    cursor.getString(8);

            String dataStr;

//            "shapeName TEXT, shapeScript TEXT, " +
//                    "x float, y float, " +
//                    "width float, height float, imageName TEXT, visibility INT, movability INT, textStr TEXT, textSize float, clickability INT, " +
//                    "_id INTEGER PRIMARY KEY AUTOINCREMENT" +
//                    ");";

            dataStr = "INSERT INTO shape (shapeName, shapeScript, x, y, width, height, imageName, visibility, movability," +
                    "textStr, textSize, clickability, _id) " +
                    "SELECT '" + copiedShapeName + "',shapeScript, x, y, width, height, imageName, visibility, movability," +
                    "textStr, textSize, clickability, NULL " +
                    "FROM shape WHERE shapeName = '" +
                     currentShapeName + "';";

            System.err.println(dataStr);
            db.execSQL(dataStr);

            dataStr = "update shape set shapeName = '" + copiedShapeName +
                    "' where shapeName = NULL;";

            System.err.println(dataStr);
            db.execSQL(dataStr);

        //}
        //if (cursor!=null){cursor.close();}

    }

    /*
        Go to XXX under an action
     */



    public void setGoTo(String action){
        actionStr = action;

        // show a page list, and allow to select selectedPage
        createPageList();
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(pageListView);

        //final EditText goToText = new EditText(this);
        //layout.addView(goToText);
        builder.setView(layout);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                /*
                    #DB# take a look at  msg below.
                        You should add this script into DB
                        Possible example: "onClick go to page1".
                 */

                setGotoHelper();


                final String msg = actionStr + "go to " + selectedPageStr;
                Toast.makeText(EditorActivity.this,
                        msg, Toast.LENGTH_SHORT).show();

                pageData = new HashMap<>();
                shapeData = new HashMap<>();
                updateHashMap();
                currentPage = pageData.get(currentPageName);
            }
        });
        builder.setNegativeButton("CANCEL",null);
        AlertDialog dialog = builder.create();
        dialog.setTitle("Choose a page go to");
        dialog.show();
    }

    private void setGotoHelper() {

        // selectedShapeStr is Image Type!!! currentShapeName is unique name for shape!!!
        Cursor cursor = db.rawQuery("Select * from shape where shapeName = '"
                //+ selectedShapeStr + "' ;"  // cannot use selectedShapeStr here !!!!!!!!!!!!!
                + currentShapeName + "' ;"
                , null);

        Log.d(TAG, "setGotoHelper currentShapeName !!!!!!!!!!!!! " + currentShapeName);
        while(cursor.moveToNext()) {
            String scriptStr = "";
            if(cursor.getString(1) != null) {
                scriptStr = cursor.getString(1);
                scriptStr += ";";
            }
            scriptStr += actionStr;
            scriptStr += "goto " + selectedPageStr;

            Log.d(TAG, "ShapeScript is !!!!!!!!!!: " + scriptStr);

            String resStr = "Update shape "
                    + "set shapeScript = '" + scriptStr
                    + "' where shapeName = \"" + currentShapeName
                    + "\" ;";

            System.err.println(resStr);
            db.execSQL(resStr);
        }
        if (cursor!=null){ cursor.close(); }

    }


    /*
        Play XXX under an action
     */
    public void setPlaySound(String action){
        actionStr = action;
        createSoundList();
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(soundListView);

        builder.setView(layout);

        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int button){
                /*
                    #DB# take a look at  msg below.
                        You should add this script into DB
                        Possible example: "onClick go to page1".
                 */

                playSoundHelper();

                final String msg = actionStr + "play sound " + selectedSoundStr;
                Toast.makeText(EditorActivity.this,
                        msg, Toast.LENGTH_SHORT).show();

                pageData = new HashMap<>();
                shapeData = new HashMap<>();
                updateHashMap();
                currentPage = pageData.get(currentPageName);

            }
        });
        builder.setNegativeButton("CANCEL",null);

        AlertDialog dialog = builder.create();
        dialog.setTitle("Choose a sound to play");
        dialog.show();
    }


    private void playSoundHelper() {
        // selectedShapeStr is Image Type!!! currentShapeName is unique name for shape!!!
        Cursor cursor = db.rawQuery("Select * from shape where shapeName = '"
                        //+ selectedShapeStr + "' ;"  // cannot use selectedShapeStr here !!!!!!!!!!!!!
                        + currentShapeName + "' ;"
                , null);

        Log.d(TAG, "setGotoHelper currentShapeName !!!!!!!!!!!!! " + currentShapeName);
        while(cursor.moveToNext()) {
            String scriptStr = "";
            if(cursor.getString(1) != null) {
                scriptStr = cursor.getString(1);
                scriptStr += ";";
            }
            scriptStr += actionStr;
            scriptStr += "play " + selectedSoundStr;

            Log.d(TAG, "ShapeScript is !!!!!!!!!!: " + scriptStr);

            String resStr = "Update shape "
                    + "set shapeScript = '" + scriptStr
                    + "' where shapeName = \"" + currentShapeName
                    + "\" ;";

            System.err.println(resStr);
            db.execSQL(resStr);
        }
        if (cursor!=null){ cursor.close(); }
    }



    /*
        trying to play sounds by users
     */
    public void tryToPlaySound(String sound){
        /*
            No need to create so much media
         */
        if (sound.equals("carrotcarrotcarrot")){
            MediaPlayer.create(EditorActivity.this, R.raw.carrotcarrotcarrot).start();
        } else if (sound.equals("evillaugh")){
            MediaPlayer.create(EditorActivity.this, R.raw.evillaugh).start();
        } else if (sound.equals("fire")){
            MediaPlayer.create(EditorActivity.this, R.raw.fire).start();
        } else if (sound.equals("hooray")){
            MediaPlayer.create(EditorActivity.this, R.raw.hooray).start();
        } else if (sound.equals("munch")){
            MediaPlayer.create(EditorActivity.this, R.raw.munch).start();
        } else if (sound.equals("munching")){
            MediaPlayer.create(EditorActivity.this, R.raw.munching).start();
        } else if (sound.equals("woof")){
            MediaPlayer.create(EditorActivity.this, R.raw.woof).start();
        }
    }


    public void setShow(String action){
        actionStr = action;


        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        View createView = getLayoutInflater().inflate(R.layout.popwindow_editor,null);
        final EditText createPageInput = (EditText) createView.findViewById(R.id.create_page_str_menu);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            //@Override
            //public void onClick(View view) {
            public void onClick(DialogInterface dialog, int button){

                /*
                    #DB# when a action is done onto a page, you should show something,
                        You can set the visibility of shape to be true.
                 */

                selectedShapeStr = createPageInput.getText().toString();
                final String createPageInputStr = createPageInput.getText().toString();
                createAllShapeList();
                if (!allShapeList.contains(createPageInputStr)){
                    Toast.makeText(EditorActivity.this,
                            "Shape Name Does Not Exist.", Toast.LENGTH_SHORT).show();
                } else {
                    setShowHelper();

                    Toast.makeText(EditorActivity.this,
                            "Show " + createPageInputStr + " when " + actionStr,
                            Toast.LENGTH_SHORT).show();

                    pageData = new HashMap<>();
                    shapeData = new HashMap<>();
                    updateHashMap();
                    currentPage = pageData.get(currentPageName);
                }

            }
        });
        builder.setNegativeButton("CANCEL",null);

        builder.setView(createView);
        AlertDialog dialog = builder.create();
        dialog.setTitle("When you " + actionStr + ", you want to show");
        dialog.show();
    }


    private void setShowHelper() {
        // selectedShapeStr is Image Type!!! currentShapeName is unique name for shape!!!
        Cursor cursor = db.rawQuery("Select * from shape where shapeName = '"
                        //+ selectedShapeStr + "' ;"  // cannot use selectedShapeStr here !!!!!!!!!!!!!
                        + currentShapeName + "' ;"
                , null);

        Log.d(TAG, "setGotoHelper currentShapeName !!!!!!!!!!!!! " + currentShapeName);
        while(cursor.moveToNext()) {
            String scriptStr = "";
            if(cursor.getString(1) != null) {
                scriptStr = cursor.getString(1);
                scriptStr += ";";
            }
            scriptStr += actionStr;
            scriptStr += "show " + selectedShapeStr;

            Log.d(TAG, "ShapeScript is !!!!!!!!!!: " + scriptStr);

            String resStr = "Update shape "
                    + "set shapeScript = '" + scriptStr
                    + "' where shapeName = \"" + currentShapeName
                    + "\" ;";

            System.err.println(resStr);
            db.execSQL(resStr);
        }
        if (cursor!=null){ cursor.close(); }
    }



    public void setHide(String action){
        actionStr = action;


        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setCancelable(true);
        View createView = getLayoutInflater().inflate(R.layout.popwindow_editor,null);
        final EditText createPageInput = (EditText) createView.findViewById(R.id.create_page_str_menu);
        builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
            //@Override
            //public void onClick(View view) {
            public void onClick(DialogInterface dialog, int button){

                /*
                    #DB# when a action is done onto a page, you should hide something,
                        You can set the visibility of shape to be false.
                 */

                final String createPageInputStr = createPageInput.getText().toString();
                createAllShapeList();
                if (!allShapeList.contains(createPageInputStr)){
                    Toast.makeText(EditorActivity.this,
                            "Shape Name Does Not Exist.", Toast.LENGTH_SHORT).show();
                } else {
                    selectedShapeStr = createPageInput.getText().toString();

                    setHideHelper();

                    Toast.makeText(EditorActivity.this,
                            "Hide " + createPageInput.getText().toString() + " when " + actionStr,
                            Toast.LENGTH_SHORT).show();

                    pageData = new HashMap<>();
                    shapeData = new HashMap<>();
                    updateHashMap();
                    currentPage = pageData.get(currentPageName);
                }

            }
        });
        builder.setNegativeButton("CANCEL",null);

        builder.setView(createView);
        AlertDialog dialog = builder.create();
        dialog.setTitle("When you " + actionStr + ", you want to hide");
        dialog.show();
    }


    private void setHideHelper() {
        // selectedShapeStr is Image Type!!! currentShapeName is unique name for shape!!!
        Cursor cursor = db.rawQuery("Select * from shape where shapeName = '"
                        //+ selectedShapeStr + "' ;"  // cannot use selectedShapeStr here !!!!!!!!!!!!!
                        + currentShapeName + "' ;"
                , null);

        Log.d(TAG, "setGotoHelper currentShapeName !!!!!!!!!!!!! " + currentShapeName);
        while(cursor.moveToNext()) {
            String scriptStr = "";
            if(cursor.getString(1) != null) {
                scriptStr = cursor.getString(1);
                scriptStr += ";";
            }
            scriptStr += actionStr;
            scriptStr += "hide " + selectedShapeStr;

            Log.d(TAG, "ShapeScript is !!!!!!!!!!: " + scriptStr);

            String resStr = "Update shape "
                    + "set shapeScript = '" + scriptStr
                    + "' where shapeName = \"" + currentShapeName
                    + "\" ;";

            System.err.println(resStr);
            db.execSQL(resStr);
        }
        if (cursor!=null){ cursor.close(); }
    }

    public void setProperty(){

        // if setProperty() is called directly after addShape(), currentShapeName is default shapeName
        Cursor cursor = db.rawQuery("select * from shape where shapeName = '" + currentShapeName + "';", null);
        while(cursor.moveToNext()) {
            currentScripts = cursor.getString(1);
            float x = cursor.getFloat(2);
            float y = cursor.getFloat(3);
            float width = cursor.getFloat(4);
            float height = cursor.getFloat(5);
            selectedImageStr = cursor.getString(6);
            String a = cursor.getString(0) + cursor.getString(1) +
                    cursor.getString(2) + cursor.getString(3) +cursor.getString(4) +
                    cursor.getString(5) + cursor.getString(6) + cursor.getString(7) +
                    cursor.getString(8);
            boolean visibility = cursor.getInt(7) == 1? true: false;
            boolean movability = cursor.getInt(8) == 1? true: false;

            AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
            builder.setCancelable(true);
            View shapePropertyView = getLayoutInflater().inflate(R.layout.popwindow_shape_property,null);

            final EditText leftTxt = (EditText) shapePropertyView.findViewById(R.id.shape_left_menu);
            leftTxt.setText(String.valueOf(x));
            leftTxt.setInputType(TYPE_NUMBER_FLAG_DECIMAL);

            final EditText rightTxt = (EditText) shapePropertyView.findViewById(R.id.shape_right_menu);
            rightTxt.setText(String.valueOf(x + width));
            rightTxt.setInputType(TYPE_NUMBER_FLAG_DECIMAL);

            final EditText topTxt = (EditText) shapePropertyView.findViewById(R.id.shape_top_menu);
            topTxt.setText(String.valueOf(y));
            topTxt.setInputType(TYPE_NUMBER_FLAG_DECIMAL);

            final EditText botTxt = (EditText) shapePropertyView.findViewById(R.id.shape_bottom_menu);
            botTxt.setText(String.valueOf(y + height));
            botTxt.setInputType(TYPE_NUMBER_FLAG_DECIMAL);

            final EditText shapeNameTxt = (EditText) shapePropertyView.findViewById(R.id.shape_name_menu);
            shapeNameTxt.setText(currentShapeName);

            final Switch movableSwt = (Switch) shapePropertyView.findViewById(R.id.shape_movable_menu);
            final Switch setShowSwt = (Switch) shapePropertyView.findViewById(R.id.shape_set_show_menu);
            setShowSwt.setChecked(visibility);
            movableSwt.setChecked(movability);

            final EditText textinput = (EditText) shapePropertyView.findViewById(R.id.text_editor_menu);
            if (shapeData.get(currentShapeName).getText()==null){
                textinput.setText("");
            } else {
                textinput.setText(shapeData.get(currentShapeName).getText());
            }
            final EditText textSizeinput = (EditText) shapePropertyView.findViewById(R.id.text_size_editor_menu);
            textSizeinput.setText(Float.toString(shapeData.get(currentShapeName).getTextsize()));
            textSizeinput.setInputType(TYPE_NUMBER_FLAG_DECIMAL);

            // Show current shape's scripts
            final TextView scriptInfo = (TextView) shapePropertyView.findViewById(R.id.shape_script_menu);

            cursor.close();
            if (currentScripts==null){
                scriptInfo.setText("Nothing Yet. Try some!");
            } else {
                scriptInfo.setText(currentScripts);
            }

            // Show current shape's image-name
            final TextView imageInfo = shapePropertyView.findViewById(R.id.shape_image_name_menu);
            if (selectedImageStr!=null){
                imageInfo.setText(selectedImageStr);
            }

            final Button undo_btn = shapePropertyView.findViewById(R.id.shape_undo_name);
            undo_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!oldShapeName.equals("")) shapeNameTxt.setText(oldShapeName);
                    else Toast.makeText(EditorActivity.this,
                            "No Previous Name", Toast.LENGTH_SHORT);
                }
                });


            builder.setPositiveButton("Enter", new DialogInterface.OnClickListener(){
                //@Override
                //public void onClick(View view) {
                public void onClick(DialogInterface dialog, int button){

                /*
                    #DB# 7 properties to change:
                    Get String of the below:
                    1. change left;
                    2. change right;
                    3. change top;
                    4. change bot;
                    5. change name;
                    Get switch of the below;
                    6. movable;
                    7. show;
                 */

                    float x = Float.valueOf(leftTxt.getText().toString());
                    float y = Float.valueOf(topTxt.getText().toString());
                    float width = Float.valueOf(rightTxt.getText().toString()) - x;
                    float height = Float.valueOf(botTxt.getText().toString()) - y;

                    String newName = shapeNameTxt.getText().toString();
                    boolean movable = movableSwt.isChecked();
                    boolean showable = setShowSwt.isChecked();
                    String textinputStr = textinput.getText().toString();

                    float textSizeFloat = Float.valueOf(textSizeinput.getText().toString());

                    createAllShapeList();
                    if (!currentShapeName.equals(newName) && allShapeList.contains(newName)){
                        Toast.makeText(EditorActivity.this,
                                "Shape name already exists. Please try another one.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        setPropertyHelper(x, y, width, height, newName, movable, showable,
                                textinputStr, textSizeFloat);
                    }
                    pageData = new HashMap<>();
                    shapeData = new HashMap<>();
                    updateHashMap();
                    currentPage = pageData.get(currentPageName);
                    currentShapeName = newName;

                    editorView.setCurrentPage(currentPage);


                }
            });

            builder.setNegativeButton("CANCEL",null);

            builder.setView(shapePropertyView);
            AlertDialog dialog = builder.create();
            dialog.setTitle("Current shape property: ");
            dialog.show();
        }
        if (cursor!=null){cursor.close();}
    }


    private void setPropertyHelper(float x, float y, float width, float height,
                                   String newName, boolean movable, boolean showable,
                                   String textinputStr, float textSizeFloat) {


        String updateStr = "Update shape "
                + "set x = " + x
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr);
        db.execSQL(updateStr);

        String updateStr2 = "Update shape "
                + "set y = " + y
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr2);
        db.execSQL(updateStr2);

        String updateStr3 = "Update shape "
                + "set width = " + width
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr3);
        db.execSQL(updateStr3);

        String updateStr4 = "Update shape "
                + "set height = " + height
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr4);
        db.execSQL(updateStr4);

        int movableInt = movable? 1:0;

        String updateStr5 = "Update shape "
                + "set movability = " + movableInt
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr5);
        db.execSQL(updateStr5);

        int showableInt = showable? 1:0;

        String updateStr6 = "Update shape "
                + "set visibility = " + showableInt
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr6);
        db.execSQL(updateStr6);

        String updateStr_text = "Update shape "
                + "set textStr = \"" + textinputStr
                + "\" where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr_text);
        db.execSQL(updateStr_text);

        String updateStr_size = "Update shape "
                + "set textSize = " + textSizeFloat
                + " where shapeName = \"" + currentShapeName
                + "\" ;";
        System.err.println(updateStr_size);
        db.execSQL(updateStr_size);


            // update shapeName: should update in both shape Table and page table
            String updateStr7 = "Update shape "
                    + "set shapeName = \"" + newName
                    + "\" where shapeName = \"" + currentShapeName
                    + "\" ;";
            System.err.println(updateStr7);
            db.execSQL(updateStr7);




        // update shapelist for pages contains this renamed shape
        Cursor cursor = db.rawQuery("Select * from page where shapeList like '%"
                        + currentShapeName + "%' ;"
                , null);

        while(cursor.moveToNext()) {
            // replace selectedShapeStr with newName in page Table
            if(cursor.getString(1) != null) {
                String shapeList = cursor.getString(1);
                shapeList = shapeList.replace(currentShapeName, newName);

                String updateStr8 = "Update page "
                        + "set shapeList = \"" + shapeList
                        + "\" where pageName = \"" + currentPageName
                        + "\" ;";
                System.err.println(updateStr8);
                db.execSQL(updateStr8);

            }
        }




        // update shapeScripts for shapes with the renamed shape in scripts
        cursor = db.rawQuery("Select * from shape where shapeScript like '%"
                        + currentShapeName + "%' ;"
                , null);

        while(cursor.moveToNext()) {
            String shapeName = cursor.getString(0);

            // replace selectedShapeStr with newName in page Table
            if(cursor.getString(1) != null) {
                String script = cursor.getString(1);
                script.replace(currentShapeName, newName);

                String updateStr8 = "Update shape "
                        + "set shapeScript = \"" + script
                        + "\" where shapeName = \"" + shapeName
                        + "\" ;";
                System.err.println(updateStr8);
                db.execSQL(updateStr8);

            }
        }
        if (cursor!=null){cursor.close();}
        oldShapeName = currentShapeName;


//        String updateStr8 = "Update page "
//                + "set shapeList = \"" + newName
//                + "\" where pageName = \"" + currentPageName
//                + "\" ;";
//        /*
//            Call the view to redraw
//         */
//        currentPage.deleteShape(currentShape);
//        currentShape.setX(x);
//        currentShape.setY(y);
//        currentShape.setWidth(width);
//        currentShape.setHeight(height);
//        currentShape.setMovable(movable);
//        currentShape.setVisible(showable);
//        currentShape.setShapeName(newName);
//        currentPage.addShape(currentShape);
//        editorView.setCurrentPage(currentPage);
//
//        System.err.println(updateStr8);
//        db.execSQL(updateStr8);

    }


    private void bunnyMode() {
        setUpPageBunny();
        setUpShapeBunny();
        populatePageBunny();
        populateShapeBunny();

        shapeData = new HashMap<>();
        pageData = new HashMap<>();
        updateHashMap();
        currentPageName = "Page1";
        currentPage = pageData.get("Page1");
        editorView.setCurrentPage(currentPage);
    }


    // create page Table
    private void setUpPageBunny() {
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
    private void setUpShapeBunny() {
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
    private void populatePageBunny() {
        String dataStr = "Insert into page Values "
                + "('Page1','page1door1 page1door2 page1door3 page1text1', NULL),"
                + "('Page2','page2bunny1 page2door1 page2text1 page2carrot1', NULL),"
                + "('Page3','page3fire page3text1 page3door page3carrot', NULL),"
                + "('Page4','page4text1 page4door page4hungrybunny', NULL),"
                + "('Page5','page5carrot1 page5carrot2 page5carrot3 page5text1', NULL)"
                + ";";
        System.err.println(dataStr);
        db.execSQL(dataStr);
    }

    // add entries to shape table
    private void populateShapeBunny() {
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

    public void shapeDelete(){
        //#TODO# #DB#
        shapeDeleteHelper();
    }

    private void shapeDeleteHelper() {
        String deleteShapeStr = "delete from shape where shapeName = '" + currentShapeName + "';";
        System.err.println(deleteShapeStr);
        db.execSQL(deleteShapeStr);

        shapeData = new HashMap<>();
        pageData = new HashMap<>();
        updateHashMap();
        currentPage = pageData.get(currentPageName);
        editorView.setCurrentPage(currentPage);
    }


    // ========================== Update hashtables:  pageData and shapeData============
    /*
     * get Page info from page Table
     */
    private void updateHashMap() {
        Cursor cursor = db.rawQuery("SELECT * FROM page;", null);

        while(cursor.moveToNext()) {
            String pageName = cursor.getString(0);

            Pages page = new Pages(pageName);

            if(cursor.getString(1) != null) {
                String[] shapeList = cursor.getString(1).split(" ");
                // go through all shapes in the Page, and then add all shapes to the Page
                for(int i=0; i<shapeList.length; i++) {

                    // add current shape objects to the Page, pass in the unique shapeName, instead of imageName
                    page.addShape(getShapesMap(shapeList[i]));

                    Log.d(TAG, "page.getShapes == null ?" + (page.getShapes() == null));
                }
            }

            // create a new Page with given pageName




            // put current Page to Page Map
            Log.d(TAG,"ADD Page!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            pageData.put(pageName, page);

        }

        if (cursor!=null){cursor.close();}

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
            if(scriptList != null) {
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
        if (cursor!=null){cursor.close();}

        Log.d(TAG, "newly added shape in return: " + shapeData.get(shapeName));
        return shapeData.get(shapeName);
    }

    // ==================================================================================








//========================= UI Design ============================================//


}
