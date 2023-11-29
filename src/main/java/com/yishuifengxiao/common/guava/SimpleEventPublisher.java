package com.yishuifengxiao.common.guava;

import com.google.common.eventbus.EventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>默认实现的事件发布者</p>
 * <p>基于guava的EventBus实现</p>
 *
 * @author yishui
 * @version 1.0.0
 * @since 1.0.0
 */
@Slf4j
public class SimpleEventPublisher implements EventPublisher, InitializingBean {

    /**
     * 消息总线
     */
    private EventBus asyncEventBus;

    /**
     * Posts an event to all registered subscribers.
     * This method will return successfully after the event has been posted to all subscribers,
     * and regardless of any exceptions thrown by subscribers.
     * If no subscribers have been subscribed for event's class,
     * and event is not already a DeadEvent, it will be wrapped in a DeadEvent and reposted.
     *
     * @param event event to post.
     */
    @Override
    public synchronized void post(Object event) {
        if (null == this.asyncEventBus) {
            log.debug("The event publisher did not initialize successfully, but received a message call," +
                    " so the message was discarded. The discarded message is {}", event);
        }
        this.asyncEventBus.post(event);
    }

    @Override
    public EventBus eventBus() {
        return this.asyncEventBus;
    }


    public EventBus getAsyncEventBus() {
        return asyncEventBus;
    }

    public void setAsyncEventBus(EventBus asyncEventBus) {
        this.asyncEventBus = asyncEventBus;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        asyncEventBus.register(this);
    }
}
