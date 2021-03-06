package kashmirr.social.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

import kashmirr.social.models.BadgeResponseModel;
import kashmirr.social.models.Config;
import kashmirr.social.models.FeedModel;
import kashmirr.social.models.MafiaMessageModel;
import kashmirr.social.models.MafiaRoomsModel;
import kashmirr.social.models.MyCollectionResponseModel;
import kashmirr.social.models.MyMessagesModel;
import kashmirr.social.models.ParticipantModel;
import kashmirr.social.models.ServerInformationModel;
import kashmirr.social.models.SipResponse;
import kashmirr.social.models.UserModel;
import kashmirr.social.models.UserNotificationModel;
import kashmirr.social.models.VipGlobalChatResponseModel;
import lombok.Getter;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by USER on 2016-06-19.
 */
public class Retrofit {
    private static Retrofit retrofitInstance = new Retrofit();

    public static Retrofit getInstance() {
        return retrofitInstance;
    }

    public InkService inkService;
    public MusicCloudInterface musicCloudInterface;
    private NewsInterface newsInterface;
    @Getter
    public SipService sipService;

    private Retrofit() {
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.MAIN_URL)
                .build();

        retrofit2.Retrofit cloudRetrofit = new retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.CLOUD_API_URL)
                .build();

        retrofit2.Retrofit newsInterfaceRetrofit = new retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.NEWS_BASE_URL)
                .build();

        retrofit2.Retrofit sipRetrofit = new retrofit2.Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(Constants.SIP_MAIN_URL)
                .build();


        inkService = retrofit.create(InkService.class);
        musicCloudInterface = cloudRetrofit.create(MusicCloudInterface.class);
        newsInterface = newsInterfaceRetrofit.create(NewsInterface.class);
        sipService = sipRetrofit.create(SipService.class);

    }

    public MusicCloudInterface getMusicCloudInterface() {
        return musicCloudInterface;
    }

    public NewsInterface getNewsInterface() {
        return newsInterface;
    }

    public interface SipService {
        @POST(Constants.SPI_REGISTER_URL)
        @FormUrlEncoded
        Call<SipResponse> registerSipAccount(@Field("username") String username,
                                             @Field("password") String password,
                                             @Field("email") String email,
                                             @Field("display_name") String displayName);

    }

    public InkService getInkService() {
        return inkService;
    }

    public interface InkService {
        @FormUrlEncoded
        @POST(Constants.REGISTER_URL)
        Call<ResponseBody> register(@Field("login")
                                            String login, @Field("password")
                                            String password, @Field("firstName") String firstName,
                                    @Field("lastName") String lastName);

        @FormUrlEncoded
        @POST(Constants.LOGIN_URL)
        Call<ResponseBody> login(@Field("login") String login, @Field("password") String password);

        @FormUrlEncoded
        @POST(Constants.VIP_MEMBERS_URL)
        Call<List<UserModel>> getVipMembers(@Field("userId") String userId);


        @POST(Constants.FRIENDS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getFriends(@Field("user_id") String userId);

        @POST(Constants.OPEN_PACK_URL)
        @FormUrlEncoded
        Call<ResponseBody> openPack(@Field("userId") String userId, @Field("packId") String packId);

        @POST(Constants.SINGLE_USER_URL)
        @FormUrlEncoded
        Call<ResponseBody> getSingleUserDetails(@Field("user_id") String userId,
                                                @Field("currentUserId") String currentUserId);

        @POST(Constants.REPORT_POST_URL)
        @FormUrlEncoded
        Call<ResponseBody> reportPost(@Field("postId") String postId,
                                      @Field("isGlobalPost") String isGlobalPost,
                                      @Field("causeMessage") String causeMessage,
                                      @Field("reporterId") String reporterId);

        @POST(Constants.CHANGE_VIP_MEMBERSHIP)
        @FormUrlEncoded
        Call<ResponseBody> changeMembership(@Field("user_id") String userId,
                                            @Field("membershipType") String user_id);

        @POST(Constants.POLLFISH_URL)
        @FormUrlEncoded
        Call<ResponseBody> getReward(@Field("userId") String userId,
                                     @Field("token") String token);


        @POST(Constants.BADGE_SHOP_URL)
        @FormUrlEncoded
        Call<BadgeResponseModel> getBagdes(@Field("type") String actionType);

        @POST(Constants.BADGE_SHOP_URL)
        @FormUrlEncoded
        Call<ResponseBody> buyBadge(@Field("type") String actionType,
                                    @Field("userId") String userId,
                                    @Field("badgeId") String badgeId);

        @POST(Constants.VIP_GLOBAL_CHAT_URL)
        @FormUrlEncoded
        Call<VipGlobalChatResponseModel> vipGlobalChatAction(@Nullable @Field("messageId") String messageId,
                                                             @Nullable @Field("userId") String userId,
                                                             @Nullable @Field("message") String message,
                                                             @NonNull @Field("type") String type);

        @POST(Constants.VIP_URL)
        @FormUrlEncoded
        Call<ResponseBody> callVipServer(@Field("user_id") String userId,
                                         @Field("type") String type);

        @POST(Constants.PROFILE_VISIBILITY_URL)
        @FormUrlEncoded
        Call<ResponseBody> changeProfile(@Field("type") String action,
                                         @Field("userId") String userId);

        @POST(Constants.DELETE_USER_PACK)
        @FormUrlEncoded
        Call<ResponseBody> deleteCollection(@Field("userId") String userId,
                                            @Field("packId") String packId);

        @POST(Constants.WHO_VIEWED_URL)
        @FormUrlEncoded
        Call<ResponseBody> getWhoViewed(@Field("currentUserId") String userId);

        @POST(Constants.SEND_FRIEND_REQUEST_URL)
        @FormUrlEncoded
        Call<ResponseBody> requestFriend(@Field("requesterId") String requesterId,
                                         @Field("requestedUserId") String requestedUserId,
                                         @Field("requesterName") String requesterFullName);


        @POST(Constants.GET_USER_PASSWORD)
        @FormUrlEncoded
        Call<ResponseBody> getUserPassword(@Field("userId") String userId,
                                           @Field("token") String token);

        @POST(Constants.CHANGE_PASSWORD)
        @FormUrlEncoded
        Call<ResponseBody> changePassword(@Field("userId") String userId,
                                          @Field("token") String token,
                                          @Field("password") String newPassword);

        @POST(Constants.SECURITY_QUESTION)
        @FormUrlEncoded
        Call<ResponseBody> setSecurityQuestion(@Field("userId") String userId,
                                               @Field("securityQuestion") String securityQuestion,
                                               @Field("securityAnswer") String securityAnswer);

        @POST(Constants.SEND_LOCATION_UPDATE_URL)
        @FormUrlEncoded
        Call<ResponseBody> sendLocationUpdate(@Field("opponent_id") String opponentId,
                                              @Field("longitude") String longitude,
                                              @Field("latitude") String latitude);

        @POST(Constants.USER_COINS_UPDATE)
        @FormUrlEncoded
        Call<ResponseBody> silentCoinsUpdate(@Field("userId") String userId,
                                             @Field("coinsAmount") String coinsAmount,
                                             @Field("coinsToken") String coinsToken);

        @POST(Constants.INSERT_MAFIA_CHAT_URL)
        @FormUrlEncoded
        Call<ResponseBody> silentMafiaMessageInsert(@Field("roomId") int roomId,
                                                    @Field("message") String message,
                                                    @Field("senderId") String senderId,
                                                    @Field("isSystemMessage") boolean isSystemMessage,
                                                    @Field("isMafiaMessage") boolean isMafiaMessage);

        @POST(Constants.START_MAFIA_GAME_URL)
        @FormUrlEncoded
        Call<ResponseBody> startMafiaGame(@Field("roomId") int roomId);

        @POST(Constants.SHOOT_MAFIA_PLAYER)
        @FormUrlEncoded
        Call<ResponseBody> shoot(@Field("roomId") int roomId,
                                 @Field("shooterId") String shooterId,
                                 @Field("victimId") String victimId);

        @POST(Constants.MAFIA_ROOM_PARTICIPANTS)
        @FormUrlEncoded
        Call<List<ParticipantModel>> getMafiaRoomParticipants(@Field("roomId") int roomId);

        @POST(Constants.GET_SINGLE_MAFIA_ROOM)
        @FormUrlEncoded
        Call<MafiaRoomsModel> getSingleMafiaRoom(@Field("roomId") int roomId);

        @POST(Constants.MAFIA_CHAT_URL)
        @FormUrlEncoded
        Call<List<MafiaMessageModel>> getMafiaChat(@Field("roomId") int roomId);

        @POST(Constants.MAFIA_ROOM_CHECK_URL)
        @FormUrlEncoded
        Call<MafiaRoomsModel> checkMafiaRoom(@Field("roomId") int roomId,
                                             @Field("userId") String userId);

        @POST(Constants.SEARCH_GROUP_URL)
        @FormUrlEncoded
        Call<ResponseBody> searchGroups(@Field("userId") String userId,
                                        @Field("textToSearch") String textToSearch);


        @POST(Constants.SEARCH_PERSON_URL)
        @FormUrlEncoded
        Call<ResponseBody> searchPerson(@Field("userId") String currentUserId,
                                        @Field("textToSearch") String textToSearch);

        @POST(Constants.DELETE_POST_URL)
        @FormUrlEncoded
        Call<ResponseBody> deletePost(@Field("postId") String postId,
                                      @Field("attachmentName") String attachmentName);

        @POST(Constants.TRANSFER_COINS_URL)
        @FormUrlEncoded
        Call<ResponseBody> transferCoins(@Field("transferrerId") String transferrerId,
                                         @Field("receiverId") String receiverId, @Field("amount") int amount);

        @POST(Constants.GROUP_REQUESTS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getMyRequests(@Field("ownerId") String userId);

        @POST(Constants.DELETE_ACCOUNT_URL)
        @FormUrlEncoded
        Call<ResponseBody> deleteAccount(@Field("userId") String userId);

        @POST(Constants.INSERT_USER_NOTIFICATION)
        @FormUrlEncoded
        Call<ResponseBody> insertUserNotification(@Field("userId") String userId,
                                                  @Field("notificationTitle") String notificationTitle,
                                                  @Field("notificationMessage") String notificationMessage,
                                                  @Field("isSystemMessage") String isSystemMessage,
                                                  @Field("methodToRun") String methodToRun);

        @POST(Constants.SHOP_COINS_URL)
        Call<ResponseBody> getCoins();

        @POST(Constants.SHOP_PACKS_URL)
        Call<ResponseBody> getPacks();

        @POST(Constants.JOIN_GROUP_URL)
        @FormUrlEncoded
        Call<ResponseBody> requestJoin(@Field("ownerId") String ownerId,
                                       @Field("participantId") String participantId,
                                       @Field("participantName") String participantname,
                                       @Field("participantImage") String participantImage,
                                       @Field("participantGroupId") String participantGroupId);

        @POST(Constants.SEND_MESSAGE_URL)
        @FormUrlEncoded
        Call<ResponseBody> sendMessage(@Field("user_id") String userId,
                                       @Field("opponent_id") String opponentId,
                                       @Field("message") String message,
                                       @Field("timezone") String timezone,
                                       @Field("hasGif") boolean hasGif,
                                       @Field("gifUrl") String gifUrl,
                                       @Field("isAnimated") String isAnimated);


        @POST(Constants.CHECK_BAN_URL)
        @FormUrlEncoded
        Call<ServerInformationModel> checkBan(@Field("userId") String userId,
                                              @Field("appVersion") int appVersion);

        @POST(Constants.WAITERS_QUE)
        @FormUrlEncoded
        Call<ResponseBody> waitersQueAction(@Field("user_id") String userId,
                                            @Field("name") String name,
                                            @Field("status") String status,
                                            @Field("action") String action,
                                            @Field("opponentId") String opponentId);


        @POST(Constants.DISCONNECT_URL)
        @FormUrlEncoded
        Call<ResponseBody> sendDisconnectNotification(@Field("opponentId") String opponentId);

        @POST(Constants.ADD_MAFIA_ROOM_URL)
        @FormUrlEncoded
        Call<ResponseBody> addMafiaRoom(@Field("roomName") String roomName,
                                        @Field("roomLanguage") String roomLanguage,
                                        @Field("gameType") String gameType,
                                        @Field("morningDuration") String morningDuration,
                                        @Field("morningDurationUnit") String morningDurationUnit,
                                        @Field("nightDuration") String nightDuration,
                                        @Field("nightDurationUnit") String nightDurationUnit,
                                        @Field("creatorId") String creatorId,
                                        @Field("maxPlayers") int maxPlayers);

        @POST(Constants.GET_MAFIA_ROOMS_URL)
        Call<List<MafiaRoomsModel>> getMafiaRooms();

        @POST(Constants.GET_MAFIA_OWN_ROOMS)
        @FormUrlEncoded
        Call<List<MafiaRoomsModel>> getMyMafiaRooms(@Field("userId") String userId);

        @POST(Constants.DELETE_MAFIA_ROOM)
        @FormUrlEncoded
        Call<ResponseBody> deleteMafiaRoom(@Field("roomId") int roomId,
                                           @Field("userId") String userId);

        @POST(Constants.LEAVE_MAFIA_ROOM)
        @FormUrlEncoded
        Call<ResponseBody> leaveRoom(@Field("roomId") int roomId,
                                     @Field("userId") String userId);


        @POST(Constants.CHECK_MAFIA_PLAYER)
        @FormUrlEncoded
        Call<ResponseBody> checkMafiaPlayer(@Field("roomId") int roomId,
                                            @Field("checkerId") String checkerId,
                                            @Field("userIdToCheck") String userIdToCheck);

        @POST(Constants.CONFIGS_URL)
        @FormUrlEncoded
        Call<Config> getConfigs(@Field("configType") String type);

        @POST(Constants.MAFIA_VOTE_PLAYER)
        @FormUrlEncoded
        Call<ResponseBody> voteMafiaPlayer(@Field("voterId") String voterId,
                                           @Field("roomId") int roomId,
                                           @Field("userToVote") String userToVote);

        @POST(Constants.GET_USER_NOTIFICATIONS)
        @FormUrlEncoded
        Call<List<UserNotificationModel>> getUserNotifications(@Field("userId") String userId);

        @POST(Constants.MAFIA_REMOVE_VOTE_PLAYER)
        @FormUrlEncoded
        Call<ResponseBody> removeMafiaPlayerVote(@Field("voterId") String voterId,
                                                 @Field("roomId") int roomId,
                                                 @Field("userToUnvote") String userToVote);


        @POST(Constants.JOIN_MAFIA_ROOM)
        @FormUrlEncoded
        Call<ResponseBody> joinRoom(@Field("roomId") int roomId,
                                    @Field("userId") String userId);


        @POST(Constants.CUSTOMIZATION_URL)
        @FormUrlEncoded
        Call<ResponseBody> saveCustomization(@Field("type") String type,
                                             @Field("userId") String userId,
                                             @Field("statusBar") String statusBar,
                                             @Field("actionBar") String actionBar,
                                             @Field("menuButton") String menuButton,
                                             @Field("sendButton") String sendButton,
                                             @Field("notificationIcon") String notificationIcon,
                                             @Field("shopIcon") String shopIcon,
                                             @Field("hamburgerIcon") String hamburgerIcon,
                                             @Field("leftHeader") String leftHeader,
                                             @Field("feedBackground") String feedBackground,
                                             @Field("friendsBackground") String friendsBackground,
                                             @Field("messagesBackground") String messagesBackground,
                                             @Field("chatBackground") String chatBackground,
                                             @Field("requestBackground") String requestBackground,
                                             @Field("opponentBubble") String opponentBubble,
                                             @Field("ownBubble") String ownBubble,
                                             @Field("opponentText") String opponentText,
                                             @Field("ownText") String ownText,
                                             @Field("chatField") String chatField,
                                             @Field("trendColor") String trendColor,
                                             @Field("opponentProfileColor") String opponentProfile);

        @POST(Constants.CUSTOMIZATION_URL)
        @FormUrlEncoded
        Call<ResponseBody> restoreCustomization(@Field("userId") String userId, @Field("type") String type);

        @POST(Constants.CUSTOMIZATION_URL)
        @FormUrlEncoded
        Call<ResponseBody> removeFromCloud(@Field("userId") String userId, @Field("type") String type);

        @POST(Constants.SINGLE_USER_MESSAGES)
        @FormUrlEncoded
        Call<MyMessagesModel> getMyMessages(@Field("user_id") String userId);

        @POST(Constants.GROUP_PARTICIPANTS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getParticipants(@Field("userId") String userId,
                                           @Field("groupId") String groupId);

        @POST(Constants.GROUP_MESSAGES_URL)
        @FormUrlEncoded
        Call<ResponseBody> getGroupMessages(@Field("group_id") String groupId,
                                            @Field("user_id") String userId);


        @POST(Constants.GROUP_MESSAGES_OPTIONS_URL)
        @FormUrlEncoded
        Call<ResponseBody> changeGroupMessages(@Field("fileName") String fileName,
                                               @Field("type") String type,
                                               @Field("message") String message,
                                               @Field("messageId") String messageId);

        @POST(Constants.GROUP_OPTIONS_URL)
        @FormUrlEncoded
        Call<ResponseBody> groupOptions(@Field("type") String type,
                                        @Field("groupId") String groupId,
                                        @Field("groupName") String groupName,
                                        @Field("groupDescription") String groupDescription);


        @POST(Constants.RESPOND_TO_REQUEST_URL)
        @FormUrlEncoded
        Call<ResponseBody> respondToRequest(@Field("respondType") String respondType,
                                            @Field("participantId") String participantId,
                                            @Field("participantName") String participantName,
                                            @Field("participantImage") String participantImage,
                                            @Field("groupId") String groupId);

        @POST(Constants.GET_GROUP_URL)
        @FormUrlEncoded
        Call<ResponseBody> getGroups(@Field("user_id") String userId,
                                     @Field("type") String type);

        @POST(Constants.REMOVE_FRIEND_URL)
        @FormUrlEncoded
        Call<ResponseBody> removeFriend(@Field("ownerId") String ownerId,
                                        @Field("friendId") String friendId);


        @POST(Constants.USER_COINS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getCoins(@Field("user_id") String userId);

        @POST(Constants.ADD_GROUP_URL)
        @FormUrlEncoded
        Call<ResponseBody> createGroup(@Field("user_id") String userId,
                                       @Field("base64") String base64,
                                       @Field("groupName") String groupName,
                                       @Field("groupDescription") String groupDescription,
                                       @Field("groupColor") String groupColor,
                                       @Field("ownerName") String ownerName,
                                       @Field("ownerImage") String ownerImage);

        @Multipart
        @POST(Constants.ADD_GROUP_MESSAGE_URL)
        Call<ResponseBody> sendGroupMessage(@PartMap Map<String, ProgressRequestBody> map,
                                            @Part("group_id") String groupId,
                                            @Part("group_message") String groupMessage,
                                            @Part("sender_id") String senderId,
                                            @Part("sender_image") String senderImage,
                                            @Part("sender_name") String senderName);


        @POST(Constants.ADD_COMMENT_URL)
        @FormUrlEncoded
        Call<ResponseBody> addComment(@Field("commenter_id") String commenterId, @Field("commenter_image") String commenterImage,
                                      @Field("comment_body") String commentBody,
                                      @Field("post_id") String postId,
                                      @Field("first_name") String firstName,
                                      @Field("last_name") String lastName,
                                      @Field("stickerUrl") String stickerUrl,
                                      @Field("isAnimated") boolean isAnimated);

        @POST(Constants.CHAT_MESSAGES)
        @FormUrlEncoded
        Call<ResponseBody> getChatMessages(@Field("user_id") String userId);

        @POST(Constants.DELETE_MESSAGE_URL)
        @FormUrlEncoded
        Call<ResponseBody> deleteMessage(@Field("messageId") String messageId,
                                         @Field("currentUserId") String currentUserId,
                                         @Field("opponentId") String opponentId);

        @POST(Constants.REGISTER_TOKEN)
        @FormUrlEncoded
        Call<ResponseBody> registerToken(@Field("user_id") String userId, @Field("token") String token);


        @POST(Constants.USER_GIFS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getUserStickers(@Field("userId") String userId,
                                           @Field("authKey") String authKey);

        @POST(Constants.GET_SINGLE_STICKER_PACK)
        @FormUrlEncoded
        Call<ResponseBody> getsSinglePack(@Field("packId") String packId,
                                          @Field("authKey") String authKey);


        @POST(Constants.GET_USER_COLLECTIONS)
        @FormUrlEncoded
        Call<MyCollectionResponseModel> getUserCollection(@Field("userId") String userId);


        @POST(Constants.REQUEST_DELETE_URL)
        @FormUrlEncoded
        Call<ResponseBody> requestDelete(@Field("user_id") String userId, @Field("opponent_id") String opponentId);

        @POST(Constants.TREND_CATEGORIES_URL)
        @FormUrlEncoded
        Call<ResponseBody> getTrendCategories(@Field("token") String token);

        @POST(Constants.TREND_URL)
        @FormUrlEncoded
        Call<ResponseBody> getTrends(@Field("type") String categoryType);

        @POST(Constants.DELETE_TREND_URL)
        @FormUrlEncoded
        Call<ResponseBody> deleteTrend(@Field("trendId") String trendId);

        @POST(Constants.UPDATE_USER_IMAGE)
        @FormUrlEncoded
        Call<ResponseBody> updateUserImage(@Field("userId") String userId,
                                           @Field("imageLink") String imageLink);

        @POST(Constants.ADD_ADVERTISEMENT_URL)
        @FormUrlEncoded
        Call<ResponseBody> addAdvertisement(@Field("title") String title,
                                            @Field("content") String content,
                                            @Field("imageUrl") String imageUrl,
                                            @Field("externalUrl") String externalUrl,
                                            @Field("category") String category,
                                            @Field("isTop") boolean isTop,
                                            @Field("userId") String userId);

        @POST(Constants.GET_POSTS_URL)
        @FormUrlEncoded
        Call<List<FeedModel>> getPosts(@Field("user_id") String userId,
                                       @Field("offset") String offset,
                                       @Field("count") String count);

        @POST(Constants.GET_USER_POSTS_URL)
        @FormUrlEncoded
        Call<List<FeedModel>> getUserPosts(@Field("user_id") String userId,
                                           @Field("currentUserId") String currentUserId);

        @POST(Constants.GET_LIKED_USERS)
        @FormUrlEncoded
        Call<List<UserModel>> getLikedUsers(@Field("postId") String postId);

        @POST(Constants.GET_COMMENTS_URL)
        @FormUrlEncoded
        Call<ResponseBody> getComments(@Field("userId") String userId,
                                       @Field("post_id") String postId);

        @POST(Constants.SOCIAL_LOGIN_URL)
        @FormUrlEncoded
        Call<ResponseBody> socialLogin(@Field("login") String emailOrLogin,
                                       @Field("firstName") String firstName,
                                       @Field("lastName") String lastName,
                                       @Field("imageUrl") String imageUrl,
                                       @Field("token") String token,
                                       @Field("type") String socialLoginType,
                                       @Field("userLink") String userLink,
                                       @Field("facebookName") String facebookName,
                                       @Field("email") String email);

        @POST(Constants.LIKE_URL)
        @FormUrlEncoded
        Call<ResponseBody> likePost(@Field("user_id") String userId, @Field("post_id") String postId,
                                    @Field("isLiking") int isLiking);


        @POST(Constants.UPDATE_DETAILS)
        @FormUrlEncoded
        Call<ResponseBody> updateUserDetails(@Field("user_id") String userId, @Field("first_name") String firstName,
                                             @Field("last_name") String lastName,
                                             @Field("address") String address,
                                             @Field("phone_number") String phoneNumber,
                                             @Field("relationship") String relationship,
                                             @Field("gender") String gender,
                                             @Field("facebook") String facebook,
                                             @Field("skype") String skype,
                                             @Field("base64") String base64Image,
                                             @Field("status") String status,
                                             @Field("facebook_name") String facebookName,

                                             @Field("image_link") String imageLink);

        @Multipart
        @POST(Constants.MAKE_POST_URL)
        Call<ResponseBody> makePost(@PartMap Map<String, ProgressRequestBody> map,
                                    @Part("user_id") String userId,
                                    @Part("postBody") String postBody,
                                    @Part("googleAddress") String googleAddress,
                                    @Part("imageLink") String userImageLink,
                                    @Part("firstName") String firstName,
                                    @Part("lastName") String lastName,
                                    @Part("timezone") String timezone,
                                    @Part("type") String type,
                                    @Part("postId") String postId,
                                    @Part("shouldDelete") String shouldDelete,
                                    @Part("postType") String postType);


        @POST(Constants.MAKE_POST_URL)
        @FormUrlEncoded
        Call<ResponseBody> makePost(@Field("user_id") String userId,
                                    @Field("postBody") String postBody,
                                    @Field("googleAddress") String googleAddress,
                                    @Field("imageLink") String userImageLink,
                                    @Field("firstName") String firstName,
                                    @Field("lastName") String lastName,
                                    @Field("timezone") String timezone,
                                    @Field("type") String type,
                                    @Field("editedFileName") String editedFileName,
                                    @Field("postId") String postId,
                                    @Field("shouldDelete") String shouldDelete,
                                    @Field("postType") String postType);


        @POST(Constants.COMMENT_OPTIONS_URL)
        @FormUrlEncoded
        Call<ResponseBody> commentOptions(@Field("type") String type,
                                          @Field("commentId") String commentId,
                                          @Field("newCommentBody") String $newCommentBody);

        @POST(Constants.GET_USER_LOGIN)
        @FormUrlEncoded
        Call<ResponseBody> getUserLogin(@Field("login") String login,
                                        @Field("token") String token);

        @POST(Constants.POLICY_URL)
        Call<ResponseBody> getPolicy();


        @POST(Constants.TEMPORARY_PASSWORD)
        @FormUrlEncoded
        Call<ResponseBody> getTemporaryPassword(@Field("login") String inputLogin,
                                                @Field("token") String token);

        @POST(Constants.SET_USER_COINS_URL)
        @FormUrlEncoded
        Call<ResponseBody> setCoins(@Field("userId") String userId,
                                    @Field("coinsCount") String coinsCount);

        @POST(Constants.TEST_URL)
        Call<ResponseBody> testCall();

        @POST(Constants.REMOVE_NOTIFICATION)
        @FormUrlEncoded
        Call<ResponseBody> removeNotification(@Field("notificationId") String notificationId);

        @POST(Constants.CHECK_NOTIFICATIONS_READ)
        @FormUrlEncoded
        Call<ResponseBody> checkNotificationAsRead(@Field("userId") String userId);

        @POST(Constants.HAS_UNREAD_NOTIFICATIONS)
        @FormUrlEncoded
        Call<ResponseBody> hasUnreadNotifications(@Field("userId") String userId);
    }

    public interface MusicCloudInterface {
        @GET("/tracks?client_id=" + Constants.CLOUD_CLIENT_ID)
        Call<ResponseBody> getAllTracks();

        @GET("/tracks?client_id=" + Constants.CLOUD_CLIENT_ID)
        Call<ResponseBody> searchSong(@Query("q") String searchString);

    }

    public interface NewsInterface {
        @GET()
        Call<ResponseBody> getNews(@Url() String fullUrl);
    }
}
