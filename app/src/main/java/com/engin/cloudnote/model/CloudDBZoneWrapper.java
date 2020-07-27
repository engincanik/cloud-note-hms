package com.engin.cloudnote.model;

import android.content.Context;
import android.content.LocusId;
import android.util.Log;

import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.cloud.database.AGConnectCloudDB;
import com.huawei.agconnect.cloud.database.CloudDBZone;
import com.huawei.agconnect.cloud.database.CloudDBZoneConfig;
import com.huawei.agconnect.cloud.database.CloudDBZoneObjectList;
import com.huawei.agconnect.cloud.database.CloudDBZoneQuery;
import com.huawei.agconnect.cloud.database.CloudDBZoneSnapshot;
import com.huawei.agconnect.cloud.database.CloudDBZoneTask;
import com.huawei.agconnect.cloud.database.ListenerHandler;
import com.huawei.agconnect.cloud.database.OnFailureListener;
import com.huawei.agconnect.cloud.database.OnSuccessListener;
import com.huawei.agconnect.cloud.database.exceptions.AGConnectCloudDBException;

import java.util.ArrayList;
import java.util.List;

import static com.engin.cloudnote.model.CloudDBZoneWrapper.TAG;

public class CloudDBZoneWrapper {
    public final static String TAG = "CloudDBZoneWrapper";
    private AGConnectCloudDB mCloudDB;
    private CloudDBZone mCloudDBZone;
    private ListenerHandler mRegister;
    private CloudDBZoneConfig mConfig;
    private UiCallBack mUiCallBack = UiCallBack.DEFAULT;
    Boolean state;
    public CloudDBZoneWrapper() {
        mCloudDB = AGConnectCloudDB.getInstance();
    }

    public static void initAGConnectCloudDB(Context context) {
        AGConnectCloudDB.initialize(context);
        Log.w(TAG, "initAGConnectCloudDB");
    }

