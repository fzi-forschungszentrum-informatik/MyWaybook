package de.fzi.edu.MyWaybook.Helper;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;



import java.util.ArrayList;

/**
 * Created by rickert on 23.01.2017.
 * Used by Dialog Fragments to populate the view with Images
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private Integer[] imageIds;


    public ImageAdapter(Context context, ArrayList<ImageView> imageViews) {


        this.context = context;
        this.imageIds = extractIDs(imageViews);
    }

    /**
     * number of images
     * @return length of the array
     */
    public int getCount() {
        return imageIds.length;
    }


    public Object getItem(int position) {
        return null;
    }


    public long getItemId(int position) {
        return 0;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            //imageButton.setBackground(Drawable.createFromPath("/drawable/circle.png"));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(6, 6, 6, 6);


        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(imageIds[position]);


        return imageView;
    }


    private Integer[] extractIDs(ArrayList<ImageView> imageViews){

        Integer[] ids = new Integer[imageViews.size()];

        for(int i =0; i<imageViews.size(); i++){
            int id = imageViews.get(i).getId();
            ids[i] = id;

        }

        return ids;

    }

}
