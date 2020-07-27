package com.engin.cloudnote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.engin.cloudnote.R;
import com.engin.cloudnote.model.CloudDBZoneWrapper;
import com.engin.cloudnote.model.NoteOT;
import com.engin.cloudnote.viewmodel.NoteViewModel;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    MainActivity context;
    RecyclerView recyclerView;
    private CloudDBZoneWrapper mCloudDBZoneWrapper;
    NoteListAdapter noteAdapter;
    NoteViewModel noteViewModel;
    private static final String TAG = "NoteList";

    public MainActivity() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        ButterKnife.bind(this);
        noteAdapter = new NoteListAdapter(context, null);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        noteViewModel = new ViewModelProvider(context).get(NoteViewModel.class);
        noteViewModel.getNoteLiveData().observe(context, noteUpdateObserver);
    }

    @OnClick(R.id.takeNoteBtn)
    public void openTakeNote() {
        Intent intent = new Intent(this, TakeNoteActivity.class);
        startActivity(intent);
    }

    Observer<ArrayList<NoteOT>> noteUpdateObserver = new Observer<ArrayList<NoteOT>>() {
        @Override
        public void onChanged(ArrayList<NoteOT> noteItems) {
            noteAdapter = new NoteListAdapter(context, noteItems);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(noteAdapter);
        }
    };
}