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

public class AddDetails extends BaseActivity {

    private static final String TAG = "AddToDatabase";
    private Button mAddToDB;
    private EditText nNewName;
    private EditText nPhone;
    private EditText nWeight;
    private EditText nHeight;
    private Spinner mDoctorDropdown;

    private LinkedList<DoctorUser> mDoctors;
    private LinkedList<String> mDoctorNames;
    private LinkedList<String> mDoctorKeys;
    private String selectedDoctorKey;


    //add Firebase
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        mAddToDB = (Button) findViewById(R.id.btnAddNewName);
        nNewName = (EditText) findViewById(R.id.add_name);
        nPhone = (EditText) findViewById(R.id.add_phone);
        nWeight = (EditText) findViewById(R.id.add_weight);
        nHeight = (EditText) findViewById(R.id.add_height);
        mDoctorDropdown = findViewById(R.id.doctorDropdown);

        selectedDoctorKey = "";

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Object value = dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textBoxesNotEmpty()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    PatientUser userInfo = createUser();


                    myRef.child("Users").child(userID).setValue(userInfo);
                    toastMessage("Added "+ nNewName.getText().toString() + " successfully");

                    clearTextBoxes();

                }

            }
        });

        myRef.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                createDoctorDropdown(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDoctorDropdown.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDoctorKey = mDoctorKeys.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


    }

    private void createDoctorDropdown(DataSnapshot snapshotUsers) {
        LinkedList<DoctorUser> doctors = new LinkedList<>();
        LinkedList<String> doctorNames = new LinkedList<>();
        LinkedList<String> doctorKeys = new LinkedList<>();

        // add a blank initial doctor
        doctors.add(new DoctorUser());
        doctorNames.add("");
        doctorKeys.add("");

        for (DataSnapshot snapshotUser : snapshotUsers.getChildren()) {
            BaseUser baseUser = snapshotUser.getValue(BaseUser.class);
            if (baseUser.getIsDoctor()) {
                DoctorUser doctorUser = snapshotUser.getValue(DoctorUser.class);

                doctors.add(doctorUser);
                doctorNames.add(doctorUser.getName());
                doctorKeys.add(snapshotUser.getKey());
            }
        }

        mDoctors = doctors;
        mDoctorNames = doctorNames;
        mDoctorKeys = doctorKeys;

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, doctorNames);
        mDoctorDropdown.setAdapter(adapter);
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //add a toast to show when successfully signed in

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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

        return new PatientUser(
                nNewName.getText().toString(),
                nPhone.getText().toString(),
                nWeight.getText().toString(),
                nHeight.getText().toString(),
                selectedDoctorKey
        );
    }

    void clearTextBoxes()
    {
        nNewName.setText("");
        nPhone.setText("");
        nWeight.setText("");
        nHeight.setText("");
        selectedDoctorKey = "";
        mDoctorDropdown.setSelection(0);
    }
}
