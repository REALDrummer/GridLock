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

	public final byte Int1_lanes;
	public final byte Int2_lanes;

	public final LinkedList<Car> cars;

	public enum Lane {
		LEFT_LANE, STRAIGHT_LANE, RIGHT_LANE
	}

	public Road(Intersection Int1, Intersection Int2, byte Int1_LL,
			byte Int1_SL, byte Int1_RL, byte Int2_LL, byte Int2_SL, byte Int2_RL) {

		this.Int1 = Int1;
		this.Int2 = Int2;

		this.Int1_LL = Int1_LL;
		this.Int1_SL = Int1_SL;
		this.Int1_RL = Int1_RL;
		Int1_lanes = Int1_LL + Int1_SL + Int1_RL;

		this.Int2_LL = Int2_LL;
		this.Int2_SL = Int2_SL;
		this.Int2_RL = Int2_RL;
		Int2_lanes = Int2_LL + Int2_SL + Int2_RL;

		cars = new LinkedList<Car>();
	}

	public void addCar(Car car) {
		cars.add(car);
	}

	public void removeCar(Car car) {
		cars.remove(car);
	}

	public Lane getLaneType(byte lane, boolean NW){
		switch(NW)
		case NW:
			if(isRHLane(lane, NW))
				return RIGHT_LANE;
			if(isLHLane(lane, NW))
				return LEFT_LANE;
			if(isSLane(lane, NW))
				return STRAIGHT_LANE;
		case !NW:
			if(isRHLane(lane, NW))
				return RIGHT_LANE;
			if(isLHLane(lane, NW))
				return LEFT_LANE;
			if(isSLane(lane, NW))
				return STRAIGHT_LANE;
	}

	public boolean isRHLane(byte lane, boolean NW){
		if(NW){
			if(Int1_RL == 1 && lane == Int1_lanes){
				return 1;
			}else if(Int1_RL == 2 && lane == Int1_lanes || Int1_lanes - 1){
				return 1;
			}else{
				return 0;
			}
		}else if(!NW){
			if(Int2_RL == 1 && lane == 0){
				return 1;
			}else if(Int2_RL == 2 && lane == 0 || lane == 1){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

	public boolean isLHLane(byte lane, boolean NW) {
		if(NW){
			if(Int1_LL == 2 && lane == Int1_SL || lane == (Int1_SL + 1)){
				return 1;
			}else if(Int1_LL == 1 && lane == Int1_SL){
				return 1;
			}else{
				return 0;
			}
		}else if(!NW){
			if(Int1_LL == 2 && lane == Int1_SL || lane == (Int1_SL + 1)){
				return 1;
			}else if(Int1_LL == 1 && lane == Int1_SL){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}

	public boolean isSLane(byte lane, boolean NW) {
		if(NW){
			if(lane < Int1_SL || lane == (Int1_SL + Int1_LL) && lane < (2 * Int1_SL + Int1_LL)){
				return 1;
			}else{
				return 0;
			}
		}else if(!NW){
			if(lane < Int1_SL || lane == (Int1_SL + Int1_LL) && lane < (2 * Int1_SL + Int1_LL)){
				return 1;
			}else{
				return 0;
			}
		}else{
			return 0;
		}
	}
}
