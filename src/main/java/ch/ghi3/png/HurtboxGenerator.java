package ch.ghi3.png;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class HurtboxGenerator {

  private static final int BYTE_DEPTH = 4;
  private static final int IMAGE_TYPE = BufferedImage.TYPE_4BYTE_ABGR;
  private static final int MAX_VALUE = 255;

  public static void main(String[] args) throws IOException {
    for (String nextArgument : args) {
      File imageFile = new File(nextArgument);
      BufferedImage sourceImage = ImageIO.read(imageFile);
      int sourceType = sourceImage.getType();
      if (sourceType != IMAGE_TYPE) {
        System.out.println("Warning: File " + nextArgument + " is ignored.");
        continue;
      }
      int sourceWidth = sourceImage.getWidth();
      int sourceHeight = sourceImage.getHeight();
      BufferedImage targetImage = new BufferedImage(sourceWidth, sourceHeight, IMAGE_TYPE);
      WritableRaster targetRaster = targetImage.getRaster();
      WritableRaster sourceRaster = sourceImage.getRaster();
      for (int row = 0; row < sourceHeight; row++) {
        int[] targetPixelRow = new int[BYTE_DEPTH * sourceWidth];
        int[] sourcePixelRow = new int[BYTE_DEPTH * sourceWidth];
        sourceRaster.getPixels(0, row, sourceWidth, 1, sourcePixelRow);
        for (int i = 0, j = 0; i < sourcePixelRow.length; i += BYTE_DEPTH, j += BYTE_DEPTH) {
          if (sourcePixelRow[i + 3] == 0) {
            System.out.print(' ');
            targetPixelRow[j + 3] = 0;
          } else {
            System.out.print('#');
            targetPixelRow[j + 1] = MAX_VALUE;
            targetPixelRow[j + 3] = MAX_VALUE;
          }
        }
        System.out.println();
        targetRaster.setPixels(0, row, sourceWidth, 1, targetPixelRow);
      }
      String outFileName = getFileName(nextArgument);
      File outFile = new File(outFileName);
      ImageIO.write(targetImage, "png", outFile);
    }
  }

  private static String getFileName(String sourceFileName) {
    int underscorePosition = sourceFileName.indexOf('_');
    return sourceFileName.substring(0, underscorePosition) + "_hurt" + sourceFileName.substring(underscorePosition);
  }

}
