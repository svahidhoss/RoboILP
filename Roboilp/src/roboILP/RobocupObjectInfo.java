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
//	This class holds visual information about player
//
//***************************************************************************
class RobocupPlayerInfo extends ObjectInfo
{
  String  m_teamName = "";
  String  m_side = "";
  int     m_uniformName = -1;        // recognise -1 as not being able to see number
  double  m_bodyDir	= -1000;	// absolute body direction of this player
  double  m_headDir	= -1000;	// absolute head direction of this player
  double  m_bodyFaceDir	= -1000;	// from Robocup server
  double  m_headFaceDir	= -1000;	// from Robocup server
  boolean m_goalie = false;

  double  m_absDir	= 0.0;

  //===========================================================================
  // Initialization member functions
  public RobocupPlayerInfo()
  {
    super("player");
  }

  public RobocupPlayerInfo(String team, int number, boolean is_goalie)
  {
    super("player");
    setTeamName(team);
    m_uniformName = number;
    m_goalie = is_goalie;
    m_absDir  = 0;
  }

  public RobocupPlayerInfo(String team, int number)
  {
    super("player");
    setTeamName(team);
    m_uniformName = number;
    m_absDir  = 0;
  }

  public void setTeamName(String team)
  {
    if (team.compareTo("") == 0) {
      m_teamName = team;
    }
    else if (team.charAt(0) == '"' && team.charAt(team.length()-1) == '"') {
      m_teamName = team.substring(1, team.length() - 1);
    }
    else {
      m_teamName = team;
    }   
  }

  public String getTeamName()
  {
    return m_teamName;
  }


  public String getSide()
  {
    return m_side;
  }

  public void setSide(String currentTeam)
  {
    if(m_teamName.equals("")) {
      m_side = "unk";
    } else if(m_teamName.equals(currentTeam)) {
      m_side = "my";
    } else {
      m_side = "opp";
    }
  }

  public int setInstName(String currentTeam, int idx)
  {
    if(m_teamName.equals("") || m_uniformName == -1) {
//      assert idx != -1;
//      System.out.println("setInstName index: "+idx);
      m_instName = "u" + idx;
      return (idx + 1);
    } else if(m_teamName.equals(currentTeam)) {
      m_instName = "p" + m_uniformName;
    } else {
      m_instName = "q" + m_uniformName;
    }
    return idx;		// don't inc if player has known team
  }

  public void setGoalie(boolean goalie)
  {
    m_goalie = goalie;
  }

  public boolean isGoalie()
  {
    return m_goalie;
  }

  public void setPlayerNum(int number)
  {
    m_uniformName = number;
  }

  public int getPlayerNum()
  {
    return m_uniformName;
  }

  public double getBodyFaceDir()
  {
    return m_bodyFaceDir;
  }

  public double getHeadFaceDir()
  {
    return m_headFaceDir;
  }

  public void print()
  {
    super.print();
    System.out.println("Player num: "+m_uniformName+", Team: "+m_teamName+" ("+m_bodyDir+","+m_headDir+")");
  }

  public String getKey()
  {
    return m_type+"_"+m_teamName+"_"+m_uniformName;
  }
}


//***************************************************************************
//
//	This class holds visual information about goal
//
//***************************************************************************
class RobocupGoalInfo extends ObjectInfo
{
  private char m_side;
  //===========================================================================
  // Initialization member functions
  public RobocupGoalInfo()
  {
    super("goal");
    m_side = ' ';
    m_instName = "gu";		// unknown goal
  }

  public RobocupGoalInfo(char side)
  {
    super("goal");
    m_side = side;
    m_instName   = (side == 'l') ? "gl" : "gr";
  }

  public char getSide()
  {
    return m_side;
  }

  public void print()
  {
    super.print();
    System.out.println("Goal side: " + m_side);
  }

  public String getKey()
  {
    return m_type;
  }
}


//***************************************************************************
//
//	This class holds visual information about ball
//
//***************************************************************************
class RobocupBallInfo extends ObjectInfo
{
  //===========================================================================
  // Initialization member functions
  public RobocupBallInfo()
  {
    super("ball");
    m_instName = "ball1";
  }

  public String getKey()
  {
    return m_type;
  }
}


