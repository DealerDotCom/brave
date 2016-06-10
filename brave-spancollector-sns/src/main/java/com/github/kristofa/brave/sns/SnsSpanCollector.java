package com.github.kristofa.brave.sns;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.sns.AmazonSNSAsyncClient;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.PublishResult;
import com.github.kristofa.brave.EmptySpanCollectorMetricsHandler;
import com.github.kristofa.brave.FlushingSpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.twitter.zipkin.gen.Span;
import com.twitter.zipkin.gen.SpanCodec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class SnsSpanCollector extends FlushingSpanCollector {
    private AmazonSNSAsyncClient client;
    private String snsTopicArn;

    /**
     * @param metrics
     * @param flushInterval in seconds. 0 implies spans are {@link #flush() flushed externally.
     * @param client
     */
    public SnsSpanCollector(SpanCollectorMetricsHandler metrics, int flushInterval, AmazonSNSAsyncClient client, String snsTopicArn) {
        super(metrics, flushInterval);
        this.client = client;
        this.snsTopicArn = snsTopicArn;
    }

    @Override
    protected void reportSpans(List<Span> drained) throws IOException {
        for (Span span : drained) {
            client.publishAsync(snsTopicArn, span.toString());
        }
    }
}
