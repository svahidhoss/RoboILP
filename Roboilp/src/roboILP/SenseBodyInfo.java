//  Modified by:  Paul Marlow, Amir Ghavam, Yoga Selvaraj
//  Course:       Software Agents
//  Date Due:     November 30, 2000   

//  Modified by:  Tarek Hassan
//  Date:         9 July 2001

//  Modified by:  Kevin Lam
//  Date:         11 November 2003

//--------------------------------------------------------------
//  This class is used to store body information about a player.
//  Currently this class is not used within the actual program,
//  but was implemented incase time allowed the introduction of
//  stamina as input into the backpropagation network.
//--------------------------------------------------------------

package roboILP;
import java.util.*;

class SenseBodyInfo
{
  // Player sensory information
	public float  stamina=0;
	public float  effort=0;
	public float  speed=0;
	public float  speed_angle=0;
	public float	head_angle=0;   
	public float    time=0;
	public int    kickCount=0;
	public int 	  dashCount=0;
	public int    turnCount=0;
	public int    sayCount=0;
	public int    turnNeckCount=0;
  public String m_message;

	private StringTokenizer	m_tokenizer;

  // Default constructor
	public SenseBodyInfo()
	{
		stamina=0;
		effort=0;
		speed=0;
		speed_angle=0;
		head_angle=0;
    		time=0;
	}

  // Constructor, which takes in a 'sense_body' server response, and parses it
	public SenseBodyInfo(String info)
	{
		info.trim();
		m_tokenizer = new StringTokenizer(info,"() ", true);
    m_message = new String(info);

    // Parse information
		parse();
	}

  // Method for parsing 'sense_body' command
	public void parse()
	{
//		String token = null;

		try
		{
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'sense_body'
			m_tokenizer.nextToken();	// ' '
			time=Float.valueOf(m_tokenizer.nextToken()).floatValue();  // TIME
      //System.out.println("Time = " + time + "  **************************");
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'view_mode'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// 'high | low'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// 'narrow | normal | wide'
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'stamina'
			m_tokenizer.nextToken();	// ' '
			stamina=Float.valueOf(m_tokenizer.nextToken()).floatValue();	// STAMINA
			m_tokenizer.nextToken();	// ' '
			effort=Float.valueOf(m_tokenizer.nextToken()).floatValue();	// EFFORT
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'speed'
			m_tokenizer.nextToken();	// ' '
			speed=Float.valueOf(m_tokenizer.nextToken()).floatValue();	// AMOUNT_OF_SPEED
			m_tokenizer.nextToken();	// ' '
			speed_angle=Float.valueOf(m_tokenizer.nextToken()).floatValue();	// ANGLE_OF_SPEED

			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			if (m_tokenizer.nextToken().equals("head_angle"))	// 'head_angle' v. 5.0+ only
			{
				m_tokenizer.nextToken();	// ' '
				head_angle=Float.valueOf(m_tokenizer.nextToken()).floatValue(); // ANGLE
				m_tokenizer.nextToken();	// ')'
				m_tokenizer.nextToken();	// ' '
				m_tokenizer.nextToken();	// '('
			}
			m_tokenizer.nextToken();	// 'kick'
			m_tokenizer.nextToken();	// ' '
      kickCount=Integer.valueOf(m_tokenizer.nextToken()).intValue(); // KICK_COUNT
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'dash'
			m_tokenizer.nextToken();	// ' '
      dashCount=Integer.valueOf(m_tokenizer.nextToken()).intValue(); // DASH_COUNT
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'turn'
			m_tokenizer.nextToken();	// ' '
      turnCount=Integer.valueOf(m_tokenizer.nextToken()).intValue(); // TURN_COUNT
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'say'
			m_tokenizer.nextToken();	// ' '
      sayCount=Integer.valueOf(m_tokenizer.nextToken()).intValue(); // SAY_COUNT
			m_tokenizer.nextToken();	// ')'
			m_tokenizer.nextToken();	// ' '
			m_tokenizer.nextToken();	// '('
			m_tokenizer.nextToken();	// 'turn_neck' v. 5.0+ only
			m_tokenizer.nextToken();	// ' '
        turnNeckCount=Integer.valueOf(m_tokenizer.nextToken()).intValue(); // TURN_NECK_COUNT
			m_tokenizer.nextToken();	// ')'

// lots more, but we'll ignore these for now...
// (catch 0) (move 19) (change_view 481) (arm (movable 0) (expires 0) (target 0 0) (count 0)) (focus (target l 7) (count 15(tackle (expires 0) (count 0))).


		}
		catch(Exception e)
		{   // just temporarily commented-out
    
			System.out.println("Error parsing sense_body information");
//			System.out.println(token);
			while( m_tokenizer.hasMoreTokens() ) System.out.print(m_tokenizer.nextToken());

			System.out.println(""); 
		}
	}
}
