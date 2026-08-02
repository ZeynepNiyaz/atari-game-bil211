import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.CardLayout; 
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;
import java.util.ArrayList;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.util.Random;
import java.io.*;

public class Main extends JFrame implements ActionListener,KeyListener{

public static final int width = 500;
public static final int height = 590;

public static final Object pauseLock = new Object();

CardLayout cardLayout;
JPanel mainCardPanel;
String currentCard = "MenuCard";
GamePanel gameAreaPanel;

public Main(){
super("Arcade");
setSize(width,height);
setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


cardLayout = new CardLayout();
mainCardPanel = new JPanel(cardLayout);

this.addKeyListener(this);
this.setFocusable(true);

JPanel menuPanel = new JPanel(new BorderLayout());
JPanel buttonSubPanel = new JPanel(new FlowLayout());

JButton newGame= new JButton("NEW GAME");
newGame.setActionCommand("NEW_GAME");
newGame.addActionListener(this);
newGame.setFocusable(false);
        
JButton loadGame= new JButton("LOAD GAME");
loadGame.setActionCommand("LOAD_GAME");
loadGame.addActionListener(this);
loadGame.setFocusable(false);

buttonSubPanel.add(newGame);
buttonSubPanel.add(loadGame);
menuPanel.add(buttonSubPanel, BorderLayout.CENTER);

gameAreaPanel = new GamePanel(); 
BulletManager manager = new BulletManager(this,gameAreaPanel);
manager.start();

JPanel pausePanel = new JPanel(new BorderLayout());
JButton saveExitButton = new JButton("SAVE AND EXIT");
saveExitButton.setActionCommand("SAVE_EXIT");
saveExitButton.addActionListener(this);
pausePanel.add(saveExitButton, BorderLayout.CENTER);


mainCardPanel.add(menuPanel, "MenuCard");
mainCardPanel.add(gameAreaPanel, "GameCard");
mainCardPanel.add(pausePanel, "PauseCard");

add(mainCardPanel);
setLocationRelativeTo(null); // oyunu ekranın ortasında açma kodu

cardLayout.show(mainCardPanel, "MenuCard");
}


public class GamePanel extends JPanel implements MouseListener{ //GamePlan subclass 
Player p = new Player(250,250,"player",this);

List<Bullet> bullets = new ArrayList<>();
List<Friend> friends = new ArrayList<>();
List<Enemy> enemies = new ArrayList<>();
List<BomberBox> bombs = new ArrayList<>();


public GamePanel() {
setBackground(Color.WHITE);
addMouseListener(this);
}
public void paintComponent(Graphics g) {
super.paintComponent(g);
g.setColor(Color.MAGENTA);
g.fillRect(p.x, p.y, p.playerSize, p.playerSize);

g.setColor(Color.LIGHT_GRAY);//CANVAS
g.drawRect(0,500,500,50);


synchronized(bombs) {//BOMB
for (BomberBox b : bombs) {
if(b.can==3){
g.setColor(Color.CYAN);
}else if(b.can==2){
g.setColor(Color.BLUE);
}else if(b.can==1){
g.setColor(Color.DARK_GRAY);}
g.fillRect(b.x, b.y, 10, 10);
}
}


synchronized(bullets) {
for (Bullet b : bullets) {
if(b.owner.contains("player")){
g.setColor(Color.YELLOW);
}else if(b.owner.contains("enemy")){
g.setColor(Color.ORANGE);
}else if(b.owner.contains("bomb")){//bomb
g.setColor(Color.BLACK);
}else if(b.owner.contains("friend")){
g.setColor(Color.BLUE);}
g.fillRect(b.x, b.y, 5, 5);
}
}

synchronized(friends) {
g.setColor(Color.GREEN);
for (Friend f : friends) {
g.fillRect(f.x, f.y, 10, 10);
}
}

synchronized(enemies) {
g.setColor(Color.RED);
for (Enemy e : enemies) {
g.fillRect(e.x, e.y, 10, 10);
}
}

}

public void moveLeft()  { 
p.x -= 10; 
if(p.x >490){
p.x = 490;
}else if(p.x<0){
p.x=0;
}
repaint(); 
}
public void moveRight() { 
p.x += 10; 
if(p.x >490){
p.x = 490;
}else if(p.x<0){
p.x=0;
}
repaint(); 
}
public void moveUp()    { 
p.y -= 10;
if(p.y >490){
p.y = 490;
}else if(p.y<0){
p.y=0;
}
repaint();  
}
public void moveDown()  { 
p.y += 10; 
if(p.y>490){
p.y = 490;
}else if(p.y<0){
p.y=0;
}
repaint(); 
}


public void mousePressed(MouseEvent e) {
if (SwingUtilities.isLeftMouseButton(e)) {
synchronized(bullets) {
Bullet b1 = new Bullet(p.x, p.y + (p.playerSize / 2), "player_left");
bullets.add(b1);
Bullet b2 = new Bullet(p.x+ p.playerSize, p.y + (p.playerSize / 2), "player_right");
bullets.add(b2);}

}else if(SwingUtilities.isRightMouseButton(e)) {// nokta terminal yazdırma
int hedefX = e.getX();
int hedefY = e.getY();
System.out.println("X: "+ hedefX+" Y: "+ hedefY);
}
}
public void mouseClicked(MouseEvent e) {}
public void mouseReleased(MouseEvent e) {}
public void mouseEntered(MouseEvent e) {}
public void mouseExited(MouseEvent e) {}


}// GamePanel inner classın bitişi

public void actionPerformed(ActionEvent e) {
String command = e.getActionCommand();
        
if (command.equals("NEW_GAME")) {
currentCard = "GameCard";
cardLayout.show(mainCardPanel, "GameCard");
level=1;
loadLevel(level);
isAction = true;
System.out.println("Yeni oyun başlatılıyor...");



javax.swing.SwingUtilities.invokeLater(new Runnable() {
public void run() {
Main.this.requestFocusInWindow();
}
});

} else if (command.equals("LOAD_GAME")) {
currentCard = "GameCard";
cardLayout.show(mainCardPanel, "GameCard");
load();




javax.swing.SwingUtilities.invokeLater(new Runnable() {
public void run() {
Main.this.requestFocusInWindow();
}
});

}else if (command.equals("SAVE_EXIT")) {
save();
currentCard = "MenuCard"; 
cardLayout.show(mainCardPanel, "MenuCard");
}
}

static boolean isAction=true;
public void keyPressed(KeyEvent e) {
if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
if (currentCard.equals("GameCard")) {
isAction=false;
currentCard = "PauseCard";
cardLayout.show(mainCardPanel, "PauseCard");
} else if (currentCard.equals("PauseCard")) {
isAction=true;
currentCard = "GameCard";
cardLayout.show(mainCardPanel, "GameCard");
synchronized (Main.pauseLock) {
Main.isAction = true; 
Main.pauseLock.notifyAll(); 
}

javax.swing.SwingUtilities.invokeLater(new Runnable() {
public void run() {
Main.this.requestFocusInWindow();
}
}); 
}
}
if (currentCard.equals("GameCard")) {
int keyCode = e.getKeyCode();
        
if (keyCode == KeyEvent.VK_W) {
gameAreaPanel.moveUp();
} else if (keyCode == KeyEvent.VK_S) {
gameAreaPanel.moveDown();
} else if (keyCode == KeyEvent.VK_A) {
gameAreaPanel.moveLeft();
} else if (keyCode == KeyEvent.VK_D) {
gameAreaPanel.moveRight();
} else if( keyCode== KeyEvent.VK_SPACE) {
synchronized(gameAreaPanel.bullets) {
Bullet b3 = new Bullet(gameAreaPanel.p.x, gameAreaPanel.p.y + (gameAreaPanel.p.playerSize / 2), "player_down");
gameAreaPanel.bullets.add(b3);}}

}

}

