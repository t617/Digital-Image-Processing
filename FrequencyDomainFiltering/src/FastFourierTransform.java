import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class FastFourierTransform {
  public BufferedImage image;
  public final int height, width;
  public FastFourierTransform(BufferedImage img) throws IOException {
    height = img.getHeight();
    width = img.getWidth();
    int[][] paddingImage = getPaddingImage(img);
    // BufferedImage buffImage = ArrayToBufferedImage(paddingImage);
    // showImage(buffImage, "origin");
    double[][][] fourierArray = fft_2d(paddingImage);
    BufferedImage fftSpectrum = DiscreteFourierTransform.ToFourierSpectrum(fourierArray, height, width);
    showImage(fftSpectrum, "FFTSpectrum");
    BufferedImage ifftImage = ifft_2d(fourierArray, height, width);
    showImage(ifftImage, "IFFTImage");
  }
  // public BufferedImage ArrayToBufferedImage(int[][] temp) {
  //   BufferedImage desImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
  //   for (int i = 0; i < height; i++) {
  //     for (int j = 0; j < width; j++) {
  //   	  int piexl;
  //       if ((i + j) % 2 != 0) {
  //         piexl = -temp[i][j];
  //       } else {
  //         piexl = temp[i][j];
  //       }
  //       System.out.print(piexl + "+");
		//     int gray = 255 << 24 | piexl << 16 | piexl << 8 | piexl;
  //       desImg.setRGB(j, i, gray);
  //     }
  //   }
  //   System.out.println("finish");
	 //  return desImg;
  // }

  public static int[][] getPaddingImage(BufferedImage img) {
    int srcHeight = img.getHeight();
    int srcWidth = img.getWidth();
    int maxSrcHeight = getMax2Pow(srcHeight);
    int maxSrcWidth = getMax2Pow(srcWidth);

    int[][] paddingImage = new int[maxSrcHeight][maxSrcWidth];
    int piexl, gray = 0;
    for (int i = 0; i < maxSrcHeight; i++) {
      for (int j = 0; j < maxSrcWidth; j++) {
        if (i < srcHeight && j < srcWidth) {
          piexl = img.getRGB(j, i);
          // System.out.print((int)(piexl&0xFF) + "+");
          if ((i + j) % 2 != 0) {
            gray = -(piexl & 0xFF);
          } else {
            gray = piexl & 0xFF;
          }
          paddingImage[i][j] = gray;
        } else {
          paddingImage[i][j] = 0;
        }
      }
    }
    return paddingImage;
  }

  public static int getMax2Pow(int temp) {
    int cur = 1;
    while(true) {
      if (temp > cur && temp <= 2 * cur) {
        return cur * 2;
      } else {
        cur *= 2;
      }
    }
  }

  public static double[][][] fft_2d(double[][][] fourierArray) {
    int height = fourierArray.length;
    int width = fourierArray[0].length;
    double[][] tempw = new double[width][2];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        tempw[j][0] = fourierArray[i][j][0];
        tempw[j][1] = fourierArray[i][j][1];
      }
      fourierArray[i] = fft_1d(tempw);
    }
    double[][] temph = new double[height][2];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        temph[j][0] = fourierArray[j][i][0];
        temph[j][1] = fourierArray[j][i][1];
      }
      double[][] temp = fft_1d(temph);
      for (int k = 0; k < height; k++) {
        fourierArray[k][i][0] = temp[k][0];
        fourierArray[k][i][1] = temp[k][1];
      }
    }
    return fourierArray;
  }

  public static double[][][] fft_2d(int[][] paddingImage) {
    int height = paddingImage.length;
    int width = paddingImage[0].length;
    double[][][] next = new double[height][width][2];
    double[][] tempw = new double[width][2];

    for (int i = 0; i < height; i++) {
      for (int j  = 0; j < width; j++) {
        tempw[j][0] = (double)paddingImage[i][j];
        tempw[j][1] = 0;
      }
      double[][] temp = fft_1d(tempw);
      for (int k = 0; k < width; k++) {
        next[i][k][0] = temp[k][0];
        next[i][k][1] = temp[k][1];
      }
    }

    double[][] temph = new double[height][2];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        temph[j][0] = next[j][i][0];
        temph[j][1] = next[j][i][1];
      }
      double[][] temp = fft_1d(temph);
      // for (int k = 0; k < height; k++) {
      //   temph[k][0] = temp[k][0];
      //   temph[k][1] = temp[k][1];
      // }
      for (int k = 0; k < height; k++) {
        next[k][i][0] = temp[k][0];
        next[k][i][1] = temp[k][1];
      }
    }
    return next;
  }

  public static BufferedImage ifft_2d(double[][][] fftArray, int srcHeight, int srcWidth) {
    int height = fftArray.length;
    int width = fftArray[0].length;
    BufferedImage desImg = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_BYTE_GRAY);

    double[][] tempw = new double[width][2];

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        tempw[j][0] = fftArray[i][j][0];
        tempw[j][1] = fftArray[i][j][1];
      }
      double[][] tempArray1 = ifft_1d(tempw);
      for (int k = 0; k < width; k++) {
        fftArray[i][k] = tempArray1[k];
      }
    }
    double[][] temph = new double[height][2];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        temph[j][0] = fftArray[j][i][0];
        temph[j][1] = fftArray[j][i][1];
      }
      double[][] tempArray2 = ifft_1d(temph);
      for (int k = 0; k < height; k++) {
        fftArray[k][i][0] = tempArray2[k][0];
        fftArray[k][i][1] = tempArray2[k][1];
      }
    }
    int[][] last = new int[height][width];
    for (int i = 0; i < srcHeight; i++) {
      for (int j = 0; j < srcWidth; j++) {
        last[i][j] = (int)fftArray[i][j][0];
      }
    }
    for (int i = 0; i < srcHeight; i++) {
      for (int j = 0; j < srcWidth; j++) {
        int piexl = last[i][j];
        if ((i + j) % 2 != 0) {
          piexl = -piexl;
        }
        int gray = 255 << 24 | piexl << 16 | piexl << 8 | piexl;
        desImg.setRGB(j, i, gray);
      }
    }
    return desImg;
  }

  public static double[][] fft_1d(double[][] fft_1dArray) {
    int n = fft_1dArray.length;
    if (n == 1) {
      return fft_1dArray;
    }
    if (n % 2 != 0) {
      throw new RuntimeException("");
    }

    double[][] even = new double[n / 2][2];
    for (int i = 0; i < n / 2; i++) {
      even[i][0] = fft_1dArray[2 * i][0];
      even[i][1] = fft_1dArray[2 * i][1];
    }

    double[][] tempe = fft_1d(even);
    double[][] odd = new double[n / 2][2];
    for (int i = 0; i < n / 2; i++) {
      odd[i][0] = fft_1dArray[2 * i + 1][0];
      odd[i][1] = fft_1dArray[2 * i + 1][1];
    }
    double[][] tempo = fft_1d(odd);
    double[][] tempn = new double[n][2];
    for (int i = 0; i < n / 2; i++) {
      double t = -2 * i * Math.PI / n;
      double[] wk = new double[2];
      wk[0] = Math.cos(t);
      wk[1] = Math.sin(t);
      tempn[i][0] = tempe[i][0] + wk[0] * tempo[i][0] - wk[1] * tempo[i][1];
      tempn[i][1] = tempe[i][1] + wk[1] * tempo[i][0] + wk[0] * tempo[i][1];
      tempn[i + n / 2][0] = tempe[i][0] - (wk[0] * tempo[i][0] - wk[1] * tempo[i][1]);
      tempn[i + n / 2][1] = tempe[i][1] - (wk[1] * tempo[i][0] + wk[0] * tempo[i][1]);
    }
    return tempn;
  }

  public static double[][] ifft_1d(double[][] x) {
    int n = x.length;
    double[][] y = new double[n][2];
    for (int i = 0; i < n; i++) {
      y[i][0] = x[i][0];
      y[i][1] = -x[i][1];     
    }
    y = fft_1d(y);
    for (int i = 0; i < n; i++) {
      y[i][0] = y[i][0];
      y[i][1] = -y[i][1];
    }
    for (int i = 0; i < n; i++) {
      y[i][0] /= (double)n;
      y[i][1] /= (double)n;
    }
    return y;
  }

  // public double[][] convole(double[][] x, double[][] y) {
  //   if (x.length != y.length) {
  //     throw new RuntimeException("d");
  //   }
  //   int n = x.length;
  //   double[][] a = fft_1d(x);
  //   double[][] b = fft_1d(y);
  //   double[][] c = new double[n][2];
  //   for (int i = 0; i < n; i++) {
  //     c[i][0] = a[i][0] * b[i][0] - a[i][1] * b[i][1];
  //     c[i][1] = a[i][0] * b[i][1] + a[i][0] * b[i][0];
  //   }
  //   return c;
  // }
  public void showImage(BufferedImage img, String s) throws IOException {
    ImageIO.write(img, "png", new FileOutputStream("img/" + s + ".png"));
  }
}
