//
//  File:	  LibsClient.java
//  Author:	  Alan Wai
//  Date:	  2008/10/01
//
//
//  This class contains many routines ported (with modifications) from libsclient by I. Noda
//


package roboILP;
import java.util.*;
import java.io.*;
import java.lang.Math;
import java.lang.Double;
import java.awt.geom.*;
import java.awt.geom.Point2D.*;



class LibsClient
{

      //
      // Attributes used for estimate_current_pos
      //
      private SeenObjs  seen_points;
      private SeenObjs  seen_lines;

      public static final int UNKNOWN_POSITION         = 0;
      public static final int KNOWN_INSIDE             = 1;
      public static final int UNKNOWN_INSIDE           = 2;
      public static final int KNOWN_OUTSIDE            = -1;
      public static final int UNKNOWN_OUTSIDE          = -2;

      public static final int NUM_FLAGS    		= 54;
      public static final int NUM_GOALS    		= 2;
      public static final int NUM_LINES    		= 5;
      public static final int NUM_POINTS   		= NUM_FLAGS + NUM_GOALS;

      public static final double EPSILON          	= 3.0;
      public static final double PITCH_LENGTH		= 105.0;
      public static final double PITCH_WIDTH		= 68.0;
      public static final double PENALTY_AREA_LENGTH	= 16.5;
      public static final double PENALTY_AREA_WIDTH	= 40.32;
      public static final double GOAL_WIDTH		= 14.64;

      public static final double M_PI			= 3.1415927;
      public static final double POS_ERR		= -100.0;

      //
      // This threshold indicates the further distance that the estimate_current_pos
      // algorithm will still contain the flag/line/goal as valid.  Beyond this threshold,
      // the object will not be used to consider as part of the calculation.
      //
      // For Measurement Mode, may be making this smaller will help to ensure the accuracy
      // of the data more, at the cost of losing many models that could be valid.  However,
      // this is not a big problem as we have lots of sample data logs of Robocup.
      //
      // With DIST_THRESHOLD = PITCH_LENGTH/2, we have generated 2071 models out of 6000
      // (2601 models before abs pos is introduced).  Accuracy looks to be promising 
      // (visual check indicates ~80%).
      //
      // With DIST_THRESHOLD = PITCH_LENGTH/3, we have generated only 879 models.
      // However, it's found that two_flags and one_flag_and_line generates results that
      // are far too inaccurate.  Therefore, we need to take out those two routines.
      //
      // Retry DIST_THRESHOLD = PITCH_LENGTH/2, and take out those two routines.
      // 1884 models have been generated.
      //
      // Turns out there there was a bug in RobocupObjectInfo.java with the generation of
      // the flag ID, which explains the poor quality of the results above.
      //
      // After fixing that bug:
      //
      // (1)
      // - DIST_THRESHOLD set to 1000 (infinite)
      // - all routines are put back in place
      //
      // Results:
      //   After the fix, even the quality of two_flags and one_flag hve improved.  Howevr,
      //   the quality of one_flag is still quite rough (but not very bad), so we'll take it out.
      //   Overall, the quality of the positioning is very good, with no obvious errors
      //   detected by visual inspection.  A total of 2466 models (compared to 2601) were
      //   found.  The rest are due to not enough information to detect abs pos.
      //
      // (2)
      // - take out one_flag routine
      //   2388 models were found.  Qualities of triPoint and two_flags_and_line are very good.
      //
      //
      public static final double DIST_THRESHOLD		= 1000.0;

      public static final int LP_Unknown		= 0;
      public static final int LP_L			= 1;
      public static final int LP_T			= 2;
      public static final int LP_B			= 3;
      public static final int LP_R			= 4;

      public static final int FP_Unknown		= 0;
      public static final int FP_L_T			= 1;
      public static final int FP_L_B			= 2;
      public static final int FP_R_T			= 3;
      public static final int FP_R_B			= 4;
      public static final int FP_C_T			= 5;
      public static final int FP_C_B			= 6;
      public static final int FP_P_L_T			= 7;
      public static final int FP_P_L_C			= 8;
      public static final int FP_P_L_B			= 9;
      public static final int FP_P_R_T			= 10;
      public static final int FP_P_R_C			= 11;
      public static final int FP_P_R_B			= 12;
      public static final int FP_C			= 13;
      public static final int FP_G_L_T			= 14;
      public static final int FP_G_L_B			= 15;
      public static final int FP_G_R_T			= 16;
      public static final int FP_G_R_B			= 17;

      public static final int FP_T_L_10			= 18;
      public static final int FP_T_L_20			= 19;
      public static final int FP_T_L_30			= 20;
      public static final int FP_T_L_40			= 21;
      public static final int FP_T_L_50			= 22;
      public static final int FP_T_R_10			= 23;
      public static final int FP_T_R_20			= 24;
      public static final int FP_T_R_30			= 25;
      public static final int FP_T_R_40			= 26;
      public static final int FP_T_R_50			= 27;
      public static final int FP_B_L_10			= 28;
      public static final int FP_B_L_20			= 29;
      public static final int FP_B_L_30			= 30;
      public static final int FP_B_L_40			= 31;
      public static final int FP_B_L_50			= 32;
      public static final int FP_B_R_10			= 33;
      public static final int FP_B_R_20			= 34;
      public static final int FP_B_R_30			= 35;
      public static final int FP_B_R_40			= 36;
      public static final int FP_B_R_50			= 37;

      public static final int FP_L_T_10			= 38;
      public static final int FP_L_T_20			= 39;
      public static final int FP_L_T_30			= 40;
      public static final int FP_L_B_10			= 41;
      public static final int FP_L_B_20			= 42;
      public static final int FP_L_B_30			= 43;
      public static final int FP_R_T_10			= 44;
      public static final int FP_R_T_20			= 45;
      public static final int FP_R_T_30			= 46;
      public static final int FP_R_B_10			= 47;
      public static final int FP_R_B_20			= 48;
      public static final int FP_R_B_30			= 49;

