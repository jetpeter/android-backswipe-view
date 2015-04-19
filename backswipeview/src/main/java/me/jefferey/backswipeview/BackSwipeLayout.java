package me.jefferey.backswipeview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class BackSwipeLayout extends ViewGroup {

    private static final int PAGE_SWIPE_VELOCITY = 100;

    private final ViewDragHelper mContentDragHelper;

    private BackSwipeInterface mBackSwipeInterface;

    private boolean mBackSwipeEnabled;
    private boolean mMarkPopBackStack;
    private boolean mIsAnimatingScroll;
    private boolean mIsDragging = false;

    public BackSwipeLayout(Context context) {
        this(context, null);
    }

    public BackSwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
        setChildrenDrawingCacheEnabled(true);
        mContentDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mContentDragHelper.setEdgeTrackingEnabled(ViewDragHelper.DIRECTION_HORIZONTAL);
        mMarkPopBackStack = false;
        mIsAnimatingScroll = false;

    }

    public void setBackSwipeInterface(BackSwipeInterface backSwipeInterface) {
        mBackSwipeInterface = backSwipeInterface;
    }

    /**
     * this will cancel any current swipe animation or current swipe in progress
     * Call this if the user wants to manually transition the fragments by hitting the
     * back or up button.
     */
    public void cancelBackSwipe() {
        mIsAnimatingScroll = false;
        mContentDragHelper.abort();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // First pass. Measure based on child LayoutParams width/height.
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        for (int i = 0; i < getChildCount(); i ++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // Keep the children where they are if we are currently dragging a view
            if (mIsDragging) {
                child.layout(child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            } else {
                child.layout(left, top, right, bottom);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (mIsAnimatingScroll) {
            animateScroll();
        }
    }

    private void animateScroll() {
        if (mContentDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else if (mBackSwipeInterface != null) {
            mBackSwipeInterface.onBackSwipeEnd(mMarkPopBackStack);
            mMarkPopBackStack = false;
            mIsAnimatingScroll = false;
        }
    }

    public void setBackSwipeEnabled(boolean enabled) {
        mBackSwipeEnabled = enabled;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return false;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            //Only update the incoming view's position if there is more than one view.
            if (getChildCount() > 1) {
                final View contentView = getChildAt(getChildCount() - 1);
                final View incomingView = getChildAt(getChildCount() - 2);
                int halfWidth = contentView.getWidth() / 2;
                int incomingLeft = left - contentView.getWidth();
                incomingView.layout((incomingLeft / 2), contentView.getTop(), left, contentView.getBottom());
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            mMarkPopBackStack = xvel > PAGE_SWIPE_VELOCITY || releasedChild.getLeft() > (getWidth() / 2);
            int leftJustifiedEndPoint = mMarkPopBackStack ? getWidth() : getPaddingLeft();
            if (mContentDragHelper.smoothSlideViewTo(releasedChild, leftJustifiedEndPoint, getPaddingTop())) {
                mIsAnimatingScroll = true;
                invalidate();
            } else if (mBackSwipeInterface != null) {
                //End the swipe since we are not animating
                mBackSwipeInterface.onBackSwipeEnd(false);
            }
            mIsDragging = false;

        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            mIsDragging = true;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            if (mBackSwipeEnabled && !mIsAnimatingScroll) {
                //The content view will always be the last child since it is drawn on top.
                if (getChildCount() > 0) {
                    final View contentView = getChildAt(getChildCount() - 1);
                    mContentDragHelper.captureChildView(contentView, pointerId);
                    if (mBackSwipeInterface != null) {
                        // This will add another child to the layout at the last spot as
                        // the new content fragment.
                        mBackSwipeInterface.onBackSwipeStarted();
                    }
                } else {
                    Log.v("BackSwipeLayout", "There is No content View");
                }
            }
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftBound = getPaddingLeft();
            final int rightBound = getWidth();
            return Math.min(Math.max(left, leftBound), rightBound);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Don't allow click events if we are animating
        if (mIsAnimatingScroll) {
            return true;
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mContentDragHelper.cancel();
            return false;
        }
        return mContentDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mContentDragHelper.processTouchEvent(ev);
        return true;
    }

    public interface BackSwipeInterface {
        public void onBackSwipeStarted();
        //No longer needed because we move the incoming view in the layout
        //public void onBackSwipeMove(int leftPosition);
        public void onBackSwipeEnd(boolean didSwipeBack);
    }
}