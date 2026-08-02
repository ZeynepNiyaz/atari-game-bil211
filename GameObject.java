import java.util.Random;
import java.io.Serializable;
public abstract class GameObject extends Thread implements Serializable{

int x;
int y;
String owner;
String type;
Main.GamePanel panel;
Random random = new Random();
int tick = 0;

public GameObject(int x,int y,String type,Main.GamePanel panel){
this.x = x;
this.y = y;
this.type=type;
this.panel = panel;
}


public volatile boolean isAlive = true; 

public void stopThread() {
this.isAlive = false; 
this.interrupt();
}

public void run(){
while(isAlive){



Object lock;
if (this.type.equals("friend")) {
lock = panel.friends;
}else if(this.type.equals("enemy")) {
lock = panel.enemies;
}else if(this.type.equals("bombs")){
lock = panel.bombs;
}else{
lock="";}



try {
Thread.sleep(1000);
tick++;
if(tick %2==0){

synchronized (Main.pauseLock) {
while (!Main.isAction) {
try {
Main.pauseLock.wait(); 
} catch (InterruptedException e) {
break;
}}
}


synchronized(panel.bullets) {

if (this.type.equals("bomb")) {
Bullet b3 = new Bullet(x, y + 5, type+"_up");
panel.bullets.add(b3);
}else{
Bullet b1 = new Bullet(x, y + 5, type+"_left");
panel.bullets.add(b1);
Bullet b2 = new Bullet(x + 10, y + 5, type+"_right");
panel.bullets.add(b2);
}}

}
} catch (InterruptedException e) {
break;
}


synchronized(lock) {
int num= random.nextInt(4);

if(num ==0){
if(this.x >=490){
this.x=490;
}else{
this.x +=10;}
}else if( num==1){
if(this.x <=0){
this.x=0;
}else{
this.x -= 10;}}

if (this.type.equals("bomb")) {
if(num==2){
if(this.y >= 540){
this.y=540;
}else{
this.y +=10;}
}else if(num==3){
if(this.y <= 500){
this.y=500;
}else{
this.y -= 10;}}
continue;}




if(num==2){
if(this.y >= 490){
this.y=490;
}else{
this.y +=10;}
}else if(num==3){
if(this.y <= 0){
this.y=0;
}else{
this.y -= 10;}}

panel.repaint();
}
}
}
}
