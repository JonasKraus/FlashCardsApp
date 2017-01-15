package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.DELETE.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerAnswer;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.Statistics;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

/**
 * {@link RecyclerView.Adapter} that can display a {@link de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers} and makes a call to the
 * TODO: Replace the implementation with code for your data type.
 */
public class RecyclerViewAdapterFlashCardAnswers extends RecyclerView.Adapter<RecyclerViewAdapterFlashCardAnswers.ViewHolder> {


    private final List<Answer> answers;
    private final List<ViewHolder> holders = new ArrayList<ViewHolder>();
    private final OnFragmentInteractionListenerAnswer mListener;
    private final boolean isUpToDate;
    private final boolean isPlayMultiplyChoiceMode;
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
     * @param isPlayMultiplyChoiceMode
     */
    public RecyclerViewAdapterFlashCardAnswers(List<Answer> items, OnFragmentInteractionListenerAnswer listener, boolean isUpToDate, boolean isPlayMultiplyChoiceMode) {
        answers = items;
        mListener = listener;
        this.isUpToDate = isUpToDate;
        this.isPlayMultiplyChoiceMode = isPlayMultiplyChoiceMode;
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
        holder.mDownvote.setVisibility(View.VISIBLE);
        holder.mUpvote.setVisibility(View.VISIBLE);

        setViewState(holder, position);

        checkAnswerMediatype(holder, answers.get(position));

        final long answerId = answers.get(position).getId();

        /**
         * Sets the clicklistener for clicking a list item (answer)
         * gives this click to the mainActivit by passing the Interface
         *
         * @author Jonas Kraus jonas.kraus@uni-ulm.de
         *
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
        if (isUpToDate) {
            holder.mDownvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int lastVoting = db.getAnswerVoting(answerId);

                    if (!db.saveAnswerVoting(answerId, -1)) {

                        Toast.makeText(context, context.getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
                    } else {

                        Long ratingId = db.getAnswerVotingRatingId(answerId);

                        // Check if ratingExists and deletes it
                        if (ratingId != null) {
                            AsyncDeleteRemoteRating taskDelete = new AsyncDeleteRemoteRating(ratingId);

                            if (ProcessConnectivity.isOk(context)) {

                                taskDelete.execute();
                            }

                        }


                        AsyncPostRemoteRating task = new AsyncPostRemoteRating("answer", answerId, db.getLoggedInUser().getId(), -1, db);

                        if (ProcessConnectivity.isOk(context)) {

                            task.execute();
                        }

                        int rating = Integer.parseInt(holder.mCardRatingView.getText().toString());
                        rating -= 1 + lastVoting;

                        holder.mCardRatingView.setText(rating + "");
                        holder.mDownvote.setColorFilter(context.getResources().getColor(R.color.colorAccent));
                        holder.mUpvote.setColorFilter(Color.BLACK);
                        holder.mCardRatingView.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    }
                }
            });
        }

        /**
         * Listener to up vote an answer
         */
        if (isUpToDate) {
            holder.mUpvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int lastVoting = db.getAnswerVoting(answerId);

                    if (!db.saveAnswerVoting(answerId, +1)) {

                        Toast.makeText(context, context.getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
                    } else {

                        Long ratingId = db.getAnswerVotingRatingId(answerId);

                        // Check if ratingExists and deletes it
                        if (ratingId != null) {

                            AsyncDeleteRemoteRating taskDelete = new AsyncDeleteRemoteRating(ratingId);

                            if (ProcessConnectivity.isOk(context)) {

                                taskDelete.execute();
                            }
                        }

                        AsyncPostRemoteRating task = new AsyncPostRemoteRating("answer", answerId, db.getLoggedInUser().getId(), 1, db);

                        if (ProcessConnectivity.isOk(context)) {

                            task.execute();
                        }

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

        holders.add(holder);
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

        holder.llAnswerRating.setVisibility(isPlayMultiplyChoiceMode ? View.INVISIBLE : View.VISIBLE);
        holder.llAnswerUserDateRating.setVisibility(isPlayMultiplyChoiceMode ? View.INVISIBLE : View.VISIBLE);
        holder.llAnswerProfile.setVisibility(isPlayMultiplyChoiceMode ? View.INVISIBLE : View.VISIBLE);
        holder.checkBoxPlay.setVisibility(isPlayMultiplyChoiceMode ? View.VISIBLE : View.INVISIBLE);
        holder.mIsCorrect.setVisibility(isPlayMultiplyChoiceMode ? View.GONE : View.VISIBLE);
        //holder.llAnswerCheck.setVisibility(isPlayMultiplyChoiceMode ? View.GONE : View.VISIBLE);


        // Sets the hint to the list item
        if (answers.get(position).getHintText() != null && !answers.get(position).getHintText().equals("")) {

            holder.mHintView.setVisibility(View.VISIBLE);
            holder.mHintView.setText(answers.get(position).getHintText());
        } else {
            holder.mHintView.setVisibility(View.GONE);
        }


        // Set kind of check icon
        if (answers.get(position).isCorrect()) {

            holder.mIsCorrect.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_check));
        } else {

            holder.mIsCorrect.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_close));
        }

        // Check if check icon should be visible
        if (!isPlayMultiplyChoiceMode && !Globals.getDb().isAnswerOfTypeMultiplyChoice(answers.get(position).getId())) {

            holder.mIsCorrect.setVisibility(View.GONE);
            holder.llAnswerCheck.setVisibility(View.GONE);
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
    }


