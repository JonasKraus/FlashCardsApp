package de.uulm.einhoernchen.flashcardsapp.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.uulm.einhoernchen.flashcardsapp.Model.Tag;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2017.02.23
 */
public class HashtagParser {


    /**
     * parses a string and gives all tags as arraylist
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-23
     *
     * @param str
     * @return
     */
    public static List<String> parse(String str) {

        Pattern patternTag = Pattern.compile("#(\\S+)");
        Matcher mat = patternTag.matcher(str);

        List<String> tags = new ArrayList<String>();

        while (mat.find()) {

            tags.add(mat.group(1));
        }

        return tags;
    }


    /**
     * Generates a string from tags that ca be placed in an edittext
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-02-23
     *
     * @param tags
     * @return
     */
    public static String getString(List<Tag> tags) {

        String string = "";

        for (Tag tag : tags) {

            string += tag.toStringForView() + " ";
        }

        return string;
    }
}
