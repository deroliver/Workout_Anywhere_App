package user;


public class User {
    private static User instance;

    public static String userName = "";
    public static String userID = "";
    public static String userAvatarUrl = "";
    public static String userStatus = "";
    public static String firstName = "";
    private static String lastName = "";

    public static User getInstance() {
        if(instance == null) {
            instance = new User();
        }
        return instance;
    }

    public static void initUser(String firstname, String lastname, String username, String userid, String useravatarUrl, String userstatus) {
        userName = username;
        userID = userid;
        userAvatarUrl = useravatarUrl;
        userStatus = userstatus;
        firstName = firstname;
        lastName = lastname;
    }

    public static String getUserName() {
        return userName;
    }

    public static String getUserID() {
        return userID;
    }

    public static String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public static String getUserStatus() {
        return userStatus;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }
}
