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

import jmbench.impl.MatrixLibrary;
import jmbench.tools.ResultPlotter;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Creates plots for all the results in a directory
 *
 * @author Peter Abeles
 */
public class PlotRuntimeResultsXml {

    File directory;

    public PlotRuntimeResultsXml( String dir ) {
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

            String fileName = directory.getPath()+"/"+key;

            ResultPlotter.Reference refType = ResultPlotter.Reference.MEDIAN;
            ResultPlotter.relativePlots(l, refType,MatrixLibrary.JAMA,fileName,whichMetric,true,true);
        }
    }

    public static void main( String args[] ) {
        PlotRuntimeResultsXml p = new PlotRuntimeResultsXml("/home/pja/projects/jmatbench/trunk/results/PentiumM_2010_01_31");

        p.plot(RuntimeEvaluationMetrics.METRIC_MAX);
    }
}
