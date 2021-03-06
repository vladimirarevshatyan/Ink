package kashmirr.social.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kashmirr.social.R;
import com.mikhaellopez.hfrecyclerview.HFRecyclerView;

import java.util.List;

import kashmirr.social.activities.MyProfile;
import kashmirr.social.activities.OpponentProfile;
import kashmirr.social.interfaces.CommentClickHandler;
import kashmirr.social.interfaces.RecyclerItemClickListener;
import kashmirr.social.models.CommentModel;
import kashmirr.social.models.UserModel;
import kashmirr.social.utils.ClipManager;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.FileUtils;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.Retrofit;
import kashmirr.social.utils.SharedHelper;
import lombok.Setter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by USER on 2016-07-05.
 */
public class CommentAdapter extends HFRecyclerView<CommentModel> {

    private List<CommentModel> commentModels;
    private Context context;
    private String ownerImage;
    private String ownerPostBody;
    private String attachment;
    private String location;
    private String date;
    private String name;
    @Setter
    private String likesCount;
    private CommentClickHandler commentClickHandler;
    private boolean isLiked;
    private SharedHelper sharedHelper;
    private boolean isOwnerSocialAccount;
    String ownerId;
    private RecyclerItemClickListener onItemClickListener;
    private boolean showingNoComments = false;
    @Setter
    private String postId;

    public CommentAdapter(String ownerId, List<CommentModel> data,
                          Context context, String ownerImage,
                          String ownerPostBody, String attachment,
                          String location, String date, String name,
                          String likesCount, boolean isLiked, boolean isOwnerSocialAccount) {
        super(data, true, false);
        this.context = context;
        this.isLiked = isLiked;
        this.ownerId = ownerId;
        this.likesCount = likesCount;
        this.name = name;
        this.date = date;
        this.attachment = attachment;
        this.location = location;
        this.isOwnerSocialAccount = isOwnerSocialAccount;
        this.ownerImage = ownerImage;
        this.ownerPostBody = ownerPostBody;
        commentModels = data;
        sharedHelper = new SharedHelper(context);
    }

