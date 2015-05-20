/**
 * @author   Parrotbay Donald 
 * @version  0.2
 * @since    2015-05-19
 */
public class QRcoords {
	//obvious enough
	private double xcoord, ycoord, zcoord, cam;
 
	QRcoords(double x, double y, double z, double c){ 
	  xcoord = x; 
	  ycoord = y;
	  zcoord = z;
	  cam = c;
	} 
	public double getX(){ 
		return(xcoord); 
	} 
	public double getY(){ 
		return(ycoord); 
	}
	public double getZ(){ 
		return(zcoord); 
	}
	public double getC(){ 
		return(cam); 
	}
}
