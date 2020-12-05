package com.Fpoly.music143.Fragment.Search;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ProgressBar;

import androidx.annotation.NonNull;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.Fpoly.music143.Fragment.Search.Adapter.SongByKindAdapter;
import com.Fpoly.music143.Database.DAO.SearchDAO;
import com.Fpoly.music143.Database.Services.CallBack.SongCallBack;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import java.util.ArrayList;

public class SongByKindFragment extends Fragment {
    Toolbar toolbar;
    RecyclerView rcvsongbykind;
    String kindID;
    ArrayList<Song> mySong;
    SongByKindAdapter adapter;
    private boolean loading = false;
    ProgressBar progressBar;
    LinearLayoutManager mLayoutManager;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_musicbykind, container, false);
        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment();
            }
        });
        mLayoutManager = new LinearLayoutManager(getContext());
        rcvsongbykind = root.findViewById(R.id.rcvsongbykind);
        progressBar = root.findViewById(R.id.progressBar);
        UserInfor userInfor = UserInfor.getInstance();
        kindID = userInfor.getKindID();
        getData(kindID);
        //bắt sự kiện khi kéo đến vị trí cuối cùng trong danh sách
        rcvsongbykind.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(!loading){
                    if(mLayoutManager!=null && mLayoutManager.findLastCompletelyVisibleItemPosition() == mySong.size()-1){
                        loadMore(kindID);
                        loading = true;
                    }
                }
            }
        });
        return root;
    }

    private void loadMore(String kindID){
        progressBar.setVisibility(View.VISIBLE);
        SearchDAO searchDAO = new SearchDAO(getContext());
        searchDAO.getNextMusicByKind(kindID, mySong.get(mySong.size() - 1).getID(), new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                for(int i= 0; i<song.size();i ++){
                    mySong.add(song.get(i));
                }
                progressBar.setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void getData(String kindID) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
        SearchDAO searchDAO = new SearchDAO(getContext());
        searchDAO.getMusicByKind(kindID, new SongCallBack() {
            @Override
            public void getCallBack(ArrayList<Song> song) {
                Log.d("hehe", "123456");
                mySong = song;
                adapter = new SongByKindAdapter(getContext(),mySong,SongByKindFragment.this);
                rcvsongbykind.setLayoutManager(mLayoutManager);
                rcvsongbykind.setAdapter(adapter);
                dialog.dismiss();
            }
        });
    }

    private void changeFragment(){
        Fragment fragment = new SearchFragment();
        FragmentTransaction ft  = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        ft.replace(R.id.nav_host_fragment,fragment);
        ft.addToBackStack(null);
        ft.commit();
    }
}