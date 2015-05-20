import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class ParrotBay {

	public static void main(String[] args) throws Exception {

		//STEP 1: READ FILE & STEP 2: FIND BEST ROUTE
		nearestneighbourTSP(filereader());

		//STEP 3: CALCULATE ROUTE ACTUAL MOVEMENTS
		// take off
		System.out.println("Take Off");
		
		String qrValue = DisplayCameraImage.findQR(1);
		System.out.println(qrValue);

		//STEP 4: QUIT
	}

	/**
	 * STEP 1: READ FILE
	 * @return  an arraylist of QRcoords
	 */

	public static ArrayList<QRcoords> filereader() throws IOException  
	{
		Scanner coordReader;
		ArrayList<QRcoords> List = new ArrayList<QRcoords>();
		File readH = new File("E:\\DroneTestData.txt");
		coordReader = new Scanner(new FileInputStream(readH));
		
		String[] lineelements = new String[4];
		while(coordReader.hasNext()){
			String line = coordReader.nextLine();
			lineelements = line.split(",");
			QRcoords set = new QRcoords(Double.parseDouble(lineelements[0]),
					Double.parseDouble(lineelements[1]),
					Double.parseDouble(lineelements[2]),
					Double.parseDouble(lineelements[3]));		
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
	 * STEP 3: COMPUTE ALL MOVEMENTS
	 * @param L	an arraylist of the QR coords as read from file input
	 * @return  a QRcoords[] that has the coords ordered as the shortest route
	 */
	public static void movecalculations(QRcoords[] route){	
		//going to the next point
		for (int i = 0; i < route.length; ){
			double x0 = route[0].getX();
			double y0 = route[0].getY();
			double z0 = route[0].getZ();
			double c0 = route[0].getC();
			if (c0 == 1){
				//no z movement (+0.88)
				if (x0 < 0){
					// move left
				}
				else if (x0 > 0){
					// move right
				}
				if (y0 < 0){
					// move backward
				}
				else if (y0 > 0){
					// move forward
				}
			}
			else if (c0 == 0){
				//TODO
			}
		}
		
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
