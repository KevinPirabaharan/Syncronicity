package com.example.avjindersinghsekhon.minimaltodo;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.UUID;

//This class is used to handle handle asynchronous requests
public class DeleteNotificationService extends IntentService {

    private StoreRetrieveData storeRetrieveData;
    private ArrayList<ToDoItem> mToDoItems;
    private ToDoItem mItem;

    public DeleteNotificationService(){
        super("DeleteNotificationService");
    }

    @Override
    //This method is invoked on the worker thread with a request to process
    protected void onHandleIntent(Intent intent) {
        storeRetrieveData = new StoreRetrieveData(this, MainActivity.FILENAME);
        UUID todoID = (UUID)intent.getSerializableExtra(TodoNotificationService.TODOUUID);

        mToDoItems = loadData();
        if(mToDoItems!=null){
            for(ToDoItem item : mToDoItems){
                if(item.getIdentifier().equals(todoID)){
                    mItem = item;
                    break;
                }
            }

            if(mItem!=null){
                mToDoItems.remove(mItem);
                dataChanged();
                saveData();
            }

        }

    }

    //signals the data has changed and makes appropriate cahnges
    private void dataChanged(){
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(MainActivity.CHANGE_OCCURED, true);
        editor.apply();
    }

    //saves the list of toDoItems
    private void saveData(){
        try{
            storeRetrieveData.saveToFile(mToDoItems);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    //saves data and closes the activity
    public void onDestroy() {
        super.onDestroy();
        saveData();
    }

    //loads the list of toDoItems from a file on the device
    private ArrayList<ToDoItem> loadData(){
        try{
            return storeRetrieveData.loadFromFile();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;

    }
}
