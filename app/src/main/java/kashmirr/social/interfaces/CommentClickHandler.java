package kashmirr.social.interfaces;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import kashmirr.social.models.CommentModel;

/**
 * Created by USER on 2016-07-06.
 */
public interface CommentClickHandler {
    void onLikeClicked(int position, TextView likesCountTV, ImageView likeView, View likeWrapper);

    void onAddressClick(int position);

    void onAttachmentClick(int position);

    void onMoreClick(int position, View view);

    void onImageClicked(int position);

    void onStickerClicked(CommentModel commentModel);
}
