import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
 
public class parser {
	private int cheese;
	private int cat;
	private int mouse_limit;
	int bx;
	int by;
	private ArrayList<Point> pos_train;
	private ArrayList<Point> pos_play;
	private int[][] train_map;
	
	public parser(){
		cheese = 0;
		cat = 0;
		mouse_limit = 0;
		pos_train = new ArrayList<Point>();
		pos_play = new ArrayList<Point>();
		train_map = new int[10][10];
		bx =0;
		by =0;
	}
	
	public void readFromFile(String namafile, int type){
		if(type==1){
			//Baca file yang jenis pertama
			try (BufferedReader br = new BufferedReader(new FileReader(namafile)))
			{
				//Variable penampung
				String sCurrentLine;
				int it =0;
				int temp_x = 0, temp_y = 0;
				String temp = "";
				for(int i = 0; i< 4; i++){
					if(i==0){
						sCurrentLine = br.readLine();
						mouse_limit = Integer.parseInt(sCurrentLine);
					}
					else if(i==1){
						sCurrentLine = br.readLine();
						cheese = Integer.parseInt(""+sCurrentLine.charAt(0));
						cat = (Integer.parseInt(""+sCurrentLine.charAt(2)));
					}
					else if(i==2){
						sCurrentLine = br.readLine();
						while(it < sCurrentLine.length()){
							if(sCurrentLine.charAt(it)=='('){
								it+=1;
								temp="";
								while(sCurrentLine.charAt(it)!= ','){
									temp+= sCurrentLine.charAt(it);
									it+=1;
								}
							}
							else if(sCurrentLine.charAt(it)==','){
								temp_x = Integer.parseInt(temp);
								it+=1;
							}
							else if(sCurrentLine.charAt(it)==' '){ 
								it+=1;
							}
							else if(sCurrentLine.charAt(it)==')'){ 
								temp_y = Integer.parseInt(temp);
								pos_train.add(new Point(temp_x,temp_y));
								temp_x = 0;
								temp_y = 0;
								it+=1;
							}
							else {
								temp = "";
								while(sCurrentLine.charAt(it)!= ')'){
									temp+= sCurrentLine.charAt(it);
									it+=1;
								}
							}
						}
					}
					else if(i==3){
						sCurrentLine = br.readLine();
						it =0;
						while(it < sCurrentLine.length()){
							if(sCurrentLine.charAt(it)=='('){
								it+=1;
								temp="";
								while(sCurrentLine.charAt(it)!= ','){
									temp+= sCurrentLine.charAt(it);
									it+=1;
								}
							}
							else if(sCurrentLine.charAt(it)==','){
								temp_x = Integer.parseInt(temp);
								it+=1;
							}
							else if(sCurrentLine.charAt(it)==' '){ 
								it+=1;
							}
							else if(sCurrentLine.charAt(it)==')'){ 
								temp_y = Integer.parseInt(temp);
								pos_play.add(new Point(temp_x-1, temp_y-1));
								temp_x = 0;
								temp_y = 0;
								it+=1;
							}
							else {
								temp = "";
								while(sCurrentLine.charAt(it)!= ')'){
									temp+= sCurrentLine.charAt(it);
									it+=1;
								}
							}
						}
					}
				}
				
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else if(type==2){
			//Baca file yang jenis kedua
			try (BufferedReader br = new BufferedReader(new FileReader(namafile)))
			{
				//Variable penampung
				String sCurrentLine;
				int i=0;
				while((sCurrentLine=br.readLine())!=null){
					sCurrentLine = br.readLine();
					String[] numpang = sCurrentLine.split(" ");
					for(int j=0; j<numpang.length; j++){
						train_map[i][j] = Integer.parseInt(numpang[j]);
					}
					i++;
					bx = numpang.length;
				}
				by = i;
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getCheese(){
		return cheese;
	}
	
	public int getBx(){
		return bx;
	}
	
	public int getBy(){
		return by;
	}
	public int getCat(){
		return cat;
	}
	
	public int getLimit(){
		return mouse_limit;
	}
	
	public ArrayList<Point> getCheesePlay(){
		return pos_play;
	}
	
	public ArrayList<Point> getCheeseTrain(){
		return pos_train;
	}
	
	public int[][] getTrainMap(){
		return train_map;
	}
	
	public int getTrainMapAt(int x, int y){
		return train_map[x][y];
	}
	
	public void PrintDetailParser(){
		System.out.println("Cheese :"+ cheese);
		System.out.println("Cat :"+ cat);
		System.out.println("Posisi Pelatihan :");
		for(int i=0; i< pos_train.size();i++){
			System.out.println(pos_train.get(i).getX()+" , "+ pos_train.get(i).getY());
		}
		System.out.println("Posisi Permainan :");
		for(int i=0; i< pos_play.size();i++){
			System.out.println(pos_play.get(i).getX()+" , "+ pos_play.get(i).getY());
		}
		System.out.println("Peta Pelatihan :");
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				System.out.print(train_map[i][j]);
			}
			System.out.println(" ");
		}
	}
}

class Point{
	int x;
	int y;
	
	Point(){
		x=0;
		y=0;
	}
	Point(int _x, int _y){
		x= _x;
		y= _y;
	}
	public void setX(int _x){
		x=_x;
	}
	public void setY(int _y){
		y=_y;
	}
	public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
}