package treadstone.game.GameEngine;

import android.graphics.Point;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import android.graphics.Rect;
import android.graphics.Color;
import android.graphics.Paint;
import android.content.Context;
import android.graphics.Canvas;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class GameView extends SurfaceView implements Runnable
{
    volatile boolean                view_active;
    Thread                          game_thread = null;
    Player                          curr_player;
    TestEnemy                       test_enemy1, test_enemy2, test_enemy3;
    private int                     max_x, max_y;

    private Paint                   paint;
    private Canvas                  canvas;
    private SurfaceHolder           curr_holder;

    ArrayList<TestEnemy>            enemy_list = new ArrayList<>();
    ArrayList<Projectile>           temp_buffer = new ArrayList<>();
    ArrayList<BackgroundEffect>     background_visuals = new ArrayList<>();

    BackgroundEffect                b1, b2, b3, b4;
    CollisionChecker                collision_check;

    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public GameView(Context curr_context, Point max)
    {
        super(curr_context);
        max_x = max.x;
        max_y = max.y;
        init();
    }

    public void init()
    {
        // Initialize Surface Requirements
        paint = new Paint();
        curr_holder = getHolder();

        // Player added to Game
        curr_player = new Player(getContext(), 50, 50);
        curr_player.setMaxBounds(max_x/3, max_y);

        // Test Enemy added to Map
        test_enemy1 = new TestEnemy(getContext(), "bob_evil", 2000, 50);
        test_enemy1.setMaxBounds(max_x - test_enemy1.getImageHeight(), max_y);
        test_enemy2 = new TestEnemy(getContext(), "bob_evil", 1000, 50);
        test_enemy2.setMaxBounds(max_x - test_enemy2.getImageHeight(), max_y);
        test_enemy3 = new TestEnemy(getContext(), "bob_evil", 1000,  max_y + 10);
        test_enemy3.setMaxBounds(max_x - test_enemy2.getImageHeight(), max_y);

        enemy_list.add(test_enemy1);
        enemy_list.add(test_enemy2);
        enemy_list.add(test_enemy3);


        // Background Effects
        b1 = new BackgroundEffect(getContext(), "star_yellow", 0, 0, 4);
        b2 = new BackgroundEffect(getContext(), "star_small", 0, 0, 10);
        b3 = new BackgroundEffect(getContext(), "star_small", 0, 0, 8);
        b4 = new BackgroundEffect(getContext(), "star_small", 0, 0, 6);

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
        updateProjectiles();
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
            drawProjectiles();
            drawBackgroundEffects();
            drawHitBoxes();
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
            Log.d("CONTROL_CRASH", "Interrupt caught within control() [View]");
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
        if (curr_motion.getAction() == MotionEvent.ACTION_UP)
        {
            curr_player.toggleMoving(false);
        }

        else if (curr_motion.getAction() == MotionEvent.ACTION_DOWN)
        {
            curr_player.toggleMoving(true);
            return true;
        }

        else if (curr_motion.getAction() == MotionEvent.ACTION_MOVE)
        {
            curr_player.processMovement(curr_motion.getRawX(), curr_motion.getRawY());
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
        TestEnemy curr_enemy;

        for (ListIterator<TestEnemy> iterator = enemy_list.listIterator(); iterator.hasNext();)
        {
            curr_enemy = iterator.next();
            drawTarget(curr_enemy);
        }
    }

    public void updateBackgroundEffects()
    {
        BackgroundEffect curr_item;

        for (ListIterator<BackgroundEffect> iterator = background_visuals.listIterator(); iterator.hasNext();)
        {
            curr_item = iterator.next();
            curr_item.update();
        }

    }

    public void drawProjectiles()
    {
        for (Iterator<Projectile> iterator = curr_player.getProjectiles().iterator(); iterator.hasNext();)
        {
            Projectile p = iterator.next();
            drawProjectile(p);
        }

    }

    public void updateProjectiles()
    {
        // Add temp_buffer to Projectile List
        curr_player.getProjectiles().addAll(temp_buffer);

        for (Iterator<Projectile> iterator = curr_player.getProjectiles().iterator(); iterator.hasNext();)
        {
            Projectile p = iterator.next();
            p.update();
        }

        temp_buffer.removeAll(temp_buffer);

    }

    public void drawProjectile(Projectile curr_target)
    {
        canvas.drawBitmap(curr_target.getImage(), curr_target.getX(), curr_target.getY(), paint);
    }

    public void updateEnemies()
    {
        for (Iterator<TestEnemy> iterator = enemy_list.iterator(); iterator.hasNext();)
        {
            TestEnemy e = iterator.next();
            e.update();
        }

    }

    public void checkCollisions() /** TO DO : Handle Collision **/
    {
        collision_check = new CollisionChecker(curr_player, enemy_list);

        if (collision_check.shipCollisions())
        {
            Log.d("collision_log", "Collision [Ship] just occurred");
            // handle collision here
        }

        if (collision_check.projectileCollisions())
        {
            Log.d("collision_log", "Collision [Projectiles] just occurred!");
        }

        if (collision_check.projectileBoundary())
        {
            Log.d("collision_log", "Projectile out of bounds and being deleted!");
        }

    }

    public void drawHitBoxes()
    {
        Rect curr_box;
        curr_box = curr_player.getHitRect().getHitbox();
        canvas.drawRect(curr_box.left, curr_box.top, curr_box.right, curr_box.bottom, paint);

        Projectile p;

        for (ListIterator<Projectile> iterator = curr_player.getProjectiles().listIterator(); iterator.hasNext();)
        {
            p = iterator.next();
            curr_box = p.getHitRect().getHitbox();
            canvas.drawRect(curr_box.left, curr_box.top, curr_box.right, curr_box.bottom, paint);
        }

        TestEnemy curr_enemy;

        for (Iterator<TestEnemy> iterator = enemy_list.iterator(); iterator.hasNext();)
        {
            curr_enemy = iterator.next();
            curr_box = curr_enemy.getHitRect().getHitbox();
            canvas.drawRect(curr_box.left, curr_box.top, curr_box.right, curr_box.bottom, paint);
        }

    }

    public void addProjectileToPlayer(ControllerFragment.ProjectileType p)
    {
        float speed;

        switch (p)
        {
            case BULLET:
                speed = 12.0f;
                break;

            case MISSILE:
                speed = 8.0f;
                break;

            default:
                speed = 0.0f;
                break;
        }

        Projectile new_p = new Projectile(getContext(), p, curr_player.getX(), curr_player.getY(), speed);
        new_p.setMaxBounds(max_x - new_p.getImageHeight(), max_y);

        // Buffer for adding to Projectiles
        ListIterator<Projectile> iterator = temp_buffer.listIterator();
        iterator.add(new_p);
    }

}