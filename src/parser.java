import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
 
public class parser {
	private int cheese;
	private int cat;
	private int mouse_limit;
	private int[][] pos_train;
	private int[][] pos_play;
	private int[][] train_map;
	
	public parser(){
		cheese = 0;
		cat = 0;
		mouse_limit = 0;
		pos_train = new int[10][10];
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				pos_train[i][j]=0;
			}
		}
		pos_play = new int[10][10];
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				pos_play[i][j]=0;
			}
		}
		train_map = new int[10][10];
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				pos_play[i][j]=0;
			}
		}
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
								pos_train[temp_x-1][temp_y-1] = 1;
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
								pos_play[temp_x-1][temp_y-1] = 1;
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
				int it =0;
				int temp_x = 0, temp_y = 0;
				String temp = "";
				for(int i = 0; i< 10; i++){
					sCurrentLine = br.readLine();
					String[] numpang = sCurrentLine.split(" ", 10);
					for(int j=0; j<10; j++){
						train_map[i][j] = Integer.parseInt(numpang[j]);
					}
				}
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getCheese(){
		return cheese;
	}
	
	public int getCat(){
		return cat;
	}
	
	public int getLimit(){
		return mouse_limit;
	}
	
	public int[][] getCheesePlay(){
		return pos_play;
	}
	
	public int[][] getCheeseTrain(){
		return pos_train;
	}
	
	public int[][] getTrainMap(){
		return train_map;
	}
	
	public int getPlayCheeseAt(int x, int y){
		return pos_play[x][y];
	}
	
	public int getTrainCheeseAt(int x, int y){
		return pos_train[x][y];
	}
	
	public int getTrainMapAt(int x, int y){
		return train_map[x][y];
	}
	
	public void PrintDetailParser(){
		System.out.println("Cheese :"+ cheese);
		System.out.println("Cat :"+ cat);
		System.out.println("Cheese Pelatihan :");
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				System.out.print(pos_train[i][j]);
			}
			System.out.println(" ");
		}
		System.out.println("Cheese Permainan :");
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				System.out.print(pos_play[i][j]);
			}
			System.out.println(" ");
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