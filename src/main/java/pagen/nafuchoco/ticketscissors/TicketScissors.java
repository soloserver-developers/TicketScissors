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

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import pagen.nafuchoco.ticketscissors.commands.TicketCommandExecutor;
import pagen.nafuchoco.ticketscissors.commands.TicketReplyCommandExecutor;
import pagen.nafuchoco.ticketscissors.database.DatabaseConnector;
import pagen.nafuchoco.ticketscissors.database.tables.TicketResponseTable;
import pagen.nafuchoco.ticketscissors.database.tables.TicketTable;

import java.sql.SQLException;
import java.util.logging.Level;

public final class TicketScissors extends JavaPlugin {
    private static TicketScissors instance;

    private static DatabaseConnector connector;
    private static TicketTable ticketTable;
    private static TicketResponseTable responseTable;

    public static TicketScissors getInstance() {
        if (instance == null)
            instance = (TicketScissors) Bukkit.getServer().getPluginManager().getPlugin("TicketScissors");
        return instance;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        reloadPlugin();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (connector != null)
            connector.close();
    }

    // Init plugin method.
    public void reloadPlugin() {
        ConfigLoader.reloadConfig();

        getLogger().info("Preparing the database...");
        connector = new DatabaseConnector(ConfigLoader.getDatabaseType(),
                ConfigLoader.getAddress() + ":" + ConfigLoader.getPort(),
                ConfigLoader.getDatabase(),
                ConfigLoader.getUsername(),
                ConfigLoader.getPassword());
        ticketTable = new TicketTable(ConfigLoader.getTablePrefix(), connector);
        responseTable = new TicketResponseTable(ConfigLoader.getTablePrefix(), connector);
        try {
            ticketTable.crateTable();
            responseTable.crateTable();
        } catch (SQLException e) {
            getLogger().log(Level.WARNING, "An error occurred while initializing the database table.", e);
            return;
        }
        getLogger().info("Database is now ready.");

        // Register command executor.
        getCommand("ticket").setExecutor(new TicketCommandExecutor(ticketTable));
        getCommand("ticketrp").setExecutor(new TicketReplyCommandExecutor(ticketTable, responseTable));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("tsreload")) {
            sender.sendMessage("[TicketScissors] Reload the plugin settings. All features will not be available until it is completed.");
            instance.getDescription().getCommands().keySet().forEach(key -> getCommand(key).setExecutor(null));
            if (connector != null)
                connector.close();
            reloadPlugin();
        }
        return true;
    }
}
