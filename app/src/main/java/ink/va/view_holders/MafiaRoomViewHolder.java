package ink.va.view_holders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ink.va.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ink.va.interfaces.MafiaItemClickListener;
import ink.va.models.MafiaRoomsModel;
import ink.va.utils.SharedHelper;

/**
 * Created by PC-Comp on 3/1/2017.
 */

public class MafiaRoomViewHolder extends RecyclerView.ViewHolder {
    private MafiaItemClickListener mafiaItemClickListener;
    @BindView(R.id.roomNameTV)
    TextView roomNameTV;
    @BindView(R.id.gameTypeTV)
    TextView gameTypeTV;
    @BindView(R.id.morningDurationTV)
    TextView morningDurationTV;
    @BindView(R.id.nightDurationTV)
    TextView nightDurationTV;
    @BindView(R.id.languageTV)
    TextView languageTV;
    @BindView(R.id.playersHeader)
    TextView playersHeader;
    @BindView(R.id.gameStatus)
    TextView gameStatus;

    private Context context;
    private SharedHelper sharedHelper;
    private MafiaRoomsModel mafiaRoomsModel;

    public MafiaRoomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initData(Context context, MafiaRoomsModel mafiaRoomsModel, @Nullable MafiaItemClickListener mafiaItemClickListener) {
        this.mafiaRoomsModel = mafiaRoomsModel;
        if (sharedHelper == null) {
            sharedHelper = new SharedHelper(context);
        }
        this.context = context;
        this.mafiaItemClickListener = mafiaItemClickListener;
        roomNameTV.setText(mafiaRoomsModel.getRoomName());
        gameTypeTV.setText(mafiaRoomsModel.getGameType().equals(context.getString(R.string.classic)) ?
                context.getString(R.string.classic) : context.getString(R.string.yakudza));
        String finalMorningDurationName = context.getString(R.string.minutesUnit);

        if (mafiaRoomsModel.getMorningDurationUnit().equals(context.getString(R.string.hoursUnit))) {
            finalMorningDurationName = context.getString(R.string.hoursUnit);
        } else if (mafiaRoomsModel.getMorningDurationUnit().equals(context.getString(R.string.daysUnit))) {
            finalMorningDurationName = context.getString(R.string.daysUnit);
        }

        String finalNightDurationName = context.getString(R.string.minutesUnit);

        if (mafiaRoomsModel.getNightDurationUnit().equals(context.getString(R.string.hoursUnit))) {
            finalNightDurationName = context.getString(R.string.hoursUnit);
        } else if (mafiaRoomsModel.getNightDurationUnit().equals(context.getString(R.string.daysUnit))) {
            finalNightDurationName = context.getString(R.string.daysUnit);
        }

        morningDurationTV.setText(mafiaRoomsModel.getMorningDuration() + " " + finalMorningDurationName);

        nightDurationTV.setText(mafiaRoomsModel.getNightDuration() + " " + finalNightDurationName);

        languageTV.setText(mafiaRoomsModel.getRoomLanguage().equals(context.getString(R.string.english)) ?
                context.getString(R.string.english) : context.getString(R.string.russian));
        String playersCount = String.valueOf(mafiaRoomsModel.getJoinedUserIds().size());
        playersHeader.setText(context.getString(R.string.mafiaPlayersText, playersCount));
        if (!mafiaRoomsModel.isGameStarted()) {
            gameStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            gameStatus.setText(context.getString(R.string.gameNotStarted));
        } else {
            gameStatus.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            gameStatus.setText(context.getString(R.string.gameStarted));
        }
    }

    @OnClick(R.id.mafiaRoomMoreIcon)
    public void onMoreIconClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        if (sharedHelper.getUserId().equals(mafiaRoomsModel.getCreatorId())) {
            popupMenu.getMenu().add(0, 0, 0, context.getString(R.string.delete));
        }
        boolean isParticipant = false;
        for (String eachUserId : mafiaRoomsModel.getJoinedUserIds()) {
            if (sharedHelper.getUserId().equals(eachUserId)) {
                isParticipant = true;
                break;
            }
        }

        if (isParticipant) {
            popupMenu.getMenu().add(0, 1, 1, context.getString(R.string.leave));
        } else {
            popupMenu.getMenu().add(0, 1, 1, context.getString(R.string.join));
        }
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().toString().equals(context.getString(R.string.leave))) {
                    if (mafiaItemClickListener != null) {
                        mafiaItemClickListener.onLeaveClicked(mafiaRoomsModel);
                    }
                } else if (item.getTitle().toString().equals(context.getString(R.string.join))) {
                    if (mafiaItemClickListener != null) {
                        mafiaItemClickListener.onJoinClicked(mafiaRoomsModel);
                    }
                } else if (item.getTitle().toString().equals(context.getString(R.string.delete))) {
                    if (mafiaItemClickListener != null) {
                        mafiaItemClickListener.onDeleteClicked(mafiaRoomsModel);
                    }
                }
                return false;
            }
        });
        popupMenu.show();
    }

    @OnClick(R.id.mafiaRoomRoot)
    public void rootClicked() {
        if (mafiaItemClickListener != null) {
            mafiaItemClickListener.onItemClicked(mafiaRoomsModel);
        }
    }
}
