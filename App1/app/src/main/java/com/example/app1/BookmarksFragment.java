package com.example.app1;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Sibhali on 7/19/2017.
 */

public class BookmarksFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bookmarks, container, false);
        getActivity().setTitle("Bookmarks");
        Log.d("ddc","bookmark fragment");
        return v;
    }
}
