package com.shreeganesh.enterprises.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "why_choose_us")
public class WhyChooseUs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String iconPath;

    private boolean active = true;

    // ===== Getters & Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconPath() { return iconPath; }
    public void setIconPath(String iconPath) { this.iconPath = iconPath; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
