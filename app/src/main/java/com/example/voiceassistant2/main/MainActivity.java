package com.example.voiceassistant2.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import com.example.voiceassistant2.R;
import com.example.voiceassistant2.dbh.DBHelper;
import com.example.voiceassistant2.dialog.AI;
import com.example.voiceassistant2.msg.Message;
import com.example.voiceassistant2.msg.MessageEntity;
import com.example.voiceassistant2.msg.MessageListAdapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    protected Button sendButton;
    protected EditText questionText;
    protected TextToSpeech textToSpeech;
    protected RecyclerView chatMessageList;
    protected MessageListAdapter messageListAdapter;
    SharedPreferences sPref;
    public static final String APP_PREFERENCES = "mysettings";
    private boolean isLight = true;
    private String THEME = "THEME";
    DBHelper dBHelper;
    SQLiteDatabase database;

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("mL", messageListAdapter.messageList);
        Log.i("LOG", "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        messageListAdapter.messageList = (ArrayList<Message>) savedInstanceState.getSerializable("mL");
        Log.i("LOG", "onRestoreInstanceState");
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i("LOG", "onDestroy");
    }

    protected void onPause() {
        super.onPause();
        Log.i("LOG", "onPause");
    }

    protected void onRestart() {
        super.onRestart();
        Log.i("LOG", "onRestart");
    }

    protected void onResume() {
        super.onResume();
        Log.i("LOG", "onResume ");
    }

    protected void onStart() {
        super.onStart();
        Log.i("LOG", "onStart");
    }

    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(THEME, isLight);
        editor.apply();

        database.delete(dBHelper.TABLE_MESSAGES, null, null);
        for (int i = 0; i < messageListAdapter.messageList.size(); i++) {
            MessageEntity entity = new MessageEntity(messageListAdapter.messageList.get(i));
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.FIELD_MESSAGE, entity.text);
            contentValues.put(DBHelper.FIELD_SEND, entity.isSend);
            contentValues.put(DBHelper.FIELD_DATE, entity.date);
            database.insert(dBHelper.TABLE_MESSAGES,null,contentValues);
        }

        Log.i("LOG", "onStop");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day_settings:
                //установка дневной темы
                isLight = true;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.night_settings:
                //установка ночной темы
                isLight = false;
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    void switchLight()
    {
        isLight = sPref.getBoolean(THEME, true);
        if(isLight == true){
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sPref = getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        switchLight();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendButton = findViewById(R.id.sendButton);
        questionText = findViewById(R.id.questionField);
        chatMessageList = findViewById(R.id.chatMessageList);
        messageListAdapter = new MessageListAdapter();
        chatMessageList.setLayoutManager(new LinearLayoutManager(this));
        chatMessageList.setAdapter(messageListAdapter);
        dBHelper = new DBHelper(this);
        database = dBHelper.getWritableDatabase();

        Cursor cursor = database.query(dBHelper.TABLE_MESSAGES, null, null, null,
                null, null, null);
        if (cursor.moveToFirst()){
            int messageIndex = cursor.getColumnIndex(dBHelper.FIELD_MESSAGE);
            int dateIndex = cursor.getColumnIndex(dBHelper.FIELD_DATE);
            int sendIndex = cursor.getColumnIndex(dBHelper.FIELD_SEND);
            do{
                MessageEntity entity = new MessageEntity(cursor.getString(messageIndex),
                        cursor.getString(dateIndex), cursor.getInt(sendIndex));
                Message message = new Message();
                try {
                    message = new Message(entity);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                messageListAdapter.messageList.add(message);
            }while (cursor.moveToNext());
        }
        cursor.close();

        sendButton.setOnClickListener(new View.OnClickListener() {

            protected void onSend() {
                AI ai = new AI();
                String text = questionText.getText().toString();
                messageListAdapter.messageList.add(new Message(text,true));
                messageListAdapter.notifyDataSetChanged();

                ai.getAnswer(text, new Consumer<String>() {
                    @Override
                    public void accept(String answer) {
                        messageListAdapter.messageList.add(new Message(answer, false));
                        messageListAdapter.notifyDataSetChanged();
                        textToSpeech.speak(answer, TextToSpeech.QUEUE_FLUSH, null, null);
                        chatMessageList.smoothScrollToPosition(chatMessageList.getAdapter().getItemCount() - 1);
                    }
                });

                //chatMessageList.scrollToPosition(messageListAdapter.messageList.size() - 1);
                questionText.setText("");

            }

            @Override
            public void onClick(View view) {
                onSend();
                chatMessageList.smoothScrollToPosition(chatMessageList.getAdapter().getItemCount() - 1);

            }

        });


        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!= TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(new Locale("ru"));
                }
            }
        });
        chatMessageList.smoothScrollToPosition(chatMessageList.getAdapter().getItemCount() - 1);
        Log.i("LOG", "onCreate");

    }

    /*http://developer.alexanderklimov.ru/android/theory/activity_methods.php#onsaveinstancestate*/

}
