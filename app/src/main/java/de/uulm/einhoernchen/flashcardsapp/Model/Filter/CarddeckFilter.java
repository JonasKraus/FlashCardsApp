package de.uulm.einhoernchen.flashcardsapp.Model.Filter;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterCarddecks;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.CardDeck;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.03
 */

public class CarddeckFilter extends Filter {

    private final RecyclerViewAdapterCarddecks adapter;

    private final List<CardDeck> originalList;

    private final List<CardDeck> filteredList;

    public CarddeckFilter(RecyclerViewAdapterCarddecks adapter, List<CardDeck> originalList) {

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

            for (final CardDeck item : originalList) {

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
        adapter.getList().addAll((ArrayList<CardDeck>) results.values);
        adapter.notifyDataSetChanged();

    }
}
