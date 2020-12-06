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

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pagen.nafuchoco.ticketscissors.ticket.EditedSupportTicket;
import pagen.nafuchoco.ticketscissors.ticket.TicketResponse;

import java.text.SimpleDateFormat;
import java.util.List;

public class TicketsCommandExecutor implements CommandExecutor, TabCompleter {
    private static final Gson gson = new Gson();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    public void sendMessageViewer(Player player, EditedSupportTicket editedSupportTicket) {
        player.sendMessage(ChatColor.AQUA + "====== " + ChatColor.RESET + "["
                + editedSupportTicket.getTicketStatus().getColor() + ChatColor.BOLD + editedSupportTicket.getTicketStatus().name() + ChatColor.RESET
                + "] #" + editedSupportTicket.getId() + " " + editedSupportTicket.getSubject() + ChatColor.AQUA + " ======");
        player.sendMessage("Author: " + Bukkit.getOfflinePlayer(editedSupportTicket.getAuthor()).getName()
                + ", Priority: " + editedSupportTicket.getPriority() + ", Related: #"
                + (editedSupportTicket.getRelated() == 0 ? "N/A" : editedSupportTicket.getRelated()));
        player.sendMessage("Send Date: " + dateFormat.format(editedSupportTicket.getSendDate()));
        player.sendMessage("Location: " + editedSupportTicket.getLocation());
        player.sendMessage("------");
        player.sendMessage("Message:");
        List<String> messages = gson.fromJson(editedSupportTicket.getMessage(), new TypeToken<List<String>>() {}.getType());
        messages.forEach(message -> player.sendMessage(message));
    }

    public void sendReplyViewer(Player player, TicketResponse response) {
        player.sendMessage(ChatColor.AQUA + "====== Ticket Reply ======");
        player.sendMessage("Send Date:" + dateFormat.format(response.getSendDate()) + ", Author: " + player.getDisplayName());
        player.sendMessage("------");
        player.sendMessage("Message:");
        List<String> messages = gson.fromJson(response.getMessage(), new TypeToken<List<String>>() {}.getType());
        messages.forEach(message -> player.sendMessage(message));
    }
}
