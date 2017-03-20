package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteHeartbeat;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class FragmentHome extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textViewHeartbeat;
    private boolean isAlive = false;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private CardView cardViewHeartBeat;
    private ImageView heartbeatImage;
    private TextView toolbarTextViewTitle;
    private CollapsingToolbarLayout collapsingToolbar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public FragmentHome() {
        // Required empty public constructor

        // TODO check with broadcast isAlive = ProcessConnectivity.isOk(getContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        textViewHeartbeat = (TextView) view.findViewById(R.id.textview_heartbeat);
        cardViewHeartBeat = (CardView) view.findViewById(R.id.heartbeat_cardView);
        heartbeatImage = (ImageView) view.findViewById(R.id.heartbeat_image);

        toolbarTextViewTitle = (TextView ) view.getRootView().findViewById(R.id.toolbar_text_view_title);
        collapsingToolbar = (CollapsingToolbarLayout) view.getRootView().findViewById(R.id.collapsing_toolbar);

        cardViewHeartBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setHeartBeatCard();
            }
        });

        setHeartBeatCard();

        // Inflate the layout for this fragment
        return view;
    }



    /**
     * Sets the cardview to display if a connection to the service is established
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-03-17
     *
     */
    private void setHeartBeatCard() {

        Globals.getProgressBar().setVisibility(View.VISIBLE);
        AsyncGetRemoteHeartbeat heartbeat = new AsyncGetRemoteHeartbeat(new AsyncGetRemoteHeartbeat.AsyncResponseHeartbeat() {

            @Override
            public void processFinish(Boolean isAlive) {

                Globals.getProgressBar().setVisibility(View.GONE);
                cardViewHeartBeat.setBackgroundColor(isAlive ? Globals.getContext().getResources().getColor(R.color.green_0) :Globals.getContext().getResources().getColor(R.color.red_0));
                textViewHeartbeat.setText(isAlive ? R.string.label_established : R.string.label_unestablished);
                heartbeatImage.setImageDrawable(isAlive ? Globals.getContext().getResources().getDrawable(R.drawable.ic_offline_pin) :Globals.getContext().getResources().getDrawable(R.drawable.ic_close));

            }
        });

        heartbeat.execute();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
