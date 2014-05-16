package com.astrov.yuri.dictionary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;


import java.util.List;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends ActionBarActivity implements View.OnClickListener,
        EditText.OnEditorActionListener {
    private static String from_lang = "Ru";
    private static String to_lang = "En";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static TextView t_from, t_to;
    private static final int REQUEST_FROM_LANG = 1;
    private static final int REQUEST_TO_LANG = 2;
    private static final int REQUEST_TO_ADD_WORD = 3;
    private static EditText editText;
    private static WordDB mDB;  // Our connection to the database.
    private static ArrayAdapter<String> adapter;
    private static final String TAG = "MainActivity";

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
        editText = (EditText) findViewById(R.id.editText);
        editText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_GO);
        editText.setOnEditorActionListener(this);

        Button addWord = (Button) findViewById(R.id.add_word_button);
        addWord.setOnClickListener(this);

        mDB = new WordDB(this);
        mDB.open();
        ListView listView = (ListView) findViewById(R.id.listView);
        List<String> values = mDB.queryAll();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values);
        listView.setAdapter(adapter);
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
        switch( item.getItemId() ) {
            case R.id.action_settings:
                return true;
            case R.id.action_drop_db_menu:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.dialog_drop_db_message)
                        .setTitle(R.string.dialog_drop_db_title)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDB.DropDB();
                            }

                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ;
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
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
                intent = new Intent(this, AddWordActivity.class);
                intent.putExtra("firstWord", editText.getText().toString());
                intent.putExtra("to_lang", to_lang);
                intent.putExtra("from_lang", from_lang);
                startActivityForResult(intent, REQUEST_TO_ADD_WORD);
                break;
        }
    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String lang, firstWord, secondWord;
            switch (requestCode){
                case REQUEST_FROM_LANG:
                    lang = data.getStringExtra("lang");
                    from_lang = lang;
                    t_from.setText(lang);
                    break;
                case REQUEST_TO_LANG:
                    lang = data.getStringExtra("lang");
                    to_lang = lang;
                    t_to.setText(lang);
                    break;
                case REQUEST_TO_ADD_WORD:
                    //from_lang = data.getStringExtra("from_lang");
                    //to_lang = data.getStringExtra("to_lang");
                    firstWord = data.getStringExtra("firstWord");
                    secondWord = data.getStringExtra("secondWord");
                    mDB.createWordRow(from_lang, to_lang, firstWord, secondWord);
                    Log.v(TAG, "REQUEST_TO_ADD_WORD");
                    List<String> values = mDB.queryAll();
                    adapter.clear();
                    adapter.addAll(values);
                    break;
            }
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // User press key. Here we search word from EditText in SQL
        Log.v(TAG, "onEditorAction");
        try{
        String input = v.getText().toString();
        List<String> list = mDB.queryWords(from_lang, to_lang, input);
        adapter.clear();
        adapter.addAll(list);
        } catch (NullPointerException e) {
            Toast.makeText(getApplicationContext(), "No elements to show!", Toast.LENGTH_LONG)
                    .show();
        }
        catch (Exception e) {
            Log.v(TAG, e.getMessage());
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
                    .show();
        }
        return false;
    }

}