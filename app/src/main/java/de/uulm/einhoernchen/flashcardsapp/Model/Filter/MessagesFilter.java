package de.uulm.einhoernchen.flashcardsapp.Model.Filter;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterMessages;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.Message;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.03
 */

public class MessagesFilter extends Filter {

    private final RecyclerViewAdapterMessages adapter;

    private final List<Message> originalList;

    private final List<Message> filteredList;

    public MessagesFilter(RecyclerViewAdapterMessages adapter, List<Message> originalList) {

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

            for (final Message item : originalList) {

                if (item.getContent().contains(filterPattern)) {

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
        adapter.getList().addAll((ArrayList<Message>) results.values);
        adapter.notifyDataSetChanged();

    }
}
