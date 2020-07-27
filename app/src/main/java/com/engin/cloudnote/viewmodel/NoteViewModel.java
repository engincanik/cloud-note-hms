package com.engin.cloudnote.viewmodel;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.engin.cloudnote.model.CloudDBZoneWrapper;
import com.engin.cloudnote.model.NoteOT;
import com.engin.cloudnote.view.MainActivity;
import com.engin.cloudnote.view.TakeNoteActivity;
import com.huawei.agconnect.auth.AGConnectAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class NoteViewModel extends ViewModel implements CloudDBZoneWrapper.UiCallBack {

    private CloudDBZoneWrapper mCloudDBZoneWrapper;
    private static final String TAG = "Note List";
    MutableLiveData<ArrayList<NoteOT>> noteLiveData;
    ArrayList<NoteOT> noteList = new ArrayList<NoteOT>();
    final Handler handler  = new Handler();
    Timer timer = new Timer();
    TimerTask timerTask;
    public NoteViewModel() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
        noteLiveData = new MutableLiveData<>();
        init();
    }

    public MutableLiveData<ArrayList<NoteOT>> getNoteLiveData() {
        return noteLiveData;
    }

    public void init() {
        setNoteLiveData();
        noteLiveData.setValue(noteList);
    }

    public void setNoteLiveData() {
        getNotes();
    }

    public void getNotes() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    public void run() {
                        mCloudDBZoneWrapper.addCallBacks(NoteViewModel.this);
                        mCloudDBZoneWrapper.createObjectType();
                        mCloudDBZoneWrapper.openCloudDBZone();
                        mCloudDBZoneWrapper.getAllNotes();
                    }
                }, 500);
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, (60 * 500));
    }

    public void insertNote(NoteOT noteOT) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                mCloudDBZoneWrapper.addCallBacks(NoteViewModel.this);
                mCloudDBZoneWrapper.createObjectType();
                mCloudDBZoneWrapper.openCloudDBZone();
                mCloudDBZoneWrapper.insertNote(noteOT);
                Log.w("ViewModel", "Running");
            }
        });
    }

    @Override
    public void onAddOrQueryNoteList(List<NoteOT> noteListTmp) {
        noteList.clear();
        noteList.addAll(noteListTmp);
        for (int i = 0; i < noteList.size(); i++) {
            Log.w(TAG, String.valueOf(noteList.get(i)));
        }
        noteLiveData.setValue(noteList);
    }

    @Override
    public void onSubscribeNoteList(List<NoteOT> noteList) {

    }

    @Override
    public void onDeleteNoteList(List<NoteOT> noteList) {

    }

    @Override
    public void updateUiOnError(String errorMessage) {

    }

    @Override
    public void isDataUpsert(Boolean state) {
        if (state) {
            Log.w(TAG, "State is True");
        } else {
            Log.e(TAG, "State is False");
        }
    }
}
