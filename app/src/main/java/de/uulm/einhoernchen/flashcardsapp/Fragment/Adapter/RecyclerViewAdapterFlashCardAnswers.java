package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.DummyContent.DummyItem;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interfaces.OnFragmentInteractionListenerCategory;
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

    public RecyclerViewAdapterFlashCardAnswers(List<Answer> items, OnFragmentInteractionListenerAnswer listener, boolean isUpToDate) {
        answers = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
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
        holder.mBookmarkView.setVisibility(View.INVISIBLE);

        if (!isUpToDate) {

            holder.mLocalView.setVisibility(View.INVISIBLE);
        } else {

            holder.mLocalView.setVisibility(View.VISIBLE);
        }

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
        public final ImageView mBookmarkView;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon
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
            mBookmarkView = (ImageView) view.findViewById(R.id.image_view_bookmark);
            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