    @Override
    protected RecyclerView.ViewHolder getItemView(LayoutInflater inflater, ViewGroup parent) {
        return new ItemViewHolder(inflater.inflate(R.layout.comment_single_view, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getHeaderView(LayoutInflater inflater, ViewGroup parent) {
        return new HeaderViewHolder(inflater.inflate(R.layout.comment_header_view, parent, false));
    }

    @Override
    protected RecyclerView.ViewHolder getFooterView(LayoutInflater inflater, ViewGroup parent) {
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            handleComments(position, holder);
        } else if (holder instanceof HeaderViewHolder) {
            handleHeaderView(position, holder);
        }
    }

    private void handleHeaderView(final int position, final RecyclerView.ViewHolder holder) {
        final HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.getLiker(postId);

        headerViewHolder.commentMoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentClickHandler != null) {
                    commentClickHandler.onMoreClick(position, ((HeaderViewHolder) holder).commentMoreIcon);
                }
            }
        });
        if (ownerImage != null && !ownerImage.isEmpty()) {
            if (isOwnerSocialAccount) {

                ImageLoader.loadImage(context, true, false, ownerImage,
                        0, R.drawable.user_image_placeholder, ((HeaderViewHolder) holder).postOwnerImage, null);
            } else {
                String encodedImage = Uri.encode(ownerImage);
                ImageLoader.loadImage(context, true, false, Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + encodedImage,
                        0, R.drawable.user_image_placeholder, ((HeaderViewHolder) holder).postOwnerImage, null);
            }
        } else {

            ImageLoader.loadImage(context, true, true, null,
                    R.drawable.no_image, R.drawable.user_image_placeholder, ((HeaderViewHolder) holder).postOwnerImage, null);

        }
        headerViewHolder.postBody.setMovementMethod(LinkMovementMethod.getInstance());
        headerViewHolder.postBody.setText(context.getString(R.string.quoteOpen) + ownerPostBody + context.getString(R.string.quoteClose));

        headerViewHolder.postDate.setText(date);


        if (attachment != null && !attachment.isEmpty()) {
            headerViewHolder.commentAttachmentLayout.setVisibility(View.VISIBLE);
            String fileName = attachment;
            int index = fileName.indexOf(":");
            headerViewHolder.commentAttachmentName.setText(fileName.substring(index + 1, fileName.length()));

            if (FileUtils.isImageType(attachment)) {
                headerViewHolder.imageHolder.setVisibility(View.VISIBLE);
                String encodedImage = Uri.encode(attachment);
                ((HeaderViewHolder) holder).commentAttachmentLayout.setVisibility(View.GONE);

                ImageLoader.loadImage(context, false, false, Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + encodedImage,
                        0, R.drawable.big_image_place_holder, headerViewHolder.imageHolder, null);
            } else {
                ((HeaderViewHolder) holder).commentAttachmentLayout.setVisibility(View.VISIBLE);
                headerViewHolder.imageHolder.setVisibility(View.GONE);
            }

        } else {
            headerViewHolder.imageHolder.setVisibility(View.GONE);
            headerViewHolder.commentAttachmentLayout.setVisibility(View.GONE);
        }

        headerViewHolder.imageHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentClickHandler != null) {
                    commentClickHandler.onImageClicked(position);
                }
            }
        });
        if (location != null && !location.isEmpty()) {
            headerViewHolder.commentAddressLayout.setVisibility(View.VISIBLE);
            headerViewHolder.commentAddress.setText(location);
        } else {
            headerViewHolder.commentAddressLayout.setVisibility(View.GONE);
        }

        if (!likesCount.equals("0")) {
            headerViewHolder.likesCountTV.setVisibility(View.VISIBLE);
            if (Integer.parseInt(likesCount) > 1) {
                headerViewHolder.likesCountTV.setText(likesCount + " " + context.getString(R.string.likesText));
            } else {
                headerViewHolder.likesCountTV.setText(likesCount + " " + context.getString(R.string.singleLikeText));
            }
        } else {
            headerViewHolder.likesCountTV.setVisibility(View.GONE);
        }

        if (isLiked) {
            headerViewHolder.likeIcon.setBackgroundResource(R.drawable.like_active);
        } else {
            headerViewHolder.likeIcon.setBackgroundResource(R.drawable.like_inactive);
        }

        headerViewHolder.commenterName.setText(name);

        headerViewHolder.commentLikeWrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentClickHandler != null) {
                    int actualPosition = position - 1;
                    commentClickHandler.onLikeClicked(actualPosition, headerViewHolder.likesCountTV,
                            headerViewHolder.likeIcon, headerViewHolder.commentLikeWrapper);
                }
            }
        });


        headerViewHolder.commentAttachmentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentClickHandler != null) {
                    int actualPosition = position - 1;
                    commentClickHandler.onAttachmentClick(actualPosition);
                }
            }
        });
        headerViewHolder.commentAddressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (commentClickHandler != null) {
                    int actualPosition = position - 1;
                    commentClickHandler.onAddressClick(actualPosition);
                }
            }
        });
        if (showingNoComments) {
            headerViewHolder.noCommentWrapper.setVisibility(View.VISIBLE);
        } else {
            headerViewHolder.noCommentWrapper.setVisibility(View.GONE);
        }
    }

    private void handleComments(final int position, RecyclerView.ViewHolder holder) {
        final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        final CommentModel commentModel = getItem(position);
        checkForSticker(commentModel, itemViewHolder);

        itemViewHolder.commenterBody.setText(commentModel.getCommentBody());
        itemViewHolder.commenterBody.setMovementMethod(LinkMovementMethod.getInstance());
        itemViewHolder.commenterName.setText(commentModel.getFirstName() + " " + commentModel.getLastName());
        itemViewHolder.commentRootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(position, null);
                }
            }
        });
        itemViewHolder.commentRootLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemLongClick(position);
                }
                return true;
            }
        });

        if (sharedHelper.getUserId().equals(commentModel.getCommenterId())) {
            itemViewHolder.commentMoreIcon.setImageResource(R.drawable.more_icon);
            sharedHelper.putCommentedPost(commentModel.getPostId());
        } else {
            itemViewHolder.commentMoreIcon.setImageResource(R.drawable.copy_icon);
        }
        if (commentModel.getCommenterImage() != null && !commentModel.getCommenterImage().isEmpty()) {
            if (commentModel.isSocialAccount()) {
                ImageLoader.loadImage(context, true, false, commentModel.getCommenterImage(),
                        0, R.drawable.user_image_placeholder, itemViewHolder.commenterImage, null);
            } else {
                String encodedImage = Uri.encode(commentModel.getCommenterImage());

                ImageLoader.loadImage(context, true, false, Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + encodedImage,
                        0, R.drawable.user_image_placeholder, itemViewHolder.commenterImage, null);
            }
        } else {
            ImageLoader.loadImage(context, true, true, null,
                    R.drawable.no_image, R.drawable.user_image_placeholder, itemViewHolder.commenterImage, null);
        }

        if (sharedHelper.getUserId().equals(commentModel.getCommenterId())) {
            itemViewHolder.commentMoreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onAdditionalItemClick(position, itemViewHolder.commentMoreIcon);
                    }
                }
            });
        } else {
            itemViewHolder.commentMoreIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipManager.copy(context, commentModel.getCommentBody());
                }
            });
        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder implements RecyclerItemClickListener {
        private ImageView postOwnerImage, likeIcon, imageHolder;
        private TextView postBody, postDate, commenterName, commentAttachmentName, commentAddress, likesCountTV;
        private RelativeLayout commentLikeWrapper, commentAddressLayout, commentAttachmentLayout;
        private ImageView commentMoreIcon;
        private RelativeLayout noCommentWrapper;
        private RecyclerView likerHorizontalRecycler;
        private ProgressBar likerProgress;
        private LikerAdapter likerAdapter;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            postOwnerImage = (ImageView) itemView.findViewById(R.id.postOwnerImage);
            likeIcon = (ImageView) itemView.findViewById(R.id.commentLikeIcon);
            imageHolder = (ImageView) itemView.findViewById(R.id.imageHolder);
            postBody = (TextView) itemView.findViewById(R.id.postBody);
            commentAttachmentName = (TextView) itemView.findViewById(R.id.commentAttachmentName);
            commentMoreIcon = (ImageView) itemView.findViewById(R.id.commentMoreIcon);
            postDate = (TextView) itemView.findViewById(R.id.postDate);
            commentAddress = (TextView) itemView.findViewById(R.id.commentAddress);
            commenterName = (TextView) itemView.findViewById(R.id.commenterName);
            likesCountTV = (TextView) itemView.findViewById(R.id.commentLikesCount);
            commentLikeWrapper = (RelativeLayout) itemView.findViewById(R.id.commentLikeWrapper);
            commentAddressLayout = (RelativeLayout) itemView.findViewById(R.id.commentAddresslayout);
            noCommentWrapper = (RelativeLayout) itemView.findViewById(R.id.noCommentWrapper);
            commentAttachmentLayout = (RelativeLayout) itemView.findViewById(R.id.commentAttachmentLayout);
            likerProgress = (ProgressBar) itemView.findViewById(R.id.likerProgress);
            likerAdapter = new LikerAdapter();
            likerAdapter.setOnItemClickListener(this);
            likerHorizontalRecycler = (RecyclerView) itemView.findViewById(R.id.likerHorizontalRecycler);
            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            likerHorizontalRecycler.setLayoutManager(horizontalLayoutManager);
            likerHorizontalRecycler.setAdapter(likerAdapter);
        }

        private void getLiker(String postId) {
            Retrofit.getInstance().getInkService().getLikedUsers(postId).enqueue(new Callback<List<UserModel>>() {
                @Override
                public void onResponse(Call<List<UserModel>> call, Response<List<UserModel>> response) {
                    if (likerProgress.getVisibility() == View.VISIBLE) {
                        likerProgress.setVisibility(View.GONE);
                    }

                    if (response.body().isEmpty()) {
                        likerHorizontalRecycler.setVisibility(View.GONE);
                    } else {
                        likerHorizontalRecycler.setVisibility(View.VISIBLE);
                        likerAdapter.setUserModels(response.body());
                    }
                }

                @Override
                public void onFailure(Call<List<UserModel>> call, Throwable t) {
                    likerProgress.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public void onItemClicked(int position, View view) {

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
            UserModel userModel = (UserModel) object;
            if (userModel.getUserId().equals(sharedHelper.getUserId())) {
                context.startActivity(new Intent(context, MyProfile.class));
            } else {
                Intent intent = new Intent(context, OpponentProfile.class);
                intent.putExtra("id", userModel.getUserId());
                intent.putExtra("firstName", userModel.getFirstName());
                intent.putExtra("lastName", userModel.getLastName());
                intent.putExtra("isFriend", true);
                context.startActivity(intent);
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView commenterBody;
        private ImageView commenterImage;
        private ImageView commentMoreIcon;
        private TextView commenterName;
        private RelativeLayout commentRootLayout;
        private ImageView imageView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.stickerView);

            commentMoreIcon = (ImageView) itemView.findViewById(R.id.commentMoreIcon);

            commenterBody = (TextView) itemView.findViewById(R.id.commenterBody);
            commenterName = (TextView) itemView.findViewById(R.id.commenterName);
            commenterImage = (ImageView) itemView.findViewById(R.id.commenterImage);
            commentRootLayout = (RelativeLayout) itemView.findViewById(R.id.commentRootLayout);
        }


    }

    public void setOnLikeClickListener(CommentClickHandler onLikeClickListener) {
        this.commentClickHandler = onLikeClickListener;
    }

    public void setOnItemClickListener(RecyclerItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setShowingNoComments(boolean showingNoComments) {
        this.showingNoComments = showingNoComments;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    private void checkForSticker(final CommentModel commentModel, final ItemViewHolder holder) {
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentClickHandler.onStickerClicked(commentModel);
            }
        });
        if (commentModel.hasSticker()) {
            holder.imageView.setImageResource(0);
            holder.imageView.setVisibility(View.VISIBLE);

            ImageLoader.loadImage(context, false, false, Constants.MAIN_URL + commentModel.getStickerUrl(),
                    0, R.drawable.time_loading_vector, holder.imageView, null);
        } else {
            holder.imageView.setImageResource(0);
            holder.imageView.setVisibility(View.GONE);
        }
    }
}
