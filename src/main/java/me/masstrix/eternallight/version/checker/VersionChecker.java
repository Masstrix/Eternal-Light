/*
 * Copyright 2020 Matthew Denton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.masstrix.eternallight.version.checker;

public class VersionChecker {

    private final int ID;
    private final String CURRENT;
    private CheckerApi api = CheckerApi.SPIGET;

    public VersionChecker(int resource, String current) {
        ID = resource;
        CURRENT = current;
    }

    public VersionChecker useApi(CheckerApi api) {
        if (api == null) return this;
        this.api = api;
        return this;
    }

    /**
     * Connects to the selected api and checks the plugins version.
     *
     * @param callback callback method to be ran when the task is complete.
     */
    public void run(VersionCallback callback) {
        new Thread(() -> api.run(ID, CURRENT, callback), "VersionChecker").start();
    }
}
