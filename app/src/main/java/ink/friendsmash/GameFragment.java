/**
 * Copyright (c) 2014-present, Facebook, Inc. All rights reserved.
 * <p/>
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to use,
 * copy, modify, and distribute this software in source code or binary form for use
 * in connection with the web services and APIs provided by Facebook.
 * <p/>
 * As with any software that integrates with the Facebook platform, your use of
 * this software is subject to the Facebook Developer Principles and Policies
 * [http://developers.facebook.com/policy/]. This copyright notice shall be
 * included in all copies or substantial portions of the software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ink.friendsmash;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.ink.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Fragment shown once a user starts playing a game
 */
public class GameFragment extends Fragment {


    private static final int CELEB_FREQUENCY = 5;
    private static final int COIN_FREQUENCY = 8;

    private static final String TAG = GameFragment.class.getSimpleName();

    private FrameLayout gameFrame;
    private FrameLayout progressContainer;
    private TextView smashPlayerNameTextView;
    private TextView scoreTextView;
    private LinearLayout livesContainer;
    private LinearLayout bombsContainer;
    private ImageView bombButton;

    private int iconWidth;

    private int screenWidth;
    private int screenHeight;

    // Handler for putting messages on Main UI thread from background threads periodically
    private Handler timerHandler;

    // Handler for putting messages on Main UI thread from background thread after fetching images
    private Handler uiHandler;

    // Runnable task used to produce images to fly across the screen
    private Runnable fireImageTask = null;

    private boolean imagesStartedFiring = false;
    private boolean firstImagePendingFiring = false;
    private boolean firstImageFired = false;

    private int friendToSmashIndex = -1;
    private boolean isSocialMode = true;

    private String friendToSmashIDProvided = null;
    private String friendToSmashFirstName = null;
    private Bitmap friendToSmashBitmap;

    private int score = 0;
    private int lives = 3;
    private int bombsRemaining = FriendSmashHelper.get().NUM_BOMBS_ALLOWED_IN_GAME;
    private int bombsUsed = 0;
    private int coinsCollected = 0;

    private ArrayList<UserImageView> userImageViews = new ArrayList<UserImageView>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        timerHandler = new Handler();
        uiHandler = new Handler();

        // Make sure there are non-zero friends.
        JSONArray friends = FriendSmashHelper.get().getFriends();
        if (friends != null && friends.length() > 0) {
            isSocialMode = true;
            friendToSmashIndex = getRandomFriendIndex();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game, parent, false);

