package com.example.podpaper4;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;


/*
This class provides some static functions used in the MainActivity class. These functions process text
by stemming it
 */
public class utils {

    /*
    This function uses dynamic programming to find the levenshtein edit distance between two words.
    By assembling a matrix, we are able to figure out the edit distance between subwords as a mechanism
    to finding the edit distance between the full words.
     */
    public static double findEditDistance(String word1, String word2){
        double [] [] matrix = new double[word1.length() + 1][word2.length() +1];
        for (int i = 0; i < word1.length(); i ++){
            for (int j = 0; j <word2.length(); j ++){
                matrix[i][j] = 0;
            }
        }
        for (int i = 0; i <word2.length()+1; i++) {
            matrix[word1.length()][i] = i;
        }

        for (int i = 0; i < word1.length()+1; i++) {
            matrix[i][0] = word1.length() - i;
        }

        for (int i =1; i < word2.length() + 1; i++) {
            for (int j = 1; j < word1.length() + 1; j++) {
                double sub = matrix[word1.length() - j + 1][i - 1];
                if(word1.charAt(j - 1) != word2.charAt(i - 1)) {
                    sub += 2;
                }
                double e1 = matrix[word1.length() - j + 1][i] + 1;
                double e2 = matrix[word1.length() - j][i - 1]+ 1;

                ArrayList<Double> poss = new ArrayList<>();
                poss.add(sub);
                poss.add(e1);
                poss.add(e2);
                matrix[word1.length() - j][i] =  Collections.min(poss);
            }
        }

        return matrix[0][word2.length()];
    }

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

    /*
    This function uses the edit distance function to determine what a mispelled word is likely to be
     */
    public static String findClosestWord(String fakeWord, Set<String> realWords) {
        String bestWord = fakeWord;
        double bestDistance = 99999999;
        for (String word : realWords){
            double editDistance = findEditDistance(fakeWord, word);
            if (editDistance < bestDistance){
                bestDistance = editDistance;
                bestWord = word;
            }
        }
        return bestWord;
    }


}
