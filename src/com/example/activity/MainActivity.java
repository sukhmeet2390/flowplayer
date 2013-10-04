package com.example.activity;

import android.app.DialogFragment;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.R;
import com.example.database.ContentProvider.DbHelper;
import com.example.widgets.EditNameDialog;
import com.google.gson.Gson;

public class MainActivity extends ListActivity implements EditNameDialog.EditNameDialogListener, SearchView.OnQueryTextListener {

    private String TAG = "MAIN ACTIVITY";
    static final String[] FROM = {DbHelper.C_NAME};
    static final int[] TO = {R.id.gig_list_name};
    DbQueryActivity queries;
    SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queries = new DbQueryActivity(getApplicationContext());
        setContentView(R.layout.main);
        registerForContextMenu(getListView());
        displayItems("");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);    //To change body of overridden methods use File | Settings | File Templates.
        getMenuInflater().inflate(R.menu.actions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deleteItem:
                long id = info.id;
                String name = queries.deleteGigNameAndData(id, getApplicationContext());
                //getListView().invalidate();
                displayItems("");
                Toast.makeText(getApplicationContext(), "Deleted " + name, Toast.LENGTH_LONG).show();
                return true;
            default:
                return false;
        }

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
        switch (item.getItemId()) {
            case R.id.createGig:
                Log.i(TAG, "Create Gig Click");
                showDialog();
                break;
            case R.id.home_search:
                Log.i(TAG, "Search Click");
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
        Intent playIntent = new Intent(this, PlayGigActivity.class);
        playIntent.putExtra("gig_id", id);
        startActivity(playIntent);
    }

    @Override
    public void onFinishEditDialog(String inputText) {

        queries.insertName(inputText,getApplicationContext());
        Toast.makeText(getApplicationContext(), "Welcome ! " + inputText + " Created, start adding ", Toast.LENGTH_LONG).show();

        Intent createIntent = new Intent(this, CreateActivity.class);
        createIntent.putExtra("name", inputText);
        createIntent.putExtra("parentImage", "null");

        String[] al = new String[]{"null"};

        Gson gson = new Gson();
        String value = gson.toJson(al);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = prefs.edit();
        e.putString("list", value);
        e.commit();

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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        displayItems(s);
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void displayItems(String query) {
        DbQueryActivity queries = new DbQueryActivity(getApplicationContext());
        Cursor cursor = queries.queryGigByName(query, getApplicationContext());
        adapter = new SimpleCursorAdapter(this, R.layout.list_row, cursor, FROM, TO, 0);
        setListAdapter(adapter);


    }
}
