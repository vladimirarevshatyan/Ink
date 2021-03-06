package kashmirr.social.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kashmirr.social.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kashmirr.social.adapters.MusicAdapter;
import kashmirr.social.callbacks.GeneralCallback;
import kashmirr.social.decorators.DividerItemDecoration;
import kashmirr.social.interfaces.MusicClickListener;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.models.Track;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.MediaPlayerManager;
import kashmirr.social.utils.Retrofit;
import okhttp3.ResponseBody;


public class Music extends BaseActivity implements MusicClickListener {

    @BindView(R.id.musicRecycler)
    RecyclerView musicRecycler;
    @BindView(R.id.musicLoading)
    AVLoadingIndicatorView musicLoading;
    @BindView(R.id.musicInfoSheet)
    View musicInfoSheet;
    @BindView(R.id.openCloseMusicSheet)
    ImageView openMusicSheet;
    @BindView(R.id.statusText)
    TextView statusText;
    @BindView(R.id.currentlyPlayingName)
    TextView currentlyPlayingName;
    @BindView(R.id.playPauseButton)
    ImageView playPauseButton;
    @BindView(R.id.currentlyPlayingImage)
    ImageView currentlyPlayingImage;
    @BindView(R.id.musicGeneralTitle)
    TextView musicGeneralTitle;
    @BindView(R.id.bottomSheetImageProgress)
    ProgressBar bottomSheetImageProgress;
    private boolean isMusicChosen;
    private Animation slideUp;
    private Animation slideDown;
    private Animation rotate;
    private boolean shouldRotate;

