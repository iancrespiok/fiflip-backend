package com.fiflip.backend.project;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectCategory category;

    @Column(nullable = false)
    private String coverImageUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_before_images", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position")
    @Column(name = "image_url")
    private List<String> beforeImageUrls = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "project_after_images", joinColumns = @JoinColumn(name = "project_id"))
    @OrderColumn(name = "position")
    @Column(name = "image_url")
    private List<String> afterImageUrls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;

    private Double tea;

    private Boolean teaProjected;

    private LocalDate projectDate;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected Project() {
    }

    public Project(String title, String description, ProjectCategory category, String coverImageUrl,
            List<String> beforeImageUrls, List<String> afterImageUrls, ProjectStatus status, Double tea,
            Boolean teaProjected, LocalDate projectDate) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.coverImageUrl = coverImageUrl;
        this.beforeImageUrls = beforeImageUrls;
        this.afterImageUrls = afterImageUrls;
        this.status = status;
        this.tea = tea;
        this.teaProjected = teaProjected;
        this.projectDate = projectDate;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectCategory getCategory() {
        return category;
    }

    public void setCategory(ProjectCategory category) {
        this.category = category;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public List<String> getBeforeImageUrls() {
        return beforeImageUrls;
    }

    public void setBeforeImageUrls(List<String> beforeImageUrls) {
        this.beforeImageUrls = beforeImageUrls;
    }

    public List<String> getAfterImageUrls() {
        return afterImageUrls;
    }

    public void setAfterImageUrls(List<String> afterImageUrls) {
        this.afterImageUrls = afterImageUrls;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Double getTea() {
        return tea;
    }

    public void setTea(Double tea) {
        this.tea = tea;
    }

    public Boolean getTeaProjected() {
        return teaProjected;
    }

    public void setTeaProjected(Boolean teaProjected) {
        this.teaProjected = teaProjected;
    }

    public LocalDate getProjectDate() {
        return projectDate;
    }

    public void setProjectDate(LocalDate projectDate) {
        this.projectDate = projectDate;
    }
}
