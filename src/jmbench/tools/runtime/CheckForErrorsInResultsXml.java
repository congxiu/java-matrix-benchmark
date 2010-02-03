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

package jmbench.tools.runtime;

import jmbench.tools.OutputError;
import pja.util.UtilXmlSerialization;

import java.io.File;


/**
 * Examines all the results in a directory looking for situations that merit additional attention.
 * It prints an error message whenever something goes wrong.
 *
 * @author Peter Abeles
 */
public class CheckForErrorsInResultsXml {

    File directory;

    public CheckForErrorsInResultsXml( String dir ) {
        directory = new File(dir);

        if( !directory.exists() ) {
            throw new IllegalArgumentException("Directory does not exist");
        }

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("Need to specify a directory");
        }
    }

    @SuppressWarnings({"unchecked"})
    public void examine() {
        String[] files = directory.list();

        for( String nameLevel0 : files ) {
            File level0 = new File(directory.getPath()+"/"+nameLevel0);

            if( level0.isDirectory() ) {
                String []files2 = level0.list();

                System.out.println("Examining "+level0);

                for( String name2 : files2 ) {
                    if( name2.contains(".xml") ) {

                        name2 = level0.getPath()+"/"+name2;

                        OperationResults r = UtilXmlSerialization.deserializeXml(name2);

                        checkForExceptions(r);
                    }
                }
            }

        }

    }

    public void checkForExceptions( OperationResults r ) {
        int numNull = 0;
        int numLargeError = 0;
        int numNaN = 0;
        int numUnknown = 0;

        for( RuntimeEvaluationMetrics metrics : r.getMetrics() ) {
            for( RuntimeResults rr : metrics.getRawResults() ) {
                if( rr.error == null ) {
                   numNull++;
                } else if( rr.error == OutputError.LARGE_ERROR ) {
                    numLargeError++;
                } else if( rr.error == OutputError.UNCOUNTABLE ) {
                    numNaN++;
                } else if( rr.error != OutputError.NO_ERROR ) {
                    numUnknown++;
                }
            }
        }

        if( numLargeError == 0 && numNaN == 0 && numUnknown == 0 ) {
//            if( numNull == 0 && numLargeError == 0 && numNaN == 0 && numUnknown == 0 ) {
            return;
        }

        System.out.print(r.getLibrary()+" "+r.getOpName()+" ");

        if( numNull != 0 ) {
            System.out.printf("null %d",numNull);
        }
        if( numLargeError != 0 ) {
            System.out.printf("large error %d ",numLargeError);
        }
        if( numNaN != 0 ) {
            System.out.printf("NaN %d ",numNaN);
        }
        if( numUnknown != 0 ) {
            System.out.printf("unknown %d ",numUnknown);
        }
        System.out.println();
    }

    public static void main( String args[] ) {
        CheckForErrorsInResultsXml p = new CheckForErrorsInResultsXml("/home/pja/projects/jmatbench/trunk/results/1265160742280");

        p.examine();
    }
}