package com.mkirsch42.electricity;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

public final class ParticlePresets {

    public static final PresetFunction[] PRESETS = { ParticlePresets::test, ParticlePresets::figure8,
	    ParticlePresets::binaryOrbit, ParticlePresets::h2, ParticlePresets::h2Bad, ParticlePresets::inkjetPrinter,
	    ParticlePresets::dipole, ParticlePresets::shell };

    public static void figure8(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 3000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -3000, 3000));
	particles.get(2).vx = 6.08855;
    }

    public static void h2(ArrayList<Particle> particles, float width, float height) {
	particles.add(new Particle(width / 2, height / 2, 9000000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2 - 300, height / 2, 9000000, Double.POSITIVE_INFINITY));
	particles.add(new Particle(width / 2, height / 2 + 100, -1, 3000));
	particles.get(2).vx = 6.08855;
	particles.add(new Particle(width / 2 - 300, height / 2 - 100, -1, 3000));
//	particles.get(3).vx = 6.08855;
	particles.get(3).vx = 6.09515;
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

    public static void inkjetPrinter(ArrayList<Particle> particles, float width, float height) {
	for (int i = 0; i < 10; i++) {
	    particles.add(new Particle(width / 2 - i * 50, height / 2, -300, Double.POSITIVE_INFINITY));
	}
	for (int i = 0; i < 10; i++) {
	    particles.add(new Particle(width / 2 - i * 50, height / 2 - 150, 300, Double.POSITIVE_INFINITY));
	}
	particles.add(new Particle(width / 2 - 450, height / 2 - 25, -300, 1000));
	particles.get(particles.size() - 1).vx = 15;
	particles.get(particles.size() - 1).c = Color.YELLOW;
    }

    public static void dipole(ArrayList<Particle> particles, float width, float height) {
	double q = 20001.51593409412635082844644784927368164065;
	particles.add(new Particle(width / 2, height / 2, q, 1000));
	particles.add(new Particle(width / 2 - 100, height / 2 - 100, -q, 1000));
	ElectricitySim.gefX = 1;
    }
    
    public static void shell(ArrayList<Particle> particles, float width, float height) {
	double step = 1;
	double r = 1000;
	for(double i = 0; i < 360; i += step) {
	    double x = Math.cos(Math.toRadians(i)) * r;
	    double y = Math.sin(Math.toRadians(i)) * r;
	    particles.add(new Particle(width / 2 + x, height / 2 + y, -10, Double.POSITIVE_INFINITY));
	}
	ElectricitySim.gefX = 1;
    }

    public static void test(ArrayList<Particle> particles, float width, float height) {
    }

    public static interface PresetFunction {
	void apply(ArrayList<Particle> particles, float width, float height);
    }

}
