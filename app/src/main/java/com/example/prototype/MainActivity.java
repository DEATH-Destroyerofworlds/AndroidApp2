package com.example.prototype;

import android.os.Bundle;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Example usage of the Card class with transcript functionality
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create a simple linear layout to hold our cards
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        setContentView(layout);
        
        // Create and configure example cards
        createExampleCard(layout, "Hello", "Привет", "Phonetic: /həˈloʊ/ - A greeting used when meeting someone");
        createExampleCard(layout, "Goodbye", "До свидания", "Phonetic: /ɡʊdˈbaɪ/ - A farewell expression");
        createExampleCard(layout, "Thank you", "Спасибо", "Phonetic: /θæŋk juː/ - Expression of gratitude");
    }
    
    private void createExampleCard(LinearLayout parent, String englishWord, String russianWord, String transcript) {
        Card card = new Card(this);
        
        // Set the card data
        card.setEngWordText(englishWord);
        card.setRusWordText(russianWord);
        card.setTranscript(transcript);
        
        // Add some margin between cards
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        card.setLayoutParams(params);
        
        parent.addView(card);
    }
}