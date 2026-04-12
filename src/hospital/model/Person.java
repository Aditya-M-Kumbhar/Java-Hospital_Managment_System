package hospital.model;

public abstract class Person {
    protected int id;
    protected String name;
    protected String phone;

    public Person(int id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public int getId()       { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }

    public abstract void displayDetails();
}
