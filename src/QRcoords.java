/**
 * @author   Parrotbay Donald 
 * @version  0.1
 * @since    2015-05-19
 */
public class QRcoords {
	//obvious enough
	private int xcoord, ycoord, zcoord, cam;
 
	QRcoords(int x, int y, int z, int c){ 
	  xcoord = x; 
	  ycoord = y;
	  zcoord = z;
	  cam = c;
	} 
	public int GetX(){ 
		return(xcoord); 
	} 
	public int GetY(){ 
		return(ycoord); 
	}
	public int GetZ(){ 
		return(zcoord); 
	}
	public int GetC(){ 
		return(cam); 
	}
}
