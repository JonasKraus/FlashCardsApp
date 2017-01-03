package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncPatchRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTasks.Remote.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;


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

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFlashCardFragmentInteractionListener mListener;
    private DbManager db;
    private boolean isUpToDate;
    private TextView mIdView;
    private TextView mContentView;
    private TextView mAuthorView;
    private TextView mCardRatingView;
    private TextView mDateView;
    private ImageView mBookmarkView;
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

    private ProgressBar progressBar;
    private ProgressDialog progressBarWebView;

    public FragmentFlashCard() {
        // Required empty public constructor
    }


    /**
     * Sets the progressbar
     *
     * @param progressBar
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
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

        /*
        ListView listview =(ListView) view.findViewById(R.id.listview_answers);

        if (view instanceof RecyclerView) {
            Log.d("recycler", view.toString());
        }
        String[] items = new String[] {"Item 1", "Item 2", "Item 3","Item 1", "Item 2", "Item 3","Item 1", "Item 2", "Item 3","Item 1", "Item 2", "Item 3","Item 1", "Item 2", "Item 3","Item 1", "Item 2", "Item 3"};
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, items);

        listview.setAdapter(adapter);
        */

        new ContentFlashCardAnswers().collectItemsFromServer(flashCard.getId(), getFragmentManager(), progressBar, false, db);


        mIdView = (TextView) view.findViewById(R.id.id);
        mContentView = (TextView) view.findViewById(R.id.content);
        mAuthorView = (TextView) view.findViewById(R.id.textView_listItem_author);

        // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
        mCardRatingView = (TextView) view.findViewById(R.id.text_view_listItem_card_rating);
        mDateView = (TextView) view.findViewById(R.id.text_view_listItem_date);
        mBookmarkView = (ImageView) view.findViewById(R.id.image_view_iscorrect);
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

        imageViewPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);
        radioGroupAnswerCorrect = (RadioGroup) view.findViewById(R.id.radio_buttongroup_answer_editor);
        radioButtonAnswerCorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_correct);
        radioButtonAnswerIncorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_incorrect);

        buttonAnswerEditorSave.setOnClickListener(this);

        mContentView.setText(flashCard.getQuestion().getQuestionText());
        mAuthorView.setText(flashCard.getQuestion().getAuthor().getName());
        mCardRatingView.setText(flashCard.getRatingForView());
        mDateView.setText(flashCard.getLastUpdatedString());

        //misCorrectView =; TODO

        // check if the question was created by the current user - so set the edit button enabled
        if (flashCard.getQuestion().getAuthor().getId() == db.getLoggedInUser().getId()) {

            imageViewEditQuestion.setAlpha(1f);
            imageViewEditQuestion.setOnClickListener(this);

        } else {
            imageViewEditQuestion.setAlpha(.1f);

        }

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

        /*
        FrameLayout container1 = (FrameLayout) view.findViewById(R.id.fragment_container_card_answer);
        RecyclerView recyclerView = (RecyclerView) container1.findViewById(R.id.list);
        recyclerView.setAdapter(new RecyclerViewAdapterFlashCardAnswers(db, itemList, mListener, isUpToDate, context));*/

        return view;

    }


    /**
     * Sets the mediatype and content
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     */
    private void setMedia() {

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

                    uriString = "https://" + uriString;

                } else if (uriString.equals("")) {

                    uriString = "http://134.60.51.72:9000/";
                }

                WebSettings settings = webViewUri.getSettings();
                settings.setJavaScriptEnabled(true);
                webViewUri.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                progressBar.setVisibility(View.VISIBLE);

                webViewUri.setWebViewClient(new WebViewClient() {

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
                        taskDelete.execute();
                    }

                    AsyncPostRemoteRating task = new AsyncPostRemoteRating("flashcard", cardID, db.getLoggedInUser().getId(), -1, db);
                    task.execute();

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
                            taskDelete.execute();
                        }

                        AsyncPostRemoteRating task = new AsyncPostRemoteRating("flashcard", cardID, db.getLoggedInUser().getId(), 1, db);
                        task.execute();

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
        // TODO Jonas hier kommt der code
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        mListener = null;
    }

    public void setItem(FlashCard flashCard) {
        this.flashCard = flashCard;
    }

    public void setDb(DbManager db) {
        this.db = db;
    }

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

            //save answer
            case R.id.button_answer_editor_save:

                String text = editTextAnswerText.getText().toString();
                String hint = editTextAnswerHint.getText().toString();
                String uri = editTextAnswerUri.getText().toString();

                // Check if edittext is empty
                if (text.equals("")) {

                    Toast.makeText(getContext(), getContext().getResources().getText(R.string.insert_text), Toast.LENGTH_SHORT).show();
                    break;
                }

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

                    AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObject, this.flashCard.getId());
                    task.execute();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                new ContentFlashCardAnswers().collectItemsFromServer(flashCard.getId(), getFragmentManager(), progressBar, false, db);


                editTextAnswerText.setText(null);
                editTextAnswerHint.setText(null);
                editTextAnswerUri.setText(null);

                // hides the softkeyboard
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                break;

            case R.id.imageview_card_media_play:

                // starts the youtube player
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,flashCard.getQuestion().getUri()));
                break;

            case R.id.imageview_question_edit:

                mContentView.setVisibility(View.GONE);
                textInputLayoutContent.setVisibility(View.VISIBLE);
                textInputLayoutUri.setVisibility(View.VISIBLE);
                imageViewEditQuestion.setVisibility(View.GONE);
                imageViewSaveQuestion.setVisibility(View.VISIBLE);

                imageViewSaveQuestion.setOnClickListener(this);

                editTextQuestionText.setText(flashCard.getQuestion().getQuestionText());
                editTextQuestionUri.setText(flashCard.getQuestion().getUri().toString());

                break;

            case R.id.imageview_question_save:

                String newUri = editTextQuestionUri.getText().toString();
                String newQuestionText = editTextQuestionText.getText().toString();

                mContentView.setText(editTextQuestionText.getText());

                mContentView.setVisibility(View.VISIBLE);
                textInputLayoutContent.setVisibility(View.GONE);
                textInputLayoutUri.setVisibility(View.GONE);
                imageViewEditQuestion.setVisibility(View.VISIBLE);
                imageViewSaveQuestion.setVisibility(View.GONE);


                // TODO Start async task to save answer
                // TODO reload question

                JSONObject jsonObjectQuestion = new JSONObject();
                JSONObject questionData = new JSONObject();
                JSONObject author = new JSONObject();
                JSONObject jsonUser = new JSONObject();


                try {

                    jsonUser.put("userId", db.getLoggedInUser().getId());
                    author.put("author", jsonUser);
                    questionData.put("questionText", newQuestionText);
                    questionData.put("mediaURI", newUri);
                    jsonObjectQuestion.put("question", questionData);
                    questionData.put("author", jsonUser);

                } catch (JSONException e) {

                    Log.d("Flascard", "init json for question update");
                    e.printStackTrace();
                }

                AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObjectQuestion, flashCard.getId());
                task.execute();

                new ContentFlashCard().collectItemFromServer(flashCard.getId(), getFragmentManager(), progressBar, false, db);

                break;
        }
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
