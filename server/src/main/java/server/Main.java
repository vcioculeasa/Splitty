/*
 * Copyright 2021 Delft University of Technology
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
package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import server.api.PasswordService;

@SpringBootApplication(scanBasePackages = {"server", "api"})
@EntityScan(basePackages = { "commons", "server" })
public class Main {

    /**
     * Main method of the server.
     * @param args array of String args
     */
    public static void main(String[] args) {
        int password = (int)(Math.random() * 999999);
        PasswordService.setPassword(String.valueOf(password));
        SpringApplication.run(Main.class, args);
        System.out.println("The admin Password is: " + PasswordService.getPassword());
    }
}