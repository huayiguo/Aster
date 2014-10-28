import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Random;
import java.util.Scanner;

class Stone {
	private Polygon shape;
	
	final int MAX_CORNERS = 20;
	final int MIN_CORNERS = 10;
	
	final int BIG_SIZE = 50;
	final int MID_SIZE = 20;
	final int SMA_SIZE = 10;
	final int DELTR = 10;
	
	final static int SMALL = 0;
	final static int MIDIUM = 1;
	final static int LARGE = 2;
	
	final private int SMALL_SIZE = 20;
	final private int MIDIUM_SIZE = 40;
	final private int LARGE_SIZE = 80;
	
	float moveRad;
	
	public int stonesize;
	public Point center;
	AsterPanel parent;
	private void ss(Object o){System.out.println(o);}
	public Stone(int size , AsterPanel parent){	// 1 or 2 or 3 as largest ..
		this.parent = parent;
		shape = new Polygon();
		center = new Point();
		
		// generate shape randomly
		Random rdm = new Random(System.nanoTime());
				
		int drawSize;
		if(size==SMALL){
			drawSize = SMALL_SIZE;
		}else if(size==MIDIUM){
			drawSize = MIDIUM_SIZE;
		}else{
			drawSize = LARGE_SIZE;
		}
		stonesize = size;
		int ii=Math.abs(rdm.nextInt()%MAX_CORNERS) + MIN_CORNERS;
		float deltAng = 360/ii;

		if(rdm.nextBoolean()){
			center.x = Math.abs(rdm.nextInt()%parent.getSize().width);
			center.y = -LARGE_SIZE-10;
		}
		else{
			center.y = Math.abs(rdm.nextInt()%parent.getSize().height);
			center.y = -LARGE_SIZE-10;
		}
		moveRad = rdm.nextInt(3)*90+rdm.nextInt(30);
		moveRad = (float) (moveRad*Math.PI/180);	// in radix
		//ss(moveRad + ",("+center.x+","+center.y+")");
		for(; ii>0; ii--){
			int rr = drawSize-Math.abs(rdm.nextInt()%DELTR);
			//ss(rr + ","+Math.sin(deltAng*ii/180.0*Math.PI)*rr + " , " + 
			//Math.cos(deltAng*ii/180.0*Math.PI)*rr);
			shape.addPoint((int)(Math.sin(deltAng*ii/180.0*Math.PI)*rr)+center.x,
				(int)(Math.cos(deltAng*ii/180.0*Math.PI)*rr)+center.y);
		}
		
	}
	//newly added
	public Stone(int size , AsterPanel parent, Point postion){
		this.parent = parent;
		shape = new Polygon();
		center = postion;
		int drawSize;
		stonesize = SMALL;
		if(size==SMALL){
			drawSize = SMALL_SIZE;
		}else if(size==MIDIUM){
			drawSize = MIDIUM_SIZE;
		}else{
			drawSize = LARGE_SIZE;
		}
		stonesize = size;
		Random rdm = new Random(System.nanoTime());
		int ii=Math.abs(rdm.nextInt()%MAX_CORNERS) + MIN_CORNERS;
		float deltAng = 360/ii;
		moveRad = rdm.nextInt(3)*90+rdm.nextInt(30);
		moveRad = (float) (moveRad*Math.PI/180);	// in radix
		for(; ii>0; ii--){
			int rr = drawSize-Math.abs(rdm.nextInt()%DELTR);
			//ss(rr + ","+Math.sin(deltAng*ii/180.0*Math.PI)*rr + " , " + 
			//Math.cos(deltAng*ii/180.0*Math.PI)*rr);
			shape.addPoint((int)(Math.sin(deltAng*ii/180.0*Math.PI)*rr)+center.x,
				(int)(Math.cos(deltAng*ii/180.0*Math.PI)*rr)+center.y);
		}
		
	}
	
	private String si(){return new Scanner(System.in).nextLine();}
	public Polygon getPoly(){return this.shape;}
	
	public void moveDir(double speed){
		center.translate((int)(speed*Math.sin(moveRad))
				,(int)(speed*Math.cos(moveRad)));
		this.shape.translate((int)(speed*Math.sin(moveRad))
				,(int)(speed*Math.cos(moveRad)));
		
		Rectangle boundRec = shape.getBounds();
		
		if(boundRec.getMaxX()<0){shape.translate(parent.getSize().width+boundRec.width, 0);}
		if(boundRec.x>parent.getSize().width){shape.translate(-parent.getSize().width-boundRec.width, 0);}
		if(boundRec.getMaxY()<0){shape.translate(0, parent.getSize().height+boundRec.height);}
		if(boundRec.y>parent.getSize().height){shape.translate(0, -parent.getSize().height-boundRec.height);}
		
	}
	public void setSuperSize(Dimension d){
		this.parent.getSize().width = d.width;
		this.parent.getSize().height = d.height;
	}
	
	// save load
	//newly added
	public void SetmoveRad(float newRad){
		this.moveRad=newRad;
	}
	public float GetmoveRad(){
		return this.moveRad;
	}
}

