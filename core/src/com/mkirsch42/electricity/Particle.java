package com.mkirsch42.electricity;

import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;

public strictfp class Particle {

    public LinkedList<Float> path;
    public double x, y, vx, vy, m, q, ox, oy;
    public long t = 1;
    public Color c;
    
    public float[] path() {
	float[] floatArray = new float[path.size()];
	int i = 0;

	for (Float f : path) {
	    floatArray[i++] = (f != null ? f : Float.NaN); // Or whatever default you want.
	}
	return floatArray;
    }
    
    public void pushPath() {
	path.push((float)(y));
	path.push((float)(x));
	if(path.size() >= 5000) {
	    path.removeLast();
	    path.removeLast();
	}
    }
    
    public Particle(double x, double y, double q, double m) {
	this.x = x;
	this.y = y;
	this.q = q;
	this.m = m;
	path = new LinkedList<>();
	vx = 0;
	vy = 0;
    }
    
    public double q() {
	return q;
    }
    
    public Particle(Particle p) {
	this(p.x, p.y, p.q, p.m);
	ox = p.ox;
	oy = p.oy;
	c = p.c;
    }

    public Color color() {
	if (c != null)
	    return c;
	if (q > 0)
	    return Color.RED;
	if (q < 0)
	    return Color.GREEN;
	return Color.GRAY;
    }

}
