package com.example.avjindersinghsekhon.minimaltodo;


import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.Button;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by joel on 2018-03-31.
 */

public class Feedback extends AppCompatActivity {

    Button sendButton;
    EditText nameF;
    EditText messageF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        sendButton=(Button)findViewById(R.id.button);
        nameF=(EditText)findViewById(R.id.editText1);
        messageF=(EditText)findViewById(R.id.editText2);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/html");
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"synchronicity3760@gmail.com"});
                i.putExtra(Intent.EXTRA_SUBJECT, "Feedback from App");
                i.putExtra(Intent.EXTRA_TEXT, "Name : "+nameF.getText()+"\nMessage : "+messageF.getText());
                try {
                    startActivity(Intent.createChooser(i, "Send feedback..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.       // getMenuInflater().inflate(R.menu.menu_main, menu);
        return false;
    }

    @Override    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.menu_main) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }


}

