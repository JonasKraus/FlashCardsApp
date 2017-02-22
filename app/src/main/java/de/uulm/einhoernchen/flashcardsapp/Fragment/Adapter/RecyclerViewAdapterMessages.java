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
import java.util.Date;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerMessage;
import de.uulm.einhoernchen.flashcardsapp.Model.Filter.MessagesFilter;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterMessages extends RecyclerView.Adapter<RecyclerViewAdapterMessages.ViewHolder> implements Filterable {


    private final List<Message> messages;
    private final List<Message> filteredList;
    private final List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private final OnFragmentInteractionListenerMessage mListener;
    private final boolean isUpToDate;
    private final Context context = Globals.getContext();
    private final DbManager db = Globals.getDb();
    private final ProgressBar progressBar = Globals.getProgressBar();
    private MessagesFilter messagesFilter;

    /**
     * Constructs the recycler views
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param items
     * @param listener
     * @param isUpToDate
     */
    public RecyclerViewAdapterMessages(List<Message> items, OnFragmentInteractionListenerMessage listener, boolean isUpToDate) {
        this.messages = items;
        this.filteredList = new ArrayList<>();
        this.mListener = listener;
        this.isUpToDate = isUpToDate;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = messages.get(position);
        // holder.mIdView.setText(messages.get(position).getId()+""); TODO Wird das benötigt?
        holder.textViewMessageType.setText(messages.get(position).getMessageType().toString());
        holder.textViewMessageSender.setText(messages.get(position).getSender().getName());

        holder.textViewMessageContent.setText(messages.get(position).getContent());
        holder.textViewMessageTargetDeck.setText(messages.get(position).getTargetCardDeck().getName());
        holder.textViewMessageDate.setText((new Date(messages.get(position).getCreated())).toGMTString());

        setViewState(holder, position);

        holders.add(holder);

        setRoundIcon(messages.get(position), holder);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onMessageListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private void setRoundIcon(Message message, ViewHolder holder) {
        //get first letter of each String item
        final String firstLetter = String.valueOf(message.getMessageType().toString().charAt(0)); // hier wird der buchstabe gesetzt

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
        // generate random color
        final int color = generator.getColor(message.getId()); // TODO
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
    public List<Message> getList() {

        return this.messages;
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
        if (messages != null) {
            return messages.size();
        }
        return 0;
    }

    @Override
    public Filter getFilter() {

        if(this.messagesFilter == null) {

            this.messagesFilter = new MessagesFilter(this, messages);
        }

        return this.messagesFilter;
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
        public final TextView textViewMessageType;
        public final TextView textViewMessageSender;
        public final TextView textViewMessageContent;
        public final TextView textViewMessageTargetDeck;
        public final TextView textViewMessageDate;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon

        public Message mItem;


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
            textViewMessageType = (TextView) view.findViewById(R.id.textview_message_type);
            textViewMessageSender = (TextView) view.findViewById(R.id.textview_message_sender);
            textViewMessageContent = (TextView) view.findViewById(R.id.textview_message_content);
            textViewMessageTargetDeck = (TextView) view.findViewById(R.id.textview_message_target_deck);
            textViewMessageDate = (TextView) view.findViewById(R.id.textview_message_date);

            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + textViewMessageType.getText() + "'";
        }

    }
}
