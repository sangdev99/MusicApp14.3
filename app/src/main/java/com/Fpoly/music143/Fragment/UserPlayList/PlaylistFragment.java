package com.Fpoly.music143.Fragment.UserPlayList;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.Fpoly.music143.Activity.MainActivity;
import com.Fpoly.music143.Fragment.Account.AccountFragment;
import com.Fpoly.music143.Fragment.Music.PlayMusicFragment;
import com.Fpoly.music143.Fragment.UserPlayList.Apdater.AddItemPlayListAdapter;
import com.Fpoly.music143.Database.DAO.PlayListDAO;
import com.Fpoly.music143.Database.Services.CallBack.PlayListCallBack;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import com.Fpoly.music143.Fragment.UserPlayList.Apdater.PlaylistAdapter;
import com.Fpoly.music143.Model.PlayList;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {
    ArrayList<PlayList> myplayLists;
    RecyclerView rcvplaylist;
    Button btn_createPlaylist;
    PlaylistAdapter adapter;
    AddItemPlayListAdapter add_adapter;
    Boolean addMusic;
    private ArrayList<Song> mangbaihatPlaylist = new ArrayList<>();
    PlayMusicFragment playMusicFragment = new PlayMusicFragment() ;
    Toolbar toolbar ;
    int position;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_playlist, container, false);
        rcvplaylist = root.findViewById(R.id.rcvplaylist);
        btn_createPlaylist = root.findViewById(R.id.btn_createPlaylist);

//        UserInfor UserInfor = UserInfor.getInstance();
        UserInfor userInfor = UserInfor.getInstance();
        getData(userInfor.getID());

        toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbarOnclick() ;
        btn_createPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        return root;
    }

    private void toolbarOnclick() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Xác định nếu đang thao tác thêm bài hát vào playlist
                if(addMusic){
                    Bundle bundle1 = new Bundle();
                    if(mangbaihatPlaylist.size()>1){
                        bundle1.putParcelableArrayList("MultipleSongs",mangbaihatPlaylist);
                        bundle1.putInt("CurrentPosition",position);
                    }else{
                        bundle1.putParcelable("Songs",mangbaihatPlaylist.get(0));
                    }
                    Fragment fragment = new PlayMusicFragment();
                    fragment.setArguments(bundle1);
                    changeFragment(view, fragment,true);
                }else {
                    changeFragment(view, new AccountFragment(),false);
                }

            }
        });
    }

    private void showDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        final EditText name = new EditText(getContext());
        name.setHint("Nhập Tên PlayList");
        name.setMinEms(16);
        linearLayout.addView(name);
        linearLayout.setPadding(10,50,10,10);
        builder.setView(linearLayout);
        //button Rename
        builder.setPositiveButton("Tạo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //input email
                CreateNew(name.getText().toString());
                dialog.dismiss();
            }
        });
        //button Cancel
        builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //Show Dialog
        builder.create().show();

    }

    private void CreateNew(String name) {
        UserInfor userInfor = UserInfor.getInstance();
        PlayListDAO playListDAO = new PlayListDAO(getContext());
        playListDAO.createPlaylist(userInfor.getID(), name, new PlayListCallBack() {
            @Override
            public void getCallBack(ArrayList<PlayList> playLists) {
                myplayLists.clear();
                myplayLists.addAll(playLists);
                if(addMusic){
                    add_adapter.notifyDataSetChanged();
                }else{
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void getData(String UID) {
        final Bundle bundle = getArguments();
        addMusic = bundle.getBoolean("AddMusic");
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading);
        dialog.show();
        PlayListDAO playListDAO = new PlayListDAO(getContext());
        playListDAO.getPlayList(UID, new PlayListCallBack() {
            @Override
            public void getCallBack(ArrayList<PlayList> playlist) {
                myplayLists = playlist;
                Log.d("MyPlaylist",playlist.toString()) ;
                rcvplaylist.setLayoutManager(new LinearLayoutManager(getActivity()));
                Log.d("playlistTest", playlist.toString());
                if(addMusic){
                    mangbaihatPlaylist = bundle.getParcelableArrayList("mangbaihatPlayList");
                    position = bundle.getInt("Position");
                    Log.d("chuyenPlaylist",mangbaihatPlaylist.size() + "") ;
                    Log.d("chuyenPlaylist",position + "") ;
                    add_adapter = new AddItemPlayListAdapter(getContext(),myplayLists,PlaylistFragment.this);
                    rcvplaylist.setAdapter(add_adapter);
                }else{
                    adapter = new PlaylistAdapter(getContext(),myplayLists,PlaylistFragment.this);
                    rcvplaylist.setAdapter(adapter);
                }
                dialog.dismiss();
            }
        });
    }

    private void changeFragment(View view , Fragment fragment, boolean isback){
        FragmentTransaction fragmentTransaction =this.getFragmentManager().beginTransaction();
        if(isback){
            fragmentTransaction.setCustomAnimations(R.anim.slide_out_left, R.anim.slide_in_right);
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();
        }else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
            fragmentTransaction.replace(R.id.nav_host_fragment,fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }


    }

}