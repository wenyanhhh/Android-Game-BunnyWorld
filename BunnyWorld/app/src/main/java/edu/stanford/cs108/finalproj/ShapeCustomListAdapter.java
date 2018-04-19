package edu.stanford.cs108.finalproj;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by wenshengli on 3/10/18.
 */

public class ShapeCustomListAdapter extends ArrayAdapter<String> {

    private final Activity activity;
    private final ArrayList<String>  info;
    private final ArrayList<Integer>  imageID;

    public ShapeCustomListAdapter(Activity context, ArrayList<String> info, ArrayList<Integer> imageID){
        super(context, R.layout.popwindow_shape_selection, info);

        this.activity=context;
        this.info=info;
        this.imageID=imageID;
    }

    public View getView(int i, View view, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) activity.getLayoutInflater();
        View shapeListView = inflater.inflate(R.layout.popwindow_shape_selection, null, true);

        ImageView imageView = (ImageView) shapeListView.findViewById(R.id.shape_image);
        TextView imageInfo = (TextView) shapeListView.findViewById(R.id.shape_image_name);

        imageView.setImageResource(imageID.get(i));
        imageInfo.setText(info.get(i));

        return shapeListView;
    }
}
