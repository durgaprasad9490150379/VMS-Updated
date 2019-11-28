package com.example.visitormgmt;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static android.provider.SettingsSlicesContract.AUTHORITY;


public class PreviewActivity extends AppCompatActivity {

    public String fname, mobile, email1, image, idproof,company,image_path,idproof_pathq
            , purpose, meet_whom, address, city, state,country, status  ;
    public int userExistingOrNot;
    Bitmap img_src, id_src;
    TextView status_code;
    ImageView img1;
    CircleImageView img2;

    private RelativeLayout llPdf;
    private Bitmap bitmap;
    private String dir;

    private static final int REQUEST = 112;
    private String mCurrentPhotoPath;
    SharedPreferences sharedpreferences;

    //public String token = "Bearer "+"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwiaXNBZG1pbiI6dHJ1ZSwiaWF0IjoxNTcxMjA4MjkyLCJleHAiOjE1NzM4MDAyOTJ9.fxBbFQ29gqQ-vPRDws0zHKIw3l0tEdB0rEfBvaJSVfs";



    public RetrofitInterface ApiInterfaceObject;

    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.checkin_thankyou);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        //[Geting data which is entered by user using shared preferences which is stored previous page]

//
//        editor.putString("status", "Mr");
//        editor.putString("FirstName", name_object.getText().toString());
//        editor.putString("LastName", "sss");
//        editor.putString("Email", email_object.getText().toString());
////                editor.putString("State", state_object.getText().toString());
////                editor.putString("Country", country_object.getText().toString());
////                editor.putString("City", city_object.getText().toString());
////                editor.putString("Address", address_object.getText().toString());
//        editor.putString("Company", organization_object.getText().toString());
//        editor.putString("Purpose", purpose);
//        editor.putString("MeetWhom", meetwhom_object.getText().toString());
//        editor.putString("Existing", "0");
//        editor.commit();
//
//


        sharedpreferences =  getSharedPreferences("MyPrefs", MODE_PRIVATE);
        status =  sharedpreferences.getString("status", "");
        fname = sharedpreferences.getString("FirstName", "");
        mobile = sharedpreferences.getString("Phone", "");
        email1 = sharedpreferences.getString("Email", "");
        image = sharedpreferences.getString("UserImage", "");
        idproof = sharedpreferences.getString("UserIDImage", "");
        company = sharedpreferences.getString("Company", "");
        purpose = sharedpreferences.getString("Purpose", "");
        meet_whom = sharedpreferences.getString("MeetWoom", "");

        sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();  // Clearing Shared Preferences
        editor.apply();

//        System.out.print(image);


        updateVisitorDetails();  // Calling updateVisitorDetails API

        //For Auto redirection to first page
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(PreviewActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        }, 5000);


    }



    //Generating QR Code
    private void UpdateQRCode() {
        String dataInQRCode = fname + "\n" + "\n" + mobile;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(dataInQRCode, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            img1.setImageBitmap(bitmap);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.draw(c);

        return b;
    }


    private void updateVisitorDetails() {


        System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>> Enter in function update post");


        OkHttpClient client1 = new OkHttpClient.Builder()
                .connectTimeout(1000, TimeUnit.SECONDS)
                .readTimeout(1000,TimeUnit.SECONDS).build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl(RetrofitInterface.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiInterfaceObject = retrofit.create(RetrofitInterface.class);


       JsonObject fields = new JsonObject();

        fields.addProperty("status", status);
        fields.addProperty("name", fname);
        fields.addProperty("contactno",mobile );
        fields.addProperty("organization", company);
        fields.addProperty("purpose", purpose);
//        fields.addProperty("contactPerson", meet_whom);
        fields.addProperty("emailId", email1 );
        fields.addProperty("visitorpassPic", image );  //
        fields.addProperty("visitorpassIdProof", idproof);  //idproof


        System.out.println("Request data is " + status + "  " +  fname + "  " + mobile + "  " + company + "  " + purpose + "  " + meet_whom + "  " + email1);
        System.out.println("#######");
        System.out.println("User Image >>>>>   " + image);
        System.out.println("#######");
        System.out.println("#######");
        System.out.println("User IdImage >>>>>   " + idproof);

        //clearing user selfie image and id proof image from shared preferences
        image = image.replace(" ", "");
        idproof = idproof.replace(" ", "");



        Call<Object> call = ApiInterfaceObject.updateExisting(fields);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {

                System.out.println("After calling API "  + response.code());


                if (!response.isSuccessful()) {
                    System.out.println("        Failure Response for UpdateVisitorDetails");
                    return;
                }else{
                    System.out.print(">>>>>>>>>>>>>>>>>>>>>>>>>code" + response.code());
                    Log.e("MYAPP", "created" + response.body().toString());

                    System.out.println("created      " + response.body().toString());


                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                System.out.print(">>>>>>>>>>>>>>>>>>>. Error");
                t.printStackTrace();

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
//                    createPdf();
                } else {
                    Toast.makeText(PreviewActivity.this, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
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
