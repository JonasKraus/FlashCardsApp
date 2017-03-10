package de.uulm.einhoernchen.flashcardsapp.Model.Filter;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterHashtagFlashcards;
import de.uulm.einhoernchen.flashcardsapp.Fragment.Adapter.RecyclerViewAdapterUserGroups;
import de.uulm.einhoernchen.flashcardsapp.Model.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Model.UserGroup;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.03
 */

public class HashtagFlashCardFilter extends Filter {

    private final RecyclerViewAdapterHashtagFlashcards adapter;

    private final List<FlashCard> originalList;

    private final List<FlashCard> filteredList;

    public HashtagFlashCardFilter(RecyclerViewAdapterHashtagFlashcards adapter, List<FlashCard> originalList) {

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

            for (final FlashCard item : originalList) {

                if (item.getTags().contains(filterPattern)) {

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
        adapter.getList().addAll((ArrayList<FlashCard>) results.values);
        adapter.notifyDataSetChanged();

    }
}
