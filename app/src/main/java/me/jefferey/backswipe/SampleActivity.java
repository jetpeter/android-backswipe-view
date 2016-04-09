package me.jefferey.backswipe;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import me.jefferey.backswipeview.BackSwipeLayout;
import me.jefferey.backswipeview.BackSwipeManager;


public class SampleActivity extends AppCompatActivity implements  SampleFragment.ContentFragmentInterface {

    private BackSwipeManager mBackSwipeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackSwipeLayout backSwipeLayout = (BackSwipeLayout) findViewById(R.id.back_swipe_layout);
        backSwipeLayout.setBackSwipeEnabled(false);
        FragmentManager fm = getSupportFragmentManager();
        mBackSwipeManager = new BackSwipeManager(fm, backSwipeLayout);
        if (savedInstanceState == null) {
            Fragment contentFragment = SampleFragment.newInstance();
            mBackSwipeManager.setBaseFragment(contentFragment);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("savedState", 1);
    }

    @Override
    public void onNewPageClicked() {
        Fragment content = SampleFragment.newInstance();
        mBackSwipeManager.addContentFragment(content);
    }
}
