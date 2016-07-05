package com.justinkleiber.focus2d.controllers;

import com.justinkleiber.focus2d.base.*;

public class FocusCollision implements Collision{

	@Override
	public boolean isCollisionExist(Sprite s, Sprite t) {
		// TODO Auto-generated method stub
			//Sprite S data and position
			int s_h = s.getHeight();
			int s_w = s.getWidth();
			int s_x = s.getPosition().x;
			int s_y = s.getPosition().y;
			
			//Sprite T data and position
			int t_h = t.getHeight();
			int t_w = t.getWidth();
			int t_x = t.getPosition().x;
			int t_y = t.getPosition().y;
			if (s_y+s_h<t_y)
			{
				return false;
            }
            if (s_y>t_y+t_h)
            {
            	return false;
            }
            if (s_x+s_w<t_x)
            {
            	return false;
            }
            if (s_x>t_w+t_x)
            {
            	return false;
            }
            return true;
	}

	@Override
	public boolean isCollisionExist(SpriteSheet ss, SpriteSheet tt) {
		// TODO Auto-generated method stub
		//SpriteSheet S data and position
		int s_h = ss.getSpriteHeight();
		int s_w = ss.getSpriteWidth();
		int s_x = ss.getPosition().x;
		int s_y = ss.getPosition().y;
		
		//SpriteSheet T data and position
		int t_h = tt.getSpriteHeight();
		int t_w = tt.getSpriteWidth();
		int t_x = tt.getPosition().x;
		int t_y = tt.getPosition().y;
		if (s_y+s_h<t_y)
		{
			return false;
        }
        if (s_y>t_y+t_h)
        {
        	return false;
        }
        if (s_x+s_w<t_x)
        {
        	return false;
        }
        if (s_x>t_w+t_x)
        {
        	return false;
        }
        return true;
	}

	@Override
	public boolean isCollisionExist(Sprite s, SpriteSheet ss) {
		// TODO Auto-generated method stub
		//Sprite S data and position
		int s_h = s.getHeight();
		int s_w = s.getWidth();
		int s_x = s.getPosition().x;
		int s_y = s.getPosition().y;
		
		//Sprite T data and position
		int t_h = ss.getSpriteHeight();
		int t_w = ss.getSpriteWidth();
		int t_x = ss.getPosition().x;
		int t_y = ss.getPosition().y;
		if (s_y+s_h<t_y)
		{
			return false;
        }
        if (s_y>t_y+t_h)
        {
        	return false;
        }
        if (s_x+s_w<t_x)
        {
        	return false;
        }
        if (s_x>t_w+t_x)
        {
        	return false;
        }
        return true;
	}


	public boolean collide(double cx, double cy, double cw, double ch, double ex, double ey, double ew, double eh){
		if (cy+ch<ey){
            return false;
            }
            if (cy>ey+eh){
            return false;
            }
            if (cx+cw<ex){
            return false;
            }
            if (cx>ew+ex){
            return false;
            }
            return true;
	}

