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
import org.jblas.DoubleMatrix;
import org.jblas.Eigen;
import org.jblas.Solve;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class JBlasMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.JBLAS;
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
            DoubleMatrix A = new DoubleMatrix(size,size);
            DoubleMatrix B = new DoubleMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.put(i,j,rand.nextDouble());
                    B.put(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                A.mmul(B);
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
            DoubleMatrix A = new DoubleMatrix(size,size);
            DoubleMatrix B = new DoubleMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.put(i,j,rand.nextDouble());
                    B.put(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                A.add(B);
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
            DoubleMatrix A = new DoubleMatrix(size,size);
            DoubleMatrix y = new DoubleMatrix(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.put(i,j,rand.nextDouble());
                }
                y.put(i,0,rand.nextDouble());
            }

            for( int i = 0; i < numCycles; i++ )
                Solve.solve(A,y);
        }
    }

    @Override
    public MemoryProcessorInterface solveLS() {
        return null;
    }

    @Override
    public MemoryProcessorInterface svd() {
        return null;
    }


    @Override
    public MemoryProcessorInterface eig() {
        return new Eig();
    }

    public static class Eig extends MyInterface
    {
        @Override
        public void process(int size, int numCycles, Random rand) {
            DoubleMatrix A = new DoubleMatrix(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.put(i,j,rand.nextDouble());
                    A.put(j,i,A.get(i,j));
                }
            }
            
            for( int i = 0; i < numCycles; i++ ) {
                Eigen.symmetricEigenvectors(A);
            }
        }
    }
}