package com.example.activity;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.R;
import com.example.widgets.EditNameDialog;

public class MainActivity extends Activity implements EditNameDialog.EditNameDialogListener{
    /**
     * Called when the activity is first created.
     */
    private String TAG = "MAIN ACTIVITY";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.createGig:
                Log.i(TAG, "Create Gig Click");
                showDialog();
        }
        return super.onOptionsItemSelected(item);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private void showDialog() {
        EditNameDialog editNameDialog = new EditNameDialog();
        editNameDialog.show(getFragmentManager(), "text_dialog");

    }


    @Override
    public void onFinishEditDialog(String inputText) {

        //To change body of implemented methods use File | Settings | File Templates.
        //getContentResolver().insert(GigNameContentProvider.NAME_URI, com.example.Utility.Helper.nameToContentValues(inputData));
        Toast.makeText(getApplicationContext(), "Welcome ! " + inputText + " Created, start adding ", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Created Gig "+ inputText);

        //Intent createIntent = new Intent(this, CreateGig.class);
        //createIntent.putExtra("name", inputData);
        //createIntent.putExtra("isFirstImage", "true");
        //startActivity(createIntent);
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
}
