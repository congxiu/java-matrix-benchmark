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

package jmbench.tools.runtime.evaluation;

import jmbench.tools.runtime.OperationResults;
import jmbench.tools.runtime.RuntimeEvaluationMetrics;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Converts results from xml format into a CSV (column space value) format.
 *
 * @author Peter Abeles
 */
public class ConvertRuntimeResultsXmlToCSV {

    File directory;

    public ConvertRuntimeResultsXmlToCSV( String dir ) {
        directory = new File(dir);

        if( !directory.exists() ) {
            throw new IllegalArgumentException("Directory does not exist");
        }

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("Need to specify a directory");
        }
    }

    @SuppressWarnings({"unchecked"})
    public void plot(int whichMetric) {
        String[] files = directory.list();

        Map<String, List> opMap = new HashMap<String,List>();

        for( String nameLevel0 : files ) {
            File level0 = new File(directory.getPath()+"/"+nameLevel0);

            if( level0.isDirectory() ) {
                String []files2 = level0.list();

                for( String name2 : files2 ) {
                    if( name2.contains(".xml") ) {

                        String stripName = name2.substring(0,name2.length()-4);
                        name2 = level0.getPath()+"/"+name2;

                        OperationResults r = UtilXmlSerialization.deserializeXml(name2);

                        List l;
                        if( opMap.containsKey(stripName) ) {
                            l = opMap.get(stripName);
                        } else {
                            l = new ArrayList();
                            opMap.put(stripName,l);
                        }
                        l.add(r);
                    }
                }
            }

        }
        for( String key : opMap.keySet() ) {
            List<OperationResults> l = opMap.get(key);

            String fileName = directory.getPath()+"/"+key+".csv";
            System.out.println("Writing file: "+fileName);
            try {
                PrintStream fileStream = new PrintStream(new FileOutputStream(fileName));
                printResults(fileStream,l,whichMetric,key);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }


        }
    }

    private void printResults( PrintStream fileStream,
                               List<OperationResults> results ,
                               int metricType ,
                               String opName) {


        fileStream.println("# Operation: "+opName);
        fileStream.println("# Metric:    "+metricType);
        fileStream.print("#");
        for( OperationResults r : results ) {
            fileStream.print("\t"+r.getLibrary().getPlotName());
        }
        fileStream.println();

        int matrixSize[] = findMaxMatrixSize(results);

        for( int indexMatrix = 0; indexMatrix < matrixSize.length; indexMatrix++ ) {
            int s = matrixSize[indexMatrix];

            // print the size of this matrix
            fileStream.print(s);

            // print results for each library at this size
            for( OperationResults r : results ) {
//                fileStream.print(r.getLibrary());

                // sanity check
                if( r.getMatDimen()[indexMatrix] != s ) {
                    throw new RuntimeException("Matrix size miss match");
                }

                RuntimeEvaluationMetrics m[] = r.getMetrics();

                if( m.length > indexMatrix && m[indexMatrix] != null )
                    fileStream.print("\t"+m[indexMatrix].getMetric(metricType));
                else
                    fileStream.print("\t"+Double.NaN);

            }
            fileStream.println();
        }
        fileStream.println();

    }

    private int[] findMaxMatrixSize( List<OperationResults> results) {
        int max = 0;
        int arrayMax[] = null;

        for( OperationResults r : results ) {
            int d[] = r.matDimen;

            if( d.length > max ) {
                max = d.length;
                arrayMax = d;
            }
        }

        return arrayMax;
    }

    public static void main( String args[] ) {

        String dir = args.length == 0 ? PlotRuntimeResultsXml.findMostRecentDirectory() : args[0];

        ConvertRuntimeResultsXmlToCSV p = new ConvertRuntimeResultsXmlToCSV(dir);

        p.plot(RuntimeEvaluationMetrics.METRIC_MAX);

        System.out.println("Done");
    }
}