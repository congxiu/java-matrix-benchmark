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

import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.LibraryAlgorithmFactory;
import org.ejml.alg.dense.decomposition.*;
import org.ejml.alg.dense.decomposition.svd.SvdNumericalRecipes;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;


/**
 * @author Peter Abeles
 */
public class EjmlAlgorithmFactory implements LibraryAlgorithmFactory {

    private static abstract class MyInterface implements AlgorithmInterface
    {
        @Override
        public String getName() {
            return MatrixLibrary.EJML.getVersionName();
        }
    }

    @Override
    public AlgorithmInterface chol() {
        return new Chol();
    }

    public static class Chol extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            CholeskyDecomposition chol = DecompositionFactory.chol(matA.numRows, false, true);

            for( long i = 0; i < numTrials; i++ ) {
                if( !chol.decompose(matA) ) {
                    throw new RuntimeException("Not SPD");
                }
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface lu() {
        return new LU();
    }

    public static class LU extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            LUDecomposition lu = DecompositionFactory.lu();

            for( long i = 0; i < numTrials; i++ ) {
                lu.decompose(matA);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface svd() {
        return new SVD();
    }

    public static class SVD extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            SvdNumericalRecipes svd = new SvdNumericalRecipes();

            for( long i = 0; i < numTrials; i++ ) {
                svd.decompose(matA);
                svd.getU();
                svd.getW();
                svd.getV();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface eigSymm() {
        return new MyEig();
    }

    public static class MyEig extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            EigenDecomposition eig = DecompositionFactory.eig();

            for( long i = 0; i < numTrials; i++ ) {
                eig.decompose(matA);
                eig.getEigenvalue(0);
                eig.getEigenVector(0);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface qr() {
        return new QR();
    }

    public static class QR extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            QRDecomposition qr = DecompositionFactory.qr();

            for( long i = 0; i < numTrials; i++ ) {
                qr.decompose(matA);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface det() {
        return new Det();
    }

    public static class Det extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.det(matA);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface invert() {
        return new Inv();
    }

    public static class Inv extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            DenseMatrix64F B = new DenseMatrix64F(matA.numRows,matA.numCols);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.invert(matA,B);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface add() {
        return new Add();
    }

    public static class Add extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];

            DenseMatrix64F matC = new DenseMatrix64F(matA);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.add(matA,matB,matC);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    public static class AddTransA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];

            DenseMatrix64F matC = new DenseMatrix64F(matA);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.transpose(matA,matC);
                CommonOps.addEquals(matC,matB);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface mult() {
        return new Mult();
    }

    public static class Mult extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];

            long prev = System.currentTimeMillis();

            DenseMatrix64F results = new DenseMatrix64F(matA.numRows,matB.numCols);

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.mult(matA,matB,results);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface multTransA() {
        return new MulTranA();
    }

    public static class MulTranA extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];

            long prev = System.currentTimeMillis();

            DenseMatrix64F results = new DenseMatrix64F(matA.numCols,matB.numCols);

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.multTransA(matA,matB,results);
            }

            return System.currentTimeMillis()-prev;
        }
    }

    @Override
    public AlgorithmInterface scale() {
        return new Scale();
    }

    public static class Scale extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            long prev = System.currentTimeMillis();

            DenseMatrix64F matB = new DenseMatrix64F(matA.numRows,matA.numCols);

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.scale(2.5,matA,matB);
            }

            return System.currentTimeMillis() - prev;
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
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];
            DenseMatrix64F matB = inputs[1];

            long prev = System.currentTimeMillis();

            DenseMatrix64F results = new DenseMatrix64F(matA.numCols,matB.numCols);

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.solve(matA,matB,results);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    @Override
    public AlgorithmInterface transpose() {
        return new Transpose();
    }

    public static class Transpose extends MyInterface {
        @Override
        public long process(DenseMatrix64F[]inputs, long numTrials) {
            DenseMatrix64F matA = inputs[0];

            DenseMatrix64F matB = new DenseMatrix64F(matA.numCols,matA.numRows);

            long prev = System.currentTimeMillis();

            for( long i = 0; i < numTrials; i++ ) {
                CommonOps.transpose(matA,matB);
            }

            return System.currentTimeMillis() - prev;
        }
    }
}
