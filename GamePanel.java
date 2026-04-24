import javax.swing.*;



import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;
import javax.swing.Timer;

public class GamePanel extends JPanel {

    int rows;
int cols;

int cellSize = 40;
int padding = 6;


JButton playAgainBtn;
JButton quitBtn;

int[][] maze;


int tigerRow, tigerCol;

Random random = new Random();
    

    int startRow = 0;
    int startCol = 0;

    int lionRow = 0;
    int lionCol = 0;

    int deerRow, deerCol;

    int userSteps = 0;

    long startTime;
    long endTime;

    JButton extreme;

    int killRadius = 2;   // you can change later
    String difficulty = "Easy";

    boolean isExtreme = false;
    Timer tigerTimer;
    int tigerSpeed = 300;   // fast for extreme

    Image lionImg, lionDeadImg;
    Image tigerImg, tigerAttackImg;
    Image deerImg, deerDeadImg;

    boolean isGameOver = false;
    boolean isWin = false;

    String endMessage = "";

double finalTime = 0;
int finalBest = 0;
double finalAccuracy = 0;

    String direction = "DOWN";

    Image wall1Img;
    Image soilImg;   // 🔥 ADD THIS

    List<Node> shortestPath;
    List<Point> userPath = new ArrayList<>();

    public GamePanel(int cellSize, String level) {

     

        playAgainBtn = new JButton("Play Again");
playAgainBtn.setBounds(350, 10, 130, 30);
playAgainBtn.setVisible(false);
add(playAgainBtn);

quitBtn = new JButton("Quit");
quitBtn.setBounds(490, 10, 100, 30);
quitBtn.setVisible(false);
add(quitBtn);

   quitBtn.addActionListener(e -> {
    System.exit(0);
    });


        this.cellSize = cellSize;
        setLayout(null);
        setFocusable(true);
        

        JButton restart = new JButton("Restart");
        restart.setBounds(10, 10, 100, 30);
        add(restart);

        JButton newMaze = new JButton("New Maze");
        newMaze.setBounds(120, 10, 120, 30);
        add(newMaze);

        lionImg = new ImageIcon("lion.png").getImage();
        lionDeadImg = new ImageIcon("deadlion.png").getImage();

        tigerImg = new ImageIcon("tiger.png").getImage();
        tigerAttackImg = new ImageIcon("tigerattack.png").getImage();

        deerImg = new ImageIcon("deer.png").getImage();
        deerDeadImg = new ImageIcon("deaddeer.png").getImage();

        wall1Img = new ImageIcon("wall1.png").getImage();
        soilImg = new ImageIcon("soil.png").getImage();

       

        // KEY INPUT
        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

                int key = e.getKeyCode();
        if (key == KeyEvent.VK_UP) {
        direction = "UP";
        moveLion(lionRow - 1, lionCol);
    }
        else if (key == KeyEvent.VK_DOWN) {
        direction = "DOWN";
        moveLion(lionRow + 1, lionCol);
    }
        else if (key == KeyEvent.VK_LEFT) {
        direction = "LEFT";
        moveLion(lionRow, lionCol - 1);
    }
        else if (key == KeyEvent.VK_RIGHT) {
        direction = "RIGHT";
        moveLion(lionRow, lionCol + 1);
    }
            }
        });

        // MOUSE
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
                handleClick(e.getX(), e.getY());
            }
        });

        // play again 
playAgainBtn.addActionListener(e -> {

    // 🔥 RESET GAME STATE
    maze = null;

    lionRow = startRow;
    lionCol = startCol;

    userSteps = 0;
    userPath.clear();
    shortestPath = null;

    isGameOver = false;
    isWin = false;

    // 🔥 RESET END SCREEN DATA
    endMessage = "";
    finalTime = 0;
    finalBest = 0;
    finalAccuracy = 0;

    // 🔥 STOP TIGER
    if (tigerTimer != null) tigerTimer.stop();

    // 🔥 HIDE BUTTONS AGAIN
    playAgainBtn.setVisible(false);
    quitBtn.setVisible(false);

    // 🔥 RESET TIMER
    startTime = System.currentTimeMillis();

    repaint();
});

        // NEW MAZE
        newMaze.addActionListener(e -> {

            maze = null;

            lionRow = startRow;
            lionCol = startCol;
            

            userSteps = 0;
            userPath.clear();
            shortestPath = null;

            repaint();

            if (tigerTimer != null) tigerTimer.stop();

            isGameOver = false;
            isWin = false;
        });

        
        extreme = new JButton("Extreme");
        extreme.setBounds(240, 10, 100, 30);
        add(extreme);
        setDifficulty(level);
        
        extreme.addActionListener(e -> {
         

         setDifficulty("Extreme");   // set mode
        isExtreme = true;           // enable moving tiger

        maze = null;                // regenerate maze

        if (tigerTimer != null) tigerTimer.stop();

        repaint();
        });
 

        // RESTART
        restart.addActionListener(e -> {

            lionRow = startRow;
            lionCol = startCol;

            userSteps = 0;
            userPath.clear();
            shortestPath = null;

            startTime = System.currentTimeMillis();

            repaint();

            if (tigerTimer != null) tigerTimer.stop();

            isGameOver = false;
            isWin = false;
        });
    }

 //   ==============  Tiger Movement ================
    void startTigerMovement() {

        if (tigerTimer != null) {
        tigerTimer.stop();
}

    tigerTimer = new javax.swing.Timer(tigerSpeed, e -> {

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};
        List<int[]> directions = Arrays.asList(dirs);
        Collections.shuffle(directions);

        for (int[] d : directions) {

            int nr = tigerRow + d[0];
            int nc = tigerCol + d[1];

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols 
                && maze[nr][nc] == 0) {

                tigerRow = nr;
                tigerCol = nc;
                break;
            }
        }

        checkTigerKill();   // 🔥 important
        repaint();
    });

    tigerTimer.start();
}
    // ================Place tiger ============
