import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;


public class ParrotBay {

	static int seq = 1;
	static float speed = (float) 0.1
			;
	static FloatBuffer fb;
	static IntBuffer ib;
	static InetAddress inet_addr;
	static DatagramSocket socket;

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

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

		forwards(2000);
		left(2000);
		backwards(2000); 
		right(2000);


		land();

		//		Takedown



	}

	static String action;
	static String at_cmd;



	public static void up() throws Exception {
		action = "Go Up (gaz+)";
		at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed)
				+ ",0";
		System.out.println(action);
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
	}

}
