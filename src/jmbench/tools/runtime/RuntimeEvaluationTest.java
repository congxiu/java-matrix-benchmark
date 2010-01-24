/*
 * Copyright (c) 2009-2010, Peter Abeles. All Rights Reserved.
 *
 * This file is part of JMatrixBenchmark.
 *
 * JMatrixBenchmark is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * JMatrixBenchmark is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JMatrixBenchmark.  If not, see <http://www.gnu.org/licenses/>.
 */

package jmbench.tools.runtime;

import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.MatrixGenerator;
import jmbench.interfaces.MatrixProcessorInterface;
import jmbench.tools.EvaluationTest;
import jmbench.tools.TestResults;
import org.ejml.data.DenseMatrix64F;


/**
 * @author Peter Abeles
 */
public class RuntimeEvaluationTest extends EvaluationTest {
    private int dimen;
    private MatrixProcessorInterface alg;
    private MatrixGenerator generators[];

    // how long it should try to run the tests for in milliseconds
    private long expectedRuntime;

    // randomly generated input matrices
    private volatile DenseMatrix64F inputs[];

    // an estimate of how many cycles it will take to finish the test in the desired
    // amount of time
    private volatile long estimatedTrials;

    /**
     * Creates a new evaluation test.
     *
     * @param dimen How big the matrices are that are being processed.
     * @param alg The algorithm that is being processed.
     * @param generators What generates the random matrices.
     * @param expectedRuntime  How long it wants to try to run the test for in milliseconds
     * @param randomSeed The random seed used for the tests.
     */
    public RuntimeEvaluationTest( int dimen , MatrixProcessorInterface alg ,
                                  MatrixGenerator []generators ,
                                  long expectedRuntime, long randomSeed )
    {
        super(randomSeed);
        this.dimen = dimen;
        this.alg = alg;
        this.generators = generators;
        this.expectedRuntime = expectedRuntime;
    }

    public RuntimeEvaluationTest(){}

    public void printInfo() {
        if( getAlg() instanceof AlgorithmInterface) {
            String name = ((AlgorithmInterface)getAlg()).getName();
            System.out.println("Slave running: name = "+name+" dimen = "+getDimen()+
                    "  seed = "+getRandomSeed());
        } else {
            System.out.println("Slave running: dimen = "+getDimen()+
                    "  seed = "+getRandomSeed());
        }
    }

    /**
     * The slave should call this function before anything else.
     */
    @Override
    public void init() {
        for(MatrixGenerator m : generators ) {
            m.setSeed(randomSeed);
        }
    }

    @Override
    public void setupTrial()
    {
        if( inputs == null ) {
            inputs = new DenseMatrix64F[ generators.length ];
            estimatedTrials = 0;
        }

        for( int i = 0; i < inputs.length; i++ ) {
            MatrixGenerator m = generators[i];
            inputs[i] = m.createMatrix(dimen,dimen);
        }
    }

    /**
     * Returns how much memory the input matrices will require.
     *
     * @return Required memory in bytes
     */
    @Override
    public long getInputMemorySize() {
        long bytes = 0;

        for( MatrixGenerator g : generators ) {
            bytes += g.getMemory(dimen,dimen);
        }

        return bytes;
    }

    /**
     * Computes the number of operations per second it takes to run the specified algortihm
     * with the inputs specified in {@link #setupTrial()}.
     *
     * @return Number of operations per second.
     */
    @Override
    public TestResults evaluate()
    {
        int cycles = 0;
        long numTrials = estimatedTrials;

        if( numTrials <= 0 ) {
            numTrials = 1;
        }

        // translate it to nanoseconds
        long goalDuration = this.expectedRuntime *1000000;

        while( true ) {
            // nano is more precise than the millisecond timer
            long startTime = System.nanoTime();
            alg.process(inputs,numTrials);
            long stopTime = System.nanoTime();

            long elapsedTime = stopTime-startTime;
//            System.out.println("elapsed time = "+elapsedTime + "  numTrials "+numTrials);
//            System.out.println("  in seconds "+(elapsedTime/1e9));
            if( elapsedTime > goalDuration*0.9 )  {
                estimatedTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
//                System.out.println("  elpasedTime = "+elapsedTime);
                return new RuntimeResults((double)numTrials/(elapsedTime/1e9));
            } else if( elapsedTime > 2e8 ) {  // 0.2 seconds
                // if enough time has elapsed use a linear model to predict how many trials it will take
                long oldNumTrials = numTrials;
                numTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
//                System.out.println("numTrials A = "+numTrials);
                if( oldNumTrials > numTrials ) {
                    numTrials = oldNumTrials;
//                    System.out.println("Got smaller!?!?" );
                }
            } else if( elapsedTime > 1e7 ) { // 0.01 seconds
                // for smaller periods of time its better just to blindly increment it
                numTrials *= 10;
//                System.out.println("numTrials B = "+numTrials);
            } else {
                numTrials *= 100;
//                System.out.println("numTrials C = "+numTrials);
            }
            if( cycles++ > 20 ) {
                throw new RuntimeException("Exceeded the opsPerSecondMax cycles");
            }
        }
    }

    public int getDimen() {
        return dimen;
    }

    public void setDimen(int dimen) {
        this.dimen = dimen;
    }

    public MatrixProcessorInterface getAlg() {
        return alg;
    }

    public void setAlg(MatrixProcessorInterface alg) {
        this.alg = alg;
    }

    public MatrixGenerator[] getGenerators() {
        return generators;
    }

    public void setGenerators(MatrixGenerator[] generators) {
        this.generators = generators;
    }

    public long getExpectedRuntime() {
        return expectedRuntime;
    }

    public void setExpectedRuntime(long expectedRuntime) {
        this.expectedRuntime = expectedRuntime;
    }
}
