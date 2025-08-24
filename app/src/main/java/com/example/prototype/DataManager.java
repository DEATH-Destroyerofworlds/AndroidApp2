package com.example.prototype;
import android.content.Context;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<Item> itemList;

    private DataManager(Context context) {
        itemList = loadItems(context);
    }

    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context.getApplicationContext());
        }
        return instance;
    }

    public List<Item> loadItems(Context context) {
        List<Item> itemList = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("worddata.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            JSONArray jsonArray = new JSONArray(jsonBuilder.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String rus_word = jsonObject.getString("rus_word");
                String eng_word = jsonObject.getString("eng_word");
                
                // Read transcript with fallback to empty string if not present
                String transcript = jsonObject.optString("transcript", "");
                
                itemList.add(new Item(rus_word, eng_word, transcript));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return itemList;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public Item getItemByName(String name) {
        for (Item item : itemList) {
            if (item.getEng_word().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null; // Or throw an exception
    }

    public class Item{
        private String rus_word, eng_word, transcript;

        public String getRus_word() {
            return rus_word;
        }

        public void setRus_word(String rus_word) {
            this.rus_word = rus_word;
        }

        public String getEng_word() {
            return eng_word;
        }

        public void setEng_word(String eng_word) {
            this.eng_word = eng_word;
        }

        public String getTranscript() {
            return transcript;
        }

        public void setTranscript(String transcript) {
            this.transcript = transcript;
        }

        public Item(String rus_word, String eng_word) {
            this.rus_word = rus_word;
            this.eng_word = eng_word;
            this.transcript = "";
        }

        public Item(String rus_word, String eng_word, String transcript) {
            this.rus_word = rus_word;
            this.eng_word = eng_word;
            this.transcript = transcript != null ? transcript : "";
        }
    }
}