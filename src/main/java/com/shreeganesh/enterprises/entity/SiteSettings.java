package com.shreeganesh.enterprises.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "site_settings")
public class SiteSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ================= HEADER =================
    private String siteTitle;
    private String logoPath;

    // ================= FOOTER =================
    private String footerCompanyName;

    @Column(length = 1000)
    private String footerDescription;

    private String footerAddress;
    private String footerPhone;
    private String footerEmail;

    // ================= SOCIAL LINKS =================
    private String whatsappUrl;
    private String instagramUrl;
    private String facebookUrl;
    private String linkedinUrl;

    // ================= COPYRIGHT =================
    private String footerCopyright;

    // ================= GETTERS & SETTERS =================

    public Long getId() {
        return id;
    }

    public String getSiteTitle() {
        return siteTitle;
    }

    public void setSiteTitle(String siteTitle) {
        this.siteTitle = siteTitle;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getFooterCompanyName() {
        return footerCompanyName;
    }

    public void setFooterCompanyName(String footerCompanyName) {
        this.footerCompanyName = footerCompanyName;
    }

    public String getFooterDescription() {
        return footerDescription;
    }

    public void setFooterDescription(String footerDescription) {
        this.footerDescription = footerDescription;
    }

    public String getFooterAddress() {
        return footerAddress;
    }

    public void setFooterAddress(String footerAddress) {
        this.footerAddress = footerAddress;
    }

    public String getFooterPhone() {
        return footerPhone;
    }

    public void setFooterPhone(String footerPhone) {
        this.footerPhone = footerPhone;
    }

    public String getFooterEmail() {
        return footerEmail;
    }

    public void setFooterEmail(String footerEmail) {
        this.footerEmail = footerEmail;
    }

    public String getWhatsappUrl() {
        return whatsappUrl;
    }

    public void setWhatsappUrl(String whatsappUrl) {
        this.whatsappUrl = whatsappUrl;
    }

    public String getInstagramUrl() {
        return instagramUrl;
    }

    public void setInstagramUrl(String instagramUrl) {
        this.instagramUrl = instagramUrl;
    }

    public String getFacebookUrl() {
        return facebookUrl;
    }

    public void setFacebookUrl(String facebookUrl) {
        this.facebookUrl = facebookUrl;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getFooterCopyright() {
        return footerCopyright;
    }

    public void setFooterCopyright(String footerCopyright) {
        this.footerCopyright = footerCopyright;
    }
}
