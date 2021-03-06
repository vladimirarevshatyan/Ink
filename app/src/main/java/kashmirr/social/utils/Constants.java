package kashmirr.social.utils;

/**
 * Created by USER on 2016-06-19.
 */
public class Constants {
    /**
     * LinkedIn Constants
     */
    public static final String LINKEDIN_HOST = "api.linkedin.com";
    public static final String PEOPLE_LINKEDIN_URL = "https://" + LINKEDIN_HOST + "/v1/people/~:(id,first-name,last-name,public-profile-url,picture-urls::(original))";
    public static final String LINKEDIN_SHARE_URL = "https://" + LINKEDIN_HOST + "/v1/people/~/shares";

    /**
     * App main url
     */
    public static final String MAIN_URL = "http://35.196.113.134/Ink_Base_project_1992_no_15_dp_deployed_57_olAllolclslf_8954/";
    public static final String SOCKET_URL = "http://35.196.113.134:4000";
    public static final String SIP_MAIN_URL = "https://blink.sipthor.net/";
    public static final String SPI_REGISTER_URL = "enrollment-webrtc.phtml";
    public static final String SIP_OUTBOUND_PROXY_URL = "proxy.sipthor.net:5060";
    public static final String MAFIA_SOCKET_URL = "http://35.196.113.134:4500";
    public static final String FILE_SHARING_URL = "http://35.196.113.134:3000";
    public static final String SIP_DOMAIN_URL = "sip2sip.info";

