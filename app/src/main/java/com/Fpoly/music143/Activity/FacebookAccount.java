package com.Fpoly.music143.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.Fpoly.music143.Database.DAO.UserDAO;
import com.Fpoly.music143.Database.Services.CallBack.SucessCallBack;
import com.Fpoly.music143.Model.UserInfor;
import com.facebook.FacebookAuthorizationException;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import java.util.Arrays;

public class FacebookAccount extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Boolean getToken;
    private CallbackManager mCallbackManager;
    UserInfor userInfor = UserInfor.getInstance();
    // testcomit

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getToken=getIntent().getBooleanExtra("getToken",false);
        mAuth = FirebaseAuth.getInstance();

        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        finish();// App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        Log.e("error",exception.toString());
                        finish();
                    }
                });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        if(getToken){
            mAuth.getCurrentUser().unlink(mAuth.getCurrentUser().getProviderId());
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
//                                UserDAO userDAO = new UserDAO(FacebookAccount.this);
                            }else{
//                                Toast.makeText(FacebookAccount.this, "Liên Kết Thất Bại", Toast.LENGTH_SHORT).show();
                                Log.e("link_error",task.getException().toString());
                            }
                        }
                    });
        }
        if(mAuth.getCurrentUser()!=null){
            mAuth.signOut();
        }
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String ID = mAuth.getCurrentUser().getUid();
                            String email = mAuth.getCurrentUser().getEmail();
                            String name = mAuth.getCurrentUser().getDisplayName();
                            Log.e("test",ID+" "+email+" "+name);
                            userInfor.setID(mAuth.getCurrentUser().getUid());
                            finish();
                        } else {
                            Log.e("link_error",task.getException().toString());
                            finish();
                        }
                    }
                });
    }

}

