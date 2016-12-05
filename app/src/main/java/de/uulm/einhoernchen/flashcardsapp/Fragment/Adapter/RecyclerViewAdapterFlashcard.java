package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCard.ItemFragmentFlashcard.OnFlashcardListFragmentInteractionListener;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.DummyContent.DummyItem;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link ContentCard.ItemFragmentFlashcard.OnFlashcardListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterFlashcard extends RecyclerView.Adapter<RecyclerViewAdapterFlashcard.ViewHolder> {

    private final List<FlashCard> flashCards;
    private final OnFlashcardListFragmentInteractionListener mListener;
    private final boolean isUpToDate;
    private final DbManager db;

    public RecyclerViewAdapterFlashcard(DbManager db, List<FlashCard> items, OnFlashcardListFragmentInteractionListener listener, boolean isUpToDate) {
        flashCards = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
        this.db = db;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = flashCards.get(position);
        // holder.mIdView.setText(flashCards.get(position).getId()+""); TODO Wird das benötigt?
        holder.mContentView.setText(flashCards.get(position).getQuestion().getQuestionText());
        String authorName = flashCards.get(position).getAuthor() != null ? flashCards.get(position).getAuthor().getName() : "No Author";

        holder.mAuthorView.setText(flashCards.get(position).getAuthor().getId() + " " +authorName); //TODO delete
        // holder.mGroupRatingView.setVisibility(View.INVISIBLE);
        holder.mCardRatingView.setText(flashCards.get(position).getRatingForView());
        holder.mDateView.setText(flashCards.get(position).getLastUpdatedString());
        holder.mBookmarkView.setVisibility(View.VISIBLE);
        // holder.mBookmarkView.setImageDrawable(// TODO set if marked);


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
                    mListener.onFlashcardListFragmentInteraction(holder.mItem);
                }
            }
        });

        //get first letter of each String item
        final String firstLetter = String.valueOf(flashCards.get(position).getQuestion().getQuestionText().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        int authorRanking = flashCards.get(position).getAuthor() != null ? flashCards.get(position).getAuthor().getRating() : 0;
        final int color = generator.getColor(authorRanking);
        //int color = generator.getRandomColor();

        final long cardID = holder.mItem.getId();
        holder.mItem.setSelectionDate(db.getCardSelectionDate(cardID));
        holder.imageView.setTag(holder.mItem.getSelectionDate()>0);

        TextDrawable drawable;

        if (holder.imageView.getTag().equals(false)) {

            drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px
            holder.imageView.setTag(false);


        } else {

            drawable = TextDrawable.builder()
                    .buildRound(String.valueOf("✓"), Color.GRAY); // radius in px
            holder.imageView.setTag(true);
        }

        holder.imageView.setImageDrawable(drawable);


        holder.imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                //v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.card_flip_left_out));
                
                TextDrawable drawable;

                // @TODO Set card as checked
                if (holder.imageView.getTag().equals(true)) {
                    drawable = TextDrawable.builder()
                            .buildRound(firstLetter, color); // radius in px
                    holder.imageView.setTag(false);

                    db.deselectCard(cardID);
                } else {

                    db.selectCard(cardID);
                    String firstLetter = String.valueOf("✓"); // hier wird der buchstabe gesetzt
                    drawable = TextDrawable.builder()
                            .buildRound(firstLetter, Color.GRAY); // radius in px
                    holder.imageView.setTag(true);
                }

                holder.imageView.setImageDrawable(drawable);
            }
        });
    }

    @Override
    public int getItemCount() {
        return flashCards.size();
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
        public FlashCard mItem;

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