      public static final int FP_T_0			= 50;
      public static final int FP_B_0			= 51;
      public static final int FP_L_0			= 52;
      public static final int FP_R_0			= 53;


      public static final int LEFT_GOAL_ID		= NUM_FLAGS;
      public static final int RIGHT_GOAL_ID		= (NUM_FLAGS+1);


      private double		m_line;
      private Point2D.Double[]	m_flags = {
		new Point2D.Double(POS_ERR, POS_ERR),		// FP_Unknown 
    
		new Point2D.Double(-PITCH_LENGTH/2.0, -PITCH_WIDTH/2.0),           /* FP_L_T */
		new Point2D.Double(-PITCH_LENGTH/2.0, PITCH_WIDTH/2.0),            /* FP_L_B */
		new Point2D.Double(PITCH_LENGTH/2.0, -PITCH_WIDTH/2.0),            /* FP_R_T */
		new Point2D.Double(PITCH_LENGTH/2.0, PITCH_WIDTH/2.0),             /* FP_R_B */
		new Point2D.Double(0.0, -PITCH_WIDTH/2.0),                         /* FP_C_T */
		new Point2D.Double(0.0, PITCH_WIDTH/2.0),                          /* FP_C_B */
		new Point2D.Double(-PITCH_LENGTH/2.0 + PENALTY_AREA_LENGTH, -PENALTY_AREA_WIDTH/2.0),  /* FP_P_L_T*/
		new Point2D.Double(-PITCH_LENGTH/2.0 + PENALTY_AREA_LENGTH, 0.0),  /* FP_P_L_C*/
		new Point2D.Double(-PITCH_LENGTH/2.0 + PENALTY_AREA_LENGTH,  
				   PENALTY_AREA_WIDTH/2.0),                        /* FP_P_L_B*/
		new Point2D.Double(PITCH_LENGTH/2.0 - PENALTY_AREA_LENGTH,   
				   -PENALTY_AREA_WIDTH/2.0),                       /* FP_P_R_T*/
		new Point2D.Double(PITCH_LENGTH/2.0 - PENALTY_AREA_LENGTH, 0.0),   /* FP_P_R_C */
		new Point2D.Double(PITCH_LENGTH/2.0 - PENALTY_AREA_LENGTH,  
				   PENALTY_AREA_WIDTH/2.0),                        /* FP_P_R_B */
    
		new Point2D.Double(0.0,0.0),      /* Center Mark */                 /* FP_C */
		     
		// Goal Posts 

		new Point2D.Double(-PITCH_LENGTH/2.0, -GOAL_WIDTH/2.0),   	   /* FP_G_L_T*/
		new Point2D.Double(-PITCH_LENGTH/2.0, GOAL_WIDTH/2.0),     /* FP_G_L_B*/
		new Point2D.Double(PITCH_LENGTH/2.0, -GOAL_WIDTH/2.0),    /* FP_G_R_T */
		new Point2D.Double(PITCH_LENGTH/2.0, GOAL_WIDTH/2.0),      /* FP_G_R_B */
    
		// Flags outside of the pitch 
    
		new Point2D.Double(-10,-PITCH_WIDTH/2.0 - 5),         /* FP_T_L_10 */
		new Point2D.Double(-20,-PITCH_WIDTH/2.0 - 5),         /* FP_T_L_20 */
		new Point2D.Double(-30,-PITCH_WIDTH/2.0 - 5),         /* FP_T_L_30 */
		new Point2D.Double(-40,-PITCH_WIDTH/2.0 - 5),         /* FP_T_L_40 */
		new Point2D.Double(-50,-PITCH_WIDTH/2.0 - 5),         /* FP_T_L_50 */
		new Point2D.Double(10,-PITCH_WIDTH/2.0 - 5),          /* FP_T_R_10 */
		new Point2D.Double(20,-PITCH_WIDTH/2.0 - 5),          /* FP_T_R_20 */
		new Point2D.Double(30,-PITCH_WIDTH/2.0 - 5),          /* FP_T_R_30 */
		new Point2D.Double(40,-PITCH_WIDTH/2.0 - 5),          /* FP_T_R_40 */
		new Point2D.Double(50,-PITCH_WIDTH/2.0 - 5),          /* FP_T_R_50 */
		new Point2D.Double(-10,PITCH_WIDTH/2.0 + 5),          /* FP_B_L_10 */
		new Point2D.Double(-20,PITCH_WIDTH/2.0 + 5),          /* FP_B_L_20 */
		new Point2D.Double(-30,PITCH_WIDTH/2.0 + 5),          /* FP_B_L_30 */
		new Point2D.Double(-40,PITCH_WIDTH/2.0 + 5),          /* FP_B_L_40 */
		new Point2D.Double(-50,PITCH_WIDTH/2.0 + 5),          /* FP_B_L_50 */
		new Point2D.Double(10,PITCH_WIDTH/2.0 + 5),           /* FP_B_R_10 */
		new Point2D.Double(20,PITCH_WIDTH/2.0 + 5),           /* FP_B_R_20 */
		new Point2D.Double(30,PITCH_WIDTH/2.0 + 5),           /* FP_B_R_30 */
		new Point2D.Double(40,PITCH_WIDTH/2.0 + 5),           /* FP_B_R_40 */
		new Point2D.Double(50,PITCH_WIDTH/2.0 + 5),           /* FP_B_R_50 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, -10),       /* FP_L_T_10 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, -20),       /* FP_L_T_20 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, -30),       /* FP_L_T_30 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, 10),        /* FP_L_B_10 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, 20),        /* FP_L_B_20 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, 30),        /* FP_L_B_30 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, -10),        /* FP_R_T_10 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, -20),        /* FP_R_T_20 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, -30),        /* FP_R_T_30 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, 10),         /* FP_R_B_10 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, 20),         /* FP_R_B_20 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, 30),         /* FP_R_B_30 */
    
		// Flags located 5 meters outside from the edge of the pitch.
    
		new Point2D.Double(0,-PITCH_WIDTH/2.0 - 5),           /* FP_T_0 */
		new Point2D.Double(0, PITCH_WIDTH/2.0 + 5),           /* FP_B_0 */
		new Point2D.Double(-PITCH_LENGTH/2.0 - 5, 0),         /* FP_L_0 */
		new Point2D.Double(PITCH_LENGTH/2.0 + 5, 0),          /* FP_R_0 */
    
		new Point2D.Double(-PITCH_LENGTH/2.0, 0.0), 	      /* LEFT_GOAL_ID */
		new Point2D.Double(PITCH_LENGTH/2.0, 0.0)             /* RIGHT_GOAL_ID */
      };

    
      private double[]		m_lines = {
		POS_ERR,  		// LP_Unknown 
		(-PITCH_LENGTH/2.0), 	// LP_L 
		(-PITCH_WIDTH/2.0),  	// LP_T
		(PITCH_WIDTH/2.0),   	// LP_B
		(PITCH_LENGTH/2.0)   	// LP_R
      };

