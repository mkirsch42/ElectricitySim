package com.mkirsch42.electricity;

import java.util.LinkedList;
import java.util.function.Function;

import com.badlogic.gdx.graphics.Color;

public class VariableParticle extends Particle {

    public LinkedList<Float> path;
    public Function<Long, Double> func;
    
    public VariableParticle(double x, double y, double m, Function<Long, Double> f) {
	super(x, y, 0, m);
	func = f;
    }
    
    public double q() {
	return func.apply(t);
    }

    public VariableParticle(VariableParticle p) {
	super(p);
	func = p.func;
    }

    public Color color() {
	if (c != null)
	    return c;
	return Color.VIOLET;
    }

}
