package ru.rutmiit.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import ru.rutmiit.models.entities.Master;
import ru.rutmiit.models.enums.ObjectLevels;
import ru.rutmiit.models.enums.Status;
import ru.rutmiit.utils.validation.ValidDateRange;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@ValidDateRange(
        message = "Дата начала проекта должна быть раньше даты окончания",
        startDateField = "startDate",
        endDateField = "endDate"
)
public class AddProjectDto {
    private String name;
    private String address;
    private String description;
    private String brigadierId;
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
    private Set<String> masterIds = new HashSet<>();

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public Set<String> getMasterIds() {
        return masterIds;
    }

    public void setMasterIds(Set<String> masterIds) {
        this.masterIds = masterIds;
    }

    public AddProjectDto() {
    }

    @NotEmpty(message = "Название проекта не должно быть пустым")
    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    @NotEmpty(message = "Поле адрес должно быть заполнено")
    public String getAddress() { return address;}

    @NotEmpty(message = "Введите небольшое описание")
    public String getDescription(){return description;}

    public void setDescription(String description){this.description = description;}

    public LocalDate getStartDate(){return startDate;}
    public void setStartDate(LocalDate startDate){this.startDate = startDate;}

    public LocalDate getEndDate(){return endDate;}
    public void setEndDate(LocalDate endDate){this.endDate = endDate;}

    public Double getSalary(){return salary;}
    public void setSalary(Double salary){this.salary = salary;}

    public String getSpace(){
        return space;
    }
    public void setSpace(String space){
        this.space = space;
    }

    @NotEmpty(message = "Введите ФИО клиента")
    public String getClientFio(){return client;}

    @NotEmpty(message = "Введите номер телефона или id для связи")
    public String getPhoneClient(){return phoneClient;}
    public void setPhoneClient(String phoneClient){this.phoneClient = phoneClient;}

    public String getEmail(){return email;}
    public void setEmail(String email){this.email = email;}

    public ObjectLevels getType(){return type;}
    public void setType(ObjectLevels type){this.type = type;}

    @NotNull(message = "Выберите на каком этапе проект")
    public Status getStatus(){return status;}

    public void setStatus(Status status) {this.status = status;}

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBrigadierId() {
        return brigadierId;
    }

    public void setBrigadierId(String brigadierId) {
        this.brigadierId = brigadierId;
    }

    public void setClientFio(String clientFio) {
        this.client = clientFio;
    }

}