      private PosState		m_pstate;
      private PosState		m_objPos;


	public LibsClient()
	{
	    m_pstate   = new PosState();
	    m_objPos   = new PosState();
	    seen_points	= new SeenObjs(NUM_POINTS);
	    seen_lines	= new SeenObjs(NUM_LINES);
	}


	/////////////////////////////////////////////////////////////////////////////
	//
	// Public functions
	//

	public void reset_seen_objects()
	{
	    seen_points.reset();
	    seen_lines.reset();
	}

	public PosState getPState()
	{
	    return m_pstate;
	}

	public PosState getObjPos()
	{
	    return m_objPos;
	}

	public void addSeenObj(boolean bPoints, int objId, double dist, double dir) 
	{
	    Estimate objEst = new Estimate(objId, dist, dir);
	    if(bPoints) {
	      seen_points.addEstimate(objEst);
	    } else {
	      seen_lines.addEstimate(objEst);
	    }
	}


	public void init_points_locations(char side)
	{
	  if(side == 'r') {
	    for(int i=0; i<NUM_POINTS; i++) {
	      m_flags[i].setLocation(-m_flags[i].getX(), -m_flags[i].getY());
	    }
	  }
	}


	public boolean estimate_current_pos(Vector currentObjs)
	{
	    boolean know_position = false;
	    int     guess_at_inside;
	    int     i;

/*
	    // TODO: this should be really done from within the parser for effiency
	    for(i = 0; i < currentObjs.size(); i++)
	    {
	      build_position_knowledge(((ObjectInfo)currentObjs.elementAt(i)));
	    }
*/

	    switch (seen_lines.getSize()) {
	      case 1: {
	        guess_at_inside = KNOWN_INSIDE;
		break;
	      }
	      case 0:
	      case 2: {
	        guess_at_inside = KNOWN_OUTSIDE;
		break;
	      }
	      default: {
	        System.err.println("Seen more than 2 lines!");
		guess_at_inside = UNKNOWN_INSIDE;
	        break;
	      }
	    }

	    // if didn't see anything, can't help at all
	    if ((seen_points.getSize() == 0) && (seen_lines.getSize() == 0)) {
	      System.out.println("LibsClient::estimate_current_pos: No flgs/lines/goals were seen - returning false.");
	      return false;
	    }

	    // sort by distance
	    sort_position_knowledge();


	    ////////////////////////////////////////////////////////////////////////
	    // now infer position

	    if (seen_points.getSize() > 2) {
	        System.out.println("LibsClient::estimate_current_pos: At least 3 points were seen - calling informed_triPoint...");
		seen_points.print();
	        know_position = informed_triPoint(guess_at_inside);
	    }

	    if (!know_position)		// seen_points.getSize() <= 2
	    {
	        // at least two flags and at least one line
	        if ((seen_points.getSize() > 1) && (seen_lines.getSize() > 0)) {
	            System.out.println("LibsClient::estimate_current_pos: At least 2 points and at least one line were seen - calling two_flags_and_line...");
		    seen_points.print();
		    seen_lines.print();
		    know_position = two_flags_and_line(guess_at_inside);
		}

		if (!know_position)
		{
		    // call two_flags using best two flags we have
		    if (seen_points.getSize() > 1) {
	              System.out.println("LibsClient::estimate_current_pos: At least 2 points were seen - calling two_flags...");
		      know_position = two_flags(guess_at_inside, 0, 1);
		    }

		    if (!know_position)
		    {
/*
		        // TODO: Note, guess_at_inside was not part of the parameter in the 
			//       original code. 
			// TODO: also, the original code did not check that there is indeed
			//       at least one flag and one line available.  Many times the 
			//       program crashed because this is not true.
			if ((seen_points.getSize() >= 1) & (seen_lines.getSize() >= 1)) {
	                  System.out.println("LibsClient::estimate_current_pos: one flag and line is called...");
		          know_position = one_flag_and_line(guess_at_inside);
			}
*/
			if (!know_position)
			{
			    System.out.println("LibsClient::estimate_current_pos: ERROR - not enough information to calculate abs pos.");
			}
		    }
		}    
	    }	// if (!know_position)

	    if (know_position) {
	        if(seen_points.getSize() != 0) {
		    // turns out if you look at closest object for angle,
		    // error increases dramatically.  We should be looking at the
		    // object furthest away from us (last index)
		    m_pstate.setDirection(
		    	calc_angle(m_pstate.getPosition(),
				m_flags[seen_points.getEstimate(seen_points.getSize()-1).getId()], 
				seen_points.getEstimate(seen_points.getSize()-1).getAngle())
		    );
		}
	    }

	    System.out.println("LibsClient::estimate_current_pos: returning " + know_position);
	    System.out.println("calculated position: " + m_pstate.getPosition() + ", angle: " + m_pstate.getDirection());
	    return know_position;
	}

	
	public boolean estimate_object_pos(double object_dist, double object_dir)
	{
	    double x = m_pstate.getPosition().x;
	    double y = m_pstate.getPosition().y;
	    double dir = m_pstate.getDirection();

	    m_objPos.setPosition((x + object_dist * Math.cos(Deg2Rad(dir+object_dir))),
				 (y + object_dist * Math.sin(Deg2Rad(dir+object_dir))));

	    return true;
	}



