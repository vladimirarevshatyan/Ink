package kashmirr.social.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionMenu;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.github.nkzawa.socketio.client.IO;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationMode;
import com.kashmirr.social.R;
import com.koushikdutta.ion.bitmap.BitmapDecodeException;
import com.pollfish.interfaces.PollfishClosedListener;
import com.pollfish.interfaces.PollfishOpenedListener;
import com.pollfish.interfaces.PollfishSurveyCompletedListener;
import com.pollfish.interfaces.PollfishSurveyNotAvailableListener;
import com.pollfish.interfaces.PollfishSurveyReceivedListener;
import com.pollfish.interfaces.PollfishUserNotEligibleListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fab.FloatingActionButton;
import io.smooch.ui.ConversationActivity;
import it.sephiroth.android.library.picasso.Picasso;
import it.sephiroth.android.library.picasso.Target;
import kashmirr.social.callbacks.GeneralCallback;
import kashmirr.social.fragments.Feed;
import kashmirr.social.fragments.MyFriends;
import kashmirr.social.interfaces.AccountDeleteListener;
import kashmirr.social.interfaces.ColorChangeListener;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.managers.SipManagerUtil;
import kashmirr.social.models.CoinsResponse;
import kashmirr.social.models.FriendModel;
import kashmirr.social.service.SendTokenService;
import kashmirr.social.service.SocketService;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.DeviceChecker;
import kashmirr.social.utils.DialogUtils;
import kashmirr.social.utils.DimDialog;
import kashmirr.social.utils.ErrorCause;
import kashmirr.social.utils.FileUtils;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.Keyboard;
import kashmirr.social.utils.Notification;
import kashmirr.social.utils.PermissionsChecker;
import kashmirr.social.utils.PollFish;
import kashmirr.social.utils.RealmHelper;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import kashmirr.social.utils.User;
import lombok.Getter;
import me.leolin.shortcutbadger.ShortcutBadger;
import okhttp3.ResponseBody;

import static kashmirr.social.utils.Constants.FACEBOOK_GRAPH_FIRST_URL;
import static kashmirr.social.utils.Constants.FACEBOOK_GRAPH_LAST_URL;
import static kashmirr.social.utils.Constants.NOTIFICATION_AUTO_REDIRECT_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.NOTIFICATION_BUNDLE_EXTRA_KEY;
import static kashmirr.social.utils.Constants.NOTIFICATION_MESSAGE_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.NOTIFICATION_POST_ID_KEY;


