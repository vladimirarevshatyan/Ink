package kashmirr.social.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by PC-Comp on 9/12/2016.
 */
public class NewsTopic {
    @SerializedName("category")
    public String category;
    @SerializedName("description")
    public String description;
    @SerializedName("image_url")
    public String imageUrl;
}
