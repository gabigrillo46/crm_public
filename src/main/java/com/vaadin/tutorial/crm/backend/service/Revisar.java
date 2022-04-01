package com.vaadin.tutorial.crm.backend.service;

import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.repository.ClienteRepository;
import com.vaadin.tutorial.crm.backend.repository.UsersRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class Revisar{


    private ClienteRepository clienteRepository;

    private UsersRepository usuarioRepository;


    private JavaMailSender sender;


    public Revisar(ClienteRepository clienteRepository, UsersRepository usuarioRepository,JavaMailSender sender)
    {
        this.clienteRepository=clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.sender=sender;
    }





    public boolean sendEmailTool(String textMessage, String email,String subject) {
        boolean send = false;
        MimeMessage message = this.sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setTo(email);
            helper.setText(textMessage, true);
            helper.setSubject(subject);
            sender.send(message);
            send = true;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return send;
    }



    @Async
    public void run()
    {
        boolean continuar = true;
        while(continuar)
        {
            LocalDateTime ahora = LocalDateTime.now();
            LocalDateTime menor = ahora.minusSeconds(30);
            LocalDateTime mayor = ahora.plusSeconds(30);
            List<Cliente> listaClientesAhora = clienteRepository.getCLienteConLlamadaAhora(menor, mayor);
            for(int a=0;a<listaClientesAhora.size();a++)
            {
                Cliente clienteAhora = listaClientesAhora.get(a);
                String mensajeEmail = "Client mobile: "+clienteAhora.getMovil();
                if(clienteAhora.getCalltobemade().equalsIgnoreCase("Yes") && clienteAhora.getAppoiment()==1)
                {
                    mensajeEmail=mensajeEmail+" with appoiment and call to be made";
                }
                else if(clienteAhora.getCalltobemade().equalsIgnoreCase("Yes"))
                {
                    mensajeEmail =mensajeEmail+" with call to be made";
                }
                else if(clienteAhora.getAppoiment()==1)
                {
                    mensajeEmail =mensajeEmail+" with appoiment";
                }
                List<Users> usuariosANotificar = this.usuarioRepository.buscarPorSucursal(clienteAhora.getSucursal().getId());
                //SenderSms sender = new SenderSms();
                SenderSMSTelstra sender = new SenderSMSTelstra();

                for (int j = 0; j < usuariosANotificar.size(); j++) {
                    Users usuario = usuariosANotificar.get(j);
                    if (usuario != null && usuario.getMovil() != null && usuario.getMovil().trim().length()>0) {
                        //this.sendEmailTool(mensajeEmail, usuario.getEmail(), mensajeEmail);
                        sender.enviarSMSTelstra(usuario.getMovil(),mensajeEmail);
                        //sender.enviarMensaje(mensajeSms, usuario.getMovil());
                    }
                }
            }
            int minutos = 1;
            int segundos =minutos * 60;
            int milisegundos = segundos * 1000;
            try {
                Thread.currentThread().sleep(milisegundos);
            } catch (Exception e) {
                return;
            }
        }
    }
}
