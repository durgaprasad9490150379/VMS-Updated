package com.example.visitormgmt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

public class Invited_visitor_activity extends AppCompatActivity {



    MaterialBetterSpinner materialBetterSpinner ;
    SharedPreferences sharedpreferences;
    
    String[] SPINNER_DATA = {"Mr.","Mrs.","Ms."};

    TextInputEditText name_object, mobile_object, email_object, organization_object, comapny_object,  meetwhom_object;
    EditText purpose_object;
    public String fname, mobile, email1,company
            , purpose, meet_whom, visitor_id, status ;

    TextView MeetWoomErrorMesage, FirstNameErrorMessage, EmailErrormessage, OranizationErrorMessage,SubjectErrorMesage, MobieErrorMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeStatusBarColor("#40a7e5");
        setContentView(R.layout.visitor_info_existing);
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        setContentView(R.layout.visitor_info_existing);

        materialBetterSpinner = (MaterialBetterSpinner)findViewById(R.id.material_spinner1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(Invited_visitor_activity.this, android.R.layout.simple_dropdown_item_1line, SPINNER_DATA);

        materialBetterSpinner.setAdapter(adapter);

        name_object = (TextInputEditText) findViewById(R.id.name_txt);
        mobile_object = (TextInputEditText) findViewById(R.id.mobile_txt);
        email_object = (TextInputEditText) findViewById(R.id.email_txt);
        organization_object = (TextInputEditText) findViewById(R.id.organization_txt);
        meetwhom_object = (TextInputEditText) findViewById(R.id.meet_whom_txt);
        purpose_object = (EditText) findViewById(R.id.subject_txt);


        FirstNameErrorMessage = (TextView) findViewById(R.id.validateName);
        EmailErrormessage = (TextView) findViewById(R.id.validateEmail);
        OranizationErrorMessage = (TextView) findViewById(R.id.validateOrg);
        SubjectErrorMesage  = (TextView) findViewById(R.id.validateSubject);
        MeetWoomErrorMesage = (TextView) findViewById(R.id.validateMeetWhom);
        MobieErrorMessage = (TextView) findViewById(R.id.validateMobile);


        //Getting visitor information from shared preferences
        sharedpreferences =  getSharedPreferences("MyPrefs", MODE_PRIVATE);
        status = sharedpreferences.getString("status", "");
        fname = sharedpreferences.getString("FirstName", "");
        mobile = sharedpreferences.getString("Phone", "");
        email1 = sharedpreferences.getString("Email", "");
        company = sharedpreferences.getString("Company", "");
        purpose = sharedpreferences.getString("Purpose", "");
        meet_whom = sharedpreferences.getString("MeetWoom", "");


        //Setting the data to edit text fields
        name_object.setText(fname);
        mobile_object.setText(mobile);
        email_object.setText(email1);
        organization_object.setText(company);
        meetwhom_object.setText(meet_whom);
        purpose_object.setText(purpose);
        materialBetterSpinner.setText(status);


        materialBetterSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                status = adapterView.getItemAtPosition(position).toString();
            }
        });


        Button submit = (Button) findViewById(R.id.next);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();


                System.out.println("Company name from API" + company);


                if(name_object.getText().toString().isEmpty()){
                    FirstNameErrorMessage.setVisibility(View.VISIBLE);
                    FirstNameErrorMessage.setText("Field cannot be empty");
                    return;
                }


                if(!validateEmail()){
                    EmailErrormessage.setVisibility(View.VISIBLE);
                    EmailErrormessage.setText("Invalid Email");
                    return;
                }
                if(organization_object.getText().toString().isEmpty()){
                    OranizationErrorMessage.setVisibility(View.VISIBLE);
                    OranizationErrorMessage.setText("Field cannot be empty");
                    return;
                }

                if(purpose_object.getText().toString().isEmpty()){
                    SubjectErrorMesage.setVisibility(View.VISIBLE);
                    SubjectErrorMesage.setText("Field cannot be empty");
                    return;
                }

                if(meetwhom_object.getText().toString().isEmpty()){
                    MeetWoomErrorMesage.setVisibility(View.VISIBLE);
                    MeetWoomErrorMesage.setText("Field cannot be empty");
                    return;
                }
                mobile = mobile_object.getText().toString();

                if(mobile_object.getText().toString().isEmpty() || mobile.length() > 10 || mobile.length() < 10){
                    MobieErrorMessage.setVisibility(View.VISIBLE);
                    MobieErrorMessage.setText("Field cannot be empty");
                    return;
                }


                editor.putString("status", "Mr");
                editor.putString("FirstName", name_object.getText().toString());
                editor.putString("LastName", "sss");
                editor.putString("Email", email_object.getText().toString());
                editor.putString("Company", organization_object.getText().toString());
                editor.putString("Purpose", purpose);
                editor.putString("MeetWhom", meetwhom_object.getText().toString());
                editor.putString("Existing", "0");
                editor.commit();

                Intent Camera = new Intent(Invited_visitor_activity.this, IdProofActivity1.class);
                startActivity(Camera);


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

    public final boolean validateEmail(){
        email1 = email_object.getText().toString().trim();

        if (email1.isEmpty()) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email1).matches();
        }
    }

}




