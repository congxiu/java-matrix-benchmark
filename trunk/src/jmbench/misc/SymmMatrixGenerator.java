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

package jmbench.misc;

import jmbench.interfaces.MatrixGenerator;
import org.ejml.data.DenseMatrix64F;

import java.util.Random;


/**
 * Create square symmetric matrices with both positive and negative elements from -1 to 1 drawn
 * from a uniform distribution.
 *
 * @author Peter Abeles
 */
public class SymmMatrixGenerator implements MatrixGenerator {
    private Random rand;


    public SymmMatrixGenerator( long seed ) {
        rand = new Random(seed);
    }


    public SymmMatrixGenerator(){
        rand = new Random();
    }

    @Override
    public DenseMatrix64F createMatrix(int numRows, int numCols) {
        DenseMatrix64F A = new DenseMatrix64F(numRows,numCols);

        for( int i = 0; i < numRows; i++ ) {
            for( int j = i; j < numCols ; j++ ) {
                double val = (rand.nextDouble()-0.5)*2;
                A.set(i,j,val);
                A.set(j,i,val);
            }
        }

        return A;
    }

    @Override
    public void setSeed(long randomSeed) {
        rand.setSeed(randomSeed);
    }

    @Override
    public int getMemory(int numRows, int numCols) {
        return (numRows*numCols*8)+50;
    }

    public Random getRand() {
        return rand;
    }

    public void setRand(Random rand) {
        this.rand = rand;
    }
}