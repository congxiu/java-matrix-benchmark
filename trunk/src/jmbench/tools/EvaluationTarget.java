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

import jmbench.impl.MatrixLibrary;
import jmbench.interfaces.LibraryFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;


/**
 * Provides a description of the library that is being processed and a factory for the functions
 * that are being evaluated.
 *
 * @author Peter Abeles
 */
public class EvaluationTarget implements Serializable {

    private MatrixLibrary lib;
    private String factoryPath;

    private transient List<String> jarFiles;

    public EvaluationTarget(){

    }

    public EvaluationTarget( MatrixLibrary lib,
                             String factoryPath )
    {
        this.lib = lib;
        this.factoryPath = factoryPath;
    }

    /**
     * Returns a list of jars that need to be added to the classpath.
     */
    private void loadJarNames() {

        if( lib.dirName == null )
            return;

        List<String> jarNames = new ArrayList<String>();

        File rootDir = new File("lib/"+ lib.dirName);

        File files[] = rootDir.listFiles();
        if( files == null)
            return;

        for( File f : files ) {
            if( !f.isFile() ) continue;

            String n = f.getName();
            if( n.contains("-doc.") || n.contains("-src."))
                continue;

            if( n.contains(".jar") || n.contains(".zip")) {
                jarNames.add(rootDir.getAbsolutePath()+"/"+n);
            }
        }

        this.jarFiles = jarNames;
    }

    public List<String> getJarFiles() {
        return jarFiles;
    }

    /**
     * Creates a new instance of the algorithm factory for this library
     *
     * @return The RuntimePerformanceFactory for this library.
     */
    @SuppressWarnings({"unchecked"})
    public <T extends LibraryFactory> T loadAlgorithmFactory() {
        loadJarNames();

        loadJars(jarFiles);

        try {
            return (T)Class.forName(factoryPath).newInstance();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadJars( List<String> listJars ) {
        if( listJars != null ) {
            for (String name : listJars) {
                try {
                    addURL(new URL("file:" + name));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }

    /**
     * This is a complete hack I found on the internets.  Seems to be the only way I can
     * do what I want to do.
     *
     * @param u What is to be added to the classpath
     * @throws IOException
     */
    public static void addURL(URL u) throws IOException
    {
        Class[] parameters = new Class[]{URL.class};

        if( !(ClassLoader.getSystemClassLoader() instanceof URLClassLoader )) {
            // see http://forums.sun.com/thread.jspa?threadID=300557&start=0&tstart=0
            throw new RuntimeException("NOOOO so they broke the class loader hack, this is bad.");
        }

        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", parameters);
            method.setAccessible(true);
            method.invoke(sysloader, u);
        } catch (Throwable t) {
            t.printStackTrace();
            throw new IOException("Error, could not add URL to system classloader");
        }
    }

    public MatrixLibrary getLib() {
        return lib;
    }

    public void setLib(MatrixLibrary lib) {
        this.lib = lib;
    }

    public String getFactoryPath() {
        return factoryPath;
    }

    public void setFactoryPath(String factoryPath) {
        this.factoryPath = factoryPath;
    }
}


