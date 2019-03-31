/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.ocrreader;

import android.graphics.Color;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;

/**
 * A very simple Processor which receives detected TextBlocks and adds them to the overlay
 * as OcrGraphics.
 */
public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OcrGraphic> mGraphicOverlay;

    OcrDetectorProcessor(GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Called by the detector to deliver detection results.
     * If your application called for it, this could be a place to check for
     * equivalent detections by tracking TextBlocks that are similar in location and content from
     * previous frames, or reduce noise by eliminating TextBlocks that have not persisted through
     * multiple detections.
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();
        SparseArray<TextBlock> items = detections.getDetectedItems();
        StringBuilder builder = new StringBuilder();
        int tempHeight = 0;
        double eps = 1.;
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if((double)item.getBoundingBox().height() / (double) tempHeight > eps || (double) tempHeight / (double)item.getBoundingBox().height() > eps){
                builder.append("******");
            }
            tempHeight = item.getBoundingBox().height();
            builder.append(item.getValue());
        }

        boolean ok = true;
        for(final TextTemplate tt : TextTemplate.templates){
            if(tt.enabled) {
                final EditText editText = ((EditText) tt.card.findViewById(R.id.editProperty));
                if (!tt.userEdited) {
                    String match = tt.getBestMatch(builder.toString().replace("\n", "*****"));
                    tt.addMatch(match);
                    final String str = tt.getTotalBestMatch();

                    if (editText.getText().toString().compareTo(str) != 0){
                        OcrCaptureActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                editText.setText((str != null) ? str : "Not recognized!");
                            }
                        });
                    }

                }
                ok &= Math.abs(tt.evalFunc(editText.getText().toString()) - 1) < 1e-3;
            }
        }
        final Button button = OcrCaptureActivity.activity.findViewById(R.id.save);
        if(ok){
            OcrCaptureActivity.activity.runOnUiThread(new Runnable() {
                public void run() {
                    button.setVisibility(View.VISIBLE);
                }
            });

        }else{
            OcrCaptureActivity.activity.runOnUiThread(new Runnable() {
                public void run() {
                    button.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * Проверяет, валиден ли номер карты
     * @param num Номер карты с пробелами и b
     * @return
     */
    static boolean checkLuhn(String num) {
        num = num.trim();
        num = num.replace("b", "6");
        int nDigs = 0;
        for (int i = 0; i < num.length(); i++)
            if (num.charAt(i) != ' ')
                nDigs++;
        int[] digits = new int[nDigs];
        int ind = 0;
        for (int i = 0; i < num.length(); i++)
            if (num.charAt(i) != ' ')
                digits[ind++] = Integer.parseInt(Character.toString(num.charAt(i)));

        int sum = 0;
        for (int i = 0; i < nDigs; i++) {
            if ((nDigs - i) % 2 == 0)
                digits[i] *= 2;
            if (digits[i] > 9)
                digits[i] -= 9;
            sum += digits[i];
        }
        return sum % 10 == 0;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
