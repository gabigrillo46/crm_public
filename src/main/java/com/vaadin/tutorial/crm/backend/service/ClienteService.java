package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ClienteService {

    private ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository)
    {
        this.clienteRepository=clienteRepository;
    }


    public Cliente getClientePorTelefono(String telefono)
    {
        return this.clienteRepository.getClientePorNumeroTelefono(telefono);
    }


    public List<Cliente> getClientesPorTelefono(String telefono)
    {
        return this.clienteRepository.getClientesPorNumeroTelefono(telefono);
    }

    public List<Cliente> buscarClientesMovilParecido(String telefono)
    {
        return this.clienteRepository.getClientesPorMovilParecido(telefono);
    }

    public List<Cliente> getClientePorSucursal(Long idSucursal)
    {
        return this.clienteRepository.getClientePorSucursal(idSucursal);
    }

    public List<Cliente> getTodosClientesPorSucursalFecha(Long idSucursal, Date fechaDesde, Date fechaHasta)
    {
        return this.clienteRepository.getTodosClientePorSucursalFecha(idSucursal, fechaDesde, fechaHasta);
    }

    public List<Cliente> getClienteActivosPorSucursal(Long idSucursal)
    {
        return this.clienteRepository.getClienteActivosPorSucursal(idSucursal);
    }

    public List<Cliente> getClienteTentativePorSucursal(Long idSucursal)
    {
        return this.clienteRepository.getClienteTentativePorSucursal(idSucursal);
    }

    public void registrarCliente(Cliente cliente)
    {
        this.clienteRepository.save(cliente);
    }

    public List<Cliente> findAll()
    {
        return this.clienteRepository.findAll();
    }

    public List<Cliente> getClientesPorFiltro(String nombre, String obsercacion, String movil, Long idSucursal, String calltobemade, Integer appoiment, Integer lost, Integer sale, Long idSOurce, Date fecha_desde, Date fecha_hasta)
    {
        return clienteRepository.buscarClientesPorFiltro(nombre,obsercacion,movil, idSucursal, calltobemade, appoiment, lost, sale, idSOurce, fecha_desde, fecha_hasta);
    }

    public List<Cliente> getTodosClientesShowUpSucursalFecha(Long idSucursal, Date fechaDesde, Date fechaHasta)
    {
        return clienteRepository.buscarTodosClienteShowUpSucursalFecha(idSucursal,fechaDesde,fechaHasta);
    }

    public List<Cliente> getTodosClientesAppoimentSucursalFecha(Long idSucursal, Date fecha_desde, Date fecha_hasta)
    {
        return clienteRepository.buscarTodosClienteAppoimentSucursalFecha(idSucursal,fecha_desde, fecha_hasta);
    }

    public List<Cliente> getTodosClientesSaleSucursalFecha(Long idSucursal, Date fechaDesde, Date fechaHasta)
    {
        return clienteRepository.buscarTodosClienteSaleSucursalFecha(idSucursal,fechaDesde,fechaHasta);
    }

    public List<Cliente> getClientePorSucursalYSource(Long idSucursal, Long idSource, Date fechaDesde, Date fechaHasta)
    {
        return clienteRepository.getClientePorSucursalSource(idSucursal, idSource, fechaDesde, fechaHasta);
    }

    public List<Cliente> getClientePorSource( Long idSource, Date fechaDesde, Date fechaHasta)
    {
        return clienteRepository.getClientePorSource(idSource, fechaDesde, fechaHasta);
    }

    public List<Cliente> getClienteConAppDeSucursal(Long sucursal)
    {
        return clienteRepository.buscarClienteSucursalConApp(sucursal);
    }

    public List<Cliente> getClienteSucursalConCall(Long sucursal)
    {
        return clienteRepository.buscarClienteSucursalConCall(sucursal);
    }

    public Cliente getClientePorId(Long idCliente)
    {
        return clienteRepository.getClientePorId(idCliente);
    }

}
