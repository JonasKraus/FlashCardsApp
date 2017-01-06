package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.AsyncDeleteRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.AsyncPatchRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.AsyncPostRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.AsyncPostRemoteRating;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Question;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;

import static com.google.android.gms.analytics.internal.zzy.v;
import static de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCardAnswers.fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFlashCardFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFlashCardCreate#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFlashCardCreate extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private FlashCard flashCard;

    private DbManager db = Globals.getDb();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private boolean isUpToDate;
    private TextView mIdView;
    private ImageView mLocalView;
    private ImageView imageViewUri;
    private ImageView imageViewPlay;

    private WebView webViewUri;

    private ImageButton buttonAnswerEditorAdd;

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

    private FragmentFlashCardAnswers fragmentAnswers;
    private ArrayList<Answer> answers;

    public FragmentFlashCardCreate() {
        // Required empty public constructor
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
    public static FragmentFlashCardCreate newInstance(String param1, String param2) {
        FragmentFlashCardCreate fragment = new FragmentFlashCardCreate();
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
        // Inflate the layout for this fragment TODO
        View view = inflater.inflate(R.layout.fragment_flashcard_parallax_create, container, false);

        this.flashCard = new FlashCard(db.getLoggedInUser(), null, new Question("","",db.getLoggedInUser()),false);

        mIdView = (TextView) view.findViewById(R.id.id);

        // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
        mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

        imageViewUri = (ImageView) view.findViewById(R.id.image_view_question_uri);

        webViewUri = (WebView) view.findViewById(R.id.webview_card_question);

        buttonAnswerEditorAdd = (ImageButton) view.findViewById(R.id.button_answer_editor_add);
        buttonAnswerEditorAdd.setVisibility(View.VISIBLE);
        Button buttonSaveAnswer = (Button) view.findViewById(R.id.button_answer_editor_save);
        buttonSaveAnswer.setVisibility(View.GONE);
        buttonAnswerEditorAdd.setOnClickListener(this);

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


        editTextQuestionText.setText(flashCard.getQuestion().getQuestionText());
        editTextQuestionUri.setText(flashCard.getQuestion().getUri().toString());

        setMedia();

        fragmentAnswers = new FragmentFlashCardAnswers();
        answers = new ArrayList<Answer>();

        fragmentAnswers.setUpToDate(false);

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                Globals.getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container_card_answer, fragmentAnswers);
        fragmentTransaction.commit();

        radioGroupAnswerCorrect.setVisibility(View.VISIBLE);

        Globals.getFloatingActionButton().setOnClickListener(this);

        Globals.getFloatingActionButton().setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_save));

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


    @Override
    public void onResume() {
        super.onResume();
        // TODO Jonas hier kommt der code
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {

        super.onDetach();

        // reset action button
        Globals.getFloatingActionButton().setOnClickListener(null);
        Globals.getFloatingActionButton().setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_school));
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

            case R.id.button_answer_editor_add:

                addAnswerToListView();

                break;

            //save answer
            case R.id.fab:

                String answerText = editTextAnswerText.getText().toString();
                // Check if the text is not empty
                if (answerText != null && !answerText.equals("")) {

                    addAnswerToListView();
                }

                saveQuestion();

                break;
            case R.id.imageview_card_media_play:

                // starts the youtube player
                getContext().startActivity(new Intent(Intent.ACTION_VIEW,flashCard.getQuestion().getUri()));
                break;
        }


        hideSoftKeyboard(v);
    }


    /**
     * hides the softkeyboard
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     *
     * @param v
     */
    private void hideSoftKeyboard(View v) {

        // hides the softkeyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    /**
     * Adds the answer from the editor to the list view
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     *
     */
    private void addAnswerToListView() {

        String answerText = editTextAnswerText.getText().toString();

        if (answerText == null || answerText.equals("")) {

            Toast.makeText(getContext(), getContext().getText(R.string.insert_text), Toast.LENGTH_SHORT).show();
            return;
        }

        editTextAnswerText.setText(null);
        String answerHint = editTextAnswerHint.getText().toString();
        editTextAnswerHint.setText(null);
        String answerUri = editTextAnswerUri.getText().toString();
        editTextAnswerUri.setText(null);


        Boolean isCorrect = true;

        if (this.flashCard.isMultipleChoice()) {

            isCorrect = radioButtonAnswerCorrect.isChecked();
        }

        answers.add(new Answer(answerText, answerHint, answerUri, isCorrect, db.getLoggedInUser()));
        fragmentAnswers.setItemList(answers);

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                Globals.getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container_card_answer, fragmentAnswers);
        fragmentTransaction.commit();

        RecyclerViewAdapterFlashCardAnswers recyclerViewAdapterFlashCardAnswers = new RecyclerViewAdapterFlashCardAnswers(answers, null, false);

        fragmentAnswers.getRecyclerView().setAdapter(recyclerViewAdapterFlashCardAnswers);
    }


    /**
     * Saves a question to the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void saveQuestion() {

        String newUri = editTextQuestionUri.getText().toString();
        String newQuestionText = editTextQuestionText.getText().toString();

        String answerText = editTextAnswerText.getText().toString();
        String answerHint = editTextAnswerHint.getText().toString();
        String answerUri = editTextAnswerUri.getText().toString();

        Boolean isCorrect = true;

        if (this.flashCard.isMultipleChoice()) {

            isCorrect = radioButtonAnswerCorrect.isChecked();
        }

        // sets the new values to the flashcard
        flashCard.getQuestion().setQuestionText(newQuestionText);
        flashCard.getQuestion().setUri(Uri.parse(newUri));


        // TODO Start async task to save answer
        // TODO reload question

        JSONObject jsonObjectQuestion = new JSONObject();
        JSONObject questionData = new JSONObject();
        JSONObject author = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        JSONObject jsonObjectAnswers = new JSONObject();

        try {

            jsonUser.put(JsonKeys.USER_ID, db.getLoggedInUser().getId());
            //author.put(JsonKeys.AUTHOR, jsonUser);
            questionData.put(JsonKeys.QUESTION_TEXT, newQuestionText);
            questionData.put(JsonKeys.URI, newUri);
            jsonObjectQuestion.put(JsonKeys.FLASHCARD_QUESTION, questionData);
            jsonObjectQuestion.put(JsonKeys.AUTHOR, jsonUser);
            questionData.put(JsonKeys.AUTHOR, jsonUser);

            JSONObject jsonObjectAuthor = new JSONObject();
            jsonObjectAuthor.put("userId", db.getLoggedInUser().getId());

            JSONArray jsonArray = new JSONArray();

            // Iterates over all temporary saved answers
            for (Answer a : answers) {

                JSONObject jsonObjectAnswer = new JSONObject();
                jsonObjectAnswer.put("answerText", a.getAnswerText());
                jsonObjectAnswer.put("answerHint", a.getHintText());
                jsonObjectAnswer.put("mediaURI", a.getUri());
                jsonObjectAnswer.put("answerCorrect", a.isCorrect());
                jsonObjectAnswer.put("author", jsonObjectAuthor);

                jsonArray.put(jsonObjectAnswer);
            }

            jsonObjectQuestion.put("answers", jsonArray);

        } catch (JSONException e) {

            Log.d("Flascard", "init json for question update");
            e.printStackTrace();
        }

        Log.d("create", "new flashcard " + jsonObjectQuestion.toString());
        AsyncPostRemoteCard task = new AsyncPostRemoteCard(jsonObjectQuestion);

        if (ProcessConnectivity.isOk(getContext(), true)) {

            task.execute(this.carddeckId);
        }

        // TODO new ContentFlashCard().collectItemFromServer(flashCard.getId(), getFragmentManager(), progressBar, false, db);    }

    }


    /**
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     *
     * Saves and reloads the answers
     */
    private void saveAnswerAndReload(View view) {

        String answerText = editTextAnswerText.getText().toString();
        String answerHint = editTextAnswerHint.getText().toString();
        String answerUri = editTextAnswerUri.getText().toString();

        // Check if edittext is empty
        if (answerText.equals("")) {

            Snackbar.make(view, getContext().getResources().getText(R.string.insert_text), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return;
        }

        boolean isCorrect = true;

        if (this.flashCard.isMultipleChoice()) {

            isCorrect = radioButtonAnswerCorrect.isChecked();
        }

        try {

            JSONObject jsonObject = new JSONObject();

            JSONObject jsonObjectAnswer = new JSONObject();

            jsonObjectAnswer.put("answerText", answerText);
            jsonObjectAnswer.put("answerHint", answerHint);
            jsonObjectAnswer.put("mediaURI", answerUri);
            jsonObjectAnswer.put("answerCorrect", isCorrect);

            JSONObject jsonObjectAuthor = new JSONObject();
            jsonObjectAuthor.put("userId", db.getLoggedInUser().getId());

            jsonObjectAnswer.put("author", jsonObjectAuthor);

            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObjectAnswer);

            jsonObject.put("answers", jsonArray);

            AsyncPatchRemoteCard task = new AsyncPatchRemoteCard(jsonObject, this.flashCard.getId());

            if (ProcessConnectivity.isOk(getContext())) {

                task.execute();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        new ContentFlashCardAnswers().collectItemsFromDb(flashCard.getId(), false);
        new ContentFlashCardAnswers().collectItemsFromServer(flashCard.getId(), false);


        editTextAnswerText.setText(null);
        editTextAnswerHint.setText(null);
        editTextAnswerUri.setText(null);

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
