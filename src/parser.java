import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
 
public class parser {

	public static void main(String[] args) {
		Mouse mou= new Mouse();
		World wor = new World();
		try (BufferedReader br = new BufferedReader(new FileReader("testing.txt")))
		{
 
			String sCurrentLine;
			int c,k;
			int it =0;
			int temp_x = 0, temp_y = 0;
			String temp = "";
			for(int i = 0; i< 4; i++){
				if(i==0){
					sCurrentLine = br.readLine();
					mou.setLimit(Integer.parseInt(sCurrentLine));
				}
				else if(i==1){
					sCurrentLine = br.readLine();
					wor.setCheese(Integer.parseInt(""+sCurrentLine.charAt(0)));
					wor.setCat(Integer.parseInt(""+sCurrentLine.charAt(2)));
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
							//System.out.println("X masuk: "+temp_x);
							it+=1;
						}
						else if(sCurrentLine.charAt(it)==' '){ 
							it+=1;
						}
						else if(sCurrentLine.charAt(it)==')'){ 
							temp_y = Integer.parseInt(temp);
							//System.out.println("Y masuk: "+temp_y);
							wor.addCheese(temp_x-1,temp_y-1);
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
			
		mou.printMouseDetail();
		wor.printWorldDetail();
		} catch (IOException e) {
			e.printStackTrace();
		} 
 
	}
}

class Mouse {
	private int limit;
	
	public Mouse(){
		limit = 0;
	}
	public void setLimit(int i){
		limit = i;
	}
	public void printMouseDetail(){
		System.out.println("Limit: "+limit);
	}
}

class World {
	private int cheese;
	private int cat;
	private int[][] pos_train;
	private int[][] pos_play;
	
	public World(){
		cheese =0;
		cat=0;
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
	}
	public void setCheese(int c){
		cheese = c;
	}
	public void setCat(int k){
		cat = k;
	}
	public void addCheese(int x, int y){
		pos_train[x][y] = 1;
	}
	public void printWorldDetail(){
		System.out.println("Cheese :"+ cheese);
		System.out.println("Cat :"+ cat);
		System.out.println("Peta Latihan :");
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				System.out.print(pos_train[i][j]);
			}
			System.out.println(" ");
		}
		System.out.println("Peta Permainan :");
		for(int i=0; i< 10;i++){
			for(int j=0; j< 10;j++){
				System.out.print(pos_play[i][j]);
			}
			System.out.println(" ");
		}
	}
}