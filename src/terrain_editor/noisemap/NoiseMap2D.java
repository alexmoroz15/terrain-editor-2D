package terrain_editor.noisemap;

import java.util.ArrayList;
import java.util.Random;

/*
Abstract Class that defines common functionality for all noise maps.
 */
public abstract class NoiseMap2D {
    // RNG seed
    long seed;

    // 512 pre-generated RNG values used for noise map
    int[] randomVals;

    // How often the noise map pattern repeats horizontally and vertically.
    // Smaller numbers mean the map is more "zoomed in"
    double frequency;

    // Interpolation method used to retrieve intermediate values
    InterpMethod interp;

    // Collection of functions that are called when one of the properties of
    // the map has updated
    ArrayList<ChangeListener> changeListeners;

    public NoiseMap2D(long seed, double frequency) {
        changeListeners = new ArrayList<>();
        setSeed(seed);
        this.frequency = frequency;
        interp = new Lerp();
    }

    // Internal constructor used to make "sub-maps" with the same pattern.
    // Used by FractalMap2D.
    protected NoiseMap2D(int[] randomVals, double frequency, InterpMethod interp) {
        changeListeners = new ArrayList<>();
        this.seed = 0;
        this.randomVals = randomVals;
        this.frequency = frequency;
        this.interp = interp;
    }

    // Returns a deterministic semi-random number that corresponds to the
    // position components.
    public abstract double get(double x, double y);

    public void setSeed(long seed) {
        this.seed = seed;
        var random = new Random(this.seed);
        randomVals = new int[512];
        for (int i = 0; i < randomVals.length; i++) {
            randomVals[i] = random.nextInt(256);
        }
        commitChanges();
    }

    public long getSeed() { return seed; }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
        commitChanges();
    }

    public double getFrequency() {
        return frequency;
    }

    public void addChangeListener(ChangeListener cl) {
        changeListeners.add(cl);
    }

    // Calls each of the change listeners. Used to report changes.
    protected void commitChanges() {
        for (var cl : changeListeners) {
            cl.handle(this);
        }
    }

    public interface ChangeListener {
        void handle(NoiseMap2D newValue);
    }

    // General interpolation method.
    public interface InterpMethod {
        // y1 and y2 can be any number.
        // t represents the distance from y1 to y2 in the range [0.0, 1.0].
        double interp(double y1, double y2, double t);
    }

    // Standard linear interpolation.
    public static class Lerp implements InterpMethod {
        public double interp(double y1, double y2, double t) {
            return y1 * (1 - t) + y2 * t;
        }
    }
}
