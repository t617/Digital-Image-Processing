import javax.imageio.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class HistogramAndSpatialImage{
  public BufferedImage image;

  public HistogramAndSpatialImage() throws IOException {
    image = ImageIO.read(new File("img/78.png"));
    histogramProcessing();
    spatialFiltering();
  }
  
  public void histogramProcessing() throws IOException {
    new HistogramProcessing(image);
  }
  
  public void smoothingFiltering() throws IOException {
    int[][] smoothingFilterArray3 = getSmoothingArray(3);
    new SpatialFiltering(image, smoothingFilterArray3, "SmoothingFiltering3");
    int[][] smoothingFilterArray7 = getSmoothingArray(7);
    new SpatialFiltering(image, smoothingFilterArray7, "SmoothingFiltering7");
    int[][] smoothingFilterArray11 = getSmoothingArray(11);
    new SpatialFiltering(image, smoothingFilterArray11, "SmoothingFiltering11");
  }

  public void laplacianFiltering() throws IOException {
    new SpatialFiltering(image, getLaplacianArray(), "LaplacianFiltering");
  }

  public void highboostFiltering() throws IOException {
    new SpatialFiltering(image, getSmoothingArray(3), 2);
  }

  public int[][] getSmoothingArray(int length) {
    int[][] temp = new int[length][length];
    for (int i = 0; i < length; i++) {
      for (int j = 0; j < length; j++) {
        temp[i][j] = 1;
      }
    }
    return temp;
  }

  public int[][] getLaplacianArray() {
    int[][] temp = {{0, 1, 0}, {1, -4, 1}, {0, 1, 0}};
    return temp;
  }

  public void spatialFiltering() throws IOException {
    smoothingFiltering();
    laplacianFiltering();
    highboostFiltering();
  }

  public static void main(String[] args) throws IOException {
    new HistogramAndSpatialImage();
	}
}