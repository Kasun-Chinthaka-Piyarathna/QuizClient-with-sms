package hms.quiz.client.activities;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import hms.quiz.client.R;
import hms.quiz.client.db.QuizDataSource;

public class ChangeLanguageActivity extends AppCompatActivity {

    private String LanguageChar;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private QuizDataSource mDataSource;
    private static ChangeLanguageActivity activity;
    private static String sqliteInsertPatternKey="check_language";
    private static String sqliteInsertPatternValue1="S";
    private static String sqliteInsertPatternValue2="E";
    private static String sqliteInsertPatternValue3="T";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);
        mDataSource = new QuizDataSource(this);
        mDataSource.open();

        String status = mDataSource.getSingleEntryFroLanguageTable(sqliteInsertPatternKey);
        if (status.equals(sqliteInsertPatternValue1)) {
            RadioButton radioButton = findViewById(R.id.languageToSinhala);
            radioButton.setTextSize(30);
            radioButton.setChecked(true);
        } else if (status.equals(sqliteInsertPatternValue2)) {
            RadioButton radioButton = findViewById(R.id.languageToEnglish);
            radioButton.setTextSize(30);
            radioButton.setChecked(true);
        } else if (status.equals(sqliteInsertPatternValue3)) {
            RadioButton radioButton = findViewById(R.id.languageToTamil);
            radioButton.setTextSize(30);
            radioButton.setChecked(true);
        } else {
            RadioButton radioButton = findViewById(R.id.languageToEnglish);
            radioButton.setTextSize(30);
            radioButton.setChecked(true);
        }

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
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions2 = {Manifest.permission.READ_SMS};
                requestPermissions(permissions2, PERMISSION_REQUEST_CODE);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions3 = {Manifest.permission.RECEIVE_SMS};
                requestPermissions(permissions3, PERMISSION_REQUEST_CODE);
            }
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions4 = {Manifest.permission.READ_PHONE_STATE};
                requestPermissions(permissions4, PERMISSION_REQUEST_CODE);
            }
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this;
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, MoreFeaturesActivity.class);
        startActivity(intent);
    }

    public void changeLanguageNow(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.activity_change_language_send_choice_alert_title));
        builder.setMessage(getString(R.string.activity_change_language_send_choice_alert_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.activity_change_language_send_choice_alert_positive_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RadioGroup radioQuestions = findViewById(R.id.groupLanguages);
                // btnDisplay = (Button) findViewById(R.id.btnDisplay);
                int selectedId = radioQuestions.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                View radioButton = radioQuestions.findViewById(selectedId);
                int idx = radioQuestions.indexOfChild(radioButton);
                if (Integer.toString(idx).equals(getString(R.string.change_language_activity_change_language_radio_button_first_index))) {
                    LanguageChar = getString(R.string.change_language_activity_change_language_sinhala);
                    mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                    mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue1);
                } else if (Integer.toString(idx).equals(getString(R.string.change_language_activity_change_language_radio_button_second_index))) {
                    LanguageChar = getString(R.string.change_language_activity_change_language_english);
                    mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                    mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue2);
                } else {
                    LanguageChar = getString(R.string.change_language_activity_change_language_tamil);
                    mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                    mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue3);
                }
                Toast.makeText(ChangeLanguageActivity.this,
                        LanguageChar, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), ChangeLanguageActivity.class);
                PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                //Get the SmsManager instance and call the sendTextMessage method to send message
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(getString(R.string.change_language_activity_change_language_mobile_partner_uniq_code1), null, LanguageChar, pi, null);
                Toast.makeText(getApplicationContext(), getString(R.string.change_language_activity_change_language_successfully),
                        Toast.LENGTH_LONG).show();
            }

        });

        builder.setNegativeButton(getString(R.string.activity_change_language_send_choice_alert_negative_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.activity_change_language_send_choice_alert_negative_button_toast), Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }
}
