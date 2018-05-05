package hms.quiz.client;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import org.apache.commons.lang.StringUtils;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import hms.quiz.client.activities.HomeActivity;
import hms.quiz.client.activities.InviteFriendsActivity;
import hms.quiz.client.activities.PlayActivity;
import hms.quiz.client.activities.ScoreActivity;
import hms.quiz.client.activities.WelcomeActivity;
import hms.quiz.client.entities.Item;
import hms.quiz.client.interfaces.OnInviteSmsReceivedListener;
import hms.quiz.client.interfaces.OnRegSmsReceivedListener;
import hms.quiz.client.interfaces.OnSmsReceivedListener;

public class SMSReceiver extends BroadcastReceiver {

    private static OnSmsReceivedListener listener = null;
    private static OnRegSmsReceivedListener listener2 = null;
    private static OnInviteSmsReceivedListener listener3 = null;
    private static OnRegSmsReceivedListener listener4 = null;
    // SmsManager class is responsible for all SMS related actions
    PlayActivity inst;

    public static void setOnSmsReceivedListener(ScoreActivity onSmsReceivedListener) {
        listener = onSmsReceivedListener;
    }

    public static void setOnRegSmsReceivedListener(HomeActivity onRegSmsReceivedListener) {
        listener2 = onRegSmsReceivedListener;
    }

    public static void setOnRegSmsReceivedListener(WelcomeActivity onRegSmsReceivedListener) {
        listener4 = onRegSmsReceivedListener;
    }

    public static void setOnInviteSmsReceivedListener(InviteFriendsActivity onInviteSmsReceivedListener) {
        listener3 = onInviteSmsReceivedListener;
    }

