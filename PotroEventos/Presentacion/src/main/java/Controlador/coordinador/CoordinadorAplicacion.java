package Controlador.coordinador;

import Controlador.interfaz.ICoordinadorAplicacion;
import Controlador.interfaz.ICoordinadorDevolucion;
import InicioSesionEmpleado.FachadaInicioSesionEmpleado;
import InicioSesionEmpleado.IInicioSesionEmpleado;
import Pantallas.FrmAsistencias;
import Pantallas.FrmCamara;
import Pantallas.FrmConsultarEventosActuales;
import Pantallas.FrmDatosFacturar;
import Pantallas.FrmInicioSesion;
import Pantallas.FrmPago;
import Pantallas.FrmPlantillaSistema;
import Pantallas.FrmRegistrarse;
import Pantallas.FrmDetallesCompra;
import Pantallas.FrmRegistroItson;
import Pantallas.vistas.PnlConsultar;
import Pantallas.vistas.PnlConsultarEvento;
import Pantallas.vistas.PnlConsultarMenu;
import Pantallas.vistas.PnlEventos;
import Pantallas.vistas.dialogos.DlgBuscarPerfil;
import Pantallas.vistas.dialogos.DlgDetalleFactura;
import dtos.AsientoDTO;
import dtos.AsientoEventoDTO;
import dtos.AsistenciaDTO;
import dtos.BoletoDTO;
import dtos.CategoriaDTO;
import dtos.CobroDTO;
import dtos.EmpleadoDTO;
import dtos.EventoDTO;
import dtos.LoginDTO;
import dtos.PagoDTO;
import dtos.RegistroUsuarioDTO;
import dtos.ReporteAsistenciaDTO;
import dtos.ReservacionDTO;
import dtos.SeccionDTO;
import dtos.TarjetaDTO;
import dtos.UsuarioDTO;
import dtos.UsuarioInstitucionalDTO;
import inicioSesion.IFachadaInicioSesion;
import inicioSesion.InicioSesionFachada;
import excepciones.CompraBoletoException;
import excepciones.CoordinadorException;
import excepciones.EmpleadoException;
import excepciones.GestionEventoException;
import excepciones.GestionUsuarioException;
import excepciones.InicioSesionException;
import excepciones.RevisionBoletosException;
import compraBoleto.CompraBoletoFachada;
import compraBoleto.ICompraBoleto;
import gestionEvento.GestionEventoFachada;
import gestionEvento.IFachadaGestionEvento;
import gestionUsuarios.GestionUsuarioFachada;
import gestionUsuarios.IGestionUsuariosFachada;
import RevisionBoletos.IRevisionBoletos;
import RevisionBoletos.RevisionBoletos;
import dtos.FacturaDTO;
import dtos.PerfilFiscalDTO;
import excepciones.FacturaException;
import factura.FachadaFactura;
import factura.IFactura;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

public class CoordinadorAplicacion implements ICoordinadorAplicacion {

    private final IFachadaInicioSesion controlInicioSesion = InicioSesionFachada.getInstance();
    private final ICompraBoleto controlCompra = new CompraBoletoFachada();
    private final IFachadaGestionEvento controlEvento = new GestionEventoFachada();
    private final IGestionUsuariosFachada controlUsuarios = new GestionUsuarioFachada();
    private final IRevisionBoletos controlRevision = new RevisionBoletos();
    private final IInicioSesionEmpleado controlEmpleados = new FachadaInicioSesionEmpleado();
    private final ICoordinadorDevolucion coordinadorDevolucion = new CoordinadorDevolucion(this);
    private final IFactura controlFactura = new FachadaFactura();
    
    private FrmAsistencias frmAsistencias;
    private FrmCamara frmCamara;
    private FrmConsultarEventosActuales frmConsultarEventosActuales;
    private FrmInicioSesion frmInicioSesion;
    private FrmRegistrarse frmRegistrarse;
    private FrmPago frmPago;
    private FrmPlantillaSistema frmPlantilla;
    private FrmDetallesCompra frmDetalles;
    private FrmRegistroItson frmRegistro;
    private ReservacionDTO reservacionActual;
    private FrmDatosFacturar frmDatosFacturar;
    private DlgBuscarPerfil dlgBuscarPerfil;
    private DlgDetalleFactura dlgResumenFactura;

