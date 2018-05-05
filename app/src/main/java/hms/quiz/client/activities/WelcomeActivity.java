package hms.quiz.client.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import hms.quiz.client.Constant;
import hms.quiz.client.PrefManager;
import hms.quiz.client.R;
import hms.quiz.client.SMSReceiver;
import hms.quiz.client.db.QuizDataSource;
import hms.quiz.client.interfaces.OnRegSmsReceivedListener;

public class WelcomeActivity extends AppCompatActivity implements OnRegSmsReceivedListener {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;
    private Context context;
    private Activity activity;
    private RelativeLayout relativeLayout;
    private QuizDataSource mDataSource;
    private static final int REQUEST_READ_PHONE_STATE = 1;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        Constant.listenerChanger = "wizard";
        SMSReceiver.setOnRegSmsReceivedListener(this);
        // Get the activity
        activity = WelcomeActivity.this;
        relativeLayout = findViewById(R.id.rl);
        // Checking for first time launch - before calling setContentView()
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome);
        viewPager = findViewById(R.id.view_pager);
        dotsLayout = findViewById(R.id.layoutDots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);
        // layouts of all welcome sliders
        // add few more layouts if you want
        mDataSource = new QuizDataSource(WelcomeActivity.this);
        mDataSource.open();
        String number = getNetworkOperatorName();
        String status = mDataSource.getSingleEntryFromLoginTable(number);

        if (status.equals(getString(R.string.homeactivity_register_response))) {
            layouts = new int[]{
                    R.layout.activity_welcome_slider1,
                    R.layout.activity_welcome_slider2,
                    R.layout.activity_welcome_slider3,
                    R.layout.activity_welcome_slider4
            };
        } else {
            layouts = new int[]{
                    R.layout.activity_welcome_slider1,
                    R.layout.activity_welcome_slider2,
                    R.layout.activity_welcome_slider3,
                    R.layout.activity_welcome_slider4,
                    R.layout.activity_welcome_slider5
            };
        }
        // adding bottom dots
        addBottomDots(0);
        // making notification bar transparent
        changeStatusBarColor();
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_DENIED) {
                        Log.d("permission", "permission denied to SEND_SMS - requesting it");
                        String[] permissions = {Manifest.permission.SEND_SMS};
                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    }
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.stopCommand, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.stopCommand, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                launchHomeScreen();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // activity_more_features_buttons_animation2 to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    launchHomeScreen();
                }
            }
        });
    }


    public void registerNow(View view) {

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

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.welcome_activity_register_alert_title));
        builder.setMessage(getString(R.string.welcome_activity_register_alert_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.welcome_activity_register_alert_positive_button_txt), new DialogInterface.OnClickListener() {
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
                            progressDialog = new ProgressDialog(WelcomeActivity.this);
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
                        progressDialog = new ProgressDialog(WelcomeActivity.this);
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

        builder.setNegativeButton(getString(R.string.activity_more_features_stop_service_alert_negative_button_txt), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.welcome_activity_register_service_alert_negative_button_toast), Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    @Override
    public void onRegSmsReceived(String listenerRegisteredStatus) {

        progressDialog.dismiss();
        //receiver2 = new SMSReceiver();
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
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(WelcomeActivity.this);
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
                    Intent intent = new Intent(WelcomeActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                }
            });
            // Setting Negative "NO" Button
            alertDialog.setNegativeButton(getString(R.string.homeactivity_check_default_sim_alert_message_set_possitive_button_txt), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke NO event
                    finish();
                    Intent intent = new Intent(WelcomeActivity.this, SplashScreenActivity.class);
                    startActivity(intent);
                }
            });
            // Showing Alert Message
            alertDialog.show();
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

    private void addBottomDots(int currentPage) {

        dots = new TextView[layouts.length];
        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, HomeActivity.class));
        finish();
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);

            } else if (position == 1) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_DENIED) {
                        Log.d("permission", "permission denied to SEND_SMS - requesting it");
                        String[] permissions = {Manifest.permission.SEND_SMS};
                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                    }
                }

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.SEND_SMS)
                            == PackageManager.PERMISSION_GRANTED) {
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.stopCommand, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(String.valueOf(Constant.destinationAddress), null, Constant.stopCommand, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.next));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
        }

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void skipNow(View view) {
        finish();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
