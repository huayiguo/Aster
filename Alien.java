import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.util.Random;


class Alien {
	private Polygon shape;
	private Point center;
	private int hitpoint;
	public int dead;
	public Alien(){
		dead = 0;
		center = new Point(100,350);	
		hitpoint = 3;
		int ployx[] = {center.x-20,center.x-20,center.x+20,center.x+20};
		int ployy[] = {center.y+7,center.y-7,center.y+7,center.y-7};
		this.shape = new Polygon(ployx,ployy,ployx.length);
	}
	public void getHit(){
		hitpoint--;
		if(hitpoint==0){
			dead = 1;
		}
	}
	public void draw(Graphics g){
		if(dead==1)return;
		if(hitpoint ==2)g.setColor(Color.GREEN);
		if(hitpoint ==1)g.setColor(Color.RED);
		g.drawLine(center.x-20,center.y+2,center.x+20,center.y+2);
		g.drawLine(center.x-20,center.y-2,center.x+20,center.y-2);
		g.drawArc(center.x-15,center.y-7, 30, 14, 0, 360);	
		g.setColor(Color.WHITE);
	}
	
	int xdir = new Random().nextInt()%2*2;
	int ydir = new Random().nextInt()%2*2;
	public void move(Dimension bounds){
		if(dead==1)return;
		if(new Random().nextInt()%50==0){
			xdir = new Random().nextInt()%2*2;
			ydir = new Random().nextInt()%2*2;
		}
		Point nextcenter = new Point(center.x+xdir , center.y+ydir);
		center = nextcenter;
		if(center.getX()<0){center.x=bounds.width;}
		if(center.x>bounds.width){center.x=0;}
		if(center.getY()<0){center.y= bounds.height;}
		if(center.y>bounds.height){center.y= 0;}
		int ployx[] = {center.x-20,center.x-20,center.x+20,center.x+20};
		int ployy[] = {center.y+2,center.y-2,center.y+2,center.y-2};
		this.shape = new Polygon(ployx,ployy,ployx.length);
	}
	public Polygon getPoly(){
		return this.shape;
	}
	public Point getPoint(){
		return this.center;
	}

	//newly added for save and load
	public void setCenter(Point newcenter){
		this.center=newcenter;
	}
	public int getHitpoint(){
		return this.hitpoint;
	}
	public void setHitpoint(int newhitpoint){
		this.hitpoint = newhitpoint;
	}
}
