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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class TicketResponse {
    private static final Gson gson = new Gson();

    private SupportTicket originalTicket;
    private UUID responseId;
    private UUID sender;
    private Date sendDate;
    private String message;

    public static TicketResponseBuilder builder() {
        return new TicketResponseBuilder();
    }

    @Getter
    public static class TicketResponseBuilder {
        private EditedSupportTicket originalTicket;
        private UUID responseId;
        private UUID sender;
        private Date sendDate;
        private List<String> message;

        TicketResponseBuilder() {
        }

        public TicketResponseBuilder originalTicket(EditedSupportTicket originalTicket) {
            this.originalTicket = originalTicket;
            return this;
        }

        public TicketResponseBuilder responseId(UUID responseId) {
            this.responseId = responseId;
            return this;
        }

        public TicketResponseBuilder sender(UUID sender) {
            this.sender = sender;
            return this;
        }

        public TicketResponseBuilder sendDate(Date sendDate) {
            this.sendDate = sendDate;
            return this;
        }

        public TicketResponseBuilder addMessageLine(String messageLine) {
            this.message.add(messageLine);
            return this;
        }

        public TicketResponseBuilder removeMessageLine(int line) {
            message.remove(line);
            return this;
        }

        public TicketResponse build() {
            return new TicketResponse(originalTicket, responseId, sender, sendDate, gson.toJson(message));
        }

        @Override
        public String toString() {
            return "TicketResponse.TicketResponseBuilder(originalTicket=" + this.originalTicket + ", responseId=" + this.responseId + ", sender=" + this.sender + ", sendDate=" + this.sendDate + ", message=" + this.message + ")";
        }
    }
}
