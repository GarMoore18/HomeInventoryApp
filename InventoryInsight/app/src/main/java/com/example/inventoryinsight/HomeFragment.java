package com.example.inventoryinsight;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    public static final String combined_URL = "http://10.0.0.184/InventoryDB/HomeFragPHP/select_recent_joined.php";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        ArrayList<Recents> arrayOfRecents = new ArrayList<>();
        RecentsAdapter adapter = new RecentsAdapter(getActivity(), arrayOfRecents);

        ListView listView = v.findViewById(R.id.listview);
        listView.setAdapter(adapter);

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, combined_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObjectFromArray =
                                        response.getJSONObject(i);

                                Recents recent = new Recents(jsonObjectFromArray.getString("barcode"),
                                        jsonObjectFromArray.getString("iid"),
                                        jsonObjectFromArray.getString("image"),
                                        jsonObjectFromArray.getString("lid"),
                                        jsonObjectFromArray.getString("username"),
                                        jsonObjectFromArray.getString("quantity"),
                                        jsonObjectFromArray.getString("last_modified"));

                                arrayOfRecents.add(recent);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapter.notifyDataSetChanged();
                        Log.d("JSONArray Response", "Items pulled: " + arrayOfRecents.size());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ((MainActivity)getActivity()).volleyRequestError();
            }
        });
        requestQueue.add(jsonArrayRequest);   //Add request to the queue

        return v;
    }

}