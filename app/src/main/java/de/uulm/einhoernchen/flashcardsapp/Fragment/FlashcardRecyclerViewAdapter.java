package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import de.uulm.einhoernchen.flashcardsapp.Fragment.dummy.DummyContentCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.dummy.DummyContentCard.ItemFragmentFlashcard.OnFlashcardListFragmentInteractionListener;
import de.uulm.einhoernchen.flashcardsapp.Fragment.dummy.DummyContent.DummyItem;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link DummyContentCard.ItemFragmentFlashcard.OnFlashcardListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class FlashcardRecyclerViewAdapter extends RecyclerView.Adapter<FlashcardRecyclerViewAdapter.ViewHolder> {

    private final List<FlashCard> flashCards;
    private final OnFlashcardListFragmentInteractionListener mListener;

    public FlashcardRecyclerViewAdapter(List<FlashCard> items, OnFlashcardListFragmentInteractionListener listener) {
        flashCards = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = flashCards.get(position);
        holder.mIdView.setText(flashCards.get(position).getId()+"");
        holder.mContentView.setText(flashCards.get(position).getQuestion().getQuestionText());
        String authorName = flashCards.get(position).getAuthor() != null ? flashCards.get(position).getAuthor().getName() : "No Author";
        holder.mAuthorView.setText(authorName);

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

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);
        holder.imageView.setTag(false);


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
                } else {
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
        public final ImageView imageView; // Text icon
        public FlashCard mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);
            imageView = (ImageView) view.findViewById(R.id.image_view);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
