package com.example.app1;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecordingFragment extends Fragment {
    @Nullable
    private Context context;
    public static List<BookmarkedDatabaseRow> data= new ArrayList<>();
    public static List<File> checkEntry = new ArrayList<>();
    public static File vdoDirectory = new File(Environment.getExternalStoragePublicDirectory("MagicEye"), "MagicEyeVideos");

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_fragment, container, false);
        getActivity().setTitle("Videos");
        context = getContext();

        try {
            for (File fileEntry : vdoDirectory.listFiles()) {
                System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
                if(checkEntry.contains(fileEntry)) {
                    System.out.println("continued");
                    continue;
                }

                String extension = (fileEntry.getName()).split("\\.")[1];
                checkEntry.add(fileEntry);
                if(extension.equals("mp4")) {
                    BookmarkedDatabaseRow bookmarkedDatabaseRow = MainActivity.bookmarkedDatabaseHandler.getRowFromUrl(fileEntry.getPath());

                    if (bookmarkedDatabaseRow == null) {
                        bookmarkedDatabaseRow = new BookmarkedDatabaseRow();
                        bookmarkedDatabaseRow.setUrl(fileEntry.getPath());
                        bookmarkedDatabaseRow.setBkmrk(false);
                        MainActivity.bookmarkedDatabaseHandler.addRow(bookmarkedDatabaseRow);
                        bookmarkedDatabaseRow = MainActivity.bookmarkedDatabaseHandler.getRowFromUrl(fileEntry.getPath());
                    }

                    data.add(bookmarkedDatabaseRow);

                    if (bookmarkedDatabaseRow.getBkmrk())
                        ImageGalleryAdapter.bkmrkVideos.add(bookmarkedDatabaseRow);
                }
            }
        }catch (NullPointerException e){
            System.out.println("Video directory empty");
            Toast.makeText(context, "No Files to Show!", Toast.LENGTH_SHORT).show();
        }

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.gallery_list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setHasFixedSize(true);

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(context,data);
        adapter.classSelector = 2;
        adapter.imageRecyclerView = recyclerView;
        recyclerView.setAdapter(adapter);

        return v;

    }

}