    /**
     * The appendable of main url
     */
    public static final String USER_IMAGES_FOLDER = "UserImages/";
    public static final String UPDATE_USER_IMAGE = "UpdateUserImage.php";
    public static final String FACEBOOK_GRAPH_FIRST_URL = "http://graph.facebook.com/";
    public static final String FACEBOOK_GRAPH_LAST_URL = "/picture?type=large";
    public static final String PACK_BACKGROUNDS_FOLDER = "PackBackgrounds/";
    public static final String GROUP_IMAGES_FOLDER = "GroupImages/";
    public static final String ADD_GROUP_URL = "AddGroup.php";
    public static final String CHANGE_VIP_MEMBERSHIP = "ChangeVipMembership.php";
    public static final String START_MAFIA_GAME_URL = "StartGame.php";
    public static final String CONFIGS_URL = "AppConfigs.php";
    public static final String CHECK_BAN_URL = "CheckBan.php";
    public static final String WHO_VIEWED_URL = "WhoViewed.php";
    public static final String USER_COINS_UPDATE = "CoinsUpdate.php";
    public static final String TEST_URL = "Test.php";
    public static final String REMOVE_NOTIFICATION = "RemoveNotification.php";
    public static final String CHECK_NOTIFICATIONS_READ = "CheckNotificationsRead.php";
    public static final String HAS_UNREAD_NOTIFICATIONS = "HasUnreadNotification.php";
    public static final String GET_USER_POSTS_URL = "GetUserPosts.php";
    public static final String GROUP_REQUESTS_URL = "GetUserRequests.php";
    public static final String INSERT_USER_NOTIFICATION = "InsertUserNotification.php";
    public static final String ADD_MAFIA_ROOM_URL = "AddMafiaRoom.php";
    public static final String GET_MAFIA_ROOMS_URL = "GetMafiaRooms.php";
    public static final String VIP_GLOBAL_CHAT_URL = "VipGlobalChat.php";
    public static final String DELETE_TREND_URL = "DeleteTrend.php";
    public static final String USER_GIFS_URL = "GetUserGifs.php";
    public static final String GET_USER_COLLECTIONS = "GetUserCollection.php";
    public static final String GET_SINGLE_MAFIA_ROOM = "GetSingleMafiaRoom.php";
    public static final String GET_SINGLE_STICKER_PACK = "GetSingleStickerPack.php";
    public static final String PROFILE_VISIBILITY_URL = "ProfileVisibility.php";
    public static final String DELETE_USER_PACK = "DeleteUserPack.php";
    public static final String DELETE_ACCOUNT_URL = "DeleteAccount.php";
    public static final String SEARCH_GROUP_URL = "SearchGroups.php";
    public static final String SEND_CHAT_ROULETTE_MESSAGE = "SendChatRouletteMessage.php";
    public static final String GET_USER_STATUS = "GetUserStatus.php";
    public static final String DISCONNECT_URL = "Disconnect.php";
    public static final String BADGE_SHOP_URL = "BadgeShop.php";
    public static final String USER_COINS_URL = "GetUserCoins.php";
    public static final String SHOP_COINS_URL = "GetCoinsShop.php";
    public static final String PING_TIME_URL = "PingTime.php";
    public static final String CUSTOMIZATION_URL = "Customization.php";
    public static final String SHOP_PACKS_URL = "GetPacks.php";
    public static final String JOIN_GROUP_URL = "JoinGroup.php";
    public static final String ADD_GROUP_MESSAGE_URL = "AddGroupMessage.php";
    public static final String GET_GROUP_URL = "GetGroups.php";
    public static final String REGISTER_URL = "Register.php";
    public static final String MY_GROUPS_URL = "UserGroups.php";
    public static final String SEARCH_PERSON_URL = "SearchPerson.php";
    public static final String GROUP_PARTICIPANTS_URL = "GroupParticipants.php";
    public static final String VIP_URL = "Vip.php";
    public static final String GROUP_MESSAGES_URL = "GroupMessages.php";
    public static final String GROUP_MESSAGES_OPTIONS_URL = "GroupMessagesOptions.php";
    public static final String NOTIFY_OPPONENT = "NotifyOpponent.php";
    public static final String DELETE_MAFIA_ROOM = "DeleteMafiaRoom.php";
    public static final String ADD_ADVERTISEMENT_URL = "CreateTrend.php";
    public static final String LEAVE_MAFIA_ROOM = "LeaveMafiaRoom.php";
    public static final String CHECK_MAFIA_PLAYER = "CheckMafiaPlayer.php";
    public static final String MAFIA_VOTE_PLAYER = "MafiaVotePlayer.php";
    public static final String MAFIA_REMOVE_VOTE_PLAYER = "MafiaRemoveVotePlayer.php";
    public static final String GET_USER_NOTIFICATIONS = "GetUserNotificaitons.php";
    public static final String MAFIA_ROOM_PARTICIPANTS = "GetMafiaRoomParticipants.php";
    public static final String JOIN_MAFIA_ROOM = "JoinMafiaRoom.php";
    public static final String RESPOND_TO_REQUEST_URL = "RespondToRequest.php";
    public static final String REMOVE_FRIEND_URL = "RemoveFriend.php";
    public static final String RESPOND_TYPE_ACCEPT_FRIEND_REQUEST = "acceptFriendRequest";
    public static final String CONFIG_TYPE_POLICY = "policyUrl";
    public static final String RESPOND_TYPE_DENY_FRIEND_REQUEST = "denyFriendRequest";
    public static final String RESPOND_TYPE_DENY_LOCATION_REQUEST = "denyLocationRequest";
    public static final String RESPOND_TYPE_ACCEPT_LOCATION_REQUEST = "acceptLocationRequest";
    public static final String LOGIN_URL = "Login.php";
    public static final String OPEN_PACK_URL = "OpenPack.php";
    public static final String POLLFISH_URL = "PollFishReward.php";
    public static final String MAFIA_CHAT_URL = "GetMafiaChat.php";
    public static final String INSERT_MAFIA_CHAT_URL = "InsertMafiaChat.php";
    public static final String FRIENDS_URL = "UserFriends.php";
    public static final String SINGLE_USER_URL = "SingleUser.php";
    public static final String REPORT_POST_URL = "ReportPost.php";
    public static final String MAFIA_ROOM_CHECK_URL = "MafiaRoomCheck.php";
    public static final String SEND_FRIEND_REQUEST_URL = "SendFriendRequest.php";
    public static final String NEWS_BASE_URL = "http://api.breakingnews.com";
    public static final String NEWS_PRIMARY_URL = "/api/v1/item/?format=json";
    public static final String DELETE_MESSAGE_URL = "DeleteMessage.php";
    public static final String SEND_LOCATION_UPDATE_URL = "SendLocationUpdate.php";
    public static final String GET_USER_PASSWORD = "GetPassword.php";
    public static final String REQUEST_LOCATION_URL = "RequestLocation.php";
    public static final String MESSAGES_URL = "Messages.php";
    public static final String GET_MAFIA_OWN_ROOMS = "GetOwnRooms.php";
    public static final String UPLOADED_FILES_DIR = "UserUploads/";
    public static final String POLICY_URL = "Policy.php";
    public static final String SEND_MESSAGE_URL = "SendMessage.php";
    public static final String SET_USER_COINS_URL = "SetUserCoins.php";
    public static final String SINGLE_USER_MESSAGES = "SingleUserMessages.php";
    public static final String CHAT_MESSAGES = "ChatMessages.php";
    public static final String SHOOT_MAFIA_PLAYER = "ShootMafiaPlayer.php";
    public static final String GROUP_OPTIONS_URL = "GroupOptions.php";
    public static final String CHANGE_PASSWORD = "ChangePassword.php";
    public static final String SECURITY_QUESTION = "SecurityQuestion.php";
    public static final String REGISTER_TOKEN = "RegisterToken.php";
    public static final String REQUEST_DELETE_URL = "RequestDeleteMessage.php";
    public static final String GET_POSTS_URL = "GetPosts.php";
    public static final String UPDATE_DETAILS = "UpdateDetails.php";
    public static final String MAKE_POST_URL = "MakePost.php";
    public static final String DELETE_POST_URL = "DeletePost.php";
    public static final String COMMENT_OPTIONS_URL = "CommentOptions.php";
    public static final String GET_COMMENTS_URL = "GetComments.php";
    public static final String GET_LIKED_USERS = "GetLikedUsers.php";
    public static final String SOCIAL_LOGIN_URL = "SocialLogin.php";
    public static final String TEMPORARY_PASSWORD = "TemporaryPassword.php";
    public static final String ADD_COMMENT_URL = "AddComment.php";
    public static final String GET_USER_LOGIN = "GetUserLogin.php";
    public static final String LIKE_URL = "LikePost.php";
    public static final String WAITERS_QUE = "WaitRoomQue.php";
    public static final String GET_WAITERS = "GetWaitersQue.php";
    public static final String TREND_CATEGORIES_URL = "TrendCategories.php";
    public static final String TREND_URL = "GetTrends.php";
    public static final String CHECK_IS_FRIEND_URL = "CheckIsFriend.php";
    public static final String VIP_MEMBERS_URL = "VipMembers.php";
    public static final String TRANSFER_COINS_URL = "TransferCoins.php";
    public static final String COIN_ICON_FOLDER = "CoinsIcon/";

