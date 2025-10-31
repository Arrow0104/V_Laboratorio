package Domain.Dtos.cars;

public class UpdateCarRequestDto {
    private Long id;
    private String make;
    private String model;
    private int year;
    private Long ownerId;

    public UpdateCarRequestDto() {}

    public UpdateCarRequestDto(Long id, String make, String model, int year, Long ownerId) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.year = year;
        this.ownerId = ownerId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
}