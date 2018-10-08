package M5.seshealthpatient.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import M5.seshealthpatient.R;

public class BaseActivity extends AppCompatActivity {

    private Toolbar toolbar;

    protected int getLayoutId() { return 0; }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (!isUserLoggedIn())
            navigateToLogin();

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        
        overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    /**
     * This function changes the title of the fragment.
     *
     * @param newTitle The new title to write in the
     */
    public void ChangeTitle(String newTitle) {
        toolbar.setTitle(newTitle);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.trans_right_in, R.anim.trans_right_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
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
}
