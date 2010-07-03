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

import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;
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
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.EJML;
    }

    private static abstract class MyInterface implements MemoryProcessorInterface
    {
//        @Override
//        public String getName() {
//            return MatrixLibrary.EJML.getVersionName();
//        }
    }

    @Override
    public MemoryProcessorInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DenseMatrix64F A = new DenseMatrix64F(size,size);
            DenseMatrix64F B = new DenseMatrix64F(size,size);
            DenseMatrix64F C = new DenseMatrix64F(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                CommonOps.mult(A,B,C);
        }
    }

    @Override
    public MemoryProcessorInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DenseMatrix64F A = new DenseMatrix64F(size,size);
            DenseMatrix64F B = new DenseMatrix64F(size,size);
            DenseMatrix64F C = new DenseMatrix64F(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                CommonOps.add(A,B,C);
        }
    }

    @Override
    public MemoryProcessorInterface solveEq() {
        return new SolveLinear();
    }

    public static class SolveLinear extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DenseMatrix64F A = new DenseMatrix64F(size,size);
            DenseMatrix64F x = new DenseMatrix64F(size,1);
            DenseMatrix64F y = new DenseMatrix64F(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                CommonOps.solve(A,y,x);
        }
    }

    @Override
    public MemoryProcessorInterface solveLS() {
        return new SolveLS();
    }

    public static class SolveLS extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            int numRows = size*2;
            int numCols = size;

            DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);
            DenseMatrix64F x = new DenseMatrix64F(numCols,1);
            DenseMatrix64F y = new DenseMatrix64F(numRows,1);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                CommonOps.solve(A,y,x);
        }
    }

    @Override
    public MemoryProcessorInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            int numRows = size*2;
            int numCols = size;
            DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
            }

            SingularValueDecomposition svd = DecompositionFactory.svd();

            for( int i = 0; i < numCycles; i++ ) {
                svd.decompose(A);

                DenseMatrix64F U = svd.getU(false);
                DenseMatrix64F V = svd.getV(false);
                DenseMatrix64F S = svd.getW(null);
            }
        }
    }

    @Override
    public MemoryProcessorInterface eig() {
        return new Eig();
    }

    public static class Eig extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DenseMatrix64F A = new DenseMatrix64F(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    A.set(j,i,A.get(i,j));
                }
            }

            EigenDecomposition eig = DecompositionFactory.eig();

            for( int i = 0; i < numCycles; i++ ) {
                eig.decompose(A);

                DenseMatrix64F v[] = new DenseMatrix64F[size];
                for( int j = 0; j < size; j++ ) {
                    v[j] = eig.getEigenVector(j);
                }
            }
        }
    }
}
