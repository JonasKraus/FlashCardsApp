package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerChallengesRanking;
import de.uulm.einhoernchen.flashcardsapp.Model.Challenge;
import de.uulm.einhoernchen.flashcardsapp.Model.Filter.ChallengesFilter;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

public class RecyclerViewAdapterChallengesRanking extends RecyclerView.Adapter<RecyclerViewAdapterChallengesRanking.ViewHolder> implements Filterable {


    private final List<Challenge> challenges;
    private final List<Challenge> filteredList;
    private final List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private final OnFragmentInteractionListenerChallengesRanking mListener;
    private final boolean isUpToDate;
    private final Context context = Globals.getContext();
    private final DbManager db = Globals.getDb();
    private final ProgressBar progressBar = Globals.getProgressBar();
    private ChallengesFilter challengesFilter;

    /**
     * Constructs the recycler views
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param items
     * @param listener
     * @param isUpToDate
     */
    public RecyclerViewAdapterChallengesRanking(List<Challenge> items, OnFragmentInteractionListenerChallengesRanking listener, boolean isUpToDate) {
        this.challenges = items;
        this.filteredList = new ArrayList<>();
        this.mListener = listener;
        this.isUpToDate = isUpToDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_challenge, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = challenges.get(position);
        // holder.mIdView.setText(challenges.get(position).getId()+""); TODO Wird das benötigt?
        holder.textviewName.setText(challenges.get(position).getMessage().getTargetCardDeck().getName());
        holder.textViewdescription.setVisibility(View.VISIBLE);
        holder.textViewdescription.setText(challenges.get(position).getMessage().getContent());

        setViewState(holder, position);

        holders.add(holder);

        setRoundIcon(challenges.get(position), holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onChallengesRankingListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private void setRoundIcon(Challenge challenge, ViewHolder holder) {
        //get first letter of each String item
        final String firstLetter = String.valueOf(challenge.getCardDeck().getName().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        final int color = generator.getColor(challenge.getId()); // TODO
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);
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
     * Returns the filtered list
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-03
     *
     * @return
     */
    public List<Challenge> getList() {

        return this.challenges;
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
        if (challenges != null) {
            return challenges.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {

        if(this.challengesFilter == null) {

            this.challengesFilter = new ChallengesFilter(this, challenges);
        }

        return this.challengesFilter;
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
        public final TextView textviewName;
        public final TextView textViewdescription;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon

        public Challenge mItem;


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
            textviewName = (TextView) view.findViewById(R.id.textview_tag_name);
            textViewdescription = (TextView) view.findViewById(R.id.textview_user_email);

            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + textviewName.getText() + "'";
        }

    }
}
