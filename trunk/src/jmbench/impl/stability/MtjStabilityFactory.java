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
import static jmbench.impl.runtime.MtjAlgorithmFactory.convertToMtj;
import static jmbench.impl.runtime.MtjAlgorithmFactory.mtjToEjml;
import jmbench.interfaces.StabilityFactory;
import jmbench.interfaces.StabilityOperationInterface;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.EVD;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


/**
 * @author Peter Abeles
 */
public class MtjStabilityFactory implements StabilityFactory {

    @Override
    public MatrixLibrary getLibrary() {
        return MatrixLibrary.MTJ;
    }

    public static abstract class CommonOperation implements StabilityOperationInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.MTJ.getVersionName();
        }
    }

    public StabilityOperationInterface createLinearSolver() {
        return new MyLinearSolver();
    }

    public StabilityOperationInterface createLSSolver() {
        return new MyLinearSolver();
    }

    public static class MyLinearSolver extends CommonOperation
    {

        @Override
        public DenseMatrix64F[] process(DenseMatrix64F[] inputs) {
            DenseMatrix matA = convertToMtj(inputs[0]);
            DenseMatrix matB = convertToMtj(inputs[1]);

            DenseMatrix result = new DenseMatrix(matA.numColumns(),matB.numColumns());

            DenseMatrix x = (DenseMatrix)matA.solve(matB,result);

            return new DenseMatrix64F[]{mtjToEjml(x)};
        }
    }

    @Override
    public StabilityOperationInterface createSvd() {
        return new MySvd();
    }

    public static class MySvd extends CommonOperation
    {

        @Override
        public DenseMatrix64F[] process(DenseMatrix64F[] inputs) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            int m = matA.numRows();
            int n = matA.numColumns();

            SVD s = new SVD(matA.numRows(),matA.numColumns());
            try {
                s.factor(matA);
            } catch (NotConvergedException e) {
                return null;
            }

            DenseMatrix64F ejmlU = mtjToEjml(s.getU());
            DenseMatrix64F ejmlS = CommonOps.diagR(m,n,s.getS());
            DenseMatrix64F ejmlV = mtjToEjml(s.getVt());

            CommonOps.transpose(ejmlV);

            return new DenseMatrix64F[]{ejmlU,ejmlS,ejmlV};
        }
    }

    @Override
    public StabilityOperationInterface createSymmEigen() {
        return new MySymmEig();
    }

    public static class MySymmEig extends CommonOperation {
        @Override
        public DenseMatrix64F[] process(DenseMatrix64F[] inputs) {
            DenseMatrix matA = convertToMtj(inputs[0]);

            EVD eig = new EVD(matA.numRows());
            try {
                eig.factor(matA);
            } catch (NotConvergedException e) {
                return null;
            }

            DenseMatrix64F ejmlD = CommonOps.diag(eig.getRealEigenvalues());
            DenseMatrix64F ejmlV = mtjToEjml(eig.getLeftEigenvectors());

            return new DenseMatrix64F[]{ejmlD,ejmlV};
        }
    }
}
