package com.markrebuck.algorithms.dlx.samples;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.markrebuck.algorithms.dlx.DLX;

/**
 * Solves 6x10 Pentomino.  http://en.wikipedia.org/wiki/Pentomino
 *  
 * Quite handy that we are looking at a 6x10 board... We can cram
 * the entire thing in a 64 bit long using a bitboard to store each
 * piece's possible moves.  http://en.wikipedia.org/wiki/Bitboard
 * 
 * To express this game as appropriate input to Knuth's DLX algorithm,
 * we define 12 columns (one for each piece) with a constraint that 
 * each piece is used exactly once.  Then we have 60 columns for each
 * square on the board, so each square must be filled by exactly one
 * block.  That gives a total of 72 constraints, and one row for every
 * possible piece placement.  We restrict the placement of the 'v'
 * piece to avoid duplicate solutions.
 */

public class DLXPentomino extends DLX {
   static final int WIDTH = 10;
   static final int HEIGHT = 6;
   static final int SIZE = 60;
   private HashMap<String, Long> boardHash = new HashMap<String, Long>();
   private final char[] pieceNames = "filnptuvwxyz".toCharArray();

   private int numSolutionsShown = 0;

   @Override
   protected void showHistory(int depth) {
      final char text[] = new char[SIZE];
      for (int d = 0; d < depth; d++) {
         final String name = history[d].name;
         final char c = name.charAt(0);
         final long board = boardHash.get(name);
         showBoard(text, board, c);
      }
      ++numSolutionsShown;
      //System.out.println(++numSolutionsShown + ": " + new String(text));
      System.out.println(new String(text));
      //showBoard(new String(text));
   }

   private void showBoard(final String compact) {
      System.out.println(compact.substring(0, 10));
      System.out.println(compact.substring(10, 20));
      System.out.println(compact.substring(20, 30));
      System.out.println(compact.substring(30, 40));
      System.out.println(compact.substring(40, 50));
      System.out.println(compact.substring(50));
      System.out.println();
   }

   private void showBoard(final long board, final char c) {
      final char text[] = new char[SIZE];
      for (int p = 0; p < SIZE; p++) {
         text[p] = (0 != (board & (1L << p))) ? c : '-';
      }
      showBoard(new String(text));
   }

   private void showBoard(final char[] text, final long board, final char c) {
      for (int p = 0; p < SIZE; p++) {
         if (0 != (board & (1L << p))) {
            text[p] = c;
         }
      }
   }

   private void doIt() {
      final long then = System.currentTimeMillis();
      long[][] possibleMoves = new long[12][];
      possibleMoves[0] = getBoards(new int[] { 0, 1, 3, 3, 0 }, true); // f
      possibleMoves[1] = getBoards(new int[] { 0, 0, 0, 0 }, true); // i
      possibleMoves[2] = getBoards(new int[] { 0, 0, 0, 1 }, true); // l
      possibleMoves[3] = getBoards(new int[] { 0, 1, 0, 0 }, true); // n
      possibleMoves[4] = getBoards(new int[] { 0, 0, 1, 2 }, true); // p
      possibleMoves[5] = getBoards(new int[] { 1, 1, 2, 0, 0 }, true); // t
      possibleMoves[6] = getBoards(new int[] { 2, 1, 1, 0 }, true); // u

      //  Bit of a hack here, but we're going to skip showing any solution
      //  without the "v" piece in the "L" formation.  Every board has
      //  a version of itself rotated 180 degrees, flipped, and
      //  flipped-and-rotated-180 degrees.  So without this elimination
      //  we will report all 9356 solutions (there are 9356 / 4 = 2339
      //  solutions once deflipped).  We'll simply skip generation of 
      //  rotated/flipped moves for this piece.  If we generated the flips
      //  and rotations of this pieces, we would find all 9356 solutions.
      possibleMoves[7] = getBoards(new int[] { 1, 1, 0, 0 }, false); // v
      possibleMoves[8] = getBoards(new int[] { 3, 0, 3, 0 }, true); // w
      possibleMoves[9] = getBoards(new int[] { 0, 0, 2, 3, 1, 1 }, true); // x
      possibleMoves[10] = getBoards(new int[] { 0, 3, 1, 1, 1 }, true); // y
      possibleMoves[11] = getBoards(new int[] { 1, 0, 0, 1 }, true); // z
      StringBuffer problem = new StringBuffer();
      for (int piece = 0; piece < possibleMoves.length; piece++) {
         String preamble = "";
         for (int i = 0; i < pieceNames.length; i++) {
            preamble += (i == piece) ? '1' : '0';
         }
         long[] moves = possibleMoves[piece];
         for (int move = 0; move < moves.length; move++) {
            String text = "" + pieceNames[piece] + "-" + move;
            final long board = moves[move];
            boardHash.put(text, board);
            text += " " + preamble;
            for (int i = 0; i < SIZE; i++) {
               text += (0 == (board & (1L << i))) ? '0' : '1';
            }
            text += "\n";
            problem.append(text);
         }
      }
      init(problem.toString());
      solve();
      final long now = System.currentTimeMillis();
      System.out.println(numSolutionsShown + " solutions shown in " + (now - then) + "ms");      
   }

   /**
    * 0 - right
    * 1 - down
    * 2 - left
    * 3 - up
    */
   public long[] getBoards(final int[] moves, final boolean doAllGeometries) {
      //  hash would be faster for our list of all moves, but I want predictable ordering,
      //  and getBoards() is only a tiny fraction of our total runtime :-)
      List<Long> all = new ArrayList<Long>(); 
      
      generate(moves, all, doAllGeometries);
      
      if (doAllGeometries) {
         final int[] reflected = new int[moves.length];
         for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
            if ((move == 0) || (move == 2)) {
               move = (move + 2) % 4;
            }
            reflected[i] = move;
         }
         generate(reflected, all, doAllGeometries);
         for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
            if ((move == 1) || (move == 3)) {
               move = (move + 2) % 4;
            }
            reflected[i] = move;
         }
         generate(reflected, all, doAllGeometries);
         for (int i = 0; i < moves.length; i++) {
            int move = moves[i];
            move = (move + 2) % 4;
            reflected[i] = move;
         }
         generate(reflected, all, doAllGeometries);
      }
      long[] possibles = new long[all.size()];
      for (int i = 0; i < possibles.length; i++) {
         possibles[i] = all.get(i);
      }
      return possibles;
   }

   private void generate(final int[] moves, List<Long> all, final boolean doRotations) {
      final int rotationInc = doRotations ? 1 : 821;
      for (int rotation = 0; rotation < 4; rotation += rotationInc) {
         for (int origin = 0; origin < SIZE; origin++) {
            long board = 1L << origin;
            int column = origin % WIDTH;
            int row = origin / WIDTH;
            for (int move : moves) {
               move = (move + rotation) % 4;
               if (move == 0) {
                  column++;
               } else if (move == 1) {
                  row++;
               } else if (move == 2) {
                  column--;
               } else if (move == 3) {
                  row--;
               }
               //  If this move takes us off the edge, it isn't valid.
               if ((row < 0) || (row >= HEIGHT) || (column < 0) || (column >= WIDTH)) {
                  board = 0L;
                  break;
               }
               board |= 1L << ((row * WIDTH) + column);
            }
            if ((board != 0L) && !all.contains(board)) {
               all.add(board);
               //showBoard(board, 'X');
            }
         }
      }
   }

   public static void main(String[] args) {
      new DLXPentomino().doIt();
   }
}