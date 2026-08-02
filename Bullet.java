import java.io.Serializable;
public class Bullet implements Serializable{

int x;
int y;
String owner;

public Bullet(int x, int y, String owner) {
this.x = x;
this.y = y;
this.owner = owner;
}
}
