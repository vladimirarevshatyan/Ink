package kashmirr.social.view_holders;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kashmirr.social.R;

import kashmirr.social.interfaces.RecyclerItemClickListener;
import kashmirr.social.models.GroupMessagesModel;
import kashmirr.social.utils.Constants;
import kashmirr.social.utils.ImageLoader;
import kashmirr.social.utils.SharedHelper;

/**
 * Created by PC-Comp on 12/20/2016.
 */

public class GroupMessagesViewHolder extends RecyclerView.ViewHolder {

    private Context context;
    public TextView messageSenderName, groupMessageBody;
    public ImageView messageSenderImage;
    public ImageView groupImageView;
    private ImageView groupMessageMoreIcon;
    private RelativeLayout groupMessageCard;
    private SharedHelper sharedHelper;
    private View rootLayout;

    public GroupMessagesViewHolder(View view) {
        super(view);
        messageSenderName = (TextView) view.findViewById(R.id.messageSenderName);
        rootLayout = view.findViewById(R.id.rootLayout);
        groupMessageBody = (TextView) view.findViewById(R.id.groupMessageBody);
        groupMessageCard = (RelativeLayout) view.findViewById(R.id.groupMessageCard);
        messageSenderImage = (ImageView) view.findViewById(R.id.messageSenderImage);
        groupImageView = (ImageView) view.findViewById(R.id.group_image);
        groupMessageMoreIcon = (ImageView) view.findViewById(R.id.groupMessageMoreIcon);
    }


    public void initData(final Context context, final GroupMessagesModel groupMessagesModel,
                         final RecyclerItemClickListener onClickListener, final int position) {
        this.context = context;
        sharedHelper = new SharedHelper(context);
        messageSenderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClicked(position);
                }
            }
        });
        if (groupMessagesModel.getFileName().isEmpty()) {
            groupImageView.setVisibility(View.GONE);
        } else {
            groupImageView.setVisibility(View.VISIBLE);
            String encodedImage = Uri.encode(groupMessagesModel.getFileName());
            if (encodedImage.isEmpty()) {
                groupImageView.setVisibility(View.GONE);
            } else {
                ImageLoader.loadImage(context, false, false, Constants.MAIN_URL + Constants.UPLOADED_FILES_DIR + encodedImage,
                        0, R.drawable.big_image_place_holder, groupImageView, null);
            }

        }
        if (!groupMessagesModel.getSenderImage().isEmpty()) {
            final String encodedImage = Uri.encode(groupMessagesModel.getSenderImage());

            ImageLoader.loadImage(context, true, false, Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + encodedImage,
                    0, R.drawable.user_image_placeholder, messageSenderImage, new ImageLoader.ImageLoadedCallback() {
                        @Override
                        public void onImageLoaded(Object result, Exception e) {
                            if (e != null) {
                                ImageLoader.loadImage(context, true, false, groupMessagesModel.getSenderImage(),
                                        0, R.drawable.user_image_placeholder, messageSenderImage, null);
                            }
                        }
                    });
        } else {
            ImageLoader.loadImage(context, true, true, null,
                    R.drawable.no_image, R.drawable.user_image_placeholder, messageSenderImage, null);
        }

        groupMessageBody.setMovementMethod(LinkMovementMethod.getInstance());
        if (groupMessagesModel.getGroupMessage().isEmpty()) {
            groupMessageBody.setVisibility(View.GONE);
        } else {
            groupMessageBody.setVisibility(View.VISIBLE);
        }
        groupMessageBody.setText(groupMessagesModel.getGroupMessage());
        messageSenderName.setText(groupMessagesModel.getSenderName());
        groupMessageMoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onAdditionalItemClick(position, groupMessageMoreIcon);
                }
            }
        });
        if (sharedHelper.getUserId().equals(groupMessagesModel.getSenderId())) {
            groupMessageMoreIcon.setVisibility(View.VISIBLE);
        } else {
            groupMessageMoreIcon.setVisibility(View.GONE);
        }
        groupMessageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onClickListener != null) {
                    onClickListener.onItemClicked(position, messageSenderImage);
                }
            }
        });
    }


    public View getViewToAnimate() {
        return rootLayout;
    }
}
