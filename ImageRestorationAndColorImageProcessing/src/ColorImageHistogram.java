import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

public class ColorImageHistogram {
  int height, width;
  public ColorImageHistogram(BufferedImage img) throws IOException {
    height = img.getHeight();
    width = img.getWidth();
    equalize_hist(img);
  }
  //题目的三个量化
  public void equalize_hist(BufferedImage img) throws IOException {
    int[][] imageArray =  bufferedImageToArray(img);
    int[][] intensityNumber = getIntensityNumber(imageArray);
    double[][][] HSIArray = imageRGBToHSI(imageArray);
    double[][][] HSIIntensityArray = HSIIntensityHistogram(HSIArray);
    showImage(imageHSIToRGB(HSIIntensityArray), "HSIIntensityHistogram");
    showImage(histogram(imageArray, intensityNumber), "histogram");
    showImage(averageHistogram(imageArray, intensityNumber), "averageHistogram");
  }
  public int[][] bufferedImageToArray(BufferedImage img) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        temp[i][j] = img.getRGB(j, i);
      }
    }
    return temp;
  }
  //HSI转RGB
  public BufferedImage imageHSIToRGB(double[][][] HSIArray) {
    int rChannel = 0, gChannel = 0, bChannel = 0;
    int pixel = 0;
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (HSIArray[0][i][j] >= 0 && HSIArray[0][i][j] < 2 * Math.PI / 3d) {
          rChannel = (int)(HSIArray[2][i][j] * (1 + (HSIArray[1][i][j] * 
            Math.cos(HSIArray[0][i][j]))  / Math.cos(Math.PI / 3d - HSIArray[0][i][j])));
          bChannel = (int)(HSIArray[2][i][j] * (1 - HSIArray[1][i][j]));
          gChannel = (int)(3 * HSIArray[2][i][j] - rChannel - bChannel);
        } else if (HSIArray[0][i][j] >= 2 * Math.PI / 3d && HSIArray[0][i][j] < 4 * Math.PI / 3d) {
          rChannel = (int)(HSIArray[2][i][j] * (1 - HSIArray[1][i][j]));
          gChannel = (int)(HSIArray[2][i][j] * (1 + (HSIArray[1][i][j] * 
            Math.cos(HSIArray[0][i][j]))  / Math.cos(Math.PI / 3d - HSIArray[0][i][j])));
          bChannel = (int)(3 * HSIArray[2][i][j] - rChannel - gChannel);
        } else if (HSIArray[0][i][j] >= 4 * Math.PI / 3d && HSIArray[0][i][j] < 2d * Math.PI) {
          bChannel = (int)(HSIArray[2][i][j] * (1 + (HSIArray[1][i][j] * 
            Math.cos(HSIArray[0][i][j]))  / Math.cos(Math.PI / 3d - HSIArray[0][i][j])));
          gChannel = (int)(HSIArray[2][i][j] * (1 - HSIArray[1][i][j]));
          rChannel = (int)(3 * HSIArray[2][i][j] - bChannel - gChannel);
        }
        pixel = 255 << 24 | rChannel << 16 | gChannel << 8 | bChannel;
        img.setRGB(j, i, pixel);
      }
    }
    return img;
  }
  //HSI的强度通道直方图均衡化
  public double[][][] HSIIntensityHistogram(double[][][] array) {
    int[] intensityArray = new int[256];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int t = (int)array[2][i][j];
        intensityArray[t]++;
      }
    }
    double k = (double)255 / (double)(height * width);
    double[] r = new double[256];
    r[0] = k * intensityArray[0];
    for (int i = 1; i < 256; i++) {
      r[i] = r[i - 1] + k * intensityArray[i];
    }
    double[][][] imageHSIArray = new double[3][height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int iChannel = (int)array[2][i][j];
        double pixel = r[iChannel];
        imageHSIArray[0][i][j] = array[0][i][j];
        imageHSIArray[1][i][j] = array[1][i][j];
        imageHSIArray[2][i][j] = pixel;
      }
    }
    return imageHSIArray;
  }
  //RGB转HSI
  public double[][][] imageRGBToHSI(int[][] imageArray) {
    double[][][] imageHSIArray = new double[3][height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int rChannel = (imageArray[i][j] >> 16) & 0xFF;
        int gChannel = (imageArray[i][j] >> 8) & 0xFF;
        int bChannel = imageArray[i][j] & 0xFF;
        double tempH = (double)(2 * rChannel - gChannel - bChannel) / (2 * (double)
          Math.pow(Math.pow(rChannel - gChannel, 2) + (rChannel - bChannel) * (gChannel - bChannel), 0.5));
        if (gChannel < bChannel) {
          imageHSIArray[0][i][j] = 2 * Math.PI - (double)Math.acos(tempH);
        } else {
          imageHSIArray[0][i][j] = (double)Math.acos(tempH);
        }
        double tempS = 1 - (3 * (double)Math.min(rChannel, Math.min(gChannel, bChannel))) / (double)(rChannel + gChannel + bChannel);
        imageHSIArray[1][i][j] = tempS;
        double tempI = (double)(rChannel + gChannel + bChannel) / 3;
        imageHSIArray[2][i][j] = tempI;
      }
    }
    return imageHSIArray;
  }
  //获取每个直方图的灰度的频数
  public int[][] getIntensityNumber(int[][] imageArray) {
    int[][] temp = new int[3][256];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int rChannel = (imageArray[i][j] >> 16) & 0xFF;
        temp[0][rChannel]++;
        int gChannel = (imageArray[i][j] >> 8) & 0xFF;
        temp[1][gChannel]++;
        int bChannel = imageArray[i][j] & 0xFF;
        temp[2][bChannel]++;
      }
    }
    return temp;
  }

  //对三个通道的平均值做直方图均衡化
  public BufferedImage averageHistogram(int[][] imageArray, int[][] array) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    double k = (double)255 / (double)(height * width);

    int[] temp = new int[256];
    for (int i = 0; i < 256; i++) {
      temp[i] = (array[0][i] + array[1][i] + array[2][i]) / 3; 
    }

    double[] r = new double[256];
    r[0] = k * temp[0];
    for (int i = 1; i < 256; i++) {
      r[i] = r[i - 1] + k * temp[i];
    }
    int t = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int rChannel = (imageArray[i][j] >> 16) & 0xFF;
        int rPixel = (int)(r[rChannel]);
        int gChannel = (imageArray[i][j] >> 8) & 0xFF;
        int gPixel = (int)(r[gChannel]);
        int bChannel = imageArray[i][j] & 0xFF;
        int bPixel = (int)(r[bChannel]);
        t = 255 << 24 | rPixel << 16 | gPixel << 8 | bPixel;
        img.setRGB(j, i, t);
      }
    }
    return img;
  }
  //直方图均衡化
  public BufferedImage histogram(int[][] imageArray, int[][] array) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
    double k = (double)255 / (double)(height * width);

    double[] rr = new double[256];
    rr[0] = k * array[0][0];
    for (int i = 1; i < 256; i++) {
      rr[i] = rr[i - 1] + k * array[0][i];
    }
    double[] rg = new double[256];
    rg[0] = k * array[1][0];
    for (int i = 1; i < 256; i++) {
      rg[i] = rg[i - 1] + k * array[1][i];
    }
    double[] rb = new double[256];
    rb[0] = k * array[2][0];
    for (int i = 1; i < 256; i++) {
      rb[i] = rb[i - 1] + k * array[2][i];
    }
    int temp = 0;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int rChannel = (imageArray[i][j] >> 16) & 0xFF;
        int rPixel = (int)(rr[rChannel]);
        int gChannel = (imageArray[i][j] >> 8) & 0xFF;
        int gPixel = (int)(rg[gChannel]);
        int bChannel = imageArray[i][j] & 0xFF;
        int bPixel = (int)(rb[bChannel]);
        temp = 255 << 24 | rPixel << 16 | gPixel << 8 | bPixel;
        img.setRGB(j, i, temp);
      }
    }
    return img;
  }
  public void showImage(BufferedImage img, String s) throws IOException {
     OutputStream output = new FileOutputStream("img/histogram/" + s + ".png");
     ImageIO.write(img, "png", output);
  }
  public static void main(String[] args) throws IOException {
    BufferedImage img = ImageIO.read(new File("img/78.png"));
    new ColorImageHistogram(img);
  }
}
