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

package jmbench.impl.runtime;

import Jama.*;
import jmbench.impl.MatrixLibrary;
import jmbench.impl.wrapper.EjmlBenchmarkMatrix;
import jmbench.impl.wrapper.JavaBenchmarkMatrix;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.BenchmarkMatrix;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.SpecializedOps;


/**
 * @author Peter Abeles
 */
public class JamaAlgorithmFactory implements RuntimePerformanceFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.JAMA.getVersionName();
        }
    }

    @Override
    public void configure() {
    }

    @Override
    public BenchmarkMatrix create(int numRows, int numCols) {
        return wrap(new Matrix(numRows,numCols));
    }

    @Override
    public BenchmarkMatrix wrap(Object matrix) {
        return new JavaBenchmarkMatrix((Matrix)matrix);
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix L = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                CholeskyDecomposition chol = matA.chol();
                if( !chol.isSPD() ) {
                    throw new RuntimeException("Is not SPD");
                }
                L = chol.getL();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(L);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix L = null;
            Matrix U = null;
            int pivot[] = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                LUDecomposition lu = matA.lu();
                L = lu.getL();
                U = lu.getU();
                pivot = lu.getPivot();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(L);
            outputs[1] = new JavaBenchmarkMatrix(U);
            outputs[2] = new EjmlBenchmarkMatrix(SpecializedOps.pivotMatrix(null, pivot, pivot.length,false));
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix U = null;
            Matrix S = null;
            Matrix V = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                SingularValueDecomposition s = matA.svd();
                U = s.getU();
                S = s.getS();
                V = s.getV();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(U);
            outputs[1] = new JavaBenchmarkMatrix(S);
            outputs[2] = new JavaBenchmarkMatrix(V);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface eigSymm() {
        return new Eig();
    }

    public static class Eig extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix D = null;
            Matrix V = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                EigenvalueDecomposition e = matA.eig();
                D = e.getD();
                V = e.getV();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(D);
            outputs[1] = new JavaBenchmarkMatrix(V);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return new QR();
    }

    public static class QR extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix Q = null;
            Matrix R = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                QRDecomposition decomp = matA.qr();

                Q = decomp.getQ();
                R = decomp.getR();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(Q);
            outputs[1] = new JavaBenchmarkMatrix(R);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return new Det();
    }

    public static class Det extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                matA.det();
            }

            return System.nanoTime()-prev;
        }
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.inverse();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface invertSymmPosDef() {
        return new InvSymmPosDef();
    }

    public static class InvSymmPosDef extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix result = null;

            int N = matA.getColumnDimension();

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.chol().solve(Matrix.identity(N,N));
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();
            Matrix matB = inputs[1].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.plus(matB);
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();
            Matrix matB = inputs[1].getOriginal();

            long prev = System.nanoTime();

            Matrix result = null;

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.times(matB);
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface multTransB() {
        return new MulTranB();
    }

    public static class MulTranB extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();
            Matrix matB = inputs[1].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.times(matB.transpose());
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.times(ScaleGenerator.SCALE);
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface solveExact() {
        return new Solve();
    }

    @Override
    public AlgorithmInterface solveOver() {
        return new Solve();
    }

    public static class Solve extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();
            Matrix matB = inputs[1].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.solve(matB);
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        return new Transpose();
    }

    public static class Transpose extends MyInterface {
        @Override
        public long process(BenchmarkMatrix[] inputs, BenchmarkMatrix[] outputs, long numTrials) {
            Matrix matA = inputs[0].getOriginal();

            Matrix result = null;

            long prev = System.nanoTime();

            for( long i = 0; i < numTrials; i++ ) {
                result = matA.transpose();
            }

            long elapsed = System.nanoTime()-prev;
            outputs[0] = new JavaBenchmarkMatrix(result);
            return elapsed;
        }
    }
    
    public static Matrix convertToJama( DenseMatrix64F orig )
    {
        Matrix ret = new Matrix(orig.getNumRows(),orig.getNumCols());

        for( int i = 0; i < orig.numRows; i++ ) {
            for( int j = 0; j < orig.numCols; j++ ) {
                ret.set(i,j,orig.get(i,j));
            }
        }

        return ret;
    }

    public static DenseMatrix64F jamaToEjml( Matrix orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F ret = new DenseMatrix64F(orig.getRowDimension(),orig.getColumnDimension());

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j,orig.get(i,j));
            }
        }

        return ret;
    }
}