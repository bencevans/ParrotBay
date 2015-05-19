import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author   ParrotBay Donald 
 * @version  0.2
 * @since    2015-05-19
 */
public class fastestroute{
	public static void main(String[] args) {
		//just creating a random coords arraylist to test the algorithm
		QRcoords one = new QRcoords (10, 5.5, 7, 1);
		QRcoords two = new QRcoords (11, -3.5, 8, 1);
		QRcoords three = new QRcoords (-12, 5.5, 6, 0);
		QRcoords four = new QRcoords (13, 4.5, 9, 0);

		ArrayList<QRcoords> QRs = new ArrayList<QRcoords>(4);
		QRs.add(one);
		QRs.add(two);
		QRs.add(three);
		QRs.add(four);

		bruteTSP(QRs);
	}

	/**
	 * @return  a QRcoords[] that has the coords ordered as the shortest route
	 */
	public static QRcoords[] bruteTSP(ArrayList<QRcoords> L){
		//total number of QR codes
		int listsize = L.size();
		System.out.println(listsize);
		//array to store number of distances between two QRs
		int chooses2 = factorial(listsize) / (2* factorial(listsize - 2));
		System.out.println(chooses2);
		//array to store route output
		QRcoords[] route = new QRcoords[chooses2];

		//finding a point to start with
		double startdist[] = new double[listsize];
		for(int i = listsize - 1; i >= 0; i--){
			QRcoords temp = L.get(i);
			//pythagorean calculations for distance between two QRs
			double xsqdiff = Math.pow((temp.GetX() - 0), 2);
			double ysqdiff = Math.pow((temp.GetX() - 0), 2);
			double zsqdiff = Math.pow((temp.GetX() - 0), 2);
			startdist[i] = Math.sqrt(xsqdiff + ysqdiff + zsqdiff);
		}
//		System.out.println(Arrays.toString(startdist));
//		System.out.println(getMin(startdist));
		route[0] = L.get(getMin(startdist));
		
		//TODO
		
		return route;		

	}

	/**
	 * A generic factorial function
	 */
	public static int factorial(int n) {
		int result = 1; 
		for (int i = 1; i <= n; i++) {
			result *= i;
		}
		return result;
	}

	/**
	 * A generic function to get min value position from an array
	 */
	public static int getMin(double[] inputArray){ 
		double minValue = inputArray[0];
		int minVpos = 0;
		for(int i=1;i<inputArray.length;i++){ 
			if(inputArray[i] < minValue){ 
				minValue = inputArray[i];
				minVpos = i;
			} 
		} 
		return minVpos; 
	} 
}
