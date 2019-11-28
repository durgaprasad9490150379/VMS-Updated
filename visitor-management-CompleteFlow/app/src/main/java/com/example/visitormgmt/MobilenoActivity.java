package com.example.visitormgmt;

import android.content.Context;
import java.lang.String;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import java.util.Random;


public class MobilenoActivity extends AppCompatActivity {


    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    EditText mobileNumber;
    String UserNumber;
    SharedPreferences sharedpreferences;
    TextView validation;
    CountryCodePicker ccp;
    public String VerificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.checkin_mobile);

        sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        Button otp_send = (Button) findViewById(R.id.sendotp);
        mobileNumber = (EditText) findViewById(R.id.mob_no);
        validation = (TextView) findViewById(R.id.validationform);
        ccp = (CountryCodePicker) findViewById(R.id.ccp);


        StrictMode.ThreadPolicy policy= new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        otp_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedpreferences.edit();

                UserNumber =  mobileNumber.getText().toString();

                Log.d("myTag", "mobile" + UserNumber);

                if(UserNumber.isEmpty() || UserNumber.length() > 10 || UserNumber.length() < 10){
                    validation.setText("Enter Valid mobile number");
                    return;
                }
                VerificationId = new String(OTP(6));

                editor.putString("Phone", UserNumber);
                editor.putString("SendOTP", VerificationId);
                editor.putInt("ExistingUser", 1);
                editor.apply();


                Log.d("myTag", "send otp " + VerificationId);

                Intent otpIntent = new Intent(MobilenoActivity.this, OTPActivity.class);

                // Start the new activity
                startActivity(otpIntent);

            }

        });

    }



    public static char[] OTP(int len){

        String numbers= "0123456789";
        Random rndm_method = new Random();
        char[] otp = new char[len];
        for (int i = 0; i < len; i++) {
            // Use of charAt() method : to get character value
            // Use of nextInt() as it is scanning the value as int
            otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
        }
        return otp;
    }

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
