package ru.rutmiit.models.entities;

import jakarta.persistence.*;
import org.springframework.security.core.parameters.P;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "masters")
public class Master extends BaseEntity {

    @Column(nullable = false)
    private String fio;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private Boolean isBrigadier = false;

    @Column(nullable = false)
    private Double experience;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "brigadier", fetch = FetchType.LAZY)
    private Set<Project> projectsAsBrigadier = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public Boolean getBrigadier() {
        return isBrigadier;
    }

    public void setBrigadier(Boolean brigadier) {
        isBrigadier = brigadier;
    }

    public Set<Project> getProjectsAsBrigadier() {
        return projectsAsBrigadier;
    }

    public void setProjectsAsBrigadier(Set<Project> projectsAsBrigadier) {
        this.projectsAsBrigadier = projectsAsBrigadier;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFio() {
        return fio;
    }

    public String getRole() {
        return role;
    }

    public String getCity() {
        return city;
    }

    public Boolean getIsBrigadier() {
        return isBrigadier;
    }

    public Double getExperience() {
        return experience;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }


    public void setFio(String fio) {
        this.fio = fio;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setIsBrigadier(Boolean isBrigadier) {
        this.isBrigadier = isBrigadier;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

}
