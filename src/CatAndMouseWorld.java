import java.awt.Dimension;
import java.util.ArrayList;

public class CatAndMouseWorld implements RLWorld{
	public int bx, by;
	public int numCats, numCheeses;
	public int posisiMouse;
	public int cntpos;
	
	public int mx, my;
	public int cx[], cy[];
	public int chx[], chy[];
	public int hx, hy;
	public boolean gotCheese = false;
	
	public int catscore = 0, mousescore = 0;
	public int cheeseReward=50, deathPenalty=100;
	
	static final int NUM_OBJECTS=6, NUM_ACTIONS=8, WALL_TRIALS=100, CAT_TRIALS=100, CHEESE_TRIALS=100;
	static final double INIT_VALS=0;
	static final int Kanan=0, KananAtas=1, Atas=2, KiriAtas=3, Kiri=4, KiriBawah=5, Bawah=6, KananBawah=7;
	
		
	int[] stateArray;
	double waitingReward;
	public boolean[][] walls;
	public boolean[][] cats;
	public boolean[][] cheeses;
	
	//Information from Parser
	public ArrayList<Point> set_pos;
	public int[][] train_map;
	
	public int getPosisiMouse(int posisiMouse){
		return posisiMouse;
	}

	public CatAndMouseWorld(int x, int y, int numWalls, int numCats, int numCheeses) {
		bx = x;
		by = y;
		makeWalls(x,y,numWalls);
		
		this.numCats = numCats;
		this.numCheeses = numCheeses;
		
		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];
		
