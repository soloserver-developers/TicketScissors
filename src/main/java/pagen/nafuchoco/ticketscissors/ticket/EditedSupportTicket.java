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

import lombok.Getter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@ToString
public class EditedSupportTicket extends SupportTicket {
    private final int id;
    private final boolean read;
    private List<UUID> responses;

    public EditedSupportTicket(Date sendDate, UUID author, String subject, TicketPriority priority, TicketStatus ticketStatus, String location, int related, String message, int id, boolean read) {
        super(sendDate, author, subject, priority, ticketStatus, location, related, message);
        this.id = id;
        this.read = read;
        responses = new ArrayList<>();
    }

    public EditedSupportTicket(Date sendDate, UUID author, String subject, TicketPriority priority, TicketStatus ticketStatus, String location, int related, String message, int id, boolean read, List<UUID> responses) {
        super(sendDate, author, subject, priority, ticketStatus, location, related, message);
        this.id = id;
        this.read = read;
        if (responses != null)
            this.responses = responses;
        else
            this.responses = new ArrayList<>();
    }

    public void addResponse(TicketResponse response) {
        responses.add(response.getResponseId());
    }
}
