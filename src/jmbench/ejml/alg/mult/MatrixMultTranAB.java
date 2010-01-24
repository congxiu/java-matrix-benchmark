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

package jmbench.ejml.alg.mult;

import jmbench.interfaces.AlgorithmInterface;
import org.ejml.alg.dense.mult.MatrixMatrixMult;
import org.ejml.data.DenseMatrix64F;


/**
 * @author Peter Abeles
 */
public class MatrixMultTranAB {

    public static class Small implements AlgorithmInterface
    {
        @Override
        public long process(DenseMatrix64F[] inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];
            DenseMatrix64F matResult = inputs[2];

            long prev = System.currentTimeMillis();

            for( int i = 0; i < numTrials; i++ ) {
                MatrixMatrixMult.multTransAB(matA,matB,matResult);
            }

            return System.currentTimeMillis()-prev;
        }

        @Override
        public String getName() {
            return "TranAB Small";
        }
    }

    public static class Aux implements AlgorithmInterface
    {
        @Override
        public long process(DenseMatrix64F[] inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];
            DenseMatrix64F matResult = inputs[2];

            long prev = System.currentTimeMillis();

            for( int i = 0; i < numTrials; i++ ) {
                MatrixMatrixMult.multTransAB_aux(matA,matB,matResult,null);
            }

            return System.currentTimeMillis()-prev;
        }

        @Override
        public String getName() {
            return "TranAB Aux";
        }
    }
}