package de.uulm.einhoernchen.flashcardsapp.Fragment.Interface;

import android.view.View;

import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.databinding.ListItemUserBinding;

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
public interface OnFragmentInteractionListenerUserBinding {

    //void onUserListFragmentInteraction(ListItemUserBinding item);

    /**
     *
     * @param view
     * @param user
     */
    void onClickUser(View view, User user);


    /**
     *
     * @param binding
     */
    void onClickBinding(ListItemUserBinding binding);
}