    /**
     * App Constants
     */

    public static final String ANDROID_RESOURCE_DIR = "android.resource://";
    public static final String HUAWEI_MODEL = "HUAWEI";
    public static final String APP_SOURCE_LANGUAGE = "en";
    public static final String SERVER_TIME_ZONE = "America/Los_Angeles";
    public static final String GOOGLE_API_KEY = "AIzaSyDwESqkAVNo_6TQi5F2WDH-8mYJ10bRPDc";
    public static final String CLOUD_CLIENT_ID = "15bf6f497229585b1cf45983d3d65b10";
    public static final String CLOUD_API_URL = "https://api.soundcloud.com";
    public static final String SUBJECT_FEEDBACK = "Feedback";
    public static final String SUBJECT_REQUEST_SUPPORT = "Support Needed";
    public static final String FEEDBACK_EMAIL = "support@vaentertaiment.xyz";
    public static final String CONTACT_EMAIL = "contact@vaentertaiment.xyz";
    public static final String GENDER_MALE = "Male";
    public static final String GENDER_FEMALE = "Female";
    public static final String STATUS_DELIVERED = "delivered";
    public static final String STATUS_NOT_DELIVERED = "not sent";
    public static final String REALM_DB_NAME = "messages.realm";
    public static final String FIREBASE_STORAGE_BUCKET = "gs://inkfcm.appspot.com";
    public static final String GROUP_OPTIONS_LEAVE = "leaveGroup";
    public static final String SERVER_AUTH_KEY = "fsahga-s447489asg-gGgashagasgfag-JKGKAJGKAS-gasgasga678sag8as-GASGJASKLG";
    public static final String NO_IMAGE_NAME = "no_image.png";
    public static final String NO_IMAGE_URL = MAIN_URL + USER_IMAGES_FOLDER + NO_IMAGE_NAME;
    public static final String ANDROID_DRAWABLE_DIR = ANDROID_RESOURCE_DIR + "com.ink.va" + "/drawable/";
    public static final String FUNNY_USER_IMAGE = "funny_image.jpg";
    public static final int TEXT_VIEW_DEFAULT_COLOR = android.R.color.tab_indicator_text;
    public static final String TREND_CATEGORIES_TOKEN = "Fasfklasgjp_KIkGPKAGKA=ga558787as785as-as6539sa-6gas32-ga6s";
    public static final String USER_COINS_TOKEN = "Fsalkghasfiugehwt34t04334-2:2io5l25:f:::Faskl80-r03fas-=2q935saf5a223419ga65z23....265";
    public static final String DELETE_MESSAGE_REQUESTED = "deleteSingleMessageRequested";
    public static final String NOTIFICATION_TYPE_POST_MADE = "postMade";
    public static final int BUY_COINS_REQUEST_CODE = 196;
    public static final int REQUEST_CUSTOMIZE_MADE = 78;
    public static final int REQUEST_CODE_CHOSE_STICKER = 1;
    public static final String COINS_BOUGHT_KEY = "coins_bought";
    public static final String PASSWORD_REQUEST_TOKEN = "fasjflkgkaga7g8asggf-a0gf98asu-gangas-g389t-nke_wrlPg_h34890udglka";
    public static final String POLLFISH_TOKEN = "asklfhauigagq2-464erhtnsq-bsd0hwh73t3r-hfd4x0hrhsx-ertgeseghpr4450-afaui3290lk[asz";
    public static final String USER_LOGIN_TOKEN = "asjflkasf_gasjgkla=-gsangklasjklsag-sagnas3r532r3w52r523q-gsa43-t3t54a-";
    public static final String SERVER_NOTIFICATION_SHARED_KEY = "ServerNewsNotification-";

