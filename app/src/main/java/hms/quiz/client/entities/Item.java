package hms.quiz.client.entities;

public class Item {

    private int smsIndicatedLogo;
    private String question;
    private String ans1;
    private String ans2;
    private String ans3;

    public Item(String question, int smsIndicatedLogo, String ans1, String ans2, String ans3) {
        this.smsIndicatedLogo = smsIndicatedLogo;
        this.question = question;
        this.ans1 = ans1;
        this.ans2 = ans2;
        this.ans3 = ans3;
    }

    public String getQuestion() {
        return question;
    }

    public String getAns1() {
        return ans1;
    }

    public String getAns2() {
        return ans2;
    }

    public String getAns3() {
        return ans3;
    }

}