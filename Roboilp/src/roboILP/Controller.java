//
//	File:		Controller.java
//	Author:		Alan Wai
//	Date:		2008/09/26

//
//  incorporated multiple object vector lists
//  removed StringTokenizer global and made class serializable
//

package roboILP;
import java.util.*;
import java.io.*;


abstract class Controller
{

      //
      // Note:
      // If the application requires more sensor types, it needs to be added here.
      // pre_process and post_process routines will need to be extended as well as
      // new routines in Brain.
      //

      protected static final int SENSORTYPE_INIT	= 0;
      protected static final int SENSORTYPE_SEE 	= 1;
      protected static final int SENSORTYPE_HEAR	= 2;
      protected static final int SENSORTYPE_SENSEBODY	= 3;
      protected static final int SENSORTYPE_ERROR	= 4;

      // Note: this sensortype is only used in train and test mode only.
      protected static final int SENSORTYPE_ACTION	= 5;


      protected String 		m_message;
      protected ICmdParser	m_parser;
      protected IBrain		m_brain;
      protected Memory		m_memory;
      protected int		m_mode;

      protected int		m_currentCycle;
      protected boolean		m_visionSeen;
      protected boolean		m_bActionPreCondition;	// whether pre_process for SEE is allowed
      protected boolean		m_bFirstAction;		// very first see/action detected
      							// still allowed the very first see/action
							// pair to be captured in measurement mode
      protected Action		m_lastAction;
      protected String		m_lastMessage;
      protected int		m_sameMessageCnt = 0;

      //
      // Sensory Objects
      //
      protected Vector		m_VisualObjects;	// Vector of ObjectInfo
      protected Action		m_action;		// Action associated with the test scene

      protected String		m_model;		// Model resulted from current scene
      protected boolean		m_bModelValid;		// whether m_model is valid or not


	public Controller(int mode, IBrain brainRef, Memory memoryRef)
	{
	    m_brain		= brainRef;
	    m_memory		= memoryRef;
	    m_mode		= mode;
	    m_lastMessage	= "";
	    reset();
	}

	public void reset()
	{
	    m_action		= null;
	    m_visionSeen	= false;
	    m_currentCycle	= 0;
	    m_model		= new String();
	    m_bActionPreCondition	= false;
	    m_bFirstAction	= true;
	    m_lastAction	= null;
	    m_bModelValid	= false;
	}


	//---------------------------------------------------------------------------
	// Execute()
	// 
	// This method is the overall execution flow of each cycle.
	// (1) parse():
	//	calls upon the application-specific parse routine to determine the 
	//	type of incoming sensory information .
	// (2) pre_process():
	//	calls the generic CmdParser parse routines (implemented by 
	//      application-specific CmdParser) based on the sensory type found by (1).
	// (3) process():
	//	calls upon any application-specific routines to convert the objects 
	//	found in (2) into FOL model (suitable for that sensory type).
	// (4) post_process():
	//	calls upon m_brain routines based on the sensory type.
	//

	public void execute(String message) {
	    int sensorytype;

	    m_message	= message;

//	    System.out.println("Controller::execute() message: " + m_message);

	    sensorytype	= determineSensoryType();
//	    System.out.println("Controller::execute() sensorytype: " + sensorytype);

	    if(sensorytype == SENSORTYPE_ERROR) return;

	    pre_process(sensorytype);
	    process(sensorytype);
	    post_process(sensorytype);
	}


	//---------------------------------------------------------------------------
	// This function parses messages from the server
	// This method must be implemented by the subclass since these messages
	// are application-dependent.
	//


