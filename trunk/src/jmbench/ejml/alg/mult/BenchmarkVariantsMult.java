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

package jmbench.ejml.alg.mult;

import jmbench.interfaces.AlgorithmInterface;
import jmbench.interfaces.MatrixGenerator;
import jmbench.misc.RandomMatrixGenerator;
import jmbench.tools.BenchmarkTools;
import jmbench.tools.runtime.RuntimeEvaluationTest;


/**
 * Compare the different variants of matrix multiplication against different sizes matrices.
 *
 * @author Peter Abeles
 */
public class BenchmarkVariantsMult {


    public static void main( String args[] ) {
        long randSeed = 0x37374;
        int dimens[] = new int[]{2,5,10,20,50,100,200,500,1000,2000,5000};

        long goalTime = 2000;

        AlgorithmInterface algs[] = new AlgorithmInterface[5];
//        algs[0] = new MatrixMult.Small();
        algs[0] = new MatrixMult.Reoder();
        algs[1] = new MatrixMult.Aux();
//        algs[3] = new MatrixMult_D2.Small();
//        algs[4] = new MatrixMult_D2.Aux();
        algs[2] = new MatrixMultTranAB.Small();
        algs[3] = new MatrixMultTranAB.Aux();

        MatrixGenerator gens[] = new MatrixGenerator[3];
        for( int i = 0; i < gens.length; i++ ) {
            gens[i] = new RandomMatrixGenerator();
        }

        BenchmarkTools tools = new BenchmarkTools(4,10,2,null);

        for( int i = 0; i < dimens.length; i++ ) {
            int dimen = dimens[i];
            System.out.println("---- dimen = "+dimen);

            for( int j = 0; j < algs.length; j++ ) {
                AlgorithmInterface alg = algs[j];
                if( alg == null ) continue;

                RuntimeEvaluationTest test = new RuntimeEvaluationTest(dimen,alg,gens,goalTime,randSeed);

//                double opsPerSec = tools.runTest(test).getBest();

//                System.out.printf("%20s ops/sec = %6.3e\n",alg.getName(),opsPerSec);
//                System.out.println(alg.getName()+" ops/sec = "+opsPerSec);
                throw new RuntimeException("Fix this so that it works again");
            }
        }
    }
}
