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

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.dummy.DummyContent.DummyItem;
import de.uulm.einhoernchen.flashcardsapp.Models.Categroy;
import de.uulm.einhoernchen.flashcardsapp.R;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link de.uulm.einhoernchen.flashcardsapp.Fragment.ItemFragmentCategory.OnCategoryListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<Categroy> categroys;
    private final ItemFragmentCategory.OnCategoryListFragmentInteractionListener mListener;

    public CategoryRecyclerViewAdapter(List<Categroy> items, ItemFragmentCategory.OnCategoryListFragmentInteractionListener listener) {
        categroys = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = categroys.get(position);
        // holder.mIdView.setText(categroys.get(position).getId()+""); TODO Wird das benötigt?
        holder.mContentView.setText(categroys.get(position).getName());
        holder.mAuthorView.setVisibility(View.INVISIBLE);
        holder.mGroupRatingView.setVisibility(View.INVISIBLE);
        holder.mCardRatingView.setVisibility(View.INVISIBLE);
        holder.mDateView.setVisibility(View.INVISIBLE);
        holder.mBookmarkView.setVisibility(View.INVISIBLE);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onCategoryListFragmentInteraction(holder.mItem);
                }
            }
        });

        //get first letter of each String item
        final String firstLetter = String.valueOf(categroys.get(position).getName().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        final int color = generator.getColor(categroys.get(position).getId()); // TODO
        //int color = generator.getRandomColor();

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px

        holder.imageView.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return categroys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final TextView mAuthorView;
        public final TextView mGroupRatingView;
        public final TextView mCardRatingView;
        public final TextView mDateView;
        public final ImageView mBookmarkView;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon
        public Categroy mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

            mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
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
