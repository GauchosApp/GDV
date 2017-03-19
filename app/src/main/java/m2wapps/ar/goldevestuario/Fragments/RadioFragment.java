package m2wapps.ar.goldevestuario.Fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;

import m2wapps.ar.goldevestuario.Services.MusicService;
import m2wapps.ar.goldevestuario.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RadioFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.

 * create an instance of this fragment.
 */
public class RadioFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

  //  private ProgressBar playSeekBar;

    private Button buttonPlay;

    private ProgressBar loading;

    private Button buttonStopPlay;

    private MediaPlayer player;

    private MusicService musicService;

    private static boolean musicBound=false;

    private Intent playIntent;

    private  AudioPlayerBroadcastReceiver broadcastReceiver;

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service

            musicService = binder.getService();

            //pass list
            //   musicSrv.setList(songList);
          //  musicSrv.setMain(ArtistsActivity.this);

            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    public RadioFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment RadioFragment.
     */
    // TODO: Rename and change types and number of parameters
 /*   public static RadioFragment newInstance(String param1, String param2) {
        RadioFragment fragment = new RadioFragment(packageName);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        broadcastReceiver = new AudioPlayerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        // set the custom action
        intentFilter.addAction("playpause");
        intentFilter.addAction("close");
        // register the receiver
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        musicService = new MusicService();

    }

    private void initializeUIElements(View view) {

     //   playSeekBar = (ProgressBar) view.findViewById(R.id.progressBar1);
     //   playSeekBar.setMax(100);
    //    playSeekBar.setVisibility(View.INVISIBLE);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        buttonPlay = (Button) view.findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);
        buttonPlay.setBackgroundResource(R.drawable.abc);
        buttonStopPlay = (Button) view.findViewById(R.id.buttonStopPlay);
        buttonStopPlay.setEnabled(false);
        buttonStopPlay.setOnClickListener(this);
        buttonStopPlay.setBackgroundResource(R.drawable.abc2);


         /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                buttonPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary,null));
                buttonStopPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
            }else {
                buttonPlay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                buttonStopPlay.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }*/


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_radio, container, false);

        initializeUIElements(view);

        initializeMediaPlayer();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == buttonPlay) {
            startPlaying();
        } else if (v == buttonStopPlay) {
            stopPlaying();
        }
    }
    private void startPlaying() {
        loading.setVisibility(View.VISIBLE);
        if(!player.isPlaying()) {
            buttonStopPlay.setEnabled(true);
            buttonPlay.setEnabled(false);

            //   playSeekBar.setVisibility(View.VISIBLE);

            player.prepareAsync();

            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                public void onPrepared(MediaPlayer mp) {
                    loading.setVisibility(View.GONE);
                    player.start();
                }
            });
            if(playIntent != null) {
                getActivity().stopService(playIntent);
            }
            playIntent = null;
            playIntent = new Intent(getActivity(), MusicService.class);
            playIntent.putExtra("ispng", false);
            getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }
    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }

        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
        if (musicConnection != null) {
            getActivity().unbindService(musicConnection);
        }
        if(playIntent != null) {
            getActivity().stopService(playIntent);
        }
        playIntent = null;
        playIntent = new Intent(getActivity(), MusicService.class);
        playIntent.putExtra("ispng", true);
        getActivity().bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        getActivity().startService(playIntent);
    //    playSeekBar.setVisibility(View.INVISIBLE);
    }
    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            player.setDataSource("http://207.7.80.14:9992/");
        } catch (IllegalArgumentException | IllegalStateException | IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {
           //     playSeekBar.setSecondaryProgress(percent);
                Log.i("Buffering", "" + percent);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player.isPlaying()) {
            player.stop();
        }
        if(playIntent != null) {
            getActivity().stopService(playIntent);
        }
        if (musicConnection != null) {
            if(!buttonPlay.isEnabled()) {
                getActivity().unbindService(musicConnection);
            }
        }
        if(broadcastReceiver != null){
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class AudioPlayerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
                    if (action.equalsIgnoreCase("playpause")) {
                        // do your stuff to play action;
                        if (player.isPlaying()) {
                            stopPlaying();

                        } else {
                            startPlaying();
                        }
                    }
            if(action.equalsIgnoreCase("close")){
                // do your stuff to play action;
                getActivity().finish();
            }
        }
    }
}
