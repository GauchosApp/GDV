package m2wapps.ar.goldevestuario;

/**
 * Created by mariano on 17/5/2016.
 */
public class ThemeHelper {
    /**
     * Helper function to get the correct play button drawable for our
     * notification: The notification does not depend on the theme but
     * depends on the API level
     */
    public static int getPlayButtonResource(boolean playing)
    {
        int playButton;
        playButton = playing ? R.mipmap.play : R.mipmap.pause;
        return playButton;
    }
}

