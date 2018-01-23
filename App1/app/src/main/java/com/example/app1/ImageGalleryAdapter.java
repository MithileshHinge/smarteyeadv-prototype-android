package com.example.app1;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder>{

    ActionMode mActionMode = null;
    public View view;
    private Context context;
    public List<BookmarkedDatabaseRow> data;
    public static List<BookmarkedDatabaseRow> bkmrkImages = new ArrayList<>();
    public static List<BookmarkedDatabaseRow> bkmrkVideos = new ArrayList<>();
    public static List<BookmarkedDatabaseRow> deleteItems = new ArrayList<>();
    private final View.OnClickListener onClickListener = new MyOnClickListener();
    private final View.OnLongClickListener onLongClickListener = new MyOnLongClickListner();
    private final ToggleButton.OnCheckedChangeListener onCheckedChangeListener = new MyOnCheckedListener();
    public int classSelector;        //1=image gallery, 2=video gallery, 3=bookmarked images, 4=bookmarked videos
    public RecyclerView imageRecyclerView;




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
        private ToggleButton Bkmrk_btn;
        public ViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.item_img);
            Bkmrk_btn = (ToggleButton) itemView.findViewById(R.id.BookMark_btn);
            if(mActionMode == null) {
                Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                Bkmrk_btn.setVisibility(View.VISIBLE);
                System.out.println("..................view holder created @@@.................");
            }else{
                Bkmrk_btn.setOnCheckedChangeListener(null);
                Bkmrk_btn.setVisibility(View.GONE);
                System.out.println("..................view holder created.................");
            }

        }
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println("************************** Case = " + classSelector);

        switch (classSelector){

            case 1 :
                Glide.with(context).load(data.get(position).getUrl()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                System.out.println("************************** case 1 madhe ghusla ");
                if(ImageFragment.data.get(position).getBkmrk()) {
                    holder.Bkmrk_btn.setOnCheckedChangeListener(null);
                    holder.Bkmrk_btn.setChecked(true);
                    System.out.println("**************************position bookmarked= " + position);
                    holder.Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                else {
                    holder.Bkmrk_btn.setOnCheckedChangeListener(null);
                    holder.Bkmrk_btn.setChecked(false);
                    holder.Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }

                if(data.get(position).getStatus()) {
                    System.out.println("background color set");
                    holder.img.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                    //holder.img.setBackgroundColor(Color.CYAN);
                }
                else
                    holder.img.setColorFilter(null);
                break;
            case 2:
                Glide.with(context).load(Uri.fromFile(new File(data.get(position).getUrl()))).thumbnail(0.5f).override(600, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                if(RecordingFragment.data.get(position).getBkmrk()) {
                    holder.Bkmrk_btn.setOnCheckedChangeListener(null);
                    holder.Bkmrk_btn.setChecked(true);
                    holder.Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }
                else{
                    holder.Bkmrk_btn.setOnCheckedChangeListener(null);
                    holder.Bkmrk_btn.setChecked(false);
                    holder.Bkmrk_btn.setOnCheckedChangeListener(onCheckedChangeListener);
                }

                if(data.get(position).getStatus()) {
                    System.out.println("background color set");
                    //holder.img.setBackgroundColor(0x00FFFF);
                    holder.img.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }
                else
                    holder.img.setColorFilter(null);
                break;
            case 3:
                holder.Bkmrk_btn.setChecked(true);
                Glide.with(context).load(bkmrkImages.get(position).getUrl()).thumbnail(0.5f).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                if(data.get(position).getStatus()) {
                    System.out.println("background color set");
                    //holder.img.setBackgroundColor(0x00FFFF);
                    holder.img.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }
                else
                    holder.img.setColorFilter(null);
                break;
            case 4:
                holder.Bkmrk_btn.setChecked(true);
                Glide.with(context).load(Uri.fromFile(new File(bkmrkVideos.get(position).getUrl()))).thumbnail(0.5f).override(600, 200).crossFade().diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.img);
                if(data.get(position).getStatus()) {
                    System.out.println("background color set");
                    //holder.img.setBackgroundColor(0x00FFFF);
                    holder.img.setColorFilter(Color.CYAN, PorterDuff.Mode.MULTIPLY);
                }
                else
                    holder.img.setColorFilter(null);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (mActionMode == null ? 0 : 1);
    }


    public class MyOnLongClickListner implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {
            System.out.println("...............on long click..................");
            int position = imageRecyclerView.getChildLayoutPosition(view);
            if (mActionMode == null)
                mActionMode = view.startActionMode(mActionModeCallback);
            multi_select(position);
            notifyDataSetChanged();
            return true;
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MainActivity.toolbar.setVisibility(View.GONE);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_menu, menu);
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
                    AlertDialog diaBox = AskOption();
                    diaBox.show();
                    System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&"+deleteItems);
                    //mode.finish();
                    return true;
                default:
                    break;
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mode.finish();
            mActionMode = null;
            int position;
            for(position=0; position<deleteItems.size(); position++) {
                deleteItems.get(position).setStatus(false);
            }
            deleteItems.clear();
            System.out.println("..................on destroy madhe ghusla.....................");
            MainActivity.toolbar.setVisibility(View.VISIBLE);
            ImageGalleryAdapter adapter2 = new ImageGalleryAdapter(context, data);
            adapter2.classSelector = classSelector;
            adapter2.imageRecyclerView = imageRecyclerView;
            imageRecyclerView.setAdapter(adapter2);
        }

    };

    public void multi_select(int position) {
        System.out.println("..........multiselect madhe ghusla.............");
        if (mActionMode != null) {
            if (deleteItems.contains(data.get(position))) {
                deleteItems.remove(data.get(position));
                data.get(position).setStatus(false);
            }else {
                deleteItems.add(data.get(position));
                data.get(position).setStatus(true);
            }
            if (deleteItems.size() > 0)
                mActionMode.setTitle("" + deleteItems.size() + " items selected");
            else {
                mActionMode.setTitle("");
                mActionModeCallback.onDestroyActionMode(mActionMode);
                deleteItems.clear();
                MainActivity.toolbar.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }

    public class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            if(mActionMode == null) {
                Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
                try {
                    int itemPosition;
                    String imgUrl;
                    System.out.println("...................onClick madhe ghusla.................");
                    switch (classSelector) {
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
                } catch (NullPointerException n) {
                    Toast.makeText(context, "Cannot Open File!", Toast.LENGTH_SHORT).show();
                }
                galleryIntent.setAction(Intent.ACTION_VIEW);
                context.startActivity(galleryIntent);
            }else{
                int itemPosition = imageRecyclerView.getChildLayoutPosition(view);
                data.get(itemPosition).setStatus(true);
                multi_select(itemPosition);
                notifyDataSetChanged();
            }


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
                        ImageGalleryAdapter adapter = new ImageGalleryAdapter(context, ImageGalleryAdapter.bkmrkImages);
                        adapter.classSelector = 3;
                        adapter.imageRecyclerView = imageRecyclerView;
                        imageRecyclerView.setAdapter(adapter);
                    } else if(classSelector == 4){
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
                Toast.makeText(context, "Cannot Bookmark file!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private AlertDialog AskOption()
    {
        final AlertDialog myQuittingDialogBox =new AlertDialog.Builder(context)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Delete "+deleteItems.size()+" items ?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        //your deleting code
                        int position;
                        System.out.println(deleteItems.size()+" items present");
                        for(position=0; position<deleteItems.size(); position++) {
                            File file = new File(deleteItems.get(position).getUrl());
                            //activity.deleteFile(deleteItems.get(position).getUrl());
                            file.delete();

                            data.remove(deleteItems.get(position));
                            bkmrkImages.remove(deleteItems.get(position));
                            bkmrkVideos.remove(deleteItems.get(position));
                            ImageFragment.data.remove(deleteItems.get(position));
                            RecordingFragment.data.remove(deleteItems.get(position));
                            System.out.println("...........file deleted...........");
                        }
                        mActionModeCallback.onDestroyActionMode(mActionMode);
                        notifyDataSetChanged();
                        deleteItems.clear();
                        dialog.dismiss();
                    }

                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mActionModeCallback.onDestroyActionMode(mActionMode);
                        deleteItems.clear();
                        dialog.dismiss();
                    }
                })
                .create();
        return myQuittingDialogBox;

    }
}