		resetState();
	}
	
	public CatAndMouseWorld(int x, int y,int numWalls, String namafile) {
		parser parserai = new parser();
		parserai.readFromFile(namafile, 1);
		bx = x;
		by = y;
		makeWalls(x,y,numWalls);
		
		this.numCats = parserai.getCat();
		this.numCheeses = parserai.getCheese();
		
		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];
		
		set_pos = parserai.getCheesePlay();
		train_map = parserai.getTrainMap();
		resetState();
	}
	public CatAndMouseWorld(String namafile1, String namafile2, int numWalls) {
		parser parserai = new parser();
		parserai.readFromFile(namafile1, 1);
		parserai.readFromFile(namafile2, 2);
		
		bx = parserai.getBx();
		by = parserai.getBy();
		
		this.numCats = parserai.getCat();
		this.numCheeses = parserai.getCheese();
		
		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];
		
		set_pos = parserai.getCheeseTrain();
		train_map = parserai.getTrainMap();
		resetState();
	}
	
	
	public CatAndMouseWorld(int x, int y, boolean[][] newwalls, boolean[][] newcats, boolean[][] newcheeses) {
		bx = x;
		by = y;
		
		walls = newwalls;
		cats = newcats;
		cheeses = newcheeses;
		
		numCats = 0;
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				if (newcats[i][j]) numCats++;
		
		numCheeses = 0;
		for(int i = 0; i < x; i++)
			for(int j = 0; j < y; j++)
				if (newcats[i][j]) numCheeses++;
		
		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];
		
		resetState();
	}

	/******* RLWorld interface functions ***********/
	public int[] getDimension() { 
		int[] retDim = new int[NUM_OBJECTS+1];
		int i;
		for (i=0; i<NUM_OBJECTS;) {
			retDim[i++] = bx;
			retDim[i++] = by;
		}
		retDim[i] = NUM_ACTIONS;
		
		return retDim;
	}
	
	public void rotateKiri(int posisiMouse){
		if(posisiMouse==Kanan) posisiMouse=KananAtas;
		else if (posisiMouse==KananAtas) posisiMouse=Atas;
		else if (posisiMouse==Atas) posisiMouse=KiriAtas;
		else if (posisiMouse==KiriAtas) posisiMouse=Kiri;
		else if (posisiMouse==Kiri) posisiMouse=KiriBawah;
		else if (posisiMouse==KiriBawah) posisiMouse=Bawah;
		else if (posisiMouse==Bawah) posisiMouse=KananBawah;
		else if (posisiMouse==KananBawah) posisiMouse=Kanan;
	}
	
	public void rotateKanan(int posisiMouse){
		if(posisiMouse==Kanan) posisiMouse=KananBawah;
		else if (posisiMouse==KananBawah) posisiMouse=Bawah;
		else if (posisiMouse==Bawah) posisiMouse=KiriBawah;
		else if (posisiMouse==KiriBawah) posisiMouse=Kiri;
		else if (posisiMouse==Kiri) posisiMouse=KiriAtas;
		else if (posisiMouse==KiriAtas) posisiMouse=Atas;
		else if (posisiMouse==Atas) posisiMouse=KananAtas;
		else if (posisiMouse==KananAtas) posisiMouse=Kanan;
	}
	
	// given action determine next state
	public int[] getNextState(int posisiMouse) {
		
		// INI TINGGAL PILIH MAU ROTATE KEMANA
		
		// action is mouse action:  0=u 1=ur 2=r 3=dr ... 7=ul
		Dimension d = getCoords(posisiMouse); 
		int ax=d.width, ay=d.height;
		if (legal(ax,ay)) {
			// move agent
			mx = ax; my = ay;
		} else {
			//System.err.println("Illegal action: "+action);
		}
		// update world

		waitingReward = calcReward();
		
		return getState();
	}
	
	public double getReward(int i) { return getReward(); }
	public double getReward() {	return waitingReward; }
	
	public boolean validAction(int action) {
		Dimension d = getCoords(action);
		return legal(d.width, d.height);
	}
	
	Dimension getCoords(int posisiMouse) {
		int ax=mx, ay=my;
		switch(posisiMouse) {
			case Kanan: ay = mx + 1; break;
			case KananAtas: ay = my + 1; ax = mx + 1; break;
			case Atas: ax = my + 1; break;
			case KiriAtas: ay = my + 1; ax = mx - 1; break;
			case Kiri: ay = mx - 1; break;
			case KiriBawah: ay = my - 1; ax = mx - 1; break;
			case Bawah: ax = my - 1; break;
			case KananBawah: ay = my - 1; ax = mx + 1; break;
			default: //System.err.println("Invalid action: "+action);
		}
		return new Dimension(ax, ay);
	}

	// find action value given x,y=0,+-1
	int getAction(int x, int y) {
		int[][] vals={{7,0,1},
		              {6,0,2},
					  {5,4,3}};
		if ((x<-1) || (x>1) || (y<-1) || (y>1) || ((y==0)&&(x==0))) return -1;
		int retVal = vals[y+1][x+1];
		return retVal;
	}

	public boolean endState() { return endGame(); }
	public int[] resetState() { 
		catscore = 0;
		mousescore = 0;
		setRandomPos(); 
		return getState();
	}
		
	public double getInitValues() { return INIT_VALS; }
	/******* end RLWorld functions **********/
	
	public int[] getState() {
		// translates current state into int array
		stateArray = new int[NUM_OBJECTS];
		stateArray[0] = mx;
		stateArray[1] = my;
		//stateArray[2] = cx;
		//stateArray[3] = cy;
		//stateArray[4] = chx;
		//stateArray[5] = chy;
		return stateArray;
	}

	public double calcReward() {
		double newReward = 0;
		if (isMouseOnCheese()) {
			mousescore++;
			newReward += cheeseReward;
		}
		if (isMouseOnCat()) {
			catscore++;
			newReward -= deathPenalty;
		}
		//if ((mx==hx)&&(my==hy)&&(gotCheese)) newReward += 100;
		return newReward;		
	}
	
	public boolean isMouseOnCat(){
		for (int i = 0; i < numCats; i++){
			if ((cx[i] == mx) && (cy[i] == my)) 
				return true;
		}
		return false;
	}
	
	public boolean isMouseOnCheese(){
		for (int i = 0; i < numCheeses; i++){
			if ((chx[i] == mx) && (chy[i] == my)) 
				return true;
		}
		return false;
	}
	
	public void setRandomPos() {
		
		makeCheeses(bx, by, numCheeses);
		makeCats(bx,by,numCats);
		
		Dimension d = getRandomPos();
		mx = d.width;
		my = d.height;	
		
		d = getRandomPos();
		hx = d.width;
		hy = d.height;
	}

	boolean legal(int x, int y) {
		return ((x>=0) && (x<bx) && (y>=0) && (y<by)) && (!walls[x][y]);
	}

	boolean endGame() {
		//return (((mx==hx)&&(my==hy)&& gotCheese) || ((cx==mx) && (cy==my)));
		
		return isMouseOnCat();
	}

	Dimension getRandomPos() {
		int nx, ny;
		nx = (int)(Math.random() * bx);
		ny = (int)(Math.random() * by);
		for(int trials=0; (!legal(nx,ny)) && (trials < WALL_TRIALS); trials++){
			nx = (int)(Math.random() * bx);
			ny = (int)(Math.random() * by);
		}
		return new Dimension(nx, ny);
	}
	
	/******** heuristic functions ***********/
	Dimension getNewPos(int x, int y, int tx, int ty) {
		int ax=x, ay=y;
		
		if (tx==x) ax = x;
 		else ax += (tx - x)/Math.abs(tx-x); // +/- 1 or 0
		if (ty==y) ay = y;
 		else ay += (ty - y)/Math.abs(ty-y); // +/- 1 or 0
		
		// check if move legal	
		if (legal(ax, ay)) return new Dimension(ax, ay);
		
		// not legal, make random move
		while(true) {
			// will definitely exit if 0,0
			ax=x; ay=y;
			ax += 1-(int) (Math.random()*3);
			ay += 1-(int) (Math.random()*3);
			
			//System.out.println("old:"+x+","+y+" try:"+ax+","+ay);
			if (legal(ax,ay)) return new Dimension(ax,ay);
		}
	}
