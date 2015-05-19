import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;
/** JDK 7+. */
public final class CoordReader 
{
	private static Scanner coordReader;

	public static void main(String args[]) throws FileNotFoundException 
	{
		double[] arr = new double[4];
		
		File readH = new File("C:\\Users\\user\\Desktop\\xyzCoord.txt");
		coordReader = new Scanner(new FileInputStream(readH));
		int arrcount=0; 
		while(coordReader.hasNext())
		{
			String coord_nocomma = coordReader.next().replaceAll(",","");
			arr[arrcount] = Double.parseDouble(coord_nocomma);
			System.out.println("Co-ordinate " + (arrcount + 1) + ": " + arr[arrcount]);
			arrcount++;
		}
	}
} 