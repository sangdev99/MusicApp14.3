package com.Fpoly.music143.Fragment.Account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.Fpoly.music143.Activity.FacebookAccount;
import com.Fpoly.music143.Activity.GoogleAccount;
import com.Fpoly.music143.Activity.LoginActivity;
import com.Fpoly.music143.Fragment.SongsList.SongsListFragment;
import com.Fpoly.music143.Fragment.UserPlayList.PlaylistFragment;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
    TextView tvUsername,tvUserEmail;
    ArrayList<String> list;
    UserInfor userInfor = UserInfor.getInstance();
    LinearLayout Favorites;
    LinearLayout Playlist;
    LinearLayout EditAccount;
    SwitchCompat swface, swgmail;
    SwitchCompat swdarkmode  ;
    Button btnSignOut ;
    // darkMode
    public static final String MyPREFERENCES = "nightModePrefs";
    public static final String KEY_ISNIGHTMODE = "isNightMode";
    SharedPreferences sharedPreferences;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_account, container, false);

        //==========================================================================================
        init(root) ;
        GetUser();
//        check() ;
        onClick() ;

        return root;
    }
    private void init(View root) {
        tvUsername = root.findViewById(R.id.tvUserName);
        tvUserEmail = root.findViewById(R.id.tvUserEmail);
        swface =root.findViewById(R.id.swface);
        swgmail = root.findViewById(R.id.swgmail);
        swdarkmode = root.findViewById(R.id.swDarkMode);
        Favorites = root.findViewById(R.id.Favorites);
        Playlist = root.findViewById(R.id.PlayList);
        EditAccount = root.findViewById(R.id.Edit_Account);
        btnSignOut = root.findViewById(R.id.btnSignOut);
    }

    private void onClick() {
        //Khi nhấn vào bài hát yêu thích
        Favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfor.setisFavorites(true);
                userInfor.setisPlayList(true);
                changeFragment(new SongsListFragment());
            }
        });
        //Khi Nhấn Vào PlayList
        Playlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle =new Bundle();
                bundle.putBoolean("AddMusic",false);
                Fragment fragment = new PlaylistFragment();
                fragment.setArguments(bundle);
                changeFragment(fragment);
            }
        });
        swgmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(swgmail.isChecked()){
//                  CreateLink(true);
                }
            }
        });

        swface.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(swface.isChecked()){
//                   CreateLink(false);
                }
            }
        });
        //DarkMode
        sharedPreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        checkNightModeActivated();
        swdarkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    saveNightModeState(true);
                    getActivity().recreate();
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    saveNightModeState(false);
                    getActivity().recreate();
                }

            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });
    }

    // Dark Mode
    private void saveNightModeState(boolean nightMode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ISNIGHTMODE, nightMode);
        editor.apply();
    }
    public void checkNightModeActivated(){
        if(sharedPreferences.getBoolean(KEY_ISNIGHTMODE, true)){
            swdarkmode.setChecked(true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            swdarkmode.setChecked(false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private void changeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction =this.getFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_out_left,R.anim.slide_in_right);
        fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void GetUser() {
        UserInfor userInfor = UserInfor.getInstance();
                if(userInfor.getUsername()!=null){
                    tvUsername.setText(userInfor.getUsername());
                    tvUserEmail.setText(userInfor.getEmail());
                    list = (ArrayList)userInfor.getFavorites();
                    swface.setChecked(userInfor.getLinkFaceBook()?true:false);
                    swgmail.setChecked(userInfor.getLinkGmail()?true:false);
                }else{
                    Toast.makeText(getContext(),"Bạn Chưa Có Tài Khoản hệ Thống, Vui Lòng Đăng Ký",Toast.LENGTH_SHORT).show();
                    Favorites.setEnabled(false);
                    Playlist.setEnabled(false);
                    EditAccount.setEnabled(false);
                    swgmail.setEnabled(false);
                    swface.setEnabled(false);
                }
            }

    private void SignOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
    }

    @Override
    public void onResume() {
        super.onResume();
        GetUser();
    }

    // Connect Fb GG
    private void Unlink() {
        Intent intent =new Intent(getContext(), GoogleAccount.class);
        intent.putExtra("getToken",true);
        intent.putExtra("Link",false);
        startActivity(intent);
    }
    private void CreateLink(boolean isMail) {
        if(isMail){
            Intent intent =new Intent(getContext(), GoogleAccount.class);
            intent.putExtra("getToken",true);
            intent.putExtra("Link",true);
            startActivity(intent);
        }else{
            Intent intent =new Intent(getContext(), FacebookAccount.class);
            intent.putExtra("getToken",true);
            startActivity(intent);
        }
    }
    private void check(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        for(int i = 0; i<mAuth.getCurrentUser().getProviderData().size();i++){
            System.out.println(user.getProviderData().get(i).getProviderId());
        }
    }
}