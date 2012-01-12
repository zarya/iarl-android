package org.iarl.mobile.api;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.util.Log;

public class API {
    private static String API_URL = "http://iarl.org/api/";
    private static AsyncHttpClient client = new AsyncHttpClient();

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

    public void getJson(String path, JsonHttpResponseHandler handler) {
        httpGet(path, null, handler);
    }

    public void getJson(String path, RequestParams query, JsonHttpResponseHandler handler) {
        httpGet(path, query, handler);
    }

    private void httpGet(String path, RequestParams query, AsyncHttpResponseHandler handler) {
        String URL = API_URL + path;
        Log.d(API.class.getCanonicalName(), "Fetching " + URL);
        client.get(URL, query, handler);
    }
}
