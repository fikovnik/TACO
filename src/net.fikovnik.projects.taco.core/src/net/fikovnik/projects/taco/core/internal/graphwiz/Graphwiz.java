package net.fikovnik.projects.taco.core.internal.graphwiz;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fikovnik.projects.taco.core.graphwiz.GraphwizException;
import net.fikovnik.projects.taco.core.graphwiz.IGraphwiz;

import org.apache.commons.io.IOUtils;

public final class Graphwiz implements IGraphwiz {

	private static class StreamGobbler extends Thread {
		private final InputStream is;
		private final StringWriter sw = new StringWriter();
		private AtomicBoolean stop = new AtomicBoolean(false);

		public StreamGobbler(InputStream is) {
			this.is = is;
		}

		public void shutdown() {
			this.stop.set(true);
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null && !stop.get()) {
					sw.write(line + "\n");
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(sw);
			}
		}

		@Override
		public String toString() {
			return sw.toString();
		}
	}

	private final String dotPath;

	public Graphwiz(String dotPath) {
		this.dotPath = dotPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.fikovnik.projects.taco.core.internal.graphwiz.IGraphwitz#generatePDF
	 * (java.io.File, java.io.File)
	 */
	@Override
	public void generate(File in, File out, GraphwizOutputType type)
			throws GraphwizException {
		Runtime r = Runtime.getRuntime();
		StreamGobbler stderr = null;
		StreamGobbler stdout = null;

		try {
			Process p = r.exec(String.format("%s -T %s %s -o %s", dotPath,
					type.getFileExtension(), in.getAbsolutePath(),
					out.getAbsolutePath()));

			stderr = new StreamGobbler(p.getErrorStream());
			stderr.start();
			stdout = new StreamGobbler(p.getInputStream());
			stdout.start();

			int exitCode = p.waitFor();

			if (exitCode != 0) {
				throw new GraphwizException("Unable to generate PDF from " + in
						+ ". Dot process exited with " + exitCode
						+ "\nOutput: " + stdout.toString() + "\n"
						+ stderr.toString());
			}
		} catch (IOException e) {
			throw new GraphwizException("Unable to generate PDF from " + in
					+ ".", e);
		} catch (InterruptedException e) {
			throw new GraphwizException("Unable to generate PDF from " + in
					+ ". Process interrupted", e);
		} finally {
			if (stderr != null) {
				stderr.shutdown();
			}
			if (stdout != null) {
				stdout.shutdown();
			}
		}
	}

	private static final Pattern DOT_VERSION = Pattern
			.compile("^dot - graphviz version (\\d+)\\.(\\d+)\\.(\\d+).*$");

	@Override
	public int[] getVersion() throws IOException, InterruptedException,
			GraphwizException {
		ExecutionStatus status = executeDot("-V");
		
		if (status.exitCode != 0) {
			throw new GraphwizException(
					"Unable to determine graphwiz version. Exit code: "
							+ status.exitCode + "\nOutput: " + status.stdout
							+ "\nError: " + status.stderr);
		}
		
		Matcher m = DOT_VERSION.matcher(status.stderr.trim());
		if (!m.matches()) {
			throw new GraphwizException("Unexpected output: "
					+ status.stderr);
		}
		
		return new int[] { Integer.valueOf(m.group(1)).intValue(),
				Integer.valueOf(m.group(2)).intValue(),
				Integer.valueOf(m.group(3)).intValue() };

	}

	private static final class ExecutionStatus {
		int exitCode;
		String stdout;
		String stderr;
	}

	private ExecutionStatus executeDot(String arguments) throws IOException,
			InterruptedException {
		Runtime r = Runtime.getRuntime();
		StreamGobbler stderr = null;
		StreamGobbler stdout = null;

		ExecutionStatus status = new ExecutionStatus();

		try {
			Process p = r.exec(dotPath + " " + arguments);

			stderr = new StreamGobbler(p.getErrorStream());
			stderr.start();
			stdout = new StreamGobbler(p.getInputStream());
			stdout.start();

			status.exitCode = p.waitFor();
		} finally {
			if (stderr != null) {
				stderr.shutdown();
				status.stderr = stderr.toString();
			}
			if (stdout != null) {
				stdout.shutdown();
				status.stdout = stdout.toString();
			}
		}

		return status;
	}

}
