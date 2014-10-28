import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;


public class Plane {
	Point planeHead;
	private Point planeLeft;
	private Point planeRigh;
	private Point planeLeftTail;
	private Point planeRighTail;
	double planeAngle = 0;

	public static final int rotateSpeed = 5;
	public static final double ACC = 0.1;	// acceleration
	public static final double DCC = 0.1;	// acceleration
	public static final double GAC = 0.08;	// gravititional acceleration
	public static final int MAX_SPEED = 6;
	
	
	private Point centerPoint;
	private final int planeHeadLong = 20;
	private final int planeBodyWidth = 8;
	private final int planeBackLong = 4;
	private final int planeTailLong = 6;
	private final int planeTailWidth = 10;
	
	boolean forward = false;
	
	boolean isR;

	private double speedAngle = 0;
	private double speedVal = 0;
	
	private Polygon planeshape; //the shape of plane;
	
	
	HashSet<tangle> tangleSet;
	
	AsterPanel parent;

	//newly added
	public int hitcount = 3;
	public int alive = 1;
	public int immutal = 0;
	public int totalpoints;
	
	
	public Plane(AsterPanel parent , boolean isR){
		this.isR = isR;
		this.parent = parent;
		centerPoint = new Point((new Random().nextInt(500)+200)
				,(new Random().nextInt(500)+200));
		ss("center"+centerPoint);
		planeHead = new Point(centerPoint.x , centerPoint.y+planeHeadLong);
		planeLeft = new Point(centerPoint.x-planeBodyWidth , centerPoint.y);
		planeRigh = new Point(centerPoint.x+planeBodyWidth , centerPoint.y);
		double bodySine = Math.sin(planeAngle/180*Math.PI);
		double bodyCose = Math.cos(planeAngle/180*Math.PI);
		planeLeftTail = new Point(
				(int)(-(bodySine*planeTailLong-bodyCose*planeBodyWidth) + centerPoint.x)
				,(int)(-(bodyCose*planeTailLong+planeBodyWidth*bodySine) + centerPoint.y) 
				);
		planeRighTail= new Point((int)(planeLeft.x-bodyCose*planeBodyWidth*2),
				(int)(planeLeft.y+bodySine*planeBodyWidth*2)
				);
		tangleSet = new HashSet<tangle>();
	}
	
