package com.example.avjindersinghsekhon.minimaltodo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.auth.FirebaseAuth;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static android.view.View.*;

public class
MainActivity extends AppCompatActivity {
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private RecyclerViewEmptySupport mRecyclerView;
    private FloatingActionMenu mCreateFAM;
    private FloatingActionButton mAddListFAB;
    private FloatingActionButton mAddToDoItemFAB;
    private FloatingActionButton mDeleteListFAB;
    private ArrayList<ToDoItem> mToDoItemsArrayList;
    private CoordinatorLayout mCoordLayout;
    public static final String TODOITEM = "com.avjindersinghsekhon.com.avjindersinghsekhon.minimaltodo.MainActivity";
    private BasicListAdapter adapter;
    private static final int REQUEST_ID_TODO_ITEM = 100;
    private ToDoItem mJustDeletedToDoItem;
    private int mIndexOfDeletedToDoItem;
    public static final String DATE_TIME_FORMAT_12_HOUR = "MMM d, yyyy  h:mm a";
    public static final String DATE_TIME_FORMAT_24_HOUR = "MMM d, yyyy  k:mm";
    public static final String FILENAME = "todoitems.json";
    private StoreRetrieveData storeRetrieveData;
    public ItemTouchHelper itemTouchHelper;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    public static final String SHARED_PREF_DATA_SET_CHANGED = "com.avjindersekhon.datasetchanged";
    public static final String CHANGE_OCCURED = "com.avjinder.changeoccured";
    private int mTheme = -1;
    private String theme = "name_of_the_theme";
    public static final String THEME_PREFERENCES = "com.avjindersekhon.themepref";
    public static final String RECREATE_ACTIVITY = "com.avjindersekhon.recreateactivity";
    public static final String THEME_SAVED = "com.avjindersekhon.savedtheme";
    public static final String DARKTHEME = "com.avjindersekon.darktheme";
    public static final String LIGHTTHEME = "com.avjindersekon.lighttheme";
    private AnalyticsApplication app;
    public ArrayList<ToDoItem> tempList = new ArrayList();
    private BasicListAdapter tempAdapter;

    int SIGN_IN_REQUEST_CODE;
    public static boolean offline = false;
    public static ArrayList<String> lists = new ArrayList();
    public static ArrayList<ToDoItem> itemsToDelete = new ArrayList();
    private TextView loginName;
    private ImageView deleteList;
    private ArrayAdapter<String> arrayAdapter;
    public static String currentList = "Show All Lists";
    //Initialization of hardcoded lists. Users will be able to add their own lists in the future
    ListView simpleList;

    //loads the toDoItems from a local file into an arraylist of toDoItems
    public static ArrayList<ToDoItem> getLocallyStoredData(StoreRetrieveData storeRetrieveData){
        ArrayList<ToDoItem> items = null;
        try {
            items  = storeRetrieveData.loadFromFile();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        if(items == null){
            items = new ArrayList<>();
        }
        return items;
    }

    protected void printListList(String currentList){

        if(tempList != null){
            tempList.clear();
        }
        if(currentList == null || currentList.equals("Show All Lists")){
            setTitle("Synchronicity");
           // Toast.makeText(MainActivity.this, "Showing All Lists ", Toast.LENGTH_LONG).show();
            tempAdapter = new BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(tempAdapter);
        } else {
            //Toast.makeText(MainActivity.this, "Showing: "+currentList, Toast.LENGTH_LONG).show();
            for(ToDoItem item : mToDoItemsArrayList){
                if(item.getToDoParent().equals(currentList)){
                    tempList.add(item);
                    //Toast.makeText(MainActivity.this, item.getToDoText(), Toast.LENGTH_LONG).show();
                }
            }
            if(currentList != null){
                setTitle(currentList);
            } else {
                setTitle("Synchronicity");
            }

            //Toast.makeText(MainActivity.this, tempList.get(1).getToDoText(), Toast.LENGTH_LONG).show();
            tempAdapter = new BasicListAdapter(tempList);
            //mRecyclerView.showEmptyView();
            mRecyclerView.setAdapter(tempAdapter);
        }

    }

    @Override
    //when the user opens the app from a paused state
    protected void onResume() {
        super.onResume();
        app.send(this);
        printListList(currentList);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(ReminderActivity.EXIT, false)){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(ReminderActivity.EXIT,false);
            editor.apply();
            finish();
        }
        /*
        We need to do this, as this activity's onCreate won't be called when coming back from SettingsActivity,
        thus our changes to dark/light mode won't take place, as the setContentView() is not called again.
        So, inside our SettingsFragment, whenever the checkbox's value is changed, in our shared preferences,
        we mark our recreate_activity key as true.

        Note: the recreate_key's value is changed to false before calling recreate(), or we woudl have ended up in an infinite loop,
        as onResume() will be called on recreation, which will again call recreate() and so on....
        and get an ANR

         */
        if(getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getBoolean(RECREATE_ACTIVITY, false)){
            SharedPreferences.Editor editor = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).edit();
            editor.putBoolean(RECREATE_ACTIVITY, false);
            editor.apply();
            recreate();
        }

        printListList(currentList);

    }

    @Override
    //when the user opens the app from a closed state, checks if the users preferences changed, if not then
    //loads the toDoItems from a local file
    protected void onStart() {
        app = (AnalyticsApplication)getApplication();
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        if(sharedPreferences.getBoolean(CHANGE_OCCURED, false)){


            mToDoItemsArrayList = getLocallyStoredData(storeRetrieveData);

            try{
                lists = storeRetrieveData.loadListsFromFile();
            }catch(IOException ex)
            {

            }
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, convert(lists));
            simpleList.setAdapter(arrayAdapter);

            adapter = new BasicListAdapter(mToDoItemsArrayList);
            mRecyclerView.setAdapter(adapter);
            setAlarms();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CHANGE_OCCURED, false);
            editor.apply();


        }
    }

    //sets alarms for any toDoItems that require a notification
    private void setAlarms(){
        if(mToDoItemsArrayList!=null){
            for(ToDoItem item : mToDoItemsArrayList){
                if(item.hasReminder() && item.getToDoDate()!=null){
                    if(item.getToDoDate().before(new Date())){
                        item.setToDoDate(null);
                        continue;
                    }
                    Intent i = new Intent(this, TodoNotificationService.class);
                    i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                    i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                    createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
                }
            }
        }
    }

    //when the users enters the main activity
    protected void onCreate(Bundle savedInstanceState) {

        app = (AnalyticsApplication)getApplication();

        //We recover the theme we've set and setTheme accordingly
        theme = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE).getString(THEME_SAVED, LIGHTTHEME);

        if(theme.equals(LIGHTTHEME)){
            mTheme = R.style.CustomStyle_LightTheme;
        }
        else{
            mTheme = R.style.CustomStyle_DarkTheme;
        }
        this.setTheme(mTheme);

        super.onCreate(savedInstanceState);

        storeRetrieveData = new StoreRetrieveData(this, FILENAME);
        simpleList = (ListView)findViewById(R.id.left_drawer);

        // ----- Check if user is already logged in -----
        if(FirebaseAuth.getInstance().getCurrentUser() == null && offline == false) {
            // Start sign in/sign up activity
            startActivityForResult(new Intent(MainActivity.this, ActivityLogin.class), SIGN_IN_REQUEST_CODE);
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
            } else if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null){
                Toast.makeText(this, "Welcome", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Welcome " + FirebaseAuth.getInstance()
                                .getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
            }
        }

        setContentView(R.layout.activity_main);
        //Initialize Floating Action Menu
        mCreateFAM = (FloatingActionMenu) findViewById(R.id.floating_action_menu);
        mDeleteListFAB = (FloatingActionButton) findViewById(R.id.fam_item3);
        mAddListFAB = (FloatingActionButton) findViewById(R.id.fam_item2);
        mAddToDoItemFAB = (FloatingActionButton)findViewById(R.id.fam_item1);

        //Populate the navigation drawer header
                loginName = (TextView) findViewById(R.id.header);
        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            loginName.setText("Welcome ");
        else {
            if (FirebaseAuth.getInstance().getCurrentUser().getDisplayName() == null)
                loginName.setText("Welcome ");
            else
                loginName.setText("Welcome " + FirebaseAuth.getInstance()
                        .getCurrentUser().getDisplayName());
        }

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_DATA_SET_CHANGED, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANGE_OCCURED, false);
        editor.apply();



        mToDoItemsArrayList =  getLocallyStoredData(storeRetrieveData);
        adapter = new BasicListAdapter(mToDoItemsArrayList);
        setAlarms();
        //loads parents list names

        try{
            lists = storeRetrieveData.loadListsFromFile();
        }catch(IOException ex)
        {

        }
        lists.add(0, "Show All Lists");

        //Code to populate the navigation drawer
        simpleList = (ListView)findViewById(R.id.left_drawer);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_listview, R.id.textView, convert(lists));
        simpleList.setAdapter(arrayAdapter);
        // ListView on item selected listener.
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                //lists.add(0, "Show All Lists");
                currentList = lists.get(position);

                //To prevent against stacking lists
                if(tempList != null){
                    tempList.clear();
                }
                if(currentList.equals("Show All Lists")){
                    setTitle("Synchronicity");
                    //Toast.makeText(MainActivity.this, "Showing All Lists ", Toast.LENGTH_LONG).show();
                    tempAdapter = new BasicListAdapter(mToDoItemsArrayList);
                    mRecyclerView.setAdapter(tempAdapter);
                } else {
                    //Toast.makeText(MainActivity.this, "Showing: "+currentList, Toast.LENGTH_LONG).show();
                    for(ToDoItem item : mToDoItemsArrayList){
                        if(item.getToDoParent().equals(currentList)){
                            tempList.add(item);
                            //Toast.makeText(MainActivity.this, item.getToDoText(), Toast.LENGTH_LONG).show();
                        }
                    }
                    if(currentList != null){
                        setTitle(currentList);
                    } else {
                        setTitle("Synchronicity");
                    }
                    //Toast.makeText(MainActivity.this, tempList.get(1).getToDoText(), Toast.LENGTH_LONG).show();
                    tempAdapter = new BasicListAdapter(tempList);
                    //mRecyclerView.showEmptyView();
                    mRecyclerView.setAdapter(tempAdapter);
                }
                //lists.remove("Show All Lists");
                mDrawerLayout.closeDrawers();
            }
        });


        //Creates toolbar and menus
        final android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Loads up nav drawer and toggle for it
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        android.support.v7.app.ActionBarDrawerToggle toggle = new android.support.v7.app.ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mCoordLayout = (CoordinatorLayout)findViewById(R.id.myCoordinatorLayout);

        //When create To Do Event is selected
        mAddToDoItemFAB.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                lists.remove("Show All Lists");
                lists.remove("Show All Lists");
                if(!lists.isEmpty()){
                    app.send(this, "Action", "FAB pressed");
                    Intent newTodo = new Intent(MainActivity.this, AddToDoActivity.class);
                    ToDoItem item = new ToDoItem("", false, null, null);
                    int color = ColorGenerator.MATERIAL.getRandomColor();
                    item.setTodoColor(color);
                    newTodo.putExtra(TODOITEM, item);
                    startActivityForResult(newTodo, REQUEST_ID_TODO_ITEM);
                }
               else{
                    Toast.makeText(MainActivity.this, "You need to create a list to add to!", Toast.LENGTH_LONG).show();
                }
                lists.add(0, "Show All Lists");
            }
        });

        //When create To Do List is Selected
        mAddListFAB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                lists.remove("Show All Lists");
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_new_list, null);
                final EditText mList = (EditText) mView.findViewById(R.id.newList);
                Button mNewList = (Button) mView.findViewById(R.id.btn_create_list);
                Button cancelCreateList = (Button) mView.findViewById(R.id.btn_cancel_creList);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                cancelCreateList.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                mNewList.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!mList.getText().toString().isEmpty()){
                            if (!lists.contains(mList.getText().toString())) {
                                lists.add(mList.getText().toString());
                                arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_listview, R.id.textView, convert(lists));
                                simpleList.setAdapter(arrayAdapter);

                                Toast.makeText(MainActivity.this,
                                        "ToDo List Created!",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                try {
                                    storeRetrieveData.saveListToFile(lists);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else
                            {
                                Toast.makeText(MainActivity.this,
                                        "List already exists!",
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                        }else{
                            Toast.makeText(MainActivity.this,
                                    "Field Empty! No List Created.",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                lists.add(0, "Show All Lists");

            }
        });

        mDeleteListFAB.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_delete_list, null);
                Button mDelList = (Button) mView.findViewById(R.id.btn_delete_list);
                Button cancelDelList = (Button) mView.findViewById(R.id.btn_cancel_deList);
                TextView noListWarning = (TextView) mView.findViewById(R.id.Error);
                final Spinner delSpinner = (Spinner) mView.findViewById(R.id.del_list_spinner);
                if(lists.contains("Show All Lists")){
                    //Toast.makeText(MainActivity.this, "DELETETHIS", Toast.LENGTH_LONG).show();
                    lists.remove("Show All Lists");
                    lists.remove("Show All Lists");
                }

                ArrayAdapter<String>adapter = new ArrayAdapter<String>(MainActivity.this,
                        android.R.layout.simple_spinner_item, convert(lists));
                if ((lists.size() == 0)) {
                    delSpinner.setEnabled(false);
                    delSpinner.setClickable(false);
                    noListWarning.setVisibility(View.VISIBLE);
                    mDelList.setBackgroundColor(Color.GRAY);
                    mDelList.setEnabled(false);
                    mDelList.setClickable(false);
                }
                else {
                    delSpinner.setEnabled(true);
                    delSpinner.setClickable(true);
                    noListWarning.setVisibility(View.GONE);
                    mDelList.setEnabled(true);
                    mDelList.setClickable(true);
                }

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                delSpinner.setAdapter(adapter);
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                cancelDelList.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                mDelList.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!lists.contains(delSpinner.getSelectedItem().toString())) {
                               Toast.makeText(MainActivity.this,
                                       "List doesn't exist!",
                                        Toast.LENGTH_SHORT).show();
                               dialog.dismiss();
                        }
                        else
                        {
                            lists.remove(delSpinner.getSelectedItem().toString());
                            arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.activity_listview, R.id.textView, convert(lists));
                            simpleList.setAdapter(arrayAdapter);
                            printListList("Show All Lists");
                            storeRetrieveData.saveListsToDatabase(lists);

                            for(ToDoItem item : mToDoItemsArrayList)
                            {
                                if(item.getToDoParent().equals(delSpinner.getSelectedItem().toString()))
                                {
                                    itemsToDelete.add(item);
                                }
                            }
                            mToDoItemsArrayList.removeAll(itemsToDelete);
                            Toast.makeText(MainActivity.this,
                                        "ToDo List Deleted!",
                                        Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
                lists.add(0, "Show All Lists");

            }
        });


        mRecyclerView = (RecyclerViewEmptySupport)findViewById(R.id.toDoRecyclerView);
        if(theme.equals(LIGHTTHEME)){
            mRecyclerView.setBackgroundColor(getResources().getColor(R.color.primary_lightest));
        }
        mRecyclerView.setEmptyView(findViewById(R.id.toDoEmptyView));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


