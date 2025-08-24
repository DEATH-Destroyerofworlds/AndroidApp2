# Android JSON Asset Reader

A Java class for Android Studio that provides easy methods to read JSON files from the assets folder and return them as Java Lists and Maps.

## Features

- Read JSON arrays from assets folder and return as `List<Map<String, Object>>`
- Read single JSON objects from assets folder and return as `Map<String, Object>`
- Handle nested JSON objects and arrays recursively
- Proper error handling with IOException and JSONException
- Uses Android's built-in JSON parsing (no external dependencies required)

## Usage

### 1. Basic Setup

Place your JSON files in the `app/src/main/assets/` folder of your Android project.

### 2. Initialize the Reader

```java
JsonAssetReader jsonReader = new JsonAssetReader(context);
```

### 3. Read JSON Array

```java
try {
    List<Map<String, Object>> dataList = jsonReader.readJsonArrayFromAssets("sample_data.json");
    
    for (Map<String, Object> item : dataList) {
        String name = (String) item.get("name");
        Integer id = (Integer) item.get("id");
        // Process your data...
    }
} catch (IOException | JSONException e) {
    Log.e("TAG", "Error reading JSON: " + e.getMessage());
}
```

### 4. Read Single JSON Object

```java
try {
    Map<String, Object> data = jsonReader.readJsonObjectFromAssets("config.json");
    String value = (String) data.get("key");
} catch (IOException | JSONException e) {
    Log.e("TAG", "Error reading JSON: " + e.getMessage());
}
```

### 5. Read Raw JSON String

```java
try {
    String jsonString = jsonReader.loadJsonFromAssets("data.json");
    // Process raw JSON string...
} catch (IOException e) {
    Log.e("TAG", "Error reading file: " + e.getMessage());
}
```

## Sample JSON File

The project includes a sample JSON file (`sample_data.json`) in the assets folder:

```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "age": 30
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane.smith@example.com",
    "age": 25
  }
]
```

## Class Methods

### `readJsonArrayFromAssets(String fileName)`
- **Returns**: `List<Map<String, Object>>`
- **Purpose**: Reads a JSON array from assets and converts it to a list of maps
- **Throws**: IOException, JSONException

### `readJsonObjectFromAssets(String fileName)`
- **Returns**: `Map<String, Object>`
- **Purpose**: Reads a single JSON object from assets and converts it to a map
- **Throws**: IOException, JSONException

### `loadJsonFromAssets(String fileName)`
- **Returns**: `String`
- **Purpose**: Reads the raw JSON string from assets
- **Throws**: IOException

## Error Handling

The class properly handles:
- File not found errors (IOException)
- Malformed JSON errors (JSONException)
- Nested JSON objects and arrays

Always wrap method calls in try-catch blocks to handle these exceptions appropriately.

## Requirements

- Android API level 24 or higher
- Uses built-in Android JSON parsing (org.json package)
- No external dependencies required