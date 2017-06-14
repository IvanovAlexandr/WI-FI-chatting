package com.example.ivanovalexander.finalproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


public class SettingActivity extends AppCompatActivity {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_NAME = "nickname";
    public static final String APP_PREFERENCES_CHECK = "check";

    SharedPreferences mySharedPreferences;
    EditText nicknameEditText;
    CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        nicknameEditText = (EditText) findViewById(R.id.nicknameEditText);
        checkBox = (CheckBox) findViewById(R.id.checkAnonymous);

        mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        loadSettings();

    }

    private void loadSettings() {
        SharedPreferences mySharedPreferences = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if(mySharedPreferences.contains(APP_PREFERENCES_CHECK)) {
            checkBox.setChecked(mySharedPreferences.getBoolean(APP_PREFERENCES_CHECK, false));
        }

        if(mySharedPreferences.contains(APP_PREFERENCES_NAME)) {
            nicknameEditText.setText(mySharedPreferences.getString(APP_PREFERENCES_NAME, ""));

        }

    }

    public void onClick(View view) {
        String nickname = nicknameEditText.getText().toString();
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(APP_PREFERENCES_NAME, nickname);
        editor.apply();
        Toast.makeText(this, R.string.save, Toast.LENGTH_SHORT)
                .show();
    }

    public void onClickCheckBox(View view) {
        boolean notAnonymous = checkBox.isChecked();
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(APP_PREFERENCES_CHECK, notAnonymous);
        editor.apply();
        if (notAnonymous) {
            Toast.makeText(this, R.string.you_not_anonymous, Toast.LENGTH_SHORT)
                    .show();
        }
        else {
            Toast.makeText(this, R.string.you_anonymous, Toast.LENGTH_SHORT)
                    .show();
        }

    }
}
