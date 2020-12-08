package com.Fpoly.music143.Fragment.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterViewFlipper;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


import com.Fpoly.music143.Activity.MainActivity;
import com.Fpoly.music143.Database.DAO.AlbumDAO;
import com.Fpoly.music143.Database.DAO.UserDAO;
import com.Fpoly.music143.Database.Services.CallBack.UserCallBack;
import com.Fpoly.music143.Fragment.Home.Adapter.AlbumAdapter;
import com.Fpoly.music143.Fragment.Home.Adapter.RankAdapter;
import com.Fpoly.music143.Fragment.Home.Adapter.SongNewAdapter;
import com.Fpoly.music143.Fragment.Home.Adapter.SuggestAdapter;
import com.Fpoly.music143.Database.DAO.SongsDAO;
import com.Fpoly.music143.Database.Services.CallBack.AlbumCallBack;
import com.Fpoly.music143.Database.Services.CallBack.SongCallBack;
import com.Fpoly.music143.Fragment.Music.PlayMusicFragment;
import com.Fpoly.music143.Model.Album;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import com.squareup.picasso.Picasso;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment {
    RecyclerView rcvalbum, rcvsuggest, rcvtop;
    ArrayList<Album> albumInRank;
    ArrayList<Song> SongList;
    ArrayList<Song> songsInRank;
    ArrayList<Song> songNew;
    AlbumAdapter albumAdapter;
    SuggestAdapter suggestAdapter;
    RankAdapter rankAdapter;
    SongNewAdapter songNewAdapter ;
    UserInfor userInfor = UserInfor.getInstance();

    DotsIndicator dotsIndicator;
    ViewPager viewPager ;
    Handler handler ;
    Runnable runnable ;
    int currentSong ;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        Log.d("devH", userInfor.getID());
        init(root) ;
        getUser(userInfor.getID());
        autoSwipe() ;
        return root;
    }

    private void init(View root) {
        rcvalbum = root.findViewById(R.id.rcvalbum);
        rcvsuggest = root.findViewById(R.id.rcvsuggest);
        rcvtop = root.findViewById(R.id.rcvtop);
        dotsIndicator =  root.findViewById(R.id.dots_indicator);
        viewPager =  root.findViewById(R.id.viewPager);
    }

    @Override
    public void onStart() {
        getData();
        super.onStart();
    }

    private void getData() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
        final SongsDAO songsDAO = new SongsDAO(getContext());
        final AlbumDAO albumDAO = new AlbumDAO(getContext());


        songsDAO.getRankSongs(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                songsInRank = song;
                rankAdapter = new RankAdapter(getContext(), song, HomeFragment.this);
                rcvtop.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcvtop.setAdapter(rankAdapter);
            }
        });
        albumDAO.getAlbum(new AlbumCallBack() {
            @Override
            public void getCallBack(ArrayList<Album> album) {
                albumInRank = album;
                albumAdapter = new AlbumAdapter(getContext(), albumInRank, HomeFragment.this);
                rcvalbum.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                rcvalbum.setAdapter(albumAdapter);
            }
        });

        songsDAO.getSuggest(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                SongList = song;
                suggestAdapter = new SuggestAdapter(getContext(), SongList, HomeFragment.this);
                rcvsuggest.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcvsuggest.setAdapter(suggestAdapter);
            }
        });

        songsDAO.getNewSongs(new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                songNew = song ;
                Log.d("songNew", String.valueOf(songNew.size())) ;
                songNewAdapter = new SongNewAdapter( getContext(), songNew, HomeFragment.this);
                viewPager.setAdapter(songNewAdapter);
                dotsIndicator.setViewPager(viewPager);
            }
        });
        dialog.dismiss();
    }

    private void getUser(final String userID) {
        final UserDAO userDAO = new UserDAO(getContext());
        userDAO.getUser(userID, new UserCallBack() {
            @Override
            public void getCallback(ArrayList<UserInfor> userInfors) {
//                Log.d("test", userInfors.get(0).toString());
                if(userInfors.size()>0){
                    userInfor.setUsername(userInfors.get(0).getUsername());
                    Log.d("test", userInfors.get(0).getUsername());
                    userInfor.setEmail(userInfors.get(0).getEmail());
                    userInfor.setFavorites(userInfors.get(0).getFavorites());
                    userInfor.setLinkFaceBook(userInfors.get(0).getLinkFaceBook());
                    userInfor.setLinkGmail(userInfors.get(0).getLinkGmail());
                }else{
                    Toast.makeText(getContext(),"Bạn Chưa Có Tài Khoản hệ Thống, Vui Lòng Đăng Ký",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void autoSwipe() {
        handler = new Handler() ;
        runnable = new Runnable() {
            @Override
            public void run() {
                currentSong = viewPager.getCurrentItem() ;
                currentSong++ ;
                if (currentSong >= viewPager.getAdapter().getCount()) {
                    currentSong = 0 ;
                } viewPager.setCurrentItem(currentSong, true);
                handler.postDelayed(runnable,4500) ;
            }
        } ; handler.postDelayed(runnable,4500) ;
    }


}