package com.example.visitormgmt;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class OTPActivity extends AppCompatActivity {


    SharedPreferences sharedpreferences;

    public String VerificationId;
    public String userPhone;
    public String phoneNumber, name, company, email, purpose, meet_whoom, status; public int  statusCode;
    int exist = 0;
    public String Enterotp;


    EditText editText1, editText2, editText3, editText4, editText5, editText6;
    private EditText[] editTexts;
    Button resendOTP, otp_verify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_verification);

        //creating the objects of EditTexts to read data using this objects

        editText1 = (EditText) findViewById(R.id.digit1);
        editText2 = (EditText) findViewById(R.id.digit2);
        editText3 = (EditText) findViewById(R.id.digit3);
        editText4 = (EditText) findViewById(R.id.digit4);
        editText5 = (EditText) findViewById(R.id.digit5);
        editText6 = (EditText) findViewById(R.id.digit6);
        editTexts = new EditText[]{editText1, editText2, editText3, editText4, editText5, editText6};


        //for moving cursor from one text box to next text box when we enter the opt
        editText1.addTextChangedListener(new PinTextWatcher(0));
        editText2.addTextChangedListener(new PinTextWatcher(1));
        editText3.addTextChangedListener(new PinTextWatcher(2));
        editText4.addTextChangedListener(new PinTextWatcher(3));
        editText5.addTextChangedListener(new PinTextWatcher(4));
        editText6.addTextChangedListener(new PinTextWatcher(5));

        editText1.setOnKeyListener(new PinOnKeyListener(0));
        editText2.setOnKeyListener(new PinOnKeyListener(1));
        editText3.setOnKeyListener(new PinOnKeyListener(2));
        editText4.setOnKeyListener(new PinOnKeyListener(3));
        editText5.setOnKeyListener(new PinOnKeyListener(4));
        editText6.setOnKeyListener(new PinOnKeyListener(5));


        sharedpreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        VerificationId = sharedpreferences.getString("SendOTP","");
        userPhone = sharedpreferences.getString("Phone","");

        TextView showPhone = (TextView) findViewById(R.id.mobilenumberDisplay);
        showPhone.setText("Please enter the verification code that sent to " + userPhone);
        ButtonListners();

    }

    private void ButtonListners() {

        otp_verify = (Button) findViewById(R.id.verify);
        otp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //  Reading OTP that entered by te user
                Enterotp = (editText1.getText().toString() +editText2.getText().toString() +editText3.getText().toString() +editText4.getText().toString()
                        + editText5.getText().toString() + editText6.getText().toString());

               // Hadrd coded for OTP verification
                VerificationId = "111111";
                // Verifying the OTP
                if(Enterotp.equals(VerificationId)) {

                    Log.d("myTag", "user phone " + userPhone + "Empty");

                    Log.d("myTag", "phone number " + phoneNumber + "Empty");


//                    Intent visitorIntent = new Intent(OTPActivity.this, VisitorActivity.class);
//                    //start the new activity
//                    startActivity(visitorIntent);

                    if (sharedpreferences.getInt("ExistingUser", 0) == 0) {

                        Intent VisitorIntent = new Intent(OTPActivity.this, Invited_visitor_activity.class);
                        //start the new activity
                        startActivity(VisitorIntent);

                    } else {

                        //Adding in shared preference to use in next page
                        getVisitorsByMobileNumer();

                        Log.e("myApp", "after by mobile function" + exist);

                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Invalid OTP", Toast.LENGTH_LONG).show();
                }

            }

        });

        resendOTP = (Button) findViewById(R.id.resend);

        resendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //for clearing otp text boxes
                editText1.setText("");
                editText2.setText("");
                editText3.setText("");
                editText4.setText("");
                editText5.setText("");
                editText6.setText("");

                //for moving cursor to first position
                editText1.requestFocus();


            }

        });
    }


    private void getVisitorsByMobileNumer() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        RetrofitInterface request = retrofit.create(RetrofitInterface.class);
        Call<String> call = request.getVisitorDetails(userPhone);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String str = String.valueOf(response.code());
                if (!response.isSuccessful()) {
                    Log.e("myApp", "failed to get details using mobile");
                    return;
                }else {


                    try {
                        String jsonresponse = response.body().toString();
                        JSONObject dataobj = new JSONObject(jsonresponse);
                        statusCode = dataobj.getInt("status");

                        Log.e("myApp", "API called" + statusCode);

                        if(statusCode == 200) {
                            JSONArray dataArray = dataobj.getJSONArray("contactdetails");
                            JSONObject originalData = dataArray.getJSONObject(0);
                            status = originalData.getString("status");
                            name = originalData.getString("name");
                            phoneNumber = originalData.getString("contactno");
                            company = originalData.getString("organization");
                            Log.e("myApp", "API called company name" + company);
                            purpose = originalData.getString("purpose");
                            Log.e("myApp", "API called purpose" + purpose);
                            meet_whoom = originalData.getString("contactPerson");
                            Log.e("myApp", "API called meet_whoom" + meet_whoom);
                            email = originalData.getString("emailId");


                            SharedPreferences.Editor editor = sharedpreferences.edit();

                            editor.putString("FirstName", name);
                            editor.putString("Email", email);
                            editor.putString("Company", company);
                            editor.putString("Purpose", purpose);
                            editor.putString("MeetWoom", meet_whoom);
//                    editor.putString("State", state);
//                    editor.putString("Country", CountryName);
//                    editor.putString("City", City);
//                    editor.putString("Address", Address);
                            editor.putString("status", status);
                            editor.apply();

                            Intent preview = new Intent(OTPActivity.this, Invited_visitor_activity.class);
                            // Start the new activity
                            startActivity(preview);


                        }
                        else {

                            SharedPreferences.Editor editor = sharedpreferences.edit();

                                editor.putString("FirstName", "");
                                editor.putString("Email", "");
                                editor.putString("Company", "");
                                editor.putString("Purpose", "");
                                editor.putString("MeetWoom", "");
//                    editor.putString("State", state);
//                    editor.putString("Country", CountryName);
//                    editor.putString("City", City);
//                    editor.putString("Address", Address);
                                editor.putString("status", "");

                                editor.apply();

                                Intent VisitorIntent = new Intent(OTPActivity.this, VisitorActivity.class);
                                //start the new activity
                                startActivity(VisitorIntent);

                            }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Log.e("MYAPP", "exception: " + t.toString());

            }
        });

    }

   public class PinTextWatcher implements TextWatcher {

        private int currentIndex;
        private boolean isFirst = false, isLast = false;
        private String newTypedString = "";

        PinTextWatcher(int currentIndex) {
            this.currentIndex = currentIndex;

            if (currentIndex == 0)
                this.isFirst = true;
            else if (currentIndex == editTexts.length - 1)
                this.isLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            newTypedString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = newTypedString;

            /* Detect paste event and set first char*/
            if (text.length() > 1)
                text = String.valueOf(text.charAt(0)); // TODO: We can fill out other EditTexts

            editTexts[currentIndex].removeTextChangedListener(this);
            editTexts[currentIndex].setText(text);
            editTexts[currentIndex].setSelection(text.length());
            editTexts[currentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!isLast)
                editTexts[currentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && isLast) { // isLast is optional
                editTexts[currentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!isFirst)
                editTexts[currentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : editTexts)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        }

    }

    public class PinOnKeyListener implements View.OnKeyListener {

        private int currentIndex;

        PinOnKeyListener(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (editTexts[currentIndex].getText().toString().isEmpty() && currentIndex != 0)
                    editTexts[currentIndex - 1].requestFocus();
            }
            return false;
        }

    }
    private void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
           // window.setNavigationBarColor(Color.parseColor(color));
        }
    }

}




