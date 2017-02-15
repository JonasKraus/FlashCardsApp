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

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeck;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerCarddeckLongClick;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentCarddecks} and makes a call to the
 */
public class RecyclerViewAdapterCarddecks extends RecyclerView.Adapter<RecyclerViewAdapterCarddecks.ViewHolder> {

    private final List<CardDeck> cardDecks;
    private final OnFragmentInteractionListenerCarddeck mListener;
    private final OnFragmentInteractionListenerCarddeckLongClick mLongClickListener;
    private final boolean isUpToDate;
    private final DbManager db = Globals.getDb();

    public RecyclerViewAdapterCarddecks(List<CardDeck> items, OnFragmentInteractionListenerCarddeck listener,  OnFragmentInteractionListenerCarddeckLongClick longClickListener, boolean isUpToDate) {
        cardDecks = items;
        mListener = listener;
        mLongClickListener = longClickListener;
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
        holder.mItem = cardDecks.get(position);
        // holder.mIdView.setText(cardDecks.get(position).getId() + ""); TODO Wird das benötigt?
        holder.mContentView.setText(holder.mItem.getName() + "");
        String userGroupName = holder.mItem.getUserGroup() != null ? cardDecks.get(position).getUserGroup().getName() : "No Author";
        holder.mAuthorView.setText(userGroupName);
        // holder.mGroupRatingView.setVisibility(View.INVISIBLE);
        holder.mCardRatingView.setText(holder.mItem.getRatingForView());
        //holder.mDateView.setText(cardDecks.get(position).getLastUpdatedString());
        holder.mDateView.setVisibility(View.INVISIBLE);
        holder.mBookmarkView.setVisibility(View.GONE);

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
                    mListener.onCarddeckListFragmentInteraction(holder.mItem);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                if (null != mLongClickListener) {

                    mLongClickListener.onCarddeckListFragmentInteractionLongClick(holder.mItem);
                }

                return true;
            }
        });

        //get first letter of each String item
        final String firstLetter = String.valueOf(cardDecks.get(position).getName().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        final int color = generator.getColor(holder.mItem.getId()); // TODO
        //int color = generator.getRandomColor();


        final long carddeckID = holder.mItem.getId();
        holder.mItem.setSelectionDate(db.getCarddeckSelectionDate(carddeckID));
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

                    db.deselectCarddeck(carddeckID);

                } else {

                    db.selectCarddeck(carddeckID);
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
        if (cardDecks != null) {
            return cardDecks.size();

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
        public CardDeck mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

            // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
            mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
            mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
            mBookmarkView = (ImageView) view.findViewById(R.id.image_view_iscorrect);
            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
