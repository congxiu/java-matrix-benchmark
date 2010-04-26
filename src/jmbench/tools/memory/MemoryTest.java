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

package jmbench.tools.memory;

import jmbench.interfaces.MemoryProcessorInterface;
import jmbench.tools.EvaluationTest;
import jmbench.tools.TestResults;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class MemoryTest extends EvaluationTest {

    MemoryProcessorInterface op;
    int N;
    int size;

    public void setup( MemoryProcessorInterface op , int N , int size ) {
        this.op = op;
        this.N = N;
        this.size = size;
    }

    @Override
    public void init() {
    }

    @Override
    public void setupTrial() {
    }

    @Override
    public void printInfo() {
    }

    @Override
    public long getMaximumRuntime() {
        return -1;
    }

    @Override
    public long getInputMemorySize() {
        return -1;
    }

    @Override
    public TestResults evaluate() {
        Random rand = new Random(randomSeed);

        long start = System.currentTimeMillis();
        op.process(size,N,rand);
        long stop= System.currentTimeMillis();

        return new Results(stop-start);
    }

    public static class Results implements TestResults
    {
        long elapsedTime;

        public Results(long elapsedTime) {
            this.elapsedTime = elapsedTime;
//            System.out.println(" Elapsed time  "+elapsedTime);
        }

        public Results() {
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }
    }

    public MemoryProcessorInterface getOp() {
        return op;
    }

    public void setOp(MemoryProcessorInterface op) {
        this.op = op;
    }

    public int getN() {
        return N;
    }

    public void setN(int n) {
        N = n;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
