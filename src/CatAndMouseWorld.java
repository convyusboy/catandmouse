import java.awt.Dimension;
import java.util.ArrayList;

public class CatAndMouseWorld implements RLWorld {
	public int bx, by;
	public int numCats, numCheeses, numWalls;
	public int posisiMouse;
	public int cntpos;
	public int mouseLimit;
	public int type; //WOrld untuk play atau training

	// Information from Parser
	public ArrayList<Point> set_pos;

	public int mx, my;
	public int cx[], cy[];
	public int chx[], chy[];
	public int hx, hy;
	public boolean gotCheese = false;

	public int catscore = 0, mousescore = 0;
	public int cheeseReward, deathPenalty;

	static final int NUM_OBJECTS = 2, NUM_ACTIONS = 8, WALL_TRIALS = 100,
			CAT_TRIALS = 100, CHEESE_TRIALS = 100;
	static final double INIT_VALS = 0;
	static final int Kanan = 0, KananAtas = 1, Atas = 2, KiriAtas = 3,
			Kiri = 4, KiriBawah = 5, Bawah = 6, KananBawah = 7;

	public int mouse_limit;

	int[] stateArray;
	double waitingReward;
	public boolean[][] walls;
	public boolean[][] cats;
	public boolean[][] cheeses;

	public int getPosisiMouse() {
		return posisiMouse;
	}

	public int getCheeseReward() {
		cheeseReward = (bx + by);
		return cheeseReward;
	}

	public int getDeathReward() {
		deathPenalty = (bx + by);
		return deathPenalty;
	}

	public CatAndMouseWorld(String namafile1, String namafile2) {
		type = 1;
		parser parserai = new parser();
		parserai.readFromFile(namafile1, 1);
		parserai.readFromFile(namafile2, 2);

		bx = parserai.getBx();
		by = parserai.getBy();
		mouseLimit = parserai.getLimit();

		walls = parserai.getTrainMap();
		numWalls = parserai.getNumWalls();
		numCats = parserai.getCat();
		numCheeses = parserai.getCheese();

		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];

		set_pos = parserai.getCheeseTrain();
		cntpos = 0;

