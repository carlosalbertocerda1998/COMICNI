package com.example.ccvalle.comicni;
//declaracion de clase java Comic para obtener los objetos Json
public class Comic {
    //Atritibutos de la clase
    //Los atributos de la clase deben coincidir con los atributos de la estructura Json
    //No se omitio ningun atributo ya que podria servir adelante
    protected String month;
    protected int num;
    protected String link;
    protected String year;
    protected String news;
    protected String safe_title;
    protected String transcript;
    protected String alt;
    protected String img;
    protected String title;
    protected String day;

    //Constructor de la clase
    public Comic(String month, int num, String link, String year,
                 String news, String safe_title, String transcript, String alt, String img, String title ,String day) {
        this.month = month;
        this.num = num;
        this.link = link;
        this.year = year;
        this.news = news;
        this.safe_title = safe_title;
        this.transcript = transcript;
        this.alt = alt;
        this.img = img;
        this.title = title;
        this.day = day;
    }


    //Metodo toString() para comprobar que los datos se obtuvieron
    @Override
    public String toString() {
        return "month=" + month + ", num=" + num + ", link="
                + link +  ", year=" + year+ ", news=" + news + ", safe_title=" + safe_title + ", transcript=" + transcript+  ", alt=" + alt
                +  ", img=" + img + ", title=" + title +  ", day=" + day;
    }

}