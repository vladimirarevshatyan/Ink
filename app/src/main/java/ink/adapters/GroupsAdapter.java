package ink.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.ink.R;

import java.util.List;

import ink.models.GroupsModel;
import ink.utils.CircleTransform;
import ink.utils.Constants;

/**
 * Created by USER on 2016-07-06.
 */

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    private List<GroupsModel> groupsModelList;
    private Context mContext;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView groupName, followersCount;
        public ImageView groupImage, ownerImage;
        public CardView groupBackground;
        private ProgressBar singleItemLoading;

        public ViewHolder(View view) {
            super(view);
            groupName = (TextView) view.findViewById(R.id.groupName);
            followersCount = (TextView) view.findViewById(R.id.followersCount);
            groupImage = (ImageView) view.findViewById(R.id.groupImage);
            ownerImage = (ImageView) view.findViewById(R.id.ownerImage);
            groupBackground = (CardView) view.findViewById(R.id.groupBackground);
            singleItemLoading = (ProgressBar) view.findViewById(R.id.singleItemLoading);
        }
    }


    public GroupsAdapter(List<GroupsModel> friendsModelList, Context context) {
        mContext = context;
        this.groupsModelList = friendsModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_group_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GroupsModel groupsModel = groupsModelList.get(position);
        String hexColor = groupsModel.getGroupColor();
        if (hexColor.isEmpty()) {
            hexColor = "#404040";
        } else {
            hexColor = groupsModel.getGroupColor();
        }
        if (isWhite(hexColor)) {
            holder.followersCount.setTextColor(Color.parseColor("#000000"));
            holder.groupName.setTextColor(Color.parseColor("#000000"));
        } else {
            holder.followersCount.setTextColor(Color.parseColor("#ffffff"));
            holder.groupName.setTextColor(Color.parseColor("#ffffff"));
        }
        if (!groupsModel.getGroupImage().isEmpty()) {
            holder.groupImage.setScaleType(ImageView.ScaleType.FIT_XY);
            Glide.with(mContext).load(Constants.MAIN_URL + Constants.GROUP_IMAGES_FOLDER +
                    groupsModel.getGroupImage()).fitCenter().centerCrop().listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    holder.singleItemLoading.setVisibility(View.GONE);
                    return false;
                }
            }).into(holder.groupImage);
        } else {
            holder.groupImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            holder.groupImage.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_group_image));
            holder.singleItemLoading.setVisibility(View.GONE);
        }
        if (groupsModel.getOwnerImage() != null && !groupsModel.getOwnerImage().isEmpty()) {
            if (groupsModel.isSocialAccount()) {
                Glide.with(mContext).load(groupsModel.getOwnerImage()).transform(new CircleTransform(mContext)).into(holder.ownerImage);
            } else {
                Glide.with(mContext).load(Constants.MAIN_URL + Constants.USER_IMAGES_FOLDER +
                        groupsModel.getOwnerImage()).transform(new CircleTransform(mContext)).into(holder.ownerImage);
            }
        } else {
            Glide.with(mContext).load(Constants.ANDROID_DRAWABLE_DIR + "no_image").transform(new CircleTransform(mContext)).into(holder.ownerImage);
        }
        holder.groupBackground.setCardBackgroundColor(Color.parseColor(hexColor));
        holder.groupName.setText(groupsModel.getGroupName());
        holder.followersCount.setText(groupsModel.getParticipantsCount() + " " + mContext.getString(R.string.participantText));
    }


    @Override
    public int getItemCount() {
        return groupsModelList.size();
    }


    private boolean isWhite(String hexColor) {
        int color = Color.parseColor(hexColor);
        if (color == Color.WHITE) {
            return true;
        } else {
            return false;
        }
    }
}