	/////////////////////////////////////////////////////////////////////////////
	//
	// Private functions
	//

	private void sort_position_knowledge()
	{
	    Arrays.sort(seen_points.getObjects(), 0, seen_points.getSize(), 
	    	new Comparator() {
		    public int compare(Object o1, Object o2)
		    {
		      double dist1 = ((Estimate)o1).getDistance();
		      double dist2 = ((Estimate)o2).getDistance();

		      if (dist1==dist2)
		        return 0;
		      else
		        return (dist1 > dist2 ? 1 : -1);
		    }
		}
	    );

	    Arrays.sort(seen_lines.getObjects(), 0, seen_lines.getSize(), 
	    	new Comparator() {
		    public int compare(Object o1, Object o2)
		    {
		      double dist1 = ((Estimate)o1).getDistance();
		      double dist2 = ((Estimate)o2).getDistance();

		      if (dist1==dist2)
		        return 0;
		      else
		        return (dist1 > dist2 ? 1 : -1);
		    }
		}
	    );
	}

	private boolean informed_triPoint(int guess_at_inside)
	{
	    double x1=0,x2=0,y1=0,y2=0,x3=0,y3=0,dis1=0,dis2=0,dis3=0;
	    Point2D.Double xy0, xy1;
	    Point2D.Double pq0, pq1;
	    Point2D.Double estPos1,estPos2;
	    int i;
	    int id1,id2,id3;
	    int maxPoints;

	    xy0 = new Point2D.Double();
	    xy1 = new Point2D.Double();
	    pq0 = new Point2D.Double();
	    pq1 = new Point2D.Double();
	    estPos1 = new Point2D.Double();
	    estPos2 = new Point2D.Double();

	    maxPoints = seen_points.getSize();


	    /* First estimation */

	    id1=seen_points.getEstimate(0).getId();
	    x1=m_flags[id1].getX();
	    y1=m_flags[id1].getY();
	    dis1=seen_points.getEstimate(0).getDistance();

	    id2=seen_points.getEstimate(1).getId();
	    x2=m_flags[id2].getX();
	    y2=m_flags[id2].getY();
	    dis2=seen_points.getEstimate(1).getDistance();
  
	    for (i=2; i<maxPoints ; i++) {

		id3=seen_points.getEstimate(i).getId();
		x3=m_flags[id3].getX();
		y3=m_flags[id3].getY();
		dis3=seen_points.getEstimate(i).getDistance();

		if (y1==y2) {
		    if (y1!=y3) break;
		}
		else if ((x1-x2)*(y1-y3)/(y1-y2) != (x1-x3)) break;
	    } // for

	    if (i>=maxPoints) return false;
  
	    triDistance(x1,y1,dis1,x2,y2,dis2,xy0,xy1);
	    triDistance(x1,y1,dis1,x3,y3,dis3,pq0,pq1);

	    bestPoint(xy0,xy1,pq0,pq1,estPos1);

	    /* Second estimation */

	    id2=seen_points.getEstimate(2).getId();
	    x2=m_flags[id2].getX();
	    y2=m_flags[id2].getY();
	    dis2=seen_points.getEstimate(2).getDistance();
  
	    for (i=3; i<maxPoints ; i++) {

		id3=seen_points.getEstimate(i).getId();
		x3=m_flags[id3].getX();
		y3=m_flags[id3].getY();
		dis3=seen_points.getEstimate(i).getDistance();

		if (y1==y2) {
		    if (y1!=y3) break;
		}
		else if ((x1-x2)*(y1-y3)/(y1-y2) != (x1-x3)) break;
	    } // for

	    if (i>=maxPoints) {
	        m_pstate.setPosition(estPos1.getX(), estPos1.getY());
		return true;
	    }
  
	    triDistance(x1,y1,dis1,x2,y2,dis2,xy0,xy1);
	    triDistance(x1,y1,dis1,x3,y3,dis3,pq0,pq1);

	    bestPoint(xy0,xy1,pq0,pq1,estPos2);

	    i = common_sense_disambig(estPos1, estPos2, guess_at_inside);

	    if ((UNKNOWN_INSIDE == i) || (UNKNOWN_OUTSIDE == i))
	    {
	        m_pstate.setPosition((estPos1.x+estPos2.x)/2, (estPos1.y+estPos2.y)/2);
	    }
	    return true;
	}


	/* This function computes the global position of (x,y) based on the distnace to
	 * (x1,y1)=d1 and (x2,y2) = d2 , it returns 2 points (x,y) and (p,q)
	 * I use of a rotation of x2  around x1  and a relation to get to the x */

	private boolean triDistance (double x1, double y1,double d1,double x2, double y2,double d2, Point2D.Double xy, Point2D.Double pq)

	{
	    double cos_1, sin_1, cos_2, sin_2, dd, d, temp, xt1, xt2, yt1, yt2;    /* dd = d*d */

   
	    /*   fprintf(stdout,"triDistance function \n");*/
	    dd = (x2-x1)* (x2-x1) + (y2-y1) * (y2-y1);
 
	    d = Math.sqrt(dd);
 
	    if (d == 0) {
		return false;
	    }
	    else if( d1 == 0 ) {
	        xy.setLocation(x1, y1);
	        pq.setLocation(x1, y1);
	    }
	    else if (d2 == 0) {
	        xy.setLocation(x2, y2);
	        pq.setLocation(x2, y2);
	    }
	    else{
		cos_1 = (d1*d1 + dd - d2*d2)/(2.0 * d * d1);
		if (cos_1 > 1) {
		    cos_1 = 1;
		}
		else if (cos_1 < -1) {
		    cos_1 = -1;
		}
    
		sin_1 = Math.sqrt( 1 - (cos_1 * cos_1));
    
		x2 = x2 - x1;  /* change the center to x1 */
		y2 = y2 - y1;
    
		xt1 = (d1 / d) * ((x2 * cos_1) + (y2 * sin_1));
		yt1 =  (d1 / d) * ((-x2 * sin_1) + (y2 * cos_1));
    
		xt2 = (d1 / d) * ((x2 * cos_1) + (-y2 * sin_1));
		yt2 =  (d1 / d) * ((x2 * sin_1) + (y2 * cos_1));
 
		xt1 = xt1 + x1;
		yt1 = yt1 + y1;

		xt2 = xt2 + x1;
		yt2 = yt2 + y1;

	        xy.setLocation(xt1, yt1);
	        pq.setLocation(xt2, yt2);
	    }
  	    return true;

	}  // triDistance


