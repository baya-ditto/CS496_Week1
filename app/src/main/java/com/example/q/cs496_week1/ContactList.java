package com.example.q.cs496_week1;

import android.support.v7.util.SortedList;
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
    JSONArray contactJSON_starred;
    ArrayList<String> starredNames = new ArrayList<>();

    public ContactList() {
        contactJSON = new JSONArray();
        contactJSON_starred = new JSONArray();
    }

    public void addContact(String contactId, String contactName, ArrayList<Pair<String, String>> contactNumbers, ArrayList<Triplet<String, String, String>> contactEmails, String contactNote, String starred) throws JSONException {
        JSONObject contact = new JSONObject();

        contact.put("contactid", contactId);
        contact.put("name", contactName);
        contact.put("note", contactNote);
        contact.put("starred", starred);

        JSONArray number_infos = new JSONArray();
        for (Pair<String, String> pair : contactNumbers){
            JSONObject number_info = new JSONObject();
            try {
                number_info.put("number", pair.first);
                number_info.put("id", pair.second);
                number_infos.put(number_info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        contact.put("numbers", number_infos);

        JSONArray email_infos = new JSONArray();
        for (Triplet<String, String, String> trip : contactEmails) {
            JSONObject email_info = new JSONObject();
            try {
                email_info.put("email", trip.first);
                email_info.put("type", trip.second);
                email_info.put("id", trip.third);
                email_infos.put(email_info);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        contact.put("emails",email_infos);

        contactJSON.put(contact);
        if (starred.equals("1")) {
            contactJSON_starred.put(contact);
            starredNames.add(contactName);
        }
    }

    public void sorting(boolean starred)  {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonList = new ArrayList<JSONObject>();
        JSONArray target_JSON = (starred) ? contactJSON_starred : contactJSON;
        for (int i = 0; i < target_JSON.length(); i++) {
            try {
                jsonList.add(target_JSON.getJSONObject(i));
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
        for (int i = 0; i < target_JSON.length(); i++) {
            sortedJsonArray.put(jsonList.get(i));
        }
        if (starred)
            contactJSON_starred = sortedJsonArray;
        else
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

    public ArrayList<String> getStarredNameArray(boolean sorted) {
        if (sorted) {
            Collections.sort( starredNames, new Comparator<String>() {
                public int compare(String a, String b) {
                    return a.compareTo(b);
                }
            });
        }
        return starredNames;
    }

    public int getLength() {
        return contactJSON.length();
    }

    public JSONObject getJSONObjectByIndex(int index) {
        JSONObject contact = null;
        try {
            contact = (JSONObject) contactJSON.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contact;
    }

    public JSONObject getJSONObjectByIndex_starred(int index) {
        JSONObject contact = null;
        try {
            contact = (JSONObject) contactJSON_starred.get(index);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return contact;
    }


    public JSONObject getJSONObjectByContactId(String contactid){
        JSONObject target = null;
        for (int i = 0; i < contactJSON.length(); ++i){
            JSONObject contact = getJSONObjectByIndex(i);
            if (contact == null)
                continue;
            if (contact.optString("contactid", "-1").equals(contactid)){
                target = contact;
                break;
            }
        }
        return target;
    }

}
