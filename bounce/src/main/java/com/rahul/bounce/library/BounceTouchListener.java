package com.rahul.bounce.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ListView;
import android.widget.ScrollView;


public class BounceTouchListener implements View.OnTouchListener {
    private static final long DEFAULT_ANIMATION_TIME = 600L;

    private boolean downCalled = false;
    private OnTranslateListener onTranslateListener;
    private View mMainView;
    private View mContent;
    private float mDownY;
    private boolean mSwipingDown;
    private boolean mSwipingUp;
    private Interpolator mInterpolator = new DecelerateInterpolator(3f);
    private boolean swipUpEnabled = true;
    private int mActivePointerId = -99;
    private float mLastTouchY = -99;
    private int mMaxAbsTranslation = -99;


    private BounceTouchListener(View mainView, int contentResId, @Nullable OnTranslateListener listener) {
        mMainView = mainView;
        mContent = (contentResId == -1) ? mMainView : mMainView.findViewById(contentResId);
        onTranslateListener = listener;
    }

    /**
     * Creates a new BounceTouchListener
     *
     * @param mainScrollableView  The main view that this touch listener is attached to
     * @param onTranslateListener To perform action on translation, can be null if not needed
     * @return A new BounceTouchListener attached to the given scrollable view
     */
    public static BounceTouchListener create(View mainScrollableView, @Nullable OnTranslateListener onTranslateListener) {
        return create(mainScrollableView, -1, onTranslateListener);
    }

