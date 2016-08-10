package mushirih.pickup.http;

import android.graphics.Bitmap;

/**
 * Created by p-tah on 09/08/2016.
 */
public class Load {
    //TODO CLASS RECIEVING ALL LOAD/REQUEST PROPERIES
    private static Bitmap IMAGE;
    private static int DAY;
    private static int MONTH;
    private static int YEAR;
    private static int HOUR;
    private static int MINUTE;



    public static void setImage(Bitmap image) {
        IMAGE = image;
    }

    public static void setDate(int dayOfMonth, int monthOfYear, int year) {
        DAY=dayOfMonth;
        MONTH=monthOfYear;
        YEAR=year;
        
    }

    public static void setTime(int hourOfDay, int minute) {
        HOUR=hourOfDay;
        MINUTE=minute;
    }
}
