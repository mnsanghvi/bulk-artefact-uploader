/**
 * run as java Importer [dirtosearch]
 **/
public class Importer {
    private String directory;
    private final static String repositoryId = "repo1";
    private final static String url = "http://remoteserver/nexus/content/repositories/repo1/";
    private final static String mvn = "C:/java/apache-maven-3.0.3/bin/mvn.bat";
 
    /**
     * @param directory
     */
    public Importer(String directory) {
	super();
	this.directory = directory;
    }
 
    public static void main(String[] args) {
	Importer importer = new Importer(args[0]);
	importer.go();
    }
 
    private void go() {
	try {
	    File dir = new File(directory);
 
	    doDir(dir);
	}
	catch (Throwable e) {
	    e.printStackTrace();
	}
    }
 
    private void doDir(File dir) throws IOException, InterruptedException {
	File[] listFiles = dir.listFiles(new PomFilter());
	if (listFiles != null) {
	    for (File pom : listFiles) {
		doPom(pom);
	    }
	}
 
	File[] listDirs = dir.listFiles(new DirFilter());
	if (listDirs != null) {
	    for (File subdir : listDirs) {
		doDir(subdir);
	    }
	}
    }
 
    private void doPom(File pom) throws IOException, InterruptedException {
	File base = pom.getParentFile();
	String fileName = pom.getName();
	String jarName = fileName.substring(0, fileName.length() - 3) + "jar";
	File jar = new File(base.getAbsolutePath() + "/" + jarName);
 
	String exec = mvn + " deploy:deploy-file -DrepositoryId=" + repositoryId + " -Durl=" + url;
	if (jar.exists()) {
	    exec += " -Dfile=\"" + jar.getAbsolutePath() + "\"";
	}
	else {
	    exec += " -Dfile=\"" + pom.getAbsolutePath() + "\"";
	}
	exec += " -DpomFile=\"" + pom.getAbsolutePath() + "\"";
	exec(exec);
 
    }
 
    private void exec(String exec) throws InterruptedException, IOException {
	System.out.println(exec);
	Process p = Runtime.getRuntime().exec(exec);
	String line;
	BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
	BufferedReader bre = new BufferedReader(new InputStreamReader(p.getErrorStream()));
	while ((line = bri.readLine()) != null) {
	    System.out.println(line);
	}
	bri.close();
	while ((line = bre.readLine()) != null) {
	    System.out.println(line);
	}
	bre.close();
	p.waitFor();
	System.out.println("Done.");
    }
 
    private class PomFilter implements java.io.FileFilter {
 
	@Override
	public boolean accept(File pathname) {
	    return pathname.getName().endsWith(".pom");
	}
    }
 
    private class DirFilter implements java.io.FileFilter {
 
	@Override
	public boolean accept(File pathname) {
	    return pathname.isDirectory();
	}
    }
}