    public void createObjectType() {
        try {
            mCloudDB.createObjectType(ObjectTypeInfoHelper.getObjectTypeInfo());
            Log.w(TAG, "createObjectTypeSuccess");
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "createObjectTypeError: " + e.getMessage());
        }
    }

    /**
     * Call AGConnectCloudDB.openCloudDBZone to open a cloudDBZone.
     * We set it with cloud cache mode, and data can be store in local storage
     */
    public void openCloudDBZone() {
        mConfig = new CloudDBZoneConfig("CloudNote",
                CloudDBZoneConfig.CloudDBZoneSyncProperty.CLOUDDBZONE_CLOUD_CACHE,
                CloudDBZoneConfig.CloudDBZoneAccessProperty.CLOUDDBZONE_PUBLIC);
        mConfig.setPersistenceEnabled(true);
        Log.w(TAG, "openCloudDBZoneSuccess");
        try {
            mCloudDBZone = mCloudDB.openCloudDBZone(mConfig, true);
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "openCloudDBZoneError: " + e.getMessage());
        }
    }

    /**
     * Call AGConnectCloudDB.closeCloudDBZone
     */
    public void closeCloudDBZone() {
        try {
            mRegister.remove();
            mCloudDB.closeCloudDBZone(mCloudDBZone);
            Log.w(TAG, "closeCloudDBZoneSuccess");
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "closeCloudDBZoneError: " + e.getMessage());
        }
    }

    /**
     * Call AGConnectCloudDB.deleteCloudDBZone
     */
    public void deleteCloudDBZone() {
        try {
            mCloudDB.deleteCloudDBZone(mConfig.getCloudDBZoneName());
            Log.w(TAG, "deleteCloudBZoneSuccess");
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "deleteCloudDBZone: " + e.getMessage());
        }
    }

    public void getAllNotes() {
        if (mCloudDB == null) {
            Log.w(TAG, "GET Note DETAIL: CloudDBZone is null, try to re-open it");
            return;
        }
        CloudDBZoneTask<CloudDBZoneSnapshot<NoteOT>> queryTask = mCloudDBZone.executeQuery(
                CloudDBZoneQuery.where(NoteOT.class),
                CloudDBZoneQuery.CloudDBZoneQueryPolicy.POLICY_QUERY_FROM_CLOUD_ONLY);
        queryTask.addOnSuccessListener(new com.huawei.agconnect.cloud.database.OnSuccessListener<CloudDBZoneSnapshot<NoteOT>>() {
            @Override
            public void onSuccess(CloudDBZoneSnapshot<NoteOT> snapshot) {
                    noteListResult(snapshot);
                    Log.w(TAG, "GET Note DETAIL : GoResults: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (mUiCallBack != null) {
                    mUiCallBack.updateUiOnError("GET Note DETAIL : Query film list from cloud failed");
                }
            }
        });
    }

    private void noteListResult (CloudDBZoneSnapshot<NoteOT> snapshot) {
        CloudDBZoneObjectList<NoteOT> noteCursor = snapshot.getSnapshotObjects();
        List<NoteOT> noteInfoList = new ArrayList<>();
        try {
            while (noteCursor.hasNext()) {
                NoteOT note = noteCursor.next();
                noteInfoList.add(note);
                Log.w(TAG, "NOTE DETAIL RESULT: processQueryResult: ");
            }
        } catch (AGConnectCloudDBException e) {
            Log.w(TAG, "Note DETAIL RESULT: processQueryResult: " + e.getMessage());
        }
        snapshot.release();
        if (mUiCallBack != null) {
            mUiCallBack.onAddOrQueryNoteList(noteInfoList);
        }
    }

    public void insertNote(NoteOT note) {
        Log.w("ViewModel", "Inside Wrapper");
        state = false;
        if (mCloudDBZone == null) {
            Log.w(TAG, "Insert User: CloudDBZone is null, try re-open it");
            return;
        }

        CloudDBZoneTask<Integer> upsertTask = mCloudDBZone.executeUpsert(note);
        if (mUiCallBack == null) {
            return;
        }
        upsertTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer cloudDBZoneResult) {
                state = true;
                Log.w("ViewModel", "Success");
                Log.w(TAG, "Insert User: upsert " + cloudDBZoneResult + " records");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                state = false;
                Log.w("ViewModel", "Failed: " + e.getMessage());
                mUiCallBack.updateUiOnError("Insert User: Insert user info failed");
            }
        });
        if (mUiCallBack != null) {
            mUiCallBack.isDataUpsert(state);
        }

    }

    /**
     * Call back to update ui in MainActivity
     */
    public interface UiCallBack {
        UiCallBack DEFAULT = new UiCallBack() {

            /**
             *
             * @param noteList
             */
            @Override
            public void onAddOrQueryNoteList(List<NoteOT> noteList) {
                Log.w(TAG, "Using default onAddOrQuery");
            }

            @Override
            public void onSubscribeNoteList(List<NoteOT> noteList) {
                Log.w(TAG, "Using default onSubscribe");
            }

            @Override
            public void onDeleteNoteList(List<NoteOT> noteList) {
                Log.w(TAG, "Using default onDelete");
            }

            @Override
            public void updateUiOnError(String errorMessage) {
                Log.w(TAG, "Using default updateUiOnError");
            }

            @Override
            public void isDataUpsert(Boolean state) {
                Log.w(TAG, "Using default upsert");
                Log.w("ViewModel", "DEFAULT");
            }
        };

        /**
         * Call
         * @param noteList
         */
        void onAddOrQueryNoteList(List<NoteOT> noteList);
        void onSubscribeNoteList(List<NoteOT> noteList);
        void onDeleteNoteList(List<NoteOT> noteList);
        void updateUiOnError(String errorMessage);
        void isDataUpsert(Boolean state);
    }

    /**
     * Add a callback to update book info list
     *
     * @param uiCallBack callback to update book list
     */
    public void addCallBacks(CloudDBZoneWrapper.UiCallBack uiCallBack) {
        mUiCallBack = uiCallBack;
    }

}



