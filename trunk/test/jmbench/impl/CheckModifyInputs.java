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

package jmbench.impl;

import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.MatrixGenerator;
import jmbench.interfaces.RuntimePerformanceFactory;
import jmbench.misc.PosDefSymGenerator;
import jmbench.misc.RandomMatrixGenerator;
import org.ejml.data.DenseMatrix64F;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Makes sure the function does not modify the inputs
 *
 * @author Peter Abeles
 */
// TODO have all these algs write their output back into the EJML matrix so this test does something usefull.
public class CheckModifyInputs {
    AlgorithmInterface alg;
    int numInputs;

    MatrixGenerator gen;



    public static void checkFactory( RuntimePerformanceFactory factory ) {
        List<AlgorithmInterface> operation = new ArrayList<AlgorithmInterface>();

        operation.add(factory.det());
        operation.add(factory.invert());
        operation.add(factory.lu());
        operation.add(factory.qr());
        operation.add(factory.svd());
        operation.add(factory.scale());
        CheckModifyInputs.checkList(operation,1);

        operation.clear();
        operation.add(factory.chol());
        CheckModifyInputs.checkListSPD(operation,1);

        operation.clear();
        operation.add( factory.add() );
        operation.add( factory.mult() );
        operation.add( factory.multTransA() );
        operation.add( factory.solveExact() );
        operation.add( factory.solveOver() );
        CheckModifyInputs.checkList(operation,2);
    }

    public static void checkList( List<AlgorithmInterface> algs , int numInputs )
    {
        for(AlgorithmInterface a : algs ) {
            if( a == null ) continue;

            CheckModifyInputs check = new CheckModifyInputs(a,numInputs, new RandomMatrixGenerator(0xff3));
            check.checkAll();
        }
    }

    public static void checkListSPD( List<AlgorithmInterface> algs , int numInputs )
    {
        for(AlgorithmInterface a : algs ) {
            if( a == null ) continue;
            
            CheckModifyInputs check = new CheckModifyInputs(a,numInputs, new PosDefSymGenerator(4435));
            check.checkAll();
        }
    }

    public CheckModifyInputs( AlgorithmInterface alg , int numInputs , MatrixGenerator gen ) {
        this.gen = gen;
        this.alg = alg;
        this.numInputs = numInputs;
    }

    public void checkAll() {
        DenseMatrix64F[] orig = new DenseMatrix64F[numInputs];
        DenseMatrix64F[] inputs = new DenseMatrix64F[numInputs];

        for( int i = 0; i < numInputs; i++ ) {
            orig[i] = gen.createMatrix(4,4);
            inputs[i] = orig[i].copy();
        }

        alg.process(inputs, null, 12);

        for( int i = 0; i < numInputs; i++ ) {
            checkEquals(orig[i],inputs[i],1e-8);
        }
    }

    public static void checkEquals( DenseMatrix64F matA , DenseMatrix64F matB , double tol )
    {
        assertEquals(matA.numCols,matB.numCols);
        assertEquals(matA.numRows,matB.numRows);

        int size = matA.getNumElements();

        for( int i = 0; i < size; i++ ) {
            assertEquals(matA.data[i],matB.data[i],tol);
        }
    }
}