void placeTiger() {

    int minDistance;

    if (isExtreme) {
        minDistance = rows / 2;   // 🔥 BIG distance for extreme
    } else {
        minDistance = 5;          // normal modes
    }

    do {
        tigerRow = random.nextInt(rows);
        tigerCol = random.nextInt(cols);

    } while (
        maze[tigerRow][tigerCol] == 1 ||
        Math.abs(tigerRow - lionRow) + Math.abs(tigerCol - lionCol) < minDistance ||
        (tigerRow == deerRow && tigerCol == deerCol)
    );
}
     // ==================  Tiger killing lion logic ==================
void checkTigerKill() {

    if (isGameOver) return;

    int dist = Math.abs(lionRow - tigerRow) + Math.abs(lionCol - tigerCol);

    if (dist <= killRadius) {

        isGameOver = true;

        if (tigerTimer != null) tigerTimer.stop();

        endMessage = "LOSE";

        playAgainBtn.setVisible(true);
        quitBtn.setVisible(true);

        repaint();
    }
}

    // ================= MAZE =================

   void generateMaze() {
    maze = new int[rows][cols];

    for (int i = 0; i < rows; i++) {
        Arrays.fill(maze[i], 1);
    }

    generateDFS(1, 1, -1, 0);

    addExtraPaths();       // 🔥 many loops
    openStartAndGoal();    // 🔥 free movement at start & end
}
 // ========== for tiger radius =========
void setDifficulty(String level) {

    difficulty = level;

    if (level.equals("Easy")) {
        killRadius = 2;
        isExtreme = false;

        extreme.setVisible(false);   // ❌ hide

    } 
    else if (level.equals("Medium")) {
        killRadius = 4;
        isExtreme = false;

        extreme.setVisible(true);    // ✅ show

    } 
    else if (level.equals("Hard")) {
        killRadius = 6;
        isExtreme = false;

        extreme.setVisible(true);    // ✅ show

    }
    else { // EXTREME
        killRadius = 6;
        isExtreme = true;

        extreme.setVisible(true);
    }
}

    void generateDFS(int r, int c, int lastDir, int straightCount) {

        maze[r][c] = 0;

        int[][] dirs = {
            {0,1}, {1,0}, {0,-1}, {-1,0}
        };

        List<Integer> order = Arrays.asList(0,1,2,3);
        Collections.shuffle(order);

        for (int dirIndex : order) {

            int[] d = dirs[dirIndex];

            int nr = r + d[0]*2;
            int nc = c + d[1]*2;

            int newStraight = (dirIndex == lastDir) ? straightCount + 1 : 1;

            if (newStraight > 2) continue; // 🔥 less straight forcing

            if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && maze[nr][nc] == 1) {

                maze[r + d[0]][c + d[1]] = 0;

                generateDFS(nr, nc, dirIndex, newStraight);
            }
        }
    }

 

    // 🔥 CREATE LOOPS (MULTIPLE PATHS)
   void addExtraPaths() {

    int attempts = (rows * cols) / 2; // 🔥 BIG increase (many loops)

    for (int i = 0; i < attempts; i++) {

        int r = 1 + (int)(Math.random() * (rows - 2));
        int c = 1 + (int)(Math.random() * (cols - 2));

        if (maze[r][c] == 1) {

            int open = 0;

            if (maze[r-1][c] == 0) open++;
            if (maze[r+1][c] == 0) open++;
            if (maze[r][c-1] == 0) open++;
            if (maze[r][c+1] == 0) open++;

            // 🔥 Only break if it creates REAL branching
            if (open >= 2) {
                maze[r][c] = 0;
            }
        }
    }
}
    //      =============== open start and end ======== 
