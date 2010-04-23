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

package jmbench.impl.memory;

import jmbench.interfaces.MemoryFactory;
import org.ejml.alg.dense.decomposition.DecompositionFactory;
import org.ejml.alg.dense.decomposition.EigenDecomposition;
import org.ejml.alg.dense.decomposition.SingularValueDecomposition;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class EjmlMemoryFactory implements MemoryFactory {
    @Override
    public void mult(int size, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(size,size);
        DenseMatrix64F B = new DenseMatrix64F(size,size);
        DenseMatrix64F C = new DenseMatrix64F(size,size);

        for( int i = 0; i < size; i++ ) {
            for( int j = 0; j < size; j++ ) {
                A.set(i,j,rand.nextDouble());
                B.set(i,j,rand.nextDouble());
            }
        }

        CommonOps.mult(A,B,C);
    }

    @Override
    public void add(int size, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(size,size);
        DenseMatrix64F B = new DenseMatrix64F(size,size);
        DenseMatrix64F C = new DenseMatrix64F(size,size);

        for( int i = 0; i < size; i++ ) {
            for( int j = 0; j < size; j++ ) {
                A.set(i,j,rand.nextDouble());
                B.set(i,j,rand.nextDouble());
            }
        }

        CommonOps.add(A,B,C);
    }

    @Override
    public void solveEq(int size, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(size,size);
        DenseMatrix64F x = new DenseMatrix64F(size,1);
        DenseMatrix64F y = new DenseMatrix64F(size,1);

        for( int i = 0; i < size; i++ ) {
            for( int j = 0; j < size; j++ ) {
                A.set(i,j,rand.nextDouble());
            }
            y.set(i,0,rand.nextDouble());
        }

        CommonOps.solve(A,x,y);
    }

    @Override
    public void solveLS(int numRows, int numCols, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);
        DenseMatrix64F x = new DenseMatrix64F(numCols,1);
        DenseMatrix64F y = new DenseMatrix64F(numRows,1);

        for( int i = 0; i < numRows; i++ ) {
            for( int j = 0; j < numCols; j++ ) {
                A.set(i,j,rand.nextDouble());
            }
            y.set(i,0,rand.nextDouble());
        }

        CommonOps.solve(A,x,y);
    }

    @Override
    public void svd(int numRows, int numCols, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);

        for( int i = 0; i < numRows; i++ ) {
            for( int j = 0; j < numCols; j++ ) {
                A.set(i,j,rand.nextDouble());
            }
        }

        SingularValueDecomposition svd = DecompositionFactory.svd();

        svd.decompose(A);

        DenseMatrix64F U = svd.getU();
        DenseMatrix64F V = svd.getV();
        DenseMatrix64F S = svd.getW(null);
    }

    @Override
    public void eig(int size, Random rand) {
        DenseMatrix64F A = new DenseMatrix64F(size,size);

        for( int i = 0; i < size; i++ ) {
            for( int j = i; j < size; j++ ) {
                A.set(i,j,rand.nextDouble());
                A.set(j,i,A.get(i,j));
            }
        }

        EigenDecomposition eig = DecompositionFactory.eig();

        eig.decompose(A);

        DenseMatrix64F v[] = new DenseMatrix64F[size];
        for( int i = 0; i < size; i++ ) {
            v[i] = eig.getEigenVector(i);
        }
    }
}
