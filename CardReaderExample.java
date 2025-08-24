package com.example.prototype.backend;

import android.content.Context;
import android.util.Log;
import com.example.prototype.DataManager;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;

public class CardReaderExample {
    private static final String TAG = "CardReaderExample";
    private CardReader cardReader;
    
    public CardReaderExample(Context context) {
        cardReader = new CardReader(context);
    }
    
    /**
     * Example 1: Read all items at once and return as a list
     */
    public void loadAllItemsExample() {
        try {
            // Read all items from the JSON array file
            List<DataManager.Item> allItems = cardReader.readAllItemsFromAssets("vocabulary.json");
            
            Log.d(TAG, "Loaded " + allItems.size() + " vocabulary items");
            
            // Process all items
            for (int i = 0; i < allItems.size(); i++) {
                DataManager.Item item = allItems.get(i);
                Log.d(TAG, "Item " + (i + 1) + ": " + 
                      item.getEng_word() + " - " + 
                      item.getRus_word() + " [" + 
                      item.getTranscript() + "]");
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }
    
    /**
     * Example 2: Read items progressively with callback
     */
    public void loadItemsProgressivelyExample() {
        try {
            cardReader.readItemsProgressively("vocabulary.json", new CardReader.ItemCallback() {
                @Override
                public void onItemProcessed(DataManager.Item item, int currentIndex, int totalItems) {
                    // Process each item as it's loaded
                    Log.d(TAG, "Processing item " + (currentIndex + 1) + "/" + totalItems + 
                          ": " + item.getEng_word());
                    
                    // You can do any processing here, such as:
                    // - Add to database
                    // - Update UI
                    // - Validate data
                    // - etc.
                    
                    // Example: Update progress
                    float progress = ((float)(currentIndex + 1) / totalItems) * 100;
                    Log.d(TAG, "Progress: " + String.format("%.1f", progress) + "%");
                }
                
                @Override
                public void onAllItemsProcessed(int totalProcessed) {
                    Log.d(TAG, "Finished processing all " + totalProcessed + " items!");
                    // Notify completion, update UI, etc.
                }
            });
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }
    
    /**
     * Example 3: Progressive reading with error handling and item validation
     */
    public void loadItemsWithValidation() {
        try {
            cardReader.readItemsProgressively("vocabulary.json", new CardReader.ItemCallback() {
                private int validItems = 0;
                private int invalidItems = 0;
                
                @Override
                public void onItemProcessed(DataManager.Item item, int currentIndex, int totalItems) {
                    // Validate each item
                    if (isValidItem(item)) {
                        validItems++;
                        Log.d(TAG, "Valid item: " + item.getEng_word());
                        // Process valid item (save to database, add to list, etc.)
                    } else {
                        invalidItems++;
                        Log.w(TAG, "Invalid item at index " + currentIndex + 
                              ": missing required fields");
                    }
                }
                
                @Override
                public void onAllItemsProcessed(int totalProcessed) {
                    Log.d(TAG, "Processing complete!");
                    Log.d(TAG, "Valid items: " + validItems);
                    Log.d(TAG, "Invalid items: " + invalidItems);
                    Log.d(TAG, "Total processed: " + totalProcessed);
                }
                
                private boolean isValidItem(DataManager.Item item) {
                    return item.getEng_word() != null && !item.getEng_word().isEmpty() &&
                           item.getRus_word() != null && !item.getRus_word().isEmpty();
                }
            });
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }
}