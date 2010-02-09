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

package jmbench.impl.stability;

import jmbench.impl.MatrixLibrary;
import static jmbench.impl.runtime.JScienceAlgorithmFactory.convertToFloat64;
import static jmbench.impl.runtime.JScienceAlgorithmFactory.jsciToEjml;
import jmbench.interfaces.StabilityFactory;
import jmbench.interfaces.StabilityOperationInterface;
import org.ejml.data.DenseMatrix64F;
import org.jscience.mathematics.vector.Float64Matrix;


/**
 * @author Peter Abeles
 */
public class JScienceStabilityFactory implements StabilityFactory {

    @Override
    public MatrixLibrary getLibrary() {
        return MatrixLibrary.JSCIENCE;
    }

    public static abstract class CommonOperation implements StabilityOperationInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.JSCIENCE.getVersionName();
        }
    }

    public StabilityOperationInterface createLinearSolver() {
        return new MyLinearSolver();
    }

    public StabilityOperationInterface createLSSolver() {
        return null;
    }

    public static class MyLinearSolver extends CommonOperation
    {

        @Override
        public DenseMatrix64F[] process(DenseMatrix64F[] inputs) {
            Float64Matrix matA = convertToFloat64(inputs[0]);
            Float64Matrix matB = convertToFloat64(inputs[1]);


            DenseMatrix64F x = jsciToEjml(matA.solve(matB));

            return new DenseMatrix64F[]{x};
        }
    }

    @Override
    public StabilityOperationInterface createSvd() {
        return null;
    }

    @Override
    public StabilityOperationInterface createSymmEigen() {
        return null;
    }
}