        gameFrame = (FrameLayout) v.findViewById(R.id.gameFrame);
        progressContainer = (FrameLayout) v.findViewById(R.id.progressContainer);
        smashPlayerNameTextView = (TextView) v.findViewById(R.id.smashPlayerNameTextView);
        scoreTextView = (TextView) v.findViewById(R.id.scoreTextView);
        livesContainer = (LinearLayout) v.findViewById(R.id.livesContainer);
        bombsContainer = (LinearLayout) v.findViewById(R.id.bombsContainer);
        bombButton = (ImageView) v.findViewById(R.id.bombButton);
        bombButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBombButtonTouched();
                return false;
            }
        });

        progressContainer.setVisibility(View.INVISIBLE);

        iconWidth = getResources().getDimensionPixelSize(R.dimen.icon_width);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        fireImageTask = new Runnable() {
            public void run() {
                spawnImage(false);
            }
        };

        setScore(getScore());
        setLives(getLives());
        setBombsRemaining(getBombsRemaining());

        return v;
    }

    private void onBombButtonTouched() {
        if (getBombsRemaining() > 0) {
            hideAllUserImageViews();
            markAllUserImageViewsAsVoid();

            bombsUsed++;

            setBombsRemaining(getBombsRemaining() - 1);
        } else {
            setBombsRemaining(0);
        }
    }

    private void setSmashPlayerNameTextView() {
        if (isSocialMode) {
            if (friendToSmashFirstName == null) {
                JSONObject friend = FriendSmashHelper.get().getFriend(friendToSmashIndex);
                friendToSmashFirstName = friend.optString("name");
            }
            smashPlayerNameTextView.setText("Smash " + friendToSmashFirstName + " !");
        }
    }

    private int getRandomFriendIndex() {
        Random randomGenerator = new Random(System.currentTimeMillis());
        int friendIndex = randomGenerator.nextInt(FriendSmashHelper.get().getFriends().length());
        return friendIndex;
    }


    private void setFriendImageAndFire(UserImageView imageView, Bitmap friendBitmap, boolean extraImage) {
        imageView.setImageBitmap(friendBitmap);
        if (extraImage) {
            imageView.setExtraPoints(1);
        }
        fireImage(imageView, extraImage);
    }


    private void setCoinImageAndFire(UserImageView imageView, boolean extraImage) {
        imageView.setImageResource(R.drawable.coins_icon);
        fireImage(imageView, extraImage);
    }

    private void fireImage(final UserImageView imageView, boolean extraImage) {
        imageView.setupAndStartAnimations(iconWidth, iconWidth, screenWidth, screenHeight, new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!imageView.isWrongImageSmashed()) {
                    if (imageView.getVisibility() == View.VISIBLE && imageView.shouldSmash() && !imageView.isVoid() && !imageView.isCoin()) {
                        // Image is still visible, so user didn't smash it and they should have done (and it isn't void), so decrement the lives by one
                        setLives(getLives() - 1);
                    }

                    hideAndRemove(imageView);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });

        if (!extraImage) {
            fireAnotherImage();
        }

        firstImagePendingFiring = false;
    }

    private void hideAndRemove(UserImageView userImageView) {
        if (userImageView.getVisibility() == View.VISIBLE) {
            userImageView.setVisibility(View.GONE);
        }

        getGameFrame().removeView(userImageView);

        getUserImageViews().remove(userImageView);
    }

    private void fireAnotherImage() {
        if (fireImageTask != null) {
            timerHandler.postDelayed(fireImageTask, 700);
        }
    }

    private void fireFirstImage() {
        if (isSocialMode) {
            Bundle bundle = getActivity().getIntent().getExtras();

            String requestID = null;
            String userID = null;
            int numBombsRemaining = 0;
            userID = FriendSmashHelper.get().getFriend(friendToSmashIndex).optString("id");
            if (bundle != null) {
                requestID = bundle.getString("request_id");
                userID = bundle.getString("user_id");
                numBombsRemaining = bundle.getInt("num_bombs") <= FriendSmashHelper.get().NUM_BOMBS_ALLOWED_IN_GAME ?
                        bundle.getInt("num_bombs") : FriendSmashHelper.get().NUM_BOMBS_ALLOWED_IN_GAME;
                setBombsRemaining(numBombsRemaining);
            }

            if (requestID != null && friendToSmashIDProvided == null) {
                progressContainer.setVisibility(View.VISIBLE);
                fireFirstImageWithRequestID(requestID);
            } else if (userID != null && friendToSmashIDProvided == null) {
                progressContainer.setVisibility(View.VISIBLE);
                fireFirstImageWithUserID(userID);
            } else {
                progressContainer.setVisibility(View.INVISIBLE);
                setSmashPlayerNameTextView();

                spawnImage(false);
            }
        } else {
            progressContainer.setVisibility(View.INVISIBLE);
            setSmashPlayerNameTextView();

            spawnImage(false);
        }
    }

    private void fireFirstImageWithRequestID(String requestID) {

    }

    private void fireFirstImageWithUserID(String userID) {
        friendToSmashIDProvided = userID;
    }


    private void spawnImage(final boolean extraImage) {
        Random randomGenerator = new Random(System.currentTimeMillis());

        boolean shouldSmash = true;
        boolean isCoin = false;
        if (firstImageFired) {
            if (randomGenerator.nextInt(CELEB_FREQUENCY) == 0 && firstImageFired) {
                shouldSmash = false;
            } else if (randomGenerator.nextInt(COIN_FREQUENCY) == 0 && firstImageFired) {
                isCoin = true;
            }
        } else {
            shouldSmash = true;
            firstImageFired = true;
        }

        final UserImageView userImageView = (new UserImageView(getActivity(), shouldSmash, isCoin));
        userImageView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (userImageView.shouldSmash()) {
                    if (userImageView.isCoin()) {
                        coinsCollected++;
                    } else {
                        setScore(getScore() + 1 + userImageView.getExtraPoints());
                    }

                    v.setVisibility(View.GONE);

                    getUserImageViews().remove(v);
                } else {
                    wrongImageSmashed(userImageView);
                }
                return false;
            }
        });
        userImageView.setLayoutParams(new LinearLayout.LayoutParams(iconWidth, iconWidth));
        gameFrame.addView(userImageView);
        userImageViews.add(userImageView);

        if (userImageView.shouldSmash()) {
            if (userImageView.isCoin()) {
                setCoinImageAndFire(userImageView, extraImage);
            } else {
                if (isSocialMode) {
                    if (friendToSmashBitmap != null) {
                        setFriendImageAndFire(userImageView, friendToSmashBitmap, extraImage);
                    } else {
                        progressContainer.setVisibility(View.VISIBLE);

                        final String friendToSmashID = friendToSmashIDProvided != null ? friendToSmashIDProvided :
                                FriendSmashHelper.get().getFriend(friendToSmashIndex).optString("id");

                        fetchFriendBitmapAndFireImages(userImageView, friendToSmashID, extraImage,
                                FriendSmashHelper.get().getFriend(friendToSmashIndex).optString("image"));
                    }
                }
            }
        }
    }

    private void wrongImageSmashed(final UserImageView userImageView) {
        userImageView.setWrongImageSmashed(true);
        userImageView.stopMovementAnimations();
        hideAllUserImageViewsExcept(userImageView);

        userImageView.scaleUp(new AnimatorListener() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                userImageView.stopRotationAnimation();
                setLives(0);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });

        getGameFrame().bringChildToFront(userImageView);
    }

    private void fetchFriendBitmapAndFireImages(final UserImageView userImageView,
                                                final String friendToSmashID,
                                                final boolean extraImage,
                                                final String imageURLString) {
        AsyncTask.execute(new Runnable() {
            public void run() {
                try {
                    URL imageURL = new URL(imageURLString);
                    friendToSmashBitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                } catch (Exception e) {
                    closeAndShowError(getResources().getString(R.string.error_fetching_friend_bitmap));
                }
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        progressContainer.setVisibility(View.INVISIBLE);

                        if (friendToSmashBitmap != null) {
                            setFriendImageAndFire(userImageView, friendToSmashBitmap, extraImage);

                            // Also set the lastFriendSmashedID and lastFriendSmashedName in the application
                            FriendSmashHelper.get().setLastFriendSmashedID(friendToSmashID);
                            FriendSmashHelper.get().setLastFriendSmashedName(friendToSmashFirstName);
                        } else {
                            closeAndShowError(getResources().getString(R.string.error_fetching_friend_bitmap));
                        }
                    }
                });
            }
        });
    }

    private void closeAndShowError(String error) {
        Bundle bundle = new Bundle();
        bundle.putString("error", error);

        Intent i = new Intent();
        i.putExtras(bundle);

        getActivity().setResult(Activity.RESULT_CANCELED, i);
        getActivity().finish();
    }

    private void closeAndHandleError(FacebookRequestError error) {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    void hideAllUserImageViews() {
        Iterator<UserImageView> userImageViewsIterator = userImageViews.iterator();
        while (userImageViewsIterator.hasNext()) {
            UserImageView currentUserImageView = userImageViewsIterator.next();
            currentUserImageView.setVisibility(View.GONE);
        }
    }

    void hideAllUserImageViewsExcept(UserImageView userImageView) {
        timerHandler.removeCallbacks(fireImageTask);

        Iterator<UserImageView> userImageViewsIterator = userImageViews.iterator();
        while (userImageViewsIterator.hasNext()) {
            UserImageView currentUserImageView = userImageViewsIterator.next();
            if (!currentUserImageView.equals(userImageView)) {
                currentUserImageView.setVisibility(View.GONE);
            }
        }
    }

    private void markAllUserImageViewsAsVoid() {
        Iterator<UserImageView> userImageViewsIterator = userImageViews.iterator();
        while (userImageViewsIterator.hasNext()) {
            UserImageView currentUserImageView = userImageViewsIterator.next();
            currentUserImageView.setVoid(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        stopTheFiringImages();
    }

    @Override
    public void onResume() {
        super.onResume();

        stopTheFiringImages();

        if (!imagesStartedFiring) {
            if (isSocialMode) {
                if (!firstImagePendingFiring) {
                    firstImagePendingFiring = true;
                    imagesStartedFiring = true;
                    fireFirstImage();
                }
            } else {
                imagesStartedFiring = true;
                fireFirstImage();
            }
        }
    }

    private void stopTheFiringImages() {
        markAllUserImageViewsAsVoid();

        timerHandler.removeCallbacks(fireImageTask);
        imagesStartedFiring = false;
    }

    private int getScore() {
        return score;
    }

    private void setScore(int score) {
        this.score = score;

        scoreTextView.setText("Score: " + score);

        if (score > 0 && score % 10 == 0) {
            for (int i = 0; i < score / 20; i++) {
                spawnImage(true);
            }
        }
    }

    private int getLives() {
        return lives;
    }

    private void setLives(int lives) {
        this.lives = lives;

        if (getActivity() != null) {
            livesContainer.removeAllViews();
            for (int i = 0; i < lives; i++) {
                ImageView heartImageView = new ImageView(getActivity());
                heartImageView.setImageResource(R.drawable.heart_red);
                livesContainer.addView(heartImageView);
            }

            if (lives <= 0) {
                Bundle bundle = new Bundle();
                bundle.putInt("score", getScore());
                bundle.putInt("coins_collected", coinsCollected);
                bundle.putInt("bombs_used", bombsUsed);

                Intent i = new Intent();
                i.putExtras(bundle);

                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        }
    }

    private int getBombsRemaining() {
        return bombsRemaining;
    }

    private void setBombsRemaining(int bombsRemaining) {
        this.bombsRemaining = bombsRemaining;

        if (getActivity() != null) {
            bombsContainer.removeAllViews();
            for (int i = 0; i < bombsRemaining; i++) {
                ImageView bombImageView = new ImageView(getActivity());
                bombImageView.setImageResource(R.drawable.bomb_vector);
                bombsContainer.addView(bombImageView);
            }

            if (bombsRemaining <= 0) {
                getGameFrame().removeView(bombButton);
            }
        }
    }

    public FrameLayout getGameFrame() {
        return gameFrame;
    }

    public ArrayList<UserImageView> getUserImageViews() {
        return userImageViews;
    }
}
