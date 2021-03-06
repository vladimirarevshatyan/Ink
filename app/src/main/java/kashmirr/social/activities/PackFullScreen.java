package kashmirr.social.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.kashmirr.social.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.ErrorCause;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import kashmirr.social.utils.User;
import okhttp3.ResponseBody;
import tyrantgit.explosionfield.ExplosionField;

import static kashmirr.social.fragments.Packs.PACK_BUY_RESULT_CODE;
import static kashmirr.social.utils.Constants.PACK_BACKGROUND_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.PACK_CONTENT_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.PACK_ID_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.PACK_IMAGE_BUNDLE_KEY;

public class PackFullScreen extends BaseActivity {
    private ExplosionField mExplosionField;
    @BindView(R.id.activity_pack_full_screen)
    View rootLayout;
    @BindView(R.id.buyButton)
    Button buyButton;

    @BindView(R.id.packBackground)
    ImageView packBackground;

    @BindView(R.id.packImage)
    ImageView packImage;

    @BindView(R.id.packContent)
    TextView packContent;

    private Dialog mProgressDialog;
    private String packId;
    private SharedHelper sharedHelper;
    private Dialog loadingDialog;
    private boolean packBackgroundLoaded;
    private boolean packImageLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pack_full_screen);
        ButterKnife.bind(this);
        changeButtonState(false);
        sharedHelper = new SharedHelper(this);
        Bundle extras = getIntent().getExtras();
        packId = extras.getString(PACK_ID_BUNDLE_KEY);
        initializeDialog();
        mExplosionField = ExplosionField.attach2Window(this);
        loadingDialog = new Dialog(this, R.style.Theme_Transparent);
        loadingDialog.setContentView(R.layout.dim_comment_layout);
        TextView content = (TextView) loadingDialog.findViewById(R.id.addingCommentText);
        content.setText(getString(R.string.loadingText));
        loadingDialog.show();
        String packBackgroundURL = extras.getString(PACK_BACKGROUND_BUNDLE_KEY);
        String packImageURL = extras.getString(PACK_IMAGE_BUNDLE_KEY);
        String packContentString = extras.getString(PACK_CONTENT_BUNDLE_KEY);
        packContent.setText(packContentString);

        ImageLoader.loadImage(this, false, false, Constants.MAIN_URL + Constants.PACK_BACKGROUNDS_FOLDER + packBackgroundURL, 0, R.drawable.big_image_place_holder, packBackground, new ImageLoader.ImageLoadedCallback() {
            @Override
            public void onImageLoaded(Object result, Exception e) {
                packBackgroundLoaded = true;
                notifyImagesLoaded();
            }
        });

        ImageLoader.loadImage(this, false, false, Constants.MAIN_URL + Constants.PACK_BACKGROUNDS_FOLDER + packImageURL, 0, R.drawable.user_image_placeholder, packImage, new ImageLoader.ImageLoadedCallback() {
            @Override
            public void onImageLoaded(Object result, Exception e) {
                packImageLoaded = true;
                notifyImagesLoaded();
            }
        });

    }


    @OnClick(R.id.buyButton)
    public void buyClicked() {
        buy(rootLayout);
    }

    public void notifyImagesLoaded() {
        if (packImageLoaded && packBackgroundLoaded) {
            loadingDialog.dismiss();
            changeButtonState(true);
        }
    }

    private void buy(View view) {
        changeButtonState(false);
        mExplosionField.explode(view, new ExplosionField.ExplosionAnimationListener() {
            @Override
            public void onAnimationEnd() {
                showProgress();
                openPack(packId);
            }
        });

    }

    private void changeButtonState(boolean state) {
        buyButton.setEnabled(state);
        buyButton.setClickable(state);
    }

    private void initializeDialog() {
        mProgressDialog = new Dialog(this);
        mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mProgressDialog.setContentView(R.layout.dialog_progress);
        mProgressDialog.setCancelable(false);
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void showProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.show();
            }
        });
    }

    public void hideProgress() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressDialog.dismiss();
            }
        });
    }

    private void openPack(final String packId) {
        makeRequest(Retrofit.getInstance().getInkService().openPack(sharedHelper.getUserId(), packId), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    hideProgress();
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    AlertDialog.Builder builder = new AlertDialog.Builder(PackFullScreen.this);
                    builder.setCancelable(false);
                    if (success) {
                        int userCoinsLeft = jsonObject.optInt("userCoinsLeft");
                        User.get().setCoins(userCoinsLeft);
                        builder.setTitle(getString(R.string.congratulation));
                        builder.setMessage(getString(R.string.gift_bought_message));
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                setResult(PACK_BUY_RESULT_CODE);
                                finish();
                                overrideActivityAnimation();
                            }
                        });
                        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();
                                setResult(PACK_BUY_RESULT_CODE);
                                finish();
                                overrideActivityAnimation();
                            }
                        });
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();
                                setResult(PACK_BUY_RESULT_CODE);
                                finish();
                                overrideActivityAnimation();
                            }
                        });
                        builder.show();
                    } else {
                        String cause = jsonObject.optString("cause");
                        if (cause.equals(ErrorCause.PACK_ALREADY_BOUGHT)) {
                            builder.setTitle(getString(R.string.error));
                            builder.setMessage(getString(R.string.gift_already_bought));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                    overrideActivityAnimation();
                                }
                            });
                            builder.show();
                        } else if (cause.equals(ErrorCause.NOT_ENOUGH_COINS)) {

                            builder.setMessage(getString(R.string.not_enough_coins));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                    overrideActivityAnimation();
                                }
                            });
                            builder.show();
                        } else {

                            builder.setTitle(getString(R.string.error));
                            builder.setMessage(getString(R.string.serverErrorText));
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    finish();
                                    overrideActivityAnimation();
                                }
                            });
                            builder.show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    hideProgress();
                    finish();
                    overrideActivityAnimation();
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgress();
                    finish();
                    overrideActivityAnimation();
                }
            }

            @Override
            public void onRequestFailed(Object[] result) {
                finish();
                overrideActivityAnimation();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overrideActivityAnimation();
    }
}
