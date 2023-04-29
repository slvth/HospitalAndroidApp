package com.example.hospitalandroidapp.database;

public class RecordReceptionModel {
    private int record_id;
    private int doctor_id;
    private int patient_id;
    private String date;
    private String doctorFIO; //дополнительный столбец "Врач" ФИО

    public RecordReceptionModel() {
    }

    public RecordReceptionModel(int record_id, int doctor_id, int patient_id, String date) {
        this.record_id = record_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.date = date;
    }

    public RecordReceptionModel(int record_id, int doctor_id, int patient_id, String date, String doctorFIO) {
        this.record_id = record_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.date = date;
        this.doctorFIO = doctorFIO;
    }

    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(int doctor_id) {
        this.doctor_id = doctor_id;
    }

    public int getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(int patient_id) {
        this.patient_id = patient_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoctorFIO() {
        return doctorFIO;
    }

    public void setDoctorFIO(String doctorFIO) {
        this.doctorFIO = doctorFIO;
    }
}
