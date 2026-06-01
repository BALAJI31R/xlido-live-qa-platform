package com.crio.xlido;

import java.util.*;
import java.util.stream.Collectors;

public class App {

    private int userIdCounter = 0;
    private int eventIdCounter = 0;
    private int questionIdCounter = 0;

    private Map<Integer, User> users = new LinkedHashMap<>();
    private Map<Integer, Event> events = new LinkedHashMap<>();
    private Map<Integer, Question> questions = new LinkedHashMap<>();

    public static void main(String[] args) {
        if (args.length == 1) {
            String inputFile = args[0].split("=")[1];
            try {
                List<String> commands = java.nio.file.Files.readAllLines(
                    java.nio.file.Paths.get(inputFile));
                new App().run(commands);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run(List<String> commands) {
        for (String command : commands) {
            if (command == null || command.trim().isEmpty()) continue;
            String[] tokens = command.split(",");
            String action = tokens[0].trim();

            switch (action) {
                case "CREATE_USER":
                    createUser(tokens);
                    break;
                case "CREATE_EVENT":
                    createEvent(tokens);
                    break;
                case "DELETE_EVENT":
                    deleteEvent(tokens);
                    break;
                case "ADD_QUESTION":
                    addQuestion(tokens);
                    break;
                case "DELETE_QUESTION":
                    deleteQuestion(tokens);
                    break;
                case "UPVOTE_QUESTION":
                    upvoteQuestion(tokens);
                    break;
                case "REPLY_QUESTION":
                    replyQuestion(tokens);
                    break;
                case "LIST_QUESTIONS":
                    listQuestions(tokens);
                    break;
                default:
                    break;
            }
        }
    }

    private void createUser(String[] tokens) {
        String email = tokens[1].trim();
        String password = tokens[2].trim();
        userIdCounter++;
        users.put(userIdCounter, new User(userIdCounter, email, password));
        System.out.println("User ID: " + userIdCounter);
    }

    private void createEvent(String[] tokens) {
        String eventName = tokens[1].trim();
        int userId = Integer.parseInt(tokens[2].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        eventIdCounter++;
        events.put(eventIdCounter, new Event(eventIdCounter, eventName, userId));
        System.out.println("Event ID: " + eventIdCounter);
    }

    private void deleteEvent(String[] tokens) {
        int eventId = Integer.parseInt(tokens[1].trim());
        int userId = Integer.parseInt(tokens[2].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        if (!events.containsKey(eventId)) {
            System.out.println("ERROR: Event with an id " + eventId + " does not exist");
            return;
        }

        Event event = events.get(eventId);
        if (event.getOrganizerId() != userId) {
            System.out.println("ERROR: User with an id " + userId + " is not a organizer of Event with an id " + eventId);
            return;
        }

        events.remove(eventId);
        // Remove all questions associated with this event
        questions.entrySet().removeIf(entry -> entry.getValue().getEventId() == eventId);
        System.out.println("EVENT_DELETED " + eventId);
    }

    private void addQuestion(String[] tokens) {
        String content = tokens[1].trim();
        int userId = Integer.parseInt(tokens[2].trim());
        int eventId = Integer.parseInt(tokens[3].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        if (!events.containsKey(eventId)) {
            System.out.println("ERROR: Event with an id " + eventId + " does not exist");
            return;
        }

        questionIdCounter++;
        questions.put(questionIdCounter, new Question(questionIdCounter, content, userId, eventId));
        System.out.println("Question ID: " + questionIdCounter);
    }

    private void deleteQuestion(String[] tokens) {
        int questionId = Integer.parseInt(tokens[1].trim());
        int userId = Integer.parseInt(tokens[2].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        if (!questions.containsKey(questionId)) {
            System.out.println("ERROR: Question with an id " + questionId + " does not exist");
            return;
        }

        Question question = questions.get(questionId);
        if (question.getAuthorId() != userId) {
            System.out.println("ERROR: User with an id " + userId + " is not an author of question with an id " + questionId);
            return;
        }

        questions.remove(questionId);
        System.out.println("QUESTION_DELETED " + questionId);
    }

    private void upvoteQuestion(String[] tokens) {
        int questionId = Integer.parseInt(tokens[1].trim());
        int userId = Integer.parseInt(tokens[2].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        if (!questions.containsKey(questionId)) {
            System.out.println("ERROR: Question with an id " + questionId + " does not exist");
            return;
        }

        Question question = questions.get(questionId);
        if (question.hasUpvoted(userId)) {
            System.out.println("ERROR: User with an id " + userId + " has already upvoted a question with an id " + questionId);
            return;
        }

        question.addUpvote(userId);
        System.out.println("QUESTION_UPVOTED " + questionId);
    }

    private void replyQuestion(String[] tokens) {
        String replyContent = tokens[1].trim();
        int questionId = Integer.parseInt(tokens[2].trim());
        int userId = Integer.parseInt(tokens[3].trim());

        if (!users.containsKey(userId)) {
            System.out.println("ERROR: User with an id " + userId + " does not exist");
            return;
        }

        if (!questions.containsKey(questionId)) {
            System.out.println("ERROR: Question with an id " + questionId + " does not exist");
            return;
        }

        Question question = questions.get(questionId);
        question.addReply(new Reply(userId, replyContent));
        System.out.println("REPLY_ADDED");
    }

    private void listQuestions(String[] tokens) {
        int eventId = Integer.parseInt(tokens[1].trim());
        String sortOrder = tokens[2].trim();

        if (!events.containsKey(eventId)) {
            System.out.println("ERROR: Event with an id " + eventId + " does not exist");
            return;
        }

        List<Question> eventQuestions = questions.values().stream()
            .filter(q -> q.getEventId() == eventId)
            .collect(Collectors.toList());

        if (sortOrder.equals("POPULAR")) {
            eventQuestions.sort((a, b) -> {
                int cmp = Integer.compare(b.getVoteCount(), a.getVoteCount());
                if (cmp != 0) return cmp;
                return Integer.compare(a.getId(), b.getId());
            });
        } else if (sortOrder.equals("RECENT")) {
            eventQuestions.sort((a, b) -> Integer.compare(b.getId(), a.getId()));
        }

        for (Question q : eventQuestions) {
            System.out.println("Question ID: " + q.getId());
            System.out.println("Content: " + q.getContent());
            System.out.println("Votes: " + q.getVoteCount());
            System.out.println("Replies:");
            for (Reply reply : q.getReplies()) {
                System.out.println("  - User " + reply.getUserId() + ": " + reply.getContent());
            }
            System.out.println();
        }
    }

    // Inner classes

    static class User {
        private int id;
        private String email;
        private String password;

        public User(int id, String email, String password) {
            this.id = id;
            this.email = email;
            this.password = password;
        }

        public int getId() { return id; }
        public String getEmail() { return email; }
    }

    static class Event {
        private int id;
        private String name;
        private int organizerId;

        public Event(int id, String name, int organizerId) {
            this.id = id;
            this.name = name;
            this.organizerId = organizerId;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public int getOrganizerId() { return organizerId; }
    }

    static class Question {
        private int id;
        private String content;
        private int authorId;
        private int eventId;
        private Set<Integer> upvotedBy = new LinkedHashSet<>();
        private List<Reply> replies = new ArrayList<>();

        public Question(int id, String content, int authorId, int eventId) {
            this.id = id;
            this.content = content;
            this.authorId = authorId;
            this.eventId = eventId;
        }

        public int getId() { return id; }
        public String getContent() { return content; }
        public int getAuthorId() { return authorId; }
        public int getEventId() { return eventId; }
        public int getVoteCount() { return upvotedBy.size(); }
        public List<Reply> getReplies() { return replies; }

        public boolean hasUpvoted(int userId) {
            return upvotedBy.contains(userId);
        }

        public void addUpvote(int userId) {
            upvotedBy.add(userId);
        }

        public void addReply(Reply reply) {
            replies.add(reply);
        }
    }

    static class Reply {
        private int userId;
        private String content;

        public Reply(int userId, String content) {
            this.userId = userId;
            this.content = content;
        }

        public int getUserId() { return userId; }
        public String getContent() { return content; }
    }
}