public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, AccountDeleteListener, PollfishSurveyCompletedListener, PollfishOpenedListener,
        PollfishClosedListener, PollfishSurveyReceivedListener,
        PollfishSurveyNotAvailableListener, PollfishUserNotEligibleListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final int PROFILE_RESULT_CODE = 836;
    private static final int USE_SIP_REQUEST_PERMISSION = 10;


    private FloatingActionMenu mFab;
    private ImageView mProfileImage;
    private ActionBarDrawerToggle toggle;
    private TextView coinsText;
    private SharedHelper mSharedHelper;
    private FloatingActionButton mMessages;
    private FloatingActionButton mNewPost;
    private Feed mFeed;
    private Toolbar mToolbar;
    private DrawerLayout mDrawer;
    public static String PROFILE;
    public static String FEED;
    public static String MESSAGES;
    public static String GROUPS;
    public static String FRIENDS;
    public static String SETTINGS;
    private TextView mUserNameTV;
    private Class<?> mLastClassToOpen;
    private boolean shouldOpenActivity;
    private FloatingActionButton mMakePost;
    private ProgressDialog progressDialog;
    private Menu menuItem;
    private boolean activityForResult;
    private int lastRequestCode;
    private ColorChangeListener colorChangeListener;
    private RelativeLayout panelHeader;
    private TextView messagesCountTV;
    private PollFish pollFish;
    public String currentScreen;
    public static final String SCREEN_FEED = "feed";
    public static final String SCREEN_FRIENDS = "friends";
    @Getter
    private SocketService socketService;
    private boolean openInstaBug;
    private Gson friendGson;
    private SipManagerUtil sipManagerUtil;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        pollFish = PollFish.get();
        pollFish.setActivity(this);
        sipManagerUtil = SipManagerUtil.getManager();
        sipManagerUtil.setContext(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        PROFILE = getString(R.string.profileText);
        FEED = getString(R.string.feedText);
        MESSAGES = getString(R.string.messageText);
        GROUPS = getString(R.string.groupsText);
        FRIENDS = getString(R.string.friendsText);
        SETTINGS = getString(R.string.settingsString);
        mToolbar.setTitle(FEED);
        mSharedHelper = new SharedHelper(this);
        friendGson = new Gson();
        com.github.nkzawa.socketio.client.Socket socket = null;

        try {
            socket = IO.socket(Constants.FILE_SHARING_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        initService();
//        checkSipPermission();

        if (!mSharedHelper.isSecurityQuestionSet() && isAccountRecoverable()) {
            View warningView = getLayoutInflater().inflate(R.layout.app_warning_view, null);
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(warningView);
            Button warningButton = (Button) warningView.findViewById(R.id.warningButton);
            ImageView closeWarning = (ImageView) warningView.findViewById(R.id.closeWarning);
            warningButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.hide();
                    startActivity(new Intent(getApplicationContext(), SecurityQuestion.class));
                }
            });
            closeWarning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog.hide();
                }
            });
            bottomSheetDialog.show();
        }

        checkNotification(getIntent());

        LocalBroadcastManager.getInstance(this).registerReceiver(feedUpdateReceiver, new IntentFilter(getPackageName() + "HomeActivity"));
        User.get().setUserName(mSharedHelper.getFirstName() + " " + mSharedHelper.getLastName());
        User.get().setUserId(mSharedHelper.getUserId());
        mFab = (FloatingActionMenu) findViewById(R.id.fab);
        mMessages = (FloatingActionButton) findViewById(R.id.messages);
        mMakePost = (FloatingActionButton) findViewById(R.id.makePost);
        mNewPost = (FloatingActionButton) findViewById(R.id.makePost);
        mFeed = Feed.newInstance();
        mMessages.setOnClickListener(this);
        mMakePost.setOnClickListener(this);
        mNewPost.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.loggingout));
        progressDialog.setMessage(getString(R.string.loggingoutPleaseWait));
        setOnAccountDeleteListener(this);
        mSharedHelper.putShouldLoadImage(true);

        FileUtils.deleteDirectoryTree(getApplicationContext().getCacheDir());

        checkIsWarned();
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                if (shouldOpenActivity) {
                    shouldOpenActivity = false;
                    if (activityForResult) {
                        activityForResult = false;
                        startActivityForResult(new Intent(getApplicationContext(), getLastKnownClass()), lastRequestCode);
                    } else {
                        startActivity(new Intent(getApplicationContext(), getLastKnownClass()));
                    }

                } else if (openInstaBug) {
                    openInstaBug = false;
                    openInstBug();
                }
                super.onDrawerClosed(view);
            }

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                Keyboard.hideKeyboard(HomeActivity.this);
                super.onDrawerOpened(drawerView);
            }
        };
        if (mDrawer != null) {
            mDrawer.addDrawerListener(toggle);
        }
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }

        messagesCountTV = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.messages));

        View headerView = navigationView.getHeaderView(0);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, mFeed).commit();
        currentScreen = SCREEN_FEED;


        mProfileImage = (ImageView) headerView.findViewById(R.id.profileImage);
        coinsText = (TextView) headerView.findViewById(R.id.coinsText);
        getCoins();
        mProfileImage.setOnClickListener(this);
        mUserNameTV = (TextView) headerView.findViewById(R.id.userNameTextView);
        panelHeader = (RelativeLayout) headerView.findViewById(R.id.panelHeader);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void checkSipPermission() {
        if (!PermissionsChecker.isSipPermissionGranted(this)) {
            DialogUtils.showCustomDialog(this, getString(R.string.sipPermissionNeeded),
                    getString(R.string.proceed), getString(R.string.actionRequired), new DialogUtils.DialogListener() {
                        @Override
                        public void onNegativeClicked() {

                        }

                        @Override
                        public void onDialogDismissed() {

                        }

                        @Override
                        public void onPositiveClicked() {
                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.USE_SIP}, USE_SIP_REQUEST_PERMISSION);
                        }
                    });
        } else {
            startSip();
        }
    }

    public void initService() {
        try {
            stopService(new Intent(this, SocketService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(new Intent(getApplicationContext(), SocketService.class));
    }


    private void getFriends() {
        final List<String> friendIds = new LinkedList<>();
        try {
            makeRequest(Retrofit.getInstance().getInkService().getFriends(mSharedHelper.getUserId()), null, new RequestCallback() {
                @Override
                public void onRequestSuccess(Object result) {
                    try {
                        String responseString = ((ResponseBody) result).string();
                        try {
                            JSONObject jsonObject = new JSONObject(responseString);
                            boolean success = jsonObject.optBoolean("success");
                            if (success) {
                                JSONArray friendsArray = jsonObject.optJSONArray("friends");
                                FriendModel[] friendModels = friendGson.fromJson(friendsArray.toString(), FriendModel[].class);
                                for (FriendModel friendModel : friendModels) {
                                    friendIds.add(friendModel.getFriendId());
                                }
                                User.get().setFriendIds(friendIds);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRequestFailed(Object[] result) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initializeCountDrawer(final TextView messages) {
        messages.setGravity(Gravity.CENTER_VERTICAL);
        if (mSharedHelper.getLeftSlidingPanelHeaderColor() != null) {
            messages.setTextColor(Color.parseColor(mSharedHelper.getLeftSlidingPanelHeaderColor()));
        } else {
            messages.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        RealmHelper.getInstance().getNotificationCount(new RealmHelper.QueryReadyListener() {
            @Override
            public void onQueryReady(Object... results) {
                final int notificationCount = (int) results[0];
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messages.setText(
                                String.valueOf(notificationCount != 0 ?
                                        notificationCount : "")

                        );
                        if (notificationCount == 0) {
                            ShortcutBadger.removeCount(getApplicationContext());
                        } else {
                            messages.setText(
                                    String.valueOf(notificationCount != 0 ?
                                            notificationCount : ""));
                            ShortcutBadger.applyCount(getApplicationContext(), notificationCount);
                            Snackbar.make(mToolbar, getString(R.string.unreadMessage), Snackbar.LENGTH_LONG).setAction(
                                    getString(R.string.view), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startActivity(new Intent(getApplicationContext(), Messages.class));
                                        }
                                    }
                            ).show();
                        }
                    }
                });

            }
        });
    }

    private BroadcastReceiver feedUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean feedUpdateReceiver = intent.getExtras() != null ? intent.getExtras().containsKey("updateFromPost") ? intent.getExtras().getBoolean("updateFromPost") : false : false;
            if (feedUpdateReceiver) {
                mFeed.onRefresh();
            }
        }
    };

    private void checkIsWarned() {
        if (!mSharedHelper.isDeviceWarned()) {
            if (DeviceChecker.isHuawei()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(getString(R.string.caution));
                builder.setMessage(getString(R.string.huaweiWarning));
                builder.setCancelable(false);
                builder.setPositiveButton(getString(R.string.dontShowAgain), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //just override for dialog not to close automatically
                    }
                });
                builder.setNegativeButton(getString(R.string.navigateToSettings), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //just override for dialog not to close automatically
                    }
                });
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        mSharedHelper.putWarned(true);
                    }
                });
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));

                    }
                });
            }
        }
    }


    private void openInstBug() {
        Instabug.invoke(InstabugInvocationMode.NEW_FEEDBACK);
    }


    private void openEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"contact@vaentertaiment.xyz"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Support Needed");
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkNotification(intent);
    }

    private void checkNotification(Intent intent) {
        if (intent != null && intent.getExtras() != null) {

            Bundle notificationBundle = intent.getBundleExtra(NOTIFICATION_BUNDLE_EXTRA_KEY);
            if (notificationBundle != null) {
                String postId = notificationBundle.getString(NOTIFICATION_POST_ID_KEY);
            } else {
                String jsonObject = intent.getExtras().getString(NOTIFICATION_MESSAGE_BUNDLE_KEY);
                boolean autoRedirect = intent.getExtras().getBoolean(NOTIFICATION_AUTO_REDIRECT_BUNDLE_KEY);
                if (autoRedirect) {
                    Intent chatIntent = new Intent(this, Chat.class);
                    chatIntent.putExtra(NOTIFICATION_MESSAGE_BUNDLE_KEY, jsonObject.toString());
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    chatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chatIntent);
                }
            }
        }
    }

    @Override
    public void onServiceConnected(SocketService socketService) {
        super.onServiceConnected(socketService);
        this.socketService = socketService;
    }

    @OnClick(R.id.earnCoins)
    public void earnClicked() {
        mFab.close(true);
        initPollFish();
    }

    private void getCoins() {
        coinsText.setText(getString(R.string.updating));
        makeRequest(Retrofit.getInstance().getInkService().getCoins(mSharedHelper.getUserId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                Gson gson = new Gson();
                try {
                    CoinsResponse coinsResponse = gson.fromJson(((ResponseBody) result).string(), CoinsResponse.class);
                    if (coinsResponse.success) {
                        User.get().setCoins(coinsResponse.coins);
                        coinsText.setText(getString(R.string.coinsText, coinsResponse.coins));
                        User.get().setCoinsLoaded(true);
                        User.get().setCoins(coinsResponse.coins);
                    } else {
                        getCoins();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {

            }
        });
    }

    private Class<?> getLastKnownClass() {
        return mLastClassToOpen;
    }


    public FloatingActionMenu getHomeFab() {
        return mFab;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentScreen.equals(SCREEN_FRIENDS)) {
                openFeeds();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        menuItem = menu;
        setHomeActivityColors();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.notifications:
                if (menuItem != null) {
                    if (mSharedHelper.getNotificationIconColor() == null) {
                        menuItem.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(),
                                R.drawable.notification_icon));
                    } else {
                        menuItem.getItem(2).getIcon().setColorFilter(Color.parseColor(mSharedHelper.getNotificationIconColor()),
                                PorterDuff.Mode.SRC_ATOP);
                    }
                }
                startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                break;
            case R.id.stickerShop:
                startActivity(new Intent(getApplicationContext(), Shop.class));
                break;
            case R.id.buyCoins:
                startActivityForResult(new Intent(getApplicationContext(), BuyCoins.class), Constants.BUY_COINS_REQUEST_CODE);
                break;

            case R.id.badgeShop:
                startActivity(new Intent(getApplicationContext(), BadgeShop.class));
                break;

            case R.id.news:
                startActivity(new Intent(getApplicationContext(), NewsAndTrendsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        openInstaBug = false;
        switch (id) {
            case R.id.profile:
                shouldOpenActivity = true;
                lastRequestCode = PROFILE_RESULT_CODE;
                setLastClassToOpen(MyProfile.class, true);
                break;
            case R.id.myCollection:
                shouldOpenActivity = true;
                setLastClassToOpen(MyCollection.class, false);
                break;
            case R.id.myRequests:
                shouldOpenActivity = true;
                setLastClassToOpen(RequestsView.class, false);
                break;
            case R.id.vipChat:
                callToVipServer(Constants.TYPE_ENTER_VIP);
                break;
            case R.id.games:
                shouldOpenActivity = true;
                setLastClassToOpen(GamesActivity.class, false);
                break;
            case R.id.whoViewed:
                shouldOpenActivity = true;
                setLastClassToOpen(WhoViewedActivity.class, false);
                break;
            case R.id.feeds:
                shouldOpenActivity = false;
                openFeeds();
                break;

            case R.id.messages:
                shouldOpenActivity = true;
                setLastClassToOpen(Messages.class, false);
                break;

            case R.id.groups:
                setLastClassToOpen(Groups.class, false);
                shouldOpenActivity = true;
                break;

            case R.id.chatRoulette:
                setLastClassToOpen(ChatRoulette.class, false);
                shouldOpenActivity = true;
                break;

            case R.id.friends:
                shouldOpenActivity = false;
                if (!mToolbar.getTitle().equals(FRIENDS)) {
                    mToolbar.setTitle(getString(R.string.friendsText));
                    getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, MyFriends.newInstance()).commit();
                    currentScreen = SCREEN_FRIENDS;
                }
                break;

            case R.id.music:
                shouldOpenActivity = true;
                setLastClassToOpen(Music.class, false);
                break;
            case R.id.imageEdit:
                shouldOpenActivity = true;
                setLastClassToOpen(ImageEditor.class, false);
                break;

            case R.id.settings:
                shouldOpenActivity = true;
                setLastClassToOpen(Settings.class, true);
                break;

            case R.id.customizeApp:
                shouldOpenActivity = true;
                lastRequestCode = Constants.REQUEST_CUSTOMIZE_MADE;
                setLastClassToOpen(CustomizeLook.class, true);
                break;

            case R.id.about:
                shouldOpenActivity = true;
                setLastClassToOpen(About.class, true);
                break;

            case R.id.nav_share:
                shouldOpenActivity = false;

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.kashmirr.social");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_ink_with)));
                break;

            case R.id.sendFeedback:
                shouldOpenActivity = false;
                openInstaBug = true;
                break;

            case R.id.contactSupport:
                shouldOpenActivity = false;
                ConversationActivity.show(this);
                break;

            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle(getString(R.string.warning));
                builder.setMessage(getString(R.string.logoutWaring));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        stopService(new Intent(HomeActivity.this, SocketService.class));
                        logoutUser();
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFeeds() {
        if (!mToolbar.getTitle().equals(FEED)) {
            mToolbar.setTitle(getString(R.string.feedText));
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().replace(R.id.container, mFeed).commit();
            currentScreen = SCREEN_FEED;
        }
    }


    private void callToVipServer(final String type) {
        progressDialog.setTitle(getString(R.string.logging));
        progressDialog.setMessage(getString(R.string.loggingIntoVip));
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.show();

        makeRequest(Retrofit.getInstance().getInkService().callVipServer(mSharedHelper.getUserId(), type), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                progressDialog.dismiss();
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        final boolean firstVipLogin = jsonObject.optBoolean("isFirstVipLogin");
                        final boolean hasGift = jsonObject.optBoolean("hasGift");
                        final String giftType = jsonObject.optString("giftType");
                        final String membershipType = jsonObject.optString("membershipType");

                        final Bundle bundle = new Bundle();
                        bundle.putBoolean("firstVipLogin", firstVipLogin);
                        bundle.putBoolean("hasGift", hasGift);
                        bundle.putString("giftType", giftType);
                        bundle.putString("membershipType", membershipType);

                        if (type.equals(Constants.TYPE_BUY_VIP)) {
                            int remainingCoins = jsonObject.optInt("remainingCoins");
                            User.get().setCoins(remainingCoins);
                            coinsText.setText(getString(R.string.coinsText, User.get().getCoins()));
                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                            builder.setTitle(getString(R.string.congratulation));
                            builder.setMessage(getString(R.string.vip_bought_Text));
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    alertDialog.dismiss();

                                    openVipRoom(bundle);
                                }
                            });
                        } else {
                            openVipRoom(bundle);
                        }
                    } else {
                        String cause = jsonObject.optString("cause");
                        switch (cause) {
                            case ErrorCause.SERVER_ERROR:
                                Snackbar.make(mToolbar, getString(R.string.serverErrorText), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                                break;
                            case ErrorCause.NOT_VIP_ERROR:
                                int vipPrice = jsonObject.optInt("vipPrice");
                                String shortInfo = jsonObject.optString("vipDescription");
                                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setTitle(getString(R.string.notVipText));
                                builder.setMessage(getString(R.string.youAreNotVipText, vipPrice) + shortInfo);
                                builder.setPositiveButton(getString(R.string.buy_text), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                builder.setCancelable(false);
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                        callToVipServer(Constants.TYPE_BUY_VIP);
                                    }
                                });
                                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog.dismiss();
                                    }
                                });
                                break;
                            case ErrorCause.NOT_ENOUGH_COINS:
                                builder = new AlertDialog.Builder(HomeActivity.this);
                                builder.setMessage(getString(R.string.not_enough_coins));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                builder.show();
                                break;
                        }
                    }
                } catch (IOException e) {
                    progressDialog.dismiss();
                    Snackbar.make(mToolbar, getString(R.string.vip_enter_error), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    e.printStackTrace();
                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Snackbar.make(mToolbar, getString(R.string.vip_enter_error), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                progressDialog.dismiss();
            }
        });
    }

    private void openVipRoom(@Nullable Bundle bundle) {
        Intent intent = new Intent(getApplicationContext(), VIPActivity.class);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overrideActivityAnimation();
    }

    private void logoutUser() {
        progressDialog.setTitle(getString(R.string.loggingout));
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(getString(R.string.loggingoutPleaseWait));
        progressDialog.show();
        shouldOpenActivity = false;
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                LoginManager.getInstance().logOut();
                RealmHelper.getInstance().clearDatabase(null);
                ImageLoader.clearIonCache(getApplicationContext());
                FileUtils.clearApplicationData(getApplicationContext());
                boolean editorHintValue = mSharedHelper.isEditorHintShown();
                mSharedHelper.clean();
                mSharedHelper.putShouldShowIntro(false);
                if (editorHintValue) {
                    mSharedHelper.putEditorHintShow(true);
                }
                mSharedHelper.putWarned(true);

                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Login.class));
                            finish();
                        }
                    });

                }
            }
        });
        thread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messages:
                mFab.close(true);
                openMessages();
                break;
            case R.id.makePost:
                mFab.close(true);
                startActivity(new Intent(getApplicationContext(), MakePost.class));
                overridePendingTransition(R.anim.activity_scale_up, R.anim.activity_scale_down);
                break;
            case R.id.profileImage:
                shouldOpenActivity = true;
                lastRequestCode = PROFILE_RESULT_CODE;
                setLastClassToOpen(MyProfile.class, true);
                mDrawer.closeDrawer(Gravity.LEFT);
                break;
        }
    }

    private void openMessages() {
        startActivity(new Intent(getApplicationContext(), Messages.class));
    }


    private void startTokenService() {
        try {
            stopService(new Intent(getApplicationContext(), SendTokenService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
        startService(new Intent(getApplicationContext(), SendTokenService.class));
    }

    @Override
    protected void onResume() {
        hasNotifications();
        if (messagesCountTV != null) {
            initializeCountDrawer(messagesCountTV);
        }
        if (mSharedHelper.isTokenRefreshed()) {
            startTokenService();
        }
        updateUserCoins();
        mUserNameTV.setText(mSharedHelper.getFirstName() + " " + mSharedHelper.getLastName());
        if (mSharedHelper.shouldLoadImage()) {
            loadImage();
        }
        getFriends();
        Picasso.with(this).load("https://images.sciencedaily.com/2016/10/161026102701_1_540x360.jpg").into(target);
        super.onResume();
    }


    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Log.d(TAG, "onBitmapLoaded: ");
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            Log.d(TAG, "onBitmapFailed: " + drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            Log.d(TAG, "onPrepareLoad: " + drawable);
        }
    };

    private void hasNotifications() {
        makeRequest(Retrofit.getInstance().getInkService().hasUnreadNotifications(mSharedHelper.getUserId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        boolean hasUnreadNotifications = jsonObject.optBoolean("hasRead");
                        if (hasUnreadNotifications) {
                            if (menuItem != null) {
                                menuItem.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_notification_icon));
                            }
                        } else {
                            if (menuItem != null) {
                                if (mSharedHelper.getNotificationIconColor() == null) {
                                    menuItem.getItem(2).setIcon(ContextCompat.getDrawable(getApplicationContext(),
                                            R.drawable.notification_icon));
                                } else {
                                    menuItem.getItem(2).getIcon().setColorFilter(Color.parseColor(mSharedHelper.getNotificationIconColor()),
                                            PorterDuff.Mode.SRC_ATOP);
                                }
                            }
                        }
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

    private void updateUserCoins() {
        if (coinsText != null) {
            coinsText.setText(getString(R.string.coinsText, User.get().getCoins()));
        }
    }

    private void initPollFish() {
        DimDialog.showDimDialog(this, getString(R.string.loadingSurvey));
        pollFish.initPollFish();
        pollFish.hidePollFish();

    }


    private void loadImage() {
        if (mSharedHelper.hasImage()) {
            if (!mSharedHelper.getImageLink().isEmpty()) {
                if (isSocialAccount()) {
                    ImageLoader.loadImage(getApplicationContext(), true, false, mSharedHelper.getImageLink(),
                            0, R.drawable.user_image_placeholder, mProfileImage, new ImageLoader.ImageLoadedCallback() {
                                @Override
                                public void onImageLoaded(Object result, Exception e) {
                                    if (e != null) {
                                        if (e instanceof BitmapDecodeException) {
                                            handleSocialImage();
                                        }
                                        mSharedHelper.putShouldLoadImage(false);
                                    }
                                }
                            });
                } else {
                    String encodedImage = Uri.encode(mSharedHelper.getImageLink());

                    ImageLoader.loadImage(getApplicationContext(), true, false, Constants.MAIN_URL +
                                    Constants.USER_IMAGES_FOLDER + encodedImage,
                            0, R.drawable.user_image_placeholder, mProfileImage, new ImageLoader.ImageLoadedCallback() {
                                @Override
                                public void onImageLoaded(Object result, Exception e) {
                                    if (e != null) {
                                        if (e instanceof BitmapDecodeException) {
                                            handleSocialImage();
                                        }
                                        mSharedHelper.putShouldLoadImage(false);
                                    }
                                }
                            });
                }
            }
        } else {
            ImageLoader.loadImage(getApplicationContext(), true, true, null,
                    R.drawable.no_image, R.drawable.user_image_placeholder, mProfileImage, new ImageLoader.ImageLoadedCallback() {
                        @Override
                        public void onImageLoaded(Object result, Exception e) {
                            mSharedHelper.putShouldLoadImage(false);
                        }
                    });
        }

    }

    private void handleSocialImage() {
        if (mSharedHelper.isSocialAccount()) {
            makeRequest(Retrofit.getInstance().getInkService().getSingleUserDetails(mSharedHelper.getUserId(), ""), null, new RequestCallback() {
                @Override
                public void onRequestSuccess(Object result) {
                    try {
                        String responseBody = ((ResponseBody) result).string();
                        JSONObject jsonObject = new JSONObject(responseBody);
                        String facebookProfileUrl = jsonObject.optString("facebook_profile");
                        if (!facebookProfileUrl.isEmpty()) {
                            if (facebookProfileUrl.contains("/")) {
                                String[] parts = facebookProfileUrl.split("/");
                                if (parts.length >= 3) {
                                    String userId = parts[4].trim();
                                    final String finalUrl = FACEBOOK_GRAPH_FIRST_URL + userId + FACEBOOK_GRAPH_LAST_URL;

                                    ImageLoader.loadImage(getApplicationContext(), true, false, finalUrl,
                                            0, R.drawable.user_image_placeholder, mProfileImage, new ImageLoader.ImageLoadedCallback() {
                                                @Override
                                                public void onImageLoaded(Object result, Exception e) {
                                                    if (e == null) {
                                                        mSharedHelper.putImageLink(finalUrl);
                                                        sendUpdateToServer();
                                                    }
                                                }
                                            });
                                }
                            }
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

    }

    private void sendUpdateToServer() {
        makeRequest(Retrofit.getInstance().getInkService().updateUserImage(mSharedHelper.getUserId(), mSharedHelper.getImageLink()), null, null);
    }


    private void setLastClassToOpen(Class<?> classToOpen, boolean activityForResult) {
        mLastClassToOpen = classToOpen;
        this.activityForResult = activityForResult;
    }

    private void setShouldOpenActivity(boolean shouldOpenActivity) {
        this.shouldOpenActivity = shouldOpenActivity;
    }


    public Toolbar getToolbar() {
        return mToolbar;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sipManagerUtil != null) {
            sipManagerUtil.destroy();
        }
        Notification.get().setAppAlive(false);
    }


    @Override
    public void onAccountDeleted() {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Constants.BUY_COINS_REQUEST_CODE:
                if (data != null && data.getAction() != null) {
                    boolean coinsBought = data.getExtras().getBoolean(Constants.COINS_BOUGHT_KEY);
                    if (coinsBought) {
                        updateUserCoins();
                    }
                }
                break;
            case Constants.REQUEST_CUSTOMIZE_MADE:
                if (data != null && data.getExtras() != null) {
                    boolean anythingChanged = data.getExtras().getBoolean("anythingChanged");
                    if (anythingChanged) {
                        triggerColorChangeListener();
                        setHomeActivityColors();
                    } else if (data.getExtras().containsKey("reset")) {
                        boolean reset = data.getExtras().getBoolean("reset");
                        if (reset) {
                            triggerReset();
                            resetColors();
                        }
                    }
                }
                break;
            case PROFILE_RESULT_CODE:
                getCoins();
                break;
            case USE_SIP_REQUEST_PERMISSION:
                startSip();
                break;

        }
    }

    private void startSip() {
        String firstName = mSharedHelper.getFirstName();
        String lastName = mSharedHelper.getLastName();
        final String userId = mSharedHelper.getUserId();
        final String sipUserName = firstName + lastName + Constants.SIP_USERNAME_EXTENSION;
        final String displayName = firstName + " " + lastName;
        if (mSharedHelper.isSipRegistered()) {
            sipManagerUtil.loginIntoSip(sipUserName, Constants.SIP_GENERIC_PASSWORD, displayName, userId);
        } else {
            sipManagerUtil.registerSipAccount(firstName + lastName + Constants.SIP_USERNAME_EXTENSION, Constants.SIP_GENERIC_PASSWORD, displayName, new GeneralCallback() {
                @Override
                public void onSuccess(Object o) {
                    sipManagerUtil.loginIntoSip(sipUserName, Constants.SIP_GENERIC_PASSWORD, displayName, userId);
                    mSharedHelper.setSipRegistered(true);
                }

                @Override
                public void onFailure(Object o) {
                    Log.d(TAG, "onFailure: " + o);
                    if (o != null) {
                        switch (o.toString()) {
                            case ErrorCause.SIP_USER_EXISTS_ERROR:
                                sipManagerUtil.loginIntoSip(sipUserName, Constants.SIP_GENERIC_PASSWORD, displayName, userId);
                                mSharedHelper.setSipRegistered(true);
                                break;
                        }
                    }
                }
            });
        }
    }


    private void triggerReset() {
        if (colorChangeListener != null) {
            colorChangeListener.onColorReset();
        }
    }

    public void setOnColorChangeListener(ColorChangeListener colorChangeListener) {
        this.colorChangeListener = colorChangeListener;
    }

    private void triggerColorChangeListener() {
        if (colorChangeListener != null) {
            colorChangeListener.onColorChanged();
        }
    }

    private void setHomeActivityColors() {
        if (mSharedHelper.getHamburgerColor() != null) {
            toggle.getDrawerArrowDrawable().setColor(Color.parseColor(mSharedHelper.getHamburgerColor()));
            toggle.syncState();
        }
        if (mSharedHelper.getMenuButtonColor() != null) {
            mFab.setMenuButtonColorNormal(Color.parseColor(mSharedHelper.getMenuButtonColor()));
            mFab.setMenuButtonColorPressed(Color.parseColor("#cccccc"));
        }

        if (mSharedHelper.getTrendColor() != null) {
            menuItem.getItem(0).getIcon().setColorFilter(Color.parseColor(mSharedHelper.getTrendColor()),
                    PorterDuff.Mode.SRC_ATOP);
        }

        if (mSharedHelper.getNotificationIconColor() != null) {
            menuItem.getItem(2).getIcon().setColorFilter(Color.parseColor(mSharedHelper.getNotificationIconColor()),
                    PorterDuff.Mode.SRC_ATOP);
        }

        if (mSharedHelper.getShopIconColor() != null) {
            menuItem.getItem(1).getIcon().setColorFilter(Color.parseColor(mSharedHelper.getShopIconColor()),
                    PorterDuff.Mode.SRC_ATOP);
        }
        if (mSharedHelper.getActionBarColor() != null) {
            mToolbar.setBackgroundColor(Color.parseColor(mSharedHelper.getActionBarColor()));
        }

        if (mSharedHelper.getStatusBarColor() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.parseColor(mSharedHelper.getStatusBarColor()));
            }
        }
        if (mSharedHelper.getLeftSlidingPanelHeaderColor() != null) {
            panelHeader.setBackgroundColor(Color.parseColor(mSharedHelper.getLeftSlidingPanelHeaderColor()));
        }
    }


    private void resetColors() {
        if (mSharedHelper.getHamburgerColor() == null) {
            toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        }
        if (mSharedHelper.getMenuButtonColor() == null) {
            mFab.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.colorPrimary));
            mFab.setMenuButtonColorPressed(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        if (mSharedHelper.getTrendColor() == null) {
            menuItem.getItem(0).getIcon().setColorFilter(null);
        }

        if (mSharedHelper.getNotificationIconColor() == null) {
            menuItem.getItem(2).getIcon().setColorFilter(null);
        }
        if (mSharedHelper.getShopIconColor() == null) {
            menuItem.getItem(1).getIcon().setColorFilter(null);
        }
        if (mSharedHelper.getActionBarColor() == null) {
            mToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        if (mSharedHelper.getLeftSlidingPanelHeaderColor() == null) {
            panelHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.side_nav_bar));
        }
        if (mSharedHelper.getStatusBarColor() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
        }
    }


    @Override
    public void onUserNotEligible() {
        DimDialog.hideDialog();
        Toast.makeText(this, getString(R.string.notEligible), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPollfishClosed() {
    }

    @Override
    public void onPollfishOpened() {
        DimDialog.hideDialog();
    }

    @Override
    public void onPollfishSurveyCompleted(boolean b, int i) {
        DimDialog.hideDialog();
        getReward();
    }

    private void getReward() {
        DimDialog.showDimDialog(this, getString(R.string.redeeming));
        makeRequest(Retrofit.getInstance().getInkService().getReward(mSharedHelper.getUserId(), Constants.POLLFISH_TOKEN), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                pollFish.hidePollFish();
                DimDialog.hideDialog();
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        int coins = jsonObject.optInt("userCoins");
                        int reward = jsonObject.optInt("reward");
                        User.get().setCoins(coins);
                        coinsText.setText(getString(R.string.coinsText, Integer.valueOf(coins)));
                        Toast.makeText(HomeActivity.this, getString(R.string.redeemed, reward), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(HomeActivity.this, getString(R.string.redeemError), Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                pollFish.hidePollFish();
                DimDialog.hideDialog();
            }
        });
    }

    @Override
    public void onPollfishSurveyNotAvailable() {
        DimDialog.hideDialog();
        Toast.makeText(this, getString(R.string.noSurvey), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPollfishSurveyReceived(boolean b, int i) {
        pollFish.showPolFish();
    }

}
