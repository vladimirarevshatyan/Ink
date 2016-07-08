package ink.models;

/**
 * Created by USER on 2016-07-06.
 */
public class GroupsModel {
    private String groupId;
    private String groupImage;
    private String groupName;
    private String groupOwnerName;
    private String groupDescription;
    private String groupOwnerId;
    private String groupColor;
    private String participantsCount;
    private String ownerImage;

    public GroupsModel(String groupId,
                       String groupImage,
                       String groupName, String groupOwnerName,
                       String groupDescription, String groupOwnerId,
                       String groupColor, String participantsCount,
                       String ownerImage) {
        this.groupId = groupId;
        this.ownerImage = ownerImage;
        this.participantsCount = participantsCount;
        this.groupImage = groupImage;
        this.groupName = groupName;
        this.groupOwnerName = groupOwnerName;
        this.groupDescription = groupDescription;
        this.groupOwnerId = groupOwnerId;
        this.groupColor = groupColor;
    }


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupImage() {
        return groupImage;
    }

    public void setGroupImage(String groupImage) {
        this.groupImage = groupImage;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupOwnerName() {
        return groupOwnerName;
    }

    public String getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(String participantsCount) {
        this.participantsCount = participantsCount;
    }

    public void setGroupOwnerName(String groupOwnerName) {
        this.groupOwnerName = groupOwnerName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getGroupOwnerId() {
        return groupOwnerId;
    }

    public void setGroupOwnerId(String groupOwnerId) {
        this.groupOwnerId = groupOwnerId;
    }

    public String getGroupColor() {
        return groupColor;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public void setGroupColor(String groupColor) {
        this.groupColor = groupColor;
    }
}
