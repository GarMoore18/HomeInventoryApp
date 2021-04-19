package com.example.inventoryinsight;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsFragment extends Fragment {

    private Button logout_btn;
    private SwitchMaterial toggle_color;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        logout_btn = v.findViewById(R.id.logout);
        toggle_color = v.findViewById(R.id.color_toggle);

        toggle_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((MainActivity)getActivity()).getcTheme();
                //Log.d("Test", setTheme(android.R.style.Theme));
                //themeUtils.changeToTheme(this, themeUtils.BLUE);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).logoutDialog();
            }
        });

        return v;   //Returning the view for the fragment
    }
}