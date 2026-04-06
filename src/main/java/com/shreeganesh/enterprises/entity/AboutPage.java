package com.shreeganesh.enterprises.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "about_page")
public class AboutPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =======================
       ABOUT INTRO SECTION
    ======================= */

    private String companyName;

    private String subtitle;

    @Column(length = 3000)
    private String description;

    private String aboutImageUrl;

    /* =======================
       BULLET POINTS
    ======================= */

    private String point1;
    private String point2;
    private String point3;
    private String point4;

    /* =======================
       MISSION / VISION / VALUES
    ======================= */

    @Column(length = 1500)
    private String mission;

    @Column(length = 1500)
    private String vision;

    // ❗ renamed from "values" → "coreValues" (MySQL safe)
    @Column(length = 1500)
    private String coreValues;

    /* =======================
       PRODUCT RANGE
    ======================= */



    // 🔹 Constructor
    public AboutPage() {
    }

    // 🔹 Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAboutImageUrl() {
        return aboutImageUrl;
    }

    public void setAboutImageUrl(String aboutImageUrl) {
        this.aboutImageUrl = aboutImageUrl;
    }

    public String getPoint1() {
        return point1;
    }

    public void setPoint1(String point1) {
        this.point1 = point1;
    }

    public String getPoint2() {
        return point2;
    }

    public void setPoint2(String point2) {
        this.point2 = point2;
    }

    public String getPoint3() {
        return point3;
    }

    public void setPoint3(String point3) {
        this.point3 = point3;
    }

    public String getPoint4() {
        return point4;
    }

    public void setPoint4(String point4) {
        this.point4 = point4;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getCoreValues() {
        return coreValues;
    }

    public void setCoreValues(String coreValues) {
        this.coreValues = coreValues;
    }


}
