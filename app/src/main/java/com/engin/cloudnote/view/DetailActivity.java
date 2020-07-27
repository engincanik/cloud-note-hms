package com.engin.cloudnote.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.engin.cloudnote.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        TextView noteCreator, noteDesc;
        String id = intent.getStringExtra("noteItemID");
        noteCreator = findViewById(R.id.noteCreatorDetail);
        noteDesc = findViewById(R.id.noteDescDetail);

        noteCreator.setText(intent.getStringExtra("noteItemCreator"));
        noteDesc.setText(intent.getStringExtra("noteItemNote"));
    }
}