//        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
//            @Override
//            public void show() {
//
//                mAddToDoItemFAB.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
//            }
//
//            @Override
//            public void hide() {
//
//                CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams)mAddToDoItemFAB.getLayoutParams();
//                 int fabMargin = lp.bottomMargin;
//                mAddToDoItemFAB.animate().translationY(mAddToDoItemFAB.getHeight()+fabMargin).setInterpolator(new AccelerateInterpolator(2.0f)).start();
//            }
//        };
//        mRecyclerView.addOnScrollListener(customRecyclerScrollViewListener);

        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setAdapter(adapter);
    }

    //converts an array list into an string array
    public String [] convert (ArrayList<String> toConvert)
    {
        String [] toReturn = new String[toConvert.size()];
        int i = 0;

        for (i = 0; i < toConvert.size(); i++)
        {
            toReturn[i] = toConvert.get(i);
        }

        return toReturn;
    }

    //stores user prefrences
    public void addThemeToSharedPreferences(String theme){
        SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(THEME_SAVED, theme);
        editor.apply();
    }

    @Override
    //displays the menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    //handles a click event
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            //take the user to the send feedback view
            case R.id.feedbackButton:
                Intent intent3 = new Intent(this, Feedback.class);
                startActivity(intent3);
                return true;

            //sends the user to the about activity
            case R.id.aboutMeMenuItem:
                Intent i = new Intent(this, AboutActivity.class);
                startActivity(i);
                return true;

            case R.id.preferences:
                //sends the user to the prefrences activity
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.signOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent2 = new Intent(this, ActivityLogin.class);
                startActivity(intent2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    //gets results from a recently closed activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode!= RESULT_CANCELED && requestCode == REQUEST_ID_TODO_ITEM){
            ToDoItem item =(ToDoItem) data.getSerializableExtra(TODOITEM);
            if(item.getToDoText().length()<=0){
                return;
            }
            boolean existed = false;

            if(item.hasReminder() && item.getToDoDate()!=null){
                Intent i = new Intent(this, TodoNotificationService.class);
                i.putExtra(TodoNotificationService.TODOTEXT, item.getToDoText());
                i.putExtra(TodoNotificationService.TODOUUID, item.getIdentifier());
                createAlarm(i, item.getIdentifier().hashCode(), item.getToDoDate().getTime());
            }

            //checks to see if there are any new toDoItems
            for(int i = 0; i<mToDoItemsArrayList.size();i++){
                if(item.getIdentifier().equals(mToDoItemsArrayList.get(i).getIdentifier())){
                    mToDoItemsArrayList.set(i, item);
                    existed = true;
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            //if the item is new store it
            if(!existed) {
                addToDataStore(item);
            }


        }
    }

    private AlarmManager getAlarmManager(){
        return (AlarmManager)getSystemService(ALARM_SERVICE);
    }

    private boolean doesPendingIntentExist(Intent i, int requestCode){
        PendingIntent pi = PendingIntent.getService(this,requestCode, i, PendingIntent.FLAG_NO_CREATE);
        return pi!=null;
    }

    //creates an alarm for a notification
    private void createAlarm(Intent i, int requestCode, long timeInMillis){
        AlarmManager am = getAlarmManager();
        PendingIntent pi = PendingIntent.getService(this,requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT);
        am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi);
//        Log.d("OskarSchindler", "createAlarm "+requestCode+" time: "+timeInMillis+" PI "+pi.toString());
    }
    //deletes an alarm
    private void deleteAlarm(Intent i, int requestCode){
        if(doesPendingIntentExist(i, requestCode)){
            PendingIntent pi = PendingIntent.getService(this, requestCode,i, PendingIntent.FLAG_NO_CREATE);
            pi.cancel();
            getAlarmManager().cancel(pi);
            Log.d("OskarSchindler", "PI Cancelled " + doesPendingIntentExist(i, requestCode));
        }
    }

    //adds an item to the array list of toDoItems
    private void addToDataStore(ToDoItem item){
        mToDoItemsArrayList.add(item);
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            saveToDatabase(mToDoItemsArrayList);
        adapter.notifyItemInserted(mToDoItemsArrayList.size() - 1);

    }