		System.out.println("constructor train world");
		resetState();
	}

	public CatAndMouseWorld(int x, int y, int numwalls, int numcats,
			int numcheeses, String namafile) {
		type = 2;
		parser parserai = new parser();
		parserai.readFromFile(namafile, 1);
//		parserai.PrintDetailParser();

		bx = x;
		by = y;
		mouseLimit = parserai.getLimit();

		numWalls = numwalls;
		numCats = numcats;
		numCheeses = numcheeses;

		cx = new int[numCats];
		cy = new int[numCats];
		chx = new int[numCheeses];
		chy = new int[numCheeses];

		set_pos = parserai.getCheesePlay();
		cntpos = 0;
		
		System.out.println("constructor play world");
		resetState();
	}

	/******* RLWorld interface functions ***********/
	public int[] getDimension() {
		int[] retDim = new int[NUM_OBJECTS + 1];
		int i;
		for (i = 0; i < NUM_OBJECTS;) {
			retDim[i++] = bx;
			retDim[i++] = by;
		}
		retDim[i] = NUM_ACTIONS;

		return retDim;
	}

	public void rotateKiri() {
		System.out.println("rotate kiri!");
		if (posisiMouse == Kanan)
			posisiMouse = KananAtas;
		else if (posisiMouse == KananAtas)
			posisiMouse = Atas;
		else if (posisiMouse == Atas)
			posisiMouse = KiriAtas;
		else if (posisiMouse == KiriAtas)
			posisiMouse = Kiri;
		else if (posisiMouse == Kiri)
			posisiMouse = KiriBawah;
		else if (posisiMouse == KiriBawah)
			posisiMouse = Bawah;
		else if (posisiMouse == Bawah)
			posisiMouse = KananBawah;
		else if (posisiMouse == KananBawah)
			posisiMouse = Kanan;
	}

	public void rotateKanan() {
		System.out.println("rotate kanan!");
		if (posisiMouse == Kanan)
			posisiMouse = KananBawah;
		else if (posisiMouse == KananBawah)
			posisiMouse = Bawah;
		else if (posisiMouse == Bawah)
			posisiMouse = KiriBawah;
		else if (posisiMouse == KiriBawah)
			posisiMouse = Kiri;
		else if (posisiMouse == Kiri)
			posisiMouse = KiriAtas;
		else if (posisiMouse == KiriAtas)
			posisiMouse = Atas;
		else if (posisiMouse == Atas)
			posisiMouse = KananAtas;
		else if (posisiMouse == KananAtas)
			posisiMouse = Kanan;
	}

	// given action determine next state
	public int[] getNextState(int action) {
		System.out.println("getnextstate, action = " + action);
		// action is mouse action: 0=forward, 1=rotateKanan, 2=rotateKiri
		switch (action) {
		case 0:
			Dimension d = getCoords(action);
			int ax = d.width,
			ay = d.height;
			if (legal(ax, ay)) {
				// move agent
				mx = ax;
				my = ay;
			} else {
				// System.err.println("Illegal action: "+action);
			}
			break;
		case 1:
			rotateKanan();
			break;
		case 2:
			rotateKiri();
			break;
		}
		// update world

		waitingReward = calcReward();
		System.out.println(mousescore);
		return getState();
	}

	public double getReward(int i) {
		return getReward();
	}

	public double getReward() {
		return waitingReward;
	}

	public boolean validAction(int action) {
		Dimension d = getCoords(action);
		return legal(d.width, d.height);
	}

	Dimension getCoords(int action) {
		int ax = mx, ay = my;
		switch (posisiMouse) {
		case Kanan:
			ax = mx + 1;
			break;
		case KananAtas:
			ay = my - 1;
			ax = mx + 1;
			break;
		case Atas:
			ay = my - 1;
			break;
		case KiriAtas:
			ay = my - 1;
			ax = mx - 1;
			break;
		case Kiri:
			ax = mx - 1;
			break;
		case KiriBawah:
			ay = my + 1;
			ax = mx - 1;
			break;
		case Bawah:
			ay = my + 1;
			break;
		case KananBawah:
			ay = my + 1;
			ax = mx + 1;
			break;
		default: // System.err.println("Invalid action: "+action);
		}
		return new Dimension(ax, ay);
	}

	// find action value given x,y=0,+-1
	int getAction(int x, int y) {
		/*
		 * int[][] vals={{7,0,1}, {6,0,2}, {5,4,3}}; if ((x<-1) || (x>1) ||
		 * (y<-1) || (y>1) || ((y==0)&&(x==0))) return -1; int retVal =
		 * vals[y+1][x+1]; return retVal;
		 */
		
		
		
		return 1;
	}

	public boolean endState() {
		return endGame();
	}

	public int[] resetState() {
		catscore = 0;
		mousescore = 0;
		setRandomPos();
		return getState();
	}

	public double getInitValues() {
		return INIT_VALS;
	}

	/******* end RLWorld functions **********/
	
	/*public boolean isThereWall(int x, int y){
		for (int i=0; i<numWalls; i++){
			return walls[x][y];
		}
		return false;
	}
	
	public boolean isThereCat(int x, int y){
		for (int i=0; i<numCats; i++){
			if(cx[i]==x && cy[i]==y){
				return true;
			}
		}
		return false;
	}
	
	public boolean isThereMouse(int x, int y){
		for (int i=0; i<numCheeses; i++){
			if(chx[i]==x && chy[i]==y){
				return true;
			}
		}
		return false;
	}*/

	public int[] getState() {
		// translates current state into int array
		stateArray = new int[NUM_OBJECTS];
		boolean notsee = true;
		int kolom = 0, baris = 0;
		
		switch(posisiMouse){
			case 0:
				kolom = 1;
				baris = 0;
				break;
			case 1:
				kolom = 1;
				baris = 1;
				break;
			case 2:
				kolom = 0;
				baris = 1;
				break;
			case 3:
				kolom = -1;
				baris = 1;
				break;
			case 4:
				kolom = -1;
				baris = 0;
				break;
			case 5:
				kolom = -1;
				baris = -1;
				break;
			case 6:
				kolom = 0;
				baris = -1;
				break;
			case 7:
				kolom = 1;
				baris = -1;
				break;
		}

		// jarak berapa
		//stateArray[0] = .... ;
		// objeknya apa -> 1.wall, 2.cat, 3.cheese 4.kosong
		//stateArray[1] = .... ;
		
		for(int i=1; notsee && i<=mouseLimit; i++){
			if(mx+(kolom*i) == bx || mx+(kolom*i) == -1 || my+(baris*i) == by || my+(baris*i) == -1){
				stateArray[0]=i;
				stateArray[1]=1;
				notsee = false;
			} else {
				if(walls[mx+(kolom*i)][my+(baris*i)]){
					stateArray[0]=i;
					stateArray[1]=1;
					notsee = false;
				} else if(cats[mx+(kolom*i)][my+(baris*i)]){
					stateArray[0]=i;
					stateArray[1]=2;
					notsee = false;
				} else if(cheeses[mx+(kolom*i)][my+(baris*i)]){
					stateArray[0]=i;
					stateArray[1]=3;
					notsee = false;
				}
			}
		}
		stateArray[0]=mouseLimit;
		stateArray[1]=4;
		
		return stateArray;
	}

	public double calcReward() {
		double newReward = 0;
		if (isMouseOnCheese()) {
			newReward += getCheeseReward();
			cheeses[mx][my] = false;
		} else if (isMouseOnCat()) {
			newReward -= getDeathReward();
		} else
			mousescore = -1;

		// if ((mx==hx)&&(my==hy)&&(gotCheese)) newReward += 100;

		return newReward;
	}

	public boolean isMouseOnCat() {
		return cats[mx][my];
	}

	public boolean isMouseOnCheese() {
		return cheeses[mx][my];
	}

	public boolean isMouseHitWall() {
		return walls[mx][my];
	}

	public void setRandomPos() {
		if (type == 2)
			makeWalls(bx, by, numWalls);
		makeMouse();
		makeCheeses(bx, by, numCheeses);
		makeCats(bx, by, numCats);
	}

	private void makeMouse() {
		int tx = set_pos.get(cntpos).getX();
		int ty = set_pos.get(cntpos).getY();
		cntpos = (cntpos + 1) % set_pos.size();
		while (walls[tx][ty]) {
			tx = set_pos.get(cntpos).getX();
			ty = set_pos.get(cntpos).getY();
			cntpos = (cntpos + 1) % set_pos.size();
		}
		mx = tx;
		my = ty;
	}

	boolean legal(int x, int y) {
		return ((x >= 0) && (x < bx) && (y >= 0) && (y < by)) && (!walls[x][y]);
	}

	boolean endGame() {
		// return (((mx==hx)&&(my==hy)&& gotCheese) || ((cx==mx) && (cy==my)));
		int n_cheese = 0;
		for (int i = 0; i < numCheeses; i++){
			if (cheeses[chx[i]][chy[i]]) n_cheese++;
		}
		return (isMouseOnCat() || (n_cheese == 0));
	}

	Dimension getRandomPos() {
		int nx, ny;
		nx = (int) (Math.random() * bx);
		ny = (int) (Math.random() * by);
		for (int trials = 0; (!legal(nx, ny)) && (trials < WALL_TRIALS); trials++) {
			nx = (int) (Math.random() * bx);
			ny = (int) (Math.random() * by);
		}
		return new Dimension(nx, ny);
	}

	/******** heuristic functions ***********/
	Dimension getNewPos(int x, int y, int tx, int ty) {
		int ax = x, ay = y;

		if (tx == x)
			ax = x;
		else
			ax += (tx - x) / Math.abs(tx - x); // +/- 1 or 0
		if (ty == y)
			ay = y;
		else
			ay += (ty - y) / Math.abs(ty - y); // +/- 1 or 0

		// check if move legal
		if (legal(ax, ay))
			return new Dimension(ax, ay);

		// not legal, make random move
		while (true) {
			// will definitely exit if 0,0
			ax = x;
			ay = y;
			ax += 1 - (int) (Math.random() * 3);
			ay += 1 - (int) (Math.random() * 3);

			// System.out.println("old:"+x+","+y+" try:"+ax+","+ay);
			if (legal(ax, ay))
				return new Dimension(ax, ay);
		}
	}

	void moveMouse() {
		Dimension newPos = getNewPos(mx, my, chx[0], chy[0]);
		mx = newPos.width;
		my = newPos.height;

	}

	int mouseAction() {
		Dimension newPos = getNewPos(mx, my, chx[0], chy[0]);
		return getAction(newPos.width - mx, newPos.height - my);
	}

	/******** end heuristic functions ***********/

	/******** wall generating functions **********/
	void makeWalls(int xdim, int ydim, int numWalls) {
		walls = new boolean[xdim+1][ydim+1];

		// loop until a valid wall set is found
		for (int t = 0; t < WALL_TRIALS; t++) {
			// clear walls
			for (int i = 0; i < walls.length; i++) {
				for (int j = 0; j < walls[0].length; j++)
					walls[i][j] = false;
			}

			// randomly assign walls.
			for (int i = 0; i < numWalls; i++) {
				Dimension d = getRandomPos();
				walls[d.width][d.height] = true;
			}

			// check no trapped points
			if (validWallSet(walls))
				break;

		}

	}

	boolean validWallSet(boolean[][] w) {
		// copy array
		boolean[][] c;
		c = new boolean[w.length][w[0].length];

		for (int i = 0; i < w.length; i++) {
			for (int j = 0; j < w[0].length; j++)
				c[i][j] = w[i][j];
		}

		// fill all 8-connected neighbours of the first empty
		// square.
		boolean found = false;
		search: for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++) {
				if (!c[i][j]) {
					// found empty square, fill neighbours
					fillNeighbours(c, i, j);
					found = true;
					break search;
				}
			}
		}

		if (!found)
			return false;

		// check if any empty squares remain
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[0].length; j++)
				if (!c[i][j])
					return false;
		}
		return true;
	}

	void fillNeighbours(boolean[][] c, int x, int y) {
		c[x][y] = true;
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++)
				if ((i >= 0) && (i < c.length) && (j >= 0) && (j < c[0].length)
						&& (!c[i][j]))
					fillNeighbours(c, i, j);
		}
	}

	/******** wall generating functions **********/

	/******** cheese generating functions **********/
	void makeCheeses(int xdim, int ydim, int numCheeses) {
		cheeses = new boolean[xdim][ydim];
		// clear cheeses
		for (int i = 0; i < cheeses.length; i++) {
			for (int j = 0; j < cheeses[0].length; j++)
				cheeses[i][j] = false;
		}

		// Assign cheeses from File
		for (int i = 0; i < numCheeses; i++) {
			int tx = set_pos.get(cntpos).getX();
			int ty = set_pos.get(cntpos).getY();
			cntpos = (cntpos + 1) % set_pos.size();
			System.out.println(tx + " " + ty);
//			while (walls[tx][ty]) {
//				tx = set_pos.get(cntpos).getX();
//				ty = set_pos.get(cntpos).getY();
//				cntpos = (cntpos + 1) % set_pos.size();
//			}

			cheeses[tx][ty] = true;
			chx[i] = tx;
			chy[i] = ty;
		}

	}

	/******** cat generating functions **********/
	void makeCats(int xdim, int ydim, int numCats) {
		cats = new boolean[xdim][ydim];
		// clear cats
		for (int i = 0; i < cats.length; i++) {
			for (int j = 0; j < cats[0].length; j++)
				cats[i][j] = false;
		}

		// Assign cats from File
		for (int i = 0; i < numCats; i++) {
			int tx = set_pos.get(cntpos).getX();
			int ty = set_pos.get(cntpos).getY();
			cntpos = (cntpos + 1) % set_pos.size();
			while (walls[tx][ty] || cheeses[tx][ty]) {
				tx = set_pos.get(cntpos).getX();
				ty = set_pos.get(cntpos).getY();
				cntpos = (cntpos + 1) % set_pos.size();
			}

			cats[tx][ty] = true;
			cx[i] = tx;
			cy[i] = ty;
		}

	}
	/******** cat generating functions **********/

}
