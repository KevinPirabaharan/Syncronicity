package com.example.avjindersinghsekhon.minimaltodo;

import android.content.Context;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


public class StoreRetrieveData {
    private Context mContext;
    private String mFileName;

    //Constructor for a StoreRetrieveData object
    //In: context: a context is an Interface to global information about an application environment
    //In: filename: the name of the file to be read from
    public StoreRetrieveData(Context context, String filename){
        mContext = context;
        mFileName = filename;
    }

    //This function creates a JSONArray of JSONObjects from a list of toDoItems
    //in order to save it to a file
    public static JSONArray toJSONArray(ArrayList<ToDoItem> items) throws JSONException{
        JSONArray jsonArray = new JSONArray();
        for(ToDoItem item : items){
            JSONObject jsonObject = item.toJSON();
            jsonArray.put(jsonObject);
        }
        return  jsonArray;
    }

    //saves parent lists names to a file
    public void saveListToFile(ArrayList<String> lists) throws IOException
    {
        FileOutputStream fileOutputStream;
        OutputStreamWriter outputStreamWriter;
        fileOutputStream = mContext.openFileOutput("lists.txt", Context.MODE_PRIVATE);
        outputStreamWriter = new OutputStreamWriter(fileOutputStream);

        for(int i = 0; i < lists.size(); i++) {
            outputStreamWriter.write(lists.get(i) + "\n");
        }

        outputStreamWriter.close();
        fileOutputStream.close();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            saveListsToDatabase(lists);
        }

    }

    //Saves information about toDoItems to a file
    //to be retrieved next time the app is opened
    public void saveToFile(ArrayList<ToDoItem> items) throws JSONException, IOException{
        FileOutputStream fileOutputStream;
        OutputStreamWriter outputStreamWriter;
        fileOutputStream = mContext.openFileOutput(mFileName, Context.MODE_PRIVATE);
        outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        outputStreamWriter.write(toJSONArray(items).toString());
        outputStreamWriter.close();
        fileOutputStream.close();
    }

    //loads parent list names from a file
    public ArrayList<String> loadListsFromFile() throws IOException
    {
        ArrayList<String> toReturn = new ArrayList<>();

        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            toReturn = getListsFromDB();
            return toReturn;
        }

        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        String line;

        //toReturn.add("Default placeholder");

        try
        {
            fileInputStream = mContext.openFileInput("lists.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                while ((line = bufferedReader.readLine()) != null)
                {
                    if (!toReturn.contains(line)) {
                        if (line.equals("Show All Lists") != true) {
                            toReturn.add(line);
                        }
                    }
                }
        }
        catch (FileNotFoundException fnfe) {
            //do nothing about it
            //file won't exist first time app is run
        }
        finally {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(fileInputStream!=null){
                fileInputStream.close();
            }
        }

        return toReturn;
    }

    //loads information about toDoItems from a file
    public ArrayList<ToDoItem> loadFromFile() throws IOException, JSONException{
        ArrayList<ToDoItem> items = new ArrayList<>();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            items = readFromDatatbase(items);
            return items;
        }
        BufferedReader bufferedReader = null;
        FileInputStream fileInputStream = null;
        try {
            //builds a string from the entir textfile
            fileInputStream =  mContext.openFileInput(mFileName);
            StringBuilder builder = new StringBuilder();
            String line;
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            while((line = bufferedReader.readLine())!=null){
                builder.append(line);
            }

            //seperates each toDoItem's information and creates an arraylist of toDoItems
            JSONArray jsonArray = (JSONArray)new JSONTokener(builder.toString()).nextValue();
            for(int i =0; i<jsonArray.length();i++){
                ToDoItem item = new ToDoItem(jsonArray.getJSONObject(i));
                items.add(item);
            }


        } catch (FileNotFoundException fnfe) {
            //do nothing about it
            //file won't exist first time app is run
        }
        finally {
            if(bufferedReader!=null){
                bufferedReader.close();
            }
            if(fileInputStream!=null){
                fileInputStream.close();
            }

        }

        return items;
    }

    public  void saveListsToDatabase (ArrayList<String> items){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mDatabase = mRootRef.child("users").child(uid);
        mDatabase.child("lists").setValue(items);
    }


    public ArrayList<String> getListsFromDB(){
        final ArrayList<String> localList = new ArrayList<>();
        //localList.add("Show All Lists");
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mDatabase = mRootRef.child("users").child(uid);
        mDatabase.child("lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<String>> t = new GenericTypeIndicator<ArrayList<String>>(){};
                ArrayList<String> list = dataSnapshot.getValue(t);
                if (list != null) {
                    for (String item : list) {
                        localList.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return localList;
    }

    public ArrayList<ToDoItem> readFromDatatbase(final ArrayList<ToDoItem> localList){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mDatabase = mRootRef.child("users").child(uid);
        mDatabase.child("toDoItems").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<ArrayList<ToDoItem>> t = new GenericTypeIndicator<ArrayList<ToDoItem>>(){};
                ArrayList<ToDoItem> list = dataSnapshot.getValue(t);
                if (list != null) {
                    for (ToDoItem item : list) {
                        localList.add(item);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}

        });
        return localList;
    }



}
