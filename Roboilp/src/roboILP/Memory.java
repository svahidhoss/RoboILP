//
//	File:		Memory.java
//	Author:		Krzysztof Langner
//	Date:		1997/04/28
//
//      Modified by Alan Wai, 2008
//

package roboILP;
import java.util.*;
import java.io.*;


class Memory 
{

	//---------------------------------------------------------------------------
	// This constructor:
	// - initializes all variables
	public Memory(boolean bPerCycle)
	{
	    m_bStorePerCycle    = bPerCycle;
	    reset();
	}

	public void reset() {
	    m_objectsPerCycle	= new Vector(MAX_OBJECTS);
	    m_actionsPerCycle	= new Vector(MAX_ACTIONS);
	    m_actionsPerType	= new Vector();
	    System.gc();
	}

	public boolean isStorePerCycle() {
	    return m_bStorePerCycle;
	}

	//---------------------------------------------------------------------------
	// This function puts see information into our memory
	public void store(Vector visualObjects)
	{
	    if(m_objectsPerCycle.size() == MAX_OBJECTS) {
	        //
	        // pop the first (oldest) object 
	        //
	        m_objectsPerCycle.remove(0);
	    }

	    m_objectsPerCycle.add(visualObjects);
	}

	//
	// TODO: it makes more sense to store multiple types of action (only the latest
	//       action), rather than a queue of actions?
	//
	public void store(Action action)
	{
	  if(m_bStorePerCycle) {

	    RoboILP.println(3,"Memory::store (m_bStorePerCycle == 1) - storing action: " + action.getAction());
	    if(m_actionsPerCycle.size() == MAX_ACTIONS) {
	        // keep the history of the latest actions
	        m_actionsPerCycle.remove(0);
	    }
	    m_actionsPerCycle.add(action);

	  } else {		// store actions per type

	    if(m_actionsPerType.isEmpty()) {
	      m_actionsPerType.add(action);
	    } else {
	      // search through the vector to see if it's already there.  If so, remove it,
	      // since we are only storing the latest action of each type.
	      Iterator actionsIt = m_actionsPerType.iterator();

	      RoboILP.println(3,"Memory::store (m_bStorePerCycle == 0) - storing action: " + action.getAction());

	      while(actionsIt.hasNext()) {
	        Action a = (Action)actionsIt.next();
		RoboILP.println(3,"Memory::store (m_bStorePerCycle == 0) - existing action: " + a.getAction());
		if(a.getAction().equals(action.getAction())) {
		  RoboILP.println(3,"Memory::store (m_bStorePerCycle == 0) - removing action: " + a.getAction());
		  m_actionsPerType.remove(a);
		  break;
		}
	      }
	      m_actionsPerType.add(action);
	    }
	  }

	}


	public void removeAllObjects()
	{
	  m_objectsPerCycle.removeAllElements();
	}

	public void removeAllActions()
	{
	  m_actionsPerCycle.removeAllElements();
	}

	public void removeAll()
	{
	  m_objectsPerCycle.removeAllElements();
	  m_actionsPerCycle.removeAllElements();
	}

	public Vector getVisualObjects()
	{
	  if (!m_objectsPerCycle.isEmpty()) {
	    return m_objectsPerCycle;
	  } else {
	    return null;
	  }
	}


	public Vector getActions()
	{
	  if (m_bStorePerCycle) {
	    return (m_actionsPerCycle.isEmpty() ? null : m_actionsPerCycle);
	  } else {
	    return (m_actionsPerType.isEmpty() ? null : m_actionsPerType);
	  }
	}



//===========================================================================
// Private members
	volatile private String	m_info;		// place where all information is stored
	final static int SIMULATOR_STEP 	= 50;

	//
	// Indicates how many past simulation cycles will the memory store
	//
	final static int MAX_OBJECTS	 	= 100;
	final static int MAX_ACTIONS	 	= 1;

	private	Vector	m_objectsPerCycle	= null;
	private	Vector	m_actionsPerCycle	= null;
	private	Vector	m_actionsPerType	= null;
	private boolean m_bStorePerCycle	= true;
}

