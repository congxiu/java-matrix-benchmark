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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class JamaMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.JAMA;
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
            Matrix A = new Matrix(size,size);
            Matrix B = new Matrix(size,size);
            Matrix C;

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ){
                C = A.times(B);
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
            Matrix A = new Matrix(size,size);
            Matrix B = new Matrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                A.plus(B);
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
            Matrix A = new Matrix(size,size);
            Matrix y = new Matrix(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                A.solve(y);
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

            Matrix A = new Matrix(numRows,numCols);
            Matrix y = new Matrix(numRows,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                A.solve(y);
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
            Matrix A = new Matrix(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                SingularValueDecomposition svd = A.svd();

                Matrix S = svd.getS();
                Matrix U = svd.getU();
                Matrix V = svd.getV();
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
            Matrix A = new Matrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    A.set(j,i,A.get(i,j));
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                EigenvalueDecomposition eig = A.eig();

                Matrix D = eig.getD();
                Matrix V = eig.getV();
            }
        }
    }
}