package com.example.quickcash.model;

public class SecurityModel {
    private String question1;
    private String question2;
    private String question3;

    public SecurityModel() {
    }

    public SecurityModel(String question1, String question2, String question3) {
        this.question1 = question1;
        this.question2 = question2;
        this.question3 = question3;
    }

    public String getQuestion1() {
        return question1;
    }

    public String getQuestion2() {
        return question2;
    }

    public String getQuestion3() {
        return question3;
    }

    public boolean isQuestions(String q1, String q2, String q3){
        return (this.question1.equals(q1) &&
                this.question2.equals(q2) &&
                this.question3.equals(q3));
    }

    public boolean isEqual(SecurityModel securityModel) {
        return securityModel.getQuestion1().equals(this.question1) &&
                securityModel.getQuestion2().equals(this.question2) &&
                securityModel.getQuestion3().equals(this.question3);
    }
}
