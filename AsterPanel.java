import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class AsterPanel extends JPanel implements KeyListener,ComponentListener,MouseListener{
	
	private HashSet<Integer> keySet;
	
	
	private Stack<Stone> stoneList;
	
	private boolean menuOn = false;
	private boolean menuFirstOn = false;
	private boolean optionsOn = false;
	
	Font menuTitleFont = new Font("Courier" , Font.PLAIN , 50);
	Font menuTextFont = new Font("Courier" , Font.PLAIN , 30);
	
	private Rectangle grvExist , grvVisible , unmLives,
		nastSub,nastInc,restHigh,toLoad,startLevel,multRec,levelSub,levelInc;
	private Rectangle resumeRec , saveRec , exitRec;
	
	boolean gexist = false;
	private boolean gvisible;


	boolean unlives = true;
	private boolean multP = true;	// multiplayer
	private boolean printRank = false;
	private HashSet<Rectangle>menuRecs;
	
	private boolean hasMouse = false;
	
	// save file
	String outFileName = "";
	
	// planes
	Plane plane0 , plane1 , planer;
	
	// sound
	Clip shootClip;

	//alienship properties
	private Alien alienship;  
	private int firecounts;
	private HashSet<tangle> Alientangle;
	
	// rank
	ArrayList<Integer> toppoints;
	ArrayList<String> topstring;
	
	// level control
	private int level = 1;
	private int levelDisplayCount = 0;
	
	// number of stones(initially)
	int nstones = 3;
	private int gamefinished =0;
	
	
	public AsterPanel(){
		setOpaque(true);		// not transparent
		this.setBackground(Color.black);
		this	.setPreferredSize(new Dimension(800,1436));
		addComponentListener(this);
		
		plane0 = new Plane(this , false);
		plane1 = new Plane(this , false);
		planer = new Plane(this , true);
		
		keySet = new HashSet<Integer>();
		
		stoneList = new Stack<Stone>();
		menuRecs = new HashSet<Rectangle>();
		
		plane0.totalpoints = 0;
		plane1.totalpoints = 0;
		
		// init sound
		try {
			shootClip = AudioSystem.getClip();
		    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
		    		new File("biu.wav"));
		    shootClip.open(inputStream);
		    
		} catch (Exception e) {
			ss(e);
		}
		
		// alian
		alienship = new Alien();
		Alientangle = new HashSet<tangle>();
		
		toppoints = new ArrayList<Integer>();
		topstring = new ArrayList<String>();
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		topstring.add("aaa");
		
		Timer oneTimer = new Timer(this);
		oneTimer.start();
		
		
	}
	
	private void calcAll(){
		if(menuOn){return;}
		
		//alien part	
		alienship.move(this.getSize());
		if(firecounts==100 && alienship.dead==0){
			firecounts =0;
			this.Alientangle.add(new tangle(alienship.getPoint(),0));
			this.Alientangle.add(new tangle(alienship.getPoint(),60));
			this.Alientangle.add(new tangle(alienship.getPoint(),120));
			this.Alientangle.add(new tangle(alienship.getPoint(),180));
			this.Alientangle.add(new tangle(alienship.getPoint(),240));
			this.Alientangle.add(new tangle(alienship.getPoint(),300));
		}
		if(new Random().nextInt()%20==0){
			planer.forward = !planer.forward;
		}
		if(new Random().nextInt()%3==0){
			if(System.currentTimeMillis()%2000>1000){
				planer.planeAngle += Plane.rotateSpeed;
			}
			else{
				planer.planeAngle -= Plane.rotateSpeed;
			}
		}
		planer.forward = new Random().nextInt()%3!=0;
		
		if(new Random().nextInt()%20==0){
			planer.addTangle();
		}

		planer.calcPlane();
		plane0.calcPlane();
		if(multP){
			plane1.calcPlane();
		}
		
	}
	
	public void startup(){
		menuOn = true;
		menuFirstOn = true;
		optionsOn = true;
		repaint();
	}
	
	public void checkcollision(Plane onePlane){
		//check collision of plane
		onePlane.immutal--;
		if(onePlane.immutal<=0){
			Rectangle planebound = onePlane.getBounds();
			for(Stone thestone: stoneList){
				Rectangle stonebound = thestone.getPoly().getBounds();
				if(planebound.intersects(stonebound)){
					if(!unlives)onePlane.hitcount--;
					if(onePlane.hitcount==0&&!unlives){
						onePlane.alive = 0;
					}
					onePlane.SetCenter(new Point(this.getWidth()/2, this.getHeight()/2));
					onePlane.immutal=150;
				}
			}
			
			for(tangle thestangle: Alientangle){
				Point tanglebound = thestangle.getPoly();
				if(planebound.contains(tanglebound)){
					if(!unlives)onePlane.hitcount--;
					if(onePlane.hitcount==0&&!unlives){
						onePlane.alive = 0;
					}
					onePlane.SetCenter(new Point(this.getWidth()/2, this.getHeight()/2));
					onePlane.immutal=150;
				}
			}
			
			for(tangle thestangle: planer.tangleSet){
				Point tanglebound = thestangle.getPoly();
				if(planebound.contains(tanglebound)){
					if(!unlives)onePlane.hitcount--;
					if(onePlane.hitcount==0&&!unlives){
						onePlane.alive = 0;
					}
					onePlane.SetCenter(new Point(this.getWidth()/2, this.getHeight()/2));
					onePlane.immutal=150;
				}
			}

			if(gexist){
				if(planebound.contains(getWidth()/2 , getHeight()/2)
					|planebound.contains(getWidth()/2+20 , getHeight()/2)
					|planebound.contains(getWidth()/2 , getHeight()/2+20)
					|planebound.contains(getWidth()/2+20 , getHeight()/2+20)){
					if(!unlives)onePlane.hitcount--;
					if(onePlane.hitcount==0&&!unlives){
						onePlane.alive = 0;
					}
					onePlane.SetCenter(new Point(this.getWidth()/2, this.getHeight()/2));
					onePlane.immutal=150;
				}
			}
			
		}
		
		ArrayList<Stone> Deletes = new ArrayList<Stone>();
		ArrayList<tangle> deletes = new ArrayList<tangle>();
		ArrayList<Stone> Adds = new ArrayList<Stone>();
		//check collision of bullet and stones
		for(Stone thestone: stoneList){
			Rectangle stonebound = thestone.getPoly().getBounds();
			for(tangle thebullet: onePlane.tangleSet){
				Point bulletbound = thebullet.getPoly();
				if(stonebound.contains(bulletbound)){
					// delete the bullet, split the stone
					deletes.add(thebullet);
					onePlane.totalpoints+=5;
					if(thestone.stonesize==Stone.SMALL){
						//make it disappear
						
					}else if(thestone.stonesize==Stone.MIDIUM){
						Point newcenter = thestone.center;
						Adds.add(new Stone(Stone.SMALL, this,new Point(newcenter.x+5,newcenter.y+5)));
						Adds.add(new Stone(Stone.SMALL, this,new Point(newcenter.x-5,newcenter.y-5)));
						Adds.add(new Stone(Stone.SMALL, this,new Point(newcenter.x-5,newcenter.y-5)));
					}else{
						Point newcenter = thestone.center;
						Adds.add(new Stone(Stone.MIDIUM, this,new Point(newcenter.x+5,newcenter.y+5)));
						Adds.add(new Stone(Stone.MIDIUM, this,new Point(newcenter.x-5,newcenter.y-5)));
						Adds.add(new Stone(Stone.MIDIUM, this,new Point(newcenter.x-5,newcenter.y-5)));
					}
					Deletes.add(thestone);
				}
			}
		}
		for(tangle Thebullet: deletes){
			onePlane.tangleSet.remove(Thebullet);
		}
		for(Stone Thestone: Deletes){
			stoneList.remove(Thestone);
		}
		for(Stone Thestone: Adds){
			stoneList.add(Thestone);
		}

		//check collision of bullet and alienship
		killAlien(plane0);
		if(multP){killAlien(plane1);}
	}
	
	
	private void killAlien(Plane onePlane){
		Rectangle alienbound = alienship.getPoly().getBounds();
		ArrayList<tangle>deletes = new ArrayList<tangle>();
		if(alienship.dead==1){return;}
		for(tangle thebullet: onePlane.tangleSet){
			Point bulletbound = thebullet.getPoly();
			if(alienbound.contains(bulletbound)){
				if(alienship.dead==0){
					onePlane.totalpoints+=100;
					deletes.add(thebullet);
				}
				alienship.getHit();
			}
		}
		for(tangle oneBullet:deletes){onePlane.tangleSet.remove(oneBullet);}
	}
	
	private void drawRank(Graphics g){
		int startHeight = this.getHeight()/10;
		g.setFont(menuTitleFont);
		startHeight+=drawCenterString(g , "haha you are dead" , startHeight).height+5;
		g.setFont(menuTextFont);
		for(int ii=0;ii<10;ii++){
			String str = topstring.get(ii)+"\t\t"+toppoints.get(ii);
			startHeight+=drawCenterString(g , str , startHeight).height+5;
		}
	}
	
	private void drawMenu(Graphics g){
		if(printRank){
			drawRank(g);
		}
		else if(optionsOn){
			drawOptions(g);
		}else{
			drawPause(g);
		}
	}
	
	private void drawPause(Graphics g){
		g.setFont(menuTitleFont);
		int startHeight = this.getHeight()/8;
		startHeight += drawCenterString(g , "PAUSE" , startHeight).height;
		g.setFont(menuTextFont);
		resumeRec = drawCenterString(g , "continue" , startHeight);
		startHeight += resumeRec.height + 10;
		saveRec = drawCenterString(g , "save game" , startHeight);
		startHeight += saveRec.height + 10;
		exitRec = drawCenterString(g , "exit game" , startHeight);
		
		
		if(menuFirstOn){
			menuRecs.add(resumeRec);
			menuRecs.add(saveRec);
			menuRecs.add(exitRec);
		}
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePoint, this);
		for(Rectangle onerec:menuRecs){
			if(onerec.contains(mousePoint)){
				g.drawRect(onerec.x , onerec.y , onerec.width , onerec.height);
			}
		}
	}
	
	private void drawOptions(Graphics g){
		g.setFont(menuTitleFont);
		FontMetrics oneMetrics = g.getFontMetrics(g.getFont());
		int startHeight = this.getHeight()/8;	// 7 lines in menu
		startHeight += drawCenterString(g , "ASTEROID" , startHeight).height;

		g.setFont(menuTextFont);
		startHeight += drawCenterString(g , "press esc to start playing", startHeight).height+10;
		grvExist = drawCenterString(g , "Gravititional Obj Exist?..."+gexist , startHeight);
		startHeight += grvExist.height + 10;
		grvVisible = drawCenterString(g , "Gravititional Obj Visible?..."+gvisible , startHeight);
		startHeight += grvVisible.height + 10;
		unmLives = drawCenterString(g , "Unlimited lives?..."+unlives , startHeight);
		startHeight += unmLives.height + 30;
		
		g.drawString("--" , 
				this.getWidth()/2 - oneMetrics.stringWidth("-- 10 stones in ++")/2 , startHeight);
		nastSub = new Rectangle(
				this.getWidth()/2 - oneMetrics.stringWidth("-- 10 stones in ++")/2
				, startHeight - oneMetrics.getHeight(), 
				oneMetrics.stringWidth("--") , oneMetrics.getHeight());
		drawCenterString(g , nstones+" stones in screen" , startHeight);
		nastInc = new Rectangle(
				this.getWidth()/2 + oneMetrics.stringWidth("-- 10 stones in ++")/2
				, startHeight - oneMetrics.getHeight(), 
				oneMetrics.stringWidth("++") , oneMetrics.getHeight());
		g.drawString("++" , 
				this.getWidth()/2 + oneMetrics.stringWidth("-- 10 stones in ++")/2 , startHeight);
		startHeight += unmLives.height + 10;
		restHigh 	= drawCenterString(g , "reset high score" , startHeight);
		startHeight += unmLives.height + 10;
		toLoad 		= drawCenterString(g , "  load game  " , startHeight);
		startHeight += unmLives.height + 10;
		
		g.drawString("--" , 
				this.getWidth()/2 - oneMetrics.stringWidth("-- starting level ++")/2 , startHeight);
		levelSub = new Rectangle(
				this.getWidth()/2 - oneMetrics.stringWidth("-- starting level ++")/2
				, startHeight - oneMetrics.getHeight(), 
				oneMetrics.stringWidth("--") , oneMetrics.getHeight());
		drawCenterString(g , "starting level "+(level) , startHeight);
		levelInc = new Rectangle(
				this.getWidth()/2 + oneMetrics.stringWidth("-- 10 stones in ++")/2
				, startHeight - oneMetrics.getHeight(), 
				oneMetrics.stringWidth("++") , oneMetrics.getHeight());
		g.drawString("++" , 
				this.getWidth()/2 + oneMetrics.stringWidth("-- 10 stones in ++")/2 , startHeight);
		startHeight += unmLives.height + 10;
		multRec 	= drawCenterString(g , "multiple player.."+multP , startHeight);
		
		if(menuFirstOn){
			menuRecs.add(grvExist);
			//ss(grvExist);
			menuRecs.add(grvVisible);
			//ss(grvVisible);
			menuRecs.add(unmLives);
			//ss(unmLives);
			menuFirstOn = false;
			menuRecs.add(nastSub);
			menuRecs.add(nastInc);
			menuRecs.add(restHigh);
			menuRecs.add(toLoad);
			menuRecs.add(levelSub);
			menuRecs.add(levelInc);
			menuRecs.add(multRec);
		}
		
		Point mousePoint = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mousePoint, this);
		
		for(Rectangle onerec:menuRecs){
			if(onerec.contains(mousePoint)){
				g.drawRect(onerec.x , onerec.y , onerec.width , onerec.height);
			}
		}
	}
	
	private void drawNotMenu(Graphics g){
		for(Stone oneSt:stoneList){
			oneSt.moveDir(level+1);
			g.drawPolygon(oneSt.getPoly());
		}
		g.setColor(Color.GREEN);
		ArrayList<tangle> tangledeletes = new ArrayList<tangle>();
		for(tangle onebullet:Alientangle){
			onebullet.move(10,this.getSize());
			g.drawRect(onebullet.getPoly().x,onebullet.getPoly().y,1,1);
			if(onebullet.getdeath()){
				tangledeletes.add(onebullet);
			}
		}
		for(tangle onebullet:tangledeletes){
			Alientangle.remove(onebullet);
		}
		g.setFont(menuTextFont);
		g.setColor(Color.WHITE);	
		String a = String.valueOf(plane0.totalpoints);
		String b = "Player 1 Points: "+a;
		g.drawString(b, 50, 30);
		if(multP){
			b = "Player 2 Points: "+plane1.totalpoints;
			g.drawString(b, 50, 80);
		}
		// level stuff
		if(levelDisplayCount!=0){
			levelDisplayCount--;
			g.drawString("level"+(level-1), this.getWidth()/2-50, this.getHeight()/2);
			firecounts = 0;
		}
		
		if(gexist&gvisible){
			g.fillRect(this.getWidth()/2, this.getHeight()/2, 20, 20);
		}
		if(plane0.hitcount>=3){
			g.fillOval(50, this.getHeight()-100, 10,10);
		}
		if(plane0.hitcount>=2){
			g.fillOval(100, this.getHeight()-100, 10,10);
		}
		if(plane0.hitcount>=1){
			g.fillOval(150, this.getHeight()-100, 10,10);
		}
		if(plane1.hitcount>=3){
			g.fillOval(this.getWidth()-50, this.getHeight()-100, 10,10);
		}
		if(plane1.hitcount>=2){
			g.fillOval(this.getWidth()-100, this.getHeight()-100, 10,10);
		}
		if(plane1.hitcount>=1){
			g.fillOval(this.getWidth()-150, this.getHeight()-100, 10,10);
		}
	}
	@Override	// main
	protected void paintComponent(Graphics g){
		g.setColor(Color.WHITE);
		super.paintComponent(g);
		if(menuOn){
			drawMenu(g);
		}
		else{
			drawNotMenu(g);
			alienship.draw(g);
			g.setColor(Color.red);
			planer.draw(g);
			g.setColor(Color.cyan);
			plane0.draw(g);
			if(multP){
				g.setColor(Color.orange);
				plane1.draw(g);
			}
			g.setColor(Color.orange);
		}
	}
	
	private Rectangle drawCenterString(Graphics g , String str , int height){
		
		FontMetrics oneMetrics = g.getFontMetrics(g.getFont());
		g.drawString(str , 
				this.getWidth()/2 - oneMetrics.stringWidth(str)/2 , height);
		return new Rectangle(
				this.getWidth()/2 - oneMetrics.stringWidth(str)/2 - 10
				, height - oneMetrics.getHeight(), 
				oneMetrics.stringWidth(str) +20 , oneMetrics.getHeight() + 3);
	}
	
	private void handleLevel(){
		level++;
		if(level==2){
			for(int ii=0;ii<nstones;ii++){
				addStone();
			}
		}else{
			for(int ii=0;ii<level*2+2;ii++){
				addStone();
			}
		}
		alienship = new Alien();
		
		levelDisplayCount = 100;
	}
	
	public void timerHandler(){
		
		if(menuOn){
			repaint();
			return;
		}
		
		if(gamefinished==1){
			printRank = true;
			menuOn = true;
			repaint();
			return;
		}
		
		if(stoneList.size()==0){
			handleLevel();
		}
		if(!multP){
			if(plane0.alive==0){
				gamefinished=1;
				Rank();
				return;
			}
		}else{
			if((plane0.alive==0)&(plane1.alive==0)){
				gamefinished =1;
				Rank();
				return;
			}
		}
		firecounts++;
		Iterator<Integer> i = keySet.iterator();
		plane0.forward = false;
		plane1.forward = false;
		while(i.hasNext()){
			switch(i.next()){
			case KeyEvent.VK_W:
				plane1.forward = true;
				break;
			case KeyEvent.VK_A:
				plane1.planeAngle+=Plane.rotateSpeed;
				break;
			case KeyEvent.VK_D:
				plane1.planeAngle-=Plane.rotateSpeed;
				break;

			case KeyEvent.VK_UP:
				plane0.forward = true;
				break;
			case KeyEvent.VK_LEFT:
				plane0.planeAngle+=Plane.rotateSpeed;
				break;
			case KeyEvent.VK_RIGHT:
				plane0.planeAngle-=Plane.rotateSpeed;
				break;
				
			case KeyEvent.VK_F:
				plane1.addTangle();
				new Thread(){
					public void run(){
					shootClip.setFramePosition(0);
					shootClip.start();
				}}.start();
				break;
			case KeyEvent.VK_ENTER:
				//ss("colon");
				plane0.addTangle();
				new Thread(){
					public void run(){
					shootClip.setFramePosition(0);
					shootClip.start();
				}}.start();
				break;
			}
		}
		keySet.remove(KeyEvent.VK_ENTER);
		keySet.remove(KeyEvent.VK_F);
		
		calcAll();
		checkcollision(plane0);
		if(multP){checkcollision(plane1);}
		repaint();
	}
	
	public void addStone(){
		stoneList.add(new Stone(Math.abs(new Random().nextInt()%3),this));
	}
	
	private void menuHandler(){
		menuOn = !menuOn;
		if(menuOn){
			menuFirstOn = true;
		}
		else{
			menuRecs.clear();
			optionsOn = false;
		}
		repaint();
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE){
			menuHandler();
		}
		if(e.getKeyCode()!=KeyEvent.VK_F && e.getKeyCode()!=KeyEvent.VK_ENTER){
			keySet.add(e.getKeyCode());
		}
		timerHandler();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_F || e.getKeyCode()==KeyEvent.VK_ENTER){
			keySet.add(e.getKeyCode());
		}else{
			keySet.remove(e.getKeyCode());
		}
		//handleKeys();
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void componentResized(ComponentEvent e) {
		for(Stone oneStone:stoneList){
			oneStone.setSuperSize(this.getSize());
		}
    }
	private static final long serialVersionUID = 1L;
	private String si(){return new Scanner(System.in).nextLine();}
	private void ss(Object o){System.out.println(o);}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// DO NOTHING
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		// DO NOTHING
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		// DO NOTHING
		
	}
	
	private void handleOptions(Point me){
		if(grvExist.contains(me)){
			//ss("grv exist");
			gexist = !gexist;
		}
		else if(grvVisible.contains(me)){
			gvisible = !gvisible;
		}
		else if(unmLives.contains(me)){
			unlives = !unlives;
		}
		else if(nastSub.contains(me)){
			nstones--;
		}
		else if(nastInc.contains(me)){
			nstones++;
		}
		else if(restHigh.contains(me)){
			// TODO reset high score holder
			ResetRank();
		}
		else if(toLoad.contains(me)){
			try {
				Load();
			} catch (IOException e) {
				e.printStackTrace();
			}
			menuOn = false;
			optionsOn = false;
		}
		else if(multRec.contains(me)){
			multP = !multP;
		}
		else if(levelSub.contains(me)){
			level--;
		}
		else if(levelInc.contains(me)){
			level++;
		}
	}
	
	private void handlePause(Point me){
		if(resumeRec.contains(me)){
			menuOn = false;
		}
		if(saveRec.contains(me)){
			Save();
		}
		if(exitRec.contains(me)){
			int result = JOptionPane.showConfirmDialog(null, 
					   "Do you want to save your game?",null,
					   JOptionPane.YES_NO_CANCEL_OPTION);
			if(result == JOptionPane.CANCEL_OPTION) {
			    return;
			}
			if(result==JOptionPane.YES_OPTION){
				if(!Save()){
					return;
				}
			}
			System.exit(0);
		}
	}

	@Override
	public void mouseClicked(MouseEvent mouseE) {
		if(!menuOn){return;}
		Point me = mouseE.getPoint();
		me.y -= 24;
		if(optionsOn){
			handleOptions(me);
		}
		else{
			handlePause(me);
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		hasMouse = true;
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		hasMouse = false;
	}

	@Override
	public void mousePressed(MouseEvent me) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	

	public void Savetofile(PrintStream ps){
		//save stones
		ps.println(stoneList.size());
		for(Stone thestone: stoneList){
			ps.println(thestone.stonesize);
			ps.println((int)thestone.center.x+" "+(int)thestone.center.y);
			ps.println(thestone.GetmoveRad());
		}
		//save planes
		if(multP){
			ps.println("2");
			ps.println((int)plane1.getPos().x+" "+(int)plane1.getPos().y);
		}
		else ps.println("1");
		ps.println(plane0.getPos().x+" "+plane0.getPos().y);
		//HashSet<tangle> 
		//save alienship
		if(alienship.dead==1){
			ps.println("dead");
		}else {
			ps.println("live");
			ps.println(alienship.getPoint().x+" "+alienship.getPoint().y);
			ps.println(alienship.getHitpoint());
		}
		
		ps.close();
	}
	
	public void readfile(BufferedReader reader) throws IOException{
		stoneList.clear();
		String line = null;
		int stones = 0;
		//read stones
		
		line = reader.readLine();
		stones = Integer.parseInt(line.split("\n")[0]);
		
		int stonesize,centerx,centery;
		float newrad;
		for(int i=0;i<stones;i++){
			line = reader.readLine();
			ss(line.split("\n")[0]);
			stonesize = Integer.parseInt(line.split("\n")[0]);
			line = reader.readLine();
			String[] sub = line.split(" ");
			centerx = Integer.parseInt(sub[0]);
			centery = Integer.parseInt(sub[1].split("\n")[0]);
			line = reader.readLine();
			newrad = Float.parseFloat(line.split("\n")[0]);
			Stone newstone = new Stone(stonesize,this,new Point(centerx,centery));
			newstone.SetmoveRad(newrad);
			stoneList.add(newstone);
		}
		//read planes
		plane0 = new Plane(this , false);
		plane1 = new Plane(this , false);
		planer = new Plane(this , true);
		line = reader.readLine();
		if(Integer.parseInt(line.split("\n")[0])==2){
			//means multi-player
			line = reader.readLine();
			String[] sub = line.split(" ");
			centerx = Integer.parseInt(sub[0]);
			centery = Integer.parseInt(sub[1].split("\n")[0]);
			plane1.SetCenter(new Point(centerx,centery));
		}
		line = reader.readLine();
		String[] sub = line.split(" ");
		centerx = Integer.parseInt(sub[0]);
		centery = Integer.parseInt(sub[1].split("\n")[0]);
		plane0.SetCenter(new Point(centerx,centery));
		
		//read alienship
		line = reader.readLine();
		if(line.split("\n")[0].equals("live")){
			alienship = new Alien();
			line = reader.readLine();
			sub = line.split(" ");
			centerx = Integer.parseInt(sub[0]);
			centery = Integer.parseInt(sub[1].split("\n")[0]);
			line = reader.readLine();
			int hitpoint = Integer.parseInt(line.split("\n")[0]);
			alienship.setHitpoint(hitpoint);
			alienship.setCenter(new Point(centerx,centery));
		}	
		

	}
	
	public void Load() throws IOException{
		JFileChooser chooser = new JFileChooser();
	    chooser.getCurrentDirectory();
		//showSaveDialog(parent);
		int returnVal = chooser.showOpenDialog(chooser);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File file = chooser.getSelectedFile();
		    try {
		    	BufferedReader reader = new BufferedReader(new FileReader(file));
				readfile(reader);
		    } catch (IOException ioe) {
		    	System.out.println("IOerror!");
		        // ... handle errors!
		    }
		}
	}
	
	public boolean Save(){
		// set up the jfilechooser...
		JFileChooser chooser = new JFileChooser();
	    chooser.getCurrentDirectory();
		//showSaveDialog(parent);
		int returnVal = chooser.showSaveDialog(chooser);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
		    File file = chooser.getSelectedFile();
		    try {
		        PrintStream ps = new PrintStream(file);
		        Savetofile(ps);
		    } catch (IOException ioe) {
		    	System.out.println("IOerror!");
		        // ... handle errors!
		    }
		    return true;
		}
		return false;
	}

    public void ResetRank(){
		File file = new File("rank");
		try {
			resetrankhelper(file);
		} catch (IOException ioe) {
			System.out.println("IOerror!");
			// ... handle errors!
		}
	}
    
	public void resetrankhelper(File file) throws IOException{
		int i;
		PrintStream ps = new PrintStream(file);
		for(i=0;i<10;i++){
			ps.println(0);
			ps.println("");
		}
		ps.close();
	}
    
	public void Rank(){
		    File file = new File("rank");
		    try {
		    	BufferedReader reader = new BufferedReader(new FileReader(file));
				rankhelper(reader,file);
		    } catch (IOException ioe) {
		    	System.out.println("IOerror!");
		        // ... handle errors!
		    }
			
	}


	public void rankhelper(BufferedReader reader, File file) throws IOException{
		toppoints.clear();
		topstring.clear();
		String line =null;
		int temppoints;
		for(int i=0;i<10;i++){
			line = reader.readLine();
			temppoints =Integer.parseInt(line.split("\n")[0]);
			toppoints.add(temppoints);
			line = reader.readLine();
			topstring.add(line.split("\n")[0]);
		}
		if(multP){
			int i=0;
			while(plane1.totalpoints<toppoints.get(i)){
				i++;
				if(i==10)break;
			}
			if(i==10);//nothing happened or?
			else{
				if(i==0){
					toppoints.set(0, plane1.totalpoints);
				}else{
					int k=9;
					while(k!=i){
						toppoints.set(k, toppoints.get(k-1));
						k--;
					}
					toppoints.set(i, plane1.totalpoints);
				}
				String name= JOptionPane.showInputDialog("Congrats! player2\nPlease Enter your name: ");
				if(name.isEmpty()){
					name = "HAHA YOU HAVE NO NAME..";
				}
				topstring.set(i,name);
			}
			
		}
		int i=0;
		while(plane0.totalpoints<toppoints.get(i)){
			i++;
			if(i==10)break;
		}
		if(i==10);//nothing happened or?
		else{
			if(i==0){
				toppoints.set(0, plane0.totalpoints);
			}else{
				int k=9;
				while(k!=i){
					toppoints.set(k, toppoints.get(k-1));
					k--;
				}
				toppoints.set(i, plane0.totalpoints);
			}
			String name= JOptionPane.showInputDialog("Congrats! player1\nPlease Enter your name: ");
			topstring.set(i,name);
		}
		PrintStream ps = new PrintStream(file);
		for(i=0;i<10;i++){
			ps.println(toppoints.get(i));
			ps.println(topstring.get(i));
		}
		ps.close();
	}
	
	
}

class Timer extends Thread{
	private void ss(Object o){System.out.println(o);}
	AsterPanel master;
	public Timer(AsterPanel master){
		this.master = master;
	}
	@Override
	public void run(){
		try {
			sleep(1000);
			master.startup();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(true){
			try{
				while(true){
					master.timerHandler();
					sleep(20);
				}
			}catch(Exception e){ss("sleep failed??");
			e.printStackTrace();}
		}
	}
}
