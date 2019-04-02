/**
 * 
 */
package com.ly.train.flower.common.sample;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author leeyazhou
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
    com.ly.train.flower.common.sample.condition.Sample.class, 
    com.ly.train.flower.common.sample.programflow.Sample.class,
    com.ly.train.flower.common.sample.supervisor.Sample.class,
    com.ly.train.flower.common.sample.supervisor.BatchSample.class,
    com.ly.train.flower.common.sample.textflow.BatchSample.class,
    com.ly.train.flower.common.sample.textflow.Sample.class,
    com.ly.train.flower.common.sample.textflow.ServiceRouterSample.class

})
public class AllTests {

}
