package com.google.android.gms.samples.vision.ocrreader;

import android.support.annotation.NonNull;

import java.util.Iterator;

public class Matches implements Iterable<String>{
    private String[] lastMatches;
    private int i = 0;

    public Matches(int a){
        lastMatches = new String[a];
    }

    public void add(String s){
        lastMatches[i] = s;
        i++;
        i %= lastMatches.length;
    }

    @NonNull
    @Override
    public Iterator<String> iterator() {
        return new Iter();
    }

    class Iter implements Iterator<String>{
        private int i = -1;
        @Override
        public boolean hasNext() {
            return (i + 1 < lastMatches.length);
        }

        @Override
        public String next() {
            i++;
            return lastMatches[i];
        }
    }
}
