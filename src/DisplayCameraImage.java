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

public class DisplayCameraImage {
	static private Socket socket_video_tcp = null;
	static private IContainer container = null;
	static private int videoStreamId = -1;
	static private IStreamCoder videoCoder = null;
	static private VideoImage mScreen = null;
	static private InetAddress inet_addr = null;
	static private DatagramSocket atsocket = null;

	public static void main(String args[]) {
		byte[] ip_bytes = new byte[4];
		ip_bytes[0] = (byte) 192;
		ip_bytes[1] = (byte) 168;
		ip_bytes[2] = (byte) 1;
		ip_bytes[3] = (byte) 1;

		try {
			inet_addr = InetAddress.getByAddress(ip_bytes);
			atsocket = new DatagramSocket();
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
								System.out
										.println("could not resample video from");
								return (null);
							}
						}
						if (newPic.getPixelType() != IPixelFormat.Type.BGR24) {
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

	public static void send_at_cmd(String at_cmd) throws Exception {

		System.out.println("AT command: " + at_cmd);
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inet_addr, 5556);
		atsocket.send(packet);
		Thread.sleep(250);
	}
}