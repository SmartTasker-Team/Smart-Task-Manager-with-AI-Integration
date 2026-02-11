package com.smarttask.manager.domain.model;


public class Project {
    private String id;
    private String name;
    private String description;
    private String ownerId;
    private String teamId;

    public Project(String id, String name, String ownerId, String teamId) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom du projet ne peut pas être vide");
        }
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.teamId = teamId;
    }

    public String getId() { return id; }
    public String getOwnerId() { return ownerId; }
    public String getTeamId() { return teamId; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }

//    public String toString() {
//        return "Task{" +
//                "id='" + id + '\'' +
//                ", name='" + name + '\'' +
//                ", description='" + description + '\'' +
//                ", owner_id='" + ownerId + '\'' +
//                ", team_id='" + teamId + '\'' +
//                '}';
//    }
//
//    public static void main(String[] args){
//        Project p1 = new Project("1","FFFFFFFF","1","2");
//        System.out.println(p1);
//    }
}