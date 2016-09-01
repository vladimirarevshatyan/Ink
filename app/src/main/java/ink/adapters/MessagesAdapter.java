package ink.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ink.R;

import java.util.List;

import ink.models.UserMessagesModel;
import ink.utils.CircleTransform;
import ink.utils.Constants;
import ink.utils.SharedHelper;

/**
 * Created by USER on 2016-07-02.
 */
public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private List<UserMessagesModel> userMessagesModels;
    private Context mContext;
    private SharedHelper mSharedHelper;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messagesUserName, messageBody, messageDate;
        public ImageView messagesImage;

        public ViewHolder(View view) {
            super(view);
            messagesUserName = (TextView) view.findViewById(R.id.messagesUserName);
            messageDate = (TextView) view.findViewById(R.id.messageDate);
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

        holder.messageBody.setText(userMessagesModel.getMessage().replaceAll("userid=" + mSharedHelper.getUserId() + ":" + Constants.TYPE_MESSAGE_ATTACHMENT, ""));
        holder.messageDate.setText(userMessagesModel.getDate());
        if (!userMessagesModel.getImageName().isEmpty()) {
            String url = Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER + userMessagesModel.getImageLink();
            if (userMessagesModel.isSocialAccount()) {
                url = userMessagesModel.getImageLink();
            }
            Glide.with(mContext).load(url)
                    .placeholder(R.drawable.no_background_image)
                    .transform(new CircleTransform(mContext)).into(holder.messagesImage);
        } else {
            Glide.with(mContext).load(Constants.ANDROID_DRAWABLE_DIR + "no_image")
                    .transform(new CircleTransform(mContext)).into(holder.messagesImage);
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesModels.size();
    }
}
