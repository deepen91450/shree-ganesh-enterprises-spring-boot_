package com.shreeganesh.enterprises.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contact_settings")
public class ContactSettings {

    @Id
    private Long id;   // MANUALLY CONTROLLED (ALWAYS 1)

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_primary", nullable = false)
    private String phone1;

    @Column(name = "phone_secondary")
    private String phone2;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String whatsapp;
    private String instagram;
    private String facebook;
    private String linkedin;

    // ===== GETTERS & SETTERS =====

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone1() { return phone1; }
    public void setPhone1(String phone1) { this.phone1 = phone1; }

    public String getPhone2() { return phone2; }
    public void setPhone2(String phone2) { this.phone2 = phone2; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getWhatsapp() { return whatsapp; }
    public void setWhatsapp(String whatsapp) { this.whatsapp = whatsapp; }

    public String getInstagram() { return instagram; }
    public void setInstagram(String instagram) { this.instagram = instagram; }

    public String getFacebook() { return facebook; }
    public void setFacebook(String facebook) { this.facebook = facebook; }

    public String getLinkedin() { return linkedin; }
    public void setLinkedin(String linkedin) { this.linkedin = linkedin; }
}
