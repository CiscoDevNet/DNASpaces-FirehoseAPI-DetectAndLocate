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

package com.cisco.dnaspaces.server;

import com.cisco.dnaspaces.server.handler.APIHandler;
import com.cisco.dnaspaces.utils.ConfigUtil;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HTTPVerticle extends AbstractVerticle {

    private static final Logger log = LogManager.getLogger(HTTPVerticle.class);
    public void start() {
        Integer httpPort = Integer.parseInt(ConfigUtil.getConfig().getProperty("http.port"));

        HttpServerOptions options = new HttpServerOptions();

        Router router = Router.router(vertx);
        router.mountSubRouter("/api/v1", new APIHandler().router(vertx));

        vertx.createHttpServer(options).requestHandler(router).listen(httpPort);

    }

}