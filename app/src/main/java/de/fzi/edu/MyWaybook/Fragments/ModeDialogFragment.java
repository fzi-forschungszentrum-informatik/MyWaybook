package de.fzi.edu.MyWaybook.Fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import de.fzi.edu.MyWaybook.Database.ModeSource;
import de.fzi.edu.MyWaybook.Helper.ImageAdapter;

import de.fzi.edu.MyWaybook.Database.Mode;
import de.fzi.edu.MyWaybook.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for DialogFragments for Modes
 */
public class ModeDialogFragment extends DialogFragment implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private GridView gridview;
    private ModeSource modeSource;
    private List<Mode> modes;
    private ArrayList<ImageView> imagebuttons;


    private OnFragmentInteractionListener mListener;

    public ModeDialogFragment() {


    }

    /**
     * Getting the Images for the gridview
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modeSource = new ModeSource(getActivity());
        modeSource.open();
        modes = modeSource.getAllModes();
        modeSource.close();
        imagebuttons = new ArrayList<ImageView>();
        for (int i = 0; i < modes.size(); i++) {
            int iconID = getResources().getIdentifier(modes.get(i).getMode_Icon(), "drawable", getActivity().getPackageName());
            ImageView ib = new ImageView(getActivity());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ib.setImageDrawable(getResources().getDrawable(iconID, null));
            }
            ib.setId(iconID);
            imagebuttons.add(ib);
        }
    }

    /**
     * Setting layoutparameters
     */

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT;
        int dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;

        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * Filling the gridview with images
     *
     * @param inflater           inflater used to inflate any views
     * @param container          used to generate LayoutParams for the view
     * @param savedInstanceState null
     * @return returns the filled view
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mode, container, false);
        gridview = (GridView) view.findViewById(R.id.gridView2);
        ImageAdapter ibAdapter = new ImageAdapter(getActivity(), imagebuttons);
        gridview.setAdapter(ibAdapter);
        gridview.setOnItemClickListener(this);
        gridview.setOnItemLongClickListener(this);
        Toast.makeText(getActivity(), R.string.toast_Mode,
                Toast.LENGTH_SHORT).show();
        return view;
    }

    /**
     * Instancing the FragmentInteractionListener
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ModeDialogFragment.OnFragmentInteractionListener) {
            mListener = (ModeDialogFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * Setting the Listener to null when the Fragment is closed
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Extracts the Id of the clicked Image and closes the Fragment
     *
     * @param parent   the view where the click happened
     * @param view     view that was clicked
     * @param position position of the view
     * @param id       id of the view
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onFragmentInteraction2(imagebuttons.get(position).getId(), modes.get(position).getMode_ID());
        dismiss();
    }

    /**
     * Used for getting Information about an Image
     *
     * @param parent   the view where the click happened
     * @param view     view that was clicked
     * @param position position of the view
     * @param id       id of the item
     * @return true
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), modes.get(position).getMode_Name(), Toast.LENGTH_SHORT).show();
        return true;
    }

    /**
     * Giving the using class of the Interface Information about the clicked image.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction2(int mode_Icon_ID, int mode_ID);
    }

}
