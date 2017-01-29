package com.mkirsch42.electricity;

import java.util.ArrayList;

public final class ParticlePresets {

    public static final PresetFunction[] PRESETS = {
	    ParticlePresets::test,
	    ParticlePresets::figure8,
	    ParticlePresets::binaryOrbit
    };
    
    public static void figure8(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -3000, 3000));
	particles.get(2).vx = 6;
    }
    
    public static void binaryOrbit(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 3000, 1000));
	particles.add(new Particle(width / 2 - 300, height / 2, -3000, 1000));
	particles.get(0).vy = +2;
	particles.get(1).vy = -2;
    }
    
    public static void test(ArrayList<Particle> particles, float width, float height) {
    }
    
    public static interface PresetFunction {
	void apply(ArrayList<Particle> particles, float width, float height);
    }
    
}
