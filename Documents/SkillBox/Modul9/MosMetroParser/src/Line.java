import com.google.gson.annotations.SerializedName;

class Line{
    private String id;
    @SerializedName("name")
    private String nameLine;

    Line(String id, String nameLine) {
        this.id = id;
        this.nameLine = nameLine;
    }

    public String getId(){
        return id;
    }

    public String getNameLine(){
        return nameLine;
    }
}

