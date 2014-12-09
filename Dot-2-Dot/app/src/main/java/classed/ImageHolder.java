package classed;

import android.graphics.Bitmap;

/**
 * Created by ashwin on 22/11/14.
 * This class holds the properties of the bitmap of the images that are drawn on the canvas
 * The Bitmap image, the coordinates of the image, the name of the image etc
 */
public class ImageHolder {
    String name;
    float x,y;
    Bitmap bitmap;

    public ImageHolder(String name,float x,float y,Bitmap bitmap)
    {
        this.name=name;
        this.x=x;
        this.y=y;
        this.bitmap=bitmap;
    }

    public String getName() {
        return name;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
