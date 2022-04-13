package com.mirea.lavrenov.notebook;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Notebook";
    EditText textInputEditName;
    EditText textInputEditText;
    private SharedPreferences preferences;
    final String TEXT_TAG_NAME = "filename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getPreferences(MODE_PRIVATE);
        textInputEditName = findViewById(R.id.editTextName);
        textInputEditText = findViewById(R.id.editTextText);

        new Thread(new Runnable() {
            @Override
            public void run() {
                loadPreferences();
                loadFile();
            }
        }).start();
    }

    public void loadPreferences() {
//         Загрузка значения по ключу
        String text = preferences.getString(TEXT_TAG_NAME, "new file");
        textInputEditName.setText(text);
    }

    public void savePreferences() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(TEXT_TAG_NAME, textInputEditName.getText().toString());
        editor.apply();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
    }

    public void loadFile() {
        textInputEditText.setText(getTextFromFile());
    }
    // открытие файла
    public String getTextFromFile() {
        FileInputStream fin = null;
        try {
            fin = openFileInput(textInputEditName.getText().toString() + ".txt");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String(bytes);
            Log.d(LOG_TAG, text);
            return text;
        } catch (IOException ex) {
            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (fin != null)
                    fin.close();
            } catch (IOException ex) {
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return "Write your text";
    }


    public void saveFile() {
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput(textInputEditName.getText().toString() + ".txt", Context.MODE_PRIVATE);
            outputStream.write(textInputEditText.getText().toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        savePreferences();
        saveFile();
    }
}