package com.example.widgets; /**
 * Created with IntelliJ IDEA.
 * User: sukhmeet
 * Date: 07/10/13
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import com.example.R;
import com.example.activity.MainActivity;

public class MyWidgetProvider extends AppWidgetProvider {
    private String TAG = "WIDGETPROVIDER";
    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        Log.i(TAG, "on App Widget options changed");
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "on deleted");
        super.onDeleted(context, appWidgetIds);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onEnabled(Context context) {
        Log.i(TAG, "on Enabled");
        super.onEnabled(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "on Disabled");
        super.onDisabled(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "on update");



        ComponentName thisWidget = new ComponentName(context, MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

            PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            RemoteViews remoteViews= new RemoteViews(context.getPackageName(), R.layout.widget);
            remoteViews.setOnClickPendingIntent(R.id.update, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}