package com.vaadin.tutorial.crm.backend.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vaadin.tutorial.crm.backend.entity.Cliente;
import com.vaadin.tutorial.crm.backend.entity.Mensaje;
import com.vaadin.tutorial.crm.backend.entity.Sucursal;
import com.vaadin.tutorial.crm.backend.entity.Users;
import com.vaadin.tutorial.crm.backend.repository.ClienteRepository;
import com.vaadin.tutorial.crm.backend.repository.MensajeRepository;
import com.vaadin.tutorial.crm.backend.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

@RestController
@RequestMapping("/sms/")
public class Prueba {

    Calendar c;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private JavaMailSender sender;

    private static final Logger LOGGER = LoggerFactory.getLogger(EnviarEmail.class);




    @PostMapping()
    public String pruebaPost(@RequestBody String fullName)
    {
        JsonObject jsonObject = JsonParser.parseString(fullName).getAsJsonObject();
        String numero=jsonObject.get("from").getAsString();
        String mensaje= jsonObject.get("body").getAsString();
        String to =jsonObject.get("to").getAsString();

        TimeZone timeZone = TimeZone.getTimeZone("Australia/Sydney");
        c = Calendar.getInstance(timeZone);

        numero = numero.substring(3,numero.length());
        Cliente clienteTelefono = this.clienteRepository.getClientePorNumeroTelefono(numero);
        if(clienteTelefono==null)
        {
            numero="0"+numero;
            clienteTelefono = this.clienteRepository.getClientePorNumeroTelefono(numero);
        }

        if(clienteTelefono!=null && clienteTelefono.getId()!=null)
        {
            Mensaje mensajeGuardar = new Mensaje();
            mensajeGuardar.setCliente(clienteTelefono);
            mensajeGuardar.setSentido(Mensaje.ENTRADA);
            mensajeGuardar.setNumero_origen(numero);
            mensajeGuardar.setNumero_destino(to);
            mensajeGuardar.setMensaje(mensaje);
            LocalDateTime ahora = c.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            mensajeGuardar.setFecha_hora(ahora);
            this.mensajeRepository.save(mensajeGuardar);

            if(clienteTelefono.getAppoiment()==0 && !clienteTelefono.getCalltobemade().equalsIgnoreCase("yes"))
            {
                clienteTelefono.setCalltobemade("yes");
                this.clienteRepository.save(clienteTelefono);
            }
            else if(clienteTelefono.getCalltobemade().equalsIgnoreCase("yes"))
            {
                LocalDateTime fechaLlamada = clienteTelefono.getFecha_llamada();
                if(fechaLlamada!= null && fechaLlamada.isBefore(ahora))
                {
                    clienteTelefono.setFecha_llamada(null);
                    this.clienteRepository.save(clienteTelefono);
                }
            }
            else if(clienteTelefono.getAppoiment()==1)
            {
                clienteTelefono.setCalltobemade("yes");
                clienteTelefono.setFecha_llamada(null);
                this.clienteRepository.save(clienteTelefono);
            }

            Sucursal sucursalCliente = clienteTelefono.getSucursal();
            if(sucursalCliente!=null)
            {
                List<Users> listaUsuarios =  this.usersRepository.buscarPorSucursal(sucursalCliente.getId());
                for(int h=0;h<listaUsuarios.size();h++)
                {
                    Users usuarioSucursal = listaUsuarios.get(h);
                    if(usuarioSucursal.getEmail().trim().length()>0)
                    {
                        String mensajeEmail ="The client: "+clienteTelefono.getNombre()+" "+clienteTelefono.getApellido()+" with phone number: "+clienteTelefono.getMovil()+" has answered.";
                        String emailDestino = usuarioSucursal.getEmail();
                        String subject ="New SMS Client: "+clienteTelefono.getNombre()+" "+clienteTelefono.getApellido();
                        this.sendEmailTool(mensajeEmail,emailDestino,subject);
                    }
                }
            }
        }
        System.out.println(fullName);
        return "hola post";
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
            LOGGER.info("Mail enviado!");
        } catch (MessagingException e) {
            LOGGER.error("Hubo un error al enviar el mail: {}", e);
        }
        return send;
    }
}