void openStartAndGoal() {

    // open area around start (lion)
    for (int i = 0; i <= 1; i++) {
        for (int j = 0; j <= 1; j++) {
            if (startRow + i < rows && startCol + j < cols) {
                maze[startRow + i][startCol + j] = 0;
            }
        }
    }

    // open area around goal (deer)
    for (int i = 0; i <= 1; i++) {
        for (int j = 0; j <= 1; j++) {
            int r = deerRow - i;
            int c = deerCol - j;

            if (r >= 0 && c >= 0) {
                maze[r][c] = 0;
            }
        }
    }
}

    // ================= MOVEMENT =================

    void moveLion(int newRow, int newCol) {

        if (isWin || isGameOver) return;

        if (newRow < 0 || newRow >= rows || newCol < 0 || newCol >= cols)
            return;

        if (maze[newRow][newCol] == 1)
            return;

        lionRow = newRow;
        lionCol = newCol;
        checkTigerKill();


        userSteps++;

        userPath.add(new Point(newCol, newRow));

        repaint();
        checkWin();
    }

    void handleClick(int x, int y) {

        int col = x / cellSize;
        int row = y / cellSize;

        if (Math.abs(row - lionRow) + Math.abs(col - lionCol) == 1) {
            moveLion(row, col);
        }
    }

    // ================= WIN =================
void checkWin() {
    if (lionRow == deerRow && lionCol == deerCol) {

        isWin = true;
        endTime = System.currentTimeMillis();

        finalTime = (endTime - startTime) / 1000.0;

        if (!isExtreme) {
            shortestPath = findPath();
        }

        if (!isExtreme && shortestPath != null) {
            finalBest = shortestPath.size() - 1;
            finalAccuracy = (finalBest * 100.0) / userSteps;
        }

        endMessage = "WIN";

        playAgainBtn.setVisible(true);
        quitBtn.setVisible(true);

        repaint();
    }
}

    // ================= DRAW =================

    protected void paintComponent(Graphics g) {
        g.setColor(new Color(120, 170, 120)); // jungle background
        g.fillRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);

        int size = (int)(cellSize * 0.9);
        int offset = (cellSize - size) / 2;

        cols = getWidth() / cellSize;
        rows = getHeight() / cellSize;


if (maze == null) {

    deerRow = rows - 1;
    deerCol = cols - 1;

    generateMaze();   // deer is now known BEFORE generation

    maze[deerRow][deerCol] = 0; // force open

    placeTiger();

    if (isExtreme) {
    startTigerMovement();
}

    // 🔥 FORCE START ALSO OPEN
    maze[lionRow][lionCol] = 0;
    // 🔥 OPEN AREA AROUND LION (IMPORTANT)
for (int i = 0; i <= 1; i++) {
    for (int j = 0; j <= 1; j++) {
        if (lionRow + i < rows && lionCol + j < cols) {
            maze[lionRow + i][lionCol + j] = 0;
        }
    }
}

    startTime = System.currentTimeMillis();
}

      

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {

                int x = j * cellSize;
                int y = i * cellSize;

              if (maze[i][j] == 1) {
    // 🌿 WALL (grass)
    g.drawImage(wall1Img, x, y, cellSize, cellSize, null);
} else {
    // 🟤 GROUND (soil)
    g.drawImage(soilImg, x, y, cellSize, cellSize, null);
}


                g.setColor(Color.GRAY);
               
            }
        }

        // shortest path
        if (shortestPath != null) {
            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(4));

            for (int i = 0; i < shortestPath.size() - 1; i++) {
                Node a = shortestPath.get(i);
                Node b = shortestPath.get(i + 1);

                g2.drawLine(
                        a.col * cellSize + cellSize / 2,
                        a.row * cellSize + cellSize / 2,
                        b.col * cellSize + cellSize / 2,
                        b.row * cellSize + cellSize / 2
                );
            }
        }

        // user path
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(4));

        for (int i = 0; i < userPath.size() - 1; i++) {
            Point a = userPath.get(i);
            Point b = userPath.get(i + 1);

            g2.drawLine(
                    a.x * cellSize + cellSize / 2,
                    a.y * cellSize + cellSize / 2,
                    b.x * cellSize + cellSize / 2,
                    b.y * cellSize + cellSize / 2
            );
        }

      if (isWin) {
    g.drawImage(deerDeadImg,
        deerCol * cellSize + offset,
        deerRow * cellSize + offset,
        size, size, null);
} else {
    g.drawImage(deerImg,
        deerCol * cellSize + offset,
        deerRow * cellSize + offset,
        size, size, null);
}

        // lion
       
        Graphics2D g2d = (Graphics2D) g;