    private void ocultarTodo() {
        if (frmInicioSesion != null) {
            frmInicioSesion.setVisible(false);
        }
        if (frmRegistrarse != null) {
            frmRegistrarse.setVisible(false);
        }
        if (frmPago != null) {
            frmPago.setVisible(false);
        }
        if (frmDetalles != null) {
            frmDetalles.setVisible(false);
        }
        if (frmRegistro != null) {
            frmRegistro.setVisible(false);
        }
        if (frmPlantilla != null) {
            frmPlantilla.setVisible(false);
        }
        if (frmConsultarEventosActuales != null) {
            frmConsultarEventosActuales.setVisible(false);
        }
    }

    @Override
    public void iniciaSistema() {
        if (frmInicioSesion == null) {
            frmInicioSesion = new FrmInicioSesion(this);
        }
        frmInicioSesion.setVisible(true);
        frmInicioSesion.setLocationRelativeTo(null);
    }

    @Override
    public void mostrarInicioSesion() {
        ocultarTodo();
        if (frmPlantilla != null) {
            frmPlantilla.dispose();
            frmPlantilla = null;
        }
        if (frmInicioSesion == null) {
            frmInicioSesion = new FrmInicioSesion(this);
        }
        frmInicioSesion.limpiarCampos();
        frmInicioSesion.setVisible(true);
        frmInicioSesion.setLocationRelativeTo(null);
    }

    @Override
    public void mostrarRegistro() {
        ocultarTodo();
        if (frmRegistrarse == null) {
            frmRegistrarse = new FrmRegistrarse(this);
        }
        frmRegistrarse.setVisible(true);
        frmRegistrarse.setLocationRelativeTo(null);
    }

