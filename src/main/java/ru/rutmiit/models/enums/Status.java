package ru.rutmiit.models.enums;
public enum Status {
    NotStarted(1, "Не начато"),
    InProgress(2, "В процессе"),
    Completed(3, "Завершено");

    private final int value;
    private final String russian;

    Status(int value, String russian) {
        this.value = value;
        this.russian = russian;
    }

    public int getValue() {
        return value;
    }

    public String getRussian() {
        return russian;
    }
}
