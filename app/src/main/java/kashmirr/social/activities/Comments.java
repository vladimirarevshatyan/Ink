package kashmirr.social.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kashmirr.social.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fab.FloatingActionButton;
import kashmirr.social.adapters.CommentAdapter;
import kashmirr.social.decorators.DividerItemDecoration;
import kashmirr.social.interfaces.CommentClickHandler;
import kashmirr.social.interfaces.ItemClickListener;
import kashmirr.social.interfaces.RecyclerItemClickListener;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.models.CommentModel;
import kashmirr.social.service.SocketService;
import kashmirr.social.utils.Animations;
import kashmirr.social.utils.ClipManager;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.DialogUtils;
import kashmirr.social.utils.InputField;
import kashmirr.social.utils.Keyboard;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import okhttp3.ResponseBody;

import static kashmirr.social.utils.Constants.EVENT_COMMENT_ADDED;
import static kashmirr.social.utils.Constants.EVENT_POST_LIKED;
import static kashmirr.social.utils.Constants.NOTIFICATION_BUNDLE_EXTRA_KEY;
import static kashmirr.social.utils.Constants.REQUEST_CODE_CHOSE_STICKER;
import static kashmirr.social.utils.Constants.STARTING_FOR_RESULT_BUNDLE_KEY;

public class Comments extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, CommentClickHandler, RecyclerItemClickListener {

    @BindView(R.id.commentBody)
    EditText mCommentBody;

