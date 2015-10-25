package com.rahul.bounce;

import android.animation.*;
import android.app.Fragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ScrollView;
import android.widget.TextView;
import com.rahul.bounce.library.BounceTouchListener;

public class MainActivity extends AppCompatActivity {
    private static final int STATE_INITIAL_ANIMATING = 0;
    private static final int STATE_INITIAL_ANIMATED = 1;
    private static final int STATE_OPTIONS_APPEAR_ANIMATING = 2;
    private static final int STATE_OPTIONS_APPEAR_ANIMATED = 3;
    private static final int STATE_EXAMPLE_APPEAR_ANIMATING = 4;
    private static final int STATE_EXAMPLE_APPEAR_ANIMATED = 5;

    private View ball;
    private ScrollView scrollView;
    private View scrollHouse;
    private View mainBg;
    private View header;
    private View splashNextBg;
    private View splashNext;
    private View optionsBg;
    private View optionsBullet1, optionsBullet2, optionsBullet3;
    private View options1, options2, options3;
    private View optionsHouse;
    private View toolbar;
    private Fragment exampleFragment;
    private AnimatorSet finalAnimator;

    private Interpolator initAnimationInterpolator = PathInterpolatorCompat.create(.8f, 0, .2f, 1);
    private Interpolator midAnimationInterpolator = PathInterpolatorCompat.create(.01f, 0, .025f, 1);

