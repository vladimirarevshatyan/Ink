package ink.va.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ink.va.R;
import com.koushikdutta.ion.Ion;

import java.util.List;

import ink.va.models.UserMessagesModel;
import ink.va.utils.CircleTransform;
import ink.va.utils.Constants;
import ink.va.utils.SharedHelper;

/**
 * Created by USER on 2016-07-02.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<UserMessagesModel> userMessagesModels;
    private Context mContext;
    private SharedHelper mSharedHelper;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messagesUserName, messageBody;
        public ImageView messagesImage;

        public ViewHolder(View view) {
            super(view);
            messagesUserName = (TextView) view.findViewById(R.id.messagesUserName);
            messageBody = (TextView) view.findViewById(R.id.messageBody);
            messagesImage = (ImageView) view.findViewById(R.id.messagesImage);
        }
    }


    public MessagesAdapter(List<UserMessagesModel> friendsModelList, Context context) {
        mContext = context;
        this.userMessagesModels = friendsModelList;
        mSharedHelper = new SharedHelper(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_messages_single_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UserMessagesModel userMessagesModel = userMessagesModels.get(position);
        holder.messagesUserName.setText(userMessagesModel.getFirstName() + " " + userMessagesModel.getLastName());

        String message = userMessagesModel.getMessage();
        if (userMessagesModel.getMessage().contains(":")) {
            int index = userMessagesModel.getMessage().indexOf(":");
            message = userMessagesModel.getMessage().substring(index + 1, userMessagesModel.getMessage().length());
        }


        holder.messageBody.setText(message);
        if (!userMessagesModel.getImageName().isEmpty()) {
            String encodedImage = Uri.encode(userMessagesModel.getImageLink());

            String url = Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + encodedImage;
            if (userMessagesModel.isSocialAccount()) {
                url = userMessagesModel.getImageLink();
            }
            Ion.with(mContext).load(url)
                    .withBitmap().placeholder(R.drawable.no_background_image).transform(new CircleTransform()).intoImageView(holder.messagesImage);
        } else {
            Ion.with(mContext).load(Constants.ANDROID_DRAWABLE_DIR + "no_image").withBitmap()
                    .transform(new CircleTransform()).intoImageView(holder.messagesImage);
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesModels.size();
    }
}
