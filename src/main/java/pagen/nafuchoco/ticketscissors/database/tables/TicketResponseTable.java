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

import pagen.nafuchoco.ticketscissors.database.DatabaseConnector;
import pagen.nafuchoco.ticketscissors.database.DatabaseTable;
import pagen.nafuchoco.ticketscissors.ticket.TicketResponse;

import java.sql.*;
import java.util.UUID;

public class TicketResponseTable extends DatabaseTable {

    public TicketResponseTable(String prefix, DatabaseConnector connector) {
        super(prefix, "responses", connector);
    }

    public void crateTable() throws SQLException {
        super.createTable("id VARCHAR(36) NOT NULL, sender VARCHAR(36) NOT NULL, send_date TIMESTAMP, message LONGTEXT NULL");
    }

    public TicketResponse getResponse(UUID id) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM " + getTablename() + " WHERE id = ?"
             )) {
            ps.setString(1, id.toString());
            try (ResultSet resultSet = ps.executeQuery()) {
                TicketResponse response = null;
                while (resultSet.next()) {
                    UUID sender = UUID.fromString(resultSet.getString("sender"));
                    Timestamp send_date = resultSet.getTimestamp("send_date");
                    String message = resultSet.getString("message");
                    response = new TicketResponse(null, id, sender, send_date, message); //// TODO: 2020/12/06
                }
                return response;
            }
        }
    }

    public void registerResponse(TicketResponse response) throws SQLException {
        try (Connection connection = getConnector().getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO " + getTablename() + " (id, sender, send_date, message) VALUES (?, ?, ?, ?)"
             )) {
            ps.setString(1, response.getResponseId().toString());
            ps.setString(2, response.getSender().toString());
            ps.setTimestamp(3, new Timestamp(response.getSendDate().getTime()));
            ps.setString(4, response.getMessage());
            ps.execute();
        }
    }
}
