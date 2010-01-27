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

import jmbench.PackageMatrixConversion;
import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.LibraryAlgorithmFactory;
import org.ejml.data.DenseMatrix64F;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.matrix.PrimitiveMatrix;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;


/**
 * This library caches a lot of the previous results.  Need to carefully find all instance of that.
 *
 * @author Peter Abeles
 */
@SuppressWarnings({"unchecked"})
public class OjAlgoAlgorithmFactory implements LibraryAlgorithmFactory {

   public static class OpAdd extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = convertToOjAlgo(inputs[1]);

            final long prev = System.currentTimeMillis();

            final int tmpRowDim = matA.getRowDim();
            final int tmpColDim = matA.getColDim();

            for (long i = 0; i < numTrials; i++) {
                FACTORY.makeEmpty(tmpRowDim, tmpColDim).fillMatching(matA, PrimitiveFunction.ADD, matB);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpChol extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final Cholesky<Double> chol = CholeskyDecomposition.makePrimitive();

            for (long i = 0; i < numTrials; i++) {
                if (!chol.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpDet extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                // since it caches the previous result you need to make a new primitive matrix each time
                final BasicMatrix basic = new PrimitiveMatrix(matA);
                basic.getDeterminant().getReal();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpEigSymm extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final Eigenvalue<Double> eig = EigenvalueDecomposition.makePrimitive();

            for (long i = 0; i < numTrials; i++) {
                if (!eig.computeSymmetric(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                eig.getD();
                eig.getV();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpInvert extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                // results are cached here.  create a new PrimativeMatrix each time
                BasicMatrix basic = new PrimitiveMatrix(matA);
                basic.invert();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpLu extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final LU<Double> lu = LUDecomposition.makePrimitive();

            for (long i = 0; i < numTrials; i++) {
                if (!lu.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpMult extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = convertToOjAlgo(inputs[1]);

            final long prev = System.currentTimeMillis();

            final BasicMatrix basicA = new PrimitiveMatrix(matA);
            final BasicMatrix basicB = new PrimitiveMatrix(matB);
            for (long i = 0; i < numTrials; i++) {
                basicA.multiplyRight(basicB);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpMultTransA extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = convertToOjAlgo(inputs[1]);

            final long prev = System.currentTimeMillis();

            final BasicMatrix basicA = new PrimitiveMatrix(matA);
            final BasicMatrix basicB = new PrimitiveMatrix(matB);
            for (long i = 0; i < numTrials; i++) {
                basicA.transpose().multiplyRight(basicB);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpQr extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final QR<Double> qr = QRDecomposition.makePrimitive();

            for (long i = 0; i < numTrials; i++) {
                if (!qr.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpScale extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final int tmpRowDim = matA.getRowDim();
            final int tmpColDim = matA.getColDim();

            final Double tmpArg = 3.1;

            for (long i = 0; i < numTrials; i++) {
                FACTORY.makeEmpty(tmpRowDim, tmpColDim).fillMatching(matA, PrimitiveFunction.MULTIPLY, tmpArg);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpSolveExact extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = convertToOjAlgo(inputs[1]);

            final long prev = System.currentTimeMillis();

            final BasicMatrix basicB = new PrimitiveMatrix(matB);
            for (long i = 0; i < numTrials; i++) {
                // Results caching here.  Flush the old results by creating a new primitive matrix.
                BasicMatrix basicA = new PrimitiveMatrix(matA);
                basicA.solve(basicB);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpSolveOver extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = convertToOjAlgo(inputs[1]);

            final long prev = System.currentTimeMillis();

            final BasicMatrix basicB = new PrimitiveMatrix(matB);
            for (long i = 0; i < numTrials; i++) {
                // Results caching here.  Flush the old results by creating a new primitive matrix.
                BasicMatrix basicA = new PrimitiveMatrix(matA);
                basicA.solve(basicB);
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpSvd extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            final SingularValue<Double> svd = SingularValueDecomposition.makePrimitive();

            for (long i = 0; i < numTrials; i++) {
                if (!svd.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                svd.getQ1();
                svd.getD();
                svd.getQ2();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpTranspose extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final long numTrials) {

            final PhysicalStore matA = convertToOjAlgo(inputs[0]);

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                matA.transpose();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    private static abstract class MyInterface implements AlgorithmInterface {

        public String getName() {
            return MatrixLibrary.OJALGO.getVersionName();
        }
    }

    static final PhysicalStore.Factory<Double> FACTORY = PrimitiveDenseStore.FACTORY;

    public static PhysicalStore convertToOjAlgo(final DenseMatrix64F orig) {

        final double[][] raw = PackageMatrixConversion.convertToArray2D(orig);

        return PrimitiveDenseStore.FACTORY.copy(raw);
    }

    public static void main(final String args[]) {

        final MyInterface add = new OpSolveExact();

        final DenseMatrix64F a = new DenseMatrix64F(1000, 1000);
        final DenseMatrix64F b = new DenseMatrix64F(3, 3);

        add.process(new DenseMatrix64F[] { a, b }, 3);
    }

    public AlgorithmInterface add() {
        return new OpAdd();
    }

    public AlgorithmInterface chol() {
        return new OpChol();
    }

    public AlgorithmInterface det() {
        return new OpDet();
    }

    public AlgorithmInterface eigSymm() {
        return new OpEigSymm();
    }

    public AlgorithmInterface invert() {
        return new OpInvert();
    }

    public AlgorithmInterface lu() {
        return new OpLu();
    }

    public AlgorithmInterface mult() {
        return new OpMult();
    }

    public AlgorithmInterface multTransA() {
        return new OpMultTransA();
    }

    public AlgorithmInterface qr() {
        return new OpQr();
    }

    public AlgorithmInterface scale() {
        return new OpScale();
    }

    public AlgorithmInterface solveExact() {
        return new OpSolveExact();
    }

    public AlgorithmInterface solveOver() {
        return new OpSolveOver();
    }

    public AlgorithmInterface svd() {
        return new OpSvd();
    }

    public AlgorithmInterface transpose() {
        return new OpTranspose();
    }

    public static DenseMatrix64F ojAlgoToEjml( MatrixStore orig )
    {
        if( orig == null )
            return null;

        DenseMatrix64F ret = new DenseMatrix64F(orig.getRowDim(),orig.getColDim() );

        for( int i = 0; i < ret.numRows; i++ ) {
            for( int j = 0; j < ret.numCols; j++ ) {
                ret.set(i,j, orig.get(i,j).doubleValue());
            }
        }

        return ret;
    }
}