/*******************************************************************************
 * Copyright (c) 2011 Obeo.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Obeo - initial API and implementation
 *******************************************************************************/
package net.fikovnik.projects.taco.latex;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.StringTokenizer;

import org.eclipse.acceleo.parser.compiler.AbstractAcceleoCompiler;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Monitor;

/**
 * The Acceleo Stand Alone compiler.
 * 
 * @author <a href="mailto:stephane.begaudeau@obeo.fr">Stephane Begaudeau</a>
 * @since 3.1
 */
public class AcceleoCompiler extends AbstractAcceleoCompiler {
    
    /**
     * The entry point of the compilation.
     * 
     * @param args
     *             The arguments used in the compilation: the source folder,
     *             the output folder, a boolean indicating if we should use binary resource
     *             serialization and finally the dependencies of the project.
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Missing parameters"); //$NON-NLS-1$
        }
        AcceleoCompiler acceleoCompiler = new AcceleoCompiler();
        acceleoCompiler.setSourceFolder(args[0]);
        acceleoCompiler.setOutputFolder(args[1]);
        acceleoCompiler.setBinaryResource(Boolean.valueOf(args[2]).booleanValue());
        if (args.length == 4 && args[3] != null && !"".equals(args[3])) { //$NON-NLS-1$
            acceleoCompiler.setDependencies(args[3]);
        }
        acceleoCompiler.doCompile(new BasicMonitor());
    }
    
    @Override
    public void setDependencies(String allDependencies) {
		dependencies.clear();
		StringTokenizer st = new StringTokenizer(allDependencies, ";"); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			String path = st.nextToken().trim();
			if (path.length() > 0) {
				File parent = new Path(path).removeLastSegments(1).toFile();
				if (parent != null && parent.exists() && parent.isDirectory()) {
					String segmentID = new Path(path).lastSegment();
					File[] candidates = parent.listFiles();
					Arrays.sort(candidates, new Comparator<File>() {
						public int compare(File o1, File o2) {
							return -o1.getName().compareTo(o2.getName());
						}
					});
					File bestRequiredFolder = null;
					for (File candidate : candidates) {
						if (candidate.isDirectory() && candidate.getName() != null
								&& candidate.getName().equals(segmentID)) {
							bestRequiredFolder = candidate;
							break;
						}
					}
					if (bestRequiredFolder != null && !dependencies.contains(bestRequiredFolder)) {
						dependencies.add(bestRequiredFolder);
						dependenciesIDs.add(segmentID);
					}
				}
			}
		}
	}
    
    /**
     * Launches the compilation of the mtl files in the generator.
     * 
     * @see org.eclipse.acceleo.parser.compiler.AbstractAcceleoCompiler#doCompile(org.eclipse.emf.common.util.Monitor)
     */
    @Override
    public void doCompile(Monitor monitor) {
        super.doCompile(monitor);
    }
    
    /**
     * Registers the packages of the metamodels used in the generator.
     * 
     * @see org.eclipse.acceleo.parser.compiler.AbstractAcceleoCompiler#registerPackages()
     */
    @Override
    protected void registerPackages() {
        super.registerPackages();
        /*
         * If you want to add the other packages used by your generator, for example UML:
         * org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
         **/
    }

    /**
     * Registers the resource factories.
     * 
     * @see org.eclipse.acceleo.parser.compiler.AbstractAcceleoCompiler#registerResourceFactories()
     */
    @Override
    protected void registerResourceFactories() {
        super.registerResourceFactories();
        /*
         * If you want to add other resource factories, for example if your metamodel uses a specific serialization and it is not contained in a ".ecore" file:
         * org.eclipse.emf.ecore.resource.Resource.Factory.Registry.getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
         **/
    }
}

