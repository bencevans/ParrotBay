import java.net.DatagramSocket;
import java.net.InetAddress;


public class ParrotBay {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("Take Off");
		

		int seq = 1;
		byte[] ip_bytes = new byte[4];
		ip_bytes[0] = (byte) 192;
		ip_bytes[1] = (byte) 168;
		ip_bytes[2] = (byte) 1;
		ip_bytes[3] = (byte) 1;

		InetAddress inet_addr = InetAddress.getByAddress(ip_bytes);
		DatagramSocket atsocket = new DatagramSocket();
		DatagramSocket ndsocket = new DatagramSocket();
		atsocket.setSoTimeout(3000);
		ndsocket.setSoTimeout(3000);

		DroneTools.send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"");

		String action = "Takeoff";
		String at_cmd = "AT*REF=" + (seq++) + ",290718208";
		DroneTools.send_at_cmd(at_cmd);

		Thread.sleep(5000);
		
		String qrValue = DisplayCameraImage.findQR(1);
		System.out.println(qrValue);

	}

}
