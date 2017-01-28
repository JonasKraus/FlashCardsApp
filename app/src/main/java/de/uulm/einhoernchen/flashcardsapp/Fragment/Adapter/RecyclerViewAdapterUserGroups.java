package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.DELETE.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserGroup;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterUserGroups extends RecyclerView.Adapter<RecyclerViewAdapterUserGroups.ViewHolder> {


    private final List<UserGroup> groups;
    private final List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private final OnFragmentInteractionListenerUserGroup mListener;
    private final boolean isUpToDate;
    private final Context context = Globals.getContext();
    private final DbManager db = Globals.getDb();
    private final ProgressBar progressBar = Globals.getProgressBar();

    /**
     * Constructs the recycler views
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param items
     * @param listener
     * @param isUpToDate
     */
    public RecyclerViewAdapterUserGroups(List<UserGroup> items, OnFragmentInteractionListenerUserGroup listener, boolean isUpToDate) {
        groups = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_usergroup, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = groups.get(position);
        // holder.mIdView.setText(groups.get(position).getId()+""); TODO Wird das benötigt?
        holder.mContentView.setText(groups.get(position).getName());
        holder.mAuthorView.setVisibility(View.VISIBLE);
        holder.mAuthorView.setText(groups.get(position).getDescription());

        setViewState(holder, position);

        holders.add(holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserGroupListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    /**
     * Sets the visibilities and states of the gui elements
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     * @param holder
     * @param position
     */
    private void setViewState(ViewHolder holder, int position) {


        /**
         * Check if the data is from the server or from the local db
         */
        if (!isUpToDate) {

            holder.mLocalView.setVisibility(View.INVISIBLE);
        } else {

            holder.mLocalView.setVisibility(View.VISIBLE);
        }

    }



    /**
     * Counting how many items are in the view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @return
     */
    @Override
    public int getItemCount() {
        if (groups != null) {
            return groups.size();
        }
        return 0;
    }




    /**
     * Gets the view elements and sets them to the holder
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mHintView;
        public final TextView mAuthorView;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon

        public UserGroup mItem;


        /**
         * Gets the View elements by their ids
         *
         * @author Jonas Kraus jonas.kraus@uni-ulm.de
         *
         * @param view
         */
        public ViewHolder(View view) {
            super(view);

            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mHintView = (TextView) view.findViewById(R.id.hint);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
