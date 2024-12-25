# Simple Logistics Telegram bot
## Usage
### Available commands

* /register - Register a new profile
* /accounts - Show you profiles (created by your telegram account)
* /login - Select necessary profile to work with
* /profile - Show current selected profile
* /order - Publish new order (only for customers)
* /orders - Show orders (customer sees only his own orders, transporter sees all orders available for processing)
* /mark - Mark order (as accepted if called by transporter, as completed if called by customer)

### Usage example

1. Resister a new profile (/register)
2. Show accounts (/accounts) to select necessary profile
3. Login by printing profile id (/login)
4. Add new order by entering all fields (/order)
5. Show all my orders (/orders)

## Deploy

1. Create your bot with @BotFather and get it's personal bot token. In should be secret!
2. Put this token in the field `botToken` in [BotConfig.java](src/main/java/korolev/dens/logisticstgbot/configuration/BotConfig.java)
3. Put bot username in the field `userName` in [LogisticsBot.java](src/main/java/korolev/dens/logisticstgbot/bot/LogisticsBot.java)
4. Configure PostgreSQL database on your computer and change values of necessary parameters in [application.properties](src/main/resources/application.properties)
5. Create tables using [init.sql](src/main/resources/init.sql) in your database
6. Compile the app and run with `java -jar YourJarName.jar`

## Stack

* Spring Boot application
* PostgreSQL database as data store
* JPA for managing database entities
* Command state is not saved in database, it is kept in Spring `@Component` and can be lost when restarting the app
* Based on `TelegramLongPollingBot`