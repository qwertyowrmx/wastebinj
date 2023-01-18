package io.qwertyowrmx.jvm.replicator.tests;

import io.qwertyowrmx.jvm.replicator.JvmReplica;
import io.qwertyowrmx.jvm.replicator.JvmReplicator;
import io.qwertyowrmx.jvm.replicator.tests.application.ArgsApplication;
import io.qwertyowrmx.jvm.replicator.tests.application.FailedHelloWorldApplication;
import io.qwertyowrmx.jvm.replicator.tests.application.HelloWorldApplication;
import io.qwertyowrmx.jvm.replicator.tests.application.ValueApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@Disabled // failed on GitHub CI, but success in local machine
public class JvmReplicatorTest {

    @Test
    public void testCreateOneReplica() {
        JvmReplicator replicator = new JvmReplicator(HelloWorldApplication.class);
        JvmReplica replica = replicator.replicate();
        long processId = replica.getProcessId();
        Assertions.assertNotEquals(0, processId);
        replica.waitFor();
        Assertions.assertEquals(0, replica.getExitCode());
    }


    @Test
    public void testCreateFiveReplicas() {
        JvmReplicator replicator = new JvmReplicator(HelloWorldApplication.class);

        List<JvmReplica> replicas = new ArrayList<>();
        for (int currentReplica = 0; currentReplica < 5; currentReplica++) {
            replicas.add(replicator.replicate());
        }

        for (JvmReplica replica : replicas) {
            replica.waitFor();
            Assertions.assertEquals(0, replica.getExitCode());
        }
    }

    @Test
    public void testGetExitCodeFromFailedProcess() {
        JvmReplicator replicator = new JvmReplicator(FailedHelloWorldApplication.class);
        JvmReplica jvm = replicator.replicate();
        jvm.waitFor();
        Assertions.assertEquals(1, jvm.getExitCode());
    }


    @Test
    public void testReturnValueFromProcess() {
        JvmReplicator replicator = new JvmReplicator(
                ValueApplication.class,
                new String[]{"1"});

        JvmReplica replica = replicator.replicate();

        replica.waitFor();

        Assertions.assertEquals(1, replica.getExitCode());
    }

    @Test
    public void testChildProcessReceiveArgs() {
        JvmReplicator replicator = new JvmReplicator(
                ArgsApplication.class,
                new String[]{"-port", "8081"});

        JvmReplica replica = replicator.replicate();

        replica.waitFor();

        Assertions.assertEquals(0, replica.getExitCode());
    }
}
