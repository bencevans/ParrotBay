package controller;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class DroneTest 
{
	private static InetAddress inet_addr = null; 
	private static DatagramSocket atsocket = null;
	private static DatagramSocket ndsocket = null;
	public static void qqmain(String args[]) throws Exception
	{
		ByteBuffer bb = ByteBuffer.allocate(4);
		
		bb.put((byte)0,(byte)255);
		bb.put((byte)1,(byte)255);
		bb.put((byte)2,(byte)8);
		bb.put((byte)3,(byte)0);
		System.out.println(bb.getShort(0));
		System.out.println(bb.getShort(2));
	}
	public static void qmain(String args[]) throws Exception
    {
    	Socket sock = new Socket("192.168.1.1", 5559);
    	
    	System.out.println("Here 1");
        InputStream is = sock.getInputStream();
        System.out.println("Here 2");
        int read;
        byte[] buffer = new byte[1024];
        while((read = is.read(buffer)) != -1) 
        {
            String output = new String(buffer, 0, read);
            System.out.print(output);
            System.out.flush();
        }
        sock.close();
    }
	public static void main(String args[]) throws Exception
	{
		
		int seq = 0;
		byte[] ip_bytes = new byte[4];
    	ip_bytes[0] = (byte)192;
    	ip_bytes[1] = (byte)168;
    	ip_bytes[2] = (byte)1;
    	ip_bytes[3] = (byte)1;
    	
        inet_addr = InetAddress.getByAddress(ip_bytes);
        atsocket = new DatagramSocket();
       	ndsocket = new DatagramSocket();
        atsocket.setSoTimeout(3000);
        ndsocket.setSoTimeout(3000);
		
        send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"");
	
		String action = "Takeoff";
		String at_cmd = "AT*REF=" + (seq++) + ",290718208";
		send_at_cmd(at_cmd);
		
		Thread.sleep(1500);
		
		action = "Landing";
		at_cmd = "AT*REF=" + (seq++) + ",290717696";
		send_at_cmd(at_cmd);
		
		atsocket.close();
    	ndsocket.close();
	}
	
	//AT - 5556
	//ND - 5554
	//Vid - 5555
	public static void qqqmain(String args[]) throws Exception
	{	
		byte[] ip_bytes = new byte[4];
    	ip_bytes[0] = (byte)192;
    	ip_bytes[1] = (byte)168;
    	ip_bytes[2] = (byte)1;
    	ip_bytes[3] = (byte)1;
    	
        inet_addr = InetAddress.getByAddress(ip_bytes);
        atsocket = new DatagramSocket();
       	ndsocket = new DatagramSocket();
        atsocket.setSoTimeout(3000);
        ndsocket.setSoTimeout(3000);
        
        byte[] buffer = new byte[1024];
        buffer[0] = 1;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = 0;
    	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5554);
    	ndsocket.send(packet);
    	Thread.sleep(500);
    	ndsocket.receive(packet);
    	
    	send_at_cmd("AT*CTRL=0");
    	send_at_cmd("AT*CONFIG=605,\"general:navdata_demo\",\"FALSE\"");
    	//send_at_cmd("AT*CTRL=0");
    	    	
    	ndsocket.receive(packet);
    	
    	ByteBuffer bb = ByteBuffer.allocate(buffer.length);
    	bb.put(buffer);
    	
    	//send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"",inet_addr,socket); //altitude max 2m
    	
    	//send_at_cmd("AT*CONFIG=\"general:navdata_demo\",\"TRUE\"",inet_addr);
    	//send_at_cmd("AT*CTRL=0",inet_addr,socket);
    	
    	createFromData(bb,buffer.length);
    	
    	//send_at_cmd("AT*CTRL=0",inet_addr);
    	
    	
    	//socket.receive(packet);
    	
    	//bb = ByteBuffer.allocate(buffer.length);
    	//bb.put(buffer);
    	
    	//send_at_cmd("AT*CONFIG=1,\"control:altitude_max\",\"2000\"",inet_addr,socket); //altitude max 2m
    	
    	//send_at_cmd("AT*CONFIG=\"general:navdata_demo\",\"TRUE\"",inet_addr);
    	//send_at_cmd("AT*CTRL=0",inet_addr,socket);
    	
    	//createFromData(bb,buffer.length);

    	
    	for(int i=0;i<1000;++i)
    	{
    		String x = String.format("%s %2x",GetNDField(i),buffer[i] & 255);
    		System.out.println(x);
    	}
    	atsocket.close();
    	ndsocket.close();
	}

	private static String GetNDField(int i) 
	{
		String fn = "";
		
		if (i == 0) fn = "Header";
		if (i == 1) fn = "Header";
		if (i == 2) fn = "Header";
		if (i == 3) fn = "Header";
		if (i == 4) fn = "State";
		if (i == 5) fn = "State";
		if (i == 6) fn = "State";
		if (i == 7) fn = "State";
		if (i == 8) fn = "SN";
		if (i == 9) fn = "SN";
		if (i == 10) fn = "SN";
		if (i == 11) fn = "SN";
		if (i == 12) fn = "VF";
		if (i == 13) fn = "VF";
		if (i == 14) fn = "VF";
		if (i == 15) fn = "VF";
		if (i == 16) fn = "Option";
		if (i == 17) fn = "Option";
		if (i == 18) fn = "Size";
		if (i == 19) fn = "Size";
		return(fn);
	}
	protected static Mode             mode;

    // state flags
    protected static boolean          flying;
    protected static boolean          videoEnabled;
    protected static boolean          visionEnabled;
    protected static ControlAlgorithm controlAlgorithm;
    protected static boolean          altitudeControlActive;
    protected static boolean          userFeedbackOn;
                                                             // name
    protected static boolean          controlReceived;
    protected static boolean          trimReceived;
    protected static boolean          trimRunning;
    protected static boolean          trimSucceeded;
    protected static boolean          navDataDemoOnly;
    protected static boolean          navDataBootstrap;
    protected static boolean          motorsDown;
    protected static boolean          gyrometersDown;
    protected static boolean          batteryTooLow;
    protected static boolean          batteryTooHigh;
    protected static boolean          timerElapsed;
    protected static boolean          notEnoughPower;
    protected static boolean          angelsOutOufRange;
    protected static boolean          tooMuchWind;
    protected static boolean          ultrasonicSensorDeaf;
    protected static boolean          cutoutSystemDetected;
    protected static boolean          PICVersionNumberOK;
    protected static boolean          ATCodedThreadOn;
    protected static boolean          navDataThreadOn;
    protected static boolean          videoThreadOn;
    protected static boolean          acquisitionThreadOn;
    protected static boolean          controlWatchdogDelayed;
    protected static boolean          ADCWatchdogDelayed;
    protected static boolean          communicationProblemOccurred;
    protected static boolean          emergency;

    // Common nav data
    protected static int              sequence;

    // Demo nav data
    protected static CtrlState        ctrl_state;
    protected static int              battery;
    protected static float            altitude;
    protected static float            pitch;
    protected static float            roll;
    protected static float            yaw;
    protected static float            vx;
    protected static float            vy;
    protected static float            vz;

    // Vision tags data
    protected static List<VisionTag>  vision_tags;

    public static float getAltitude()
    {
        return altitude;
    }

    public static int getBattery()
    {
        return battery;
    }

    public static ControlAlgorithm getControlAlgorithm()
    {
        return controlAlgorithm;
    }

    public static CtrlState getControlState()
    {
        return ctrl_state;
    }

    public static float getLongitude()
    {
        return vy;
    }

    public Mode getMode()
    {
        return mode;
    }

    public static float getPitch()
    {
        return pitch;
    }

    public static float getRoll()
    {
        return roll;
    }

    public int getSequence()
    {
        return sequence;
    }

    public static float getVx()
    {
        return vx;
    }

    public static float getVz()
    {
        return vz;
    }

    public static float getYaw()
    {
        return yaw;
    }

    public static boolean isAcquisitionThreadOn()
    {
        return acquisitionThreadOn;
    }

    public static boolean isADCWatchdogDelayed()
    {
        return ADCWatchdogDelayed;
    }

    public static boolean isAltitudeControlActive()
    {
        return altitudeControlActive;
    }

    public static boolean isAngelsOutOufRange()
    {
        return angelsOutOufRange;
    }

    public static boolean isATCodedThreadOn()
    {
        return ATCodedThreadOn;
    }

    public static boolean isBatteryTooHigh()
    {
        return batteryTooHigh;
    }

    public static boolean isBatteryTooLow()
    {
        return batteryTooLow;
    }

    public static boolean isCommunicationProblemOccurred()
    {
        return communicationProblemOccurred;
    }

    public boolean isControlReceived()
    {
        return controlReceived;
    }

    public static boolean isControlWatchdogDelayed()
    {
        return controlWatchdogDelayed;
    }

    public static boolean isCutoutSystemDetected()
    {
        return cutoutSystemDetected;
    }

    public static boolean isEmergency()
    {
        return emergency;
    }

    public static boolean isFlying()
    {
        return flying;
    }

    public static boolean isGyrometersDown()
    {
        return gyrometersDown;
    }

    public static boolean isMotorsDown()
    {
        return motorsDown;
    }

    public static boolean isNavDataBootstrap()
    {
        return navDataBootstrap;
    }

    public static boolean isNavDataDemoOnly()
    {
        return navDataDemoOnly;
    }

    public static boolean isNavDataThreadOn()
    {
        return navDataThreadOn;
    }

    public static boolean isNotEnoughPower()
    {
        return notEnoughPower;
    }

    public static boolean isPICVersionNumberOK()
    {
        return PICVersionNumberOK;
    }

    public static boolean isTimerElapsed()
    {
        return timerElapsed;
    }

    public static boolean isTooMuchWind()
    {
        return tooMuchWind;
    }

    public static boolean isTrimReceived()
    {
        return trimReceived;
    }

    public static boolean isTrimRunning()
    {
        return trimRunning;
    }

    public static boolean isTrimSucceeded()
    {
        return trimSucceeded;
    }

    public static boolean isUltrasonicSensorDeaf()
    {
        return ultrasonicSensorDeaf;
    }

    public static boolean isUserFeedbackOn()
    {
        return userFeedbackOn;
    }

    public static boolean isVideoEnabled()
    {
        return videoEnabled;
    }

    public static boolean isVideoThreadOn()
    {
        return videoThreadOn;
    }

    public static boolean isVisionEnabled()
    {
        return visionEnabled;
    }

    public FlyingState getFlyingState()
    {
        return FlyingState.fromControlState(ctrl_state);
    }

    public static List<VisionTag> getVisionTags()
    {
        return vision_tags;
    }

    public static void setVisionTags(List<VisionTag> vt)
    {
        vision_tags = vt;
    }

    public static void createFromData(ByteBuffer buf, int len)
    {
        
        if (ByteOrder.LITTLE_ENDIAN != buf.order()) {
            buf.order(ByteOrder.LITTLE_ENDIAN);
        }
        
        mode = Mode.BOOTSTRAP; // Assume we are in bootstrap

        //int offset = 0;

        int header = buf.getInt(0);
        
        if(header != 0x55667788)
        {
            System.out.println("Error parsing NavData");
            System.exit(1);
        }
       // offset += 4;

        int state = buf.getInt(4);
        //offset += 4;

        parseState(state);

        sequence = buf.getInt(8);
        //offset += 4;

        int vision_flag = buf.getInt(12);
        System.out.println(vision_flag);
        //offset += 4;
        
        printState();
        //System.exit(1);
        
        int offset = 16;

        // Read options
        System.out.println(buf.get(offset));
        System.out.println(buf.get(offset+1));
        System.out.println(buf.get(offset+2));
        System.out.println(buf.get(offset+3));
        while(offset < len)
        {
            int option_tag = buf.getShort(offset);
            offset += 2;
            int option_len = buf.getShort(offset);
            offset += 2;

            if(option_len == 0)
            {
                System.out.println("Zero-len option with tag " + option_tag);
                //System.exit(1);
                return;
            }

            // log.fine("At offset " + (offset - 4) + " found option " +
            // option_tag + " with len=" + option_len);

            if(option_tag == NavDataTag.NAVDATA_DEMO_TAG.getValue())
            {
                parseDemoNavData(buf, offset);
                mode = Mode.DEMO;
            } else if(option_tag == NavDataTag.NAVDATA_CKS_TAG.getValue())
            {
                // this is last tag. We do not unpack it yet, but we gracefully
                // exit if it has been encountered.
                break;
            } else if(option_tag == NavDataTag.NAVDATA_VISION_DETECT_TAG.getValue())
            {
                List<VisionTag> vtags = parseVisionTags(buf, offset);
                if(vtags != null)
                    setVisionTags(vtags);
            } else
            {
                //log.warning("Skipping unknown NavData option with tag=" + option_tag);
            }
            offset = offset + option_len - 4;
        }

    }
    private static List<VisionTag> parseVisionTags(ByteBuffer buf, int offset)
    {
        int nb_detected = buf.getInt(offset);
        offset += 4;

        assert (nb_detected > 0);
        List<VisionTag> res = new ArrayList<VisionTag>(nb_detected);
        for(int i = 0; i < nb_detected; i++)
        {
            int type = buf.getInt(offset + 4 * i);
            int xc = buf.getInt(offset + 4 * i + 1 * 4 * 4);
            int yc = buf.getInt(offset + 4 * i + 2 * 4 * 4);
            int width = buf.getInt(offset + 4 * i + 3 * 4 * 4);
            int height = buf.getInt(offset + 4 * i + 4 * 4 * 4);
            int dist = buf.getInt(offset + 4 * i + 5 * 4 * 4);

            VisionTag vt = new VisionTag(VisionTag.VisionTagType.fromInt(type), new Point(xc, yc), new Dimension(width, height),dist);
            res.add(vt);
        }

        return res;
    }

    private static void parseDemoNavData(ByteBuffer buf, int offset)
    {
        ctrl_state = CtrlState.fromInt(buf.getInt(offset) >> 16);
        offset += 4;
        battery = buf.getInt(offset);
        offset += 4;
        pitch = buf.getFloat(offset) / 1000;
        offset += 4;
        roll = buf.getFloat(offset) / 1000;
        offset += 4;
        yaw = buf.getFloat(offset) / 1000;
        offset += 4;
        altitude = ((float) buf.getInt(offset)) / 1000;
        offset += 4;
        vx = buf.getFloat(offset);
        offset += 4;
        vy = buf.getFloat(offset);
        offset += 4;
        vz = buf.getFloat(offset);
        offset += 4;
    }

    private static void parseState(int state)
    {
        flying = (state & 1) != 0;
        videoEnabled = (state & (1 << 1)) != 0;
        visionEnabled = (state & (1 << 2)) != 0;
        controlAlgorithm = (state & (1 << 3)) != 0 ? ControlAlgorithm.ANGULAR_SPEED_CONTROL : ControlAlgorithm.EULER_ANGELS_CONTROL;
        altitudeControlActive = (state & (1 << 4)) != 0;
        userFeedbackOn = (state & (1 << 5)) != 0;
        controlReceived = (state & (1 << 6)) != 0;
        trimReceived = (state & (1 << 7)) != 0;
        trimRunning = (state & (1 << 8)) != 0;
        trimSucceeded = (state & (1 << 9)) != 0;
        navDataDemoOnly = (state & (1 << 10)) != 0;
        navDataBootstrap = (state & (1 << 11)) != 0;
        motorsDown = (state & (1 << 12)) != 0;
        //ARDRONE_COM_LOST_MASK       = 1U << 13, /*!< Communication Lost : (1) com problem, (0) Com is ok */
        gyrometersDown = (state & (1 << 14)) != 0;
        batteryTooLow = (state & (1 << 15)) != 0;
        batteryTooHigh = (state & (1 << 16)) != 0;
        timerElapsed = (state & (1 << 17)) != 0;
        notEnoughPower = (state & (1 << 18)) != 0;
        angelsOutOufRange = (state & (1 << 19)) != 0;
        tooMuchWind = (state & (1 << 20)) != 0;
        ultrasonicSensorDeaf = (state & (1 << 21)) != 0;
        cutoutSystemDetected = (state & (1 << 22)) != 0;
        PICVersionNumberOK = (state & (1 << 23)) != 0;
        ATCodedThreadOn = (state & (1 << 24)) != 0;
        navDataThreadOn = (state & (1 << 25)) != 0;
        videoThreadOn = (state & (1 << 26)) != 0;
        acquisitionThreadOn = (state & (1 << 27)) != 0;
        controlWatchdogDelayed = (state & (1 << 28)) != 0;
        ADCWatchdogDelayed = (state & (1 << 29)) != 0;
        communicationProblemOccurred = (state & (1 << 30)) != 0;
        emergency = (state & (1 << 31)) != 0;
    }

    public static void printState()
    {
        StringBuffer sb = new StringBuffer();

        sb.append("IsFlying: " + isFlying() + "\n");
        sb.append("IsVideoEnabled: " + isVideoEnabled() + "\n");
        sb.append("IsVisionEnabled: " + isVisionEnabled() + "\n");
        sb.append("controlAlgo: " + getControlAlgorithm() + "\n");
        sb.append("AltitudeControlActive: " + isAltitudeControlActive() + "\n");
        sb.append("IsUserFeedbackOn: " + isUserFeedbackOn() + "\n");
        sb.append("ControlReceived: " + isVideoEnabled() + "\n");
        sb.append("IsTrimReceived: " + isTrimReceived() + "\n");
        sb.append("IsTrimRunning: " + isTrimRunning() + "\n");
        sb.append("IsTrimSucceeded: " + isTrimSucceeded() + "\n");
        sb.append("IsNavthisDemoOnly: " + isNavDataDemoOnly() + "\n");
        sb.append("IsNavthisBootstrap: " + isNavDataBootstrap() + "\n");
        sb.append("IsMotorsDown: " + isMotorsDown() + "\n");
        sb.append("IsGyrometersDown: " + isGyrometersDown() + "\n");
        sb.append("IsBatteryLow: " + isBatteryTooLow() + "\n");
        sb.append("IsBatteryHigh: " + isBatteryTooHigh() + "\n");
        sb.append("IsTimerElapsed: " + isTimerElapsed() + "\n");
        sb.append("isNotEnoughPower: " + isNotEnoughPower() + "\n");
        sb.append("isAngelsOutOufRange: " + isAngelsOutOufRange() + "\n");
        sb.append("isTooMuchWind: " + isTooMuchWind() + "\n");
        sb.append("isUltrasonicSensorDeaf: " + isUltrasonicSensorDeaf() + "\n");
        sb.append("isCutoutSystemDetected: " + isCutoutSystemDetected() + "\n");
        sb.append("isPICVersionNumberOK: " + isPICVersionNumberOK() + "\n");
        sb.append("isATCodedThreadOn: " + isATCodedThreadOn() + "\n");
        sb.append("isNavthisThreadOn: " + isNavDataThreadOn() + "\n");
        sb.append("isVideoThreadOn: " + isVideoThreadOn() + "\n");
        sb.append("isAcquisitionThreadOn: " + isAcquisitionThreadOn() + "\n");
        sb.append("isControlWatchdogDelayed: " + isControlWatchdogDelayed() + "\n");
        sb.append("isADCWatchdogDelayed: " + isADCWatchdogDelayed() + "\n");
        sb.append("isCommunicationProblemOccurred: " + isCommunicationProblemOccurred() + "\n");
        sb.append("IsEmergency: " + isEmergency() + "\n");
        sb.append("CtrlState: " + getControlState() + "\n");
        sb.append("Battery: " + getBattery() + "\n");
        sb.append("Altidtude: " + getAltitude() + "\n");
        sb.append("Pitch: " + getPitch() + "\n");
        sb.append("Roll: " + getRoll() + "\n");
        sb.append("Yaw: " + getYaw() + "\n");
        sb.append("X velocity: " + getVx() + "\n");
        sb.append("Y velocity: " + getLongitude() + "\n");
        sb.append("Z velocity: " + getVz() + "\n");
        sb.append("Vision Tags: " + getVisionTags() + "\n");
        System.out.println(sb);
    }
    
    public static void send_at_cmd(String at_cmd) throws Exception 
    {
 
    	System.out.println("AT command: " + at_cmd);    	
    	byte[] buffer = (at_cmd + "\r").getBytes();
    	DatagramPacket packet = new DatagramPacket(buffer, buffer.length, inet_addr, 5556);
    	atsocket.send(packet);
    	//sock.close();
    	//socket.receive(packet); //AR.Drone does not send back ack message (like "OK")
    	//System.out.println(new String(packet.getData(),0,packet.getLength()));   	
    }
}