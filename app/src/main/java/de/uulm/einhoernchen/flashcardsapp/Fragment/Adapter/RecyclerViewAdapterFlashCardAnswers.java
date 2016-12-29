package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncSetRemoteFlashCardRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.DummyContent.DummyItem;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterFlashCardAnswers extends RecyclerView.Adapter<RecyclerViewAdapterFlashCardAnswers.ViewHolder> {

    private final List<Answer> answers;
    private final OnFragmentInteractionListenerAnswer mListener;
    private final boolean isUpToDate;
    private final Context context;
    private final DbManager db;

    public RecyclerViewAdapterFlashCardAnswers(DbManager db, List<Answer> items, OnFragmentInteractionListenerAnswer listener, boolean isUpToDate, Context context) {
        answers = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
        this.context = context;
        this.db = db;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_answer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = answers.get(position);
        // holder.mIdView.setText(answers.get(position).getId()+""); TODO Wird das benötigt?
        holder.mContentView.setText(answers.get(position).getAnswerText());
        holder.mAuthorView.setVisibility(View.VISIBLE);
        holder.mAuthorView.setText(answers.get(position).getAuthor().getName());
        // holder.mGroupRatingView.setVisibility(View.INVISIBLE);
        holder.mCardRatingView.setVisibility(View.VISIBLE);
        holder.mCardRatingView.setText(answers.get(position).getRating() + "");
        holder.mDateView.setVisibility(View.VISIBLE);
        holder.mDateView.setText(answers.get(position).getLastUpdatedString());
        holder.misCorrectView.setVisibility(View.VISIBLE);
        holder.mDownvote.setVisibility(View.VISIBLE);
        holder.mUpvote.setVisibility(View.VISIBLE);
        final long answerId = answers.get(position).getId();

        if (answers.get(position).isCorrect()) {
            holder.misCorrectView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
        } else {
            holder.misCorrectView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close));
        }

        /**
         * Check if the data is from the server or from the local db
         */
        if (!isUpToDate) {

            holder.mLocalView.setVisibility(View.INVISIBLE);
        } else {

            holder.mLocalView.setVisibility(View.VISIBLE);
        }

        // Set votings of logged in user
        int voting = db.getAnswerVoting(answers.get(position).getId());
        switch (voting) {
            case -1:
                holder.mDownvote.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                holder.mCardRatingView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                break;
            case 1:
                holder.mUpvote.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                holder.mCardRatingView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                break;

        }

        /**
         * Sets the clicklistener for clicking a list item (answer)
         */
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onAnswerListFragmentInteraction(holder.mItem);
                }
            }
        });

        /**
         * Listener to downwote an answer
         */
        holder.mDownvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lastVoting = db.getAnswerVoting(answerId);

                if (!db.saveAnswerVoting(answerId, -1)) {

                    Toast.makeText(context, context.getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
                } else {

                    AsyncSetRemoteFlashCardRating task = new AsyncSetRemoteFlashCardRating("answer", answerId, db.getLoggedInUser().getId(), -1);
                    task.execute();

                    int rating = Integer.parseInt(holder.mCardRatingView.getText().toString());
                    rating -= 1 + lastVoting;

                    holder.mCardRatingView.setText(rating + "");
                    holder.mDownvote.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                    holder.mUpvote.setColorFilter(Color.BLACK);
                    holder.mCardRatingView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }
        });

        /**
         * Listener to up vote an answer
         */
        holder.mUpvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lastVoting = db.getAnswerVoting(answerId);

                if (!db.saveAnswerVoting(answerId, +1)) {

                    Toast.makeText(context, context.getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
                } else {

                    AsyncSetRemoteFlashCardRating task = new AsyncSetRemoteFlashCardRating("answer", answerId, db.getLoggedInUser().getId(), 1);

                    int rating = Integer.parseInt(holder.mCardRatingView.getText().toString());
                    rating += 1 - lastVoting;

                    holder.mCardRatingView.setText(rating + "");
                    holder.mUpvote.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                    holder.mDownvote.setColorFilter(Color.BLACK);
                    holder.mCardRatingView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (answers != null) {
            return answers.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mAuthorView;
        // public final TextView mGroupRatingView;
        public final TextView mCardRatingView;
        public final TextView mDateView;
        public final ImageView misCorrectView;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon
        public final ImageView mDownvote; // Text icon
        public final ImageView mUpvote; // Text icon
        public Answer mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

            // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
            mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
            mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
            misCorrectView = (ImageView) view.findViewById(R.id.image_view_iscorrect);
            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);
            mUpvote = (ImageView) view.findViewById(R.id.button_up_vote);
            mDownvote = (ImageView) view.findViewById(R.id.button_down_vote);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
