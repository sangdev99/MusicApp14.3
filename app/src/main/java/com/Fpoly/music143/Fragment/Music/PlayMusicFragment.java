package com.Fpoly.music143.Fragment.Music;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.Fpoly.music143.Fragment.Music.Adapter.ViewPagerPlayListNhac;
import com.Fpoly.music143.Database.DAO.FavoritesDAO;
import com.Fpoly.music143.Database.Services.CallBack.SucessCallBack;
import com.Fpoly.music143.Fragment.Music.Notification.ActionPlaying;
import com.Fpoly.music143.Fragment.Music.Notification.MusicService;
import com.Fpoly.music143.Fragment.Music.Notification.NotificationReceiver;
import com.Fpoly.music143.Fragment.UserPlayList.PlaylistFragment;
import com.Fpoly.music143.Fragment.SongsList.SongsListFragment;
import com.Fpoly.music143.Fragment.Home.HomeFragment;
import com.Fpoly.music143.Fragment.Search.SearchFragment;
import com.Fpoly.music143.Fragment.Search.SongByKindFragment;
import com.Fpoly.music143.Model.Song;
import com.Fpoly.music143.Model.UserInfor;
import com.Fpoly.music143.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

import static android.content.Context.BIND_AUTO_CREATE;
import static android.content.Context.NOTIFICATION_SERVICE;
import static com.Fpoly.music143.Fragment.Music.Notification.ApplicationClass.ACTION_NEXT;
import static com.Fpoly.music143.Fragment.Music.Notification.ApplicationClass.ACTION_PLAY;
import static com.Fpoly.music143.Fragment.Music.Notification.ApplicationClass.ACTION_PREV;
import static com.Fpoly.music143.Fragment.Music.Notification.ApplicationClass.CHANNEL_ID_2;

public class PlayMusicFragment extends Fragment implements ActionPlaying, ServiceConnection {
    public static ArrayList<Song> mangbaihat = new ArrayList<>();
    public static ViewPagerPlayListNhac adapternhac;
    Toolbar toolbar;
    TextView txttimesong,txttotaltimesong;
    SeekBar sktime;
    ImageButton imgbtnsuffle,imgbtnpre,imgbtnplay,imgbtnnext,imgbtnrepeat,imgbtnlike,imgbtnplaylist;
    ViewPager viewpagerplaynhac;
    MediaPlayer mediaPlayer;
    PlayMp3 playMp3 = new PlayMp3();
    Fragment_Dia_Nhac fragment_dia_nhac;
    Fragment_Play_Danh_Sach_Cac_Bai_Hat fragment_play_danh_sach_cac_bai_hat;
    int fragment;
    int CurrentPosition;
    Fragment oldFragment;
    UserInfor userInfor = UserInfor.getInstance();
    boolean stop = false  ;
    int position = 0 ;
    boolean repeat = false;
    boolean checkrandom = false;
    boolean next = false;
    Boolean isFavorites=false;
    MediaSessionCompat mediaSession;
    MusicService musicService;
    BottomNavigationView navBar ;
    private DotsIndicator dotsIndicator_music;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_play_music,container,false);
        toolbar =root.findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop = true;
                mangbaihat.clear();
                mediaPlayer.stop();
                changeFragment();
            }
        });
        Log.e("stop", String.valueOf(stop)) ;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        getDataFromIntent();
        init(root);
        eventClick();
        return root;

    }
    //Hàm bắt các sự kiện nhấn của các nút
    private void eventClick() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(adapternhac.getItem(0)!=null){
                    if(mangbaihat.size()>0){
                        fragment_dia_nhac.Playnhac(mangbaihat.get(0).getImage());
                        handler.removeCallbacks(this);
                    }else{
                        handler.postDelayed(this,300);
                    }
                }
            }
        },500);

        // hide bottom