	public void calcPlane(){
		if(this.hitcount<=0&&!parent.unlives){return;}
		double bodySine = Math.sin(planeAngle/180*Math.PI);
		double bodyCose = Math.cos(planeAngle/180*Math.PI);
		double deltx =0;
		double delty =0;
		double toCenterD = 
				centerPoint.distance(parent.getWidth()/2 , parent.getHeight()/2);
		if(forward){
			// calc inertia
			double spdSine = Math.sin(speedAngle/180*Math.PI);
			double spdCose = Math.cos(speedAngle/180*Math.PI);
			
			deltx = bodySine*ACC+spdSine*speedVal;
			delty = bodyCose*ACC+spdCose*speedVal;
			if(parent.gexist && toCenterD!=0 && !isR){
				deltx += GAC*(-centerPoint.x+parent.getWidth()/2)/toCenterD;
				delty += GAC*(-centerPoint.y+parent.getHeight()/2)/toCenterD;
			}
			if(delty==0){
				if(deltx>0)
					speedAngle=90;
				else
					speedAngle = 270;
			}else if(delty>0){
				speedAngle = Math.toDegrees(Math.atan(deltx/delty));
			}
			else{
				speedAngle = Math.toDegrees(Math.atan(deltx/delty))+180;
			}
			
			
			
			speedVal = Math.sqrt(deltx*deltx + delty*delty);
			if(speedVal>MAX_SPEED){
				speedVal = MAX_SPEED;
			}
		}
		else{
			//ss(speedVal);
			if(speedVal>0){speedVal-=DCC;}else{
				speedVal = 0;
			}
			deltx =  speedVal*Math.sin(speedAngle/180*Math.PI);
			delty =  speedVal*Math.cos(speedAngle/180*Math.PI);
			if(parent.gexist && toCenterD!=0 && !isR){
				deltx += GAC*(-centerPoint.x+parent.getWidth()/2)/toCenterD;
				delty += GAC*(-centerPoint.y+parent.getHeight()/2)/toCenterD;
			}
		}
		
		centerPoint.move(
				(int)(deltx+centerPoint.x), 
				(int)(delty+centerPoint.y));
		
		if(centerPoint.getX()<0){centerPoint.x=parent.getSize().width;}
		if(centerPoint.x>parent.getSize().width){centerPoint.x=0;}
		if(centerPoint.getY()<0){centerPoint.y= parent.getSize().height;}
		if(centerPoint.y>parent.getSize().height){centerPoint.y= 0;}
		//ss(centerPoint);
		planeHead.move(
				(int)(bodySine
						*planeHeadLong+centerPoint.x), 
				(int)(bodyCose
						*planeHeadLong+centerPoint.y));
		planeLeft.move(
				(int)(-(bodySine*planeBackLong-bodyCose*planeBodyWidth) + centerPoint.x)
				,(int)(-(bodyCose*planeBackLong+planeBodyWidth*bodySine) + centerPoint.y) 
				);

		planeRigh.move((int)(planeLeft.x-bodyCose*planeBodyWidth*2),
				(int)(planeLeft.y+bodySine*planeBodyWidth*2)
				);

		planeLeftTail.move(
				(int)(-(bodySine*(planeBackLong+planeTailLong)-bodyCose*planeTailWidth) + centerPoint.x)
				,(int)(-(bodyCose*(planeBackLong+planeTailLong)+planeTailWidth*bodySine) + centerPoint.y) 
				);
		planeRighTail.move((int)(planeLeftTail.x-bodyCose*planeTailWidth*2),
				(int)(planeLeftTail.y+bodySine*planeTailWidth*2)
				);
		if(isR){
			ss("tocenter" + toCenterD);
		}
	}

	private void drawLine(Point p1 , Point p2 , Graphics g){
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	public void draw(Graphics g){
		if(this.hitcount<=0){return;}
		if(this.immutal>0){
			if(immutal%3==0){
				drawLine(planeLeft , planeRigh , g);
				drawLine(planeLeft , planeHead , g);
				drawLine(planeRigh , planeHead , g);
				drawLine(planeLeft , planeLeftTail , g);
				drawLine(planeRigh , planeRighTail , g);
			}
		}else{
			drawLine(planeLeft , planeRigh , g);
			drawLine(planeLeft , planeHead , g);
			drawLine(planeRigh , planeHead , g);
			drawLine(planeLeft , planeLeftTail , g);
			drawLine(planeRigh , planeRighTail , g);
		}
		drawTangles(g);
	}
	public Rectangle getBounds(){
		int polyx[] = {planeLeft.x,planeRigh.x,planeHead.x,planeLeftTail.x,planeLeftTail.x};
		int polyy[] = {planeLeft.y,planeRigh.y,planeHead.y,planeLeftTail.y,planeLeftTail.y};
		planeshape = new Polygon(polyx,polyy,polyx.length);
		return planeshape.getBounds();
	}
	
	public void drawTangles(Graphics g){
		ArrayList<tangle> tangledeletes = new ArrayList<tangle>();
		for(tangle onebullet:tangleSet){
			onebullet.move(10,this.parent.getSize());
			g.drawRect(onebullet.getPoly().x,onebullet.getPoly().y,1,1);
			if(onebullet.getdeath()){
				tangledeletes.add(onebullet);
			}
		}
		for(tangle onebullet:tangledeletes){
			tangleSet.remove(onebullet);
		}
		tangledeletes = new ArrayList<tangle>();
	}
	public void addTangle(){
		this.tangleSet.add(new tangle(planeHead,planeAngle));
	}
	//newly added for save and load
	public Point getPos(){
		return this.centerPoint;
	}
	public HashSet<tangle> getTangles(){
		return this.tangleSet;
	}
	public void SetCenter(Point center){
		this.centerPoint = center;
	}
	public void Settangle(HashSet<tangle> newtangles){
		this.tangleSet=newtangles;
	}
	private void ss(Object o){System.out.println(o);}
}
