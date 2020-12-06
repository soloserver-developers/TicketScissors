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

package pagen.nafuchoco.ticketscissors.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pagen.nafuchoco.ticketscissors.TicketScissors;
import pagen.nafuchoco.ticketscissors.database.tables.TicketTable;
import pagen.nafuchoco.ticketscissors.ticket.SupportTicket;
import pagen.nafuchoco.ticketscissors.ticket.TicketPriority;
import pagen.nafuchoco.ticketscissors.ticket.TicketStatus;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class TicketCommandExecutor implements CommandExecutor, TabCompleter {
    private final TicketTable ticketTable;

    private Map<Player, SupportTicket.SupportTicketBuilder> ticketBuilders = new HashMap<>();

    public TicketCommandExecutor(TicketTable ticketTable) {
        this.ticketTable = ticketTable;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            // TODO: 2020/12/04 Command Help.
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            switch (args[0]) {
                case "create": {
                    sendTicketEditor(player, getTicketBuilder(player));
                    break;
                }

                case "subject": {
                    SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                    builder.subject(args[1]);
                    sendTicketEditor(player, builder);
                    break;
                }

                case "priority": {
                    SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                    try {
                        TicketPriority priority = TicketPriority.valueOf(args[1]);
                        builder.priority(priority);
                        sendTicketEditor(player, builder);
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.YELLOW + "[TicketScissors] Choose from LOWEST, LOW, NORMAL, HIGH, and HIGHEST.");
                    }
                    break;
                }

                case "location": {
                    SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                    builder.location(player.getLocation());
                    sendTicketEditor(player, builder);
                    break;
                }

                case "related": {
                    SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                    try {
                        builder.related(Integer.parseInt(args[1]));
                        sendTicketEditor(player, builder);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.YELLOW + "[TicketScissors] Enter the ID of the relevant ticket.");
                    }
                    break;
                }

                case "message":
                    if (args.length >= 3) {
                        switch (args[1]) {
                            case "add": {
                                SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                                builder.addMessageLine(args[2]);
                                sendTicketEditor(player, builder);
                            }
                            break;

                            case "remove": {
                                SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                                try {
                                    builder.removeMessageLine(Integer.parseInt(args[2]));
                                    sendTicketEditor(player, builder);
                                } catch (NumberFormatException e) {
                                    player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Specify a number of lines.");
                                }
                            }
                            break;
                        }
                    } else if (args[1].equals("add")) {
                        SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                        builder.addMessageLine("");
                        sendTicketEditor(player, builder);
                    }
                    break;

                case "send": {
                    SupportTicket.SupportTicketBuilder builder = getTicketBuilder(player);
                    if (builder.getSubject() != null && !builder.getMessage().isEmpty()) {
                        try {
                            // 関連チケットの存在確認
                            if (builder.getRelated() != 0 && ticketTable.getTicket(builder.getRelated()) == null)
                                builder.related(0);

                            builder.sendDate(new Date());
                            builder.ticketStatus(TicketStatus.OPEN);

                            ticketTable.registerTicket(builder.build());
                            ticketBuilders.remove(builder);
                        } catch (SQLException e) {
                            player.sendMessage(ChatColor.RED + "[TicketScissors] An error occurred during registration. Please try again later.");
                            TicketScissors.getInstance().getLogger().log(Level.WARNING, "An error occurred while communicating with the database.", e);
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[TicketScissors] The required fields are blank. The title and body must be entered.");
                    }
                    break;
                }

                default:
                    sender.sendMessage(ChatColor.RED + "[TicketScissors] This command is not registered.");
                    break;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length <= 0) {
            return Arrays.asList("create", "subject", "priority", "location", "related", "message", "send");
        } else switch (args[0]) {
            case "priority":
                return Arrays.asList("LOWEST", "LOW", "NORMAL", "HIGH", "HIGHEST");
                
        }
        return null;
    }

    public void sendTicketEditor(Player player, SupportTicket.SupportTicketBuilder builder) {
        player.sendMessage(ChatColor.AQUA + "====== Ticket Edit ======");
        player.sendMessage("Author: " + player.getDisplayName() + ", Priority: " + builder.getPriority() + ", Related: #"
                + (builder.getRelated() == 0 ? "N/A" : builder.getRelated()));
        player.sendMessage("Location: " + builder.getLocation());
        player.sendMessage("------");
        player.sendMessage("Subject: " + builder.getSubject());
        player.sendMessage("------");
        player.sendMessage("Message:");
        builder.getMessage().forEach(message -> player.sendMessage(message));
    }

    private SupportTicket.SupportTicketBuilder getTicketBuilder(Player player) {
        return ticketBuilders.computeIfAbsent(player, k -> {
            SupportTicket.SupportTicketBuilder b = SupportTicket.builder();
            b.author(player.getUniqueId());
            return b;
        });
    }
}
