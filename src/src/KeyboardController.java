/*
Author: MAPGPS on
	https://projects.ardrone.org/projects/ardrone-api/boards
	http://bbs.5imx.com/bbs/forumdisplay.php?fid=453
Initial: 2010.09.20
Updated: 2010.09.26

UI_BIT:
00010001010101000000000000000000
   |   | | | |        || | ||||+--0: Button turn to left
   |   | | | |        || | |||+---1: Button altitude down (ah - ab)
   |   | | | |        || | ||+----2: Button turn to right
   |   | | | |        || | |+-----3: Button altitude up (ah - ab)
   |   | | | |        || | +------4: Button - z-axis (r1 - l1)
   |   | | | |        || +--------6: Button + z-axis (r1 - l1)
   |   | | | |        |+----------8: Button emergency reset all
   |   | | | |        +-----------9: Button Takeoff / Landing
   |   | | | +-------------------18: y-axis trim +1 (Trim increase at +/- 1??/s)
   |   | | +---------------------20: x-axis trim +1 (Trim increase at +/- 1??/s)
   |   | +-----------------------22: z-axis trim +1 (Trim increase at +/- 1??/s)
   |   +-------------------------24: x-axis +1
   +-----------------------------28: y-axis +1

AT*REF=<sequence>,<UI>
AT*PCMD=<sequence>,<enable>,<pitch>,<roll>,<gaz>,<yaw>
	(float)0.05 = (int)1028443341		(float)-0.05 = (int)-1119040307
	(float)0.1  = (int)1036831949		(float)-0.1  = (int)-1110651699
	(float)0.2  = (int)1045220557		(float)-0.2  = (int)-1102263091
	(float)0.5  = (int)1056964608		(float)-0.5  = (int)-1090519040
AT*ANIM=<sequence>,<animation>,<duration>
AT*CONFIG=<sequence>,\"<name>\",\"<value>\"

########## Commandline mode ############
Usage: java ARDrone <IP> <AT command>

altitude max2m:	java ARDrone 192.168.1.1 AT*CONFIG=1,\"control:altitude_max\",\"2000\"
Takeoff:	java ARDrone 192.168.1.1 AT*REF=101,290718208
Landing:	java ARDrone 192.168.1.1 AT*REF=102,290717696
Hovering:	java ARDrone 192.168.1.1 AT*PCMD=201,1,0,0,0,0
gaz 0.1:	java ARDrone 192.168.1.1 AT*PCMD=301,1,0,0,1036831949,0
gaz -0.1:	java ARDrone 192.168.1.1 AT*PCMD=302,1,0,0,-1110651699,0
pitch 0.1:	java ARDrone 192.168.1.1 AT*PCMD=303,1,1036831949,0,0,0
pitch -0.1:	java ARDrone 192.168.1.1 AT*PCMD=304,1,-1110651699,0,0,0
yaw 0.1:	java ARDrone 192.168.1.1 AT*PCMD=305,1,0,0,0,1036831949
yaw -0.1:	java ARDrone 192.168.1.1 AT*PCMD=306,1,0,0,0,-1110651699
roll 0.1:	java ARDrone 192.168.1.1 AT*PCMD=307,1,0,1036831949,0,0
roll -0.1:	java ARDrone 192.168.1.1 AT*PCMD=308,1,0,-1110651699,0,0
pitch -30 deg:	java ARDrone 192.168.1.1 AT*ANIM=401,0,1000
pitch 30 deg:	java ARDrone 192.168.1.1 AT*ANIM=402,1,1000

########## Keyboad mode ############
Usage: java ARDrone [IP]

PgUp key:     Takeoff
PgDn key:     Landing
SpaceBar key: Hovering

Arrow keys:
        Go Forward
            ^
            |
Go Left <---+---> Go Right
            |
            v
       Go Backward

Arrow keys with Shift key pressed:
              Go Up
                ^
                |
Rotate Left <---+---> Rotate Right
                |
                v
             Go Down
             
Digital keys 1~9 + Arrow keys or Digital keys 1~9 + Shift + Arrow keys:
Change speed, 1 is min and 9 is max, release the Digital key will reset to default speed (10%).
 */

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.StringTokenizer;

