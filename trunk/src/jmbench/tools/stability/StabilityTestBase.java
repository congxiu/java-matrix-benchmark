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

package jmbench.tools.stability;

import jmbench.interfaces.StabilityOperationInterface;
import jmbench.tools.EvaluationTest;
import jmbench.tools.TestResults;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public abstract class StabilityTestBase extends EvaluationTest {

    protected StabilityOperationInterface operation;

    protected int totalTrials;
    protected double breakingPoint;

    protected transient Random rand;
    protected transient double foundResult;
    protected transient Reason reason;
    protected transient StabilityTrialResults results;
    protected transient int numResults;

    protected StabilityTestBase(long randomSeed,
                                StabilityOperationInterface operation,
                                int totalTrials,
                                double breakingPoint ) {
        super(randomSeed);
        this.operation = operation;
        this.totalTrials = totalTrials;
        this.breakingPoint = breakingPoint;
    }

    public StabilityTestBase(){}

    abstract public void performTest();

    /**
     * The full name of the test being performed.
     *
     * @return Name of the test being performed.
     */
    public abstract String getTestName();

    /**
     * Name of the file this test should be saved to.
     *
     * @return File name.
     */
    public abstract String getFileName();


    @Override
    public void init() {
        rand = new Random(randomSeed);
    }

    @Override
    public void setupTrial() {
    }

    @Override
    public void printInfo() {
        System.out.println("Library = "+operation.getName());
    }

    @Override
    public long getExpectedRuntime() {
        return -1;
    }

    @Override
    public TestResults evaluate() {
        results = new StabilityTrialResults();

        numResults = 0;

        performTest();

        return results;
    }

    protected void addUnexpectedException( Exception e ) {
        String name = e.getClass().getSimpleName();

        for( ExceptionInfo i : results.unexpectedExceptions ) {
            if( i.getShortName().compareTo(name) == 0 ) {
                i.numTimesThrown++;
                return;
            }
        }
        
        results.unexpectedExceptions.add(new ExceptionInfo(e));

    }

    protected int findMaxPow( double a ) {
        for( int i = 0; true; i++ ) {
            double p = Math.pow(a,i);
            if( Double.isInfinite(p) || p == 0)
                return i;
        }
    }

    protected void saveResults() {
        results.breakingPoints.add(foundResult);

        switch( reason ) {
            case FINISHED:
                results.numFinished++;
                break;

            case UNCOUNTABLE:
                results.numUncountable++;
                break;

            case LARGE_ERROR:
                results.numLargeError++;
                break;

            case UNEXPECTED_EXCEPTION:
                results.numUnexpectedException++;
                break;

            case GRACEFULL_FAILURE:
                results.numGraceful++;
                break;

            default:
                throw new RuntimeException("Unknown reason: "+reason);
        }
        System.gc();
    }

    public static enum Reason
    {
        /**
         * Its solution was outside of tolerance
         */
        LARGE_ERROR,
        /**
         * The answer it produced has uncountable numbers
         */
        UNCOUNTABLE,
        /**
         * Detected that it was failing and gave up
         */
        GRACEFULL_FAILURE,
        /**
         * It threw some runtime exception
         */
        UNEXPECTED_EXCEPTION,
        /**
         * The test finished without error
         */
        FINISHED
    }

    public StabilityOperationInterface getOperation() {
        return operation;
    }

    public void setOperation(StabilityOperationInterface operation) {
        this.operation = operation;
    }

    public int getTotalTrials() {
        return totalTrials;
    }

    public void setTotalTrials(int totalTrials) {
        this.totalTrials = totalTrials;
    }

    public double getBreakingPoint() {
        return breakingPoint;
    }

    public void setBreakingPoint(double breakingPoint) {
        this.breakingPoint = breakingPoint;
    }
}
