package hms.quiz.client.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import hms.quiz.client.Constant;
import hms.quiz.client.R;
import hms.quiz.client.SMSReceiver;
import hms.quiz.client.interfaces.OnInviteSmsReceivedListener;

public class InviteFriendsActivity extends AppCompatActivity implements OnInviteSmsReceivedListener {

    private LinearLayout linearLayout1;
    private SMSReceiver receiver3;
    private ProgressDialog progressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        SMSReceiver.setOnInviteSmsReceivedListener(this);
        ImageView imageView = findViewById(R.id.heading_image_view);
        Animation blink_animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_invite_invite_eveeryone_blink_animation);
        imageView.startAnimation(blink_animation);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
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
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    public void inviteFriends(View view) {

        EditText friendsnumber = findViewById(R.id.friendsnumber);
        final String value_friendsnumber = friendsnumber.getText().toString();
        linearLayout1 = findViewById(R.id.invitefriends);

        if (!value_friendsnumber.equals("")) {
            String keyword = Constant.etislatRecognition;
            int index = value_friendsnumber.indexOf(keyword);
            if (index == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.invite_friends_activity_send_invitation_alert_title));
                builder.setMessage(getString(R.string.invite_friends_activity_send_invitation_alert_message));
                builder.setCancelable(false);
                // Initialize a new foreground color span instance
                String pssitiveButtonText = getString(R.string.invite_friends_activity_send_invitation_alert_positive_button_txt);
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
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(Constant.destinationAddress, null, Constant.inviteCommand + " " + value_friendsnumber, null, null);
                            Toast.makeText(InviteFriendsActivity.this, getString(R.string.invite_friends_activity_invite_alert_sent_sms_toast), Toast.LENGTH_SHORT).show();
                            progressDialog = new ProgressDialog(InviteFriendsActivity.this);
                            progressDialog.setMessage(getString(R.string.progress_dialog_alert_message_sending_invitation)); // Setting Message
                            progressDialog.setTitle(getString(R.string.progress_dialog_alert_title)); // Setting Title
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                            progressDialog.show(); // Display Progress Dialog
                            progressDialog.setCancelable(false);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.invite_friends_activity_invite_alert_sent_sms_failed_toast),
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                });

                builder.setNegativeButton(getString(R.string.invite_friends_activity_send_invitation_alert_negative_button_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.invite_friends_activity_send_invitation_alert_negative_button_toast), Toast.LENGTH_SHORT).show();
                    }
                });

                builder.show();
            } else {

                final AlertDialog.Builder builder = new AlertDialog.Builder(InviteFriendsActivity.this);
                builder.setTitle(getString(R.string.invite_friends_activity_invalid_etisalat_number_alert_title));
                builder.setMessage(getString(R.string.invite_friends_activity_invalid_etisalat_number_alert_message));
                builder.setCancelable(false);
                // Initialize a new foreground color span instance
                String possitiveButtonText = getString(R.string.invite_friends_activity_invalid_etisalat_number_alert_positive_button_txt);
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.green(497320));
                // Initialize a new spannable string builder instance
                SpannableStringBuilder ssBuilder = new SpannableStringBuilder(possitiveButtonText);
                // Apply the text color span
                ssBuilder.setSpan(
                        foregroundColorSpan,
                        0,
                        possitiveButtonText.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                builder.setPositiveButton(ssBuilder, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //    Toast.makeText(InviteFriendsActivity.this, getString(R.string.invite_friends_activity_progress_dialog_sms_received_type_one), Toast.LENGTH_SHORT).show();
                    }

                });
                builder.show();
            }
        } else {
            friendsnumber.setError(getString(R.string.invite_friends_activity_empty_edit_text_toast));
        }
    }

    @Override
    public void onInviteSmsReceived(String s1) {
        if (s1.equals(getString(R.string.invite_friends_activity_sent_inviation))) {

            progressDialog.dismiss();
            receiver3 = new SMSReceiver();
            EditText friendsnumber = findViewById(R.id.friendsnumber);
            friendsnumber.setText("");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.invite_friends_activity_sent_invitation_alert_title));
            builder.setMessage(getString(R.string.invite_friends_activity_sent_invitation_alert_message));
            builder.setCancelable(false);
            // Initialize a new foreground color span instance
            String possitiveButtonText = getString(R.string.invite_friends_activity_send_invitation_alert_positive_button_txt);
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.green(497320));
            // Initialize a new spannable string builder instance
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(possitiveButtonText);

            // Apply the text color span
            ssBuilder.setSpan(
                    foregroundColorSpan,
                    0,
                    possitiveButtonText.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            builder.setPositiveButton(ssBuilder, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                //    Toast.makeText(InviteFriendsActivity.this, getString(R.string.invite_friends_activity_progress_dialog_sms_received_type_one), Toast.LENGTH_SHORT).show();

                }

            });

            builder.show();

        } else if (s1.equals(getString(R.string.invite_friends_activity_already_registered_one))) {
            progressDialog.dismiss();
            receiver3 = new SMSReceiver();
            EditText friendsnumber = findViewById(R.id.friendsnumber);
            friendsnumber.setText("");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.invite_friends_activity_sent_invitation_alert_title));
            builder.setMessage(getString(R.string.invite_friends_activity_sent_invitation_alert_message));
            builder.setCancelable(false);
            // Initialize a new foreground color span instance
            String pssitiveButtonText = getString(R.string.invite_friends_activity_send_invitation_alert_positive_button_txt);
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
                    Toast.makeText(InviteFriendsActivity.this, getString(R.string.invite_friends_activity_progress_dialog_sms_received_type_two), Toast.LENGTH_SHORT).show();
                }

            });

            builder.show();

        } else {
            progressDialog.dismiss();
            receiver3 = new SMSReceiver();
            EditText friendsnumber = findViewById(R.id.friendsnumber);
            friendsnumber.setText("");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.invite_friends_activity_sent_invitation_alert_title));
            builder.setMessage(getString(R.string.invite_friends_activity_sent_invitation_alert_message));
            builder.setCancelable(false);
            // Initialize a new foreground color span instance
            String pssitiveButtonText = getString(R.string.invite_friends_activity_send_invitation_alert_positive_button_txt);
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
                    Toast.makeText(InviteFriendsActivity.this, getString(R.string.invite_friends_activity_progress_dialog_sms_received_type_three), Toast.LENGTH_SHORT).show();

                }

            });
            builder.show();
        }
    }
}
