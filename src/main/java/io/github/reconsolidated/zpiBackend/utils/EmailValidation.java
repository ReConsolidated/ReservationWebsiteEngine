package io.github.reconsolidated.zpiBackend.utils;

import java.util.regex.Pattern;

public class EmailValidation {
    private static final Pattern pattern = Pattern.compile("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]" +
            "+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");

    public static boolean isValid(String email) {
        return pattern.matcher(email).matches();
    }
}
