package com.ly.train.flower.common.service;

import junit.framework.TestCase;

public class EnvBuilderTest extends TestCase {

    public void testBuildEnv() {
        EnvBuilder envBuilder = new EnvBuilder();
        try {
            envBuilder.buildEnv();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}