    /**
     * Creates a new BounceTouchListener
     *
     * @param mainView            The main view that this touch listener is attached to
     * @param contentResId        Resource Id of the scrollable view
     * @param onTranslateListener To perform action on translation, can be null if not needed
     * @return A new BounceTouchListener attached to the given scrollable view
     */
    public static BounceTouchListener create(View mainView, @IdRes int contentResId,
                                             @Nullable OnTranslateListener onTranslateListener) {
        return new BounceTouchListener(mainView, contentResId, onTranslateListener);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                onDownMotionEvent(motionEvent);
                view.onTouchEvent(motionEvent);
                downCalled = true;
                if (mContent.getTranslationY() == 0) {
                    return false;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == -99) {
                    onDownMotionEvent(motionEvent);
                    downCalled = true;
                }
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                final float y = MotionEventCompat.getY(motionEvent, pointerIndex);

                if (!hasHitTop() && !hasHitBottom() || !downCalled) {
                    if (!downCalled) {
                        downCalled = true;
                    }
                    mDownY = y;
                    view.onTouchEvent(motionEvent);
                    return false;
                }

                float deltaY = y - mDownY;
                if (Math.abs(deltaY) > 0 && hasHitTop() && deltaY > 0) {
                    mSwipingDown = true;
                    sendCancelEventToView(view, motionEvent);
                }
                if (swipUpEnabled) {
                    if (Math.abs(deltaY) > 0 && hasHitBottom() && deltaY < 0) {
                        mSwipingUp = true;
                        sendCancelEventToView(view, motionEvent);
                    }
                }
                if (mSwipingDown || mSwipingUp) {
                    if ((deltaY <= 0 && mSwipingDown) || (deltaY >= 0 && mSwipingUp)) {
                        mDownY = 0;
                        mSwipingDown = false;
                        mSwipingUp = false;
                        downCalled = false;
                        MotionEvent downEvent = MotionEvent.obtain(motionEvent);
                        downEvent.setAction(MotionEvent.ACTION_DOWN |
                                (MotionEventCompat.getActionIndex(motionEvent) << MotionEventCompat.ACTION_POINTER_INDEX_SHIFT));
                        view.onTouchEvent(downEvent);
                        break;
                    }
                    int translation = (int) ((deltaY / Math.abs(deltaY)) * Math.pow(Math.abs(deltaY), .8f));
                    if (mMaxAbsTranslation > 0) {
                        if (translation < 0) {
                            translation = Math.max(-mMaxAbsTranslation, translation);
                        } else {
                            translation = Math.min(mMaxAbsTranslation, translation);
                        }
                    }
                    mContent.setTranslationY(translation);
                    if (onTranslateListener != null)
                        onTranslateListener.onTranslate(mContent.getTranslationY());

                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = -99;
                // cancel
                mContent.animate()
                        .setInterpolator(mInterpolator)
                        .translationY(0)
                        .setDuration(DEFAULT_ANIMATION_TIME)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                ((ValueAnimator) animation).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        if (onTranslateListener != null) {
                                            onTranslateListener.onTranslate(mContent.getTranslationY());
                                        }
                                    }
                                });
                                super.onAnimationStart(animation);
                            }
                        });

                mDownY = 0;
                mSwipingDown = false;
                mSwipingUp = false;
                downCalled = false;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = -99;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                final int pointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);

                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchY = MotionEventCompat.getY(motionEvent, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(motionEvent, newPointerIndex);

                    if (mContent.getTranslationY() > 0) {
                        mDownY = mLastTouchY - (int) Math.pow(mContent.getTranslationY(), 10f / 8f);
                        mContent.animate().cancel();
                    } else if (mContent.getTranslationY() < 0) {
                        mDownY = mLastTouchY + (int) Math.pow(-mContent.getTranslationY(), 10f / 8f);
                        mContent.animate().cancel();
                    }
                }
                break;
            }
        }
        return false;
    }

    private void sendCancelEventToView(View view, MotionEvent motionEvent) {
        ((ViewGroup) view).requestDisallowInterceptTouchEvent(true);
        MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                (MotionEventCompat.getActionIndex(motionEvent) << MotionEventCompat.ACTION_POINTER_INDEX_SHIFT));
        view.onTouchEvent(cancelEvent);
    }

    private void onDownMotionEvent(MotionEvent motionEvent) {
        final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
        mLastTouchY = MotionEventCompat.getY(motionEvent, pointerIndex);
        mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);

        if (mContent.getTranslationY() > 0) {
            mDownY = mLastTouchY - (int) Math.pow(mContent.getTranslationY(), 10f / 8f);
            mContent.animate().cancel();
        } else if (mContent.getTranslationY() < 0) {
            mDownY = mLastTouchY + (int) Math.pow(-mContent.getTranslationY(), 10f / 8f);
            mContent.animate().cancel();
        } else {
            mDownY = mLastTouchY;
        }
    }

    private boolean hasHitBottom() {
        if (mMainView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mMainView;
            View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
            int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));// Calculate the scrolldiff
            return diff == 0;
        } else if (mMainView instanceof ListView) {
            ListView listView = (ListView) mMainView;
            if (listView.getAdapter() != null) {
                if (listView.getAdapter().getCount() > 0) {
                    return listView.getLastVisiblePosition() == listView.getAdapter().getCount() - 1 &&
                            listView.getChildAt(listView.getChildCount() - 1).getBottom() <= listView.getHeight();
                }
            }
        } else if (mMainView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mMainView;
            if (recyclerView.getAdapter() != null && recyclerView.getLayoutManager() != null) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter.getItemCount() > 0) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        return linearLayoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        int[] checks = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                        for (int check : checks) {
                            if (check == adapter.getItemCount() - 1)
                                return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean hasHitTop() {
        if (mMainView instanceof ScrollView) {
            ScrollView scrollView = (ScrollView) mMainView;
            return scrollView.getScrollY() == 0;
        } else if (mMainView instanceof ListView) {
            ListView listView = (ListView) mMainView;
            if (listView.getAdapter() != null) {
                if (listView.getAdapter().getCount() > 0) {
                    return listView.getFirstVisiblePosition() == 0 &&
                            listView.getChildAt(0).getTop() >= 0;
                }
            }
        } else if (mMainView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) mMainView;
            if (recyclerView.getAdapter() != null && recyclerView.getLayoutManager() != null) {
                RecyclerView.Adapter adapter = recyclerView.getAdapter();
                if (adapter.getItemCount() > 0) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    if (layoutManager instanceof LinearLayoutManager) {
                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                        return linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                    } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                        int[] checks = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
                        for (int check : checks) {
                            if (check == 0)
                                return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public void setMaxAbsTranslation(int maxAbsTranslation) {
        this.mMaxAbsTranslation = maxAbsTranslation;
    }

    public interface OnTranslateListener {
        void onTranslate(float translation);
    }
}
