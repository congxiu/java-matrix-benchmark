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
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.EVD;
import no.uib.cipr.matrix.NotConvergedException;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class MtjMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.MTJ;
    }

    private static abstract class MyInterface implements MemoryProcessorInterface
    {
    }

    @Override
    public MemoryProcessorInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DenseMatrix A = new DenseMatrix(size,size);
            DenseMatrix B = new DenseMatrix(size,size);
            DenseMatrix C = new DenseMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ){
                A.mult(B,C);
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
            DenseMatrix A = new DenseMatrix(size,size);
            DenseMatrix B = new DenseMatrix(size,size);
            DenseMatrix C = new DenseMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                C.set(A);
                C.add(B);
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
            DenseMatrix A = new DenseMatrix(size,size);
            DenseMatrix y = new DenseMatrix(size,1);
            DenseMatrix x = new DenseMatrix(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                A.solve(y,x);
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

            DenseMatrix A = new DenseMatrix(numRows,numCols);
            DenseMatrix y = new DenseMatrix(numRows,1);
            DenseMatrix x = new DenseMatrix(numCols,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                A.solve(y,x);
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
            DenseMatrix A = new DenseMatrix(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
            }

            no.uib.cipr.matrix.SVD svd = new no.uib.cipr.matrix.SVD(A.numRows(),A.numColumns());
            DenseMatrix tmp = new DenseMatrix(A);

            for( int i = 0; i < numCycles; i++ ) {
                try {
                    // the input matrix is over written
                    tmp.set(A);
                    no.uib.cipr.matrix.SVD s = svd.factor(tmp);
                    s.getU();
                    s.getS();
                    s.getVt();
                } catch (NotConvergedException e) {
                    throw new RuntimeException(e);
                }
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
            DenseMatrix A = new DenseMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    A.set(j,i,A.get(i,j));
                }
            }

            no.uib.cipr.matrix.EVD eig = new no.uib.cipr.matrix.EVD(A.numRows());
            DenseMatrix tmp = new DenseMatrix(A);

            for( int i = 0; i < numCycles; i++ ) {
                try {
                    // the input matrix is over written
                    tmp.set(A);
                    EVD e = eig.factor(tmp);
                    e.getRightEigenvectors();
                    e.getRealEigenvalues();
                } catch (NotConvergedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}