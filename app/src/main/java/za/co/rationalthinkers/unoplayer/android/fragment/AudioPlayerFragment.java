package za.co.rationalthinkers.unoplayer.android.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.RepeatMode;
import za.co.rationalthinkers.unoplayer.android.config.ShuffleMode;
import za.co.rationalthinkers.unoplayer.android.interfaces.MusicPlayerServiceEventListener;
import za.co.rationalthinkers.unoplayer.android.model.Song;
import za.co.rationalthinkers.unoplayer.android.util.MusicPlayerRemote;
import za.co.rationalthinkers.unoplayer.android.util.Utils;

public class AudioPlayerFragment extends Fragment
        implements MusicPlayerServiceEventListener,
        ImageView.OnClickListener,
        SeekBar.OnSeekBarChangeListener{

    private OnAudioPlayerActionListener mListener;

    private Context mContext;

    private boolean fragmentPaused = false;
    private boolean duetoplaypause = false;

    private int overflowcounter = 0;
    private int lastPlaybackControlsColor;
    private int lastDisabledPlaybackControlsColor;

    //UI references
    ImageView mExitView;
    TextView mTitleView;
    TextView mDetailsView;
    ImageView mMenuView;
    ImageView mImageView;
    ImageView mRepeatView;
    ImageView mPreviousView;
    ImageView mControllerView;
    ImageView mNextView;
    ImageView mShuffleView;
    AVLoadingIndicatorView mLoadingIndicatorView;
    TextView mCurrentTimeView;
    TextView mTotalTimeView;
    SeekBar mSeekBar;

    public interface OnAudioPlayerActionListener {
        void onAudioPlayerExit();
        void onViewPlaylist();
    }

    public AudioPlayerFragment() {
        // Required empty public constructor
    }

    public static AudioPlayerFragment newInstance() {
        return new AudioPlayerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_audio_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lastPlaybackControlsColor = getResources().getColor(R.color.colorAccent);
        lastDisabledPlaybackControlsColor = getResources().getColor(R.color.gray_dark);

        initUI(view);
        setUpListeners();
    }

    private void initUI(View view){
        mExitView = view.findViewById(R.id.audio_player_exit);
        mTitleView = view.findViewById(R.id.audio_player_title);
        mDetailsView = view.findViewById(R.id.audio_player_details);
        mMenuView = view.findViewById(R.id.audio_player_menu);
        mImageView = view.findViewById(R.id.audio_player_image);
        mRepeatView = view.findViewById(R.id.audio_player_repeat);
        mPreviousView = view.findViewById(R.id.audio_player_previous);
        mControllerView = view.findViewById(R.id.audio_player_controller);
        mNextView = view.findViewById(R.id.audio_player_next);
        mShuffleView = view.findViewById(R.id.audio_player_shuffle);
        mLoadingIndicatorView = view.findViewById(R.id.audio_player_buffer);
        mCurrentTimeView = view.findViewById(R.id.audio_player_current_time);
        mTotalTimeView = view.findViewById(R.id.audio_player_total_time);
        mSeekBar = view.findViewById(R.id.audio_player_progress);
    }

    private void setUpListeners(){
        mExitView.setOnClickListener(this);
        mMenuView.setOnClickListener(this);
        mRepeatView.setOnClickListener(this);
        mPreviousView.setOnClickListener(this);
        mNextView.setOnClickListener(this);
        mShuffleView.setOnClickListener(this);
        mControllerView.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    private void setUpSongDetails() {
        updateSongDetails();
        updateShuffleState();
        updateRepeatState();
    }

    public void updateSongDetails() {
        Song song = MusicPlayerRemote.getCurrentSong();

        if(song == null){
            noSongLoaded();

            return;
        }

        //do not reload image if it was a play/pause change
        if (!duetoplaypause) {
            if (mImageView != null) {
                //String albumArt = MusicPlayer.getCurrentAlbumId().toString();
                //TODO: load image with glide
            }

            if (mTitleView != null) {
                String title = song.getTitle();
                mTitleView.setText(title);
            }

            if (mDetailsView != null) {
                String artistName = TextUtils.isEmpty(song.getArtistName()) ? "Unknown Artist" : song.getArtistName();
                mDetailsView.setText(artistName);
            }

        }
        duetoplaypause = false;

        if (mControllerView != null){
            updatePlayPauseButton();
        }

        if (mCurrentTimeView != null && getActivity() != null){
            long duration = song.getDuration();

            mCurrentTimeView.setText(Utils.makeShortTimeString(getActivity(), duration/1000));
        }

        if (mSeekBar != null) {
            long duration = song.getDuration();
            mSeekBar.setMax((int) duration);

            if (mUpdateProgress != null) {
                mSeekBar.removeCallbacks(mUpdateProgress);
            }

            mSeekBar.postDelayed(mUpdateProgress, 10);
        }

        if(mPreviousView != null){
            /*if(MusicPlayerRemote.position() == 0){
                mPreviousView.setEnabled(false);
            }
            else{
                mPreviousView.setEnabled(true);
            }*/
        }

        if(mNextView != null){
            /*if((MusicPlayerRemote.getPlayingQueue().size() - 1) >= MusicPlayerRemote.position()){
                mNextView.setEnabled(false);
            }
            else{
                mNextView.setEnabled(true);
            }*/
        }

    }

    private void noSongLoaded(){

        if (mTitleView != null) {
            String title = "Error!";
            mTitleView.setText(title);
        }

        if (mDetailsView != null) {
            String artistName = "No song loaded";
            mDetailsView.setText(artistName);
        }

        if (mControllerView != null){
            mControllerView.setEnabled(false);
        }

        if(mPreviousView != null){
            mPreviousView.setEnabled(false);
        }

        if(mNextView != null){
            mNextView.setEnabled(false);
        }

        if (mCurrentTimeView != null && getActivity() != null){
            long duration = 0;

            mCurrentTimeView.setText(Utils.makeShortTimeString(getActivity(), duration/1000));
        }

        if (mSeekBar != null) {
            mSeekBar.setMax((int) 0);
            if (mUpdateProgress != null) {
                mSeekBar.removeCallbacks(mUpdateProgress);
            }
        }

    }

    public void updatePlayPauseButton() {
        if (MusicPlayerRemote.isPlaying()) {
            mControllerView.setImageResource(R.drawable.ic_pause_white_24dp);
        }
        else {
            mControllerView.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    private void updateRepeatState() {
        int mode = MusicPlayerRemote.getRepeatMode();
        switch (mode) {
            case RepeatMode.REPEAT_OFF:
                mRepeatView.setImageResource(R.drawable.ic_repeat_white_24dp);
                mRepeatView.setColorFilter(lastDisabledPlaybackControlsColor);
                break;

            case RepeatMode.REPEAT_ALL:
                mRepeatView.setImageResource(R.drawable.ic_repeat_white_24dp);
                mRepeatView.setColorFilter(lastPlaybackControlsColor);
                break;

            case RepeatMode.REPEAT_CURRENT:
                mRepeatView.setImageResource(R.drawable.ic_repeat_one_white_24dp);
                mRepeatView.setColorFilter(lastPlaybackControlsColor);
                break;
        }
    }

    private void updateShuffleState() {
        int mode = MusicPlayerRemote.getShuffleMode();
        switch (mode) {
            case ShuffleMode.SHUFFLE_ON:
                mShuffleView.setColorFilter(lastPlaybackControlsColor);
                break;

            default:
                mShuffleView.setColorFilter(lastDisabledPlaybackControlsColor);
                break;
        }
    }

    //@Override
    public void onUpdateProgressViews(int progress, int total) {
        if (mSeekBar != null) {
            mSeekBar.setMax(total);
            mSeekBar.setProgress(progress);
        }

        if(mCurrentTimeView != null && getActivity() != null){
            mCurrentTimeView.setText(Utils.makeShortTimeString(getActivity(), progress/1000));
        }

        if(mTotalTimeView != null && getActivity() != null){
            mTotalTimeView.setText(Utils.makeShortTimeString(getActivity(), total/1000));
        }

        //Song has finished playing, close player
        if(progress >= total){
            if(!fragmentPaused && mListener != null){
                mListener.onAudioPlayerExit();
            }
        }

    }

    @Override
    public void onServiceConnected() {
        updateSongDetails();
        updateRepeatState();
        updateShuffleState();
    }

    @Override
    public void onServiceDisconnected() {
        if(!fragmentPaused && mListener != null){
            mListener.onAudioPlayerExit();
        }
    }

    @Override
    public void onQueueChanged() {

    }

    @Override
    public void onPlayingMetaChanged() {
        updateSongDetails();
    }

    @Override
    public void onPlayStateChanged() {
        //updatePlayPauseButton();
        updateSongDetails();
    }

    @Override
    public void onRepeatModeChanged() {
        updateRepeatState();
    }

    @Override
    public void onShuffleModeChanged() {
        updateShuffleState();
    }

    @Override
    public void onMediaStoreChanged() {

    }

    @Override
    public void onClick(View v) {
        Handler handler = new Handler();

        switch (v.getId()){

            case R.id.audio_player_exit:
                if(mListener != null){
                    mListener.onAudioPlayerExit();
                }
                break;

            case R.id.audio_player_menu:

                break;

            case R.id.audio_player_controller:
                duetoplaypause = true;

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.playOrPause();
                        updatePlayPauseButton();
                    }
                }, 200);
                break;

            case R.id.audio_player_next:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.playNextSong();
                    }
                }, 200);
                break;

            case R.id.audio_player_previous:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.playPreviousSong();
                    }
                }, 200);
                break;

            case R.id.audio_player_shuffle:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.cycleShuffle();
                        updateShuffleState();
                    }
                }, 200);
                break;

            case R.id.audio_player_repeat:
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayerRemote.cycleRepeat();
                        updateRepeatState();
                    }
                }, 200);
                break;

        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            MusicPlayerRemote.seek((long) progress);
            onUpdateProgressViews(MusicPlayerRemote.songProgressMillis(), MusicPlayerRemote.songDurationMillis());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnAudioPlayerActionListener) {
            mListener = (OnAudioPlayerActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnAudioPlayerActionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        fragmentPaused = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        fragmentPaused = false;

        setUpSongDetails();
    }

    //seekbar
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {
            int progressMillis = MusicPlayerRemote.songProgressMillis();
            int totalMillis = MusicPlayerRemote.songDurationMillis();

            onUpdateProgressViews(progressMillis, totalMillis);

            overflowcounter--;
            int delay = 250; //not sure why this delay was so high before

            if (overflowcounter < 0 && !fragmentPaused) {
                overflowcounter++;
                mSeekBar.postDelayed(mUpdateProgress, delay); //delay
            }
        }
    };

}
