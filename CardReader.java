package com.example.prototype.backend;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.prototype.DataManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CardReader {
    private static final String TAG = "CardReader";
    private static DataManager instance;
    private List<DataManager.Item> itemList;

    private Context context;
    
    public CardReader(Context context) {
        this.context = context;
    }

    /**
     * Progressively reads all items from a JSON array file in the assets folder.
     * This method reads the entire JSON array and returns a list of all Item objects.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return List of Item objects representing all JSON objects in the array
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public List<DataManager.Item> readAllItemsFromAssets(String fileName) 
            throws IOException, JSONException {
        
        String jsonString = loadJsonFromAssets(fileName);
        JSONArray jsonArray = new JSONArray(jsonString);
        
        List<DataManager.Item> allItems = new ArrayList<>();
        
        // Progressively read each item in the array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataManager.Item item = jsonObjectToItem(jsonObject);
            allItems.add(item);
            
            // Log progress for debugging
            Log.d(TAG, "Processed item " + (i + 1) + " of " + jsonArray.length() + 
                  ": " + item.getEng_word());
        }
        
        Log.d(TAG, "Successfully loaded " + allItems.size() + " items from " + fileName);
        return allItems;
    }

    /**
     * Progressively reads items from a JSON array file with a callback for each item.
     * This allows processing items one by one as they're read.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @param callback Interface to handle each item as it's processed
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public void readItemsProgressively(String fileName, ItemCallback callback) 
            throws IOException, JSONException {
        
        String jsonString = loadJsonFromAssets(fileName);
        JSONArray jsonArray = new JSONArray(jsonString);
        
        int totalItems = jsonArray.length();
        Log.d(TAG, "Starting progressive read of " + totalItems + " items from " + fileName);
        
        // Process each item progressively
        for (int i = 0; i < totalItems; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            DataManager.Item item = jsonObjectToItem(jsonObject);
            
            // Call the callback with current item, index, and total count
            callback.onItemProcessed(item, i, totalItems);
            
            Log.d(TAG, "Processed item " + (i + 1) + "/" + totalItems + ": " + item.getEng_word());
        }
        
        // Notify completion
        callback.onAllItemsProcessed(totalItems);
        Log.d(TAG, "Completed progressive reading of all " + totalItems + " items");
    }

    /**
     * Interface for progressive item processing callback
     */
    public interface ItemCallback {
        /**
         * Called for each item as it's processed
         * @param item The processed Item object
         * @param currentIndex Current item index (0-based)
         * @param totalItems Total number of items in the file
         */
        void onItemProcessed(DataManager.Item item, int currentIndex, int totalItems);
        
        /**
         * Called when all items have been processed
         * @param totalProcessed Total number of items that were processed
         */
        void onAllItemsProcessed(int totalProcessed);
    }

    /**
     * Reads a single JSON object from assets and returns it as an Item.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return Item representing the JSON object
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public DataManager.Item readJsonObjectFromAssets(String fileName)
            throws IOException, JSONException {

        String jsonString = loadJsonFromAssets(fileName);
        JSONObject jsonObject = new JSONObject(jsonString);

        return jsonObjectToItem(jsonObject);
    }

    /**
     * Reads the raw JSON string from assets folder.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return Raw JSON string
     * @throws IOException if file cannot be read
     */
    public String loadJsonFromAssets(String fileName) throws IOException {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open(fileName);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder jsonBuilder = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            jsonBuilder.append(line);
        }

        inputStream.close();
        reader.close();

        return jsonBuilder.toString();
    }

    /**
     * Converts a JSONObject to an Item recursively.
     *
     * @param jsonObject The JSONObject to convert
     * @return Item representation of the JSONObject
     * @throws JSONException if JSON is malformed
     */
    private DataManager.Item jsonObjectToItem(JSONObject jsonObject) throws JSONException {
        DataManager.Item item = new DataManager.Item();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);

            switch (key){
                case "rus_word":
                    item.setRus_word(value.toString());
                    break;
                case "eng_word":
                    item.setEng_word(value.toString());
                    break;
                case "transcript":
                    item.setTranscript(value.toString());
                    break;
                default:
                    Log.w(TAG, "Unknown key found while reading: " + key);
                    break;
            }
        }

        return item;
    }
}