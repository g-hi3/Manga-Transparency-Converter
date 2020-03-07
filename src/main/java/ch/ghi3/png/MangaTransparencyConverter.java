package ch.ghi3.png;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class MangaTransparencyConverter {

  private static final int ALPHA_BIT_MASK = 255;

  public static void main(String[] args) throws IOException {
    for (String nextArgument : args) {
      File imageFile = new File(nextArgument);
      BufferedImage sourceImage = ImageIO.read(imageFile);
      int sourceType = sourceImage.getType();
      int sourceByteDepth = getByteDepth(sourceType);
      if (sourceType == BufferedImage.TYPE_4BYTE_ABGR) {
        System.out.println("Warning: File " + nextArgument + " is ignored.");
        continue;
      }
      int sourceWidth = sourceImage.getWidth();
      int sourceHeight = sourceImage.getHeight();
      int targetType = BufferedImage.TYPE_4BYTE_ABGR;
      int targetByteDepth = 4;
      BufferedImage targetImage = new BufferedImage(sourceWidth, sourceHeight, targetType);
      WritableRaster targetRaster = targetImage.getRaster();
      WritableRaster sourceRaster = sourceImage.getRaster();
      for (int row = 0; row < sourceHeight; row++) {
        int[] rgba = new int[targetByteDepth * sourceWidth];
        int[] rgb = new int[sourceByteDepth * sourceWidth];
        sourceRaster.getPixels(0, row, sourceWidth, 1, rgb);
        for (int i = 0, j = 0; i < rgb.length; i += sourceByteDepth, j += targetByteDepth) {
          int alpha = getAlpha(sourceByteDepth, rgb, i);
          rgba[j] = ALPHA_BIT_MASK - alpha;
          rgba[j + 1] = ALPHA_BIT_MASK - alpha;
          rgba[j + 2] = ALPHA_BIT_MASK - alpha;
          rgba[j + 3] = alpha;
        }
        targetRaster.setPixels(0, row, sourceWidth, 1, rgba);
      }
      String outFileName = getFileName(nextArgument);
      File outFile = new File(outFileName);
      ImageIO.write(targetImage, "png", outFile);
    }
  }

  private static int getByteDepth(int imageType) {
    switch (imageType) {
      case BufferedImage.TYPE_3BYTE_BGR:
        return 3;

      case BufferedImage.TYPE_4BYTE_ABGR:
        return 4;

      case BufferedImage.TYPE_BYTE_GRAY:
        return 1;
    }
    throw new IllegalArgumentException("Unsupported image type!");
  }

  private static int getAlpha(int sourceByteDepth, int[] colorRow, int index) {
    switch (sourceByteDepth) {
      case 4:
        return colorRow[index + 3];

      case 3:
        return ALPHA_BIT_MASK - (colorRow[index] + colorRow[index + 1] + colorRow[index + 2]) / 3;

      case 1:
        return ALPHA_BIT_MASK - colorRow[index];
    }
    throw new IllegalArgumentException("Unsupported byte depth!");
  }

  private static String getFileName(String sourceFileName) {
    if (sourceFileName.toLowerCase().endsWith(".png")) {
      return sourceFileName;
    } else {
      return sourceFileName + ".png";
    }
  }

}
