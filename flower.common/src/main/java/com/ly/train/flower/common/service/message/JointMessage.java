package com.ly.train.flower.common.service.message;

import java.util.Set;

public class JointMessage extends FlowMessage {
    Set<Object> set;

    public Set<Object> getSet() {
        return set;
    }

    public void setSet(Set<Object> set) {
        this.set = set;
    }
}