class KeyboardController extends Frame implements KeyListener {
	static final long serialVersionUID = 1L;
	InetAddress inet_addr;
	DatagramSocket socket;
	/* Send AT command with sequence number 1 will reset the counter */
	int seq = 1;
	float speed = (float) 0.1;
	boolean shift = false;
	FloatBuffer fb;
	IntBuffer ib;

	public KeyboardController(String name, String args[]) throws Exception {
		super(name);

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
		send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"");
		if (args.length == 2) { // Command line mode
			send_at_cmd(args[1]);
			System.exit(0);
		}

		addKeyListener(this);
		setSize(320, 160);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	public static void main(String args[]) {
		try {
			new KeyboardController("ARDrone", args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void keyTyped(KeyEvent e) {
		;
	}

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		System.out.println("Key: " + keyCode + " ("
				+ KeyEvent.getKeyText(keyCode) + ")");

		try {
			control(keyCode);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode >= 49 && keyCode <= 57)
			speed = (float) 0.1; // Reset speed
		if (keyCode == 16)
			shift = false; // Shift off
	}

	public int intOfFloat(float f) {
		fb.put(0, f);
		return ib.get(0);
	}

	public void send_at_cmd(String at_cmd) throws Exception {
		System.out.println("AT command: " + at_cmd);
		byte[] buffer = (at_cmd + "\r").getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				inet_addr, 5556);
		socket.send(packet);
	}

	// Control AR.Drone via AT commands per key code
	public void control(int keyCode) throws Exception {
		String at_cmd = "";
		String action = "";

		switch (keyCode) {
		case 49: // 1
			speed = (float) 0.05;
			break;
		case 50: // 2
			speed = (float) 0.1;
			break;
		case 51: // 3
			speed = (float) 0.15;
			break;
		case 52: // 4
			speed = (float) 0.25;
			break;
		case 53: // 5
			speed = (float) 0.35;
			break;
		case 54: // 6
			speed = (float) 0.45;
			break;
		case 55: // 7
			speed = (float) 0.6;
			break;
		case 56: // 8
			speed = (float) 0.8;
			break;
		case 57: // 9
			speed = (float) 0.99;
			break;
		case 16: // Shift
			shift = true;
			break;
		case 38: // Up
			if (shift) {
				action = "Go Up (gaz+)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(speed)
						+ ",0";
			} else {
				action = "Go Forward (pitch+)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(speed)
						+ ",0,0,0";
			}
			break;
		case 40: // Down
			if (shift) {
				action = "Go Down (gaz-)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0," + intOfFloat(-speed)
						+ ",0";
			} else {
				action = "Go Backward (pitch-)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1," + intOfFloat(-speed)
						+ ",0,0,0";
			}
			break;
		case 37: // Left
			if (shift) {
				action = "Rotate Left (yaw-)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,"
						+ intOfFloat(-speed);
			} else {
				action = "Go Left (roll-)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(-speed)
						+ ",0,0";
			}
			break;
		case 39: // Right
			if (shift) {
				action = "Rotate Right (yaw+)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0," + intOfFloat(speed);
			} else {
				action = "Go Right (roll+)";
				at_cmd = "AT*PCMD=" + (seq++) + ",1,0," + intOfFloat(speed)
						+ ",0,0";
			}
			break;
		case 32: // SpaceBar
			action = "Hovering";
			at_cmd = "AT*PCMD=" + (seq++) + ",1,0,0,0,0";
			break;
		case 33: // PageUp
			action = "Takeoff";
			at_cmd = "AT*REF=" + (seq++) + ",290718208";
			break;
		case 34: // PageDown
			action = "Landing";
			at_cmd = "AT*REF=" + (seq++) + ",290717696";
			break;
		default:
			break;
		}

		System.out.println("Speed: " + speed);
		System.out.println("Action: " + action);
		send_at_cmd(at_cmd);
	}
}