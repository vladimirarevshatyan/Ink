package kashmirr.social.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kashmirr.social.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fab.FloatingActionButton;
import kashmirr.social.callbacks.GeneralCallback;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.models.ColorModel;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.ErrorCause;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import okhttp3.ResponseBody;
import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by USER on 2016-08-30.
 */

public class CustomizeLook extends BaseActivity {

    @BindView(R.id.customizeToolbar)
    Toolbar customizeToolbar;

    @BindView(R.id.customizeFabMenu)
    android.support.design.widget.FloatingActionMenu customizeFabMenu;

    @BindView(R.id.customizeFabMenuText)
    TextView customizeFabMenuText;

    @BindView(R.id.pickNotificationIconColorText)
    TextView pickNotificationIconColorText;

    @BindView(R.id.pickShopIconColorText)
    TextView pickShopIconColorText;

    @BindView(R.id.pickLeftDrawerColorText)
    TextView pickLeftDrawerColorText;

    @BindView(R.id.postBackgroundColorText)
    TextView postBackgroundColorText;

    @BindView(R.id.friendsBackgroundColorText)
    TextView friendsBackgroundColorText;

    @BindView(R.id.messagesBackgroundColorText)
    TextView messagesBackgroundColorText;

    @BindView(R.id.chatBackgroundColorText)
    TextView chatBackgroundColorText;

    @BindView(R.id.requestsBackgroundColorText)
    TextView requestsBackgroundColorText;

    @BindView(R.id.opponentBubbleIcon)
    ImageView opponentBubbleIcon;

    @BindView(R.id.ownBubbleIcon)
    ImageView ownBubbleIcon;

    @BindView(R.id.notificationIcon)
    ImageView notificationIcon;

    @BindView(R.id.shopIcon)
    ImageView shopIcon;

    @BindView(R.id.pickTrendIcon)
    ImageView pickTrendIcon;

    @BindView(R.id.pickHamburgerIcon)
    ImageView hamburgerIcon;

    @BindView(R.id.ownBubbleCleaner)
    ImageView ownBubbleCleaner;

    @BindView(R.id.opponentBubbleCleaner)
    ImageView opponentBubbleCleaner;

    @BindView(R.id.actionBarCleaner)
    ImageView actionBarCleaner;

    @BindView(R.id.chatFieldCleaner)
    ImageView chatFieldCleaner;

    @BindView(R.id.ownTextCleaner)
    ImageView ownTextCleaner;

    @BindView(R.id.opponentTextCleaner)
    ImageView opponentTextCleaner;

    @BindView(R.id.hamburgerCleaner)
    ImageView hamburgerCleaner;

    @BindView(R.id.statusBarCleaner)
    ImageView statusBarCleaner;

    @BindView(R.id.fabButtonCleaner)
    ImageView fabButtonCleaner;

    @BindView(R.id.notificationCleaner)
    ImageView notificationCleaner;

    @BindView(R.id.shopCleaner)
    ImageView shopCleaner;

    @BindView(R.id.leftPanelCleaner)
    ImageView leftPanelCleaner;

    @BindView(R.id.feedCleaner)
    ImageView feedCleaner;

    @BindView(R.id.sendButtonIcon)
    FloatingActionButton sendButtonIcon;

    @BindView(R.id.sendButtonCleaner)
    ImageView sendButtonCleaner;

    @BindView(R.id.friendsCleaner)
    ImageView friendsCleaner;

    @BindView(R.id.trendCleaner)
    ImageView trendCleaner;

    @BindView(R.id.messagesCleaner)
    ImageView messagesCleaner;

    @BindView(R.id.chatCleaner)
    ImageView chatCleaner;

    @BindView(R.id.requestCleaner)
    ImageView requestCleaner;
    @BindView(R.id.opponentProfileCleaner)
    ImageView opponentProfileCleaner;


    private boolean actionBarColorPicked;
    private boolean fabMenuButtonColorPicked;
    private boolean pickNotificationIconColorPicked;
    private boolean pickShopIconColorPicked;
    private boolean pickLeftDrawerColorPicked;
    private boolean feedColorPicked;
    private boolean messagesBackgroundColorPicked;
    private boolean friendsBackgroundColorPicked;
    private boolean chatBackgroundColorPicked;
    private boolean requestBackgroundColorPicked;
    private boolean opponentBubbleColorPicked;
    private boolean ownBubbleColorPicked;
    private boolean hamburgerColorPicked;
    private boolean statusBarColorPicked;
    private boolean sendButtonColorPicked;
    private boolean opponentTextColorPicked;
    private boolean chatFieldColorPicked;
    private boolean ownTextColorPicked;
    private boolean allResetCalled;
    private boolean trendColorPicked;
    private boolean opponentProfileColorPicked;


    private AppCompatCheckBox statusBarCheckBox;
    private AppCompatCheckBox selectAllCheckBox;
    private AppCompatCheckBox actionBarCheckBox;
    private AppCompatCheckBox menuButtonCheckBox;
    private AppCompatCheckBox sendButtonCheckBox;
    private AppCompatCheckBox notificationCheckBox;
    private AppCompatCheckBox shopCheckBox;
    private AppCompatCheckBox hamburgerCheckBox;
    private AppCompatCheckBox leftPanelCheckBox;
    private AppCompatCheckBox feedCheckBox;
    private AppCompatCheckBox friendsCheckBox;
    private AppCompatCheckBox messagesCheckBox;
    private AppCompatCheckBox chatCheckBox;
    private AppCompatCheckBox requestCheckBox;
    private AppCompatCheckBox opponentBubbleCheckBox;
    private AppCompatCheckBox ownBubbleCheckBox;
    private AppCompatCheckBox opponentTextCheckBox;
    private AppCompatCheckBox ownTextCheckBox;
    private AppCompatCheckBox chatFieldCheckBox;
    private AppCompatCheckBox trendCheckbox;
    private AppCompatCheckBox opponentCheckBox;

