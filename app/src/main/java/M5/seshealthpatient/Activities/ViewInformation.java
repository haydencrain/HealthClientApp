package M5.seshealthpatient.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import M5.seshealthpatient.Activities.AddDetails;
import M5.seshealthpatient.Models.UserInformation;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class ViewInformation extends AppCompatActivity {
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private  String userID;

    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_information);

        mListView = (ListView) findViewById(R.id.listview);

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
            UserInformation uInfo = new UserInformation();

            UserInformation user = ds.child(userID).getValue(UserInformation.class);
            if (user == null) {
                uInfo.setName(""); //set the name
                uInfo.setPhone(""); //set the phone
                uInfo.setWeight(""); //set the weight
                uInfo.setHeight("");//set the height
                uInfo.setDoctorID(""); //set the doctorID

            } else {
                uInfo.setName(user.getName()); //set the name
                uInfo.setPhone(user.getPhone()); //set the phone
                uInfo.setWeight(user.getWeight()); //set the weight
                uInfo.setHeight(user.getHeight());//set the height
                uInfo.setDoctorID(user.getDoctorID()); //set the doctorID

            }

            ArrayList<String> array = new ArrayList<>();
            array.add(uInfo.getName());
            array.add(uInfo.getPhone());
            array.add(uInfo.getWeight() + "kg");
            array.add(uInfo.getHeight() + "cm");
            array.add(uInfo.getDoctorID()+ "ID");


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
