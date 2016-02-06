/*
 * Copyright 2016 Pigumer Group Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.pigumer.web;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Component
public class SubscribeHandler {
 
    private static final Logger LOGGER = Logger.getLogger(SubscribeHandler.class.getName());
    
    @SubscribeMapping("/queue/{path}")
    public void subscribe(@DestinationVariable String path) {
        LOGGER.log(Level.INFO, String.format("subscribe: %s", path));
    }

}
