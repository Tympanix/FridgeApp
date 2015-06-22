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
import android.util.Log;
import android.view.View;

import java.util.Calendar;

public class FoodExpireBroadcastReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("FRIDGELOG", "Broadcast has been received!");

        if (intent == null){
            Log.i("FRIDGELOG", "Broadcast: No intent");
            return;
        }

        String action = intent.getAction();
        if (action == null){
            Log.i("FRIDGELOG", "Action is null");
            return;
        }

        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preference), Context.MODE_PRIVATE);
        int hours = sharedPreferences.getInt(context.getString(R.string.settings_timeHour), FridgeApp.DEFAULT_ALARM_HOUR);
        int minutes = sharedPreferences.getInt(context.getString(R.string.settings_timeMinute), FridgeApp.DEFAULT_ALARM_MINUTE);


        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(FridgeApp.ACTION_REG_ALARM)) {
            Log.i("FRIDGELOG", "Broadcast was caught by: " + action);
            setUpAlarmManager(context, hours, minutes);
        } else if (action.equals(FridgeApp.ACTION_NOTIFICATIONS)) {
            Log.i("FRIDGELOG", "Broadcast is creating notifications");
            showNotifications(context);
        }

    }


    public void setUpAlarmManager(Context context, int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Create an Intent to broadcast to the AlarmNotificationReceiver
        Intent alarmIntent = new Intent(context,
                FoodExpireBroadcastReceiver.class);

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
        // Build the Notification
        Notification.Builder notificationBuilder = new Notification.Builder(
                context).setTicker("Hej")
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setAutoCancel(true).setContentTitle("Title")
                .setContentText("Content text");

        // Get the NotificationManager
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Pass the Notification to the NotificationManager:
        mNotificationManager.notify(MY_NOTIFICATION_ID,
                notificationBuilder.getNotification());


    }
}
