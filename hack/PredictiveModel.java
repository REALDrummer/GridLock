package hack;


public class PredictiveModel {
    private static final int SIZE = GridLock.GRID_WIDTH + GridLock.GRID_HEIGHT;
    private static final float congestionMatrix[] = new float[SIZE];
    private static final float adjacencyMatrix[][] = new float[GridLock.GRID_WIDTH][GridLock.GRID_HEIGHT];

    private float[] test1 = {1,2};
    private float[][] test2 = {{3,4},{5,6}};
    PredictiveModel(){
        /*
            Need to Initialize the Predictive Model Later
         */
        float[] D = mult(test1,test2);
        for (float i : D)
            System.out.println(i);
    }

    float[] mult(float[] X, float[][] Y){
        float[] result = new float[X.length];
        for (int i = 0; i < X.length; i++){
            for (int j = 0; j < Y.length; j++){
                result[i] += X[j] * Y[i][j];
            }
        }
        return result;
    }

}
