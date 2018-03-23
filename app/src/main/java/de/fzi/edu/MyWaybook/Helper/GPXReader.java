package de.fzi.edu.MyWaybook.Helper;

import android.location.Location;
import android.util.Log;

import de.fzi.edu.MyWaybook.Database.TrackSegment;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Used for reading .gpx Files
 * Provides a static Method that returns an ArrayList of Locations
 * Created by rickert on 15.02.2017.
 */

public class GPXReader {

    public static ArrayList<TrackSegment> readGPX(File file){

        ArrayList<TrackSegment> segmentList= new ArrayList<>();


        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try{
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            //TODO: Locate each Segment first

            NodeList nodelist_trkseg = elementRoot.getElementsByTagName("trkseg");

            for(int i =0; i < nodelist_trkseg.getLength();i++) {



                Node segNode = nodelist_trkseg.item(i);

                NodeList nodeList_trkpt = segNode.getChildNodes();

                TrackSegment gpxSegment = new TrackSegment();

                for (int j = 0; j < nodeList_trkpt.getLength(); j++) {

                    Node node = nodeList_trkpt.item(j);

                    if ( node.getNodeName().equals("trkpt")) {


                        NamedNodeMap attributes = node.getAttributes();

                        String newLatitude = attributes.getNamedItem("lat").getTextContent();
                        Double gpxLat = Double.parseDouble(newLatitude);

                        String newLongtitude = attributes.getNamedItem("lon").getTextContent();
                        Double gpxLon = Double.parseDouble(newLongtitude);

                        Location gpxLocation = new Location("gpxLocation");
                        gpxLocation.setLatitude(gpxLat);
                        gpxLocation.setLongitude(gpxLon);

                        gpxSegment.addPoints(gpxLocation);
                        Log.i("","Parsing Location "+j);

                    }


                }
                segmentList.add(gpxSegment);
                Log.i("","Parsing Segment "+i);

            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return segmentList;
    }
}
