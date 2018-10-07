package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import M5.seshealthpatient.Models.DoctorUser;
import M5.seshealthpatient.Models.PatientUser;
import M5.seshealthpatient.R;

public class RegisterActivity extends AppCompatActivity {
    EditText emailTxt, passTxt,confirmTxt;
    RadioGroup radioGroup;
    RadioButton radioButton1, radioButton2;
    FirebaseAuth auth;
    FirebaseDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emailTxt = (EditText)findViewById(R.id.email_field);
        passTxt = (EditText)findViewById(R.id.password_field);
        confirmTxt = (EditText)findViewById(R.id.confirmTxt);
        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        radioButton1 = (RadioButton)findViewById(R.id.radio_patient);
        radioButton2 = (RadioButton)findViewById(R.id.radio_doctor);
        db = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public void createUser(View v){

       // int radiobuttonid = radioGroup.getCheckedRadioButtonId();
        // RadioButton radioButton = (RadioButton) findViewById(radiobuttonid);

        String confirmPassword = confirmTxt.getText().toString();
        String email = emailTxt.getText().toString();
        String password = passTxt.getText().toString();

        if(emailTxt.getText().toString().equals("") && passTxt.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Blank not allowed", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }

        else if(password.length()<6)
        {
            Toast.makeText(getApplicationContext(), "Password less than 6 characters!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            auth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                // IF RADIO BUTTON IS PATIENT
                                    // Add a new empty Patient Record
                                    if(radioButton1.isChecked()) {
                                        db.getReference()
                                                .child("Users")
                                                .child(auth.getCurrentUser().getUid())
                                                .setValue(new PatientUser());
                                    }
                                    else if(radioButton2.isChecked())
                                    {
                                        db.getReference()
                                                .child("Users")
                                                .child(auth.getCurrentUser().getUid())
                                                .setValue(new DoctorUser());
                                    }
                                // ELSE
                                    // Add a new empty Doctor Record
                                    //db.getReference()
                                    //        .child("Users")
                                    //        .child(auth.getCurrentUser().getUid())
                                    //        .setValue(new DoctorUser());

                                Toast.makeText(getApplicationContext(),"Patient is registered", Toast.LENGTH_SHORT).show();
                                Intent in = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(in);
                        }
                        else{
                                Toast.makeText(getApplicationContext(),"Patient cannot be registered", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }




}
