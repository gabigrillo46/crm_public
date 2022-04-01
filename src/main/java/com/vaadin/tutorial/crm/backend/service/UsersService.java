package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsersService {

    private UsersRepository usersRepository;

    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }



    public Users buscarPorNombreUsuario(String nombreUsuario)
    {
        return usersRepository.buscarPorNomberUusario(nombreUsuario);
    }




    public Users buscarPorId(int id)
    {
        boolean enable=true;
        return usersRepository.buscarPorId(id);
    }

    public void saveUsuario(Users usuario)
    {
        usersRepository.save(usuario);

    }

    public List<Users> buscarUsuariosPorSucursal(Long idSucursal)
    {
        return usersRepository.buscarPorSucursal(idSucursal);
    }

    public List<Users> buscarPorFiltro(String nombre, Long idSucursal)
    {
        return usersRepository.buscarPorFiltro(nombre,idSucursal);
    }

    public Users buscarPorNombre(String nombre)
    {
        return usersRepository.buscarPorNombre(nombre);
    }

}
