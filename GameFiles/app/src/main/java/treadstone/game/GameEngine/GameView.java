package treadstone.game.GameEngine;

import java.util.ArrayList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class GameView extends SurfaceView implements Runnable
{

    volatile boolean                view_active;
    Thread                          game_thread = null;
    Player                          curr_player;
    TestEnemy                       test_enemy1, test_enemy2;
    private int                     max_x, max_y;

    private Paint                   paint;
    private Canvas                  canvas;
    private SurfaceHolder           curr_holder;

    ArrayList<TestEnemy>            enemy_list = new ArrayList<>();
    ArrayList<BackgroundEffect>     background_visuals = new ArrayList<>();

    BackgroundEffect b1, b2, b3, b4;

    public GameView(Context curr_context, Point max)
    {

        super(curr_context);
        curr_holder = getHolder();
        paint = new Paint();
        max_x = max.x;
        max_y = max.y;

        // Player added to Game
        curr_player = new Player(curr_context, "Mini-Meep", 50, 50);
        curr_player.setMaxBounds(max.x - curr_player.getImageHeight(), max.y);

        // Test Enemy added to Map
        test_enemy1 = new TestEnemy(curr_context, "bob_evil", 2000, 50);
        test_enemy1.setMaxBounds(max.x - test_enemy1.getImageHeight(), max.y);
        test_enemy2 = new TestEnemy(curr_context, "bob_evil", 1000, 50);
        test_enemy2.setMaxBounds(max.x - test_enemy2.getImageHeight(), max.y);

        enemy_list.add(test_enemy1);
        enemy_list.add(test_enemy2);

        // Background Effects
        b1 = new BackgroundEffect(curr_context, "star_yellow", 0, 0, 4);
        b2 = new BackgroundEffect(curr_context, "star_small", 0, 0, 10);
        b3 = new BackgroundEffect(curr_context, "star_small", 0, 0, 8);
        b4 = new BackgroundEffect(curr_context, "star_small", 0, 0, 6);

        background_visuals.add(b1);
        background_visuals.add(b2);
        background_visuals.add(b3);
        background_visuals.add(b4);

        setBackgroundLimits(background_visuals);

    }

    @Override
    public void run()
    {

        while (view_active)
        {
            update();
            draw();
            control();
        }

    }

    public void update()
    {
        curr_player.update();
        updateEnemies();
        updateBackgroundEffects();
    }

    public void draw()
    {

        if (curr_holder.getSurface().isValid())
        {

            // Lock Canvas
            canvas = curr_holder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            // Paint Colour
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Entities
            drawTarget(curr_player);
            drawEnemies();
            drawBackgroundEffects();
            drawHitboxes();
            checkCollisions();

            // Unlock and draw
            curr_holder.unlockCanvasAndPost(canvas);

        }

    }

    public void control()
    {

        try
        {
            game_thread.sleep(20);
        }

        catch (InterruptedException e)
        {
            System.out.println("Interrupt caught within control() [View]");
        }

    }

    public void pause()
    {

        view_active = false;

        try
        {
            game_thread.join();
        }

        catch (InterruptedException e)
        {
            System.out.println("Exception within pause() [View]");
        }

    }

    public void resume()
    {
        view_active = true;
        game_thread = new Thread(this);
        game_thread.start();
    }

    public boolean onTouchEvent(MotionEvent curr_motion)
    {

        switch (curr_motion.getAction() & MotionEvent.ACTION_MASK)
        {

            case MotionEvent.ACTION_UP:
                System.out.println("Finger lifted");
                curr_player.toggleMoving(false);
                break;

            case MotionEvent.ACTION_DOWN:
                System.out.println("Movement tap detected");
                curr_player.toggleMoving(true);
                // curr_player.processMovement(curr_motion.getRawX(), curr_motion.getRawY());
                break;

            case MotionEvent.ACTION_MOVE:
                System.out.println("Movement move");
                curr_player.processMovement(curr_motion.getRawX(), curr_motion.getRawY());
                break;
        }

        return true;

    }

    public void drawTarget(MovableImage curr_target)
    {
        canvas.drawBitmap(curr_target.getImage(), curr_target.getX(), curr_target.getY(), paint);
    }

    public void setBackgroundLimits(ArrayList<BackgroundEffect> curr_list)
    {

        for (BackgroundEffect curr_item : curr_list)
        {
            curr_item.setMaxBounds(max_x - curr_item.getImageHeight(), max_y);
        }

    }

    public void drawBackgroundEffects()
    {

        for (MovableImage curr_item : background_visuals)
        {
            drawTarget(curr_item);
        }
    }

    public void drawEnemies()
    {

        for (TestEnemy curr_enemy : enemy_list)
        {
            drawTarget(curr_enemy);
        }
    }

    public void updateBackgroundEffects()
    {

        for (BackgroundEffect curr_item : background_visuals)
        {
            curr_item.update();
        }

    }

    public void updateEnemies()
    {

        for (TestEnemy curr_enemy : enemy_list)
        {
            curr_enemy.update();
        }

    }

    public void checkCollisions()
    {

        for (TestEnemy curr_enemy : enemy_list)
        {

            if (Rect.intersects(curr_player.getHitRect().getHitbox(), curr_enemy.getHitRect().getHitbox()))
            {
                System.out.println("Collision found, removing enemy and affecting player!");
                enemy_list.remove(curr_enemy);
            }

        }

    }

    public void drawHitboxes()
    {

        Rect curr_box;
        curr_box = curr_player.getHitRect().getHitbox();
        canvas.drawRect(curr_box.left, curr_box.top, curr_box.right, curr_box.bottom, paint);

        for (TestEnemy curr_enemy : enemy_list)
        {
            curr_box = curr_enemy.getHitRect().getHitbox();
            canvas.drawRect(curr_box.left, curr_box.top, curr_box.right, curr_box.bottom, paint);
        }

    }

}
