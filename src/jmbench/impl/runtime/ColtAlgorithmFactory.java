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

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.*;
import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.LibraryAlgorithmFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;


/**
 * @author Peter Abeles
 */
public class ColtAlgorithmFactory implements LibraryAlgorithmFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.COLT.getVersionName();
        }
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            DoubleMatrix2D L = null;
            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CholeskyDecomposition chol = new CholeskyDecomposition(matA);

                if( !chol.isSymmetricPositiveDefinite() ) {
                    throw new RuntimeException("Is not SPD");
                }

                L = chol.getL();
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = coltToEjml(L);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            LUDecompositionQuick lu = new LUDecompositionQuick();
            DenseDoubleMatrix2D tmp = new DenseDoubleMatrix2D(matA.rows(),matA.columns());

            DoubleMatrix2D L = null;
            DoubleMatrix2D U = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                tmp.assign(matA);
                lu.decompose(tmp);

                L = lu.getL();
                U = lu.getU();

                if( !lu.isNonsingular() )
                    throw new RuntimeException("Singular matrix");
            }

            long elapsed = System.currentTimeMillis()-prev;
            outputs[0] = coltToEjml(L);
            outputs[1] = coltToEjml(U);
            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                SingularValueDecomposition s = new SingularValueDecomposition(matA);
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
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                EigenvalueDecomposition eig = new EigenvalueDecomposition(matA);

                eig.getD();
                eig.getV();
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
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                QRDecomposition qr = new QRDecomposition(matA);
                if( !qr.hasFullRank() )
                    throw new RuntimeException("Doesn't have full rank");
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return new Det();
    }

    public static class Det extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            Algebra alg = new Algebra();

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
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            Algebra alg = new Algebra();

            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.inverse(matA);
            }

            long elapsed = System.currentTimeMillis()-prev;

            outputs[0] = coltToEjml(result);

            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);
            DenseDoubleMatrix2D matB = convertToColt(inputs[1]);

            Blas blas = SmpBlas.smpBlas;
            DoubleMatrix2D result = new DenseDoubleMatrix2D(matA.rows(),matA.columns());

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // In-place operation here
                result.assign(matA);
                blas.daxpy(1,matB,result);
            }

            long elapsed = System.currentTimeMillis()-prev;

            outputs[0] = coltToEjml(result);

            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);
            DenseDoubleMatrix2D matB = convertToColt(inputs[1]);

            Algebra alg = new Algebra();
            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.mult(matA,matB);
            }

            long elapsed = System.currentTimeMillis()-prev;

            outputs[0] = coltToEjml(result);

            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);
            DenseDoubleMatrix2D matB = convertToColt(inputs[1]);

            Algebra alg = new Algebra();
            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                DoubleMatrix2D tranA = alg.transpose(matA);
                result = alg.mult(tranA,matB);
            }

            long elapsed = System.currentTimeMillis()-prev;

            outputs[0] = coltToEjml(result);

            return elapsed;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);

            DoubleMatrix2D result = new DenseDoubleMatrix2D(matA.rows(),matA.columns());
            Blas blas = SmpBlas.smpBlas;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                // in-place operator
                result.assign(matA);
                blas.dscal(ScaleGenerator.SCALE,result);
            }

            long elapsed = System.currentTimeMillis()-prev;

            outputs[0] = coltToEjml(result);

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
        public long process(DenseMatrix64F[] inputs, DenseMatrix64F[] outputs, long numTrials) {
            DenseDoubleMatrix2D matA = convertToColt(inputs[0]);
            DenseDoubleMatrix2D matB = convertToColt(inputs[1]);

            Algebra alg = new Algebra();
            DoubleMatrix2D result = null;

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                result = alg.solve(matA,matB);
            }

            outputs[0] = coltToEjml(result);

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        // yep this is one of "those" libraries that just flags the matrix as being transposed
        return null;
    }
    
    public static DenseDoubleMatrix2D convertToColt( DenseMatrix64F orig )
    {
        DenseDoubleMatrix2D mat = new DenseDoubleMatrix2D(orig.numRows,orig.numCols);

        for( int i = 0; i < orig.numRows; i++ ) {
            for( int j = 0; j < orig.numCols; j++ ) {
                mat.set(i,j,orig.get(i,j));
            }
        }

        return mat;
    }

    public static DenseMatrix64F coltToEjml( DoubleMatrix2D orig )
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
}