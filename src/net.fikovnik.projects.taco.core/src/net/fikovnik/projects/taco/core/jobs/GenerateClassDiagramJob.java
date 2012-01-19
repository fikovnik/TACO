package net.fikovnik.projects.taco.core.jobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.fikovnik.projects.taco.core.gen.main.ClassDiagram;
import net.fikovnik.projects.taco.core.graphwiz.GraphwizException;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwizService;
import net.fikovnik.projects.taco.core.util.PlatformUtil;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.ecore.EPackage;

public final class GenerateClassDiagramJob extends Job {

	private static final String OUTPUT_FILE_NAME = "diagram.dot";
	private final EPackage pkg;
	private final File target;
	private IGraphwizService graphwizService;

	public GenerateClassDiagramJob(EPackage pkg, File target,
			IGraphwizService graphwizService) {
		super("Generate Class diagram");

		this.pkg = pkg;
		this.target = target;
		this.graphwizService = graphwizService;

		setPriority(Job.LONG);
		setUser(true);
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			doRun(monitor);
		} catch (IOException e) {
			return PlatformUtil.createError("Unable to save class diagram", e,
					null);
		} catch (GraphwizException e) {
			return PlatformUtil.createError("Unable to save class diagram", e,
					null);
		}

		return Status.OK_STATUS;
	}

	private void doRun(IProgressMonitor monitor) throws IOException,
			GraphwizException {
		File tmpFolder = PlatformUtil.createTempDir();

		monitor.beginTask("Creating class diagram", 1);

		List<Object> arguments = new ArrayList<Object>();
		arguments.add(OUTPUT_FILE_NAME);

		ClassDiagram diagram = new ClassDiagram(pkg, tmpFolder, arguments);
		diagram.generate(BasicMonitor.toMonitor(monitor));

		IGraphwiz graphwiz = graphwizService.getGraphwiz();
		File in = new File(tmpFolder, OUTPUT_FILE_NAME);

		graphwiz.generate(in, target);

		FileUtils.deleteDirectory(tmpFolder);
		
		monitor.done();
	}

	public File getTarget() {
		return target;
	}

}
