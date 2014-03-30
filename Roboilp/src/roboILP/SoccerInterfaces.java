package roboILP;

//
//	File:			SoccerInterfaces.java
//	Author:		Krzysztof Langner
//	Date:			1997/04/28
//

import java.util.*;
import java.io.*;




//***************************************************************************
//
//	This interface declares functions which are used to send
//	command to player
//
//***************************************************************************
interface SendCommand
{
	// This function sends move command to the server
	void move(double x, double y);
	// This function sends turn command to the server
//	void turn(double moment);
//	void turn_neck(double moment);
	// This function sends dash command to the server
//	void dash(double power);
	// This function sends kick command to the server
//	void kick(double power, double direction);
	// This function sends say command to the server
//	void say(String message);
	// This function sends chage_view command to the server
	void changeView(String angle, String quality);

	// This function sends any command to the server
	void send(String message);
}




interface IBrain
{
	//---------------------------------------------------------------------------
	// This function sends see information for brain to think
	public Action think(String info, int cycle);

	public void sendMessage(String message);
}


interface ICmdParser
{
	public void parseSeeCommand (String cmd, Controller controller) throws ParseException;
	public void parseHearCommand (String cmd, Controller controller) throws ParseException;
	public void parseInitCommand (String cmd, Controller controller) throws ParseException;
	public void parseErrorCommand (String cmd) throws ParseException;
	public void parseSenseBodyCommand (String cmd, Controller controller) throws ParseException;
	public void parseActionCommand (String cmd, Controller controller) throws ParseException;
}


