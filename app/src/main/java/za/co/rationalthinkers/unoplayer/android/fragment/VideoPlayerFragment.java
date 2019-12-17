package za.co.rationalthinkers.unoplayer.android.fragment;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSource;

import za.co.rationalthinkers.unoplayer.android.R;
import za.co.rationalthinkers.unoplayer.android.config.Constants;
import za.co.rationalthinkers.unoplayer.android.model.Video;

public class VideoPlayerFragment extends Fragment
        implements ImageView.OnClickListener,
        Player.EventListener {

    private OnVideoPlayerActionListener mListener;

    private Context mContext;
    private SimpleExoPlayer mVideoPlayer;
    private int mResumeWindow;
    private long mResumePosition;

    private Video video;

    //UI references
    PlayerView mVideoPlayerView;
    ImageView mRotateView;

    public interface OnVideoPlayerActionListener {
        void onViewPlaylist();
        void onRotateScreen();
        void onVideoPlayerExit();
    }

    public VideoPlayerFragment() {
        // Required empty public constructor
    }

    public static VideoPlayerFragment newInstance(Video video) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();

        Bundle data = new Bundle();
        data.putParcelable(Constants.ARG_VIDEO, video);
        fragment.setArguments(data);

        return fragment;
    }

    public static VideoPlayerFragment newInstance(String videoPath) {
        VideoPlayerFragment fragment = new VideoPlayerFragment();

        Bundle data = new Bundle();
        data.putString(Constants.ARG_VIDEO_PATH, videoPath);
        fragment.setArguments(data);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();

        if (getArguments() != null) {
            video = getArguments().getParcelable(Constants.ARG_VIDEO);
        }
        else{
            //Exit video player
            if(mListener != null){
                mListener.onVideoPlayerExit();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_video_player, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI(view);
        setUpListeners();
        setUpVideoDetails();
        setUpVideoPlayer();

        clearResumePosition();
        playVideo();
    }

    private void initUI(View view){
        mVideoPlayerView = view.findViewById(R.id.video_player);
        mRotateView = view.findViewById(R.id.video_player_rotate);
    }

    private void setUpListeners(){
        mRotateView.setOnClickListener(this);
    }

    private void setUpVideoDetails(){

    }

    private void setUpVideoPlayer(){
        RenderersFactory mRenderersFactory = new DefaultRenderersFactory(mContext);
        TrackSelector mTrackSelector = new DefaultTrackSelector();
        LoadControl mLoadControl = new DefaultLoadControl();

        mVideoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, mRenderersFactory, mTrackSelector, mLoadControl);

        mVideoPlayerView.setUseController(true);
        mVideoPlayerView.requestFocus();
        mVideoPlayerView.setPlayer(mVideoPlayer);
    }

    private void playVideo(){
        String path = video.getPath();

        MediaSource videoSource = buildMediaSource(Uri.parse(path));
        boolean hasResumePosition = mResumeWindow != C.INDEX_UNSET;

        mVideoPlayer.prepare(videoSource, !hasResumePosition, false);
        if(hasResumePosition){
            mVideoPlayer.seekTo(mResumeWindow, mResumePosition);
        }
        mVideoPlayer.addListener(this);

        mVideoPlayer.setPlayWhenReady(true);
    }

    private MediaSource buildMediaSource(Uri uri){
        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        }
        catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };

        return new ExtractorMediaSource.Factory(factory).createMediaSource(uri);
    }

    private void setResumePosition(){
        mResumeWindow = mVideoPlayer.getCurrentWindowIndex();
        if(mVideoPlayer.isCurrentWindowSeekable()){
            mResumePosition = Math.max(0, mVideoPlayer.getCurrentPosition());
        }
        else{
            mResumePosition = C.TIME_UNSET;
        }
    }

    private void clearResumePosition(){
        mResumeWindow = C.INDEX_UNSET;
        mResumePosition = C.TIME_UNSET;
    }

    private void resumeVideo() {
        try {
            if (mVideoPlayer == null) {
                setUpVideoPlayer();
            }

            playVideo();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (mVideoPlayer != null) {
            setResumePosition();
            mVideoPlayer.stop();
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    /**
     * Called when the player starts or stops loading the source.
     */
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Called when the current playback parameters change.
     */
    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    /**
     * Called when an error occurs.
     */
    @Override
    public void onPlayerError(ExoPlaybackException error) {
        clearResumePosition();
    }

    /**
     * Called when the value returned from either Player.getPlayWhenReady()
     * or Player.getPlaybackState() changes.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        switch(playbackState) {
            case Player.STATE_BUFFERING:
                break;

            case Player.STATE_ENDED:
                if(mListener != null){
                    mListener.onVideoPlayerExit();
                }
                break;

            case Player.STATE_IDLE:
                break;

            case Player.STATE_READY:
                break;

            default:
                break;
        }

    }

    /**
     * Called when a position discontinuity occurs without a change to the timeline.
     */
    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    /**
     * Called when the value of Player.getRepeatMode() changes
     */
    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    /**
     * Called when all pending seek requests have been processed by the player.
     */
    @Override
    public void onSeekProcessed() {

    }

    /**
     * Called when the value of Player.getShuffleModeEnabled() changes.
     */
    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    /**
     * Called when the timeline and/or manifest has been refreshed.
     */
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    /**
     * Called when the available or selected tracks change.
     */
    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.video_player_rotate:
                if(mListener != null){
                    mListener.onRotateScreen();
                }
                break;

        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnVideoPlayerActionListener) {
            mListener = (OnVideoPlayerActionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + " must implement OnVideoPlayerActionListener");
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

        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null && activity.getSupportActionBar() != null){
            activity.getSupportActionBar().hide();
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); //remove status and navigation bar
        }

        if(mVideoPlayer == null){
            resumeVideo();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if(activity != null && activity.getSupportActionBar() != null){
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS); //Show status and navigation bar
            activity.getSupportActionBar().show();
        }
    }
}
