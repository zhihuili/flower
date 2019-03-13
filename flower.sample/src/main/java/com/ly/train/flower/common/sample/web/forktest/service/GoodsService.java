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
package com.ly.train.flower.common.sample.web.forktest.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ly.train.flower.common.sample.web.dao.GoodsDao;
import com.ly.train.flower.common.service.container.ServiceContext;

/**
 * @Author: fengyu.zhang
 * @Date: 2019/2/24 17:16
 */
@Service("GoodsService")
public class GoodsService implements com.ly.train.flower.common.service.Service<Integer> {
    @Autowired
    private GoodsDao goodsDao;

    @Override
    public Object process(Integer message, ServiceContext context) throws Exception {
        List<Integer> list = goodsDao.findGoodsIdForRecommend(message);
        return list == null? new ArrayList<Integer>():list;
    }
}
