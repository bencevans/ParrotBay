import java.awt.image.BufferedImage;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;

public class DroneTools {
	static private Socket socket_video_tcp = null;
	static private IContainer container = null;
	static private int videoStreamId = -1;
	static private IStreamCoder videoCoder = null;
	private static VideoImage mScreen = null;
	private static InetAddress inet_addr = null;
	private static DatagramSocket atsocket = null;
//	private static DatagramSocket ndsocket = null;
//	private static FloatBuffer fb;
//	private static IntBuffer ib;
//	private static ByteBuffer bb = ByteBuffer.allocate(4);

	public static void main(String args[]) throws Exception {
		byte[] ip_bytes = new byte[4];
		ip_bytes[0] = (byte) 192;
		ip_bytes[1] = (byte) 168;
		ip_bytes[2] = (byte) 1;
		ip_bytes[3] = (byte) 1;

		inet_addr = InetAddress.getByAddress(ip_bytes);
		atsocket = new DatagramSocket();
		//Comment here -0 front 1 bottom
		send_at_cmd("AT*CONFIG=605,\"video:video_channel\",\"0\"");
		openJavaWindow();
		BufferedImage im = CaptureDroneImage();
		updateJavaWindow(im);
		CloseDrone();
		atsocket.close();
	}

//	public static void zzzmain(String args[]) throws Exception {
//		fb = bb.asFloatBuffer();
//		ib = bb.asIntBuffer();
//		float speed = (float) 0.9;
//		int seq = 1;
//		byte[] ip_bytes = new byte[4];
//		ip_bytes[0] = (byte) 192;
//		ip_bytes[1] = (byte) 168;
//		ip_bytes[2] = (byte) 1;
//		ip_bytes[3] = (byte) 1;
//
//		inet_addr = InetAddress.getByAddress(ip_bytes);
//		atsocket = new DatagramSocket();
//		ndsocket = new DatagramSocket();
//		atsocket.setSoTimeout(3000);
//		ndsocket.setSoTimeout(3000);
//
//		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"");
//
//		// String action = "Takeoff";
//		String at_cmd = "AT*REF=" + (seq++) + ",290718208";
//		send_at_cmd(at_cmd);
//
//		Thread.sleep(5000);
//
//		// Switch on nav data
//		byte[] buffer = new byte[1024];
//		buffer[0] = 1;
//		buffer[1] = 0;
//		buffer[2] = 0;
//		buffer[3] = 0;
//		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
//				inet_addr, 5554);
//		ndsocket.send(packet);
//
//		// tell drone to start sending
//		send_at_cmd("AT*CTRL=0");
//		send_at_cmd("AT*CONFIG=605,\"general:navdata_demo\",\"FALSE\"");
//		send_at_cmd("AT*CONFIG=605,\"video:video_channel\",\"0\"");
//
//		ndsocket.receive(packet);
//		ByteBuffer bb = ByteBuffer.allocate(buffer.length);
//		bb.put(buffer);
//		// createFromData(bb,buffer.length);
//		// printState();
//		// Thread.sleep(2000);
//
//		openJavaWindow();
//		for (int i = 0; i < 50; ++i) {
//			BufferedImage im = CaptureDroneImage();
//			// if (im != null) updateJavaWindow(im);
//			if (im != null) {
//				System.out.println(i + " Got image");
//				updateJavaWindow(im);
//				String qr = QRDecoder(im);
//				System.out.println(qr);
//				dumpImageToFile("c:\\temp\\x.png", im);
//				if (qr.compareTo("QUIT") == 0)
//					i = 100;
//				if (qr.compareTo("RIGHT") == 0) {
//					at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,"
//							+ intOfFloat(speed);
//					send_at_cmd(at_cmd);
//				}
//				if (qr.compareTo("LEFT") == 0) {
//					at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,"
//							+ intOfFloat(-speed);
//					send_at_cmd(at_cmd);
//				}
//			} else {
//				System.out.println(i + " No image");
//			}
//			CloseDrone();
//			Thread.sleep(100);
//		}
//
//		// Landing
//		// action = "Landing";
//		at_cmd = "AT*REF=" + (seq++) + ",290717696";
//		send_at_cmd(at_cmd);
//
//		atsocket.close();
//		ndsocket.close();
//	}
//
//	public static int intOfFloat(float f) {
//		fb.put(0, f);
//		return ib.get(0);
//	}

//	public static void zzmain(String[] args) throws Exception {
//		openJavaWindow();
//		Scanner scan = new Scanner(System.in);
//		String x = "";
//		while (x.length() == 0) {
//			x = scan.nextLine();
//			// System.out.println(x+ " "+x.length());
//			BufferedImage im = CaptureDroneImage();
//			// if (im != null) updateJavaWindow(im);
//			if (im != null) {
//				System.out.println("Got image");
//				updateJavaWindow(im);
//				String tmpRetString = QRDecoder(im);
//				System.out.println(tmpRetString);
//				dumpImageToFile("c:\\temp\\x.png", im);
//			} else {
//				System.out.println("No image");
//			}
//		}
//		scan.close();
//		CloseDrone();
//	}

//	public static String QRDecoder(BufferedImage im) {
//		String decodedString = "";
//		QRCodeDecoder decoder = new QRCodeDecoder();
//		try {
//			decodedString = new String(decoder.decode(new J2SEImage(im)));
//		} catch (Exception E) {
//			decodedString = "";
//		}
//		return (decodedString);
//	}
//
//	public static void qmain(String[] args) throws Exception {
//		System.out.println("Here 1");
//		BufferedImage x = CaptureDroneImage();
//		System.out.println("Here 2");
//		openJavaWindow();
//		updateJavaWindow(x);
//		dumpImageToFile("c:\\temp\\temp.png", x);
//		Thread.sleep(2500);
//		closeJavaWindow();
//		CloseDrone();
//	}

