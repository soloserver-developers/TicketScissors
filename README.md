# TicketScissors
"TicketScissors" is a Spigot plugin that provides a support ticket system.  
You can create tickets in an intuitive operation from within the game.  
Also, you can perform simple operations from the console.

## Feature

- 

### Planned Features

- Support for SQLite
- Modifiable Multilingual Messages
- Attachment of inventory snapshots

## How to start
Download the Jar file of the plugin and insert it into the plugins folder of Spigot.  
Start the server and check the configuration file is generated and fill in the required settings.  
Then run the "tsreload" command to re-execute the initialization process.  
The plugin will show an error about the database when it is first started,  
but it is waiting for you to enter the "tsreload" command without any problems.

## Command & Permission

- **tsreload**
    - Description: Reload all the features of the plugin.
    - Permission: ticketscissors.reload
    - Default: OP

- **ticket**
    - Description: You can create a support ticket.
    - Permission: ticketscissors.ticket

- **tickets**
    - Description:

- **ticketmd**
    - Description:

- **ticketrp**
    - Description: You can return a reply to the ticket.
    - Permission: ticketscissors.ticket.reply