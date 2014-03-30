/*
 * 
 * By:		Alan Wai
 * Date:	July 24, 2008
 *
 */
package roboILP;

import java.util.*;


public class ModelObject
{
  private String objectName;
  private Vector parameterList = null;
  int numInstances = 0;

	public ModelObject(String name, int numInst) {
	  objectName = name;
	  parameterList = new Vector();
	  numInstances = numInst;
	}

	public String getName() {
	  return objectName;
	}


	/********************
	 * update functions
	 ********************/

	public void add(String parameter, boolean bFirstRound) {

	  parameterList.add(parameter);
//	  if(!bFirstRound) {
	  if(parameterList.size() > numInstances) {
	     // pop off the corresponding number of old entries
	     for(int i=0; i<parameterList.size()-numInstances; i++) {
	       parameterList.remove(0);
	     }
//	  } else {
//	    numInstances++;
	  }
	}

	public String returnString() {
	  String temp = "";
	  if(numInstances == 1) {
	    temp = objectName + "(" + (String)parameterList.elementAt(0) + ")\n";
	  } else {

	    Iterator It = parameterList.iterator();
	    int count = 1;

	    while(It.hasNext()) {
	      String param = (String)It.next();
	      temp = temp + objectName + "(" + count + ", " + param + ")\n";
	      count++;
	    }
	  }
	  return temp;
	}


}


