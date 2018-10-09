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
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_doctor_profile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Add Doctor Details");

        mAddToDB = (Button) findViewById(R.id.btnAddNewName);
        nName = (EditText) findViewById(R.id.add_doctor_name);
        nDepartment = (EditText) findViewById(R.id.add_doctor_department);
        nOccupation = (EditText) findViewById(R.id.add_doctor_occupation);
        nIntroduction = (EditText) findViewById(R.id.add_doctor_introduction);

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

                    DoctorUser userInfo = createUser();
                    DatabaseReference userRef = myRef.child("Users").child(userID);
                    setValuesToUser(userInfo, userRef);
                    toastMessage("Added "+ nName.getText().toString() + " successfully");

                    clearTextBoxes();

                }

            }
        });
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

    void clearTextBoxes()
    {
        nName.setText("");
        nDepartment.setText("");
        nOccupation.setText("");
        nIntroduction.setText("");
    }
}
