package kashmirr.social.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.kashmirr.social.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kashmirr.social.adapters.RequestsAdapter;
import kashmirr.social.interfaces.RequestCallback;
import kashmirr.social.interfaces.RequestListener;
import kashmirr.social.models.RequestsModel;
import kashmirr.social.service.SocketService;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.DimDialog;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import okhttp3.ResponseBody;

import static kashmirr.social.utils.Constants.EVENT_ACCEPT_FRIEND_REQUEST;
import static kashmirr.social.utils.Constants.EVENT_DECLINE_FRIEND_REQUEST;

public class RequestsView extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, RequestListener {

    private SharedHelper sharedHelper;
    @BindView(R.id.requestsRecycler)
    RecyclerView requestsRecycler;
    @BindView(R.id.requestSwipe)
    SwipeRefreshLayout requestSwipe;
    @BindView(R.id.noRequestsLayout)
    RelativeLayout noRequestsLayout;
    @BindView(R.id.requestsRootLayout)
    RelativeLayout requestsRootLayout;
    private RequestsAdapter requestsAdapter;
    private RequestsModel requestsModel;
    private List<RequestsModel> requestsModels;
    private SocketService socketService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests_view);
        ButterKnife.bind(this);
        sharedHelper = new SharedHelper(this);
        if (sharedHelper.getMyRequestColor() != null) {
            requestsRootLayout.setBackgroundColor(Color.parseColor(sharedHelper.getMyRequestColor()));
        }
        requestSwipe.setOnRefreshListener(this);
        requestsModels = new ArrayList<>();
        requestsAdapter = new RequestsAdapter(requestsModels, this);
        requestsAdapter.setRequestListener(this);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        requestsRecycler.setLayoutManager(new LinearLayoutManager(this));
        requestsRecycler.setItemAnimator(itemAnimator);
        requestsRecycler.setAdapter(requestsAdapter);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.myRequests));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getMyRequests();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getMyRequests();
    }

    private void getMyRequests() {
        if (requestsModels != null) {
            requestsModels.clear();
            requestsAdapter.notifyDataSetChanged();
        }
        if (!requestSwipe.isRefreshing()) {
            requestSwipe.post(new Runnable() {
                @Override
                public void run() {
                    requestSwipe.setRefreshing(true);
                }
            });
        }

        makeRequest(Retrofit.getInstance().getInkService().getMyRequests(sharedHelper.getUserId()), requestSwipe, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        JSONArray jsonArray = jsonObject.optJSONArray("requests");
                        if (jsonArray.length() <= 0) {
                            noRequestsLayout.setVisibility(View.VISIBLE);
                            requestSwipe.setRefreshing(false);
                            return;
                        }
                        noRequestsLayout.setVisibility(View.GONE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject eachObject = jsonArray.getJSONObject(i);

                            String requesterId = eachObject.optString("requester_id");
                            String requesterName = eachObject.optString("requester_name");
                            String requesterImage = eachObject.optString("requester_image");
                            String requestId = eachObject.optString("request_id");
                            boolean isSocialAccount = eachObject.optBoolean("isSocialAccount");
                            String isFriend = eachObject.optString("isFriend");

                            String type = eachObject.optString("type");
                            String groupName = "";
                            String groupOwnerId = "";
                            String requestedGroupId = "";
                            if (type.equals(Constants.REQUEST_RESPONSE_TYPE_GROUP)) {
                                groupName = eachObject.optString("group_name");
                                groupOwnerId = eachObject.optString("group_owner_id");
                                requestedGroupId = eachObject.optString("requested_group_id");
                            }

                            requestsModel = new RequestsModel(type, isSocialAccount, Boolean.valueOf(isFriend), groupOwnerId, requesterId, requesterName, requesterImage, requestedGroupId,
                                    requestId, groupName);
                            requestsModels.add(requestsModel);
                            requestsAdapter.notifyDataSetChanged();
                        }

                    } else {
                        getMyRequests();
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
    public void onRefresh() {

        getMyRequests();
    }

    @Override
    public void onAcceptClicked(int position) {
        DimDialog.showDimDialog(RequestsView.this, getString(R.string.accepting));
        RequestsModel requestsModel = requestsModels.get(position);
        if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_GROUP)) {
            acceptGroupRequest(position);
        } else if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_FRIEND_REQUEST)) {
            acceptFriendRequest(position);
        } else if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_LOCATION_REQUEST)) {
            acceptLocationRequest(position);
        }

    }


    @Override
    public void onDeclineClicked(int position) {
        DimDialog.showDimDialog(RequestsView.this, getString(R.string.declining));
        RequestsModel requestsModel = requestsModels.get(position);
        if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_GROUP)) {
            denyGroupRequest(position);
        } else if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_FRIEND_REQUEST)) {
            denyFriendRequest(position);
        } else if (requestsModel.getType().equals(Constants.REQUEST_RESPONSE_TYPE_LOCATION_REQUEST)) {
            declineLocationRequest(position);
        }

    }

    private void acceptLocationRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        final RequestsModel requestsModel = requestsModels.get(position);

        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.RESPOND_TYPE_ACCEPT_LOCATION_REQUEST, sharedHelper.getUserId(),
                sharedHelper.getFirstName() + " " + sharedHelper.getLastName(), requestsModel.getRequestId(), requestsModel.getRequesterId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    DimDialog.hideDialog();
                    if (success) {
                        String fullNameParts[] = requestsModel.getRequesterName().split("\\s");
                        String firstName;
                        String lastName;
                        try {
                            firstName = fullNameParts[0];
                            lastName = fullNameParts[1];
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            firstName = requestsModel.getRequesterName();
                            lastName = "";
                        }

                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                        intent.putExtra("firstName", firstName);
                        intent.putExtra("lastName", lastName);
                        intent.putExtra("hasSession", true);
                        intent.putExtra("opponentId", requestsModel.getRequesterId());
                        intent.putExtra("opponentImage", requestsModel.getRequesterImage());
                        intent.putExtra("isSocialAccount", requestsModel.isSocialAccount());
                        startActivity(intent);

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
                DimDialog.hideDialog();
            }
        });
    }

    private void declineLocationRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        RequestsModel requestsModel = requestsModels.get(position);
        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.RESPOND_TYPE_DENY_LOCATION_REQUEST, requestsModel.getRequestId(),
                sharedHelper.getFirstName() + " "
                        + sharedHelper.getLastName(),
                requestsModel.getRequesterId(), sharedHelper.getUserId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        DimDialog.hideDialog();
                        Snackbar.make(requestsRecycler, getString(R.string.friendRequestDenied), Snackbar.LENGTH_LONG).show();
                        getMyRequests();
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
                DimDialog.hideDialog();
            }
        });
    }

    private void acceptFriendRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        final RequestsModel requestsModel = requestsModels.get(position);
        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.RESPOND_TYPE_ACCEPT_FRIEND_REQUEST, sharedHelper.getUserId(),
                sharedHelper.getFirstName() + " " + sharedHelper.getLastName(), "", requestsModel.getRequesterId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    DimDialog.hideDialog();
                    if (success) {
                        JSONObject requestAcceptedJson = new JSONObject();
                        requestAcceptedJson.put("destinationId", requestsModel.getRequesterId());
                        requestAcceptedJson.put("acceptorFirstName", sharedHelper.getFirstName());
                        requestAcceptedJson.put("acceptorLastName", sharedHelper.getLastName());
                        requestAcceptedJson.put("requestId", requestsModel.getRequestId());

                        socketService.emit(EVENT_ACCEPT_FRIEND_REQUEST, requestAcceptedJson);
                        requestAcceptedJson = null;
                        Snackbar.make(requestsRecycler, getString(R.string.friendRequestAccepted), Snackbar.LENGTH_LONG).show();
                        getMyRequests();
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
                DimDialog.hideDialog();
                getMyRequests();
            }
        });
    }


    private void denyFriendRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        final RequestsModel requestsModel = requestsModels.get(position);
        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.RESPOND_TYPE_DENY_FRIEND_REQUEST, requestsModel.getRequestId(), "",
                "", ""), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        JSONObject requestDeclinedJson = new JSONObject();
                        requestDeclinedJson.put("destinationId", requestsModel.getRequesterId());
                        requestDeclinedJson.put("declinerFirstName", sharedHelper.getFirstName());
                        requestDeclinedJson.put("declinerLastName", sharedHelper.getLastName());
                        requestDeclinedJson.put("requestId", requestsModel.getRequestId());

                        socketService.emit(EVENT_DECLINE_FRIEND_REQUEST, requestDeclinedJson);
                        requestDeclinedJson = null;
                        DimDialog.hideDialog();
                        Snackbar.make(requestsRecycler, getString(R.string.friendRequestDenied), Snackbar.LENGTH_LONG).show();
                        getMyRequests();
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
                DimDialog.hideDialog();
            }
        });
    }

    @Override
    public void onItemClicked(int position) {
        RequestsModel singleModel = requestsModels.get(position);
        String name = singleModel.getRequesterName();
        String[] splited = name.split("\\s+");
        String firstName = splited[0];
        String lastName = splited[1];
        Intent intent = new Intent(getApplicationContext(), OpponentProfile.class);
        intent.putExtra("id", singleModel.getRequesterId());
        intent.putExtra("firstName", firstName);
        intent.putExtra("lastName", lastName);
        intent.putExtra("isFriend", singleModel.isFriend());
        startActivity(intent);
    }

    @Override
    public void onServiceConnected(SocketService socketService) {
        super.onServiceConnected(socketService);
        this.socketService = socketService;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        DimDialog.hideDialog();
        super.onDestroy();
    }

    private void acceptGroupRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        final RequestsModel requestsModel = requestsModels.get(position);
        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.TYPE_ACCEPT_REQUEST,
                requestsModel.getRequesterId(), requestsModel.getRequesterName(), requestsModel.getRequesterImage(),
                requestsModel.getRequestedGroupId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        Snackbar.make(noRequestsLayout, getString(R.string.requestAccepted), Snackbar.LENGTH_SHORT).show();
                        requestsModels.clear();
                        getMyRequests();
                    } else {
                        denyGroupRequest(position);
                    }
                    DimDialog.hideDialog();
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
                DimDialog.hideDialog();
            }
        });
    }

    private void denyGroupRequest(final int position) {
        requestSwipe.post(new Runnable() {
            @Override
            public void run() {
                requestSwipe.setRefreshing(true);
            }
        });
        RequestsModel requestsModel = requestsModels.get(position);
        makeRequest(Retrofit.getInstance().getInkService().respondToRequest(Constants.TYPE_DENY_REQUEST,
                requestsModel.getRequesterId(), requestsModel.getRequesterName(), requestsModel.getRequesterImage(),
                requestsModel.getRequestedGroupId()), null, new RequestCallback() {
            @Override
            public void onRequestSuccess(Object result) {
                try {
                    String responseBody = ((ResponseBody) result).string();
                    JSONObject jsonObject = new JSONObject(responseBody);
                    boolean success = jsonObject.optBoolean("success");
                    if (success) {
                        DimDialog.hideDialog();
                        Snackbar.make(noRequestsLayout, getString(R.string.requestDenied), Snackbar.LENGTH_SHORT).show();
                        requestsModels.clear();
                        getMyRequests();
                    } else {
                        denyGroupRequest(position);
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
                DimDialog.hideDialog();
            }
        });
    }

}
