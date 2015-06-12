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

import javax.swing.JOptionPane;


public class ParrotBay {

	static int seq = 1;
	static float speed = (float) 0.4; //speed
	static FloatBuffer fb;
	static IntBuffer ib;
	static InetAddress inet_addr;
	static DatagramSocket socket;

	public static void main(String[] args) throws Exception {

		//STEP 1: READ FILE & STEP 2: FIND BEST ROUTE
		QRcoords[] bestroute = convert2diff(nearestneighbourTSP(filereader()));

		//STEP 3: CONNECT TO DRONE...
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
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"3000\"");
		if (args.length == 2) 
		{ // Command line mode
			send_at_cmd(args[1]);
			System.exit(0);
		}

		//STEP 4: MOVE AND READ QR
		System.out.println("Take Off");
		takeoff();
//		DisplayCameraImage.findQR(1); //This will display the camera and then reads qr code
		move(bestroute);
		land();
	}

	static String action;
	static String at_cmd;


	public static void up(int timeToRun) throws Exception {
		action = "Go Up (gaz+)";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed)
				+ ",0";
//		System.out.println(action);
		send_at_cmd_time(at_cmd, timeToRun);
	}

	public static void down(int timeToRun) throws Exception {
		action = "Go Up (gaz+)";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed)
				+ ",0";
//		System.out.println(action);
		send_at_cmd_time(at_cmd, timeToRun);
	}

	public static void hover() throws Exception {
		action = "Hovering";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,0";
		send_at_cmd(at_cmd);
//		System.out.println(action);
		Thread.sleep(2000);
	}

	public static void right(int timeToRun) throws Exception {
		String action = "Go right";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed)
				+ ",0,0,0";
		send_at_cmd_time(at_cmd, timeToRun);
//		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void left(int timeToRun) throws Exception {
		String action = "Go left";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed)
				+ ",0,0,0";
		send_at_cmd_time(at_cmd, timeToRun);
//		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void forwards(int timeToRun) throws Exception {
		String action = "Go forwards";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed)
				+ ",0,0";
		send_at_cmd_time(at_cmd, timeToRun);
//		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void backwards(int timeToRun) throws Exception {
		String action = "Go backwards";
		String at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed)
				+ ",0,0";
		send_at_cmd_time(at_cmd, timeToRun);
//		System.out.println(action);
		System.out.println("seq:" + seq++);
	}

	public static void takeoff() throws Exception {
		String action = "Takeoff";
		String at_cmd = "AT*REF=" + (seq++) + ",290718208";
		send_at_cmd(at_cmd);
//		System.out.println(action);
		System.out.println("seq:" + seq++);
		Thread.sleep(4000); // Takes ~4 seconds to take off
	}

	public static void land() throws Exception {
		String action = "Landing";
		String at_cmd = "AT*REF=" + (seq++) + ",290717696";
		send_at_cmd(at_cmd);
//		System.out.println(action);
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

	}

	/**
	 * STEP 1: READ FILE
	 * @return  an arraylist of QRcoords
	 */

	public static ArrayList<QRcoords> filereader() throws IOException  
	{
		Scanner coordReader;
		ArrayList<QRcoords> List = new ArrayList<QRcoords>();
		File readH = new File("C:\\Users\\user\\Desktop\\Test Data\\TestData.txt");
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
	
	//STEP 2B: convert bestroute to differences
	public static QRcoords[] convert2diff(QRcoords[] before){
		QRcoords[] after = new QRcoords[before.length];
		after[0] = before[0];
		for (int i = 1; i < before.length; i++){
			double x, y, z, c;
			c = before[i].getC();
			x = before[i].getX() - before[i-1].getX();
			y = before[i].getY() - before[i-1].getY();
			z = before[i].getZ() - before[i-1].getZ();
			after[i] = new QRcoords(x,y,z,c);			
		}
		return after;
	}
	/**
	 * STEP 4: COMPUTE MOVEMENTS AND MOVE 
	 * @param route	an array of the QR coords as read from file input
	 * @return  a QRcoords[] that has the coords ordered as the shortest route
	 * @throws Exception 
	 */
	public static void move(QRcoords[] route) throws Exception{	
		double xnext = 0;
		double ynext = 0;
		double znext = 0;
		double cnext = 0;
		//using this simple conversion for now: 1 second = 2.6 meters

		for (int i = 0; i < route.length; i++){
			xnext = route[i].getX();
			ynext = route[i].getY();
			znext = route[i].getZ();
			cnext = route[i].getC();
			//actual moving logic
			if (cnext == 1){ // bottom camera
				//no z movement (+0.5)
				if (xnext <= 0){
					// move left
					System.out.println("Moving Left");
					left((int)(xnext/2.6*1000));
				}
				else if (xnext > 0){
					// move right
					System.out.println("Moving Right");
					right((int)(xnext/2.6*1000));
				}
				if (ynext <= 0){
					// move backward
					System.out.println("Moving back");
					backwards((int)(ynext/2.6*1000));
				}
				else if (ynext > 0){
					// move forward
					System.out.println("Moving Forwards");
					forwards((int)(ynext/2.6*1000));
				}
				DisplayCameraImage.findQR(1); //This will display the camera and then reads qr code
			}
			else if (cnext == 0){ //front camera
				if (znext >= 0){
					//move up
					System.out.println("Moving up");
					up((int)(znext/2.6*1000));
				}
				else if (znext < 0){
					//move down
					System.out.println("Moving down");
					down((int)(znext/2.6*1000));
				}
				if (xnext <= 0){
					// move left
					System.out.println("Moving left");
					left((int)(xnext/2.6*1000));
				}
				else if (xnext > 0){
					// move right
					System.out.println("Moving right");
					right((int)(xnext/2.6*1000));
				}
				if (ynext <= 0){
					// move backward
					System.out.println("Moving backwards");
					backwards((int)(ynext/2.6*1000));
				}
				else if (ynext > 0){
					// move forward
					System.out.println("Moving forwards");
					forwards((int)(ynext/2.6*1000));
				}
				DisplayCameraImage.findQR(0); //This will display the camera and then reads qr code
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
