import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class ParrotBay {

	public static void main(String[] args) throws Exception {
		
		// TODO Auto-generated method stub
		//STEP 1: READ FILE & STEP 2: FIND BEST ROUTE
		nearestneighbourTSP(filereader());
		//STEP 3: CALCULATE ROUTE ACTUAL MOVEMENTS
		
		//STEP 4: MOVE AND QR READ CONFIRMS
		System.out.println("Take Off");
		
		String qrValue = DisplayCameraImage.findQR(1);
		System.out.println(qrValue);

		//STEP 5: QUIT
	}

	/**
	 * STEP 1: READ FILE
	 * @return  an arraylist of QRcoords
	 */

	public static ArrayList<QRcoords> filereader() throws IOException  
	{
		Scanner coordReader;
		ArrayList<QRcoords> List = new ArrayList<QRcoords>();
		
		File readH = new File("E:\\xyzCoord.txt");
		coordReader = new Scanner(new FileInputStream(readH));
		
		double[] arr = new double[4];
		
		while(coordReader.hasNext()){
			for (int i = 0; i < 4; i++){
				String coord_nocomma = coordReader.next().replaceAll(",","");
				arr[i] = Double.parseDouble(coord_nocomma);
			}
			QRcoords set = new QRcoords(arr[0], arr[1], arr[2], arr[3]);		
			List.add(set);
		}
		System.out.println(List);
		return List;
	}
	
	/**
	 * STEP 2: FIND BEST ROUTE
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
	 * MISC: A generic factorial function
	 */
	public static int factorial(int n) {
		int result = 1; 
		for (int i = 1; i <= n; i++) {
			result *= i;
		}
		return result;
	}

	/**
	 * MISC: A generic function to get min value position from an array
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
