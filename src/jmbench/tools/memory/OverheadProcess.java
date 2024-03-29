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

package jmbench.tools.memory;

import jmbench.interfaces.BenchmarkMatrix;
import jmbench.interfaces.MatrixProcessorInterface;

/**
 * A process that does nothing that is used to determine the system overhead of just launching
 * a test.  It just waits until two seconds have passed.
 *
 * @author Peter Abeles
 */
public class OverheadProcess implements MatrixProcessorInterface {

    @Override
    public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
        synchronized( this ) {
            long startTime = System.currentTimeMillis();

            while( startTime + 1000 >= System.currentTimeMillis() ) {
                try {
                    wait(500);
                } catch (InterruptedException e) {

                }
            }
        }
        return 0;
    }
}
