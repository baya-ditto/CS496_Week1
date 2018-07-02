package com.example.q.cs496_week1;

import android.util.Pair;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ContactList {

    JSONArray contactJSON;

    public ContactList() {
        contactJSON = new JSONArray();
    }

    public void addContact(String contactId, String contactName, ArrayList<String> contactNumbers, ArrayList<Pair<String, String>> contactEmails, String contactNote) throws JSONException {
        JSONObject contact = new JSONObject();

        contact.put("id", contactId);
        contact.put("name", contactName);
        contact.put("numbers", new JSONArray(contactNumbers));
        contact.put("note", contactNote);

        JSONArray email_infos = new JSONArray();
        for (Pair<String, String> pair : contactEmails) {
            JSONObject email_info = new JSONObject();
            try {
                email_info.put("email", pair.first);
                email_info.put("type", pair.second);
                email_infos.put(email_info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        contact.put("emails",email_infos);
        contactJSON.put(contact);
    }

    public void sorting()  {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        for (int i = 0; i < contactJSON.length(); i++) {
            try {
                jsonList.add(contactJSON.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort( jsonList, new Comparator<JSONObject>() {

            public int compare(JSONObject a, JSONObject b) {
                String valA = new String();
                String valB = new String();

                try {
                    valA = (String) a.get("name");
                    valB = (String) b.get("name");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                return valA.compareTo(valB);
            }
        });
        for (int i = 0; i < contactJSON.length(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }
        contactJSON = sortedJsonArray;
    }


    public ArrayList<String> getNameArray() {
        ArrayList<String> names = new ArrayList<String>();
        for(int i=0;i< contactJSON.length();i++){
            try {
                names.add(contactJSON.getJSONObject(i).optString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return names;
    }

    public int getLength() {
        return contactJSON.length();
    }

    public JSONObject getJSONObject(int index) {
        JSONObject contact = new JSONObject();
        try {
            contact = (JSONObject) contactJSON.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contact;
    }

}
