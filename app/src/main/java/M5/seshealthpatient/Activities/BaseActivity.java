package M5.seshealthpatient.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import M5.seshealthpatient.Models.BaseUser;
import M5.seshealthpatient.R;

/**
*   BaseActivity class. All activites that are not Login/Register Activites extend this.
*   This activity contains logic to handle ActionBar toolbars, general Firebase Authenication
*   and Database actions, as well as animations for when navigating between activities.
*
*   IMPORTANT: any activity's that extends BaseActivity must contain a toolbar element with id '@+id/toolbar',
*   otherwise BaseActivity will error.
*/
public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private boolean isUserDoctor;

    protected int getLayoutId() {
        // layout id is to be overwritten by the extending class
        return 0;
    }

    // a check to see if a user is logged in
    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // get the current logged in user's id
    public String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    // allows an activity to set the toolbar title
    public void ChangeTitle(String newTitle) {
        toolbar.setTitle(newTitle);
    }

    // allow all activities that extend BaseActivity to access the private toolbar field.
    public Toolbar getToolbar() {
        return toolbar;
    }

    public boolean isUserDoctor() {
        return isUserDoctor;
    }

    public DatabaseReference getDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // If user is not authenticated, then redirect the user to the login view
        if (!isUserLoggedIn())
            navigateToLogin();

        setIsUserDoctor();

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        
        // New activities should transition from left to right
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        // setup the toolbar to add a back button on the top left side
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public void setIsUserDoctor() {
        // get listen to the logged in user's account, and check if the user is a doctor
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.child("Users").child(getUserId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                BaseUser user = dataSnapshot.getValue(BaseUser.class);
                isUserDoctor = user.getIsDoctor();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // when exiting an activity, the activity should exit from left to right
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                // when exiting an activity, the activity should exit from left to right
                overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToLogin() {
        Intent in = new Intent(BaseActivity.this, LoginActivity.class);
        startActivity(in);
        finish();
    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
        navigateToLogin();
        Toast.makeText(this, "Signed out successfully.", Toast.LENGTH_LONG).show();
    }

    public void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            // TODO: Log error
        }
    }

    /**
     * customizable toast
     *
     * @param message
     */
    public void toastMessage(Context context, String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
