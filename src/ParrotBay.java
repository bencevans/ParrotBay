import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class ParrotBay {

	static int seq = 1;
	static float speed = (float) 0.6
			;
	static FloatBuffer fb;
	static IntBuffer ib;
	static InetAddress inet_addr;
	static DatagramSocket socket;

	public static void main(String[] args) throws Exception {
// 1 second = 3.9 meters

		//STEP 1: READ FILE & STEP 2: FIND BEST ROUTE
//		nearestneighbourTSP(filereader());

		//STEP 3: CALCULATE ROUTE ACTUAL MOVEMENTS
		// take off
		System.out.println("Take Off");

		//		String qrValue = DisplayCameraImage.findQR(1);
		//		System.out.println(qrValue);

		//		Connect ...

		String ip = "192.168.1.1";

		if (args.length >= 1) {
			ip = args[0];
		}

		StringTokenizer st = new StringTokenizer(ip, ".");

		byte[] ip_bytes = new byte[4];
		if (st.countTokens() == 4) {
			for (int i = 0; i < 4; i++) {
				ip_bytes[i] = (byte) Integer.parseInt(st.nextToken());
			}
		} else {
			System.out.println("Incorrect IP address format: " + ip);
			System.exit(-1);
		}

		System.out.println("IP: " + ip);
		System.out.println("Speed: " + speed);


		ByteBuffer bb = ByteBuffer.allocate(4);
		fb = bb.asFloatBuffer();
		ib = bb.asIntBuffer();

		inet_addr = InetAddress.getByAddress(ip_bytes);
		socket = new DatagramSocket();
		socket.setSoTimeout(3000);

		/* altitude max 2m */
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"5000\"");
		if (args.length == 2) { // Command line mode
			send_at_cmd(args[1]);
			System.exit(0);
		}

		//		Takeoff
		takeoff();

		up(500);
		down(500);

		land();

		//		Takedown



	}

	static String action;
	static String at_cmd;



	public static void up(int timeToRun) throws Exception {
		action = "Go Up (gaz+)";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed)
				+ ",0";
		System.out.println(action);
		send_at_cmd_time(at_cmd, timeToRun);
	}
	
	public static void down(int timeToRun) throws Exception {
		action = "Go Up (gaz+)";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed)
				+ ",0";
		System.out.println(action);
		send_at_cmd_time(at_cmd, timeToRun);
	}

	public static void hover() throws Exception {
		action = "Hovering";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,0";
		send_at_cmd(at_cmd);
		System.out.println(action);
		Thread.sleep(2000);
	}


	public static void right(int timeToRun) throws Exception {
		String action = "Go right";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed)
				+ ",0,0,0";
		send_at_cmd_time(at_cmd, timeToRun);
		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void left(int timeToRun) throws Exception {
		String action = "Go left";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed)
				+ ",0,0,0";
		send_at_cmd_time(at_cmd, timeToRun);
		System.out.println(action);
		System.out.println("seq:" + seq++);
	}
	
	public static void forwards(int timeToRun) throws Exception {
		String action = "Go forwards";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed)
				+ ",0,0";
		send_at_cmd_time(at_cmd, timeToRun);
		System.out.println(action);
		System.out.println("seq:" + seq++);
	}
	
	public static void backwards(int timeToRun) throws Exception {
		String action = "Go backwards";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed)
				+ ",0,0";
		send_at_cmd_time(at_cmd, timeToRun);
		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void takeoff() throws Exception {
		String action = "Takeoff";
		String at_cmd = "AT*REF=" + (seq++) + ",290718208";
		send_at_cmd(at_cmd);
		System.out.println(action);
		System.out.println("seq:" + seq++);
		Thread.sleep(4000); // Takes ~4 seconds to take off
	}

	public static void land() throws Exception {
		String action = "Landing";
		String at_cmd = "AT*REF=" + (seq++) + ",290717696";
		send_at_cmd(at_cmd);
		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);
	}
	
	public static void send_at_cmd_time(String at_cmd, long timeToRun) throws Exception {
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() < startTime + timeToRun) {
			send_at_cmd(at_cmd);
		}
		hover();
	}

	public static void send_at_cmd(String at_cmd) throws Exception {
		System.out.println("AT command: " + at_cmd);
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inet_addr, 5556);
		socket.send(packet);

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

		//loop: find the nearest neighbour to visit
		for(int i = 0; listsize > 1; i++){
			double nextdist[] = new double[listsize];
			for(int j = 0; j < listsize; j++){
				QRcoords temp = L.get(j);
				//pythagorean calculations for distance between two QRs
				if (i == 0){
					xsqdiff = Math.pow((temp.getX() - 0), 2);
					ysqdiff = Math.pow((temp.getY() - 0), 2);
					zsqdiff = Math.pow((temp.getZ() - 0), 2);
				}
				else{
					xsqdiff = Math.pow((route[i-1].getX() - temp.getX()), 2);
					ysqdiff = Math.pow((route[i-1].getY() - temp.getY()), 2);
					zsqdiff = Math.pow((route[i-1].getZ() - temp.getZ()), 2);
				}
				nextdist[j] = Math.sqrt(xsqdiff + ysqdiff + zsqdiff);
			}
			route[i] = L.get(getMin(nextdist));
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
		double xprev = 0;
		double yprev = 0;
		double zprev = 0;
		double cprev = 0;
		
		for (int i = 0; i < route.length; i++){
			if (i > 0){
			xprev = route[i-1].getX();
			yprev = route[i-1].getY();
			zprev = route[i-1].getZ();
			cprev = route[i-1].getC();
			}
			double xnext = route[i].getX();
			double ynext = route[i].getY();
			double znext = route[i].getZ();
			double cnext = route[i].getC();
			if (cnext == 1){
				//no z movement (+0.88)
				if (xnext < 0){
					xnext = xnext - xprev;
					// move left
				}
				else if (xnext > 0){
					xnext = xnext - xprev;
					// move right
				}
				if (ynext < 0){
					ynext = ynext - yprev;
					// move backward
				}
				else if (ynext > 0){
					ynext = ynext - yprev;
					// move forward
				}
			}
			else if (cnext == 0){
				if (znext > zprev){
					//move z by znext = znext - zprev -0.88
				}
				else if (zprev > znext){
					//move z by znext = znext - zprev +0.88
				}
				if (xnext < 0){
					xnext = xnext - xprev;
					// move left
				}
				else if (xnext > 0){
					xnext = xnext - xprev;
					// move right
				}
				if (ynext < 0){
					ynext = ynext - yprev;
					// move backward
				}
				else if (ynext > 0){
					ynext = ynext - yprev;
					// move forward
				}
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