    /**
     * Type Constants
     */
    public static final String TYPE_MESSAGE_ATTACHMENT = "TYPE_MESSAGE_ATTACHMENT";
    public static final String BADGE_TYPE_VIEW = "view";
    public static final String POST_TYPE_LOCAL = "local";
    public static final String POST_TYPE_GLOBAL = "global";
    public static final String BADGE_TYPE_BUY = "buy";
    public static final String TYPE_NEW_OWNER = "newOwner";
    public static final String TYPE_ENTER_VIP = "enterVip";
    public static final String TYPE_BUY_VIP = "buyVip";
    public static final String REQUEST_RESPONSE_TYPE_GROUP = "group";
    public static final String REQUEST_RESPONSE_TYPE_FRIEND_REQUEST = "friendRequest";
    public static final String REQUEST_RESPONSE_TYPE_LOCATION_REQUEST = "locationRequest";
    public static final String NOTIFICATION_TYPE_MESSAGE = "message";
    public static final String NOTIFICAITON_TYPE_GLOBAL_CHAT_MESSAGE = "globalChatMessage";
    public static final String NOTIFICATION_TYPE_GROUP_REQUEST = "request";
    public static final String TYPE_MESSAGE_SENT = "messageSent";
    public static final String NOTIFICATION_TYPE_FRIEND_REQUEST = "NOTIFICATION_TYPE_FRIEND_REQUEST";
    public static final String NOTIFICATION_TYPE_CHAT_ROULETTE = "chatRoulette";
    public static final String NOTIFICATION_TYPE_REQUESTING_LOCATION = "requestingLocation";
    public static final String NOTIFICATION_TYPE_LOCATION_SESSION_ENDED = "locationSessionEnded";
    public static final String NOTIFICATION_TYPE_COMMENT_ADDED = "commentAdded";
    public static final String NOTIFICATION_TYPE_POST_LIKED = "postLiked";
    public static final String NOTIFICATION_TYPE_POSTED_IN_GROUP = "postedInGroup";
    public static final String NOTIFICATION_TYPE_LOCATION_REQUEST_DECLINED = "requestLocationDeclined";
    public static final String NOTIFICATION_TYPE_LOCATION_REQUEST_ACCEPTED = "requestLocationAccepted";
    public static final String NOTIFICATION_TYPE_FRIEND_REQUEST_ACCEPTED = "friendRequestAccepted";
    public static final String NOTIFICATION_TYPE_LOCATION_UPDATES = "locationUpdate";
    public static final String CUSTOMIZATION_TYPE_RESTORE = "restore";
    public static final String CUSTOMIZATION_TYPE_SAVE = "save";
    public static final String CUSTOMIZATION_TYPE_REMOVE = "remove";
    public static final String TYPE_ACCEPT_REQUEST = "acceptGroupRequest";
    public static final String TYPE_DENY_REQUEST = "denyGroupRequest";
    public static final String STATUS_WAITING_NOT_AVAILABLE = "waiting_not_available";
    public static final String STATUS_AVAILABLE = "available";
    public static final String STATUS_IN_CHAT = "inChat";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_INSERT = "insert";
    public static final String POST_TYPE_CREATE = "TYPE_INSERT";
    public static final String POST_TYPE_EDIT = "TYPE_EDIT";
    public static final String NOTIFICATION_TYPE_CALL = "type=call";
    public static final String COMMENT_TYPE_DELETE = "delete";
    public static final String COMMENT_TYPE_EDIT = "edit";
    public static final String GROUP_TYPE_ALL = "all";
    public static final String GROUP_TYPE_MINE = "mine";
    public static final String GROUP_TYPE_EDIT = "edit";
    public static final String GROUP_TYPE_DELETE = "delete";
    public static final String SOCIAL_TYPE_FACEBOOK = "facebook";
    public static final String SOCIAL_TYPE_LINKEDIN = "linkedIn";
    public static final String SOCIAL_TYPE_VK = "vkontakte";
    public static final String GROUP_MESSAGES_TYPE_EDIT = "edit";
    public static final String GROUP_MESSAGES_TYPE_DELETE = "delete";
    public static final String SOCIAL_TYPE_GOOGLE = "google";
    public static final String TREND_TYPE_ALL = "getAllTrends";
    public static final String VIP_GLOBAL_CHAT_TYPE_SEND = "SEND";
    public static final String VIP_GLOBAL_CHAT_TYPE_GET = "GET";
    public static final String VIP_GLOBAL_CHAT_TYPE_DELETE = "DELETE";
    public static final String WALL_TYPE_POST = "post";
    public static final String WALL_TYPE_GROUP_MESSAGE = "groupMessage";
    public static final String LOCATION_REQUEST_TYPE_INSERT = "insertRequest";
    public static final String LOCATION_REQUEST_TYPE_ACCEPT = "acceptRequest";
    public static final String LOCATION_REQUEST_TYPE_DECLINE = "declineRequest";
    public static final String LOCATION_REQUEST_TYPE_DELETE = "delete";

