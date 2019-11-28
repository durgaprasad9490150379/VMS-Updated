package com.example.visitormgmt;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.zxing.Result;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CheckOutActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public RetrofitInterface ApiInterfaceObject;

    public String visitorID, mobileNumber, visitorCheckout;

    private ZXingScannerView mScannerView;
    public int status_code;

    private static final int CAMERA_PERMISSION_CODE = 1460;


    TextInputEditText visitorID_object, mobileNumber_object;
    Button checkout, scan_qr_code;
    TextView result ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        visitorID_object = (TextInputEditText) findViewById(R.id.visitorid_txt);
        mobileNumber_object = (TextInputEditText) findViewById(R.id.mobile_txt);

        checkout = (Button) findViewById(R.id.proceed);
        scan_qr_code = (Button) findViewById(R.id.qrcode);


        mScannerView = new ZXingScannerView(CheckOutActivity.this);



        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visitorID = visitorID_object.getText().toString();
                mobileNumber = mobileNumber_object.getText().toString();
                if (visitorID.isEmpty() && mobileNumber.isEmpty()) {
                    result.setText("Please enter yout VisitorID");
                    return;
                } else if (!visitorID.isEmpty() && !mobileNumber.isEmpty()) {
                    result.setText("Please enter only one");
                    return;
                } else {

                    if(visitorID.isEmpty()){
                        create_checkout_mobile();
                    } else {

                        create_checkout();
                    }
                }
            }
        });



        scan_qr_code.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (EasyPermissions.hasPermissions(CheckOutActivity.this, Manifest.permission.CAMERA)) {

                    // Creating the object for Zxing to scan the QRCode.
                    mScannerView = new ZXingScannerView(CheckOutActivity.this);
                    // Set the scanner view as the content view.
                    setContentView(mScannerView);
                    // The camera is started to scan the QRCode.
                    mScannerView.startCamera();
                    // Setting the result handler to handle the result that getting from the QRCode.
                    mScannerView.setResultHandler(CheckOutActivity.this);

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(CheckOutActivity.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
                }
            }
        });
    }




    //Checkout using Visitor Id
    private void   create_checkout() {

        OkHttpClient client1 = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl(RetrofitInterface.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterfaceObject = retrofit.create(RetrofitInterface.class);

        System.out.println(">>>>>>>>>>>> >>>  In Checkout Using VisitoId function. VisitorId = "+ visitorID);


//        Call<Object> call = ApiInterfaceObject.createPost( token, fields);
        Call<checkOutStatus> call = ApiInterfaceObject.createCheckOut(visitorID);

        call.enqueue(new Callback<checkOutStatus>() {
            @Override
            public void onResponse(Call<checkOutStatus> call, Response<checkOutStatus> response) {

                // textViewResult.setText();
                if (!response.isSuccessful()) {
                    result.setText("Response!!! failure");
                    System.out.println("Response!!! failure");
                    return;
                }else{


                    try {
                        int status = response.body().getStatus();
                        status_code = status;
                        System.out.println("In API stauscode = " + status_code + "status = " + status);
                        System.out.println(">>>>>>>>>>>>Response is coming  code=" + response.code() + " and status=" + status);

                        if(status == 400) {
                            Intent refreshActivity = new Intent(CheckOutActivity.this, CheckOutActivity.class);
//                             Start the new activity
                            startActivity(refreshActivity);
                            TextView result = (TextView) findViewById(R.id.validateInput);
                            result.setText("VisitorID does not exist");

                            System.out.println("Visitor Id does not exist");
                        }
                        else{
                            System.out.println(">>>>>>>>> CheckedOut Succesfully  " + response.code());
                            Intent thankyouActivity = new Intent(CheckOutActivity.this, Thankyou.class);
//                             Start the new activity
                            startActivity(thankyouActivity);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<checkOutStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });


    }

    //Checkout using mobile number
    private void   create_checkout_mobile() {

        OkHttpClient client1 = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl(RetrofitInterface.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterfaceObject = retrofit.create(RetrofitInterface.class);


        System.out.println(">>>>>>>>>>>>In Checkout using  mobile number function. mobile number = "+ mobileNumber);


        Call<checkOutStatus> call = ApiInterfaceObject.createCheckOut_mobileNumber(mobileNumber);

        call.enqueue(new Callback<checkOutStatus>() {
            @Override
            public void onResponse(Call<checkOutStatus> call, Response<checkOutStatus> response) {

                if (!response.isSuccessful()) {
                    result.setText("Response!!! failure");
                    System.out.println(">>>>>>>>>>>> >>>Response!!! failure" );
                    return;
                }else{
                    System.out.println("Response is coming. " + response.body().toString());
                    try {
                        int status = response.body().getStatus();

                        if(status == 400) {
                            TextView result = (TextView) findViewById(R.id.validateInput);
                            result.setText("Mobile number does not exist");
                        }
                        else{
                            System.out.println(">>>>>>>>>>>> >>> Checked-Out Successfully & code=  " + response.code());

                            Intent thankyouActivity = new Intent(CheckOutActivity.this, Thankyou.class);
                            // Start the new activity
                            startActivity(thankyouActivity);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<checkOutStatus> call, Throwable t) {
                t.printStackTrace();
            }
        });
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

    //Permissions for QR Scanner
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE: {

                /* If the camera permission is granted the QRcode scanner will start*/
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Creating the object for Zxing to scan the QRCode.
                    mScannerView = new ZXingScannerView(CheckOutActivity.this);
                    // Set the scanner view as the content view
                    setContentView(mScannerView);
                    // The camera is started to scan the QRCode.
                    mScannerView.startCamera();
                    // Setting the result handler to handle the result that getting from the QRCode.
                    mScannerView.setResultHandler(CheckOutActivity.this);
                } else {
                    /* If the camera permission is not acceepted the error meassage will display*/
                    Toast.makeText(CheckOutActivity.this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    //For handling QR code scan result
    @Override
    public void handleResult(Result result) {
        visitorID = result.getText();
        System.out.println("VIsitor Id from QR Code" + visitorID);
//        create_checkout();
        CheckOutWithQRCode();
    }

    private void CheckOutWithQRCode() {
        create_checkout();
        setContentView(R.layout.checkout);


    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(CheckOutActivity.this);
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



