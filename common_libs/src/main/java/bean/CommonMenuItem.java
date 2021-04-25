package bean;

public class CommonMenuItem {
    private String name;
    private int icon;
    private String des;
    private int priority;
    private String url;


    public CommonMenuItem(String name, int icon) {
        this.name = name;
        this.icon = icon;
    }

    public CommonMenuItem(String name, int icon, String url) {
        this.name = name;
        this.icon = icon;
        this.url = url;
    }

    public CommonMenuItem(String name, int icon, String des, int priority, String url) {
        this.name = name;
        this.icon = icon;
        this.des = des;
        this.priority = priority;
        this.url = url;
    }

    public CommonMenuItem(String name, int icon, int priority) {
        this.name = name;
        this.icon = icon;
        this.priority = priority;
    }

    public CommonMenuItem(String name, int icon, String des, int priority) {
        this.name = name;
        this.icon = icon;
        this.des = des;
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
