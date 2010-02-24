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
import jmbench.interfaces.MatrixProcessorInterface;
import jmbench.tools.EvaluationTest;
import jmbench.tools.TestResults;
import org.ejml.data.DenseMatrix64F;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class RuntimeEvaluationTest extends EvaluationTest {

    public static final double MAX_ERROR_THRESHOLD = 0.05;

    private int dimen;
    private MatrixProcessorInterface alg;
    private InputOutputGenerator generator;

    // how long it should try to run the tests for in milliseconds
    private long expectedRuntime;

    // randomly generated input matrices
    private volatile Random rand;
    private volatile DenseMatrix64F inputs[];
    private volatile DenseMatrix64F outputs[];

    private volatile DenseMatrix64F residual;

    // an estimate of how many cycles it will take to finish the test in the desired
    // amount of time
    private volatile long estimatedTrials;

    /**
     * Creates a new evaluation test.
     *
     * @param dimen How big the matrices are that are being processed.
     * @param alg The algorithm that is being processed.
     * @param generator Creates the inputs and expected outputs for the tested operation
     * @param expectedRuntime  How long it wants to try to run the test for in milliseconds
     * @param randomSeed The random seed used for the tests.
     */
    public RuntimeEvaluationTest( int dimen ,
                                  MatrixProcessorInterface alg ,
                                  InputOutputGenerator generator ,
                                  long expectedRuntime, long randomSeed )
    {
        super(randomSeed);
        this.dimen = dimen;
        this.alg = alg;
        this.generator = generator;
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
        estimatedTrials = 0;
        rand = new Random(randomSeed);
        residual = new DenseMatrix64F(1,1);
    }

    @Override
    public void setupTrial()
    {
        inputs = generator.createRandomInputs(rand,dimen);
        outputs = new DenseMatrix64F[ generator.numOutputs() ];
    }

    /**
     * Returns how much memory the input matrices will require.
     *
     * @return Required memory in bytes
     */
    @Override
    public long getInputMemorySize() {
        return generator.getRequiredMemory(dimen);
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
            alg.process(inputs, outputs, numTrials);
            long stopTime = System.nanoTime();

            long elapsedTime = stopTime-startTime;
//            System.out.println("elapsed time = "+elapsedTime + "  numTrials "+numTrials+"  ops/sec "+(double)numTrials/(elapsedTime/1e9));
//            System.out.println("  in seconds "+(elapsedTime/1e9));
            if( elapsedTime > goalDuration*0.9 )  {
                estimatedTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
//                System.out.println("  elpasedTime = "+elapsedTime);
                return compileResults((double)numTrials/(elapsedTime/1e9));
            } else {  // 0.2 seconds
                // if enough time has elapsed use a linear model to predict how many trials it will take
                long oldNumTrials = numTrials;
                
                numTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
//                System.out.println("numTrials A = "+numTrials);
                if( oldNumTrials > numTrials ) {
                    numTrials = oldNumTrials;
//                    System.out.println("Got smaller!?!?" );
                }
            }

            // try to get it to clean up some
            System.gc();
            Thread.yield();
            System.gc();
            Thread.yield();

//            if( elapsedTime > 1e7 ) { // 0.01 seconds
//                // for smaller periods of time its better just to blindly increment it
//                numTrials *= 10;
////                System.out.println("numTrials B = "+numTrials);
//            } else {
//                numTrials *= 100;
////                System.out.println("numTrials C = "+numTrials);
//            }
            if( cycles++ > 20 ) {
                throw new RuntimeException("Exceeded the opsPerSecondMax cycles");
            }
        }
    }

    /**
     * Generates the results based upon the computed opsPerSecond and the expected output.
     */
    private RuntimeResults compileResults( double opsPerSecond )
    {
        RuntimeResults results = new RuntimeResults(opsPerSecond,Runtime.getRuntime().totalMemory());
        results.error = generator.checkResults(outputs,MAX_ERROR_THRESHOLD);

        return results;
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

    public InputOutputGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(InputOutputGenerator generator) {
        this.generator = generator;
    }

    public long getExpectedRuntime() {
        return expectedRuntime;
    }

    public void setExpectedRuntime(long expectedRuntime) {
        this.expectedRuntime = expectedRuntime;
    }
}