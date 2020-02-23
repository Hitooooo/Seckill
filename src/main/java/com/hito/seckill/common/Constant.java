package com.hito.seckill.common;

/**
 * TODO
 *
 * @author HitoM
 * @date 2020/2/23 10:39
 **/
public interface Constant {
    String DEFAULT_QUEUE_NAME = "queue";

    String TOPIC_QUEUE_1 = "topic.queue.1";
    String TOPIC_QUEUE_2 = "topic.queue.2";
    String TOPIC_EXCHANGE = "topicExchange";
    String ROUTING_KEY_1 = "topic.key1";
    String ROUTING_KEY_2 = "topic.#";

    String FANOUT_QUEUE_1 = "fanout.queue.1";
    String FANOUT_QUEUE_2 = "fanout.queue.2";
    String FANOUT_EXCHANGE = "fanoutExchange";

    String HEADERS_QUEUE = "headers.queue";
    String HEADERS_EXCHANGE = "headersExchange";

    String MIAOSHA_QUEUE = "miaosha.queue";
}
