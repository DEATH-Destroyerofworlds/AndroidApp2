package com.example.jsonreader;

import android.content.Context;
import android.content.res.AssetManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Utility class for reading JSON files from the assets folder
 * and converting them to Java data structures.
 */
public class JsonAssetReader {

    private Context context;

    public JsonAssetReader(Context context) {
        this.context = context;
    }

    /**
     * Progressively reads all items from a JSON array file in the assets folder.
     * This method reads the entire JSON array and returns a list of all items.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return List of Maps representing all JSON objects in the array
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public List<Map<String, Object>> readAllItemsFromAssets(String fileName) 
            throws IOException, JSONException {
        
        String jsonString = loadJsonFromAssets(fileName);
        JSONArray jsonArray = new JSONArray(jsonString);
        
        List<Map<String, Object>> allItems = new ArrayList<>();
        
        // Progressively read each item in the array
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> item = jsonObjectToMap(jsonObject);
            allItems.add(item);
            
            // Optional: Log progress for debugging
            // Log.d("JsonAssetReader", "Processed item " + (i + 1) + " of " + jsonArray.length());
        }
        
        return allItems;
    }

    /**
     * Progressively reads items from a JSON array file with a callback for each item.
     * This allows processing items one by one without loading all into memory at once.
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
        
        // Process each item progressively
        for (int i = 0; i < totalItems; i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> item = jsonObjectToMap(jsonObject);
            
            // Call the callback with current item, index, and total count
            callback.onItemProcessed(item, i, totalItems);
        }
        
        // Notify completion
        callback.onAllItemsProcessed(totalItems);
    }

    /**
     * Interface for progressive item processing callback
     */
    public interface ItemCallback {
        /**
         * Called for each item as it's processed
         * @param item The processed item as a Map
         * @param currentIndex Current item index (0-based)
         * @param totalItems Total number of items in the file
         */
        void onItemProcessed(Map<String, Object> item, int currentIndex, int totalItems);
        
        /**
         * Called when all items have been processed
         * @param totalProcessed Total number of items that were processed
         */
        void onAllItemsProcessed(int totalProcessed);
    }

    /**
     * Reads a JSON file from assets and returns it as a List of Maps.
     * Each Map represents a JSON object with key-value pairs.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return List of Maps representing the JSON data
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public List<Map<String, Object>> readJsonArrayFromAssets(String fileName) 
            throws IOException, JSONException {
        
        String jsonString = loadJsonFromAssets(fileName);
        JSONArray jsonArray = new JSONArray(jsonString);
        
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Map<String, Object> map = jsonObjectToMap(jsonObject);
            resultList.add(map);
        }
        
        return resultList;
    }

    /**
     * Reads a single JSON object from assets and returns it as a Map.
     *
     * @param fileName The name of the JSON file in the assets folder
     * @return Map representing the JSON object
     * @throws IOException if file cannot be read
     * @throws JSONException if JSON is malformed
     */
    public Map<String, Object> readJsonObjectFromAssets(String fileName) 
            throws IOException, JSONException {
        
        String jsonString = loadJsonFromAssets(fileName);
        JSONObject jsonObject = new JSONObject(jsonString);
        
        return jsonObjectToMap(jsonObject);
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
     * Converts a JSONObject to a Map recursively.
     *
     * @param jsonObject The JSONObject to convert
     * @return Map representation of the JSONObject
     * @throws JSONException if JSON is malformed
     */
    private Map<String, Object> jsonObjectToMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> keys = jsonObject.keys();
        
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = jsonObject.get(key);
            
            if (value instanceof JSONObject) {
                // Recursively convert nested JSON objects
                map.put(key, jsonObjectToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                // Convert JSON arrays to lists
                map.put(key, jsonArrayToList((JSONArray) value));
            } else {
                // Primitive values (String, Integer, Boolean, etc.)
                map.put(key, value);
            }
        }
        
        return map;
    }

    /**
     * Converts a JSONArray to a List recursively.
     *
     * @param jsonArray The JSONArray to convert
     * @return List representation of the JSONArray
     * @throws JSONException if JSON is malformed
     */
    private List<Object> jsonArrayToList(JSONArray jsonArray) throws JSONException {
        List<Object> list = new ArrayList<>();
        
        for (int i = 0; i < jsonArray.length(); i++) {
            Object value = jsonArray.get(i);
            
            if (value instanceof JSONObject) {
                list.add(jsonObjectToMap((JSONObject) value));
            } else if (value instanceof JSONArray) {
                list.add(jsonArrayToList((JSONArray) value));
            } else {
                list.add(value);
            }
        }
        
        return list;
    }
}