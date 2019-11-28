package com.example.visitormgmt;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CheckInActivity extends AppCompatActivity {

    private LinearLayout visitor;
    private TextView visitorTxt ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*Calling the function to change the color of satus bar*/
        changeStatusBarColor("#40a7e5");

        super.onCreate(savedInstanceState);

        // Setting the orentation to landscape mode only
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.checkin_activity);

        /*creating objects for TextView and LinearLayout*/
        visitor = (LinearLayout) findViewById(R.id.visitor);
        visitorTxt = (TextView) findViewById(R.id.visitor_txt);

        // creating OnClickListener for Visitor button
        visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Changing activity from this activity to mobile number activity
                Intent VisitorIntent = new Intent(CheckInActivity.this, MobilenoActivity.class);
                // Start the new activity
                startActivity(VisitorIntent);
            }
        });
    }

    /* Changing the colour of status bar*/
    private void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
            //window.setNavigationBarColor(Color.parseColor(color));
        }
    }
}