    private List<Track> tracks;
    private MusicAdapter musicAdapter;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.music));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        rotate = AnimationUtils.loadAnimation(this, R.anim.rotate_animation);

        gson = new Gson();
        tracks = new ArrayList<>();
        musicAdapter = new MusicAdapter(tracks, this);
        musicAdapter.setOnMusicClickListener(this);

        checkForMusicPlaying();

        findViewById(R.id.closeMusicSheet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeMenuSheet();
            }
        });
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMusicChosen) {
                    return;
                }
                if (MediaPlayerManager.get().isSoundPlaying()) {
                    playPauseButton.setImageResource(R.drawable.play_icon);
                    MediaPlayerManager.get().pauseMusic();
                    shouldRotate = false;
                } else {
                    playPauseButton.setImageResource(R.drawable.pause);
                    MediaPlayerManager.get().playMusic(null, null);
                    shouldRotate = true;
                }
            }
        });


        openMusicSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                musicInfoSheet.setVisibility(View.VISIBLE);
                musicInfoSheet.startAnimation(slideUp);
                openMusicSheet.setVisibility(View.GONE);
            }
        });

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        musicRecycler.setLayoutManager(new LinearLayoutManager(this));
        musicRecycler.setItemAnimator(itemAnimator);
        musicRecycler.addItemDecoration(
                new DividerItemDecoration(this));
        musicRecycler.setAdapter(musicAdapter);
        getAllTracks();
    }

    private void checkForMusicPlaying() {
        if (MediaPlayerManager.get().isSoundPlaying()) {
            isMusicChosen = true;
            shouldRotate = true;
            initBottomSheet(null);
            openMusicSheet.startAnimation(rotate);
        }
    }


    @Override
    public void onMusicItemClick(int position) {
        Track track = tracks.get(position);
        initBottomSheet(track);

        if (musicInfoSheet.getVisibility() == View.GONE) {
            musicInfoSheet.setVisibility(View.VISIBLE);
            musicInfoSheet.startAnimation(slideUp);
        }

        openMusicSheet.setVisibility(View.GONE);

        isMusicChosen = true;
        shouldRotate = true;
    }

    private void initBottomSheet(@Nullable Track track) {
        bottomSheetImageProgress.setVisibility(View.VISIBLE);
        statusText.setText(getString(R.string.buffering));

        playPauseButton.setImageResource(R.drawable.pause);

        String title;
        String image;

        if (track == null) {
            statusText.setText(getString(R.string.currentlyPlaying));
            title = MediaPlayerManager.get().getTitle();
            image = MediaPlayerManager.get().getLastImage();
        } else {
            title = track.mTitle;
            image = track.mArtworkURL;
        }


        if (image != null && !image.equals("null")) {
            ImageLoader.loadImage(getApplicationContext(), true, false, image,
                    0, R.drawable.user_image_placeholder, currentlyPlayingImage, new ImageLoader.ImageLoadedCallback() {
                        @Override
                        public void onImageLoaded(Object result, Exception e) {
                            bottomSheetImageProgress.setVisibility(View.GONE);
                            if (e != null) {
                                currentlyPlayingImage.setBackground(null);
                                currentlyPlayingImage.setImageResource(R.drawable.gradient_no_image);
                            }
                        }
                    });
        } else {
            bottomSheetImageProgress.setVisibility(View.GONE);
            currentlyPlayingImage.setBackground(null);
            currentlyPlayingImage.setBackground(ContextCompat.getDrawable(this, R.drawable.gradient_no_image));
        }
        currentlyPlayingName.setText(title);

        if (track == null) {
            return;
        }
        MediaPlayerManager.get().playMusic(track, new GeneralCallback() {
            @Override
            public void onSuccess(Object o) {
                statusText.setText(getString(R.string.currentlyPlaying));
            }

            @Override
            public void onFailure(Object o) {
                statusText.setText(getString(R.string.errorPlaying));
            }
        });
    }

    private void getAllTracks() {
        clearTrackArray();

        makeRequest(Retrofit.getInstance().getMusicCloudInterface().getAllTracks(), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    Track[] tracks = gson.fromJson(((ResponseBody) result).string(), Track[].class);
                    loadTracks(tracks);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {

            }
        });
    }

    private void clearTrackArray() {
        if (tracks != null) {
            tracks.clear();
        }
        musicAdapter.notifyDataSetChanged();
    }


    private void loadTracks(Track[] trackList) {
        for (int i = 0; i < trackList.length; i++) {
            Track eachTrack = trackList[i];
            tracks.add(eachTrack);
            musicAdapter.notifyDataSetChanged();
        }

        musicLoading.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.music_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            openSearchDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    public void closeMenuSheet() {
        openMusicSheet.setVisibility(View.VISIBLE);
        musicInfoSheet.startAnimation(slideDown);
        slideDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                musicInfoSheet.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        if (shouldRotate) {
            openMusicSheet.startAnimation(rotate);
        }
    }

    private void openSearchDialog() {
        final Dialog dialog = new Dialog(Music.this);
        dialog.setContentView(R.layout.search_dialog_view);
        final AutoCompleteTextView searchField = (AutoCompleteTextView) dialog.findViewById(R.id.searchField);
        Button searchButton = (Button) dialog.findViewById(R.id.searchButton);
        RelativeLayout searchRootLayout = (RelativeLayout) dialog.findViewById(R.id.searchRootLayout);

        ImageView closeSearch = (ImageView) dialog.findViewById(R.id.closeSearch);
        closeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchField.getText().toString().trim();
                if (searchText.isEmpty()) {
                    Toast.makeText(Music.this, getString(R.string.pleaseInputSearch), Toast.LENGTH_SHORT).show();
                } else {
                    dialog.dismiss();
                    musicGeneralTitle.setText(getString(R.string.musicTitleHint));
                    musicLoading.setVisibility(View.VISIBLE);
                    clearTrackArray();
                    makeRequest(Retrofit.getInstance().getMusicCloudInterface().searchSong(searchText), musicLoading, new RequestCallback() {
                        @Override
                        public void onRequestSuccess(Object result) {
                            try {
                                String responseBody = ((ResponseBody) result).string();
                                Track[] tracks = gson.fromJson(responseBody, Track[].class);
                                loadTracks(tracks);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onRequestFailed(Object[] result) {

                        }
                    });
                }
            }
        });
        dialog.show();
    }
}
