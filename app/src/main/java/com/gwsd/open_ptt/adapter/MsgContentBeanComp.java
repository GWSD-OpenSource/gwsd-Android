package com.gwsd.open_ptt.adapter;

import com.gwsd.open_ptt.dao.pojo.MsgContentPojo;

import java.io.Serializable;
import java.util.Comparator;

public class MsgContentBeanComp implements Comparator<MsgContentPojo>, Serializable {
    @Override
    public int compare(MsgContentPojo o1, MsgContentPojo o2) {
        return Long.compare(o1.getTime(),o2.getTime());
    }
}
