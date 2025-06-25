package com.dalread.viewmodel;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dalread.util.LanguageUtil;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.util.List;
import java.util.StringJoiner;

public class GoogleMLViewModel extends AndroidViewModel {
    private static final String TAG = "MainViewModel";
    private final TextRecognizer recognizer;
    private double confidenceThreshold;
    private final MutableLiveData<Boolean> processing = new MutableLiveData<>(false);
    private final MutableLiveData<String> progress = new MutableLiveData<>();
    private final MutableLiveData<String> result = new MutableLiveData<>();

    private final Object recycleLock = new Object();
    public GoogleMLViewModel(@NonNull Application application) {
        super(application);
        confidenceThreshold = 0.7;
        recognizer = TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());
    }

    @Override
    protected void onCleared() {
        synchronized (recycleLock) {
            if (recognizer != null)
                recognizer.close();
        }
    }

    public void setConfidenceThreshold(double confidenceThreshold) {
        this.confidenceThreshold = confidenceThreshold;
    }

    public void runTextRecognition(Bitmap mSelectedImage) {
        InputImage image = InputImage.fromBitmap(mSelectedImage, 0);
        recognizer.process(image)
                .addOnSuccessListener(texts -> processTextRecognitionResult(texts))
                .addOnFailureListener(e -> e.printStackTrace());
    }

    private void processTextRecognitionResult(Text texts) {
        List<Text.TextBlock> blocks = texts.getTextBlocks();
        StringJoiner sjText = new StringJoiner(" ");
        if (blocks.size() == 0) {
            result.setValue("There is no blocks in the Text");
            return;
        }
        progress.setValue("ML started");
        for (int i = 0; i < blocks.size(); i++) {
            List<Text.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<Text.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Text.Element element = elements.get(k);

                    StringJoiner sjElement = new StringJoiner("");
                    String elementText = element.getText();
                    for (int elementIndex = 0; elementIndex < elementText.length(); elementIndex++) {
                        String str = String.valueOf(elementText.charAt(elementIndex));
                        if (LanguageUtil.containsChineseCharacters(str)) {
                            sjElement.add(str);
                        }
                    }
                    if (sjElement.length() > 0) {
                        sjText.add(sjElement.toString());
                    }
//                    sjText.add(element.getText());

                    double confidence = element.getConfidence();

                    String formattedConfidence = String.format("%.2f", confidence);
//                    sjText.add("Element : " + element.getText() + "(" + formattedConfidence + ")\n");

                    Point[] elementCornerPoints = element.getCornerPoints();
                    Rect elementFrame = element.getBoundingBox();
                    for (Text.Symbol symbol : element.getSymbols()) {
                        String symbolText = symbol.getText();
//                        if (LanguageUtil.containsChineseCharacters(symbolText)) {
//                            sjText.add(symbolText);
//                        }
                        confidence = symbol.getConfidence();
                        if ( confidence < confidenceThreshold)
                            continue;

                        Point[] symbolCornerPoints = symbol.getCornerPoints();
                        formattedConfidence = String.format("%.2f", symbol.getConfidence());
//                        sjText.add(symbolText + "(" + formattedConfidence + ") ");
                        Rect symbolFrame = symbol.getBoundingBox();
                    }
//                    sjText.add("\n=====================\n");

                }
            }
        }

        result.setValue(sjText.toString());
    }

    @NonNull
    public LiveData<Boolean> getProcessing() {
        return processing;
    }

    @NonNull
    public LiveData<String> getProgress() {
        return progress;
    }

    @NonNull
    public LiveData<String> getResult() {
        return result;
    }
}
