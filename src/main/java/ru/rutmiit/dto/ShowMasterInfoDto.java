package ru.rutmiit.dto;

import ru.rutmiit.models.entities.Project;

import java.io.Serializable;
import java.util.HashSet;

public class ShowMasterInfoDto implements Serializable {
    private String fio;
    private String role;

    private String city;
    private Boolean isBrigadier;
    private Double experience;
    private String phone;
    private String email;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getIsBrigadier() {
        return isBrigadier;
    }

    public void setIsBrigadier(Boolean isBrigadier) {
        this.isBrigadier = isBrigadier;
    }

    public Double getExperience() {
        return experience;
    }

    public void setExperience(Double experience) {
        this.experience = experience;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HashSet<Project> getProjects() {
        return projects;
    }

    public void setProjects(HashSet<Project> projects) {
        this.projects = projects;
    }

    private transient HashSet<Project> projects;
    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
