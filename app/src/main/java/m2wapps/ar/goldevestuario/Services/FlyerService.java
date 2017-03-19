package m2wapps.ar.goldevestuario.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.ImageView;
import android.widget.RemoteViews;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import m2wapps.ar.goldevestuario.Activities.MainActivity;
import m2wapps.ar.goldevestuario.R;


/**
 * Created by mariano on 08/02/2017.
 */

public class FlyerService extends Service {
    private String tweetOld = " ";
    private SharedPreferences sharedPref;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Context context = this;
        sharedPref = context.getSharedPreferences(
                "Base de datos", Context.MODE_PRIVATE);

        hilo();
        return super.onStartCommand(intent, flags, startId);
    }
    private void task(){
        final Context ctx = this;
        class GetMP3 extends AsyncTask<Void,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if (s != null) {
                    URL url = null;
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        String aux = s.replace("https", "http");
                        url = new URL(aux);
                        Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                        createNotification(bmp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            protected String doInBackground(Void... params) {
                Document doc;
                try {
                    doc = Jsoup.connect("https://twitter.com/goldevestuario").get();
                    Elements tweets = doc.select("div#doc.route-profile div#page-outer div#page-container.AppContent div.AppContainer div.AppContent-main.content-main.u-cf div.Grid.Grid--withGutter div.Grid-cell.u-size2of3.u-lg-size3of4 div.Grid.Grid--withGutter div.Grid-cell.u-lg-size2of3 div#timeline.ProfileTimeline div.stream-container div.stream ol#stream-items-id.stream-items.js-navigable-stream");
                    //    Element imagen = doc.select("div#doc.route-profile div#page-outer div#page-container.AppContent div.AppContainer div.AppContent-main.content-main.u-cf div.Grid.Grid--withGutter div.Grid-cell.u-size2of3.u-lg-size3of4 div.Grid.Grid--withGutter div.Grid-cell.u-lg-size2of3 div#timeline.ProfileTimeline div.stream-container div.stream ol#stream-items-id.stream-items.js-navigable-stream li#stream-item-tweet-829472767208804353.js-stream-item.stream-item.stream-item div.tweet.js-stream-tweet.js-actionable-tweet.js-profile-popup-actionable.original-tweet.js-original-tweet.has-cards.has-content div.content div.AdaptiveMediaOuterContainer div.AdaptiveMedia.is-square div.AdaptiveMedia-container.js-adaptive-media-container div.AdaptiveMedia-singlePhoto div.AdaptiveMedia-photoContainer.js-adaptive-photo img").first();
                    String tweet;
                    int contador = 1;
                    for (int i = 0; i < 18; i++) {
                            tweet = tweets.get(0).getElementsByClass("js-tweet-text-container").get(i).text();
                            tweetOld = sharedPref.getString("tweetOld", " ");
                            if(tweet.contains("[AIRE]") && tweet.equals(tweetOld)){
                                break;
                            }else {
                                if (tweet.contains("[AIRE]") && !tweet.equals(tweetOld)) {
                                    Element imagen = tweets.get(0).select("img").get(i + contador);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putString("tweetOld", tweet);
                                    editor.apply();
                                    return imagen.absUrl("src");
                                } else if (tweet.contains("pic.twitter.com")) {
                                    contador++;
                                }
                            }
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    return null;
                }

                return null;
            }
        }
        GetMP3 gmp3 = new GetMP3();
        gmp3.execute();
    }
    private void createNotification(Bitmap bmp){
        RemoteViews views = new RemoteViews(this.getPackageName(),R.layout.notification_flyer);
        RemoteViews viewsExpanded = new RemoteViews(this.getPackageName(),R.layout.notification_flyer_expanded);
        viewsExpanded.setImageViewBitmap(R.id.cover, bmp);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.icon)
                            .setContent(views)
                            .setCustomBigContentView(viewsExpanded)
                            .setOngoing(false)
                            .setVibrate(new long[] { 200,200,200,200})
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);




            // Creates an explicit intent for an Activity in your app
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("radio", true);
            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(MainActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // mId allows you to update the notification later on.
            mNotificationManager.notify(2, mBuilder.build());
    }
    private void hilo(){
        Thread checkEnVivo = new Thread(new Runnable() {
            @Override
            public void run() {
                    while(true){
                        try {
                            task();
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
            }
        });checkEnVivo.start();
    }

}
