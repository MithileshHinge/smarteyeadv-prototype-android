package com.example.app1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sibhali on 12/19/2016.
 */
public class NotifyService extends Service {

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    public static final byte BYTE_PEOPLE_VDOGENERATING = 1, BYTE_PEOPLE_VDOGENERATED = 2, BYTE_ALERT1 = 3, BYTE_ALERT2 = 4, BYTE_ABRUPT_END = 5;

    NotifyServiceReceiver notifyServiceReceiver;

    private int MY_NOTIFICATION_ID;
    public static int NotifByte;
    private static int MY_VIDEO_NOTIFICATION_ID;

    public static String servername ;
    private NotificationManager notificationManager;

    private static Thread t;
    private static SharedPreferences spref_ip;

    public static DatabaseHandler db;

    @Override
    public void onCreate() {
        notifyServiceReceiver = new NotifyServiceReceiver();
        db = new DatabaseHandler(getApplicationContext());
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(notifyServiceReceiver, intentFilter);

        final Context context = getApplicationContext();
        String notifTitle = "Someone is at your door!";
        String notifText = "Generating video...";

        String notifVdoTitle = "Someone is at your door! Video generated.";
        String notifVdoText = "Tap to watch video.";
        final String lightTitle = "Lights have changed";

        final NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_notif).setContentTitle(notifTitle).setContentText(notifText).setAutoCancel(true);
        final NotificationCompat.Builder notifVdoBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_video).setContentTitle(notifVdoTitle).setContentText(notifVdoText).setAutoCancel(true);
        final NotificationCompat.Builder lightBuilder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_light).setContentTitle(lightTitle).setAutoCancel(true);

        notifBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notifVdoBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        lightBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Toast.makeText(this, "Notification service started", Toast.LENGTH_LONG).show();



        final Handler handler = new Handler();

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    int nPersons = 0, nFaces = 0;

                    try {
                        spref_ip = PreferenceManager.getDefaultSharedPreferences(context);
                        servername = spref_ip.getString("ip_address","");
                        Socket client = new Socket(servername, 6667);
                        System.out.println("CONNECTED "+"to " + servername);
                        InputStream in = client.getInputStream();
                        OutputStream out = client.getOutputStream();
                        int p = in.read();
                        out.write(1);
                        out.flush();
                        System.out.println("NOTIF  RECIEVED: "+ String.valueOf(p));

                        Intent firstNotifIntent = new Intent(context, NotifActivity.class);
                        Intent secondNotifIntent = new Intent(context, NotifActivity.class);

                        firstNotifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        secondNotifIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        if(p==BYTE_ALERT1)
                        {
                            notifBuilder.setContentTitle("Suspicious activity.");
                        }
                        if(p == BYTE_PEOPLE_VDOGENERATED)
                        {
                            Database._name = "Face Found.";


                        }
                        if(p == BYTE_ALERT2)
                        {
                            notifVdoBuilder.setContentTitle("Suspicious activity. Video generated");
                            Database._name = "Suspicious activity.Alert level 2";
                        }
                        if( p == BYTE_ABRUPT_END){
                            Database._name = "Abrupt end of activity.";
                        }

                        if (p == BYTE_PEOPLE_VDOGENERATING || p == BYTE_PEOPLE_VDOGENERATED){
                            nPersons = in.read();
                            nFaces = in.read();

                            out.write(1);
                            out.flush();


                            if (nPersons <= nFaces){
                                if (p == BYTE_PEOPLE_VDOGENERATING){
                                    notifBuilder.setContentTitle(nPersons + "Someone is on your doorstep.");
                                }else if (p == BYTE_PEOPLE_VDOGENERATED){
                                    notifVdoBuilder.setContentTitle(nPersons + " people on your doorstep.");
                                }
                            }else {
                                if (p == BYTE_PEOPLE_VDOGENERATING){
                                    notifBuilder.setContentTitle(nPersons + "Someone is on your doorstep.");
                                }else if (p == BYTE_PEOPLE_VDOGENERATED){
                                    notifVdoBuilder.setContentTitle(nPersons + " people with " + (nPersons - nFaces) + " faces covered on your doorstep.");
                                }
                            }
                        }

                        if(p==BYTE_PEOPLE_VDOGENERATING || p== BYTE_ALERT1) {
                            Socket socketFrame = new Socket(servername, 6669);
                            System.out.println("............FRAME SOCKET........");
                            InputStream inFrame = socketFrame.getInputStream();
                            final Bitmap notifFrame = BitmapFactory.decodeStream(new FlushedInputStream(inFrame));
                            socketFrame.close();
                            System.out.println("............FRAME SOCKET closed!!!!!!!!!!");
                            String imageName = getCurrentTimeStamp();
                            saveImage(context, notifFrame, imageName);

                            firstNotifIntent.putExtra("image_name", imageName);

                            int requestID = (int) System.currentTimeMillis();
                            PendingIntent firstPendingIntent = PendingIntent.getActivity(context, requestID, firstNotifIntent, 0);
                            notifBuilder.setContentIntent(firstPendingIntent);

                            NotificationCompat.BigPictureStyle bps = new NotificationCompat.BigPictureStyle().bigPicture(notifFrame);
                            notifBuilder.setStyle(bps);


                            if (NotifActivity.jIV != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotifActivity.jIV.setImageBitmap(notifFrame);
                                    }
                                });
                            }
                        }
                        if(p== BYTE_PEOPLE_VDOGENERATED || p ==BYTE_ALERT2 || p == BYTE_ABRUPT_END){
                            DataInputStream dataInputStream = new DataInputStream(in);
                            Database._date = dataInputStream.readUTF();
                            System.out.println(Database._date);
                            db.addContact(new Database(Database._name, Database._date));

                        }
                        DataInputStream din = new DataInputStream(in);

                        MY_NOTIFICATION_ID = din.readInt();
                        System.out.println("My notification Id received is:"+MY_NOTIFICATION_ID);
                        out.write(9);
                        out.flush();
                        din.close();
                        client.close();

                        if(p == BYTE_PEOPLE_VDOGENERATING || p == BYTE_ALERT1)
                        {
                            notificationManager.notify(MY_NOTIFICATION_ID, notifBuilder.build());

                        }

                        if (p == BYTE_PEOPLE_VDOGENERATED || p == BYTE_ALERT2 || p == BYTE_ABRUPT_END) {
                            System.out.println("My notification Id received is:" + MY_NOTIFICATION_ID);

                            secondNotifIntent.putExtra("video_notif_id", MY_NOTIFICATION_ID);

                            int requestID = (int) System.currentTimeMillis();
                            PendingIntent secondPendingIntent = PendingIntent.getActivity(context, requestID, secondNotifIntent, 0);
                            notifVdoBuilder.setContentIntent(secondPendingIntent);

                            notificationManager.notify(MY_NOTIFICATION_ID, notifVdoBuilder.build());
                            System.out.println("NOTIF 2nd GIVEN");

                        }
                        if(p == 6){
                            notificationManager.notify(MY_NOTIFICATION_ID,lightBuilder.build());
                            Database._name = lightTitle;
                            String datenow = Database.dateFormat.format(Database.date);
                            db.addContact(new Database(Database._name,datenow));
                        }


                    } catch (IOException e) {
                        e.printStackTrace();

                    }


                }
            }
        });
        t.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("DESTROYEDDD!", "HAHAHA!");
        Toast.makeText(NotifyService.this, "Notification service stopped", Toast.LENGTH_SHORT).show();
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int rqs = intent.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE){
                stopSelf();
            }
        }
    }

    public void saveImage(Context context, Bitmap b, String name){
        name=name+".jpg";
        FileOutputStream out;
        try {
            out = context.openFileOutput(name, Context.MODE_PRIVATE);
            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }
}
