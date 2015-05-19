import java.awt.image.BufferedImage;

import jp.sourceforge.qrcode.data.QRCodeImage;

class J2SEImage implements QRCodeImage {
  BufferedImage image;

	public J2SEImage(BufferedImage source) {
    this.image = source;
	}

	public int getWidth() {
		return image.getWidth();
	}
	
	public int getHeight() {
    return image.getHeight();
	}

	public int getPixel(int x, int y) {
    return image.getRGB(x ,y);

	}
}