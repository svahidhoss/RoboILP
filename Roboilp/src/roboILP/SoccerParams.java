package roboILP;
import java.awt.Color;

class SoccerParams
{
  final static int simulator_step = 100;
  final static double catchRadius = 14.0; // not currently used

  // Constants used for drawing objects on the canvas
  final static double field_length = 105;
  final static double field_width = 68;
  final static Color fieldcolor = Color.green.darker();
  final static Color conecolor = Color.green;
  final static Color goalcolor = Color.yellow;
  final static Color ballcolor = Color.white;
  final static Color opponentcolor = Color.red;
  final static Color teamcolor = Color.blue;
  final static Color unknownplayercolor = Color.magenta.darker();
  final static Color linecolor = Color.white;
  final static Color flagcolor = Color.lightGray;
  final static Color selectedColor = Color.cyan;
  final static int ball_width = 4;
  final static int player_width = 6;
  final static int goalpost_width = 5;
  final static int line_width = 10;
  final static int flag_width = 2;
  final static int centrecircle_width = 105;

  final static char SIDE_LEFT	= 'l';
  final static char SIDE_RIGHT	= 'r';

}

