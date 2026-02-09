package ru.rutmiit.dto;


import org.springframework.format.annotation.DateTimeFormat;
import ru.rutmiit.models.enums.ObjectLevels;
import ru.rutmiit.models.enums.Status;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public class ShowActiveProjectDto implements Serializable {
    private String name;
    private String address;
    private String description;
    private String brigadierId;
    private Set<String> masterIds;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private Double salary;
    private String space;
    private String client;
    private String phoneClient;
    private String email;
    private ObjectLevels type;
    private Status status;
    private int countProject;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrigadierId() {
        return brigadierId;
    }

    public void setBrigadierId(String brigadierId) {
        this.brigadierId = brigadierId;
    }

    public Set<String> getMasterIds() {
        return masterIds;
    }

    public void setMasterIds(Set<String> masterIds) {
        this.masterIds = masterIds;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getSpace() {
        return space;
    }

    public void setSpace(String space) {
        this.space = space;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getPhoneClient() {
        return phoneClient;
    }

    public void setPhoneClient(String phoneClient) {
        this.phoneClient = phoneClient;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ObjectLevels getType() {
        return type;
    }

    public void setType(ObjectLevels type) {
        this.type = type;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getCountProject() {
        return countProject;
    }

    public void setCountProject(int countProject) {
        this.countProject = countProject;
    }
}
