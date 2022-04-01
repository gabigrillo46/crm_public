package com.vaadin.tutorial.crm.backend.repository;

import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.ui.Constante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {


    @Query("FROM Cliente c where c.movil = :phone and c.estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA)
    Cliente getClientePorNumeroTelefono(@Param("phone") String telefono);

    @Query("FROM Cliente c where c.movil = :phone and c.estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA)
    List<Cliente> getClientesPorNumeroTelefono(@Param("phone") String telefono);

    @Query("FROM Cliente c where (:phone is null or c.movil like %:phone%) and c.estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA)
    List<Cliente> getClientesPorMovilParecido(@Param("phone") String telefono);



    @Query("FROM Cliente c where c.sucursal.id = :sucursal and estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA +"and estado != "+Constante.ESTADOS_CLIENTES.LOST)
    List<Cliente> getClientePorSucursal(@Param("sucursal") Long idSucursal);

    @Query("FROM Cliente c where c.sucursal.id = :sucursal and " +
            " c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta")
    List<Cliente> getTodosClientePorSucursalFecha(@Param("sucursal") Long idSucursal, @Param("fecha_desde") Date fecha_desde, @Param("fecha_hasta") Date fecha_hasta);

    @Query("FROM Cliente c where c.sucursal.id = :sucursal and estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA+" and estado != "+Constante.ESTADOS_CLIENTES.LOST+" and sale = 0 and (appoiment=1 or calltobemade = 'yes')")
    List<Cliente> getClienteActivosPorSucursal(@Param("sucursal") Long idSucursal);

    @Query("FROM Cliente c where c.sucursal.id = :sucursal and estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA+" and estado != "+Constante.ESTADOS_CLIENTES.LOST+" and sale = 0 and appoiment=0 and calltobemade = 'no' ")
    List<Cliente> getClienteTentativePorSucursal(@Param("sucursal") Long idSucursal);

    @Query("FROM Cliente c where  c.fecha_llamada >= :fechaHoraMenor and c.fecha_llamada <= :fechaHoraMayor and c.calltobemade = 'Yes' and c.sale =0 and  c.estado = "+Constante.ESTADOS_CLIENTES.ACTIVO)
    List<Cliente> getCLienteConLlamadaAhora(@Param("fechaHoraMenor")LocalDateTime menor,@Param("fechaHoraMayor")LocalDateTime mayor);


    @Query("FROM Cliente c where c.sucursal.id = :sucursal and c.source.id = :source and " +
            "c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta ")
    List<Cliente> getClientePorSucursalSource(@Param ("sucursal")Long idSucursal, @Param("source") Long idSource, @Param("fecha_desde") Date fechaDesde, @Param("fecha_hasta") Date fechaHasta);

    @Query("FROM Cliente c where  c.source.id = :source and " +
            "c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta")
    List<Cliente> getClientePorSource( @Param("source") Long idSource, @Param("fecha_desde") Date fechaDesde, @Param("fecha_hasta") Date fechaHasta);


    @Query("FROM Cliente c where (:nombre is null or c.nombre LIKE %:nombre% or c.apellido like  %:nombre% ) " +
            "and (:observacion is null or c.id in (select co.cliente.id from Comentario co  where co.comentario like %:observacion%)  )  " +
            "and (:movil is null or c.movil like %:movil%) " +
            "and (:sucursal is null or c.sucursal.id = :sucursal)" +
            "and (:source is null or c.source.id = :source)" +
            "and (:calltobemade is null or c.calltobemade = :calltobemade)" +
            "and (:appoiment is null or c.appoiment=:appoiment )" +
            " and (:lost is null or c.estado = :lost)" +
            " and (:sale is null or c.sale = :sale)" +
            " and (:fecha_desde is null or c.fecha_creado >= :fecha_desde) " +
            " and (:fecha_hasta is null or c.fecha_creado <= :fecha_hasta) " +
            "and c.estado !="+Constante.ESTADOS_CLIENTES.BAJA_LOGICA)
    List<Cliente>buscarClientesPorFiltro(@Param("nombre") String nombre,
                                         @Param("observacion") String observacion,
                                         @Param("movil") String movil,
                                         @Param("sucursal")Long idSucursal,
                                         @Param("calltobemade")String calltobe,
                                         @Param("appoiment")Integer appoiment,
                                         @Param("lost")Integer lost,
                                         @Param("sale")Integer sale,
                                         @Param("source")Long idSource,
                                         @Param("fecha_desde") Date fecha_desde,
                                         @Param("fecha_hasta") Date fecha_hasta);

    @Query("FROM Cliente c where (:sucursal is null or c.sucursal.id = :sucursal) " +
            "and c.appoiment =1 and c.sale =0 and c.estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA+"" +
            "and c.estado != "+Constante.ESTADOS_CLIENTES.LOST)
    List<Cliente>buscarClienteSucursalConApp(@Param("sucursal")Long sucursal);

    @Query("FROM Cliente c where (:sucursal is null or c.sucursal.id = :sucursal) " +
            "and c.show_up =1 and c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta")
    List<Cliente>
    buscarTodosClienteShowUpSucursalFecha(@Param("sucursal")Long sucursal, @Param("fecha_desde") Date fecha_desde, @Param("fecha_hasta") Date fecha_hasta);

    @Query("FROM Cliente c where (:sucursal is null or c.sucursal.id = :sucursal) " +
            "and c.sale =1 and c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta")
    List<Cliente>buscarTodosClienteSaleSucursalFecha(@Param("sucursal")Long sucursal, @Param("fecha_desde") Date fecha_desde, @Param("fecha_hasta") Date fecha_hasta);

    @Query("FROM Cliente c where (:sucursal is null or c.sucursal.id = :sucursal) " +
            "and c.appoiment =1 and c.fecha_creado >= :fecha_desde and c.fecha_creado <= :fecha_hasta")
    List<Cliente>buscarTodosClienteAppoimentSucursalFecha(@Param("sucursal")Long sucursal, @Param("fecha_desde") Date fecha_desde, @Param("fecha_hasta") Date fecha_hasta);


    @Query("FROM Cliente c where (:sucursal is null or c.sucursal.id = :sucursal) " +
            "and c.calltobemade='Yes' and c.sale = 0 and c.estado != "+Constante.ESTADOS_CLIENTES.BAJA_LOGICA+"" +
            "and c.estado != "+Constante.ESTADOS_CLIENTES.LOST)
    List<Cliente>buscarClienteSucursalConCall(@Param("sucursal")Long sucursal);

    @Query("FROM Cliente c where c.id = ?1")
    Cliente getClientePorId(Long id);

}
