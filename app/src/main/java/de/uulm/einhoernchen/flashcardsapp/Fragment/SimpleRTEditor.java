package de.uulm.einhoernchen.flashcardsapp.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.HtmlTags;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link SimpleRTEditor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SimpleRTEditor extends Fragment implements CompoundButton.OnCheckedChangeListener, TextWatcher {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CheckBox checkBoxBold;
    private CheckBox checkBoxItalic;
    private CheckBox checkBoxUnderlined;
    private CheckBox checkBoxStrikethrough;
    private EditText editTextEditor;
    private String rawText = "";
    private Spanned htmlText;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int pos;
    private CheckBox checkBoxCode;

    public SimpleRTEditor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SimpleRTEditor.
     */
    // TODO: Rename and change types and number of parameters
    public static SimpleRTEditor newInstance(String param1, String param2) {
        SimpleRTEditor fragment = new SimpleRTEditor();
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

        View view = inflater.inflate(R.layout.fragment_simple_rt_editor, container, false);

        checkBoxBold = (CheckBox) view.findViewById(R.id.button_bold);
        checkBoxBold.setOnCheckedChangeListener(this);
        checkBoxItalic = (CheckBox) view.findViewById(R.id.button_italic);
        checkBoxItalic.setOnCheckedChangeListener(this);
        checkBoxUnderlined = (CheckBox) view.findViewById(R.id.button_underlined);
        checkBoxUnderlined.setOnCheckedChangeListener(this);
        checkBoxStrikethrough = (CheckBox) view.findViewById(R.id.button_strikethrough);
        checkBoxCode = (CheckBox) view.findViewById(R.id.checkbox_raw_view);
        checkBoxCode.setOnCheckedChangeListener(this);
        checkBoxStrikethrough.setOnCheckedChangeListener(this);
        editTextEditor = (EditText) view.findViewById(R.id.edittext_editor);
        editTextEditor.addTextChangedListener(this);

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editTextEditor, InputMethodManager.SHOW_IMPLICIT);

        rawText = toHtml(fromHtml(editTextEditor.getText().toString()));
        htmlText = fromHtml(rawText);

        editTextEditor.setText(rawText);
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

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        String tagStart = "";
        String tagEnd = "";

        switch (buttonView.getId()) {

            case R.id.button_bold:

                if (isChecked) {

                    tagStart = HtmlTags.BOLD_START;
                    tagEnd = HtmlTags.BOLD_END;
                } else {

                    setCursorToEnd();

                }

                break;
            case R.id.button_italic:

                if (isChecked) {

                    tagStart = HtmlTags.ITALIC_START;
                    tagEnd = HtmlTags.ITALIC_END;
                } else {

                    setCursorToEnd();

                }

                break;
            case R.id.button_underlined:

                if (isChecked) {

                    tagStart = HtmlTags.UNDERLINE_START;
                    tagEnd = HtmlTags.UNDERLINE_END;
                } else {

                    setCursorToEnd();

                }

                break;
            case R.id.button_strikethrough:

                if (isChecked) {

                    tagStart = HtmlTags.STRIKE_START;
                    tagEnd = HtmlTags.STRIKE_END;
                } else {

                    setCursorToEnd();

                }

                break;

        }

        if (buttonView.getId() == R.id.checkbox_raw_view) {

            if (isChecked) {

                editTextEditor.setText(rawText);
                pos = editTextEditor.getSelectionStart();
            } else {

                editTextEditor.setText(htmlText);
                pos = editTextEditor.getSelectionStart();
            }

        } else {

            insertTag(tagStart, tagEnd);
        }
    }


    private void setCursor() {

        editTextEditor.setSelection(pos);
    }

    private void setCursorToEnd() {

        //editTextEditor.setSelection(editTextEditor.getText().length());

    }

    private void insertTag(String tagStart, String tagEnd) {

        pos = editTextEditor.getSelectionEnd();

        int selectionStart = editTextEditor.getSelectionStart();
        int selectionEnd = editTextEditor.getSelectionEnd();
        String textSart = "";
        String textEnd = "";

        if (rawText.length() > 0) {

            textSart = rawText.substring(0, selectionStart);
            textEnd  = rawText.substring(selectionEnd, rawText.length());
        }

        boolean surroundSelection = false;

        if (selectionEnd != selectionStart) {

            surroundSelection = true;
        }



        rawText = textSart + tagStart + tagEnd + textEnd;
        htmlText = fromHtml(rawText);


        if (checkBoxCode.isChecked()) {

            editTextEditor.setText(rawText);
        } else {

            editTextEditor.setText(htmlText);
        }

        Log.d("select", selectionStart + " " +selectionEnd + " length: " + rawText.length() + " select " + selectionEnd + tagStart.length());
        editTextEditor.setSelection(selectionStart + tagStart.length());



        Log.d("text", rawText);
        Log.d("textHtml", htmlText.toString());
/*
        String t = editTextEditor.getText().toString();
        editTextEditor.setText(fromHtml(t));
        editTextEditor.setSelection(pos);*/
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @SuppressWarnings("deprecation")
    public static String toHtml(Spanned html){
        String result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.toHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.toHtml(html);
        }
        return result;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        pos = editTextEditor.getSelectionStart();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {


        if (!checkBoxCode.isChecked()) {

            rawText = toHtml(editTextEditor.getText());
            htmlText = fromHtml(rawText);

        } else {

            rawText = editTextEditor.getText().toString();
            htmlText = fromHtml(rawText);
        }

    }
}
