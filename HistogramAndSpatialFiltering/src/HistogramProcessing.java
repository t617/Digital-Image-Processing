import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
public class HistogramProcessing {

	public HistogramProcessing(BufferedImage img) throws IOException {
    equalize_hist(img);
  }

  public void equalize_hist(BufferedImage img) throws IOException {
    int[][] imageArray =  bufferedImageToArray(img);
    int[] intensityNumber = getIntensityNumber(imageArray);
    showImage(histogram(imageArray, intensityNumber));
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

  public int[] getIntensityNumber(int[][] imageArray) {
    int[] temp = new int[256];
    int height = imageArray.length, width = imageArray[0].length;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int k = imageArray[i][j] & 0xFF;
        temp[k]++;
      }
    }
    return temp;
  }

  public BufferedImage histogram(int[][] imageArray, int[] array) {
    int height = imageArray.length, width = imageArray[0].length;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    double k = (double)255 / (double)(height * width);
    double[] c = new double[256];
    c[0] = k * array[0];
    for (int i = 1; i < 256; i++) {
      c[i] = c[i - 1] + k * array[i];
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int grey = imageArray[i][j] & 0xFF;
        int piexl = (int)(c[grey]);
        imageArray[i][j] = 255 << 24 | piexl << 16 | piexl << 8 | piexl;
        img.setRGB(j, i, imageArray[i][j]);
      }
    }
    return img;
  }
  
  public void showImage(BufferedImage img) throws IOException {
     OutputStream output = new FileOutputStream("img/histogram.png");
     ImageIO.write(img, "png", output);
  }
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("img/78.png"));
    new HistogramProcessing(img);
  }
}
