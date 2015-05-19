import java.awt.image.BufferedImage;


public class CameraHandler {
	
	public static void main(String args[]) throws Exception {
		
		boolean QRNotFound = true;
		int cameraSelection = 1;
		
		while(QRNotFound) {
			
			System.out.println("Capturing Image");
			BufferedImage image = captureImage(cameraSelection);
			
			String qrValue = QRTools.DecodeQRCode(image);
			
			
			// check for QR
		}
	}
	
	public static BufferedImage captureImage(int camera) throws Exception {
		// TODO: accept camera int to change camera. ATM camera 1 is always used
		return DroneTools.CaptureDroneImage();
	}
	
	public String scanForQR(BufferedImage img) {
		return "";
	}
}
