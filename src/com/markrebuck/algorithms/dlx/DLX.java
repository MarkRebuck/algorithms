package com.markrebuck.algorithms.dlx;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of Knuth's Dancing Links Algorithm (aka "Algorithm X").
 * This implementation flows directly from http://www-cs-faculty.stanford.edu/~uno/papers/dancing-color.ps.gz 
 */

public class DLX {
   public static final int MAX_DEPTH = 64;
   protected DLXNode root;
   protected DLXNode history[] = new DLXNode[MAX_DEPTH];

   protected void solve() {
      solve(0);
   }

   /**
    * 1.  If R[h] = h, print solution and return
    * 2.  Otherwise choose column object c
    * 3.  Cover column c
    * 4.  For each r <- D[c], D[D[c]],..., while r != c,
    * 5.     set Ok <- r
    * 6.     for each j <- R[r], R[R[r]],..., while j != r
    * 7.        Cover columnn C[j]
    * 8.     search(k + 1)
    * 9.     set r <- Ok and c <- C[r]
    * 10.    for each j < L[r], L[L[r]],..., while j != r
    * 11.       Uncover column C[j]
    * 12. Uncover column c
    * @param depth
    */
   protected void solve(final int depth) {
      if (root.right == root) {
         showHistory(depth);
         return;
      }

      DLXNode choice = pickColumn();
      coverColumn(choice);
      for (DLXNode down = choice.down; down != choice; down = down.down) {
         history[depth] = down;
         for (DLXNode right = down.right; right != down; right = right.right) {
            coverColumn(right.header);
         }
         solve(depth + 1);
         choice = down.header;
         down = history[depth];
         for (DLXNode left = down.left; left != down; left = left.left) {
            uncoverColumn(left.header);
         }
      }
      uncoverColumn(choice);
   }

   //  Pick a column.  We'll pick the one with the smallest size because, hey, why not?
   //  Other implementations will likely want to pick a better heuristic here.
   private DLXNode pickColumn() {
      int max = Integer.MAX_VALUE;
      DLXNode val = null;

      for (DLXNode node = root.right; node != root; node = node.right) {
         final int size = node.size;
         if (size < max) {
            max = node.size;
            val = node;
         }
      }

      return val;
   }

   private void uncoverColumn(DLXNode choice) {
      for (DLXNode up = choice.up; up != choice; up = up.up) {
         for (DLXNode left = up.left; left != up; left = left.left) {
            left.header.size++;
            left.down.up = left;
            left.up.down = left;
         }
      }
      choice.right.left = choice;
      choice.left.right = choice;
   }

   private void coverColumn(DLXNode choice) {
      choice.right.left = choice.left;
      choice.left.right = choice.right;

      for (DLXNode down = choice.down; down != choice; down = down.down) {
         for (DLXNode right = down.right; right != down; right = right.right) {
            right.down.up = right.up;
            right.up.down = right.down;
            right.header.size--;
         }
      }
   }

   protected void showHistory(int depth) {
      System.out.println("History at depth " + depth + ":");
      for (int d = 0; d < depth; d++) {
         DLXNode start = history[d];
         DLXNode node = start;
         do {
            System.out.print(node.name + "-" + node.header.name + " ");
            node = node.right;
         } while (node != start);
         System.out.println();
      }
   }

   protected void init(String problem) {
      //System.out.println("Initializing problem with:\n" + problem + "\n-------------------\n");
      List<DLXNode[]> rowList = new ArrayList<DLXNode[]>();
      String lines[] = problem.split("\n");

      String sampleLine = lines[0].trim();
      String headerParts[] = sampleLine.split(" ");
      final int numCols = headerParts[1].toCharArray().length;
      final DLXNode[] header = new DLXNode[numCols];
      rowList.add(header);
      for (int col = 0; col < numCols; col++) {
         final String name = "" + col;
         DLXNode node = new DLXNode(name, null); // No header, because we are the header
         header[col] = node;
      }
      //  link the header row...  first and last will link to root later.
      for (int col = 0; col < numCols; col++) {
         DLXNode node = header[col];
         if (col > 0) {
            node.left = header[col - 1];
         }
         if (col < (numCols - 1)) {
            node.right = header[col + 1];
         }
      }

      for (int i = 0; i < lines.length; i++) {
         String line = lines[i];
         line = line.trim();
         String[] parts = line.split(" ");
         String rowName = parts[0];
         char[] data = parts[1].toCharArray();
         DLXNode[] row = new DLXNode[numCols];
         for (int col = 0; col < data.length; col++) {
            final char datum = data[col];
            if ('0' == datum) {
               row[col] = null;
            } else {
               row[col] = new DLXNode(rowName, header[col]);
               header[col].size++;
            }
         }
         rowList.add(row);
      }

      DLXNode[][] matrix = rowList.toArray(new DLXNode[][] {});
      final int numRows = matrix.length;

      for (int y = 0; y < numRows; y++) {
         DLXNode[] row = matrix[y];
         for (int x = 0; x < numCols; x++) {
            final DLXNode node = row[x];
            if (node == null) {
               continue;
            }
            int c = x;
            do {
               c--;
               c = (c < 0) ? (numCols - 1) : c;
            } while (row[c] == null);
            node.left = row[c];

            c = x;
            do {
               c++;
               c = (c == numCols) ? 0 : c;
            } while (row[c] == null);
            node.right = row[c];

            int r = y;
            do {
               r--;
               r = (r < 0) ? (numRows - 1) : r;
            } while (matrix[r][x] == null);
            node.up = matrix[r][x];

            r = y;
            do {
               r++;
               r = (r == numRows) ? 0 : r;
            } while (matrix[r][x] == null);
            node.down = matrix[r][x];
         }
      }

      //  initialize the root, finishing the header row...
      root = new DLXNode("root", null);
      root.right = header[0];
      root.left = header[numCols - 1];
      header[0].left = root;
      header[numCols - 1].right = root;
   }
}