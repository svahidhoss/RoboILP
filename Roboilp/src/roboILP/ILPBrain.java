//
//	File:			ILPBrain.java
//	Author:			Alan Wai
//	Date:			July 24, 2008
//	Last Modified:		Sep 21, 2010
//

package roboILP;

import java.io.*;
import java.util.*;
import java.lang.*;
import java.awt.geom.*;
import java.awt.geom.Point2D.*;
import java.util.Random;

import jpl.Query;

//import roboILP.performance.*;
import com.declarativa.interprolog.*;
import com.xsb.interprolog.*;

//class ILPBrain extends Thread implements IBrain
class ILPBrain implements IBrain {
	// ===========================================================================
	// Private members
	//
	// public Memory m_memory; // place where all information is stored
//	private PrologEngine m_prologEngine;
	private SendCommand m_roboILP; // robot which is controled by this brain

	private int m_mode;
	private String m_theory;; // roboILP theory file
	private int m_numBest; // # of best scenes to return
	private boolean m_bFastMode; // Fast or complete mode in measurement
	private Vector theories = null;
	private int m_numAccumClausesSearched = 0;
	private int m_pp; // post-processing?

	private String m_currentCycleStr = null;
	private int[] m_searchOrder;
	private boolean m_bUseScoring = true;
	private long m_lastTime;
	private long m_totalPrologTime = 0;
	private int m_totalIterations = 0;

	private double m_x;
	private double m_y;
	
	

	int numRows = 0;
	int numCols = 0;

	// ---------------------------------------------------------------------------
	// This constructor:
	// - stores connection to roboILP
	// - starts thread for this object
	public ILPBrain(SendCommand roboILP, int mode, String theory, int numBest,
			int numScenes, int pp, boolean debug, double x, double y) {
		reset();

		m_lastTime = 0;
		m_roboILP = roboILP;
		m_mode = mode; // Run: 0; Train: 1; Test: 2
		m_theory = theory;
		m_numBest = numBest;
		m_bUseScoring = true;
		m_pp = pp;
		m_x = x;
		m_y = y;

		if ((m_mode == RoboILP.MODE_TRAIN) || !m_bUseScoring) {
			System.out.println("Fast Mode OFF");
			m_bFastMode = false; // Iterate through all theory clauses
		} else {
			System.out.println("Fast Mode ON");
			m_bFastMode = true; // Stop searching after first solution is found.
		}

		//
		// The sensory information is to be parsed into the first-order logic
		// format.
		// Background and rules files are also in first-order logic format, so
		// that this can be application-independent.
		//
		String bgFile = m_theory + ".bg";
		String rulesFile = m_theory + ".rules";
//		String XSBpath = "C:/Program Files (x86)/XSB/config/x64-pc-windows/bin";

		// If it is just for converting .lsf files to .kb files, then no need to
		// load the (solution) theory file
		if (m_mode != RoboILP.MODE_GENERATION) {
			// m_prologEngine = new
			// NativeEngine(System.getProperty("java.library.path")+"xsb",debug);
			//TODO changed here
//			m_prologEngine = new NativeEngine(XSBpath, debug);
//			m_prologEngine.command("import append/3 from basics"); // Only for
																	// XSB
																	// Prolog
			// m_prologEngine.command("dynamic dirQl/4"); // Only for XSB Prolog
			// m_prologEngine.command("dynamic distQl/4"); // Only for XSB
			// Prolog
			
			consultFile(bgFile);
			consultFile(rulesFile);
//			prologConsultFile(bgFile);
//			prologConsultFile(rulesFile);
		}

		//
		// During Measurement Mode, a score file will be created that contains
		// all the scores of each hypothesis clause. This will determine the
		// order in which the clauses will be searched. In the Run Mode, this
		// coverage
		// file will be loaded.
		//

		if ((m_mode != RoboILP.MODE_GENERATION)
				&& (m_mode != RoboILP.MODE_TRAIN) && m_bUseScoring) {
			// loadScoreFile();
		}

		// start();

	}

	public void reset() {
		m_numAccumClausesSearched = 0;
		m_currentCycleStr = null;
		m_searchOrder = null;
		System.gc();
		// TODO:
//		if (m_prologEngine != null) {
//			m_prologEngine.shutdown();
//		}
	}

//	public void prologConsultFile(String fname) {
//		RoboILP.println(1, "proglogConsultFile('" + fname + "')");
//		m_prologEngine.command("consult('" + fname + "')");
//	}

