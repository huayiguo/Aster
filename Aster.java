import javax.swing.JFrame;


public class Aster {
	public static void main(String args[]){
		JFrame oneFrame = new JFrame("framename");
		oneFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AsterPanel onePanel = new AsterPanel();
		
		oneFrame.add(onePanel);
		oneFrame.addKeyListener(onePanel);
		oneFrame.addMouseListener(onePanel);
		
		oneFrame.setSize(800,1436);
		oneFrame.pack();
		oneFrame.setVisible(true);
		
		
	}
}
