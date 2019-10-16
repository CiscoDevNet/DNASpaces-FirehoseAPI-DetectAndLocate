/*
 * Copyright (c) 2019 Cisco Systems, Inc. and/or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cisco.dnaspaces.client;

import com.cisco.dnaspaces.consumers.JsonEventConsumer;
import com.cisco.dnaspaces.exceptions.FireHoseAPIException;
import com.cisco.dnaspaces.utils.ConfigUtil;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Properties;

public class FireHoseAPIClient implements Closeable {

    private static final Logger log = LogManager.getLogger(FireHoseAPIClient.class);
    private static final Properties config = ConfigUtil.getConfig();
    final String API_URL;
    final String API_KEY;
    long fromTimeStampAdvanceWindow;
    CloseableHttpClient httpclient;
    JsonEventConsumer consumer;

    public FireHoseAPIClient(final String API_URL, final String API_KEY) {
        this.API_KEY = API_KEY;
        this.API_URL = API_URL;
        init();
    }

    public void setConsumer(JsonEventConsumer consumer) {
        this.consumer = consumer;
    }

    public long getFromTimeStampAdvanceWindow() {
        return fromTimeStampAdvanceWindow;
    }

    public void setFromTimeStampAdvanceWindow(long fromTimeStampAdvanceWindow) {
        this.fromTimeStampAdvanceWindow = fromTimeStampAdvanceWindow;
    }

    private void init() {
        log.info("Initializing FireHoseAPIClient");
        RequestConfig requestConfig = RequestConfig
                .custom()
                .setRedirectsEnabled(true)
                .build();
        this.httpclient = HttpClients
                .custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public void startConsumeEvents() throws FireHoseAPIException {
        if (consumer == null) {
            throw new FireHoseAPIException("Event Data consumer not set");
        }
        HttpGet request = null;
        KafkaProducer<String, String> producer = null;
        // read kafka related configurations
        final Boolean isKafkaEnabled = Boolean.valueOf(config.getProperty("kafka.enabled"));
        final String topicName = config.getProperty("kafka.topic.name");
        final String eventKeyProperty = config.getProperty("kafka.event.key.property");
        // create Kafka producer only if kafka is enabled
        if(isKafkaEnabled) {
            Properties options = new Properties();
            options.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("kafka.bootstrap.servers"));
            options.put(ProducerConfig.ACKS_CONFIG, config.getProperty("kafka.acks"));
            options.put(ProducerConfig.RETRIES_CONFIG, config.getProperty("kafka.retries"));
            options.put(ProducerConfig.BATCH_SIZE_CONFIG, config.getProperty("kafka.batch.size"));
            options.put(ProducerConfig.LINGER_MS_CONFIG, config.getProperty("kafka.linger.ms"));
            options.put(ProducerConfig.BUFFER_MEMORY_CONFIG, config.getProperty("kafka.buffer.memory"));
            options.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, config.getProperty("kafka.key.serializer"));
            options.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, config.getProperty("kafka.value.serializer"));
            producer = new KafkaProducer(options);
        }
        try {
            request = this.getRequest(this.API_URL);
            log.debug("Executing GET request over http client. URL :: " + request.getURI().toString());
            HttpResponse response = httpclient.execute(request);
            log.debug("GET request executed. Received status code :: " + response.getStatusLine().getStatusCode());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode >= 300 && statusCode == 399) {
                String location = redirectHandler(response);
                throw new FireHoseAPIException("Couldn't startConsumeEvents", response.getStatusLine().getStatusCode());
            } else if (statusCode >= 400 && statusCode == 499) {
                throw new FireHoseAPIException("Couldn't startConsumeEvents", response.getStatusLine().getStatusCode());
            } else if (statusCode >= 500 && statusCode == 599) {
                throw new FireHoseAPIException("Couldn't startConsumeEvents", response.getStatusLine().getStatusCode());
            } else if (response.getStatusLine().getStatusCode() != 200) {
                log.error("Response status code :: " + response.getStatusLine().getStatusCode());
                throw new FireHoseAPIException("Couldn't startConsumeEvents", response.getStatusLine().getStatusCode());
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            while ((line = rd.readLine()) != null) {
                JSONObject eventData = new JSONObject(line);
                consumer.accept(eventData);
                if (isKafkaEnabled && producer != null) {
                    producer.send(new ProducerRecord(topicName, eventData.getString(eventKeyProperty), eventData.toString()));
                }
            }
        } catch (IOException | URISyntaxException e) {
            throw new FireHoseAPIException(e);
        } finally {
            log.info("request has been ended");
            request.releaseConnection();
            // flush and close Kafka Producer
            if (isKafkaEnabled && producer != null) {
                producer.flush();
                producer.close();
            }
        }
    }

    private HttpGet getRequest(String url) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(url);
        uriBuilder.setPath("/api/partners/v1/firehose/events");
        if (this.consumer.getLastSuccessTimeStamp() > 0)
            uriBuilder.setParameter("fromTimestamp", String.valueOf(this.getFromTimeStamp()));
        HttpGet request = new HttpGet(uriBuilder.build());
        request.addHeader("X-API-Key", this.API_KEY);
        return request;
    }

    private String redirectHandler(HttpResponse response) throws FireHoseAPIException {
        if (response.getHeaders(HttpHeaders.LOCATION) != null) {
            Header[] headers = response.getHeaders(HttpHeaders.LOCATION);
            if (headers.length == 1) {
                String location = headers[0].getValue();
                return location;
            }
        }
        throw new FireHoseAPIException("Invalid redirect. Location header not found.");
    }

    public long getFromTimeStamp() {
        if (this.consumer == null)
            return Instant.now().toEpochMilli();
        long lastSuccessTimeStamp = this.consumer.getLastSuccessTimeStamp();
        lastSuccessTimeStamp -= this.getFromTimeStampAdvanceWindow() * 1000;
        return lastSuccessTimeStamp;
    }

    public void close() {

    }

}