import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Scale {
  public static BufferedImage image;

  public Scale(BufferedImage image, String outputImagePath, int width, int height) throws IOException {
    testExample(image, outputImagePath, width, height);
  }

  public void testExample(BufferedImage img, String outputImagePath, int width, int height) throws IOException {
    int[][] srcArray = bufferedImageToArray(img);
    int[][] scaleImg = imgScale(getGrayArray(srcArray), width, height);
    int[][] imageArray = ToImageArray(scaleImg);
    BufferedImage bufImg = arrayToBufferedImage(imageArray);
    showImage(bufImg, outputImagePath, width, height);
  }

  public BufferedImage arrayToBufferedImage(int[][] array) {
    BufferedImage bufImg = new BufferedImage(array[0].length, array.length, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[0].length; j++) {
        bufImg.setRGB(j, i, array[i][j]);
      }
    }
    return bufImg;
  }

  public int[][] getGrayArray(int[][] twoDimensionarray) {
    int height = twoDimensionarray.length, width = twoDimensionarray[0].length;
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = twoDimensionarray[i][j] & 0xFF;
      }
    }
    return temp;
  }

  public int[][] ToImageArray(int[][] imageArray) {
    int height = imageArray.length, width = imageArray[0].length;
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = 255 << 24 | imageArray[i][j] << 16 | imageArray[i][j] << 8 | imageArray[i][j];
      }
    }
    return temp;
  }

  public int[][] bufferedImageToArray(BufferedImage img) {
    int srcWidth = img.getWidth(), srcHeight = img.getHeight();
    int[][] temp = new int[srcHeight][srcWidth];
    for (int i = 0; i < srcHeight; i++) {
      for (int j = 0; j < srcWidth; j++) {
        temp[i][j] = img.getRGB(j, i);
      }
    }
    return temp;
  }

  public void showImage(BufferedImage img, String outputImagePath, int width, int height) throws IOException {
    OutputStream output = new FileOutputStream(outputImagePath + String.valueOf(width) + "-" + String.valueOf(height) + ".png");
    ImageIO.write(img, "png", output);
  }
  
  private int avoidOutside(int x, int boder) {
    return x > boder ? boder : x;
  }

  public int[][] imgScale(int[][] pixelsArray, int destW, int destH) {
    int srcH = pixelsArray.length, srcW = pixelsArray[0].length;
    int[][] tempArray = new int[destH][destW];

    float rowRatio = ((float)srcH) / ((float)destH), colRatio = ((float)srcW) / ((float)destW);
    
    for(int row = 0; row < destH; row++) {
      
      double srcRow = ((float)row) * rowRatio;
      double j = Math.floor(srcRow);
      double t = srcRow - j;
      for(int col = 0; col < destW; col++) {

        double srcCol = ((float)col) * colRatio;
        double k = Math.floor(srcCol);
        double u = srcCol - k;
        
        double c1 = (1.0d - t) * (1.0d - u), c2 = (t) * (1.0d - u);
        double c3 = t * u,c4 = (1.0d - t) * u;

        tempArray[row][col] = (int) (c1 * pixelsArray[(int)j][(int)k] + c2 * pixelsArray[avoidOutside((int)(j + 1), srcH - 1)][avoidOutside((int)k, srcW - 1)] + 
        c3 * pixelsArray[avoidOutside((int)(j + 1), srcH - 1)][avoidOutside((int)(k + 1),srcW - 1)] +
        c4 * pixelsArray[avoidOutside((int)j, srcH - 1)][avoidOutside((int)(k + 1), srcW - 1)]);
      }
    }
    return tempArray;
  }
}
