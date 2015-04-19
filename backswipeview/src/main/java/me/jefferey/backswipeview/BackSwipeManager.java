package me.jefferey.backswipeview;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by jpetersen on 4/12/15.
 */
public class BackSwipeManager implements BackSwipeLayout.BackSwipeInterface, FragmentManager.OnBackStackChangedListener {

    private final String FRAGMENT_TAG = "StackEntry";

    private final FragmentManager mFragmentManager;
    private final BackSwipeLayout mBackSwipeLayout;
    private final int mContentViewId;

    public BackSwipeManager(FragmentManager fragmentManager, BackSwipeLayout backSwipeLayout) {
        mBackSwipeLayout = backSwipeLayout;
        mBackSwipeLayout.setBackSwipeInterface(this);
        mBackSwipeLayout.setBackSwipeEnabled(fragmentManager.getBackStackEntryCount() > 0);
        mContentViewId = mBackSwipeLayout.getId();
        mFragmentManager = fragmentManager;
        mFragmentManager.addOnBackStackChangedListener(this);
    }

    /**
     * Must be called when the parent activity is first created. This adds the base level fragment
     * that cannot be swiped back.
     * Do not call this if the activity is being recreated from a savedInstanceState since the fragment
     * manager will add it for you.
     */
    public void setBaseFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.add(mContentViewId, fragment, FRAGMENT_TAG + "_" + mFragmentManager.getBackStackEntryCount());
        ft.commit();
    }

    /**
     * Adds a new back swipeable content fragment. The fragment should fill the entire page.
     */
    public void addContentFragment(Fragment fragment) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        Fragment currentContentFragment = mFragmentManager.findFragmentById(mContentViewId);
        int backStackCount = mFragmentManager.getBackStackEntryCount();
        ft.addToBackStack(FRAGMENT_TAG + "_" + backStackCount);
        ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Manually increase the current fragment back stack count.
        ft.add(mContentViewId, fragment, FRAGMENT_TAG + "_" + (backStackCount + 1));
        ft.hide(currentContentFragment);
        ft.commit();
    }

    @Override
    public void onBackStackChanged() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            mBackSwipeLayout.setBackSwipeEnabled(false);
        } else {
            mBackSwipeLayout.setBackSwipeEnabled(true);
        }
    }

    @Override
    public void onBackSwipeStarted() {
        Fragment frag = getLatestFragmentFromBackStack();
        mFragmentManager.beginTransaction().show(frag).commit();
    }

    @Override
    public void onBackSwipeEnd(boolean didSwipeBack) {
        if (didSwipeBack) {
            mFragmentManager.popBackStack();
        } else {
            // The swipe was canceled and the animation finished, we now need to hide the first fragment in the stack
            Fragment frag = getLatestFragmentFromBackStack();
            mFragmentManager.beginTransaction().hide(frag).commit();
        }
    }

    /**
     * Using the tags set when adding a fragment find the next fragment in the back stack if one exists
     */
    private Fragment getLatestFragmentFromBackStack() {
        int entryCount = mFragmentManager.getBackStackEntryCount();
        if (entryCount > 0) {
            FragmentManager.BackStackEntry entry = mFragmentManager.getBackStackEntryAt(entryCount - 1);
            return mFragmentManager.findFragmentByTag(entry.getName());
        } else {
            return null;
        }
    }
}
