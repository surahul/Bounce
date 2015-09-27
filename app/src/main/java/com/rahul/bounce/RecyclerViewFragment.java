package com.rahul.bounce;


import android.animation.ArgbEvaluator;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.rahul.bounce.library.BounceTouchListener;


public class RecyclerViewFragment extends Fragment {



    public static RecyclerViewFragment newInstance() {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RecyclerViewFragment() {

    }


    private RecyclerView recyclerView;
    private View header;
    private View headerOverlay;
    private BounceTouchListener bounceTouchListener;
    private View main;
    private ImageView sun;

    private int maxSunTranslation;
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private int dayColor = 0Xff23c7c9;
    private int nightColor = 0Xff0b1329;

    int headerHeight;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_recycler_view, container, false);
        maxSunTranslation = -(int)(getResources().getDimension(R.dimen.header_height)*.25f);
        main = root.findViewById(R.id.main);
        sun = (ImageView)root.findViewById(R.id.sun_image_view);
        header = root.findViewById(R.id.header_image_view);
        headerOverlay = root.findViewById(R.id.header_overlay);
        header.setPivotX(Utils.getScreenWidth(getActivity()) * .5f);
        header.setPivotY(getResources().getDimension(R.dimen.header_height));

        headerOverlay.setPivotX(header.getPivotX());
        headerOverlay.setPivotY(header.getPivotY());
        headerOverlay.setAlpha(0);

        headerHeight = (int)getResources().getDimension(R.dimen.header_height);
        recyclerView = (RecyclerView)root.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new DemoAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setPivotX(Utils.getScreenWidth(getActivity()));
        recyclerView.addOnScrollListener(onScrollListener);
        bounceTouchListener = new BounceTouchListener(recyclerView);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    sun.setTranslationY(Math.max(maxSunTranslation,-translation));
                    sun.setAlpha(Math.min(1, translation / getResources().getDimension(R.dimen.options_pad)));
                    bounceTouchListener.setMaxAbsTranslation(-99);
                    header.setRotationX(Math.min((float) Math.pow(translation, .55), 90));
                    header.setTranslationY(translation);
                    headerOverlay.setRotationX(header.getRotationX());
                    headerOverlay.setTranslationY(header.getTranslationY());
                    headerOverlay.setAlpha(Math.min(.4f, translation / -(float)maxSunTranslation));
                    main.setBackgroundColor((int) argbEvaluator.evaluate(Math.min(1, translation / -(float)maxSunTranslation),  dayColor, nightColor));
                }else{
                    main.setBackgroundColor(getResources().getColor(R.color.grey));
                }
            }
        });


        recyclerView.setOnTouchListener(bounceTouchListener);

        return root;
    }

    int scrollY;


    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY+=dy;

            int headerTranslation = (int)(-scrollY*.5f);
            header.setTranslationY(headerTranslation);
            headerOverlay.setTranslationY(headerTranslation);
            headerOverlay.setAlpha(Math.min(1,((float) scrollY) / headerHeight));
        }
    };



    private class DemoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private static final int VIEW_TYPE_PAD = 0;
        private static final int VIEW_TYPE_NORMAL = 1;

        private Integer[] data = new Integer[14];

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
            switch (type){
                case VIEW_TYPE_NORMAL:{
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_sroll_view, parent, false);
                    NormalViewHolder vh = new NormalViewHolder(v);
                    return vh;
                }
                case VIEW_TYPE_PAD:{
                    View v = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.item_pad, parent, false);
                    PadViewHolder vh = new PadViewHolder(v);
                    return vh;
                }
            }

            return null;

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {


        }

        @Override
        public int getItemCount() {
            return data.length;
        }

        @Override
        public int getItemViewType(int position) {
            return position==0?VIEW_TYPE_PAD:VIEW_TYPE_NORMAL;
        }

        private class NormalViewHolder extends RecyclerView.ViewHolder{
            public NormalViewHolder(View itemView) {
                super(itemView);
            }
        }
        private class PadViewHolder extends RecyclerView.ViewHolder{
            public PadViewHolder(View itemView) {
                super(itemView);
            }
        }

    }




}
