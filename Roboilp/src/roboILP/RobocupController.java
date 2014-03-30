//
//  File:	  RobocupController.java
//  Author:	  Alan Wai
//  Date:	  2008/09/25

package roboILP;

import java.util.*;
import java.io.*;
import java.awt.geom.*;
import java.awt.geom.Point2D.*;

class RobocupController extends Controller {

	//
	// Robocup-specific static values
	//

	// max # of unknown players to show in model
	private static final int MAX_UNK_PLAYERS = 22;

	//
	// The following are obtained from atan.model.Controller

	public static final int REFREE_MESSAGE_FOUL_OWN = 3030;
	public static final int REFREE_MESSAGE_FOUL_OTHER = 3040;
	public static final int REFREE_MESSAGE_HALF_TIME = 3050;
	public static final int REFREE_MESSAGE_TIME_UP = 3060;
	public static final int REFREE_MESSAGE_TIME_UP_WITHOUT_A_TEAM = 3070;
	public static final int REFREE_MESSAGE_TIME_EXTENDED = 3080;
	public static final int REFREE_MESSAGE_DROP_BALL = 3085;
	public static final int REFREE_MESSAGE_OFFSIDE_OWN = 3086;
	public static final int REFREE_MESSAGE_OFFSIDE_OTHER = 3087;
	public static final int PLAY_MODE_BEFORE_KICK_OFF = 3090;
	public static final int PLAY_MODE_TIME_OVER = 3100;
	public static final int PLAY_MODE_PLAY_ON = 3110;
	public static final int PLAY_MODE_KICK_OFF_OWN = 3120;
	public static final int PLAY_MODE_KICK_OFF_OTHER = 3130;
	public static final int PLAY_MODE_KICK_IN_OWN = 3140;
	public static final int PLAY_MODE_KICK_IN_OTHER = 3150;
	public static final int PLAY_MODE_FREE_KICK_OWN = 3160;
	public static final int PLAY_MODE_FREE_KICK_OTHER = 3170;
	public static final int PLAY_MODE_CORNER_KICK_OWN = 3180;
	public static final int PLAY_MODE_CORNER_KICK_OTHER = 3190;
	public static final int PLAY_MODE_GOAL_KICK_OWN = 3200;
	public static final int PLAY_MODE_GOAL_KICK_OTHER = 3210;
	public static final int PLAY_MODE_GOAL_OWN = 3220;
	public static final int PLAY_MODE_GOAL_OTHER = 3230;
	public static final int PLAY_MODE_FREE_KICK_FAULT_OWN = 3240;
	public static final int PLAY_MODE_FREE_KICK_FAULT_OTHER = 3250;

	//
	// Robocup-specific attributes
	//

	private String m_teamName;
	private char m_side;
	private int m_playerNum;
	private int m_maxActionCnt;
	private String m_selfAbsXStr;
	private String m_selfAbsYStr;
	private String m_selfAbsDirStr;
	private boolean m_bUseAllModels;
	private boolean m_bUseAbsInfo = false;
	private String m_lastGoal = "gl";

	private int m_currentModel;
	// private LibsClient libsclient;
	private boolean m_bPrintMultipleActions = false;
	private int m_nNumInvalidAction;
	private int m_nInvalidActionThreshold;

	//
	// The extended Robocup Controller class should simply implement
	// determineSensoryType()
	// and convertVisualObjectsToModel().
	//

	private int m_time;

	// Split objects into specific lists
	private Vector m_ball_list;
	private Vector m_player_list;
	private Vector m_teammates_list;
	private Vector m_opponents_list;
	private Vector m_unknownPlayers_list;
	private Vector m_flag_list;
	private Vector m_goal_list;
	private Vector m_line_list;
	private Vector m_action_list;

