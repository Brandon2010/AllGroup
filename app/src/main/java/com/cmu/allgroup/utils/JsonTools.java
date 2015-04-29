package com.cmu.allgroup.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangxi on 4/6/15.
 */
public class JsonTools {
    /**
     * Get the place maps
     *
     * @param key
     * @param jsonString
     * @return
     */
    public static List<Map<String, Object>> getEvents(String key,
                                                      String jsonString) {

        ArrayList<Map<String, Object>> events = new ArrayList<Map<String, Object>>();
        try {

            Log.v("DEBUG", jsonString);
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject eventJsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("eventId", eventJsonObject.getLong("eventId"));
                map.put("name", eventJsonObject.getString("name"));
                map.put("description", eventJsonObject.getString("description"));
                map.put("location", eventJsonObject.getString("location"));
               // SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Log.v("DEBUG", eventJsonObject.getString("time"));
                map.put("time", eventJsonObject.getString("time"));

                // map.put("time", sdfDate.format(eventJsonObject.getString("time")));
//                map.put("picture_address",
//                        eventJsonObject.getString("picture_address"));
                events.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return events;
    }

    public static List<Map<String, Object>> getPosts(String key, String jsonString) {
        ArrayList<Map<String, Object>> posts = new ArrayList<Map<String, Object>>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject postJsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("postId", postJsonObject.getLong("postId"));
                map.put("eventId", postJsonObject.getLong("eventId"));
                map.put("content", postJsonObject.getString("content"));
//                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                map.put("time", postJsonObject.getString("time"));
                map.put("userId", postJsonObject.getLong("userId"));
                JSONObject userJsonObject = postJsonObject.getJSONObject("user");
                map.put("username", userJsonObject.getString("name"));
                posts.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return posts;
    }

    public static List<Map<String, Object>> getCategories(String key, String jsonString) {
        ArrayList<Map<String, Object>> cates = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cateJsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("cateId", cateJsonObject.getLong("cateId"));
                map.put("userId", cateJsonObject.getLong("userId"));
                map.put("name", cateJsonObject.getString("name"));
                cates.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cates;
    }

    public static List<Map<String, Object>> getUsers(String key, String jsonString) {
        ArrayList<Map<String, Object>> users = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject userJsonObject = jsonArray.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("facebookId", userJsonObject.getLong("facebookId"));
                map.put("name", userJsonObject.getString("name"));
                map.put("userId", userJsonObject.getLong("userId"));
                users.add(map);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return users;
    }
}
