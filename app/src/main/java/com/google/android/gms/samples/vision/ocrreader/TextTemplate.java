package com.google.android.gms.samples.vision.ocrreader;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TextTemplate {
    static ArrayList<TextTemplate> templates = new ArrayList<>();
    static HashMap<TextTemplate, String[]> templToMatches = new HashMap<>();
    private ArrayList<ArrayList<String>> samples = new ArrayList<>();
    private int maxLength = 0, minLength = 100;
    private Matches matches = new Matches(20);
    private String bestFound = null;

    public TextTemplate(){
        templates.add(this);
        templToMatches.put(this, new String[20]);
    }

    public void add(ArrayList<String> str){
        samples.add(str);
        if(maxLength < str.size()) maxLength = str.size();
        if(minLength > str.size()) minLength = str.size();
    }

    public void addMatch(String str){
        matches.add(str);
    }

    private double evalFunc(String str){
        if(str == null) return 0;
        double best = 0;
        for (ArrayList<String> sample : samples){
            double match = 0;
            for(int i = 0; i < sample.size(); i++){
                Pattern pattern = Pattern.compile(sample.get(i));

                if(str.length() > i && pattern.matcher("" + str.charAt(i)).matches()) match++;
            }
            match -= Math.abs(sample.size() - str.length());
            match /= sample.size();
            if(best < match) best = match;
        }
        return best;
    }

    public String getBestMatch(String str){
        String bestString = null;
        double bestEval = 0;

        for(int l = minLength - 1; l <= maxLength + 1; l++){
            for(int i = 0; i + l <= str.length(); i++){
                String current = str.substring(i, i + l);
                double e = evalFunc(current);
                if(e > bestEval) {
                    bestString = current;
                    bestEval = e;
                }
            }
        }

        return bestString;
    }

    public String getTotalBestMatch(){
        String bestMatch = null;
        double bestVal = 0;
        HashMap<String, Integer> counts = new HashMap<>();
        for (String str : this.matches) {
            if (str != null) {
                if (counts.containsKey(str))
                    counts.put(str, counts.get(str) + 1);
                else
                    counts.put(str, 1);
                if (bestVal < evalFunc(str)) {
                    bestMatch = str;
                    bestVal = evalFunc(str);
                }
                else if (Math.abs(bestVal - evalFunc(str)) < 1e-3 && counts.get(bestMatch) < counts.get(str)) {
                    bestMatch = str;
                }
            }
        }
        if(bestVal > evalFunc(bestFound) || Math.abs(bestVal - evalFunc(bestFound)) < 1e-3) bestFound = bestMatch;

        return bestFound;
    }
}
