package de.uulm.einhoernchen.flashcardsapp.Fragment.ViewHolder;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Binder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUsers;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.SortedListAdapter;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserBinding;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.Util.ProcessorImage;
import de.uulm.einhoernchen.flashcardsapp.databinding.ListItemUserBinding;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.02
 */

public class UserViewHolder extends SortedListAdapter.ViewHolder<User> {

    private final ListItemUserBinding mBinding;
    private final ArrayList<Long> userIdsOfGroup;

    public UserViewHolder(final ListItemUserBinding binding, List<User> usersOfGroup, Boolean isUpToDate, final OnFragmentInteractionListenerUserBinding mListener) {
        super(binding.getRoot());

        this.userIdsOfGroup = new ArrayList<>();

        // Only do this if a group was checked
        if (usersOfGroup != null) {

            // get all ids in an list to check if it contains
            for (User user : usersOfGroup) {

                userIdsOfGroup.add(user.getId());
            }
        }

        mBinding = binding;
        mBinding.imageViewOffline.setVisibility(isUpToDate ? View.VISIBLE : View.INVISIBLE);
        //mBinding.setListener(mListener);
        mBinding.getRoot().setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    //mListener.onClickUser(mBinding.getRoot(), mBinding.getModel());
                    mListener.onClickBinding(mBinding);
                }
            }
        });
    }


    @Override
    protected void performBind(final User item) {

        mBinding.setModel(item);

        boolean isChecked = setCheckIcon(mBinding);

        if (!isChecked) {

            setRoundIcon(item, mBinding.imageViewRoundIcon);
        }

    }


    /**
     * checks the users that are in the clicked group
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-01
     *
     * @param mBinding
     */
    public boolean setCheckIcon(final ListItemUserBinding mBinding) {

        if (
            (userIdsOfGroup.size() > 0 && userIdsOfGroup.contains(mBinding.getModel().getId()))
            || (mBinding.getModel().getId() == Globals.getDb().getLoggedInUserId() && userIdsOfGroup.size() == 0)
            || mBinding.getModel().isChecked()
            ) {

            mBinding.getModel().setChecked(true);

            mBinding.imageViewRoundIcon.setTag("checked");

            // TODO get better ressource
            mBinding.imageViewRoundIcon.setImageDrawable(Globals.getContext().getResources().getDrawable(R.drawable.ic_check));

            // Set to null, so its unclickable
            //item.mView.setOnClickListener(null);
            mBinding.getRoot().setEnabled(false);

            return true;
        }

        return false;
    }


    /**
     * Sets the round icon
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-29
     *
     * @param user
     * @param imageView
     */
    private void setRoundIcon(User user, ImageView imageView) {

        if (user.getAvatar() != null && !user.getAvatar().equals("")) {

            // Automatically sets image view after downloading
            ProcessorImage.download(user.getAvatar(), imageView, user.getId(), null);

            imageView.setTag("unchecked");

        } else{

            //get first letter of each String item
            final String firstLetter = String.valueOf(user.getName().charAt(0)); // hier wird der buchstabe gesetzt

            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            // generate random color
            final int color = generator.getColor(user.getId()); // TODO
            //int color = generator.getRandomColor();

            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px

            imageView.setImageDrawable(drawable);
        }
    }
}
