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


/**
 * @author Peter Abeles
 */
public enum MatrixLibrary {
    EJML("EJML","ejml","EJML 0.8","2010-01-20",false,false),
    JAMA("JAMA","jama-1.0.2","Jama 1.0.2","",false,false),
    MTJ("MTJ","mtj-0.9.12","MTJ 0.9.12","",false,true),
    SEJML("SEJML","sejml","SEJML 0.7","",false,true),
    CM("CommMath","commons-math-2.0","Commons Math 2.0","",false,true),
    JSCIENCE("JScience","jscience-4.3","JScience 4.3","",true,true),
    OJALGO("ojAlgo","ojalgo-28.31","ojAlgo 28.31","2010-01-20",true,true),
    COLT("Colt","colt-1.2","Colt 1.2","",true,true),
    PCOLT("PColt","parallelcolt-0.9.2","Parallel Colt 0.9.2","2010-01-24",true,true),
    UJMP("UJMP","ujmp-svn","UJMP svn","2010-01-20",true,true);

    public String plotName;
    public String versionName;
    public String dirName;
    // the date the library was last updated
    public String dateModified;
    public boolean multThread;
    // does the slave need to load additional libraries
    public boolean extraLibs;

    MatrixLibrary( String plotName , String dirName ,String versionName , String dateModified,
                   boolean multThread , boolean extraLibs )
    {
        this.plotName = plotName;
        this.versionName = versionName;
        this.dirName = dirName;
        this.dateModified = dateModified;
        this.multThread = multThread;
        this.extraLibs = extraLibs;
    }

    public String getPlotName() {
        return plotName;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getDirName() {
        return dirName;
    }

    public String getDateModified() {
        return dateModified;
    }

    public boolean isMultThread() {
        return multThread;
    }

    public boolean isExtraLibs() {
        return extraLibs;
    }
}
