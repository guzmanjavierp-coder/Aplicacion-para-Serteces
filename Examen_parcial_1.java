/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package examen_parcial_1;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author victus
 */
public class Examen_parcial_1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Usuario u1 = new Usuario("Gustav", 18);
        Usuario u2 = new Usuario("Fisi", 19);
        Usuario u3 = new Usuario("Denis", 20);
        Usuario u4 = new Usuario("Carcamo", 18);

        Bibliotecario b1 = new Bibliotecario("Cindy", "Intro. Progra");
        Bibliotecario b2 = new Bibliotecario("Succini", "Ciencia");

        List<Usuario> listaClub1 = new ArrayList<>();
        listaClub1.add(u1);
        listaClub1.add(u2);

        List<Usuario> listaClub2 = new ArrayList<>();
        listaClub2.add(u3);
        listaClub2.add(u4);

        ClubDeLectura club1 = new ClubDeLectura("Club de Novela", listaClub1, b1);
        ClubDeLectura club2 = new ClubDeLectura("Club de Ciencia", listaClub2, b2);

        club1.mostrarClub();
        System.out.println();
        club2.mostrarClub();

        u1.setEdad(21);
        System.out.println();
        System.out.println("Edad modificada de " + u1.getNombre() + ": " + u1.getEdad());
    }
}
        

    
    

