package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.DELETE.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.GET.AsyncGetRemoteCarddecks;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.PATCH.AsyncPatchRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.HashtagParser;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.ValidatorInput;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFlashCardFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFlashCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFlashCard extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FlashCard flashCard;

    private DbManager db = Globals.getDb();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFlashCardFragmentInteractionListener mListener;
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

    private WebView webViewUri;

    private Button buttonAddAnswer;
    private Button buttonAnswerEditorSave;

    private EditText editTextAnswerText;
    private EditText editTextAnswerHint;
    private EditText editTextAnswerUri;

    private EditText editTextQuestionUri;
    private EditText editTextQuestionText;

    private TextInputLayout textInputLayoutUri;
    private TextInputLayout textInputLayoutContent;

    private RadioGroup radioGroupAnswerCorrect;
    private RadioButton radioButtonAnswerCorrect;
    private RadioButton radioButtonAnswerIncorrect;

    private long carddeckId;
    private FloatingActionButton floatingActionButtonSave;
    // Should be invisible
    private FloatingActionButton floatingActionButtonAdd;
    private FloatingActionButton fabEdit;
    private LinearLayout linearlayoutHashTags;
    private EditText editTextQuestionTags;
    private TextInputLayout textInputLayoutTags;

    public FragmentFlashCard() {
        // Required empty public constructor

        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFlashCard.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFlashCard newInstance(String param1, String param2) {
        FragmentFlashCard fragment = new FragmentFlashCard();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_flashcard_parallax, container, false);

        Globals.getFloatingActionButton().setVisibility(View.GONE);

        // Load answers
        ContentFlashCardAnswers contentFlashCardAnswers = new ContentFlashCardAnswers();
        contentFlashCardAnswers.collectItemsFromDb(flashCard.getId(), false, false);
        contentFlashCardAnswers.collectItemsFromServer(flashCard.getId(), false, false);

        // Make toolbar of activity main invisible
        Globals.setVisibilityToolbarMain(View.GONE);

        mIdView = (TextView) view.findViewById(R.id.id);
        mContentView = (TextView) view.findViewById(R.id.content);
        mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

        // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
        mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
        mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
        mIsCorrect = (ImageView) view.findViewById(R.id.image_view_iscorrect);
        mIsCorrect.setVisibility(flashCard.isMultipleChoice() ? View.VISIBLE : View.GONE);

        mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

        imageViewUri = (ImageView) view.findViewById(R.id.image_view_question_uri);

        webViewUri = (WebView) view.findViewById(R.id.webview_card_question);

        imageViewVoteDown = (ImageView) view.findViewById(R.id.button_down_vote);
        imageViewVoteUp = (ImageView) view.findViewById(R.id.button_up_vote);

        imageViewEditQuestion = (ImageView) view.findViewById(R.id.imageview_question_edit);
        imageViewSaveQuestion = (ImageView) view.findViewById(R.id.imageview_question_save);

        buttonAddAnswer = (Button) view.findViewById(R.id.button_add_answer);
        buttonAnswerEditorSave = (Button) view.findViewById(R.id.button_answer_editor_save);

        editTextAnswerText = (EditText) view.findViewById(R.id.edittext_answer_text);
        editTextAnswerHint = (EditText) view.findViewById(R.id.edittext_answer_hint);
        editTextAnswerUri = (EditText) view.findViewById(R.id.edittext_answer_uri);

        textInputLayoutUri = (TextInputLayout) view.findViewById(R.id.textInputLayout_uri);
        textInputLayoutContent = (TextInputLayout) view.findViewById(R.id.textInputLayout_content);

        editTextQuestionUri = (EditText) view.findViewById(R.id.edittext_uri);
        editTextQuestionText = (EditText) view.findViewById(R.id.edittext_content);
        editTextQuestionTags = (EditText) view.findViewById(R.id.edittext_tags);

        textInputLayoutTags = (TextInputLayout) view.findViewById(R.id.textInputLayout_tags);
        linearlayoutHashTags = (LinearLayout) view.findViewById(R.id.ll_hash_tags);

        imageViewPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);
        radioGroupAnswerCorrect = (RadioGroup) view.findViewById(R.id.radio_buttongroup_answer_editor);
        radioButtonAnswerCorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_correct);
        radioButtonAnswerIncorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_incorrect);

        floatingActionButtonSave = (FloatingActionButton) view.findViewById(R.id.fab_answer_save);
        floatingActionButtonAdd = (FloatingActionButton) view.findViewById(R.id.fab_answer_add);
        floatingActionButtonSave = (FloatingActionButton) view.findViewById(R.id.fab_card_details_answer_add);

        buttonAnswerEditorSave.setOnClickListener(this);
        buttonAnswerEditorSave.setVisibility(View.GONE);
        floatingActionButtonAdd.setVisibility(View.GONE);
        floatingActionButtonSave.setOnClickListener(this);

        mContentView.setText(Html.fromHtml(flashCard.getQuestion().getQuestionText()));
        mAuthorView.setText(flashCard.getQuestion().getAuthor().getName());
        mCardRatingView.setText(flashCard.getRatingForView());
        mDateView.setText(flashCard.getLastUpdatedString());


        createTagViewWithListener();

        fabEdit = (FloatingActionButton) view.findViewById(R.id.fab_card_edit);
        fabEdit.setVisibility(View.GONE);

        checkIfEditable();

        setMedia();

        if (!isUpToDate) {

            mLocalView.setVisibility(View.GONE);
        } else {

            mLocalView.setVisibility(View.VISIBLE);
        }

        if (flashCard.isMultipleChoice()) {

            radioButtonAnswerCorrect.setChecked(true);
            radioButtonAnswerIncorrect.setChecked(false);
            radioGroupAnswerCorrect.setVisibility(View.VISIBLE);
        } else {

            radioButtonAnswerCorrect.setChecked(true);
            radioButtonAnswerIncorrect.setChecked(false);
            radioGroupAnswerCorrect.setVisibility(View.GONE);
        }

        int voting = db.getCardVoting(flashCard.getId());

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

        setListener();

        return view;

    }


    /**
     * Creates textviews for every single tag and adds an listener
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-04
     */
    private void createTagViewWithListener() {

        AppBarLayout.LayoutParams lparams = new AppBarLayout.LayoutParams(
                AppBarLayout.LayoutParams.WRAP_CONTENT, AppBarLayout.LayoutParams.WRAP_CONTENT);
        lparams.rightMargin = 10;

        for (Tag tag : flashCard.getTags()) {

            // TODO TAG

            TextView tv = new TextView(getContext());
            tv.setLayoutParams(lparams);
            tv.setTag(tag.getId());
            tv.setText(tag.getName());

            linearlayoutHashTags.addView(tv);

            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.d("click", v.getTag() + " tag");
                    // TODO add some logic
                }
            });
        }
    }


    /**
     * Check if this card is created by the user and is edible
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     */
    private void checkIfEditable() {

        // check if the question was created by the current user - so set the edit button enabled
        if (flashCard.getQuestion().getAuthor().getId() == db.getLoggedInUser().getId()) {

            fabEdit.setVisibility(View.VISIBLE);
            fabEdit.setTag("mode_edit");
            fabEdit.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_mode_edit));
            fabEdit.setOnClickListener(this);

        }
    }


    /**
     * Sets the mediatype and content
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     */
    private void setMedia() {

        Log.d("hier media", flashCard.getAnswers().size()+"");

        if (flashCard.getQuestion().getUri() != null && flashCard.getQuestion().getUri().toString() != "") {

            String uriString = flashCard.getQuestion().getUri().toString().toLowerCase();

            if (uriString.contains("youtube")) {

                imageViewPlay.setVisibility(View.VISIBLE);
                imageViewPlay.setOnClickListener(this);
            }

            if (uriString.endsWith("jpg") || uriString.endsWith(".png") || uriString.contains("youtube")) {

                ProcessorImage.download(flashCard.getQuestion().getUri().toString(), imageViewUri, flashCard.getQuestion().getId(), "_question");
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

        final long cardID = flashCard.getId();

        /**
         * Sets the clicklistener to vote a cards rating down
         */
        imageViewVoteDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int lastVoting = db.getCardVoting(cardID);

                if (!db.saveCardVoting(cardID, -1)) {

                    Toast.makeText(getContext(), getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
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

                int lastVoting = db.getCardVoting(cardID);
                    if (!db.saveCardVoting(cardID, +1)) {

                        Toast.makeText(getContext(), getResources().getText(R.string.voting_already_voted), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onResume() {
        super.onResume();

        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);
    }



    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFlashCardFragmentInteractionListener) {

            mListener = (OnFlashCardFragmentInteractionListener) context;
        } else {

            throw new RuntimeException(context.toString()
                    + " must implement OnFlashCardFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();

        fabEdit.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_mode_edit));
        fabEdit.setOnClickListener(null);
        fabEdit.setVisibility(View.GONE);
        fabEdit.setTag(null);
        mListener = null;
    }


    /**
     * Use setter after instantiate this fragment
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param flashCard
     */
    public void setItem(FlashCard flashCard) {

        this.flashCard = flashCard;
    }


    /**
     * Sets if the data is synced with the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     *
     * @param isUpToDate
     */
    public void setUpToDate(boolean isUpToDate) {
        this.isUpToDate = isUpToDate;
    }


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2016-12-30
     *
     * @param v
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.fab_card_edit:

                if (fabEdit.getTag().equals("mode_edit")) {

                    if (ProcessConnectivity.isOk(getContext())) {

                        fabEdit.setTag("mode_save");
                        fabEdit.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_check));
                        setQuestionInEditMode();
                    } else {

                        Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                        return;
                    }

                } else if (fabEdit.getTag().equals("mode_save")){

                    if (ProcessConnectivity.isOk(getContext())) {

                        saveQuestion();
                        fabEdit.setTag("mode_edit");
                        fabEdit.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_mode_edit));

                        // hides the softkeyboard
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    } else {

                        Snackbar.make(v, getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
                        return;
                    }

                }

                break;

            //save answer
            case R.id.fab_card_details_answer_add: // TODO Unused
            case R.id.fab_answer_save: // TODO unused
            case R.id.button_answer_editor_save:

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

                    if (this.flashCard.isMultipleChoice()) {

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

                        AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObject, this.flashCard.getId(), new AsyncPatchRemoteCard.AsyncPatchResponseRemoteCard() {

                            @Override
                            public void processFinish(long id) {

                                //new ContentFlashCard().collectItemFromServer(flashCard.getId(), false);
                                // Load answers
                                ContentFlashCardAnswers contentFlashCardAnswers = new ContentFlashCardAnswers();
                                //contentFlashCardAnswers.collectItemsFromDb(flashCard.getId(), false, false);
                                contentFlashCardAnswers.collectItemsFromServer(flashCard.getId(), false, false);

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

                break;

            case R.id.imageview_card_media_play:

                // starts the youtube player
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,flashCard.getQuestion().getUri()));
                break;

            case R.id.imageview_question_edit:

                setQuestionInEditMode();

                break;

            case R.id.imageview_question_save:

                saveQuestion();

                break;
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


    /**
     * Saves a question
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-08
     *
     */
    private void saveQuestion() {

        String newUri = editTextQuestionUri.getText().toString();
        String newQuestionText = editTextQuestionText.getText().toString();
        List<String> tagStrings = HashtagParser.parse(editTextQuestionTags.getText().toString());

        // sets the new values to the flashcard
        flashCard.getQuestion().setQuestionText(newQuestionText);
        flashCard.getQuestion().setUri(Uri.parse(newUri));

        mContentView.setText(Html.fromHtml(editTextQuestionText.getText().toString()));

        mContentView.setVisibility(View.VISIBLE);
        textInputLayoutContent.setVisibility(View.GONE);
        textInputLayoutUri.setVisibility(View.GONE);

        textInputLayoutTags.setVisibility(View.GONE);
        linearlayoutHashTags.setVisibility(View.VISIBLE);

        JSONObject jsonObjectQuestion = new JSONObject();
        JSONObject questionData = new JSONObject();
        JSONObject author = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        JSONArray jsonArrayTags = new JSONArray();

        try {

            // Get tags and set them to array
            for (String tag : tagStrings) {

                JSONObject jsonObjectTag = new JSONObject();
                jsonObjectTag.put(JsonKeys.TAG_NAME, tag);
                jsonArrayTags.put(jsonObjectTag);
            }

            // only set tags attribute when necessary
            if (tagStrings.size() > 0) {

                jsonObjectQuestion.put(JsonKeys.FLASHCARD_TAGS, jsonArrayTags);
            }

            jsonUser.put(JsonKeys.USER_ID, db.getLoggedInUser().getId());
            author.put(JsonKeys.AUTHOR, jsonUser);

            questionData.put(JsonKeys.QUESTION_TEXT, newQuestionText);
            questionData.put(JsonKeys.URI, newUri);
            jsonObjectQuestion.put(JsonKeys.FLASHCARD_QUESTION, questionData);

            //jsonObjectQuestion.put(JsonKeys.AUTHOR, jsonUser);
            questionData.put(JsonKeys.AUTHOR, jsonUser);

        } catch (JSONException e) {

            Log.d("Flascard", "init json for question update");
            e.printStackTrace();
        }

        AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObjectQuestion, this.flashCard.getId(), new AsyncPatchRemoteCard.AsyncPatchResponseRemoteCard() {

            @Override
            public void processFinish(long id) {

                Log.d("antwort", id+"");
                new ContentFlashCard().collectItemFromServer(flashCard.getId(), false);
            }
        });

        if (ProcessConnectivity.isOk(getContext())) {

            task.execute();
        } else {

            Snackbar.make(this.getView(), getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_LONG).show();
            return;
        }

    }


    /**
     * Sets the question in edit mode
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-08
     */
    private void setQuestionInEditMode() {

        mContentView.setVisibility(View.GONE);
        textInputLayoutContent.setVisibility(View.VISIBLE);
        textInputLayoutUri.setVisibility(View.VISIBLE);
        textInputLayoutTags.setVisibility(View.VISIBLE);

        linearlayoutHashTags.setVisibility(View.GONE);

        imageViewSaveQuestion.setOnClickListener(this);

        editTextQuestionText.setText(flashCard.getQuestion().getQuestionText());
        editTextQuestionUri.setText(flashCard.getQuestion().getUri().toString());
        editTextQuestionTags.setText(HashtagParser.getString(flashCard.getTags()));
    }

    public void setCarddeckId(long carddeckId) {
        this.carddeckId = carddeckId;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFlashCardFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

 }