    @BindView(R.id.commentsLoading)
    ProgressBar mCommentsLoading;
    @BindView(R.id.addCommentButton)
    FloatingActionButton mAddCommentButton;
    @BindView(R.id.commentRecycler)
    RecyclerView mCommentRecycler;
    @BindView(R.id.commentRefresher)
    SwipeRefreshLayout mCommentRefresher;
    @BindView(R.id.commentCard)
    CardView commentCard;
    @BindView(R.id.chosenStickerLayout)
    public View stickerChosenLayout;
    private String mPostId;
    private String mUserImage;
    private String mPostBody;
    private SharedHelper mSharedHelper;
    private List<CommentModel> mCommentModels;
    private CommentAdapter mCommentAdapter;
    private CommentModel mCommentModel;
    private String mAttachment;
    private boolean isOwnerSocialAccount;
    private String mLocation;
    private String mDate;
    private String mName;
    private String mLikesCount;
    private boolean isLiked;
    private Dialog addCommentDialog;
    @BindView(R.id.commentRootLayout)
    View contentView;
    private boolean isResponseReceived;
    private boolean hasComments;
    private String ownerId;
    private ProgressDialog deleteDialog;
    private boolean hasAttachment;
    private boolean isPostOwner;
    private boolean isFriend;
    private boolean hasAddress;
    private String attachmentName;
    private String addressName;
    private String postId;
    private String postBody;
    private Snackbar snackbar;
    private BroadcastReceiver broadcastReceiver;
    private boolean shouldUpdate;
    private boolean isStickerChosen;
    private String lastChosenStickerUrl;
    private boolean isAnimated;
    private SocketService socketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        ButterKnife.bind(this);
        mSharedHelper = new SharedHelper(this);
        Bundle extras = getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        mCommentModels = new ArrayList<>();
        mCommentRefresher.setOnRefreshListener(this);
        deleteDialog = new ProgressDialog(Comments.this);
        deleteDialog.setTitle(getString(R.string.deleting));
        deleteDialog.setMessage(getString(R.string.deletingPost));
        deleteDialog.setCancelable(false);
        snackbar = Snackbar.make(mCommentRefresher, getString(R.string.savingChanges), Snackbar.LENGTH_INDEFINITE);
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.comments));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (extras != null) {
            if (extras.containsKey(NOTIFICATION_BUNDLE_EXTRA_KEY)) {
                Bundle bundle = extras.getBundle(NOTIFICATION_BUNDLE_EXTRA_KEY);
                mPostId = bundle.getString("postId");
                mUserImage = bundle.getString("userImage");
                mPostBody = bundle.getString("postBody");
                mAttachment = bundle.getString("attachment");
                isOwnerSocialAccount = bundle.getBoolean("isSocialAccount");
                mLocation = bundle.getString("location");
                mName = bundle.getString("name");
                mDate = bundle.getString("date");
                mLikesCount = bundle.getString("likesCount");
                isLiked = bundle.getBoolean("isLiked");
                ownerId = bundle.getString("ownerId");
                hasAttachment = bundle.getBoolean("attachmentPresent");
                hasAddress = bundle.getBoolean("addressPresent");
                attachmentName = bundle.getString("attachmentName");
                addressName = bundle.getString("addressName");
                postId = bundle.getString("postId");
                postBody = bundle.getString("postBody");
                isPostOwner = bundle.getBoolean("isPostOwner");
                isFriend = bundle.getBoolean("isFriend");
            } else {
                mPostId = extras.getString("postId");
                mUserImage = extras.getString("userImage");
                mPostBody = extras.getString("postBody");
                mAttachment = extras.getString("attachment");
                isOwnerSocialAccount = extras.getBoolean("isSocialAccount");
                mLocation = extras.getString("location");
                mName = extras.getString("name");
                mDate = extras.getString("date");
                mLikesCount = extras.getString("likesCount");
                isLiked = extras.getBoolean("isLiked");
                ownerId = extras.getString("ownerId");
                hasAttachment = extras.getBoolean("attachmentPresent");
                hasAddress = extras.getBoolean("addressPresent");
                attachmentName = extras.getString("attachmentName");
                addressName = extras.getString("addressName");
                postId = extras.getString("postId");
                postBody = extras.getString("postBody");
                isPostOwner = extras.getBoolean("isPostOwner");
                isFriend = extras.getBoolean("isFriend");
            }

        }

        mCommentAdapter = new CommentAdapter(ownerId, mCommentModels, this, mUserImage,
                mPostBody, mAttachment, mLocation, mDate, mName, mLikesCount, isLiked, isOwnerSocialAccount);
        mCommentAdapter.setPostId(mPostId);
        mCommentAdapter.setOnLikeClickListener(this);
        mCommentRefresher.setColorSchemeColors(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);

        mCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecycler.setItemAnimator(itemAnimator);
        mCommentRecycler.addItemDecoration(new DividerItemDecoration(this));
        mCommentRecycler.setAdapter(mCommentAdapter);

        addCommentDialog = new Dialog(this, R.style.Theme_Transparent);
        addCommentDialog.setContentView(R.layout.dim_comment_layout);

        mCommentRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(mCommentRecycler.getWindowToken(), 0);
                }
            }
        });
        mCommentAdapter.setOnItemClickListener(this);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + "Comments"));

        mAddCommentButton.setEnabled(false);
        getComments(mPostId, false);
        mCommentBody.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().isEmpty()) {
                    if (isStickerChosen) {
                        mAddCommentButton.setEnabled(true);
                    } else {
                        mAddCommentButton.setEnabled(false);
                    }
                } else {
                    mAddCommentButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        attachKeyboardCallback();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (shouldUpdate) {
            LocalBroadcastManager.getInstance(Comments.this).sendBroadcast(new Intent(getPackageName() + "HomeActivity"));
        }
        finish();
        return super.onOptionsItemSelected(item);
    }


    private void getComments(final String postId, final boolean shouldFocus) {
        isResponseReceived = false;
        if (mCommentsLoading.getVisibility() == View.GONE) {
            mCommentsLoading.setVisibility(View.VISIBLE);
        }

        mCommentModels.clear();
        mCommentAdapter.notifyDataSetChanged();
        makeRequest(Retrofit.getInstance().getInkService().getComments(mSharedHelper.getUserId(), postId), mCommentsLoading, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                if (mCommentModels != null) {
                    mCommentModels.clear();
                    mCommentAdapter.notifyDataSetChanged();
                }
                try {
                    isResponseReceived = true;
                    String responseBody = ((ResponseBody) result).string();
                    JSONArray jsonArray = new JSONArray(responseBody);
                    if (jsonArray.length() <= 0) {
                        mCommentAdapter.setShowingNoComments(true);
                        mCommentAdapter.notifyDataSetChanged();

                        mCommentRefresher.setRefreshing(false);
                        hasComments = false;
                    } else {
                        hasComments = true;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eachObject = jsonArray.optJSONObject(i);
                            String commenterId = eachObject.optString("commenter_id");
                            String commenterImage = eachObject.optString("commenter_image");
                            String commentBody = eachObject.optString("comment_body");
                            String postId = eachObject.optString("post_id");
                            String commentId = eachObject.optString("comment_id");
                            String isFriend = eachObject.optString("isFriend");
                            String firstName = eachObject.optString("commenter_first_name");
                            String lastName = eachObject.optString("commenter_last_name");
                            boolean isSocialAccount = eachObject.optBoolean("isSocialAccount");
                            boolean isIncognito = eachObject.optBoolean("isIncognito");
                            String stickerUrl = eachObject.optString("stickerUrl");
                            boolean isAnimated = eachObject.optBoolean("isAnimated");
                            mCommentModel = new CommentModel(stickerUrl, isAnimated,
                                    isSocialAccount, isIncognito, Boolean.valueOf(isFriend),
                                    commentId,
                                    commenterId, commenterImage, commentBody, postId, firstName,
                                    lastName);
                            mCommentModels.add(mCommentModel);
                            mCommentAdapter.notifyDataSetChanged();
                        }
                        mCommentAdapter.setShowingNoComments(false);
                        mCommentAdapter.notifyDataSetChanged();

                        mCommentsLoading.setVisibility(View.GONE);
                        mCommentRefresher.setRefreshing(false);
                        if (shouldFocus) {
                            if (addCommentDialog.isShowing()) {
                                addCommentDialog.dismiss();
                            }
                            mCommentBody.setText("");
                        }
                    }
                } catch (IOException e) {
                    Toast.makeText(Comments.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    Toast.makeText(Comments.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                mCommentRefresher.post(new Runnable() {
                    @Override
                    public void run() {
                        mCommentRefresher.setRefreshing(false);
                    }
                });
            }
        });
    }

    @OnClick(R.id.attachSticker)
    public void attachStickerClicked() {
        openStickerChooser();
    }

    @OnClick(R.id.removeSticker)
    public void removeClicked() {
        if (mCommentBody.getText().toString().trim().isEmpty()) {
            mAddCommentButton.setEnabled(false);
        }
        isStickerChosen = false;
        stickerChosenLayout.setVisibility(View.GONE);
    }

    @OnClick(R.id.addCommentButton)
    public void addCommentButton() {
        isResponseReceived = false;

        mCommentAdapter.setShowingNoComments(false);
        mCommentAdapter.notifyDataSetChanged();

        addCommentDialog.show();
        addComment(mCommentBody.getText().toString().trim(), mSharedHelper.getImageLink(),
                mSharedHelper.getUserId(), mPostId, lastChosenStickerUrl, isStickerChosen, isAnimated);
        mCommentBody.setText("");
        stickerChosenLayout.setVisibility(View.GONE);
        mAddCommentButton.setEnabled(false);
        isStickerChosen = false;
    }

    private void openStickerChooser() {
        Intent intent = new Intent(getApplicationContext(), MyCollection.class);
        intent.putExtra(STARTING_FOR_RESULT_BUNDLE_KEY, true);
        startActivityForResult(intent, Constants.REQUEST_CODE_CHOSE_STICKER);
    }

    private void addComment(final String commentBody, final String userImage,
                            final String commenterId, final String postId,
                            final String stickerUrl, final boolean isStickerChosen, final boolean isAnimated) {
        makeRequest(Retrofit.getInstance().getInkService().addComment(commenterId,
                userImage, commentBody, postId, mSharedHelper.getFirstName(), mSharedHelper.getLastName(), isStickerChosen ? stickerUrl : "", isAnimated),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        addCommentDialog.dismiss();
                        try {
                            String responseBody = ((ResponseBody) result).string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            boolean success = jsonObject.optBoolean("success");
                            if (success) {
                                String commentId = jsonObject.optString("commentId");

                                if (socketService != null) {
                                    JSONObject commentJson = new JSONObject();


                                    commentJson.put("postId", mPostId);
                                    commentJson.put("userImage", mUserImage);
                                    commentJson.put("postBody", mPostBody);
                                    commentJson.put("attachment", mAttachment);
                                    commentJson.put("isSocialAccount", isOwnerSocialAccount);
                                    commentJson.put("location", mLocation);
                                    commentJson.put("name", mName);
                                    commentJson.put("date", mDate);
                                    commentJson.put("likesCount", mLikesCount);
                                    commentJson.put("isLiked", isLiked);
                                    commentJson.put("ownerId", ownerId);
                                    commentJson.put("attachmentPresent", hasAttachment);
                                    commentJson.put("addressPresent", hasAddress);
                                    commentJson.put("attachmentName", attachmentName);
                                    commentJson.put("addressName", addressName);
                                    commentJson.put("postId", postId);
                                    commentJson.put("postBody", postBody);
                                    commentJson.put("isPostOwner", isPostOwner);
                                    commentJson.put("isFriend", isFriend);

                                    commentJson.put("commentId", commentId);
                                    commentJson.put("currentUserId", mSharedHelper.getUserId());
                                    commentJson.put("commenterId", commenterId);
                                    commentJson.put("commentBody", commentBody);
                                    commentJson.put("commenterFirstName", mSharedHelper.getFirstName());
                                    commentJson.put("commenterLastName", mSharedHelper.getLastName());

                                    socketService.emit(EVENT_COMMENT_ADDED, commentJson);
                                    commentJson = null;
                                }

                                mSharedHelper.putCommentedPost(postId);
                                Snackbar.make(mCommentRefresher, getString(R.string.commentAdded), Snackbar.LENGTH_SHORT).
                                        setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        }).show();
                                getComments(postId, true);
                            } else {
                                Toast.makeText(Comments.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            Toast.makeText(Comments.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                            addCommentDialog.dismiss();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            Toast.makeText(Comments.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                            addCommentDialog.dismiss();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        addCommentDialog.dismiss();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (broadcastReceiver != null) {
            try {
                LocalBroadcastManager.getInstance(Comments.this).unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {

            }
        }
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey(NOTIFICATION_BUNDLE_EXTRA_KEY)) {
            Bundle bundle = extras.getBundle(NOTIFICATION_BUNDLE_EXTRA_KEY);
            String postId = bundle.getString("postId");
            if (this.postId.equals(postId)) {
                getComments(mPostId, false);
            } else {
                Intent relaunchIntent = new Intent(this, Comments.class);
                relaunchIntent.putExtra(NOTIFICATION_BUNDLE_EXTRA_KEY, bundle);
                finish();
                startActivity(relaunchIntent);
            }
        }
    }

    @Override
    public void onServiceConnected(SocketService socketService) {
        super.onServiceConnected(socketService);
        this.socketService = socketService;
    }

    @Override
    public void onRefresh() {
        getComments(mPostId, false);
    }

    @Override
    public void onLikeClicked(int position, TextView likesCountTV, ImageView likeView, View likeWrapper) {
        Animations.animateCircular(likeView);
        likeWrapper.setEnabled(false);
        if (isLiked) {
            //must dislike
            like(mPostId, 1, likesCountTV, likeWrapper);
            likeView.setBackgroundResource(R.drawable.like_inactive);
            mCommentAdapter.setIsLiked(false);
            isLiked = false;
        } else {
            //must like
            like(mPostId, 0, likesCountTV, likeWrapper);
            likeView.setBackgroundResource(R.drawable.like_active);
            mCommentAdapter.setIsLiked(true);
            isLiked = true;
            JSONObject likeJson = new JSONObject();
            try {
                likeJson.put("postOwnerId", ownerId);
                likeJson.put("likerFirstName", mSharedHelper.getFirstName());
                likeJson.put("likerLastName", mSharedHelper.getLastName());
                likeJson.put("likerId", mSharedHelper.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (socketService != null) {
                socketService.emit(EVENT_POST_LIKED, likeJson);
            }
        }
    }

    @Override
    public void onAddressClick(int position) {
        openGoogleMaps(mLocation);
    }

    @Override
    public void onAttachmentClick(int position) {
        showPromptDialog(mAttachment);
    }

    @Override
    public void onMoreClick(int position, View view) {
        if (isPostOwner) {
            DialogUtils.showPopUp(Comments.this, view, new ItemClickListener<MenuItem>() {
                @Override
                public void onItemClick(MenuItem clickedItem) {
                    switch (clickedItem.getItemId()) {
                        case 0:
                            Intent intent = new Intent(getApplicationContext(), MakePost.class);
                            intent.putExtra("isEditing", true);
                            intent.putExtra("attachmentPresent", hasAttachment);
                            intent.putExtra("addressPresent", hasAddress);
                            intent.putExtra("attachmentName", attachmentName);
                            intent.putExtra("addressName", addressName);
                            intent.putExtra("postId", postId);
                            intent.putExtra("postBody", postBody);
                            startActivity(intent);
                            break;
                        case 1:
                            AlertDialog.Builder builder = new AlertDialog.Builder(Comments.this);
                            builder.setTitle(getString(R.string.deletePost));
                            builder.setMessage(getString(R.string.areYouSure));
                            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    deleteDialog.show();
                                    deletePost();
                                }
                            });
                            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            builder.show();
                            break;
                    }
                }
            }, getString(R.string.edit), getString(R.string.delete));
        } else {
            DialogUtils.showPopUp(Comments.this, view, new ItemClickListener<MenuItem>() {
                @Override
                public void onItemClick(MenuItem clickedItem) {
                    String nameParts[] = mName.split("\\s");
                    Intent intent = new Intent(Comments.this, OpponentProfile.class);
                    intent.putExtra("id", ownerId);
                    intent.putExtra("firstName", nameParts[0]);
                    intent.putExtra("lastName", nameParts[1]);
                    intent.putExtra("isFriend", isFriend);
                    startActivity(intent);
                }
            }, getString(R.string.viewProfile));
        }

    }

    @Override
    public void onImageClicked(int position) {

        Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
        String encodedFileName = Uri.encode(attachmentName);
        intent.putExtra("link", Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + encodedFileName);
        startActivity(intent);
    }

    @Override
    public void onStickerClicked(CommentModel commentModel) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        int lastIndex = Constants.MAIN_URL.lastIndexOf("/");
        String urlWithoutSlash = Constants.MAIN_URL.substring(0, lastIndex);
        intent.putExtra("link", urlWithoutSlash + commentModel.getStickerUrl());
        startActivity(intent);
    }

    private void deletePost() {
        makeRequest(Retrofit.getInstance().getInkService().deletePost(mPostId, mAttachment), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    deleteDialog.dismiss();
                    if (success) {
                        LocalBroadcastManager.getInstance(Comments.this).sendBroadcast(new Intent(getPackageName() + "HomeActivity"));
                        finish();
                    } else {
                        Snackbar.make(mCommentsLoading, getString(R.string.couldNotDeletePost), Snackbar.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    deleteDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                deleteDialog.dismiss();
            }
        });
    }

    private void openGoogleMaps(String address) {
        String uri = "geo:0,0?q=" + address;
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    private void showPromptDialog(final String fileName) {
        int index = fileName.indexOf(":");
        String finalFileName = fileName.substring(index + 1, fileName.length());

        AlertDialog.Builder builder = new AlertDialog.Builder(Comments.this);
        builder.setTitle(getString(R.string.downloadQuestion));
        builder.setMessage(getString(R.string.downloadTheFile) + " " + fileName);
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                queDownload(fileName);
            }
        });
        builder.show();
    }


    private void queDownload(String fileName) {
        int index = fileName.indexOf(":");
        String finalFileName = fileName.substring(index + 1, fileName.length());

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + fileName));
        request.setTitle(finalFileName);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setVisibleInDownloadsUi(true);
        downloadManager.enqueue(request);

    }

    private void like(final String postId, final int isLiking, final TextView likeCountTV, final View likeWrapper) {
        shouldUpdate = true;
        makeRequest(Retrofit.getInstance().getInkService().likePost(mSharedHelper.getUserId(), postId, isLiking), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    String likesCount = jsonObject.optString("likes_count");
                    likeWrapper.setEnabled(true);
                    if (!likesCount.equals("0")) {
                        likeCountTV.setVisibility(View.VISIBLE);
                        if (Integer.parseInt(likesCount) > 1) {
                            likeCountTV.setText(likesCount + " " + getString(R.string.likesText));
                        } else {
                            likeCountTV.setText(likesCount + " " + getString(R.string.singleLikeText));
                        }
                    } else {
                        likeCountTV.setVisibility(View.GONE);
                    }
                    mCommentAdapter.setLikesCount(likesCount);
                    mCommentAdapter.notifyDataSetChanged();
                } catch (IOException e) {
                    shouldUpdate = false;
                    e.printStackTrace();
                } catch (JSONException e) {
                    shouldUpdate = false;
                    e.printStackTrace();
                }

            }

            @Override
            public void onRequestFailed(Object[] result) {

            }
        });
    }

    private void focusUp() {
        mCommentRecycler.post(new Runnable() {
            @Override
            public void run() {
                // Call smooth scroll
                mCommentRecycler.smoothScrollToPosition(0);
            }
        });

    }

    private void attachKeyboardCallback() {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    if (isResponseReceived) {
                        if (!hasComments) {
                            //hide no comments
                        }
                    }
                } else {
                    if (isResponseReceived) {
                        if (!hasComments) {
                            //show no comments
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClicked(int position, View view) {

        int actualPosition = position - 1;
        try {
            CommentModel singleModel = mCommentModels.get(actualPosition);
            String currentId = singleModel.getCommenterId();
            if (singleModel.isIncognito()) {
                DialogUtils.showDialog(Comments.this, getString(R.string.incognito), getString(R.string.incognito_hint), true,
                        null, false, null);
            } else {
                if (currentId.equals(mSharedHelper.getUserId())) {
                    Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), OpponentProfile.class);
                    intent.putExtra("id", currentId);
                    intent.putExtra("firstName", singleModel.getFirstName());
                    intent.putExtra("lastName", singleModel.getLastName());
                    intent.putExtra("isFriend", singleModel.isFriend());
                    startActivity(intent);
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            //the header or footer was clicked nothing else.
        }
    }

    @Override
    public void onItemLongClick(Object object) {

    }

    private void callCommentServer(final String type, final String commentId, final String newCommentBody) {
        makeRequest(Retrofit.getInstance().getInkService().commentOptions(type, commentId, newCommentBody), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();

                    getComments(mPostId, false);
                    snackbar.setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    if (type.equals(Constants.COMMENT_TYPE_EDIT)) {
                        snackbar.setText(getString(R.string.changesWasSaved));
                        snackbar.show();
                    } else {
                        snackbar.setText(getString(R.string.commentWasDeleted));
                        snackbar.show();
                    }
                    Log.d("fasfsafsafa", "onResponse: " + responseBody);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {

            }
        });
    }

    @Override
    public void onAdditionalItemClick(int position, View view) {
        int actualPosition = position - 1;
        try {
            final CommentModel commentModel = mCommentModels.get(actualPosition);
            DialogUtils.showPopUp(Comments.this, view, new ItemClickListener<MenuItem>() {
                @Override
                public void onItemClick(MenuItem clickedItem) {
                    switch (clickedItem.getItemId()) {
                        case 0:
                            InputField.createInputFieldView(Comments.this, new InputField.ClickHandler() {
                                @Override
                                public void onPositiveClicked(Object... result) {
                                    snackbar.show();
                                    AlertDialog dialog = (AlertDialog) result[1];
                                    dialog.dismiss();
                                    Keyboard.hideKeyboard(Comments.this);
                                    callCommentServer(Constants.COMMENT_TYPE_EDIT, commentModel.getCommentId(), String.valueOf(result[0]));

                                }

                                @Override
                                public void onNegativeClicked(Object... result) {
                                    AlertDialog dialog = (AlertDialog) result[1];
                                    dialog.dismiss();
                                }
                            }, commentModel.getCommentBody(), null, null);
                            break;
                        case 1:
                            snackbar.show();
                            callCommentServer(Constants.COMMENT_TYPE_DELETE, commentModel.getCommentId(), "");
                            break;
                        case 2:
                            ClipManager.copy(Comments.this, commentModel.getCommentBody());
                            break;
                    }
                }
            }, getString(R.string.editComment), getString(R.string.deleteComment), getString(R.string.copy));

        } catch (ArrayIndexOutOfBoundsException e) {

        }
    }

    @Override
    public void onAdditionalItemClicked(Object object) {

    }

    @Override
    public void onItemClicked(Object object) {

    }

    @Override
    public void onBackPressed() {
        if (shouldUpdate) {
            LocalBroadcastManager.getInstance(Comments.this).sendBroadcast(new Intent(getPackageName() + "HomeActivity"));
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case REQUEST_CODE_CHOSE_STICKER:
                if (data != null) {
                    isAnimated = data.getExtras().getBoolean(Constants.STICKER_IS_ANIMATED_EXTRA_KEY);
                    lastChosenStickerUrl = data.getExtras().getString(Constants.STICKER_URL_EXTRA_KEY);
                    isStickerChosen = true;
                    handleStickerChosenView();
                }

                break;
            default:
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleStickerChosenView() {
        mAddCommentButton.setEnabled(true);
        stickerChosenLayout.setVisibility(View.VISIBLE);
    }
}
