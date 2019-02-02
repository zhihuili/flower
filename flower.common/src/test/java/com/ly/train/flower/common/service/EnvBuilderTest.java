package com.ly.train.flower.common.service;

import junit.framework.TestCase;

public class EnvBuilderTest extends TestCase {

    public void testBuildEnv() {
        try {
            EnvBuilder.buildEnv(EnvBuilderTest.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}