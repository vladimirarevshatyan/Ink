package kashmirr.social.activities;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kashmirr.social.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kashmirr.social.adapters.OpponentProfileAdapter;
import kashmirr.social.interfaces.FeedItemClick;
import kashmirr.social.interfaces.ItemClickListener;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.models.FeedModel;
import kashmirr.social.models.UserModel;
import kashmirr.social.service.SocketService;
import kashmirr.social.utils.Animations;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.DialogUtils;
import kashmirr.social.utils.DimDialog;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.InputField;
import kashmirr.social.utils.PermissionsChecker;
import kashmirr.social.utils.RealmHelper;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import kashmirr.social.utils.Time;
import kashmirr.social.view_holders.OpponentProfileHeaderView;
import okhttp3.ResponseBody;

import static kashmirr.social.utils.Constants.EVENT_FRIEND_REQUESTED;
import static kashmirr.social.utils.Constants.EVENT_POST_LIKED;


/**
 * Created by USER on 2016-06-22.
 */
public class OpponentProfile extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, FeedItemClick, OpponentProfileHeaderView.HeaderViewClickListener {

    private static final int SHARE_INTENT_RESULT = 5;
    private static final int STORAGE_PERMISSION_REQUEST = 115;

    private boolean isFriend;
    private boolean disableButton;
    @BindView(R.id.opponentProfileRoot)
    public View opponentProfileRoot;

    @BindView(R.id.opponentProfileRefresh)
    SwipeRefreshLayout opponentProfileRefresh;
    @BindView(R.id.opponentProfileRecycler)
    RecyclerView opponentProfileRecycler;
    private OpponentProfileAdapter opponentProfileAdapter;
    private List<FeedModel> feedModels;