public void keyTyped(KeyEvent e) {}
public void keyReleased(KeyEvent e) {}



//Level up kodları ve methodları

int level = 1;
public void nextLevel() {
level++;
System.out.println("Tebrikler! Level " + level + " Başlıyor!");
loadLevel(level);
}

public void restart(){
loadLevel(level);
}


public boolean isOccupiedEnemy(int x, int y) {
for (Friend f : gameAreaPanel.friends) {
if (f.x == x && f.y == y){
return true;}}
return false; 
}

public boolean isOccupiedFriend(int x, int y) {
for (Friend f : gameAreaPanel.friends) {
if (f.x == x && f.y == y) {
return true;}
}
return false;
}

public boolean isOccupiedBomb(int x, int y) {//bomb
for (BomberBox b : gameAreaPanel.bombs) {
if (b.x == x && b.y == y) {
return true;}
}
return false;
}

public void loadLevel(int level) {

synchronized (gameAreaPanel.bullets) {
gameAreaPanel.bullets.clear();}
synchronized (gameAreaPanel.friends) {
for (Friend f : gameAreaPanel.friends) { f.stopThread(); }
gameAreaPanel.friends.clear();}
synchronized (gameAreaPanel.enemies) {
for (Enemy e : gameAreaPanel.enemies) { e.stopThread(); }
gameAreaPanel.enemies.clear();}
synchronized (gameAreaPanel.bombs) {//bomb
for (BomberBox b : gameAreaPanel.bombs) { b.stopThread(); }
gameAreaPanel.bombs.clear();}

gameAreaPanel.p.x = 250;
gameAreaPanel.p.y = 250;

Random rand= new Random();

int friendCount = level * 1;
int friendX=0;int friendY=0;
for (int i = 0; i < friendCount; i++) {

do {
friendX = rand.nextInt(50) * 10;
friendY = rand.nextInt(50) * 10;
} while (friendX == 250 && friendY == 250 && isOccupiedFriend(friendX, friendY));

Friend f = new Friend(friendX,friendY,"friend",gameAreaPanel);
gameAreaPanel.friends.add(f);
f.start();
}

int enemyCount= level*2;
int enemyX;int enemyY;
for (int i = 0; i < enemyCount; i++) {
do {
enemyX = rand.nextInt(50) * 10;
enemyY = rand.nextInt(50) * 10;
} while (enemyX == 250 && enemyY == 250 && isOccupiedFriend(enemyX, enemyY)&& isOccupiedEnemy(enemyX, enemyY));
        
Enemy e = new Enemy(enemyX, enemyY, "enemy", gameAreaPanel);
gameAreaPanel.enemies.add(e);
e.start(); 
}

int bombCount= level*1;//bomb
int bombX;int bombY;
for (int i = 0; i < bombCount; i++) {
do {
bombX = rand.nextInt(50) * 10;
bombY = rand.nextInt(50,55) * 10;
} while (isOccupiedFriend(bombX, bombY)&& isOccupiedEnemy(bombX, bombY) && isOccupiedBomb(bombX, bombY));
        
BomberBox b = new BomberBox(bombX, bombY, "bomb", gameAreaPanel,3);
gameAreaPanel.bombs.add(b);
b.start(); 
}
}



