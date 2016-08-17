package de.uulm.einhoernchen.flashcardsapp.Fragment.dummy;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.uulm.einhoernchen.flashcardsapp.Models.Answer;
import de.uulm.einhoernchen.flashcardsapp.Models.Categroy;
import de.uulm.einhoernchen.flashcardsapp.Models.FlashCard;
import de.uulm.einhoernchen.flashcardsapp.Models.Question;
import de.uulm.einhoernchen.flashcardsapp.Models.User;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p/>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContentCategory {


    public static List<Categroy> collectItemsFromServer(long parentId) {

        List<Categroy> categroys = new ArrayList<>();

        for (int position = 0; position < 100; position++) {
            categroys.add(position, new Categroy(position, parentId, "categroy " +position, ""));
        }
        return categroys;
    }



}
