package hms.quiz.client.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;
import hms.quiz.client.Constant;
import hms.quiz.client.R;
import hms.quiz.client.db.QuizDataSource;

public class MoreFeaturesActivity extends AppCompatActivity {

    private Button btnMove, btnMove2, btnMove3;
    private Animation animMove1, animMove2, animMove3;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private QuizDataSource mDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_features);
        mDataSource = new QuizDataSource(this);
        mDataSource.open();

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }

        btnMove = findViewById(R.id.change_language);
        btnMove2 = findViewById(R.id.stop_service);
        btnMove3 = findViewById(R.id.about_us);

        animMove1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_more_features_buttons_animation1);
        btnMove.startAnimation(animMove1);

        animMove2 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_more_features_buttons_animation2);
        btnMove2.startAnimation(animMove2);

        animMove3 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_more_features_buttons_animation3);
        btnMove3.startAnimation(animMove3);

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void changeLanguage(View view) {
        finish();
        Intent intent = new Intent(this, ChangeLanguageActivity.class);
        startActivity(intent);
    }
    public void viewAboutUs(View view){
        finish();
        Intent intent = new Intent(this, AboutUsActivity.class);
        startActivity(intent);
    }

    public void stopService(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
                String[] permissions = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions, REQUEST_READ_PHONE_STATE);
            }
        }

        // To Get System TELEPHONY service ref
        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();

        if (!networkOperatorName.equals(getString(R.string.activity_more_feature_check_default_sim_alert_network_operator))) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MoreFeaturesActivity.this);
            // Setting Dialog Title
            alertDialog.setTitle(getString(R.string.activity_more_feature_check_default_sim_alert_title));
            // Setting Dialog Message
            alertDialog.setMessage(getString(R.string.activity_more_feature_check_default_sim_alert_message_pre_part) + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
            // Setting Icon to Dialog
            alertDialog.setIcon(R.drawable.etisalat);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton(getString(R.string.activity_more_feature_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                    Intent intent = new Intent(MoreFeaturesActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                }
            });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getString(R.string.activity_more_feature_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    finish();
                    Intent intent = new Intent(MoreFeaturesActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            });
            // Showing Alert Message
            alertDialog.show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.activity_more_features_stop_service_alert_title));
            builder.setMessage(getString(R.string.activity_more_features_stop_service_alert_message));
            builder.setCancelable(false);
            builder.setPositiveButton(getString(R.string.activity_more_features_stop_service_alert_positive_button_txt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(Constant.destinationAddress, null, Constant.stopCommand, null, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.activity_more_features_stop_service_alert_sent_sms_toast),
                                Toast.LENGTH_LONG).show();
                        String number = getNetworkOperatorName();
                        mDataSource.deleteEntryFromLoginTable(number);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(MoreFeaturesActivity.this);
                        builder.setTitle(getString(R.string.more_features_activity_stop_service_received_sms_alert_title));
                        builder.setMessage(getString(R.string.more_features_activity_stop_service_received_sms_alert_message));
                        builder.setCancelable(false);
                        // Initialize a new foreground color span instance
                        String pssitiveButtonText = getString(R.string.more_features_activity_stop_service_received_sms_positive_button_txt);
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.green(497320));
                        // Initialize a new spannable string builder instance
                        SpannableStringBuilder ssBuilder = new SpannableStringBuilder(pssitiveButtonText);

                        // Apply the text color span
                        ssBuilder.setSpan(
                                foregroundColorSpan,
                                0,
                                pssitiveButtonText.length(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        );

                        builder.setPositiveButton(ssBuilder, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                Intent intent = new Intent(MoreFeaturesActivity.this,HomeActivity.class);
                                startActivity(intent);

                            }

                        });

                        builder.show();

                    } catch (Exception e)

                    {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.activity_more_features_stop_service_alert_sms_failed_toast),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }

            });

            builder.setNegativeButton(getString(R.string.activity_more_features_stop_service_alert_negative_button_txt), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), getString(R.string.activity_more_features_stop_service_alert_negative_button_toast), Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();
        }
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


