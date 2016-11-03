package com.example.jcdc.emotionsample.helper;

/**
 * Created by jcdc on 11/4/16.
 */

public class StringHelper {
    public static String cap1stChar(String userIdea)
    {
        char[] stringArray = userIdea.toCharArray();
        stringArray[0] = Character.toUpperCase(stringArray[0]);
        return userIdea = new String(stringArray);
    }
}