    /**
     * Checks wich mediatype the answer has and sets its content
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-01-02
     *
     * @param holder
     * @param answer
     */
    private void checkAnswerMediatype(ViewHolder holder, Answer answer) {

        //Log.d("answer uri", answer.getUri());

        String uriString = answer.getUri();
        final String uriStringFinal = uriString;

        boolean isImage = false;
        boolean isVideo = false;
        boolean isAudio = false;

        if (uriString.toLowerCase().endsWith(".png") || uriString.toLowerCase().endsWith(".jpg")) {

            ProcessorImage.download(uriString, holder.mediaImage, answer.getId(), "_answer" );
            isImage = true;

        } else if (uriString.contains("youtube")) {

            ProcessorImage.download(uriString, holder.mediaImage, answer.getId(), "_answer" );
            isImage = true;
            isVideo = true;

            holder.mediaPlay.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    context.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(uriStringFinal)));
                }
            });


        } else {

            holder.webViewUri.setVisibility(View.VISIBLE);
            holder.mediaPlay.setVisibility(View.GONE);
            holder.mediaImage.setVisibility(View.GONE);

            // empty string do nothing
            if (uriString.equals("")) {

                holder.relativeLayoutWebview.setVisibility(View.GONE);
                return;
            }

            // add protocoll if needed
            if (!uriString.toLowerCase().startsWith("http")) {

                uriString = "http://" + uriString;

            }

            WebSettings settings = holder.webViewUri.getSettings();
            settings.setJavaScriptEnabled(true);
            holder.webViewUri.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

            progressBar.setVisibility(View.VISIBLE);

            holder.webViewUri.setWebViewClient(new WebViewClient() {

                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {

                    if (progressBar.isShown()) {
                        progressBar.setVisibility(View.GONE);
                    }
                }

            });
            holder.webViewUri.loadUrl(uriString);
        }

        holder.mediaImage.setVisibility(isImage ? View.VISIBLE : View.GONE);
        holder.mediaPlay.setVisibility(uriString.contains("youtube") ? View.VISIBLE : View.GONE);
        holder.webViewUri.setVisibility(!isImage && !isVideo ? View.VISIBLE : View.GONE);


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
        if (answers != null) {
            return answers.size();
        }
        return 0;
    }


    /**
     * Validates if the answeres have been correct
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    public void validateAnswers(Statistics statistics) {

        // 100 percent
        long knowledge = 100;

        for (int i = 0; i < answers.size(); i++) {

            holders.get(i).mIsCorrect.setVisibility(View.VISIBLE);
            holders.get(i).llAnswerCheck.setVisibility(View.VISIBLE);
            holders.get(i).llAnswerProfile.setVisibility(View.VISIBLE);
            holders.get(i).llAnswerUserDateRating.setVisibility(View.VISIBLE);
            holders.get(i).llAnswerRating.setVisibility(View.VISIBLE);
            holders.get(i).checkBoxPlay.setVisibility(View.GONE);

            boolean shouldBeChecked = answers.get(i).isCorrect();
            boolean userChecked = holders.get(i).checkBoxPlay.isChecked();

            if (shouldBeChecked != userChecked) {

                holders.get(i).mIsCorrect.setBackgroundColor(Globals.getContext().getResources().getColor(R.color.red_0));
                //holders.get(i).llAnswerCheck.setBackgroundColor(Color.RED);

                // Its about 100 percent
                knowledge -= (100 / answers.size());
            } else {

                holders.get(i).mIsCorrect.setBackgroundColor(Globals.getContext().getResources().getColor(R.color.green_0));
                //holders.get(i).llAnswerCheck.setBackgroundColor(Color.GREEN);
            }
        }

        //SAVING statistics
        statistics.save(knowledge);
        Log.d("recycler", "validate " + statistics.toString());
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
        public final TextView mContentView;
        public final TextView mHintView;
        public final TextView mAuthorView;
        // public final TextView mGroupRatingView;
        public final TextView mCardRatingView;
        public final TextView mDateView;
        public final ImageView mIsCorrect;
        public final ImageView mLocalView;
        public final ImageView imageView; // Text icon
        public final ImageView mDownvote; // Text icon
        public final ImageView mUpvote; // Text icon
        public final ImageView mediaImage; // Answer url image
        public Answer mItem;
        public final ImageView mediaPlay;
        public final RelativeLayout relativeLayoutWebview;
        protected WebView webViewUri;

        // View for playmode
        public final LinearLayout llAnswerRating;
        public final LinearLayout llAnswerUserDateRating;
        public final LinearLayout llAnswerProfile;
        public final LinearLayout llAnswerCheck;
        public final CheckBox checkBoxPlay;


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
            mContentView = (TextView) view.findViewById(R.id.content);
            mHintView = (TextView) view.findViewById(R.id.hint);
            mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

            // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
            mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
            mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
            mIsCorrect = (ImageView) view.findViewById(R.id.image_view_iscorrect);
            mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

            imageView = (ImageView) view.findViewById(R.id.image_view_round_icon);
            mUpvote = (ImageView) view.findViewById(R.id.button_up_vote);
            mDownvote = (ImageView) view.findViewById(R.id.button_down_vote);

            mediaImage = (ImageView) view.findViewById(R.id.imageview_answer_media);
            mediaPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);

            webViewUri = (WebView) view.findViewById(R.id.webview_answer_question);
            relativeLayoutWebview = (RelativeLayout) view.findViewById(R.id.rl_answer_webview);

            //view for playmode
            llAnswerRating = (LinearLayout) view.findViewById(R.id.ll_answer_Rating);
            llAnswerProfile = (LinearLayout) view.findViewById(R.id.ll_answer_profile);
            llAnswerCheck = (LinearLayout) view.findViewById(R.id.ll_answer_check);
            llAnswerUserDateRating = (LinearLayout) view.findViewById(R.id.ll_answer_user_date_rating);

            checkBoxPlay = (CheckBox) view.findViewById(R.id.answer_checkbox_play);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
