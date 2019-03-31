package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class TextTemplate {
    static ArrayList<TextTemplate> templates = new ArrayList<>();
    static HashMap<TextTemplate, String[]> templToMatches = new HashMap<>();
    private ArrayList<ArrayList<String>> samples = new ArrayList<>();
    private int maxLength = 0, minLength = 100;
    Matches matches = new Matches(20);
    String bestFound = null;
    private String propery;
    private boolean onlyNumbers;
    public boolean userEdited = false, enabled = true;
    public View card;

    public TextTemplate(String property, boolean onlyNumbers){
        templates.add(this);
        templToMatches.put(this, new String[20]);
        this.propery = property;
        this.onlyNumbers = onlyNumbers;
    }

    public void add(ArrayList<String> str){
        samples.add(str);
        if(maxLength < str.size()) maxLength = str.size();
        if(minLength > str.size()) minLength = str.size();
    }

    public void addMatch(String str){
        matches.add(str);
    }

    public double evalFunc(String str){
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

        return onlyNumbers ? replaceSymbols(bestString) : bestString;
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

    public void toAddPropertyCard(Context context, final LinearLayout linearLayout){
        final View view = LayoutInflater.from(context).inflate(R.layout.property_card, null);
        TextView headerText = view.findViewById(R.id.headerProperty);
        headerText.setText(propery);
        final EditText editText = view.findViewById(R.id.editProperty);
        editText.setEnabled(false);
        editText.setText((bestFound != null) ? bestFound : "");
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Math.abs(evalFunc(editText.getText().toString()) - 1) < 1e-3){
                    editText.setTextColor(Color.GREEN);
                }else{
                    editText.setTextColor(Color.RED);
                }
            }
        });

        CheckBox using = view.findViewById(R.id.using);
        using.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b){
                    enabled = false;
                    editText.setTextColor(Color.parseColor("#BDC3C7"));
                }else{
                    enabled = true;
                    if(Math.abs(evalFunc(editText.getText().toString()) - 1) < 1e-3){
                        editText.setTextColor(Color.GREEN);
                    }else{
                        editText.setTextColor(Color.RED);
                    }
                }

            }
        });

        view.findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setEnabled(true);
                editText.requestFocus();
                userEdited = true;
            }
        });
        card = view;
        linearLayout.addView(view);
    }

    /**
     * Принимает номер карты, и если она не валидна, возвращает наиболее похожий вариант
     * @param num
     * @return Если подобрать не удалось - вернет изначальное число
     */
    static String replaceSymbols(String num) {
        if(num == null) return null;
        num = num.replace("b", "6");
        num = num.replace("O", "0");
        num = num.replace("o", "0");
        num = num.replace("D", "0");

        return num;
    }
}
