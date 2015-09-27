package com.rahul.bounce;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rahul.bounce.library.BounceTouchListener;

import java.util.Dictionary;
import java.util.Hashtable;


public class ListViewFragment extends Fragment implements AbsListView.OnScrollListener{



    public static ListViewFragment newInstance() {
        ListViewFragment fragment = new ListViewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ListViewFragment() {

    }


    private ListView listView;
    private View header;
    private View headerOverlay;
    private BounceTouchListener bounceTouchListener;

    float pivotY1,pivotY2;
    int headerHeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_list_view, container, false);

        header = root.findViewById(R.id.header_image_view);
        headerHeight = (int)getResources().getDimension(R.dimen.header_height);
        headerOverlay = root.findViewById(R.id.header_overlay);
        headerOverlay.setAlpha(0);
        listView = (ListView)root.findViewById(R.id.list_view);
        listView.setAdapter(new DemoAdapter());
        listView.setPivotX(Utils.getScreenWidth(getActivity()) * .5f);
        pivotY1 = getResources().getDimension(R.dimen.header_height);
        pivotY2 = (Utils.getScreenHeight(getActivity())-getResources().getDimension(R.dimen.toolbar_size))*.5f;
        listView.setDivider(null);
        listView.setDividerHeight(0);
        listView.setOnScrollListener(this);
        bounceTouchListener = new BounceTouchListener(listView);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    bounceTouchListener.setMaxAbsTranslation(-99);
                    listView.setPivotY(pivotY1);
                    float scale = ((2 * translation) / header.getMeasuredHeight()) + 1;
                    header.setScaleY(scale);
                    listView.setScaleY(scale);
                }else{
                    bounceTouchListener.setMaxAbsTranslation((int) (pivotY2 * .33f));
                    listView.setPivotY(pivotY2);
                    float scale = ((2 * translation) / listView.getMeasuredHeight()) + 1;
                    listView.setScaleY((float)Math.pow(scale,.5f));
                }
            }
        });


        listView.setOnTouchListener(bounceTouchListener);

        return root;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int headerTranslation = (int)(-getScroll()*.5f);
        header.setTranslationY(headerTranslation);
        headerOverlay.setTranslationY(headerTranslation);
        headerOverlay.setAlpha(Math.min(1,((float) getScroll()) / headerHeight));
    }

    private class DemoAdapter extends ArrayAdapter<Integer>{

        public DemoAdapter() {
            super(getActivity(), R.layout.item_sroll_view,new Integer[15]);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                rowView = inflater.inflate(R.layout.item_sroll_view, null);
            }
            View pad = rowView.findViewById(R.id.pad);
            pad.setVisibility(position==0?View.VISIBLE:View.GONE);
            return rowView;
        }
    }

    private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<>();

    private int getScroll() {
        View c = listView.getChildAt(0);
        if(c==null)
            return 0;
        int scrollY = -c.getTop();
        listViewItemHeights.put(listView.getFirstVisiblePosition(), c.getHeight());
        for (int i = 0; i < listView.getFirstVisiblePosition(); ++i) {
            if (listViewItemHeights.get(i) != null)
                scrollY += listViewItemHeights.get(i);
        }
        return scrollY;
    }




}
