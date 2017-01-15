package de.uulm.einhoernchen.flashcardsapp.Const;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.08.22
 */
public enum Constants {
    ROOT_LIST, CATEGORY_LIST, CARD_DECK_LIST, FLASH_CARD_LIST, FLASH_CARD_DETAIL, PLAY, FLASH_CARD_CREATE,
    PLAY_ANSWER, PLAY_QUESTION,
    SETTINGS_LEARN_MODE_KNOWLEDGE,
    SETTINGS_LEARN_MODE_DATE,
    SETTINGS_LEARN_MODE_RANDOM,
    SETTINGS_LEARN_MODE_DRAWER;


    /**
     * Method to check wich string equels to which enum
     *
     * @author Jonas Kraus jonas.kraus@uni-ulm.de
     * @since 2017-01-15
     *
     * @param learnModeString
     * @return
     */
    public static Constants equalsEnum(String learnModeString) {

            if (learnModeString.equals(String.valueOf(SETTINGS_LEARN_MODE_KNOWLEDGE))) {

                return SETTINGS_LEARN_MODE_DATE;
            } else if (learnModeString.equals(String.valueOf(SETTINGS_LEARN_MODE_DATE))) {

                return SETTINGS_LEARN_MODE_RANDOM;
            } else if (learnModeString.equals(String.valueOf(SETTINGS_LEARN_MODE_RANDOM))) {

                return SETTINGS_LEARN_MODE_RANDOM;
            } else if (learnModeString.equals(String.valueOf(SETTINGS_LEARN_MODE_DRAWER))) {

                return SETTINGS_LEARN_MODE_DRAWER;
            }

            return SETTINGS_LEARN_MODE_DRAWER;
    }
}
