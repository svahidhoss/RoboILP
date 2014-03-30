/*
 * Created on Mar 1, 2004
 *
 * 03/29/2004: this class is ready to go
 * 
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package roboILP;


/**
 * @author kevlam
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Action extends Object
{

	private int	m_time;
	private String actionStr;
	private boolean bParam1Valid;
	private boolean bParam2Valid;
	private double param1;
	private double param2;
	private String param1QualStr;
	private String param2QualStr;
	private double action_x;
	private double action_y;

	/**
	 * 
	 */
	public Action()
	{
		actionStr = new String("");
		m_time = 0;
		param2 = 0;
		param1 = 0;
		bParam1Valid = false;
		bParam2Valid = false;
		action_x = 0;
		action_y = 0;
		param1QualStr = null;
		param2QualStr = null;
	}

	/**
	 * 
	 */
	public Action(Action other)
	{
		actionStr = other.getAction();
		m_time = other.getTime();
		param2 = other.getParam2();
		param1 = other.getParam1();
		bParam1Valid = other.isParam1Valid();
		bParam2Valid = other.isParam2Valid();
		action_x = other.getActionX();
		action_y = other.getActionY();
		param1QualStr = other.getParam1Qual();
		param2QualStr = other.getParam2Qual();
	}

	/**
	 * 
	 */
	public Action(String act)
	{
		actionStr = act;
		m_time = 0;
		param2 = 0;
		param1 = 0;
		bParam1Valid = false;
		bParam2Valid = false;
		action_x = 0;
		action_y = 0;
		param1QualStr = null;
		param2QualStr = null;
	}

	// could introduce a set of contextually-sensitive constructors
	
	/**
	 * @return
	 */
	public String getAction()
	{
		return actionStr;
	}

	public int getTime()
	{
		return m_time;
	}

	/**
	 * @return
	 */
	public double getParam2()
	{
		return param2;
	}

	/**
	 * @return
	 */
	public double getParam1()
	{
		return param1;
	}

	public String getParam1Qual()
	{
		return param1QualStr;
	}

	public String getParam2Qual()
	{
		return param2QualStr;
	}

	public boolean isParam1Valid()
	{
		return bParam1Valid;
	}

	public boolean isParam2Valid()
	{
		return bParam2Valid;
	}

	/**
	 * @return
	 */
	public double getActionX()
	{
		return action_x;
	}

	/**
	 * @return
	 */
	public double getActionY()
	{
		return action_y;
	}

	/**
	 * @param i
	 */
	public void setAction(String str)
	{
		actionStr = str;
	}

	/**
	 * @param f
	 */
	public void setParam2(double d)
	{
		bParam2Valid	= true;
		param2 		= d;
	}

	public void setParam2Qual(String str)
	{
		param2QualStr	= str;
	}

	/**
	 * @param f
	 */
	public void setParam1(double d)
	{
		bParam1Valid	= true;
		param1 		= d;
	}

	public void setParam1Qual(String str)
	{
		param1QualStr	= str;
	}

	/**
	 * @param f
	 */
	public void setActionX(double f)
	{
		action_x = f;
	}

	/**
	 * @param f
	 */
	public void setActionY(double f)
	{
		action_y = f;
	}
	
	public void setTime(int time)
	{
		m_time = time;
	}

	public boolean equals(Action other)
	{
		if ((actionStr.equals(other.getAction())) && 
		    (param2 == other.getParam2()) && (param1 == other.getParam1()) && 
		    (action_x == other.getActionX()) && (action_y == other.getActionY()) )
		{
			return true;
		}
		else 
		{
			return false;
		}
	}
	 
	public String toString()
	{
		return actionStr;
	}

	public void print()
	{
	    System.out.println(actionStr+" ("+param1+","+param2+") at cycle "+m_time);
	}

}
