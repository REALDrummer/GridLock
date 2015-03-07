package hack;
import java.util.LinkedList;

public class Adaptive{
	public final byte ncars;
	public final byte scars;
	public final byte ecars;
	public final byte wcars;
	
	public static final int STRAIGHT_DELAY = 10000;
	public static final int LEFT_DELAY = 6000;
	
	public Adaptive(){
		for (int i = 0; i < GridLock.GRID_WIDTH; i++){
			for(int j = 0; j < GridLock.GRID_HEIGHT; j++){
				if(getNScars(Intersection.INTERSECTIONS[i][j]) > getEWcars(Intersection.INTERSECTIONS[i][j])){
					//do something to traffic lights at this intersection
				}
			}
		}
	}
	
	public byte getNScars(Intersection this_intersection){
		ncars = this_intersection.north_road.CARS.size();
		scars = this_intersection.south_road.CARS.size();
		return ncars + scars;
	}
	
	public byte getEWcars(Intersection this_intersection){
		ecars = this_intersection.east_road.CARS.size();
		wcars = this_intersection.west_road.CARS.size();
		return ecars + wcars;
	}
}