	public RobocupController(int mode, IBrain brainRef, Memory memoryRef,
			String teamName, boolean bUseAllModels, int maxActionCnt) {
		super(mode, brainRef, memoryRef);
		m_parser = new RobocupCmdParser(new StringReader(""));

		m_teamName = teamName;
		m_side = ' ';
		m_playerNum = -1;
		m_currentModel = 0;
		m_nNumInvalidAction = 0;
		m_nInvalidActionThreshold = 30;
		m_selfAbsXStr = "-1000.0";
		m_selfAbsYStr = "-1000.0";
		m_selfAbsDirStr = "0.0";
		m_bUseAllModels = bUseAllModels;
		m_maxActionCnt = maxActionCnt;

		m_player_list = new Vector(22);
		m_teammates_list = new Vector(11);
		m_opponents_list = new Vector(11);
		m_unknownPlayers_list = new Vector(22);
		m_ball_list = new Vector(1);
		m_goal_list = new Vector(2);
		m_line_list = new Vector(20);
		m_flag_list = new Vector(60);
		m_action_list = new Vector(1);

		// libsclient = new LibsClient();
	}

	public RobocupController(int mode, IBrain brainRef, Memory memoryRef) {
		super(mode, brainRef, memoryRef);
		m_parser = new RobocupCmdParser(new StringReader(""));

		m_teamName = "";
		m_side = ' ';
		m_playerNum = -1;
		m_currentModel = 0;
		m_nNumInvalidAction = 0;
		m_nInvalidActionThreshold = 30;

		m_player_list = new Vector(22);
		m_teammates_list = new Vector(11);
		m_opponents_list = new Vector(11);
		m_unknownPlayers_list = new Vector(22);
		m_ball_list = new Vector(1);
		m_goal_list = new Vector(2);
		m_line_list = new Vector(20);
		m_flag_list = new Vector(60);
		m_action_list = new Vector(1);

		// libsclient = new LibsClient();
	}

