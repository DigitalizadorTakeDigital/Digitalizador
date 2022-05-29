package br.com.webscanner.util;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarioUtils {    
       
      public static int getDia(int mDia){
       Calendar dataInicial = Calendar.getInstance();
       dataInicial.add(Calendar.DATE, mDia);
       int dia;
       String lista = new String(dataInicial.getTime().toString());
       String [] anual = lista.split(" ");
       dia = Integer.parseInt(anual[2]);
       return dia;
     }
      
      public static String pastaMes(String mes) {
    	  String mMes =  null;
    	  switch (mes){
          case "01" :
              mMes = "Janeiro";
              break;
          case "02":
        	  mMes = "Fevereiro"; 
              break;
          case "03":
        	  mMes = "Marco";
              break;
          case "04":
        	  mMes = "Abril";
              break;
          case "05":
        	  mMes = "Maio";
              break;
          case "06":
        	  mMes = "Junho";
              break;
          case "07":
        	  mMes = "Julho";
               break;
          case "08":
        	  mMes = "Agosto";
              break;
          case "09":
        	  mMes = "Setembro";
               break;
          case "10":
        	  mMes = "Outubro";
              break;
          case "11":
        	  mMes = "Novembro";
               break;
          case "12":
        	  mMes = "Dezembro";
               break;
      }
         return mMes;
      }
   
      
      public static int getMes(int mMes){
      Calendar dataInicial = Calendar.getInstance();
      dataInicial.add(Calendar.DATE, mMes);
      int mes =  0;
      String mesString;
      String lista = new String(dataInicial.getTime().toString());
      String [] anual = lista.split(" ");
      mesString = String.valueOf(anual[1]);
      switch (mesString){
          case "Jan" :
              mes = 1;
              break;
          case "Feb":
              mes = 2;
              break;
          case "Mar":
              mes = 3;
              break;
          case "Apr":
              mes = 4;
              break;
          case "May":
              mes = 5;
               break;
          case "Jun":
              mes = 6;
               break;
          case "Jul":
              mes = 7;
               break;
          case "Aug":
              mes = 8;
               break;
          case "Sep":
              mes = 9;
               break;
          case "Oct":
              mes = 10;
              break;
          case "Nov":
              mes = 11;
               break;
          case "Dec":
              mes = 12;
               break;
      }
         return mes;
   }
   
      public static int getAno(int mAno){
       Calendar dataInicial = Calendar.getInstance();
       dataInicial.add(Calendar.DATE, mAno);             
       int ano;
       String lista = new String(dataInicial.getTime().toString());
       String [] anual = lista.split(" ");
       ano = Integer.parseInt(anual[5]);
       return ano;
      }
   
      public static int getHora(int mHora){
       Calendar dataInicial = Calendar.getInstance();
       dataInicial.add(Calendar.DATE, mHora);        
       int hora;
       String lista = new String(dataInicial.getTime().toString());
       String [] anual = lista.split(" ");
       String [] horas = anual[3].split(":");
       hora = Integer.parseInt(horas[0]);     
       return hora;       
   }   
      
         
}
