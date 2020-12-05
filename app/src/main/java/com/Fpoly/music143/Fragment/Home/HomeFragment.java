package com.Fpoly.music143.Fragment.Home;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.Fpoly.music143.Activity.MainActivity;
import com.Fpoly.music143.Database.DAO.AlbumDAO;
import com.Fpoly.music143.Database.DAO.UserDAO;
import com.Fpoly.music143.Database.Services.CallBack.UserCallBack;
import com.Fpoly.music143.Fragment.Home.Adapter.AlbumAdapter;
import com.Fpoly.music143.Fragment.Home.Adapter.RankAdapter;
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
    ViewFlipper viewFlipper;
    UserInfor userInfor = UserInfor.getInstance();

    private Animation.AnimationListener animationListener;
    private static final int Swipe_Minimum_Distance = 10;
    private static final int Swipe_Threshold_Velocity = 18;

    @SuppressWarnings("deprecation")
    private final GestureDetector detector = new GestureDetector(new SwipeGesture());

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rcvalbum = root.findViewById(R.id.rcvalbum);
        rcvsuggest = root.findViewById(R.id.rcvsuggest);
        rcvtop = root.findViewById(R.id.rcvtop);
        viewFlipper = root.findViewById(R.id.viewflipper);
//        Log.d("devH", userInfor.getID());
        getUser(userInfor.getID());
        ActionViewFlipper();
        return root;
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
                for (int i = 0; i < songNew.size(); i++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    Picasso.get().load(songNew.get(i).getImage()).into(imageView);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    viewFlipper.addView(imageView);
                }
            }
        });
        dialog.dismiss();
    }

    private void ChangeFragment(int position){
        Bundle bundle = new Bundle();
        bundle.putParcelable("Songs",songNew.get(position));
        bundle.putInt("fragment",4);
        Fragment fragment = new PlayMusicFragment();
        fragment.setArguments(bundle);
        FragmentManager fragmentManager = this.getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_out_left,R.anim.slide_in_right);
        transaction.replace(R.id.nav_host_fragment, fragment);
        transaction.commit();

    }

    private void ActionViewFlipper() {
        viewFlipper.setAutoStart(true);
        //Flip Interval- time after which image changes.
        viewFlipper.setFlipInterval(5000);

        //Flipping with animation
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.left_in));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.left_out));

        // controlling animation

        viewFlipper.getInAnimation().setAnimationListener(animationListener);
        //Start Flipping
        viewFlipper.startFlipping();

        //On Touch Listener for SwipeGesture
        viewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);
                viewFlipper.setFlipInterval(5000);
                return true;
            }
        });

        //Animation listener
        animationListener = new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                //When Animation Starts

            }

            public void onAnimationRepeat(Animation animation) {
                //On Repeatation of animation

            }

            public void onAnimationEnd(Animation animation) {
                //After Animation end

            }
        };



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

    //Class for Swipe Gesture
    class SwipeGesture extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            try {

                //right to left swipe
                if (me1.getX() - me2.getX() > Swipe_Minimum_Distance && Math.abs(velocityX) > Swipe_Threshold_Velocity) {

                    //stop Flipping
                    viewFlipper.stopFlipping();
                    viewFlipper.getInAnimation().setAnimationListener(animationListener);
                    viewFlipper.showNext();

                    viewFlipper.setAutoStart(true);
                    viewFlipper.setFlipInterval(2000);
                    viewFlipper.startFlipping();

                    return true;
                }

                //left to right swipe

                else if (me2.getX() - me1.getX() > Swipe_Minimum_Distance && Math.abs(velocityX) > Swipe_Threshold_Velocity) {

                    viewFlipper.stopFlipping();
                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.right_out));

                    // controlling animation
                    viewFlipper.getInAnimation().setAnimationListener(animationListener);
                    viewFlipper.showPrevious();

                    viewFlipper.setAutoStart(true);
                    viewFlipper.setFlipInterval(2000);
                    viewFlipper.startFlipping();

                    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_in));
                    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.left_out));

                    return true;
                }

                else {
                    viewFlipper.getDisplayedChild() ;
                    Log.d("viewFlipper",viewFlipper.getDisplayedChild() + "") ;
                    ChangeFragment(viewFlipper.getDisplayedChild()) ;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            viewFlipper.getDisplayedChild() ;
            Log.d("viewFlipper",viewFlipper.getDisplayedChild() + "") ;
            ChangeFragment(viewFlipper.getDisplayedChild()) ;
            return super.onSingleTapConfirmed(e);
        }
    }
}