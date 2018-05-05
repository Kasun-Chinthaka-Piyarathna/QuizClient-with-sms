package hms.quiz.client.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import hms.quiz.client.Constant;
import hms.quiz.client.R;
import hms.quiz.client.SMSReceiver;
import hms.quiz.client.db.QuizDataSource;
import hms.quiz.client.interfaces.OnRegSmsReceivedListener;

public class HomeActivity extends AppCompatActivity implements OnRegSmsReceivedListener {

    private SMSReceiver receiver2;
    private QuizDataSource mDataSource;
    private ProgressBar home_spinner;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Constant.listenerChanger = "home";
        TextView txt = findViewById(R.id.sinhala_font_in_content_home);
        Typeface font = Typeface.createFromAsset(getAssets(), "Amal.TTF");
        txt.setTypeface(font);
        Button button = findViewById(R.id.play_now);
        Animation blink_animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_home_play_now_blink_animation);
        button.startAnimation(blink_animation);
        SMSReceiver.setOnRegSmsReceivedListener(this);
        mDataSource = new QuizDataSource(this);
        mDataSource.open();
        String number = getNetworkOperatorName();
        String status = mDataSource.getSingleEntryFromLoginTable(number);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }

        if (status.equals(getString(R.string.homeactivity_register_response))) {
        } else {
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to READ_SMS - requesting it");
                String[] permissions2 = {Manifest.permission.READ_SMS};
                requestPermissions(permissions2, PERMISSION_REQUEST_CODE);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to RECEIVE_SMS - requesting it");
                String[] permissions3 = {Manifest.permission.RECEIVE_SMS};
                requestPermissions(permissions3, PERMISSION_REQUEST_CODE);
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to READ_PHONE_STATE - requesting it");
                String[] permissions4 = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions4, PERMISSION_REQUEST_CODE);
            }
        }

        checkDefaultSim();
    }

    public void checkDefaultSim() {

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
        if (!networkOperatorName.equals(getString(R.string.homeactivity_check_default_sim_alert_network_operator))) {

            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + " " + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    public void play(View view) {

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
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();

        if (!networkOperatorName.equals(Constant.mobileOperator)) {
            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        } else {

            String number = getNetworkOperatorName();
            String status = mDataSource.getSingleEntryFromLoginTable(number);
            if (status.equals(getString(R.string.homeactivity_register_response))) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.playCommand, null, null);
                    Toast.makeText(getApplicationContext(), getString(R.string.homeactivity_play_button_looking_ques_toast),
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.homeactivity_play_button_looking_ques_sms_failed_toast),
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                finish();
                Intent intent = new Intent(this, PlayActivity.class);
                startActivity(intent);
            } else {
                regsiterInHomePage();

            }
        }
    }

    public void noSimCard() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_no_sim_title));
        // Setting Icon to Dialog
        alertDialog.setIcon(R.drawable.etisalat);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    public void regsiterInHomePage() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.homeactivity_regsiter_quiz__alert_title));
        builder.setMessage(getString(R.string.homeactivity_regsiter_quiz__alert_message));
        builder.setCancelable(false);
        // Initialize a new foreground color span instance
        String pssitiveButtonText = getString(R.string.homeactivity_regsiter_quiz_button_text);
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
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.playCommand, null, null);
                            Toast.makeText(getApplicationContext(), getString(R.string.welcome_activity_register_alert_sms_sent_toast),
                                    Toast.LENGTH_LONG).show();
                            progressDialog = new ProgressDialog(HomeActivity.this);
                            progressDialog.setMessage(getString(R.string.welcome_activity_register_progress_dialog_message)); // Setting Message
                            progressDialog.setTitle(getString(R.string.welcome_activity_register_progress_dialog_title)); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.welcome_activity_register_alert_sms_failed_toast),
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.playCommand, null, null);
                        Toast.makeText(getApplicationContext(), getString(R.string.welcome_activity_register_alert_sms_sent_toast),
                                Toast.LENGTH_LONG).show();
                        progressDialog = new ProgressDialog(HomeActivity.this);
                        progressDialog.setMessage(getString(R.string.welcome_activity_register_progress_dialog_message)); // Setting Message
                        progressDialog.setTitle(getString(R.string.welcome_activity_register_progress_dialog_title)); // Setting Title
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                        progressDialog.show(); // Display Progress Dialog
                        progressDialog.setCancelable(false);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.welcome_activity_register_alert_sms_failed_toast),
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        builder.setNegativeButton(getString(R.string.homeactivity_regsiter_quiz__alert_negative_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.homeactivity_regsiter_quiz__alert_negative_button_toast), Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void checkScore(View view) {

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
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();
        if (!networkOperatorName.equals(Constant.mobileOperator)) {
            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + networkOperatorName + ". To use this application, you have to change Etisalat as the default sim. ");
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });
                // Showing Alert Message
                alertDialog.show();
            }
        } else {

            String number = getNetworkOperatorName();
            String status = mDataSource.getSingleEntryFromLoginTable(number);
            if (status.equals(getString(R.string.homeactivity_register_response))) {
                finish();
                Intent intent = new Intent(this, ScoreActivity.class);
                startActivity(intent);
            } else {
                regsiterInHomePage();
            }
        }
    }

    public void inviteFriends(View view) {

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
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();
        if (!networkOperatorName.equals(Constant.mobileOperator)) {
            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        } else {

            String number = getNetworkOperatorName();
            String status = mDataSource.getSingleEntryFromLoginTable(number);
            if (status.equals(getString(R.string.homeactivity_register_response))) {
                finish();
                Intent intent = new Intent(this, InviteFriendsActivity.class);
                startActivity(intent);
            } else {
                regsiterInHomePage();
            }
        }
    }

    @Override
    public void onRegSmsReceived(String listenerRegisteredStatus) {

        progressDialog.dismiss();
        receiver2 = new SMSReceiver();
        final String number = getNetworkOperatorName();

        if (listenerRegisteredStatus.equals(getString(R.string.welcome_activity_register_response))) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.welcome_activity_send_invitation_alert_title));
            builder.setMessage(getString(R.string.welcome_activity_send_invitation_alert_message));
            builder.setCancelable(false);
            // Initialize a new foreground color span instance
            String pssitiveButtonText = getString(R.string.welcome_activity_send_invitation_alert_positive_button_txt);
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
                    mDataSource.insertEntryToLoginTable(number, String.valueOf(getString(R.string.welcome_activity_register_response)));
                    finish();
                    Intent intent = new Intent(getBaseContext(), PlayActivity.class);
                    startActivity(intent);
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

    public void getHelpForQuiz(View view) {

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
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();

        if (!networkOperatorName.equals(Constant.mobileOperator)) {
            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        } else {

            String number = getNetworkOperatorName();
            String status = mDataSource.getSingleEntryFromLoginTable(number);
            if (status.equals(getString(R.string.homeactivity_register_response))) {

                finish();
                Intent intent = new Intent(this, HelpActivity.class);
                startActivity(intent);

            } else {
                regsiterInHomePage();
            }
        }

    }

    public void viewMoreFeatures(View view) {

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
        // Get carrier name (Network Operator Name)
        String networkOperatorName = tManager.getNetworkOperatorName();

        if (!networkOperatorName.equals(Constant.mobileOperator)) {
            if (networkOperatorName == "") {
                noSimCard();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
                // Setting Dialog Title
                alertDialog.setTitle(getString(R.string.homeactivity_check_default_sim_alert_title));
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.homeactivity_check_default_sim_alert_message_pre_part) + networkOperatorName + getString(R.string.homeactivity_check_default_sim_alert_message_post_part));
                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.etisalat);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_negative_button_txt), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        finish();
                        Intent intent = new Intent(HomeActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();
            }
        } else {

            String number = getNetworkOperatorName();
            String status = mDataSource.getSingleEntryFromLoginTable(number);
            if (status.equals(getString(R.string.homeactivity_register_response))) {
                finish();
                Intent intent = new Intent(this, MoreFeaturesActivity.class);
                startActivity(intent);
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.homeactivity_regsiter_quiz__alert_title));
                builder.setMessage(getString(R.string.homeactivity_regsiter_quiz__alert_message));
                builder.setCancelable(false);
                // Initialize a new foreground color span instance
                String pssitiveButtonText = getString(R.string.homeactivity_regsiter_quiz_button_text);
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
                        Intent intent = new Intent(HomeActivity.this, SplashScreenActivity.class);
                        startActivity(intent);

                    }
                });

                builder.setNegativeButton(getString(R.string.homeactivity_regsiter_quiz__alert_negative_button_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.homeactivity_regsiter_quiz__alert_negative_button_toast), Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            }
        }
    }

}
