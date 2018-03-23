package de.fzi.edu.MyWaybook.Helper;

import android.location.Location;
import android.util.Log;


import de.fzi.edu.MyWaybook.Database.TrackSegment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Used for writing .gpx Files
 * Provides a static Method that uses the current ArrayList of Locations
 * Created by rickert on 14.02.2017.
 * derivated from https://gist.github.com/carlosefonseca/6143182
 */

public class GPXWriter {

    private static final String TAG = GPXWriter.class.getName();

    public static void writePath(File file, String n, ArrayList<TrackSegment> trackSegments) {

        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n<gpx xmlns=\"http://www.topografix.com/GPXWriter/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPXWriter/1/1 http://www.topografix.com/GPXWriter/1/1/gpx.xsd\">\n<metadata></metadata>\n<trk>\n";
        String name = " <name>" + n + "</name>\n";
        String desc = "<desc></desc>\n";

        String segmentHeader = "";
        String segments = "";
        String segmentFooter ="";
        String segmentComplete ="";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for(TrackSegment s : trackSegments) {
             segmentHeader = "  <trkseg>\n";


            ArrayList<Location> points = s.getSegmentPoints();
            for (Location l : points) {
                segments += "       <trkpt lat=\"" + l.getLatitude() + "\" lon=\"" + l.getLongitude() + "\">\n          <time>" + df.format(new Date(l.getTime())) + "</time>\n       </trkpt>\n";
            }
            segmentFooter = "   </trkseg>\n";

            segmentComplete += segmentHeader+segments+segmentFooter;
            segments ="";
        }

        String footer = "</trk> \n <extensions></extensions>\n</gpx>";

        try {
            FileWriter writer = new FileWriter(file, false);
            writer.append(header);
            writer.append(name);
            writer.append(desc);
            writer.append(segmentComplete);
            writer.append(footer);
            writer.flush();
            writer.close();

            Log.i(TAG, "Saved " + trackSegments.size() + " Segments.");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, "Error Writing Path", e);
        }
    }
}

