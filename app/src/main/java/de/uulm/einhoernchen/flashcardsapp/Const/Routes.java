package de.uulm.einhoernchen.flashcardsapp.Const;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.08.23
 */
public class Routes {


    public static final String SLASH = "/";
    public static final String QUESTION_MARK = "?";
    public static final String EQUAL = "=";
    public static final String ROOT = "root";
    public static final String BOOL_TRUE = "true";
    public static final String BOOL_FALSE = "false";
    public static final String CHILDREN = "children";
    public static final String APPEND = "append";
    public static final String BASE_PROTOCOL = "http:" + SLASH + SLASH;
    public static final String BASE_PORT = ":9000";

    public static final String HEARTBEAT = "heartbeat";

    public static final String BASE_URL_LOCAL_HOST = BASE_PROTOCOL + "localhost" + BASE_PORT;
    public static final String BASE_URL_LOCAL_IP_HOME = BASE_PROTOCOL + "192.168.56.1" + BASE_PORT;
    public static final String BASE_URL_LOCAL_IP_WELCOME = BASE_PROTOCOL + "134.60.132.174" + BASE_PORT;
    public static final String BASE_URL_LOCAL_DOMAIN = BASE_PROTOCOL + "dev-flashcards.de" + BASE_PORT;
    public static final String BASE_URL_LIVE_DOMAIN = BASE_PROTOCOL + "chernobog.dd-dns.de" + BASE_PORT;
    public static final String BASE_URL_BW_CLOUD = BASE_PROTOCOL + "134.60.51.72" + BASE_PORT;

    public static final String URL = BASE_URL_BW_CLOUD;

    public static final String FLASH_CARDS = "cards";
    public static final String CARD_DECKS = "cardDecks";
    public static final String DECKS = "decks";
    public static final String CATEGORIES = "categories";
    public static final String USERS = "users";
    public static final String ANSWERS = "answers";
    public static final String RATINGS = "ratings";
    public static final String USER_GROUPS = "groups";
}