//    public void makeUpItems(ArrayList<ToDoItem> items, int len){
//        for (String testString : testStrings) {
//            ToDoItem item = new ToDoItem(testString, false, new Date());
//            //noinspection ResourceType
////            item.setTodoColor(getResources().getString(R.color.red_secondary));
//            items.add(item);
//        }
//
//    }

    public class BasicListAdapter extends RecyclerView.Adapter<BasicListAdapter.ViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{
        private ArrayList<ToDoItem> items;

        @Override
        //if the user rearranges the items on the screen
        public void onItemMoved(int fromPosition, int toPosition) {
           if(fromPosition<toPosition){
               for(int i=fromPosition; i<toPosition; i++){
                   Collections.swap(items, i, i+1);
               }
           }
            else{
               for(int i=fromPosition; i > toPosition; i--){
                   Collections.swap(items, i, i-1);
               }
           }
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        //if the use deletes an item
        public void onItemRemoved(final int position) {
            Log.d("Position is", String.valueOf(position));
            //Remove this line if not using Google Analytics
            app.send(this, "Action", "Swiped Todo Away");
            int newPosition = position;

            if (!currentList.equals("Show All Lists") && tempList.size() > 0)
            {
                ToDoItem tempItem = tempList.get(position);
                newPosition = items.indexOf(tempItem);
            }

            //delets the item from the list and its associated notification
            mJustDeletedToDoItem =  items.remove(newPosition);
            mIndexOfDeletedToDoItem = newPosition;
            Intent i = new Intent(MainActivity.this,TodoNotificationService.class);
            deleteAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode());
            notifyItemRemoved(newPosition);

//            String toShow = (mJustDeletedToDoItem.getToDoText().length()>20)?mJustDeletedToDoItem.getToDoText().substring(0, 20)+"...":mJustDeletedToDoItem.getToDoText();
            String toShow = "Todo";
            Snackbar.make(mCoordLayout, "Deleted "+toShow,Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //Comment the line below if not using Google Analytics
                            app.send(this, "Action", "UNDO Pressed");
                            items.add(mIndexOfDeletedToDoItem, mJustDeletedToDoItem);
                            if(mJustDeletedToDoItem.getToDoDate()!=null && mJustDeletedToDoItem.hasReminder()){
                                Intent i = new Intent(MainActivity.this, TodoNotificationService.class);
                                i.putExtra(TodoNotificationService.TODOTEXT, mJustDeletedToDoItem.getToDoText());
                                i.putExtra(TodoNotificationService.TODOUUID, mJustDeletedToDoItem.getIdentifier());
                                createAlarm(i, mJustDeletedToDoItem.getIdentifier().hashCode(), mJustDeletedToDoItem.getToDoDate().getTime());
                            }
                            notifyItemInserted(mIndexOfDeletedToDoItem);
                            printListList(currentList);
                        }
                    }).show();

            printListList(currentList);
            saveToDatabase(items);
        }

        @Override
        public BasicListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_circle_try, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final BasicListAdapter.ViewHolder holder, final int position) {
            ToDoItem item = items.get(position);
//            if(item.getToDoDate()!=null && item.getToDoDate().before(new Date())){
//                item.setToDoDate(null);
//            }
            SharedPreferences sharedPreferences = getSharedPreferences(THEME_PREFERENCES, MODE_PRIVATE);
            //Background color for each to-do item. Necessary for night/day mode
            int bgColor;
            //color of title text in our to-do item. White for night mode, dark gray for day mode
            int todoTextColor;
            if(sharedPreferences.getString(THEME_SAVED, LIGHTTHEME).equals(LIGHTTHEME)){
                bgColor = Color.WHITE;
                todoTextColor = getResources().getColor(R.color.secondary_text);
            }
            else{
                bgColor = Color.DKGRAY;
                todoTextColor = Color.WHITE;
            }
            holder.linearLayout.setBackgroundColor(bgColor);

            if(item.hasReminder() && item.getToDoDate()!=null) {
                holder.mTimeTextView.setVisibility(VISIBLE);
                holder.mToDoTextview.setMaxLines(1);
            }
            else {
                holder.mTimeTextView.setVisibility(GONE);
                holder.mToDoTextview.setMaxLines(2);
            }
            String temp = item.getToDoParent() + ": " + item.getToDoText();
            holder.mToDoTextview.setText(temp);
            holder.mToDoTextview.setTextColor(todoTextColor);

            TextDrawable myDrawable = TextDrawable.builder().beginConfig()
                    .textColor(Color.WHITE)
                    .useFont(Typeface.DEFAULT)
                    .toUpperCase()
                    .endConfig()
                    .buildRound(item.getToDoText().substring(0,1),item.getTodoColor());

//            TextDrawable myDrawable = TextDrawable.builder().buildRound(item.getToDoText().substring(0,1),holder.color);
            holder.mColorImageView.setImageDrawable(myDrawable);
            if(item.getToDoDate()!=null){
                String timeToShow;
                if(android.text.format.DateFormat.is24HourFormat(MainActivity.this)){
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_24_HOUR, item.getToDoDate());
                }
                else{
                    timeToShow = AddToDoActivity.formatDate(MainActivity.DATE_TIME_FORMAT_12_HOUR, item.getToDoDate());
                }
                holder.mTimeTextView.setText(timeToShow);
            }

        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        BasicListAdapter(ArrayList<ToDoItem> items){

            this.items = items;
        }


        @SuppressWarnings("deprecation")
        public class ViewHolder extends RecyclerView.ViewHolder{

            View mView;
            LinearLayout linearLayout;
            TextView mToDoTextview;
//            TextView mColorTextView;
            ImageView mColorImageView;
            TextView mTimeTextView;
//            int color = -1;

            public ViewHolder(View v){
                super(v);
                mView = v;
                v.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ToDoItem item = items.get(ViewHolder.this.getAdapterPosition());
                        Intent i = new Intent(MainActivity.this, AddToDoActivity.class);
                        i.putExtra(TODOITEM, item);
                        startActivityForResult(i, REQUEST_ID_TODO_ITEM);
                    }
                });
                mToDoTextview = (TextView)v.findViewById(R.id.toDoListItemTextview);
                mTimeTextView = (TextView)v.findViewById(R.id.todoListItemTimeTextView);
//                mColorTextView = (TextView)v.findViewById(R.id.toDoColorTextView);
                mColorImageView = (ImageView)v.findViewById(R.id.toDoListItemColorImageView);
                linearLayout = (LinearLayout)v.findViewById(R.id.listItemLinearLayout);
            }


        }
    }

    //Used when using custom fonts
//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
//    }

    private void saveDate(){
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
            storeRetrieveData.saveListToFile(lists);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    //when the activity is pause
    protected void onPause() {
        super.onPause();
        try {
            storeRetrieveData.saveToFile(mToDoItemsArrayList);
            storeRetrieveData.saveListToFile(lists);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    //when the activity is ended
    protected void onDestroy() {

        super.onDestroy();
        mRecyclerView.removeOnScrollListener(customRecyclerScrollViewListener);
    }

    public  void saveToDatabase (ArrayList<ToDoItem> items){
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference mDatabase = mRootRef.child("users").child(uid);
        mDatabase.child("toDoItems").setValue(items);
    }

}


