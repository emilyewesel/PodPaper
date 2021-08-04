package com.example.podlibrary;

import java.util.Set;

import static com.example.podlibrary.EditDistance.findEditDistance;

public class FindBestWord {

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
