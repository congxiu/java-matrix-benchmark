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
import org.ojalgo.function.implementation.PrimitiveFunction;
import org.ojalgo.matrix.decomposition.*;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;

import java.util.Random;


/**
 * @author Peter Abeles
 */
public class OjAlgoMemoryFactory implements MemoryFactory {


    @Override
    public MatrixLibrary getLibraryInfo() {
        return MatrixLibrary.OJALGO;
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
            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(size,size);
            PhysicalStore B = PrimitiveDenseStore.FACTORY.makeZero(size,size);
            PhysicalStore C = PrimitiveDenseStore.FACTORY.makeZero(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                C.fillByMultiplying(A, B);
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
            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(size,size);
            PhysicalStore B = PrimitiveDenseStore.FACTORY.makeZero(size,size);
            PhysicalStore C = PrimitiveDenseStore.FACTORY.makeZero(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    B.set(i,j,rand.nextDouble());
                }
            }

            for( int i = 0; i < numCycles; i++ )
                C.fillMatching(A, PrimitiveFunction.ADD, B);
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
            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(size,size);
            PhysicalStore y = PrimitiveDenseStore.FACTORY.makeZero(size,1);

            for( int i = 0; i < size; i++ ) {
                for( int j = 0; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            final LU<Double> lu = LUDecomposition.makePrimitive();

            for( int i = 0; i < numCycles; i++ ){
                lu.compute(A);
                lu.solve(y);
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

            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(numRows,numCols);
            PhysicalStore y = PrimitiveDenseStore.FACTORY.makeZero(numRows,1);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
                y.set(i,0,rand.nextDouble());
            }

            final QR<Double> qr = QRDecomposition.makePrimitive();

            for( int i = 0; i < numCycles; i++ ){
                qr.compute(A);
                qr.solve(y);
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
            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(numRows,numCols);

            for( int i = 0; i < numRows; i++ ) {
                for( int j = 0; j < numCols; j++ ) {
                    A.set(i,j,rand.nextDouble());
                }
            }

            final SingularValue<Double> svd = SingularValueDecomposition.makePrimitive();

            for( int i = 0; i < numCycles; i++ ) {
                if (!svd.compute(A)) {
                    throw new RuntimeException("Decomposition failed");
                }
                svd.getQ1();
                svd.getD();
                svd.getQ2();
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
            PhysicalStore A = PrimitiveDenseStore.FACTORY.makeZero(size,size);

            for( int i = 0; i < size; i++ ) {
                for( int j = i; j < size; j++ ) {
                    A.set(i,j,rand.nextDouble());
                    A.set(j,i,A.get(i,j));
                }
            }

            final Eigenvalue<Double> eig = EigenvalueDecomposition.makePrimitive();

            for( int i = 0; i < numCycles; i++ ) {
                if (!eig.computeSymmetric(A)) {
                    throw new RuntimeException("Decomposition failed");
                }
                eig.getD();
                eig.getV();
            }
        }
    }
}