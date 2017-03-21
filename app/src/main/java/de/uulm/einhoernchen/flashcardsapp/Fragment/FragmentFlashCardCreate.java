package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteCard;
import de.uulm.einhoernchen.flashcardsapp.AsyncTask.Remote.POST.AsyncPostRemoteImage;
import de.uulm.einhoernchen.flashcardsapp.Const.Constants;
import de.uulm.einhoernchen.flashcardsapp.Const.Routes;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterFlashCardAnswers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Dataset.ContentFlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Answer;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.Question;
import de.uulm.einhoernchen.flashcardsapp.Model.Tag;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.HashtagParser;
import de.uulm.einhoernchen.flashcardsapp.Util.JsonKeys;
import de.uulm.einhoernchen.flashcardsapp.Util.PermissionManager;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessConnectivity;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.Util.ValidatorInput;

import static android.app.Activity.RESULT_OK;


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


    private static final int MY_INTENT_CLICK_GALLERY = 303;
    private static final int MY_INTENT_CLICK_CAMERA = 304;

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
    private EditText editTextQuestionTags;

    private TextInputLayout textInputLayoutUri;
    private TextInputLayout textInputLayoutContent;

    private ImageButton imageButtonInsertImage;
    private ImageButton imageButtonDeleteImage;

    private RadioGroup radioGroupAnswerCorrect;
    private RadioButton radioButtonAnswerCorrect;
    private RadioButton radioButtonAnswerIncorrect;

    private CheckBox checkBoxMultipleChoice;

    private long carddeckId;

    private FragmentFlashCardAnswers fragmentAnswers;
    private ArrayList<Answer> answers;

    private FloatingActionButton floatingActionButtonCardCreate;
    private FloatingActionButton floatingActionButtonAnswerAdd;
    private FloatingActionButton floatingActionButtonAnswerSave;
    private FloatingActionButton floatingActionButtonCardAnswerAdd;
    private Bitmap bitmapQuestion;


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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        // TODO Jonas check wich items to inflate or disable
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
        final View view = inflater.inflate(R.layout.fragment_flashcard_parallax_create, container, false);

        // Make toolbar of activity main invisible
        Globals.setVisibilityToolbarMain(View.GONE);

        // create dummy card
        this.flashCard = new FlashCard(db.getLoggedInUser(), null, new Question("","",db.getLoggedInUser()),false);

        mIdView = (TextView) view.findViewById(R.id.id);

        // mGroupRatingView = (TextView) view.findViewById(R.id.text_view_listItem_group_rating);
        mLocalView = (ImageView) view.findViewById(R.id.image_view_offline);

        imageViewUri = (ImageView) view.findViewById(R.id.image_view_question_uri);

        webViewUri = (WebView) view.findViewById(R.id.webview_card_question);

        buttonAnswerEditorAdd = (ImageButton) view.findViewById(R.id.button_answer_editor_add);
        buttonAnswerEditorAdd.setVisibility(View.GONE);

        Button buttonSaveAnswer = (Button) view.findViewById(R.id.button_answer_editor_save);
        buttonSaveAnswer.setVisibility(View.GONE);
        buttonAnswerEditorAdd.setOnClickListener(this);

        editTextAnswerText = (EditText) view.findViewById(R.id.edittext_answer_text);
        editTextAnswerHint = (EditText) view.findViewById(R.id.edittext_answer_hint);
        editTextAnswerUri = (EditText) view.findViewById(R.id.edittext_answer_uri);

        imageButtonInsertImage = (ImageButton) view.findViewById(R.id.button_insert_image);
        imageButtonDeleteImage = (ImageButton) view.findViewById(R.id.button_delete_image);

        textInputLayoutUri = (TextInputLayout) view.findViewById(R.id.textInputLayout_uri);
        textInputLayoutContent = (TextInputLayout) view.findViewById(R.id.textInputLayout_content);

        editTextQuestionUri = (EditText) view.findViewById(R.id.edittext_uri);

        /* TODO uncomment to automatically show uri
        editTextQuestionUri.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(!hasFocus && ValidatorInput.isValidUri(editTextQuestionUri)) {

                    setMedia(editTextQuestionUri.getText().toString());
                }
            }
        });
        */

        editTextQuestionText = (EditText) view.findViewById(R.id.edittext_content);
        editTextQuestionTags = (EditText) view.findViewById(R.id.edittext_tags);

        imageViewPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);
        radioGroupAnswerCorrect = (RadioGroup) view.findViewById(R.id.radio_buttongroup_answer_editor);
        radioButtonAnswerCorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_correct);
        radioButtonAnswerIncorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_incorrect);
        radioButtonAnswerCorrect.setEnabled(false);
        radioButtonAnswerIncorrect.setEnabled(false);

        checkBoxMultipleChoice = (CheckBox) view.findViewById(R.id.checkbox_multiplechoice);

        checkBoxMultipleChoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                radioGroupAnswerCorrect.setEnabled(isChecked);

                if(!isChecked) {

                    radioButtonAnswerCorrect.setChecked(true);
                    radioButtonAnswerCorrect.setEnabled(false);
                    radioButtonAnswerIncorrect.setEnabled(false);
                } else {

                    radioButtonAnswerCorrect.setEnabled(true);
                    radioButtonAnswerIncorrect.setEnabled(true);
                }

            }
        });

        editTextQuestionText.setText(flashCard.getQuestion().getQuestionText());
        editTextQuestionUri.setText(flashCard.getQuestion().getUri().toString());

        String tagsForEditText = "";

        for (Tag tag : flashCard.getTags()) {

            tagsForEditText += "#" + tag.getName();
        }
        editTextQuestionTags.setText(tagsForEditText);

        // the header action button to save the card
        floatingActionButtonCardCreate = (FloatingActionButton) view.findViewById(R.id.fab_card_create);
        floatingActionButtonCardAnswerAdd = (FloatingActionButton) view.findViewById(R.id.fab_card_answer_add);
        floatingActionButtonCardAnswerAdd.setVisibility(View.VISIBLE);
        floatingActionButtonAnswerAdd = (FloatingActionButton) view.findViewById(R.id.fab_answer_add);
        floatingActionButtonAnswerAdd.setVisibility(View.GONE);
        floatingActionButtonAnswerSave = (FloatingActionButton) view.findViewById(R.id.fab_answer_save);
        floatingActionButtonAnswerSave.setVisibility(View.GONE);

        // List for newly added answers
        fragmentAnswers = new FragmentFlashCardAnswers();
        answers = new ArrayList<Answer>();

        fragmentAnswers.setUpToDate(false);

        inflateFragmentAnswer();

        /*
        Globals.getFloatingActionButton().setOnClickListener(this);

        Globals.getFloatingActionButton().setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_save));
        */

        Globals.getFloatingActionButton().setVisibility(View.GONE);
        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);

        floatingActionButtonCardCreate.setOnClickListener(this);
        floatingActionButtonAnswerAdd.setOnClickListener(this);
        floatingActionButtonCardAnswerAdd.setOnClickListener(this);

        imageButtonInsertImage.setOnClickListener(this);
        imageButtonDeleteImage.setOnClickListener(this);

        return view;

    }

    /**
     * Creates the answer fragment
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     */
    private void inflateFragmentAnswer() {

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                Globals.getFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container_card_answer, fragmentAnswers);
        fragmentTransaction.commit();
    }


    /**
     * Sets the mediatype and content
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-03
     */
    private void setMedia(String uriString) {

        Globals.getProgressBar().setVisibility(View.VISIBLE);

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

                uriString = Routes.URL;
            }


            WebSettings settings = webViewUri.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            webViewUri.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);


            webViewUri.setWebViewClient(new WebViewClient() {

                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    view.loadUrl(url);
                    return true;
                }

                public void onPageFinished(WebView view, String url) {

                    if (Globals.getProgressBar().isShown()) {

                        Globals.getProgressBar().setVisibility(View.GONE);
                    }

                    // Set maximal height of webview
                    if (view.getHeight() > Globals.getContext().getResources().getDisplayMetrics().heightPixels) {

                        view.setLayoutParams(new RelativeLayout.LayoutParams(
                                        Globals.getContext().getResources().getDisplayMetrics().widthPixels,
                                        Globals.getContext().getResources().getDisplayMetrics().heightPixels
                                )
                        );
                    }
                }

            });

            webViewUri.loadUrl(uriString);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }


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
        /*
        Globals.getFloatingActionButton().setOnClickListener(null);
        Globals.getFloatingActionButton().setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_school));
        */
        //Globals.getFloatingActionButton().setVisibility(View.VISIBLE);




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

            case R.id.button_insert_image:

                handleInsertUriClick();
                editTextQuestionUri.setEnabled(false);
                imageButtonDeleteImage.setVisibility(View.VISIBLE);

                break;

            case R.id.button_delete_image:

                imageButtonDeleteImage.setVisibility(View.GONE);
                imageViewUri.setImageBitmap(null);
                editTextQuestionUri.setEnabled(true);
                editTextQuestionUri.setText(null);

                break;

            case R.id.fab_answer_add:
            case R.id.fab_card_answer_add:
            case R.id.button_answer_editor_add:

                if (ProcessConnectivity.isOk(this.getContext())) {

                    addAnswerToListView();
                } else {

                    Snackbar.make(v, Globals.getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_SHORT).show();
                }

                break;

            //save answer
            case  R.id.fab_card_create:
            case R.id.fab:

                if (answers.size() > 0
                        && (editTextAnswerText.getText().toString().equals(""))
                        && (editTextAnswerUri.getText().toString().equals(""))
                        && (editTextAnswerHint.getText().toString().equals(""))
                        ) {
                    saveQuestion();
                    return;
                }

                if (!validateQuestion()) return;

                String answerText = editTextAnswerText.getText().toString();
                // Check if the text is not empty
                if (validateAnswer()) {

                    if (ProcessConnectivity.isOk(this.getContext())) {

                        addAnswerToListView();
                    } else {

                        Snackbar.make(v, Globals.getContext().getString(R.string.service_unavailable), Snackbar.LENGTH_SHORT).show();
                    }
                } else {

                    return;
                }

                // Check if multiple choice - so you need to set two answers
                if (answers.size() < 1) {

                    Snackbar.make(v, getContext().getString(R.string.add_one_answer), Snackbar.LENGTH_LONG).show();
                    return;
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

        Boolean isCorrect = true;

        // Check if textfields are valid and not empty
        if (!validateAnswer()) {

            return;
        }

        if (checkBoxMultipleChoice.isChecked()) {

            if (!ValidatorInput.hasCheck(radioGroupAnswerCorrect)) {

                return;
            }

            isCorrect = radioButtonAnswerCorrect.isChecked();
        }

        String answerText = editTextAnswerText.getText().toString();
        editTextAnswerText.setText(null);
        String answerHint = editTextAnswerHint.getText().toString();
        editTextAnswerHint.setText(null);
        String answerUri = editTextAnswerUri.getText().toString();
        editTextAnswerUri.setText(null);

        answers.add(new Answer(answerText, answerHint, answerUri, isCorrect, db.getLoggedInUser(), checkBoxMultipleChoice.isChecked()));
        fragmentAnswers.setItemList(answers);

        createNewAnswerAdapter(answers, null, false);

        inflateFragmentAnswer();
    }


    /**
     * Creates a new adapter to update fragment
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     * @param answers
     * @param mListener
     * @param isUpToDate
     */
    private void createNewAnswerAdapter(ArrayList<Answer> answers, FragmentHome.OnFragmentInteractionListener mListener, boolean isUpToDate) {

        RecyclerViewAdapterFlashCardAnswers recyclerViewAdapterFlashCardAnswers = new RecyclerViewAdapterFlashCardAnswers(answers, null, false, false);

        fragmentAnswers.getRecyclerView().setAdapter(recyclerViewAdapterFlashCardAnswers);

    }


    /**
     * Saves a question to the server
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void saveQuestion() {

        // return if non valid values
        if (!validateQuestion()) return;

        if (!editTextQuestionUri.isEnabled() && !editTextQuestionUri.getText().toString().equals("")) {

            AsyncPostRemoteImage asyncPostRemoteImage = new AsyncPostRemoteImage(new AsyncPostRemoteImage.AsyncPostRemoteImageResponse() {

                @Override
                public void processFinished(String mediaUri) {

                    editTextQuestionUri.setText(mediaUri);
                    saveFinalFlashcard();

                }
            });
            asyncPostRemoteImage.execute(ProcessorImage.getImageUri(getContext(), bitmapQuestion));
        } else {

            saveFinalFlashcard();
        }

    }


    private void saveFinalFlashcard() {

        String newUri = editTextQuestionUri.getText().toString();
        String newQuestionText = editTextQuestionText.getText().toString();
        boolean isMultiplyChoice = checkBoxMultipleChoice.isChecked();
        List<String> tagStrings = HashtagParser.parse(editTextQuestionTags.getText().toString());

        // sets the new values to the flashcard
        flashCard.getQuestion().setQuestionText(newQuestionText);
        flashCard.getQuestion().setUri(Uri.parse(newUri));

        // TODO Start async task to save answer
        // TODO reload question

        JSONArray jsonArrayTags = new JSONArray();
        JSONObject jsonObjectQuestion = new JSONObject();
        JSONObject questionData = new JSONObject();
        JSONObject author = new JSONObject();
        JSONObject jsonUser = new JSONObject();
        JSONObject jsonObjectAnswers = new JSONObject();

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
            //author.put(JsonKeys.AUTHOR, jsonUser);
            questionData.put(JsonKeys.QUESTION_TEXT, newQuestionText);

            questionData.put(JsonKeys.QUESTION_MEDIA_URI, newUri);

            jsonObjectQuestion.put(JsonKeys.FLASHCARD_QUESTION, questionData);
            jsonObjectQuestion.put(JsonKeys.FLASHCARD_MULTIPLE_CHOICE, isMultiplyChoice);
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

            Log.d("Flashcard", "init json for question update");
            e.printStackTrace();
        }

        AsyncPostRemoteCard task = new AsyncPostRemoteCard(jsonObjectQuestion, new AsyncPostRemoteCard.AsyncPostRemoteCardResponse() {

            @Override
            public void processFinished(long id) {

                new ContentFlashCard().collectItemFromServer(id, false);

                ((MainActivity)Globals.getContext()).setCatalogueState(Constants.FLASH_CARD_DETAIL);
            }
        });

        if (ProcessConnectivity.isOk(getContext(), true)) {

            task.execute(this.carddeckId);
        }
    }


    /**
     * Validates the values
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @return
     */
    private boolean validateAnswer() {

        if(checkBoxMultipleChoice.isChecked()) {

            return ValidatorInput.isNotEmpty(editTextAnswerText)
                    //&& ValidatorInput.isValidUri(editTextAnswerUri)
                    && ValidatorInput.hasCheck(radioGroupAnswerCorrect);
        }

        return ValidatorInput.isNotEmpty(editTextAnswerText)
                && ValidatorInput.isValidUri(editTextAnswerUri);
    }


    /**
     * Validates the values
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-07
     *
     * @return
     */
    private boolean validateQuestion() {

        if (editTextQuestionText.getText().equals("")) return true;

        return ValidatorInput.isNotEmpty(editTextQuestionText);
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



    /**
     *
     *
     */
    private void setQuestionImage() {

        PermissionManager.verifyStoragePermissionsWrite((Activity) getContext());
        File sd =  Environment.getExternalStorageDirectory();

        File folder = new File(sd + "/flashcards");
        boolean success = true;

        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (success) {


            //Bitmap bitmapQuestion = ProcessorImage.download(imageUri);

        }

    }


    /**
     * Adds a clicklistener to the profile image
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-06
     */
    private void handleInsertUriClick() {

        //MEDIA GALLERY
        PermissionManager.verifyStoragePermissionsWrite((Activity) getContext());

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int btn) {

                switch (btn){

                    case DialogInterface.BUTTON_POSITIVE:

                        Intent camIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        FragmentFlashCardCreate.this.startActivityForResult(camIntent, MY_INTENT_CLICK_CAMERA);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        FragmentFlashCardCreate.this.startActivityForResult(Intent.createChooser(intent, "Select File"), MY_INTENT_CLICK_GALLERY);
                        break;

                    default:
                        // On canel

                        imageButtonDeleteImage.setVisibility(View.GONE);
                        imageViewUri.setImageBitmap(null);
                        editTextQuestionUri.setEnabled(true);
                        editTextQuestionUri.setText(null);

                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(R.string.prompt_choose_image) //TODO set in constans strings
                .setPositiveButton(R.string.prompt_choose_camera, dialogClickListener)
                .setNegativeButton(R.string.prompt_choose_gallery, dialogClickListener)
                .setNeutralButton(R.string.prompt_cancel, dialogClickListener).show();
    }



    /**
     * Here you get the return values of the intends
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode != RESULT_OK) return;


        // Get Photo from camera intent
        if (resultCode == RESULT_OK && data.getData() == null && requestCode == MY_INTENT_CLICK_CAMERA) {

            bitmapQuestion = (Bitmap) data.getExtras().get("data");

            imageViewUri.setVisibility(View.VISIBLE);
            imageViewUri.setImageBitmap(bitmapQuestion);

            editTextQuestionUri.setText(ProcessorImage.getImageUri(getContext(), bitmapQuestion));

            setQuestionImage();

        }

        // Get Data from Gallery intent
        if (requestCode == MY_INTENT_CLICK_GALLERY) {

            if (null == data) return;

            String selectedImagePath;
            Uri selectedImageUri = data.getData();

            selectedImagePath = ProcessorImage.getPath(getContext(), selectedImageUri);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();

            bitmapQuestion = BitmapFactory.decodeFile(selectedImagePath, bmOptions);

            imageViewUri.setVisibility(View.VISIBLE);
            imageViewUri.setImageBitmap(bitmapQuestion);

            editTextQuestionUri.setText(ProcessorImage.getImageUri(getContext(), bitmapQuestion));

            setQuestionImage();
        }

    }
}
