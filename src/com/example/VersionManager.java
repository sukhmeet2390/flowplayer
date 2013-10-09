package com.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 09/10/13
 * Time: 12:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class VersionManager {
    private static final String TAG = "VERSIONMANGER";
    private String versionCheckUrl;
    private Activity activity;
    private Context context;
    private AlertDialogButtonListener listener;
    VersionRequest versionRequest;
    private int latestServerVersion;

    public VersionManager(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        this.listener = new AlertDialogButtonListener();
    }

    public String getVersionCheckUrl() {
        return versionCheckUrl;
    }

    public void setVersionCheckUrl(String versionCheckUrl) {
        this.versionCheckUrl = versionCheckUrl;
    }

    public void checkNewVersion() {
        Log.i("VERSIONMANAGERCHECK", "Checking for new Version...");
        if (com.example.Util.checkConnectivity(context)) {
            String versionUrl = getVersionCheckUrl();
            updateLatestServerVersionCode(versionUrl);
            if (versionUrl == null) {
                Log.e(TAG, "Version Url is Null");
                return;
            }
            if (isUpdateAvailable()) {
                Log.i(TAG, "Update Available ! ");
                showDialog();
            }
        } else {
            Toast.makeText(context, "Update check failed ! You are not connected to internet right now", Toast.LENGTH_LONG).show();
        }


    }

    private int getServerVersionCode() {
        return latestServerVersion;
    }

    private void setServerVersionCode(int versionNumber) {
        latestServerVersion = versionNumber;
    }

    private void updateLatestServerVersionCode(String url) {
        versionRequest = new VersionRequest(context);
        String data = null;
        try {
            data = versionRequest.execute(url).get();
            JSONObject jsonObject = new JSONObject(data);
            int latestServerVersion = jsonObject.getInt("version");
            setServerVersionCode(latestServerVersion);
            Log.i(TAG, "Latest version from server updated");
        } catch (InterruptedException e) {
            Log.i(TAG, "Failed to update the version");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ExecutionException e) {
            Log.i(TAG, "Failed to update the version");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (JSONException e) {
            Log.i(TAG, "Failed to update the version");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setIcon(R.drawable.gnome_list_add);
        builder.setTitle("Update Available...");
        builder.setMessage("A new update is available message\n Older version " + getVersionCode() + " and new Version " + getServerVersionCode());
        builder.setPositiveButton("Update Now", listener);
        builder.setNegativeButton("Later", listener);
        builder.setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private boolean isUpdateAvailable() {
        int currentVersion = getVersionCode();
        int serverVersion = getServerVersionCode();
        if (currentVersion < serverVersion) {
            return true;
        }
        return false;
    }

    public int getVersionCode() {
        int code = -1;
        try {
            code = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Version Code not found");
        } catch (NullPointerException e) {
            Log.e(TAG, "Context is null");
        }
        return code;
    }


    private void ignoreVersion() {

    }

    private void updateNow(String url) {
        Log.i(TAG, "Updating from ..." + url);

        // do some ansync task
        // 1-> Check connectivity
        // 2->  launch the browser with the URL

    }


    private class VersionRequest extends AsyncTask<String, String, String> {
        Context context;

        private VersionRequest(Context context) {
            this.context = context;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... updateUrl) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpClient.execute(new HttpGet(updateUrl[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();

                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (IOException e) {
                Log.i(TAG, "IO exceoption while getting file");
            }

            return responseString;
        }
    }

    private class AlertDialogButtonListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int position) {
            switch (position) {
                case AlertDialog.BUTTON_POSITIVE:
                    updateNow(getVersionCheckUrl());
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    ignoreVersion();
                    break;
            }

        }
    }
}
