package com.markrebuck.algorithms.dlx.samples;

import java.util.Arrays;
import java.util.HashSet;

import com.markrebuck.algorithms.dlx.DLX;

/**
 * 
 * From the Wikipedia page  http://en.wikipedia.org/wiki/Knuth%27s_Algorithm_X
 *  
 * For example, consider the exact cover problem specified by the universe 
 * U = {1, 2, 3, 4, 5, 6, 7} and the collection of sets {S} = {A, B, C, D, E, F}, where:
 * A = {1, 4, 7};
 * B = {1, 4};
 * C = {4, 5, 7};
 * D = {3, 5, 6};
 * E = {2, 3, 6, 7}; and
 * F = {2, 7}.
 * This problem is represented by the matrix:
 *    1  2  3  4  5  6  7
 * A  1  0  0  1  0  0  1
 * B  1  0  0  1  0  0  0
 * C  0  0  0  1  1  0  1
 * D  0  0  1  0  1  1  0
 * E  0  1  1  0  0  1  1
 * F  0  1  0  0  0  0  1
 * 
 * And is covered by:
 *    1  2  3  4  5  6  7
 * B  1  0  0  1  0  0  0
 * D  0  0  1  0  1  1  0
 * F  0  1  0  0  0  0  1
 * 
 * In other words, the subcollection {B, D, F} is an exact cover, since every element is contained 
 * in exactly one of the sets B = {1, 4}, D = {3, 5, 6}, or F = {2, 7}.
 * ...
 * In summary, the algorithm determines there is only one exact cover: {S}* = {B, D, F}.
 */

public class DLXWikiSample extends DLX {
   @Override
   protected void showHistory(int depth) {
      //  We don't really care about the actual contents of the sets,
      //  we only care about their names...
      HashSet<String> setNames = new HashSet<String>();
      for (int d = 0; d < depth; d++) {
         setNames.add(history[d].name);
      }
      //  Sometimes Java can be a pain.  Jumping through some hoops to format the output.
      String[] asArray = setNames.toArray(new String[0]);
      Arrays.sort(asArray);
      System.out.println(Arrays.toString(asArray));
   }

   private void doIt() {
      final String problem = //
      "A 1001001\n" + //
            "B 1001000\n" + //
            "C 0001101\n" + //
            "D 0010110\n" + //
            "E 0110011\n" + //
            "F 0100001\n";
      init(problem);
      solve();
   }

   public static void main(String[] args) {
      new DLXWikiSample().doIt();
   }
}