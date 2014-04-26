package com.astrov.yuri.dictionary;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AddWordActivity extends ActionBarActivity implements View.OnClickListener {
    private static String from_lang = "Ru";
    private static String to_lang = "En";
    private static TextView t_from, t_to;
    private static EditText firstWord, secondWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);

        Intent intent = getIntent();
        from_lang = intent.getStringExtra("from_lang");
        to_lang = intent.getStringExtra("to_lang");
        firstWord = (EditText) findViewById(R.id.editText1);
        secondWord = (EditText) findViewById(R.id.editText2);
        try {
            firstWord.setText(
                intent.getStringExtra("firstWord")
            );
        }
        catch(Exception e){;}

        Button button = (Button) findViewById(R.id.button_add_word2);
        button.setOnClickListener(this);
        t_from = (TextView) findViewById(R.id.v_from_lang2);
        t_from.setText(from_lang);
        t_from.setOnClickListener(this);
        t_to = (TextView) findViewById(R.id.v_to_lang2);
        t_to.setText(to_lang);
        t_to.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_word, menu);
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
                //startActivityForResult(intent, REQUEST_FROM_LANG);
                break;
            case R.id.v_to_lang:
                // Choice new language
                //startActivityForResult(intent, REQUEST_TO_LANG);
                break;
            case R.id.button_add_word2:
                // Start Activity for add new word
                intent = new Intent();
                intent.putExtra("firstWord", firstWord.getText().toString());
                intent.putExtra("secondWord", secondWord.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

}