    private int state = STATE_INITIAL_ANIMATING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wireUpWidgets();
        startAnimation();

    }

    private void wireUpWidgets() {
        toolbar = findViewById(R.id.toolbar);
        ball = findViewById(R.id.splash_ball);
        scrollView = (ScrollView) findViewById(R.id.splash_scroll_view);
        scrollHouse = findViewById(R.id.splash_scroll_view_house);
        scrollHouse.getLayoutParams().width = (int) (Utils.getScreenWidth(this) * .5f);
        scrollHouse.getLayoutParams().height = (int) (Utils.getScreenHeight(this) * .5f);
        scrollHouse.setLayoutParams(scrollHouse.getLayoutParams());
        optionsBg = findViewById(R.id.options_bg);
        optionsBg.getLayoutParams().width = scrollHouse.getLayoutParams().width;
        optionsBg.getLayoutParams().height = scrollHouse.getLayoutParams().height;
        optionsBg.setLayoutParams(optionsBg.getLayoutParams());
        mainBg = findViewById(R.id.splash_post_bg);
        header = findViewById(R.id.scroll_header);
        header.getLayoutParams().height = (int) (scrollHouse.getLayoutParams().height * .4f);
        header.setLayoutParams(header.getLayoutParams());
        splashNext = findViewById(R.id.splash_next);
        splashNextBg = findViewById(R.id.splash_next_bg);
        splashNextBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_INITIAL_ANIMATED)
                    optionsAppearAnimation();
            }
        });
        header.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                header.setPivotX(header.getMeasuredWidth() * .5f);
            }
        });

        BounceTouchListener bounceTouchListener = new BounceTouchListener(scrollView, R.id.splash_scroll_content);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    float scale = ((translation) / header.getLayoutParams().height) + 1;
                    header.setScaleX(scale);
                    header.setScaleY(scale);

                }
            }
        });

        scrollView.setOnTouchListener(bounceTouchListener);


        optionsHouse = findViewById(R.id.options_house);
        optionsBullet1 = findViewById(R.id.options_bullet_1);
        optionsBullet2 = findViewById(R.id.options_bullet_2);
        optionsBullet3 = findViewById(R.id.options_bullet_3);
        options1 = findViewById(R.id.options_1);
        options2 = findViewById(R.id.options_2);
        options3 = findViewById(R.id.options_3);

        findViewById(R.id.option_1_house).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_OPTIONS_APPEAR_ANIMATED) {
                    toFragmentAnimation(0);
                }
            }
        });
        findViewById(R.id.option_2_house).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_OPTIONS_APPEAR_ANIMATED) {
                    toFragmentAnimation(1);
                }
            }
        });
        findViewById(R.id.option_3_house).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_OPTIONS_APPEAR_ANIMATED) {
                    toFragmentAnimation(2);
                }
            }
        });
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == STATE_EXAMPLE_APPEAR_ANIMATED) {
                    animateFromExampleToOptions();
                }
            }
        });


    }

    private void startAnimation() {
        if (finalAnimator != null) {
            finalAnimator.removeAllListeners();
            finalAnimator.cancel();
        }

        ball.setTranslationY(-((Utils.getScreenHeight(this) * .5f) + getResources().getDimension(R.dimen.splash_ball_size)));
        ObjectAnimator ballFallAnimator = ObjectAnimator.ofFloat(ball, "translationY", 0);
        ballFallAnimator.setDuration(400);
        ballFallAnimator.setInterpolator(new AccelerateInterpolator(2.5f));

        ObjectAnimator ballFallDownSquezeOut = ObjectAnimator.ofFloat(ball, "scaleY", 1, 1.3f);
        ballFallDownSquezeOut.setDuration(150);
        ballFallDownSquezeOut.setInterpolator(new AccelerateInterpolator(2.5f));

        ObjectAnimator ballOnDownSquezeOutY = ObjectAnimator.ofFloat(ball, "scaleY", 1);
        ballOnDownSquezeOutY.setDuration(250);
        ballOnDownSquezeOutY.setInterpolator(new DecelerateInterpolator(3f));
        ObjectAnimator ballOnDownSquezeOutX = ObjectAnimator.ofFloat(ball, "scaleX", 1.3f);
        ballOnDownSquezeOutX.setDuration(250);
        ballOnDownSquezeOutX.setInterpolator(new DecelerateInterpolator(3f));


        ObjectAnimator ballFallAnimatorReverse = ObjectAnimator.ofFloat(ball, "translationY", -((Utils.getScreenHeight(this) * .5f) + getResources().getDimension(R.dimen.splash_ball_size)) * .5f);
        ballFallAnimatorReverse.setDuration(300);
        ballFallAnimatorReverse.setInterpolator(new DecelerateInterpolator(2.5f));


        ObjectAnimator ballOnDownSquezeOutYReverse = ObjectAnimator.ofFloat(ball, "scaleY", 1.3f);
        ballOnDownSquezeOutYReverse.setDuration(300);
        ballOnDownSquezeOutYReverse.setInterpolator(new DecelerateInterpolator(3f));
        ObjectAnimator ballOnDownSquezeOutXReverse = ObjectAnimator.ofFloat(ball, "scaleX", 1);
        ballOnDownSquezeOutXReverse.setDuration(300);
        ballOnDownSquezeOutXReverse.setInterpolator(new DecelerateInterpolator(3f));


//        ObjectAnimator ballFallAnimatorZoom = ObjectAnimator.ofFloat(ball,"translationY",((Utils.getScreenHeight(this)*.5f)+getResources().getDimension(R.dimen.splash_ball_size))*.3f);
        ObjectAnimator ballFallAnimatorZoom = ObjectAnimator.ofFloat(ball, "translationY", 0);
        ballFallAnimatorZoom.setDuration(300);
        //  ballFallAnimatorZoom.setInterpolator(new AccelerateInterpolator(3f));


        float ballScale = (float) (Math.sqrt(Math.pow(Utils.getScreenHeight(this), 2) + Math.pow(Utils.getScreenWidth(this), 2)) / getResources().getDimension(R.dimen.splash_ball_size));
        ObjectAnimator ballOnDownSquezeOutYZoom = ObjectAnimator.ofFloat(ball, "scaleY", ballScale);
        ballOnDownSquezeOutYZoom.setDuration(300);
        ballOnDownSquezeOutYZoom.setInterpolator(new AccelerateInterpolator(3f));
        ObjectAnimator ballOnDownSquezeOutXZoom = ObjectAnimator.ofFloat(ball, "scaleX", ballScale);
        ballOnDownSquezeOutXZoom.setDuration(300);
        ballOnDownSquezeOutXZoom.setInterpolator(new AccelerateInterpolator(3f));

        ObjectAnimator ballOnDownPivotYZoom = ObjectAnimator.ofFloat(ball, "pivotY", getResources().getDimension(R.dimen.splash_ball_size_half));
        ballOnDownPivotYZoom.setDuration(300);
        ObjectAnimator ballOnDownPivotXZoom = ObjectAnimator.ofFloat(ball, "pivotX", getResources().getDimension(R.dimen.splash_ball_size_half));
        ballOnDownPivotXZoom.setDuration(300);

        AnimatorSet initSet = new AnimatorSet();
        AnimatorSet midSet = new AnimatorSet();
        AnimatorSet endSet = new AnimatorSet();

        initSet.play(ballFallAnimator).with(ballFallDownSquezeOut).before(ballOnDownSquezeOutX).before(ballOnDownSquezeOutY);

        midSet.play(ballFallAnimatorReverse).with(ballOnDownSquezeOutXReverse).with(ballOnDownSquezeOutYReverse);

        endSet.play(ballFallAnimatorZoom).with(ballOnDownSquezeOutXZoom).with(ballOnDownSquezeOutYZoom).with(ballOnDownPivotYZoom).with(ballOnDownPivotXZoom);

        AnimatorSet ballAnimationSet = new AnimatorSet();
        ballAnimationSet.play(midSet).after(initSet).before(endSet);
        ballAnimationSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                state = STATE_INITIAL_ANIMATED;
            }
        });

        mainBg.setAlpha(0);
        scrollHouse.setAlpha(0);
        splashNext.setTranslationY(getResources().getDimension(R.dimen.splash_next_button_height));
        splashNextBg.setTranslationY(getResources().getDimension(R.dimen.splash_next_button_height));
        optionsBg.setAlpha(0);


        optionsBullet1.setScaleX(0);
        optionsBullet1.setScaleY(0);

        optionsBullet2.setScaleX(0);
        optionsBullet2.setScaleY(0);

        optionsBullet3.setScaleX(0);
        optionsBullet3.setScaleY(0);

        options1.setAlpha(0);
        options2.setAlpha(0);
        options3.setAlpha(0);

        toolbar.setAlpha(0);


        ObjectAnimator mainBgSwipeUp = ObjectAnimator.ofFloat(mainBg, "translationY", Utils.getScreenHeight(this) * .5f, 0);
        mainBgSwipeUp.setInterpolator(new DecelerateInterpolator(4f));
        mainBgSwipeUp.setDuration(1600);

        ObjectAnimator mainBgFadeIn = ObjectAnimator.ofFloat(mainBg, "alpha", 0, 1);
        mainBgFadeIn.setInterpolator(new DecelerateInterpolator(1.3f));
        mainBgFadeIn.setDuration(500);


        ObjectAnimator scrollSwipeUp = ObjectAnimator.ofFloat(scrollHouse, "translationY", Utils.getScreenHeight(this) * .1f, -Utils.getScreenHeight(this) * .1f);
        scrollSwipeUp.setInterpolator(new DecelerateInterpolator(4f));
        scrollSwipeUp.setStartDelay(100);
        scrollSwipeUp.setDuration(1600);

        ObjectAnimator scrollFadeIn = ObjectAnimator.ofFloat(scrollHouse, "alpha", 0, 1);
        scrollFadeIn.setInterpolator(new DecelerateInterpolator(1.3f));
        scrollFadeIn.setStartDelay(100);
        scrollFadeIn.setDuration(500);

        ObjectAnimator headerScale = ObjectAnimator.ofFloat(header, "scaleY", 2f, 1);
        headerScale.setInterpolator(new DecelerateInterpolator(4f));
        headerScale.setStartDelay(100);
        headerScale.setDuration(1600);

        ObjectAnimator nextSwipeUp = ObjectAnimator.ofFloat(splashNext, "translationY", 0);
        nextSwipeUp.setInterpolator(new DecelerateInterpolator(4f));
        nextSwipeUp.setStartDelay(100);
        nextSwipeUp.setDuration(1600);

        ObjectAnimator nextBgSwipeUp = ObjectAnimator.ofFloat(splashNextBg, "translationY", 0);
        nextBgSwipeUp.setInterpolator(new DecelerateInterpolator(4f));
        nextBgSwipeUp.setStartDelay(100);
        nextBgSwipeUp.setDuration(1600);

        AnimatorSet postAnimationSet = new AnimatorSet();
        postAnimationSet.play(mainBgSwipeUp).with(mainBgFadeIn).with(scrollSwipeUp).with(scrollFadeIn).with(headerScale).with(nextSwipeUp).with(nextBgSwipeUp);

        finalAnimator = new AnimatorSet();
        finalAnimator.play(postAnimationSet).after(ballAnimationSet);

        finalAnimator.setStartDelay(400);

        finalAnimator.start();


    }

    private void animateFromOptionsToSplash() {

        if (finalAnimator != null) {
            finalAnimator.removeAllListeners();
            finalAnimator.cancel();
        }

        state = STATE_INITIAL_ANIMATING;


        scrollHouse.setTranslationY(optionsBg.getTranslationY());


        ObjectAnimator bgScaleDown = ObjectAnimator.ofFloat(splashNextBg, "scaleY", 1);
        bgScaleDown.setInterpolator(midAnimationInterpolator);
        ObjectAnimator nextTranslateUp = ObjectAnimator.ofFloat(splashNext, "translationY", 0);
        nextTranslateUp.setInterpolator(midAnimationInterpolator);
        ObjectAnimator scrollTranslateDown = ObjectAnimator.ofFloat(scrollHouse, "translationY", -Utils.getScreenHeight(this) * .1f);
        scrollTranslateDown.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsBgTranslateDown = ObjectAnimator.ofFloat(optionsBg, "translationY", -Utils.getScreenHeight(this) * .1f);
        optionsBgTranslateDown.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsBgFadeOut = ObjectAnimator.ofFloat(optionsBg, "alpha", 0f);
        optionsBgFadeOut.setInterpolator(midAnimationInterpolator);


        AnimatorSet midSet = new AnimatorSet();
        midSet.setDuration(600);
        midSet.setInterpolator(midAnimationInterpolator);
        midSet.setStartDelay(60);
        midSet.play(bgScaleDown).with(nextTranslateUp).with(scrollTranslateDown).with(optionsBgTranslateDown).with(optionsBgFadeOut);


        ObjectAnimator optionsBgScaleX = ObjectAnimator.ofFloat(optionsBg, "scaleX", 1);
        optionsBgScaleX.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBgScaleY = ObjectAnimator.ofFloat(optionsBg, "scaleY", 1);
        optionsBgScaleY.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBgElev = ObjectAnimator.ofFloat(optionsBg, "elevation", getResources().getDimension(R.dimen.elev_options_pre_animate));
        optionsBgElev.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsElev = ObjectAnimator.ofFloat(optionsHouse, "elevation", getResources().getDimension(R.dimen.elev_options_pre_animate));
        optionsElev.setInterpolator(initAnimationInterpolator);
        Integer colorFrom = ((ColorDrawable) splashNextBg.getBackground()).getColor();
        Integer colorTo = getResources().getColor(R.color.splash_purple);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setInterpolator(initAnimationInterpolator);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                splashNextBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });
        ObjectAnimator optionsBullet1ScaleX = ObjectAnimator.ofFloat(optionsBullet1, "scaleX", 0);
        optionsBullet1ScaleX.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBullet1ScaleY = ObjectAnimator.ofFloat(optionsBullet1, "scaleY", 0);
        optionsBullet1ScaleY.setInterpolator(initAnimationInterpolator);

        ObjectAnimator optionsBullet2ScaleX = ObjectAnimator.ofFloat(optionsBullet2, "scaleX", 0);
        optionsBullet2ScaleX.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBullet2ScaleY = ObjectAnimator.ofFloat(optionsBullet2, "scaleY", 0);
        optionsBullet2ScaleY.setInterpolator(initAnimationInterpolator);

        ObjectAnimator optionsBullet3ScaleX = ObjectAnimator.ofFloat(optionsBullet3, "scaleX", 0);
        optionsBullet3ScaleX.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBullet3ScaleY = ObjectAnimator.ofFloat(optionsBullet3, "scaleY", 0);
        optionsBullet3ScaleY.setInterpolator(initAnimationInterpolator);

        ObjectAnimator options1Alpha = ObjectAnimator.ofFloat(options1, "alpha", 0);
        options1Alpha.setInterpolator(initAnimationInterpolator);
        ObjectAnimator options2Alpha = ObjectAnimator.ofFloat(options2, "alpha", 0);
        options2Alpha.setInterpolator(initAnimationInterpolator);
        ObjectAnimator options3Alpha = ObjectAnimator.ofFloat(options3, "alpha", 0);
        options3Alpha.setInterpolator(initAnimationInterpolator);

        ObjectAnimator option1Translate = ObjectAnimator.ofFloat(options1, "translationX", -getResources().getDimension(R.dimen.options_pad));
        option1Translate.setInterpolator(initAnimationInterpolator);
        ObjectAnimator option2Translate = ObjectAnimator.ofFloat(options2, "translationX", -getResources().getDimension(R.dimen.options_pad));
        option2Translate.setInterpolator(initAnimationInterpolator);
        ObjectAnimator option3Translate = ObjectAnimator.ofFloat(options3, "translationX", -getResources().getDimension(R.dimen.options_pad));
        option3Translate.setInterpolator(initAnimationInterpolator);


        AnimatorSet initSet = new AnimatorSet();
        initSet.setDuration(400);
        initSet.setInterpolator(initAnimationInterpolator);
        initSet.play(colorAnimation).with(optionsBgScaleX).with(optionsBgScaleY).with(optionsBgElev).with(optionsElev).with(optionsBullet1ScaleX).with(optionsBullet1ScaleY).with(optionsBullet2ScaleX).with(optionsBullet2ScaleY).with(optionsBullet3ScaleX).with(optionsBullet3ScaleY).with(options1Alpha).with(options2Alpha).with(options3Alpha).with(option1Translate).with(option2Translate).with(option3Translate);


        ObjectAnimator scrollShow = ObjectAnimator.ofFloat(scrollHouse, "alpha", 1);
        scrollShow.setDuration(0);

        finalAnimator = new AnimatorSet();
        finalAnimator.play(scrollShow).after(initSet).with(midSet);
        finalAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                state = STATE_INITIAL_ANIMATED;
                Utils.setElevation(scrollHouse, getResources().getDimension(R.dimen.elev_options_pre_animate));

            }
        });
        finalAnimator.start();

        enableClick(false);

    }


    private void optionsAppearAnimation() {
        state = STATE_OPTIONS_APPEAR_ANIMATING;

        if (finalAnimator != null) {
            finalAnimator.removeAllListeners();
            finalAnimator.cancel();
        }

        optionsHouse.setAlpha(1);
        Utils.setElevation(scrollHouse, 0);
        splashNextBg.setPivotX(Utils.getScreenWidth(this) * .5f);
        splashNextBg.setPivotY(getResources().getDimension(R.dimen.splash_next_button_height));
        optionsBg.setTranslationY(scrollHouse.getTranslationY());
        optionsHouse.setTranslationY(0);
        optionsHouse.getLayoutParams().width = (int) (Utils.getScreenWidth(this) * .85f);
        optionsHouse.getLayoutParams().height = (int) (Utils.getScreenHeight(this) * .53f);
        optionsHouse.setLayoutParams(optionsHouse.getLayoutParams());
//        optionsHouse.setTranslationY((scrollHouse.getTranslationY() - Utils.getScreenHeight(this) * .1f) + ((scrollHouse.getLayoutParams().height - (Utils.getScreenHeight(this)*.4f) )*.5f));


        ObjectAnimator bgScaleUp = ObjectAnimator.ofFloat(splashNextBg, "scaleY", Utils.getScreenHeight(this) / getResources().getDimension(R.dimen.splash_next_button_height));
        bgScaleUp.setInterpolator(initAnimationInterpolator);
        ObjectAnimator bgSwipeUp = ObjectAnimator.ofFloat(splashNextBg, "translationY", 0);
        bgSwipeUp.setInterpolator(initAnimationInterpolator);
        ObjectAnimator mainBgSwipeUp = ObjectAnimator.ofFloat(mainBg, "translationY", 0);
        mainBgSwipeUp.setInterpolator(initAnimationInterpolator);


        ObjectAnimator nextTranslateDown = ObjectAnimator.ofFloat(splashNext, "translationY", getResources().getDimension(R.dimen.splash_next_button_height));
        nextTranslateDown.setInterpolator(initAnimationInterpolator);
        ObjectAnimator scrollTranslateUp = ObjectAnimator.ofFloat(scrollHouse, "translationY", 0);
        scrollTranslateUp.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBgTranslateUp = ObjectAnimator.ofFloat(optionsBg, "translationY", 0);
        optionsBgTranslateUp.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsBgFadeIn = ObjectAnimator.ofFloat(optionsBg, "alpha", 1f);
        optionsBgFadeIn.setInterpolator(initAnimationInterpolator);


        AnimatorSet initSet = new AnimatorSet();
        initSet.setDuration(400);
        initSet.setInterpolator(initAnimationInterpolator);
        initSet.play(bgScaleUp).with(mainBgSwipeUp).with(bgSwipeUp).with(nextTranslateDown).with(scrollTranslateUp).with(optionsBgTranslateUp).with(optionsBgFadeIn);
        initSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                state = STATE_OPTIONS_APPEAR_ANIMATED;
            }
        });


        ObjectAnimator optionsBgScaleX = ObjectAnimator.ofFloat(optionsBg, "scaleX", (Utils.getScreenWidth(this) * .85f) / optionsBg.getLayoutParams().width);
        optionsBgScaleX.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsBgScaleY = ObjectAnimator.ofFloat(optionsBg, "scaleY", (Utils.getScreenHeight(this) * .53f) / optionsBg.getLayoutParams().height);
        optionsBgScaleY.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsBgElev = ObjectAnimator.ofFloat(optionsBg, "elevation", getResources().getDimension(R.dimen.elev_options_post_animate));
        optionsBgElev.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsElev = ObjectAnimator.ofFloat(optionsHouse, "elevation", getResources().getDimension(R.dimen.elev_options_post_animate));
        optionsElev.setInterpolator(midAnimationInterpolator);


        AnimatorSet midSet = new AnimatorSet();
        midSet.setDuration(600);
        midSet.setInterpolator(midAnimationInterpolator);
        midSet.setStartDelay(60);
        midSet.play(optionsBgScaleX).with(optionsBgScaleY).with(optionsBgElev).with(optionsElev);


        Interpolator bulletScaleInterpolator = PathInterpolatorCompat.create(.2f, 0, .2f, 1);

        ObjectAnimator optionsBullet1ScaleX = ObjectAnimator.ofFloat(optionsBullet1, "scaleX", 1);
        optionsBullet1ScaleX.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator optionsBullet1ScaleY = ObjectAnimator.ofFloat(optionsBullet1, "scaleY", 1);
        optionsBullet1ScaleY.setInterpolator(bulletScaleInterpolator);

        ObjectAnimator optionsBullet2ScaleX = ObjectAnimator.ofFloat(optionsBullet2, "scaleX", 1);
        optionsBullet2ScaleX.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator optionsBullet2ScaleY = ObjectAnimator.ofFloat(optionsBullet2, "scaleY", 1);
        optionsBullet2ScaleY.setInterpolator(bulletScaleInterpolator);

        ObjectAnimator optionsBullet3ScaleX = ObjectAnimator.ofFloat(optionsBullet3, "scaleX", 1);
        optionsBullet3ScaleX.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator optionsBullet3ScaleY = ObjectAnimator.ofFloat(optionsBullet3, "scaleY", 1);
        optionsBullet3ScaleY.setInterpolator(bulletScaleInterpolator);

        ObjectAnimator options1Alpha = ObjectAnimator.ofFloat(options1, "alpha", 1);
        options1Alpha.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator options2Alpha = ObjectAnimator.ofFloat(options2, "alpha", 1);
        options2Alpha.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator options3Alpha = ObjectAnimator.ofFloat(options3, "alpha", 1);
        options3Alpha.setInterpolator(bulletScaleInterpolator);

        ObjectAnimator option1Translate = ObjectAnimator.ofFloat(options1, "translationX", -getResources().getDimension(R.dimen.options_pad), 0);
        option1Translate.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator option2Translate = ObjectAnimator.ofFloat(options2, "translationX", -getResources().getDimension(R.dimen.options_pad), 0);
        option2Translate.setInterpolator(bulletScaleInterpolator);
        ObjectAnimator option3Translate = ObjectAnimator.ofFloat(options3, "translationX", -getResources().getDimension(R.dimen.options_pad), 0);
        option3Translate.setInterpolator(bulletScaleInterpolator);


        Integer colorFrom = ((ColorDrawable) splashNextBg.getBackground()).getColor();
        Integer colorTo = getResources().getColor(R.color.splash_yellow);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setInterpolator(midAnimationInterpolator);
        colorAnimation.setDuration(400);
        colorAnimation.setStartDelay(130);
        colorAnimation.setInterpolator(midAnimationInterpolator);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                splashNextBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });


        AnimatorSet bulletsScale = new AnimatorSet();
        bulletsScale.setDuration(400);
        bulletsScale.setStartDelay(150);
        bulletsScale.setInterpolator(bulletScaleInterpolator);
        bulletsScale.play(optionsBullet1ScaleX).with(optionsBullet1ScaleY).with(optionsBullet2ScaleX).with(optionsBullet2ScaleY).with(optionsBullet3ScaleX).with(optionsBullet3ScaleY).with(options1Alpha).with(options2Alpha).with(options3Alpha).with(option1Translate).with(option2Translate).with(option3Translate);


        ObjectAnimator scrollHide = ObjectAnimator.ofFloat(scrollHouse, "alpha", 0);
        scrollHide.setDuration(0);

        finalAnimator = new AnimatorSet();
        finalAnimator.play(midSet).after(initSet).with(scrollHide).with(colorAnimation).with(bulletsScale);
        finalAnimator.start();

        enableClick(true);
    }


    private void animateFromExampleToOptions() {

        state = STATE_OPTIONS_APPEAR_ANIMATING;
        if (finalAnimator != null) {
            finalAnimator.removeAllListeners();
            finalAnimator.cancel();
        }

        getFragmentManager().beginTransaction().setCustomAnimations(0, R.anim.fragment_slide_out).remove(exampleFragment).commit();


        ObjectAnimator toolbarSquezeIn = ObjectAnimator.ofFloat(optionsBg, "scaleX", (Utils.getScreenWidth(this) * .85f) / optionsBg.getLayoutParams().width);
        ;
        toolbarSquezeIn.setInterpolator(initAnimationInterpolator);
        ObjectAnimator toolbarAlpha = ObjectAnimator.ofFloat(toolbar, "alpha", 0);
        toolbarAlpha.setInterpolator(initAnimationInterpolator);
        ObjectAnimator toolbarSlide = ObjectAnimator.ofFloat(toolbar, "translationX", getResources().getDimension(R.dimen.options_pad));
        toolbarSlide.setInterpolator(initAnimationInterpolator);

        Integer toolbarColorFrom = ((ColorDrawable) optionsBg.getBackground()).getColor();
        Integer toolbarColorTo = getResources().getColor(R.color.splash_scroll_content_bg);
        ValueAnimator toolBarColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), toolbarColorFrom, toolbarColorTo);
        toolBarColorAnimation.setInterpolator(initAnimationInterpolator);
        toolBarColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                optionsBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });

        Integer bgColorFrom = ((ColorDrawable) splashNextBg.getBackground()).getColor();
        Integer bgColorTo = getResources().getColor(R.color.splash_yellow);
        ValueAnimator bgColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), bgColorFrom, bgColorTo);
        bgColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                splashNextBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });


        AnimatorSet initSet = new AnimatorSet();

        initSet.setDuration(400);
        initSet.setInterpolator(initAnimationInterpolator);
        initSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                state = STATE_OPTIONS_APPEAR_ANIMATED;
            }
        });
        initSet.play(toolbarSquezeIn).with(toolBarColorAnimation).with(bgColorAnimation).with(toolbarAlpha).with(toolbarSlide);


        ObjectAnimator toolbarTranslateUp = ObjectAnimator.ofFloat(optionsBg, "translationY", 0);
        toolbarTranslateUp.setInterpolator(midAnimationInterpolator);
        ObjectAnimator toolbarSqueze = ObjectAnimator.ofFloat(optionsBg, "scaleY", (Utils.getScreenHeight(this) * .53f) / optionsBg.getLayoutParams().height);
        toolbarSqueze.setInterpolator(midAnimationInterpolator);
        ObjectAnimator toolbarElevation = ObjectAnimator.ofFloat(optionsBg, "elevation", getResources().getDimension(R.dimen.elev_options_post_animate));
        toolbarElevation.setInterpolator(midAnimationInterpolator);
        ObjectAnimator optionsElevation = ObjectAnimator.ofFloat(optionsHouse, "elevation", getResources().getDimension(R.dimen.elev_options_post_animate));
        optionsElevation.setInterpolator(midAnimationInterpolator);

        optionsBullet1.setScaleX(1);
        optionsBullet1.setScaleY(1);

        optionsBullet2.setScaleX(1);
        optionsBullet2.setScaleY(1);

        optionsBullet3.setScaleX(1);
        optionsBullet3.setScaleY(1);

        options1.setAlpha(1);
        options2.setAlpha(1);
        options3.setAlpha(1);

        options1.setTranslationX(0);
        options2.setTranslationX(0);
        options3.setTranslationX(0);


        AnimatorSet midSet = new AnimatorSet();
        midSet.setDuration(600);
        midSet.setInterpolator(midAnimationInterpolator);
        midSet.setStartDelay(60);
        midSet.play(toolbarTranslateUp).with(toolbarElevation).with(optionsElevation).with(toolbarSqueze);


        ObjectAnimator optionsAlpha = ObjectAnimator.ofFloat(optionsHouse, "alpha", 1);
        optionsAlpha.setInterpolator(midAnimationInterpolator);
        optionsHouse.setTranslationY(getResources().getDimension(R.dimen.options_pad) * .5f);
        ObjectAnimator optionsTrasnlation = ObjectAnimator.ofFloat(optionsHouse, "translationY", 0);
        optionsTrasnlation.setInterpolator(midAnimationInterpolator);

        AnimatorSet subMidSet = new AnimatorSet();
        subMidSet.setDuration(500);
        subMidSet.setInterpolator(midAnimationInterpolator);
        subMidSet.setStartDelay(100);
        subMidSet.play(optionsAlpha).with(optionsTrasnlation);


        finalAnimator = new AnimatorSet();
        finalAnimator.play(midSet).after(initSet).with(subMidSet);
        finalAnimator.start();

        enableClick(true);

    }


    private void toFragmentAnimation(int index) {

        state = STATE_EXAMPLE_APPEAR_ANIMATING;

        if (finalAnimator != null) {
            finalAnimator.removeAllListeners();
            finalAnimator.cancel();
        }


        float toolbarHeight = getResources().getDimension(R.dimen.toolbar_size);
        float distanceToTranslate = -(Utils.getScreenHeight(this) - toolbarHeight) * .5f;
        float scaleY = toolbarHeight / scrollHouse.getLayoutParams().height;

        int color = getResources().getColor(R.color.splash_pink);
        int titleResId = R.string.option_a;
        switch (index) {
            case 0:
                color = getResources().getColor(R.color.splash_pink);
                titleResId = R.string.option_a;
                exampleFragment = ScrollViewFragment.newInstance();
                break;
            case 1:
                color = getResources().getColor(R.color.splash_purple);
                titleResId = R.string.option_b;
                exampleFragment = ListViewFragment.newInstance();
                break;
            case 2:
                color = getResources().getColor(R.color.splash_green);
                titleResId = R.string.option_c;
                exampleFragment = RecyclerViewFragment.newInstance();
                break;
        }

        ((TextView) findViewById(R.id.title)).setText(titleResId);

        ObjectAnimator toolbarTranslateUp = ObjectAnimator.ofFloat(optionsBg, "translationY", distanceToTranslate);
        toolbarTranslateUp.setInterpolator(initAnimationInterpolator);
        ObjectAnimator toolbarSqueze = ObjectAnimator.ofFloat(optionsBg, "scaleY", scaleY);
        toolbarSqueze.setInterpolator(initAnimationInterpolator);
        ObjectAnimator toolbarElevation = ObjectAnimator.ofFloat(optionsBg, "elevation", getResources().getDimension(R.dimen.elev_options_pre_animate));
        toolbarElevation.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsHide = ObjectAnimator.ofFloat(optionsHouse, "alpha", 0);
        optionsHide.setInterpolator(initAnimationInterpolator);
        ObjectAnimator optionsTranslateDown = ObjectAnimator.ofFloat(optionsHouse, "translationY", toolbarHeight);
        optionsTranslateDown.setInterpolator(initAnimationInterpolator);

        AnimatorSet initSet = new AnimatorSet();
        initSet.setDuration(400);
        initSet.setInterpolator(initAnimationInterpolator);
        initSet.play(toolbarTranslateUp).with(toolbarElevation).with(toolbarSqueze).with(optionsHide).with(optionsTranslateDown);
        initSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                getFragmentManager().beginTransaction().setCustomAnimations(R.anim.fragment_slide_in, 0).replace(R.id.fragment_container, exampleFragment).commit();
                enableClick(false);
                state = STATE_EXAMPLE_APPEAR_ANIMATED;
            }
        });

        float scaleX = Utils.getScreenWidth(this) / scrollHouse.getLayoutParams().width;
        ObjectAnimator toolbarSquezeOut = ObjectAnimator.ofFloat(optionsBg, "scaleX", scaleX);
        toolbarSquezeOut.setInterpolator(midAnimationInterpolator);
        ObjectAnimator toolbarAlpha = ObjectAnimator.ofFloat(toolbar, "alpha", 1);
        toolbarAlpha.setInterpolator(midAnimationInterpolator);
        ObjectAnimator toolbarSlide = ObjectAnimator.ofFloat(toolbar, "translationX", getResources().getDimension(R.dimen.options_pad), 0);
        toolbarSlide.setInterpolator(midAnimationInterpolator);

        Integer toolbarColorFrom = getResources().getColor(R.color.splash_scroll_content_bg);
        Integer toolbarColorTo = color;
        ValueAnimator toolBarColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), toolbarColorFrom, toolbarColorTo);
        toolBarColorAnimation.setInterpolator(midAnimationInterpolator);
        toolBarColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                optionsBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });

        Integer bgColorFrom = getResources().getColor(R.color.splash_yellow);
        Integer bgColorTo = getResources().getColor(R.color.bg_white);
        ValueAnimator bgColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), bgColorFrom, bgColorTo);
        bgColorAnimation.setInterpolator(midAnimationInterpolator);
        bgColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                splashNextBg.setBackgroundColor((Integer) animator.getAnimatedValue());
            }
        });


        AnimatorSet midSet = new AnimatorSet();
        midSet.setDuration(600);
        midSet.setInterpolator(midAnimationInterpolator);
        midSet.setStartDelay(60);
        midSet.play(toolbarSquezeOut).with(toolBarColorAnimation).with(bgColorAnimation).with(toolbarAlpha).with(toolbarSlide);

        finalAnimator = new AnimatorSet();
        finalAnimator.play(midSet).after(initSet);
        finalAnimator.start();

    }


    @Override
    public void onBackPressed() {

        if (state == STATE_EXAMPLE_APPEAR_ANIMATED) {
            animateFromExampleToOptions();
        } else if (state == STATE_OPTIONS_APPEAR_ANIMATED) {
            animateFromOptionsToSplash();
        } else if (state == STATE_INITIAL_ANIMATED)
            super.onBackPressed();
    }

    private void enableClick(boolean enable) {
        findViewById(R.id.option_1_house).setClickable(enable);
        findViewById(R.id.option_2_house).setClickable(enable);
        findViewById(R.id.option_3_house).setClickable(enable);
    }


}
