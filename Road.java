import java.util.LinkedList;

public class Road {
	public final Intersection Int1;
	public final Intersection Int2;
	
	public final byte Int1_LL;
	public final byte Int1_SL;
	public final byte Int1_RL;
	
	public final byte Int2_LL;
	public final byte Int2_SL;
	public final byte Int2_RL;
	
	public final LinkedList<Car> cars;
	
	public enum Lane{
		LEFT_LANE, STRAIGHT_LANE, RIGHT_LANE
	}
	
	public Road(Intersection Int1, Intersection Int2, 
				byte Int1_LL, byte Int1_SL, byte Int1_RL,
				byte Int2_LL, byte Int2_SL, byte Int2_RL){
		
		this.Int1 = Int1;
		this.Int2 = Int2;
		
		this.Int1_LL = Int1_LL;
		this.Int1_SL = Int1_SL;
		this.Int1_RL = Int1_RL;
		
		this.Int2_LL = Int2_LL;
		this.Int2_SL = Int2_SL;
		this.Int2_RL = Int2_RL;
		
		cars = new LinkedList<Car>();
	}
	
	public void addCar(Car car){
		cars.add(car);
	}
	
	public void removeCar(Car car){
		cars.remove(car);
	}
	
	public Lane getLaneType(byte lane, Intersection intersec){
		
	}
	
	public Lane isRHLane(byte lane, RoadEnd roadend){
		
	}
}
