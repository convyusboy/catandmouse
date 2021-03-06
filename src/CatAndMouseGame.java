import java.awt.*;
import javax.swing.*;

public class CatAndMouseGame extends Thread {
	long delay;
	boolean first = true;
	SwingApplet a;
	RLPolicy policy;
	CatAndMouseWorld world;
	static final int GREEDY=0, SMART=1; // type of mouse to use
	int mousetype = SMART;
	
	public boolean gameOn = false, single=false, gameActive, newInfo = false;
	
	public CatAndMouseGame(SwingApplet s, long delay, CatAndMouseWorld w, RLPolicy policy) {
		world = w;
		
		a=s;
		this.delay = delay;
		this.policy = policy;
	}
	
	/* Thread Functions */
	public void run() {
		System.out.println("--Game thread started");
		// start game
		try {
			while(true) {
				while(gameOn) {
					gameActive = true;
					if (first)
						first = false;
					else
						resetGame();						
					SwingUtilities.invokeLater(a); // draw initial state
					runGame();
					gameActive = false;
					newInfo = true;
					SwingUtilities.invokeLater(a); // update state
					sleep(delay);
				}
				sleep(delay);
			}
		} catch (InterruptedException e) {
			System.out.println("interrupted.");
		}
		System.out.println("== Game finished.");
	}
	
	public void runGame() {
		while(!world.endGame()) {
			//System.out.println("Game playing. Making move.");
			int action=-1;
			if (mousetype == GREEDY) {
				action = world.mouseAction();
			} else if (mousetype == SMART) {
				if (Math.random() < 0.2) action = world.mouseAction();
				else action = policy.getBestAction(world.getState());
			} else {
				System.err.println("Invalid mouse type:"+mousetype);
			}
			world.getNextState(action);

			//a.updateBoard();
			SwingUtilities.invokeLater(a);
				
			try {
				sleep(delay);
			} catch (InterruptedException e) {
				System.out.println("interrupted.");
			}
			a.mousescore += world.mousescore;
		}
		
		a.episode += 1;
		//a.catscore += world.catscore;
		
		// turn off gameOn flag if only single game
		if (single) gameOn = false;
	}
	
	public void interrupt() {
		super.interrupt();
		System.out.println("(interrupt)");
	}
	
	/* end Thread Functions */

	public void setPolicy(RLPolicy p) {	policy = p; }
	
	public Dimension getMouse() { return new Dimension(world.mx, world.my); }
	public Dimension getCat(int i) { return new Dimension(world.cx[i], world.cy[i]); }
	public Dimension getCheese(int i) { return new Dimension(world.chx[i], world.chy[i]); }
	public Dimension getHole() { return new Dimension(world.hx, world.hy); }
	public boolean[][] getWalls() { return world.walls; }
	public boolean[][] getCats() { return world.cats; }
	public boolean[][] getCheeses() { return world.cheeses; }
	public int getPosisiMouse(){ return world.getPosisiMouse(); }
	
	public void makeMove() {
		world.moveMouse();
		//world.moveCat();
	}

	public void resetGame() {
		world.resetState();
	}
}

