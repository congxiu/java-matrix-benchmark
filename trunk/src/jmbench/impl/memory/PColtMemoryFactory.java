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

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleEigenvalueDecomposition;
import cern.colt.matrix.tdouble.algo.decomposition.DenseDoubleSingularValueDecomposition;
import cern.colt.matrix.tdouble.impl.DenseDoubleMatrix2D;
import cern.jet.math.tdouble.DoubleFunctions;
import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class PColtMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.COLT;
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
            DoubleMatrix2D A = new DenseDoubleMatrix2D(size,size);
            DoubleMatrix2D B = new DenseDoubleMatrix2D(size,size);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                alg.mult(A,B);
            }
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
            DoubleMatrix2D A = new DenseDoubleMatrix2D(size,size);
            DoubleMatrix2D B = new DenseDoubleMatrix2D(size,size);
            DoubleMatrix2D C = new DenseDoubleMatrix2D(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                C.assign(A);
                C.assign(B, DoubleFunctions.plus);
            }
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
            DoubleMatrix2D A = new DenseDoubleMatrix2D(size,size);
            DoubleMatrix2D y = new DenseDoubleMatrix2D(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            for( int i = 0; i < numCycles; i++ )
                alg.solve(A,y);
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

            DoubleMatrix2D A = new DenseDoubleMatrix2D(numRows,numCols);
            DoubleMatrix2D y = new DenseDoubleMatrix2D(numRows,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            for( int i = 0; i < numCycles; i++ )
                alg.solve(A,y);
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
            DoubleMatrix2D A = new DenseDoubleMatrix2D(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
            }

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            for( int i = 0; i < numCycles; i++ ) {
                DenseDoubleSingularValueDecomposition s = alg.svd(A);
                DoubleMatrix2D U = s.getU();
                DoubleMatrix2D S = s.getS();
                DoubleMatrix2D V = s.getV();
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
            DoubleMatrix2D A = new DenseDoubleMatrix2D(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    A.set(j,i,A.get(i,j));
                }
            }

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            for( int i = 0; i < numCycles; i++ ) {
                DenseDoubleEigenvalueDecomposition eig = alg.eig(A);

                DoubleMatrix2D D = eig.getD();
                DoubleMatrix2D V = eig.getV();
            }
        }
    }
}