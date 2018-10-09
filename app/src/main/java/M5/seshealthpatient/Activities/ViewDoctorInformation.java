package M5.seshealthpatient.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import M5.seshealthpatient.Models.DoctorUser;
import M5.seshealthpatient.Models.PatientUser;

import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import M5.seshealthpatient.R;

public class ViewDoctorInformation extends BaseActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private  String userID;

    private ListView mListView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_doctor_information;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("View Information");

        mListView = (ListView) findViewById(R.id.listview2);

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    toastMessage("Successfully signed in with: " + user.getEmail());
                } else {
                    // User is signed out

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
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void showData(DataSnapshot dataSnapshot) {
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            DoctorUser uInfo = new DoctorUser();

            DoctorUser user = ds.child(userID).getValue(DoctorUser.class);
            if (user == null) {
                uInfo.setName(""); //set the name
                uInfo.setDepartment(""); //set the department
                uInfo.setOccupation(""); //set the occupation
                uInfo.setIntroduction("");//set the introduction

            } else {
                uInfo = user;
            }

            ArrayList<String> array = new ArrayList<>();
            array.add("Name:        " + uInfo.getName());
            array.add("Department:  " + uInfo.getDepartment());
            array.add("Occupation:      " + uInfo.getOccupation());
            array.add("Introduction:      " + uInfo.getIntroduction());

            ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, array);
            mListView.setAdapter(adapter);
        }
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

    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}
