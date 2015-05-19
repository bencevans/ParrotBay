import java.awt.image.BufferedImage;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class QRReader {

	byte[] ip_bytes = new byte[4];
	ip_bytes[0] = (byte) 192;
	ip_bytes[1] = (byte) 168;
	ip_bytes[2] = (byte) 1;
	ip_bytes[3] = (byte) 1;

	try {
		InetAddress inet_addr = InetAddress.getByAddress(ip_bytes);
		DatagramSocket atsocket = new DatagramSocket();
		/*
		 * Camera Selection: 0 = front, 1 = bottom
		 */
		int cameraSelection = 0;
		send_at_cmd("AT*CONFIG=605,\"video:video_channel\",\""
				+ cameraSelection + "\"");
		openJavaWindow();
		BufferedImage im = CaptureDroneImage();
		updateJavaWindow(im);
		CloseDrone();
		atsocket.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

}
