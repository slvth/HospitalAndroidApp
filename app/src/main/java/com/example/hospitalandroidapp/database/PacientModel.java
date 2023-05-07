package com.example.hospitalandroidapp.database;

import android.app.Application;

public class PacientModel  {

    private int pacient_id;
    private String surname;
    private String name;
    private String middleName;

    private String gender;

    private String date;
    private String phone;
    private String passport;
    private String policyOMS;
    private String SNILS;
    private String addressRegistration; //адрес регистрации
    private String addressResidence ; //адрес проживания

    public PacientModel() {
    }

    public PacientModel(int pacient_id, String surname, String name,
                        String middleName, String gender, String date, String phone,
                        String passport, String policyOMS, String SNILS,
                        String addressRegistration, String addressResidence) {
        this.pacient_id = pacient_id;
        this.surname = surname;
        this.name = name;
        this.middleName = middleName;
        this.gender = gender;
        this.date = date;
        this.phone = phone;
        this.passport = passport;
        this.policyOMS = policyOMS;
        this.SNILS = SNILS;
        this.addressRegistration = addressRegistration;
        this.addressResidence = addressResidence;
    }

    public int getPacient_id() {
        return pacient_id;
    }

    public void setPacient_id(int pacient_id) {
        this.pacient_id = pacient_id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public String getPolicyOMS() {
        return policyOMS;
    }

    public void setPolicyOMS(String policyOMS) {
        this.policyOMS = policyOMS;
    }

    public String getSNILS() {
        return SNILS;
    }

    public void setSNILS(String SNILS) {
        this.SNILS = SNILS;
    }

    public String getAddressRegistration() {
        return addressRegistration;
    }

    public void setAddressRegistration(String addressRegistration) {
        this.addressRegistration = addressRegistration;
    }

    public String getAddressResidence() {
        return addressResidence;
    }

    public void setAddressResidence(String addressResidence) {
        this.addressResidence = addressResidence;
    }

    public String getFio(){
        return surname+" "+name+" "+middleName;
    }
}
