package pie;

import java.util.Random;

/**
 *
 * @author rooyesh
 *
 * This class contains various function for generating random numbers
 */
public class RandomGenerator {

    // these numbers are required for the pseudo random number generator
    private static double k = 16807;
    private static double m = 2147483647;
    private static double s = 1111;
    private static double r1;

    private static Random r = new Random();
    ;
    private static double temp;

    /**
     * The constructor of RG
     */
    public RandomGenerator() {
        k = 16807;
        m = 2147483647;
        s = 1111;
    }

    /**
     * Generates a uniform random number between [0,1).
     *
     * If Math.random() is used, the function is not deterministic. If the first
     * two lines of this function are used, the random number is deterministic
     * and upon each run of the simulation, we always get the same results
     * (since all of the numbers returned upon call of this function will return
     * the same sequence)
     */
    public static double genUniformRandom() {
//        s = (k * s) % m;
//        return s / m;
        return Math.random();
    }

    /**
     * Generates random number exponentially with rate lambda
     *
     * @param lambda the rate of the exponential distribution
     */
    public static double genExponentialRandom(double lambda) {
        return (-1 / lambda) * Math.log(genUniformRandom());

    }

    
    /**
     * Generates a uniformly distributed random number between min and max
     *
     * @param min the low range
     * @param max the high range
     * @return a uniformly distributed random number between min and max
     */
    public static double genUniformRandomBetween(double min, double max) {
        return min + (max - min) * genUniformRandom();
    }

    /**
     * Generates a uniformly distributed random number between min and max
     *
     * @param min the low range
     * @param max the high range
     * @return a uniformly distributed random number between min and max
     */
    public static int genUniformRandomBetween(int min, int max) {
        return (int) (min + (max - min) * genUniformRandom());
    }
    /**
     * Generates a uniformly distributed random number with the specified mean
     * and variance
     *
     * @param mean the specified mean
     * @param variance the specified variance
     * @return a uniformly distributed random number with the specified mean and
     * variance
     *
     */
    public static double genUniformRandomMeanVariance(double mean, double variance) {
        // a = mean - sqrt(3.var),  b = mean + sqrt(3.var)
        temp = Math.sqrt(3 * variance);
        return genUniformRandomBetween(mean - temp, mean + temp);
    }

    /**
     * Generates a normally distributed random number with the specified mean
     * and variance
     *
     * @param mean the specified mean
     * @param variance the specified variance
     * @return a normally distributed random number with the specified mean and
     * variance
     *
     */
    public static double genNormalRandomMeanVariance(double mean, double variance) {
        return (r.nextGaussian() * Math.sqrt(variance) + mean);
    }

    /**
     * Generates a normally distributed random number with the mean 0 and
     * variance 1
     *
     * @return a normally distributed random number with the mean 0 and variance
     * 1
     */
    private static double genNormalRandom() {
        return r.nextGaussian();
    }

    /**
     * Generates non-uniform random number
     *
     * @return
     */
    public static double genNonUniformRandom() {
        r1 = genExponentialRandom(2);
        if (r1 < 1) {
            return r1;
        } else {
            return genNonUniformRandom();
        }
    }
    

    	/**
	 * Given an array, it assigns as the elements of the array the probabilities
	 * of a probability density function (PDF). The sum of elements will add up
	 * to 1
	 *
	 * @param input
	 */
	public static void fillRandomUniInArray(double[] input) {
	    int[] weight = new int[input.length];
	    double sum = 0;
	    for (int a = 0; a < input.length; a++) {
	        weight[a] = (int) RandomGenerator.genUniformRandomBetween(10, 100);
	        System.out.println("wa: "+weight[a]);
	 	   
	        sum += weight[a];
	    }
	    for (int a = 0; a < input.length; a++) {
	        input[a] = (double) weight[a] / sum;
	    }
	    System.out.println("sum: "+sum);
		   
	}

	private static int getpCapacity(char type) {
		if (type=='f')
			return getivalue(800, 1300);
		else
			return getivalue(16000, 26000);
	}
	
	private static int getsCapacity(char type) {
		if (type=='f')
			return getivalue(25000, 50000);//MB
		else
			return getivalue(250000, 1000000);
		}
	
	private static int getLinkBW(char type) {
		if (type=='f')
			return getivalue(54, 10000);//Mb/s
		else
			return getivalue(10000,100000);//cloud
	}
	
	public static int getivalue(int min, int max)//must change ???
	{
		return genUniformRandomBetween( min,  max);
	}
	
	public static int getHost(int max, int min) {
		
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
        
	}

	private static double getdvalue(double min, double max)
	{
		Random r = new Random();
		double randomValue = min + r.nextInt()%(max - min);
		return randomValue;
	}
	
	private static float getfvalue(float min, float max)
	{
		Random r = new Random();
		float randomValue = min + r.nextFloat()%(max - min);
		return randomValue;
	}
	
	private double getvalue(double min, double max)
		{
			    if (min >= max) {
	                throw new IllegalArgumentException("max must be greater than min");
	            }
	            
			Random r = new Random();
			double randomValue = min + (max - min) * r.nextDouble();
			return randomValue;
		}
		
    /*
     This is for unit testing. You can call any function in this class to see how it works
     */
//    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(genUniformRandom());
//        }
//    }
}
