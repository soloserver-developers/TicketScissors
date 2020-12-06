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
import pagen.nafuchoco.ticketscissors.database.tables.TicketResponseTable;
import pagen.nafuchoco.ticketscissors.database.tables.TicketTable;
import pagen.nafuchoco.ticketscissors.ticket.EditedSupportTicket;
import pagen.nafuchoco.ticketscissors.ticket.TicketResponse;

import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class TicketReplyCommandExecutor implements CommandExecutor, TabCompleter {
    private final TicketTable ticketTable;
    private final TicketResponseTable responseTable;

    private Map<Player, TicketResponse.TicketResponseBuilder> replyBuilders = new HashMap<>();

    public TicketReplyCommandExecutor(TicketTable ticketTable, TicketResponseTable responseTable) {
        this.ticketTable = ticketTable;
        this.responseTable = responseTable;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length <= 1) {
            // TODO: 2020/12/04 Command Help.
        } else if (sender instanceof Player) {
            Player player = (Player) sender;
            switch (args[0]) {
                case "reply": {
                    try {
                        int ticketId = Integer.parseInt(args[1]);
                        EditedSupportTicket ticket = ticketTable.getTicket(ticketId);
                        if (ticket != null) {
                            TicketResponse.TicketResponseBuilder builder = TicketResponse.builder();
                            builder.sender(player.getUniqueId());
                            replyBuilders.put(player, builder);
                            sendReplyEditor(player, builder);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Please specify the ID of the ticket to reply.");
                        }
                    } catch (NumberFormatException e) {
                        player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Please specify the ID of the ticket to reply.");
                    } catch (SQLException e) {
                        player.sendMessage(ChatColor.RED + "[TicketScissors] An error occurred while retrieving the ticket information.");
                        TicketScissors.getInstance().getLogger().log(Level.WARNING, "An error occurred while retrieving the ticket information.", e);
                    }
                    break;
                }

                case "message":
                    if (args.length >= 3) {
                        switch (args[1]) {
                            case "add": {
                                TicketResponse.TicketResponseBuilder builder = replyBuilders.get(player);
                                if (builder != null) {
                                    builder.addMessageLine(args[2]);
                                    sendReplyEditor(player, builder);
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Please specify the ID of the ticket to reply first.");
                                }
                            }
                            break;

                            case "remove": {
                                TicketResponse.TicketResponseBuilder builder = replyBuilders.get(player);
                                if (builder != null) {
                                    try {
                                        builder.removeMessageLine(Integer.parseInt(args[2]));
                                        sendReplyEditor(player, builder);
                                    } catch (NumberFormatException e) {
                                        player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Specify a number of lines.");
                                    }
                                } else {
                                    player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Please specify the ID of the ticket to reply first.");
                                }
                            }
                            break;
                        }
                    } else if (args[1].equals("add")) {
                        TicketResponse.TicketResponseBuilder builder = replyBuilders.get(player);
                        if (builder != null) {
                            builder.addMessageLine("");
                            sendReplyEditor(player, builder);
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "[TicketScissors] Please specify the ID of the ticket to reply first.");
                        }
                    }
                    break;

                case "send": {
                    TicketResponse.TicketResponseBuilder builder = replyBuilders.get(player);
                    if (!builder.getMessage().isEmpty()) {
                        try {
                            builder.responseId(UUID.randomUUID());
                            builder.sendDate(new Date());

                            builder.getOriginalTicket().getResponses().add(builder.getResponseId());
                            responseTable.registerResponse(builder.build());
                            ticketTable.updateResponses(builder.getOriginalTicket().getId(), builder.getOriginalTicket().getResponses());
                            replyBuilders.remove(builder);
                        } catch (SQLException e) {
                            player.sendMessage(ChatColor.RED + "[TicketScissors] An error occurred during registration. Please try again later.");
                            TicketScissors.getInstance().getLogger().log(Level.WARNING, "An error occurred while communicating with the database.", e);
                        }
                    } else {
                        player.sendMessage(ChatColor.YELLOW + "[TicketScissors] The required fields are blank.");
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
        return null; // TODO: 2020/12/04
    }

    public void sendReplyEditor(Player player, TicketResponse.TicketResponseBuilder builder) {
        player.sendMessage(ChatColor.AQUA + "====== Ticket Reply Edit ======");
        player.sendMessage("Author: " + player.getDisplayName());
        player.sendMessage("------");
        player.sendMessage("Message:");
        builder.getMessage().forEach(message -> player.sendMessage(message));
    }
}
