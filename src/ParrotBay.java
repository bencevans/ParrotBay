
public class ParrotBay {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		System.out.println("Take Off");
		
		String qrValue = DisplayCameraImage.findQR(1);
		System.out.println(qrValue);

	}

}
