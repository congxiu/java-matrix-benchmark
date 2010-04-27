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
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class UjmpMemoryFactory implements MemoryFactory {


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
            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(size,size);
            DenseDoubleMatrix2D B = DenseDoubleMatrix2D.factory.zeros(size,size);
            DenseDoubleMatrix2D C = DenseDoubleMatrix2D.factory.zeros(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                    B.setDouble(rand.nextDouble(),i,j);
                }
            }

            for( int i = 0; i < numCycles; i++ )
                DenseDoubleMatrix2D.mtimes.calc(A, B, C);
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
            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(size,size);
            DenseDoubleMatrix2D B = DenseDoubleMatrix2D.factory.zeros(size,size);
            DenseDoubleMatrix2D C = DenseDoubleMatrix2D.factory.zeros(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                    B.setDouble(rand.nextDouble(),i,j);
                }
            }

            for( int i = 0; i < numCycles; i++ )
                DenseDoubleMatrix2D.plusMatrix.calc(A, B, C);
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
            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(size,size);
            DenseDoubleMatrix2D y = DenseDoubleMatrix2D.factory.zeros(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                }
                y.setDouble(rand.nextDouble(),i,0);
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

            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(numRows,numCols);
            DenseDoubleMatrix2D y = DenseDoubleMatrix2D.factory.zeros(numRows,1);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                }
                y.setDouble(rand.nextDouble(),i,0);
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
            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                DenseDoubleMatrix2D.svd.calc(A);
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
            DenseDoubleMatrix2D A = DenseDoubleMatrix2D.factory.zeros(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.setDouble(rand.nextDouble(),i,j);
                    A.setDouble(A.getDouble(i,j),j,i);
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                DenseDoubleMatrix2D.eig.calc(A);
            }
        }
    }
}