	public void pre_process(int sensorytype)
	{

	  try {
	    switch(sensorytype) {
	      case Controller.SENSORTYPE_SEE: {

		//
	        // remove all memory of seen objects - stateless machine
		// In other words, only objects seen in the current cycle 
		// are relevant.
		//
	        m_memory.removeAllObjects();
		m_visionSeen = false;
		m_model = new String();

		//
		// In both Run & Measurement Mode, don't allow the agent to see if
		// it's not allowed to perform an action.  This has been confirmed
		// from the Krislet log files (TODO: what about other players??)
		//

		if(m_bActionPreCondition || m_bFirstAction) {
	          RoboILP.println(2,"Controller::pre_process(): sensorytype==SEE");
//	        System.out.println("Controller::execute() message: " + m_message);

		  m_VisualObjects	= new Vector();
	          RoboILP.println(2,"Controller::pre_process(): message" + m_message);
	          m_parser.parseSeeCommand(m_message, this);

		  if(!m_VisualObjects.isEmpty()) {
		    Enumeration e 	= m_VisualObjects.elements();
		    m_currentCycle	= ((ObjectInfo)e.nextElement()).getTime();
		    m_visionSeen	= true;

		    // store objects into Memory
		    m_memory.store(m_VisualObjects);
		  }
		}
	        break;
	      }
	      case Controller.SENSORTYPE_HEAR: {
	        RoboILP.println(2,"Controller::pre_process(): sensorytype==HEAR");

	        m_parser.parseHearCommand(m_message, this);
	        break;
	      }
	      case Controller.SENSORTYPE_SENSEBODY: {
	        RoboILP.println(2,"Controller::pre_process(): sensorytype==SENSEBODY");
	        m_parser.parseSenseBodyCommand(m_message, this);
	        break;
	      }
	      case Controller.SENSORTYPE_ACTION: {

		// remove all past actions 
	        m_memory.removeAllActions();

	        if((m_bActionPreCondition || m_bFirstAction) && m_visionSeen) {
	          RoboILP.println(2,"Controller::pre_process(): sensorytype==ACTION");
	          m_parser.parseActionCommand(m_message, this);

		  m_action.setTime(m_currentCycle);

		  // store action into memory
		  m_memory.store(m_action);
		}
	        break;
	      }
	      case Controller.SENSORTYPE_INIT: {
	        RoboILP.println(2,"Controller::pre_process(): sensorytype==INIT");
	        m_parser.parseInitCommand(m_message, this);
	        break;
	      }
	    }
	  }
	  catch(ParseException e) {
	    e.printStackTrace();
	  }
	}

	public void process(int sensorytype)
	{
	    switch(sensorytype) {
	      case Controller.SENSORTYPE_SEE: {

		//
		// In Measurement Mode, wait for the associated action to arrive
		// In Run Mode, don't bother converting to model if it is not allowed
		// to perform action.
		//
		if (m_bActionPreCondition && (m_mode == RoboILP.MODE_RUN)) {
	          RoboILP.println(2,"Controller::process(): sensorytype==SEE");
	          m_bModelValid = convertSensoryInputsToModel(false);
		}
	        break;
	      }

	      case Controller.SENSORTYPE_ACTION: {		
	        //
	        // Only convert to model when action is available for the current cycle.
	        // This is because when there is no action, this is usually a result of 
	        // the delay in processing the input information instead of depending
	        // on the state of the agent's world [driessens].
	        //
	        // Also, only convert to model when there is a vision associated with 
	        // the action (m_visionSeen == true)
		//
		// If there are multiple actions within the same cycle, then only the 
		// first action will be captured.
		//
	        if((m_bActionPreCondition || m_bFirstAction) && m_visionSeen) {
	          RoboILP.println(2,"Controller::process(): sensorytype==ACTION");
	          m_bModelValid = convertSensoryInputsToModel(true);
		}
		break;
	      }
	      default: {
	        break;
	      }
	    }
	}


