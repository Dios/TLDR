/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tldr.sessionBeans;

import cl.tldr.dato.Curso;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author drobles
 */
@Local
public interface HorarioSBLocal {
    
    public void guardaCursos();
}
