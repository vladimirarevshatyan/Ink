package kashmirr.social.activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kashmirr.social.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kashmirr.social.adapters.GroupMessagesAdapter;
import kashmirr.social.adapters.MemberAdapter;
import kashmirr.social.interfaces.ItemClickListener;
import kashmirr.social.interfaces.RecyclerItemClickListener;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.models.GroupMessagesModel;
import kashmirr.social.models.MemberModel;
import kashmirr.social.service.SocketService;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.DialogUtils;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.InputField;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.ScrollAwareFABButtonehavior;
import kashmirr.social.utils.SharedHelper;
import kashmirr.social.utils.User;
import okhttp3.ResponseBody;

import static kashmirr.social.utils.Constants.EVENT_REQUEST_GROUP_JOIN;
import static kashmirr.social.utils.Constants.NOTIFICATION_RECEIVED_GROUP_BUNDLE;

public class SingleGroupView extends BaseActivity implements RecyclerItemClickListener, AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.groupCollapsingToolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.groupToolbar)
    Toolbar mToolbar;
    @BindView(R.id.groupImage)
    ImageView mGroupImageView;
    @BindView(R.id.groupBackgroundColor)
    RelativeLayout mGroupBackgroundColor;
    @BindView(R.id.joinGroupButton)
    Button mJoinGroupButton;
    @BindView(R.id.groupSingleDescription)
    TextView mGroupSingleDescription;
    @BindView(R.id.groupSingleFollowersCount)
    TextView mGroupSingleFollowersCount;
    @BindView(R.id.ownerImageView)
    ImageView mOwnerImageView;
    @BindView(R.id.addMessageToGroup)
    android.support.design.widget.FloatingActionButton mAddMessageToGroup;
    @BindView(R.id.groupMessagesRecycler)
    RecyclerView groupMessagesRecycler;
    @BindView(R.id.groupMessagesLoading)
    AVLoadingIndicatorView groupMessagesLoading;
    @BindView(R.id.groupImageLoading)
    ProgressBar groupImageLoading;
    @BindView(R.id.noGroupMessageLayout)
    RelativeLayout noGroupMessageLayout;
    @BindView(R.id.singleGroupSwipe)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.singleGroupAppBar)
    AppBarLayout singleGroupAppBar;

    private String mGroupName = "";
    private boolean isSocialAccount;
    private BroadcastReceiver broadcastReceiver;
    private boolean isFollowing;
    private String mOwnerImage;
    private String mGroupId;
    private String mGroupColor;
    private String mGroupImage;
    private String mGroupDescription;
    private String mGroupOwnerId;
    private String mGroupOwnerName;
    private String mCount;
    private List<GroupMessagesModel> groupMessagesModels;
    private GroupMessagesModel groupMessagesModel;
    private GroupMessagesAdapter groupMessagesAdapter;
    private SharedHelper mSharedHelper;
    private boolean isMember;
    private boolean isRequested;
    private MemberModel memberModel;
    private MemberAdapter memberAdapter;
    private List<MemberModel> memberModels;
    private boolean hasAnythingChanged;
    private ProgressDialog progressDialog;
    private Snackbar snackbar;
    private boolean isFriendWithOwner;
    private SocketService socketService;
    private boolean isParticipantsLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_group_view);
        ButterKnife.bind(this);
        mSharedHelper = new SharedHelper(this);
        Bundle extras = getIntent().getExtras();
        groupMessagesModels = new ArrayList<>();
        memberModels = new ArrayList<>();
        snackbar = Snackbar.make(mOwnerImageView, getString(R.string.savingChanges), Snackbar.LENGTH_INDEFINITE);
        groupImageLoading.getIndeterminateDrawable().setColorFilter(Color.parseColor("#ffffff"), android.graphics.PorterDuff.Mode.MULTIPLY);
        memberAdapter = new MemberAdapter(memberModels, this);
        singleGroupAppBar.addOnOffsetChangedListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        groupMessagesAdapter = new GroupMessagesAdapter(groupMessagesModels, this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        mJoinGroupButton.setEnabled(false);


        mJoinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isRequested) {
                    requestJoin();
                }
            }
        });
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Snackbar.make(mGroupImageView, getString(R.string.messagePosted), Snackbar.LENGTH_SHORT).setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                getGroupMessages();
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(getPackageName() + "SingleGroupView"));


        groupMessagesRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) groupMessagesRecycler.getLayoutManager();
                if (linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0) {

                }

                if (!isMember) {
                    return;
                }
                if (dy > 0)
                    mAddMessageToGroup.hide();
                else if (dy < 0)
                    mAddMessageToGroup.show();
            }
        });

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        groupMessagesRecycler.setLayoutManager(new LinearLayoutManager(this));
        groupMessagesRecycler.setItemAnimator(itemAnimator);

        groupMessagesAdapter.setOnClickListener(this);
        groupMessagesRecycler.setAdapter(groupMessagesAdapter);
        if (extras != null) {
            if (extras.containsKey(NOTIFICATION_RECEIVED_GROUP_BUNDLE)) {
                Bundle bundle = extras.getBundle(NOTIFICATION_RECEIVED_GROUP_BUNDLE);
                mGroupName = bundle.getString("groupName");
                isSocialAccount = bundle.getBoolean("isSocialAccount");
                mGroupId = bundle.getString("groupId");
                mGroupColor = bundle.getString("groupColor");
                mGroupImage = bundle.getString("groupImage");
                mGroupDescription = bundle.getString("groupDescription");
                mGroupOwnerId = bundle.getString("groupOwnerId");
                mGroupOwnerName = bundle.getString("groupOwnerName");
                mCount = bundle.getString("count");
                mOwnerImage = bundle.getString("ownerImage");
                isMember = bundle.getBoolean("isMember");
                isFriendWithOwner = bundle.getBoolean("isFriend");
            } else {
                mGroupName = extras.getString("groupName");
                isSocialAccount = extras.getBoolean("isSocialAccount");
                mGroupId = extras.getString("groupId");
                mGroupColor = extras.getString("groupColor");
                mGroupImage = extras.getString("groupImage");
                mGroupDescription = extras.getString("groupDescription");
                mGroupOwnerId = extras.getString("groupOwnerId");
                mGroupOwnerName = extras.getString("groupOwnerName");
                mCount = extras.getString("count");
                mOwnerImage = extras.getString("ownerImage");
                isMember = extras.getBoolean("isMember");
                isFriendWithOwner = extras.getBoolean("isFriend");
            }
        }
        mJoinGroupButton.setEnabled(false);
        mJoinGroupButton.setText(getString(R.string.loadingText));
        initButtons();
        getGroupMessages();
        mGroupSingleDescription.setText(mGroupDescription);
        initCount();
        setSupportActionBar(mToolbar);
        mCollapsingToolbar.setTitle(mGroupName);

        if (mGroupImage != null && !mGroupImage.isEmpty()) {
            mGroupImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            String encodedImage = Uri.encode(mGroupImage);
            ImageLoader.loadImage(this, false, false, Constants.MAIN_URL + Constants.GROUP_IMAGES_FOLDER + encodedImage,
                    0, R.drawable.no_group_image, mGroupImageView, new ImageLoader.ImageLoadedCallback() {
                        @Override
                        public void onImageLoaded(Object result, Exception e) {
                            hideGroupImageLoading();
                        }
                    });
        } else {
            mGroupImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mGroupImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_group_image));
            hideGroupImageLoading();
        }
        if (mOwnerImage != null && !mOwnerImage.isEmpty()) {
            if (isSocialAccount) {
                ImageLoader.loadImage(this, true, false, mOwnerImage, 0, R.drawable.user_image_placeholder, mOwnerImageView, null);
            } else {
                String encodedImage = Uri.encode(mOwnerImage);
                ImageLoader.loadImage(this, true, false, Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER +
                        encodedImage, 0, R.drawable.user_image_placeholder, mOwnerImageView, null);
            }
        } else {
            ImageLoader.loadImage(this, true, true, null, R.drawable.no_image, R.drawable.user_image_placeholder, mOwnerImageView, null);
        }

        if (mGroupColor != null && !mGroupColor.isEmpty()) {
            mGroupBackgroundColor.setBackgroundColor(Color.parseColor(mGroupColor));
            mJoinGroupButton.setTextColor(Color.parseColor(mGroupColor));
            mAddMessageToGroup.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(mGroupColor)));
            mAddMessageToGroup.setRippleColor(Color.parseColor("#cccccc"));
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initCount() {
        mGroupSingleFollowersCount.setText(mCount + " " + getString(R.string.participantText));
        if (!mCount.equals("0")) {
            mGroupSingleFollowersCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getParticipants();
                }
            });
        }
    }

    private void initButtons() {
        if (!isMember) {
            if (mSharedHelper.getUserId().equals(mGroupOwnerId)) {
                mJoinGroupButton.setVisibility(View.GONE);
                mAddMessageToGroup.setVisibility(View.VISIBLE);
                CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mAddMessageToGroup.getLayoutParams();
                p.setBehavior(new ScrollAwareFABButtonehavior(this));
                mAddMessageToGroup.setLayoutParams(p);
            } else {
                mJoinGroupButton.setVisibility(View.VISIBLE);
                mAddMessageToGroup.setVisibility(View.GONE);
                mJoinGroupButton.setText(getString(R.string.joinGroup));
            }
        } else {
            mJoinGroupButton.setVisibility(View.GONE);
            mAddMessageToGroup.setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) mAddMessageToGroup.getLayoutParams();
            p.setBehavior(new ScrollAwareFABButtonehavior(this));
            mAddMessageToGroup.setLayoutParams(p);
        }
    }


    public boolean isRecyclerScrollable(RecyclerView recyclerView) {
        return recyclerView.computeHorizontalScrollRange() > recyclerView.getWidth() || recyclerView.computeVerticalScrollRange() > recyclerView.getHeight();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey(NOTIFICATION_RECEIVED_GROUP_BUNDLE)) {
            Bundle bundle = extras.getBundle(NOTIFICATION_RECEIVED_GROUP_BUNDLE);
            String groupId = bundle.getString("groupId");
            if (!mGroupId.equals(groupId)) {
                Intent relaunchIntent = new Intent(this, SingleGroupView.class);
                relaunchIntent.putExtra(NOTIFICATION_RECEIVED_GROUP_BUNDLE, bundle);
                finish();
                startActivity(relaunchIntent);
            } else {
                getGroupMessages();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getParticipantsFromServer();
    }

    private void leaveGroup() {
        progressDialog.setTitle(getString(R.string.leaveGroup));
        progressDialog.setMessage(getString(R.string.leaving));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        makeRequest(Retrofit.getInstance().getInkService().groupOptions(Constants.GROUP_OPTIONS_LEAVE, mGroupId, mSharedHelper.getUserId(), ""), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        progressDialog.dismiss();
                        LocalBroadcastManager.getInstance(SingleGroupView.this).sendBroadcast(new Intent(getPackageName() + "Groups"));
                        sendBroadcast(new Intent("com.ink.va.Groups"));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(mGroupImageView, getString(R.string.serverErrorText), Snackbar.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                progressDialog.dismiss();
            }
        });

    }

    private void hideGroupImageLoading() {
        groupImageLoading.setVisibility(View.GONE);
    }

    private void getParticipants() {
        if (memberModels != null) {
            memberModels.clear();
        }
        if (User.get().getParticipantIds() != null) {
            User.get().getParticipantIds().clear();
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(SingleGroupView.this);
        builder.setTitle(getString(R.string.membersTitle));
        View memberView = getLayoutInflater().inflate(R.layout.member_dialog_view, null);
        RecyclerView recyclerView = (RecyclerView) memberView.findViewById(R.id.memberRecycler);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(itemAnimator);
        final AVLoadingIndicatorView membersLoading = (AVLoadingIndicatorView) memberView.findViewById(R.id.membersLoading);
        recyclerView.setAdapter(memberAdapter);
        memberAdapter.setOnClickListener(new RecyclerItemClickListener() {
            @Override
            public void onItemClicked(int position, View view) {
                if (memberModels.get(position).isIncognito()) {
                    DialogUtils.showDialog(SingleGroupView.this, getString(R.string.incognito), getString(R.string.incognito_hint), true,
                            null, false, null);
                } else {
                    if (memberModels.get(position).getMemberId().equals(mSharedHelper.getUserId())) {
                        startActivity(new Intent(getApplicationContext(), MyProfile.class));
                    } else {
                        String name = memberModels.get(position).getMemberName();
                        String[] splited = name.split("\\s+");
                        String firstName = splited[0];
                        String lastName = splited[1];
                        Intent intent = new Intent(getApplicationContext(), OpponentProfile.class);
                        intent.putExtra("id", memberModels.get(position).getMemberId());
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("isFriend", memberModels.get(position).isFriend());
                        intent.putExtra("lastName", lastName);
                        startActivity(intent);
                    }
                }

            }

            @Override
            public void onItemLongClick(Object object) {

            }

            @Override
            public void onAdditionalItemClick(int position, View view) {

            }

            @Override
            public void onAdditionalItemClicked(Object object) {

            }

            @Override
            public void onItemClicked(Object object) {

            }
        });

        builder.setView(memberView);
        builder.setNegativeButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
        isParticipantsLoaded = false;
        makeRequest(Retrofit.getInstance().getInkService().getParticipants(mSharedHelper.getUserId(), mGroupId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                isParticipantsLoaded = true;
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.optJSONArray("members");
                        List<String> participantIds = new LinkedList<String>();
                        if (jsonArray.length() <= 0) {
                            Snackbar.make(membersLoading, getString(R.string.noMembers), Snackbar.LENGTH_LONG).show();
                            membersLoading.setVisibility(View.GONE);
                            participantIds.add("dummy");
                            return;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eachObject = jsonArray.optJSONObject(i);
                            String memberId = eachObject.optString("participant_id");
                            participantIds.add(memberId);
                            String memberName = eachObject.optString("participant_name");
                            String memberImage = eachObject.optString("participant_image");
                            String memberItemId = eachObject.optString("participant_item_id");
                            String memberGroupId = eachObject.optString("participant_group_id");
                            String isFriend = eachObject.optString("isFriend");
                            boolean isIncognito = eachObject.optBoolean("isIncognito");
                            memberModel = new MemberModel(Boolean.valueOf(isFriend), isIncognito, memberId, memberName, memberImage, memberItemId
                                    , memberGroupId);
                            memberModels.add(memberModel);
                            memberAdapter.notifyDataSetChanged();
                        }

                        if (!mSharedHelper.getUserId().equals(mGroupOwnerId)) {
                            participantIds.add(mGroupOwnerId);
                        }
                        User.get().setParticipantIds(participantIds);
                        participantIds = null;
                        membersLoading.setVisibility(View.GONE);
                    } else {
                        getParticipants();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                isParticipantsLoaded = true;
                membersLoading.setVisibility(View.GONE);
            }
        });
    }

    private void getParticipantsFromServer() {
        if (User.get().getParticipantIds() != null) {
            User.get().getParticipantIds().clear();
        }
        isParticipantsLoaded = false;
        makeRequest(Retrofit.getInstance().getInkService().getParticipants(mSharedHelper.getUserId(), mGroupId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                isParticipantsLoaded = true;
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.optJSONArray("members");
                        List<String> participantIds = new LinkedList<>();
                        if (jsonArray.length() <= 0) {
                            participantIds.add("dummy");
                            return;
                        }
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eachObject = jsonArray.optJSONObject(i);
                            String memberId = eachObject.optString("participant_id");
                            participantIds.add(memberId);
                            String memberName = eachObject.optString("participant_name");
                            String memberImage = eachObject.optString("participant_image");
                            String memberItemId = eachObject.optString("participant_item_id");
                            String memberGroupId = eachObject.optString("participant_group_id");
                            String isFriend = eachObject.optString("isFriend");
                            boolean isIncognito = eachObject.optBoolean("isIncognito");
                            memberModel = new MemberModel(Boolean.valueOf(isFriend), isIncognito, memberId, memberName, memberImage, memberItemId
                                    , memberGroupId);
                            memberModels.add(memberModel);
                            memberAdapter.notifyDataSetChanged();
                        }

                        if (!mSharedHelper.getUserId().equals(mGroupOwnerId)) {
                            participantIds.add(mGroupOwnerId);
                        }
                        User.get().setParticipantIds(participantIds);
                        participantIds = null;
                    } else {
                        Toast.makeText(socketService, getString(R.string.serverErrorText), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                isParticipantsLoaded = true;
            }
        });
    }

    private void requestJoin() {
        makeRequest(Retrofit.getInstance().getInkService().requestJoin(mGroupOwnerId,
                mSharedHelper.getUserId(), mSharedHelper.getFirstName() + " " + mSharedHelper.getLastName(),
                mSharedHelper.getImageLink(), mGroupId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        mJoinGroupButton.setText(getString(R.string.pending));
                        Snackbar.make(mJoinGroupButton, getString(R.string.successfullyRequested), Snackbar.LENGTH_SHORT).show();
                        isRequested = true;
                        JSONObject requestJson = new JSONObject();
                        requestJson.put("destinationId", mGroupOwnerId);
                        requestJson.put("requesterId", mSharedHelper.getUserId());
                        requestJson.put("groupName", mGroupName);
                        requestJson.put("requesterName", mSharedHelper.getFirstName() + " " + mSharedHelper.getLastName());
                        socketService.emit(EVENT_REQUEST_GROUP_JOIN, requestJson);
                        requestJson = null;
                    } else {
                        requestJoin();
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

    @Override
    public void onServiceConnected(SocketService socketService) {
        super.onServiceConnected(socketService);
        this.socketService = socketService;
    }

    @OnClick(R.id.groupImage)
    public void groupImageView() {
        Intent intent = new Intent(getApplicationContext(), FullscreenActivity.class);
        if (mGroupImage != null && !mGroupImage.isEmpty()) {
            String encodedFileName = Uri.encode(mGroupImage);
            intent.putExtra("link", Constants.MAIN_URL + Constants.GROUP_IMAGES_FOLDER + encodedFileName);
            startActivity(intent);
        }
    }

    @OnClick(R.id.ownerImageView)
    public void ownerImageView() {
        if (mGroupOwnerId.equals(mSharedHelper.getUserId())) {
            Intent intent = new Intent(getApplicationContext(), MyProfile.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), OpponentProfile.class);
            String parts[] = mGroupOwnerName.split("\\s");
            String firstName = parts[0];
            String lastName = parts[1];
            intent.putExtra("id", mGroupOwnerId);
            intent.putExtra("firstName", firstName);
            intent.putExtra("lastName", lastName);
            intent.putExtra("isFriend", isFriendWithOwner);
            startActivity(intent);
        }
    }

    @OnClick(R.id.addMessageToGroup)
    public void addMessage() {
        if (User.get().getParticipantIds().isEmpty() && !isParticipantsLoaded) {
            Toast.makeText(socketService, getString(R.string.waitTillLoad), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), CreateGroupPost.class);
        intent.putExtra("groupNme", mGroupName);
        intent.putExtra("groupId", mGroupId);
        intent.putExtra("groupName", mGroupName);
        intent.putExtra("groupColor", mGroupColor);
        intent.putExtra("groupImage", mGroupImage);
        intent.putExtra("groupDescription", mGroupDescription);
        intent.putExtra("groupOwnerId", mGroupOwnerId);
        intent.putExtra("groupOwnerName", mGroupOwnerName);
        intent.putExtra("count", mCount);
        intent.putExtra("ownerImage", mOwnerImage);
        intent.putExtra("isSocialAccount", isSocialAccount);
        intent.putExtra("isMember", isMember);
        intent.putExtra("isFriend", isFriendWithOwner);

        startActivity(intent);
        overridePendingTransition(R.anim.activity_scale_up, R.anim.activity_scale_down);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (hasAnythingChanged) {
                    LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(getPackageName() + "Groups"));
                }
                finish();
                break;
            case R.id.editGroup:
                showEditDialog();
                break;
            case R.id.deleteGroup:
                deleteGroup();
                break;
            case R.id.leaveGroup:
                showWarning();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showEditDialog() {
        View editGroupView = getLayoutInflater().inflate(R.layout.edit_group_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleGroupView.this);
        builder.setView(editGroupView);
        builder.setTitle(getString(R.string.editGroup));

        final EditText groupName = (EditText) editGroupView.findViewById(R.id.editGroupName);
        final EditText groupDescription = (EditText) editGroupView.findViewById(R.id.editGroupDescription);
        groupName.setText(mGroupName);
        groupDescription.setText(mGroupDescription);

        builder.setPositiveButton(getString(R.string.saveText), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!groupName.getText().toString().trim().isEmpty() && !groupDescription.getText().toString().trim().isEmpty()) {
                    alertDialog.dismiss();
                    saveGroupChanges(Constants.GROUP_TYPE_EDIT, groupName.getText().toString().trim(), groupDescription.getText().toString().trim());
                } else {
                    if (groupName.getText().toString().trim().isEmpty()) {
                        groupName.setError(getString(R.string.groupNameError));
                    }
                    if (groupDescription.getText().toString().trim().isEmpty()) {
                        groupDescription.setError(getString(R.string.groupDescriptionError));
                    }
                }
            }
        });

        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void saveGroupChanges(final String type, final String groupName, final String groupDescription) {
        if (type.equals(Constants.GROUP_TYPE_DELETE)) {
            progressDialog.setTitle(getString(R.string.deleting));
            progressDialog.setMessage(getString(R.string.deletingGroup));
        } else {
            progressDialog.setTitle(getString(R.string.saving));
            progressDialog.setMessage(getString(R.string.savingChanges));
        }
        progressDialog.show();
        makeRequest(Retrofit.getInstance().getInkService().groupOptions(type, mGroupId, groupName, groupDescription), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (type.equals(Constants.GROUP_TYPE_EDIT)) {
                        if (success) {
                            mCollapsingToolbar.setTitle(groupName);
                            mGroupSingleDescription.setText(groupDescription);
                            progressDialog.dismiss();
                            Snackbar.make(mOwnerImageView, getString(R.string.groupInformationUpdated), Snackbar.LENGTH_SHORT).show();
                            hasAnythingChanged = true;
                        }
                    } else if (type.equals(Constants.GROUP_TYPE_DELETE)) {
                        if (success) {
                            progressDialog.dismiss();
                            LocalBroadcastManager.getInstance(SingleGroupView.this).sendBroadcast(new Intent(getPackageName() + "Groups"));
                            sendBroadcast(new Intent("com.ink.va.Groups"));
                            finish();
                        }
                    }
                } catch (IOException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                progressDialog.dismiss();
            }
        });
    }


    private void deleteGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SingleGroupView.this);
        builder.setTitle(getString(R.string.deleteGroup));
        builder.setMessage(getString(R.string.deleteGroupWarning));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveGroupChanges(Constants.GROUP_TYPE_DELETE, "", "");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mSharedHelper.getUserId().equals(mGroupOwnerId)) {
            getMenuInflater().inflate(R.menu.single_group_menu, menu);
        } else if (isMember) {
            getMenuInflater().inflate(R.menu.leave_group_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private void getGroupMessages() {
        if (noGroupMessageLayout.getVisibility() == View.VISIBLE) {
            noGroupMessageLayout.setVisibility(View.GONE);
        }
        groupMessagesLoading.setVisibility(View.VISIBLE);
        if (groupMessagesModels != null) {
            groupMessagesModels.clear();
        }
        groupMessagesAdapter.notifyDataSetChanged();
        makeRequest(Retrofit.getInstance().getInkService().getGroupMessages(mGroupId, mSharedHelper.getUserId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        boolean hasMessages = jsonObject.optBoolean("hasMessages");
                        isRequested = jsonObject.optBoolean("alreadyRequested");
                        isMember = jsonObject.optBoolean("isMember");
                        mCount = jsonObject.optString("participants");
                        initCount();
                        mJoinGroupButton.setEnabled(true);
                        if (isRequested) {
                            mJoinGroupButton.setText(getString(R.string.pending));
                        } else {
                            initButtons();
                            invalidateOptionsMenu();
                            getParticipantsFromServer();
                        }
                        if (hasMessages) {
                            noGroupMessageLayout.setVisibility(View.GONE);
                            JSONArray messagesArray = jsonObject.optJSONArray("messages");
                            for (int i = 0; i < messagesArray.length(); i++) {
                                JSONObject eachObject = messagesArray.optJSONObject(i);
                                String groupId = eachObject.optString("group_id");
                                String groupMessage = eachObject.optString("group_message");
                                String senderId = eachObject.optString("sender_id");
                                String senderImage = eachObject.optString("sender_image");
                                String senderName = eachObject.optString("sender_name");
                                String groupMessageId = eachObject.optString("group_message_id");
                                String isFriend = eachObject.optString("isFriend");
                                boolean isIncognito = eachObject.optBoolean("isIncognito");
                                String fileName = eachObject.optString("file_name");
                                groupMessagesModel = new GroupMessagesModel(Boolean.valueOf(isFriend), isIncognito, groupId, groupMessage,
                                        senderId, senderImage, senderName, groupMessageId, isRequested, fileName);
                                groupMessagesModels.add(groupMessagesModel);
                                groupMessagesAdapter.notifyDataSetChanged();
                            }
                            hideMessageLoading();
                        } else {
                            hideMessageLoading();
                            noGroupMessageLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        getGroupMessages();
                    }

                } catch (IOException e) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                hideMessageLoading();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (hasAnythingChanged) {
            LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(getPackageName() + "Groups"));
        }
        sendBroadcast(new Intent("com.ink.va.Groups"));
        super.onBackPressed();
    }

    private void hideMessageLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
        groupMessagesLoading.setVisibility(View.GONE);
    }

    @Override
    public void onItemClicked(int position, View view) {


        GroupMessagesModel singleModel = groupMessagesModels.get(position);

        if (!singleModel.getFileName().isEmpty()) {
            String encoded = Uri.encode(singleModel.getFileName());
            String url = Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + encoded;
            Intent intent = new Intent(this, FullscreenActivity.class);
            intent.putExtra("link", url);
            startActivity(intent);
        }


    }

    private void openProfile(GroupMessagesModel singleModel) {
        if (singleModel.isIncognito()) {
            DialogUtils.showDialog(this, getString(R.string.incognito), getString(R.string.incognito_hint), true,
                    null, false, null);
        } else {
            if (singleModel.getSenderId().equals(mSharedHelper.getUserId())) {
                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                startActivity(intent);
            } else {
                String name = singleModel.getSenderName();
                String[] splited = name.split("\\s+");
                String firstName = splited[0];
                String lastName = splited[1];
                Intent intent = new Intent(getApplicationContext(), OpponentProfile.class);
                intent.putExtra("id", singleModel.getSenderId());
                intent.putExtra("firstName", firstName);
                intent.putExtra("isFriend", singleModel.isFriend());
                intent.putExtra("lastName", lastName);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemLongClick(Object object) {

    }

    @Override
    public void onAdditionalItemClick(int position, View view) {
        final GroupMessagesModel groupMessagesModel = groupMessagesModels.get(position);
        DialogUtils.showPopUp(SingleGroupView.this, view, new ItemClickListener<MenuItem>() {
            @Override
            public void onItemClick(MenuItem clickedItem) {
                switch (clickedItem.getItemId()) {
                    case 0:
                        InputField.createInputFieldView(SingleGroupView.this, new InputField.ClickHandler() {
                            @Override
                            public void onPositiveClicked(Object... result) {
                                snackbar.show();
                                AlertDialog dialog = (AlertDialog) result[1];
                                dialog.dismiss();
                                updateGroupMessage(String.valueOf(result[0]), groupMessagesModel.getGroupMessageId());
                            }

                            @Override
                            public void onNegativeClicked(Object... result) {
                                AlertDialog dialog = (AlertDialog) result[1];
                                dialog.dismiss();
                            }
                        }, groupMessagesModel.getGroupMessage(), null, null);
                        break;
                    case 1:
                        snackbar.setText(getString(R.string.deleting));
                        deleteComment(groupMessagesModel.getGroupMessageId(), groupMessagesModel.getFileName());
                        break;
                }
            }
        }, getString(R.string.editMessage), getString(R.string.deleteMessage));
    }

    @Override
    public void onAdditionalItemClicked(Object object) {

    }

    @Override
    public void onItemClicked(Object object) {
        GroupMessagesModel groupMessagesModel = groupMessagesModels.get((int) object);
        openProfile(groupMessagesModel);
    }

    private void updateGroupMessage(final String message, final String messageId) {
        makeRequest(Retrofit.getInstance().getInkService().changeGroupMessages(
                "", Constants.GROUP_MESSAGES_TYPE_EDIT,
                message, messageId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        getGroupMessages();
                        snackbar.setText(getString(R.string.changesWasSaved));
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    } else {
                        snackbar.dismiss();
                    }
                } catch (IOException e) {
                    snackbar.dismiss();
                    e.printStackTrace();
                } catch (JSONException e) {
                    snackbar.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                snackbar.dismiss();
            }
        });
    }

    private void deleteComment(final String messageId, String fileName) {
        makeRequest(Retrofit.getInstance().getInkService().changeGroupMessages(
                fileName, Constants.GROUP_MESSAGES_TYPE_DELETE,
                "", messageId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        getGroupMessages();
                        snackbar.setText(getString(R.string.messageDeleted));
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                    } else {
                        snackbar.show();
                    }
                } catch (IOException e) {
                    snackbar.show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    snackbar.show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                snackbar.show();
            }
        });
    }

    private void showWarning() {
        if (!mSharedHelper.getUserId().equals(mGroupOwnerId)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SingleGroupView.this);
            builder.setTitle(getString(R.string.leaveGroup));
            builder.setMessage(getString(R.string.leaveGroupMessage));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    leaveGroup();
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
    }

    @Override
    protected void onDestroy() {
        singleGroupAppBar.removeOnOffsetChangedListener(this);
        super.onDestroy();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
    }

    @Override
    public void onRefresh() {
        getGroupMessages();
        getParticipantsFromServer();
    }
}
