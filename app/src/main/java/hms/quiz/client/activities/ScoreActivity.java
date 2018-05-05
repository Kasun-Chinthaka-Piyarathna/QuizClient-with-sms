package hms.quiz.client.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import hms.quiz.client.Constant;
import hms.quiz.client.R;
import hms.quiz.client.SMSReceiver;
import hms.quiz.client.interfaces.OnSmsReceivedListener;

public class ScoreActivity extends AppCompatActivity implements OnSmsReceivedListener, OnChartValueSelectedListener {

    private SMSReceiver receiver;
    private TextView total_score;
    private TextView answered_ques;
    private TextView wrong_ans;
    private static final int PERMISSION_REQUEST_CODE = 1;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }

        SMSReceiver.setOnSmsReceivedListener(this);
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

        total_score = findViewById(R.id.total_score);
        answered_ques = findViewById(R.id.answered_ques);
        wrong_ans = findViewById(R.id.wrong_ans);

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(Constant.destinationAddress, null, Constant.pointCommand, null, null);
            Toast.makeText(getApplicationContext(), getString(R.string.sms_sent_command),
                    Toast.LENGTH_LONG).show();
            progressDialog = new ProgressDialog(ScoreActivity.this);
            progressDialog.setMessage(getString(R.string.progress_dialog_alert_message_loading_score)); // Setting Message
            progressDialog.setTitle(getString(R.string.progress_dialog_alert_title)); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);
        } catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.sms_failed_command),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSmsReceived(String s1, String s2, String s3) {
        receiver = new SMSReceiver();
        TextView wishing_txt = findViewById(R.id.wishing_txt);
        wishing_txt.setVisibility(View.VISIBLE);
        Animation wishing_txt_animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.activity_score_congratulations_blick_animation);
        wishing_txt.startAnimation(wishing_txt_animation);
        progressDialog.dismiss();
        total_score.setVisibility(View.VISIBLE);
        answered_ques.setVisibility(View.VISIBLE);
        wrong_ans.setVisibility(View.VISIBLE);
        total_score.setText(getString(R.string.score_activity_total_score_text) + s1);
        int cAns = Integer.parseInt(s2);
        int wAns = Integer.parseInt(s3);
        answered_ques.setText(getString(R.string.score_activity_ans_questions_text) + " " + Integer.toString(cAns + wAns));
        double probability = cAns * 100 / (cAns + wAns);
        wrong_ans.setText(getString(R.string.score_activity_probability_for_getting_correct_answers_text) + " " + probability + "%");
        PieChart pieChart = findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        // IMPORTANT: In activity_play_radio_button_selected_state PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        ArrayList<Entry> yvalues = new ArrayList<Entry>();
        yvalues.add(new Entry(Integer.parseInt(s2), 0));
        yvalues.add(new Entry(Integer.parseInt(s3), 1));
        PieDataSet dataSet = new PieDataSet(yvalues, getString(R.string.score_activity_pie_data_set_label));
        ArrayList<String> xVals = new ArrayList<String>();
        xVals.add(getString(R.string.score_activity_pie_data_setx_vals1));
        xVals.add(getString(R.string.score_activity_pie_data_setx_vals2));
        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        pieChart.setData(data);
        pieChart.setDescription(getString(R.string.score_activity_pie_data_set_description));
        pieChart.getLegend().setEnabled(false);
        // pieChart.setDrawHoleEnabled(true);
        pieChart.setTransparentCircleRadius(15f);
        pieChart.setHoleRadius(15f);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        data.setValueTextSize(13f);
        data.setValueTextColor(Color.DKGRAY);
        pieChart.setOnChartValueSelectedListener(this);
        pieChart.animateXY(500, 500);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
                        + ", DataSet index: " + dataSetIndex);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    public void playNow(View view) {

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
    }

}
