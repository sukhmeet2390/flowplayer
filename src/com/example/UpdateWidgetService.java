package com.example;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.activity.MainActivity;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 07/10/13
 * Time: 5:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class UpdateWidgetService extends Service {
    private String LOG = "UPDATEWIDGETSERVICE";

    /**
     * @deprecated
     */
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(LOG, "Called");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());

        int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

        ComponentName thisWidget = new ComponentName(getApplicationContext(), MyWidgetProvider.class);
        int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
        Log.w(LOG, "From Intent" + String.valueOf(allWidgetIds.length));
        Log.w(LOG, "Direct" + String.valueOf(allWidgetIds2.length));

        for (int widgetId : allWidgetIds) {
            // Create some random data
            int number = (new Random().nextInt(100));

            RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(),R.layout.widget);
            Log.w("WidgetExample", String.valueOf(number));
            // Set the text
            remoteViews.setTextViewText(R.id.update,"Random: " + String.valueOf(number));

            // Register an onClickListener
            Intent clickIntent = new Intent(this.getApplicationContext(), MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
        stopSelf();

        super.onStart(intent, startId);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
