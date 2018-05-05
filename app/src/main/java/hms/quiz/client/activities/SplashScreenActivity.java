package hms.quiz.client.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import hms.quiz.client.PrefManager;
import hms.quiz.client.R;
import hms.quiz.client.db.QuizDataSource;

public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    private QuizDataSource mDataSource;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
                String[] permissions4 = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions4, PERMISSION_REQUEST_CODE);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }
        TextView txt = findViewById(R.id.sinhala_font);
        Typeface font = Typeface.createFromAsset(getAssets(), "Amal.TTF");
        txt.setTypeface(font);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with activity_play_radio_button_selected_state timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                PrefManager prefManager = new PrefManager(getApplicationContext());
                // make first time launch TRUE
                prefManager.setFirstTimeLaunch(true);
                mDataSource = new QuizDataSource(SplashScreenActivity.this);
                mDataSource.open();
                String number = getNetworkOperatorName();
                String status = mDataSource.getSingleEntryFromLoginTable(number);
                if (!status.equals(getString(R.string.homeactivity_register_response))) {
                    Intent i = new Intent(SplashScreenActivity.this, WelcomeActivity.class);
                    startActivity(i);
                }else {
                    Intent i = new Intent(SplashScreenActivity.this, HomeActivity.class);
                    startActivity(i);
                }
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public String getNetworkOperatorName() {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, REQUEST_READ_PHONE_STATE);
            }
        }

        // To Get System TELEPHONY service ref
        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String networkOperatorName = tManager.getNetworkOperatorName();
        return networkOperatorName;

    }
}