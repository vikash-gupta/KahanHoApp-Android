package theguywith3thumbs.kahanho;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

/**
 * Created by vikash.gupta on 1/14/2016.
 */
public class Notifier {

    private Context activityContext;

    public Notifier(Context context)
    {
        activityContext = context;
    }
    public void SendNotification(String title, String msg)
    {
        Intent intent = new Intent(activityContext, MainActivity.class);
        //PendingIntent pIntent = PendingIntent.getActivity(activityContext, (int) System.currentTimeMillis(), intent, 0); TODO

        Notification.Builder builder = new Notification.Builder(activityContext)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pIntent)
                ;

        Notification noti;
        if (Build.VERSION.SDK_INT < 16) {
            noti = builder.getNotification();
        } else {
            noti = builder.build();
        }

        NotificationManager notificationManager = (NotificationManager) activityContext.
                getSystemService(activityContext.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, noti);
    }
}
