package com.example.quickcash.model;

import java.util.HashMap;
import java.util.Map;

public class SecurityModel {
    private String Q1ans;
    private String Q2ans;
    private String Q3ans;
    public SecurityModel(String Q1ans, String Q2ans, String Q3ans){
        this.Q1ans = Q1ans;
        this.Q2ans = Q2ans;
        this.Q3ans = Q3ans;
    }

    public Map<String, String> get3Answers() {
        Map<String, String> answers = new HashMap<>();
        answers.put("Q1ans", Q1ans);
        answers.put("Q2ans", Q2ans);
        answers.put("Q3ans", Q3ans);
        return answers;
    }

    public void set3Answers(String q1ans,String q2ans,String q3ans) {
        this.Q1ans = q1ans;
        this.Q2ans = q2ans;
        this.Q3ans = q3ans;
    }

    public boolean is3AnsEquals(String q1ans, String q2ans, String q3ans) {
        return ( isQ1AnsEquals(q1ans) && isQ2AnsEquals(q2ans) && isQ3AnsEquals(q3ans) );
    }

    private boolean isQ1AnsEquals(String q1ans) {
        return this.Q1ans.equals(q1ans);
    }

    private boolean isQ2AnsEquals(String q2ans) {
        return this.Q2ans.equals(q2ans);
    }

    private boolean isQ3AnsEquals(String q3ans) {
        return this.Q3ans.equals(q3ans);
    }
}