//        navBar.visibility = View.GONE;

        //Nút Chơi Nhạc
        imgbtnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playClicked();
            }
        });

        //Nút Lặp
        imgbtnrepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat == false){
                    if (checkrandom == true){
                        //chỉ được sử dụng 1 trong 2 hoặc repeat hoặc random
                        checkrandom = false;
                        imgbtnrepeat.setImageResource(R.drawable.ic_loop_black_24dp);
                        imgbtnsuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    }
                    //set lại giá trị repeat và hình ảnh nút tương ứng
                    imgbtnrepeat.setImageResource(R.drawable.ic_loop_black_24dp2);
                    repeat = true;
                }else {
                    imgbtnrepeat.setImageResource(R.drawable.ic_loop_black_24dp);
                    repeat = false;
//                    imgbtnrepeat.setBackgroundColor("#00000000");
                }
            }
        });

        //nút nghe Ngẫu Nhiên
        imgbtnsuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkrandom == false) {
                    if (repeat == true) {
                        repeat = false;
                        imgbtnsuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                        imgbtnrepeat.setImageResource(R.drawable.ic_loop_black_24dp);
                    }
                    imgbtnsuffle.setImageResource(R.drawable.ic_shuffle_black_24dp2);
                    checkrandom = true;
                } else {
                    imgbtnsuffle.setImageResource(R.drawable.ic_shuffle_black_24dp);
                    checkrandom = false;

                }
            }
        });

        //Hàm Khi Kéo Seekbar bài hát sẽ tua đến vị trí tương ứng
        sktime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        //Nút Tiến
        imgbtnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextClicked();
            }
        });

        //Nút Lùi
        imgbtnpre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevClicked();

            }
        });
    }
    //Hàm lấy dữ liệu
    private void getDataFromIntent() {
        Bundle bundle = getArguments();
        //Nhận biết từ fragment nào gọi đến
        fragment = bundle.getInt("fragment");
        //Nhận dữ liệu từ các fragment truyền qua
        Song song = bundle.getParcelable("Songs");
        if(song==null){
            mangbaihat = bundle.getParcelableArrayList("MultipleSongs");
        }else{
            mangbaihat.clear();
            mangbaihat.add(song);
        }
    }
    //Hàm play bài hát
    @Override
    public void playClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
            imgbtnplay.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_play);
        }else{
            mediaPlayer.start();
            imgbtnplay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
        }
    }
    //Hàm chuyển về bài hát
    @Override
    public void nextClicked() {
        if (mangbaihat.size()>0){
            //nếu có bài hát nào đang chạy sẽ dừng bài hát đó
            if (mediaPlayer.isPlaying()||mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
            //Kiểm tra vị trí phải bé hơn mảng bài hát
            if (position < (mangbaihat.size())){
                //set lại hình ảnh cho nút chơi nhạc
                imgbtnplay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                position++;
                //nếu người dùng đang chọn chế dộ lặp lại
                if (repeat == true){
                    if (position == 0 ){
                        position = mangbaihat.size();
                    }
                    position -=1;
                }
                if (checkrandom == true){
                    Random random = new Random();
                    int index = random.nextInt(mangbaihat.size());
                    if (index == position){
                        position = index - 1;
                    }
                    position = index;
                }
                if (position > (mangbaihat.size() - 1)){
                    position = 0;
                }
                new PlayMp3().execute(mangbaihat.get(position).getLink());
                fragment_dia_nhac.Playnhac(mangbaihat.get(position).getImage());
                toolbar.setTitle(mangbaihat.get(position).getName());

                //Kiểm tra bài hát có nằm trong danh sách yêu thích hay không
                checkDuplicate(mangbaihat.get(position).getID());
                //Đổi màu trái tim tùy theo bài hát
                if(isFavorites){
                    imgbtnlike.setColorFilter(getResources().getColor(R.color.yellow),
                            PorterDuff.Mode.SRC_ATOP);
                }else{
                    imgbtnlike.setColorFilter(getResources().getColor(R.color.white),
                            PorterDuff.Mode.SRC_ATOP);
                }
                //Khi nhấn vào nút yêu thích(trái tim)
                imgbtnlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isFavorites){
                            ItemFavorites(mangbaihat.get(position).getID(),false);
                        }else{
                            ItemFavorites(mangbaihat.get(position).getID(),true);
                        }
                    }
                });
                imgbtnplaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPlayList(mangbaihat.get(0).getID(),position);
                    }
                });
                showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
                UpdateTime();
            }

            imgbtnpre.setClickable(false);
            imgbtnnext.setClickable(false);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imgbtnpre.setClickable(true);
                    imgbtnnext.setClickable(true);
                }
            },5000);
        }
    }
    //Hàm chuyển về bài hát trước đó trong danh sách
    @Override
    public void prevClicked() {
        if (mangbaihat.size()>0){
            if (mediaPlayer.isPlaying()||mediaPlayer!=null){
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;

            }
            if (position < (mangbaihat.size())){
                imgbtnplay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                position--;
                if (position <0){
                    position = mangbaihat.size() - 1;
                }

                if (repeat == true){
                    position +=1;
                }
                if (checkrandom == true){
                    Random random = new Random();
                    int index = random.nextInt(mangbaihat.size());
                    if (index == position){
                        position = index - 1;

                    }
                    position = index;
                }
                new PlayMp3().execute(mangbaihat.get(position).getLink());
                fragment_dia_nhac.Playnhac(mangbaihat.get(position).getImage());
                toolbar.setTitle(mangbaihat.get(position).getName());
                //Kiêm tra bài hát có nằm trong danh sách yêu thích hay không
                checkDuplicate(mangbaihat.get(position).getID());
                //Đổi màu trái tim tùy theo danh sách bài hát
                if(isFavorites){
                    imgbtnlike.setColorFilter(getResources().getColor(R.color.yellow),
                            PorterDuff.Mode.SRC_ATOP);
                }else{
                    imgbtnlike.setColorFilter(getResources().getColor(R.color.white),
                            PorterDuff.Mode.SRC_ATOP);
                }
                //Khi nhấn vào nút yêu thích(Trái tim)
                imgbtnlike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isFavorites){
                            ItemFavorites(mangbaihat.get(position).getID(),false);
                        }else{
                            ItemFavorites(mangbaihat.get(position).getID(),true);
                        }
                    }
                });
                imgbtnplaylist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addPlayList(mangbaihat.get(0).getID(),position);
                    }
                });
                showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
                UpdateTime();
            }
        }
        imgbtnpre.setClickable(false);
        imgbtnnext.setClickable(false);
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                imgbtnpre.setClickable(true);
                imgbtnnext.setClickable(true);
            }
        },5000);
    }
    //Hàm ánh xạ, và chơi nhạc ban đầu
    private void init(View root) {
        dotsIndicator_music = root.findViewById(R.id.dotsIndicator_music) ;
        navBar = root.findViewById(R.id.nav_view);
        mediaSession = new MediaSessionCompat(getContext(),"PlayerAudio");
        //Các nút thao tác với danh sách bài hát
        imgbtnlike = root.findViewById(R.id.imgbtnlike);
        imgbtnplaylist = root.findViewById(R.id.imgbtnplaylist);
        //Các nút thao tác với bài hát
        txttimesong = root.findViewById(R.id.tvtimesong);
        txttotaltimesong = root.findViewById(R.id.tvtotaltimesong);
        sktime = root.findViewById(R.id.sbsong);
        imgbtnsuffle = root.findViewById(R.id.imgbtnsuffle);
        imgbtnpre = root.findViewById(R.id.imgbtnpre);
        imgbtnplay = root.findViewById(R.id.imgbtnplay);
        imgbtnnext=root.findViewById(R.id.imgbtnnext);
        imgbtnrepeat=root.findViewById(R.id.imgbtnrepeat);
        viewpagerplaynhac= root.findViewById(R.id.vpPlaynhac);
        if(mangbaihat.size()>0){
            Bundle bundle = getArguments();
            if(bundle.getInt("CurrentPosition")!=0){
                CurrentPosition = bundle.getInt("CurrentPosition");
            }else{
                CurrentPosition = 0;
            }
            toolbar.setTitle(mangbaihat.get(CurrentPosition).getName());

            //Kiểm tra có nằm trong danh sách bài hát yêu thích hay không
            checkDuplicate(mangbaihat.get(CurrentPosition).getID());
            //Thay đổi màu trái tim tùy theo bài hát
            if(isFavorites){
                imgbtnlike.setColorFilter(getResources().getColor(R.color.yellow),
                        PorterDuff.Mode.SRC_ATOP);
            }else{
                imgbtnlike.setColorFilter(getResources().getColor(R.color.white),
                        PorterDuff.Mode.SRC_ATOP);
            }
            //Khi nhấn vào nút yêu thích(trái tim)
            imgbtnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isFavorites){
                        ItemFavorites(mangbaihat.get(CurrentPosition).getID(),false);
                    }else{
                        ItemFavorites(mangbaihat.get(CurrentPosition).getID(),true);
                    }
                }
            });
            imgbtnplaylist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addPlayList(mangbaihat.get(CurrentPosition).getID(),0);
                }
            });
            //Chạy bài hát và đổi hình ảnh nút play
            playMp3.execute(mangbaihat.get(CurrentPosition).getLink());
            imgbtnplay.setImageResource(R.drawable.ic_play);
            showNotification(R.drawable.ic_pause_circle_filled_black_24dp);
        }
        //Đổ dữ liệu hình ảnh lên fragment đĩa nhạc và danh sách lên fragment playdanhsach
        fragment_dia_nhac = new Fragment_Dia_Nhac();
        fragment_play_danh_sach_cac_bai_hat = new Fragment_Play_Danh_Sach_Cac_Bai_Hat();
