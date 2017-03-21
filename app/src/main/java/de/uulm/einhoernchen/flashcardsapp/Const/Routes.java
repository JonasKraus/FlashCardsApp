package de.uulm.einhoernchen.flashcardsapp.Const;

/**
 * @author Jonas Kraus jonas.kraus@uni-ulm.de
 * @since 2016.08.23
 */
public class Routes {

    // Change this to toggle between dev and live ip
    private static final boolean DEV = false;


    public static final String SLASH = "/";
    public static final String QUESTION_MARK = "?";
    public static final String EQUAL = "=";
    public static final String AND = "&";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String START = "start";
    public static final String STARTS_WITH = "startsWith";
    public static final String SORT_BY = "sortBy";
    public static final String USAGE_COUNT = "usageCount";
    public static final String SPACE = "%20";
    public static final String ASC = "ASC";
    public static final String DESC = "DESC";
    public static final String SIZE = "size";
    public static final String ROOT = "root";
    public static final String BOOL_TRUE = "true";
    public static final String BOOL_FALSE = "false";
    public static final String CHILDREN = "children";
    public static final String APPEND = "append";
    public static final String BASE_PROTOCOL = "http:" + SLASH + SLASH;
    public static final String BASE_PORT = ":9000";
    public static final String BASE_URL_IP_DEV = "134.60.51.72";
    public static final String BASE_URL_IP_LIVE = "134.60.51.194";
    public static final String URL_HELP = "http://134.60.51.194/help.html";

    public static final String HEARTBEAT = "heartbeat";

    public static final String URL = BASE_PROTOCOL + (DEV ? BASE_URL_IP_DEV : BASE_URL_IP_LIVE) + BASE_PORT;

    public static final String FLASH_CARDS = "cards";
    public static final String CARD_DECKS = "cardDecks";
    public static final String DECKS = "decks";
    public static final String CATEGORIES = "categories";
    public static final String USERS = "users";
    public static final String ANSWERS = "answers";
    public static final String RATINGS = "ratings";
    public static final String USER_GROUPS = "groups";
    public static final String LOGIN = "login";
    public static final String MESSAGES = "messages";
    public static final String CHALLENGES = "challenges";
    public static final String UPLOAD = "upload";
    public static final String TAGS = "tags";
}
