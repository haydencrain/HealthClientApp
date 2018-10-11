package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.Models.DoctorUser;
import M5.seshealthpatient.Models.PatientUser;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import M5.seshealthpatient.R;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddDoctorDetails extends BaseActivity {

    private static final String TAG = "AddDoctorDetailsToDatabase";
    private Button mAddToDB;
    private EditText nName;
    private EditText nDepartment;
    private EditText nOccupation;
    private EditText nIntroduction;

    //add Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference mUserDb;

    private ValueEventListener mUserEvent;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_doctor_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Doctor Details");
        bindViewComponents();
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mUserDb = myRef.child("Users/" + mAuth.getUid());
    }


    @Override
    public void onStart() {
        mUserEvent = getFirebaseUserEvent();
        mUserDb.addValueEventListener(mUserEvent);
        super.onStart();
    }

    @Override
    public void onStop() {
        mUserDb.removeEventListener(mUserEvent);
        super.onStop();
    }

    public ValueEventListener getFirebaseUserEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DoctorUser user = dataSnapshot.getValue(DoctorUser.class);
                setTextBoxes(
                        user.getName(),
                        user.getDepartment(),
                        user.getOccupation(),
                        user.getIntroduction()
                );
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void bindViewComponents() {
        mAddToDB = (Button) findViewById(R.id.btnAddNewName);
        nName = (EditText) findViewById(R.id.add_doctor_name);
        nDepartment = (EditText) findViewById(R.id.add_doctor_department);
        nOccupation = (EditText) findViewById(R.id.add_doctor_occupation);
        nIntroduction = (EditText) findViewById(R.id.add_doctor_introduction);
    }

    boolean textBoxesNotEmpty() {
        String newName = nName.getText().toString();
        String newDepartment = nDepartment.getText().toString();
        String newOccupation = nOccupation.getText().toString();
        String newIntroduction = nIntroduction.getText().toString();
        return !newName.equals("") && !newDepartment.equals("") && !newOccupation.equals("") && !newIntroduction.equals("");
    }


    //creates the user object from the user input
    DoctorUser createUser() {
        Log.d(TAG, "onClick: Attempting to add object to database.");

        return new DoctorUser(
                nName.getText().toString(),
                nDepartment.getText().toString(),
                nOccupation.getText().toString(),
                nIntroduction.getText().toString()
        );
    }

    public void setValuesToUser(DoctorUser user, DatabaseReference userRef) {
        userRef.child("name").setValue(user.getName());
        userRef.child("department").setValue(user.getDepartment());
        userRef.child("occupation").setValue(user.getOccupation());
        userRef.child("introduction").setValue(user.getIntroduction());
    }

    void setTextBoxes(String name, String department, String occupation, String introduction)
    {
        nName.setText(name);
        nDepartment.setText(department);
        nOccupation.setText(occupation);
        nIntroduction.setText(introduction);
    }

    @OnClick(R.id.btnAddNewName)
    public void onBtnClick(View view) {
        if (textBoxesNotEmpty()) {
            DoctorUser userInfo = createUser();
            setValuesToUser(userInfo, mUserDb);
            toastMessage(AddDoctorDetails.this, "Added "+ nName.getText().toString() + " successfully");
        }
    }
}
