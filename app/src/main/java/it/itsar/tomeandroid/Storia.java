package it.itsar.tomeandroid;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Storia implements Serializable {
    private String id;
    private String title;
    private String summary;
    private String content;

    public Storia() {
    }

    public Storia(String id, String title, String summary, String content) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.content = content;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();

        try  {
            jsonObject.put("id",this.id);
            jsonObject.put("title",this.title);
            jsonObject.put("summary",this.summary);
            jsonObject.put("content",content);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return getTitle();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
