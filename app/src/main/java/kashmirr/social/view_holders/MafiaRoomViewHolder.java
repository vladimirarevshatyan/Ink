package kashmirr.social.view_holders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kashmirr.social.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kashmirr.social.interfaces.MafiaItemClickListener;
import kashmirr.social.models.MafiaRoomsModel;
import kashmirr.social.models.ParticipantModel;
import kashmirr.social.utils.MafiaConstants;
import kashmirr.social.utils.SharedHelper;

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
    @BindView(R.id.playersCountTV)
    TextView playersCountTV;
    @BindView(R.id.gameStatus)
    TextView gameStatus;
    @BindView(R.id.mafiaRoomBottomSpacing)
    View mafiaRoomBottomSpacing;
    @BindView(R.id.mafiaRoomSingleVIewCard)
    CardView mafiaRoomSingleVIewCard;
    @BindView(R.id.mafiaRoomMoreIcon)
    ImageView mafiaRoomMoreIcon;

    private Context context;
    private SharedHelper sharedHelper;
    private MafiaRoomsModel mafiaRoomsModel;

    public MafiaRoomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void initData(Context context, MafiaRoomsModel mafiaRoomsModel,
                         @Nullable MafiaItemClickListener mafiaItemClickListener, int maxSize, int position) {
        this.mafiaRoomsModel = mafiaRoomsModel;
        if (sharedHelper == null) {
            sharedHelper = new SharedHelper(context);
        }
        this.context = context;
        this.mafiaItemClickListener = mafiaItemClickListener;
        roomNameTV.setText(mafiaRoomsModel.getRoomName());
        gameTypeTV.setText(mafiaRoomsModel.getGameType().equals(MafiaConstants.GAME_TYPE_CLASSIC) ?
                context.getString(R.string.classic) : context.getString(R.string.yakudza));
        String finalMorningDurationName = context.getString(R.string.minutesUnit);

        if (mafiaRoomsModel.getMorningDurationUnit().equals(MafiaConstants.UNIT_HOURS)) {
            finalMorningDurationName = context.getString(R.string.hoursUnit);
        } else if (mafiaRoomsModel.getMorningDurationUnit().equals(MafiaConstants.UNIT_DAYS)) {
            finalMorningDurationName = context.getString(R.string.daysUnit);
        }

        String finalNightDurationName = context.getString(R.string.minutesUnit);

        if (mafiaRoomsModel.getNightDurationUnit().equals(MafiaConstants.UNIT_HOURS)) {
            finalNightDurationName = context.getString(R.string.hoursUnit);
        } else if (mafiaRoomsModel.getNightDurationUnit().equals(MafiaConstants.UNIT_DAYS)) {
            finalNightDurationName = context.getString(R.string.daysUnit);
        }

        morningDurationTV.setText(mafiaRoomsModel.getMorningDuration() + " " + finalMorningDurationName);

        nightDurationTV.setText(mafiaRoomsModel.getNightDuration() + " " + finalNightDurationName);

        languageTV.setText(mafiaRoomsModel.getRoomLanguage().equals(context.getString(R.string.english)) ?
                context.getString(R.string.english) : context.getString(R.string.russian));
        String playersCount = String.valueOf(mafiaRoomsModel.getJoinedUsers().size());
        playersCountTV.setText(context.getString(R.string.mafiaPlayersText, playersCount, mafiaRoomsModel.getMaxPlayers()));

        if (!mafiaRoomsModel.isGameStarted()) {
            gameStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            gameStatus.setText(context.getString(R.string.gameNotStarted));
        } else if (mafiaRoomsModel.isGameStarted()) {
            gameStatus.setTextColor(ContextCompat.getColor(context, R.color.darkGreen));
            gameStatus.setText(context.getString(R.string.gameStarted));
        }

        if (mafiaRoomsModel.isGameEnded()) {
            gameStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
            gameStatus.setText(context.getString(R.string.gameEndedText, mafiaRoomsModel.getWhoWon().equals(MafiaConstants.ROLE_MAFIA) ?
                    context.getString(R.string.mafiaText) : context.getString(R.string.citizenText)
            ));
        }
        if (position > 2) {
            if (position == maxSize) {
                mafiaRoomBottomSpacing.setVisibility(View.VISIBLE);
            } else {
                mafiaRoomBottomSpacing.setVisibility(View.GONE);
            }
        } else {
            mafiaRoomBottomSpacing.setVisibility(View.GONE);
        }
        if (isParticipant()) {
            initParticipantView();
        } else {
            initViewerView();
        }
    }

    @OnClick(R.id.mafiaRoomMoreIcon)
    public void onMoreIconClicked(View view) {
        PopupMenu popupMenu = new PopupMenu(context, view);

        if (sharedHelper.getUserId().equals(mafiaRoomsModel.getCreatorId())) {
            popupMenu.getMenu().add(0, 0, 0, context.getString(R.string.delete));
        }

        if (isParticipant()) {
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

    private boolean isParticipant() {
        boolean isParticipant = false;
        for (ParticipantModel eachUserId : mafiaRoomsModel.getJoinedUsers()) {
            if (sharedHelper.getUserId().equals(eachUserId.getUser().getUserId())) {
                isParticipant = true;
                break;
            }
        }
        return isParticipant;
    }

    @OnClick(R.id.mafiaRoomRoot)
    public void rootClicked() {
        if (mafiaItemClickListener != null) {
            mafiaItemClickListener.onItemClicked(mafiaRoomsModel);
        }
    }

    @OnClick(R.id.mafiaRoomSingleVIewCard)
    public void cardClicked() {
        rootClicked();
    }

    private void initParticipantView() {
        mafiaRoomSingleVIewCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.activeBlack));
        roomNameTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        gameTypeTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        morningDurationTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        nightDurationTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        playersCountTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        languageTV.setTextColor(ContextCompat.getColor(context, R.color.vk_white));
        mafiaRoomMoreIcon.setImageResource(R.drawable.more_icon_white);
    }

    private void initViewerView() {
        mafiaRoomSingleVIewCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.vk_white));
        roomNameTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        gameTypeTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        morningDurationTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        nightDurationTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        playersCountTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        languageTV.setTextColor(ContextCompat.getColor(context, R.color.vk_black));
        mafiaRoomMoreIcon.setImageResource(R.drawable.more_icon);
    }

}
