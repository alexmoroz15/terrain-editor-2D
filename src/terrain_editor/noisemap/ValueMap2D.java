package terrain_editor.noisemap;

// Based on https://www.scratchapixel.com/lessons/procedural-generation-virtual-worlds/procedural-patterns-noise-part-1/

/*
Simple 2D Value Noise Map.
 */
public class ValueMap2D extends NoiseMap2D {
    public ValueMap2D(long seed, double frequency) {
        super(seed, frequency);
    }

    protected ValueMap2D(int[] randomVals, double frequency, InterpMethod interp) {
        super(randomVals, frequency, interp);
    }

    @Override
    public double get(double x, double y) {
        x *= frequency;
        y *= frequency;

        if (x >= 0) {
            x = x - Math.floor(x);
        } else {
            x = 1 - (-x - Math.floor(-x));
        }

        if (y >= 0) {
            y = y - Math.floor(y);
        } else {
            y = 1 - (-y - Math.floor(-y));
        }

        assert x >= 0 && x < 1;
        assert y >= 0 && y < 1;

        x *= 256;
        y *= 256;

        var xlo = (int) x;
        var xhi = (xlo < 255)?(xlo + 1):(0);

        var ylo = (int) y;
        var yhi = (ylo < 255)?(ylo + 1):(0);

        assert xlo >= 0 && xhi < 256;
        assert ylo >= 0 && yhi < 256;

        class MyLambda {
            int operation(int a, int b) {
                return randomVals[randomVals[a] + b];
            }
        }
        var myLambda = new MyLambda();

        var y00 = myLambda.operation(xlo, ylo);
        var y01 = myLambda.operation(xhi, ylo);
        var y10 = myLambda.operation(xlo, yhi);
        var y11 = myLambda.operation(xhi, yhi);

        var z0 = interp.interp(y00, y01, x - xlo);
        var z1 = interp.interp(y10, y11, x - xlo);
        return interp.interp(z0, z1, y - ylo);
    }
}
