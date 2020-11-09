public class Actor {

    String id;
    String name;
    String birthyear;

    public Actor(){}

    public Actor(String name, String birthyear) {
        this.name = name;
        this.birthyear = birthyear;
        this.id = null;
    }

    public Actor(String name, String birthyear, String id) {
        this.name = name;
        this.birthyear = birthyear;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBirthyear() {
        return birthyear;
    }

    public void setId(String id) { this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthyear(String birthyear) {
        this.birthyear = birthyear;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Actor Details - ");
        sb.append("Name: " + getName());
        sb.append(", Birthyear: " + getBirthyear());

        return sb.toString();
    }
}