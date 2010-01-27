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

import jmbench.tools.runtime.RuntimeBenchmarkConfig;
import jmbench.tools.runtime.RuntimeBenchmarkMaster;
import jmbench.tools.stability.StabilityBenchmark;
import jmbench.tools.stability.StabilityBenchmarkConfig;


/**
 * @author Peter Abeles
 */
public class BenchmarkAll {

    public static double SMALL_PERTURBATION = 1e-4;

    public void performBenchmarks( RuntimeBenchmarkConfig runtimeConfig ,
                                   StabilityBenchmarkConfig stabilityConfig ) {

        String directorySave = "results/"+System.currentTimeMillis();

        if( runtimeConfig != null ) {
            RuntimeBenchmarkMaster runtime = new RuntimeBenchmarkMaster(directorySave+"/runtime");

            runtime.performBenchmark(runtimeConfig);
        }

        if( stabilityConfig != null ) {
            StabilityBenchmark stability = new StabilityBenchmark(directorySave+"/stability");

            stability.performBenchmark(stabilityConfig);
        }
    }

    public static void main( String args[] ) {
        RuntimeBenchmarkConfig runtimeConfig = RuntimeBenchmarkConfig.createAllConfig();

        StabilityBenchmarkConfig stabilityConfig = StabilityBenchmarkConfig.createDefault();

        BenchmarkAll master = new BenchmarkAll();

        master.performBenchmarks(runtimeConfig,stabilityConfig);
    }

}
