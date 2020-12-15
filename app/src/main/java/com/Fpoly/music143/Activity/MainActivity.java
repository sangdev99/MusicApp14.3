package com.Fpoly.music143.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.Fpoly.music143.Database.DAO.UserDAO;
import com.Fpoly.music143.Database.Services.CallBack.UserCallBack;
import com.Fpoly.music143.Fragment.Account.AccountFragment;
import com.Fpoly.music143.Fragment.Home.HomeFragment;
import com.Fpoly.music143.Fragment.Music.Adapter.ViewPagerPlayListNhac;
import com.Fpoly.music143.Fragment.Music.BackgroundSoundService;
import com.Fpoly.music143.Fragment.Music.Fragment_Dia_Nhac;
import com.Fpoly.music143.Fragment.Music.Fragment_Play_Danh_Sach_Cac_Bai_Hat;
import com.Fpoly.music143.Fragment.Music.Notification.MusicService;
import com.Fpoly.music143.Fragment.Music.PlayMusicFragment;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.api.LogDescriptor;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

import static com.Fpoly.music143.Fragment.Account.AccountFragment.KEY_ISNIGHTMODE;
import static com.Fpoly.music143.Fragment.Account.AccountFragment.MyPREFERENCES;

public class MainActivity extends AppCompatActivity {
    public static SlidingUpPanelLayout slidingUpPanelLayout;
    public static BottomNavigationView bottomNavigationView ;
    SharedPreferences sharedPreferences;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigationView = findViewById(R.id.nav_view);
        slidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.slidingUpPanel);
        // setBottom
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);


        // check Darkmode
        checkDarkMode();
        Log.d("main","oncreate") ;


    }


   // slidingUpPanelLayout
    public static void slidingUpPanelLayout() {
        // COLLAPSED: xup do
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                slideDown(bottomNavigationView,slideOffset);
            }
            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }
        }) ;
    }

    // slideDown bottom
    public static void slideDown(BottomNavigationView child, float slideOffset) {
        float heigh_to_animate = slideOffset * child.getHeight();
        ViewPropertyAnimator animator = child.animate();
        animator
                .translationY(heigh_to_animate)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(0)
                .start();
    }

    private void checkDarkMode() {
        sharedPreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean(KEY_ISNIGHTMODE, true)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    // onBackPressed
    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout != null &&
                (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        Log.d("main","finish") ;
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        super.onDestroy();
    }

 /*LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));
   private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            slidingUpPanelLayout() ;
        }
    };*/

 /*
    UserInfor userInfor = UserInfor.getInstance();
    PlayMusicFragment playMusicFragment = new PlayMusicFragment();
    MusicService musicService ;
    private String TAG = "MainActivity" ;
    FragmentManager fragmentManager;*/


}

