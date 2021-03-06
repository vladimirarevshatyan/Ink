package kashmirr.social.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by USER on 2016-07-21.
 */
public class PacksModel {
    @SerializedName("pack_price")
    public String packsPrice;
    @SerializedName("pack_id")
    public int packsId;
    @SerializedName("pack_name_en")
    public String packNameEn;
    @SerializedName("pack_name_ru")
    public String packNameRu;
    @SerializedName("pack_background")
    public String packBackground;
    @SerializedName("pack_image_background")
    public String packImageBackground;
    @SerializedName("pack_description")
    public String packDescription;


}
