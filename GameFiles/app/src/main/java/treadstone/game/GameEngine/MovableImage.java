package treadstone.game.GameEngine;

import android.graphics.Rect;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class MovableImage extends MovableEntity
{

    private Bitmap              image;
    private int                 image_id;
    private RectangleHitbox     hitbox;

    MovableImage(Context context, String name, int x, int y, int run_speed, String image_name)
    {
        super(name, x, y, run_speed);
        image_id = context.getResources().getIdentifier(image_name, "drawable", context.getPackageName());
        image = BitmapFactory.decodeResource(context.getResources(), image_id);
        hitbox = new RectangleHitbox(x, y, image);
    }

    public Bitmap getImage()
    {
        return image;
    }

    public int getImageHeight()
    {
        return image.getHeight();
    }

    public RectangleHitbox getHitRect()
    {
        return hitbox;
    }

}