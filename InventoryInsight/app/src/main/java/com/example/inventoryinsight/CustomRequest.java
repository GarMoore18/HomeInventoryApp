package com.example.inventoryinsight;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

public class CustomRequest extends JsonRequest<JSONArray> {

    protected static final String PROTOCOL_CHARSET = "utf-8";

    public CustomRequest(int method, String url, String requestBody, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, url, requestBody, listener, errorListener);
    }

    public CustomRequest(String url, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(Method.GET, url, null, listener, errorListener);
    }

    public CustomRequest(int method, String url, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, url, null, listener, errorListener);
    }

    public CustomRequest(int method, String url, JSONArray jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
    }

    public CustomRequest(int method, String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener, errorListener);
    }

    public CustomRequest(String url, JSONArray jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest, listener, errorListener);
    }

    public CustomRequest(String url, JSONObject jsonRequest, Listener<JSONArray> listener, ErrorListener errorListener) {
        this(jsonRequest == null ? Method.GET : Method.POST, url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

}