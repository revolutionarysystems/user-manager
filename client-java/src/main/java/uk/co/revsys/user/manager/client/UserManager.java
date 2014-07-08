package uk.co.revsys.user.manager.client;

public class UserManager {

    private static String username;
    private static String password;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UserManager.username = username;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        UserManager.password = password;
    }
    
}
