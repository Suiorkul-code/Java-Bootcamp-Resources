package model;

public class Campaign {

    private String id;
    private String name;

    public Campaign(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Campaign{id='" + id + "', name='" + name + "'}";
    }

}