	private boolean bestPoint (Point2D.Double xy1, Point2D.Double pq1, Point2D.Double xy2, Point2D.Double pq2, Point2D.Double xy)
	{
	    //
	    // These were declared as static in C, but since they are assigned to new values
	    // below anyways, they don't reqlly require storage outside the scope of the function.
	    //
	    double [] dist = new double[4];
	    double min;
	    int i, item;

	    double x1 = xy1.getX();
	    double y1 = xy1.getY();
	    double x2 = xy2.getX();
	    double y2 = xy2.getY();
	    double p1 = pq1.getX();
	    double q1 = pq1.getY();
	    double p2 = pq2.getX();
	    double q2 = pq2.getY();

	    dist[0] = Math.sqrt((x2-x1) * (x2-x1) + (y2 - y1)* (y2-y1));
	    dist[1] = Math.sqrt((x2-p1) * (x2-p1) + (y2 - q1)* (y2-q1));
	    dist[2] = Math.sqrt((p2-x1) * (p2-x1) + (q2 - y1)* (q2-y1));
	    dist[3] = Math.sqrt((p2-p1) * (p2-p1) + (q2 - q1)* (q2-q1));

	    item = 0;
	    min = dist[0];
	    for (i=1; i< 4; i++) {
		if (dist[i] <= min) {
		    item = i;
		    min = dist[i];
		}
	    }

	    if( item == 0 || item == 2) {
	        xy.setLocation(x1,y1); 
	    }
	    else if (item == 1 || item ==3) {
	        xy.setLocation(p1,q1); 
	    }
	    return true;
	}  

	int common_sense_disambig(Point2D.Double p, Point2D.Double q, int inside_guess)
	{
	    /* returning:
	       UNKNOWN_INSIDE   for both points in
	       UNKNOWN_OUTSIDE  for both points out
	       KNOWN_INSIDE/KNOWN_OUTSIDE for successful disambiguation
	    */

	    if (UNKNOWN_INSIDE == inside_guess) {
		if (  reasonable(q.x, q.y) &&(!reasonable(p.x, p.y))) {
		    m_pstate.setPosition(q.x, q.y);
		    return (in_field(q.x, q.y) ? KNOWN_INSIDE : KNOWN_OUTSIDE);
		}
	        else if (  (reasonable(p.x, p.y)) &&(!reasonable(q.x, q.y))) {
		    m_pstate.setPosition(p.x, p.y);
		    return (in_field(p.x, p.y) ? KNOWN_INSIDE : KNOWN_OUTSIDE);
		}
	    }
	    else if (KNOWN_INSIDE == inside_guess) {
		if (  (in_field(q.x, q.y)) &&(!in_field(p.x, p.y))) {
		    m_pstate.setPosition(q.x, q.y);
		    return KNOWN_INSIDE;
		}
	        else if (  (in_field(p.x, p.y)) &&(!in_field(q.x, q.y))) {
		    m_pstate.setPosition(p.x, p.y);
	  	    return KNOWN_INSIDE;
		}
	    }
	    else if (KNOWN_OUTSIDE == inside_guess) {
		if (!in_field(q.x, q.y)) {
		    if (  (!reasonable(p.x, p.y)) ||(in_field(p.x, p.y))) {
		        m_pstate.setPosition(q.x, q.y);
			return KNOWN_OUTSIDE;
	    	    }
		}
		if (!in_field(p.x, p.y)) {
		    if (  (!reasonable(q.x, q.y)) || (in_field(q.x, q.y))) {
		        m_pstate.setPosition(p.x, p.y);
			return KNOWN_OUTSIDE;
		    }
		}
	    }

	    if ((Math.abs(p.x-q.x) <= EPSILON) &&(Math.abs(p.y-q.y) <= EPSILON)) {
		m_pstate.setPosition((p.x+q.x)/2.0, (p.y+q.y)/2.0);

		return (((in_field(p.x, p.y)) && (in_field(q.x, q.y))) ? KNOWN_INSIDE : KNOWN_OUTSIDE);
	    }      

	    return (((in_field(p.x, p.y)) && (in_field(q.x, q.y))) ? UNKNOWN_INSIDE : UNKNOWN_OUTSIDE);
	}  // common_sense_disambig


