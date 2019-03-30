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

import android.util.Log;
import android.util.SparseArray;

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

            OcrGraphic graphic = new OcrGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }

        String match = TextTemplate.templates.get(0).getBestMatch(builder.toString());
        TextTemplate.templates.get(0).addMatch(match);

        String str = TextTemplate.templates.get(0).getTotalBestMatch();

        String match1 = TextTemplate.templates.get(1).getBestMatch(builder.toString());
        TextTemplate.templates.get(1).addMatch(match1);

        String str1 = TextTemplate.templates.get(1).getTotalBestMatch();

        String match2 = TextTemplate.templates.get(2).getBestMatch(builder.toString());
        TextTemplate.templates.get(2).addMatch(match2);

        String str2 = TextTemplate.templates.get(2).getTotalBestMatch();

        String match3 = TextTemplate.templates.get(3).getBestMatch(builder.toString());
        TextTemplate.templates.get(3).addMatch(match3);

        String str3 = TextTemplate.templates.get(3).getTotalBestMatch();

        str1 = checkSevens(str1);

        OcrCaptureActivity.date.setText((str != null) ? str : "Not recognized!");
        OcrCaptureActivity.bankCardNumber.setText((str1 != null) ? str1 : "Not recognized!");
        OcrCaptureActivity.number.setText((str2 != null) ? str2 : "Not recognized!");
        OcrCaptureActivity.user.setText((str3 != null) ? str3 : "Not recognized!");




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
     * Принимает номер карты, и если она не валидна, возвращает наиболее похожий вариант
     * @param num
     * @return Если подобрать не удалось - вернет изначальное число
     */
    static String checkSevens(String num) {
        try {
            num = num.trim();
            num = num.replace("b", "6");
            if (checkLuhn(num))
                return num;
            ArrayList<Integer> indicies = new ArrayList<>();
            for (int i = 0; i < num.length(); i++) {
                if (num.charAt(i) == '1')
                    indicies.add(i);
            }
            for (int i = 0; i < (1 << indicies.size()); i++) {
                StringBuilder builder = new StringBuilder(num);
                for (int j = 0; j < indicies.size(); j++)
                    if ((i & (1 << j)) != 0)
                        builder.setCharAt(indicies.get(j), '7');
                if (checkLuhn(builder.toString()))
                    return builder.toString();
            }
        }catch (Exception e){

        }
        return num;
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
