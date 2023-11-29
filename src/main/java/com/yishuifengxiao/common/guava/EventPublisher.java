package com.yishuifengxiao.common.guava;

import com.google.common.eventbus.EventBus;

/**
 * 事件发布者
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
public interface EventPublisher {


    /**
     * Posts an event to all registered subscribers.
     * This method will return successfully after the event has been posted to all subscribers,
     * and regardless of any exceptions thrown by subscribers.
     * If no subscribers have been subscribed for event's class,
     * and event is not already a DeadEvent, it will be wrapped in a DeadEvent and reposted.
     *
     * @param event event to post.
     */
    void post(Object event);

    /**
     * 获取消息总线
     *
     * @return 消息总线
     */
    EventBus eventBus();
}
