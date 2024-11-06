package model;

public class HorariosDia{
    int ID;
    int idPrestador;
    int diaDaSemana;
    String horas;


    public HorariosDia(){
        this.ID = 0;
        this.idPrestador = -1;
        this.diaDaSemana = -1;
        this.horas = "";
    }

    public HorariosDia(int idPrestador, int diaDaSemana, String horas){
        this.ID = 0;
        this.idPrestador = idPrestador;
        this.diaDaSemana = diaDaSemana;
        this.horas = horas;
    }

    public HorariosDia(int ID, int idPrestador, int diaDaSemana, String horas){
        this.ID = ID;
        this.idPrestador = idPrestador;
        this.diaDaSemana = diaDaSemana;
        this.horas = horas;
    }

    public int getID(){
        return this.ID;
    }
    public void setID(int ID){
        this.ID = ID;
    }

    public int getIdPrestador(){
        return this.idPrestador;
    }
    public void setIdPrestador(int idPrestador){
        this.idPrestador = idPrestador;
    }

    public int getDiaDaSemana(){
        return this.diaDaSemana;
    }
    public void setDiaDaSemana(int diaDaSemana){
        this.diaDaSemana = diaDaSemana;
    }

    public String getHoras(){
        return this.horas;
    }
    public void setHoras(String horas){
        this.horas = horas;
    }


    public int[] getHorasArray(){
        if(this.horas.equals("")){
            return new int[0];
        }
        String[] horas = this.horas.split(";");
        int[] horasInt = new int[horas.length];

        for(int i = 0; i < horas.length; i++){
            horas[i] = horas[i].split(":")[0];
            horasInt[i] = Integer.parseInt(horas[i]);
        }

        return horasInt; 
    }

    public int[] addHoraToIntHorasArray(int[] horasInt, int newHora){
        for(int n: horasInt){
            if(n == newHora){
                return horasInt;
            }
        }

        int[] newHorasInt = new int[horasInt.length + 1];

        for(int i = 0; i < horasInt.length; i++){
            newHorasInt[i] = horasInt[i];
        }

        newHorasInt[newHorasInt.length-1] = newHora;
        
        return newHorasInt;
    }

    public int[] sort(int[] arr){
        int aux;
        for(int i = 0; i < arr.length; i++){
            for(int j = i; j < arr.length; j++){
                if(arr[i] > arr[j]){
                    aux = arr[i];
                    arr[i] = arr[j];
                    arr[j] = aux;
                }
            }
        }
        return arr;
    }

    public String generateNewHorasString(int[] horasInt){
        String aux = "";
        for(int i = 0; i < horasInt.length; i++){
            aux += horasInt[i] + ":00;";
        }
        return aux;
    }

    public boolean addHora(int hora){
        if(hora < 0 || hora > 23){
            return false;
        }
        int[] horasInt = this.getHorasArray();
        horasInt = this.addHoraToIntHorasArray(horasInt, hora);
        horasInt = this.sort(horasInt);
        if(horasInt.length == this.getHorasArray().length){
            return false;
        }
        this.horas = this.generateNewHorasString(horasInt);
        return true;
    }

    public boolean removeHora(int hora){
        if(hora < 0 || hora > 23){
            return false;
        }
        int[] horasInt = this.getHorasArray();
        int index = 0;
        
        int count = 0;

        for(int n: horasInt){
            if(n == hora){
                count++;
            }
        }
        
        int[] newHorasInt = new int[horasInt.length - count];

        for(int i = 0; i < horasInt.length; i++){
            if(horasInt[i] != hora){
                newHorasInt[index] = horasInt[i];
                index++;
            }
        }
        if(newHorasInt.length == this.getHorasArray().length){
            return false;
        }
        this.horas = this.generateNewHorasString(newHorasInt);
        return true;
    }

    public String toString(){
        String[] diasDaSemana = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
        return "ID: " + this.ID + "\n" +
                "idPrestador: " + this.idPrestador + "\n" +
                "diaDaSemana: " + this.diaDaSemana + " (" + diasDaSemana[this.diaDaSemana] + ")\n" +
                "horas: " + this.horas + "\n";
    }
    public String toJson(){
        return "{" +
                "\"ID\": " + this.ID + ", " +
                "\"idPrestador\": " + this.idPrestador + ", " +
                "\"diaDaSemana\": " + this.diaDaSemana + ", " +
                "\"horas\": \"" + this.horas + "\"" +
                "}";
    }
    
}