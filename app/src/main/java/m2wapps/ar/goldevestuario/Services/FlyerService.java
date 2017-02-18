package m2wapps.ar.goldevestuario.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;

import m2wapps.ar.goldevestuario.Activities.MainActivity;
import m2wapps.ar.goldevestuario.R;


/**
 * Created by mariano on 08/02/2017.
 */

public class FlyerService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        task();
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
                final RemoteViews expanded = new RemoteViews(getPackageName(), R.layout.notification_expanded);
                System.out.println(s);
                Picasso.with(ctx)
                        .load(s)
                        .resize(1000,500)
                        .into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {


                                expanded.setImageViewBitmap(R.id.cover,Bitmap.createScaledBitmap(bitmap,682,383,false));
                                //startForeground(2,createNotification(1, expanded));
                                Notification obj = createNotification(1,expanded);
                                synchronized(obj){
                                    obj.notify();
                                }
                               // createNotification(1,expanded).notify();
                            }

                            @Override
                            public void onBitmapFailed(Drawable errorDrawable) {
                                System.out.println("fallo");
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
            }
            @Override
            protected String doInBackground(Void... params) {
                Document doc;
                try {
                    doc = Jsoup.connect("https://twitter.com/goldevestuario").get();
                    Elements tweets = doc.select("div#doc.route-profile div#page-outer div#page-container.AppContent div.AppContainer div.AppContent-main.content-main.u-cf div.Grid.Grid--withGutter div.Grid-cell.u-size2of3.u-lg-size3of4 div.Grid.Grid--withGutter div.Grid-cell.u-lg-size2of3 div#timeline.ProfileTimeline div.stream-container div.stream ol#stream-items-id.stream-items.js-navigable-stream");
                    //    Element imagen = doc.select("div#doc.route-profile div#page-outer div#page-container.AppContent div.AppContainer div.AppContent-main.content-main.u-cf div.Grid.Grid--withGutter div.Grid-cell.u-size2of3.u-lg-size3of4 div.Grid.Grid--withGutter div.Grid-cell.u-lg-size2of3 div#timeline.ProfileTimeline div.stream-container div.stream ol#stream-items-id.stream-items.js-navigable-stream li#stream-item-tweet-829472767208804353.js-stream-item.stream-item.stream-item div.tweet.js-stream-tweet.js-actionable-tweet.js-profile-popup-actionable.original-tweet.js-original-tweet.has-cards.has-content div.content div.AdaptiveMediaOuterContainer div.AdaptiveMedia.is-square div.AdaptiveMedia-container.js-adaptive-media-container div.AdaptiveMedia-singlePhoto div.AdaptiveMedia-photoContainer.js-adaptive-photo img").first();
                    String tweet = tweets.get(0).getElementsByClass("js-tweet-text-container").get(0).text();
                  //  tweet.contains("[AIRE]")
                    if(tweet.contains("[AIRE]")){

                    Element imagen = tweets.get(0).select("img").get(1);

              //      Bitmap img = BitmapFactory.decodeFile(url);
                 //   System.out.println(url);
                    //      System.out.println(tweet);
                    //     System.out.println("imagen: "+url);
                    return  imagen.absUrl("src");
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
    protected Notification createNotification(int start, RemoteViews expanded) {
          //  boolean aux;            aux = start != 0;


        RemoteViews views = new RemoteViews(getPackageName(), R.layout.notification2);


   //     System.out.println(flyer);

        //    expanded.setImageViewResource(R.id.cover,R.drawable.gol);


       //     expanded.setImageViewResource(R.id.play_pause, playButton);




            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


            Notification notification = new Notification();
            notification.contentView = views;
            notification.icon = R.drawable.icon;
         //   notification.flags |= Notification.FLAG_AUTO_CANCEL;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                // expanded view is available since 4.1
                notification.bigContentView = expanded;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notification.visibility = Notification.VISIBILITY_PUBLIC;
            }
            return notification;

    }
}
