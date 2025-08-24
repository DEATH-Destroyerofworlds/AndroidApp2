package com.example.prototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Card extends CardView {
    /** Word in English */
    private TextView eng_word;
    /** Word in Russian */
    private TextView rus_word;
    /** VectorDrawable */
    private ImageView image;
    
    /** Transcript text that appears on long press */
    private String transcript;
    /** Original English word text */
    private String originalEngText;

    public Card(@NonNull Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Inflate the card layout
        LayoutInflater.from(context).inflate(R.layout.cardview, this, true);

        rus_word = findViewById(R.id.russianWordTxt);
        eng_word = findViewById(R.id.englishWordTxt);
        image = findViewById(R.id.imageView);
        
        setupTouchListener();
    }
    
    private void setupTouchListener() {
        eng_word.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // User pressed down - show transcript if available
                        if (transcript != null && !transcript.isEmpty()) {
                            originalEngText = eng_word.getText().toString();
                            eng_word.setText(transcript);
                        }
                        return true;
                        
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // User released or cancelled - restore original text
                        if (originalEngText != null) {
                            eng_word.setText(originalEngText);
                        }
                        // Perform click if it was a quick tap
                        if (event.getAction() == MotionEvent.ACTION_UP) {
                            v.performClick();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    public void setEng_word(TextView eng_word) {
        this.eng_word = eng_word;
    }

    public void setRus_word(TextView rus_word) {
        this.rus_word = rus_word;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }
    
    /**
     * Sets the English word text
     * @param text The English word to display
     */
    public void setEngWordText(String text) {
        if (eng_word != null) {
            eng_word.setText(text);
            originalEngText = text;
        }
    }
    
    /**
     * Sets the Russian word text
     * @param text The Russian word to display
     */
    public void setRusWordText(String text) {
        if (rus_word != null) {
            rus_word.setText(text);
        }
    }
    
    /**
     * Sets the transcript text that will be shown when the English word is held
     * @param transcript The transcript text to show on long press
     */
    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }
    
    /**
     * Gets the current transcript text
     * @return The transcript text
     */
    public String getTranscript() {
        return transcript;
    }
    
    /**
     * Gets the original English word text
     * @return The English word text
     */
    public String getEngWordText() {
        return originalEngText;
    }
    
    /**
     * Gets the Russian word text
     * @return The Russian word text
     */
    public String getRusWordText() {
        return rus_word != null ? rus_word.getText().toString() : null;
    }

    /** Gets the data of a Card using only the engword */
    public void readCard(String engWord){
        // Implementation for reading card data
        // This method can be implemented to load card data from a data source
    }
}