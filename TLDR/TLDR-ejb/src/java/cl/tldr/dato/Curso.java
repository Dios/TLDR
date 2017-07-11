/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tldr.dato;

import java.util.List;

/**
 *
 * @author drobles
 */
public class Curso {
     String nrc; 
    List<String> nrc_ligados;
    String tipo_actividad;
    String cod_asignatura;
    String seccion;
    String titulo;
    int vacantes;
    String nombre_profesor;
    String horario;
    String modalidad;
    
    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public List<String> getNrc_ligados() {
        return nrc_ligados;
    }

    public void setNrc_ligados(List<String> nrc_ligados) {
        this.nrc_ligados = nrc_ligados;
    }

    public String getTipo_actividad() {
        return tipo_actividad;
    }

    public void setTipo_actividad(String tipo_actividad) {
        this.tipo_actividad = tipo_actividad;
    }

    public String getCod_asignatura() {
        return cod_asignatura;
    }

    public void setCod_asignatura(String cod_asignatura) {
        this.cod_asignatura = cod_asignatura;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getVacantes() {
        return vacantes;
    }

    public void setVacantes(int vacantes) {
        this.vacantes = vacantes;
    }

    public String getNombre_profesor() {
        return nombre_profesor;
    }

    public void setNombre_profesor(String nombre_profesor) {
        this.nombre_profesor = nombre_profesor;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    @Override
    public String toString() {
        return "Curso{" + "nrc=" + nrc + ", nrc_ligados=" + nrc_ligados + ", tipo_actividad=" + tipo_actividad + ", cod_asignatura=" + cod_asignatura + ", seccion=" + seccion + ", titulo=" + titulo + ", vacantes=" + vacantes + ", nombre_profesor=" + nombre_profesor + ", horario=" + horario + ", modalidad=" + modalidad + '}';
    }

}
