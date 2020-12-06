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

package pagen.nafuchoco.ticketscissors.ticket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
public class SupportTicket {
    private static final Gson gson = new Gson();

    private final Date sendDate;
    private final UUID author;
    private final String subject;
    private final TicketPriority priority;
    private final TicketStatus ticketStatus;
    private final String location;
    private final int related;
    private final String message;

    SupportTicket(Date sendDate, UUID author, String subject, TicketPriority priority, TicketStatus ticketStatus, String location, int related, String message) {
        this.sendDate = sendDate;
        this.author = author;
        this.subject = subject;
        this.priority = priority;
        this.ticketStatus = ticketStatus;
        this.location = location;
        this.related = related;
        this.message = message;
    }

    public static SupportTicketBuilder builder() {
        return new SupportTicketBuilder();
    }

    public Location getSpawnLocationLocation() {
        JsonObject locationJson = gson.fromJson(location, JsonObject.class);
        String world = locationJson.get("World").getAsString();
        double x = locationJson.get("X").getAsDouble();
        double y = locationJson.get("Y").getAsDouble();
        double z = locationJson.get("Z").getAsDouble();
        return new Location(Bukkit.getWorld(world), x, y, z);
    }

    @Getter
    public static class SupportTicketBuilder {
        private Date sendDate;
        private UUID author;
        private String subject;
        private TicketPriority priority;
        private TicketStatus ticketStatus;
        private String location;
        private int related;
        private final List<String> message;

        SupportTicketBuilder() {
            message = new ArrayList<>();
        }

        public SupportTicketBuilder sendDate(Date sendDate) {
            this.sendDate = sendDate;
            return this;
        }

        public SupportTicketBuilder author(UUID author) {
            this.author = author;
            return this;
        }

        public SupportTicketBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public SupportTicketBuilder priority(TicketPriority priority) {
            this.priority = priority;
            return this;
        }

        public SupportTicketBuilder ticketStatus(TicketStatus ticketStatus) {
            this.ticketStatus = ticketStatus;
            return this;
        }

        public SupportTicketBuilder location(Location location) {
            JsonObject locationJson = new JsonObject();
            locationJson.addProperty("World", location.getWorld().getName());
            locationJson.addProperty("X", location.getBlockX());
            locationJson.addProperty("Y", location.getBlockY());
            locationJson.addProperty("Z", location.getBlockZ());
            this.location = new Gson().toJson(locationJson);
            return this;
        }

        public SupportTicketBuilder related(int related) {
            this.related = related;
            return this;
        }

        public SupportTicketBuilder addMessageLine(String messageLine) {
            this.message.add(messageLine);
            return this;
        }

        public SupportTicketBuilder removeMessageLine(int line) {
            message.remove(line);
            return this;
        }

        public SupportTicket build() {
            return new SupportTicket(sendDate, author, subject, priority, ticketStatus, location, related, gson.toJson(message));
        }

        @Override
        public String toString() {
            return "SupportTicket.SupportTicketBuilder(sendDate=" + this.sendDate + ", author=" + this.author + ", subject=" + this.subject + ", priority=" + this.priority + ", ticketStatus=" + this.ticketStatus + ", location=" + this.location + ", related=" + this.related + ", message=" + this.message + ")";
        }
    }
}