    /**
     * Bundle Constants
     */
    public static final String NOTIFICATION_MESSAGE_BUNDLE_KEY = "notificationMessage";
    public static final String NOTIFICATION_AUTO_REDIRECT_BUNDLE_KEY = "NOTIFICATION_AUTO_REDIRECT_BUNDLE_KEY";
    public static final String PACK_ID_BUNDLE_KEY = "pack_price";
    public static final String NOTIFICATION_POST_ID_KEY = "postId";
    public static final String PACK_BACKGROUND_BUNDLE_KEY = "pack_background";
    public static final String STICKER_URL_EXTRA_KEY = "stickerUrlExtra";
    public static final String FILE_TRANSFER_EXTRA_KEY = "fileTransferExtraKey";
    public static final String NOTIFICATION_BUNDLE_EXTRA_KEY = "notification_bundle_key";
    public static final String NOTIFICATION_RECEIVED_GROUP_BUNDLE = "notification_group_bundle";
    public static final String STICKER_IS_ANIMATED_EXTRA_KEY = "stickerIsAnimated";
    public static final String PACK_IMAGE_BUNDLE_KEY = "pack_image";
    public static final String SERVER_NOTIFICATION_CONTENT_BUNDLE_KEY = "SERVER_NOTIFICATION_CONTENT_BUNDLE_KEY";
    public static final String PACK_CONTENT_BUNDLE_KEY = "pack_content";
    public static final String STARTING_FOR_RESULT_BUNDLE_KEY = "startingForActivityResult";
    public static final String SHOW_SERVER_NEWS_START_UP_KEY = "start_up_news";
    public static final String KILL_APP_BUNDLE_KEY = "kill_app_key";
    public static final String WARNING_TEXT_BUNDLE_KEY = "warning_text_key";


    /**
     * Game Constants
     */
    public static final String GAME_BLACK_JACK = "blackJack";
    public static final String GAME_MAFIA = "mafia";