    private String mOpponentId;
    private String mFirstName;
    private String mLastName;
    private SharedHelper sharedHelper;
    private boolean hasFriendRequested;
    private String mOpponentImage;
    private boolean isSocialAccount;
    private Gson gson;
    private StringBuilder content;
    private Bitmap intentBitmap;
    private String shareFileName;
    private File shareOutPutDir;
    private SocketService socketService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.opponent_profile);
        ButterKnife.bind(this);
        ButterKnife.setDebug(true);
        Bundle extras = getIntent().getExtras();
        sharedHelper = new SharedHelper(this);
        feedModels = new LinkedList<>();
        gson = new Gson();
        opponentProfileRefresh.setOnRefreshListener(this);
        opponentProfileAdapter = new OpponentProfileAdapter(feedModels, true, false, this);
        opponentProfileAdapter.setOnFeedItemClickListener(this);
        opponentProfileRecycler.setLayoutManager(new LinearLayoutManager(this));
        opponentProfileRecycler.setAdapter(opponentProfileAdapter);
        opponentProfileAdapter.setHeaderViewClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        if (extras != null) {
            mOpponentId = extras.getString("id");
            mFirstName = extras.getString("firstName");
            mLastName = extras.getString("lastName");
            isFriend = extras.getBoolean("isFriend");
            if (extras.containsKey("disableButton")) {
                disableButton = extras.getBoolean("disableButton");
                opponentProfileAdapter.setDisableButton(disableButton);
                opponentProfileAdapter.notifyDataSetChanged();
            }
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setTitle(mFirstName + " " + mLastName);
            }
        }
        getSingleUser();
        initColors();
    }

    private void initColors() {
        if (sharedHelper.getOpponentProfileColor() != null) {
            opponentProfileRoot.setBackgroundColor(Color.parseColor(sharedHelper.getOpponentProfileColor()));
        }
    }


    @Override
    public void onFriendUnfriendClicked() {
        if (isFriend) {
            removeFriend(mOpponentId);
        } else {
            if (hasFriendRequested) {
                Snackbar.make(opponentProfileRecycler, getString(R.string.youHaveSentAlreadyRequest), Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            } else {
                requestFriend();
            }
        }
    }


    @Override
    public void onSendMessageClicked() {
        if (isFriend) {
            Intent intent = new Intent(getApplicationContext(), Chat.class);
            intent.putExtra("firstName", mFirstName);
            intent.putExtra("lastName", mLastName);
            intent.putExtra("opponentId", mOpponentId);
            intent.putExtra("isSocialAccount", isSocialAccount);
            intent.putExtra("opponentImage", mOpponentImage);
            startActivity(intent);
        }
    }


    private void removeFriend(final String friendId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.removeFriend));
        builder.setMessage(getString(R.string.removefriendHint));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DimDialog.showDimDialog(OpponentProfile.this, getString(R.string.removingFriend));
                RealmHelper.getInstance().removeMessage(friendId, sharedHelper.getUserId());
                makeRequest(Retrofit.getInstance().getInkService().removeFriend(sharedHelper.getUserId(), friendId),
                        null, new RequestCallback() {
                            @Override
                            public void onRequestSuccess(Object result) {
                                try {
                                    String responseBody = ((ResponseBody) result).string();
                                    JSONObject jsonObject = new JSONObject(responseBody);
                                    boolean success = jsonObject.optBoolean("success");
                                    DimDialog.hideDialog();
                                    if (success) {
                                        Toast.makeText(OpponentProfile.this, getString(R.string.friendRemoved), Toast.LENGTH_SHORT).show();

                                        LocalBroadcastManager.getInstance(OpponentProfile.this).sendBroadcast(new Intent(getPackageName() + "Comments"));
                                        LocalBroadcastManager.getInstance(OpponentProfile.this).sendBroadcast(new Intent(getPackageName() + "HomeActivity"));

                                        LocalBroadcastManager.getInstance(OpponentProfile.this).sendBroadcast(new Intent(getPackageName() + ".Chat"));
                                        LocalBroadcastManager.getInstance(OpponentProfile.this).sendBroadcast(new Intent(getPackageName() + "MyFriends"));
                                        finish();
                                    }
                                } catch (IOException e) {
                                    DimDialog.hideDialog();
                                    e.printStackTrace();
                                } catch (JSONException e) {
                                    DimDialog.hideDialog();
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onRequestFailed(Object[] result) {

                            }
                        });
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();

    }

    private void requestFriend() {
        DimDialog.showDimDialog(this, getString(R.string.sendingFriendRequest));
        makeRequest(Retrofit.getInstance().getInkService().requestFriend(sharedHelper.getUserId(), mOpponentId,
                sharedHelper.getFirstName() + " " + sharedHelper.getLastName()),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        try {
                            String responseBody = ((ResponseBody) result).string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            boolean success = jsonObject.optBoolean("success");
                            DimDialog.hideDialog();
                            if (success) {
                                JSONObject friendRequestJson = new JSONObject();
                                friendRequestJson.put("requesterFirstName", sharedHelper.getFirstName());
                                friendRequestJson.put("requesterLastName", sharedHelper.getLastName());
                                friendRequestJson.put("requestedUserId", mOpponentId);
                                friendRequestJson.put("requesterId", sharedHelper.getUserId());

                                socketService.emit(EVENT_FRIEND_REQUESTED, friendRequestJson);
                                friendRequestJson = null;
                                hasFriendRequested = true;
                                Snackbar.make(opponentProfileRecycler, getString(R.string.requestSent), Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(opponentProfileRecycler, getString(R.string.errorSendingRequest), Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            DimDialog.hideDialog();
                            Snackbar.make(opponentProfileRecycler, getString(R.string.errorSendingRequest), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        } catch (JSONException e) {
                            DimDialog.hideDialog();
                            Snackbar.make(opponentProfileRecycler, getString(R.string.errorSendingRequest), Snackbar.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        DimDialog.hideDialog();
                    }
                });
    }


    private void getSingleUser() {
        makeRequest(Retrofit.getInstance().getInkService().getSingleUserDetails(mOpponentId, sharedHelper.getUserId()),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {

                        try {
                            String responseString = ((ResponseBody) result).string();
                            try {
                                JSONObject jsonObject = new JSONObject(responseString);
                                boolean success = jsonObject.optBoolean("success");
                                if (success) {

                                    UserModel userModel = gson.fromJson(responseString, UserModel.class);
                                    opponentProfileAdapter.setUserJsonObject(jsonObject);
                                    isSocialAccount = userModel.isSocialAccount();


                                    mFirstName = userModel.getFirstName();
                                    mLastName = userModel.getLastName();

                                    mOpponentImage = jsonObject.optString("image_link");
                                    isFriend = jsonObject.optBoolean("isFriend");
                                    hasFriendRequested = jsonObject.optBoolean("hasFriendRequested");
                                    getUserPosts();

                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(OpponentProfile.this);
                                    builder.setTitle(getString(R.string.singleUserErrorTile));
                                    builder.setMessage(getString(R.string.singleUserErrorMessage));
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();
                                }
                            } catch (JSONException e) {
                                opponentProfileRefresh.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        opponentProfileRefresh.setRefreshing(false);
                                    }
                                });
                                Toast.makeText(OpponentProfile.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            opponentProfileRefresh.post(new Runnable() {
                                @Override
                                public void run() {
                                    opponentProfileRefresh.setRefreshing(false);
                                }
                            });
                            Toast.makeText(OpponentProfile.this, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        opponentProfileAdapter.setEnableButtons(true);
                        opponentProfileAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        opponentProfileRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                opponentProfileRefresh.setRefreshing(false);
                            }
                        });
                    }
                });
    }

    private void getUserPosts() {
        makeRequest(Retrofit.getInstance().getInkService().getUserPosts(mOpponentId, sharedHelper.getUserId()),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        opponentProfileAdapter.setHasServerError(false);
                        List<FeedModel> feedModels = (List<FeedModel>) result;
                        if (feedModels.isEmpty()) {
                            opponentProfileAdapter.setShowNoFeedsOrError(true);
                            feedModels.add(new FeedModel());
                            opponentProfileAdapter.notifyDataSetChanged();
                        } else {
                            opponentProfileAdapter.setShowNoFeedsOrError(false);
                            for (FeedModel feedModel : feedModels) {
                                OpponentProfile.this.feedModels.add(feedModel);
                                opponentProfileAdapter.notifyDataSetChanged();
                            }
                        }
                        opponentProfileRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                opponentProfileRefresh.setRefreshing(false);
                            }
                        });
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        opponentProfileRefresh.post(new Runnable() {
                            @Override
                            public void run() {
                                opponentProfileRefresh.setRefreshing(false);
                            }
                        });
                        opponentProfileAdapter.setShowNoFeedsOrError(true);
                        opponentProfileAdapter.setHasServerError(true);
                        feedModels.add(new FeedModel());
                        opponentProfileAdapter.notifyDataSetChanged();
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        supportFinishAfterTransition();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        DimDialog.hideDialog();
        super.onDestroy();
    }

    @Override
    public void onServiceConnected(SocketService socketService) {
        super.onServiceConnected(socketService);
        this.socketService = socketService;
    }

    @Override
    public void onRefresh() {
        feedModels.clear();
        opponentProfileAdapter.notifyDataSetChanged();
        getSingleUser();
    }

    @Override
    public void onCardViewClick(FeedModel feedModel, String type) {
        switch (type) {
            case Constants.WALL_TYPE_GROUP_MESSAGE:
                startGroupActivity(feedModel);
                break;
            case Constants.WALL_TYPE_POST:
                startCommentActivity(feedModel);
                break;
        }
    }

    @Override
    public void onAddressClick(FeedModel feedModel) {
        String address = feedModel.getAddress();
        openGoogleMaps(address);
    }

    @Override
    public void onAttachmentClick(FeedModel feedModel) {
        String fileName = feedModel.getFileName();
        String userId = feedModel.getPosterId();
        showPromptDialog(fileName);
    }

    @Override
    public void onCardLongClick(FeedModel feedModel) {

    }

    @Override
    public void onLikeClick(FeedModel feedModel, ImageView likeView, TextView likeCountTV, View likeWrapper) {
        Animations.animateCircular(likeView);
        boolean isLiked = feedModel.isLiked();
        likeWrapper.setEnabled(false);
        if (isLiked) {
            //must dislike
            like(feedModel.getId(), 1, likeCountTV, feedModel, likeWrapper);
            likeView.setBackgroundResource(R.drawable.like_inactive);
            feedModel.setLiked(false);
        } else {
            //must like
            like(feedModel.getId(), 0, likeCountTV, feedModel, likeWrapper);
            likeView.setBackgroundResource(R.drawable.like_active);
            feedModel.setLiked(true);

            JSONObject likeJson = new JSONObject();
            try {
                likeJson.put("postOwnerId", feedModel.getPosterId());
                likeJson.put("likerFirstName", sharedHelper.getFirstName());
                likeJson.put("likerLastName", sharedHelper.getLastName());
                likeJson.put("likerId", sharedHelper.getUserId());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (socketService != null) {
                socketService.emit(EVENT_POST_LIKED, likeJson);
            }
        }
    }

    @Override
    public void onCommentClicked(FeedModel feedModel, View commentView) {
        if (commentView != null) {
            Animations.animateCircular(commentView);
        }

        startCommentActivity(feedModel);
    }

    @Override
    public void onMoreClicked(final FeedModel feedModel, View view) {
        DialogUtils.showPopUp(this, view, new ItemClickListener<MenuItem>() {
            @Override
            public void onItemClick(MenuItem clickedItem) {
                switch (clickedItem.getItemId()) {
                    case 0:
                        showReportField(feedModel);
                        break;
                }

            }
        }, getString(R.string.report));
    }

    @Override
    public void onImageClicked(FeedModel feedModel) {
        Intent intent = new Intent(this, FullscreenActivity.class);
        String encodedFileName = Uri.encode(feedModel.getFileName());
        intent.putExtra("link", Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + encodedFileName);
        startActivity(intent);
    }

    @Override
    public void onShareClicked(FeedModel feedModel) {
        openShareView(feedModel);
    }

    private void showPromptDialog(final String fileName) {

        int index = fileName.indexOf(":");
        String finalFileName = fileName.substring(index + 1, fileName.length());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.downloadQuestion));
        builder.setMessage(getString(R.string.downloadTheFile) + " " + finalFileName);
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

    private void openGoogleMaps(String address) {
        String uri = "geo:0,0?q=" + address;
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
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

    private void like(final String postId, final int isLiking, final TextView likeCountTV, final FeedModel feedModel, final View likeWrapper) {
        makeRequest(Retrofit.getInstance().getInkService().likePost(sharedHelper.getUserId(), postId, isLiking), null,
                new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        try {
                            String responseBody = ((ResponseBody) result).string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            String likesCount = jsonObject.optString("likes_count");
                            feedModel.setLikesCount(likesCount);
                            likeWrapper.setEnabled(true);
                            if (!likesCount.equals("0")) {
                                likeCountTV.setVisibility(View.VISIBLE);
                                if (Integer.parseInt(likesCount) > 1) {
                                    likeCountTV.setText(likesCount + " " + getString(R.string.likesText));
                                } else {
                                    likeCountTV.setText(likesCount + " " + getString(R.string.singleLikeText));
                                }
                            } else {
                                likeCountTV.setVisibility(View.INVISIBLE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onRequestFailed(Object[] result) {

                    }
                });
    }


    private void startGroupActivity(FeedModel feedModel) {

        Intent intent = new Intent(this, SingleGroupView.class);
        intent.putExtra("groupName", feedModel.getGroupName());
        intent.putExtra("groupId", feedModel.getId());
        intent.putExtra("groupColor", feedModel.getGroupColor());
        intent.putExtra("groupImage", feedModel.getGroupImage());
        intent.putExtra("groupDescription", feedModel.getGroupDescription());
        intent.putExtra("groupOwnerId", feedModel.getGroupOwnerId());
        intent.putExtra("groupOwnerName", feedModel.getGroupOwnerName());
        intent.putExtra("count", feedModel.getCount());
        intent.putExtra("ownerImage", feedModel.getOwnerImage());
        intent.putExtra("isSocialAccount", feedModel.isSocialAccount());
        intent.putExtra("isMember", true);
        intent.putExtra("isFriend", feedModel.isFriend());
        startActivity(intent);
    }

    private void startCommentActivity(FeedModel feedModel) {

        Intent intent = new Intent(this, Comments.class);
        intent.putExtra("postId", feedModel.getId());
        intent.putExtra("userImage", feedModel.getUserImage());
        intent.putExtra("postBody", feedModel.getContent());
        intent.putExtra("attachment", feedModel.getFileName());
        intent.putExtra("location", feedModel.getAddress());
        intent.putExtra("name", feedModel.getFirstName() + " " + feedModel.getLastName());
        intent.putExtra("date", Time.convertToLocalTime(feedModel.getDatePosted()));
        intent.putExtra("likesCount", feedModel.getLikesCount());
        intent.putExtra("isLiked", feedModel.isLiked());
        intent.putExtra("isSocialAccount", feedModel.isSocialAccount());
        intent.putExtra("ownerId", feedModel.getPosterId());

        intent.putExtra("attachmentPresent", feedModel.isAttachmentPresent());
        intent.putExtra("addressPresent", feedModel.isAddressPresent());
        intent.putExtra("attachmentName", feedModel.getFileName());
        intent.putExtra("addressName", feedModel.getAddress());
        intent.putExtra("postId", feedModel.getId());
        intent.putExtra("postBody", feedModel.getContent());

        intent.putExtra("isPostOwner", feedModel.isPostOwner());
        intent.putExtra("isFriend", feedModel.isFriend());


        startActivity(intent);
    }

    private void openShareView(final FeedModel singleModel) {

        content = new StringBuilder();
        content.append(singleModel.getContent());

        if (singleModel.isAttachmentPresent()) {
            ImageLoader.loadImage(this, false, false, Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + Uri.encode(singleModel.getFileName()), null, R.drawable.chat_attachment_icon, null, new ImageLoader.ImageLoadedCallback() {
                @Override
                public void onImageLoaded(Object result, Exception e) {
                    if (e != null) {
                        if (singleModel.isAddressPresent()) {
                            content.append("\n" + getString(R.string.locatedAt) + " " + singleModel.getAddress());
                        }
                        openShareIntent(intentBitmap, content.toString());
                    } else {
                        intentBitmap = (Bitmap) result;
                        if (singleModel.isAddressPresent()) {
                            content.append("\n" + getString(R.string.locatedAt) + " " + singleModel.getAddress());
                        }
                        openShareIntent(intentBitmap, content.toString());
                    }
                }
            });
        } else {
            intentBitmap = null;
            if (singleModel.isAddressPresent()) {
                content.append("\n" + getString(R.string.locatedAt) + " " + singleModel.getAddress());
            }
            openShareIntent(intentBitmap, content.toString());
        }

    }

    private void openShareIntent(@Nullable Bitmap result, String text) {
        if (shareOutPutDir != null && shareOutPutDir.exists()) {
            shareOutPutDir.delete();
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        shareFileName = "Ink_File" + System.currentTimeMillis() + ".jpg";

        if (result != null) {
            if (!PermissionsChecker.isStoragePermissionGranted(this)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST);
                return;
            }
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            result.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            shareOutPutDir = new File(Environment.getExternalStorageDirectory() + File.separator + shareFileName);
            try {
                shareOutPutDir.createNewFile();
                FileOutputStream fo = new FileOutputStream(shareOutPutDir);
                fo.write(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
                Snackbar.make(opponentProfileRecycler, getString(R.string.error), Snackbar.LENGTH_SHORT).show();
            }
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + shareFileName));
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(text);
        stringBuilder.append("\n\n" + getString(R.string.ink_share_text));

        intent.putExtra(Intent.EXTRA_TEXT, stringBuilder.toString());
        startActivityForResult(Intent.createChooser(intent, getString(R.string.share_ink_with)), SHARE_INTENT_RESULT);
    }

    private void showReportField(final FeedModel feedModel) {
        InputField.createInputFieldView(this, new InputField.ClickHandler() {
            @Override
            public void onPositiveClicked(Object... result) {
                AlertDialog dialog = (AlertDialog) result[1];
                dialog.dismiss();
                String reportCauseMessage = String.valueOf(result[0]);
                reportPost(feedModel, reportCauseMessage);
            }

            @Override
            public void onNegativeClicked(Object... result) {
                AlertDialog dialog = (AlertDialog) result[1];
                dialog.dismiss();
            }
        }, null, getString(R.string.reportCause), null);
    }


    private void reportPost(final FeedModel feedModel, final String reportCauseMessage) {
        final kashmirr.social.utils.ProgressDialog dialog = kashmirr.social.utils.ProgressDialog.get().buildProgressDialog(OpponentProfile.this, true);
        dialog.setTitle(getString(R.string.connecting));
        dialog.setMessage(getString(R.string.loadingText));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        makeRequest(Retrofit.getInstance().getInkService().reportPost(feedModel.getId(), String.valueOf(feedModel.isGlobalPost()), reportCauseMessage, sharedHelper.getUserId()),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        dialog.hide();
                        try {
                            String responseBody = ((ResponseBody) result).string();
                            JSONObject jsonObject = new JSONObject(responseBody);
                            boolean success = jsonObject.optBoolean("success");
                            if (success) {
                                DialogUtils.showDialog(OpponentProfile.this, getString(R.string.done_text), getString(R.string.reported), true, null, false, null);
                                opponentProfileAdapter.clear();

                                onRefresh();
                            } else {
                                DialogUtils.showDialog(OpponentProfile.this, getString(R.string.error), getString(R.string.reportError), true, null, false, null);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            DialogUtils.showDialog(OpponentProfile.this, getString(R.string.error), getString(R.string.reportError), true, null, false, null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DialogUtils.showDialog(OpponentProfile.this, getString(R.string.error), getString(R.string.reportError), true, null, false, null);
                        }
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        dialog.hide();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SHARE_INTENT_RESULT:
                if (shareOutPutDir != null) {
                    shareOutPutDir.delete();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST:
                if (!PermissionsChecker.isStoragePermissionGranted(this)) {
                    Snackbar.make(opponentProfileRecycler, getString(R.string.storagePermissions), Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
                } else {
                    openShareIntent(intentBitmap, content.toString());
                }
                break;
        }
    }
}
