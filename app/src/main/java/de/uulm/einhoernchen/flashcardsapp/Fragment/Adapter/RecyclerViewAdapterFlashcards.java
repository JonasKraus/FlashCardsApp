package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerFlashcard;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Question;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCards} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterFlashcards extends RecyclerView.Adapter<RecyclerViewAdapterFlashcards.ViewHolder> implements View.OnClickListener {

    private static final int VIEW_TYPE_FOOTER = 1;
    private static final int VIEW_TYPE_CELL = 2;
    private final List<FlashCard> flashCards;
    private final OnFragmentInteractionListenerFlashcard mListener;
    private final boolean isUpToDate;
    private final DbManager db;
    private final Context context;
    private final ProgressBar progressBar;
    protected final FragmentManager supportFragmentManager;

    public RecyclerViewAdapterFlashcards(DbManager db, List<FlashCard> items, OnFragmentInteractionListenerFlashcard listener, boolean isUpToDate, Context context, ProgressBar progressBar, FragmentManager supportFragmentManager) {
        flashCards = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
        this.db = db;
        this.context = context;
        this.progressBar = progressBar;
        this.supportFragmentManager = supportFragmentManager;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        // Normal card layout for a card
        if (viewType == VIEW_TYPE_CELL) {

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_card, parent, false);
        } else {

            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_flashcard, parent, false);
        }

        return new ViewHolder(view);
    }


    /**
     * Checks if its the last item then returns a new type
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {

        // If last item then add type for the footer (add-Button)
        return (position == flashCards.size()) ? VIEW_TYPE_FOOTER : VIEW_TYPE_CELL;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {


        Globals.getFloatingActionButtonAdd().setVisibility(View.VISIBLE);
        Globals.getFloatingActionButtonAdd().setOnClickListener(this);

        //TODO Jonas delete if unused
        if (position >= flashCards.size()) {

            //TODO Jonas delete if unused
            holder.buttonAddCard.setVisibility(View.GONE);

            //TODO Jonas delete if unused
            holder.buttonAddCard.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onFlashcardListFragmentInteraction(null);
                    }
                }
            });

        } else {

        holder.mItem = flashCards.get(position);
        // holder.mIdView.setText(flashCards.get(position).getId()+""); TODO Wird das benötigt?
        holder.mContentView.setText(flashCards.get(position).getQuestion().getQuestionText());
        String authorName = flashCards.get(position).getAuthor() != null ? flashCards.get(position).getAuthor().getName() : "No Author";

        holder.mAuthorView.setText(flashCards.get(position).getAuthor().getId() + " " + authorName); //TODO delete
        // holder.mGroupRatingView.setVisibility(View.INVISIBLE);
        holder.mCardRatingView.setText(flashCards.get(position).getRatingForView());
        holder.mDateView.setText(flashCards.get(position).getLastUpdatedString());
        holder.mBookmarkView.setVisibility(View.VISIBLE);
        // holder.misCorrectView.setImageDrawable(// TODO set if marked);

        checkAnswerMediatype(holder, flashCards.get(position).getQuestion());

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
        holder.imageView.setTag(holder.mItem.getSelectionDate() > 0);

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

        /**
         * Sets the clicklistener for de/selecting a card
         */
        holder.imageView.setOnClickListener(new View.OnClickListener() {

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

    }


    /**
     * checks which type the media uri has and sets the content
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     *
     * @param holder
     * @param question
     */
    private void checkAnswerMediatype(ViewHolder holder, Question question) {

        String uriString = question.getUri().toString();
        final String uriStringFinal = uriString;

        boolean isImage = false;
        boolean isVideo = false;
        boolean isAudio = false;

        if (uriString.toLowerCase().endsWith(".png") || uriString.toLowerCase().endsWith(".jpg")) {

            ProcessorImage.download(uriString, holder.imageViewUri, question.getId(), "_question" );
            isImage = true;

        } else if (uriString.contains("youtube")) {

            ProcessorImage.download(uriString, holder.imageViewUri, question.getId(), "_question" );
            isImage = true;
            isVideo = true;


        } else {

            holder.webViewUri.setVisibility(View.VISIBLE);
            holder.imageViewUri.setVisibility(View.GONE);

            if (!uriString.startsWith("https://") && !uriString.startsWith("http://") && !uriString.equals("")) {

                uriString = "https://" + uriString;

            } else if (uriString == null || uriString.equals("")) {
                uriString = "http://134.60.51.72:9000/";
            }

            WebSettings settings = holder.webViewUri.getSettings();
            settings.setJavaScriptEnabled(true);

            progressBar.setVisibility(View.VISIBLE);

            holder.webViewUri.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {

                    if (progressBar.isShown()) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

            });
            holder.webViewUri.loadUrl(uriString);
        }

        holder.imageViewUri.setVisibility(isImage ? View.VISIBLE : View.GONE);
        holder.mediaPlay.setVisibility(uriString.contains("youtube") ? View.VISIBLE : View.GONE);
        holder.webViewUri.setVisibility(!isImage && !isVideo ? View.VISIBLE : View.GONE);


    }

    @Override
    public int getItemCount() {

        return flashCards.size() +1; // +1 to add a buttton at the end of the list
    }


    /**
     * Implementing on click
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fab_add:

                MainActivity mainActivity = (MainActivity) Globals.getContext();
                mainActivity.onFlashcardListFragmentInteraction(null);

                break;
        }

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
        public final ImageView imageViewUri; // Text icon
        public final ImageView mediaPlay; // VideoPlay Icon
        public final WebView webViewUri; // Text icon
        public final Button buttonAddCard; // Text icon

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
            mBookmarkView = (ImageView) view.findViewById(R.id.image_view_iscorrect);
            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);
            imageViewUri = (ImageView) view.findViewById(R.id.image_view_question_uri);
            webViewUri = (WebView) view.findViewById(R.id.webview_answer_question);
            mediaPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);

            buttonAddCard = (Button) view.findViewById(R.id.button_card_add);


        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
