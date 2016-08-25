package de.uulm.einhoernchen.flashcardsapp.Consts;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.08.23
 */
public class Routes {


    public static final String SLASH = "/";
    public static final String BASE_PROTOCOL = "http:" + SLASH + SLASH;
    public static final String BASE_PORT = ":9000";

    public static final String BASE_URL_LOCAL_HOST = BASE_PROTOCOL + "localhost" + BASE_PORT;
    public static final String BASE_URL_LOCAL_IP_HOME = BASE_PROTOCOL + "192.168.0.8" + BASE_PORT;
    public static final String BASE_URL_LOCAL_IP_WELCOME = BASE_PROTOCOL + "134.60.132.230" + BASE_PORT;
    public static final String BASE_URL_LOCAL_DOMAIN = BASE_PROTOCOL + "dev-flashcards.de" + BASE_PORT;

    public static final String URL = BASE_URL_LOCAL_IP_HOME;

    public static final String FLASH_CARDS = "cards";
    public static final String CARD_DECKS = "cardDecks";
    public static final String USERS = "users";


}
