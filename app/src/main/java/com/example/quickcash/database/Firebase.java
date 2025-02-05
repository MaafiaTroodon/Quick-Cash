package com.example.quickcash.database;

import com.google.firebase.database.FirebaseDatabase;


public class Firebase {
    private String URL = "https://quickcash-34895-default-rtdb.firebaseio.com/";
    private FirebaseDatabase db;
    public Firebase(){
        this.db = FirebaseDatabase.getInstance(URL);
    }

    public FirebaseDatabase getDb() {
        return db;
    }
}
