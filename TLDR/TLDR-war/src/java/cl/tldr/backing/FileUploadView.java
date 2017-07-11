/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cl.tldr.backing;

/**
 *
 * @author drobles
 */
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
 
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

 
@ManagedBean
public class FileUploadView {
    private boolean verPopup = false;
    private UploadedFile file;
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
    public void cerrar(){
        verPopup=false;
    }
    
    public void upload() {
        if(file != null) {
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public boolean isVerPopup() {
        return verPopup;
    }

    public void setVerPopup(boolean verPopup) {
        this.verPopup = verPopup;
    }
    
    
}