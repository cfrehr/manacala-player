///////////////////////////////////////////////////////////////////////////////
// Main Class File:	Mancala.java
//
// File:       		frehrPlayer.java
// Description: 	Mancala AI implemented using:
//						- minimax search
//						- alpha-beta pruning
//						- static board evaluation function
//						- iterative-deepening time management
// 
// Run Args:		frehrPlayer
//
// Author:          Cody Frehr
// Course:        	CS 540: Intro to Artificial Intelligence
// Date:			2-21-2016
///////////////////////////////////////////////////////////////////////////////

/****************************************************************
 * Implements MiniMax search with A-B pruning and iterative deepening search (IDS). The static board
 * evaluator (SBE) function is simple: the # of stones in studPlayer's
 * mancala minue the # in opponent's mancala.
 * -----------------------------------------------------------------------------------------------------------------
 * Licensing Information: You are free to use or extend these projects for educational purposes provided that
 * (1) you do not distribute or publish solutions, (2) you retain the notice, and (3) you provide clear attribution to UW-Madison
 *
 * Attribute Information: The Mancala Game was developed at UW-Madison.
 *
 * The initial project was developed by Chuck Dyer(dyer@cs.wisc.edu) and his TAs.
 *
 * Current Version with GUI was developed by Fengan Li(fengan@cs.wisc.edu).
 * Some GUI componets are from Mancala Project in Google code.
 */

public class frehrPlayer extends Player {

	// Iterative Depth Search
	int alpha;
	int beta;
	int currentDepth;
    public void move(GameState state)
    {
    	alpha = Integer.MIN_VALUE;
    	beta = Integer.MAX_VALUE;
    	currentDepth = 1;
    	for(int maxDepth=1; maxDepth<Integer.MAX_VALUE; maxDepth++) {
    		this.move = maxAction(state,currentDepth,maxDepth,alpha,beta)[1];
    	}
    }

    //return best move for max player
    public int[] maxAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	// v = result[0], bin# = result[1];
    	int v = Integer.MIN_VALUE;
    	int[] result = new int[2];
    	result[0] = v;
    	int[] tempResult = new int[2];
    	int a = alpha;
    	int b = beta;
    	// if max depth value reached, get SBE
    	if(currentDepth == maxDepth) {
    		result[0] = sbe(state);
    		result[1] = 0;
    		return result;
    	// else, get children
    	} else {
    		// confirm that moves exist
    		boolean leaf = true;
    		for(int i=0; i<6; i++) {
    			if(!state.illegalMove(i)) {
    				leaf = false;
    			}
    		}
    		// if no moves available, get SBE
    		if(leaf) {
        		result[0] = sbe(state);
        		result[1] = 0;
        		return result;
    		}
    		// scan through moves
        	for(int i=0; i<6; i++) {
        		GameState tempState = new GameState(state);
        		// check if legal move and applyMove
        		if(!tempState.illegalMove(i)) {
        			boolean moveAgain = tempState.applyMove(i);
        			// if moveAgain true, call maxAction again
        			if(moveAgain) {
        				tempResult = maxAction(tempState,currentDepth+1,maxDepth,a,b);
        				tempResult[1] = i;
        			// else, call minAction
        			} else {
        				//tempState.rotate();
    					tempResult = minAction(tempState,currentDepth+1,maxDepth,a,b);
    					tempResult[1] = i;
        			}
        			// update v and a if better evaluation
        			if(tempResult[0]>result[0]) {
        				result[0] = tempResult[0];
        				result[1] = tempResult[1];
        				a = result[0];
        			}
        			// pruning check
        			if(a>b) {
        				return result;
        			}
        		}
        	}
        	return result;
    	}
    }
    //return best move for max player
    public int[] minAction(GameState state, int currentDepth, int maxDepth, int alpha, int beta)
    {
    	// v = result[0], bin# = result[1];
    	int v = Integer.MAX_VALUE;
    	int[] result = new int[2];
    	result[0] = v;
    	int[] tempResult = new int[2];
    	int a = alpha;
    	int b = beta;
    	// if maxDepth reached, get SBE
    	if(currentDepth == maxDepth) {
    		result[0] = sbe(state);
    		result[1] = 0;
    		return result;
    	// else, get children
    	} else {
    		// confirm that moves exist
    		boolean leaf = true;
    		for(int i=7; i<13; i++) {
    			if(!state.illegalMove(i)) {
    				leaf = false;
    			}
    		}
    		// if no moves available, get SBE
    		if(leaf) {
        		result[0] = sbe(state);
        		result[1] = 0;
        		return result;
    		}
    		// scan through moves
        	for(int i=7; i<13; i++) {
        		GameState tempState = new GameState(state);
        		// check if legal move and applyMove
        		if(!tempState.illegalMove(i)) {
        			boolean moveAgain = tempState.applyMove(i);
        			// if moveAgain true, call minAction again
        			if(moveAgain) {
        				tempResult = minAction(tempState,currentDepth+1,maxDepth,a,b);
        				tempResult[1] = i;
        			// else, call maxAction
        			} else {
    					tempResult = maxAction(tempState,currentDepth+1,maxDepth,a,b);
    					tempResult[1] = i;
        			}
        			// update v and b if better evaluation
        			if(tempResult[0]<result[0]) {
        				result[0] = tempResult[0];
        				result[1] = tempResult[1];
        				b = result[0];
        			}
        			// pruning check
        			if(a>b) {
        				return result;
        			}
        		}
        	}
        	return result;
    	}
    }

    //the sbe function for game state. Note that in the game state, the bins for current player are always in the bottom row.
    private int sbe(GameState state)
    {
    	// HEURISTICS
    	
    	// jar difference
    	int jarDiff = state.stoneCount(6) - state.stoneCount(13);
    	
    	// bin difference
    	int curBins = 0;
    	int oppBins = 0;
    	for (int i=0; i<6; i++) {
    		curBins = curBins + state.stoneCount(i);
    		oppBins = oppBins + state.stoneCount(7+i);
    	}
    	int binsDiff = curBins - oppBins;
    	
    	// stealing advantage
    	int opponentTake = 0;
		//for each bin on opp side
		for(int i=7; i<13; i++) {
			//if the stone count is zero
			if(state.stoneCount(i) == 0) {
				//if the stone count in our bin opposite theirs is large
				if(state.stoneCount(12-i) > opponentTake){
					//for every bin backwards
					for(int j=i-1; j>6; j--) {
						//if the stone count will land in bin i
						if(state.stoneCount(j) == i-j) {
							//update opponentTake
							opponentTake=state.stoneCount(12-i);
						}
					}
					//for every bin forwards
					for(int j = i+1; j<13; j++) {
						if(state.stoneCount(j) == 13-j+i) {
							//update opponentTake
							opponentTake=state.stoneCount(12-i);
						}
					}
				}
			}
			//or if the stone count is 13
			if(state.stoneCount(i) == 13) {
				//update opponentTake if larger
				if(state.stoneCount(12-i) > opponentTake) {
					opponentTake = state.stoneCount(12-i);
				}
			}
		}
    	
    	// multiple move advantage
    	boolean oppAgain = false;
		for(int i=12; i>6; i--) {
			if(state.stoneCount(i) == 13-i || state.stoneCount(i) == 26-i) {
				oppAgain = true;
			}
		}
		int oppAgainVal = 0;
		if(oppAgain) oppAgainVal = 10;
		
    	// SBE function
		int sbeVal = jarDiff + binsDiff - opponentTake - oppAgainVal;
		return sbeVal;
    }


}
