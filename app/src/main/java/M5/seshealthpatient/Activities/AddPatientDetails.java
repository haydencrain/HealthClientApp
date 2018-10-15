package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
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
import android.widget.RadioButton;
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

public class AddPatientDetails extends BaseActivity {

    private static final String TAG = "AddToDatabase";
    private Button mAddToDB;
    private EditText nNewName;
    private EditText nAge;
    private EditText nPhone;
    private EditText nWeight;
    private EditText nHeight;
    private EditText nMedicalCondition;
    private RadioButton radioMale;
    private RadioButton radioFemale;
    private Spinner mDoctorDropdown;

    private LinkedList<DoctorUser> mDoctors;
    private LinkedList<String> mDoctorNames;
    private LinkedList<String> mDoctorKeys;
    private String selectedDoctorKey;

    //add Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference mUsersDb;

    private ValueEventListener mUsersEvent;
    private OnItemSelectedListener mSelectedItemEvent;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_details;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Details");
        bindViewComponents();
        ButterKnife.bind(this);
        selectedDoctorKey = "";
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mUsersDb = myRef.child("Users");
    }

    @Override
    public void onStart() {
        mUsersEvent = getFirebaseUsersEvent();
        mSelectedItemEvent = getSelectedItemEvent();
        mUsersDb.addValueEventListener(mUsersEvent);
        mDoctorDropdown.setOnItemSelectedListener(mSelectedItemEvent);
        super.onStart();
    }

    @Override
    public void onStop() {
        mUsersDb.removeEventListener(mUsersEvent);
        super.onStop();
    }

    public ValueEventListener getFirebaseUsersEvent() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PatientUser user = dataSnapshot.child(mAuth.getUid()).getValue(PatientUser.class);
                setTextBoxes(
                        user.getName(),
                        user.getSex(),
                        user.getAge(),
                        user.getPhone(),
                        user.getWeight(),
                        user.getHeight(),
                        user.getMedicalCondition(),
                        user.getDoctorID()
                );
                createDoctorDropdown(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public OnItemSelectedListener getSelectedItemEvent() {
        return new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDoctorKey = mDoctorKeys.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        };
    }

    public void bindViewComponents() {
        mAddToDB = (Button) findViewById(R.id.btnAddNewName);
        nNewName = (EditText) findViewById(R.id.add_name);
        nAge = (EditText) findViewById(R.id.add_age);
        nPhone = (EditText) findViewById(R.id.add_phone);
        nWeight = (EditText) findViewById(R.id.add_weight);
        nHeight = (EditText) findViewById(R.id.add_height);
        nMedicalCondition = (EditText) findViewById(R.id.add_medical_condition);
        radioMale = (RadioButton) findViewById(R.id.radio_male);
        radioFemale = (RadioButton) findViewById(R.id.radio_female);
        mDoctorDropdown = findViewById(R.id.doctorDropdown);
    }

    @OnClick(R.id.btnAddNewName)
    public void onClick(View view) {
        if (textBoxesNotEmpty()) {
            PatientUser userInfo = createUser();
            setValuesToUser(userInfo, mUsersDb.child(mAuth.getUid()));
            toastMessage(AddPatientDetails.this, "Added "+ nNewName.getText().toString() + " successfully");
        }
    }

    private void createDoctorDropdown(DataSnapshot snapshotUsers) {
        LinkedList<DoctorUser> doctors = new LinkedList<>();
        LinkedList<String> doctorNames = new LinkedList<>();
        LinkedList<String> doctorKeys = new LinkedList<>();
        int dropdownSelection = 0;
        // add a blank initial doctor
        doctors.add(new DoctorUser());
        doctorNames.add("");
        doctorKeys.add("");

        int i = 0;
        for (DataSnapshot snapshotUser : snapshotUsers.getChildren()) {
            BaseUser baseUser = snapshotUser.getValue(BaseUser.class);
            if (baseUser.getIsDoctor()) {
                i++;
                DoctorUser doctorUser = snapshotUser.getValue(DoctorUser.class);
                doctors.add(doctorUser);
                doctorNames.add(doctorUser.getName());
                doctorKeys.add(snapshotUser.getKey());
                if (snapshotUser.getKey().equals(selectedDoctorKey)) {
                    dropdownSelection = i;
                }
            }
        }

        mDoctors = doctors;
        mDoctorNames = doctorNames;
        mDoctorKeys = doctorKeys;

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctorNames);
        mDoctorDropdown.setAdapter(adapter);
        mDoctorDropdown.setSelection(dropdownSelection);
    }

    boolean textBoxesNotEmpty() {
        String newName = nNewName.getText().toString();
        String newPhone = nPhone.getText().toString();
        String newWeight = nWeight.getText().toString();
        String newHeight = nHeight.getText().toString();
        return !newName.equals("") && !newPhone.equals("") && !newWeight.equals("") && !newHeight.equals("") && !selectedDoctorKey.equals("");
    }


    //creates the user object from the user input
    PatientUser createUser() {
        Log.d(TAG, "onClick: Attempting to add object to database.");

        String sex = "";
        if (radioMale.isChecked())
            sex = "Male";
        if (radioFemale.isChecked()) {
            sex = "Female";
        }

        return new PatientUser(
                nNewName.getText().toString(),
                sex,
                nAge.getText().toString(),
                nPhone.getText().toString(),
                nWeight.getText().toString(),
                nHeight.getText().toString(),
                nMedicalCondition.getText().toString(),
                selectedDoctorKey
        );
    }

    public void setValuesToUser(PatientUser user, DatabaseReference userRef) {
        userRef.child("name").setValue(user.getName());
        userRef.child("age").setValue(user.getAge());
        userRef.child("sex").setValue(user.getSex());
        userRef.child("phone").setValue(user.getPhone());
        userRef.child("weight").setValue(user.getWeight());
        userRef.child("height").setValue(user.getHeight());
        userRef.child("medicalCondition").setValue(user.getMedicalCondition());
        userRef.child("doctorID").setValue(user.getDoctorID());
        userRef.child("isDoctor").setValue(user.getIsDoctor());
    }

    void setTextBoxes(String name, String sex, String age, String phone, String weight, String height, String medicalCondition, String doctorID)
    {
        nAge.setText(age);
        nNewName.setText(name);
        nPhone.setText(phone);
        nWeight.setText(weight);
        nHeight.setText(height);
        nMedicalCondition.setText(medicalCondition);
        selectedDoctorKey = doctorID;

        radioMale.setChecked(false);
        radioFemale.setChecked(false);
        if (sex.equals("Male")) {
            radioMale.setChecked(true);
        }
        if (sex.equals("Female")) {
            radioFemale.setChecked(true);
        }
    }
}
