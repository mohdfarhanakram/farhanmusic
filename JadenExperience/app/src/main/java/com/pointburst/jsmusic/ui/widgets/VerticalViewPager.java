package com.pointburst.jsmusic.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by FARHAN on 12/27/2014.
 */

/*
public class VerticalViewPager extends ViewGroup {
    public static final String TAG = "VerticalPager";

    private static final int INVALID_SCREEN = -1;
    public static final int SPEC_UNDEFINED = -1;
    private static final int TOP = 0;
    private static final int BOTTOM = 1;

    *//**
     * The velocity at which a fling gesture will cause us to snap to the next screen
     *//*
    private static final int SNAP_VELOCITY = 1000;

    private int pageHeight;
    private int measuredHeight;

    private boolean mFirstLayout = true;

    private int mCurrentPage;
    private int mNextPage = INVALID_SCREEN;

    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    private int mTouchSlop;
    private int mMaximumVelocity;

    private float mLastMotionY;
    private float mLastMotionX;

    private final static int TOUCH_STATE_REST = 0;
    private final static int TOUCH_STATE_SCROLLING = 1;

    private int mTouchState = TOUCH_STATE_REST;

    private boolean mAllowLongPress;

    private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();

    *//**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     *//*
    public VerticalViewPager(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    *//**
     * Used to inflate the Workspace from XML.
     *
     * @param context The application's context.
     * @param attrs The attribtues set containing the Workspace's customization values.
     * @param defStyle Unused.
     *//*
    public VerticalViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        //TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.com_deezapps_widget_HorizontalPager);
        //pageHeightSpec = a.getDimensionPixelSize(R.styleable.com_deezapps_widget_HorizontalPager_pageWidth, SPEC_UNDEFINED);
        //a.recycle();

        init(context);
    }

    *//**
     * Initializes various states for this workspace.
     *//*
    private void init(Context context) {
        mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        mCurrentPage = 0;

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }

    *//**
     * Returns the index of the currently displayed page.
     *
     * @return The index of the currently displayed page.
     *//*
    int getCurrentPage() {
        return mCurrentPage;
    }

    *//**
     * Sets the current page.
     *
     * @param currentPage
     *//*
    public void setCurrentPage(int currentPage) {
        mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));
        scrollTo(getScrollYForPage(mCurrentPage), 0);
        invalidate();
    }

    public int getPageHeight() {
        return pageHeight;
    }

    //public void setPageHeight(int pageHeight) {
    //    this.pageHeightSpec = pageHeight;
    //}

    *//**
     * Gets the value that getScrollX() should return if the specified page is the current page (and no other scrolling is occurring).
     * Use this to pass a value to scrollTo(), for example.
     * @param whichPage
     * @return
     *//*
    private int getScrollYForPage(int whichPage) {
        int height = 0;
        for(int i = 0; i < whichPage; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                height += child.getHeight();
            }
        }
        return height - pageHeightPadding();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextPage != INVALID_SCREEN) {
            mCurrentPage = mNextPage;
            mNextPage = INVALID_SCREEN;
            clearChildrenCache();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {

        // ViewGroup.dispatchDraw() supports many features we don't need:
        // clip to padding, layout animation, animation listener, disappearing
        // children, etc. The following implementation attempts to fast-track
        // the drawing dispatch by drawing only what we know needs to be drawn.

        final long drawingTime = getDrawingTime();
        // todo be smarter about which children need drawing
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            drawChild(canvas, getChildAt(i), drawingTime);
        }

        for (OnScrollListener mListener : mListeners) {
            int adjustedScrollY = getScrollY() + pageHeightPadding();
            mListener.onScroll(adjustedScrollY);
            if (adjustedScrollY % pageHeight == 0) {
                mListener.onViewScrollFinished(adjustedScrollY / pageHeight);
            }
        }
    }

    int pageHeightPadding() {
        return ((getMeasuredHeight() - pageHeight) / 2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        pageHeight = getMeasuredHeight();

        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(pageHeight, MeasureSpec.UNSPECIFIED));
        }

        if (mFirstLayout) {
            scrollTo(getScrollYForPage(mCurrentPage), 0);
            mFirstLayout = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        measuredHeight = 0;

        final int count = getChildCount();
        int height;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                if(i == 0) {
                    child.getHeight();
                    child.layout(0, measuredHeight, right - left, measuredHeight + (int)(pageHeight*.96));
                    measuredHeight +=  (pageHeight*.96);
                } else {
                    height = pageHeight * (int)Math.ceil((double)child.getMeasuredHeight()/(double)pageHeight);
                    height = Math.max(pageHeight, height);
                    child.layout(0, measuredHeight, right - left, measuredHeight + height);
                    measuredHeight += height;
                }
            }
        }
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        int screen = indexOfChild(child);
        if (screen != mCurrentPage || !mScroller.isFinished()) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int focusableScreen;
        if (mNextPage != INVALID_SCREEN) {
            focusableScreen = mNextPage;
        } else {
            focusableScreen = mCurrentPage;
        }
        getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
        return false;
    }

    @Override
    public boolean dispatchUnhandledMove(View focused, int direction) {
        if (direction == View.FOCUS_LEFT) {
            if (getCurrentPage() > 0) {
                snapToPage(getCurrentPage() - 1);
                return true;
            }
        } else if (direction == View.FOCUS_RIGHT) {
            if (getCurrentPage() < getChildCount() - 1) {
                snapToPage(getCurrentPage() + 1);
                return true;
            }
        }
        return super.dispatchUnhandledMove(focused, direction);
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
        getChildAt(mCurrentPage).addFocusables(views, direction);
        if (direction == View.FOCUS_LEFT) {
            if (mCurrentPage > 0) {
                getChildAt(mCurrentPage - 1).addFocusables(views, direction);
            }
        } else if (direction == View.FOCUS_RIGHT){
            if (mCurrentPage < getChildCount() - 1) {
                getChildAt(mCurrentPage + 1).addFocusables(views, direction);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //Log.d(TAG, "onInterceptTouchEvent::action=" + ev.getAction());

        *//*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onTouchEvent will be called and we do the actual
         * scrolling there.
         *//*

        *//*
         * Shortcut the most recurring case: the user is in the dragging
         * state and he is moving his finger.  We want to intercept this
         * motion.
         *//*
        final int action = ev.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            //Log.d(TAG, "onInterceptTouchEvent::shortcut=true");
            return true;
        }

        final float y = ev.getY();
        final float x = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                *//*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 *//*
                if (mTouchState == TOUCH_STATE_REST) {
                    checkStartScroll(x, y);
                }

                break;

            case MotionEvent.ACTION_DOWN:
                // Remember location of down touch
                mLastMotionX = x;
                mLastMotionY = y;
                mAllowLongPress = true;

                *//*
                 * If being flinged and user touches the screen, initiate drag;
                 * otherwise don't.  mScroller.isFinished should be false when
                 * being flinged.
                 *//*
                mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                // Release the drag
                clearChildrenCache();
                mTouchState = TOUCH_STATE_REST;
                break;
        }

        *//*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         *//*
        return mTouchState != TOUCH_STATE_REST;
    }

    private void checkStartScroll(float x, float y) {
        *//*
         * Locally do absolute value. mLastMotionX is set to the y value
         * of the down event.
         *//*
        final int xDiff = (int) Math.abs(x - mLastMotionX);
        final int yDiff = (int) Math.abs(y - mLastMotionY);

        boolean xMoved = xDiff > mTouchSlop;
        boolean yMoved = yDiff > mTouchSlop;

        if (xMoved || yMoved) {

            if (yMoved) {
                // Scroll if the user moved far enough along the X axis
                mTouchState = TOUCH_STATE_SCROLLING;
                enableChildrenCache();
            }
            // Either way, cancel any pending longpress
            if (mAllowLongPress) {
                mAllowLongPress = false;
                // Try canceling the long press. It could also have been scheduled
                // by a distant descendant, so use the mAllowLongPress flag to block
                // everything
                final View currentScreen = getChildAt(mCurrentPage);
                currentScreen.cancelLongPress();
            }
        }
    }

    void enableChildrenCache() {
        setChildrenDrawingCacheEnabled(true);
        setChildrenDrawnWithCacheEnabled(true);
    }

    void clearChildrenCache() {
        setChildrenDrawnWithCacheEnabled(false);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float x = ev.getX();
        final float y = ev.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                *//*
                * If being flinged and user touches, stop the fling. isFinished
                * will be false if being flinged.
                *//*
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                // Remember where the motion event started
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchState == TOUCH_STATE_REST) {
                    checkStartScroll(y, x);
                } else if (mTouchState == TOUCH_STATE_SCROLLING) {
                    // Scroll to follow the motion event
                    int deltaY = (int) (mLastMotionY - y);
                    mLastMotionY = y;

                    // Apply friction to scrolling past boundaries.
                    final int count = getChildCount();
                    if (getScrollY() < 0 || getScrollY() + pageHeight > getChildAt(count - 1).getBottom()) {
                        deltaY /= 2;
                    }

                    scrollBy(0, deltaY);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mTouchState == TOUCH_STATE_SCROLLING) {
                    final VelocityTracker velocityTracker = mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int velocityY = (int) velocityTracker.getYVelocity();

                    final int count = getChildCount();

                    // check scrolling past first or last page?
                    if(getScrollY() < 0) {
                        snapToPage(0);
                    } else if(getScrollY() > measuredHeight - pageHeight) {
                        snapToPage(count - 1, BOTTOM);
                    } else {
                        for(int i = 0; i < count; i++) {
                            final View child = getChildAt(i);
                            if(child.getTop() < getScrollY() &&
                                    child.getBottom() > getScrollY() + pageHeight) {
                                // we're inside a page, fling that bitch
                                mNextPage = i;
                                mScroller.fling(getScrollX(), getScrollY(), 0, -velocityY, 0, 0, child.getTop(), child.getBottom() - getHeight());
                                invalidate();
                                break;
                            } else if(child.getBottom() > getScrollY() && child.getBottom() < getScrollY() + getHeight()) {
                                // stuck in between pages, oh snap!
                                if(velocityY < -SNAP_VELOCITY) {
                                    snapToPage(i + 1);
                                } else if(velocityY > SNAP_VELOCITY) {
                                    snapToPage(i, BOTTOM);
                                } else if(getScrollY() + pageHeight/2 > child.getBottom()) {
                                    snapToPage(i + 1);
                                } else {
                                    snapToPage(i, BOTTOM);
                                }
                                break;
                            }
                        }
                    }

                    if (mVelocityTracker != null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker = null;
                    }
                }
                mTouchState = TOUCH_STATE_REST;
                break;
            case MotionEvent.ACTION_CANCEL:
                mTouchState = TOUCH_STATE_REST;
        }

        return true;
    }

    private void snapToPage(final int whichPage, final int where) {
        enableChildrenCache();

        boolean changingPages = whichPage != mCurrentPage;

        mNextPage = whichPage;

        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
            focusedChild.clearFocus();
        }

        final int delta;
        if(getChildAt(whichPage).getHeight() <= pageHeight || where == TOP) {
            delta = getChildAt(whichPage).getTop() - getScrollY();
        } else {
            delta = getChildAt(whichPage).getBottom() - pageHeight - getScrollY();
        }

        mScroller.startScroll(0, getScrollY(), 0, delta, 400);
        invalidate();
    }

    public void snapToPage(final int whichPage) {
        snapToPage(whichPage, TOP);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final SavedState state = new SavedState(super.onSaveInstanceState());
        state.currentScreen = mCurrentPage;
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.currentScreen != INVALID_SCREEN) {
            mCurrentPage = savedState.currentScreen;
        }
    }

    public void scrollUp() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller.isFinished()) {
            snapToPage(mCurrentPage - 1);
        }
    }

    public void scrollDown() {
        if (mNextPage == INVALID_SCREEN && mCurrentPage < getChildCount() - 1 && mScroller.isFinished()) {
            snapToPage(mCurrentPage + 1);
        }
    }

    public int getScreenForView(View v) {
        int result = -1;
        if (v != null) {
            ViewParent vp = v.getParent();
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (vp == getChildAt(i)) {
                    return i;
                }
            }
        }
        return result;
    }

    *//**
     * @return True is long presses are still allowed for the current touch
     *//*
    public boolean allowLongPress() {
        return mAllowLongPress;
    }

    public static class SavedState extends BaseSavedState {
        int currentScreen = -1;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentScreen = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(currentScreen);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

    public void addOnScrollListener(OnScrollListener listener) {
        mListeners.add(listener);
    }

    public void removeOnScrollListener(OnScrollListener listener) {
        mListeners.remove(listener);
    }

    *//**
     * Implement to receive events on scroll position and page snaps.
     *//*
    public static interface OnScrollListener {
        *//**
         * Receives the current scroll X value.  This value will be adjusted to assume the left edge of the first
         * page has a scroll position of 0.  Note that values less than 0 and greater than the right edge of the
         * last page are possible due to touch events scrolling beyond the edges.
         * @param scrollX Scroll X value
         *//*
        void onScroll(int scrollX);

        *//**
         * Invoked when scrolling is finished (settled on a page, centered).
         * @param currentPage The current page
         *//*
        void onViewScrollFinished(int currentPage);
    }
}*/


public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
        setPageTransformer(true, new VerticalPageTransformer());
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_IF_CONTENT_SCROLLS);
    }

    private class VerticalPageTransformer implements PageTransformer {

        @Override
        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                view.setAlpha(1);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                //set Y position to swipe in from top
                float yPosition = position * pageHeight;
                view.setTranslationY(yPosition);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }



    // * Swaps the X and Y coordinates of your touch event


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //swap the x and y coords of the touch event
        ev.setLocation(ev.getY(), ev.getX());

        return super.onTouchEvent(ev);
    }

}
