import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

public class ImageDenoising {
  BufferedImage image;
  int height, width;
  int pixelMax = 255, pixelMin = 0;
  public ImageDenoising(BufferedImage img) throws IOException {
    height = img.getHeight();
    width = img.getWidth();
    int[][] imageArray = bufferedImageToArray(img);
    denoisingTest(imageArray);
  }
  //  这里添加测试
  public void denoisingTest(int[][] imageArray) throws IOException {
    // int[][] gausssianNoiseArray = imageWithGaussianNoise(imageArray, 0, 40);
    // int[][] piperSaltNoiseArray = imageWithPiperSaltNoise(imageArray, 0.2);
    int[][] geometricFilteringArray = getGeometricFilteringArray(imageArray, 2);
    int[][] medianFilteringArray = getMedianFilteringArray(imageArray, 3);
    int[][] maxFilteringArray = getMaxFilteringArray(imageArray, 3);
    int[][] minFilteringArray = getMinFilteringArray(imageArray, 3);
    // image = arrayToBufferedImage(gausssianNoiseArray);
    // showImage(image, "gausssianNoiseImage");
    // image = arrayToBufferedImage(piperSaltNoiseArray);
    // showImage(image, "piperSaltNoiseImage");
    image = arrayToBufferedImage(geometricFilteringArray);
    showImage(image, "geometricImage2");
    image = arrayToBufferedImage(medianFilteringArray);
    showImage(image, "medianImage3");
    image = arrayToBufferedImage(maxFilteringArray);
    showImage(image, "maxImage3");
    image = arrayToBufferedImage(minFilteringArray);
    showImage(image, "minImage3");
  }

  public void showImage(BufferedImage img, String s) throws IOException {
     OutputStream output = new FileOutputStream("img/denoising/" + s + ".png");
     ImageIO.write(img, "png", output);
  }
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("img/denoising/gausssianNoiseImage.png"));
    new ImageDenoising(img);
  }


  //中值均值滤波
  public int[][] getMedianFilteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getMedianFilteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }
  //最大值均值滤波
  public int[][] getMaxFilteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getMaxFilteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }
  //最小值均值滤波
  public int[][] getMinFilteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getMinFilteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }

  public int getMaxFilteringValue(int row, int col, int[][] imageArray, int n) {
    int[] temp = new int[n * n];
    int count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          temp[count] = imageArray[row + i - gap][col + j - gap];
          count++;
        }
      }
    }
    // System.out.println(count);
    Arrays.sort(temp);
    return temp[count - 1];
  }
  public int getMinFilteringValue(int row, int col, int[][] imageArray, int n) {
    int[] temp = new int[n * n];
    int count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          temp[count] = imageArray[row + i - gap][col + j - gap];
          count++;
        }
      }
    }
    // System.out.println(count);
    Arrays.sort(temp);
    return temp[0];
  }

  public int getMedianFilteringValue(int row, int col, int[][] imageArray, int n) {
    int[] temp = new int[n * n];
    int count = 0;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          temp[count] = imageArray[row + i - gap][col + j - gap];
          count++;
        }
      }
    }
    // System.out.println(count);
    Arrays.sort(temp);
    if (count % 2 == 0) {
      return (temp[count / 2 - 1] + temp[count / 2]) / 2;
    } else {
      return temp[count / 2];
    }
  }

  //几何均值滤波
  public int[][] getGeometricFilteringArray(int[][] imageArray, int n) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = getGeometricFilteringValue(i, j, imageArray, n);
      }
    }
    return temp;
  }

  public int getGeometricFilteringValue(int row, int col, int[][] imageArray, int n) {
    int count = 1;
    int gap = (n - 1) / 2;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (row + i - gap >= 0 && row + i - gap < height && 
          col + j - gap >= 0 && col + j - gap < width) {
          count *= imageArray[row + i - gap][col + j - gap];
        }
      }
    }
    int temp = (int)Math.pow((double)count, 1d / (double)(n * n));;
    System.out.println(temp);
    return temp;
  }

  public double gausssianNoise(double mean, double var) {
	  Random rand = new Random();
    return mean + var * rand.nextGaussian();
  }
  public int[][] imageWithGaussianNoise(int[][] imageArray, double mean, double var) {
    int[][] gausssianNoiseArray = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int temp = (int) (imageArray[i][j] + gausssianNoise(mean, var));
        if (temp > pixelMax) {
          temp = pixelMax;
        } else if (temp < pixelMin) {
          temp = pixelMin;
        }
        gausssianNoiseArray[i][j] = temp;
      }
    }
    return gausssianNoiseArray;
  }
  //piperSalt
  public int[][] imageWithPiperSaltNoise(int[][] imageArray, double ratio) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = imageArray[i][j];
      }
    }
    int n = (int) (height * width * ratio);
    System.out.println(n);
    for (int i = 0; i < n; i++) {
      int x = (int) (Math.random() * (double)height);
      int y = (int) (Math.random() * (double)width);
      int r = (int)(Math.random() * 10) % 2;
      if (r == 1) {
        temp[x][y] = 255;
      } else {
        temp[x][y] = 0;
      }
    }
    return temp;
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
}
