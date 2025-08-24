package com.example.prototype.backend;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prototype.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CardDisplay {
    private static final String TAG = "CardDisplay";
    
    private Context context;
    private CardReader cardReader;
    private List<Item> cardList;
    private int currentCardIndex;
    private boolean isShowingTranslation;
    
    // UI Components
    private TextView russianWordText;
    private TextView englishWordText;
    private TextView transcriptText;
    private Button flipButton;
    private Button nextButton;
    private Button prevButton;
    private TextView cardCounterText;
    private View cardContainer;
    
    // Callback interface for card interactions
    public interface CardDisplayCallback {
        void onCardFlipped(Item currentCard, boolean showingTranslation);
        void onCardChanged(Item newCard, int cardIndex, int totalCards);
        void onAllCardsLoaded(int totalCards);
        void onError(String errorMessage);
    }
    
    private CardDisplayCallback callback;
    
    public CardDisplay(Context context) {
        this.context = context;
        this.cardReader = new CardReader(context);
        this.cardList = new ArrayList<>();
        this.currentCardIndex = 0;
        this.isShowingTranslation = false;
    }
    
    public void setCallback(CardDisplayCallback callback) {
        this.callback = callback;
    }
    
    /**
     * Initialize the card display with UI components
     */
    public void initializeViews(View rootView) {
        russianWordText = rootView.findViewById(R.id.tv_russian_word);
        englishWordText = rootView.findViewById(R.id.tv_english_word);
        transcriptText = rootView.findViewById(R.id.tv_transcript);
        flipButton = rootView.findViewById(R.id.btn_flip_card);
        nextButton = rootView.findViewById(R.id.btn_next_card);
        prevButton = rootView.findViewById(R.id.btn_prev_card);
        cardCounterText = rootView.findViewById(R.id.tv_card_counter);
        cardContainer = rootView.findViewById(R.id.card_container);
        
        setupButtonListeners();
    }
    
    /**
     * Load cards from JSON file in assets
     */
    public void loadCards(String fileName) {
        try {
            cardList = cardReader.readAllItemsFromAssets(fileName);
            
            if (cardList.isEmpty()) {
                showError("No cards found in file: " + fileName);
                return;
            }
            
            currentCardIndex = 0;
            isShowingTranslation = false;
            
            displayCurrentCard();
            updateCardCounter();
            updateNavigationButtons();
            
            if (callback != null) {
                callback.onAllCardsLoaded(cardList.size());
            }
            
            Log.d(TAG, "Loaded " + cardList.size() + " cards from " + fileName);
            
        } catch (IOException e) {
            String error = "Error reading file: " + e.getMessage();
            Log.e(TAG, error);
            showError(error);
        } catch (JSONException e) {
            String error = "Error parsing JSON: " + e.getMessage();
            Log.e(TAG, error);
            showError(error);
        }
    }
    
    /**
     * Display the current card
     */
    private void displayCurrentCard() {
        if (cardList.isEmpty() || currentCardIndex < 0 || currentCardIndex >= cardList.size()) {
            return;
        }
        
        Item currentCard = cardList.get(currentCardIndex);
        
        if (isShowingTranslation) {
            showTranslation(currentCard);
        } else {
            showOriginal(currentCard);
        }
        
        Log.d(TAG, "Displaying card " + (currentCardIndex + 1) + ": " + currentCard.getEng_word());
    }
    
    /**
     * Show the original side of the card (Russian word)
     */
    private void showOriginal(Item card) {
        russianWordText.setText(card.getRus_word());
        russianWordText.setVisibility(View.VISIBLE);
        
        englishWordText.setVisibility(View.GONE);
        transcriptText.setVisibility(View.GONE);
        
        flipButton.setText("Show Translation");
        
        // Add visual indication that this is the front of the card
        cardContainer.setAlpha(1.0f);
    }
    
    /**
     * Show the translation side of the card (English word + transcript)
     */
    private void showTranslation(Item card) {
        russianWordText.setVisibility(View.GONE);
        
        englishWordText.setText(card.getEng_word());
        englishWordText.setVisibility(View.VISIBLE);
        
        if (card.getTranscript() != null && !card.getTranscript().isEmpty()) {
            transcriptText.setText("[" + card.getTranscript() + "]");
            transcriptText.setVisibility(View.VISIBLE);
        } else {
            transcriptText.setVisibility(View.GONE);
        }
        
        flipButton.setText("Show Original");
        
        // Add visual indication that this is the back of the card
        cardContainer.setAlpha(0.9f);
    }
    
    /**
     * Setup button click listeners
     */
    private void setupButtonListeners() {
        if (flipButton != null) {
            flipButton.setOnClickListener(v -> flipCard());
        }
        
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> nextCard());
        }
        
        if (prevButton != null) {
            prevButton.setOnClickListener(v -> previousCard());
        }
        
        // Optional: Add click listener to card container for flip functionality
        if (cardContainer != null) {
            cardContainer.setOnClickListener(v -> flipCard());
        }
    }
    
    /**
     * Flip the current card
     */
    public void flipCard() {
        if (cardList.isEmpty()) return;
        
        isShowingTranslation = !isShowingTranslation;
        displayCurrentCard();
        
        if (callback != null) {
            callback.onCardFlipped(getCurrentCard(), isShowingTranslation);
        }
        
        Log.d(TAG, "Card flipped - showing " + (isShowingTranslation ? "translation" : "original"));
    }
    
    /**
     * Go to the next card
     */
    public void nextCard() {
        if (cardList.isEmpty()) return;
        
        if (currentCardIndex < cardList.size() - 1) {
            currentCardIndex++;
            isShowingTranslation = false; // Reset to original side
            displayCurrentCard();
            updateCardCounter();
            updateNavigationButtons();
            
            if (callback != null) {
                callback.onCardChanged(getCurrentCard(), currentCardIndex, cardList.size());
            }
        } else {
            Toast.makeText(context, "This is the last card", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Go to the previous card
     */
    public void previousCard() {
        if (cardList.isEmpty()) return;
        
        if (currentCardIndex > 0) {
            currentCardIndex--;
            isShowingTranslation = false; // Reset to original side
            displayCurrentCard();
            updateCardCounter();
            updateNavigationButtons();
            
            if (callback != null) {
                callback.onCardChanged(getCurrentCard(), currentCardIndex, cardList.size());
            }
        } else {
            Toast.makeText(context, "This is the first card", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Go to a specific card by index
     */
    public void goToCard(int index) {
        if (cardList.isEmpty() || index < 0 || index >= cardList.size()) {
            return;
        }
        
        currentCardIndex = index;
        isShowingTranslation = false;
        displayCurrentCard();
        updateCardCounter();
        updateNavigationButtons();
        
        if (callback != null) {
            callback.onCardChanged(getCurrentCard(), currentCardIndex, cardList.size());
        }
    }
    
    /**
     * Update the card counter display
     */
    private void updateCardCounter() {
        if (cardCounterText != null && !cardList.isEmpty()) {
            String counterText = (currentCardIndex + 1) + " / " + cardList.size();
            cardCounterText.setText(counterText);
        }
    }
    
    /**
     * Update navigation button states
     */
    private void updateNavigationButtons() {
        if (prevButton != null) {
            prevButton.setEnabled(currentCardIndex > 0);
        }
        
        if (nextButton != null) {
            nextButton.setEnabled(currentCardIndex < cardList.size() - 1);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        
        if (callback != null) {
            callback.onError(message);
        }
    }
    
    // Getter methods
    public Item getCurrentCard() {
        if (cardList.isEmpty() || currentCardIndex < 0 || currentCardIndex >= cardList.size()) {
            return null;
        }
        return cardList.get(currentCardIndex);
    }
    
    public int getCurrentCardIndex() {
        return currentCardIndex;
    }
    
    public int getTotalCards() {
        return cardList.size();
    }
    
    public boolean isShowingTranslation() {
        return isShowingTranslation;
    }
    
    public List<Item> getAllCards() {
        return new ArrayList<>(cardList);
    }
    
    /**
     * Shuffle the cards
     */
    public void shuffleCards() {
        if (cardList.size() > 1) {
            java.util.Collections.shuffle(cardList);
            currentCardIndex = 0;
            isShowingTranslation = false;
            displayCurrentCard();
            updateCardCounter();
            updateNavigationButtons();
            
            Log.d(TAG, "Cards shuffled");
            Toast.makeText(context, "Cards shuffled", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Reset to first card
     */
    public void resetToFirstCard() {
        if (!cardList.isEmpty()) {
            currentCardIndex = 0;
            isShowingTranslation = false;
            displayCurrentCard();
            updateCardCounter();
            updateNavigationButtons();
        }
    }
}