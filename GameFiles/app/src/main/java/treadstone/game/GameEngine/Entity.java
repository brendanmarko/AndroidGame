package treadstone.game.GameEngine;

import java.util.Random;

public abstract class Entity
{

    private String          name;
    private Position        curr_pos;
    private float           max_x, max_y;
    private Position        start_pos;

    Entity(String new_name, float x, float y)
    {
        name = new_name;
        start_pos = new Position(x, y);
        curr_pos = new Position(x, y);
    }

    public String getName()
    {
        return name;
    }

    public float getX()
    {
        return curr_pos.getX();
    }

    public float getY()
    {
        return curr_pos.getY();
    }

    public Position getPosition()
    {
        return curr_pos;
    }

    public void setPosition(float x, float y)
    {
        curr_pos.setPosition(x, y);
    }

    public void setMaxBounds(float x, float y)
    {
        max_x = x;
        max_y = y;
    }

    public float getXMax()
    {
        return max_x;
    }

    public float getYMax()
    {
        return max_y;
    }

    public void respawn()
    {
        curr_pos.setPosition(start_pos.getX(), start_pos.getY());
    }

    public void random_spawn()
    {
        Random generator = new Random();
        int random_factor = generator.nextInt(10);
        curr_pos.setPosition(max_x, (max_y * random_factor)/10);
    }

}