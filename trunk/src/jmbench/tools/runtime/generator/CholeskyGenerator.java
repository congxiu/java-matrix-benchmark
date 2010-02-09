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

package jmbench.tools.runtime.generator;

import jmbench.tools.OutputError;
import jmbench.tools.runtime.InputOutputGenerator;
import jmbench.tools.stability.StabilityBenchmark;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.RandomMatrices;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class CholeskyGenerator implements InputOutputGenerator {

    DenseMatrix64F A;

    @Override
    public DenseMatrix64F[] createRandomInputs(Random rand , int matrixSize ) {
        A = RandomMatrices.createRandomPosDef(matrixSize,rand);

        return new DenseMatrix64F[]{A};
    }

    @Override
    public OutputError checkResults(DenseMatrix64F[] output, double tol) {
        DenseMatrix64F L = output[0];

        if( L == null ) {
            return OutputError.MISC;
        }

        if(MatrixFeatures.hasUncountable(L)) {
            return OutputError.UNCOUNTABLE;
        }

        DenseMatrix64F A_found = new DenseMatrix64F(A.numRows,A.numCols);

        CommonOps.multTransB(L,L,A_found);

        double error = StabilityBenchmark.residualError(A_found,A);
        if( error > tol ) {
            return OutputError.LARGE_ERROR;
        }

        return OutputError.NO_ERROR;
    }

    @Override
    public int numOutputs() {
        return 1;
    }

    @Override
    public long getRequiredMemory( int matrixSize ) {
        return matrixSize*matrixSize*4;
    }
}