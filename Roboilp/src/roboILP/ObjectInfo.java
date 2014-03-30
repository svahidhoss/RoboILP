package roboILP;

import java.io.Serializable;

//
//	File:			ObjectInfo.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28

//  Modified by:  Paul Marlow, Amir Ghavam, Yoga Selvaraj
//  Course:       Software Agents
//  Date Due:     November 30, 2000

//  Modified again by Kevin Lam
//                 October 19, 2004
//                 added SceneTable col/row attributes

//***************************************************************************
//
//	This is base class for different classese with visual information
//	about objects
//
//***************************************************************************
abstract class ObjectInfo implements Serializable
{
  public int    m_time		= 0;
  public int    m_id		= 0;
  public String m_type;
  public double m_distance	= 0;
  public double m_direction	= 0;
  public double m_distChange	= 0;
  public double m_dirChange	= 0;

  public boolean m_isDistValid	= false;
  public boolean m_isDirValid	= false;
  public boolean m_isDistChgValid	= false;
  public boolean m_isDirChgValid	= false;
  public boolean m_inViewConeP	= false;
  
  public int m_tableRow;
  public int m_tableCol;

  public double m_absX		= -1000;
  public double m_absY		= -1000;

  protected String m_instName	= "";

  //===========================================================================
  // Initialization member functions
  public ObjectInfo(String type)
  {
    m_type = type;
  }

  public double getDistance()
  {
    return m_distance;
  }

  public double getDirection()
  {
    return m_direction;
  }

  public double getDistChange()
  {
    return m_distChange;
  }

  public double getDirChange()
  {
    return m_dirChange;
  }

  public String getInstName()
  {
    return m_instName;
  }

  public int getId()
  {
    // Error code is 0
    return (m_inViewConeP ? m_id : 0);
  }

  public String getType()
  {
    return m_type;
  }

  public double getAbsX() {
    return m_absX;
  }

  public double getAbsY() {
    return m_absY;
  }

  public boolean isInViewConeP()
  {
    return m_inViewConeP;
  }

  public int getTime()
  {
    return m_time;
  }

  public boolean isDistanceVld() {
    return m_isDistValid;
  }

  public boolean isDirectionVld() {
    return m_isDirValid;
  }

  public boolean isDistChgVld() {
    return m_isDistChgValid;
  }

  public boolean isDirChgVld() {
    return m_isDirChgValid;
  }

  public void setInstName(String name) {
    m_instName = name;
  }
  
  public void setDistance(double dist) {
    m_isDistValid = true;
    m_distance = dist;
  }
  
  public void setDirection(double dir) {
    m_isDirValid = true;
    m_direction = dir;
  }
  
  public void setDistChg(double distChg) {
    m_isDistChgValid = true;
    m_distChange = distChg;
  }
  
  public void setDirChg(double dirChg) {
    m_isDirChgValid = true;
    m_dirChange = dirChg;
  }
  
  public void setAbsPosition(double x, double y) {
    m_absX = x;
    m_absY = y;
  }

  public void print()
  {
    System.out.println("Object: "+m_type+"("+m_distance+","+m_direction+"),("+m_distChange+","+m_dirChange+") at cycle: "+m_time);
  }

  abstract public String getKey();

/**
 * @return
 */
public int getTableColumn()
{
	return m_tableCol;
}

/**
 * @return
 */
public int getTableRow()
{
	return m_tableRow;
}

/**
 * @param i
 */
public void setTableColumn(int i)
{
	m_tableCol = i;
}

/**
 * @param i
 */
public void setTableRow(int i)
{
	m_tableRow = i;
}

}

