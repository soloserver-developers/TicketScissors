/*
 * Copyright 2020 NAFU_at
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

package pagen.nafuchoco.ticketscissors;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import pagen.nafuchoco.ticketscissors.database.DatabaseType;

import java.io.File;

public class ConfigLoader {
    private static final TicketScissors instance = TicketScissors.getInstance();

    @Getter
    private static DatabaseType databaseType;
    @Getter
    private static String address;
    @Getter
    private static int port;
    @Getter
    private static String database;
    @Getter
    private static String username;
    @Getter
    private static String password;
    @Getter
    private static String tablePrefix;

    public static void reloadConfig() {
        if (!new File(instance.getDataFolder(), "config.yml").exists())
            instance.saveDefaultConfig();
        instance.reloadConfig();

        FileConfiguration config = instance.getConfig();
        databaseType = DatabaseType.valueOf(config.getString("system.database.type"));
        address = config.getString("system.database.address");
        port = config.getInt("system.database.port", 3306);
        database = config.getString("system.database.database");
        username = config.getString("system.database.username");
        password = config.getString("system.database.password");
        tablePrefix = config.getString("system.database.tablePrefix");
    }
}
