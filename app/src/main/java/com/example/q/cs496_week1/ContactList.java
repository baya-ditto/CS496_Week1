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

    public void addContact(String contactId, String contactName, ArrayList<Pair<String, String>> contactNumbers, ArrayList<Triplet<String, String, String>> contactEmails, String contactNote) throws JSONException {
        JSONObject contact = new JSONObject();

        contact.put("contactid", contactId);
        contact.put("name", contactName);
        contact.put("note", contactNote);

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

    public JSONObject getJSONObjectByIndex(int index) {
        JSONObject contact = null;
        try {
            contact = (JSONObject) contactJSON.get(index);
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
