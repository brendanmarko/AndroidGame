package treadstone.game.GameEngine;

import android.util.Log;
import android.graphics.Rect;
import android.graphics.Bitmap;

public class RectangleHitbox
{
    // Debug info
    private int         DEBUG = 0;
    private String      DEBUG_TAG = "RectangleHitbox";

    private Rect        hitbox;
    private int         l, b, r, t;

    public RectangleHitbox()
    {
        //
    }

    public RectangleHitbox(Position pos, Position ppm, Position dimens)
    {
        l = (int) pos.getX();
        t = (int) pos.getY();
        r = l + (int) (dimens.getX() * ppm.getX());
        b = t + (int) (dimens.getY() * ppm.getY());

        if (DEBUG == 1)
            Log.d(DEBUG_TAG, "Creating rect with: " + l + ", " + t + ", " + r + ", " + b);

        hitbox = new Rect(l, t, r, b);
    }

    public Rect getHitbox()
    {
        return hitbox;
    }

    public void update(int x, int y, Bitmap curr_bitmap)
    {
        hitbox.set(x, y, x + curr_bitmap.getWidth(), y + curr_bitmap.getHeight());
    }

    public String toString()
    {
        if (DEBUG == 1)
            Log.d(DEBUG_TAG, "Left: " + l + ", Right: " + r + ", Top: " + t + ", Bottom: " + b);
        return "Left: " + l + ", Right: " + r + ", Top: " + t + ", Bottom: " + b;
    }
}