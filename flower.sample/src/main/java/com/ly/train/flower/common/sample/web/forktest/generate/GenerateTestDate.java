/**
 * Copyright © ${project.inceptionYear} 同程艺龙 (zhihui.li@ly.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ly.train.flower.common.sample.web.forktest.generate;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 18:01
 */
public class GenerateTestDate {
    public static void main(String[] args){
        int userId = 1;
        long count = 1;// 访问次数
        // 成功次数
        long blockSuccessCount = 0;
        long blockFailureCount = 0;
        // 失败次数
        long forkSuccessCount = 0;
        long forkFailureCount = 0;
        // 成功总耗时
        long forkTotalTime = 0;
        long blockTotalTime = 0;
        String blockUrl = "http://localhost:8080/test/block?id="+userId;
        String forkUrl = "http://localhost:8080/test/fork?id="+userId;

        // 先访问一次，防止第一次访问长延时影响数据,同时测试连通
        if(sendGet(blockUrl) == 0 || sendGet(forkUrl) == 0){
            System.out.println("接口未连通");
        }

        for(int i = 1;i<= count;i++){
            // 访问blockUrl
            long timeMill = sendGet(blockUrl);
            if(timeMill == 0){
                blockFailureCount ++;
            }else {
                blockSuccessCount ++;
                blockTotalTime += timeMill;
            }

            // 访问forkUrl
            timeMill = sendGet(forkUrl);
            if(timeMill == 0){
                forkFailureCount ++;
            }else {
                forkSuccessCount ++;
                forkTotalTime += timeMill;
            }
        }
        System.out.println("测试完成-访问id："+userId );
        System.out.println("普通阻塞访问:"+blockUrl+
                "\n-总次数："+count+"" +
                "\n-成功次数："+blockSuccessCount+
                "\n-失败次数："+blockFailureCount+
                "\n-平均访问耗时:"+((blockTotalTime*1.0)/blockSuccessCount)+"ms");
        System.out.println("Flower分叉访问:"+forkUrl+
                "\n-总次数："+count+"" +
                "\n-成功次数："+forkSuccessCount+
                "\n-失败次数："+forkFailureCount+
                "\n-平均访问耗时:"+((forkTotalTime*1.0)/forkSuccessCount)+"ms");
    }

    /**
     * 成功返回耗时，失败返回0
     * @param url
     * @return
     * @throws Exception
     */
    public static long sendGet(String url) {
        try {
            long start = System.currentTimeMillis();
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应,到这里才算真正的读取了资源，因此耗时计算到这个位置
            InputStream is = connection.getInputStream();
            is.close();
            long end = System.currentTimeMillis();
            return end-start;
        }catch (Exception e){
            return 0;
        }
    }
}
