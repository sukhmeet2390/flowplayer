package com.example.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.R;
import com.example.database.ContentProvider.DbHelper;
import com.example.database.ContentProvider.GigContentProvider;
import com.example.widgets.EditNameDialog;
import com.google.gson.Gson;

import java.util.ArrayList;

public class MainActivity extends ListActivity implements EditNameDialog.EditNameDialogListener, SearchView.OnQueryTextListener{
    /**
     * Called when the activity is first created.
     */
    private String TAG = "MAIN ACTIVITY";
    static final String[] FROM = {DbHelper.C_NAME};
    static final int[] TO = {R.id.gig_list_name};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displayItems("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.home_search);

        SearchView searchView = new SearchView(MainActivity.this);
        searchView.setOnQueryTextListener(this);
        item.setActionView(searchView);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createGig:
                Log.i(TAG, "Create Gig Click");
                showDialog();
                break;
            case R.id.home_search:
                Log.i(TAG,"Search Click");
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        EditNameDialog editNameDialog = new EditNameDialog();
        editNameDialog.show(getFragmentManager(), "text_dialog");

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(TAG, "ITEM CLICK");
        Intent playIntent = new Intent(this, PlayGigActivity.class);
        playIntent.putExtra("gig_id", (long) id);
        startActivity(playIntent);
    }

    @Override
    public void onFinishEditDialog(String inputText) {

        getContentResolver().insert(GigContentProvider.NAME_URI, com.example.Util.nameToContentValues(inputText));
        Toast.makeText(getApplicationContext(), "Welcome ! " + inputText + " Created, start adding ", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Created Gig "+ inputText);

        Intent createIntent = new Intent(this, CreateActivity.class);
        createIntent.putExtra("name", inputText);
        createIntent.putExtra("parentImage", "null");

        String[] al = new String[]{"null"};

        Gson gson =  new Gson();
        String value = gson.toJson(al);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("list",value);
        e.commit();

        //createIntent.putExtras(b);

        startActivity(createIntent);
    }

    @Override
    public void onPositiveButtonClick(DialogFragment dialogFragment) {
        EditText dialogText = (EditText) dialogFragment.getDialog().findViewById(R.id.dialog_text);
        String inputText = dialogText.getText().toString();
        onFinishEditDialog(inputText);
    }

    @Override
    public void onNegativeButtonClick(DialogFragment dialogFragment) {
        dialogFragment.dismiss();
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        displayItems(s);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onQueryTextChange(String s) {
        displayItems(s);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void  displayItems(String query){
        DbQueryActivity queries = new DbQueryActivity(getApplicationContext());
        Cursor cursor = queries.queryGigByName(query,this);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, FROM, TO,0);
        setListAdapter(adapter);

    }
}