    /**
     * Socket Listener Events
     */
    public static final String EVENT_NEW_MESSAGE = "onNewMessage";
    public static final String EVENT_TYPING = "typing";
    public static final String EVENT_STOPPED_TYPING = "stoppedTyping";
    public static final String EVENT_MESSAGE_SENT = "messageSent";
    public static final String EVENT_MAFIA_GLOBAL_MESSAGE = "onMafiaGlobalMessage";
    public static final String EVENT_ON_USER_LEFT_MAFIA_ROOM = "onRoomLeft";
    public static final String EVENT_ON_USER_JOINED_MAFIA_ROOM = "onUserJoinedRoom";
    public static final String EVENT_NO_FILE_EXIST = "noFileExist";
    public static final String EVENT_ON_MAFIA_GAME_STARTED = "onMafiaGameStarted";
    public static final String EVENT_ON_GAME_CREATED = "onGameCreated";
    public static final String EVENT_ON_SOCKET_MESSAGE_RECEIVED = "socketMessageReceived";
    public static final String EVENT_ON_COMMENT_ADDED = "onCommentAdded";
    public static final String EVENT_ON_POST_LIKED = "onPostLiked";
    public static final String EVENT_ON_POST_MADE = "onPostMade";
    public static final String EVENT_ON_FRIEND_REQUESTED = "onFriendRequested";
    public static final String EVENT_ON_FILE_TRANSFER_SERVER_READY = "onFileTransferServerReady";
    public static final String EVENT_ON_FRIEND_REQUEST_ACCEPTED = "onFriendRequestAccepted";
    public static final String EVENT_ON_FRIEND_REQUEST_DECLINED = "onFriendRequestDeclined";
    public static final String EVENT_ON_REQUEST_GROUP_JOIN = "onRequestGroupJoin";
    public static final String EVENT_ON_NEW_GROUP_MESSAGE = "onNewGroupMessage";
    public static final String EVENT_ON_GLOBAL_MESSAGE = "onGlobalMessage";

    /**
     * Socket Emit Events
     */
    public static final String EVENT_ADD_USER = "addUser";
    public static final String EVENT_SEND_MESSAGE = "sendMessage";
    public static final String EVENT_FILE_TRANSFER_SERVER_READY = "fileTransferServerReady";
    public static final String EVENT_ON_NO_FILE_EXIST = "onNoFileExists";

    public static final String EVENT_MESSAGE_RECEIVED = "onClientMessageReceived";
    public static final String EVENT_COMMENT_RECEIVED = "onClientCommentReceived";
    public static final String EVENT_FRIEND_REQUEST_ACCEPT_RECEIVED = "onClientFriendRequestAcceptReceived";
    public static final String EVENT_FRIEND_REQUEST_DECLINE_RECEIVED = "onClientFriendRequestDeclineReceived";
    public static final String EVENT_POST_LIKE_RECEIVED = "onClientPostLikeReceived";
    public static final String EVENT_POST_MADE_RECEIVED = "onClientPostMadeReceived";
    public static final String EVENT_FRIEND_REQUEST_RECEIVED = "onClientFriendRequestReceived";
    public static final String EVENT_ON_FILE_TRANSFER_REQUEST = "onFileTransferRequest";
    public static final String EVENT_REQUEST_FILE_TRANSFER = "fileTransferRequest";
    public static final String EVENT_FILE_TRANSFER_CLIENT_READY = "fileTransferClientConnected";
    public static final String EVENT_TRANSFER_BYTES = "transferByte";
    public static final String EVENT_COMPLETE_TRANSFER = "completeTransfer";
    public static final String EVENT_ON_TRANSFER_COMPLETED = "onTransferCompleted";
    public static final String EVENT_ON_TRANSFER_BYTES = "onTransferByte";
    public static final String EVENT_REQUEST_GROUP_JOIN_RECEIVED = "requestGroupJoinReceived";
    public static final String EVENT_ON_SEND_GROUP_MESSAGE_RECEIVED = "onSendGroupMessageReceived";
    public static final String EVENT_ON_GLOBAL_MESSAGE_RECEIVED = "onClientGlobalMessageReceived";
    public static final String EVENT_ON_FILE_TRANSFER_CLIENT_READY = "onFileTransferClientConnected";

    public static final String EVENT_ONLINE_STATUS = "onlineStatus";
    public static final String EVENT_PING = "ping";
    public static final String EVENT_COMMENT_ADDED = "commentAdded";
    public static final String EVENT_POST_LIKED = "postLiked";
    public static final String EVENT_REQUEST_GROUP_JOIN = "requestGroupJoin";
    public static final String EVENT_POST_MADE = "postMade";
    public static final String EVENT_FRIEND_REQUESTED = "friendRequested";
    public static final String EVENT_ACCEPT_FRIEND_REQUEST = "acceptFriendRequest";
    public static final String EVENT_DECLINE_FRIEND_REQUEST = "declineFriendRequest";
    public static final String EVENT_CREATE_GAME = "creatingGame";
    public static final String EVENT_SEND_GROUP_MESSAGE = "sendGroupMessage";
    public static final String SIP_USERNAME_EXTENSION = "InkUserSip";
    public static final String SIP_GENERIC_PASSWORD = "5369615737425";
}

