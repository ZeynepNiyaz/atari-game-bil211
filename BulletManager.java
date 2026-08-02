import java.util.List;
import java.util.ArrayList;

public class BulletManager extends Thread{
Main gui;
Main.GamePanel panel;
boolean isPaused = false;

public BulletManager(Main gui, Main.GamePanel panel){
super();
this.panel=panel;
this.gui = gui;}

public boolean isColliding(Bullet b, GameObject obj) {
boolean xCakisma = (b.x < obj.x + 10) && (b.x + 5 > obj.x);
boolean yCakisma = (b.y < obj.y + 10) && (b.y + 5 > obj.y);
return xCakisma && yCakisma;
}

public boolean isCollidingBox(GameObject obj1, GameObject obj2) {
boolean xCakisma = (obj1.x < obj2.x + 10) && (obj1.x + 10 > obj2.x);
boolean yCakisma = (obj1.y < obj2.y + 10) && (obj1.y + 10 > obj2.y);
return xCakisma && yCakisma;
}


public void run(){
while(true){

try {
Thread.sleep(10);
} catch (InterruptedException e) {
e.printStackTrace();
}

synchronized (Main.pauseLock) {
while (!Main.isAction) {
try {
Main.pauseLock.wait(); 
} catch (InterruptedException e) {
break;
}}
}

boolean shouldRestart = false;
boolean shouldNextLevel = false;


synchronized(panel.bullets) {

for(int i=panel.bullets.size()-1;i>=0;i--){
Bullet b= panel.bullets.get(i);
boolean bulletRemoved = false;
if(b.owner.equals("player_left")){
if (b.x < 0){
panel.bullets.remove(i);
continue;
}else{
b.x -= 1;}}

if(b.owner.equals("player_right")){
if (b.x > 490){
panel.bullets.remove(i);
continue;
}else{
b.x += 1;}}

if(b.owner.equals("player_down")){
if (b.x > 490){
panel.bullets.remove(i);
continue;
}else{
b.y += 1;}}

if (b.owner.equals("friend_left")) {
if (b.x < 0){
panel.bullets.remove(i);
continue;
}else{
b.x -= 1;}}

if(b.owner.equals("friend_right")){
if (b.x > 490){
panel.bullets.remove(i);
continue;
}else{
b.x += 1;}}

if (b.owner.equals("enemy_left")) {
if (b.x < 0){
panel.bullets.remove(i);
continue;
}else{
b.x -= 1;}}

if(b.owner.equals("enemy_right")){
if (b.x > 490){
panel.bullets.remove(i);
continue;
}else{
b.x += 1;}}

if (b.owner.contains("bomb")) {// bomb
if (b.y < 0){
panel.bullets.remove(i);
continue;
}else{
b.y -= 1;}}

synchronized(panel.enemies) {
for (int j = panel.enemies.size() - 1; j >= 0; j--) {
Enemy e = panel.enemies.get(j);
if(isColliding(b,e)){
if(b.owner.contains("friend") ||b.owner.contains("player")) {
e.stopThread();
panel.enemies.remove(j);
panel.bullets.remove(i);
break;}}

if(isCollidingBox(panel.p, e)) {
shouldRestart = true;
break;}

}}

synchronized(panel.friends) {
for (int j = panel.friends.size() - 1; j >= 0; j--) {
Friend f = panel.friends.get(j);
if(isColliding(b,f)){
if(b.owner.contains("enemy")||b.owner.contains("bomb")){//bomb
f.stopThread();
panel.friends.remove(j);
panel.bullets.remove(i);;
break;}
}else{
continue;}}}

if(isColliding(b, panel.p) && (b.owner.contains("enemy") || b.owner.contains("bomb"))){//bomb
shouldRestart = true;
break;}

synchronized(panel.bombs) {
for (int j = panel.bombs.size() - 1; j >= 0; j--) {
BomberBox o = panel.bombs.get(j);
if(isColliding(b,o)){
if(b.owner.contains("friend") ||b.owner.contains("player")){
 if(o.can <=1){
 o.stopThread();
 panel.bombs.remove(j);
 panel.bullets.remove(i);
 break;
}else{
o.can--;
panel.bullets.remove(i);
break;
}}}



}}



}}



synchronized (panel.enemies) { 
synchronized (panel.friends) { 
for (int k = panel.enemies.size() - 1; k >= 0; k--) {
Enemy e = panel.enemies.get(k);
for (int j = panel.friends.size() - 1; j >= 0; j--) {
Friend f = panel.friends.get(j);


if (isCollidingBox(f, e)) {
e.stopThread();
f.stopThread();
panel.enemies.remove(k);
panel.friends.remove(j);
break;}

}}
}}

panel.repaint();

synchronized (panel.enemies) {
if (Main.isAction && gui.currentCard.equals("GameCard") && panel.enemies.isEmpty()) {
shouldNextLevel = true;}
}


if (shouldRestart) {
    System.out.println("PLAYER VURULDU! LEVEL RESTART");
    gui.restart();
    continue;
}

if (shouldNextLevel) {
    gui.nextLevel();
    continue; 
}
}
}




}