    @Override
    public void mostrarInicio() {
        ocultarTodo();
        if (frmPlantilla == null) {
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        frmPlantilla.ocultarInicio();
        frmPlantilla.mostrarConsultar();
        frmPlantilla.setCategorias();
        frmPlantilla.setCreditos(getUsuarioSesion().getCreditos().toString()); 
        frmPlantilla.setVisible(true);
        if (frmInicioSesion != null) {
            frmInicioSesion.dispose();
        }
    }

    @Override
    public void mostrarConsultar(String tipoEvento) {
        ocultarTodo();
        if (frmPlantilla == null) {
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        frmPlantilla.ocultarConsultar();
        frmPlantilla.mostrarInicio();
        frmPlantilla.setCreditos(getUsuarioSesion().getCreditos().toString()); 
        frmPlantilla.setContenido(new PnlConsultar(this, tipoEvento));
        frmPlantilla.setVisible(true);
    }

    @Override
    public void mostrarConsultarMenu() {
        ocultarTodo();
        if (frmPlantilla == null) {
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        frmPlantilla.ocultarConsultar();
        frmPlantilla.mostrarInicio();
        frmPlantilla.setCreditos(getUsuarioSesion().getCreditos().toString()); 
        frmPlantilla.setContenido(new PnlConsultarMenu(this));
        frmPlantilla.setVisible(true);
    }

    @Override
    public void mostrarInfoEvento(EventoDTO evento) {
        ocultarTodo();
        if (frmPlantilla == null) {
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        frmPlantilla.mostrarConsultar();
        frmPlantilla.mostrarInicio();
        frmPlantilla.setCreditos(getUsuarioSesion().getCreditos().toString()); 
        frmPlantilla.setContenido(new PnlConsultarEvento(this, evento));
        frmPlantilla.setVisible(true);
    }

    @Override
    public void mostrarDetalles(ReservacionDTO reservacion) {
        ocultarTodo();
        if (frmDetalles == null) {
            frmDetalles = new FrmDetallesCompra(this, reservacion);
        } else {
            frmDetalles.setReservacion(reservacion);
        }
        frmDetalles.setVisible(true);
    }

    @Override
    public void mostarRegistroITSON() {
        ocultarTodo();
        frmRegistro.setVisible(true);
    }

    @Override
    public void mostrarEventos(CategoriaDTO categoria) {
        ocultarTodo();
        if (frmPlantilla == null) {
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        if (frmRegistro == null) {
            frmRegistro = new FrmRegistroItson(this, categoria);
        }
        try {
            frmPlantilla.setContenido(new PnlEventos(this, categoria));
        } catch (GestionEventoException ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
        frmPlantilla.setCreditos(getUsuarioSesion().getCreditos().toString());
        frmPlantilla.mostrarInicio();
        frmPlantilla.mostrarConsultar();
        frmPlantilla.setVisible(true);
    }

    @Override
    public List<EventoDTO> consultarEventos(CategoriaDTO categoria) throws GestionEventoException {
        return controlEvento.consultarEventosPorCategoria(categoria);
    }

    @Override
    public List<CategoriaDTO> consultarCategorias() {
        try {
            return controlEvento.consultarCategorias();
        } catch (GestionEventoException ex) {
            return null;
        }
    }

    @Override
    public UsuarioDTO iniciarSesion(LoginDTO login) throws CoordinadorException {
        try {
            UsuarioDTO usuarioDTO = controlInicioSesion.iniciarSesion(login);
            setUsuarioSesion(usuarioDTO);
            return usuarioDTO;
        } catch (InicioSesionException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public boolean setUsuarioSesion(UsuarioDTO usuario) {
        try {
            return controlUsuarios.asociarUsuario(usuario);
        } catch (GestionUsuarioException gue) {
            return false;
        }
    }

    @Override
    public UsuarioDTO getUsuarioSesion() {
        try {
            return controlUsuarios.obtenerUsuarioActivo();
        } catch (GestionUsuarioException gue) {
            return null;
        }
    }

    @Override
    public void cerrarSesion() {
        controlUsuarios.cerrarSesion();
        controlEmpleados.cerrarSesion();
        frmRegistro.eliminarRegistro();
        this.mostrarInicioSesion();
    }

    @Override
    public List<ReservacionDTO> consultarReservaciones(String idUsuario) {
        try {
            return controlUsuarios.obtenerReservacionesUsuario(idUsuario);
        } catch (GestionUsuarioException ex) {
            return null;
        }
    }

    @Override
    public boolean agregarReservacion(ReservacionDTO reservacion) throws CoordinadorException {
        try {
            return controlCompra.agregarReservacion(reservacion);
        } catch (CompraBoletoException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public Map<SeccionDTO, List<AsientoEventoDTO>> obtenerMapaOcupacion(EventoDTO evento) {
        try {
            return controlCompra.obtenerMapaOcupacion(evento);
        } catch (CompraBoletoException e) {
            return new HashMap<>();
        }
    }

    @Override
    public List<AsientoDTO> obtenerCatalogoAsientos() {
        try {
            return controlCompra.obtenerAsientosPorSeccion(null);
        } catch (CompraBoletoException ex) {
            return new java.util.ArrayList<>();
        }
    }

    @Override
    public boolean reservarAsiento(String idAsientoEvento) {
        try {
            return controlCompra.reservarAsiento(idAsientoEvento);
        } catch (CompraBoletoException ex) {
            return false;
        }
    }

    @Override
    public boolean liberarAsiento(String idAsientoEvento) {
        try {
            return controlCompra.liberarAsiento(idAsientoEvento);
        } catch (CompraBoletoException ex) {
            return false;
        }
    }

    @Override
    public boolean venderAsientos(List<AsientoEventoDTO> asientosSeleccionados, Long totalCompra, boolean gratuito, ReservacionDTO reservacion) {
        try {
            return controlCompra.venderAsientos(asientosSeleccionados, totalCompra, gratuito, reservacion);
        } catch (CompraBoletoException ex) {
            return false;
        }
    }

    @Override
    public PagoDTO realizarCompra(TarjetaDTO tarjeta, CobroDTO cobro) {
        try {
            return controlCompra.realizarCompra(tarjeta, cobro);
        } catch (CompraBoletoException ex) {
            return null;
        }
    }

    @Override
    public Long getTotalPendiente() {
        return controlCompra.getTotalPendiente();
    }

    @Override
    public void volverAConsultarEvento() {
        if (frmPago != null) {
            frmPago.dispose();
        }
        frmRegistro.dispose();
        frmPlantilla.setVisible(true);
    }

    @Override
    public String generarQR(EventoDTO evento, AsientoEventoDTO asiento, String token) {
        try {
            return controlCompra.generarCodigoQR(evento, asiento, token);
        } catch (CompraBoletoException ex) {
            return null;
        }
    }

    @Override
    public void mostrarPago(ReservacionDTO reservacion) {
        ocultarTodo();
        this.reservacionActual = reservacion;
        frmPago = new FrmPago(this, reservacionActual);
        frmPago.setVisible(true);
        frmPago.setLocationRelativeTo(null);
    }

    @Override
    public boolean validarCredenciales(UsuarioInstitucionalDTO credenciales) {
        return controlCompra.validarCredencialesITSON(credenciales);
    }

    @Override
    public UsuarioDTO guardarUsuario(RegistroUsuarioDTO usuario) throws CoordinadorException {
        try {
            return controlInicioSesion.registrarUsuario(usuario);
        } catch (InicioSesionException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public boolean isUsuarioITSONRegistrado() {
        return frmRegistro.registroExitoso();
    }

    @Override
    public void cancelarReservacion(ReservacionDTO reservacion) {
        ocultarTodo();
        coordinadorDevolucion.abrirMostrarEventoCancelar(reservacion);
    }

    @Override
    public boolean iniciarCamara() throws CoordinadorException {
        try {
            return controlRevision.iniciarCamara();
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public BufferedImage obtenerFrameActual() throws CoordinadorException {
        try {
            return controlRevision.obtenerFrameActual();
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public String leerQR(BufferedImage frame) throws CoordinadorException {
        try {
            return controlRevision.leerQR(frame);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public AsistenciaDTO registrarAsistencia(String token, AsistenciaDTO asistenciaDTO, String idEvento) throws CoordinadorException {
        try {
            return controlRevision.registrarAsistencia(token, asistenciaDTO, idEvento);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public EventoDTO buscarEventoRevision(String idEvento) throws CoordinadorException {
        try {
            return controlRevision.buscarEvento(idEvento);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public List<EventoDTO> buscarEventosPorNombre(String nombre) throws CoordinadorException {
        try {
            return controlRevision.buscarEventosPorNombre(nombre);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public List<AsientoEventoDTO> obtenerAsientosConAsistencia(String idEvento) throws CoordinadorException {
        try {
            return controlRevision.obtenerAsientosConAsistencia(idEvento);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public EmpleadoDTO obtenerEmpleado(EmpleadoDTO empleado) throws CoordinadorException {
        try {
            return controlEmpleados.obtenerEmpleado(empleado);
        } catch (EmpleadoException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public EmpleadoDTO obtenerSesionEmpleado() throws CoordinadorException {
        return controlEmpleados.getEmpleadoSesion();
    }

    @Override
    public void mostrarCamara(EventoDTO evento) throws CoordinadorException {
        try {
            if (frmCamara != null) {
                frmCamara.dispose();
            }
            frmCamara = new FrmCamara(this, evento);
        } catch (CoordinadorException e) {
            return;
        }
        frmCamara.setVisible(true);
        frmConsultarEventosActuales.setVisible(false);
    }

    @Override
    public void mostrarAsistencias(EventoDTO eventoDTO) throws CoordinadorException {
        ocultarTodo();
        if (frmAsistencias != null) {
            frmAsistencias.dispose();
        }
        frmAsistencias = new FrmAsistencias(eventoDTO, this);
        frmAsistencias.setVisible(true);
        frmConsultarEventosActuales.setVisible(false);
    }

    @Override
    public void mostrarConsultarEventos() {
        ocultarTodo();
        if (frmConsultarEventosActuales == null) {
            frmConsultarEventosActuales = new FrmConsultarEventosActuales(this);
        }
        frmConsultarEventosActuales.setVisible(true);
        if (frmAsistencias != null) {
            frmAsistencias.dispose();
            frmAsistencias = null;
        }
        if (frmCamara != null) {
            frmCamara.setVisible(false);
        }
    }

    @Override
    public List<EventoDTO> obtenerEventosActuales() throws CoordinadorException {
        try {
            return controlRevision.obtenerEventosActuales();
        } catch (RevisionBoletosException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public BoletoDTO obtenerBoletoPorToken(String token) throws CoordinadorException {
        try {
            return controlRevision.obtenerBoletoPorToken(token);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public ReporteAsistenciaDTO obtenerAsistencias(String idEvento) throws CoordinadorException {
        try {
            return controlRevision.obtenerResumen(idEvento);
        } catch (RevisionBoletosException e) {
            throw new CoordinadorException(e.getMessage());
        }
    }

    @Override
    public FrmAsistencias getFrmAsistencias() {
        return frmAsistencias;
    }
    
    @Override
    public boolean facturar(String idReservacion) throws CoordinadorException {
        try {
            return controlFactura.obtenerFactura(idReservacion);
        } catch (FacturaException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public PerfilFiscalDTO recuperarPerfilFiscal(String idUsuario)throws CoordinadorException{
        try {
            PerfilFiscalDTO perfil = controlFactura.buscarPerfil(idUsuario);
            if(perfil == null){
                mostrarBuscarRFC();
                return null;
            }
            return perfil;
        } catch (FacturaException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public PerfilFiscalDTO buscarPerfilFiscal(String rfc, String idUsuario) throws CoordinadorException {
        try {
            idUsuario = controlUsuarios.obtenerUsuarioActivo().getIdUsuario();
            return controlFactura.buscarPerfilFiscal(rfc, idUsuario);
        } catch (FacturaException ex) {
            throw new CoordinadorException(ex.getMessage());
        } catch (GestionUsuarioException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public FacturaDTO crearFactura(PerfilFiscalDTO perfil, ReservacionDTO reserva) throws CoordinadorException {
        try{
            return controlFactura.crearFactura(perfil, reserva);
        }catch(FacturaException ex){
            throw new CoordinadorException(ex.getMessage());
        }
    }

    @Override
    public boolean timbrarFactura(FacturaDTO factura) throws CoordinadorException {
        try {
            return controlFactura.generarFactura(factura);
        } catch (FacturaException ex) {
            throw new CoordinadorException(ex.getMessage());
        }
    }
    
    @Override
    public void mostrarDatosFactura(FacturaDTO factura) {
        if (frmDatosFacturar != null) {
            frmDatosFacturar.dispose();
            frmDatosFacturar = null;
        }
        if(dlgResumenFactura != null){
            dlgResumenFactura.dispose();
            dlgResumenFactura = null;
        }

        if (dlgBuscarPerfil != null) {
            dlgBuscarPerfil.dispose();
            dlgBuscarPerfil = null;
        }
        frmDatosFacturar = new FrmDatosFacturar(this, factura);
        frmDatosFacturar.setLocationRelativeTo(null);
        frmDatosFacturar.setVisible(true);
    }

    @Override
    public void mostrarBuscarRFC() {
        if(frmPlantilla != null){
            frmPlantilla = new FrmPlantillaSistema(this);
        }
        if(dlgBuscarPerfil != null){
            dlgBuscarPerfil.dispose();
        }
        dlgBuscarPerfil = new DlgBuscarPerfil(frmPlantilla, true, this);
        dlgBuscarPerfil.setLocationRelativeTo(null);
        dlgBuscarPerfil.setVisible(true);
    }

    @Override
    public void mostrarResumenDatosFactura(FacturaDTO factura) {
        if (dlgResumenFactura != null) {
            dlgResumenFactura.dispose();
            dlgResumenFactura = null;
        }
    
        if (frmDatosFacturar != null) {
            frmDatosFacturar.dispose();
            frmDatosFacturar = null;
        }
        dlgResumenFactura = new DlgDetalleFactura(frmPlantilla, true, this, factura);
        dlgResumenFactura.setLocationRelativeTo(null);
        dlgResumenFactura.setVisible(true);
    }
}
