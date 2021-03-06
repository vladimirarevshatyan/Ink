package kashmirr.social.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kashmirr.social.R;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import kashmirr.social.interfaces.RecyclerItemClickListener;
import kashmirr.social.models.ParticipantModel;
import kashmirr.social.view_holders.MafiaParticipantViewHolder;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by PC-Comp on 3/13/2017.
 */

public class MafiaPlayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    @Getter
    private List<ParticipantModel> users;
    private Context context;
    @Setter
    private String ownerId;
    @Setter
    RecyclerItemClickListener onItemClickListener;

    public MafiaPlayersAdapter() {
        users = new LinkedList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.mafia_participant_single_item, parent, false);
        return new MafiaParticipantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MafiaParticipantViewHolder) holder).initData(context, users.get(position), ownerId, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setUsers(List<ParticipantModel> users) {
        for (ParticipantModel participantModel : users) {
            participantModel.setRoomCreatorId(ownerId);
        }
        this.users.clear();
        Collections.sort(users);
        this.users.addAll(users);
        notifyDataSetChanged();
    }

    public void addUser(ParticipantModel participantModel) {
        participantModel.setRoomCreatorId(ownerId);
        users.add(participantModel);
        int index = users.indexOf(participantModel);
        notifyItemInserted(index);
    }

    public void removeUser(ParticipantModel participantModel) {
        participantModel.setRoomCreatorId(ownerId);
        users.remove(participantModel);
        notifyDataSetChanged();
    }

    public void removeVictimMarks() {
        for (ParticipantModel participantModel : users) {
            participantModel.setVictim(false);
        }
        notifyDataSetChanged();
    }

    public void markPlayer(ParticipantModel victim) {
        for (ParticipantModel participantModel : users) {
            if (participantModel.getUser().getUserId().equals(victim.getUser().getUserId())) {
                participantModel.setVictim(true);
                break;
            }
        }
        notifyDataSetChanged();
    }
}
