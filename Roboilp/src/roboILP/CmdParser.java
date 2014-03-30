//
//	File:		CmdParser.java
//	Author:		Alan Wai
//	Date:		2008/09/26

//
//  incorporated multiple object vector lists
//  removed StringTokenizer global and made class serializable
//

package roboILP;
import java.util.*;
import java.io.*;


abstract class CmdParser
{

	public CmdParser(java.io.Reader reader) {}

	public void parseSeeCommand (String cmd, Controller controller) throws ParseException {}
	public void parseHearCommand (String cmd, Controller controller) throws ParseException {}
	public void parseInitCommand (String cmd, Controller controller) throws ParseException {}
	public void parseErrorCommand (String cmd, Controller controller) throws ParseException {}
	public void parseSenseBodyCommand (String cmd, Controller controller) throws ParseException {}
	public void parseActionCommand (String cmd, Controller controller) throws ParseException {}


}
