import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRTools {
	public static void main(String[] args) {
		try {
			// GenerateQRCode(500,500,"LEFT","j:\\drone\\left.png","png");
			// GenerateQRCode(500,500,"RIGHT","j:\\drone\\right.png","png");
			// GenerateQRCode(500,500,"QUIT","j:\\drone\\quit.png","png");
			String tmpRetString = DecodeQRCode("c:\\temp\\qr.bmp");
			System.out.println(tmpRetString);
		} catch (Exception tmpExpt) {
			System.out.println("main: " + "Excpt err! (" + tmpExpt.getMessage()
					+ ")");
		}
		System.out.println("main: " + "Program end.");
	}

	public static BufferedImage GenerateQRCode(int width, int height,
			String content) throws Exception {
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,
				BarcodeFormat.QR_CODE, width, height);
		return (MatrixToImageWriter.toBufferedImage(bitMatrix));
	}

	public static void GenerateQRCode(int width, int height, String content,
			String filename, String imageFormat) throws Exception {
		File tmpFile = new File(filename);
		BitMatrix bitMatrix = new QRCodeWriter().encode(content,
				BarcodeFormat.QR_CODE, width, height);
		BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(tmpFile));
		MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, outputStream);
	}

	public static String DecodeQRCode(BufferedImage image) throws Exception {
		Map<DecodeHintType, Object> whatHints = new EnumMap<DecodeHintType, Object>(
				DecodeHintType.class);
		whatHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
		whatHints.put(DecodeHintType.POSSIBLE_FORMATS,
				EnumSet.allOf(BarcodeFormat.class));
		whatHints.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
		LuminanceSource tmpSource = new BufferedImageLuminanceSource(image);
		BinaryBitmap tmpBitmap = new BinaryBitmap(
				new HybridBinarizer(tmpSource));
		MultiFormatReader tmpBarcodeReader = new MultiFormatReader();
		com.google.zxing.Result tmpResult;
		String tmpFinalResult = "";
		try {
			if (whatHints != null && !whatHints.isEmpty()) {
				tmpResult = tmpBarcodeReader.decode(tmpBitmap, whatHints);
			} else {
				tmpResult = tmpBarcodeReader.decode(tmpBitmap);
			}
			// setting results.
			tmpFinalResult = String.valueOf(tmpResult.getText());
		} catch (Exception tmpExcpt) {
			System.out.println("BarCodeUtil.decode Excpt err - "
					+ tmpExcpt.toString() + " - " + tmpExcpt.getMessage());
		}
		return (tmpFinalResult);
	}

	public static String DecodeQRCode(String filename) throws Exception {
		File whatFile = new File(filename);
		// check the required parameters
		if (whatFile == null || whatFile.getName().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"File not found, or invalid file name.");
		}
		BufferedImage tmpBfrImage;
		try {
			tmpBfrImage = ImageIO.read(whatFile);
		} catch (IOException tmpIoe) {
			throw new Exception(tmpIoe.getMessage());
		}
		if (tmpBfrImage == null) {
			throw new IllegalArgumentException("Could not decode image.");
		}
		return (DecodeQRCode(tmpBfrImage));
	}
}