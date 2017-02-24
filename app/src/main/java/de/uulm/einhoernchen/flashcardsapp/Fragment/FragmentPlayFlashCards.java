package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.DELETE.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Dialog.DialogKnowledge;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.Statistic;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.ValidatorInput;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentPlayFlashCards#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPlayFlashCards extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean isUpToDate;
    private TextView mIdView;
    private TextView mContentView;
    private TextView mAuthorView;
    private TextView mCardRatingView;
    private TextView mDateView;
    private ImageView mIsCorrect;
    private ImageView mLocalView;
    private ImageView imageViewUri;
    private ImageView imageViewPlay;
    private ImageView imageViewVoteUp;
    private ImageView imageViewVoteDown;
    private ImageView imageViewEditQuestion;
    private ImageView imageViewSaveQuestion;

    private EditText editTextAnswerText;
    private EditText editTextAnswerHint;
    private EditText editTextAnswerUri;
    private FloatingActionButton buttonAnswerEditorSave;

    private FrameLayout frameLayoutAnswers;

    private WebView webViewUri;

    private Button buttonAddAnswer;

    private RadioGroup radioGroupAnswerCorrect;
    private RadioButton radioButtonAnswerCorrect;
    private RadioButton radioButtonAnswerIncorrect;

    private FloatingActionButton fab = Globals.getFloatingActionButton();

    private DbManager db = Globals.getDb();
    public FlashCard currentFlashcard;
    private int position = 0;
    private Constants state = Constants.PLAY_QUESTION;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View view;
    private NestedScrollView nsContentAnswers;
    private ContentFlashCardAnswers contentAnswers;
    private Statistic statistic;
    private List<Statistic> statistics;
    private Message message;


    public FragmentPlayFlashCards() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPlayFlashCards.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPlayFlashCards newInstance(String param1, String param2) {
        FragmentPlayFlashCards fragment = new FragmentPlayFlashCards();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }


    /**
     * Inits the class variables
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void initVariables() {

        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);

        fab.setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_list));
        fab.setOnClickListener(this);

        Globals.getProgressBar().setVisibility(View.VISIBLE);

        statistics = Statistic.getStatisticsOfSelectedCards(message); // TODO

        if (statistics.size() == 0) {

            Snackbar.make(this.getView(), getContext().getString(R.string.select_card), Snackbar.LENGTH_LONG).show();

            MainActivity mainActivity = (MainActivity) Globals.getContext();
            mainActivity.onBackPressed();

        } else if (position < statistics.size()){

            currentFlashcard = db.getFlashCard(statistics.get(position).getCardId());
        }

        Globals.getProgressBar().setVisibility(View.GONE);

    }


    /**
     * Finds the needed viwes
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     */
    private void initViewQuestion() {

        mIdView = (TextView) view.findViewById(R.id.id);
        mContentView = (TextView) view.findViewById(R.id.content);
        mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

        // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
        mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
        mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
        mIsCorrect = (ImageView) view.findViewById(R.id.image_view_iscorrect);
        mIsCorrect.setVisibility(currentFlashcard.isMultipleChoice() ? View.VISIBLE : View.GONE);

        mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

        imageViewUri = (ImageView) view.findViewById(R.id.image_view_question_uri);

        webViewUri = (WebView) view.findViewById(R.id.webview_card_question);

        imageViewVoteDown = (ImageView) view.findViewById(R.id.button_down_vote);
        imageViewVoteUp = (ImageView) view.findViewById(R.id.button_up_vote);

        imageViewEditQuestion = (ImageView) view.findViewById(R.id.imageview_question_edit);
        imageViewSaveQuestion = (ImageView) view.findViewById(R.id.imageview_question_save);

        buttonAddAnswer = (Button) view.findViewById(R.id.button_add_answer);
        editTextAnswerText = (EditText) view.findViewById(R.id.edittext_answer_text);
        editTextAnswerHint = (EditText) view.findViewById(R.id.edittext_answer_hint);
        editTextAnswerUri = (EditText) view.findViewById(R.id.edittext_answer_uri);
        buttonAnswerEditorSave = (FloatingActionButton) view.findViewById(R.id.fab_card_details_answer_add);

        imageViewPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);
        radioGroupAnswerCorrect = (RadioGroup) view.findViewById(R.id.radio_buttongroup_answer_editor);
        radioButtonAnswerCorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_correct);
        radioButtonAnswerIncorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_incorrect);

        //frameLayoutAnswers = (FrameLayout) view.findViewById(R.id.fragment_container_card_answer);
        //frameLayoutAnswers.setVisibility(View.GONE);

        nsContentAnswers = (NestedScrollView) view.findViewById(R.id.nested_scrollview_content_answers);

        // Initially set visibility of answers
        toggleStateAndFabIcon(true);

    }


    /**
     * toggels the visibility of the card's answers
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void toggleVisibilityAnswers() {

        int mode = View.VISIBLE;

        switch (nsContentAnswers.getVisibility()) {

            case View.VISIBLE:

                mode = View.GONE;
                break;

            case View.GONE:

                mode = View.VISIBLE;
                break;
        }

        nsContentAnswers.setVisibility(mode);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play_question, container, false);

        // Create statistic Object

        statistic = new Statistic();

        initVariables();

        initViewQuestion();

        Log.d("image-view", imageViewPlay.getId()+"");

        setMedia();

        setListener();

        return view;
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    /**
     * Keep attention to reset Listeners and add listener of main activity to the fab
     * After this method the fragment will be destroyed
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();

        fab.setOnClickListener(null);
        resetFabPlay();
    }


    /**
     * Reset the listener of the ffab becouse thos method is called in the lifecycle
     * when the fragment returns to te back stack
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        fab.setOnClickListener(null);
        resetFabPlay();
    }


    /**
     * Gets the MainActivity and uses its method to reset the fab
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void resetFabPlay() {

        MainActivity mainActivity = (MainActivity) Globals.getContext();
        mainActivity.resetFabPlay();
    }


    /**
     * Collects the answers and creates the list view in its recyclerview
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *
     */
    private void setAnswers() {

        if (contentAnswers == null) {

            contentAnswers = new ContentFlashCardAnswers();
        }
        contentAnswers.collectItemsFromDb(currentFlashcard.getId(), false, currentFlashcard.isMultipleChoice());
        contentAnswers.collectItemsFromServer(currentFlashcard.getId(), false, currentFlashcard.isMultipleChoice());
    }

    /**
     * Sets the content for teh view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     */
    public void setContent() {

        setAnswers();

        if (currentFlashcard.isMultipleChoice()) {

            nsContentAnswers.setVisibility(View.VISIBLE);
        } else {

            nsContentAnswers.setVisibility(View.INVISIBLE);
        }

        // Set the cardid to the statistic object
        // save it after validating
        statistic.setCardId(currentFlashcard.getId());
        statistic.setStartDate(System.currentTimeMillis());

        mContentView.setText(Html.fromHtml(currentFlashcard.getQuestion().getQuestionText()));
        mAuthorView.setText(currentFlashcard.getQuestion().getAuthor().getName());
        mCardRatingView.setText(currentFlashcard.getRatingForView());
        mDateView.setText(currentFlashcard.getLastUpdatedString());

        if (!isUpToDate) {

            mLocalView.setVisibility(View.GONE);
        } else {

            mLocalView.setVisibility(View.VISIBLE);
        }

        if (currentFlashcard.isMultipleChoice()) {

            radioButtonAnswerCorrect.setChecked(true);
            radioButtonAnswerIncorrect.setChecked(false);
            radioGroupAnswerCorrect.setVisibility(View.VISIBLE);
        } else {

            radioButtonAnswerCorrect.setChecked(true);
            radioButtonAnswerIncorrect.setChecked(false);
            radioGroupAnswerCorrect.setVisibility(View.GONE);
        }

        int voting = db.getCardVoting(currentFlashcard.getId());

        switch (voting) {
            case -1:
                imageViewVoteDown.setColorFilter(getResources().getColor(R.color.colorAccent));
                mCardRatingView.setTextColor(getResources().getColor(R.color.colorAccent));
                break;
            case 1:
                imageViewVoteUp.setColorFilter(getResources().getColor(R.color.colorAccent));
                mCardRatingView.setTextColor(getResources().getColor(R.color.colorAccent));
                break;

        }

    }


    /**
     * Sets the mediatype and content
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     */
    private void setMedia() {

        Log.d("hier Media", (imageViewPlay == null) + "");

        if (currentFlashcard.getQuestion().getUri() != null && currentFlashcard.getQuestion().getUri().toString() != "") {

            String uriString = currentFlashcard.getQuestion().getUri().toString().toLowerCase();

            if (uriString.contains("youtube")) {

                imageViewPlay.setVisibility(View.VISIBLE);
                imageViewPlay.setOnClickListener(this);
            }

            if (uriString.endsWith("jpg") || uriString.endsWith(".png") || uriString.contains("youtube")) {

                ProcessorImage.download(currentFlashcard.getQuestion().getUri().toString(), imageViewUri, currentFlashcard.getQuestion().getId(), "_question");
                webViewUri.setVisibility(View.GONE);
                imageViewPlay.setVisibility(uriString.contains("youtube") ? View.VISIBLE : View.GONE);
                imageViewUri.setVisibility(View.VISIBLE);

            } else {

                webViewUri.setVisibility(View.VISIBLE);
                imageViewPlay.setVisibility(View.GONE);
                imageViewUri.setVisibility(View.GONE);

                if (!uriString.startsWith("https://") && !uriString.startsWith("http://") && !uriString.equals("")) {

                    uriString = "http://" + uriString;

                } else if (uriString.equals("")) {

                    uriString = "http://134.60.51.72:9000/";
                }

                WebSettings settings = webViewUri.getSettings();
                settings.setJavaScriptEnabled(true);
                webViewUri.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                Globals.getProgressBar().setVisibility(View.VISIBLE);

                webViewUri.setWebViewClient(new WebViewClient() {

                    public boolean shouldOverrideUrlLoading(WebView view, String url) {

                        view.loadUrl(url);
                        return true;
                    }

                    public void onPageFinished(WebView view, String url) {

                        if (Globals.getProgressBar().isShown()) {
                            Globals.getProgressBar().setVisibility(View.GONE);
                        }
                    }

                });
                webViewUri.loadUrl(uriString);
            }
        }

    }


    /**
     * Sets the listeners for the view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-29
     */
    private void setListener() {

        final long cardID = statistics.get(position).getCardId();

        /**
         * Sets the clicklistener to vote a cards rating down
         */
        imageViewVoteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ProcessConnectivity.isOk(getContext())) {

                    Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                    return;
                }

                int lastVoting = db.getCardVoting(cardID);

                if (!db.saveCardVoting(cardID, -1)) {

                    Snackbar.make(v, getContext().getString(R.string.voting_already_voted), Snackbar.LENGTH_LONG).show();
                } else {

                    Long ratingId = db.getCardVotingRatingId(cardID);

                    // Check if ratingExists and deletes it
                    if (ratingId != null) {

                        AsyncDeleteRemoteRating taskDelete = new AsyncDeleteRemoteRating(ratingId);

                        if (ProcessConnectivity.isOk(getContext())) {

                            taskDelete.execute();
                        }
                    }

                    AsyncPostRemoteRating task = new AsyncPostRemoteRating("flashcard", cardID, db.getLoggedInUser().getId(), -1, db);

                    if (ProcessConnectivity.isOk(getContext())) {

                        task.execute();
                    }

                    int rating = Integer.parseInt(mCardRatingView.getText().toString());
                    rating -= 1 + lastVoting;
                    mCardRatingView.setText(rating + "");
                    imageViewVoteDown.setColorFilter(getResources().getColor(R.color.colorAccent));
                    imageViewVoteUp.setColorFilter(Color.BLACK);
                    mCardRatingView.setTextColor(getResources().getColor(R.color.colorAccent));
                }

            }
        });

        /**
         * Sets the clicklistener to vote a cards rating up
         */
        imageViewVoteUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            if (!ProcessConnectivity.isOk(getContext())) {

                Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                return;
            }

            int lastVoting = db.getCardVoting(cardID);

            if (!db.saveCardVoting(cardID, +1)) {

                Snackbar.make(v, getContext().getString(R.string.voting_already_voted), Snackbar.LENGTH_LONG).show();
            } else {

                Long ratingId = db.getCardVotingRatingId(cardID);

                // Check if ratingExists and deletes it
                if (ratingId != null) {

                    AsyncDeleteRemoteRating taskDelete = new AsyncDeleteRemoteRating(ratingId);

                    if (ProcessConnectivity.isOk(getContext())) {

                        taskDelete.execute();
                    }
                }

                AsyncPostRemoteRating task = new AsyncPostRemoteRating("flashcard", cardID, db.getLoggedInUser().getId(), 1, db);

                if (ProcessConnectivity.isOk(getContext())) {

                    task.execute();
                }

                int rating = Integer.parseInt(mCardRatingView.getText().toString());
                rating += 1 - lastVoting;
                mCardRatingView.setText(rating + "");
                imageViewVoteUp.setColorFilter(getResources().getColor(R.color.colorAccent));
                imageViewVoteDown.setColorFilter(Color.BLACK);
                mCardRatingView.setTextColor(getResources().getColor(R.color.colorAccent));
            }

            }
        });


        // Click listeer to add answer
        buttonAnswerEditorSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateAnswer()) {

                    return;

                } else if (!ProcessConnectivity.isOk(getContext())) {

                    Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                    return;

                } else {

                    String text = editTextAnswerText.getText().toString();
                    String hint = editTextAnswerHint.getText().toString();
                    String uri = editTextAnswerUri.getText().toString();

                    boolean isCorrect = true;

                    if (currentFlashcard.isMultipleChoice()) {

                        isCorrect = radioButtonAnswerCorrect.isChecked();
                    }

                    try {

                        JSONObject jsonObject = new JSONObject();

                        JSONObject jsonObjectAnswer = new JSONObject();

                        jsonObjectAnswer.put("answerText", text);
                        jsonObjectAnswer.put("answerHint", hint);
                        jsonObjectAnswer.put("mediaURI", uri);
                        jsonObjectAnswer.put("answerCorrect", isCorrect);

                        JSONObject jsonObjectAuthor = new JSONObject();
                        jsonObjectAuthor.put("userId", db.getLoggedInUser().getId());

                        jsonObjectAnswer.put("author", jsonObjectAuthor);

                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(jsonObjectAnswer);

                        jsonObject.put("answers", jsonArray);

                        // final view to access it from innerclass
                        final View view = v;

                        AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObject, currentFlashcard.getId(), new AsyncPatchRemoteCard.AsyncPatchResponseRemoteCard() {

                            @Override
                            public void processFinish(long id) {

                                //new ContentFlashCard().collectItemFromServer(flashCard.getId(), false);
                                // Load answers
                                ContentFlashCardAnswers contentFlashCardAnswers = new ContentFlashCardAnswers();
                                //contentFlashCardAnswers.collectItemsFromDb(flashCard.getId(), false, false);
                                contentFlashCardAnswers.collectItemsFromServer(currentFlashcard.getId(), false, false);

                                editTextAnswerText.setText(null);
                                editTextAnswerHint.setText(null);
                                editTextAnswerUri.setText(null);

                                // hides the softkeyboard
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        });

                        if (ProcessConnectivity.isOk(getContext())) {

                            task.execute();
                        } else {

                            Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                            return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

            }
        });

    }


    /**
     * Sets the icon for the fab
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     */
    private void toggleStateAndFabIcon(boolean isInital) {


        switch (state) {

            case PLAY_QUESTION:

                if (!isInital) {

                    // count ups
                    position++;

                    // get loop if end is reached
                    position %= statistics.size();

                    // get flashcard
                    // TODO sync with server
                    currentFlashcard = db.getFlashCard(statistics.get(position).getCardId());
                }

                setContent();

                setMedia();

                setListener();

                if (currentFlashcard.isMultipleChoice()) {

                    fab.setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_check));

                } else {

                    fab.setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_list));
                }

                // Next state will be to show answers if they aren't
                // already visible because they are of type multiple choice
                state = Constants.PLAY_ANSWER;

                break;

            case PLAY_ANSWER:

                fab.setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_arrow_forward));

                if (currentFlashcard.isMultipleChoice()) {

                    contentAnswers.validateAnswers(statistic);

                } else {

                    // Do nothing else just show the answers
                    nsContentAnswers.setVisibility(View.VISIBLE);
                }

                if (message != null) {

                    db.saveChallenge(message.getId(), statistic.getId());

                    Log.d("challenge",db.getChallengeByMessageId(message.getId()).isCompleted()+"");


                }

                // Next state will be to play next card
                state = Constants.PLAY_QUESTION;

                if (!currentFlashcard.isMultipleChoice()) {

                    state = Constants.PLAY_DIALOG;
                }

                break;

            case PLAY_DIALOG:

                // Custom Dialog which saves a statistic
                new DialogKnowledge(getContext(), statistic).show();
                state = Constants.PLAY_QUESTION;
                break;

        }

        //toggleVisibilityAnswers();


    }


    /**
     * contentAnswers
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-13
     *x
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.imageview_card_media_play:

                // starts the youtube player
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,currentFlashcard.getQuestion().getUri()));
                break;

            case R.id.fab:

                toggleStateAndFabIcon(false);
                break;
        }

    }


    /**
     * Sets the message
     * null if usual mode
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param message
     */
    public void setMessage(Message message) {

        this.message = message;

    }


    /**
     * Sets the fab or navigating if this fragment is not placed in the mainActivity where you can
     * get the fab from the globals
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-17
     *
     * @param fab
     */
    public void setFab(FloatingActionButton fab) {

        if (fab != null) {

            this.fab = fab;
        } else {

            this.fab = Globals.getFloatingActionButton();
        }
    }



    /**
     * Validates if the text is not empty and the uri is valid
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @return
     */
    private boolean validateAnswer() {

        return ValidatorInput.isNotEmpty(editTextAnswerText) && ValidatorInput.isValidUri(editTextAnswerUri);
    }
}