	public void reset() {
		super.reset();

		m_side = ' ';
		m_playerNum = -1;
		m_nNumInvalidAction = 0;
		m_nInvalidActionThreshold = 30;
		m_selfAbsXStr = "-1000.0";
		m_selfAbsYStr = "-1000.0";
		m_selfAbsDirStr = "0.0";

		m_player_list = new Vector(22);
		m_teammates_list = new Vector(11);
		m_opponents_list = new Vector(11);
		m_unknownPlayers_list = new Vector(22);
		m_ball_list = new Vector(1);
		m_goal_list = new Vector(2);
		m_line_list = new Vector(20);
		m_flag_list = new Vector(60);
		m_action_list = new Vector(1);

		// libsclient = new LibsClient();
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Controller Methods overridden
	//
	public void pre_process(int sensorytype) {
		switch (sensorytype) {
		case Controller.SENSORTYPE_SEE: {
			// Need to clear the seen flags/lines every time to calculate
			// the absolute pos based on only the current scene.
			// libsclient.reset_seen_objects();
			break;
		}
		default: {
			break;
		}
		}

		super.pre_process(sensorytype);
	}

	public void addObject(ObjectInfo objInfo) {
		int objId = objInfo.getId();
		double dist = objInfo.getDistance();
		double dir = objInfo.getDirection();

		super.addObject(objInfo);
	}

	// //////////////////////////////////////////////////////////////////////////
	//
	// Abstract Controller methods defined
	//

	public int determineSensoryType() {
		char c1, c2;

		//
		// code borrowed from atan.parser.Filter.run()
		//
		// Note: Assume no turn_neck action and no move action
		//
		if ((c1 = m_message.charAt(1)) == 's') {
			if ((c2 = m_message.charAt(3)) == 'e') {
				m_message = m_message.substring(5, m_message.length() - 1);
				return Controller.SENSORTYPE_SEE;
			} else if (c2 == 'n') {
				m_message = m_message.substring(12, m_message.length() - 1);
				return Controller.SENSORTYPE_SENSEBODY;
			}
		} else if (c1 == 'e') {
			m_message = m_message.substring(7, m_message.length() - 1);
			return Controller.SENSORTYPE_ERROR;
		} else if (c1 == 'i') {
			m_message = m_message.substring(6, m_message.length() - 1);
			return Controller.SENSORTYPE_INIT;
		} else if (c1 == 'h') {
			m_message = m_message.substring(6, m_message.length() - 1);
			return Controller.SENSORTYPE_HEAR;
		} else if (c1 == 't' || c1 == 'd' || c1 == 'k' || c1 == 'c') {
			// declare invalid actions
			// TODO: consider turn_neck as invalid for now.
			if (m_message.charAt(9) == 'k') {
				return Controller.SENSORTYPE_ERROR;
			}
			// change_view is invalid
			if (m_message.charAt(2) == 'h' && m_message.charAt(8) == 'v') {
				return Controller.SENSORTYPE_ERROR;
			}

			String actionStr;
			int idx = m_message.indexOf(' ');
			actionStr = m_message.substring(1, idx);
			m_message = m_message.substring(idx + 1, m_message.length() - 1);

			m_action = new Action(actionStr);
			RoboILP.println(3, "idx: " + idx + " actionStr: " + actionStr);

			return Controller.SENSORTYPE_ACTION;
		}

		return Controller.SENSORTYPE_ERROR;
	}

	public boolean convertSensoryInputsToModel(boolean bWithActions) {

		Vector objectsPerCycle;
		Vector actionsVector;
		int numCyclesWithObjects = 0;
		int numActions = 0;
		int lastCycleWithObjects = 0;

		// boolean goal_l_found = false;
		// boolean goal_r_found = false;
		// boolean ball_found = false;
		boolean max_players_found = false;

		int i;
		int player_idx = 1;

		//
		// Clear all object and action lists
		//
		/*
		 * m_player_list.clear(); m_teammates_list.clear();
		 * m_opponents_list.clear(); m_unknownPlayers_list.clear();
		 * m_ball_list.clear(); m_goal_list.clear(); m_line_list.clear();
		 * m_flag_list.clear(); m_action_list.clear();
		 */

		RoboILP.println(3, "+++++ Cycle: " + m_currentCycle + " +++++ Model: "
				+ m_currentModel + " +++++");

		objectsPerCycle = m_memory.getVisualObjects();
		actionsVector = m_memory.getActions();

		// ///////////////////////////////////////////////////////////////////////////////
		//
		// (1) Use flags/lines to calculate absolute coordinates (self)
		// (2) For goal and ball, pick the latest available one (calculate abs
		// for all).
		// (3) For players, pick from the latest until it fills up at most
		// MAX_PLAYERS
		//

		//
		// Look from latest objects to oldest objects
		//
		// The algorithm below could be improved by searching through hash keys
		// on
		// the second and subsequent iterations, for the objects that were not
		// found
		// in the first iteration.
		//
		// Note: this can be easily modified to add to each list a number of
		// past seen
		// objects instead of adding only the latest ones.
		//

		numCyclesWithObjects = objectsPerCycle.size();

		addStringToModel("key(" + m_currentCycle + ")");

		for (i = numCyclesWithObjects - 1; i >= 0; i--) {
			Vector visualObjects = (Vector) objectsPerCycle.elementAt(i);

			for (Enumeration e = visualObjects.elements(); e.hasMoreElements();) {
				ObjectInfo objInfo = (ObjectInfo) e.nextElement();

				double dist = objInfo.getDistance();
				double dir = objInfo.getDirection();

				if (!max_players_found && objInfo.getType().equals("player")) {
					RobocupPlayerInfo playerInfo = (RobocupPlayerInfo) objInfo;
					double bodyDir = playerInfo.getBodyFaceDir();
					double headDir = playerInfo.getHeadFaceDir();

					playerInfo.setSide(m_teamName);
					// Set the player instance name here
					// player_idx will increment only if the player is not all
					// known
					player_idx = playerInfo.setInstName(m_teamName, player_idx);

					// m_model = m_model + "belongsToTeam(" +
					// playerInfo.getTime() + ",";
					// m_model = m_model + playerInfo.getInstName() + ",";
					// m_model = m_model + playerInfo.getSide() + ").\n";

					if (bodyDir != -1000) {
						addStringToModel("bodyDirQn(" + objInfo.getTime()
								+ ",self," + objInfo.getInstName() + ","
								+ bodyDir + ")");
					}
					if (headDir != -1000) {
						addStringToModel("headDirQn(" + objInfo.getTime()
								+ ",self," + objInfo.getInstName() + ","
								+ headDir + ")");
					}

					if (player_idx == MAX_UNK_PLAYERS) {
						max_players_found = true;
					}
				}

				// Set the unknown goal to last goal seen. This is a problem
				// with Sprinter
				// where Goal can be detected as unknown.
				if (objInfo.getType().equals("goal")) {
					RobocupGoalInfo goalInfo = (RobocupGoalInfo) objInfo;
					if (goalInfo.getInstName().compareTo("gu") == 0) {
						goalInfo.setInstName(m_lastGoal);
					}
					m_lastGoal = goalInfo.getInstName();
				}
				addStringToModel("objectSeen(" + objInfo.getTime() + ","
						+ objInfo.getInstName() + ")");

				addStringToModel("dirQn(" + objInfo.getTime() + ",self,"
						+ objInfo.getInstName() + "," + dir + ")");
				addStringToModel("distQn(" + objInfo.getTime() + ",self,"
						+ objInfo.getInstName() + "," + dist + ")");

			} // for e
		} // for i

		// /////////////////////////////////////////////////////////////////////////
		//
		// Action Predicate - added only in Generation mode
		//
		// /////////////////////////////////////////////////////////////////////////

		if (bWithActions) {
			//
			// get all the different actions
			//
			int stopLimit = 0;
			numActions = actionsVector.size();
			RoboILP.println(3, "numActions: " + numActions);

			if (!m_bPrintMultipleActions)
				stopLimit = numActions - 1;

			for (i = numActions - 1; i >= stopLimit; i--) {
				// i=numCyclesWithActions-1;
				Action action = (Action) actionsVector.elementAt(i);

				m_model = m_model + "actionQn(" + action.getTime() + ",";
				m_model = m_model + action.getAction() + ",";
				m_model = m_model
						+ action.getParam1()
						+ (action.isParam2Valid() ? ("," + action.getParam2())
								: "");
				m_model = m_model + ").\n";

				//
				// Dec 27, 2008
				// Calculate the frequency per action type in Generation Mode
				//
				collectStatistics(action);
			} // for
		} // if bWithActions

		addStringToModel("end(" + m_currentCycle + ")");
		m_currentModel++;

		RoboILP.println(3, "m_model @ cycle " + m_currentCycle + ":\n"
				+ m_model);

		/*
		 * // Debug Info if ((m_VisualObjects!=null) &&
		 * !m_VisualObjects.isEmpty()) {
		 * 
		 * for (Enumeration e = m_VisualObjects.elements(); e.hasMoreElements()
		 * ;) { ((ObjectInfo)e.nextElement()).print(); }
		 * 
		 * if (bWithActions) { m_action.print(); } }
		 */
		return true;
	}

	public void addStringToModel(String str) {
		if (m_mode == RoboILP.MODE_RUN) {
			m_model += "asserta(" + str + "),";
		} else {
			m_model += str + ".\n";
		}
	}

	public void sendActionToServer(Action action) {

		// TODO: Sep 20, 2010: rewrite since action is no longer a Literal but a
		// String
		// if((action != null) || !m_bFirstAction) {
		if ((action != null)) {

			/*
			 * if(m_bFirstAction) { m_bFirstAction = false; }
			 * 
			 * // If action is null, then perform the last action. Inc.
			 * numInvalidAction // so that if it crosses a threshold, then reset
			 * m_bFirstAction. // Otherwise, store it as lastAction performed.
			 * if(action != null) { m_lastAction = action; m_nNumInvalidAction =
			 * 0; } else { action = m_lastAction; m_nNumInvalidAction++; }
			 */
			Vector params = convertQualToActionParam(action);

			//
			// Form message to send to Robocup server
			//
			String strAction = action.getAction();
			String message = "(" + action.getAction() + " ";

			if (strAction.compareTo("turn") == 0) {
				message += params.elementAt(0);
			} else if (strAction.compareTo("dash") == 0) {
				message += params.elementAt(0);
			} else if (strAction.compareTo("kick") == 0) {
				message += params.elementAt(0) + " ";
				message += params.elementAt(1);
			}
			message += ")";

			if (message.compareTo(m_lastMessage) == 0) {
				m_sameMessageCnt++;
			} else {
				m_sameMessageCnt = 0;
			}
			m_lastMessage = message;

			if (m_sameMessageCnt == m_maxActionCnt) {
				RoboILP.println(1, "Agent stuck!  Send random action.");
				sendRandomAction();
			} else {
				RoboILP.println(1, "Sending message: " + message);
				m_brain.sendMessage(message);
			}

			// sleep one step to ensure that we will not send
			// two commands in one cycle.
			// try {
			// Thread.sleep(2*SoccerParams.simulator_step);
			// Thread.sleep(SoccerParams.simulator_step);
			// } catch(Exception e){}
			/*
			 * if(m_nNumInvalidAction >= m_nInvalidActionThreshold) {
			 * m_bFirstAction = true; }
			 */
		} else {
			RoboILP.println(1, "No action found.");
			sendRandomAction();
		}
	} // sendActionToServer

	protected void sendRandomAction() {
		//
		// If no solution was found, turn the agent
		// so its environment will change to kick start its reaction.
		//
		double dirParam = Math.random() * 90.0;
		double powerParam = Math.random() * 50.0;

		int actionDice = (int) (Math.random() * 100.0) % 2;

		String message;

		if (actionDice == 0) {
			message = "(turn " + dirParam;
		} else if (actionDice == 1) {
			message = "(dash " + powerParam;
		} else {
			message = "(kick " + dirParam + " " + powerParam;
		}
		message += ")";

		RoboILP.println(0, "Sending random message: " + message);
		m_brain.sendMessage(message);
	}

	// ////////////////////////////////////////////////////////////////////////////
	//
	// Support Functions
	//

	//
	// IMPORTANT: CLAUDIEN does not like numbers 0.0 or anything that ends with
	// ".0". We need to get rid of that.
	//
	// However, skip this extra step if it's in Run mode since this problem
	// won't happen
	// and this costs time to run.
	//
	private String convertDouble(double d) {
		String temp = String.valueOf(d);
		int idx = -1;
		if ((m_mode != RoboILP.MODE_RUN) && ((idx = temp.indexOf(".0")) != -1)) {
			temp = temp.substring(0, idx);
		}
		return temp;
	}

	// ////////////////////////////////////////////////////////////////////////////
	//
	// The following method names are inherited from the software package atan.
	//

	public void infoHearPlayMode(int playMode) {
		switch (playMode) {
		case PLAY_MODE_KICK_OFF_OTHER:
		case PLAY_MODE_KICK_OFF_OWN:
		case PLAY_MODE_PLAY_ON: {
			m_bActionPreCondition = true;
			RoboILP.println(1, "infoHearPlayMode: ON");
			break;
		}
		default: {
			m_bActionPreCondition = false;
			RoboILP.println(1, "infoHearPlayMode: OFF");
			break;
		}
		}
	}

	public void infoHearReferee(int refereeMsg) {
		// all referee messages will be followed by a subsequent play mode
		// message
		m_bActionPreCondition = false;
	}

	//
	// This is a local function implemented to convert object strings into an ID
	// number
	// using a hash table
	//
	private int convertKeyToID(String key) {
		return 0;
	}

	public void setPlayerNum(int num) {
		m_playerNum = num;
	}

	public void setSide(char side) {
		m_side = side;
		// libsclient.init_points_locations(m_side);
	}

	public int getPlayerNum() {
		return m_playerNum;
	}

	public String getTeamName() {
		return m_teamName;
	}

	public int getSide() {
		return m_side;
	}

	public int getTime() {
		return m_time;
	}

	public Vector getBallList() {
		return m_ball_list;
	}

	public Vector getPlayerList() {
		return m_player_list;
	}

	public Vector getTeammatesList() {
		return m_teammates_list;
	}

	public Vector getOpponentsList() {
		return m_opponents_list;
	}

	public Vector getUnknownPlayersList() {
		return m_unknownPlayers_list;
	}

	public Vector getGoalList() {
		return m_goal_list;
	}

	public Vector getLineList() {
		return m_line_list;
	}

	public Vector getFlagList() {
		return m_flag_list;
	}

}
