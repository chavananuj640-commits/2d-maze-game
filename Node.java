class Node {
   int row;
   int col;
   int g;
   int h;
   int f;
   Node parent;

   Node(int var1, int var2) {
      this.row = var1;
      this.col = var2;
   }
}

