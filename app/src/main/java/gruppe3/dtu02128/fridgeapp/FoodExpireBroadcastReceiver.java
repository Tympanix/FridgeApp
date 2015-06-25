package gruppe3.dtu02128.fridgeapp;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

public class FoodExpireBroadcastReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.i("FRIDGELOG", "Broadcast has been received!");

        if (intent == null){
            //Log.i("FRIDGELOG", "Broadcast: No intent");
            return;
        }

        String action = intent.getAction();
        if (action == null){
            //Log.i("FRIDGELOG", "Action is null");
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference), Context.MODE_PRIVATE);
        int hours = sharedPreferences.getInt(context.getString(R.string.settings_timeHour), FridgeApp.DEFAULT_ALARM_HOUR);
        int minutes = sharedPreferences.getInt(context.getString(R.string.settings_timeMinute), FridgeApp.DEFAULT_ALARM_MINUTE);


        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(FridgeApp.ACTION_REG_ALARM)) {
            //Log.i("FRIDGELOG", "Broadcast was caught by: " + action);
            setUpAlarmManager(context, hours, minutes);
        } else if (action.equals(FridgeApp.ACTION_NOTIFICATIONS)) {
            //Log.i("FRIDGELOG", "Broadcast is creating notifications");
            showNotifications(context);
        }

    }


    public void setUpAlarmManager(Context context, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        //Log.i("FRIDGELOG", "Setting alarm to hour " + hour + " and minute: " + minute);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent alarmIntent = new Intent(context,
                FoodExpireBroadcastReceiver.class);
        alarmIntent.setAction(FridgeApp.ACTION_NOTIFICATIONS);

        // Create an PendingIntent that holds the NotificationReceiverIntent
        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmPendingIntent);
    }

    public void showNotifications(Context context){
        FridgeApp fridgeApp = (FridgeApp) context.getApplicationContext();
        int daysBefore = 0;
        ItemDatabaseHelper dbhelp = fridgeApp.getDBHelper();
        Cursor cursor = dbhelp.getExpiredFood(daysBefore);
        int count = cursor.getCount();

        if (count == 0){
            //Log.i("FRIDGELOG", "No items expiring");
            return;
        }

        String ticker;
        if (daysBefore == 0){
            ticker = "Today you have " + count + " items expiring";
        } else {
            ticker = "In " + daysBefore + " days " + count + " items will expire";
        }

        ArrayList<String> items = new ArrayList<String>(3);
        while (cursor.moveToNext()){
            items.add(cursor.getString(cursor.getColumnIndexOrThrow(dbhelp.FOOD_NAME)));
            if (items.size() == 3){
                break;
            }
        }


        String sampleItems = "You have ";
        for (int i = 0; i < items.size(); i++){
            sampleItems += items.get(i);
            if (i == items.size()-1){
                break;
            }
            if (i == items.size()-2){
                sampleItems += " and ";
            } else {
                sampleItems += ", ";
            }
        }

        sampleItems += " expiring";

        // Build the Notification
        Intent clickIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingClickIntent =
                PendingIntent.getActivity(
                        context,
                        0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker(ticker)
                .setSmallIcon(R.drawable.ic_stat_fridgeappsillouetteicon)
                .setAutoCancel(true).setContentTitle(ticker)
                .setContentText(sampleItems)
                .setContentIntent(pendingClickIntent);

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.getNotification());

    }
}
