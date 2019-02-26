package com.example.avjindersinghsekhon.minimaltodo;

import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Random;


public class SettingsActivity extends AppCompatActivity{

    AnalyticsApplication app;
    @Override
    protected void onResume() {
        super.onResume();
        app.send(this);
    }

    public View root1;
    public static final Random RANDOM = new Random();
    public Button button2;



    @Override
    //sets up the settings UI
    protected void onCreate(Bundle savedInstanceState) {

        app = (AnalyticsApplication)getApplication();
        //gets the theme from user preferences and sets the them of this activity to that theme
        String theme = getSharedPreferences(MainActivity.THEME_PREFERENCES, MODE_PRIVATE).getString(MainActivity.THEME_SAVED, MainActivity.LIGHTTHEME);
        if(theme.equals(MainActivity.LIGHTTHEME)){
            setTheme(R.style.CustomStyle_LightTheme);
        }
        else{
            setTheme(R.style.CustomStyle_DarkTheme);
        }
        super.onCreate(savedInstanceState);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_settings);
        //sets up the toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //places the back arrow on the screen so the user can navigate back to the previous screen
        final Drawable backArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        if(backArrow!=null){
            backArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(backArrow);
        }

        FragmentManager fm= getFragmentManager();
        fm.beginTransaction().replace(R.id.mycontent, new SettingsFragment()).commit();

        //for the random colour button
        root1 = findViewById(R.id.mycontent);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int red = RANDOM.nextInt(255);
                int blue = RANDOM.nextInt(255);
                int green = RANDOM.nextInt(255);

                int colour = Color.argb(255,red,green,blue);

                root1.setBackgroundColor(colour);

                //try to change the theme of the project then change the colour on the theme
            }
        });



    }

    @Override
    //a function to signal when the user selects an option
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                if(NavUtils.getParentActivityName(this)!=null){
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
