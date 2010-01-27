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
import jmbench.interfaces.StabilityFactory;
import jmbench.interfaces.StabilityOperationInterface;
import org.ejml.alg.dense.decomposition.EigenDecomposition;
import org.ejml.alg.dense.decomposition.SingularValueDecomposition;
import org.ejml.data.Complex64F;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.EigenOps;
import org.ejml.ops.SpecializedOps;


/**
 * @author Peter Abeles
 */
public class EjmlStabilityFactory implements StabilityFactory {


    @Override
    public MatrixLibrary getLibrary() {
        return MatrixLibrary.EJML;
    }

    public static abstract class CommonOperation implements StabilityOperationInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.EJML.getVersionName();
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
            DenseMatrix64F A = inputs[0];
            DenseMatrix64F b = inputs[1];

            DenseMatrix64F x = new DenseMatrix64F(A.numCols,b.numCols);

            if( !CommonOps.solve(inputs[0],inputs[1],x) )
                return null;

            return new DenseMatrix64F[]{x};
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
            DenseMatrix64F A = inputs[0];

            SingularValueDecomposition svd = SpecializedOps.svd(A);

            if( svd == null )
                return null;

            DenseMatrix64F U = svd.getU();
            DenseMatrix64F S = svd.getW();
            DenseMatrix64F V = svd.getV();

            return new DenseMatrix64F[]{U,S,V};
        }
    }

    @Override
    public StabilityOperationInterface createSymmEigen() {
        return new MyEigen();
    }

    public static class MyEigen extends CommonOperation
    {

        @Override
        public DenseMatrix64F[] process(DenseMatrix64F[] inputs) {
            DenseMatrix64F A = inputs[0];

            EigenDecomposition eig = EigenOps.decompositionSymmetric();

            if( !eig.decompose(A) )
                return null;

            int N = A.numRows;

            DenseMatrix64F D = new DenseMatrix64F( N , N );
            DenseMatrix64F V = new DenseMatrix64F( N , N );

            for( int i = 0; i < N; i++ ) {
                Complex64F c = eig.getEigenvalue(i);

                if( c.isReal() ) {
                    D.set(i,i,c.real);

                    DenseMatrix64F v = eig.getEigenVector(i);

                    if( v != null ) {
                        for( int j = 0; j < N; j++ ) {
                            V.set(j,i,v.get(j,0));
                        }
                    }
                }
            }

            return new DenseMatrix64F[]{D,V};
        }
    }
}
