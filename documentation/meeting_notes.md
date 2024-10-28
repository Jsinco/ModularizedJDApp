# Technical Overview of this project

This is a modularizable Discord bot that can have
modules added to it to extend its functionality programmatically.

To add modules, you'll need to understand the fundamentals of the Java
Programming Language, and some aspects the JDA library.

## How it works
Modules are registered using reflection. When starting, our framework
will scan its child package for classes that implement or extend any aspect of a module.
Such as `Tickable`, `CommandModule`, `ListenerModule`, etc.

## How to add a module
To add a module within the scope of this project, you'll need to
create a new class that extends or implements one of the module interfaces.
Then, you'll need to add the proper annotations to the class for the specific module.
In the case of a `CommandModule`, you'll need to add the `@DiscordCommand` annotation to the class.
In the case of a `Tickable`, you'll need to add the `@Tick` annotation to the class.

## Examples can be found
In the `modules` package, you'll find examples of modules that you can use as a reference (created by me).
The current modules I've implemented have been mostly testing, debugging, and a single useful module of reminders.

## How to run the bot
To run the bot you'll either need to create a jar or run the project inside, of your IDE. Supply the `botToken` as an argument or
environment variable.

## Future plans
In the future, I'd like to be able to add Canvas LMS Instructure integration. Unfortunately, student
accounts at HCC don't have many permissions available to them through the API (or my testing was bad), so integration with canvas
may be heavily limited.