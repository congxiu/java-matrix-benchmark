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
import jmbench.impl.wrapper.UjmpBenchmarkMatrix;
import jmbench.interfaces.BenchmarkMatrix;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;
import org.ujmp.core.Matrix;
import org.ujmp.core.doublematrix.DenseDoubleMatrix2D;


/**
 * @author Peter Abeles
 */
public class UjmpMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.UJMP;
    }

    private static abstract class MyInterface implements MemoryProcessorInterface
    {
//        @Override
//        public String getName() {
//            return MatrixLibrary.EJML.getVersionName();
//        }
    }

    @Override
    public BenchmarkMatrix create(int numRows, int numCols) {
        return wrap( DenseDoubleMatrix2D.factory.zeros(numRows,numCols));
    }

    @Override
    public BenchmarkMatrix wrap(Object matrix) {
        return new UjmpBenchmarkMatrix((Matrix)matrix);
    }

    @Override
    public MemoryProcessorInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface
    {
        @Override
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();
            DenseDoubleMatrix2D B = inputs[1].getOriginal();
            DenseDoubleMatrix2D C = DenseDoubleMatrix2D.factory.zeros(A.getRowCount(),B.getColumnCount());

            for( int i = 0; i < numTrials; i++ )
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
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();
            DenseDoubleMatrix2D B = inputs[1].getOriginal();
            DenseDoubleMatrix2D C = DenseDoubleMatrix2D.factory.zeros(A.getRowCount(),A.getColumnCount());

            for( int i = 0; i < numTrials; i++ )
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
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();
            DenseDoubleMatrix2D y = inputs[1].getOriginal();

            for( int i = 0; i < numTrials; i++ )
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
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();
            DenseDoubleMatrix2D y = inputs[1].getOriginal();

            for( int i = 0; i < numTrials; i++ )
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
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();

            org.ujmp.core.Matrix []T=null;
            for( int i = 0; i < numTrials; i++ ) {
                T = DenseDoubleMatrix2D.svd.calc(A);
            }
            if( T == null )
                throw new RuntimeException("There is a null");
        }
    }

    @Override
    public MemoryProcessorInterface eig() {
        return new Eig();
    }

    public static class Eig extends MyInterface
    {
        @Override
        public void process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            DenseDoubleMatrix2D A = inputs[0].getOriginal();

            org.ujmp.core.Matrix []T=null;
            for( int i = 0; i < numTrials; i++ ) {
                T = DenseDoubleMatrix2D.eig.calc(A);
            }
            if( T == null )
                throw new RuntimeException("There is a null");
        }
    }
}