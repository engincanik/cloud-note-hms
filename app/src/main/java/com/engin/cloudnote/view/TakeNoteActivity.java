package com.engin.cloudnote.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.engin.cloudnote.R;
import com.engin.cloudnote.model.CloudDBZoneWrapper;
import com.engin.cloudnote.model.NoteOT;
import com.engin.cloudnote.viewmodel.NoteViewModel;
import com.huawei.agconnect.auth.AGConnectAuth;
import com.huawei.agconnect.auth.AGConnectAuthCredential;
import com.huawei.agconnect.auth.AGConnectUser;
import com.huawei.agconnect.auth.HwIdAuthProvider;
import com.huawei.agconnect.auth.SignInResult;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.hwid.HuaweiIdAuthAPIManager;
import com.huawei.hms.support.hwid.HuaweiIdAuthManager;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParams;
import com.huawei.hms.support.hwid.request.HuaweiIdAuthParamsHelper;
import com.huawei.hms.support.hwid.result.AuthHuaweiId;
import com.huawei.hms.support.hwid.result.HuaweiIdAuthResult;
import com.huawei.hms.support.hwid.service.HuaweiIdAuthService;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class TakeNoteActivity extends AppCompatActivity implements LifecycleOwner {
    private CloudDBZoneWrapper mCloudDBZoneWrapper;
    NoteViewModel noteViewModel;
    EditText noteCreator, noteDetail;
    private AGConnectAuth auth;

    public  TakeNoteActivity() {
        mCloudDBZoneWrapper = new CloudDBZoneWrapper();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_note);
        ButterKnife.bind(this);
        auth = AGConnectAuth.getInstance();
        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        noteCreator = findViewById(R.id.noteCreatorName);
        noteDetail = findViewById(R.id.noteEditText);

    }

    @OnClick(R.id.saveBtn)
    public void takeNote() {
        if (AGConnectAuth.getInstance().getCurrentUser() != null) {
            NoteOT noteOT = new NoteOT();
            noteOT.setId("3");
            noteOT.setNote(noteDetail.getText().toString());
            noteOT.setCreator(noteCreator.getText().toString());
            noteViewModel.insertNote(noteOT);
            Log.w("ViewModel","Button");
        } else {
            Toast.makeText(this, "Else", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.loginBtn)
    public void signInWithHuaweiAccount() {
        HuaweiIdAuthParams huaweiIdAuthParams = new HuaweiIdAuthParamsHelper(HuaweiIdAuthParams
                .DEFAULT_AUTH_REQUEST_PARAM).setAccessToken().createParams();
        HuaweiIdAuthService huaweiIdAuthService = HuaweiIdAuthManager
                .getService(TakeNoteActivity.this, huaweiIdAuthParams);
        startActivityForResult(huaweiIdAuthService.getSignInIntent(), 1001);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            Task<AuthHuaweiId> authHuaweiIdTask = HuaweiIdAuthManager.parseAuthResultFromIntent(data);
            if (authHuaweiIdTask.isSuccessful()) {
                Toast.makeText(TakeNoteActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                AuthHuaweiId huaweiAccount = authHuaweiIdTask.getResult();
                Log.i("ViewModel", "signIn success Access Token = " + huaweiAccount.getAccessToken());
                Log.i("ViewModel", "signIn success User Name = " + huaweiAccount.getDisplayName());
                transmitTokenIntoAppGalleryConnect(huaweiAccount.getAccessToken());
            } else {
                Toast.makeText(TakeNoteActivity.this, "Hi", Toast.LENGTH_SHORT).show();
                Log.i("ViewModel", "signIn failed: " + ((ApiException) authHuaweiIdTask.getException()).getStatusCode());
            }
        }
    }

    private void transmitTokenIntoAppGalleryConnect(String accessToken) {
        AGConnectAuthCredential credential = HwIdAuthProvider.credentialWithToken(accessToken);
        AGConnectAuth.getInstance().signIn(credential).addOnSuccessListener(new OnSuccessListener<SignInResult>() {
            @Override
            public void onSuccess(SignInResult signInResult) {
                Toast.makeText(TakeNoteActivity.this, signInResult.toString(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ViewModel", "Error " + e);
            }
        });
    }
}