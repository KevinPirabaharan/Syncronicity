package com.example.avjindersinghsekhon.minimaltodo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class ToDoItem implements Serializable{
    private String mToDoText;
    private boolean mHasReminder;
//    private Date mLastEdited;
    private int mTodoColor;
    private Date mToDoDate;
    private String mToDoParent;
    private UUID mTodoIdentifier;
    private static final String TODOTEXT = "todotext";
    private static final String TODOREMINDER = "todoreminder";
//    private static final String TODOLASTEDITED = "todolastedited";
    private static final String TODOCOLOR = "todocolor";
    private static final String TODODATE = "tododate";
    private static final String TODOPARENT = "todoparent";
    private static final String TODOIDENTIFIER = "todoidentifier";


    public ToDoItem(){
        mToDoText = "temp";
        mHasReminder = false;
        mToDoDate = null;
        mTodoColor = 1677725;
        mTodoIdentifier = UUID.randomUUID();
        mToDoParent = "temp";
    }

    //Constuctor for a ToDoItem object
    //In: todoBody: text to be displayed
    //In: hasReminder: a boolean to indicate whether or not the user wants a reminder set
    //IN: toDoDate: the date to be reminded
    public ToDoItem(String todoBody, boolean hasReminder, Date toDoDate, String toDoParent){
        mToDoText = todoBody;
        mHasReminder = hasReminder;
        mToDoDate = toDoDate;
        mTodoColor = 1677725;
        mTodoIdentifier = UUID.randomUUID();
        mToDoParent = toDoParent;
    }

    //Constuctor for a ToDoItem object
    //In: jsonObject: an data structure that holds the information for a toDoItem object
    public ToDoItem(JSONObject jsonObject) throws JSONException{
        mToDoText = jsonObject.getString(TODOTEXT);
        mHasReminder = jsonObject.getBoolean(TODOREMINDER);
        mTodoColor = jsonObject.getInt(TODOCOLOR);
        mTodoIdentifier = UUID.fromString(jsonObject.getString(TODOIDENTIFIER));
        mToDoParent = jsonObject.getString(TODOPARENT);

        if(jsonObject.has(TODODATE)){
            mToDoDate = new Date(jsonObject.getLong(TODODATE));
        }
    }

    //Construcor for a JSONObject that holds information for a toDoItem object
    public JSONObject toJSON() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TODOTEXT, mToDoText);
        jsonObject.put(TODOREMINDER, mHasReminder);

        if(mToDoDate!=null){
            jsonObject.put(TODODATE, mToDoDate.getTime());
        }
        jsonObject.put(TODOCOLOR, mTodoColor);
        jsonObject.put(TODOIDENTIFIER, mTodoIdentifier.toString());
        jsonObject.put(TODOPARENT, mToDoParent);

        return jsonObject;
    }


    public String getToDoText() {
        return mToDoText;
    }

    public void setToDoText(String mToDoText) {
        this.mToDoText = mToDoText;
    }

    public boolean hasReminder() {
        return mHasReminder;
    }

    public void setHasReminder(boolean mHasReminder) {
        this.mHasReminder = mHasReminder;
    }

    public Date getToDoDate() {
        return mToDoDate;
    }

    public int getTodoColor() {
        return mTodoColor;
    }

    public void setTodoColor(int mTodoColor) {
        this.mTodoColor = mTodoColor;
    }

    public void setToDoParent(String mToDoParent) { this.mToDoParent = mToDoParent; }

    public String getToDoParent() {return mToDoParent; }

    public void setToDoDate(Date mToDoDate) {
        this.mToDoDate = mToDoDate;
    }

    public UUID getIdentifier(){
        return mTodoIdentifier;
    }

}

