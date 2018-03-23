package de.fzi.edu.MyWaybook.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.fzi.edu.MyWaybook.Database.PurposeSource;
import de.fzi.edu.MyWaybook.Database.Track;
import de.fzi.edu.MyWaybook.Database.TrackSegmentSource;
import de.fzi.edu.MyWaybook.R;


import java.util.ArrayList;

/**
 * Created by rickert on 20.02.2017.
 * derivated from https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
 * Used by LastTracks Activity to populate the ListView
 */

public class TrackAdapter extends ArrayAdapter<Track> {
    private PurposeSource purposeSource;
    private TrackSegmentSource segmentSource;
    private Context context;

    /**
     * Public constructor for the TrackAdapter.
     * @param context context from the used Activity
     * @param tracks List with the Tracks to use
     * @param purposeSource created in TrackHandler class
     * @param segmentSource created in TrackHandler class
     */
    public TrackAdapter(Context context, ArrayList<Track> tracks, PurposeSource purposeSource, TrackSegmentSource segmentSource) {
        super(context, 0,0, tracks);
        this.purposeSource = purposeSource;
        this.segmentSource = segmentSource;
        this.context = context;
    }

    /**
     * Default Method for ArrayAdapter. Used
     * @param position
     * @param convertView
     * @param parent
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Track track = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.tracks_list_view, parent, false);
        }
        // Lookup view for data population
        ImageView tvIcon = (ImageView) convertView.findViewById(R.id.lv_track_id);
        TextView tvHome = (TextView) convertView.findViewById(R.id.lv_track_date);
        // Populate the data into the template view using the data object

        int icon_ID = context.getResources().getIdentifier(purposeSource.getPurposeIcon(track.getPurpose()), "drawable", context.getPackageName());
        tvIcon.setImageResource(icon_ID);
        tvHome.setText(segmentSource.getDateOfTrack(track.getTrack_ID()));


        // Return the completed view to render on screen
        return convertView;
    }



}

