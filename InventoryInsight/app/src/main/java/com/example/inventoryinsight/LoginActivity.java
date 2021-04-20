package com.example.inventoryinsight;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    public static final String login = "http://10.0.0.184/InventoryDB/Login/passwordHashing.php";

    private EditText user_name_input, user_pass_input;
    private Button login_but, register_btn;
    private String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(this.getSupportActionBar()).hide();

        user_name_input = findViewById(R.id.user_name);
        user_pass_input = findViewById(R.id.user_pass);

        login_but = findViewById(R.id.login);
        register_btn = findViewById(R.id.register);


        loginOnClick();
        registerOnClick();
    }

    private void startIntentRegister() {
        // Welcome user when the UI is redone
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void registerOnClick() {
        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startIntentRegister();
            }
        });
    }

    private void loginOnClick() {
        login_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = user_name_input.getText().toString().trim();
                password = user_pass_input.getText().toString().trim();

                Log.d("Testing", username + " " + password);
                if (checkRequiredLogin()) {
                    loginRequest();
                }
            }
        });
    }

    private boolean checkRequiredLogin() {
        boolean need = true;
        if (username.equals("")) {
            user_name_input.setError("Username required");
            need = false;
        }
        if (password.equals("")) {
            user_pass_input.setError("Password required");
            need = false;
        }
        return need;
    }

    public void showHidePassword(View view) {
        if(view.getId() == R.id.show_pass_img){

            if(user_pass_input.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.closed_eye_icon);

                //Show Password
                user_pass_input.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.open_eye_icon);

                //Hide Password
                user_pass_input.setTransformationMethod(PasswordTransformationMethod.getInstance());

            }
        }
    }

    private void loginRequest() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, login, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response", "Response:" + response);

                String[] arrOfStr = response.split(" ");

                Log.d("Array", Arrays.toString(arrOfStr));
                if (arrOfStr.length == 1) {
                    if (arrOfStr[0].equals(String.valueOf('0'))) {
                        loginSuccess();
                        user_name_input.setText("");
                        user_pass_input.setText("");
                    } else {
                        loginFailure();
                        //Log.d("Not found length 1", "The user could not be found");
                    }
                } else if (arrOfStr.length == 2) {
                    //Login successful
                    if (arrOfStr[1].equals(String.valueOf('0'))) {
                        loginSuccess();
                        user_name_input.setText("");
                        user_pass_input.setText("");
                    }
                    else {
                        loginFailure();
                        //Log.d("Not found length 2", "The user could not be found");
                    }
                }
                username = "";
                password = "";
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams()  {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void loginSuccess() {
        // Welcome user when the UI is redone
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void loginFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

        builder.setCancelable(false);
        builder.setTitle("Invalid Credentials");
        builder.setMessage("There is not a user that matches those credentials.");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
}