    private SharedHelper sharedHelper;
    private String oldActionBarColor;
    private String oldMenuButtonColor;
    private String oldNotificationIconColor;
    private String oldShopIconColor;
    private String oldLeftSlidingPanelHeaderColor;
    private String oldFeedColor;
    private String oldFriendsColor;
    private String oldMessagesColor;
    private String oldChatColor;
    private String oldMyRequestColor;
    private String oldOwnBubbleColor;
    private String oldOpponentBubbleColor;
    private String oldHamburgerColor;
    private String oldStatusBarColor;
    private String oldSendButtonColor;
    private String oldChatFieldColor;
    private String oldOwnTextColor;
    private String oldOpponentTextColor;
    private String oldTrendColor;
    private String oldOpponentColor;

    private Gson gson;
    private kashmirr.social.utils.ProgressDialog progressDialog;
    private boolean anythingChanged;
    private boolean isSavedToCloud;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customize_look_view);
        ButterKnife.bind(this);
        gson = new Gson();
        sharedHelper = new SharedHelper(this);
        customizeToolbar = (Toolbar) findViewById(R.id.customizeToolbar);
        setSupportActionBar(customizeToolbar);
        progressDialog = kashmirr.social.utils.ProgressDialog.get().buildProgressDialog(this, false);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(getString(R.string.pleaseWait));
        progressDialog.setMessage(getString(R.string.savingToServer));

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.actionBar));
        }
        oldOwnBubbleColor = sharedHelper.getOwnBubbleColor();
        oldOpponentBubbleColor = sharedHelper.getOpponentBubbleColor();
        oldActionBarColor = sharedHelper.getActionBarColor();
        oldMenuButtonColor = sharedHelper.getMenuButtonColor();
        oldNotificationIconColor = sharedHelper.getNotificationIconColor();
        oldShopIconColor = sharedHelper.getShopIconColor();
        oldLeftSlidingPanelHeaderColor = sharedHelper.getLeftSlidingPanelHeaderColor();
        oldFeedColor = sharedHelper.getFeedColor();
        oldFriendsColor = sharedHelper.getFriendsColor();
        oldMessagesColor = sharedHelper.getMessagesColor();
        oldChatColor = sharedHelper.getChatColor();
        oldMyRequestColor = sharedHelper.getMyRequestColor();
        oldHamburgerColor = sharedHelper.getHamburgerColor();
        oldStatusBarColor = sharedHelper.getStatusBarColor();
        oldSendButtonColor = sharedHelper.getSendButtonColor();
        oldChatFieldColor = sharedHelper.getChatFieldTextColor();
        oldOwnTextColor = sharedHelper.getOwnTextColor();
        oldOpponentTextColor = sharedHelper.getOpponentTextColor();
        oldTrendColor = sharedHelper.getTrendColor();
        oldOpponentColor = sharedHelper.getOpponentProfileColor();


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkAndSendResult();
                break;
            case R.id.saveToCloud:
                if (hasAnythingChanged()) {
                    showSaveWarning();
                } else if (sharedHelper.hasPendingCustomizationsToSave()) {
                    showSaveWarning();
                } else {
                    Snackbar.make(friendsCleaner, getString(R.string.nothingWasChanged), Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.restoreFromCloud:
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomizeLook.this);
                builder.setTitle(getString(R.string.warning));
                builder.setMessage(getString(R.string.localOverwritingWarning));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        restoreFromCloud();
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
            case R.id.resetToDefault:
                builder = new AlertDialog.Builder(CustomizeLook.this);
                builder.setTitle(getString(R.string.chooseWhichReset));
                final View resetView = getLayoutInflater().inflate(R.layout.reset_view, null);
                setUpViewsAndListeners(resetView);
                selectAll(true);
                allResetCalled = true;
                builder.setView(resetView);
                builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setPositiveButton(getString(R.string.reset), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        resetCustomizations();
                    }
                });
                builder.show();
                break;
            case R.id.removeFromCloud:
                builder = new AlertDialog.Builder(CustomizeLook.this);
                builder.setTitle(getString(R.string.warning));
                builder.setMessage(getString(R.string.removeFromCloudWarning));
                builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeFromCloud();
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
        return super.onOptionsItemSelected(item);
    }


    private void setUpViewsAndListeners(View view) {
        statusBarCheckBox = (AppCompatCheckBox) view.findViewById(R.id.statusBarCheckBox);
        selectAllCheckBox = (AppCompatCheckBox) view.findViewById(R.id.selectAllCheckBox);
        actionBarCheckBox = (AppCompatCheckBox) view.findViewById(R.id.actionBarCheckBox);
        menuButtonCheckBox = (AppCompatCheckBox) view.findViewById(R.id.menuButtonCheckBox);
        sendButtonCheckBox = (AppCompatCheckBox) view.findViewById(R.id.sendButtonCheckBox);
        notificationCheckBox = (AppCompatCheckBox) view.findViewById(R.id.notificationCheckBox);
        shopCheckBox = (AppCompatCheckBox) view.findViewById(R.id.shopCheckBox);
        hamburgerCheckBox = (AppCompatCheckBox) view.findViewById(R.id.hamburgerCheckBox);
        leftPanelCheckBox = (AppCompatCheckBox) view.findViewById(R.id.leftPanelCheckBox);
        feedCheckBox = (AppCompatCheckBox) view.findViewById(R.id.feedCheckBox);
        friendsCheckBox = (AppCompatCheckBox) view.findViewById(R.id.friendsCheckBox);
        messagesCheckBox = (AppCompatCheckBox) view.findViewById(R.id.messagesCheckBox);
        chatCheckBox = (AppCompatCheckBox) view.findViewById(R.id.chatCheckBox);
        requestCheckBox = (AppCompatCheckBox) view.findViewById(R.id.requestCheckBox);
        opponentBubbleCheckBox = (AppCompatCheckBox) view.findViewById(R.id.opponentBubbleCheckBox);
        ownBubbleCheckBox = (AppCompatCheckBox) view.findViewById(R.id.ownBubbleCheckBox);
        opponentTextCheckBox = (AppCompatCheckBox) view.findViewById(R.id.opponentTextCheckBox);
        ownTextCheckBox = (AppCompatCheckBox) view.findViewById(R.id.ownTextCheckBox);
        chatFieldCheckBox = (AppCompatCheckBox) view.findViewById(R.id.chatFieldCheckBox);
        trendCheckbox = (AppCompatCheckBox) view.findViewById(R.id.trendAndNewsCheckbox);
        opponentCheckBox = (AppCompatCheckBox) view.findViewById(R.id.opponentProfileCheckBox);

        LinearLayout statusBarWrapper = (LinearLayout) view.findViewById(R.id.statusBarWrapper);
        statusBarWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!statusBarCheckBox.isChecked()) {
                    statusBarCheckBox.setChecked(true);
                } else {
                    statusBarCheckBox.setChecked(false);
                    allResetCalled = false;
                }
            }
        });
        LinearLayout selectAllWrapper = (LinearLayout) view.findViewById(R.id.selectAllWrapper);
        selectAllWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectAllCheckBox.isChecked()) {
                    selectAll(false);
                } else {
                    selectAll(true);
                }
            }
        });
        LinearLayout actionBarWrapper = (LinearLayout) view.findViewById(R.id.actionBarWrapper);
        actionBarWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!actionBarCheckBox.isChecked()) {
                    actionBarCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    actionBarCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout menuButtonWrapper = (LinearLayout) view.findViewById(R.id.menuButtonWrapper);
        menuButtonWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!menuButtonCheckBox.isChecked()) {
                    menuButtonCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    menuButtonCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout sendButtonWrapper = (LinearLayout) view.findViewById(R.id.sendButtonWrapper);
        sendButtonWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!sendButtonCheckBox.isChecked()) {
                    sendButtonCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    sendButtonCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout notificationWrapper = (LinearLayout) view.findViewById(R.id.notificationWrapper);
        notificationWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!notificationCheckBox.isChecked()) {
                    notificationCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    notificationCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout shopWrapper = (LinearLayout) view.findViewById(R.id.shopWrapper);
        shopWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!shopCheckBox.isChecked()) {
                    shopCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    shopCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout hamburgerWrapper = (LinearLayout) view.findViewById(R.id.hamburgerWrapper);
        hamburgerWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hamburgerCheckBox.isChecked()) {
                    hamburgerCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    hamburgerCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout leftPanelWrapper = (LinearLayout) view.findViewById(R.id.leftPanelWrapper);
        leftPanelWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!leftPanelCheckBox.isChecked()) {
                    leftPanelCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    leftPanelCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout feedWrapper = (LinearLayout) view.findViewById(R.id.feedWrapper);
        feedWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!feedCheckBox.isChecked()) {
                    feedCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    feedCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout friendsWrapper = (LinearLayout) view.findViewById(R.id.friendsWrapper);
        friendsWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!friendsCheckBox.isChecked()) {
                    friendsCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    friendsCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout messagesWrapper = (LinearLayout) view.findViewById(R.id.messagesWrapper);
        messagesWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messagesCheckBox.isChecked()) {
                    messagesCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    messagesCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout chatWrapper = (LinearLayout) view.findViewById(R.id.chatWrapper);
        chatWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chatCheckBox.isChecked()) {
                    chatCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    chatCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout requestWrapper = (LinearLayout) view.findViewById(R.id.requestWrapper);
        requestWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!requestCheckBox.isChecked()) {
                    requestCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    requestCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout opponentBubbleWrapper = (LinearLayout) view.findViewById(R.id.opponentBubbleWrapper);
        opponentBubbleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!opponentBubbleCheckBox.isChecked()) {
                    opponentBubbleCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    opponentBubbleCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout ownBubbleWrapper = (LinearLayout) view.findViewById(R.id.ownBubbleWrapper);
        ownBubbleWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ownBubbleCheckBox.isChecked()) {
                    ownBubbleCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    ownBubbleCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout opponentTextWrapper = (LinearLayout) view.findViewById(R.id.opponentTextWrapper);
        opponentTextWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!opponentTextCheckBox.isChecked()) {
                    opponentTextCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    opponentTextCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout ownTextWrapper = (LinearLayout) view.findViewById(R.id.ownTextWrapper);
        ownTextWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ownTextCheckBox.isChecked()) {
                    ownTextCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    ownTextCheckBox.setChecked(false);
                }
            }
        });
        LinearLayout chatFieldWrapper = (LinearLayout) view.findViewById(R.id.chatFieldWrapper);
        chatFieldWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!chatFieldCheckBox.isChecked()) {
                    chatFieldCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    chatFieldCheckBox.setChecked(false);
                }
            }
        });

        LinearLayout opponentProfileWrapper = (LinearLayout) view.findViewById(R.id.opponentProfileWrapper);
        opponentProfileWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!opponentCheckBox.isChecked()) {
                    opponentCheckBox.setChecked(true);
                } else {
                    allResetCalled = false;
                    opponentCheckBox.setChecked(false);
                }
            }
        });

        LinearLayout trendWrapper = (LinearLayout) view.findViewById(R.id.tendAndNewsWrapper);
        trendWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!trendCheckbox.isChecked()) {
                    trendCheckbox.setChecked(true);
                } else {
                    allResetCalled = false;
                    trendCheckbox.setChecked(false);
                }
            }
        });
    }

    private void resetCustomizations() {
        if (!statusBarCheckBox.isChecked() && !selectAllCheckBox.isChecked() && !actionBarCheckBox.isChecked()
                && !menuButtonCheckBox.isChecked() && !sendButtonCheckBox.isChecked()
                && !notificationCheckBox.isChecked() && !shopCheckBox.isChecked()
                && !hamburgerCheckBox.isChecked() && !leftPanelCheckBox.isChecked()
                && !feedCheckBox.isChecked() && !friendsCheckBox.isChecked()
                && !messagesCheckBox.isChecked() && !chatCheckBox.isChecked()
                && !requestCheckBox.isChecked() && !opponentBubbleCheckBox.isChecked()
                && !ownBubbleCheckBox.isChecked() && !opponentTextCheckBox.isChecked()
                && !ownTextCheckBox.isChecked() && !chatFieldCheckBox.isChecked() && !trendCheckbox.isChecked()
                && !opponentCheckBox.isChecked()) {
            Snackbar.make(actionBarCleaner, getString(R.string.nothingToReset), Snackbar.LENGTH_LONG).show();
        } else {
            if (allResetCalled) {
                sharedHelper.resetCustomization();
                Intent intent = new Intent();
                Toast.makeText(CustomizeLook.this, getString(R.string.reseted), Toast.LENGTH_SHORT).show();
                intent.putExtra("reset", true);
                setResult(Constants.REQUEST_CUSTOMIZE_MADE, intent);
                finish();
            } else {
                resetAccordingly();
            }
        }
    }

    private void resetAccordingly() {
        sharedHelper.putStatusBarColor(statusBarCheckBox.isChecked() ? null : sharedHelper.getStatusBarColor());
        sharedHelper.putActionBarColor(actionBarCheckBox.isChecked() ? null : sharedHelper.getActionBarColor());
        sharedHelper.putMenuButtonColor(menuButtonCheckBox.isChecked() ? null : sharedHelper.getMenuButtonColor());
        sharedHelper.putSendButtonColor(sendButtonCheckBox.isChecked() ? null : sharedHelper.getSendButtonColor());
        sharedHelper.putNotificationIconColor(notificationCheckBox.isChecked() ? null : sharedHelper.getNotificationIconColor());
        sharedHelper.putShopIconColor(shopCheckBox.isChecked() ? null : sharedHelper.getShopIconColor());
        sharedHelper.putHamburgerColor(hamburgerCheckBox.isChecked() ? null : sharedHelper.getHamburgerColor());
        sharedHelper.putLeftSlidingPanelColor(leftPanelCheckBox.isChecked() ? null : sharedHelper.getLeftSlidingPanelHeaderColor());
        sharedHelper.putFeedColor(feedCheckBox.isChecked() ? null : sharedHelper.getFeedColor());
        sharedHelper.putFriendsColor(friendsCheckBox.isChecked() ? null : sharedHelper.getFriendsColor());
        sharedHelper.putMessagesColor(messagesCheckBox.isChecked() ? null : sharedHelper.getMessagesColor());
        sharedHelper.putChatColor(chatCheckBox.isChecked() ? null : sharedHelper.getChatColor());
        sharedHelper.putMyRequestColor(requestCheckBox.isChecked() ? null : sharedHelper.getMyRequestColor());
        sharedHelper.putOpponentBubbleColor(opponentBubbleCheckBox.isChecked() ? null : sharedHelper.getOpponentBubbleColor());
        sharedHelper.putOwnBubbleColor(ownBubbleCheckBox.isChecked() ? null : sharedHelper.getOwnBubbleColor());
        sharedHelper.putOpponentTextColor(opponentTextCheckBox.isChecked() ? null : sharedHelper.getOpponentTextColor());
        sharedHelper.putOwnTextColor(ownTextCheckBox.isChecked() ? null : sharedHelper.getOwnTextColor());
        sharedHelper.putChatFieldTextColor(chatFieldCheckBox.isChecked() ? null : sharedHelper.getChatFieldTextColor());
        sharedHelper.putTrendColor(trendCheckbox.isChecked() ? null : sharedHelper.getTrendColor());
        sharedHelper.putOpponentColor(opponentCheckBox.isChecked() ? null : sharedHelper.getOpponentProfileColor());

        Intent intent = new Intent();
        Toast.makeText(CustomizeLook.this, getString(R.string.reseted), Toast.LENGTH_SHORT).show();
        intent.putExtra("reset", true);
        setResult(Constants.REQUEST_CUSTOMIZE_MADE, intent);
        finish();
    }

    private void showSaveWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomizeLook.this);
        builder.setTitle(getString(R.string.warning));
        builder.setMessage(getString(R.string.overwriteWarning));
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                saveToCloud();
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

    private void removeFromCloud() {
        progressDialog.setMessage(getString(R.string.removingFromCloud));
        progressDialog.show();
        makeRequest(Retrofit.getInstance().getInkService().removeFromCloud(sharedHelper.getUserId(), Constants.CUSTOMIZATION_TYPE_REMOVE), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    ColorModel colorModel = gson.fromJson(responseBody, ColorModel.class);
                    if (colorModel.success) {
                        progressDialog.hide();
                        isSavedToCloud = false;
                        Snackbar.make(friendsCleaner, getString(R.string.dataRemoved), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                        anythingChanged = false;
                    } else {
                        progressDialog.hide();
                        if (colorModel.cause != null) {
                            if (colorModel.cause.equals(ErrorCause.NO_CUSTOMIZATION)) {
                                Snackbar.make(friendsCleaner, getString(R.string.noCustomizationData), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            } else {
                                progressDialog.hide();
                                Snackbar.make(friendsCleaner, getString(R.string.couldNotRemove), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            }
                        } else {
                            Snackbar.make(friendsCleaner, getString(R.string.couldNotRemove), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    progressDialog.hide();
                    Snackbar.make(friendsCleaner, getString(R.string.couldNotRemove), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                progressDialog.hide();
            }
        });
    }

    private void restoreFromCloud() {
        progressDialog.setMessage(getString(R.string.restoringFromCloud));
        progressDialog.show();
        makeRequest(Retrofit.getInstance().getInkService().restoreCustomization(sharedHelper.getUserId(), Constants.CUSTOMIZATION_TYPE_RESTORE), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    ColorModel colorModel = gson.fromJson(responseBody, ColorModel.class);
                    if (colorModel.success) {
                        progressDialog.hide();
                        isSavedToCloud = false;
                        saveDataLocally(colorModel);
                        Snackbar.make(actionBarCleaner, getString(R.string.dataRestored), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                    } else {
                        progressDialog.hide();
                        if (colorModel.cause != null) {
                            if (colorModel.cause.equals(ErrorCause.NO_CUSTOMIZATION)) {
                                Snackbar.make(friendsCleaner, getString(R.string.noCustomizationData), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            } else if (colorModel.cause.equals(ErrorCause.SERVER_ERROR)) {
                                progressDialog.hide();
                                Snackbar.make(friendsCleaner, getString(R.string.errorFetchingData), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            }
                        } else {
                            progressDialog.hide();
                            Snackbar.make(friendsCleaner, getString(R.string.errorFetchingData), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                        }
                    }
                } catch (IOException e) {
                    progressDialog.hide();
                    e.printStackTrace();
                    Snackbar.make(friendsCleaner, getString(R.string.errorFetchingData), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                progressDialog.hide();
            }
        });
    }

    private void saveDataLocally(ColorModel colorModel) {
        sharedHelper.putStatusBarColor(colorModel.statusBar);
        sharedHelper.putActionBarColor(colorModel.actionBar);
        sharedHelper.putMenuButtonColor(colorModel.menuButton);
        sharedHelper.putSendButtonColor(colorModel.sendButton);
        sharedHelper.putNotificationIconColor(colorModel.notificationIcon);
        sharedHelper.putShopIconColor(colorModel.shopIcon);
        sharedHelper.putHamburgerColor(colorModel.hamburgerIcon);
        sharedHelper.putLeftSlidingPanelColor(colorModel.leftHeader);
        sharedHelper.putFeedColor(colorModel.feedBackground);
        sharedHelper.putFriendsColor(colorModel.friendsBackground);
        sharedHelper.putMessagesColor(colorModel.messagesBackground);
        sharedHelper.putChatColor(colorModel.chatBackground);
        sharedHelper.putMyRequestColor(colorModel.requestBackground);
        sharedHelper.putOpponentBubbleColor(colorModel.opponentBubble);
        sharedHelper.putOwnBubbleColor(colorModel.ownBubble);
        sharedHelper.putOpponentTextColor(colorModel.opponentText);
        sharedHelper.putOwnTextColor(colorModel.ownText);
        sharedHelper.putChatFieldTextColor(colorModel.chatField);
        sharedHelper.putTrendColor(colorModel.trendColor);
        sharedHelper.putOpponentColor(colorModel.opponentColor);

        anythingChanged = true;
    }

    private void saveToCloud() {
        makeRequest(Retrofit.getInstance().getInkService().saveCustomization(Constants.CUSTOMIZATION_TYPE_SAVE,
                sharedHelper.getUserId(), sharedHelper.getStatusBarColor() != null ? sharedHelper.getStatusBarColor() : "",
                sharedHelper.getActionBarColor() != null ? sharedHelper.getActionBarColor() : "",
                sharedHelper.getMenuButtonColor() != null ? sharedHelper.getMenuButtonColor() : "",
                sharedHelper.getSendButtonColor() != null ? sharedHelper.getSendButtonColor() : "",
                sharedHelper.getNotificationIconColor() != null ? sharedHelper.getNotificationIconColor() : "",
                sharedHelper.getShopIconColor() != null ? sharedHelper.getShopIconColor() : "",
                sharedHelper.getHamburgerColor() != null ? sharedHelper.getHamburgerColor() : "",
                sharedHelper.getLeftSlidingPanelHeaderColor() != null ? sharedHelper.getLeftSlidingPanelHeaderColor() : "",
                sharedHelper.getFeedColor() != null ? sharedHelper.getFeedColor() : "",
                sharedHelper.getFriendsColor() != null ? sharedHelper.getFriendsColor() : "",
                sharedHelper.getMessagesColor() != null ? sharedHelper.getMessagesColor() : "",
                sharedHelper.getChatColor() != null ? sharedHelper.getChatColor() : "",
                sharedHelper.getMyRequestColor() != null ? sharedHelper.getMyRequestColor() : "",
                sharedHelper.getOpponentBubbleColor() != null ? sharedHelper.getOpponentBubbleColor() : "",
                sharedHelper.getOwnBubbleColor() != null ? sharedHelper.getOwnBubbleColor() : "",
                sharedHelper.getOpponentTextColor() != null ? sharedHelper.getOpponentTextColor() : "",
                sharedHelper.getOwnTextColor() != null ? sharedHelper.getOwnTextColor() : "",
                sharedHelper.getChatFieldTextColor() != null ? sharedHelper.getChatFieldTextColor() : "",
                sharedHelper.getTrendColor() != null ? sharedHelper.getTrendColor() : "",
                sharedHelper.getOpponentProfileColor() != null ? sharedHelper.getOpponentProfileColor() : ""),
                null, new RequestCallback() {
                    @Override
                    public void onRequestSuccess(Object result) {
                        try {
                            String responseBody = ((ResponseBody) result).string();
                            ColorModel colorModel = gson.fromJson(responseBody, ColorModel.class);
                            if (colorModel.success) {
                                progressDialog.hide();
                                isSavedToCloud = true;
                                sharedHelper.putHasPendingCustomizationsToSave(false);
                                Snackbar.make(friendsCleaner, getString(R.string.customizationSaved), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                }).show();
                            } else {
                                progressDialog.hide();
                                if (colorModel.cause != null) {
                                    if (colorModel.cause.equals(ErrorCause.SERVER_ERROR)) {
                                        Snackbar.make(friendsCleaner, getString(R.string.errorSaviingCustomaziation), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                            }
                                        }).show();
                                    }
                                } else {
                                    progressDialog.hide();
                                    Snackbar.make(friendsCleaner, getString(R.string.customizationSaved), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Snackbar.make(friendsCleaner, getString(R.string.errorSaviingCustomaziation), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            }).show();
                                        }
                                    }).show();
                                }
                            }
                        } catch (IOException e) {
                            progressDialog.hide();
                            e.printStackTrace();
                            Snackbar.make(friendsCleaner, getString(R.string.errorSaviingCustomaziation), Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onRequestFailed(Object[] result) {
                        progressDialog.hide();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.customization_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void showColorPicker(final GeneralCallback<String> generalCallback) {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, Color.parseColor("#3F51B5"), new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                // color is the color selected by the user.
                String hexWithoutAlpha = Integer.toHexString(color).toUpperCase().substring(2);
                generalCallback.onSuccess("#" + hexWithoutAlpha);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                generalCallback.onFailure(null);
            }
        });
        dialog.show();

    }

    @OnClick(R.id.pickHamburgerIconWrapper)
    public void pickHamburgerIconWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                hamburgerColorPicked = true;
                isSavedToCloud = false;
                hamburgerIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                hamburgerCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putHamburgerColor(s);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.statusBarColorPicker)
    public void statusBarColorPicker() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Snackbar.make(actionBarCleaner, getString(R.string.optionNotAvailable), Snackbar.LENGTH_LONG).show();
            return;
        }
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                statusBarCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putStatusBarColor(s);
                Snackbar.make(friendsCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                statusBarColorPicked = true;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.opponentProfileWrapper)
    public void opponentProfileClicked() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                opponentProfileColorPicked = true;
                Snackbar.make(actionBarCleaner, getString(R.string.colorSet), Snackbar.LENGTH_LONG).show();
                sharedHelper.putOpponentColor(s);
                opponentProfileCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.chatFieldWrapper)
    public void chatFieldWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                chatFieldColorPicked = true;
                Snackbar.make(actionBarCleaner, getString(R.string.colorSet), Snackbar.LENGTH_LONG).show();
                sharedHelper.putChatFieldTextColor(s);
                chatFieldCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }


    @OnClick(R.id.ownChatTextColorWrapper)
    public void ownTextColorWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ownTextColorPicked = true;
                isSavedToCloud = false;
                Snackbar.make(actionBarCleaner, getString(R.string.colorSet), Snackbar.LENGTH_LONG).show();
                sharedHelper.putOwnTextColor(s);
                ownTextCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.opponentChatTextColorWrapper)
    public void opponentTextColorWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                opponentTextColorPicked = true;
                Snackbar.make(actionBarCleaner, getString(R.string.colorSet), Snackbar.LENGTH_LONG).show();
                sharedHelper.putOpponentTextColor(s);
                opponentTextCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.actionBarColorPicker)
    public void actionBarColorPicker() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                actionBarColorPicked = true;
                customizeToolbar.setBackgroundColor(Color.parseColor(s));
                actionBarCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putActionBarColor(s);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }


    @OnClick(R.id.sendButtonPickerWrapper)
    public void sendButtonPickerWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                sendButtonColorPicked = true;
                isSavedToCloud = false;
                sendButtonIcon.setColorNormal(Color.parseColor(s));
                sharedHelper.putSendButtonColor(s);
                sendButtonCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.fabMenuButtonColorPicker)
    public void fabMenuButtonColorPicker() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                fabMenuButtonColorPicked = true;
                customizeFabMenu.setMenuButtonColorNormal(Color.parseColor(s));
                sharedHelper.putMenuButtonColor(s);
                fabButtonCleaner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.pickTrendIconWrapper)
    public void pickTrendIconWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                trendColorPicked = true;
                pickTrendIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                trendCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putTrendColor(s);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.pickNotificationIconColor)
    public void pickNotificationIconColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                pickNotificationIconColorPicked = true;
                notificationIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                notificationCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putNotificationIconColor(s);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.pickShopIconColor)
    public void pickShopIconColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                isSavedToCloud = false;
                shopCleaner.setVisibility(View.VISIBLE);
                shopIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                sharedHelper.putShopIconColor(s);
                pickShopIconColorPicked = true;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.pickLeftDrawerColor)
    public void pickLeftDrawerColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                leftPanelCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putLeftSlidingPanelColor(s);
                Snackbar.make(friendsCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                pickLeftDrawerColorPicked = true;
                isSavedToCloud = false;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.feedBackgroundColor)
    public void feedBackgroundColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                feedCleaner.setVisibility(View.VISIBLE);
                Snackbar.make(feedCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                sharedHelper.putFeedColor(s);
                feedColorPicked = true;
                isSavedToCloud = false;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.friendsBackgroundColor)
    public void friendsBackgroundColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                friendsBackgroundColorPicked = true;
                isSavedToCloud = false;
                friendsCleaner.setVisibility(View.VISIBLE);
                Snackbar.make(friendsCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                sharedHelper.putFriendsColor(s);
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.messagesBackgroundColor)
    public void messagesBackgroundColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                messagesCleaner.setVisibility(View.VISIBLE);
                Snackbar.make(messagesCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                sharedHelper.putMessagesColor(s);
                isSavedToCloud = false;
                messagesBackgroundColorPicked = true;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.chatBackgroundColor)
    public void chatBackgroundColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                chatCleaner.setVisibility(View.VISIBLE);
                Snackbar.make(chatCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                sharedHelper.putChatColor(s);
                chatBackgroundColorPicked = true;
                isSavedToCloud = false;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.requestsBackgroundColor)
    public void requestsBackgroundColor() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                requestCleaner.setVisibility(View.VISIBLE);
                Snackbar.make(requestCleaner, getString(R.string.colorSet), Snackbar.LENGTH_SHORT).show();
                sharedHelper.putMyRequestColor(s);
                requestBackgroundColorPicked = true;
                isSavedToCloud = false;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }


    @OnClick(R.id.opponentBubbleWrapper)
    public void opponentBubbleWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                opponentBubbleColorPicked = true;
                isSavedToCloud = false;
                opponentBubbleIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                opponentBubbleCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putOpponentBubbleColor(s);

            }

            @Override
            public void onFailure(String s) {

            }
        });
    }


    @OnClick(R.id.ownBubbleWrapper)
    public void ownBubbleWrapper() {
        showColorPicker(new GeneralCallback<String>() {
            @Override
            public void onSuccess(String s) {
                ownBubbleColorPicked = true;
                ownBubbleIcon.setColorFilter(Color.parseColor(s), PorterDuff.Mode.SRC_ATOP);
                ownBubbleCleaner.setVisibility(View.VISIBLE);
                sharedHelper.putOwnBubbleColor(s);
                isSavedToCloud = false;
            }

            @Override
            public void onFailure(String s) {

            }
        });
    }

    @OnClick(R.id.statusBarCleaner)
    public void statusBarCleaner() {
        statusBarCleaner.setVisibility(View.GONE);
        statusBarColorPicked = false;
        sharedHelper.putStatusBarColor(oldStatusBarColor);
        Snackbar.make(statusBarCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.hamburgerCleaner)
    public void hamburgerCleaner() {
        hamburgerColorPicked = false;
        hamburgerCleaner.setVisibility(View.GONE);
        sharedHelper.putHamburgerColor(oldHamburgerColor);
        hamburgerIcon.setColorFilter(null);
    }

    @OnClick(R.id.actionBarCleaner)
    public void actionBarCleaner() {
        actionBarColorPicked = false;
        actionBarCleaner.setVisibility(View.GONE);
        sharedHelper.putActionBarColor(oldActionBarColor);
        if (oldActionBarColor != null) {
            customizeToolbar.setBackgroundColor(Color.parseColor(oldActionBarColor));
        } else {
            customizeToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @OnClick(R.id.fabButtonCleaner)
    public void fabButtonCleaner() {
        fabMenuButtonColorPicked = false;
        fabButtonCleaner.setVisibility(View.GONE);
        sharedHelper.putMenuButtonColor(oldMenuButtonColor);
        if (oldMenuButtonColor != null) {
            customizeFabMenu.setMenuButtonColorNormal(Color.parseColor(oldMenuButtonColor));
        } else {
            customizeFabMenu.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @OnClick(R.id.ownBubbleCleaner)
    public void ownBubbleCleaner() {
        ownBubbleColorPicked = false;
        ownBubbleCleaner.setVisibility(View.GONE);
        ownBubbleIcon.setColorFilter(null);
        sharedHelper.putOwnBubbleColor(oldOwnBubbleColor);
    }

    @OnClick(R.id.sendButtonCleaner)
    public void sendButtonCleaner() {
        sendButtonColorPicked = false;
        sendButtonCleaner.setVisibility(View.GONE);
        sendButtonIcon.setColorNormal(ContextCompat.getColor(this, R.color.colorPrimary));
        sharedHelper.putSendButtonColor(oldSendButtonColor);
    }

    @OnClick(R.id.opponentBubbleCleaner)
    public void opponentBubbleCleaner() {
        opponentBubbleColorPicked = false;
        opponentBubbleCleaner.setVisibility(View.GONE);
        opponentBubbleIcon.setColorFilter(null);
        sharedHelper.putOpponentBubbleColor(oldOpponentBubbleColor);
    }

    @OnClick(R.id.trendCleaner)
    public void trendCleaner() {
        trendColorPicked = false;
        trendCleaner.setVisibility(View.GONE);
        pickTrendIcon.setColorFilter(null);
        sharedHelper.putTrendColor(oldTrendColor);
    }

    @OnClick(R.id.notificationCleaner)
    public void notificationCleaner() {
        pickNotificationIconColorPicked = false;
        notificationCleaner.setVisibility(View.GONE);
        notificationIcon.setColorFilter(null);
        sharedHelper.putNotificationIconColor(oldNotificationIconColor);
    }

    @OnClick(R.id.shopCleaner)
    public void shopCleaner() {
        pickShopIconColorPicked = false;
        shopCleaner.setVisibility(View.GONE);
        shopIcon.setColorFilter(null);
        sharedHelper.putShopIconColor(oldShopIconColor);
    }

    @OnClick(R.id.leftPanelCleaner)
    public void leftPanelCleaner() {
        pickLeftDrawerColorPicked = false;
        leftPanelCleaner.setVisibility(View.GONE);
        sharedHelper.putLeftSlidingPanelColor(oldLeftSlidingPanelHeaderColor);
        Snackbar.make(leftPanelCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
    }

    @OnClick(R.id.feedCleaner)
    public void feedCleaner() {
        feedColorPicked = false;
        feedCleaner.setVisibility(View.GONE);
        Snackbar.make(feedCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putFeedColor(oldFeedColor);
    }

    @OnClick(R.id.friendsCleaner)
    public void friendsCleaner() {
        friendsBackgroundColorPicked = false;
        friendsCleaner.setVisibility(View.GONE);
        Snackbar.make(friendsCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putFriendsColor(oldFriendsColor);
    }

    @OnClick(R.id.messagesCleaner)
    public void messagesCleaner() {
        messagesBackgroundColorPicked = false;
        messagesCleaner.setVisibility(View.GONE);
        Snackbar.make(messagesCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putMessagesColor(oldMessagesColor);
    }

    @OnClick(R.id.chatFieldCleaner)
    public void chatFieldCleaner() {
        chatFieldColorPicked = false;
        Snackbar.make(actionBarCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putChatFieldTextColor(oldChatFieldColor);
        chatFieldCleaner.setVisibility(View.GONE);
    }

    @OnClick(R.id.opponentProfileCleaner)
    public void clearClicked() {
        opponentProfileColorPicked = false;
        Snackbar.make(actionBarCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putOpponentColor(oldOpponentColor);
        opponentProfileCleaner.setVisibility(View.GONE);
    }

    @OnClick(R.id.ownTextCleaner)
    public void ownTextCleaner() {
        ownTextColorPicked = false;
        Snackbar.make(actionBarCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putOwnTextColor(oldOwnTextColor);
        ownTextCleaner.setVisibility(View.GONE);
    }

    @OnClick(R.id.opponentTextCleaner)
    public void opponentTextCleaner() {
        opponentTextColorPicked = false;
        Snackbar.make(actionBarCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putOpponentTextColor(oldOpponentTextColor);
        opponentBubbleCleaner.setVisibility(View.GONE);
    }

    @OnClick(R.id.chatCleaner)
    public void chatCleaner() {
        chatBackgroundColorPicked = false;
        chatCleaner.setVisibility(View.GONE);
        Snackbar.make(chatCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putChatColor(oldChatColor);
    }

    @OnClick(R.id.requestCleaner)
    public void requestCleaner() {
        requestBackgroundColorPicked = false;
        requestCleaner.setVisibility(View.GONE);
        Snackbar.make(requestCleaner, getString(R.string.colorRemoved), Snackbar.LENGTH_SHORT).show();
        sharedHelper.putMyRequestColor(oldMyRequestColor);
    }

    @Override
    public void onBackPressed() {
        checkAndSendResult();

    }

    private void checkAndSendResult() {
        if (anythingChanged) {
            checkForPendingCustomization();
            Intent intent = new Intent();
            intent.putExtra("anythingChanged", true);
            setResult(Constants.REQUEST_CUSTOMIZE_MADE, intent);
            finish();
        } else if (!actionBarColorPicked && !fabMenuButtonColorPicked && !pickNotificationIconColorPicked && !pickShopIconColorPicked &&
                !pickLeftDrawerColorPicked && !feedColorPicked && !messagesBackgroundColorPicked && !friendsBackgroundColorPicked &&
                !chatBackgroundColorPicked && !requestBackgroundColorPicked && !opponentBubbleColorPicked &&
                !ownBubbleColorPicked && !sendButtonColorPicked && !statusBarColorPicked && !hamburgerColorPicked
                && !opponentTextColorPicked && !chatFieldColorPicked && !ownTextColorPicked && !trendColorPicked && !opponentProfileColorPicked) {
            checkForPendingCustomization();
            Intent intent = new Intent();
            intent.putExtra("anythingChanged", false);
            setResult(Constants.REQUEST_CUSTOMIZE_MADE, intent);
            finish();
        } else {
            checkForPendingCustomization();
            Intent intent = new Intent();
            intent.putExtra("anythingChanged", true);
            setResult(Constants.REQUEST_CUSTOMIZE_MADE, intent);
            finish();
        }

    }

    private void checkForPendingCustomization() {
        if (isSavedToCloud) {
            sharedHelper.putHasPendingCustomizationsToSave(false);
        } else {
            sharedHelper.putHasPendingCustomizationsToSave(true);
        }
    }

    private boolean hasAnythingChanged() {
        if (!actionBarColorPicked && !fabMenuButtonColorPicked && !pickNotificationIconColorPicked && !pickShopIconColorPicked &&
                !pickLeftDrawerColorPicked && !feedColorPicked && !messagesBackgroundColorPicked && !friendsBackgroundColorPicked &&
                !chatBackgroundColorPicked && !requestBackgroundColorPicked && !opponentBubbleColorPicked &&
                !ownBubbleColorPicked && !sendButtonColorPicked && !statusBarColorPicked && !hamburgerColorPicked
                && !opponentTextColorPicked && !chatFieldColorPicked && !ownTextColorPicked && !trendColorPicked &&
                !opponentProfileColorPicked) {
            return false;
        } else {
            return true;
        }
    }

    private void selectAll(boolean checked) {
        allResetCalled = checked;
        statusBarCheckBox.setChecked(checked);
        selectAllCheckBox.setChecked(checked);
        actionBarCheckBox.setChecked(checked);
        menuButtonCheckBox.setChecked(checked);
        sendButtonCheckBox.setChecked(checked);
        notificationCheckBox.setChecked(checked);
        shopCheckBox.setChecked(checked);
        hamburgerCheckBox.setChecked(checked);
        leftPanelCheckBox.setChecked(checked);
        feedCheckBox.setChecked(checked);
        friendsCheckBox.setChecked(checked);
        messagesCheckBox.setChecked(checked);
        chatCheckBox.setChecked(checked);
        requestCheckBox.setChecked(checked);
        opponentBubbleCheckBox.setChecked(checked);
        ownBubbleCheckBox.setChecked(checked);
        opponentTextCheckBox.setChecked(checked);
        ownTextCheckBox.setChecked(checked);
        chatFieldCheckBox.setChecked(checked);
        trendCheckbox.setChecked(checked);
        opponentCheckBox.setChecked(checked);
    }
}
