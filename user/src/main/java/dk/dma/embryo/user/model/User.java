package dk.dma.embryo.user.model;

import java.util.Objects;

/**
 * Created by Steen on 17-03-2016.
 *
 */
public class User {

    private String maritimeCloudId;
    private String login;
    private String password;
    private String role;
    private Long shipMmsi;
    private String email;
    private String aisFilterName;

    public String getMaritimeCloudId() {
        return maritimeCloudId;
    }

    public void setMaritimeCloudId(String maritimeCloudId) {
        this.maritimeCloudId = maritimeCloudId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Long getShipMmsi() {
        return shipMmsi;
    }

    public void setShipMmsi(Long shipMmsi) {
        this.shipMmsi = shipMmsi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAisFilterName() {
        return aisFilterName;
    }

    public void setAisFilterName(String aisFilterName) {
        this.aisFilterName = aisFilterName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(maritimeCloudId, user.maritimeCloudId) &&
                Objects.equals(login, user.login) &&
                Objects.equals(password, user.password) &&
                Objects.equals(role, user.role) &&
                Objects.equals(shipMmsi, user.shipMmsi) &&
                Objects.equals(email, user.email) &&
                Objects.equals(aisFilterName, user.aisFilterName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maritimeCloudId, login, password, role, shipMmsi, email, aisFilterName);
    }
}
