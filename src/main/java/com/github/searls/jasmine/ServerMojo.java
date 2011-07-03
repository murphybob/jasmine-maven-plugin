package com.github.searls.jasmine;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;

import com.github.searls.jasmine.io.RelativizesFilePaths;
import com.github.searls.jasmine.server.JasmineResourceHandler;

/**
 * @goal server
 * @execute lifecycle="jasmine-lifecycle" phase="generate-sources"
 */
public class ServerMojo extends AbstractJasmineMojo {

	private Server server = new Server();
	
	private RelativizesFilePaths relativizesFilePaths = new RelativizesFilePaths();
	
	@Override
	public void run() throws Exception {
		SelectChannelConnector connector = new SelectChannelConnector();
        connector.setPort(serverPort);
        server.addConnector(connector);
        
        ResourceHandler resourceHandler = new JasmineResourceHandler(this);
        resourceHandler.setDirectoriesListed(true);       
        resourceHandler.setWelcomeFiles(new String[]{ manualSpecRunnerPath() });
        resourceHandler.setResourceBase(mavenProject.getBasedir().getAbsolutePath());

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, new DefaultHandler() });
        server.setHandler(handlers);

        server.start();
        getLog().info("\n\n" +
				"Server started--it's time to spec some JavaScript! You can run your specs as you develop by visiting this URL in a web browser: \n\n\t" +
				"http://localhost:"+serverPort+
				"\n\n" +
				"Just leave this process running as you test-drive your code, refreshing your browser window to re-run your specs. You can kill the server with Ctrl-C when you're done.");
        
		server.join();
	}

	private String manualSpecRunnerPath() throws IOException {
		return relativizesFilePaths.relativize(mavenProject.getBasedir(), jasmineTargetDir) + File.separator +manualSpecRunnerHtmlFileName;
	}

}
