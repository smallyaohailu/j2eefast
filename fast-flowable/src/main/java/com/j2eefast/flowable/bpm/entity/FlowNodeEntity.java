package com.j2eefast.flowable.bpm.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 流程节点
 * @author huanzhou
 */
@Data
public class FlowNodeEntity implements Serializable {
    /**
     * 节点id
     */
    private String nodeId;

    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 执行人的ID
     */
    private String userId;

    /**
     * 执行人姓名
     */
    private String userName;

    /**
     * 任务节点结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "GMT+8")
    private Date endTime;
}