	public static void CloseDrone() throws Exception {
		socket_video_tcp.close();
		if (videoCoder != null) {
			videoCoder.close();
			videoCoder = null;
		}
		if (container != null) {
			container.close();
			container = null;
		}
	}

	@SuppressWarnings("deprecation")
	static public BufferedImage CaptureDroneImage() throws Exception {
		socket_video_tcp = new Socket("192.168.1.1", 5555);
		// String source = "AR Drone";
		// Let's make sure that we can actually convert video pixel formats.
		if (!IVideoResampler
				.isSupported(IVideoResampler.Feature.FEATURE_COLORSPACECONVERSION)) {
			System.out
					.println("you must install the GPL version of Xuggler (with IVideoResampler support) for this program to work");
			System.exit(1);
		}
		// Create a Xuggler container object
		container = IContainer.make();
		// Open up the container
		if (socket_video_tcp.isClosed())
			socket_video_tcp = new Socket("192.168.1.1", 5555);
		if (container.open(socket_video_tcp.getInputStream(), null) < 0) {
			// throw new IllegalArgumentException("Cannot create stream");
			System.out.println("No socket");
			return (null);
		}
		// query how many streams the call to open found
		int numStreams = container.getNumStreams();
		// and iterate through the streams to find the first video stream
		for (int i = 0; i < numStreams; i++) {
			// Find the stream object
			IStream stream = container.getStream(i);
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder coder = stream.getStreamCoder();
			if (coder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO) {
				videoStreamId = i;
				videoCoder = coder;
				break;
			}
		}
		if (videoStreamId == -1) {
			// CloseDrone();
			System.out.println("could not find video stream in container");
			return (null);
		}
		// Now we have found the video stream in this file. Let's open up our
		// decoder so it can do work.
		if (videoCoder.open() < 0) {
			// CloseDrone();
			// throw new
			// RuntimeException("could not open video decoder for container: "+source);
			System.out.println("could not open video decoder for container");
			return (null);
		}
		IVideoResampler resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24) {
			// if this stream is not in BGR24, we're going to need to
			// convert it. The VideoResampler does that for us.
			resampler = IVideoResampler.make(videoCoder.getWidth(),
					videoCoder.getHeight(), IPixelFormat.Type.BGR24,
					videoCoder.getWidth(), videoCoder.getHeight(),
					videoCoder.getPixelType());
			if (resampler == null) {
				// CloseDrone();
				// throw new
				// RuntimeException("could not create color space resampler for: "
				// + source);
				System.out.println("could not create color space resampler");
				return (null);
			}
		}
		// And once we have that, we draw a window on screen
		// openJavaWindow();
		// Now, we start walking through the container looking at each packet.
		IPacket packet = IPacket.make();
		while (container.readNextPacket(packet) >= 0) {
			// Now we have a packet, let's see if it belongs to our video stream
			if (packet.getStreamIndex() == videoStreamId) {
				// We allocate a new picture to get the data out of Xuggler
				IVideoPicture picture = IVideoPicture.make(
						videoCoder.getPixelType(), videoCoder.getWidth(),
						videoCoder.getHeight());
				int offset = 0;
				while (offset < packet.getSize()) {
					// Now, we decode the video, checking for any errors.
					int bytesDecoded = videoCoder.decodeVideo(picture, packet,
							offset);
					if (bytesDecoded < 0) {
						// socket_video_tcp.close();
						// throw new
						// RuntimeException("got error decoding video in: " +
						// source);
						System.out.println("got error decoding video in");
						return (null);
					}
					offset += bytesDecoded;
					/*
					 * Some decoders will consume data in a packet, but will not
					 * be able to construct a full video picture yet. Therefore
					 * you should always check if you got a complete picture
					 * from the decoder
					 */
					if (picture.isComplete()) {
						IVideoPicture newPic = picture;
						/*
						 * If the resampler is not null, that means we didn't
						 * get the video in BGR24 format and need to convert it
						 * into BGR24 format.
						 */
						if (resampler != null) {
							// we must resample
							newPic = IVideoPicture.make(
									resampler.getOutputPixelFormat(),
									picture.getWidth(), picture.getHeight());
							if (resampler.resample(newPic, picture) < 0) {
								// CloseDrone();
								// throw new
								// RuntimeException("could not resample video from: "
								// + source);
								System.out
										.println("could not resample video from");
								return (null);
							}
						}
						if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
							// CloseDrone();
							// throw new
							// RuntimeException("could not decode video" +
							// " as BGR 24 bit data in: " + source);
							System.out.println("could not decode video"
									+ " as BGR 24 bit data in: ");
							return (null);
						}

						BufferedImage javaImage = Utils
								.videoPictureToImage(newPic);
						return (javaImage);
					}
				}
			}
		}
		System.out.println("No packet yet");
		return (null);
	}

	public static void updateJavaWindow(BufferedImage javaImage) {
		mScreen.setImage(javaImage);
	}

	public static void openJavaWindow() {
		mScreen = new VideoImage();
	}
//
//	private static void closeJavaWindow() {
//		System.exit(0);
//	}
//
//	public static String dumpImageToFile(String FileName, BufferedImage image) {
//		try {
//			String outputFilename = FileName;
//			ImageIO.write(image, "png", new File(outputFilename));
//			return outputFilename;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return (null);
//		}
//	}

	public static void send_at_cmd(String at_cmd) throws Exception {

		System.out.println("AT command: " + at_cmd);
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inet_addr, 5556);
		atsocket.send(packet);
		// sock.close();
		// socket.receive(packet); //AR.Drone does not send back ack message
		// (like "OK")
		// System.out.println(new
		// String(packet.getData(),0,packet.getLength()));
		Thread.sleep(250);
	}
}