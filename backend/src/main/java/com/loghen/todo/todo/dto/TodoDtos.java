package com.loghen.todo.todo.dto;

public class TodoDtos {

    public static class CreateTodoRequest {
        private String title;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
    }

    public static class UpdateTodoRequest {
        private String title;
        private Boolean completed;

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public Boolean getCompleted() { return completed; }
        public void setCompleted(Boolean completed) { this.completed = completed; }
    }

    public static class TodoResponse {
        private Long id;
        private String title;
        private boolean completed;

        public TodoResponse() {}

        public TodoResponse(Long id, String title, boolean completed) {
            this.id = id;
            this.title = title;
            this.completed = completed;
        }

        public Long getId() { return id; }
        public String getTitle() { return title; }
        public boolean isCompleted() { return completed; }
    }
}