	boolean two_flags_and_line(int guess_at_inside)
	{
	    // (f1_x,f1_y) & (f2_x,f2_y) are the xy-coordinates of the flag that I see
	    //	d1,d2 are the distances to the flags angle1,angle2 are the two angles to 
	    //  the flags  
  
	    Point2D.Double p,p1,p2;
	    Point2D.Double [] four_points = new Point2D.Double[4];
	    int f1=0,f2=0,i=0;
	    int num_lines = seen_lines.getSize();
	    double dist1, dist2;
	    int l1, l2;
	    double d1,d2;	// distance variables
	    double x, y;
	    double f1_x, f1_y, f_d1, angle1, f2_x, f2_y, f_d2, angle2;

	    p1 = new Point2D.Double();
	    p2 = new Point2D.Double();

	    // we know there are at least two flags.  Use nearest ones

	    f1_x=m_flags[seen_points.getEstimate(0).getId()].x;
	    f1_y=m_flags[seen_points.getEstimate(0).getId()].y;
	    f_d1=seen_points.getEstimate(0).getDistance();
	    angle1=seen_points.getEstimate(0).getAngle();

	    f2_x=m_flags[seen_points.getEstimate(1).getId()].x;
	    f2_y=m_flags[seen_points.getEstimate(1).getId()].y;
	    f_d2=seen_points.getEstimate(1).getDistance();
	    angle2=seen_points.getEstimate(1).getAngle();

	    // from two flags you can find two possible points: 
	    // First calculate P1:(x1,y1 & P2:(x2,y2) then use 
	    // any lines that you can see to dismbigute 
  
	    // Calculate P1 & P2 here  ......
	    triDistance(f1_x,f1_y,f_d1,f2_x,f2_y,f_d2,p1,p2);

	    // l1 used as temp here
	    l1 = common_sense_disambig(p1, p2, guess_at_inside);

	    if ((KNOWN_INSIDE == l1) ||(KNOWN_OUTSIDE == l1)) {
		return true;
	    }


	    // Now I have P1&P2 but I want to disambiguate
  
	    if (num_lines == 2) {		// I can see two lines l1 & l2 
		// set distance to l1 is dist1, and to l2 is dist2 
		// and the angle to l1 is angle1, and to l2 is angle2 

		dist1  = (seen_lines.getEstimate(0)).getDistance();
		dist2  = (seen_lines.getEstimate(1)).getDistance();
		angle1 = (seen_lines.getEstimate(0)).getAngle();
		angle2 = (seen_lines.getEstimate(1)).getAngle();
		l1 = (seen_lines.getEstimate(0)).getId();
		l2 = (seen_lines.getEstimate(1)).getId();

		// now calculate the perpendicular distance to l1 and l2 
		d1 = perpend_dist_from_line(dist1,angle1);
		d2 = perpend_dist_from_line(dist2,angle2);

		if (is_perpend(l1,l2)) {
		    // the two lines are perpendicular to each other.
		    // the info from these two lines give four possible 
		    // points, hopefully only one will intersect
		    // with the other two that were calculated
		    get_four_points(l1,d1,l2,d2, 
		    			(four_points[0]), (four_points[1]), 
					(four_points[2]), (four_points[3]));

		    for (i = 0 ; i < 4  ; i++) {
			if (semi_equal(four_points[i].x,p1.x,EPSILON) &&
			    semi_equal(four_points[i].y,p1.y,EPSILON))
				f1++;
			if (semi_equal(four_points[i].x,p2.x,EPSILON) &&
			    semi_equal(four_points[i].y,p2.y,EPSILON))
				f2++;
		    } // for

		    if (f1 != 0) // there is an intersection between the four_points and P1
			if (f2 != 0) // check if there is intersection with P2 also
			    return false; // if both P1&P2 intersects - then I can't disambiguate
			else { // only P1 intersects 
			    x = p1.x;
			    y = p1.y;
			}
		    else if (f2 != 0) { // only P2 intersects 
			x = p2.x;
			y = p2.y;
		    }
		    else return false; // none intersects 

		} // if (is_perpend)

		else // l1 & l2 are parallel
		{
		    if (!line_equation_of(l1,d1,l2,d2))
			return false;

		    if (is_vertical(l1)) {	// both lines are vertical lines
			if (semi_equal(p1.x,m_line,EPSILON)) // P1 is on line l 
			    if (semi_equal(p2.x,m_line,EPSILON)) // but also P2 is on line l 
				// then both P1 & P2 are possible - what can I do? 
				// For now just return FALSE 
				return false;
			    else { 	// p1 is on l, but p2 is not, then pick p1 
				x = p1.x ;
				y = p1.y ;
			    }
			else // P1 is not on line l 
			if (semi_equal(p2.x,m_line,EPSILON)) {	// but P2 is on line l
				// p1 is not on l but p2 is, then pick P2
			    x = p2.x ;
			    y = p2.y ;
			}
			else /* both p1&p2 are not on l */
			    return false;
		    }
		    else /* both lines are vertical */ 
	    	    {
			if (semi_equal(p1.y,m_line,EPSILON)) // P1 is on line l 
			    if (semi_equal(p2.y,m_line,EPSILON)) // but also P2 is on line l
				// then both P1 & P2 are possible - what can I do? 
				// For now just return FALSE
				return false;
			    else {		// p1 is on l, but p2 is not, then pick p1 
				x = p1.x ;
				y = p1.y ;
			    }
			else			 // P1 is not on line l
			if (semi_equal(p2.y,m_line,EPSILON)) {	// but P2 is on line l
			    // p1 is not on l but p2 is, then pick P2 
			    x = p2.x ;
			    y = p2.y ;
			}
			else // both p1&p2 are not on l 
			    return false;
		    } // end of both lines are vertical
		} // end of that lines are parallel
	    } // end of num_lines == 2 

	    else if (num_lines == 1) // I can see only one line 
	    {
		d1  = (seen_lines.getEstimate(0)).getDistance();
		angle1 = (seen_lines.getEstimate(0)).getAngle();
		l1 = (seen_lines.getEstimate(0)).getId();
      
		if (is_vertical(l1)) /* is l1 a vertical line */
		{
		    if (collide(p1.x,line_eq(l1),d1)) /* P1  lies on line of distance d1 from l1 */
			if (collide(p2.x,line_eq(l1),d1)) /* also P2 lies there */
			    /* both P1 & P2 are possible - For now return FALSE */
			    return false;
			else  /* only P1 is possible */
			{
			    x = p1.x ;
			    y = p1.y ;
			}
		    else /* P1 is no possible */
		    if (collide(p2.x,line_eq(l1),d1)) /*  P2 lies there */
		    {
			x = p2.x ;
			y = p2.y ;
		    }
		    else /* both P1 & P2 do not lie on l1 - then both are not possible*/
			return false;
		} // end of l1 vertical

		else /* l1 is horizontal */
		{
		    if (collide(p1.y,line_eq(l1),d1)) /* P1  lies on line of distance d1 from l1 */
			if (collide(p2.y,line_eq(l1),d1)) /* also P2 lies there */
			    /* both P1 & P2 are possible - For now return FALSE */
			    return false;
			else  /* only P1 is possible */
			{
			    x = p1.x ;
			    y = p1.y ;
			}
		    else /* P1 is no possible */
		    if (collide(p2.y,line_eq(l1),d1)) /*  P2 lies there */
		    {
			x = p2.x ;
			y = p2.y ;
		    }
		    else /* both P1 & P2 do not lie on l1 - then both are not possible*/
			return false;
		} /* end of l1 horizontal */
	    } /* end of num_lines == 1 */

	    else { /* num_lines seen == 0 */
		return false;
	    }

	    m_pstate.setPosition(x, y);

	    return true;
	}


