package proyecto.vista;

import javax.swing.JComboBox;
import proyecto.gestion.GestionResponsables;

/** Selector editable para reutilizar nombres o escribir un nuevo responsable. */
class SelectorResponsable extends JComboBox<String> {
    SelectorResponsable() { setEditable(true); actualizar(); }
    void actualizar() {
        String seleccionado = getText();
        removeAllItems();
        for (String nombre : new GestionResponsables().listar()) addItem(nombre);
        setText(seleccionado);
    }
    String getText() { Object valor = getEditor().getItem(); return valor == null ? "" : valor.toString().trim(); }
    void setText(String valor) { setSelectedItem(valor == null ? "" : valor); }
    @Override public void showPopup() { actualizar(); super.showPopup(); }
}
