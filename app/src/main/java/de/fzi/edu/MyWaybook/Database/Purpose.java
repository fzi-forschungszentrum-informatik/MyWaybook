package de.fzi.edu.MyWaybook.Database;

/**
 * Class for Purposes. Provides Getter and Setter
 * Created by rickert on 12.01.2017.
 */

public class Purpose {

    private int purpose_ID;
    private String purpose_Name;
    private String purpose_Icon;

    public Purpose(){

    }

    public Purpose(int purpose_ID, String purpose_Name, String purpose_Icon){
        setPurpose_Icon(purpose_Icon);
        setPurpose_ID(purpose_ID);
        setPurpose_Name(purpose_Name);
    }

    public Purpose(String purpose_Name, String purpose_Icon){
        setPurpose_Icon(purpose_Icon);
        setPurpose_Name(purpose_Name);
    }

    public int getPurpose_ID() {
        return purpose_ID;
    }

    public void setPurpose_ID(int purpose_ID) {
        this.purpose_ID = purpose_ID;
    }

    public String getPurpose_Name() {
        return purpose_Name;
    }

    public void setPurpose_Name(String purpose_Name) {
        this.purpose_Name = purpose_Name;
    }

    public String getPurpose_Icon() {
        return purpose_Icon;
    }

    public void setPurpose_Icon(String purpose_Icon) {
        this.purpose_Icon = purpose_Icon;
    }
}
