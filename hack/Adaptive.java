package hack;
import java.util.LinkedList;

public class Adaptive{
	public final byte ncars;
	public final byte scars;
	public final byte ecars;
	public final byte wcars;
	public final byte ratio;
	
	public static final int STRAIGHT_DELAY = 10000;
	public static final int LEFT_DELAY = 6000;
	public final int next_delay;
	
	private Timer timer;
	
	public enum TrafficFlow {
        NORTH_SOUTH, EAST_WEST, NORTH_SOUTH_LEFT, EAST_WEST_LEFT;
    }
	
	public int Adaptive(Intersection this_intersection){
		//returns a value to set the traffic signal cycle delay to and
		//changes flow of intersection
		switch(this_intersection.getFlow()){
			case NORTH_SOUTH:
				next_delay = LEFT_DELAY * getNSratio(this_intersection);
				this_intersection.setFlow(TrafficFlow.NORTH_SOUTH_LEFT);
				break;
			case NORTH_SOUTH_LEFT:
				next_delay = STRAIGHT_DELAY * getEWratio(this_intersection);
				this_intersection.setFlow(TrafficFlow.EAST_WEST);
				break;
			case EAST_WEST:
				next_delay = LEFT_DELAY * getEWratio(this_intersection);
				this_intersection.setFlow(TrafficFlow.EAST_WEST_LEFT);
				break;
			case EAST_WEST_LEFT:
				next_delay = STRAIGHT_DELAY * getNSratio(this_intersection);
				this_intersection.setFlow(TrafficFlow.NORTH_SOUTH);
				break;
			case default:
				System.out.println("Adaptive aint adapting");
				break;
		}
		return next_delay;
	}
	
	public initializeRandomIntersections(){
		for (int i = 0; i < GridLock.GRID_WIDTH; i++){
			for(int j = 0; j < GridLock.GRID_HEIGHT; j++){
				//set intersections to random traffic flows
				Intersection.INTERSECTIONS[i][j].setFlow(randomFlow()); 
			}
		}
	}
	
	public byte getNScars(Intersection this_intersection){
		//count total N-S cars through an intersection
		ncars = this_intersection.north_road.CARS.size();
		scars = this_intersection.south_road.CARS.size();
		return ncars + scars;
	}
	
	public byte getEWcars(Intersection this_intersection){
		//count total E-W cars through an intersection
		ecars = this_intersection.east_road.CARS.size();
		wcars = this_intersection.west_road.CARS.size();
		return ecars + wcars;
	}
	
	public byte getNSratio(Intersection this_intersection){
		return getNScars(this_intersection)/getEWcars(this_intersection);
	}
	
	public byte getEWratio(Intersection this_intersection){
		return getEWcars(this_intersection)/getNScars(this_intersection);
	}
	
	public TrafficFlow randomFlow(){
		//pick a random traffic flow
		int rand = Math.random() * 4;
		switch(rand){
			case 0: return TrafficFlow.NORTH_SOUTH;
				break;
			case 1: return TrafficFlow.EAST_WEST;
					break;
			case 2: return TrafficFlow.NORTH_SOUTH_LEFT;
				break;
			case 3: return TrafficFlow.EAST_WEST_LEFT;
				break;
			default:
				System.out.println("Something went wrong in [randomIntersection]");
				break;
		}
	}
}