package com.example.jsonreader;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private JsonAssetReader jsonReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize the JSON reader
        jsonReader = new JsonAssetReader(this);
        
        // Example usage
        loadSampleData();
    }

    private void loadSampleData() {
        try {
            // Read JSON array from assets
            List<Map<String, Object>> dataList = jsonReader.readJsonArrayFromAssets("sample_data.json");
            
            Log.d(TAG, "Loaded " + dataList.size() + " items from JSON");
            
            // Process each item in the list
            for (int i = 0; i < dataList.size(); i++) {
                Map<String, Object> item = dataList.get(i);
                
                // Extract values from the map
                Object id = item.get("id");
                String name = (String) item.get("name");
                String email = (String) item.get("email");
                Object age = item.get("age");
                
                Log.d(TAG, "Item " + i + ": ID=" + id + ", Name=" + name + 
                      ", Email=" + email + ", Age=" + age);
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error reading JSON file: " + e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON: " + e.getMessage());
        }
    }
    
    // Alternative method to get specific data types
    private void loadSampleDataWithTypeCasting() {
        try {
            List<Map<String, Object>> dataList = jsonReader.readJsonArrayFromAssets("sample_data.json");
            
            for (Map<String, Object> item : dataList) {
                // Safe type casting with null checks
                Integer id = item.get("id") instanceof Integer ? (Integer) item.get("id") : null;
                String name = item.get("name") instanceof String ? (String) item.get("name") : "";
                String email = item.get("email") instanceof String ? (String) item.get("email") : "";
                Integer age = item.get("age") instanceof Integer ? (Integer) item.get("age") : 0;
                
                // Use the data as needed
                Log.d(TAG, "Processed: " + name + " (ID: " + id + ")");
            }
            
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error processing JSON: " + e.getMessage());
        }
    }
}