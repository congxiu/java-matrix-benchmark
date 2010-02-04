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

import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.algo.DoubleBlas;
import cern.colt.matrix.tdouble.algo.SmpDoubleBlas;
import cern.colt.matrix.tdouble.algo.decomposition.*;
import cern.colt.matrix.tdouble.impl.DenseColumnDoubleMatrix2D;
import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.LibraryAlgorithmFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.SpecializedOps;


/**
 * @author Peter Abeles
 */
public class PColtAlgorithmFactory implements LibraryAlgorithmFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.PCOLT.getVersionName();
        }
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    // DenseDoubleAlgebra
    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            DoubleMatrix2D L = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // can't decompose a matrix with the same decomposition algorithm
                DenseDoubleCholeskyDecomposition chol = alg.chol(matA);

                L = chol.getL();
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(L);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            // the recommended way I think would be using Algebra, but this might allow
            // reuse of data
            DenseDoubleLUDecompositionQuick decomp = new DenseDoubleLUDecompositionQuick();
            DoubleMatrix2D tmp = new DenseColumnDoubleMatrix2D(matA.rows(),matA.columns());

            DoubleMatrix2D L = null;
            DoubleMatrix2D U = null;
            int[] pivot = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // input matrix is overwritten
                tmp.assign(matA);
                decomp.decompose(tmp);
                if( !decomp.isNonsingular() )
                    throw new RuntimeException("LU decomposition failed");

                L = decomp.getL();
                U = decomp.getU();
                pivot = decomp.getPivot();
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(L);
            outputs[1] = parallelColtToEjml(U);
            outputs[2] = SpecializedOps.pivotMatrix(null,pivot,pivot.length,false);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
            long prev = System.currentTimeMillis();

            // There are two MySVD decomposition algorithms.
            // I arbitrarily chose this version.  The java doc provided no guidelines...
            for( long i = 0; i < numTrials; i++ ) {
                DenseDoubleSingularValueDecomposition s = alg.svd(matA);
                s.getU();
                s.getS();
                s.getV();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface eigSymm() {
        return new Eig();
    }

   public static class Eig extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                DenseDoubleEigenvalueDecomposition e = alg.eig(matA);
                e.getD();
                e.getV();
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return new QR();
    }

    public static class QR extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            DoubleMatrix2D Q = null;
            DoubleMatrix2D R = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                DenseDoubleQRDecomposition decomp = alg.qr(matA);

                Q = decomp.getQ(true);
                R = decomp.getR(true);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(Q);
            outputs[1] = parallelColtToEjml(R);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return new Det();
    }

    public static class Det extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                alg.det(matA);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.inverse(matA);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);
            DoubleMatrix2D matB = convertToParallelColt(inputs[1]);

            DoubleBlas blas = new SmpDoubleBlas();
            DoubleMatrix2D result = new DenseColumnDoubleMatrix2D(matA.rows(),matA.columns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // in-place operator
                result.assign(matA);
                blas.daxpy(1.0,matB,result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);
            DoubleMatrix2D matB = convertToParallelColt(inputs[1]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.mult(matA,matB);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);
            DoubleMatrix2D matB = convertToParallelColt(inputs[1]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();

            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                DoubleMatrix2D tran = alg.transpose(matA);
                result = alg.mult(tran,matB);
                
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);

            DoubleBlas blas = new SmpDoubleBlas();
            DoubleMatrix2D result = new DenseColumnDoubleMatrix2D(matA.rows(),matA.columns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // in-place operator
                result.assign(matA);
                blas.dscal(ScaleGenerator.SCALE,result);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
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
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DoubleMatrix2D matA = convertToParallelColt(inputs[0]);
            DoubleMatrix2D matB = convertToParallelColt(inputs[1]);

            DenseDoubleAlgebra alg = new DenseDoubleAlgebra();
            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.solve(matA,matB);
            }

            long elapsedTime = System.currentTimeMillis()-prev;
            outputs[0] = parallelColtToEjml(result);
            return elapsedTime;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        // yep it just marks it as transposed
        return null;
    }
    
    public static cern.colt.matrix.tdouble.DoubleMatrix2D convertToParallelColt( DenseMatrix64F orig )
    {
        DenseColumnDoubleMatrix2D mat = new DenseColumnDoubleMatrix2D(orig.numRows,orig.numCols);

        for( int i = 0; i < orig.numRows; i++ ) {
            for( int j = 0; j < orig.numCols; j++ ) {
                mat.set(i,j,orig.get(i,j));
            }
        }

        return mat;
    }

    public static DenseMatrix64F parallelColtToEjml( cern.colt.matrix.tdouble.DoubleMatrix2D orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F mat = new DenseMatrix64F(orig.rows(),orig.columns());

        for( int i = 0; i < mat.numRows; i++ ) {
            for( int j = 0; j < mat.numCols; j++ ) {
                mat.set(i,j,orig.get(i,j));
            }
        }

        return mat;
    }

    public static void main( String args[] ) {
        MyInterface add = new Solve();

        DenseMatrix64F a = new DenseMatrix64F(10,10);
        DenseMatrix64F b = new DenseMatrix64F(10,1);

        add.process(new DenseMatrix64F[]{a,b}, null, 3);
    }

}