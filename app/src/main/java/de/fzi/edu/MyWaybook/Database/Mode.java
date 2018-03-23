package de.fzi.edu.MyWaybook.Database;

/**
 * Class for Modes. Provides Getter and Setter
 * Created by rickert on 12.01.2017.
 */

public class Mode {

    private String mode_Name;
    private String mode_Icon;
    private int mode_ID;


    public Mode(){

    }

    public Mode(int mode_ID, String mode_Name, String mode_Icon){
        setMode_ID(mode_ID);
        setMode_Icon(mode_Icon);
        setMode_Name(mode_Name);
    }

    public Mode(String mode_Name, String mode_Icon){
        setMode_Icon(mode_Icon);
        setMode_Name(mode_Name);
    }


    public int getMode_ID() {
        return mode_ID;
    }

    public void setMode_ID(int mode_ID) {
        this.mode_ID = mode_ID;
    }


    public String getMode_Icon() {
        return mode_Icon;
    }

    public void setMode_Icon(String mode_Icon) {
        this.mode_Icon = mode_Icon;
    }

    public String getMode_Name() {
        return mode_Name;
    }

    public void setMode_Name(String mode_Name) {
        this.mode_Name = mode_Name;
    }


}