	// ---------------------------------------------------------------------------
	// This function is called in Controller.java
	public Action think(String currentModelStr, int currentCycle) {
		Action resultAction = null;
		String strToProlog = null;
		Object[] bindings = null;
		/*
		 * m_memory.store(model); currentModelStr = m_memory.getInfo();
		 */

		try {
			//
			// Reformat message with asserta and abolish predicates, then send
			// to Prolog engine
			//
			// strToProlog = currentModelStr +
			// "action("+currentCycle+",Action,P1,P2,CN),abolish(key/1),abolish(objectSeen/2),abolish(distQn/4),abolish(dirQn/4)";
			String strCycle = Integer.toString(currentCycle);
			// strToProlog = currentModelStr +
			// "asserta(end(K)),action("+currentCycle+",Action,P1,P2)";
			strToProlog = currentModelStr + "action(" + currentCycle
					+ ",Action,P1,P2,CN)";
			// strToProlog = currentModelStr +
			// "action("+currentCycle+",Action,P1,P2),abolish(key/1),abolish(objectSeen/2),abolish(distQn/4),abolish(dirQn/4)";
			RoboILP.println(1, "[ILPBrain::think] cycle(" + currentCycle
					+ "): sending strToProlog: " + strToProlog);

			long prologStartTime = System.currentTimeMillis();
			
			
			Query query = new Query(strToProlog);
			
//			Hashtable<K, V> response = query.oneSolution().get("");
			
//			Hashtable[] ss4 = query.allSolutions();
			
			
/*			bindings = m_prologEngine.deterministicGoal(strToProlog,
			// "asserta(distQl(1,Object)),asserta(dirQl(1,Object)),action(1,Action,P1,P2,CN)",
					null, null,
					// "[string(Object)]",
					// new Object[]{player1},
					"[string(Action),string(P1),string(P2),string(CN)]");*/

			RoboILP.println(2,
					"[ILPBrain::think] getting results back from Prolog...");
			long deltaTime = System.currentTimeMillis() - m_lastTime;
			long prologDeltaTime = System.currentTimeMillis() - prologStartTime;

			m_totalPrologTime += prologDeltaTime;
			m_totalIterations++;
			Random r = new Random();
//			if (bindings != null) {
			Hashtable[] AllSolutions = query.allSolutions();
			if (AllSolutions!=null&&AllSolutions.length>0) {
				// Results returned from Prolog
//				String strAction = (String) bindings[0];
//				String P1 = (String) bindings[1];
//				String P2 = (String) bindings[2];
//				String CN = (String) bindings[3];
				//Hashtable Solution = AllSolutions[r.nextInt(AllSolutions.length)];
				System.out.println("DEBUG:START SOLUTION SET");
				for (Hashtable mySolution: AllSolutions){
					System.out.println("DEBUG:"+mySolution.get("Action"));
				}
				System.out.println("DEBUG:END SOLUTION SET");
				Hashtable Solution = AllSolutions[0];
				String strAction = Solution.get("Action").toString();
				String P1 = Solution.get("P1").toString();
				String P2 = Solution.get("P2").toString();
				String CN = Solution.get("CN").toString();
				// String CN = (String)bindings[3];
				// RoboILP.println(1,"[ILPBrain::think] cycle("+currentCycle+"): action("+strAction+
				// ","+P1+ "," +P2+ ") @ clause: " + CN);
				RoboILP.println(1, "[ILPBrain::think] prologDelta="
						+ prologDeltaTime + " cycle(" + currentCycle
						+ "): action(" + strAction + "," + P1 + "," + P2 + ","
						+ CN + ")");
				resultAction = new Action(strAction);
				resultAction.setParam1Qual(P1);
				resultAction.setParam2Qual(P2);
				query.close();
			} else {
				RoboILP.println(1, "[ILPBrain::think] prologDelta="
						+ prologDeltaTime + " cycle(" + currentCycle
						+ "): no action found.");
			}
			m_lastTime = System.currentTimeMillis();
			
			runQuery("abolish(key/1),abolish(objectSeen/2),abolish(distQn/4),abolish(dirQn/4)");
//			m_prologEngine
//					.command("abolish(key/1),abolish(objectSeen/2),abolish(distQn/4),abolish(dirQn/4)");

		} catch (NullPointerException e) {
			RoboILP.println(0, "[ILPBrain::think] cycle(" + currentCycle
					+ "): no action.");
		}

		return resultAction;

	} // see

	// ===========================================================================
	//
	// Utility Functions
	//

	public void sendMessage(String message) {
		m_roboILP.send(message);
	}

/*	public void shutdown() {
		m_prologEngine.shutdown();
	}*/

	public void printStats() {
		System.out.println("Average Prolog RunTime: " + m_totalPrologTime
				/ m_totalIterations);
	}
	
	private boolean runQuery(String queryString) {
//		String t1 = "consult('" + KNOWLEDGE_LOCATION + "')";
		Query q1 = new Query(queryString);
		return q1.hasSolution();
	}
	
	private boolean consultFile(String fileLocation) {
		Query q1 = new Query("consult('" + fileLocation + "')");
		return q1.hasSolution();
		
	}


}
