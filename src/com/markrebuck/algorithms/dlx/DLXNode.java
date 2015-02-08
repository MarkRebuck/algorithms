package com.markrebuck.algorithms.dlx;

//TODO:  Not happy with publics here.  Treat the public non-finals with care!
public class DLXNode {
   public final String name;
   public DLXNode left, right, up, down;
   public int size = 0; //  Makes bookkeeping easier when we are a header.
   public DLXNode header;

   public DLXNode(final String name, final DLXNode header) {
      this.name = name;
      this.header = header;

      // Initially a doubly-linked list back to ourselves.
      // Headers are used as sentinel values during list traversal.
      left = this;
      right = this;
      up = this;
      down = this;
   }
   
   public String toString() {
      return name + "(" + size + ")";
   }
}