	private boolean two_flags(int inside_guess, int flag1_index, int flag2_index)
	{
	    double x1,x2,y1,y2,dis1,dis2;
	    Point2D.Double p, q;
	    int i;
	    int id1,id2;

	    p = new Point2D.Double();
	    q = new Point2D.Double();

	    if ((flag1_index >= seen_points.getSize()) || (flag2_index >= seen_points.getSize()))
		return false;

	    id1=seen_points.getEstimate(flag1_index).getId();
	    x1=m_flags[id1].x;
	    y1=m_flags[id1].y;
	    dis1=seen_points.getEstimate(flag1_index).getDistance();

	    id2=seen_points.getEstimate(flag2_index).getId();
	    x2=m_flags[id2].x;
	    y2=m_flags[id2].y;
	    dis2=seen_points.getEstimate(flag2_index).getDistance();
  
	    triDistance(x1,y1,dis1,x2,y2,dis2,p,q);

	    i = common_sense_disambig(p, q, inside_guess);

	    if ((KNOWN_INSIDE  == i) || (KNOWN_OUTSIDE == i))
		return true;

	    return false;
	}

	private boolean reasonable_x(double x) {
	  return (((x)<((PITCH_LENGTH/2.0) + 5.0)) && ((x)>((-PITCH_LENGTH/2.0) - 5.0)));
	}
	private boolean reasonable_y(double x) {
	  return (((x)<((PITCH_WIDTH/2.0) + 5.0)) && ((x)>((-PITCH_WIDTH/2.0) - 5.0)));
	}
	private boolean reasonable(double x, double y) {
	  return (reasonable_x(x) && reasonable_y(y));
	}


	private boolean in_field_x(double x) {
	  return (((x)<=(PITCH_LENGTH/2.0)) && ((x)>=(-PITCH_LENGTH/2.0)));
	}
	private boolean in_field_y(double x) {
	  return (((x)<=(PITCH_WIDTH/2.0)) && ((x)>=(-PITCH_WIDTH/2.0)));
	}
	private boolean in_field(double x,double y) {
	  return (in_field_x(x) && in_field_y(y));
	}

	private double line_eq(int l) {
	  return m_lines[l];
	}

	private boolean is_vertical(int l) {
	  return (((l) == LP_L) || ((l) == LP_R));
	}

	private boolean semi_equal(double n1, double n2, double epsilon) {
	  return (Math.abs((n1)-(n2)) < (epsilon));
	}

	private double Rad2Deg(double x) {
	  return ((double)(x) * 180.0 / M_PI);
	}

	private double Deg2Rad(double x) {
	  return ((double)(x) * M_PI / 180.0);
	}

	private double NormalizeAngle(double angle) {
	    double retval;

	    // first make sure |retval| < 360
	    retval = angle - (int)(angle/360.)*360;
	    if (retval > 180.0) {
		retval -= 360.0;
	    } else if (retval < -180.0) {
		retval += 360.0;
	    }
	    return retval;
	}

	private double perpend_dist_from_line(double dist, double angle) {
	  return (dist * (Math.cos(Deg2Rad(90.0-Math.abs(angle)))));
	}

	private boolean line_equation_of(int l1, double d1, int l2,double d2)
	    // This function finds the line equation that satisfy the following
	    //	constraint:
	    //	it is of distance d1 from l1, and d2 from l2 ;
	    //	note that l1&l2 are supposed to be parallel
     
	{
	    double line1,line2,line3,line4 ;
  
	    line1 = line_eq(l1) - d1 ;
	    line2 = line_eq(l1) + d1 ;
	    line3 = line_eq(l2) - d2 ;
	    line4 = line_eq(l2) + d2 ;
  
	    if (semi_equal(line1,line3,EPSILON) || semi_equal(line1,line4,EPSILON))
		m_line = line1;
	    else if (semi_equal(line2,line3,EPSILON) || semi_equal(line2,line4,EPSILON))
		m_line = line2;
	    else
		return false;

	    return true;
	}

	private boolean is_perpend(int l1, int l2)
	{
	    // This function checks if the l1 & l2 are perpendicular -
	    //  l1,l2 arenames of lines (e.g. For LP_T & LP_B function return FALSE )
  
	    if (((l1 == LP_T || l1 == LP_B) && (l1 == LP_T || l1 == LP_B)) ||
		((l2 == LP_L || l1 == LP_R) && (l2 == LP_L || l1 == LP_R)))
		return true;
	    else return false;
	}

