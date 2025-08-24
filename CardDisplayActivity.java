package com.example.prototype;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.prototype.backend.CardDisplay;
import com.example.prototype.backend.Item;

public class CardDisplayActivity extends AppCompatActivity implements CardDisplay.CardDisplayCallback {
    
    private static final String TAG = "CardDisplayActivity";
    private CardDisplay cardDisplay;
    private Button shuffleButton;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_display);
        
        // Initialize CardDisplay
        cardDisplay = new CardDisplay(this);
        cardDisplay.setCallback(this);
        
        // Initialize views
        cardDisplay.initializeViews(findViewById(android.R.id.content));
        
        // Setup additional buttons
        setupAdditionalButtons();
        
        // Load cards from JSON file
        loadVocabularyCards();
    }
    
    private void setupAdditionalButtons() {
        shuffleButton = findViewById(R.id.btn_shuffle);
        resetButton = findViewById(R.id.btn_reset);
        
        if (shuffleButton != null) {
            shuffleButton.setOnClickListener(v -> cardDisplay.shuffleCards());
        }
        
        if (resetButton != null) {
            resetButton.setOnClickListener(v -> cardDisplay.resetToFirstCard());
        }
    }
    
    private void loadVocabularyCards() {
        // Load your vocabulary cards from JSON file in assets
        cardDisplay.loadCards("vocabulary.json");
    }
    
    // CardDisplay.CardDisplayCallback implementation
    @Override
    public void onCardFlipped(Item currentCard, boolean showingTranslation) {
        Log.d(TAG, "Card flipped - showing " + (showingTranslation ? "translation" : "original"));
        Log.d(TAG, "Current card: " + currentCard.getEng_word() + " - " + currentCard.getRus_word());
    }
    
    @Override
    public void onCardChanged(Item newCard, int cardIndex, int totalCards) {
        Log.d(TAG, "Card changed to " + (cardIndex + 1) + " of " + totalCards);
        Log.d(TAG, "New card: " + newCard.getEng_word() + " - " + newCard.getRus_word());
        
        // Optional: Update any additional UI based on card change
        // For example, update progress bar, save progress, etc.
    }
    
    @Override
    public void onAllCardsLoaded(int totalCards) {
        Log.d(TAG, "All " + totalCards + " cards loaded successfully");
        Toast.makeText(this, "Loaded " + totalCards + " vocabulary cards", Toast.LENGTH_SHORT).show();
        
        // Optional: Enable UI elements, show welcome message, etc.
    }
    
    @Override
    public void onError(String errorMessage) {
        Log.e(TAG, "CardDisplay error: " + errorMessage);
        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_LONG).show();
        
        // Optional: Handle error (show error screen, try reload, etc.)
    }
    
    // Optional: Handle back button to show confirmation dialog
    @Override
    public void onBackPressed() {
        if (cardDisplay.getTotalCards() > 0) {
            // Show progress before exiting
            int current = cardDisplay.getCurrentCardIndex() + 1;
            int total = cardDisplay.getTotalCards();
            Toast.makeText(this, "Progress: " + current + "/" + total + " cards", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }
    
    // Optional: Save progress when activity is paused
    @Override
    protected void onPause() {
        super.onPause();
        if (cardDisplay.getTotalCards() > 0) {
            // Save current card index to preferences
            int currentIndex = cardDisplay.getCurrentCardIndex();
            Log.d(TAG, "Saving progress: card " + (currentIndex + 1));
            
            // You can save this to SharedPreferences:
            // SharedPreferences prefs = getSharedPreferences("CardProgress", MODE_PRIVATE);
            // prefs.edit().putInt("currentCardIndex", currentIndex).apply();
        }
    }
    
    // Optional: Restore progress when activity is resumed
    @Override
    protected void onResume() {
        super.onResume();
        
        // You can restore progress from SharedPreferences:
        // SharedPreferences prefs = getSharedPreferences("CardProgress", MODE_PRIVATE);
        // int savedIndex = prefs.getInt("currentCardIndex", 0);
        // cardDisplay.goToCard(savedIndex);
    }
}