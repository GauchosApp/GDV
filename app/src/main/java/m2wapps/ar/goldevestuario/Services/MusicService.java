package m2wapps.ar.goldevestuario.Services;



import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Intent;

import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import m2wapps.ar.goldevestuario.Activities.MainActivity;
import m2wapps.ar.goldevestuario.Fragments.RadioFragment;
import m2wapps.ar.goldevestuario.R;
import m2wapps.ar.goldevestuario.ThemeHelper;


/**
 * Created by mariano on 07/02/2017.
 */

public class MusicService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }
  /*  public void play(String packageName, Context ctx){
        startForeground(0,createNotification(1, packageName, ctx));
    }*/

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            Boolean ispng = extras.getBoolean("ispng");
            startForeground(1, createNotification(ispng));
        }else{
            startForeground(1, createNotification(false));
        }

        return(START_STICKY);
    }
    protected Notification createNotification(boolean start) {
        boolean aux = start;
        String packageName = this.getPackageName();
        RemoteViews views = new RemoteViews(packageName, R.layout.notification);

        int playButton = ThemeHelper.getPlayButtonResource(aux);
        views.setImageViewResource(R.id.play_pause, playButton);

        Intent playPause = new Intent("playpause");
        PendingIntent pendingSwitchIntent2 = PendingIntent.getBroadcast(this, 100, playPause, 0);
        views.setOnClickPendingIntent(R.id.play_pause, pendingSwitchIntent2);

        Intent close = new Intent("close");
        PendingIntent pendingSwitchIntent4 = PendingIntent.getBroadcast(this, 100, close, 0);
        views.setOnClickPendingIntent(R.id.close, pendingSwitchIntent4);


        Intent notificationIntent = new Intent(this, RadioFragment.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent i = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        views.setOnClickPendingIntent(R.id.line, i);

        //   views.setTextViewText(R.id.title, song.getTitle());
        //  views.setTextViewText(R.id.artist, song.getArtist());

        Notification notification = new Notification();
        notification.contentView = views;
        notification.icon = R.mipmap.radio;
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.visibility = Notification.VISIBILITY_PUBLIC;
        }
        return notification;

    }

}