/*
	void moveCat() {
		Dimension newPos = getNewPos(cx, cy, mx, my);
		cx = newPos.width;
		cy = newPos.height;					
	}
*/

	void moveMouse() {
		Dimension newPos = getNewPos(mx, my, chx[0], chy[0]);
		mx = newPos.width;
		my = newPos.height;
	}
	
	int mouseAction() {
		Dimension newPos = getNewPos(mx, my, chx[0], chy[0]);
		return getAction(newPos.width-mx,newPos.height-my);
	}
	/******** end heuristic functions ***********/


	/******** wall generating functions **********/
	void makeWalls(int xdim, int ydim, int numWalls) {
		walls = new boolean[xdim][ydim];
		
		// loop until a valid wall set is found
		for(int t=0; t<WALL_TRIALS; t++) {
			// clear walls
			for (int i=0; i<walls.length; i++) {
				for (int j=0; j<walls[0].length; j++) walls[i][j] = false;
			}
			
			float xmid = xdim/(float)2;
			float ymid = ydim/(float)2;
			
			// randomly assign walls.  
			for (int i=0; i<numWalls; i++) {
				Dimension d = getRandomPos();
				
				// encourage walls to be in center
				double dx2 = Math.pow(xmid - d.width,2);
				double dy2 = Math.pow(ymid - d.height,2);
				double dropperc = Math.sqrt((dx2+dy2) / (xmid*xmid + ymid*ymid));
				if (Math.random() < dropperc) {
					// reject this wall
					i--;
					continue;
				}
				
				
				walls[d.width][d.height] = true;
			}
			
			// check no trapped points
			if (validWallSet(walls)) break;
			
		}
		
	}
	
	boolean validWallSet(boolean[][] w) {
		// copy array
		boolean[][] c;
		c = new boolean[w.length][w[0].length];
		
		for (int i=0; i<w.length; i++) {
			for (int j=0; j<w[0].length; j++) c[i][j] = w[i][j];
		}
		
		// fill all 8-connected neighbours of the first empty
		// square.
		boolean found = false;
		search: for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) {
				if (!c[i][j]) {
					// found empty square, fill neighbours
					fillNeighbours(c, i, j);
					found = true;
					break search;
				}
			}
		}
		
		if (!found) return false;
		
		// check if any empty squares remain
		for (int i=0; i<c.length; i++) {
			for (int j=0; j<c[0].length; j++) if (!c[i][j]) return false;
		}
		return true;
	}
	
	void fillNeighbours(boolean[][] c, int x, int y) {
		c[x][y] = true;
		for (int i=x-1; i<=x+1; i++) {
			for (int j=y-1; j<=y+1; j++)
				if ((i>=0) && (i<c.length) && (j>=0) && (j<c[0].length) && (!c[i][j])) 
					fillNeighbours(c,i,j);
		}
	}
	/******** wall generating functions **********/

	/******** cheese generating functions **********/
	public ArrayList<Point> getPosAt(int begin, int length){
		ArrayList<Point> temp = new ArrayList<Point>();
		int it= begin;
		while(temp.size()<length){
			temp.add(set_pos.get(it));
			it+= 1 %set_pos.size();
		}
		return temp;
	}
	
	void makeCheeses(int xdim, int ydim, int numCheeses) {
		cheeses = new boolean[xdim][ydim];
		
		//Menghitung posisi memulai ambil dari setpos dan mengambil Point2 dari file
		int begin = (cntpos*(numCats+numCheeses))% set_pos.size();
		ArrayList<Point> sementara = getPosAt(begin, numCheeses);
		
		// clear cheeses
		for (int i=0; i<cheeses.length; i++) {
			for (int j=0; j<cheeses[0].length; j++) cheeses[i][j] = false;
		}
		
		// Assign cheeses from File.  
		for(int i=0; i<sementara.size();i++){
			cheeses[sementara.get(i).getX()][sementara.get(i).getY()] = true;
		}
		cntpos+=1;
	}
	
	/******** cat generating functions **********/
	void makeCats(int xdim, int ydim, int numCats) {
		cats = new boolean[xdim][ydim];
		
		// loop until a valid cat set is found
		for(int t=0; t<CAT_TRIALS; t++) {
			// clear cats
			for (int i=0; i<cats.length; i++) {
				for (int j=0; j<cats[0].length; j++) cats[i][j] = false;
			}
			
			float xmid = xdim/(float)2;
			float ymid = ydim/(float)2;
			
			// randomly assign cats.  
			for (int i=0; i<numCats; i++) {
				Dimension d = getRandomPos();
				
				// encourage cats to be in center
				double dx2 = Math.pow(xmid - d.width,2);
				double dy2 = Math.pow(ymid - d.height,2);
				double dropperc = Math.sqrt((dx2+dy2) / (xmid*xmid + ymid*ymid));
				if (Math.random() < dropperc || walls[d.width][d.height] || cheeses[d.width][d.height]) {
					// reject this cat
					i--;
					continue;
				}
				
				
				cats[d.width][d.height] = true;
				cx[i] = d.width;
				cy[i] = d.height;
			}
			
		}
		
	}
	/******** cat generating functions **********/
	

	
}
