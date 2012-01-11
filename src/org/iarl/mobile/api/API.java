package org.iarl.mobile.api;

import java.util.Enumeration;
import java.util.Hashtable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class API {
    private static String API_URL = "http://iarl.org/api/";
    
    public void setTest() {
    	setTest(true);
    }
    
    public void setTest(boolean test) {
    	if (test) {
    		API_URL = "http://test.iarl.org/api/";
    	} else {
    		API_URL = "http://iarl.org/api/";
    	}
    }

    public JSONArray getArray(String path) {
    	return getArray(path, null);
    }
    
    public JSONArray getArray(String path, Hashtable<String,String> query) {
        String raw = httpGet(path, query);
        try {
            return new JSONArray(raw);
        } catch (Exception e) {
            Log.e(API.class.toString(),
            	"JSON error: " + e.getMessage());
        }
        return null;
    }
    
    public JSONObject getObject(String path) {
    	return getObject(path, null);
    }

    public JSONObject getObject(String path, Hashtable<String,String> query) {
        String raw = httpGet(path, query);
        try {
            return new JSONObject(raw);
        } catch (Exception e) {
        	Log.e(API.class.toString(),
                "JSON error: " + e.getMessage());
        }
        return null;
    }

    private String httpGet(String path, Hashtable<String,String> query) {
        String URL = API_URL + path;

        // Build URL
        if (query != null && query.size() > 0) {
            String queryString = "";
            Enumeration<String> keys = query.keys();
            while (keys.hasMoreElements()) {
                String key = (String) keys.nextElement();
                if (queryString.length() > 0) {
                    queryString = queryString + "&";
                }
                try {
                	queryString = String.format("%s%s=%s",
                		queryString,
                		key,
                		URLEncoder.encode(query.get(key), "UTF-8"));
                } catch (UnsupportedEncodingException e) {}
            }
            if (queryString.length() > 0) {
                URL = String.format("%s?%s",
                    URL,
                    queryString);
            }
        }

        Log.d(API.class.getCanonicalName(), "Fetching " + URL);
        
        // HTTP request
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(API.class.toString(),
                    "Failed to HTTP GET " + URL);
            }
        } catch (ClientProtocolException e) {
            Log.e(API.class.toString(),
                "Failed to HTTP GET " + URL + " : " + e.getMessage());
        } catch (IOException e) {
            Log.e(API.class.toString(),
                "Failed to HTTP GET " + URL + " : " + e.getMessage());
        }
        return builder.toString();
    }
}
