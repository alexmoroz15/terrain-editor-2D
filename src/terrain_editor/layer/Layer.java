package terrain_editor.layer;

import javafx.scene.image.Image;
import terrain_editor.ControlPane;
import terrain_editor.noisemap.FractalMap2D;

/*
Represents a layer in the final, composite image.

Each layer keeps track of:
- its noise map
- the control box for the noise map
  - seed input
  - frequency input
  - lacunarity
  - gain
  - tiled sprite/image
  - amplitude cutoffs for where to display image
  - number of rows and columns
- Image object which contains the noise map preview
 */
public class Layer {
    ControlPane controlPane; // Controls noiseMap
    FractalMap2D noiseMap;
    Image previewImage; // Should be updated when noiseMap changes
}
