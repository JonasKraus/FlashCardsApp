package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUser;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterUsers extends RecyclerView.Adapter<RecyclerViewAdapterUsers.ViewHolder> {


    private final List<User> users;
    private final List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private final OnFragmentInteractionListenerUser mListener;
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
    public RecyclerViewAdapterUsers(List<User> items, OnFragmentInteractionListenerUser listener, boolean isUpToDate) {
        users = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = users.get(position);
        // holder.mIdView.setText(users.get(position).getId()+""); TODO Wird das benötigt?
        holder.textviewName.setText(users.get(position).getName());
        holder.textViewEmail.setVisibility(View.VISIBLE);
        holder.textViewEmail.setText(users.get(position).getEmail());

        setViewState(holder, position);

        holders.add(holder);

        setRoundIcon(users.get(position), holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onUserListFragmentInteraction(holder.mItem);
                }
            }
        });
    }


    /**
     * Sets the round icon
     * // TODO set the profile image
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @param user
     * @param holder
     */
    private void setRoundIcon(User user, ViewHolder holder) {
        //get first letter of each String item
        final String firstLetter = String.valueOf(user.getName().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        final int color = generator.getColor(user.getId()); // TODO
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

        Log.d("visibillity", isUpToDate+"");
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
        if (users != null) {
            return users.size();
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
        public final TextView textviewName;
        public final TextView textViewEmail;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon

        public User mItem;


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
            textviewName = (TextView) view.findViewById(R.id.textview_user_name);
            textViewEmail = (TextView) view.findViewById(R.id.textview_user_email);

            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + textviewName.getText() + "'";
        }
    }
}
