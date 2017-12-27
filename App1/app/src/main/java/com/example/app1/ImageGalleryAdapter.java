package com.example.app1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>{

    private final Context context;
    private ArrayList<ImageModel> data, bkmrkImages, bkmrkVideos;
    private final View.OnClickListener onClickListener = new MyOnClickListener();
    private final ToggleButton.OnCheckedChangeListener onCheckedChangeListener = new MyOnCheckedListener();
    public boolean imageGallery = true;
    public RecyclerView imageRecyclerView, videoRecyclerView;
    //public boolean bkmrk_status = false;
    public View view;
    //File vdoDirectory = new File(Environment.getExternalStoragePublicDirectory("MagicEye"), "MagicEyeVideos");
    public ToggleButton Bkmrk_btn;

    public ImageGalleryAdapter(Context context, ArrayList<ImageModel> data){
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(onClickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (imageGallery) {
            Glide.with(context).load(data.get(position).getUrl()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
            if (data.get(position).getBkmrk()) {
                Bkmrk_btn.setChecked(true);
            }
        }
        else {
            System.out.println("@@@@@@@@@@@@********************************@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
            System.out.println(Uri.fromFile(new File(data.get(position).getUrl())));
            Glide.with(context).load(Uri.fromFile(new File(data.get(position).getUrl()))).thumbnail(0.5f).override(600, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
            if (data.get(position).getBkmrk()) {
                Bkmrk_btn.setChecked(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView img;
        //public ToggleButton Bkmrk_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_img);
            //Bkmrk_btn = (ToggleButton) itemView.findViewById(R.id.BookMark_btn);
            //Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
        }


    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
            try {
                if (imageGallery) {
                    int itemPosition = imageRecyclerView.getChildLayoutPosition(view);
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + itemPosition);
                    String imgUrl = ImageFragment.data.get(itemPosition).getUrl();
                    Log.d("ONCLICK", imgUrl);
                    galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "image/*");
                } else {
                    System.out.println("********************************************************************");
                    int itemPosition = videoRecyclerView.getChildLayoutPosition(view);
                    System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<" + itemPosition);
                    String imgUrl = RecordingFragment.data.get(itemPosition).getUrl();
                    System.out.println("ONCLICK" + imgUrl);
                    galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "video/*");
                }
            }catch (NullPointerException n){
                    System.out.println("...........................null pointer exception catched");
                    Toast.makeText(context, "Cannot Open File!", Toast.LENGTH_SHORT).show();
                }
            galleryIntent.setAction(Intent.ACTION_VIEW);
            context.startActivity(galleryIntent);

        }
    }

    private class MyOnCheckedListener implements ToggleButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            try {
                if (isChecked) {
                    //bkmrk_status = true;
                    if (imageGallery) {
                        //things for image gallery when bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition(compoundButton);
                        System.out.println("<<<<<<<<<<<<<<<<<<<< itemposition = " + itemPosition);
                        ImageFragment.data.get(itemPosition).setBkmrk(true);
                        bkmrkImages.add(ImageFragment.data.get(itemPosition));
                        System.out.println("######################################" + bkmrkImages);

                    } else {
                        //things for video gallery when bookmarked
                        int itemPosition = videoRecyclerView.getChildLayoutPosition(view);
                        RecordingFragment.data.get(itemPosition).setBkmrk(true);
                        bkmrkVideos.add(RecordingFragment.data.get(itemPosition));
                        System.out.println("######################################" + bkmrkVideos);

                    }

                } else {

                    //bkmrk_status = false;
                    if (imageGallery) {
                        //things for image gallery when not bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition(view);
                        ImageFragment.data.get(itemPosition).setBkmrk(false);
                        bkmrkImages.remove(ImageFragment.data.get(itemPosition));
                        System.out.println("######################################" + bkmrkImages);
                    } else {
                        //things for video gallery when not bookmarked
                        int itemPosition = videoRecyclerView.getChildLayoutPosition(view);
                        RecordingFragment.data.get(itemPosition).setBkmrk(false);
                        bkmrkVideos.remove(RecordingFragment.data.get(itemPosition));
                        System.out.println("######################################" + bkmrkVideos);

                    }

                }
            }catch(NullPointerException n){
                System.out.println("...........................null pointer exception catched");
                Toast.makeText(context, "Cannot Bookmark file!", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
