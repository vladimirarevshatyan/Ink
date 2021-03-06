package kashmirr.social.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.google.cloud.translate.Translation;
import com.kashmirr.social.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.LanguageUtils;
import kashmirr.social.utils.TranslationUtils;

import static kashmirr.social.utils.Constants.KILL_APP_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.SERVER_NOTIFICATION_CONTENT_BUNDLE_KEY;
import static kashmirr.social.utils.Constants.WARNING_TEXT_BUNDLE_KEY;

public class ServerNotification extends BaseActivity {


    @BindView(R.id.serverNewsContent)
    TextView serverNewsContent;
    private boolean killApp;
    private String warningText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_notification);
        ButterKnife.bind(this);
        ActionBar actionBar = getSupportActionBar();
        serverNewsContent.setMovementMethod(new LinkMovementMethod());
        if (actionBar != null) {
            actionBar.setTitle(R.string.serverNewsTitle);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        setStatusBarColor(R.color.orangeColor);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            final String content = bundle.getString(SERVER_NOTIFICATION_CONTENT_BUNDLE_KEY);
            killApp = bundle.getBoolean(KILL_APP_BUNDLE_KEY);
            warningText = bundle.getString(WARNING_TEXT_BUNDLE_KEY);
            if (killApp) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    TranslationUtils.Translate(warningText, Constants.APP_SOURCE_LANGUAGE, LanguageUtils.getLocalLanguage(this), new TranslationUtils.TranslationCallback() {
                        @TargetApi(Build.VERSION_CODES.N)
                        @Override
                        public void onTranslationDone(Translation result) {
                            serverNewsContent.setText(Html.fromHtml(result.getTranslatedText(), Html.FROM_HTML_MODE_LEGACY));
                        }

                        @TargetApi(Build.VERSION_CODES.N)
                        @Override
                        public void onTranslationFailed(Exception e) {
                            serverNewsContent.setText(Html.fromHtml(warningText, Html.FROM_HTML_MODE_LEGACY));
                        }
                    });
                } else {

                    TranslationUtils.Translate(warningText, Constants.APP_SOURCE_LANGUAGE, LanguageUtils.getLocalLanguage(this), new TranslationUtils.TranslationCallback() {
                        @Override
                        public void onTranslationDone(Translation result) {
                            serverNewsContent.setText(Html.fromHtml(result.getTranslatedText()));
                        }

                        @Override
                        public void onTranslationFailed(Exception e) {
                            serverNewsContent.setText(Html.fromHtml(warningText));
                        }
                    });
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    TranslationUtils.Translate(content, Constants.APP_SOURCE_LANGUAGE, LanguageUtils.getLocalLanguage(this), new TranslationUtils.TranslationCallback() {
                        @TargetApi(Build.VERSION_CODES.N)
                        @Override
                        public void onTranslationDone(Translation result) {
                            serverNewsContent.setText(Html.fromHtml(result.getTranslatedText(), Html.FROM_HTML_MODE_LEGACY));
                        }

                        @TargetApi(Build.VERSION_CODES.N)
                        @Override
                        public void onTranslationFailed(Exception e) {
                            serverNewsContent.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
                        }
                    });
                } else {
                    TranslationUtils.Translate(content, Constants.APP_SOURCE_LANGUAGE, LanguageUtils.getLocalLanguage(this), new TranslationUtils.TranslationCallback() {
                        @Override
                        public void onTranslationDone(Translation result) {
                            serverNewsContent.setText(Html.fromHtml(result.getTranslatedText()));
                        }

                        @Override
                        public void onTranslationFailed(Exception e) {
                            serverNewsContent.setText(Html.fromHtml(content));
                        }
                    });
                }

            }

        }
    }


    @OnClick(R.id.closeServerNotification)
    public void close() {
        if (killApp) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finishAffinity();
            finish();
        } else {
            finish();
            overridePendingTransition(R.anim.activity_scale_up, R.anim.activity_scale_down);
        }

    }

    @Override
    public void onBackPressed() {
        if (killApp) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            homeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finishAffinity();
            finish();
        }
    }
}
