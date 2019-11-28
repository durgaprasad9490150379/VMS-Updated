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
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import android.os.Environment;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class CameraActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private static final String TAG = CameraActivity.class.getSimpleName();

    private ImageView imageView;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 1460;
    private String mCurrentPhotoPath;

    SharedPreferences sharedpreferences;
    Button captureImage, nextToGovt;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        /*Calling the function to change the color of satus bar*/
        changeStatusBarColor("#40a7e5");
        // Setting the orentation to landscape mode only
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        /*Creating the object of text and image views*/

        this.imageView = (ImageView) this.findViewById(R.id.image_capture);

        captureImage = (Button) findViewById(R.id.CamptureImage);
        nextToGovt = (Button) findViewById(R.id.next_to_govt);

        nextToGovt.setVisibility(View.GONE);

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check if app has permission to access the camera.
                if (EasyPermissions.hasPermissions(CameraActivity.this, Manifest.permission.CAMERA)) {
                    launchCamera();

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(CameraActivity.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
                }

            }
        });

        nextToGovt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Changing the activity from camera activity to
                   final page.
                 */

                Intent Preview = new Intent(CameraActivity.this, PreviewActivity.class);

                startActivity(Preview);

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
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
            //Start the camera application
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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Preview the image captured by the camera
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK ) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 4;
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            //Converting Bitmap to string
            String base64 = bitmapToBase64(bitmap);
            //Image set to imageview in layout
            imageView.setImageBitmap(bitmap);

            /*String of image is storing in sharedpreferences to carry it to
            last page to post it to Api*/
            sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString("UserImage",base64);
            editor.apply();
            /*Changing  the buttons after taking the capture of image (Capture Image to Next button)*/
            captureImage.setVisibility(View.GONE);
            nextToGovt.setVisibility(View.VISIBLE);
        }
    }

    //After getting the result from camera permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, CameraActivity.this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //If permission is granted the camera will launch and visitor should take the photo.
        launchCamera();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //If the permissition is denied by the visitor the error meassage will show.
        Log.d(TAG, "Permission has been denied");
    }


    /**
     * Creates the image file in the external directory
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {

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

        return image;
    }

    /* To change the colour of status bar*/
    private void changeStatusBarColor(String color){
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
            window.setNavigationBarColor(Color.parseColor(color));
        }
    }

    /* Converting the Bitmap image to string using this function and it
    will return the base64 string
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String result = Base64.encodeToString(byteArray, Base64.DEFAULT);
        result = result.replaceAll("//s", "");
        return result;
    }



}