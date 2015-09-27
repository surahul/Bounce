package com.rahul.bounce;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.rahul.bounce.library.BounceTouchListener;


public class ScrollViewFragment extends Fragment {



    public static ScrollViewFragment newInstance() {
        ScrollViewFragment fragment = new ScrollViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ScrollViewFragment() {

    }


    private ScrollView scrollView;
    private View header;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_scroll_view, container, false);

        header = root.findViewById(R.id.header_image_view);
        scrollView = (ScrollView)root.findViewById(R.id.scroll_view);
        BounceTouchListener bounceTouchListener = new BounceTouchListener(scrollView,R.id.content);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    float scale = ((2*translation) / header.getMeasuredHeight()) + 1;
                    header.setScaleX(scale);
                    header.setScaleY(scale);
                }
            }
        });


        scrollView.setOnTouchListener(bounceTouchListener);

        return root;
    }


}
