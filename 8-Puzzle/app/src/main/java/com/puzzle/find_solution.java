package com.puzzle;

import com.droid8puzzle.R;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Stack;

public class find_solution extends Thread {
	EightPuzzle start;
	EightPuzzle goal;
	Stack<EightPuzzle> solution;
	find_solution(EightPuzzle initial_state,EightPuzzle goal_state)
	{
	  this.start=initial_state;
	  this.goal=goal_state;
	}
	public void run()
	{
		System.out.println("Started");
        if (start.inversions() % 2 == 1) {
            System.out.println("Unsolvable");
            return;
        }
        LinkedList<EightPuzzle> closedset = new LinkedList<EightPuzzle>();
        PriorityQueue<EightPuzzle> openset = new PriorityQueue<EightPuzzle>();

        openset.add(start);
        int i=0;
        while (!openset.isEmpty()) {
            EightPuzzle x = openset.poll();
            if (x.mapEquals(goal)) {
                Stack<EightPuzzle> solution = reconstruct(x);
                break;
            }
            closedset.add(x);
            LinkedList<EightPuzzle> neighbor = x.getChildren();
            while (!neighbor.isEmpty()) {
                EightPuzzle y = neighbor.removeFirst();
                if (closedset.contains(y)) {
                    continue;
                }
                openset.add(y);
            }
            System.out.println(i++);
        }
	}
	 public  Stack<EightPuzzle> reconstruct(EightPuzzle winner) {
	        solution = new Stack<EightPuzzle>();

	        while (winner.getParent() != null) {
	            solution.add(winner);
	            winner = winner.getParent();
	        }

	        return solution;
	    }

}
