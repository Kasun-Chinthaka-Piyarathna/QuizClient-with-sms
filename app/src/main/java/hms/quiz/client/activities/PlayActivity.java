package hms.quiz.client.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hms.quiz.client.R;
import hms.quiz.client.db.QuizDataSource;
import hms.quiz.client.entities.Item;

public class PlayActivity extends Activity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ListView simpleList;
    private ArrayList<Item> itemList = new ArrayList<>();
    private String answerchar;
    private QuizDataSource mDataSource;
    private static final String INBOX_URI = "content://sms/inbox";
    private static PlayActivity activity;
    private MyAdapter myAdapter;
    private static String sqliteInsertPatternKey="check_language";
    private static String sqliteInsertPatternValue1="S";
    private static String sqliteInsertPatternValue2="E";
    private static String sqliteInsertPatternValue3="T";

    public static PlayActivity instance() {
        return activity;
    }

    private static String radioButtonConfirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

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

        mDataSource = new QuizDataSource(this);
        mDataSource.open();
        simpleList = findViewById(R.id.simpleListView);
        myAdapter = new MyAdapter(this, R.layout.activity_play_received_sms_viewer, itemList);
        simpleList.setAdapter(myAdapter);
        simpleList.setOnItemClickListener(MyItemClickListener);
        readSMS();
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity = this;
    }

    public void readSMS() {
        ContentResolver contentResolver = getContentResolver();
        Cursor smsInboxCursor = contentResolver.query(Uri.parse(INBOX_URI), null, null, null, null);
        int senderIndex = smsInboxCursor.getColumnIndex(getString(R.string.play_activity_read_sms_address));
        int messageIndex = smsInboxCursor.getColumnIndex(getString(R.string.play_activity_read_sms_body));
        if (messageIndex < 0 || !smsInboxCursor.moveToFirst()) return;
        //adapter.clear();
        myAdapter.clear();
//        do {
        String sender = smsInboxCursor.getString(senderIndex);
        System.out.println(sender);
        String message = smsInboxCursor.getString(messageIndex);
        System.out.println(message);
        if (sender.equals(getString(R.string.play_activity_read_sms_mobile_partner_uniq_code1))) {
            Pattern p = Pattern.compile(getString(R.string.play_activity_read_sms_question_pattern));   // the pattern to search for
            Matcher m = p.matcher(message);
            // now try to find at least one match
            if (m.find()) {
                System.out.println(getString(R.string.play_activity_read_sms_match_found));
                String q = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_question_open_pattern), getString(R.string.play_activity_read_sms_question_close_pattern));
                String a1 = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_answer1_open_pattern), getString(R.string.play_activity_read_sms_answer1_close_pattern));
                String a2 = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_answer2_open_pattern), getString(R.string.play_activity_read_sms_answer2_close_pattern));
                String a3 = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_answer3_open_pattern), getString(R.string.play_activity_read_sms_answer3_close_pattern));

                if (!(a3 == null)) {

                    // String content = StringUtils.substringBetween(stringToSearch, "A:", "B:");
                    System.out.println(q);
                    System.out.println(a1);
                    System.out.println(a2);
                    System.out.println(a3);

                    mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                    mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue2);
                    myAdapter.add(new Item(q, R.drawable.hsenid_icon2, a1, a2, a3));
                } else {
                    String a4 = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_answer3_open_pattern), getString(R.string.play_activity_read_sms_answer3_close_pattern_sinhala));
                    if (!(a4 == null)) {
                        System.out.println(q);
                        System.out.println(a1);
                        System.out.println(a2);
                        System.out.println(a4);
                        mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                        mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue1);
                        myAdapter.add(new Item(q, R.drawable.hsenid_icon2, a1, a2, a4));
                    } else {
                        String a5 = StringUtils.substringBetween(message, getString(R.string.play_activity_read_sms_answer3_open_pattern), getString(R.string.play_activity_read_sms_answer3_close_pattern_tamil));
                        System.out.println(q);
                        System.out.println(a1);
                        System.out.println(a2);
                        System.out.println(a5);
                        mDataSource.deleteEntryFromLanguageTable(sqliteInsertPatternKey);
                        mDataSource.insertEntryToLanguageTable(sqliteInsertPatternKey, sqliteInsertPatternValue3);
                        myAdapter.add(new Item(q, R.drawable.hsenid_icon2, a1, a2, a5));
                    }
                }

            } else {
                System.out.println(getString(R.string.play_activity_read_sms_match_not_found));
                myAdapter.add(new Item(message, R.drawable.hsenid_icon2, "", "", ""));
            }
        }
