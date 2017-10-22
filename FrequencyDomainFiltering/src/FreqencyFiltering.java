public class FreqencyFiltering {
  // public FreqencyFiltering(int n, double[][][] fourierArray) {}

  public double[][][] getAfterFilter(int n, double[][][] fourierArray) {
    int height = fourierArray.length;
    int width = fourierArray[0].length;
    double[][][] filter = getMeanFilter(n, height, width);
    double[][][] afterFilter = filtering(fourierArray, filter);
    return afterFilter;
  }

  public double[][][] filtering(double[][][] fourierArray, double[][][] filter) {
    int height = fourierArray.length;
    int width = fourierArray[0].length;
    double[][][] afterFilter = new double[height][width][2];

    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        afterFilter[i][j][0] = fourierArray[i][j][0] * filter[i][j][0]
        - fourierArray[i][j][1] * filter[i][j][1];
        afterFilter[i][j][1] = fourierArray[i][j][0] * filter[i][j][1]
        + fourierArray[i][j][1] * filter[i][j][0];
      }
    }
    return afterFilter;
  }

  public double[][][] getMeanFilter(int n, int height ,int width) {
    double[][][] temp = new double[height][width][2];
    for (int i = 0; i < height; i++) { 
      for (int j = 0; j < width; j++) {
        if (i < n && j < n) {
          if ((i + j) % 2 != 0) {
            temp[i][j][0] = -(1.0d / 49d);
          } else {
            temp[i][j][0] = 1.0d / 49d;
          }
        }
        System.out.print((double)temp[i][j][0] + " ");
      }
    }
    temp = FastFourierTransform.fft_2d(temp);
	  return temp;
  }
}
