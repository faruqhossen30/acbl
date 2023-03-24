package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TestUser {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("height")
    @Expose
    private Double height;
    @SerializedName("weight")
    @Expose
    private Double weight;
    @SerializedName("religion")
    @Expose
    private String religion;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("nid")
    @Expose
    private String nid;
    @SerializedName("driving_license")
    @Expose
    private String drivingLicense;
    @SerializedName("present_address")
    @Expose
    private String presentAddress;
    @SerializedName("permanent_address")
    @Expose
    private String permanentAddress;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("reff_name")
    @Expose
    private String reffName;
    @SerializedName("reff_number")
    @Expose
    private String reffNumber;
    @SerializedName("job_location")
    @Expose
    private String jobLocation;
    @SerializedName("experience")
    @Expose
    private String experience;
    @SerializedName("experience_type")
    @Expose
    private String experienceType;
    @SerializedName("customer_type")
    @Expose
    private String customerType;
    @SerializedName("car_category")
    @Expose
    private String carCategory;
    @SerializedName("expected_salary")
    @Expose
    private Integer expectedSalary;
    @SerializedName("driver_category_id")
    @Expose
    private Integer driverCategoryId;
    @SerializedName("language")
    @Expose
    private String language;
    @SerializedName("father_name")
    @Expose
    private String fatherName;
    @SerializedName("mother_name")
    @Expose
    private String motherName;
    @SerializedName("emergency_contact")
    @Expose
    private String emergencyContact;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getDrivingLicense() {
        return drivingLicense;
    }

    public void setDrivingLicense(String drivingLicense) {
        this.drivingLicense = drivingLicense;
    }

    public String getPresentAddress() {
        return presentAddress;
    }

    public void setPresentAddress(String presentAddress) {
        this.presentAddress = presentAddress;
    }

    public String getPermanentAddress() {
        return permanentAddress;
    }

    public void setPermanentAddress(String permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getReffName() {
        return reffName;
    }

    public void setReffName(String reffName) {
        this.reffName = reffName;
    }

    public String getReffNumber() {
        return reffNumber;
    }

    public void setReffNumber(String reffNumber) {
        this.reffNumber = reffNumber;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getExperienceType() {
        return experienceType;
    }

    public void setExperienceType(String experienceType) {
        this.experienceType = experienceType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getCarCategory() {
        return carCategory;
    }

    public void setCarCategory(String carCategory) {
        this.carCategory = carCategory;
    }

    public Integer getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(Integer expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public Integer getDriverCategoryId() {
        return driverCategoryId;
    }

    public void setDriverCategoryId(Integer driverCategoryId) {
        this.driverCategoryId = driverCategoryId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
