package me.jefferey.backswipe;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.Random;

public class SampleFragment extends Fragment {

    private static final String COLOR_KEY = "background_color";

    /**
     * Create a new content fragment with a random background color. The fragment will have
     * a button that when pressed will call ContentFragmentInterface.onNewPageClicked()
     */
    public static SampleFragment newInstance() {
        SampleFragment fragment = new SampleFragment();
        Random rand = new Random();
        int r = rand.nextInt();
        int g = rand.nextInt();
        int b = rand.nextInt();
        Bundle args = new Bundle();
        args.putInt(COLOR_KEY, Color.rgb(r, g, b));
        fragment.setArguments(args);
        return fragment;
    }

    private ContentFragmentInterface mInterface;

    public SampleFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        Bundle args = getArguments();
        if (args != null) {
            view.setBackgroundColor(args.getInt(COLOR_KEY));
            view.setAlpha(0.6f);
        }
        View button = view.findViewById(R.id.button);
        Random rand = new Random();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        switch (rand.nextInt() % 4) {
            case 0:
                params.gravity = Gravity.CENTER;
                break;
            case 1:
                params.gravity = Gravity.LEFT|Gravity.BOTTOM;
                break;
            case 2:
                params.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
                break;
            case 3:
                params.gravity = Gravity.CENTER_HORIZONTAL;
                break;
        }

        button.setLayoutParams(params);

        button.setOnClickListener(mAddNewFragmentClickListener);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof ContentFragmentInterface) {
            mInterface = (ContentFragmentInterface) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mInterface = null;
    }

    private View.OnClickListener mAddNewFragmentClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mInterface != null) {
                mInterface.onNewPageClicked();
            }
        }
    };

    public interface ContentFragmentInterface {

        public void onNewPageClicked();

    }

}
