/*
 * Copyright (c) 2009-2011, Peter Abeles. All Rights Reserved.
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

import jmbench.interfaces.BenchmarkMatrix;
import jmbench.interfaces.MatrixProcessorInterface;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.EvaluationTest;
import jmbench.tools.TestResults;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class RuntimeEvaluationTest extends EvaluationTest {

    public static final double MAX_ERROR_THRESHOLD = 0.05;

    private int dimen;
    private RuntimePerformanceFactory factory;
    private MatrixProcessorInterface alg;
    private InputOutputGenerator generator;

    // how long it should try to run the tests for in milliseconds
    private long goalRuntime;
    // the max amount of time it will let a test run for
    private long maxRuntime;

    // randomly generated input matrices
    private volatile Random rand;
    private volatile BenchmarkMatrix inputs[];
    private volatile BenchmarkMatrix outputs[];

    // should it make sure the tested operation is performing the expected oepration
    private boolean sanityCheck;

    // an estimate of how many cycles it will take to finish the test in the desired
    // amount of time
    private volatile long estimatedTrials;

    /**
     * Creates a new evaluation test.
     *
     * @param dimen How big the matrices are that are being processed.
     * @param alg The algorithm that is being processed.
     * @param generator Creates the inputs and expected outputs for the tested operation
     * @param goalRuntime  How long it wants to try to run the test for in milliseconds
     * @param maxRuntime  How long it will let a test run for in milliseconds
     * @param randomSeed The random seed used for the tests.
     */
    public RuntimeEvaluationTest( int dimen ,
                                  RuntimePerformanceFactory factory,
                                  MatrixProcessorInterface alg ,
                                  InputOutputGenerator generator ,
                                  boolean sanityCheck ,
                                  long goalRuntime, long maxRuntime , long randomSeed )
    {
        super(randomSeed);
        this.dimen = dimen;
        this.factory = factory;
        this.alg = alg;
        this.generator = generator;
        this.sanityCheck = sanityCheck;
        this.goalRuntime = goalRuntime;
        this.maxRuntime = maxRuntime;
    }

    public RuntimeEvaluationTest(){}

    @Override
    public void printInfo() {

    }

    /**
     * The slave should call this function before anything else.
     */
    @Override
    public void init() {
        estimatedTrials = 0;
        rand = new Random(randomSeed);
        factory.configure();
    }

    @Override
    public void setupTrial()
    {
        inputs = generator.createInputs(factory,rand,sanityCheck,dimen);
        outputs = new BenchmarkMatrix[ generator.numOutputs() ];
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

        // try to purge all temporary data that has yet to be clean up so that the GC won't run
        // while performance is being measured
        runGarbageCollector();

        // translate it to nanoseconds
        long goalDuration = this.goalRuntime *1000000;

        while( true ) {
            // nano is more precise than the millisecond timer
            long elapsedTime = alg.process(inputs, outputs, numTrials);

//            System.out.println("elapsed time = "+elapsedTime + "  numTrials "+numTrials+"  ops/sec "+(double)numTrials/(elapsedTime/1e9));
//            System.out.println("  in seconds "+(elapsedTime/1e9));
            if( elapsedTime > goalDuration*0.9 )  {
                estimatedTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
//                System.out.println("  elapsedTime = "+elapsedTime);
                return compileResults((double)numTrials/(elapsedTime/1e9));
            } else {  // 0.2 seconds
                // if enough time has elapsed use a linear model to predict how many trials it will take
                long oldNumTrials = numTrials;
                
                numTrials = (long)Math.ceil(goalDuration * (double)numTrials / (double)elapsedTime);
                if( oldNumTrials > numTrials ) {
                    numTrials = oldNumTrials;
                }
            }
            runGarbageCollector();

            if( cycles++ > 20 ) {
                throw new RuntimeException("Exceeded the opsPerSecondMax cycles");
            }
        }
    }

    private void runGarbageCollector() {
        // try to get it to clean up some
        for( int i = 0; i < 5; i++ ) {
            System.gc();
            Thread.yield();
            System.gc();
            Thread.yield();
        }
    }

    /**
     * Generates the results based upon the computed opsPerSecond and the expected output.
     */
    private RuntimeResults compileResults( double opsPerSecond )
    {
        RuntimeResults results = new RuntimeResults(opsPerSecond,Runtime.getRuntime().totalMemory());
        if( sanityCheck )
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

    public long getGoalRuntime() {
        return goalRuntime;
    }

    public void setGoalRuntime(long goalRuntime) {
        this.goalRuntime = goalRuntime;
    }

    public boolean isSanityCheck() {
        return sanityCheck;
    }

    public void setSanityCheck(boolean sanityCheck) {
        this.sanityCheck = sanityCheck;
    }

    public RuntimePerformanceFactory getFactory() {
        return factory;
    }

    public void setFactory(RuntimePerformanceFactory factory) {
        this.factory = factory;
    }

    @Override
    public long getMaximumRuntime() {
        return maxRuntime;
    }

    public void setMaximumRuntime(long maxRuntime) {
        this.maxRuntime = maxRuntime;
    }
}
