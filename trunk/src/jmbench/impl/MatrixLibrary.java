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
    public static final MatrixLibrary EJML = new MatrixLibrary("EJML","ejml","ejml","EJML 0.11","2010-04-07",false,false,0);
    public static final MatrixLibrary JAMA = new MatrixLibrary("JAMA","jama","jama","Jama 1.0.2","",false, false, 1);
    public static final MatrixLibrary MTJ = new MatrixLibrary("MTJ","mtj","mtj","MTJ 0.9.12","",true, false, 2);
    public static final MatrixLibrary SEJML = new MatrixLibrary("SEJML","sejml","sejml","SEJML 0.7","",true, false, 3);
    public static final MatrixLibrary CM = new MatrixLibrary("CommMath","commons-math","commons-math","Commons Math 2.1","2010-04-05",true, false, 4);
    public static final MatrixLibrary JSCIENCE = new MatrixLibrary("JScience","jscience","jscience","JScience 4.3","",true, false, 5);
    public static final MatrixLibrary OJALGO = new MatrixLibrary("ojAlgo","ojalgo","ojalgo","ojAlgo 29.0","2010-03-20",true, false, 6);
    public static final MatrixLibrary COLT = new MatrixLibrary("Colt","colt","colt","Colt 1.2","",true, false, 7);
    public static final MatrixLibrary PCOLT = new MatrixLibrary("PColt","parallelcolt","parallelcolt","Parallel Colt 0.9.4","2010-03-20",true, false, 8);
    public static final MatrixLibrary UJMP = new MatrixLibrary("UJMP","ujmp","ujmp","UJMP","2010-04-28",true, true, 9);
    public static final MatrixLibrary UJMP_JAVA = new MatrixLibrary("UJMP-J","ujmp","ujmp-java","UJMP-Java","2010-04-28",true,false, 11);
    public static final MatrixLibrary JBLAS = new MatrixLibrary("JBLAS","jblas","jblas","JBLAS 1.0.2","2010-02-26",true, true, 10);

    public String plotName;
    public String versionName;
    // directory that it loads its libraries from
    public String libraryDirName;
    // directory the results are saved into
    public String saveDirName;
    // the date the library was last updated
    public String dateModified;
    // does the slave need to load additional libraries
    public boolean extraLibs;
    // if the library is native or not
    public boolean nativeCode;

    // when plotted what color and stroke should be used
    public int plotLineType;

    public MatrixLibrary(String plotName, String libraryDirName, String saveDirName ,
                         String versionName, String dateModified,
                         boolean extraLibs, boolean nativeCode, int plotLineType)
    {
        this.plotName = plotName;
        this.versionName = versionName;
        this.libraryDirName = libraryDirName;
        this.saveDirName = saveDirName;
        this.dateModified = dateModified;
        this.extraLibs = extraLibs;
        this.nativeCode = nativeCode;
        this.plotLineType = plotLineType;
    }

    public MatrixLibrary() {

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

    public String getLibraryDirName() {
        return libraryDirName;
    }

    public void setLibraryDirName(String libraryDirName) {
        this.libraryDirName = libraryDirName;
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

    public String getSaveDirName() {
        return saveDirName;
    }

    public void setSaveDirName(String saveDirName) {
        this.saveDirName = saveDirName;
    }
}
