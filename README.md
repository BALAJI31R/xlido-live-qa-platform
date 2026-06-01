# XLido - Live Q&A Platform

A command-line live event Q&A platform where users can create events, post questions, upvote them, reply, and view questions sorted by popularity or recency.

## Overview

XLido simulates a live Q&A session (similar to Slido). Event organizers create events, attendees post questions, and the community engages through upvotes and replies. Questions can be listed sorted by popularity (most upvotes) or recency.

## Tech Stack

- Java
- Gradle 7.4.2
- JUnit 5
- system-lambda (for testing console output)

## Project Structure

```
app/src/main/java/com/crio/xlido/
└── App.java          # Main application with inner classes
    ├── User          # User entity (id, email, password)
    ├── Event         # Event entity (id, name, organizerId)
    ├── Question      # Question entity (id, content, votes, replies)
    └── Reply         # Reply entity (userId, content)
```

## Supported Commands

| Command | Format | Description |
|---------|--------|-------------|
| `CREATE_USER` | `CREATE_USER,email,password` | Register a new user |
| `CREATE_EVENT` | `CREATE_EVENT,name,userId` | Create a new event |
| `DELETE_EVENT` | `DELETE_EVENT,eventId,userId` | Delete an event (organizer only) |
| `ADD_QUESTION` | `ADD_QUESTION,content,userId,eventId` | Post a question to an event |
| `DELETE_QUESTION` | `DELETE_QUESTION,questionId,userId` | Delete a question (author only) |
| `UPVOTE_QUESTION` | `UPVOTE_QUESTION,questionId,userId` | Upvote a question (once per user) |
| `REPLY_QUESTION` | `REPLY_QUESTION,content,questionId,userId` | Reply to a question |
| `LIST_QUESTIONS` | `LIST_QUESTIONS,eventId,sortOrder` | List questions (POPULAR or RECENT) |

## Features

- User authentication and ownership validation
- Event lifecycle management (create/delete)
- Question CRUD with author-only deletion
- Single upvote per user per question
- Threaded replies on questions
- Sorting: POPULAR (by votes desc) or RECENT (by creation desc)
- Comprehensive error handling with descriptive messages

## Build & Run

```bash
# Build the project
./gradlew build

# Run with input file
./gradlew run --args="input=path/to/input.txt"

# Run tests
./gradlew test
```

## Author

Balaji R — Crio.Do