//Save kodları

public void save(){
try (ObjectOutputStream obj = new ObjectOutputStream(new FileOutputStream("save.bin"))) {
obj.writeInt(level);
obj.writeInt(gameAreaPanel.p.x);
obj.writeInt(gameAreaPanel.p.y);
synchronized (gameAreaPanel.bullets) {
obj.writeObject(gameAreaPanel.bullets);}
synchronized (gameAreaPanel.enemies) {
obj.writeObject(gameAreaPanel.enemies);}
synchronized (gameAreaPanel.friends) {
obj.writeObject(gameAreaPanel.friends);}
synchronized (gameAreaPanel.bombs) {//bomb
obj.writeObject(gameAreaPanel.bombs);}
System.out.println("Oyun kaydediliyor...");
}catch (Exception e){
System.out.println("Kaydederken hata oluştu: " + e.getMessage());
e.printStackTrace();
}
}

public void load(){
isAction = false;
try(ObjectInputStream obj = new ObjectInputStream(new FileInputStream("save.bin"))) {
synchronized (gameAreaPanel.enemies) {
for (Enemy e : gameAreaPanel.enemies) { e.stopThread(); }
gameAreaPanel.enemies.clear();}
synchronized (gameAreaPanel.friends) {
for (Friend f : gameAreaPanel.friends) { f.stopThread(); }
gameAreaPanel.friends.clear();}
synchronized (gameAreaPanel.bullets) {
gameAreaPanel.bullets.clear();}
synchronized (gameAreaPanel.bombs) {//bomb
for (BomberBox b : gameAreaPanel.bombs) { b.stopThread(); }
gameAreaPanel.bombs.clear();}

this.level= obj.readInt();
gameAreaPanel.p.x = obj.readInt();
gameAreaPanel.p.y= obj.readInt();

List<Bullet> loadedBullets = (List<Bullet>) obj.readObject();
synchronized (gameAreaPanel.bullets) {
gameAreaPanel.bullets.addAll(loadedBullets);
}

List<Enemy> loadedEnemies= (List<Enemy>) obj.readObject();
synchronized (gameAreaPanel.enemies) {
for (Enemy e : loadedEnemies) {
Enemy newE = new Enemy(e.x, e.y, "enemy", gameAreaPanel);
gameAreaPanel.enemies.add(newE);
newE.start();}
}

List<Friend> loadedFriends = (List<Friend>)obj.readObject();
synchronized (gameAreaPanel.friends) {
for (Friend f : loadedFriends) {
Friend newF= new Friend(f.x, f.y, "friend", gameAreaPanel);
gameAreaPanel.friends.add(newF);
newF.start();}
}

List<BomberBox> loadedBombs = (List<BomberBox>)obj.readObject();//bomb
synchronized (gameAreaPanel.bombs) {
for (BomberBox b : loadedBombs) {
BomberBox newB= new BomberBox(b.x, b.y, "bomb", gameAreaPanel,b.can);
gameAreaPanel.bombs.add(newB);
newB.start();}
}
System.out.println("Kayıtlı oyun yükleniyor...");
gameAreaPanel.repaint();

}catch (Exception e){
System.out.println("Kayıtlı oyun yüklenirken hata oluştu: " + e.getMessage());
e.printStackTrace();
} finally {
synchronized (Main.pauseLock) {
isAction = true; 
Main.pauseLock.notifyAll(); 
}}
}
public static void main(String[]args){

Main gui= new Main();
gui.setVisible(true);

}
}