	@Override
	public boolean isInPosition(Sprite s, Position p) {
		// TODO Auto-generated method stub
		int s_x = s.getPosition().x;
		int s_y = s.getPosition().y;
		
		int p_x = p.x;
		int p_y = p.y;
		
		if(s_x== p_x && s_y == p_y)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isInPosition(Sprite s, Position p, int tolerance) {
		// TODO Auto-generated method stub
		int s_x = s.getPosition().x;
		int s_y = s.getPosition().y;
		
		int p_x = p.x;
		int p_y = p.y;
		
		//if between the position tolerance, return true
		if((s_x >= p_x-tolerance && s_x <= p_x+tolerance)&&(s_y >= p_y-tolerance && s_y <= p_y+tolerance))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isInPosition(SpriteSheet ss, Position p) {
		// TODO Auto-generated method stub
		int s_x = ss.getPosition().x;
		int s_y = ss.getPosition().y;
		
		int p_x = p.x;
		int p_y = p.y;
		
		if(s_x== p_x && s_y == p_y)
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isInPosition(SpriteSheet ss, Position p, int tolerance) {
		// TODO Auto-generated method stub
		int s_x = ss.getPosition().x;
		int s_y = ss.getPosition().y;
		
		int p_x = p.x;
		int p_y = p.y;
		
		//if between the position tolerance, return true
		if((s_x >= p_x-tolerance && s_x <= p_x+tolerance)&&(s_y >= p_y-tolerance && s_y <= p_y+tolerance))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isCollisionExist(int sx, int sy, int sw, int sh, int tx,
			int ty, int tw, int th, boolean onCenter) {
		// TODO Auto-generated method stub
		if(!onCenter)
		{
			if (sy+sh<ty)
			{
				return false;
            }
            if (sy>ty+th)
            {
            	return false;
            }
            if (sx+sw<tx)
            {
            	return false;
            }
            if (sx>tw+tx)
            {
            	return false;
            }
            return true;
		}
		else
		{
			if (sy+(sh/2)<ty-(th/2))
			{
				return false;
            }
            if (sy-(sh/2)>ty+(th/2))
            {
            	return false;
            }
            if (sx+(sw/2)<tx-(tw/2))
            {
            	return false;
            }
            if (sx-(sw/2)>(tw/2)+tx)
            {
            	return false;
            }
            return true;
		}
	}

	@Override
	public boolean isCollisionExist(int sx, int sy, int sw, int sh, int tx, int ty, int tw, int th, float s_angle) {
//Find points of hitbox
		double s_a, c_a;
		s_a = Math.sin(-s_angle*Math.PI/180);
		c_a = Math.cos(-s_angle * Math.PI / 180);

		double ax=0, ay=0;
		double bx=0, by=0;
		double cx=0, cy=0;
		double dx=0, dy=0;

        double l_slope, w_slope;

		ax = sx + (((sx-(sw/2))-sx)*c_a) + ((sy-(sh/2))-sy)*s_a;
		ay = sy - (((sx-(sw/2))-sx)*s_a) + ((sy-(sh/2))-sy)*c_a;

		bx = sx + (((sx+(sw/2))-sx)*c_a) + ((sy-(sh/2))-sy)*s_a;
		by = sy - (((sx+(sw/2))-sx)*s_a) + ((sy-(sh/2))-sy)*c_a;

		cx = sx + (((sx+(sw/2))-sx)*c_a) + ((sy+(sh/2))-sy)*s_a;
		cy = sy - (((sx+(sw/2))-sx)*s_a) + ((sy+(sh/2))-sy)*c_a;

		dx = sx + (((sx-(sw/2))-sx)*c_a) + ((sy+(sh/2))-sy)*s_a;
		dy = sy - (((sx-(sw/2))-sx)*s_a) + ((sy+(sh/2))-sy)*c_a;

        w_slope = (ay-by)/(ax-bx);

        l_slope = (by-cy)/(bx-cx);


        double ab_, bc_, cd_, da_;

        if(s_angle>0) {
            //short sides (if object is "portrait")
            ab_ = w_slope * ((tx - tw / 2) - ax) + ay;
            if (ab_ > (ty + th / 2)) {
                return false;
            }

            cd_ = w_slope * ((tx + tw / 2) - cx) + cy;
            if (cd_ < (ty - th / 2)) {
                return false;
            }

            //long sides (if object is "portrait")
            bc_ = l_slope * ((tx - tw / 2) - bx) + by;
            if (bc_ < (ty - th / 2)) {
                return false;
            }
            da_ = l_slope * ((tx + tw / 2) - dx) + dy;
            if (da_ > (ty + th / 2)) {
                return false;
            }

        }
        else
        {
            //short sides (if object is "portrait")
            ab_ = w_slope * ((tx + tw / 2) - ax) + ay;
            if (ab_ > (ty + th / 2)) {
                return false;
            }

            cd_ = w_slope * ((tx - tw / 2) - cx) + cy;
            if (cd_ < (ty - th / 2)) {
                return false;
            }

            if (s_angle != 0) {
                //long sides (if object is "portrait")
                bc_ = l_slope * ((tx - tw / 2) - bx) + by;
                if (bc_ > (ty + th / 2)) {
                    return false;
                }

                da_ = l_slope * ((tx + tw / 2) - dx) + dy;
                if (da_ < (ty - th / 2)) {
                    return false;
                }
            }
            else
            {
                if (sy+(sh/2)<ty-(th/2))
                {
                    return false;
                }
                if (sy-(sh/2)>ty+(th/2))
                {
                    return false;
                }
                if (sx+(sw/2)<tx-(tw/2))
                {
                    return false;
                }
                if (sx-(sw/2)>(tw/2)+tx)
                {
                    return false;
                }
                return true;
            }
        }

        return true;
	}


}
