package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Activity.MainActivity;
import de.uulm.einhoernchen.flashcardsapp.Database.DbHelper;
import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link FragmentPlayQuestion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPlayQuestion extends Fragment {
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

    private RadioGroup radioGroupAnswerCorrect;
    private RadioButton radioButtonAnswerCorrect;
    private RadioButton radioButtonAnswerIncorrect;

    private List<Long> cardIds;
    private DbManager db = Globals.getDb();
    private FlashCard currentFlashcard;
    private int position = 0;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FragmentPlayQuestion() {
        // Required empty public constructor
        Globals.getFloatingActionButtonAdd().setVisibility(View.GONE);
        cardIds = db.getSelectedFlashcardIDs();

        for (int i = 0; i < cardIds.size(); i++) {
            Log.d("card", " " + cardIds.get(i));
        }

        if (cardIds.size() == 0) {

            Toast.makeText(getContext(), R.string.select_card, Toast.LENGTH_SHORT).show();
            // TODO return to catalogue
            MainActivity mainActivity = (MainActivity) Globals.getContext();
            mainActivity.onBackPressed();
        } else if (position < cardIds.size()){

            currentFlashcard = db.getFlashCard(cardIds.get(position));
        }
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPlayQuestion.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPlayQuestion newInstance(String param1, String param2) {
        FragmentPlayQuestion fragment = new FragmentPlayQuestion();
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
     * Finds the needed viwes
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-12
     *
     * @param view
     */
    private void initView(View view) {
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

        imageViewPlay = (ImageView) view.findViewById(R.id.imageview_card_media_play);
        radioGroupAnswerCorrect = (RadioGroup) view.findViewById(R.id.radio_buttongroup_answer_editor);
        radioButtonAnswerCorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_correct);
        radioButtonAnswerIncorrect = (RadioButton) view.findViewById(R.id.radio_button_answer_editor_incorrect);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment_play_question, container, false);

        initView(view);

        mContentView.setText(Html.fromHtml(currentFlashcard.getQuestion().getQuestionText()));
        mAuthorView.setText(currentFlashcard.getQuestion().getAuthor().getName());
        mCardRatingView.setText(currentFlashcard.getRatingForView());
        mDateView.setText(currentFlashcard.getLastUpdatedString());



        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
