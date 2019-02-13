package com.ly.train.flower.sample.web.performance;

	import io.gatling.core.scenario.Simulation
    import io.gatling.core.Predef._
    import io.gatling.http.Predef._

    import scala.concurrent.duration._

    class LoadSimulation extends Simulation {

      // 从系统变量读取 baseUrl、path和模拟的用户数
      val baseUrl = System.getProperty("base.url")
      val testPath = System.getProperty("test.path")
      val sim_users = System.getProperty("sim.users").toInt

      val httpConf = http.baseURL(baseUrl)

      // 定义模拟的请求，重复30次
      val helloRequest = repeat(30) {
        // 自定义测试名称
        exec(http("hello-with-latency")
          // 执行get请求
          .get(testPath))
          // 模拟用户思考时间，随机1~2秒钟
          .pause(1 second, 2 seconds)
      }

      // 定义模拟的场景
      val scn = scenario("hello")
        // 该场景执行上边定义的请求
        .exec(helloRequest)

      // 配置并发用户的数量在30秒内均匀提高至sim_users指定的数量
      setUp(scn.inject(rampUsers(sim_users).over(30 seconds)).protocols(httpConf))
    }

