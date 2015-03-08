package hack;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AlgoRunner implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        GridLock.algorithm.run();

        int cars_to_spawn = (int) (GridLock.CARS_SPAWNED_PER_SECOND + (Math.random() * GridLock.CARS_PER_SECOND_VARIANCE * 2 - GridLock.CARS_PER_SECOND_VARIANCE));
        for (int i = 0; i < cars_to_spawn; i++)
            Car.spawn();

        for (int i = 0; i < Car.CARS.size(); i++)
            Car.CARS.get(i).tick();
    }
}
