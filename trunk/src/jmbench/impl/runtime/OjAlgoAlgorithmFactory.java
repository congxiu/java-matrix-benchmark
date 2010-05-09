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
import jmbench.interfaces.ConfigureLibrary;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.tools.runtime.generator.ScaleGenerator;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SpecializedOps;
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.TransposedStore;

/**
 *
 *
 * @author Peter Abeles
 * @author Anders Peterson (apete)
 */
@SuppressWarnings( { "unchecked" })
public class OjAlgoAlgorithmFactory implements RuntimePerformanceFactory {

    @Override
    public ConfigureLibrary configure() {
        return null;
    }

    public static class OpAdd extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[1]);

            final PhysicalStore<Double> result = FACTORY.makeEmpty(matA.getRowDim(), matA.getColDim());

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                result.fillMatching(matA, PrimitiveFunction.ADD, matB);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpChol extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            MatrixStore<Double> L = null;
            final Cholesky<Double> chol = CholeskyDecomposition.makePrimitive();

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!chol.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                L = chol.getL();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;

            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(L);

            return elapsedTime;
        }
    }

    public static class OpCholInvertSymmPosDef extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            MatrixStore<Double> inverse = null;
            final Cholesky<Double> chol = CholeskyDecomposition.makePrimitive();

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!chol.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                inverse = chol.getInverse();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(inverse);
            CommonOps.transpose(outputs[0]);
            return elapsedTime;
        }
    }

    public static class OpDet extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final LU<Double> lu = LUDecomposition.makePrimitive();

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!lu.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                lu.getDeterminant();
            }

            return System.currentTimeMillis() - prev;
        }
    }

    public static class OpEigSymm extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final Eigenvalue<Double> eig = EigenvalueDecomposition.makePrimitive();

            MatrixStore<Double> D = null;
            MatrixStore<Double> V = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!eig.computeSymmetric(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                D = eig.getD();
                V = eig.getV();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(D);
            outputs[1] = OjAlgoAlgorithmFactory.ojAlgoToEjml(V);
            return elapsedTime;
        }
    }

    public static class OpInvert extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final LU<Double> lu = LUDecomposition.makePrimitive();

            MatrixStore result = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!lu.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                result = lu.getInverse();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpLu extends MyInterface {

        // TODO change to what Anders said
        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final LU<Double> lu = LUDecomposition.makePrimitive();

            MatrixStore<Double> L = null;
            MatrixStore<Double> U = null;
            int pivot[] = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!lu.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }

                L = lu.getL();
                U = lu.getU();
                pivot = lu.getPivotOrder();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;

            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(L);
            outputs[1] = OjAlgoAlgorithmFactory.ojAlgoToEjml(U);
            outputs[2] = SpecializedOps.pivotMatrix(null, pivot, pivot.length, false);

            return elapsedTime;
        }
    }

    public static class OpMult extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[1]);

            final PhysicalStore result = PrimitiveDenseStore.FACTORY.makeEmpty(matA.getRowDim(), matB.getColDim());

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                result.fillByMultiplying(matA, matB);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpMultTransA extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[1]);

            final PhysicalStore result = PrimitiveDenseStore.FACTORY.makeEmpty(matA.getColDim(), matB.getColDim());

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                result.fillByMultiplying(matA.transpose(), matB);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpQr extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final QR<Double> qr = QRDecomposition.makePrimitive();

            MatrixStore<Double> Q = null;
            MatrixStore<Double> R = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!qr.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }

                Q = qr.getQ();
                R = qr.getR();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(Q);
            outputs[1] = OjAlgoAlgorithmFactory.ojAlgoToEjml(R);
            return elapsedTime;
        }
    }

    public static class OpScale extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final PhysicalStore<Double> result = FACTORY.makeEmpty(matA.getRowDim(), matA.getColDim());

            final Double tmpArg = ScaleGenerator.SCALE;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                result.fillMatching(matA, PrimitiveFunction.MULTIPLY, tmpArg);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpSolveExact extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[1]);

            final LU<Double> lu = LUDecomposition.makePrimitive();

            MatrixStore result = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                lu.compute(matA);
                result = lu.solve(matB);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpSolveOver extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);
            final PhysicalStore matB = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[1]);

            final QR<Double> qr = QRDecomposition.makePrimitive();

            MatrixStore result = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                qr.compute(matA);
                result = qr.solve(matB);
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
        }
    }

    public static class OpSvd extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final SingularValue<Double> svd = SingularValueDecomposition.makePrimitive();

            MatrixStore<Double> U = null;
            MatrixStore<Double> S = null;
            MatrixStore<Double> V = null;

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                if (!svd.compute(matA)) {
                    throw new RuntimeException("Decomposition failed");
                }
                U = svd.getQ1();
                S = svd.getD();
                V = svd.getQ2();
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(U);
            outputs[1] = OjAlgoAlgorithmFactory.ojAlgoToEjml(S);
            outputs[2] = OjAlgoAlgorithmFactory.ojAlgoToEjml(V);
            return elapsedTime;
        }
    }

    public static class OpTranspose extends MyInterface {

        public long process(final DenseMatrix64F[] inputs, final DenseMatrix64F[] outputs, final long numTrials) {

            final PhysicalStore matA = OjAlgoAlgorithmFactory.convertToOjAlgo(inputs[0]);

            final PhysicalStore<Double> result = PrimitiveDenseStore.FACTORY.makeEmpty(matA.getColDim(), matA.getRowDim());

            final long prev = System.currentTimeMillis();

            for (long i = 0; i < numTrials; i++) {
                result.fillMatching(new TransposedStore<Double>(matA));
            }

            final long elapsedTime = System.currentTimeMillis() - prev;
            outputs[0] = OjAlgoAlgorithmFactory.ojAlgoToEjml(result);
            return elapsedTime;
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

    public static DenseMatrix64F ojAlgoToEjml(final MatrixStore orig) {
        if (orig == null) {
            return null;
        }

        final DenseMatrix64F ret = new DenseMatrix64F(orig.getRowDim(), orig.getColDim());

        for (int i = 0; i < ret.numRows; i++) {
            for (int j = 0; j < ret.numCols; j++) {
                ret.set(i, j, orig.get(i, j).doubleValue());
            }
        }

        return ret;
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

    @Override
    public AlgorithmInterface invertSymmPosDef() {
        return new OpCholInvertSymmPosDef();
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
}