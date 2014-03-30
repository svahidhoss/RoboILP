/*
 * 
 * By:		Alan Wai
 * Date:	July 24, 2008
 *
 */
package roboILP;

import java.util.*;


public class Model 
{
  private Vector modelObjects = null;


	public Model() {
	  modelObjects = new Vector();
	}


	/********************
	 * update functions
	 ********************/

	public void addObject(String objStr, boolean bFirstRound) {
	  String predStr;
	  String paramStr;
	  boolean bObjectFound = false;
	  int bracketIdx = objStr.indexOf('(');
	  int bracket2Idx = objStr.indexOf(')');

	  predStr = objStr.substring(0,bracketIdx);
	  paramStr = objStr.substring(bracketIdx+1,bracket2Idx);

	  Iterator It = modelObjects.iterator();

	  while(It.hasNext()) {
	    ModelObject modelObject = (ModelObject)It.next();
//	    System.out.println("modelObject.getName(): " + modelObject.getName());
	    if(modelObject.getName().equals(predStr)) {
	      bObjectFound = true;
	      modelObject.add(paramStr, bFirstRound);
	    }
	  }

	  //
	  // Object not found - create new modelObject
	  if(!bObjectFound) {
	    // TODO: put this in a file for reading
	    int numInst;
	    if(predStr.equals("player")) numInst = 21; else numInst = 1;

	    ModelObject newObject = new ModelObject(predStr, numInst);
	    newObject.add(paramStr, bFirstRound);
//	    System.out.println("Adding new object: " + predStr + ", param: " + paramStr);
	    modelObjects.add(newObject);
	  }
	}


	public String returnString() {
	  String modelString = new String("");

	  Iterator It = modelObjects.iterator();

	  while(It.hasNext()) {
	    ModelObject modelObject = (ModelObject)It.next();
	    modelString = modelString + modelObject.returnString();
	  }

	  return modelString;

	}


}


