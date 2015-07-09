package controllers;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() throws Exception {
		// Necess�rio devido a erro do Play que n�o consegue utilizar a biblioteca correta do javassist.
		javassist.runtime.Desc.useContextClassLoader = true;
	}
}