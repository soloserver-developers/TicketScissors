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

package pagen.nafuchoco.ticketscissors.database.tables;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import pagen.nafuchoco.ticketscissors.database.DatabaseConnector;
import pagen.nafuchoco.ticketscissors.database.DatabaseTable;
import pagen.nafuchoco.ticketscissors.ticket.EditedSupportTicket;
import pagen.nafuchoco.ticketscissors.ticket.SupportTicket;
import pagen.nafuchoco.ticketscissors.ticket.TicketPriority;
import pagen.nafuchoco.ticketscissors.ticket.TicketStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TicketTable extends DatabaseTable {
    private static final Gson gson = new Gson();

    public TicketTable(String prefix, DatabaseConnector connector) {
        super(prefix, "tickets", connector);
    }

    public void crateTable() throws SQLException {
        super.createTable("id INT NOT NULL AUTO_INCREMENT, send_date TIMESTAMP, author VARCHAR(36) NOT NULL, " +
                "subject TINYTEXT NOT NULL, priority ENUM(LOWEST, LOW, NORMAL, HIGH, HIGHEST) NOT NULL, " +
                "status ENUM(OPEN, CLOSED, PENDING) NOT NULL, location TINYTEXT NULL, related INT NULL. " +
                "message LONGTEXT NULL, responses LONGTEXT NULL, read_status BOOL");
    }

    public EditedSupportTicket getTicket(int id) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE id = ?"
             )) {
            ps.setInt(1, id);
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next())
                    return resultToTicket(resultSet);
                return null;
            }
        }
    }

    public List<EditedSupportTicket> getAllTicket() throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename()
             )) {
            try (ResultSet resultSet = ps.executeQuery()) {
                List<EditedSupportTicket> tickets = new ArrayList<>();
                while (resultSet.next())
                    tickets.add(resultToTicket(resultSet));
                return tickets;
            }
        }
    }

    public List<EditedSupportTicket> getTickets(UUID author) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE author = ?"
             )) {
            ps.setString(1, author.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                List<EditedSupportTicket> tickets = new ArrayList<>();
                while (resultSet.next())
                    tickets.add(resultToTicket(resultSet));
                return tickets;
            }
        }
    }

    public List<EditedSupportTicket> getTickets(TicketStatus status) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE status = ?"
             )) {
            ps.setString(1, status.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                List<EditedSupportTicket> tickets = new ArrayList<>();
                while (resultSet.next())
                    tickets.add(resultToTicket(resultSet));
                return tickets;
            }
        }
    }

    public List<EditedSupportTicket> getTickets(int related) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE related = ?"
             )) {
            ps.setInt(1, related);
            try (ResultSet resultSet = ps.executeQuery()) {
                List<EditedSupportTicket> tickets = new ArrayList<>();
                while (resultSet.next())
                    tickets.add(resultToTicket(resultSet));
                return tickets;
            }
        }
    }

    public void registerTicket(SupportTicket ticket) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO " + getTablename() + " (send_date, author, subject, priority, status, location, " +
                             "related, message) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
             )) {
            ps.setTimestamp(1, new Timestamp(ticket.getSendDate().getTime()));
            ps.setString(2, ticket.getAuthor().toString());
            ps.setString(3, ticket.getSubject());
            ps.setString(4, ticket.getPriority().name());
            ps.setString(5, ticket.getTicketStatus().name());
            ps.setString(6, ticket.getLocation());
            ps.setInt(7, ticket.getRelated());
            ps.setString(8, ticket.getMessage());
            ps.execute();
        }
    }

    public void updateReadStatus(int id, boolean read) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTablename() + " SET read_status = ? WHERE id = ?"
             )) {
            ps.setBoolean(1, read);
            ps.setInt(2, id);
            ps.execute();
        }
    }

    public void updatePriority(int id, TicketPriority priority) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTablename() + " SET priority = ? WHERE id = ?"
             )) {
            ps.setString(1, priority.name());
            ps.setInt(2, id);
            ps.execute();
        }
    }

    public void updateTicketStatus(int id, TicketStatus status) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTablename() + " SET status = ? WHERE id = ?"
             )) {
            ps.setString(1, status.name());
            ps.setInt(2, id);
            ps.execute();
        }
    }

    public void updateResponses(int id, List<UUID> responses) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "UPDATE " + getTablename() + " SET responses = ? WHERE id = ?"
             )) {
            ps.setString(1, gson.toJson(responses));
            ps.setInt(2, id);
            ps.execute();
        }
    }


    private EditedSupportTicket resultToTicket(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        Timestamp send_date = resultSet.getTimestamp("send_date");
        UUID author = UUID.fromString(resultSet.getString("author"));
        String subject = resultSet.getString("subject");
        TicketPriority priority = TicketPriority.valueOf(resultSet.getString("priority"));
        TicketStatus status = TicketStatus.valueOf(resultSet.getString("status"));
        String location = resultSet.getString("location");
        int related = resultSet.getInt("related");
        String message = resultSet.getString("message");
        List<UUID> responses = gson.fromJson(resultSet.getString("responses"), new TypeToken<List<String>>() {
        }.getType());
        boolean read = resultSet.getBoolean("read_status");

        return new EditedSupportTicket(new Date(send_date.getTime()), author, subject, priority, status, location, related, message, id, read, responses);
    }
}
