package com.example.dean.androidlabs;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChatWindow extends Activity {
    Button sendButton;
    ListView listView;
    EditText messageField;
    final ArrayList<String> listViewContent = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        sendButton = (Button) findViewById(R.id.button_send_message);
        listView = (ListView) findViewById(R.id.list_view_chat);
        messageField   = (EditText) findViewById(R.id.edittext_enter_message);
        final ChatAdapter messageAdapter = new ChatAdapter(this);
        listView.setAdapter(messageAdapter);
        final ChatDatabaseHelper dbHelper = new ChatDatabaseHelper(this);
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(false,dbHelper.getTableName(),new String[]{dbHelper.getKeyMessage()},null,null,null,null,null,null);
        Log.i("ChatActivity","Cursor's column count="+c.getColumnCount());

        int collndex = c.getColumnIndex(dbHelper.getKeyMessage());
        c.moveToFirst();
        while (!c.isAfterLast()){

            Log.i("ChatActivity","SQL MESSAGE:"+c.getString(c.getColumnIndex(dbHelper.getKeyMessage())));
            Log.i("ChatActivity","column name="+c.getColumnName(collndex));
            String curMessage = c.getString(collndex);
            listViewContent.add(curMessage);
            c.moveToNext();

        }
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                ContentValues values = new ContentValues();
                values.put(dbHelper.getKeyMessage(), messageField.getText().toString());
                db.insert(dbHelper.getTableName(),null,values);
              listViewContent.add(messageField.getText().toString());
                messageAdapter.notifyDataSetChanged();
                messageField.setText("");
            }
        } );

    }
    protected void onDestroy(){
        super.onDestroy();
             //  db.close();

    }
private class ChatAdapter extends ArrayAdapter<String>{
        public ChatAdapter(Context ctx){
            super(ctx,0);
        }
        public int getCount(){
            return listViewContent.size();
        }
        public String getItem(int position){
        return listViewContent.get(position);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result;
            if(position%2==0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView) result.findViewById(R.id.messageText);
            message.setText(getItem(position));
            return result;
        }
    }



}

