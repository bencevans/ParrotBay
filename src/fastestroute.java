import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author   ParrotBay Donald 
 * @version  0.3
 * @since    2015-05-19
 */
public class fastestroute{
	public static void main(String[] args) {
		//just creating a random coords arraylist to test the algorithm
		QRcoords one = new QRcoords (10, 5.5, 7, 1);
		QRcoords two = new QRcoords (11, -3.5, 8, 1);
		QRcoords three = new QRcoords (-12, 5.5, 6, 0);
		QRcoords four = new QRcoords (13, 4.5, 9, 0);
		QRcoords five = new QRcoords (14, 5, 5.5, 1);

		ArrayList<QRcoords> QRs = new ArrayList<QRcoords>();
		QRs.add(one);
		QRs.add(two);
		QRs.add(three);
		QRs.add(four);
		QRs.add(five);

		System.out.println(QRs);
		nearestneighbourTSP(QRs);
	}

	/**
	 * @param L	an arraylist of the QR coords as read from file input
	 * @return  a QRcoords[] that has the coords ordered as the shortest route
	 */
	public static QRcoords[] nearestneighbourTSP(ArrayList<QRcoords> L){
		//total number of QR codes
		int listsize = L.size();
		System.out.println(listsize);
		//array to store route output
		QRcoords[] route = new QRcoords[listsize];
		//variables for calculations
		double xsqdiff, ysqdiff, zsqdiff;

		//starting: find the closest point to 0, 0, 0
		double startdist[] = new double[listsize];
		for(int i = listsize - 1; i >= 0; i--){
			QRcoords temp = L.get(i);
			//pythagorean calculations for distance between two QRs
			xsqdiff = Math.pow((temp.getX() - 0), 2);
			ysqdiff = Math.pow((temp.getY() - 0), 2);
			zsqdiff = Math.pow((temp.getZ() - 0), 2);
			startdist[i] = Math.sqrt(xsqdiff + ysqdiff + zsqdiff);
		}
		route[0] = L.get(getMin(startdist));
		L.remove(getMin(startdist));
		listsize = L.size();
		System.out.println(L);
		
		//loop: find the nearest neighbour to visit
		for(int i = 0; listsize > 1; i++){
			double nextdist[] = new double[listsize];
			for(int j = 0; j < listsize; j++){
				QRcoords temp = L.get(j);
				//pythagorean calculations for distance between two QRs
				xsqdiff = Math.pow((route[i].getX() - temp.getX()), 2);
				ysqdiff = Math.pow((route[i].getY() - temp.getY()), 2);
				zsqdiff = Math.pow((route[i].getZ() - temp.getZ()), 2);
				nextdist[j] = Math.sqrt(xsqdiff + ysqdiff + zsqdiff);
			}
			route[i+1] = L.get(getMin(nextdist));
			L.remove(getMin(nextdist));
			listsize = L.size();
			System.out.println(L);
		}
		
		//ending: put the last coords last
		route[route.length-1] = L.get(0);
		
		System.out.println(Arrays.toString(route));
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