//***************************************************************************
//
//	This class holds visual information about flag
//
//***************************************************************************
class RobocupFlagInfo extends ObjectInfo
{
  char m_kind;  // p|g
  char m_pos1;  // t|b|l|c|r
  char m_pos2;  // l|r|t|c|b
  int m_num;    // 0|10|20|30|40|50

  //===========================================================================
  // Initialization member functions
  public RobocupFlagInfo()
  {
    super("flag");
    m_kind = '_';
    m_pos1 = '_';
    m_pos2 = '_';
    m_num = 0;
    m_inViewConeP = false;
    setInstName();
  }

  public RobocupFlagInfo(char kind, char pos1, char pos2, int num, boolean inViewCone)
  {
    super("flag");
    m_kind = kind;
    m_pos1 = pos1;
    m_pos2 = pos2;
    m_num = num;
    m_inViewConeP = inViewCone;
    setInstName();
  }

  public void setInstName()
  {
    switch(m_kind) {
      case 'p': {
        m_instName = (m_pos1 == 'l') ?  (
			(m_pos2 == 't') ? "fplt" :
			(m_pos2 == 'c') ? "fplc" :
					  "fplb"
		     ) : (
			(m_pos2 == 't') ? "fprt" :
			(m_pos2 == 'c') ? "fprc" :
					  "fprb"
		     );
        break;
      }
      case 'g': {
        m_instName = (m_pos1 == 'l') ?  (
			(m_pos2 == 't') ? "fglt" :
					  "fglb"
		     ) : (
			(m_pos2 == 't') ? "fgrt" :
					  "fgrb"
		     );
        break;
      }

      case ' ': {
        switch(m_pos1) {
	  case 'c': {
	    m_instName = 	(m_pos2 == ' ') ? "fc" :
				(m_pos2 == 't') ? "fct" :
					  	  "fcb";
	    break;
	  }
	  case 't': {
	    m_instName = (m_pos2 == 'l') ?  "ftl" + m_num :
	    	   	 (m_pos2 == 'r') ?  "ftr" + m_num :
		   		      	    "ft0";
	    break;
	  }
	  case 'b': {
	    m_instName = (m_pos2 == 'l') ?  "fbl" + m_num :
	    	   	 (m_pos2 == 'r') ?  "fbr" + m_num :
		   		      	    "fb0";
	    break;
	  }
	  case 'l': {
	    m_instName = (m_pos2 == 't') ? ( 
	    			(m_num == 0) ?  "flt" :
						"flt" + m_num)
		   	 : 
	    	   	 (m_pos2 == 'b') ? (
	    			(m_num == 0) ?  "flb" :
		   				"flb" + m_num)
		   	 : "fl0";
	    break;
	  }
	  case 'r': {
	    m_instName = (m_pos2 == 't') ? ( 
	    			(m_num == 0) ?  "frt" :
						"frt" + m_num)
		   	 : 
	    	   	 (m_pos2 == 'b') ? (
	    			(m_num == 0) ?  "frb" :
		   				"frb" + m_num)
		   	 : "fr0";
	    break;
	  }
	  default: {
//	    assert false;
	    System.out.println("ERROR: bad flag name!!");
	    break;
	  }
	}
        break;
      } 

      default: {	// flag out of view Cone
        m_instName = "foV";
        break;
      }

    } // m_kind
  }

  public void print()
  {
    super.print();
    System.out.println("Flag ("+m_kind+","+m_pos1+","+m_pos2+","+m_num+")");
  }

  public String getKey()
  {
    return m_type+"_"+m_kind+"_"+m_pos1+"_"+m_pos2+"_"+String.valueOf(m_num);
  }

}


//***************************************************************************
//
//	This class holds visual information about line
//
//***************************************************************************
class RobocupLineInfo extends ObjectInfo
{
  char m_kind;  // l|r|t|b

  //===========================================================================
  // Initialization member functions
  public RobocupLineInfo()
  {
    super("line");
  }

  public RobocupLineInfo(char kind)
  {
    super("line");
    m_kind = kind;
    switch(m_kind) {
      case 'l': { m_instName = "ll"; break; }
      case 't': { m_instName = "lt"; break; }
      case 'b': { m_instName = "lb"; break; }
      case 'r': { m_instName = "lr"; break; }
    }
  }

  public void print()
  {
    super.print();
    System.out.println("Line: " + m_kind);
  }

  public String getKey()
  {
    return m_type+"_"+m_kind;
  }
}
