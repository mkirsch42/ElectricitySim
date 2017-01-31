package com.mkirsch42.electricity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

public final class ParticlePresets {

    public static final PresetFunction[] PRESETS = { ParticlePresets::test, ParticlePresets::figure8,
	    ParticlePresets::binaryOrbit, ParticlePresets::h2, ParticlePresets::h2Bad };

    public static void figure8(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -3000, 3000));
	particles.get(2).vx = 6.08855;
    }

    public static void h2(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 3000000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 3000000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -3, 3000));
	particles.get(2).vx = 6.08855;
	particles.add(new Particle(width / 2 - 300, height / 2 - 100, -3, 3000));
	particles.get(3).vx = 6.08855;
	particles.get(3).c = Color.YELLOW;
	particles.get(3).p = Color.YELLOW;
    }

    public static void h2Bad(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 300000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 300000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -30, 3000));
	particles.get(2).vx = 6.08855;
	particles.add(new Particle(width / 2 - 300, height / 2 - 100, -30, 3000));
	particles.get(3).vx = 6.08855;
	particles.get(3).c = Color.TEAL;
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
