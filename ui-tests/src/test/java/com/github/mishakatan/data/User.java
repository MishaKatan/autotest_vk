package com.github.mishakatan.data;

public record User(String username, String password) {

    private static final String PASSWORD = "secret_sauce";

    public static final User STANDARD = new User("standard_user", PASSWORD);
    public static final User LOCKED_OUT = new User("locked_out_user", PASSWORD);
    public static final User PROBLEM = new User("problem_user", PASSWORD);
    public static final User VISUAL = new User("visual_user", PASSWORD);

    @Override
    public String toString() {
        return username;
    }
}