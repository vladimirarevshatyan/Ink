package kashmirr.social.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kashmirr.social.R;

/**
 * Created by PC-Comp on 9/2/2016.
 */
public class SecondIntroFragment extends Fragment {
    private Animation slideInRight;
    private Animation slideInLeft;
    private Animation fadeIn;
    private RelativeLayout newFriendsBubble;
    private ImageView girlVector;
    private TextView secondIntroDescription;

    public static SecondIntroFragment create() {
        SecondIntroFragment secondIntroFragment = new SecondIntroFragment();
        return secondIntroFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.second_intro_view, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newFriendsBubble = (RelativeLayout) view.findViewById(R.id.newFriendsBubble);
        secondIntroDescription = (TextView) view.findViewById(R.id.secondIntroDescription);
        girlVector = (ImageView) view.findViewById(R.id.girlVector);
        slideInLeft = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_left);
        slideInRight = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
        fadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        fadeIn.setDuration(600);

    }

    public void startAnimation() {
        if (newFriendsBubble != null) {
            newFriendsBubble.setVisibility(View.VISIBLE);
            girlVector.setVisibility(View.VISIBLE);
            newFriendsBubble.startAnimation(slideInLeft);
            girlVector.startAnimation(slideInRight);
            slideInRight.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    secondIntroDescription.setVisibility(View.VISIBLE);
                    secondIntroDescription.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    public void hideItems(){
        if(newFriendsBubble!=null){
            newFriendsBubble.setVisibility(View.GONE);
            girlVector.setVisibility(View.GONE);
            secondIntroDescription.setVisibility(View.GONE);
        }

    }
}
