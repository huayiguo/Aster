import java.awt.Dimension;
import java.awt.Point;
class tangle {
	private 
		final int height = 1;
		final int width = 1;
		Point center;
		double speedAngle;
		
		//Rectangle Point;
		int age = 80;
	public tangle(Point newcenter, double nspeedAngle){
		center = new Point(newcenter.x,newcenter.y);
		this.speedAngle = nspeedAngle;
	}
	public Point getPoly(){
		return this.center;
	}
	public void move(int speed, Dimension point){
		age--;
		center.translate((int)(speed*Math.sin(speedAngle*Math.PI/180))
				,(int)(speed*Math.cos(speedAngle*Math.PI/180)));
		
		if(center.getX()<0){center.x=point.width;}
		if(center.x>point.width){center.x=0;}
		if(center.getY()<0){center.y= point.height;}
		if(center.y>point.height){center.y= 0;}
		
	}
	public boolean getdeath(){
		return this.age<=0;
	}
	
}
