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

package jmbench.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Information on a linear algebra library, such as; its name, where its located, and how to load it.
 *
 * @author Peter Abeles
 */
public class MatrixLibrary implements Serializable {
    public static final MatrixLibrary EJML = new MatrixLibrary("EJML","ejml","EJML 0.10.1a","2010-02-21",false,false,0);
    public static final MatrixLibrary JAMA = new MatrixLibrary("JAMA","jama-1.0.2","Jama 1.0.2","",false, false, 1);
    public static final MatrixLibrary MTJ = new MatrixLibrary("MTJ","mtj-0.9.12","MTJ 0.9.12","",true, false, 2);
    public static final MatrixLibrary SEJML = new MatrixLibrary("SEJML","sejml","SEJML 0.7","",true, false, 3);
    public static final MatrixLibrary CM = new MatrixLibrary("CommMath","commons-math-2.1a","Commons Math 2.1a","",true, false, 4);
    public static final MatrixLibrary JSCIENCE = new MatrixLibrary("JScience","jscience-4.3","JScience 4.3","",true, false, 5);
    public static final MatrixLibrary OJALGO = new MatrixLibrary("ojAlgo","ojalgo-29.0","ojAlgo 29.0","2010-03-20",true, false, 6);
    public static final MatrixLibrary COLT = new MatrixLibrary("Colt","colt-1.2","Colt 1.2","",true, false, 7);
    public static final MatrixLibrary PCOLT = new MatrixLibrary("PColt","parallelcolt-0.9.4","Parallel Colt 0.9.4","2010-03-20",true, false, 8);
    public static final MatrixLibrary UJMP = new MatrixLibrary("UJMP","ujmp-svn","UJMP svn","2010-03-02",true, true, 9);
    public static final MatrixLibrary JBLAS = new MatrixLibrary("JBLAS","jblas-1.0.2","JBLAS 1.0.2","2010-02-26",true, true, 10);

    public String plotName;
    public String versionName;
    public String dirName;
    // the date the library was last updated
    public String dateModified;
    // does the slave need to load additional libraries
    public boolean extraLibs;
    // if the library is native or not
    public boolean nativeCode;

    // when plotted what color and stroke should be used
    public int plotLineType;

    public MatrixLibrary(String plotName, String dirName, String versionName, String dateModified,
                         boolean extraLibs, boolean nativeCode, int plotLineType)
    {
        this.plotName = plotName;
        this.versionName = versionName;
        this.dirName = dirName;
        this.dateModified = dateModified;
        this.extraLibs = extraLibs;
        this.nativeCode = nativeCode;
        this.plotLineType = plotLineType;
    }

    public MatrixLibrary() {

    }

    public static List<MatrixLibrary> getAll() {
        List<MatrixLibrary> all = new ArrayList<MatrixLibrary>();

        all.add(EJML);
        all.add(JAMA);
        all.add(MTJ);
        all.add(SEJML);
        all.add(CM);
        all.add(JSCIENCE);
        all.add(OJALGO);
        all.add(COLT);
        all.add(PCOLT);
        all.add(UJMP);

        return all;
    }

    public String getPlotName() {
        return plotName;
    }

    public void setPlotName(String plotName) {
        this.plotName = plotName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public boolean isExtraLibs() {
        return extraLibs;
    }

    public void setExtraLibs(boolean extraLibs) {
        this.extraLibs = extraLibs;
    }

    public int getPlotLineType() {
        return plotLineType;
    }

    public void setPlotLineType(int plotLineType) {
        this.plotLineType = plotLineType;
    }

    public boolean isNativeCode() {
        return nativeCode;
    }

    public void setNativeCode(boolean nativeCode) {
        this.nativeCode = nativeCode;
    }
}
