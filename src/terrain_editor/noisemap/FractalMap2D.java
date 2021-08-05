package terrain_editor.noisemap;

/*
Simple 2D Fractal Map that uses Value Noise.
 */
public class FractalMap2D extends NoiseMap2D {

    private double lacunarity; // Rate at which frequency changes from layer to layer
    private double gain; // Rate at which amplitude changes from layer to layer
    private int numLayers;

    public FractalMap2D(long seed, double frequency, double lacunarity, double gain, int numLayers) {
        super(seed, frequency);
        this.lacunarity = lacunarity;
        this.gain = gain;
        this.numLayers = numLayers;
    }

    public FractalMap2D(long seed, double frequency) {
        super(seed, frequency);
        lacunarity = 1.0;
        gain = 1.0;
        numLayers = 1;
    }

    public FractalMap2D(int[] randomVals, double frequency, InterpMethod interp) {
        super(randomVals, frequency, interp);
        lacunarity = 1.0;
        gain = 1.0;
        numLayers = 1;
    }

    public void setLacunarity(double lacunarity) {
        this.lacunarity = lacunarity;
        commitChanges();
    }
    public double getLacunarity() {
        return lacunarity;
    }

    public void setGain(double gain) {
        this.gain = gain;
        commitChanges();
    }
    public double getGain() {
        return gain;
    }

    public void setNumLayers(int numLayers) {
        this.numLayers = numLayers;
        commitChanges();
    }
    public int getNumLayers() {
        return numLayers;
    }

    @Override
    public double get(double x, double y) {
        double out = 0.0;

        var freq = frequency;
        double amp = 1.0;
        for (int l = 0; l < numLayers; l++) {
            var subMap = new ValueMap2D(randomVals, freq, interp);
            out += subMap.get(x, y) * amp;

            freq *= lacunarity;
            amp *= gain;
        }

        return out;
    }
}
