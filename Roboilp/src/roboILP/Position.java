/*
 * 
 * By:		Alan Wai
 * Date:	September 24, 2008
 *
 */
package roboILP;

import java.util.*;


public class Position 
{
  private double x;
  private double y;

  public Position() {
	x = 0;
	y = 0;
  }

  public Position(double _x, double _y) {
	x = _x;
	y = _y;
  }

  public double getX() { return x; }
  public double getY() { return y; }

  public void set(double _x, double _y) {
	x = _x;
	y = _y;
  }

}
