package com.puzzle;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.droid8puzzle.R;
import java.util.*;

public class EightPuzzle implements Comparable<Object> {

    int[] puzzle = new int[9];
    int h_n = 0;
    int hueristic_type = 0;
    int g_n = 0;
    int f_n = 0;
    EightPuzzle parent = null;

    public EightPuzzle(int[] p, int h_type, int cost) {
        this.puzzle = p;
        this.hueristic_type = h_type;
        this.h_n = (h_type == 1) ? h1(p) : h2(p);
        this.g_n = cost;
        this.f_n = h_n + g_n;
    }

    public int getF_n() {
        return f_n;
    }

    public void setParent(EightPuzzle input) {
        this.parent = input;
    }

    public EightPuzzle getParent() {
        return this.parent;
    }

    public int inversions() {
        /*
         * Definition: For any other configuration besides the goal,
         * whenever a tile with a greater number on it precedes a
         * tile with a smaller number, the two tiles are said to be inverted
         */
        int inversion = 0;
        for (int i = 0; i < this.puzzle.length; i++) {
            for (int j = 0; j < i; j++) {
                if (this.puzzle[i] != 0 && this.puzzle[j] != 0) {
                    if (this.puzzle[i] < this.puzzle[j]) {
                        inversion++;
                    }
                }
            }
        }
        return inversion;
    }

    public int h1(int[] list) // h1 = the number of misplaced tiles
    {
        int gn = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] != i && list[i] != 0) {
                gn++;
            }
        }
        return gn;
    }

    public LinkedList<EightPuzzle> getChildren() {
        LinkedList<EightPuzzle> children = new LinkedList<EightPuzzle>();
        int loc, mod_result, div_result;

        int temparray[] = this.puzzle.clone();
        EightPuzzle unified;

        for(loc = 0; this.puzzle[loc] != 0; loc++) {}
        mod_result = loc % 3;
        div_result = loc / 3;
        do {
            temparray = this.puzzle.clone();

            if (mod_result <= 1) {
                temparray[loc] = temparray[loc + 1];
                temparray[loc + 1] = 0;
            } else {
                temparray[loc] = temparray[loc - 1];
                temparray[loc - 1] = 0;
            }
            unified = new EightPuzzle(temparray, this.hueristic_type, this.g_n + 1);
            unified.setParent(this);
            children.add(unified);
        } while (mod_result++ == 1);
        do {
            temparray = this.puzzle.clone();
            if (div_result == 0) {
                temparray[loc] = temparray[loc + 3];
                temparray[loc + 3] = 0;
            } else {
                temparray[loc] = temparray[loc - 3];
                temparray[loc - 3] = 0;
            }
            unified = new EightPuzzle(temparray, this.hueristic_type, this.g_n + 1);
            unified.setParent(this);
            children.add(unified);
        } while (div_result-- == 1);
        return children;
    }

    public int h2(int[] list) {
        int gn = 0;
        for (int i = 0; i < list.length; i++) {
            if (list[i] != 0) {
                gn += Math.abs(((list[i]-1) / 3) - (i / 3));
                gn += Math.abs(((list[i]-1) % 3) - (i % 3));
            }
        }
        return gn;
    }

    public String toString() {
        String x = "";
        for (int i = 0; i < this.puzzle.length; i++) {
            x += puzzle[i] + " ";
            if ((i + 1) % 3 == 0) {
                x += "\n";
            }
        }
        return x;
    }

    public int compareTo(Object input) {


        if (this.f_n < ((EightPuzzle) input).getF_n()) {
            return -1;
        } else if (this.f_n > ((EightPuzzle) input).getF_n()) {
            return 1;
        }
        return 0;
    }

    public boolean equals(Object test) {
        //if (this.f_n != test.getF_n()) {
          //  return false;
       // }
        return Arrays.equals(this.puzzle, ((EightPuzzle)test).puzzle);
    }
    public int hashcode()
    {
    	return Arrays.hashCode(this.puzzle);
    }

    public boolean mapEquals(EightPuzzle test) {
        return Arrays.equals(this.puzzle, test.puzzle);
    }
}

