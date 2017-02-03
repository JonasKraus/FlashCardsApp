package de.uulm.einhoernchen.flashcardsapp.Model.Filter;

import android.util.Log;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.User;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.03
 */

public class UserGroupFilter extends Filter {

    private final RecyclerViewAdapterUserGroups adapter;

    private final List<UserGroup> originalList;

    private final List<UserGroup> filteredList;

    public UserGroupFilter(RecyclerViewAdapterUserGroups adapter, List<UserGroup> originalList) {

        super();
        this.adapter = adapter;
        this.originalList = new LinkedList<>(originalList);
        this.filteredList = new ArrayList<>();
    }


    @Override
    protected FilterResults performFiltering(CharSequence constraint) {

        filteredList.clear();

        final FilterResults results = new FilterResults();

        if (constraint.length() == 0) {

            filteredList.addAll(originalList);
        } else {

            final String filterPattern = constraint.toString().toLowerCase().trim();

            for (final UserGroup item : originalList) {

                if (item.getName().contains(filterPattern)) {

                    filteredList.add(item);
                }
            }
        }

        results.values = filteredList;
        results.count = filteredList.size();

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        adapter.getList().clear();
        adapter.getList().addAll((ArrayList<UserGroup>) results.values);
        adapter.notifyDataSetChanged();

    }
}
