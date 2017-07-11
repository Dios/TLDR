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
import cl.tldr.dato.Cabecera;
import cl.tldr.dato.Curso;
import cl.tldr.util.Util;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean
@ViewScoped
public class ScheduleView implements Serializable {

    private ScheduleModel eventModel;
    private ScheduleEvent event = new DefaultScheduleEvent("Test", new GregorianCalendar(2017, 4, 1).getTime(), new GregorianCalendar(2017, 4, 1).getTime(), "eventoMat");
    private List<String> cursosSeleccionados = new ArrayList();
    private List<Curso> listaCursos = new ArrayList();
    List<Curso> seleccionarCurso;
    List<Curso> cursosTotales = new ArrayList();
    String rutaExcel = "";
    UploadedFile excel;
    private boolean verPopup = false;
    private String sessionId = "";

    @PostConstruct
    public void init() {

        excel = null;
        eventModel = new DefaultScheduleModel();
        //rutaExcel = "c:\\mapas\\";
        rutaExcel = "/usr/local/archivos/";
    }

    public void getSessionID() {
        FacesContext fCtx = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
        sessionId = session.getId();
    }

    public void cargarHorario(String nombreArchivo) {
        //Resetea las variables
        eventModel.clear();
        listaCursos.clear();

        listaCursos = obtieneListaCursos(nombreArchivo);
        cursosTotales.addAll(listaCursos);
        seleccionarCurso = new ArrayList();
        for (Curso curso : listaCursos) {
            boolean contiene = false;
            for (Curso cur : seleccionarCurso) {
                if (curso.getTitulo().toUpperCase().trim().equals(cur.getTitulo().toUpperCase().trim())) {
                    contiene = true;
                    break;
                }

            }
            if (!contiene) {
                seleccionarCurso.add(curso);
            }
        }
        Collections.sort(seleccionarCurso, new Comparator<Curso>() {
            @Override
            public int compare(Curso s1, Curso s2) {
                return s1.getTitulo().compareToIgnoreCase(s2.getTitulo());
            }
        });
        for (Curso curso : listaCursos) {
            if (curso.getHorario() != null) {
                String[] horario = curso.getHorario().trim().split(";");
                for (String str : horario) {
                    String[] detHorario = str.trim().split(" "); //0: Dia, 1:Hora 1, 3:Hora 2, 4:Sala
                    eventModel.addEvent(obtenerEvento(curso.getNrc() + "-" + curso.getTitulo(), detHorario[0], detHorario[1], detHorario[3], curso));
                }
            }
        }

    }

    public ScheduleEvent obtenerEvento(String nombre, String dia, String horaIni, String horaFin, Object data) {
        int diaEvento = 0;
        if (dia.toUpperCase().equals("LU")) {
            diaEvento = 1;
        } else if (dia.toUpperCase().equals("MA")) {
            diaEvento = 2;
        } else if (dia.toUpperCase().equals("MI")) {
            diaEvento = 3;
        } else if (dia.toUpperCase().equals("JU")) {
            diaEvento = 4;
        } else if (dia.toUpperCase().equals("VI")) {
            diaEvento = 5;
        } else if (dia.toUpperCase().equals("SA")) {
            diaEvento = 6;
        } else if (dia.toUpperCase().equals("DO")) {
            diaEvento = 7;
        }
        String[] splitHoraInit = horaIni.split(":");
        Calendar fecIni = new GregorianCalendar(2017, 4, diaEvento);
        fecIni.set(Calendar.HOUR, Integer.valueOf(splitHoraInit[0]));
        fecIni.set(Calendar.MINUTE, Integer.valueOf(splitHoraInit[1]));
        String[] splitHoraFin = horaFin.split(":");
        Calendar fecFin = new GregorianCalendar(2017, 4, diaEvento);
        fecFin.set(Calendar.HOUR, Integer.valueOf(splitHoraFin[0]));
        fecFin.set(Calendar.MINUTE, Integer.valueOf(splitHoraFin[1]));
        ScheduleEvent evento = null;
        DefaultScheduleEvent aux = new DefaultScheduleEvent(nombre, fecIni.getTime(), fecFin.getTime(), "eventoMat");
        aux.setData(data);
        evento = aux;
        return evento;
    }

