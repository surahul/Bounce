package com.rahul.bounce.library;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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


    private static final int DEFAULT_ANIMATION_TIME = 600;
    boolean downCalled = false;
    OnTranslateListener onTranslateListener;
    private View mMainView;
    private View mContent;
    private long mAnimationTime = DEFAULT_ANIMATION_TIME;
    private float mDownY;
    private boolean mSwipingDown, mSwipingUp;
    private float mTranslationY;
    private Interpolator mInterpolator = new DecelerateInterpolator(3f);
    private boolean swipUpEnabled = true;
    private int mActivePointerId = -99;
    private float mLastTouchX = -99;
    private float mLastTouchY = -99;
    private int mMaxAbsTranslation = -99;

    public BounceTouchListener(View mainScrollableView) {
        this.mMainView = mainScrollableView;
        this.mContent = this.mMainView;
    }

    public BounceTouchListener(View mainScrollableView, int contentResId) {
        this.mMainView = mainScrollableView;
        this.mContent = this.mMainView.findViewById(contentResId);
    }

    public void setOnTranslateListener(OnTranslateListener onTranslateListener) {
        this.onTranslateListener = onTranslateListener;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final int action = MotionEventCompat.getActionMasked(motionEvent);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                final float y = MotionEventCompat.getY(motionEvent, pointerIndex);
                mLastTouchX = x;
                mLastTouchY = y;
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
                view.onTouchEvent(motionEvent);
                downCalled = true;
                if (mContent.getTranslationY() == 0) {
                    return false;
                }
            }
            case MotionEvent.ACTION_MOVE: {
                if (mActivePointerId == -99) {
                    final int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                    final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
                    final float y = MotionEventCompat.getY(motionEvent, pointerIndex);
                    mLastTouchX = x;
                    mLastTouchY = y;
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
                    downCalled = true;
                }
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                final float x = MotionEventCompat.getX(motionEvent, pointerIndex);
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
                    ((ViewGroup) view).requestDisallowInterceptTouchEvent(true);
                    MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                    cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                            (MotionEventCompat.getActionIndex(motionEvent) << MotionEventCompat.ACTION_POINTER_INDEX_SHIFT));
                    view.onTouchEvent(cancelEvent);
                }
                if (swipUpEnabled) {
                    if (Math.abs(deltaY) > 0 && hasHitBottom() && deltaY < 0) {
                        mSwipingUp = true;
                        ((ViewGroup) view).requestDisallowInterceptTouchEvent(true);
                        MotionEvent cancelEvent = MotionEvent.obtain(motionEvent);
                        cancelEvent.setAction(MotionEvent.ACTION_CANCEL |
                                (MotionEventCompat.getActionIndex(motionEvent) << MotionEventCompat.ACTION_POINTER_INDEX_SHIFT));
                        view.onTouchEvent(cancelEvent);
                    }
                }
                if (mSwipingDown || mSwipingUp) {
                    mTranslationY = deltaY;
                    if ((deltaY <= 0 && mSwipingDown) || (deltaY >= 0 && mSwipingUp)) {
                        mTranslationY = 0;
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
                        .setDuration(mAnimationTime)
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

                mTranslationY = 0;
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
                    mLastTouchX = MotionEventCompat.getX(motionEvent, newPointerIndex);
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
