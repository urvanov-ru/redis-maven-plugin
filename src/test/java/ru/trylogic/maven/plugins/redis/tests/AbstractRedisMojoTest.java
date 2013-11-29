package ru.trylogic.maven.plugins.redis.tests;

import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import redis.clients.jedis.Jedis;

import java.io.File;

abstract public class AbstractRedisMojoTest extends AbstractMojoTestCase {

    public static final String TEST_KEY = "testKey";
    public static final String TEST_VALUE = "testValue";
    
    protected <T> T lookupRedisMojo(String file, String mojo) throws Exception {
        File pomFile = getTestFile(file);
        assertNotNull(pomFile);
        assertTrue(pomFile.exists());

        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(pomFile, buildingRequest).getProject();
        
        return (T) lookupConfiguredMojo(project, mojo);
    }

    protected void testConnectionDown(Jedis jedis) {
        try {
            jedis.ping();
            fail();
        } catch (Throwable ignored) {}
    }
    
    protected void waitUntilConnect(Jedis jedis) throws InterruptedException {
        int attempts = 20;
        while(true) {
            if(--attempts <= 0) {
                fail();
            }

            try {
                jedis.ping();
                break;
            } catch (Throwable ignored) {
                Thread.sleep(50);
            }
        }
    }
}
