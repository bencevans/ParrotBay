/**
 * @author   Parrotbay Donald 
 * @version  0.1
 * @since    2015-05-19
 */
public class QRcoords 
{
	//obvious enough
	private double xcoord, ycoord, zcoord, cam;
 
	QRcoords(double x, double y, double z, double c)
	{ 
	  xcoord = x; 
	  ycoord = y;
	  zcoord = z;
	  cam = c;
	} 
	public double GetX()
	{ 
		return(xcoord); 
	} 
	public double GetY()
	{ 
		return(ycoord); 
	}
	public double GetZ()
	{ 
		return(zcoord); 
	}
	public double GetC()
	{ 
		return(cam); 
	}
}