    public List<Curso> obtieneListaCursos(String nombreArchivo) {
        FileInputStream inputStream = null;
        List<Curso> listaCursos = new ArrayList();
        try {
            List<Cabecera> cabeceras = new ArrayList();
            //  String excelFilePath = "/usr/local/archivos/test.xlsx";
            String excelFilePath = rutaExcel + nombreArchivo;
            inputStream = new FileInputStream(new File(excelFilePath));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            int i = 0;
            boolean leerCeldas = true;
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                if (nextRow.getCell(0).getCellTypeEnum().equals(nextRow.getCell(0).getCellTypeEnum().STRING)) {
                    if (nextRow.getCell(0).getStringCellValue().trim().equals("NRC")) {
                        int j = 0;
                        for (Cell cell : nextRow) {
                            if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                                Cabecera cabecera = new Cabecera();
                                cabecera.setNombre(cell.getStringCellValue());
                                cabecera.setPosicion(j);
                                cabeceras.add(cabecera);
                            }
                            j++;
                        }
                        leerCeldas = false;
                    }
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                i = 0;
                Curso curso = new Curso();
                while (cellIterator.hasNext() && leerCeldas) {
                    Cell cell = cellIterator.next();
                    //Set NRC
                    if (i == getPosicionMatriz(cabeceras, "NRC")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().NUMERIC)) {
                            Double nro = cell.getNumericCellValue();
                            curso.setNrc(String.valueOf(nro.intValue()));
                        } else if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setNrc(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "NRC LIGADOS")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            String[] nrcLigadosSplit = cell.getStringCellValue().trim().split(";");
                            List<String> listNrc = new ArrayList<>();
                            for (String str : nrcLigadosSplit) {
                                listNrc.add(str);
                            }
                            curso.setNrc_ligados(listNrc);
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "TIPO\n" + "ACTIVIDAD")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setTipo_actividad(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "CODIGO\n" + "ASIGNATURA")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setCod_asignatura(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "SECCION")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setSeccion(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "TITULO")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setTitulo(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "VACANTES")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().NUMERIC)) {
                            Double nro = cell.getNumericCellValue();
                            curso.setVacantes(nro.intValue());

                        }
                    } else if (i == getPosicionMatriz(cabeceras, "NOMBRE PROFESOR")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setNombre_profesor(cell.getStringCellValue());
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "HORARIO")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setHorario(cell.getStringCellValue().replace("\n", " "));
                        }
                    } else if (i == getPosicionMatriz(cabeceras, "MODALIDAD")) {
                        if (cell.getCellTypeEnum().equals(cell.getCellTypeEnum().STRING)) {
                            curso.setModalidad(cell.getStringCellValue());
                        }
                    }
                    i++;
                }

                leerCeldas = true;
                if (curso.getNrc() != null) {
                    listaCursos.add(curso);
                }
            }
            workbook.close();
            inputStream.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScheduleView.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(ScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return listaCursos;
    }

    public int getPosicionMatriz(List<Cabecera> cabeceras, String nombre) {
        for (Cabecera cabecera : cabeceras) {
            if (cabecera.getNombre().equals(nombre)) {
                return cabecera.getPosicion();
            }
        }
        return -1;
    }

    public void cargarExcel(FileUploadEvent eventoCargar) {
        UploadedFile uploadedFile = eventoCargar.getFile();
        System.out.println("excel " + uploadedFile);
        if (uploadedFile != null) {
            if (rutaExcel.equals("")) {
                //ruta = parametrosFacadeLocal.buscarDominioCodigo("RUTA_MAPA_CRUCE", "1").getCodigo2();
            }
            if (sessionId.equals("")) {
                getSessionID();
            }
            String nombreArchivo = "excel-" + sessionId + uploadedFile.getFileName().substring(uploadedFile.getFileName().lastIndexOf('.'));
            System.out.println(nombreArchivo);
            try {
                Util.copyFile(rutaExcel, nombreArchivo, uploadedFile.getInputstream());
                cargarHorario(nombreArchivo);
            } catch (IOException ex) {
                Logger.getLogger(ScheduleView.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public Date getRandomDate(Date base) {
        Calendar date = Calendar.getInstance();
        date.setTime(base);
        date.add(Calendar.DATE, ((int) (Math.random() * 30)) + 1);    //set random day of month

        return date.getTime();
    }

    public void cargarSeleccionados() {
        eventModel.clear();
        eventModel = new DefaultScheduleModel();
        listaCursos.clear();
        for (String curso : cursosSeleccionados) {
            for (Curso cursoOri : cursosTotales) {
                if (curso.toUpperCase().trim().equals(cursoOri.getTitulo().toUpperCase().trim())) {
                    listaCursos.add(cursoOri);
                }
            }
        }
        for (Curso curso : listaCursos) {
            if (curso.getHorario() != null) {
                String[] horario = curso.getHorario().trim().split(";");
                for (String str : horario) {
                    String[] detHorario = str.trim().split(" "); //0: Dia, 1:Hora 1, 3:Hora 2, 4:Sala
                    eventModel.addEvent(obtenerEvento(curso.getNrc() + "-" + curso.getTitulo(), detHorario[0], detHorario[1], detHorario[3], curso));
                }
            }
        }
    }

    public Date getInitialDate() {
        Calendar t = new GregorianCalendar(2017, 4, 1, 0, 0, 0);
        return t.getTime();
    }

    public ScheduleModel getEventModel() {
        return eventModel;
    }

    public ScheduleEvent getEvent() {
        return event;
    }

    public void setEvent(ScheduleEvent event) {
        this.event = event;
    }

    public void cerrar() {
        verPopup = false;
    }

    public void onEventSelect(SelectEvent selectEvent) {
        verPopup = true;
        if (selectEvent != null) {
            event = (ScheduleEvent) selectEvent.getObject();
        }
    }

    public void onEventMove(ScheduleEntryMoveEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event moved", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    public void onEventResize(ScheduleEntryResizeEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Event resized", "Day delta:" + event.getDayDelta() + ", Minute delta:" + event.getMinuteDelta());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<String> getCursosSeleccionados() {
        return cursosSeleccionados;
    }

    public void setCursosSeleccionados(List<String> cursosSeleccionados) {
        this.cursosSeleccionados = cursosSeleccionados;
    }

    public List<Curso> getListaCursos() {
        return listaCursos;
    }

    public void setListaCursos(List<Curso> listaCursos) {
        this.listaCursos = listaCursos;
    }

    public List<Curso> getSeleccionarCurso() {
        return seleccionarCurso;
    }

    public void setSeleccionarCurso(List<Curso> seleccionarCurso) {
        this.seleccionarCurso = seleccionarCurso;
    }

    public String getRutaExcel() {
        return rutaExcel;
    }

    public void setRutaExcel(String rutaExcel) {
        this.rutaExcel = rutaExcel;
    }

    public UploadedFile getExcel() {
        return excel;
    }

    public void setExcel(UploadedFile excel) {
        System.out.println("this " + excel);
        this.excel = excel;
    }

    public boolean isVerPopup() {
        return verPopup;
    }

    public void setVerPopup(boolean verPopup) {
        this.verPopup = verPopup;
    }

}
