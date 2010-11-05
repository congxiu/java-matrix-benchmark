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

import jmbench.impl.MatrixLibrary;
import jmbench.impl.memory.EjmlMemoryFactory;
import jmbench.interfaces.MemoryFactory;
import jmbench.interfaces.MemoryProcessorInterface;
import pja.util.UtilXmlSerialization;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * @author Peter Abeles
 */
public class MemoryBenchmarkLibrary {

    Random rand;

    MatrixLibrary libInfo;
    MemoryConfig config;

    MemoryBenchmarkTools tool = new MemoryBenchmarkTools();

    List<Task> activeTasks = new ArrayList<Task>();

    String directorySave;

    private PrintStream logStream = System.err;

    public MemoryBenchmarkLibrary( MemoryConfig config ,
                                   MemoryFactory factory ,
                                   List<String> jarNames ,
                                   String directorySave ) {
        this.rand = new Random(config.seed);
        this.config = config;
        this.directorySave = directorySave;
        this.libInfo = factory.getLibraryInfo();

        tool.setJars(jarNames);
        tool.setVerbose(false);

        String libraryName = factory.getLibraryInfo().getPlotName();

        if( config.add )
            addOperation(config, factory.add(), "add", libraryName, 3 , config.matrixSize);

        if( config.mult )
            addOperation(config, factory.mult(), "mult", libraryName, 3 , config.matrixSize);

        if( config.solveLinear )
            addOperation(config, factory.solveEq(), "solveLinear", libraryName, 3 , config.matrixSize);

        if( config.solveLS )
            addOperation(config, factory.solveLS(), "solveLS", libraryName,3 , config.matrixSize);

        if( config.svd )
            addOperation(config, factory.svd(), "svd", libraryName,3 , config.matrixSize);

        if( config.eig )
            addOperation(config, factory.eig(), "eig", libraryName,3 , config.matrixSize);

        if( directorySave != null ) {
            setupOutputDirectory();
            setupLog();
        }
    }

    private void addOperation(MemoryConfig config, MemoryProcessorInterface op,
                              String opName , String libraryName ,
                              int scale , int matrixSize ) {
        if( op != null ) {
            activeTasks.add( new Task(op,
                    opName,libraryName,config.maxTestTimeMilli,scale,matrixSize));
        }
    }

    public void process() {
        while( !activeTasks.isEmpty() ) {
            Task task = activeTasks.get( rand.nextInt(activeTasks.size()));

            System.out.println(libInfo.getNameWithVersion()+" operation "+task.results.nameOp);

            boolean remove = false;

            long mem = findMemory(task);

            // if it has a memory result, save it no matter what
            if( mem <= 0 ) {
                logStream.println("Bad memory "+mem+" operation "+task.results.nameOp);
            } else if( tool.isFailed() ) {
                System.out.println("Failed!");
                logStream.println("FAILED: operation "+task.results.nameOp);
                remove = true;
            } else {
                task.results.results.add(mem);
                System.out.println("  memory: "+(mem/1024)+" (KB)");
                if( task.results.results.size() >= config.numTrials ) {
                    remove = true;
                }
            }

            saveResults(task.results);

            if( remove ) {
                activeTasks.remove(task);
            }
        }

        logStream.close();
    }

    private void setupOutputDirectory() {
        File d = new File(directorySave);
        if( !d.exists() ) {
            if( !d.mkdirs() ) {
                throw new IllegalArgumentException("Failed to make output directory");
            }
        } else if( !d.isDirectory())
            throw new IllegalArgumentException("The output directory already exists and is not a directory");
    }

    /**
     * Sets out a file for recording errors.
     */
    private void setupLog() {
        try {
            logStream = new PrintStream(directorySave+"/log.txt");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // For debugging purposes output the slave's classpath
        logStream.println("Current directory = "+new File(".").getAbsolutePath());
        logStream.println("Classpath:");
        logStream.println(tool.getClassPath());
    }

    private void saveResults( MemoryResults results  ) {
        if( directorySave == null )
            return;

        try {
            UtilXmlSerialization.serializeXml(results,directorySave+"/"+results.nameOp+".xml");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public long findMemory( Task task )
    {
        tool.setFrozenDefaultTime(task.timeout);
        tool.setMemory(config.memoryMinMB,config.memoryMaxMB);

        MemoryTest test = new MemoryTest();
        test.setup(task.op,1,task.matrixSize);
        test.setRandomSeed(config.seed);

        return tool.runTest(test);
    }

    private static class Task {
        MemoryProcessorInterface op;
        MemoryResults results;

        long timeout;

        int memoryScale;

        int matrixSize;

        public Task(MemoryProcessorInterface op ,
                    String nameOp , String nameLibrary ,
                    long time ,
                    int memoryScale ,
                    int matrixSize ) {
            this.op = op;
            this.timeout = time;
            results = new MemoryResults();
            results.nameOp = nameOp;
            results.nameLibrary = nameLibrary;
            this.memoryScale = memoryScale;
            this.matrixSize = matrixSize;
        }
    }

    public static void main( String []args ) {
        MemoryConfig config = MemoryConfig.createDefault();

        MemoryBenchmarkLibrary benchmark = new MemoryBenchmarkLibrary(config,
                new EjmlMemoryFactory(),null,null);

        benchmark.process();
    }
}
