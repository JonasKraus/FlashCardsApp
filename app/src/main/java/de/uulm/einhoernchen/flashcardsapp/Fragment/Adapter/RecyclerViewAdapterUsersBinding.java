package de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Database.DbManager;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUser;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Interface.OnFragmentInteractionListenerUserBinding;
import de.uulm.einhoernchen.flashcardsapp.Fragment.ViewHolder.UserViewHolder;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.R;
import de.uulm.einhoernchen.flashcardsapp.Util.Globals;
import de.uulm.einhoernchen.flashcardsapp.databinding.ListItemUserBinding;


public class RecyclerViewAdapterUsersBinding extends SortedListAdapter<User> {

    private Boolean isUpToDate;
    private OnFragmentInteractionListenerUserBinding mListener;
    private List<User> usersOfGroup;


    public RecyclerViewAdapterUsersBinding(Context context, Comparator<User> comparator, boolean isUpToDate, OnFragmentInteractionListenerUserBinding mListener, List<User> usersOfGroup) {

        super(context, User.class, comparator);

        this.mListener = mListener;
        this.isUpToDate = isUpToDate;
        this.usersOfGroup = usersOfGroup;
    }

    @Override
    protected SortedListAdapter.ViewHolder<? extends User> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        final ListItemUserBinding binding = ListItemUserBinding.inflate(inflater, parent, false);
        return new UserViewHolder(binding, usersOfGroup, isUpToDate, mListener);
    }

    @Override
    protected boolean areItemsTheSame(User item1, User item2) {
        return item1.getId() == item2.getId();
    }

    @Override
    protected boolean areItemContentsTheSame(User oldItem, User newItem) {
        return oldItem.equals(newItem);
    }


}
