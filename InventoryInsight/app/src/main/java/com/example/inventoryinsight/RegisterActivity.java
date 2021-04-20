package com.example.inventoryinsight;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    public static final String register = "http://10.0.0.184/InventoryDB/Register/register_new_user.php";

    private EditText user, pass;
    private Button submit_btn;
    private String user_txt, pass_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        user = findViewById(R.id.user_name);
        pass = findViewById(R.id.user_pass);
        submit_btn = findViewById(R.id.submit_button);

        submitOnClick();
    }

    // Checks for a username and password
    private boolean allFieldsProvided() {
        return !user_txt.equals("") && !pass_txt.equals("");
    }

    private void registerRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, register, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", "Response:" + response);
                if (response.equals("0")) {
                    registerSuccessDialog();
                } else if (response.equals("1")) {
                    registerFailureDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", String.valueOf(error));
            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", user_txt);
                params.put("password", pass_txt);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void submitOnClick() {
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_txt = user.getText().toString().trim();
                pass_txt = pass.getText().toString().trim();

                //Log.d("Testing", username + " " + password);

                if (allFieldsProvided()) {
                    registerRequest(); // Request database check
                } else {
                    promptCheckFields();
                }
            }
        });
    }

    // Return to login and do not allow back button
    private void returnToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void registerSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Welcome new user!");
        builder.setMessage("Thanks for registering! You will now need to login.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                returnToLogin();
            }
        });
        builder.show();
    }

    // Dialog for failed register
    private void registerFailureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Username Taken");
        builder.setMessage("Sorry, another user has already selected that username. Please use another username.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // Alert to fill in all of the fields
    private void promptCheckFields() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);
        builder.setTitle("Provide Information");
        builder.setMessage("Please provide a username and password.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}