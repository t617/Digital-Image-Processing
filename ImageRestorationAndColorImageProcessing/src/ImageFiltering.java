import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ImageFiltering {
  int height, width;
  BufferedImage image;
  public ImageFiltering(BufferedImage img) throws IOException {
    height = img.getHeight();
    width = img.getWidth();
    int[][] imageArray = bufferedImageToArray(img);
    filteringTest(imageArray);
  }
  //  在这里添加滤波测试
  public void filteringTest(int[][] imageArray) throws IOException {
    int[][] averageFilteringArray = getFilteringArray(imageArray, 3);
    // int[][] harmonicFilteringArray = getHarmonicFliteringArray(imageArray, 9);
    // int[][] contraHarmonicFilteringArray = getContraHarmonicFilteringArray(imageArray, 3, 1);
    image = arrayToBufferedImage(averageFilteringArray);
    showImage(image, "avarageImage3");
    // image = arrayToBufferedImage(harmonicFilteringArray);
    // showImage(image, "harmonicImage9");
    // image = arrayToBufferedImage(contraHarmonicFilteringArray);
    // showImage(image, "contraHarmonicImage1");
  }

  public int[][] bufferedImageToArray(BufferedImage img) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = img.getRGB(j, i) & 0xFF;
      }
    }
    return temp;
  }

  public BufferedImage arrayToBufferedImage(int[][] imageArray) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int gray = 255 << 24 | imageArray[i][j] << 16 | imageArray[i][j] << 8 | imageArray[i][j];
        img.setRGB(j, i, gray);
      }
    }
    return img;
  }
  //谐波均值滤波
  public int[][] getContraHarmonicFilteringArray(int[][] imageArray, int n, double q) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = (int)(getContraHarmonicFilteringValue(i, j, imageArray, n, q + 1) / getContraHarmonicFilteringValue(i, j, imageArray, n, q));
      }
    }
    return temp;
  }
  public double getContraHarmonicFilteringValue(int row, int col, int[][] imageArray, int n, double q) {
    double count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          count += Math.pow((double)imageArray[row + i - gap][col + j - gap], q);
        }
      }
    }
    return count;
  }
  //调和均值滤波
  public int[][] getHarmonicFliteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getHarmonicFliteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }
  public int getHarmonicFliteringValue(int row, int col, int[][] imageArray, int n) {
    double count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          count += 1d / (double)(imageArray[row + i - gap][col + j - gap]);
        }
      }
    }
    int harmonicValue = (int)(n * n /count);
    System.out.println(harmonicValue);
    return harmonicValue;
  }
  //算数均值滤波
  public int[][] getFilteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getFilteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }
  public int getFilteringValue(int row, int col, int[][] imageArray, int n) {
    int count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          count += imageArray[row + i - gap][col + j - gap];
        }
      }
    }
    return count / (n * n);
  }
  public void showImage(BufferedImage img, String s) throws IOException {
     OutputStream output = new FileOutputStream("img/" + s + ".png");
     ImageIO.write(img, "png", output);
  }
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("img/task_1.png"));
    new ImageFiltering(img);
  }
}