//        System.out.println(mangbaihat.get(CurrentPosition).getImage());
//        fragment_dia_nhac.Playnhac(mangbaihat.get(CurrentPosition).getImage());
        adapternhac = new ViewPagerPlayListNhac(getChildFragmentManager());
        adapternhac.addFragment(fragment_dia_nhac);
        adapternhac.addFragment(fragment_play_danh_sach_cac_bai_hat);
        viewpagerplaynhac.setAdapter(adapternhac);
        dotsIndicator_music.setViewPager(viewpagerplaynhac);
        fragment_dia_nhac = (Fragment_Dia_Nhac) adapternhac.getItem(0);
    }
    //Hàm format thời gian
    private void TimeSong(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        txttotaltimesong.setText(simpleDateFormat.format(mediaPlayer.getDuration()));
        sktime.setMax(mediaPlayer.getDuration());
    }
    //Hàm cập nhập thời gian bài hát
    private void UpdateTime(){
        //Handler cập nhập thời gian bài hát, được gọi mỗi 300ms
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //kiểm tra biến stop, biến này true khi bấm thoát khỏi fragment này,luồng lấy thời gian bài hát chỉ chạy khi biến này false
                if(!stop){
                    //kiểm tra sự tồn tại của mediaplayer
                    if (mediaPlayer !=null){
                        //set cho seekbar theo vị trí hiện tại của bài hát
                        sktime.setProgress(mediaPlayer.getCurrentPosition());
                        //format seekbar
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
                        //set thời gian chạy theo vị trí hiện tại của bài hát
                        txttimesong.setText(simpleDateFormat.format(mediaPlayer.getCurrentPosition()));
                        //chạy lại hàm trên
                        handler.postDelayed(this,300);
                        //khi bài hát kết thúc
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                next =true;
                                try{
                                    //tạm ngủ thread này 1s
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }else {
                    handler.removeCallbacks(this);
                }
            }
        },300);
        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Kiểm tra nếu next = true
                if (next == true) {
                    if (position < (mangbaihat.size())) {
                        imgbtnplay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                        position++;
                        if (repeat == true) {
                            if (position == 0) {
                                position = mangbaihat.size();
                            }
                            position -= 1;
                        }
                        if (checkrandom == true) {
                            Random random = new Random();
                            int index = random.nextInt(mangbaihat.size());
                            if (index == position) {
                                position = index - 1;

                            }
                            position = index;
                        }
                        if (position > (mangbaihat.size() - 1)) {
                            position = 0;
                        }
                        new PlayMp3().execute(mangbaihat.get(position).getLink());
                        fragment_dia_nhac.Playnhac(mangbaihat.get(position).getImage());
                        toolbar.setTitle(mangbaihat.get(position).getName());
                    }
                    //sau khi click xong vô hiệu 2 nút;
                    imgbtnpre.setClickable(false);
                    imgbtnnext.setClickable(false);
                    //tạo một luồng set lại 2 nút
                    Handler handler1 = new Handler();
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            imgbtnpre.setClickable(true);
                            imgbtnnext.setClickable(true);
                        }
                    }, 5000);
                    next = false;
                    handler1.removeCallbacks(this);
                }else {
                    handler1.postDelayed(this,1000);
                }
            }
        },1000);
    }
    //Hàm kiểm tra bài hát yêu thích
    private void checkDuplicate(String ID){
        UserInfor userInfor = UserInfor.getInstance();
        ArrayList<String> songid = userInfor.getFavorites();
        if(songid!=null){
            for(int i = 0; i<songid.size(); i++){
                if(songid.get(i).equals(ID)){
                    isFavorites = true;
                    break;
                }
            }
        }
    }
    //Hàm chuyển fragment
    private void changeFragment(){
        //Tùy theo giá trị fragment được gửi tới từ các fragment khác nhau để về lại fragment đó
        switch (fragment){
            case 1: oldFragment = new SongsListFragment();
                break;
            case 2: oldFragment = new SearchFragment();
                break;
            case 3: oldFragment = new SongByKindFragment();
                break;
            default :oldFragment = new HomeFragment();
                break;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();ft.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right);
        ft.replace(R.id.nav_host_fragment,oldFragment);
        ft.commit();
    }
    //Hàm thêm vào danh sách playlist cá nhân
    private void addPlayList(String ID,int position){
        if(userInfor.getUsername()!=null){
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.slide_out_left,R.anim.slide_in_right);
            mediaPlayer.stop();
            Fragment fragment = new PlaylistFragment();
            Log.d("chuyenPlaylist",mangbaihat.size() + "") ;
            Log.d("chuyenPlaylist",position + "") ;
            Bundle bundle = new Bundle();
            bundle.putBoolean("AddMusic",true);
            bundle.putParcelableArrayList("mangbaihatPlayList",mangbaihat);
            bundle.putInt("Position",position);
            fragment.setArguments(bundle);
            userInfor.setTempID(ID);

            ft.replace(R.id.nav_host_fragment,fragment);
            ft.commit();
        }else{
            Toast.makeText(getContext(),"Bạn Cần Đăng Nhập Để Thực Hiện Chức Năng Này",Toast.LENGTH_SHORT).show();
        }
    }
    //Hàm thêm vào danh sách bài hát yêu thích
    private void ItemFavorites(final String ID, boolean Add) {
        if(userInfor.getUsername()!=null){
            String UserID = userInfor.getID();
            FavoritesDAO favoritesDAO = new FavoritesDAO(getContext());
            if (Add) {
                favoritesDAO.addItemFavorites(UserID, ID, new SucessCallBack() {
                    @Override
                    public void getCallBack(Boolean isSucees) {
                        if (isSucees) {
                            //Khi thành công đổi màu trái tim
                            imgbtnlike.setColorFilter(getResources().getColor(R.color.yellow),
                                    PorterDuff.Mode.SRC_ATOP);
                            isFavorites = true;
                            //Sửa lại danh sách bài hát yêu thích của client
                            UserInfor userInfor = UserInfor.getInstance();
                            ArrayList<String>list = userInfor.getFavorites();
                            try {
                                list.add(ID);
                                userInfor.setFavorites(list);
                            } catch (Exception e) {
                                System.out.println("erorr" + e);
                            }
                        }
                    }
                });
            } else {
                favoritesDAO.removeItemFavorites(UserID, ID, new SucessCallBack() {
                    @Override
                    public void getCallBack(Boolean isSucees) {
                        if (isSucees) {
                            imgbtnlike.setColorFilter(getResources().getColor(R.color.white),
                                    PorterDuff.Mode.SRC_ATOP);
                            isFavorites = false;
                            UserInfor userInfor = UserInfor.getInstance();
                            try{
                                ArrayList<String>list = userInfor.getFavorites();
                                list.remove(ID);
                                userInfor.setFavorites(list);
                            }catch (Exception e) {
                                Log.d("catch", e.toString()) ;
                            }
                        }
                    }
                });
            }
        }else{
            Toast.makeText(getContext(),"Bạn Cần Đăng Nhập Để Thực Hiện Chức Năng Này",Toast.LENGTH_SHORT).show();
        }
    }
    // show notification
    public void showNotification(int playPauseBtn){
        try{
            Intent intent = new Intent(getContext(), getClass());
            PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
            Intent prevIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_PREV);
            PendingIntent prevPendingIntent = PendingIntent.getBroadcast(getContext(), 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent playIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_PLAY);
            PendingIntent playPendingIntent = PendingIntent.getBroadcast(getContext(), 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Intent nextIntent = new Intent(getContext(), NotificationReceiver.class).setAction(ACTION_NEXT);
            PendingIntent nextPendingIntent = PendingIntent.getBroadcast(getContext(), 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            // design notifi
            if (playPauseBtn == R.drawable.ic_pause_circle_filled_black_24dp) {
                // design notification
                Bitmap picture = BitmapFactory.decodeResource(getResources(),R.drawable.hinhlogo);
                Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID_2)
                        .setSmallIcon(R.drawable.hinhlogo)
                        .setLargeIcon(picture)
                        .setContentTitle(mangbaihat.get(position).getName())
                        .setContentText(mangbaihat.get(position).getSinger())
                        .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous",prevPendingIntent)
                        .addAction(R.drawable.ic_pause_circle_filled_black_24dp, "Play",playPendingIntent)
                        .addAction(R.drawable.ic_skip_next_black_24dp, "Next",nextPendingIntent)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().
                                setMediaSession(mediaSession.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setContentIntent(contentIntent)
                        .setOnlyAlertOnce(true)
                        .build();
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0,notification);
            } else {
                // design notification
                Bitmap picture = BitmapFactory.decodeResource(getResources(),R.drawable.hinhlogo);
                Notification notification = new NotificationCompat.Builder(getContext(), CHANNEL_ID_2)
                        .setSmallIcon(R.drawable.hinhlogo)
                        .setLargeIcon(picture)
                        .setContentTitle(mangbaihat.get(position).getName())
                        .setContentText(mangbaihat.get(position).getSinger())
                        .addAction(R.drawable.ic_skip_previous_black_24dp, "Previous",prevPendingIntent)
                        .addAction(R.drawable.ic_play, "Pause",playPendingIntent)
                        .addAction(R.drawable.ic_skip_next_black_24dp, "Next",nextPendingIntent)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().
                                setMediaSession(mediaSession.getSessionToken()))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .setContentIntent(contentIntent)
                        .setOnlyAlertOnce(true)
                        .build();
                NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0,notification);
            }
        } catch (Exception e) {}
    }

    // AsyncTask
    class PlayMp3 extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            return strings[0];
        }
        protected void onPostExecute(String baihat) {
            super.onPostExecute(baihat);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }
            });
            try {
                mediaPlayer.setDataSource(baihat);
                mediaPlayer.prepare();
            }catch (IOException e){
                e.printStackTrace();
            }
            mediaPlayer.start();
            imgbtnplay.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
            TimeSong();
            UpdateTime();
        }
    }

    //Hàm được gọi khi thoát khỏi fragment này, tắt nhạc và dừng luồng lấy thời gian của bài hát
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stop = true;
//        mangbaihat.clear();
        mediaPlayer.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(getContext(), MusicService.class);
        getActivity().bindService(intent, this, BIND_AUTO_CREATE);

    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MusicService.MyBinder binder = (MusicService.MyBinder) iBinder;
        musicService = binder.getService();
        musicService.setCallBack(PlayMusicFragment.this);
        Log.e("Conected", musicService + "");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
        Log.e("Disconected", musicService + "");
    }
}