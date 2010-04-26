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
import org.apache.commons.math.linear.*;
import org.apache.commons.math.util.MathUtils;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class CommonsMathMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.CM;
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
            RealMatrix A = MatrixUtils.createRealMatrix(size,size);
            RealMatrix B = MatrixUtils.createRealMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                    B.setEntry(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                A.multiply(B);
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
            RealMatrix A = MatrixUtils.createRealMatrix(size,size);
            RealMatrix B = MatrixUtils.createRealMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                    B.setEntry(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                A.add(B);
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
            RealMatrix A = MatrixUtils.createRealMatrix(size,size);
            RealMatrix y = MatrixUtils.createRealMatrix(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                }
                y.setEntry(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ ) {
                LUDecomposition lu = new LUDecompositionImpl(A);
                lu.getSolver().solve(y);
            }

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

            RealMatrix A = MatrixUtils.createRealMatrix(numRows,numCols);
            RealMatrix y = MatrixUtils.createRealMatrix(numRows,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                }
                y.setEntry(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ ) {
                QRDecomposition qr = new QRDecompositionImpl(A);
                qr.getSolver().solve(y);
            }
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
            RealMatrix A = MatrixUtils.createRealMatrix(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                }
            }
            for( int i = 0; i < numCycles; i++ ) {
                org.apache.commons.math.linear.SingularValueDecomposition svd = new SingularValueDecompositionImpl(A);
                // need to call this functions so that it performs the full decomposition
                RealMatrix U = svd.getU();
                RealMatrix S = svd.getS();
                RealMatrix V = svd.getV();
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
            RealMatrix A = MatrixUtils.createRealMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.setEntry(i,j,rand.nextDouble());
                    A.setEntry(j,i,A.getEntry(i,j));
                }
            }

            for( int i = 0; i < numCycles; i++ ) {
                EigenDecompositionImpl eig = new EigenDecompositionImpl(A, MathUtils.SAFE_MIN);
                // need to do this so that it computes the complete eigen vector
                RealMatrix V = eig.getV();
                RealMatrix D = eig.getD();
            }
        }
    }
}