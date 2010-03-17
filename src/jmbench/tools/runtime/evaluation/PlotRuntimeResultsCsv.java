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

import jmbench.tools.ResultPlotter;
import jmbench.tools.runtime.OperationResults;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Read in CSV files and plot the results.
 *
 * @author Peter Abeles
 */
public class PlotRuntimeResultsCsv {

    File directory;

    // should it include native libraries while plotting results
    boolean plotNativeLibraries = true;

    public PlotRuntimeResultsCsv( String dir ) {
        directory = new File(dir);

        if( !directory.exists() ) {
            throw new IllegalArgumentException("Directory does not exist.");
        }

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("Need to specify a directory.");
        }
    }

    @SuppressWarnings({"unchecked"})
    public void plot() throws FileNotFoundException {
        String[] files = directory.list();

        Map<String, List> opMap = new HashMap<String,List>();

        for( String nameLevel0 : files ) {
            if( !nameLevel0.contains(".csv"))
                continue;

            String opName = nameLevel0.split(".")[0];

            List<Integer>  matrixDimen = new ArrayList<Integer>();

            InputStream is = new FileInputStream(directory.getAbsolutePath()+"/"+nameLevel0);

            // read in list of libraries processed

            // go through line by line reading in results

            // create dummy

        }
        for( String key : opMap.keySet() ) {
            List<OperationResults> l = opMap.get(key);

            String fileNameVar = directory.getPath()+"/plots/variability/"+key;
            String fileNameRel = directory.getPath()+"/plots/relative/"+key;
            String fileNameAbs = directory.getPath()+"/plots/absolute/"+key;

            ResultPlotter.Reference refType = ResultPlotter.Reference.MAX;
            ResultPlotter.variabilityPlots(l, fileNameVar,true,false);
            ResultPlotter.relativePlots(l, refType,null,fileNameRel,0,true,true);
            ResultPlotter.absolutePlots(l, fileNameAbs,0,true,false);
        }
    }

    public static void main( String args[] ) throws FileNotFoundException {

        String inputDirectory = args.length == 0 ? PlotRuntimeResultsXml.findMostRecentDirectory() : args[0];


        PlotRuntimeResultsCsv p = new PlotRuntimeResultsCsv(inputDirectory);

        p.plot();
    }
}