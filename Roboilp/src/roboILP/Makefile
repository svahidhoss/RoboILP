default:
	javacc RobocupCmdParser.jj
#	javac -deprecation RoboILP.java ILPBrain.java Controller.java ObjectInfo.java Action.java Memory.java SenseBodyInfo.java Clause.java SoccerInterfaces.java SoccerParams.java Literal.java Variable.java Model.java ModelObject.java RobocupCmdParserConstants.java RobocupCmdParser.java RobocupCmdParserTokenManager.java Token.java TokenMgrError.java RobocupObjectInfo.java RobocupController.java SimpleCharStream.java ParseException.java performance/StatisticsWrapper.java
	javac -deprecation ILPBrain.java Controller.java ObjectInfo.java Action.java Memory.java SenseBodyInfo.java SoccerInterfaces.java SoccerParams.java Model.java ModelObject.java RobocupCmdParserConstants.java RobocupCmdParser.java RobocupCmdParserTokenManager.java Token.java TokenMgrError.java RobocupObjectInfo.java RobocupController.java SimpleCharStream.java ParseException.java RoboILP.java

clean:
	rm *.class

warn:
	javac -deprecation -Xlint:unchecked RoboILP.java ILPBrain.java Controller.java ObjectInfo.java SoccerInterfaces.java SoccerParams.java Action.java Memory.java SenseBodyInfo.java Clause.java Literal.java Model.java ModelObject.java RobocupCmdParserConstants.java RobocupCmdParser.java RobocupCmdParserTokenManager.java Token.java TokenMgrError.java RobocupObjectInfo.java RobocupController.java SimpleCharStream.java ParseException.java 
