package com.example.podlibrary;

public class SimpleStemmer {
    /*
    This function is a rudimentary stemmer that truncates words at punctuation.
     */
    public static String [] processText(String text){
        String delimiters = " .',?;:-’";
        text = text.toLowerCase();
        String realTitle = "";
        for (int i = 0; i < text.length(); i++) {
            if (delimiters.indexOf(text.charAt(i)) != -1) {
                realTitle += " ";
            } else {
                realTitle += text.charAt(i);
            }
        }
        String[] keywords = realTitle.split(" ");
        return keywords;
    }
}
