import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class DiscreteFourierTransform {
  public BufferedImage image;
  public int height;
  public int width;
  /*temp[0]为实部，temp[1]为虚部*/
  public DiscreteFourierTransform(BufferedImage img) throws IOException {
    height = img.getHeight();
    width = img.getWidth();
    // test();
    int[][] srcImageArray = BufferedImageToArray(img);
    double[][][] DFTArray = ToDFTArray(srcImageArray);
    double[][] destImageArray = ToSpatialArray(DFTArray);
    image = ArrayToBufferedImage(destImageArray);
    showImage(image, "DFTrealPart");
    image = ToFourierSpectrum(DFTArray, height, width);
    showImage(image, "DFTSpectrum");
  }

  // public void test() {
  //   int[][] temp = {{0, 1, 0}, {1, 0, 1}, {0, 1, 0}};
  //   double[][][] t = ToDFTArray(temp);
  //   for (int i = 0; i < 3; i++) {
  //     for (int j = 0; j < 3; j++) {
  //       System.out.println(Math.sqrt(Math.pow(t[i][j][0],2) + Math.pow(t[i][j][1],2));
  //     }
  //   }
  // }
  /* 傅里叶频谱*/
  public static BufferedImage ToFourierSpectrum(double[][][] DFTArray, int height, int width) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    double[][] modulus = new double[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        modulus[i][j] = Math.sqrt(Math.pow(DFTArray[i][j][0], 2) + Math.pow(DFTArray[i][j][1], 2));
      }
    }
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        modulus[i][j] = Math.log(modulus[i][j] + 1);
      }
    }
    double max = modulus[0][0], min = modulus[0][0];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (min > modulus[i][j]) min = modulus[i][j];
        if (max < modulus[i][j]) max = modulus[i][j];
      }
    }
    double interval = (double)255 / (max - min);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        modulus[i][j] = (modulus[i][j] - min) * interval;
      }
    }
    for (int  i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int gray = 255 << 24 | (int)modulus[i][j] << 16 |
        (int)modulus[i][j] << 8 | (int)modulus[i][j];
        img.setRGB(j, i, gray);
      }
    }
	  return img;
  }
  /*获取傅里叶矩阵*/
  public double[][][] ToDFTArray(int[][] spatialArray) {
    double[][][] temp = new double[height][width][2];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        double[] t = GetComplex(spatialArray, i, j);
        temp[i][j][0] = t[0];
        temp[i][j][1] = t[1];
      }
    }
    return temp;
  }
  /*逆变换求空间域矩阵*/
  public double[][] ToSpatialArray(double[][][] DFTArray) {
    double[][] temp = new double[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        double[] complex = GetSpatial(DFTArray, i, j);
        double n = complex[0];
        // double n = Math.sqrt(Math.pow(complex[0], 2) + Math.pow(complex[1], 2));
        temp[i][j] = n / (double)(height * width); //取实部
      }
    }
    return temp;
  }

  public double[] GetComplex(int[][] spatialArray, int x, int y) {
    double[] temp = new double[2];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        double a = (double)(x * i) / (double)height;
        double b = (double)(y * j) / (double)width;
        double c = -2 * Math.PI * (a + b);
        temp[0] += (Math.cos(c) * spatialArray[i][j]);
        temp[1] += (Math.sin(c) * spatialArray[i][j]);
      }
    }
    return temp;
  }

  public double[] GetSpatial(double[][][] DFTArray, int x, int y) {
    double[] temp = new double[2];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        double a = (double)(x * i) / (double)height;
        double b = (double)(y * j) / (double)width;
        double c = 2 * Math.PI * (a + b);
        temp[0] += (Math.cos(c) * DFTArray[i][j][0] - 
        Math.sin(c) * DFTArray[i][j][1]);
        temp[1] += Math.cos(c) * DFTArray[i][j][1] + 
        Math.sin(c) * DFTArray[i][j][0];
      }
    }
    return temp;
  }
  /*这里做中心化*/
  public int[][] BufferedImageToArray(BufferedImage img) {
    int[][] temp = new int[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if ((i + j) % 2 != 0) {
          temp[i][j] = -(img.getRGB(j, i) & 0xFF);
        } else {
          temp[i][j] = img.getRGB(j, i) & 0xFF;
        }
      }
    }
    return temp;
  }
  /*恢复中心化的灰度*/
  public BufferedImage ArrayToBufferedImage(double[][] DFTToSpatial) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
    	  int pixel;
        if ((i + j) % 2 != 0) {
          pixel = -(int)DFTToSpatial[i][j];
        } else {
          pixel = (int)DFTToSpatial[i][j];
        }
		    int gray = 255 << 24 | pixel << 16 | pixel << 8 | pixel;
        img.setRGB(j, i, gray);
      }
    }
    return img;
  }

  public void showImage(BufferedImage img, String s) throws IOException {
    ImageIO.write(img, "png", new FileOutputStream("img/" + s + ".png"));
  }
}
