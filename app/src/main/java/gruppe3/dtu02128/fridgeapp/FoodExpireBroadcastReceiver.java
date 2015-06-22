package gruppe3.dtu02128.fridgeapp;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FoodExpireBroadcastReceiver extends BroadcastReceiver {

    private static final int MY_NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("FRIDGELOG", "Broadcast has been received!");

        String action = intent.getAction();

        if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.i("FRIDGELOG", "Broadcast was caught by ACTION_BOOT");
            setUpAlarmManager();
        } else {
            Log.i("FRIDGELOG", "Broadcast is creating notifications");
            showNotifications();
        }

    }


    public void setUpAlarmManager(){

    }

    public void showNotifications(){
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