	public void post_process(int sensorytype)
	{
	    switch(sensorytype) {
	      case Controller.SENSORTYPE_SEE: {
		//
		// When m_bActionPreCondition = false, agent is still allowed to see 
		// but not allowed to act
		//
		Action resultAction = null;

		if (m_bActionPreCondition && (m_mode == RoboILP.MODE_RUN)) {
	          RoboILP.println(2,"Controller::post_process(): sensorytype==SEE");

		  // TODO: do we only perform action when model is valid??
//		  if(m_bModelValid) {
		      resultAction = m_brain.think(m_model,m_currentCycle);
//		  }
		  sendActionToServer(resultAction);
		}
	        break;
	      }

	      case Controller.SENSORTYPE_ACTION: {		// Only run in test mode
	        if((m_bActionPreCondition || m_bFirstAction) && m_visionSeen) {
	          RoboILP.println(2,"Controller::post_process(): sensorytype==ACTION");

		  m_visionSeen = false;
		  m_bFirstAction = false;
		}
		break;
	      }
	      default: {
	        break;
	      }
	    }

	}


	//---------------------------------------------------------------------------
	// Miscellaneous support Methods
	//

	public void addObject(ObjectInfo objInfo)
	{
//	    System.out.println("Adding object: ");
//	    objInfo.print();

	    m_VisualObjects.add(objInfo);
	}

	
	public void setActionP1(double p1)
	{
	    m_action.setParam1(p1);
	}


	public void setActionP2(double p2)
	{
	    m_action.setParam2(p2);
	}

	public String getModel()
	{
	    return m_model;
	}

	public int getCurrentCycle()
	{
	    return m_action.getTime();
	}

	public Action getAction()
	{
	    return m_action;
	}

	public String popModel()
	{
	    m_bModelValid = false;
	    return m_model;
	}

	public boolean isModelValid()
	{
	    return m_bModelValid;
	}


        //---------------------------------------------------------------------------
	// This routine converts the qualitative parameters associated with an action
	// into actual quantities.  Several methods are to be tested:
	// (1) simple mapping to the least value within the qualitative range
	// (2) middle point within a range
	// (3) adaptively increases parameter strength if the same solution is found
	//     in the next cycle
	// (4) search for the value within the current vision that matches the closest
	//     to the action qualitative attribute (follows intention).

	protected Vector convertQualToActionParam(Action action) {
	  Vector params = new Vector();
	  String p1 = action.getParam1Qual();
	  String p2 = action.getParam2Qual();

	  //
	  // (1) simple conversion table
	  //
	  // TODO: should retrieve these from background file directly
	  //
	  String [] conversionTable = { "farleft",  "-23.0",
					"left",     "-1.0",
					"front",    "0.0",
					"right",    "1.0",
					"farright", "23.0",
					"veryfar",   "300.0",
					"far",       "50.0",
					"close",     "10.0",
					"same",     "1.0"
				     };

	  for(int i=0; i<conversionTable.length; i+=2) {
		if(conversionTable[i].equals(p1)) {
		  params.add(conversionTable[i+1]);
                  RoboILP.println(2,"Converting p1: " + p1 + " to " + conversionTable[i+1]);
		} else if(conversionTable[i].equals(p2)) {
		  params.add(conversionTable[i+1]);
                  RoboILP.println(2,"Converting p2: " + p2 + " to " + conversionTable[i+1]);
		}
	  }

	  return params;
	}


	protected void collectStatistics(Action action) {
	    if(m_mode == RoboILP.MODE_GENERATION) {
		if(action.getTime() == m_currentCycle) {
/*
		    Integer actionFreq = m_actionStats.get(action.getAction());
		    if(actionFreq == null) {
		      actionFreq = new Integer(1);
		    } else {
		      actionFreq = new Integer(actionFreq.intValue() + 1);
		    }

		    // now put the updated values back to the Hash
		    m_actionStats.put(action.getAction(), actionFreq);
*/
		}
	    }
	} // collectStatistics


	protected void printStatistics() {
/*
	    Set<String> allActions = m_actionStats.keySet();

	    for(String currentAction: allActions) {
	      Integer actionFreq = m_actionStats.get(currentAction);
	      
	      RoboILP.println(1,"Action: " + currentAction + " - Freq: " + actionFreq);
	    }
*/
	} // printStatistics


	//---------------------------------------------------------------------------
	// Abstract Methods
	//

	abstract public int determineSensoryType();
	abstract public boolean convertSensoryInputsToModel(boolean bWithActions);
	abstract public void sendActionToServer(Action action);


}
