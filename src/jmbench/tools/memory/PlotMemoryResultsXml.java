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

package jmbench.tools.memory;

import jmbench.plots.MemoryRelativeBarPlot;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Peter Abeles
 */
public class PlotMemoryResultsXml {

    File directory;
    boolean displayResults = true;

    public PlotMemoryResultsXml( String dir ) {
        directory = new File(dir);

        if( !directory.exists() ) {
            throw new IllegalArgumentException("Directory does not exist.");
        }

        if( !directory.isDirectory() ) {
            throw new IllegalArgumentException("Need to specify a directory.");
        }
    }

    public void plot() {
        Map<String, List> opMap = parseResults();

        plotResults(opMap);
    }

    private Map<String, List> parseResults() {
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

                        MemoryResults r;
                        try {
                            r = UtilXmlSerialization.deserializeXml(name2);
                        } catch( ClassCastException e ) {
                            System.out.println("Couldn't deserialize "+name2);
                            continue;
                        }

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
        return opMap;
    }

    private void plotResults(  Map<String, List> opMap ) {
        MemoryRelativeBarPlot plot = new MemoryRelativeBarPlot("Relative Memory");

        for( String key : opMap.keySet() ) {
            List<MemoryResults> l = opMap.get(key);

            MemoryPlotData plotData = convertToPlotData(l);

            int N = l.size();

            for( int i = 0; i < N; i++ ) {
                plot.addResult(key,plotData.libNames[i],plotData.memory[i]);
            }
        }

        plot.displayWindow(600,300);
    }

    private MemoryPlotData convertToPlotData( List<MemoryResults> l ) {
        long max = 0;

        for( MemoryResults m : l ) {
            long d = m.getMinimumMemory();
            if( max < d )
                max = d;
        }

        MemoryPlotData data = new MemoryPlotData(l.size());

        for( int i = 0; i < l.size(); i++ ) {
            MemoryResults m = l.get(i);

            data.libNames[i] = m.getNameLibrary();
            data.memory[i] = (double)m.getMinimumMemory()/(double)max;
        }

        return data;
    }

    public static void main( String args[] ) {
        String dir = "/home/pja/projects/jmatbench/trunk/results/1272326136260";

        PlotMemoryResultsXml plotter = new PlotMemoryResultsXml(dir);

        plotter.plot();
    }
}
