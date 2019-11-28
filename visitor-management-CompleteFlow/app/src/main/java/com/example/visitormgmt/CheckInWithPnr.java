package com.example.visitormgmt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;


public class CheckInWithPnr extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private String PNRNumber, contents;
    EditText pnrText;

    SharedPreferences sharedpreferences;
    private ZXingScannerView mScannerView;
    public String status, f_name, email, contactPerson, contactno, organiation, purpose;

    private IntentIntegrator qrScan;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 1460;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);


        //Setting layout for this page.
        setContentView(R.layout.checkin_with_visitor_id);


        //Initializing the object.
        Button search = (Button) findViewById(R.id.proceed);
        Button QRCODE = (Button) findViewById(R.id.qrcode);
        pnrText = (EditText) findViewById(R.id.visitorid_txt);
        mScannerView = new ZXingScannerView(CheckInWithPnr.this);


        //onclickListenr for Continue button
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Getting the visitorID which is entered  by the visitor.
                PNRNumber = pnrText.getText().toString();

                /*Checking the VisitorID is empty or not, If it is empty setting the
                  error message to the UI.
                 */

                if (PNRNumber.isEmpty()) {
                    TextView Error = (TextView) findViewById(R.id.validateVisId);
                    Error.setText("Please enter PNR");
                    return;
                } else {
                    /* If the visitorID is not empty then calling the API.
                       to check whether the VisitorID is existing or not.
                     */
                    CheckPNRInAPI();
                }
            }
        });

        // Creating the OnClickListener for QRCode scanner button.
        QRCODE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                if (EasyPermissions.hasPermissions(CheckInWithPnr.this, Manifest.permission.CAMERA)) {

                    // Creating the object for Zxing to scan the QRCode.
                    mScannerView = new ZXingScannerView(CheckInWithPnr.this);
                    // Set the scanner view as the content view.
                    setContentView(mScannerView);
                    // The camera is started to scan the QRCode.
                    mScannerView.startCamera();
                    // Setting the result handler to handle the result that getting from the QRCode.
                    mScannerView.setResultHandler(CheckInWithPnr.this);

                } else {

                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(CheckInWithPnr.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
                }


//
            }
        });
    }

    /* The request camera permission will return to onRequestPermissionsResult
       After asking the permission for camera the and comparing the result code */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {

                /* If the camera permission is granted the QRcode scanner will start*/
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Creating the object for Zxing to scan the QRCode.
                    mScannerView = new ZXingScannerView(CheckInWithPnr.this);
                    // Set the scanner view as the content view
                    setContentView(mScannerView);
                    // The camera is started to scan the QRCode.
                    mScannerView.startCamera();
                    // Setting the result handler to handle the result that getting from the QRCode.
                    mScannerView.setResultHandler(CheckInWithPnr.this);
                } else {
                    /* If the camera permission is not acceepted the error meassage will display*/
                    Toast.makeText(CheckInWithPnr.this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }




    //for searching the PNR number in database for check in
    private void CheckPNRInAPI() {

        Log.e("MYAPP", "pnr " + PNRNumber);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitInterface.BASEURL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        /* Creating the object of retrofit interface*/
        RetrofitInterface request = retrofit.create(RetrofitInterface.class);
        //Calling the API by passing the visitorID that entered by the use.
        Call<String> call = request.getPNRDetails(PNRNumber);
        call.enqueue(new Callback<String>() {

            /*If the result is sucessfull the onResponse will call otherwise onFailure function
               will call.
             */
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                /* Reading the data from the response*/

                try {
                    String jsonresponse = response.body().toString();
                    Log.e("MYAPP", "reponse" + jsonresponse);


                    JSONObject dataobj = new JSONObject(jsonresponse);
                    JSONArray dataArray = dataobj.getJSONArray("pnrdetails");
                    final int numberOfItemsInResp = dataArray.length();

                    if (dataArray.length() > 0) {

                        JSONObject originalData = dataArray.getJSONObject(0);
                        status = originalData.getString("status");
                        f_name = originalData.getString("name");
                        contactno = originalData.getString("contactno");
                        organiation = originalData.getString("organization");
                        purpose = originalData.getString("purpose");
                        contactPerson = originalData.getString("contactPerson");
                        email = originalData.getString("emailId");

                        int stat = dataobj.getInt("status");
                        Log.e("MYAPP", "reponse" + f_name);


                        //Opening the sharedPrefences and saving the data that get from API
                        //To use it in another activity
                        sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.putString("status", status);
                        editor.putString("FirstName", f_name);
                        editor.putString("Phone", contactno);
                        editor.putString("Email", email);
                        editor.putString("Company", organiation);
                        editor.putString("Purpose", purpose);
                        editor.putString("MeetWoom", contactPerson);
                        Log.e("MYAPP", "I am getting contact person" + contactPerson);
                        editor.putInt("ExistingUser", 0);
                        editor.apply();


                        /* After saving the data we will pass it to OTP activity and sending the OTP to the user*/
                        Intent InvitedVisitor = new Intent(CheckInWithPnr.this, OTPActivity.class);

                        // Start the new activity
                        startActivity(InvitedVisitor);
                    } else {

                        Intent InvitedVisitor = new Intent(CheckInWithPnr.this, CheckInWithPnr.class);
                        startActivity(InvitedVisitor);
                        /* If the PNR does not exist the Error message will display*/
                        TextView Error = (TextView) findViewById(R.id.validateVisId);
                        Error.setText("VisitorId Does not exist!!!");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Log.e("MYAPP", "exception: " + t.toString());

            }
        });

    }

    // Function to chage the colour of status bar.
    private void changeStatusBarColor(String color) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
            //window.setNavigationBarColor(Color.parseColor(color));
        }
    }

    //for handling the QR scan result
    @Override
    public void handleResult(Result result) {

        PNRNumber = result.getText();
        CheckInWithQRCode();
    }

    //After completion of QR scan which page you want show
    private void CheckInWithQRCode() {
        setContentView(R.layout.checkin_with_visitor_id);
        CheckPNRInAPI();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(CheckInWithPnr.this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void onStop() {
        mScannerView.stopCamera();
        super.onStop();
    }

}

