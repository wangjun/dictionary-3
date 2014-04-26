package com.astrov.yuri.dictionary;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.content.Intent;

import com.astrov.yuri.dictionary.ChoiceLanguageActivity;
import com.astrov.yuri.dictionary.R;

public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        EditText.OnEditorActionListener {
    private static String from_lang = "Ru";
    private static String to_lang = "En";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static TextView t_from, t_to;
    private static final int REQUEST_FROM_LANG = 1;
    private static final int REQUEST_TO_LANG = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        from_lang = settings.getString("from_lang", "Ru");
        to_lang = settings.getString("to_lang", "En");

        Button button = (Button) findViewById(R.id.switch_lang_button);
        button.setOnClickListener(this);

        t_from = (TextView) findViewById(R.id.v_from_lang);
        t_from.setText(from_lang);
        t_from.setOnClickListener(this);
        t_to = (TextView) findViewById(R.id.v_to_lang);
        t_to.setText(to_lang);
        t_to.setOnClickListener(this);
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(this);

        Button addWord = (Button) findViewById(R.id.add_word_button);
        addWord.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Save options.
        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("from_lang", from_lang);
        editor.putString("to_lang", to_lang);
        // Commit the edits!
        editor.commit();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ChoiceLanguageActivity.class);
        switch(v.getId()) {
            case R.id.switch_lang_button:
                // Do something in response to button click
                String s = to_lang;
                to_lang = from_lang;
                from_lang = s;
                t_from.setText(from_lang);
                t_to.setText(to_lang);
                break;
            case R.id.v_from_lang:
                // Choice new base language
                startActivityForResult(intent, REQUEST_FROM_LANG);
                break;
            case R.id.v_to_lang:
                // Choice new language
                startActivityForResult(intent, REQUEST_TO_LANG);
                break;
            case R.id.add_word_button:
                // Start Activity for add new word
                //intent = new Intent(this, AddWordActivity.class);
                //startActivity(intent);
                break;
        }
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String lang = data.getStringExtra("lang");
            switch (requestCode){
                case REQUEST_FROM_LANG:
                    from_lang = lang;
                    t_from.setText(lang);
                    break;
                case REQUEST_TO_LANG:
                    to_lang = lang;
                    t_to.setText(lang);
                    break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // User press key. Here we search word from EditText in SQL
        String input = v.getText().toString();
        return false;
    }

}