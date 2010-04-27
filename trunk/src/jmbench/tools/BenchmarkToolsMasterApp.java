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

package jmbench.tools;

import jmbench.tools.memory.MemoryBenchmark;
import jmbench.tools.runtime.RuntimeBenchmarkMaster;
import jmbench.tools.runtime.evaluation.CheckForErrorsInResultsXml;
import jmbench.tools.runtime.evaluation.PlotRuntimeResultsXml;
import jmbench.tools.stability.StabilityBenchmark;

import java.io.IOException;


/**
 * An application for running all the benchmark related tools
 *
 * @author Peter Abeles
 */
public class BenchmarkToolsMasterApp {

    public static double SMALL_PERTURBATION = 1e-8;

    public static void printHelp() {
        System.out.println("<tool> <tool arguments>");
        System.out.println("The first argument specifies which tool is to be run:" +
                " <stability|runtime|memory|checkRuntime|plotRuntime|displayStability>");
        System.out.println();
        System.out.println("For runtime tool:");
        RuntimeBenchmarkMaster.printHelp();
        System.out.println("For runtime plot tool:");
        PlotRuntimeResultsXml.printHelp();
        System.out.println("For runtime check tool:");
        CheckForErrorsInResultsXml.printHelp();
        System.exit(0);
    }

    public static void main( String args[] ) throws IOException, InterruptedException {

        if( args.length == 0 ) {
            printHelp();
        }

        String tool = args[0];

        String[] pruned = new String[args.length-1];
        System.arraycopy(args,1,pruned,0,pruned.length);

        if( tool.compareToIgnoreCase("runtime") == 0) {
            RuntimeBenchmarkMaster.main(pruned);
        } else if( tool.compareToIgnoreCase("stability") == 0) {
            StabilityBenchmark.main(pruned);
        } else if( tool.compareToIgnoreCase("memory") == 0 ) {
            MemoryBenchmark.main(pruned);
        } else if( tool.compareToIgnoreCase("checkRuntime") == 0) {
            CheckForErrorsInResultsXml.main(pruned);    
        } else if( tool.compareToIgnoreCase("plotRuntime") == 0) {
            PlotRuntimeResultsXml.main(pruned);
        } else if( tool.compareToIgnoreCase("displayStability") == 0) {
            System.out.println("Not supported yet "+tool);
        } else {
            System.out.println("Unknown tool '"+args[0]+"'");
        }
    }

}
