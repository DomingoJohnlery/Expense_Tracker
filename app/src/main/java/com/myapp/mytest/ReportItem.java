package com.myapp.mytest;

public class ReportItem {
    private int id;  // Assuming you have an ID field
    private String type;
    private String category;
    private String account;
    private String money;
    private String note;
    private String date;
    private int icon;

    public ReportItem(int id, String type, String category, String account, String money, String note,String date, int icon) {
        this.id = id;
        this.type = type;
        this.category = category;
        this.account = account;
        this.money = money;
        this.note = note;
        this.date = date;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }
    public String getType() { return type; }

    public String getCategory() {
        return category;
    }

    public String getAccount() {
        return account;
    }

    public String getMoney() {
        return money;
    }
    public String getNote() { return note; }
    public String getDate() { return date; }

    public int getIcon() {
        return icon;
    }

    // You may also want to override equals() and hashCode() for proper DiffUtil comparison
    // ...

    // Example equals() method:
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ReportItem that = (ReportItem) obj;

        return id == that.id;
    }

}

