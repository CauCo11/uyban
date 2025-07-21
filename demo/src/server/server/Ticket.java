package server;

public class Ticket {
    private final String code;
    private String status;

    public Ticket(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}