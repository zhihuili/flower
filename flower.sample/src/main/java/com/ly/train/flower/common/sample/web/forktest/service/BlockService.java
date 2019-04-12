/**
 * Copyright © 2019 同程艺龙 (zhihui.li@ly.com)
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

import com.ly.train.flower.common.sample.web.dao.GoodsDao;
import com.ly.train.flower.common.sample.web.dao.OrderDao;
import com.ly.train.flower.common.sample.web.dao.UserDao;
import com.ly.train.flower.common.sample.web.mode.Order;
import com.ly.train.flower.common.sample.web.mode.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author fengyu.zhang
 * @date 2019/2/24 15:47
 */

@Service("BlockService")
public class BlockService {
  @Autowired
  private UserDao userDao;
  @Autowired
  private GoodsDao goodsDao;
  @Autowired
  private OrderDao orderDao;

  public Set<Object> getInfo(Integer userId) {
    User user = userDao.findUser(userId);
    List<Integer> goodsList = goodsDao.findGoodsIdForRecommend(userId);
    List<Order> orders = orderDao.findByCustomerId(userId);
    Set<Object> set = new HashSet<>();
    set.add(user);
    set.add(goodsList);
    set.add(orders);
    return set;
  }
}
