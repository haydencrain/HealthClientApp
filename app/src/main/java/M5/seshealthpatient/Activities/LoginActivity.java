package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.Models.DoctorUser;
import M5.seshealthpatient.Models.PatientUser;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import M5.seshealthpatient.R;

/**
 * Class: LoginActivity
 * Extends: {@link AppCompatActivity}
 * Author: Carlos Tirado < Carlos.TiradoCorts@uts.edu.au> and YOU!
 * Description:
 * <p>
 * Welcome to the first class in the project. I will be leaving some comments like this through all
 * the classes I write in order to help you get a hold on the project. Here I took the liberty of
 * creating an empty Log In activity for you to fill in the details of how your log in is
 * gonna work. Please, Modify Accordingly!
 * <p>
 */
public class LoginActivity extends AppCompatActivity {

    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private  String userID;

    /**
     * Use the @BindView annotation so Butter Knife can search for that view, and cast it for you
     * (in this case it will get casted to Edit Text)
     */
    @BindView(R.id.usernameET)
    EditText usernameEditText;

    /**
     * If you want to know more about Butter Knife, please, see the link I left at the build.gradle
     * file.
     */
    @BindView(R.id.passwordET)
    EditText passwordEditText;

    /**
     * It is helpful to create a tag for every activity/fragment. It will be easier to understand
     * log messages by having different tags on different places.
     */
    private static String TAG = "LoginActivity";

    private FirebaseAuth auth;
    private FirebaseDatabase db;
    private DoctorUser doctorUser;

    ValueEventListener mUserListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // You need this line on your activity so Butter Knife knows what Activity-View we are referencing
        ButterKnife.bind(this);


        // Please try to use more String resources (values -> strings.xml) vs hardcoded Strings.
        setTitle(R.string.login_activity_title);
        myRef = mFirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();

        // if there is a user already logged in, navigate them to the main screen
        handleIfAlreadyLoggedIn();
    }


    public void handleIfAlreadyLoggedIn() {
        if (auth.getCurrentUser() != null)
            handleIfDoctorOrPatient();
    }

    public void handleIfDoctorOrPatient() {
        userID = auth.getUid();
        mUserListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BaseUser user = dataSnapshot.getValue(BaseUser.class);
                if(!user.getIsDoctor())
                    navigatetoPatientMain();
                else
                    navigatetoDoctorMain();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        myRef.child("Users").child(userID).addValueEventListener(mUserListener);

    }


    /**
     * See how Butter Knife also lets us add an on click event by adding this annotation before the
     * declaration of the function, making our life way easier.
     */
    @OnClick(R.id.login_btn)
    public void LogIn() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        // Having a tag, and the name of the function on the console message helps allot in
        // knowing where the message should appear.
        Log.d(TAG, "LogIn: username: " + username + " password: " + password);

        auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            handleIfDoctorOrPatient();
                        }
                        else {
                            Toast.makeText(getApplicationContext(),"Account does not exist!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    };


    // Start a new activity
    public void onRegisterClick(View view) {
     navigateToRegister();
    }

    public void navigateToRegister() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void navigatetoPatientMain() {
        Intent intent = new Intent(LoginActivity.this, PatientActivity.class);
        startActivity(intent);
        finish();
    }

    public void navigatetoDoctorMain() {
        Intent intent = new Intent(LoginActivity.this, DoctorActivity.class);
        startActivity(intent);
        finish();
    }

    // when the activity finishes, ensure that event listeners are removed from the firebase instance,
    // otherwise they will persist and execute for the entirety of the app's lifetime.
    @Override
    public void onStop() {
        if (auth.getCurrentUser() != null && mUserListener != null)
            myRef.child("Users").child(auth.getUid()).removeEventListener(mUserListener);

        super.onStop();
    }

}
