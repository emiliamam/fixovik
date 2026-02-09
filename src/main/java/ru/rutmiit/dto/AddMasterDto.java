package ru.rutmiit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.entities.Project;

import java.util.HashSet;

public class AddMasterDto {

    private String fio;
    private String role;
    private String city;
    private Boolean isBrigadier;
    private Double experience;
    private String phone;
    private String email;
    private transient HashSet<Project> projects;

    @NotEmpty(message = "Пароль не должен быть пустым!")
    @Size(min = 5, max = 20, message = "Пароль должен быть от 5 до 20 символов!")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public AddMasterDto() {
    }

    @NotEmpty(message = "Поле не должно быть пустым")
    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    @NotEmpty(message = "Должность не должна быть пустой")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @NotEmpty(message = "Город не должен быть пустым")
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

    @NotEmpty(message = "Введите контакты для связи")
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
}
