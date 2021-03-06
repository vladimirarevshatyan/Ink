package kashmirr.social.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.kashmirr.social.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kashmirr.social.callbacks.GeneralCallback;


/**
 * Created by USER on 2016-08-07.
 */
public class SocialSignIn {

    private GoogleApiClient mGoogleApiClient;
    private static final SocialSignIn socialSignIn = new SocialSignIn();

    public void googleSignIn(Activity context, int requestCode) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        if (mGoogleApiClient.hasConnectedApi(Auth.GOOGLE_SIGN_IN_API)) {
            mGoogleApiClient.clearDefaultAccountAndReconnect();
        }else{
            mGoogleApiClient.connect();
        }
        context.startActivityForResult(signInIntent, requestCode);

    }

    public synchronized GoogleApiClient getGooglePlusCircles(final Activity activity, final int requestCode,
                                                             @Nullable final GeneralCallback<JSONArray> resultCallbacks) {

        GoogleSignInOptions gGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestScopes(new Scope(Scopes.PROFILE))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gGoogleSignInOptions)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        Plus.PeopleApi.loadVisible(mGoogleApiClient, null
                        ).setResultCallback(new ResultCallbacks<People.LoadPeopleResult>() {
                            @Override
                            public void onSuccess(@NonNull People.LoadPeopleResult loadPeopleResult) {
                                int personCount = loadPeopleResult.getPersonBuffer().getCount();
                                if (personCount != 0) {
                                    JSONArray friendsArray = new JSONArray();
                                    for (int i = 0; i < personCount; i++) {
                                        Person eachPerson = loadPeopleResult.getPersonBuffer().get(i);
                                        try {
                                            JSONObject eachFriendObject = new JSONObject();
                                            eachFriendObject.put("name", eachPerson.getDisplayName());
                                            eachFriendObject.put("id", eachPerson.getId());
                                            String imageUrl;
                                            if (eachPerson.hasUrl()) {
                                                imageUrl = eachPerson.getImage().getUrl().replaceAll("\\?sz=50", "");
                                            } else {
                                                imageUrl = Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + Constants.FUNNY_USER_IMAGE;
                                            }
                                            eachFriendObject.put("image", imageUrl);
                                            friendsArray.put(eachFriendObject);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    if (resultCallbacks != null) {
                                        resultCallbacks.onSuccess(friendsArray);
                                    }
                                } else {
                                    Snackbar.make(activity.getWindow().getDecorView(), activity.getString(R.string.noGoogleFriend), Snackbar.LENGTH_INDEFINITE).
                                            setAction("OK", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (DimDialog.isDialogAlive()) {
                                                        DimDialog.hideDialog();
                                                    }
                                                }
                                            }).show();
                                    if (resultCallbacks != null) {
                                        loadPeopleResult.getPersonBuffer().release();
                                        resultCallbacks.onFailure(null);
                                    }
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Status status) {
                                if (status.hasResolution()) {
                                    try {
                                        status.startResolutionForResult(activity, requestCode);
                                    } catch (IntentSender.SendIntentException e) {
                                        mGoogleApiClient.connect();
                                    }
                                } else {
                                    if (resultCallbacks != null) {
                                        resultCallbacks.onFailure(null);
                                    }
                                    DimDialog.hideDialog();
                                    Toast.makeText(activity, activity.getString(R.string.error_retriving_circles), Toast.LENGTH_SHORT).show();
                                }

                            }

                        });

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addApi(Plus.API)
                .build();
        mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_OPTIONAL);
        return mGoogleApiClient;
    }


    public void facebookLogin(Context context, CallbackManager callbackManager,
                              @Nullable final GeneralCallback<Map<String, String>> resultCallback) {
        final Map<String, String> resultMap = new HashMap<>();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        String userLink = object.optString("link");
                                        String userId = object.optString("id");
                                        String email = object.optString("email");
                                        String userName = object.optString("name");
                                        JSONObject jsonObject = object.optJSONObject("picture");
                                        JSONObject dataObject = jsonObject.optJSONObject("data");
                                        String imageUrl = dataObject.optString("url");

                                        resultMap.clear();
                                        resultMap.put("link", userLink);
                                        resultMap.put("name", userName);
                                        resultMap.put("email",email);
                                        resultMap.put("id", userId);
                                        resultMap.put("imageUrl", imageUrl);

                                        if (resultCallback != null) {
                                            resultCallback.onSuccess(resultMap);
                                        }

                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "link,name,picture.type(large),email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        resultCallback.onFailure(resultMap);
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        resultCallback.onFailure(resultMap);
                    }
                });
        LoginManager.getInstance().logInWithReadPermissions((Activity) context, Arrays.asList("public_profile", "email"));
    }

    public static SocialSignIn get() {
        return socialSignIn;
    }

    public void destroyGoogleClient() {
        if(mGoogleApiClient!=null){
         mGoogleApiClient.disconnect();
        }
    }
}
