package com.mkirsch42.electricity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

public strictfp class Particle {

    public ArrayList<Float> path;
    public ArrayList<float[]> archive;
    public double x, y, vx, vy, m, q, ox, oy;
    public long t = 1;
    public Color c;
    public Color p;
    private static final int LIMIT_LEN = 15;

    public float[][] path(boolean limit) {
	if (limit) {
	    float[][] store = new float[(archive.size() < LIMIT_LEN ? archive.size() : LIMIT_LEN) + 1][];

	    float[] end = new float[path.size()];
	    int i = 0;
	    for (Float f : path) {
		end[i++] = f;
	    }
	    store[store.length - 1] = end;

	    if (store.length > 1) {
		i = store.length - 2;
		int j = archive.size() - 1;
		for (; i >= (archive.size() < LIMIT_LEN ? 0 : 1); i--) {
		    store[i] = archive.get(j--);
		}
		if (archive.size() >= LIMIT_LEN) {
		    float[] start = archive.get(j);
		    store[0] = new float[5000 - path.size()];
		    int l = store[0].length - 1;
		    for (int k = 0; k <= l; k++) {
			store[0][l - k] = start[4999 - k];
		    }
		}
	    }

	    return store;
	} else {
	    if (path.size() == 0) {
		float[][] fullPath = new float[archive.size()][];
		archive.toArray(fullPath);
		return fullPath;
	    }
	    float[][] fullPath = new float[archive.size() + 1][];
	    archive.toArray(fullPath);
	    float[] endPath = new float[path.size()];
	    int i = 0;
	    for (Float f : path) {
		endPath[i++] = f;
	    }
	    fullPath[fullPath.length - 1] = endPath;
	    return fullPath;
	}
    }

    public void pushPath() {
	path.add((float) (x));
	path.add((float) (y));
	if (path.size() >= 5000) {
	    float[] store = new float[path.size()];
	    int i = 0;
	    for (Float f : path) {
		store[i++] = f;
	    }
	    archive.add(store);
	    path = new ArrayList<>(5000);
	}
    }

    public Particle(double x, double y, double q, double m) {
	this.x = x;
	this.y = y;
	this.q = q;
	this.m = m;
	path = new ArrayList<>(5000);
	archive = new ArrayList<>();
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

    public Color pathColor() {
	if (p != null)
	    return p;
	return Color.CYAN;
    }

}