	private boolean collide(double x, double l, double d)
	{
	    // This function checks if the line parallel to l and of distance d
	    // (notice that there are two possible lines of distance d from l)
	    // has the collides with the line x (i.e they are the same line within 
	    // a certain EPSILON
  
	    if (semi_equal(x,(l+d),EPSILON) || semi_equal(x,(l-d),EPSILON))
		return true;
	    else return false;
	}

  
	private void get_four_points(int l1, double d1, int l2, double d2,
		     Point2D.Double p1, Point2D.Double p2, Point2D.Double p3, Point2D.Double p4)
	{
	    // This function takes two lines parallel two l1 and of distance d1 from l1,
	    // and two lines parallel two l2 and of distance d2 of l2 
	    // The function returns the four intersection points of these lines (i.e.
	    // the heads of the triangle 

	    double x1,x2,y1,y2 ;

	    if (is_vertical(l1)) // of l1 is vertical then it should give you the x-coord
	    {
		x1 = line_eq(l1) - d1 ;
		x2 = line_eq(l1) + d1 ;
		y1 = line_eq(l2) - d2 ;
		y2 = line_eq(l2) + d2 ;
	    }
	    else {
		y1 = line_eq(l1) - d1 ;
		y2 = line_eq(l1) + d1 ;
		x1 = line_eq(l2) - d2 ;
		x2 = line_eq(l2) + d2 ;
	    }
  
  	    p1.setLocation(x1, y1);
  	    p2.setLocation(x1, y2);
  	    p3.setLocation(x2, y1);
  	    p4.setLocation(x2, y2);
	}

	private boolean inside(Point2D.Double p) {
	  return in_field(p.x, p.y);
	}

	private boolean one_flag_and_line(int guess_inside)
	{
	    // (f_x,f_y) are the xy-coordinates of the flag that I see
	    // d is the distances to the flag,  and f_angle is the angle
	    // We see one line only (so we can conclude that we are inside
	    // the field 
  
	    Point2D.Double p1,p2;
	    int l1;
	    int temp_result;
	    double dist,d ; // distance variables
	    double f_x, f_y, f_d, f_angle,angle,x,y,temp;

	    p1 = new Point2D.Double();
	    p2 = new Point2D.Double();

	    // we know there is at least one flag.  Use nearest one 
	    f_x=m_flags[seen_points.getEstimate(0).getId()].x;
	    f_y=m_flags[seen_points.getEstimate(0).getId()].y;
	    f_d=seen_points.getEstimate(0).getDistance();
	    f_angle=seen_points.getEstimate(0).getAngle();
	    dist  = (seen_lines.getEstimate(0)).getDistance();
	    angle = (seen_lines.getEstimate(0)).getAngle();
	    l1 = (seen_lines.getEstimate(0)).getId();

	    // now calculate the perpendicular distance to l1 
	    d = perpend_dist_from_line(dist,angle);

	    if (is_vertical(l1)) 	// if the line we see is vertical then we can find 
					// the x-coordinate 
	    {
		if (line_eq(l1) > 0) /* choose the inside point */
		    x = line_eq(l1) - d ;
		else x = line_eq(l1) + d ;
		p1.x = p2.x = x ;
		temp = (f_d*f_d) - ((x-f_x)*(x-f_x)) ;
		if ((temp < 0)&&(temp > -1))
		    temp = 0 ;
		if (temp < 0)
		    return false;
		p1.y = f_y + Math.sqrt(temp);
		p2.y = f_y - Math.sqrt(temp);
		temp_result = common_sense_disambig(p1, p2, guess_inside);
		if (  (KNOWN_INSIDE   == temp_result))
		    return true;
	    }
	    else  // if the line we see is horizontal then we can find the y-coordinate
	    {
		if (line_eq(l1) > 0) /* choose the inside point */
		    y = line_eq(l1) - d ;
		else y = line_eq(l1) + d ;
		    p1.y = p2.y = y ;
		temp = (f_d*f_d) - ((y-f_y)*(y-f_y));
		if ((temp < 0)&&(temp > -1))
		    temp = 0 ;
		if (temp < 0)
		    return false;
		p1.x = f_x + Math.sqrt(temp);
		p2.x = f_x - Math.sqrt(temp);
		temp_result = common_sense_disambig(p1, p2, guess_inside);
		if (  (KNOWN_INSIDE   == temp_result))
		    return true;
	    }  
	    return false;
	}

	private double calc_angle(Point2D.Double my_pos, Point2D.Double other_pos, double theta) {
	    return (NormalizeAngle(Rad2Deg(Math.atan2(other_pos.y-my_pos.y,other_pos.x-my_pos.x))-theta));
	}


}	// class LibsClient



////////////////////////////////////////////////////////////////////////////
//
// Classes ported directly from libsclient for estimate_current_pos
//

class Estimate extends Object {
  private int m_id;
  private double m_distance;
  private double m_angle;

  Estimate() {
    m_id = -1;
    m_distance = 0;
    m_angle = 0;
  }

  Estimate(int id, double dist, double angle) {
    m_id = id;
    m_distance = dist;
    m_angle = angle;
  }

  int getId() {
    return m_id;
  }

  double getDistance() {
    return m_distance;
  }

  double getAngle() {
    return m_angle;
  }

  public void print() {
    System.out.println("m_id: " + m_id + ", m_distance: " + m_distance + ", m_angle: " + m_angle);
  }
}


class SeenObjs {
  private Estimate [] objects = null;
  private int numObjs = 0;
  private int m_capacity;

  SeenObjs(int capacity) {
    objects = new Estimate [capacity];
    numObjs = 0;
    m_capacity = capacity;
  }

  public void addEstimate(Estimate obj) {
    objects[numObjs] = obj;
    numObjs++;
  }

  public int getSize() {
    return numObjs;
  }

  public Estimate getEstimate(int idx) {
    if(numObjs == 0) return null;
    return objects[idx];
  }

  public Estimate[] getObjects() {
    return objects;
  }

  public void reset() {
    objects = new Estimate [m_capacity];
    numObjs = 0;
  }
  
  public void print() {
    for(int i=0; i<numObjs; i++) {
      objects[i].print();
    }
  }
}


class PosState {
  Point2D.Double m_position;
  double	 m_dir;

  PosState() {
    m_position = new Point2D.Double();
    m_dir = 0.0;
  }

  public void setPosition(double x, double y) {
    m_position.setLocation(x, y);
  }

  public void setDirection(double dir) {
    m_dir = dir;
  } 

  public Point2D.Double getPosition() {
    return m_position;
  }

  public double getDirection() {
    return m_dir;
  }
}



    
