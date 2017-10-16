import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.IOException;
import java.io.OutputStream;
public class SpatialFiltering {

	public SpatialFiltering(BufferedImage img, int[][] array, String s) throws IOException {
    int[][] imageArray = bufferedImageToArray(img);
    int[][] filterArray = getFilteringArray(imageArray, array);
    BufferedImage image = arrayToBufferedImage(filterArray);
    showImage(image, s);
  }

  public SpatialFiltering(BufferedImage img, int[][] smoothingArray, int k) throws IOException {
    int[][] imageArray = bufferedImageToArray(img);

    int[][] smoothingFilteringArray = getFilteringArray(imageArray, smoothingArray);

    int[][] highboostFilterArray = highboostFiltering(imageArray, smoothingFilteringArray, k);
    BufferedImage image = arrayToBufferedImage(highboostFilterArray);
    int length = smoothingArray.length;
    showImage(image, "HighboostFiltering" + String.valueOf(k));
  }

  public int[][] getMaskArrsy(int[][] imageArray, int[][] filterArray) {
    int height = imageArray.length, width = imageArray[0].length;
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = imageArray[i][j] - filterArray[i][j];
      }
    }
    return temp;
  }

  public int[][] highboostFiltering(int[][] imageArray, int[][] filterArray, int k) {
    int height = imageArray.length, width = imageArray[0].length;
    int[][] temp = new int [height][width];
    temp = getMaskArrsy(imageArray, filterArray);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        imageArray[i][j] += temp[i][j];
      }
    }
    return imageArray;
  }

  public int[][] bufferedImageToArray(BufferedImage img) {
    int height = img.getHeight(), width = img.getWidth();
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = img.getRGB(j, i) & 0xFF;
      }
    }
    return temp;
  }

  public BufferedImage arrayToBufferedImage(int[][] imageArray) {
    int height = imageArray.length, width = imageArray[0].length;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int gray = 255 << 24 | imageArray[i][j] << 16 | imageArray[i][j] << 8 | imageArray[i][j];
        img.setRGB(j, i, gray);
      }
    }
    return img;
  }

  public int[][] getFilteringArray(int[][] imageArray, int[][] filteringArray) {
    int height = imageArray.length, width = imageArray[0].length;
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getFilteringValue(i, j, imageArray, filteringArray);
      }
    }
    return temp;
  }

  public int getFilteringValue(int row, int col, int[][] imageArray, int[][] filteringArray) {
    int count = 0;
    int height = imageArray.length, width = imageArray[0].length;
    int filterLength = filteringArray.length;
    int gap = (filterLength - 1) / 2;
    for (int i = 0; i < filterLength; i++) {
      for (int j = 0; j < filterLength; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          count += imageArray[row + i - gap][col + j - gap];
        }
      }
    }
    return count / (filterLength * filterLength);
  }

  public void showImage(BufferedImage img, String s) throws IOException {
     OutputStream output = new FileOutputStream("img/" + s + ".png");
     ImageIO.write(img, "png", output);
  }
}
