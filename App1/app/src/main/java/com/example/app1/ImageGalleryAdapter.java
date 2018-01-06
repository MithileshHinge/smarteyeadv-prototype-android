package com.example.app1;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bignerdranch.android.multiselector.MultiSelector;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//import android.support.v7.view.ActionMode;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>{

    MultiSelector mMultiSelector = new MultiSelector();

    public View view;
    private Context context;
    public List<BookmarkedDatabaseRow> data;
    public static List<BookmarkedDatabaseRow> bkmrkImages = new ArrayList<>();
    public static List<BookmarkedDatabaseRow> bkmrkVideos = new ArrayList<>();
    public static List<BookmarkedDatabaseRow> deleteItems = new ArrayList<>();
    private final View.OnClickListener onClickListener = new MyOnClickListener();
    private final View.OnLongClickListener onLongClickListener = new MyOnLongClickListner();
    private final ToggleButton.OnCheckedChangeListener onCheckedChangeListener = new MyOnCheckedListener();
    public int classSelector;        //1=image gallery, 2=video gallery, 3=bookmark images, 4=bookmark videos
    public RecyclerView imageRecyclerView;

    public ToggleButton Bkmrk_btn;
    public int nr = 0;
    private static boolean destroyed = false;



    public ImageGalleryAdapter(Context context, List<BookmarkedDatabaseRow> data){
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        view.setOnClickListener(onClickListener);
        view.setOnLongClickListener(onLongClickListener);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            //if(classSelector == 1 || classSelector == 2) {
            img = (ImageView) itemView.findViewById(R.id.item_img);
            Bkmrk_btn = (ToggleButton) itemView.findViewById(R.id.BookMark_btn);
            Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
            System.out.println("..................view holder created.................");
        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println("************************** Case = " + classSelector);
        
        switch (classSelector){

            case 1 :
                Glide.with(context).load(data.get(position).getUrl()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                if(ImageFragment.data.get(position).getBkmrk()) {
                    Bkmrk_btn.setOnCheckedChangeListener(null);
                    Bkmrk_btn.setChecked(true);
                    Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                else {
                    Bkmrk_btn.setOnCheckedChangeListener(null);
                    Bkmrk_btn.setChecked(false);
                    Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                break;
            case 2:
                Glide.with(context).load(Uri.fromFile(new File(data.get(position).getUrl()))).thumbnail(0.5f).override(600, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                if(RecordingFragment.data.get(position).getBkmrk()) {
                    Bkmrk_btn.setOnCheckedChangeListener(null);
                    Bkmrk_btn.setChecked(true);
                    Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                else{
                    Bkmrk_btn.setOnCheckedChangeListener(null);
                    Bkmrk_btn.setChecked(false);
                    Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                break;
            case 3:
                Bkmrk_btn.setChecked(true);
                Glide.with(context).load(bkmrkImages.get(position).getUrl()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                break;
            case 4:
                Bkmrk_btn.setChecked(true);
                Glide.with(context).load(Uri.fromFile(new File(bkmrkVideos.get(position).getUrl()))).thumbnail(0.5f).override(600, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyOnLongClickListner implements View.OnLongClickListener {


        @Override
        public boolean onLongClick(final View view) {
            System.out.println("...............on long click..................");
            mMultiSelector.setSelectable(true);

            view.startActionMode(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                    System.out.println("................item checked madhe ghusla..................");
                    if (checked) {
                        nr++;
                        //mMultiSelector.setNewSelection(i, b);
                        mMultiSelector.setSelected(position,id,checked);
                        deleteItems.add(ImageFragment.data.get(position));
                        System.out.println("/////////////////////////////"+deleteItems);
                    } else {
                        nr--;
                        //mMultiSelector.removeSelection(i);
                        deleteItems.remove(ImageFragment.data.get(position));
                    }
                    mode.setSubtitle(nr + " selected");
                    System.out.println("..................set title     " + nr );
                }
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MainActivity.toolbar.setVisibility(View.GONE);
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.contextual_menu, menu);
                    nr = 0;
                    System.out.println(mMultiSelector);
                    System.out.println("..................on create madhe ghusla.....................");
                    return true;
                }
                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    System.out.println("..................on prepare madhe ghusla.....................");
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_delete:
                            // Delete crimes from model
                            nr = 0;
                            mMultiSelector.clearSelections();
                            System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&"+deleteItems);
                            mode.finish();

                            return true;
                        default:
                            break;
                    }

                    return true;
                }
                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    destroyed = true;
                    System.out.println("..................on destroy madhe ghusla.....................");
                    MainActivity.toolbar.setVisibility(View.VISIBLE);
                    mMultiSelector.clearSelections();
                }
            });

            return true;
        }
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
            try {
                int itemPosition;
                String imgUrl;
                System.out.println("...................onClick madhe ghusla.................");
                switch(classSelector){
                    case 1:
                        itemPosition = imageRecyclerView.getChildLayoutPosition(view);
                        imgUrl = ImageFragment.data.get(itemPosition).getUrl();
                        Log.d("ONCLICK", imgUrl);
                        galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "image/*");
                        break;
                    case 2:
                        itemPosition = imageRecyclerView.getChildLayoutPosition(view);
                        imgUrl = RecordingFragment.data.get(itemPosition).getUrl();
                        System.out.println("ONCLICK" + imgUrl);
                        galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "video/*");
                        break;
                    case 3:
                        itemPosition = imageRecyclerView.getChildLayoutPosition(view);  //change
                        imgUrl = bkmrkImages.get(itemPosition).getUrl();
                        Log.d("ONCLICK", imgUrl);
                        galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "image/*");
                        break;
                    case 4:
                        itemPosition = imageRecyclerView.getChildLayoutPosition(view);  //change
                        imgUrl = bkmrkVideos.get(itemPosition).getUrl();
                        System.out.println("ONCLICK" + imgUrl);
                        galleryIntent.setDataAndType(Uri.fromFile(new File(imgUrl)), "video/*");
                        break;

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
                    if (classSelector == 1) {
                        //things for image gallery when bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        ImageFragment.data.get(itemPosition).setBkmrk(true);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(ImageFragment.data.get(itemPosition));
                        bkmrkImages.add(ImageFragment.data.get(itemPosition));
                    } else if(classSelector == 2) {
                        //things for video gallery when bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        RecordingFragment.data.get(itemPosition).setBkmrk(true);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(RecordingFragment.data.get(itemPosition));
                        bkmrkVideos.add(RecordingFragment.data.get(itemPosition));
                    }
                } else {
                    //bkmrk_status = false;
                    if (classSelector == 1) {
                        //things for image gallery when not bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        ImageFragment.data.get(itemPosition).setBkmrk(false);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(ImageFragment.data.get(itemPosition));
                        bkmrkImages.remove(ImageFragment.data.get(itemPosition));
                    } else if(classSelector == 2) {
                        //things for video gallery when not bookmarked
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        RecordingFragment.data.get(itemPosition).setBkmrk(false);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(RecordingFragment.data.get(itemPosition));
                        bkmrkVideos.remove(RecordingFragment.data.get(itemPosition));
                    } else if(classSelector == 3) {
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        BookmarkedDatabaseRow tempStore = bkmrkImages.get(itemPosition);
                        bkmrkImages.remove(tempStore);
                        tempStore.setBkmrk(false);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(tempStore);
                        //notifyDataSetChanged();
                        ImageGalleryAdapter adapter = new ImageGalleryAdapter(context, ImageGalleryAdapter.bkmrkImages);
                        adapter.classSelector = 3;
                        adapter.imageRecyclerView = imageRecyclerView;
                        imageRecyclerView.setAdapter(adapter);
                    } else if(classSelector == 4){
                        //notifyDataSetChanged();
                        int itemPosition = imageRecyclerView.getChildLayoutPosition((View) compoundButton.getParent());
                        BookmarkedDatabaseRow tempStore = bkmrkVideos.get(itemPosition);
                        bkmrkVideos.remove(tempStore);
                        tempStore.setBkmrk(false);
                        MainActivity.bookmarkedDatabaseHandler.updateRow(tempStore);
                        ImageGalleryAdapter adapter = new ImageGalleryAdapter(context, ImageGalleryAdapter.bkmrkVideos);
                        adapter.classSelector = 4;
                        adapter.imageRecyclerView = imageRecyclerView;
                        imageRecyclerView.setAdapter(adapter);
                    }
                }
            }catch(NullPointerException n){
                System.out.println("...........................null pointer exception catched");
                Toast.makeText(context, "Cannot Bookmark file!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}