double angle = 0;

if (direction.equals("UP")) angle = -Math.PI / 2;
else if (direction.equals("DOWN")) angle = Math.PI / 2;
else if (direction.equals("LEFT")) angle = Math.PI;
else angle = 0;

// int lionCenterX = lionCol * cellSize + cellSize / 2;
// int lionCenterY = lionRow * cellSize + cellSize / 2;



if (isGameOver) {
    g.drawImage(lionDeadImg,
        lionCol * cellSize + offset,
        lionRow * cellSize + offset,
        size, size, null);
} else {
    g.drawImage(lionImg,
        lionCol * cellSize + offset,
        lionRow * cellSize + offset,
        size, size, null);
}



   //tiger 
   boolean faceLeft = (lionCol < tigerCol);
g2d.setColor(new Color(255, 0, 0, 60));

            int centerX = tigerCol * cellSize + cellSize / 2;
            int centerY = tigerRow * cellSize + cellSize / 2;

           int radiusPx = (int)(killRadius * cellSize * 1.2);

            g2d.fillOval(centerX - radiusPx,
             centerY - radiusPx,
             radiusPx * 2,
             radiusPx * 2);

Image tigerToDraw = isGameOver ? tigerAttackImg : tigerImg;

int tx = tigerCol * cellSize + offset;
int ty = tigerRow * cellSize + offset;

if (faceLeft) {
    // flip horizontally
    g.drawImage(tigerToDraw,
        tx + size, ty,
        -size, size, null);
} else {
    g.drawImage(tigerToDraw,
        tx, ty,
        size, size, null);

}

if (isWin || isGameOver) {

    

    // dark overlay
    g2d.setColor(new Color(0, 0, 0, 160));
    g2d.fillRect(0, 0, getWidth(), getHeight());

    int boxW = 420;
    int boxH = 260;
    int x = (getWidth() - boxW) / 2;
    int y = (getHeight() - boxH) / 2;

    // white box
    g2d.setColor(Color.WHITE);
    g2d.fillRoundRect(x, y, boxW, boxH, 20, 20);

    g2d.setColor(Color.BLACK);

    if (endMessage.equals("WIN")) {

        g2d.setFont(new Font("Arial", Font.BOLD, 22));
        g2d.drawString("🎉 YOU WON!", x + 130, y + 40);

        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        g2d.drawString("Time: " + finalTime + " sec", x + 40, y + 80);
        g2d.drawString("Steps: " + userSteps, x + 40, y + 100);
        g2d.drawString("Best: " + finalBest, x + 40, y + 120);
        g2d.drawString("Accuracy: " + String.format("%.2f", finalAccuracy) + "%", x + 40, y + 140);

    } else {

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("💀 YOU WERE KILLED BY TIGER", x + 40, y + 100);
    }

    // position buttons INSIDE panel
    playAgainBtn.setBounds(x + 80, y + 180, 120, 30);
    quitBtn.setBounds(x + 220, y + 180, 100, 30);
}
 
       
    }

    // ================= A* =================

    List<Node> findPath() {

        PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        boolean[][] closed = new boolean[rows][cols];

        Node start = new Node(startRow, startCol);
        Node goal = new Node(deerRow, deerCol);

        start.g = 0;
        start.h = Math.abs(goal.row - start.row) + Math.abs(goal.col - start.col);
        start.f = start.g + start.h;

        open.add(start);

        while (!open.isEmpty()) {

            Node current = open.poll();

            if (current.row == goal.row && current.col == goal.col) {
                return constructPath(current);
            }

            closed[current.row][current.col] = true;

            int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

            for (int[] d : dirs) {

                int nr = current.row + d[0];
                int nc = current.col + d[1];

                if (nr < 0 || nr >= rows || nc < 0 || nc >= cols)
                    continue;

                // avoid walls
if (maze[nr][nc] == 1 || closed[nr][nc])
    continue;

// 🔥 avoid tiger danger zone
int distFromTiger = Math.abs(nr - tigerRow) + Math.abs(nc - tigerCol);

if (distFromTiger <= killRadius)
    continue;

                Node neighbor = new Node(nr, nc);

                neighbor.g = current.g + 1;
                neighbor.h = Math.abs(goal.row - nr) + Math.abs(goal.col - nc);
                neighbor.f = neighbor.g + neighbor.h;
                neighbor.parent = current;

                open.add(neighbor);
            }
        }

        return null;
    }

    List<Node> constructPath(Node node) {
        List<Node> path = new ArrayList<>();

        while (node != null) {
            path.add(node);
            node = node.parent;
        }

        Collections.reverse(path);
        return path;
    }
}

class Node {
    int row, col;
    int g, h, f;
    Node parent;

    Node(int r, int c) {
        this.row = r;
        this.col = c;
    }
}

