package edu.stanford.cs108.finalproj;

import android.util.Log;

import java.util.ArrayList;
import android.util.Log;

/**
 * Created by wenyan on 2/27/18.
 */


// ShapeScript refers to each clause in the scripts set
public class ShapeScript {
    private String trigger;
    private ArrayList<String> actions;
    private ArrayList<String> dests;
    private String onDropTarget;
    private String[] scriptArray;

    ShapeScript(String clause){
        scriptArray = clause.split(" ");
        trigger = scriptArray[0];
        actions = getActions();
        dests = getDests();
        onDropTarget = getTarget();
    }

    public String getTrigger(){
        return trigger;
    }

    public ArrayList<String> getAction(){
        return actions;
    }

    public ArrayList<String> getDest(){
        return dests;
    }

    public String getOnDropTarget(){return onDropTarget;}



    private ArrayList<String> getActions(){

        ArrayList<String> actions = new ArrayList<String>();

        if(trigger.equals("onDrop")){
            for (int i = 2; i < scriptArray.length; i += 2){
                actions.add(scriptArray[i]);
            }
        } else {
            for (int i = 1; i < scriptArray.length; i += 2){
                actions.add(scriptArray[i]);
            }
        }
        return actions;
    }

    private  ArrayList<String> getDests(){

        ArrayList<String> dests = new ArrayList<String>();

        if(trigger.equals("onDrop")){
            for (int i = 3; i < scriptArray.length; i += 2){
                dests.add(scriptArray[i]);
            }
        } else {
            for (int i = 2; i < scriptArray.length; i += 2){
                dests.add(scriptArray[i]);
            }
        }
        return dests;
    }

    private String getTarget(){
        if(trigger.equals("onDrop")){
            return scriptArray[1];
        } else return "";
    }

}
