package com.example.visitormgmt;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pub.devrel.easypermissions.EasyPermissions;

public class IdProofActivity1 extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{

    private static final String TAG = IdProofActivity1.class.getSimpleName();

    private ImageView imageView;
    private static final int CAMERA_REQUEST_CODE = 1450;
    private static final int CAMERA_PERMISSION_CODE = 1460;
    private String mCurrentPhotoPath;

    private static final String[] paths = {"Aadhar", "Driving Licence", "Voter ID"};


    MaterialBetterSpinner materialBetterSpinner ;

    public int userExistingOrNot = 0;

    Button uploadButton;
    Button nextToUser;

    SharedPreferences sharedpreferences;
    TextView IdProofvalidate;


    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_proof_activity);

        imageView = (ImageView) findViewById(R.id.image_capture_govt);
        IdProofvalidate = (TextView) findViewById(R.id.validateIdProof);
        uploadButton = (Button) findViewById(R.id.Take_picture_of_ID);
        nextToUser = (Button) findViewById(R.id.next_to_API);
        nextToUser.setVisibility(View.GONE);
        sharedpreferences =  getSharedPreferences("MyPrefs", MODE_PRIVATE);
        userExistingOrNot = sharedpreferences.getInt("UserExistOrNot", 0);

        materialBetterSpinner = (MaterialBetterSpinner)findViewById(R.id.IDProof);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(IdProofActivity1.this, android.R.layout.simple_dropdown_item_1line, paths);

        materialBetterSpinner.setAdapter(adapter);
        materialBetterSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdProofvalidate.setVisibility(View.GONE);
            }
        });

        ButtonClickListener();

    }

    private void ButtonClickListener(){

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if(materialBetterSpinner.getText().toString().equals("")){
                    IdProofvalidate.setVisibility(View.VISIBLE);
                    IdProofvalidate.setText("Please select IdProof type");
                    return;
                }


                //check if app has permission to access the camera.
                if (EasyPermissions.hasPermissions(IdProofActivity1.this, Manifest.permission.CAMERA)) {
                    launchCamera();

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(IdProofActivity1.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
                }
            }
        });

        nextToUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent CameraActivity = new Intent(IdProofActivity1.this, CameraActivity.class);
                    startActivity(CameraActivity);
            }
        });
    }

    private void launchCamera() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {

            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this,
                    getApplicationContext().getPackageName() + ".fileProvider",
                    photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //Start the camera application
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);

        }
    }

    /**
     * Previews the captured picture on the app
     * Called when the picture is taken
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Preview the image captured by the camera
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            imageView.setImageBitmap(bitmap);

            String base64 = bitmapToBase64(bitmap);

            sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("UserIDImage", base64);
            editor.apply();

            uploadButton.setVisibility(View.GONE);
            nextToUser.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, IdProofActivity1.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        launchCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }


    /**
     * Creates the image file in the external directory
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);




        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */

        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        editor.putString("IdProofPath", mCurrentPhotoPath);
        editor.apply();
        return image;
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

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        result = result.replaceAll("//s", "");
        return result;
    }

    public static String ConvertBitmapToString(Bitmap bitmap){
        String encodedImage = "";

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        try {
            encodedImage= URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return encodedImage;
    }

}