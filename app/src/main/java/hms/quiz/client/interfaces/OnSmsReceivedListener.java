package hms.quiz.client.interfaces;

public interface OnSmsReceivedListener {

    void onSmsReceived(String totalScore, String correctAns, String wrongAns);

}