    public void onReceive(Context context, Intent intent) {
        // Get the SMS message received
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                // A PDU is activity_play_radio_button_selected_state "protocol data unit". This is the industrial standard for SMS message
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    // This will create an SmsMessage object from the received pdu
                    SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    // Get sender phone number
                    String phoneNumber = sms.getDisplayOriginatingAddress();
                    String sender = phoneNumber;
                    String message = sms.getDisplayMessageBody();
                    Pattern p = Pattern.compile(Constant.patternToSearchQuestion);   // the pattern to search for
                    Matcher m = p.matcher(message);

                    // now try to find at least one match
                    if (m.find()) {

                        String q = StringUtils.substringBetween(message, Constant.questionOpenPattern, Constant.questionClosePattern);
                        String a1 = StringUtils.substringBetween(message, Constant.answer1OpenPattern, Constant.answer1ClosePattern);
                        String a2 = StringUtils.substringBetween(message, Constant.answer2OpenPattern, Constant.answer2ClosePattern);
                        String a3 = StringUtils.substringBetween(message, Constant.answer3OpenPattern, Constant.answer3ClosePattern);
                        inst = PlayActivity.instance();

                        if (!(a3 == null)) {
                            inst.updateList(new Item(q, R.drawable.hsenid_icon, a1, a2, a3));
                        } else {
                            String a4 = StringUtils.substringBetween(message, Constant.answer3OpenPattern, Constant.answer3ClosePatternSinhala);
                            if(!(a4==null)) {
                                inst.updateList(new Item(q, R.drawable.hsenid_icon, a1, a2, a4));
                            }else {
                                String a5 = StringUtils.substringBetween(message, Constant.answer3OpenPattern, Constant.answer3ClosePatternTamil);
                                inst.updateList(new Item(q, R.drawable.hsenid_icon, a1, a2, a5));
                            }
                        }

                    } else {

                        Pattern p2 = Pattern.compile(Constant.correctAnswerSinhalaPattern);   // the pattern to search for
                        Matcher m2 = p2.matcher(message);

                        Pattern p3 = Pattern.compile(Constant.correctAnswerEnglishPattern);   // the pattern to search for
                        Matcher m3 = p3.matcher(message);

                        Pattern p4 = Pattern.compile(Constant.correctAnswerTamilPattern);   // the pattern to search for
                        Matcher m4 = p4.matcher(message);

                        Pattern p5 = Pattern.compile(Constant.welcomeToGamePattern);   // the pattern to search for
                        Matcher m5 = p5.matcher(message);

                        Pattern p6 = Pattern.compile(Constant.inviteSinhalaPattern);   // the pattern to search for
                        Matcher m6 = p6.matcher(message);

                        Pattern p7 = Pattern.compile(Constant.inviteEnglishPattern);   // the pattern to search for
                        Matcher m7 = p7.matcher(message);

                        Pattern p8 = Pattern.compile(Constant.inviteTamilPattern);   // the pattern to search for
                        Matcher m8 = p8.matcher(message);

                        Pattern p9 = Pattern.compile(Constant.invitedFriendAlreadyRegisteredSinhalaPattern);   // the pattern to search for
                        Matcher m9 = p9.matcher(message);

                        Pattern p10 = Pattern.compile(Constant.invitedFriendAlreadyRegisteredEnglishPattern);   // the pattern to search for
                        Matcher m10 = p10.matcher(message);

                        Pattern p11 = Pattern.compile(Constant.invitedFriendAlreadyRegisteredTamilPattern);   // the pattern to search for
                        Matcher m11 = p11.matcher(message);

                        if (m2.find()) {
                            String m2_p0 = StringUtils.substringBetween(message, Constant.totalScoreSinhalaOpenPattern, Constant.totalScoreSinhalaClosePattern);
                            String m2_p1 = StringUtils.substringBetween(message, Constant.correctAnsSinhalaOpenPattern, Constant.correctAnsSinhalaClosePattern);
                            String m2_p2 = StringUtils.substringBetween(message, Constant.wrongAnsSinhalaOpenPattern, Constant.wrongAnsSinhalaClosePattern);

                            if (listener != null) {
                                listener.onSmsReceived(m2_p0, m2_p1, m2_p2);
                            }

                        } else if (m3.find()) {
                            String m3_p0 = StringUtils.substringBetween(message, Constant.totalScoreEnglishOpenPattern, Constant.totalScoreEnglishClosePattern);
                            String m3_p1 = StringUtils.substringBetween(message, Constant.correctAnsEnglishOpenPattern, Constant.correctAnsEnglishClosePattern);
                            String m3_p2 = StringUtils.substringBetween(message, Constant.wrongAnsEnglishOpenPattern, Constant.wrongAnsEnglishClosePattern);

                            Constant.totalPoint = m3_p0;
                            Constant.correctAns = m3_p1;
                            Constant.wrongAns = m3_p2;

                            if (listener != null) {
                                listener.onSmsReceived(m3_p0, m3_p1, m3_p2);
                            }

                        } else if (m4.find()) {
                            String m4_p0 = StringUtils.substringBetween(message, Constant.totalScoreTamilOpenPattern, Constant.totalScoreTamilClosePattern);
                            String m4_p1 = StringUtils.substringBetween(message, Constant.correctAnsTamilOpenPattern, Constant.correctAnsTamilClosePattern);
                            String m4_p2 = StringUtils.substringBetween(message, Constant.wrongAnsTamilOpenPattern, Constant.wrongAnsTamilClosePattern);

                            Constant.totalPoint = m4_p0;
                            Constant.correctAns = m4_p1;
                            Constant.wrongAns = m4_p2;

                            if (listener != null) {
                                listener.onSmsReceived(m4_p0, m4_p1, m4_p2);
                            }

                        } else if (m5.find()) {
                            if(Constant.listenerChanger.equals("home")) {
                                if (listener2 != null) {
                                    listener2.onRegSmsReceived(Constant.listenerRegisteredStatus);
                                }
                            }else {
                                if (listener4 != null) {
                                    listener4.onRegSmsReceived(Constant.listenerRegisteredStatus);
                                }
                            }
                        } else if (m6.find() || m7.find() || m8.find()) {
                            if (listener3 != null) {
                                listener3.onInviteSmsReceived(Constant.listenerSentInvitationStatus);
                            }
                        } else if (m9.find() || m10.find() || m11.find()) {
                            if (listener3 != null) {
                                listener3.onInviteSmsReceived(Constant.listenerAlreadyRegisteredStatus);
                            }
                        } else {
                            inst = PlayActivity.instance();
                            //inst.readSMS();
                            inst.updateList(new Item(message, R.drawable.hsenid_icon, "", "", ""));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}