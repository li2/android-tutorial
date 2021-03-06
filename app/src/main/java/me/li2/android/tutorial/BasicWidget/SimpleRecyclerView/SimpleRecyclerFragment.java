/*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package me.li2.android.tutorial.BasicWidget.SimpleRecyclerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.li2.android.tutorial.R;

/**
 * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
 * {@link GridLayoutManager}.
 */
public class SimpleRecyclerFragment extends Fragment {

    private static final String TAG = "SimpleRecyclerFragment";
    private static final String ARG_KEY_LAYOUT_TYPE = "arg_key_layoutType";
    private static final String ARG_KEY_DATASET = "arg_key_dataset";

    private static final int SPAN_COUNT = 3;
    private static final int DATASET_COUNT = 21;

    public interface OnItemClickListener {
        void onItemClick(final int position);
    }

    public enum LayoutType {
        GRID,
        LINEAR_HORIZONTAL,
        LINEAR_VERTICAL,
    }

    private RecyclerView mRecyclerView;
    private SimpleRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] mDataset;
    private LayoutType mCurrentLayoutType = LayoutType.LINEAR_VERTICAL;
    private OnItemClickListener mOnItemClickListener;


    public SimpleRecyclerFragment() {

    }

    public static SimpleRecyclerFragment newInstance(String[] dataset, LayoutType layoutType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_KEY_LAYOUT_TYPE, layoutType);
        args.putSerializable(ARG_KEY_DATASET, dataset);

        SimpleRecyclerFragment fragment = new SimpleRecyclerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleRecyclerFragment);
        int layoutType = a.getInt(R.styleable.SimpleRecyclerFragment_layout_type, LayoutType.LINEAR_VERTICAL.ordinal());
        mCurrentLayoutType = LayoutType.values()[layoutType];

        a.recycle();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Bundle args = getArguments();
        if (args != null) {
            mCurrentLayoutType = (LayoutType) args.getSerializable(ARG_KEY_LAYOUT_TYPE);
            mDataset = (String[]) args.getSerializable(ARG_KEY_DATASET);
        }

        if (savedInstanceState != null) {
            // Restore saved layout manager type.
            mCurrentLayoutType = (LayoutType) savedInstanceState.getSerializable(ARG_KEY_LAYOUT_TYPE);
        }

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
        if (mDataset == null) {
            initDataset();
        }

        mAdapter = new SimpleRecyclerAdapter(mDataset);
        setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_simple_recycler_view, container, false);
        rootView.setTag(TAG);
        rootView.setBackgroundColor(ContextCompat.getColor(rootView.getContext(), android.R.color.background_light));

        // BEGIN_INCLUDE(initializeRecyclerView)
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        setLayout(mCurrentLayoutType);

        // Set SimpleRecyclerAdapter as the adapter for RecyclerView.
        mRecyclerView.setAdapter(mAdapter);
        // END_INCLUDE(initializeRecyclerView)

        return rootView;
    }

    /**
     * Set RecyclerView's LayoutManager to the one given.
     *
     * @param layoutType Type of layout manager to switch to.
     */
    public void setLayout(LayoutType layoutType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (mRecyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutType) {
            case GRID:
                mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                mCurrentLayoutType = LayoutType.GRID;
                break;

            case LINEAR_HORIZONTAL:
            case LINEAR_VERTICAL:
                LinearLayoutManager lm = new LinearLayoutManager(getActivity());
                int orientation = (layoutType == LayoutType.LINEAR_HORIZONTAL)
                        ? LinearLayoutManager.HORIZONTAL
                        : LinearLayoutManager.VERTICAL;
                lm.setOrientation(orientation);
                mLayoutManager = lm;
                mCurrentLayoutType = layoutType;
                break;

            default:
                mLayoutManager = new LinearLayoutManager(getActivity());
                mCurrentLayoutType = LayoutType.LINEAR_VERTICAL;
        }

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save currently selected layout manager.
        savedInstanceState.putSerializable(ARG_KEY_LAYOUT_TYPE, mCurrentLayoutType);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener(listener);
        }
    }

    public void setDataset(String[] dataset) {
        mDataset = dataset;
        if (mAdapter != null) {
            mAdapter.setDataSet(dataset);
        }
    }


    public void show() {
        if (getActivity() != null && isAdded() && !isVisible()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .show(this)
                    .commit();
        }
    }

    public void hide() {
        if (getActivity() != null && isAdded() && isVisible()) {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .hide(this)
                    .commit();
        }
    }

    /**
     * Generates Strings for RecyclerView's adapter. This data would usually come
     * from a local content provider or remote server.
     */
    private void initDataset() {
        mDataset = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataset[i] = "Element #" + i;
        }
    }
}
