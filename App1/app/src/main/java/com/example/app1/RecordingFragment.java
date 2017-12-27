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
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class RecordingFragment extends Fragment {
    @Nullable
    private Context context;
    public static ArrayList<ImageModel> data= new ArrayList<>();
    File vdoDirectory = new File(Environment.getExternalStoragePublicDirectory("MagicEye"), "MagicEyeVideos");
    String dthumb = ".dthumb";


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recording_fragment,container,false);
        getActivity().setTitle("Videos");
        context = getContext();
        data.clear();
        try {
            for (File fileEntry : vdoDirectory.listFiles()) {
                ImageModel imageModel = new ImageModel();
                imageModel.setName(fileEntry.getName());
                System.out.println("<<<<<<<<<<<<<<<<filename = "+fileEntry.getName());
                imageModel.setUrl(fileEntry.getPath());
                String extnsn = (fileEntry.getName()).split("\\.")[1];
                if(extnsn.equals("mp4")) {
                    data.add(imageModel);
                }
            }
        }catch (NullPointerException e){
            System.out.println("Video directory empty");
            Toast.makeText(context, "No Files to Show!", Toast.LENGTH_SHORT).show();
        }

        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.vdo_gallery_list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setHasFixedSize(true);

        ImageGalleryAdapter adapter = new ImageGalleryAdapter(context,data);
        adapter.imageGallery = false;
        adapter.videoRecyclerView = recyclerView;
        recyclerView.setAdapter(adapter);

        return v;

    }
    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>file extension" + fileName.substring(i+1));
            return fileName.substring(i+1);
        } else
            return null;
    }


}