//        }
//        while (smsInboxCursor.moveToNext());
    }

    public void updateList(final Item newSms) {
        myAdapter.clear();
        myAdapter.insert(newSms, 0);
        myAdapter.notifyDataSetChanged();
    }

    public void sendsms(View view) {

        String status = mDataSource.getSingleEntryFromUserChoiceTable(getString(R.string.play_activity_send_sms_get_data_base_check_skipped_preference));
        if (status.equals(getString(R.string.play_activity_send_sms_skipped_status))) {
            RadioGroup radioQuestions = findViewById(R.id.radioQuestions);
            // btnDisplay = (Button) findViewById(R.id.btnDisplay);
            int selectedId = radioQuestions.getCheckedRadioButtonId();
            // find the radiobutton by returned id
            RadioButton radioAnswer = findViewById(selectedId);
            View radioButton = radioQuestions.findViewById(selectedId);
            int idx = radioQuestions.indexOfChild(radioButton);
            if (Integer.toString(idx).equals(getString(R.string.play_activity_send_sms_radio_button_first_index))) {
                answerchar = getString(R.string.play_activity_send_sms_answer_a);
            } else if (Integer.toString(idx).equals(getString(R.string.play_activity_send_sms_radio_button_second_index))) {
                answerchar = getString(R.string.play_activity_send_sms_answer_b);
            } else {
                answerchar = getString(R.string.play_activity_send_sms_answer_c);
            }
            Toast.makeText(PlayActivity.this,
                    answerchar, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            //Get the SmsManager instance and call the sendTextMessage method to send message
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(getString(R.string.play_activity_send_sms_mobile_partner_uniq_code1), null, answerchar, pi, null);
            Toast.makeText(getApplicationContext(), getString(R.string.play_activity_send_sms_successfully),
                    Toast.LENGTH_LONG).show();
        } else {


            final CharSequence[] items = {" Remove this confirmation box next time "};
            // arraylist to keep the selected items
            final ArrayList seletedItems = new ArrayList();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.play_activity_send_answer_alert_title))
                    .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                // If the user checked the item, add it to the selected items
                                radioButtonConfirmation = "confirmed";
                                seletedItems.add(indexSelected);

                            } else if (seletedItems.contains(indexSelected)) {
                                // Else, if the item is already in the array, remove it
                                radioButtonConfirmation = "";
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }).setPositiveButton(getString(R.string.play_activity_send_answer_alert_positive_button_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on OK
                            //  You can write the code  to save the selected item here
                            if (radioButtonConfirmation.equals("confirmed")) {
                                mDataSource.insertEntryToUserChoiceTable(getString(R.string.play_activity_send_sms_skipped_status), getString(R.string.play_activity_send_sms_get_data_base_check_skipped_preference));
                            }
                            RadioGroup radioQuestions = findViewById(R.id.radioQuestions);
                            // btnDisplay = (Button) findViewById(R.id.btnDisplay);
                            int selectedId = radioQuestions.getCheckedRadioButtonId();
                            // find the radiobutton by returned id
                            RadioButton radioAnswer = findViewById(selectedId);
                            View radioButton = radioQuestions.findViewById(selectedId);
                            int idx = radioQuestions.indexOfChild(radioButton);
                            if (Integer.toString(idx).equals(getString(R.string.play_activity_send_sms_radio_button_first_index))) {
                                answerchar = getString(R.string.play_activity_send_sms_answer_a);
                            } else if (Integer.toString(idx).equals(getString(R.string.play_activity_send_sms_radio_button_second_index))) {
                                answerchar = getString(R.string.play_activity_send_sms_answer_b);
                            } else {
                                answerchar = getString(R.string.play_activity_send_sms_answer_c);
                            }
                            Toast.makeText(PlayActivity.this,
                                    answerchar, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), PlayActivity.class);
                            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                            //Get the SmsManager instance and call the sendTextMessage method to send message
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(getString(R.string.play_activity_send_sms_mobile_partner_uniq_code1), null, answerchar, pi, null);
                            Toast.makeText(getApplicationContext(), getString(R.string.play_activity_send_sms_successfully),
                                    Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton(getString(R.string.play_activity_send_answer_alert_negative_button_txt), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            //  Your code when user clicked on Cancel
                            radioButtonConfirmation = "";
                            Toast.makeText(getApplicationContext(), getString(R.string.activity_change_language_send_choice_alert_negative_button_toast), Toast.LENGTH_SHORT).show();

                        }
                    }).create();
            radioButtonConfirmation = "";
            dialog.show();

        }
    }

    private OnItemClickListener MyItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            try {
                // Toast.makeText(getApplicationContext(), myAdapter.getItem(pos).getAns1(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}

class MyAdapter extends ArrayAdapter<Item> {

    ArrayList<Item> smsRecievedList = new ArrayList<>();

    MyAdapter(Context context, int textViewResourceId, ArrayList<Item> objects) {
        super(context, textViewResourceId, objects);
        smsRecievedList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.activity_play_received_sms_viewer, null);
        TextView textView = v.findViewById(R.id.textView);
        RadioGroup radioGroup = v.findViewById(R.id.radioQuestions);
        Button sendsmsbutton = v.findViewById(R.id.sendsms);
        //  TextView status_textview = v.findViewById(R.id.statusTextview);
        if (smsRecievedList.get(position).getAns1().equals("") || smsRecievedList.get(position).getAns2().equals("") || smsRecievedList.get(position).getAns3().equals("")) {
            radioGroup.setVisibility(View.GONE);
            sendsmsbutton.setVisibility(View.GONE);
            if (position == 0) {
                v.setBackgroundResource(R.color.color_for_sms_responses);
                v.animate();
            }

            LinearLayout linearLayout = v.findViewById(R.id.questionWithNumber);
            linearLayout.setVisibility(View.GONE);
            TextView textViewResponse = v.findViewById(R.id.textViewResponse);
            textViewResponse.setVisibility(View.VISIBLE);

            textViewResponse.setText(smsRecievedList.get(position).getQuestion());
            textViewResponse.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        } else {
            radioGroup.setVisibility(View.VISIBLE);
            sendsmsbutton.setVisibility(View.VISIBLE);
            ((RadioButton) radioGroup.getChildAt(0)).setText(smsRecievedList.get(position).getAns1());
            ((RadioButton) radioGroup.getChildAt(1)).setText(smsRecievedList.get(position).getAns2());
            //((RadioButton) radioGroup.getChildAt(1)).setGravity(View.TEXT_ALIGNMENT_CENTER);
            ((RadioButton) radioGroup.getChildAt(2)).setText(smsRecievedList.get(position).getAns3());
            //((RadioButton) radioGroup.getChildAt(2)).setGravity(View.TEXT_ALIGNMENT_CENTER);

            if (position == 0) {
                v.animate();
            }

            LinearLayout linearLayout = v.findViewById(R.id.questionWithNumber);
            linearLayout.setVisibility(View.VISIBLE);
            TextView textViewResponse = v.findViewById(R.id.textViewResponse);
            textViewResponse.setVisibility(View.GONE);
            String questionWithNumber = smsRecievedList.get(position).getQuestion();
            String questionNumber = StringUtils.substringBetween(questionWithNumber, "", ".");
            String question = StringUtils.substringBetween(questionWithNumber, ".", "?");
            TextView textView1 = v.findViewById(R.id.question_number);
            textView1.setText(questionNumber);
            textView1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            textView.setText(question + "?");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);

        }

        return v;
    }

}


